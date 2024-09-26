package smartsuite.app.bp.srm.performance.request.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.pfmcSetup.repository.PegSetupRepository;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PfmcEvalshtSetupService;
import smartsuite.app.bp.srm.performance.request.repository.PerformanceEvalRequestRepository;
import smartsuite.app.bp.srm.performance.request.validator.PerformanceEvalRequestValidator;
import smartsuite.app.bp.srm.performance.result.service.PerformanceEvalResultService;
import smartsuite.app.common.event.PerformanceEventPublisher;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.PerformanceEvalDeleteService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.PerformanceEvalStatusService;

import javax.inject.Inject;
import java.util.*;

/**
 * Performance Request 관련 처리를 하는 서비스 Class입니다.
 *
 * @author hj.jang
 * @FileName PerformanceReqService.java
 * @package smartsuite.app.bp.performance.req
 * @변경이력 : [2023. 6. 10] hj.jang 최초작성
 * @see
 * @since 2023. 06. 10
 */
@Service
@Transactional
public class PerformanceEvalRequestService {

	@Inject
	PerformanceEvalRequestValidator pfmcEvalReqValidator;

	@Inject
	PerformanceEvalRequestRepository pfmcEvalReqRepository;

	@Inject
	private PfmcEvalshtSetupService pfmcEvalshtSetupService;

	@Inject
	private PegSetupRepository pegSetupRepository;

	@Inject
	private PerformanceEvalStatusService pfmcStatusService;

	@Inject
	private PerformanceEvalResultService pfmcEvalResService;

	@Inject
	private PerformanceEventPublisher pfmcEventPublisher;

	@Inject
	SharedService sharedService;

	@Inject
	private PerformanceEvalDeleteService pfmcEvalDelService;

	/**
	 * 퍼포먼스평가 요청 정보를 조회한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : hj.jang
	 * @Date : 2023. 6. 10
	 * @Method Name : findPfmEvalReq
	 */
	public Map findPfmcEvalReq(Map param) {
		return pfmcEvalReqRepository.findPfmcEvalReq(param);
	}
	
	/**
	 * 퍼포먼스평가 평가그룹 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list
	 * @author : hj.jang
	 * @Date : 2023. 6. 10
	 * @Method Name : findListPfmcEvalPeg
	 */
	public List findListPfmcEvalPeg(Map param) {
		return pfmcEvalReqRepository.findListPfmcEvalPeg(param);
	}	
	
	/**
	 * 퍼포먼스평가 요청 정보를 저장한다.
	 *
	 * @param  : pfmcEvalReq 평가요청 정보
	 * @param  : pfmcPegList 평가요청 대상되는 평가그룹 목록
	 * @return the resultMap - resultStatus, resultData, resultMessage
	 * @author : hj.jang
	 * @Date : 2023. 6. 19
	 * @Method Name : savePfmcReq
	 */
	public ResultMap savePfmcEvalReq(Map param) {
		Map<String, Object> pfmcEvalReq = (Map<String, Object>)param.get("pfmcEvalReq");
		List<Map<String, Object>> pfmcPegList = (List<Map<String, Object>>)param.get("pfmcPegList");
		ResultMap resultMap = null;

		if(pfmcEvalReq == null || pfmcPegList == null) {
			resultMap = ResultMap.NOT_EXISTS();
			return resultMap;
		}

		String peUuid = (String)pfmcEvalReq.get("pe_uuid");

		// 신규저장
		if(peUuid == null || "".equals(peUuid)) {
			// 평가요청정보 저장
			resultMap = this.insertPfmcEvalReq(param);
		}
		// 업데이트
		else {
			// 퍼포먼스 평가 요청 정보의 정합성 검사
			ResultMap validMap = pfmcEvalReqValidator.validatePeInfo(param);

			if(!validMap.isSuccess()) {
				List<Map<String, Object>> invalidPePegList = validMap.getResultList();
				// 퍼포먼스평가 평가그룹 중 정합하지 않은 데이터 존재 시 데이저 조정
				for(Map<String, Object> invalidPePeg : invalidPePegList) {
					// 퍼포먼스 평가 요청 정보가 평가그룹 설정과 맞지 않는 경우 퍼포먼스평가 요청 정보 조정. (평가시트, 평가대상, 평가자 수정)
					this.adjustInvalidPePegData(invalidPePeg);
				}
			}

			resultMap = this.updatePfmcEvalReq(param);
		}

		if(resultMap.isSuccess()) {
			pfmcStatusService.saveDraftPfmcEval(resultMap.getResultData());
		}

		return resultMap;
	}

	/* 퍼포먼스평가 평가그룹 설정과 맞지 않는 데이터 존재 시 조정한다. */
	private ResultMap adjustInvalidPePegData(Map<String, Object> invalidPeg) {
		List<String> invalidFieldList = (List<String>)invalidPeg.getOrDefault("invalidFieldList", Lists.newArrayList());

		for(String invalidField : invalidFieldList) {
			// 유효한 평가시트로 업데이트 
			if(PfmcConst.EFCT_EVALSHT_YN.equals(invalidField)) {
				this.adjustInvalidPfmcEvalshtByPeg(invalidPeg);
			}
			// 평가그룹 관리 대상 아닌 협력사관리그룹을 평가대상에서 삭제
			else if(PfmcConst.EFCT_VMG_YN.equals(invalidField)) {
				this.adjustInvalidPeSubjByPegVmg(invalidPeg);
			}
			else if(PfmcConst.EFCT_AUTHTY_YN.equals(invalidField)) {
				this.adjustPeSubjEvaltrInvalidAuthtyByTmpl(invalidPeg);
			}
		}
		return ResultMap.SUCCESS();
	}

	// 퍼포먼스 요청 평가그룹의 펑가시트를 퍼포먼스평가그룹의 현재시점 유효한 평가시트로 업데이트한다.
	private void adjustInvalidPfmcEvalshtByPeg(Map<String, Object> param) {
		pfmcEvalReqRepository.updatePePegValidPfmcEvalshtByPeg(param);
		pfmcEvalReqRepository.updatePeSubjValidPfmcEvalshtByPeg(param);
	}
	
	// 퍼포먼스 요청 평가대상의 협력사관리그룹이 평가그룹 협력사관리그룹과 맞지 않는 경우 평가대상을 삭제한다.
	private void adjustInvalidPeSubjByPegVmg(Map<String, Object> param) {
		List<Map<String, Object>> peSubjList = pfmcEvalReqValidator.findListInvalidPeSubjByPegVmg(param);
		if(peSubjList != null) {
			for(Map<String, Object> peSubj : peSubjList) {
				peSubj.put(PfmcConst.DELETE_TYPE, PfmcConst.PE_SUBJ);
				pfmcEvalReqRepository.deletePeSubjEvaltr(peSubj);
				pfmcEvalReqRepository.deletePeSubj(peSubj);
			}
		}
	}

	// 평가시트 템플릿의 평가항목권한과 맞지 않는 퍼포먼스 평가대상 평가자를 조정한다.
	private void adjustPeSubjEvaltrInvalidAuthtyByTmpl(Map<String, Object> param) {
		// 평가시트 템플릿의 평가항목권한과 맞지 않는 퍼포먼스 평가대상 평가자를 삭제한다.
		pfmcEvalReqRepository.deletePeSubjEvaltrInvalidAuthtyByTmpl(param);
		// 평가시트 템플릿의 평가항목권한자가 퍼포먼스 평가대상 평가자에 없는 경우 추가한다.
		pfmcEvalReqRepository.insertPeSubjEvaltrValidAuthtyByTmpl(param);
	}

	/**
	 * 퍼포먼스평가요청 신규저장
	 * @param param
	 * @return
	 */
	private ResultMap insertPfmcEvalReq(Map<String, Object> param) {
		Map<String, Object> resultData = Maps.newHashMap();
		Map<String, Object> pfmcEvalReq = (Map<String, Object>)param.get("pfmcEvalReq");
		List<Map<String, Object>> pfmcPegList = (List<Map<String, Object>>)param.get("pfmcPegList");

		String peUuid = UUID.randomUUID().toString();
		String evalCd =  sharedService.generateDocumentNumber("EV");
		pfmcEvalReq.put("pe_uuid", peUuid);
		pfmcEvalReq.put("eval_cd", evalCd);

		// 1. 평가그룹 헤더 저장
		pfmcEvalReqRepository.insertPe(pfmcEvalReq);
		
		// 2. 평가그룹 목록 저장
		for(Map<String, Object> peg : pfmcPegList) {
			peg.put("pe_uuid", peUuid);
			pfmcEvalReqRepository.insertPePeg(peg);
			// pfmcEvalReqRepository.insertPeEvalGrd(peg); 평가 요청 시 저장
		}
		// 퍼포먼스평가요청 상태 변경 - 임시저장
		pfmcStatusService.saveDraftPfmcEval(pfmcEvalReq);

		resultData.put("pe_uuid", peUuid);
		resultData.put("eval_cd", evalCd);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 퍼포먼스평가요청헤더 수정 저장
 	 * @param param
	 * @return
	 */ 
	private ResultMap updatePfmcEvalReq(Map<String, Object> param) {
		Map<String, Object> resultData = Maps.newHashMap();
		Map<String, Object> pfmcEvalReq = (Map<String, Object>)param.getOrDefault("pfmcEvalReq", Maps.newHashMap());
		List<Map<String, Object>> pfmcPegList = (List<Map<String, Object>>)param.getOrDefault("pfmcPegList", Lists.newArrayList());

		String peUuid = (String)pfmcEvalReq.getOrDefault("pe_uuid", "");
		String evalCd = (String)pfmcEvalReq.getOrDefault("eval_cd", "");

		// 1. 평가그룹 헤더 저장
		pfmcEvalReqRepository.updatePe(pfmcEvalReq);

		// 2. 평가그룹 목록 저장
		for(Map<String, Object> peg : pfmcPegList) {
			String checkedYn = (String)peg.get("apply_peg_yn");
			peg.put("pe_uuid", peUuid);
			// 신규 추가
			if(checkedYn != null && PfmcConst.Y_STR_VAL.equals(checkedYn)) {
				pfmcEvalReqRepository.insertPePeg(peg);
			} 
			// 체크해제 - 삭제
			else {
				this.deletePePeg(peg);
			}
		}

		resultData.put("pe_uuid", peUuid);
		resultData.put("eval_cd", evalCd);
		return ResultMap.SUCCESS(resultData);
	}

	private void deletePePeg(Map<String, Object> param) {

		// 삭제 유형 : 퍼포먼스평가 퍼포먼스평가그룹
		param.put(PfmcConst.DELETE_TYPE, PfmcConst.PE_PEG);
		// 평가그룹 삭제
		// 1. 펴포먼스평가 대상 평가자 삭제
		pfmcEvalReqRepository.deletePeSubjEvaltr(param);

		// 2. 퍼포먼스평가 대상 삭제
		pfmcEvalReqRepository.deletePeSubj(param);

		// 3. 퍼포먼스평가 평가등급 삭제
		pfmcEvalReqRepository.deletePeEvalGrd(param);
		
		// 4. 퍼포먼스평가그룹 삭제
		pfmcEvalReqRepository.deletePePeg(param);

	}

	/**
	 * 퍼포먼스평가 평가그룹 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param : param 퍼포먼스평가 평가그룹 조회 파라미터
	 * @return the map
	 * @Date : 2023. 6. 23
	 * @Method Name : findPfmcReqPegInfo
	 */
	public Map findPfmcReqPegInfo(Map param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		// 퍼포먼스 평가 요청 정보의 정합성 검사
		ResultMap validMap = pfmcEvalReqValidator.validatePeInfo(param);

		if(!validMap.isSuccess()) {
			List<Map<String, Object>> invalidPePegList = validMap.getResultList();
			// 퍼포먼스평가 평가그룹 중 정합하지 않은 데이터 존재 시 데이저 조정
			for(Map<String, Object> invalidPePeg : invalidPePegList) {
				// 퍼포먼스 평가 요청 정보가 평가그룹 설정과 맞지 않는 경우 퍼포먼스평가 요청 정보 조정. (평가시트, 평가대상, 평가자 수정)
				this.adjustInvalidPePegData(invalidPePeg);
			}
		}

		// 1. 퍼포먼스평가그룹 Mater 조회
		Map<String, Object> pePegInfo = pfmcEvalReqRepository.findPePeg(param);

		// 2. 퍼포먼스 평가시트 조회 (현재시점 기준으로 유효한 평가그룹 평가시트 아이디)
		param.put("pfmc_evalsht_uuid", pePegInfo.get("pfmc_evalsht_uuid"));
		Map<String, Object> pfmcEvalshtInfo = pfmcEvalshtSetupService.findPfmcEvalsht(param);
		// pe_peg 저장 당시 유효했던 퍼포먼스 평가시트 (기존 연결 정보)
		pfmcEvalshtInfo.put("pe_pfmc_evalsht_uuid", pePegInfo.getOrDefault("pe_pfmc_evalsht_uuid", ""));

		// 3. 퍼포먼스평가그룹 - 협력사관리그룹 조회
		List<Map<String, Object>> pegVmgList = pegSetupRepository.findListPegVmg(param);

		// 4. 퍼포먼스평가그룹 - 평가템플릿 평가항목 담당자 권한 목록 조회
		List<Map<String, Object>> pfmcEvalshtEvalfactAuthty = pfmcEvalReqRepository.findListPfmcEvalshtEvalfactAuthty(pfmcEvalshtInfo);

		// 5. 퍼포먼스평가그룹 평가등급 - 평가템플릿 평가항목 담당자 권한 목록 조회
		List<Map<String, Object>> peEvalGrdList = pfmcEvalReqRepository.findListPeEvalGrd(pePegInfo);

		resultMap.put("pePegInfo", pePegInfo);
		resultMap.put("pfmcEvalshtInfo", pfmcEvalshtInfo);
		resultMap.put("pegVmgList", pegVmgList);
		resultMap.put("pfmcEvalshtEvalfactAuthty", pfmcEvalshtEvalfactAuthty);
		resultMap.put("peEvalGrdList", peEvalGrdList);
		return resultMap;
	}
	/**
	 * 퍼포먼스평가 요청 대상을 조회한다.
	 *
	 * @author : hj.jang
	 * @param : param 퍼포먼스평가 요청대상 조회 파라미터
	 * @return the list
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPfmcReqSubj
	 */
	public List findListPeSubjByPePeg(Map param) {
		return pfmcEvalReqRepository.findListPeSubjByPePeg(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상을 저장한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청 정보
	 * @param : param pePegInfo 퍼포먼스평가  퍼포먼스평가그룹 정보
	 * @param : param pfmcEvalshtInfo 퍼포먼스평가시트 정보
	 * @param : param savePeSubjList 저장할 퍼포먼스평가 대상 목록
	 * @return ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : savePeSubj
	 */
	public ResultMap savePeSubj(Map param) {
		Map<String, Object> pfmcEvalReq = (Map<String, Object>) param.get("pfmcEvalReq");  // 퍼포먼스평가 요청 정보
		Map<String, Object> pePegInfo = (Map<String, Object>) param.get("pePegInfo");     // 퍼포먼스평가 퍼포먼스평가그룹 정보
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>) param.get("pfmcEvalshtInfo");   // 퍼포먼스평가시트 정보
		List<Map<String, Object>> peSubjList = (List<Map<String, Object>>)param.get("savePeSubjList");  // 저장할 평가대상 목록

		// 평가대상 저장 가능 상태 체크. 파라미터 유효성 검사, 퍼포먼스평가요청 수정가능 상태 확인
		ResultMap validMap = pfmcEvalReqValidator.validateSavePeSubj(pfmcEvalReq, pePegInfo, pfmcEvalshtInfo, peSubjList);
		if(!validMap.isSuccess()) {
			return validMap;
		} else {
			String evaltrTypCcd = (String)pfmcEvalshtInfo.get(PfmcConst.EVALTR_TYP_CCD.toLowerCase()); // 평가자유형 공통코드
			for(Map<String, Object> peSubj : peSubjList ) {
				String evalSubjYn = (String)peSubj.get("eval_subj_yn");  // 평가대상여부
				String peSubjUuid = (String)peSubj.get("pe_subj_uuid");  // 퍼포먼스평가대상 아이디

				// 평가대상여부에 체크 되었고 퍼포먼스평가 대상으로 저장되지 않았던 건은 신규추가
				if(evalSubjYn != null && PfmcConst.Y_STR_VAL.equals(evalSubjYn) && peSubjUuid == null) {
					peSubjUuid = UUID.randomUUID().toString();
					peSubj.put(PfmcConst.PE_SUBJ_UUID, peSubjUuid);
					pfmcEvalReqRepository.insertPeSubj(peSubj);
					// 기본 평가대상 평가자 저장
					peSubj.put(PfmcConst.REQ_TYPE, PfmcConst.PE_SUBJ);
					this.savePeSubjDfltEvaltr(evaltrTypCcd, peSubj);
				}
				// 평가대상여부 체크 해제되었고 퍼포먼스평가 대상으로 저장되었던 건은 삭제
				else if(evalSubjYn != null && PfmcConst.N_STR_VAL.equals(evalSubjYn) && (peSubjUuid != null || "".equals(peSubjUuid))) {
					peSubj.put(PfmcConst.DELETE_TYPE, PfmcConst.PE_SUBJ);
					pfmcEvalReqRepository.deletePeSubjEvaltr(peSubj);
					pfmcEvalReqRepository.deletePeSubj(peSubj);
				}
			}

			return ResultMap.SUCCESS();
		}
	}

	/**
	 * 퍼포먼스평가 대상 기본 평가자를 저장한다.  (평가시트에 설정된 옵션에 따라 생성된다.)
	 * evaltrTypCcd :  평가자유형 공통코드
	 * param : 평가대상 정보 (PE_PEG_UUID 필수, PE_SUBJ_UUID 선택)
 	 */
	public void savePeSubjDfltEvaltr(String evaltrTypCcd, Map<String, Object> param) {
		// 평가시트 평가자 유형 : 항목별 평가자
		if(PfmcConst.EVALFACT_AUTHTY_PIC.equals(evaltrTypCcd)) {
			pfmcEvalReqRepository.insertPeSubjEvaltrFact(param);
		}
		// 평가시트 평가자 유형 : 구매 담당자
		else if(PfmcConst.PURC_PIC.equals(evaltrTypCcd)) {
			param.put("evalfact_evaltr_authty_ccd", PfmcConst.EVALFACT_EVALTR_AUTHTY_DFLT_VAL);
			pfmcEvalReqRepository.insertPeSubjEvaltrPurc(param);
		}
		// 평가시트 평가자 유형 : VMG 담당자
		else if(PfmcConst.VMG_PIC.equals(evaltrTypCcd)) {
			param.put(PfmcConst.EVALFACT_EVALTR_AUTHTY_CCD,  PfmcConst.EVALFACT_EVALTR_AUTHTY_DFLT_VAL);
			pfmcEvalReqRepository.insertPeSubjEvaltrVmg(param);
		}
	}

	/**
	 * 퍼포먼스평가 대상 자체점검 평가자를 저장한다.  (평가시트에 설정된 옵션에 따라 생성된다.)
	 * param.req_type : 요청 유형 (PE, PE_PEG, PE_SUBJ)
	 * param.pe_uuid / param.pe_peg_uuid / param.pe_subj_uuid : req_type에 맞는 uuid 필수
	 */
	public void savePsSubjSlfckEvaltr(Map<String, Object> param) {
		// 자체점검 생성 가능 상태 확인
		Map<String, Object> reqPeSubjCheckMap = this.getRequestablePeSubjSlfck(param);
		String slfckSubjYn = (String)reqPeSubjCheckMap.get("is_slfck_creatable");

		// 자체점검 평가자 : 평가대상 평가자로 협력사 추가 생성
		if(slfckSubjYn != null && PfmcConst.Y_STR_VAL.equals(slfckSubjYn)) {
			param.put("evalfact_evaltr_authty_ccd", PfmcConst.EVALFACT_EVALTR_AUTHTY_DFLT_VAL);
			pfmcEvalReqRepository.insertPeSubjEvaltrVd(param);
		}
	}

	/**
	 * 자체점검 평가대상 생성 요청 가능 여부를 조회한다.
	 *
	 * @author : hj.jang
	 * @param :  param the map (pePegInfo
	 * @return the Map (is_slfck_creatable : Y/N)
	 * @Date : 2023. 6. 23
	 * @Method Name : getRequestablePeSubjSlfck
	 */
	public Map getRequestablePeSubjSlfck(Map param) {
		return pfmcEvalReqRepository.getRequestablePeSubjSlfck(param);
	}

	/**
	 * 퍼포먼스평가 평가대상 평가자 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param :  param 퍼포먼스평가 평가대상 평가자 목록 조회 파라미터
	 * @return the list
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPeSubjEvaltr
	 */
	public List findListPeSubjEvaltr(Map param) {
		return pfmcEvalReqRepository.findListPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상 평가자를 저장한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청 정보
	 * @param : param pegInfo 퍼포먼스평가  퍼포먼스평가그룹 정보
	 * @param : param pfmcEvalshtInfo 퍼포먼스평가시트 정보
	 * @param : param insertPeSubjEvaltrList 신규 생성할 퍼포먼스평가 대상 평가자 목록
	 * @param : param udpatePeSubjEvaltrList 수정할 퍼포먼스평가 대상 평가자 목록
	 * @param : param deletePeSubjEvaltrList 삭제할 퍼포먼스평가 대상 평가자 목록
	 * @return ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : savePeSubjEvaltr
	 */
	public ResultMap savePeSubjEvaltr(Map param) {
		Map<String, Object> pfmcEvalReq = (Map<String, Object>) param.get("pfmcEvalReq");  // 퍼포먼스평가 요청 정보
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pePegInfo");     // 퍼포먼스평가 퍼포먼스평가그룹 정보
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>) param.get("pfmcEvalshtInfo");   // 퍼포먼스평가시트 정보
		List<Map<String, Object>> insertEvaltrList = (List<Map<String, Object>>)param.get("insertPeSubjEvaltrList");  // 저장할 평가대상 목록
		List<Map<String, Object>> updateEvaltrList = (List<Map<String, Object>>)param.get("udpatePeSubjEvaltrList");  // 저장할 평가대상 목록
		List<Map<String, Object>> deleteEvaltrList = (List<Map<String, Object>>)param.get("deletePeSubjEvaltrList");  // 저장할 평가대상 목록

		if(deleteEvaltrList != null) {
			for(Map<String, Object> deleteEvaltr : deleteEvaltrList) {
				pfmcEvalReqRepository.deletePeSubjEvaltr(deleteEvaltr);
			}
		}
		if(insertEvaltrList != null) {
			for(Map<String, Object> insertEvaltr : insertEvaltrList) {
				pfmcEvalReqRepository.mergePeSubjEvaltrAdd(insertEvaltr);
			}
		}
		if(updateEvaltrList != null) {
			for(Map<String, Object> udpateEvaltr : updateEvaltrList) {
				pfmcEvalReqRepository.updatePeSubjEvaltr(udpateEvaltr);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스평가를 요청한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청 정보
	 * @return ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : requestPfmcEval
	 */
	public ResultMap requestPfmcEval(Map param) {
		// 퍼포먼스평가요청 정보 재조회.
		final Map<String, Object> peInfo = this.findPfmcEvalReq(param);

		final String peStsCcd = (String)peInfo.get(PfmcConst.PE_STS_CCD);

		// 1. 평가요청 상태 검사
		if(!pfmcEvalReqValidator.getRequestPfmcEvalSts(peStsCcd).isSuccess()) {
			return ResultMap.FAIL("E999");	// 평가정보가 유효하지 않습니다.
		}
		// 2. 퍼포먼스평가등급 생성
		peInfo.put(PfmcConst.REQ_TYPE, PfmcConst.PE);
		pfmcEvalReqRepository.deletePeEvalGrd(peInfo);
		pfmcEvalReqRepository.insertPeEvalGrd(peInfo);

		// 3. 자체점검 평가자 생성 (자체점검 평가자 생성 가능 여부는 생성 서비스 내에서 판단함)
		this.savePsSubjSlfckEvaltr(peInfo);
		peInfo.remove(PfmcConst.REQ_TYPE);

		// 4. 퍼포먼스평가 결과 데이터 생성
		ResultMap createResult = pfmcEvalResService.createPeSubjResByPe(peInfo);

		return createResult;
	}


	/**
	 * 퍼포먼스평가 진행 화면 > 퍼포먼스평가 요청 대상 추가 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : findListPrgsPeSubjAdd
	 */
	public List findListPrgsPeSubjAdd(Map<String, Object> param) {
		return pfmcEvalReqRepository.findListPrgsPeSubjAdd(param);
	}

	/**
	 * 퍼포먼스평가 진행 화면 > 퍼포먼스평가 요청 대상을 추가적으로 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : savePrgsPeSubjAdd
	 */
	public ResultMap savePrgsPeSubjAdd(Map<String, Object> param) {
		List invalidList = new ArrayList<ResultMap>();
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("saveList");
		final Map<String, Object> peInfo = (Map<String, Object>)param.get("peInfo");

		if (inserts != null && !inserts.isEmpty()) {
			for (final Map<String, Object> row : inserts) {
				String evaltrTypCcd = (String)row.get(PfmcConst.EVALTR_TYP_CCD.toLowerCase()); // 평가자유형 공통코드
				String peSubjUuid = UUID.randomUUID().toString();  // 퍼포먼스 평가대상 아이디
				row.put(PfmcConst.PE_SUBJ_UUID, peSubjUuid);
				row.put(PfmcConst.REQ_TYPE, PfmcConst.PE_SUBJ);

				pfmcEvalReqRepository.insertPrgsPeSubjAdd(row); // 퍼포먼스 평가대상 추가
				this.savePeSubjDfltEvaltr(evaltrTypCcd, row); // 퍼포먼스 평가대상 평가자 추가
				this.savePsSubjSlfckEvaltr(row); // 퍼포먼스 평가대상 자체점검 평가자 추가 (자체점검 생성 가능 여부는 생성 서비스 내에서 판단)

				// 퍼포먼스평가대상 결과 생성
				//  1. 자체점검이 아닌 결과 생성
				row.put(PfmcConst.SLFCK_RES_YN, PfmcConst.N_STR_VAL);
				row.put(PfmcConst.EVAL_SUBJ_RES_UUID, null);
				ResultMap createMap = pfmcEvalResService.createPeSubjResByPeSubj(row);  // 퍼포먼스 평가대상 결과, 평가결과 추가

				// 생성 실패한 리스트
				if(!createMap.isSuccess()) {
					invalidList.add(createMap);
				}

				// 2. 자체점검 결과 생성 필요 판단 및 결과 생성
				String peSlfckSubjYn = (String)peInfo.get(PfmcConst.SLFCK_SUBJ_YN);  // 퍼포먼스평가 요청의 자체점검 대상여부
				String pegSlfckSubjYn = (String)row.get(PfmcConst.SLFCK_SUBJ_YN); // 퍼포먼스 평가그룹 자체점검 대상여부
				if(peSlfckSubjYn != null && PfmcConst.Y_STR_VAL.equals(peSlfckSubjYn) &&
						pegSlfckSubjYn != null && PfmcConst.Y_STR_VAL.equals(pegSlfckSubjYn)) {
					row.put(PfmcConst.SLFCK_RES_YN, PfmcConst.Y_STR_VAL);
					row.put(PfmcConst.EVAL_SUBJ_RES_UUID, null);
					createMap = pfmcEvalResService.createPeSubjResByPeSubj(row);
					// 생성 실패한 리스트
					if(!createMap.isSuccess()) {
						invalidList.add(createMap);
					}
				}
			}
		}
		if (invalidList.isEmpty()) {
			return ResultMap.SUCCESS();
		}
		else {
			return ResultMap.builder().resultStatus(ResultMap.STATUS.FAIL).resultList(invalidList).build();
		}
	}

	/**
	 * 퍼포먼스평가 요청 목록 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 29
	 * @Method Name : findListPfmcEvalPrgs
	 */
	public List findListPfmcEvalPrgs(Map<String, Object> param) {
		return pfmcEvalReqRepository.findListPfmcEvalPrgs(param);
	}

	/**
	 * 특정 퍼포먼스평가그룹 협력사관리그룹으로 퍼포먼스평가 통보 전 임시저장 데이터를 조회한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 8. 11
	 * @Method Name : findPeInfoBeforeCrngVmg
	 */
	public List findPeInfoBeforeCrngVmg(Map<String, Object> param) {
		return pfmcEvalReqRepository.findPeInfoBeforeCrngVmg(param);
	}

	/**
	 * 퍼포먼스평가 요청 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : findPfmcEvalPrgsInfo
	 */
	public Map findPfmcEvalPrgsInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		Map<String, Object> data = pfmcEvalReqRepository.findPfmcEvalPrgsInfo(param);
		resultMap.put("data", data);

		List<Map<String, Object>> list = pfmcEvalReqRepository.findListPfmcEvalSubjPrgs(param);
		resultMap.put("list", list);
		return resultMap;
	}
	/**
	 * 퍼포먼스평가 요청 대상 별 담당자 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : findListPrgsPeSubjEvaltr
	 */
	public List findListPrgsPeSubjEvaltr(Map<String, Object> param) {
		return pfmcEvalReqRepository.findListPrgsPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상 별 담당자 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : deleteListPrgsPeSubj
	 */
	public ResultMap deleteListPrgsPeSubj(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newConcurrentMap();
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> peSubj : deletes) {
				peSubj.put(PfmcConst.DELETE_TYPE, PfmcConst.PE_SUBJ);
				// 1. 퍼포먼스평가 대상의 평가대상결과를 삭제한다.
				pfmcEvalDelService.deletePeSubjResDataByPeSubj(peSubj);
				// 2. 퍼포먼스평가 대상의 평가자를 삭제한다.
				pfmcEvalReqRepository.deletePeSubjEvaltr(peSubj);
				// 3. 퍼포먼스 평가 대상을 삭제한다.
				pfmcEvalReqRepository.deletePeSubj(peSubj);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 저장을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * param.saveList : 추가 저장할 퍼포먼스평가대상 평가자 목록
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : saveListPrgsPeSubjEvaltr
	 */
	public ResultMap saveListPrgsPeSubjEvaltr(final Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("saveList");
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		if (inserts != null && !inserts.isEmpty()) {
			for(Map<String, Object> insertEvaltr : inserts) {
				String evaltrTypCcd = (String)insertEvaltr.get(PfmcConst.EVALTR_TYP_CCD);
				String evalfactEvaltrAuthtyCcd = (String)insertEvaltr.get(PfmcConst.EVALFACT_EVALTR_AUTHTY_CCD);

				// 평가자 구분이 평가항목 권한 담당자 이고 평가항목권한 공통코드가 '전체'인 경우 : 자체점검 담당자.
				// 자체점검 담당자는 평가자 추가 X
				if(PfmcConst.EVALFACT_AUTHTY_PIC.equals(evaltrTypCcd) && PfmcConst.EVALFACT_EVALTR_AUTHTY_DFLT_VAL.equals(evalfactEvaltrAuthtyCcd)) {
					invalidList.add(insertEvaltr);
				}
				// 퍼포먼스평가 평가자가 이미 존재하는 평가자인 경우 추가 X
				else if(pfmcEvalReqValidator.isExistPeSubjEvaltr(insertEvaltr).isDuplicate()) {
					invalidList.add(insertEvaltr);
				}
				else {
					// 1. 퍼포먼스평가 평가자 저장
					// 필수 : pe_subj_uuid, evalfact_evaltr_authty_ccd, slfck_evaltr_yn, evaltr_id
					String peSubjEvaltrUuid = UUID.randomUUID().toString();
					insertEvaltr.put(PfmcConst.PE_SUBJ_EVALTR_UUID, peSubjEvaltrUuid);
					pfmcEvalReqRepository.insertPeSubjEvaltrAdd(insertEvaltr);

					// 2. 평가대상 결과 평가자 결과 저장
					// 필수 : ten_id, eval_subj_res_uuid, evalfact_evaltr_authty_ccd, evaltr_id
					ResultMap resultMap = pfmcEvalResService.createEvalSubjEvaltrResByPeSubjEvaltr(insertEvaltr);

					if(!resultMap.isSuccess()) {
						invalidList.add(insertEvaltr);
					}

				}
			}
		}

		if(!invalidList.isEmpty()) {
			return ResultMap.FAIL(invalidList);
		} else {
			return ResultMap.SUCCESS();
		}
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 변경을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * param.saveList : 변경 저장할 퍼포먼스평가대상 평가자 목록
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : saveChangedPrgsPeSubjEvaltr
	 */
	public ResultMap saveChangedPrgsPeSubjEvaltr(final Map<String, Object> param) {
		Map<String, Object> updateEvaltr = Maps.newHashMap(param);
		String evaltrTypCcd = (String)updateEvaltr.get(PfmcConst.EVALTR_TYP_CCD);
		String evalfactEvaltrAuthtyCcd = (String)updateEvaltr.get(PfmcConst.EVALFACT_EVALTR_AUTHTY_CCD);
		String newEvaltrId = (String)updateEvaltr.get("evaltr_id");

		// 평가자 구분이 평가항목 권한 담당자 이고 평가항목권한 공통코드가 '전체'인 경우 : 자체점검 담당자.
		// 자체점검 담당자는 평가자 추가 X
		if(PfmcConst.EVALFACT_AUTHTY_PIC.equals(evaltrTypCcd) && PfmcConst.EVALFACT_EVALTR_AUTHTY_DFLT_VAL.equals(evalfactEvaltrAuthtyCcd)) {
			return ResultMap.FAIL("자체점검 담당자는 변경할 수 없습니다.");
		}
		// 퍼포먼스평가 평가자 데이터 키값으로 조회시 데이터가 존재하지 않는 경우 변경 X
		updateEvaltr.put("evaltr_id", null);
		if(!pfmcEvalReqValidator.isExistPeSubjEvaltr(updateEvaltr).isDuplicate()) {
			return ResultMap.FAIL("존재하지 않는 평가자 정보입니다.");
		}

		// 1. 퍼포먼스평가 평가자 변경
		// 필수 : pe_subj_evaltr_uuid
		updateEvaltr.put("evaltr_id", newEvaltrId);
		pfmcEvalReqRepository.updatePeSubjEvaltr(updateEvaltr);

		// 2. 평가대상 결과 평가자 결과 변경
		// 필수 : ten_id, eval_subj_res_uuid, evalfact_evaltr_authty_ccd, evaltr_id
		return pfmcEventPublisher.changeEvalSubjEvaltrRes(updateEvaltr);


	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : deleteListPrgsPeSubjEvaltr
	 */
	public ResultMap deleteListPrgsPeSubjEvaltr(Map param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				// 1. 평가대상 평가자 결과 삭제
				pfmcEvalDelService.deleteEvalSubjEvaltrRes(row);
				// 2. 펴포먼스평가 대상 평가자 삭제
				pfmcEvalReqRepository.deletePeSubjEvaltr(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 요청 상세 : 퍼포먼스 평가 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param (pfmcEvalReq)
	 * @return the ResultMap
	 * @Date : 2023. 07. 08
	 * @Method Name : deletePfmcEval
	 */
	public ResultMap deletePfmcEval(Map param) {
		// 퍼포먼스평가요청 아이디 존재할 경우 전체 데이터 삭제
		if (!"".equals(param.getOrDefault(PfmcConst.PE_UUID, ""))) {
			// 평가 결과 전체 삭제
			pfmcEvalDelService.deletePfmcEval(param);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 요청 상세 : 퍼포먼스 평가 요청울 취소한다.
	 *
	 * @author : hj.jang
	 * @param param the param (pfmcEvalReq)
	 * @return the ResultMap
	 * @Date : 2023. 08. 08
	 * @Method Name : cancelRequestPfmcEval
	 */
	public ResultMap cancelRequestPfmcEval(Map param) {

		if (!"".equals(param.getOrDefault(PfmcConst.PE_UUID, ""))) {
			// 평가 결과 전체 삭제
			pfmcEvalDelService.deleteEvalByEvalReqUuid(param);
			// 퍼포먼스 평가대상 결과 삭제
			pfmcEvalDelService.deleteRequestPeSubjRes(param);
			// 퍼포먼스 평가대상 자체점검 평가자 삭제
			pfmcEvalDelService.deleteRequestPeSubjSlfckEvaltr(param);
			// 퍼포먼스 평가대상 평가자 데이터에서 eval 관련 데이터 Null로 업데이트
			pfmcEvalReqRepository.cancelRequestPeSubjEvaltr(param);
		}
		pfmcStatusService.cancelRequestPfmcEval(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 요청 시  요청 가능 상태인지 확인한다. (퍼포먼스 평가 요청정보 유효성 검사)
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap <br>
	 *         - resultStatus <br>
	 *         - resultList : EFCT_EVALSHT_YN, EFCT_VMG_YN, EFCT_AUTHTY_YN, EXISTS_PE_SUBJ_YN, EXISTS_PE_EVALTR_YN  <br>
	 * @Date : 2023. 08. 09
	 * @Method Name : checkValidStateRequestPfmcEval
	 */
	public ResultMap checkValidStateRequestPfmcEval(Map param) {
		// 평가요청 데이터 검사
		return pfmcEvalReqValidator.validatePeInfo(param);
	}

}
