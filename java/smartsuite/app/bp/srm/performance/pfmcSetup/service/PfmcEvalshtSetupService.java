package smartsuite.app.bp.srm.performance.pfmcSetup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.pfmcEval.service.PfmcEvalService;
import smartsuite.app.bp.srm.performance.pfmcSetup.event.PfmcSetupEventPublisher;
import smartsuite.app.bp.srm.performance.pfmcSetup.repository.PfmcEvalshtSetupRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * pfmc evalsht setup 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName PfmcEvalshtSetupService.java
 * @package smartsuite.app.bp.performance.pfmcSetup.service
 * @Since 2023. 6. 13
 * @변경이력 : [2023. 6. 13] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PfmcEvalshtSetupService {

	@Inject
	private PfmcEvalshtSetupRepository pfmcEvalshtSetupRepository;

	@Inject
	private SharedService sharedService;

	@Inject
	private PegSetupService pegSetupService;

	@Inject
	private PfmcEvalService pfmcEvalService;

	@Inject
	PfmcSetupEventPublisher pfmcSetupEventPublisher;

	/**
	 * 퍼포먼스 평가시트 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the pfmc evalsht list
	 * @Date : 2023. 7. 24
	 * @Method Name : findListPfmcEvalsht
	 */
	public FloaterStream findListPfmcEvalsht(Map<String, Object> param) {
		// 대용량 처리
		return pfmcEvalshtSetupRepository.findListPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : findPfmcEvalsht
	 */
	public Map<String, Object> findPfmcEvalsht(Map<String, Object> param) {
		// 1. 퍼포먼스 평가시트 조회
		Map<String, Object> pfmcEvalshtInfo = pfmcEvalshtSetupRepository.findPfmcEvalshtInfo(param);

		// 3. 퍼포먼스 평가시트 사용여부 체크 (PE 생성 여부)
		String isCreatePeYn = pfmcEvalService.findCreatePeYnByPfmcEvalsht(pfmcEvalshtInfo);
		pfmcEvalshtInfo.put("isCreatePeYn", isCreatePeYn);

		return pfmcEvalshtInfo;
	}

	/**
	 * 최신 퍼포먼스 평가시트을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 23
	 * @Method Name : findPfmcCurrentEvalsht
	 */
	public Map<String, Object> findPfmcCurrentEvalsht(List<Map<String, Object>> pfmcEvalshtHis) {
		Map<String, Object> pfmcEvalshtInfo = new HashMap<>();
		Map<String, Object> currentEvalsht = pfmcEvalshtHis.size() != 0 ? pfmcEvalshtHis.get(0) : null;

		if(currentEvalsht != null && "Y".equals(currentEvalsht.get("current_evalsht"))){
			Map<String, Object> param = new HashMap<>();
			param.put("peg_uuid", currentEvalsht.get("peg_uuid"));
			param.put("pfmc_evalsht_uuid", currentEvalsht.get("pfmc_evalsht_uuid"));  // 최신 평가시트 UUID
			pfmcEvalshtInfo = this.findPfmcEvalsht(param);
			if(pfmcEvalshtHis.size() > 1) {
				pfmcEvalshtInfo.put("prev_pfmc_evalsht_uuid", pfmcEvalshtHis.get(1).get("pfmc_evalsht_uuid"));  // 이전 평가시트 UUID
			}
		}

		return pfmcEvalshtInfo;
	}

	/**
	 * 퍼포먼스 평가시트를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : savePfmcEvalsht
	 */
	public ResultMap savePfmcEvalsht(Map<String, Object> param) {
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pegInfo");
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>)param.get("pfmcEvalshtInfo");
		Map<String, Object> evalTmplInfo = (Map<String, Object>)param.get("evalTmplInfo");
		Map<String, Object> evaltrInfo = (Map<String, Object>)param.get("evaltrInfo");  // evaltr 정보

		String pfmcEvalshtUuid = pfmcEvalshtInfo.get("pfmc_evalsht_uuid") == null ? "" : pfmcEvalshtInfo.get("pfmc_evalsht_uuid").toString();
		if("".equals(pfmcEvalshtUuid)){  // 신규
			pfmcEvalshtUuid = UUID.randomUUID().toString();
			String evalshtCd = sharedService.generateDocumentNumber("PES");

			// 1. 퍼포먼스 평가시트 저장
			pfmcEvalshtInfo.put("peg_uuid", pegInfo.get("peg_uuid"));
			pfmcEvalshtInfo.put("pfmc_evalsht_uuid", pfmcEvalshtUuid);
			pfmcEvalshtInfo.put("evalsht_cd", evalshtCd);
			pfmcEvalshtSetupRepository.insertPfmcEvalsht(pfmcEvalshtInfo);
		}

		// 2. 퍼포먼스 평가시트 템플릿 저장
		Map<String, Object> createEvalTmplResult = new HashMap<>();
		String evaltmplNm = evalTmplInfo.get("evaltmpl_nm") == null ? "" : evalTmplInfo.get("evaltmpl_nm").toString();
		if(!"".equals(evaltmplNm)){  // 템플릿 생성 정보 존재
			createEvalTmplResult = this.savePfmcEvalTmpl(evalTmplInfo).getResultData();
			pfmcEvalshtInfo.put("evaltmpl_uuid", createEvalTmplResult.get("evaltmpl_uuid"));
		}

		// 3. 퍼포먼스 평가시트 수정 및 템플릿 매핑
		pfmcEvalshtSetupRepository.updatePfmcEvalsht(pfmcEvalshtInfo);

		// 4. 퍼포먼스 평가시트 자가진단 대상 저장
		Map<String, Object> slfckSubjParam = new HashMap<String, Object>();         // 평가자 저장 Param
		slfckSubjParam.put("pfmcEvalshtInfo", pfmcEvalshtInfo);
		slfckSubjParam.put("deleteSlfckSubjTargetList", (List<Map<String, Object>>)createEvalTmplResult.get("deleteSlfckSubjTargetList"));
		slfckSubjParam.put("insertSlfckSubjTargetList", (List<Map<String, Object>>)createEvalTmplResult.get("insertSlfckSubjTargetList"));
		this.saveListPfmcSlfckSubj(slfckSubjParam);

		// 5. 퍼포먼스 평가시트 항목별 담당자 저장
		Map<String, Object> evaltrParam = new HashMap<String, Object>();         // 평가자 저장 Param
		evaltrParam.put("pfmcEvalshtInfo", pfmcEvalshtInfo);
		evaltrParam.put("evaltrInfo", evaltrInfo);
		this.saveListPfmcFactChrGrpEvaltr(evaltrParam);

		return ResultMap.SUCCESS(pegInfo);
	}

	/**
	 * 퍼포먼스 평가시트 템플릿을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 8
	 * @Method Name : savePfmcEvalTmpl
	 */
	public ResultMap savePfmcEvalTmpl(Map<String, Object> evalTmplInfo) {
		Map<String, Object> results = new HashMap<String, Object>();

		// 1. 평가 템플릿 저장
		ResultMap createEvalTmplResult = (ResultMap) pfmcSetupEventPublisher.saveEvalTmplInfo(evalTmplInfo);

		if(ResultMap.STATUS.SUCCESS.equals(createEvalTmplResult.getResultStatus())) {
			Map<String, Object> evalTmplFactInfo = (Map<String, Object>)evalTmplInfo.get("evalTmplFactInfo");  // evaltmpl fact 정보
			Map<String, Object> createEvalTmplResultData = createEvalTmplResult.getResultData();
			List<Map<String, Object>> insertEvalTmplFactList = (List<Map<String, Object>>)createEvalTmplResultData.get("insertEvalTmplFactList");
			List<Map<String, Object>> deleteEvalTmplFactList = (List<Map<String, Object>>)evalTmplFactInfo.get("deleteEvalTmplFactList");
			List<Map<String, Object>> updateEvalTmplFactList = (List<Map<String, Object>>)evalTmplFactInfo.get("updateEvalTmplFactList");

			// 2. 평가시트 자가진단 평가항목 셋팅
			insertEvalTmplFactList = insertEvalTmplFactList.stream().filter(x -> x.get("evaltmpl_evalfact_uuid") != null && !"".equals(x.get("evaltmpl_evalfact_uuid"))).collect(Collectors.toList());
			deleteEvalTmplFactList = deleteEvalTmplFactList.stream().filter(x -> x.get("evaltmpl_evalfact_uuid") != null && !"".equals(x.get("evaltmpl_evalfact_uuid"))).collect(Collectors.toList());
			updateEvalTmplFactList = updateEvalTmplFactList.stream().filter(x -> x.get("evaltmpl_evalfact_uuid") != null && !"".equals(x.get("evaltmpl_evalfact_uuid"))).collect(Collectors.toList());

			List<Map<String, Object>> deleteSlfckSubjTargetList = Stream.concat(deleteEvalTmplFactList.stream()
																		, updateEvalTmplFactList.stream().filter(x -> "N".equals(x.get("slfck_subj_yn")))).collect(Collectors.toList());
			List<Map<String, Object>> insertSlfckSubjTargetList = Stream.concat(insertEvalTmplFactList.stream().filter(x -> "Y".equals(x.get("slfck_subj_yn")))
																		, updateEvalTmplFactList.stream().filter(x -> "Y".equals(x.get("slfck_subj_yn")))).collect(Collectors.toList());
			results.put("deleteSlfckSubjTargetList", deleteSlfckSubjTargetList);
			results.put("insertSlfckSubjTargetList", insertSlfckSubjTargetList);

			// 3. 퍼포먼스 평가시트 - 평가 템플릿 매핑
			Map<String, Object> savedEvalTmplInfo = (Map<String, Object>) createEvalTmplResultData.get("evalTmplInfo");
			results.put("evaltmpl_uuid", savedEvalTmplInfo.get("evaltmpl_uuid"));
		}

		return ResultMap.SUCCESS(results);
	}

	/**
	 * 퍼포먼스 시트를 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : deleteLisPeg
	 */
	public ResultMap updatePfmcEvalshtStsByDelete(Map<String, Object> param){
		pfmcEvalshtSetupRepository.updatePfmcEvalshtStsByDelete(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스평가그룹 - 평가시트이력 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the pfmc evalsheet list
	 * @Date : 2023. 6. 5
	 * @Method Name : findListPfmcEvalshtHis
	 */
	public List<Map<String, Object>> findListPfmcEvalshtHis(Map<String, Object> param) {
		// 대용량 처리
		return pfmcEvalshtSetupRepository.findListPfmcEvalshtHis(param);
	}

	/**
	 * 평가템플릿 평가자를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @Date : 2023. 6. 11
	 */
	public List findListFactChrGrpEvaltr(Map<String, Object> param) {
		List<Map<String, Object>> resultList = new ArrayList<>();
		String evaltrTypCcd = param.get("evaltr_typ_ccd") == null ? "" : param.get("evaltr_typ_ccd").toString();

		if("EVALFACT_AUTHTY_PIC".equals(evaltrTypCcd)){  // EVALFACT_AUTHTY_PIC: 평가항목 권한 담당자
			resultList = pfmcEvalshtSetupRepository.findListFactChrGrpEvaltr(param);
		}else if("VMG_PIC".equals(evaltrTypCcd)){        // VMG_PIC: 협력사관리그룹 담당자
			param.put("vmg_oorg_uuids", pfmcEvalshtSetupRepository.findListPegVmgUuid(param));
			resultList = pfmcSetupEventPublisher.findListVendorManagementGroupEvaltrForView(param);
		}

		return resultList;
	}

	/**
	 * 퍼포먼스 평가시트 자가진단 대상을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 15
	 * @Method Name : saveListPfmcSlfckSubj
	 */
	public void saveListPfmcSlfckSubj(Map<String, Object> param) {
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>)param.get("pfmcEvalshtInfo");
		List<Map<String, Object>> deleteSlfckSubjTargetList = (List<Map<String, Object>>)param.get("deleteSlfckSubjTargetList");
		List<Map<String, Object>> insertSlfckSubjTargetList = (List<Map<String, Object>>)param.get("insertSlfckSubjTargetList");  // Self Exam Subj

		String slfckSubjYn = pfmcEvalshtInfo.get("slfck_subj_yn").toString();
		// 퍼포먼스 평가시트 자가진단 대상 삭제 (전체)
		if("N".equals(slfckSubjYn)){  // 자가진단 미대상 평가시트
			pfmcEvalshtSetupRepository.deletePfmcSlfckSubj(pfmcEvalshtInfo);
			return;
		}

		String pfmcEvalshtUuid = pfmcEvalshtInfo.get("pfmc_evalsht_uuid").toString();

		// 퍼포먼스 평가시트 자가진단 대상 삭제 (삭제 대상만)
		if(deleteSlfckSubjTargetList != null && !deleteSlfckSubjTargetList.isEmpty()){
			for(final Map<String, Object> row : deleteSlfckSubjTargetList){
				row.put("pfmc_evalsht_uuid", pfmcEvalshtUuid);
				pfmcEvalshtSetupRepository.deletePfmcSlfckSubj(row);
			}
		}

		// 퍼포먼스 평가시트 자가진단 대상 저장
		if(insertSlfckSubjTargetList != null && !insertSlfckSubjTargetList.isEmpty()){
			for(final Map<String, Object> row : insertSlfckSubjTargetList){
				row.put("pfmc_evalsht_uuid", pfmcEvalshtUuid);
				pfmcEvalshtSetupRepository.insertPfmcSlfckSubj(row);
			}
		}
	}

	/**
	 * 퍼포먼스 평가시트 항목별 담당자를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 15
	 * @Method Name : saveListPfmcFactChrGrpEvaltr
	 */
	public ResultMap saveListPfmcFactChrGrpEvaltr(Map<String, Object> param) {
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>)param.get("pfmcEvalshtInfo");
		Map<String, Object> evaltrInfo = (Map<String, Object>)param.get("evaltrInfo");  // evaltr 정보

		String evaltrTypCcd = pfmcEvalshtInfo.get("evaltr_typ_ccd") == null ? "" : pfmcEvalshtInfo.get("evaltr_typ_ccd").toString();

		if(!"EVALFACT_AUTHTY_PIC".equals(evaltrTypCcd)){  // EVALFACT_AUTHTY_PIC: 평가항목 권한 담당자
			// 평가항목별 담당자 삭제
			pfmcEvalshtSetupRepository.updateEvaltrByDelete(pfmcEvalshtInfo);
		}else{
			// 평가항목별 담당자 저장
			String pfmcEvalshtUuid = pfmcEvalshtInfo.get("pfmc_evalsht_uuid").toString();
			List<Map<String, Object>> insertEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("insertEvaltrList");
			List<Map<String, Object>> updateEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("updateEvaltrList");
			List<Map<String, Object>> deleteEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("deleteEvaltrList");

			if(insertEvaltrList != null && !insertEvaltrList.isEmpty()){
				for(final Map<String, Object> row : insertEvaltrList){
					row.put("pfmc_evalsht_uuid", pfmcEvalshtUuid);
					pfmcEvalshtSetupRepository.insertFactChrGrpEvaltr(row);
				}
			}
			if(updateEvaltrList != null && !updateEvaltrList.isEmpty()){
				for(final Map<String, Object> row : updateEvaltrList){
					row.put("pfmc_evalsht_uuid", pfmcEvalshtUuid);
					pfmcEvalshtSetupRepository.updateFactChrGrpEvaltr(row);
				}
			}
			if(deleteEvaltrList != null && !deleteEvaltrList.isEmpty()){
				for(final Map<String, Object> row : deleteEvaltrList){
					pfmcEvalshtSetupRepository.updateFactChrGrpEvaltrByDelete(row);
				}
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가시트/평가템플릿 확정상태를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 20
	 * @Method Name : saveCnfdYnPfmcEvalsht
	 */
	public ResultMap saveCnfdYnPfmcEvalsht(Map<String, Object> param) {
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pegInfo");
		String cnfdYn = param.get("cnfdYn").toString();

		// 1. 퍼포먼스평가그룹 - 평가시트이력 조회
		List<Map<String, Object>> pfmcEvalshtHis = this.findListPfmcEvalshtHis(pegInfo);

		// 2. 확정요청한 퍼포먼스 평가시트 정보 조회 (= 최신 퍼포먼스 평가시트)
		Map<String, Object> pfmcEvalshtInfo = this.findPfmcCurrentEvalsht(pfmcEvalshtHis);
		if(pfmcEvalshtInfo != null && !pfmcEvalshtInfo.isEmpty()) {
			if("N".equals(cnfdYn)){  // 2-1) '확정취소' 처리인 경우, 퍼포먼스 평가시트 사용여부 체크 (PE 생성 여부)
				String isCreatePeYn = pfmcEvalService.findCreatePeYnByPfmcEvalsht(pfmcEvalshtInfo);
				if("Y".equals(isCreatePeYn)){  // PE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}

			// 3. 이전 온보딩 평가시트 유효일자 update
			// 4. 퍼포먼스 평가시트 확정 update
			pfmcEvalshtInfo.put("cnfd_yn", cnfdYn);
			pfmcEvalshtSetupRepository.updatePrevPfmcEvalsht(pfmcEvalshtInfo);
			pfmcEvalshtSetupRepository.updateCnfdYnPfmcEvalsht(pfmcEvalshtInfo);

			if("Y".equals(cnfdYn)) {  // '확정' 처리인 경우, evaltmpl에 update
				// 5. 평가템플릿 확정 update
				pfmcSetupEventPublisher.updateCnfdYnEvalTmpl(pfmcEvalshtInfo);
			}

			// 6. 퍼포먼스평가그룹 저장
			pegSetupService.updatePegMaster(pegInfo);
		}else{
			return ResultMap.FAIL();
		}

		return ResultMap.SUCCESS(pegInfo);
	}

	/**
	 * 평가템플릿 사용 여부를 조회한다. (퍼포먼스 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 18
	 * @Method Name : findEvalTmplUseYnInPfmcEvalSht
	 */
	public String findEvalTmplUseYnInPfmcEvalSht(Map<String, Object> param) {
		return pfmcEvalshtSetupRepository.findEvalTmplUseYnInPfmcEvalSht(param);
	}

	/**
	 * 평가템플릿 상태값을 조회한다. (퍼포먼스 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 18
	 * @Method Name : findEvalTmplStsInPfmcEvalSht
	 */
	public String findEvalTmplStsInPfmcEvalSht(Map<String, Object> param) {
		List<String> sheetList = pfmcEvalshtSetupRepository.findEvalTmplStsInPfmcEvalSht(param);
		String deleteValid = "U";	// 삭제 가능 여부 초기값 U (update sts D)
		if(sheetList.size() == 0){
			// 삭제 가능
			deleteValid = "D";
		} else {
			// STS 'D' 확인
			for(String sts : sheetList){
				if(!"D".equals(sts)){	// 존재하는 평가시트의 상태가 D가 아니라면 삭제 불가.
					deleteValid = "E";
					break;
				}
			}
		}
		return deleteValid;
	}

	/**
	 * 퍼포먼스 평가시트 확정여부를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 19
	 * @Method Name : checkPfmcEvalShtConfirmYnByEvalTmpl
	 */
	public String checkPfmcEvalShtConfirmYnByEvalTmpl(Map<String, Object> param) {
		return pfmcEvalshtSetupRepository.checkPfmcEvalShtConfirmYnByEvalTmpl(param);
	}

	/**
	 * 퍼포먼스 평가시트를 Import한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 24
	 * @Method Name : saveImportPfmcEvalsht
	 */
	public ResultMap saveImportPfmcEvalsht(Map<String, Object> param) {
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pegInfo");
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>) param.get("pfmcEvalshtInfo");

		// 1. 퍼포먼스 평가시트 copy
		String pfmcEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("PES");
		pfmcEvalshtInfo.put("new_pfmc_evalsht_uuid", pfmcEvalshtUuid);
		pfmcEvalshtInfo.put("evalsht_cd", evalshtCd);
		pfmcEvalshtInfo.put("peg_uuid", pegInfo.get("peg_uuid"));
		pfmcEvalshtSetupRepository.copyPfmcEvalsht(pfmcEvalshtInfo);

		// 2. 퍼포먼스 평가시트 자가진단 대상 copy
		pfmcEvalshtSetupRepository.copyListPfmcSlfckSubj(pfmcEvalshtInfo);

		// 3. 퍼포먼스 평가시트 항목별 담당자 copy
		pfmcEvalshtSetupRepository.copyListPfmcFactChrGrpEvaltr(pfmcEvalshtInfo);

		return ResultMap.SUCCESS(pegInfo);
	}

	/**
	 * 퍼포먼스 평가시트를 버전업한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 21
	 * @Method Name : saveVersionupPfmcEvalsht
	 */
	public ResultMap saveVersionupPfmcEvalsht(Map<String, Object> param) {
		Map<String, Object> pegInfo = (Map<String, Object>) param;

		// 1. 퍼포먼스 평가시트 copy
		String pfmcEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("PES");
		pegInfo.put("new_pfmc_evalsht_uuid", pfmcEvalshtUuid);
		pegInfo.put("evalsht_cd", evalshtCd);
		pfmcEvalshtSetupRepository.copyPfmcEvalsht(pegInfo);

		// 2. 퍼포먼스 평가시트 자가진단 대상 copy
		pfmcEvalshtSetupRepository.copyListPfmcSlfckSubj(pegInfo);

		// 3. 퍼포먼스 평가시트 항목별 담당자 copy
		pfmcEvalshtSetupRepository.copyListPfmcFactChrGrpEvaltr(pegInfo);

		return ResultMap.SUCCESS(pegInfo);
	}
	
	/**
	 * 퍼포먼스 평가시트를 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 24
	 * @Method Name : deletePfmcEvalsht
	 */
	public ResultMap deletePfmcEvalsht(Map<String, Object> param){
		Map<String, Object> pegInfo = (Map<String, Object>)param.get("pegInfo");
		Map<String, Object> pfmcEvalshtInfo = (Map<String, Object>)param.get("pfmcEvalshtInfo");
		
		// 1. 퍼포먼스 평가시트 사용여부 체크 (PE 생성 여부)
		String isCreatePeYn = pfmcEvalService.findCreatePeYnByPfmcEvalsht(pfmcEvalshtInfo);
		if ("Y".equals(isCreatePeYn)) {  // PE 생성 상태
			// param 상태와 불일치
			return ResultMap.INVALID();
		}

		// 2. DELETE
		this.updatePfmcEvalshtStsByDelete(pfmcEvalshtInfo);  // 퍼포먼스 시트 삭제

		return ResultMap.SUCCESS(pegInfo);
	}

	/**
	 * 퍼포먼스 평가시트 - 평가템플릿을 매핑한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 24
	 * @Method Name : saveMappingEvaltmplUuidToPfmcEvalsht
	 */
	public void saveMappingEvaltmplUuidToPfmcEvalsht(Map<String, Object> param){
		pfmcEvalshtSetupRepository.saveMappingEvaltmplUuidToPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트 - 수정 전 원본 평가템플릿을 매핑한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 4
	 * @Method Name : saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht
	 */
	public void saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht(Map<String, Object> param){
		pfmcEvalshtSetupRepository.saveMappingOrgnPfmcEvaltmplUuidToPfmcEvalsht(param);
	}
}
