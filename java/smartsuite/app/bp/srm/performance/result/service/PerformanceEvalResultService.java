package smartsuite.app.bp.srm.performance.result.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import smartsuite.app.common.PerformanceEvalException;
import smartsuite.app.bp.srm.performance.pfmcEval.event.PfmcEvalEventPublisher;
import smartsuite.app.bp.srm.performance.result.repository.PerformanceEvalResultRepository;
import smartsuite.app.common.event.PerformanceEventPublisher;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.PerformanceEvalDeleteService;
//import smartsuite.app.common.shared.service.PerformanceEvalSharedService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.PerformanceEvalStatusService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Performance Eval Result 관련 처리를 하는 서비스 Class입니다.
 *
 * @author hj.jang
 * @FileName PerformanceEvalResultService.java
 * @package smartsuite.app.bp.performance.result
 * @변경이력 : [2023. 6. 25] hj.jang 최초작성
 * @see
 * @since 2023. 06. 25
 */
@Service
@Transactional
public class PerformanceEvalResultService {

	@Inject
	PerformanceEvalResultRepository pfmcEvalResRepository;

	@Inject
	PerformanceEvalStatusService pfmcStatusService;

	@Inject
	PerformanceEvalDeleteService pfmcEvalDelService;

	//@Inject
	//PerformanceEvalSharedService pfmcEvalSharedService;

	@Inject
	private PerformanceEventPublisher pfmcEventPublisher;

	@Inject
	private PfmcEvalEventPublisher pfmcEvalEventPublisher;

	@Inject
	SharedService sharedService;

	/** The mail service. */
	@Inject
	MailService mailService;

	/**
	 * 퍼포먼스평가대상 결과 생성을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param (peInfo)
	 * @return the map< string, object>
	 * @Date : 2023.06.25
	 * @Method Name : createPeSubjResList
	 */
	public ResultMap createPeSubjResByPe (Map<String, Object> param) {

		// 퍼포먼스평가요청 상태 변경 - 생성 대기
		pfmcStatusService.saveRequestPfmcEval(param);

		// 퍼포먼스평가대상 결과 생성
		param.put(PfmcConst.SLFCK_RES_YN, PfmcConst.N_STR_VAL);
		pfmcEvalResRepository.createPeSubjResByPe(param);

		// 퍼포먼스평가 요청의 자체점검 대상여부가 Y인 경우 자체점검 퍼포먼스평가대상 결과를 생성한다.
		String slfckSubjYn = (String)param.getOrDefault(PfmcConst.SLFCK_SUBJ_YN, "N");
		if(PfmcConst.Y_STR_VAL.equals(slfckSubjYn)) {
			param.put(PfmcConst.SLFCK_RES_YN, PfmcConst.Y_STR_VAL);
			pfmcEvalResRepository.createPeSubjResByPe(param);
		}

		// 퍼포먼스 평가대상 결과, 퍼포먼스 평가자 정보를 조회, 평가생성을 요청한다.
		Map<String, Object> createParam = Maps.newHashMap(param);
		createParam.put(PfmcConst.REQ_TYPE, PfmcConst.PE);

		// 평가요청 결과 키값을 퍼포먼스 평가대상 결과, 퍼포먼스 평가자 테이블에 업데이트한다.
		List<Map<String, Object>> evalSubjAndEvaltrList = pfmcEvalResRepository.findListPeSubjResAndEvaltrForCreateRes(param);

		createParam.put(PfmcConst.EVAL_SUBJ_LIST, evalSubjAndEvaltrList);

		// 퍼포먼스 평가정보, 퍼포먼스 평가대상 및 평가자 목록으로 평가생성을 요청한다.
		ResultMap evalSubjResCreateResult = pfmcEventPublisher.createEvalSubjResEvalReq(createParam);

		if(evalSubjResCreateResult.isSuccess()) {
			// 평가생성 성공. 퍼포먼스 평가 진행상태 수정
			// 평가 생성 진행 상태 QUANT : 정량평가 진행 단계
			Map<String, Object> createPrgsMap = evalSubjResCreateResult.getResultData();
			if(PfmcConst.QUANT.equals(createPrgsMap.getOrDefault(PfmcConst.CREATE_PRGS_STS, PfmcConst.QUALI))) {
				pfmcStatusService.successCreateQuantEvalSubjRes(param);
			} 
			// 평가 생성 진행 상태 QUALI : 정성평가 진행 단계
			else {
				pfmcStatusService.successCreateQualiEvalSubjRes(param);
			}
			// 퍼포먼스 평가 관련 테이블에 평가공통 테이블의 키값을 저장한다.
			this.updatePeSubjResListEvalInfo(evalSubjResCreateResult.getResultList());

			this.sendMailEvaltrMailFulfill(createParam, PfmcConst.PE);
		} else {
			// 평가생성 실패
			pfmcStatusService.failCreateEvalSubjRes(param);
		}

		return evalSubjResCreateResult;
	}

	public void updatePeSubjResListEvalInfo(List<Map<String, Object>> evalSubjList) {
		if(evalSubjList != null) {
			for(Map<String, Object> evalSubj : evalSubjList) {
				// 퍼포먼스 평가대상 결과에 평가대상 결과 아이디를 업데이트 한다. (평가공통 평가대상 테이블 키 값 -> 업무평가 평가 테이블)
				pfmcEvalResRepository.updateEvalSubjResKey(evalSubj);

				List<Map<String, Object>> evalSubjEvaltrList = (List)evalSubj.getOrDefault(PfmcConst.EVAL_SUBJ_EVALTR_LIST, Lists.newArrayList());
				this.updatePeSubjEvaltrResListEvalInfo(evalSubjEvaltrList);
			}
		}
	}


	public void updatePeSubjEvaltrResListEvalInfo(List<Map<String, Object>> evalSubjEvaltrList) {
		if(evalSubjEvaltrList != null) {
			for(Map<String, Object> evalSubjEvaltr : evalSubjEvaltrList) {
				// 퍼포먼스 평가대상 평가자 결과에 평가대상 평가자 결과 아이디를 업데이트 한다.
				pfmcEvalResRepository.updateEvalSubjEvaltrResKey(evalSubjEvaltr);
			}
		}
	}

	/**
	 * 퍼포먼스평가 요청 대상 목록을 조회한다.
	 * @param : the param (peInfo)
	 * @return : the list (peSubjList)
	 * */
	public List findListPeSubjForCreateRes(Map<String, Object> param) {
		return pfmcEvalResRepository.findListPeSubjForCreateRes(param);
	}

	/**
	 * 퍼포먼스평가 대상 결과를 생성한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * param.slfck_res_yn - 평가대상 결과가 자체점검의 결과인지 여부 ( Y / N)
	 * @return the map< string, object> (peSubjInfo)
	 * @Date : 2023.06.25
	 * @Method Name : createPeSubjRes
	 */
	public ResultMap createPeSubjResByPeSubj(final Map<String, Object> param) {
		String peSubjResUuid = UUID.randomUUID().toString();
		param.put(PfmcConst.PE_SUBJ_RES_UUID, peSubjResUuid);

		// 1. 퍼포먼스평가 대상 결과 생성
		pfmcEvalResRepository.createPeSubjResByPeSubj(param);

		// 2. 평가대상 결과 생성을 요청한다.
		ResultMap evalSubjResResultMap = pfmcEventPublisher.createEvalSubjResAdd(param);

		// 3. 평가대상 결과의 키값을 저장한다. - 평가대상결과가 생성된 경우 키값을 퍼포먼스평가대상결과 테이블에 업데이트한다.
		if(!evalSubjResResultMap.isSuccess()){
			return ResultMap.FAIL("평가대상 결과 저장 실패 : " + evalSubjResResultMap.getResultMessage());
		} else {
			Map resultData = evalSubjResResultMap.getResultData();
			resultData.put("pe_subj_res_uuid", peSubjResUuid);
			pfmcEvalResRepository.updateEvalSubjResKey(resultData);

			// 4. 평가대상 평가자 결과 생성
			param.put(PfmcConst.EVAL_SUBJ_RES_UUID, resultData.get(PfmcConst.EVAL_SUBJ_RES_UUID));
			ResultMap evalSubjEvaltrResResultMap =  this.createPeSubjEvaltrResByPeSubjRes(param);
			if(!evalSubjEvaltrResResultMap.isSuccess()) {
				return evalSubjEvaltrResResultMap;
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스평가 대상 결과 아이디에 해당하는 퍼포먼스평가대상 결과 및 평가대상 결과를 생성한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * param.pe_subj_uuid - 퍼포먼스 평갇대상 아이디
	 * param.eval_subj_res_uuid - 평가대상 결과 아이디
	 * @return ResultMap
	 * @Date : 2023.06.25
	 * @Method Name : createPeSubjEvaltrResByPeSubjRes
	 */
	public ResultMap createPeSubjEvaltrResByPeSubjRes(final Map<String, Object> param) {
		Map<String, Object> createParam = Maps.newHashMap(param);
		createParam.put(PfmcConst.REQ_TYPE, PfmcConst.EVAL_SUBJ_RES);
		List<Map<String, Object>> peSubjEvaltrList = pfmcEvalResRepository.findListPeSubjEvaltrForCreateRes(createParam);

		if(peSubjEvaltrList != null) {
			for(Map<String, Object> peSubjEvaltr : peSubjEvaltrList) {

				this.createEvalSubjEvaltrResByPeSubjEvaltr(peSubjEvaltr);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스평가대상 평가자에 해당하는 평가대상 평가자 결과를 생성한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * param.eval_subj_res_uuid  - 평가대상 결과 아이디
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드
	 * param.evaltr_id - 평가자 아이디
	 * @return resultmap
	 * @Date : 2023.06.25
	 * @Method Name : createEvalSubjEvaltrResByPeSubjEvaltr
	 */
	public ResultMap createEvalSubjEvaltrResByPeSubjEvaltr(final Map<String, Object> param) {

		// 1. 평가대상 평가자 결과 생성
		ResultMap evaltrResResultMap = pfmcEventPublisher.createEvalSubjEvaltrRes(param);

		if(evaltrResResultMap.isSuccess()) {
			Map resultData = evaltrResResultMap.getResultData();
			// 2. 평가대상 결과의 키값을 저장한다. - 평가대상결과가 생성된 경우 키값을 퍼포먼스평가대상결과 테이블에 업데이트한다.
			resultData.put(PfmcConst.PE_SUBJ_EVALTR_UUID, param.get(PfmcConst.PE_SUBJ_EVALTR_UUID));
			pfmcEvalResRepository.updateEvalSubjEvaltrResKey(resultData);
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.FAIL("평가대상 평가자 결과 생성 실패 : " + evaltrResResultMap.getResultMessage());
		}
	}

	/**
	 * 퍼포먼스평가 결과 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 29
	 * @Method Name : findListPfmcEvalResult
	 */
	public List findListPfmcEvalResult(Map<String, Object> param) {
		return pfmcEvalResRepository.findListPfmcEvalResult(param);
	}

	/**
	 * 퍼포먼스평가 결과 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findPfmcEvalRes
	 */
	public Map<String, Object> findPfmcEvalRes(final Map<String, Object> param) {
		return pfmcEvalResRepository.findPfmcEvalRes(param);
	}
	/**
	 * 퍼포먼스평가 결과 평가그룹 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListPegByPe
	 */
	public List<Map<String, Object>> findListPegByPe(final Map<String, Object> param) {
		return pfmcEvalResRepository.findListPegByPe(param);
	}

	/**
	 * 퍼포먼스평가 결과 협력사관리그룹 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListVmgByPe
	 */
	public List<Map<String, Object>> findListVmgByPe(final Map<String, Object> param) {
		return pfmcEvalResRepository.findListVmgByPe(param);
	}

	/**
	 * 퍼포먼스평가 평가대상 결과 상세 목록 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : findListPeSubjResult
	 */
	public List<Map<String, Object>> findListPeSubjResult(final Map<String, Object> param) {
		return pfmcEvalResRepository.findListPeSubjResult(param);
	}

	/**
	 * 퍼포먼스평가 종합의견 목록 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 12. 29
	 * @Method Name : findListPeComprehensiveOpinion
	 */
	public List<Map<String, Object>> findListPeComprehensiveOpinion(final Map<String, Object> param) {
		return pfmcEvalResRepository.findListPeComprehensiveOpinion(param);
	}

	/**
	 * 퍼포먼스평가 대상 평가 재요청을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 6
	 * @Method Name : saveRePfmcEvalReq
	 */
	public ResultMap saveRePfmcEvalReq(final Map<String, Object> param) {
		Map<String, Object> peInfo = (Map<String, Object>) param.get("peInfo");
		List<Map<String, Object>> peSubjList = (List<Map<String, Object>>)param.get("peSubjList");

		if (peSubjList != null && !peSubjList.isEmpty()) {
			for (Map<String, Object> peSubj : peSubjList) {

				// 정성평가 진행상태 변경
				pfmcEventPublisher.updateCancleSubmEvaltrPrgsSts(peSubj);

				// 자체점검 결과 존재할 경우 자체점검 평가자의 정성평가 진행상태 변경
				if(!"".equals(peSubj.getOrDefault("slfck_eval_subj_res_uuid", ""))) {
					Map<String, Object> slfckPeSubj = Maps.newHashMap(peSubj);
					slfckPeSubj.put(PfmcConst.EVAL_SUBJ_RES_UUID, slfckPeSubj.get("slfck_eval_subj_res_uuid"));
					pfmcEventPublisher.updateCancleSubmEvaltrPrgsSts(slfckPeSubj);
				}
			}
		}

		// 평가 마스터 정성평가상태로 변경
		pfmcStatusService.updateCancleCmpldPfmcEval(peInfo);

		return ResultMap.SUCCESS();

		//return pfmcEvalResRepository.saveRePfmcEvalReq(param);
	}

	/**
	 * 평가그룹으로 퍼포먼스 평가 결과 조회를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 10
	 * @Method Name : findPeGrdCntByPeg
	 */
	public Map findPeGrdCntByPeg(Map<String, Object> param) {
		Map<String, Object> pfmcEvalResultPeg = Maps.newHashMap();
		List<String> evalGrdCds = pfmcEvalResRepository.findListPeEvalGrd(param);

		List<Map<String,Object>> peGrdCnts = pfmcEvalResRepository.findListPeGrdCntByPeg(param);
		pfmcEvalResultPeg.put("evalGrdCds", evalGrdCds);
		pfmcEvalResultPeg.put("peGrdCnts", peGrdCnts);

		return pfmcEvalResultPeg;
	}

	/**
	 * 퍼포먼스평가요청 평가대상 평가항목 결과를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the list
	 * @Date : 2023. 07. 16
	 * @Method Name : findListPeSubjEvalfactResult
	 */
	public List findListPeSubjEvalfactResult(Map<String, Object> param) {
//		Map<String, Object> peSubjEvalfactMap = Maps.newHashMap(param);
//
//		// 퍼포먼스 평가대상 결과 조회
//		List<Map> peSubjResultList = pfmcEvalResRepository.findListPeSubjResult(param);
//		// 평가대상 평가항목 결과 조회 (평가항목별 점수)
//		List<Map> evalSubjEvalfactResultList = (List)pfmcEventPublisher.findListEvalSubjEvalfactRes(peSubjEvalfactMap);
//
//		String[] joinKeys = {PfmcConst.TEN_ID, PfmcConst.EVAL_SUBJ_RES_UUID};
//		// 퍼포먼스 평가대상 결과와 평가대상 평가항목 결과를 merge한다.
//		return ListUtils.innerJoin(evalSubjEvalfactResultList, peSubjResultList, Lists.newArrayList(joinKeys), null);
		return pfmcEvalResRepository.findListPeSubjEvalfactResult(param);
	}

	/**
	 * 퍼포먼스평가요청 평가항목 의견 상세 목록 조회한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the list
	 * @Date : 2023. 12. 29
	 * @Method Name : findListPeSubjEvalfactOpinion
	 */
	public List findListPeSubjEvalfactOpinion(Map<String, Object> param) {
		return pfmcEvalResRepository.findListPeSubjEvalfactOpinion(param);
	}

	/**
	 * 퍼포먼스 평가대상 결과 조정 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 16
	 * @Method Name : findPeSubjResAdjInfo
	 */
	public Map findPeSubjResAdjInfo(Map<String, Object> param) {
		return pfmcEvalResRepository.findPeSubjResAdjInfo(param);
	}
	/**
	 * 퍼포먼스 평가대상 결과 조정 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 21
	 * @Method Name : savePeSubjResAdjInfo
	 */
	public ResultMap savePeSubjResAdjInfo(Map<String, Object> param) {
		pfmcEvalResRepository.updatePeSubjResAdjInfo(param);

		Map<String, Object> adjParam = Maps.newHashMap(param);
		adjParam.put(PfmcConst.REQ_TYPE, PfmcConst.PE_SUBJ_RES);
		adjParam.put(PfmcConst.RE_CALC_YN, PfmcConst.Y_STR_VAL);

		this.calculatePeSubjResGrdRank(adjParam);
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가대상 결과 등급을 계산한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 21
	 * @Method Name : calculatePeSubjResRank
	 */
	public ResultMap calculatePeSubjResRank(Map<String, Object> param) {
		//그룹 Rank,Cnt 대상 수 조회
		pfmcEvalResRepository.calculatePeSubjResRank(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가대상 결과 등급을 계산한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 21
	 * @Method Name : calculatePeSubjResGrade
	 */
	public ResultMap calculatePeSubjResGrade(Map<String, Object> param) {
		//그룹 Rank,Cnt 대상 수 조회
		pfmcEvalResRepository.calculatePeSubjResGrade(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가 마감 가능 여부를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 23
	 * @Method Name : checkClosablePfmcEval
	 */
	public Map checkClosablePfmcEval(Map<String, Object> param) {
		// 퍼포먼스 평가의 평가대상 수 / 평가항목별 평가자 그룹 수 / 제출 수 / 저장 수 / 작성 전 수
		return pfmcEvalResRepository.checkClosablePfmcEval(param);
	}

	/**
	 * 퍼포먼스 평가를 마감한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 23
	 * @Method Name : closePfmcEval
	 */
	public ResultMap closePfmcEval(Map<String, Object> param) {
		String closedPfmcEvalYn = (String) pfmcEvalResRepository.getPfmcEvalStatusClosedYn(param);
		String username = Auth.getCurrentUserName();

		if(PfmcConst.N_STR_VAL.equals(closedPfmcEvalYn)) {
			pfmcStatusService.startClosePfmcEval(param);
		//	try {
				// 평가 집계를 요청한다.
				param.put(PfmcConst.EVAL_REQ_UUID, param.getOrDefault(PfmcConst.PE_UUID, ""));
				pfmcEvalEventPublisher.calculateEvalSubjResByEvalReqWithCalcfact(param);

				// 평가 실적항목 존재 시 퍼포먼스 평가대상 실적항목 테이블로 복사한다.
				this.savePeSubjCalcfactByCopyEvalSubjCalcfact(param);

				// 퍼포먼스 평가대상의 점수를 업데이트한다.
				this.updatePeSubjResEvalScByEvalSubjRes(param);

				// 퍼포먼스 평가대상의 점수로 등급 및 순위를 계산한다.
				this.calculatePeSubjResGrdRank(param);
		//	} catch(PerformanceEvalException peExcption) {
//				 pfmcEvalSharedService.writeEvalErrorLog(peExcption);
//				throw new CommonException(ErrorCode.FAIL, peExcption);
//			}
			pfmcStatusService.endClosePfmcEval(param);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가 마감을 해제한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 23
	 * @Method Name : cancelClosedPfmcEval
	 */
	public ResultMap cancelClosedPfmcEval(Map<String, Object> param) {
		pfmcStatusService.cancelClosedPfmcEval(param);
		return ResultMap.SUCCESS();
	}

	public void calculatePeSubjResGrdRank(Map<String, Object> param) {
		Map<String,Object> calculateParam = Maps.newHashMap(param);

		calculateParam.put(PfmcConst.REQ_TYPE, param.getOrDefault(PfmcConst.REQ_TYPE, PfmcConst.PE));
		calculateParam.put(PfmcConst.RE_CALC_YN, param.getOrDefault(PfmcConst.RE_CALC_YN, PfmcConst.N_STR_VAL));
		this.calculatePeSubjResGrade(calculateParam);
		this.calculatePeSubjResRank(calculateParam);
	}

	/**
	 * 평가공통의 평가대상 실적항목 목록을 복사하여 퍼포먼스 평가대상 실적항목 목록에 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 23
	 * @Method Name : savePeSubjCalcfactByCopyEvalSubjCalcfact
	 */
	public void savePeSubjCalcfactByCopyEvalSubjCalcfact(Map<String, Object> param) {

		// 평가대상 결과 목록
		List<Map<String, Object>> calcfactResList = pfmcEventPublisher.findListEvalSubjCalcfactRes(param);
		if(calcfactResList != null && !calcfactResList.isEmpty()) {
			for(Map<String, Object> calcfact : calcfactResList) {
				pfmcEvalResRepository.mergePeSubjCalcfactRes(calcfact);
			}
		}
	}

	/**
	 * 평가 대상 결과를 가져와서 퍼포먼스 평가 대상 결과에 업데이트 한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 23
	 * @Method Name : updatePeSubjResByEvalSubjRes
	 */
	public void updatePeSubjResEvalScByEvalSubjRes(Map<String, Object> param) {

		// 평가대상 결과 목록
		List<Map<String, Object>> evalSubjResList = pfmcEventPublisher.findListEvalSubjRes(param);
		if(evalSubjResList != null && !evalSubjResList.isEmpty()) {
			for(Map<String, Object> evalSubj : evalSubjResList) {
				pfmcEvalResRepository.updatePeSubjResEvalSc(evalSubj);
			}
		}
	}

	/**
	 * 퍼포먼스 평가 결과 > 정량항목 결과 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPeQuantEvalfactResult
	 */
	public List findListPeQuantEvalfactResult(Map<String, Object> param) {
		return pfmcEvalResRepository.findListPeQuantEvalfactResult(param);
	}

	/**
	 * 퍼포먼스 평가 대상 정량항목의 계산항목 결과를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPeSubjCalcfactResult
	 */
	public List findListPeSubjCalcfactResult(Map<String, Object> param) {

		return pfmcEvalResRepository.findListPeSubjCalcfactResult(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPfmcEvalDetailScoreInfo
	 */
	public Map findListPfmcEvalDetailScoreInfo(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		// 평가 결과 상세(평가 항목별 점수)
		resultMap.put("detailScoreList", pfmcEvalResRepository.findListPfmcEvalDetailScoreInfo(param));

		// 평가 대상의 이전 평가 결과 리스트(차트 비교용)
		resultMap.put("prePfmcResultList", pfmcEvalResRepository.findListEvalSubjPrePfmcResult(param));
		return resultMap;
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 항목별 점수 팝업 > 항목 별 이전 평가 결과 리스트 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the Result map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListPfmcEvalDetailScoreInfo
	 */
	public List findListPreFactScoreInfo(Map<String, Object> param) {
		return pfmcEvalResRepository.findListPreFactScoreInfo(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정정보 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListAdjCalcfactByFact
	 */
	public List findListAdjCalcfactByFact(Map<String, Object> param) {

		return pfmcEvalResRepository.findListAdjCalcfactByFact(param);
	}
	/**
	 * 퍼포먼스 평가 결과 > 평가에 포함된 모든 계산항목 목록 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 24
	 * @Method Name : findListCalcfactByPeUsed
	 */
	public List findListCalcfactByPeUsed(Map<String, Object> param) {
		return pfmcEvalResRepository.findListCalcfactByPeUsed(param);
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : findPeSubjCalcfactAdj
	 */
	public Map findPeSubjCalcfactAdj(Map<String, Object> param) {
		return pfmcEvalResRepository.findPeSubjCalcfactAdj(param);
	}
	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 조정 정보 조회
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : savePeSubjCalcfactAdj
	 */
	public ResultMap savePeSubjCalcfactAdj(Map<String, Object> param) {
		// 조정정보 저장
		param.put("adj_yn", "Y");
		pfmcEvalResRepository.updatePeSubjCalcfactAdj(param);

		// 조정정보로 평가공통 계산항목 수정을 요청한다.
		Map<String, Object> calcfactAdj = findPeSubjCalcfactAdj(param);
		ResultMap updateResult = pfmcEventPublisher.updateEvalSubjCalcfactByAdj(calcfactAdj);

		return updateResult;
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 집계제외여부 목록 저장
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : savePeSubjCalcfactAdjList
	 */
	public ResultMap savePeSubjCalcfactAdjList(Map<String, Object> param) {
		Map<String,Object> peInfo = (Map<String, Object>) param.getOrDefault("peInfo", Maps.newHashMap());
		String adjustableYn = pfmcEvalResRepository.selectPfmcEvalResultAdjustableYn(peInfo);

		if ("Y".equals(adjustableYn)) {
			List<Map<String, Object>> updateCalcfacts = (List<Map<String, Object>>) param.getOrDefault("updateCalcfacts", Lists.newArrayList());
			for (Map<String, Object> calcfact : updateCalcfacts) {
				pfmcEvalResRepository.updatePeSubjCalcfactAdj(calcfact);
				pfmcEventPublisher.updateEvalSubjCalcfactByAdj(calcfact);
			}
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.INVALID();
		}
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 정량 평가항목 결과 재계산 호출
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : reCalculateQuantEvalfact
	 */
	public ResultMap reCalculateQuantEvalfact(Map<String, Object> param) {

		String adjustableYn = pfmcEvalResRepository.selectPfmcEvalResultAdjustableYn(param);

		if ("Y".equals(adjustableYn)) {
			// 정성항목 결과, 실적항목 실측값 집계 제외한 평가결과 재 계산 요청
			pfmcEventPublisher.reCalculateQuantEvalfact(param);
			pfmcEvalResRepository.updatePeSubjCalcfactCalcReqdYn(param);
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.INVALID();
		}
	}

	/**
	 * 퍼포먼스 평가 결과 > 계산항목 조정 탭 > 계산항목 재집계 호출
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 26
	 * @Method Name : reCalculateCalcfactVal
	 */
	public ResultMap reCalculateCalcfactVal(Map<String, Object> param) {

		String adjustableYn = pfmcEvalResRepository.selectPfmcEvalResultAdjustableYn(param);
		final String closeYn = pfmcEvalResRepository.getPfmcEvalStatusClosedYn(param);

		// 퍙기결과 집계 전이거나 평가결과 집계완료 상태인경우에만 재집계가능
		if ("N".equals(closeYn) || "Y".equals(adjustableYn)) {
			// 재집계 제외 목록을 조회한다.
			List<Map<String, Object>> datCollXceptList = pfmcEvalResRepository.findListDatCollXceptCalcfact(param);
			param.put(PfmcConst.DAT_COLL_XCEPT_CALCFACT_LIST, datCollXceptList);
			// 평가대상 계산항목 재집계를 요청한다.
			pfmcEventPublisher.reCalculateCalcfactVal(param);
			// 평가대상 계산항목 값을 가져와 퍼포먼스 평가대상 계산항목 테이블에 저장한다.
			this.savePeSubjCalcfactByCopyEvalSubjCalcfact(param);
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.INVALID();
		}
	}

	public void sendMailEvaltrMailFulfill (Map<String, Object> param, String reqType) {
		Map<String, Object> evaltrParam = Maps.newHashMap();
		evaltrParam.put(PfmcConst.REQ_TYPE, reqType);

		// 평가 생성 시 일괄 메일 발송
		if(PfmcConst.PE.equals(reqType)) {
			evaltrParam.put(PfmcConst.PE_UUID, param.get(PfmcConst.PE_UUID));
		}
		// 평가 대상 추가 시 추가 대상의 평가자에게 메일 발송
		else if(PfmcConst.PE_SUBJ.equals(reqType)) {
			evaltrParam.put(PfmcConst.PE_SUBJ_UUID, param.get(PfmcConst.PE_SUBJ_UUID));
		}
		// 평가자 추가 시 추가된 평가자에게 메일 발송
		else if(PfmcConst.PE_SUBJ_EVALTR.equals(reqType)) {
			evaltrParam.put(PfmcConst.PE_SUBJ_EVALTR_UUID, param.get(PfmcConst.PE_SUBJ_EVALTR_UUID));
		} 

		// 평가자 목록 조회
		List<Map<String, Object>> peSubjEvaltrList = pfmcEvalResRepository.findListPeSubjEvaltrForMail(evaltrParam);
		if(peSubjEvaltrList != null) {
			for(Map<String, Object> peSubjEvaltr : peSubjEvaltrList) {
				// 평가자에게 메일 발송
				peSubjEvaltr.put(PfmcConst.REQ_TYPE, reqType);
				mailService.sendAsync("REQUEST_FULFILL_PFMC_EVAL", null, peSubjEvaltr);
			}
		}
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가대상 결과 탭 > 개선관리 등록
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 08. 10
	 * @Method Name : savePeSubjResultImprove
	 */
	public ResultMap savePeSubjResultImprove(Map<String, Object> param) {

		Map resultData = Maps.newHashMap();

		// 등록 요청한 퍼포먼스평가대상 결과
		List<Map<String, Object>> insertVdImproveList = (List<Map<String, Object>>)param.getOrDefault("insertVdImproveList", Lists.newArrayList());

		List<Map<String, Object>> registeredList = Lists.newArrayList(); // 기존에 개선 등록된 평가대상 결과 (중복 요청건)
		List<Map<String, Object>> newRegisteredList = Lists.newArrayList(); // 신규로 개선 등록한 평가대상 결과
		for(Map<String, Object> insertVdImprove : insertVdImproveList) {
			// 개선 중복등록 허용하지 않을 경우 아래 로직 주석 해제
			// 협력사 개선 조회
			//List<Map<String, Object>> existedVdImproveList = pfmcEventPublisher.findListPeSubjVdImprove(insertVdImprove);
			//if(existedVdImproveList != null && !existedVdImproveList.isEmpty()) {
			//	registeredList.add(insertVdImprove);
			// } else {
				String vdImproveUuid = UUID.randomUUID().toString();
				insertVdImprove.put("new_vd_improv_uuid", vdImproveUuid);
				pfmcEventPublisher.createPeSubjVdImprove(insertVdImprove);
				insertVdImprove.put("vd_improv_uuid", vdImproveUuid);
				newRegisteredList.add(insertVdImprove);
			//}
		}
		resultData.put("registeredList", registeredList);
		resultData.put("newRegisteredList", newRegisteredList);

		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 결과 상세화면 > 평가 완료
	 * 퍼포먼스 평가 결과를 결재없이 확정한다.
	 * @author : hj.jang
	 * @param peInfo
	 * @return the map
	 * @Date : 2023. 08. 14
	 * @Method Name : confirmPfmcEvalResult
	 */
	public ResultMap confirmPfmcEvalResult(Map<String, Object> peInfo) {
		pfmcStatusService.confirmPfmcEvalResult(peInfo);
		return ResultMap.SUCCESS();
	}

}
