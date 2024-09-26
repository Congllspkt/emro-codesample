package smartsuite.app.sp.srm.eval.result.appeal.service;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.srm.eval.result.appeal.repository.SpAppealRepository;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class SpAppealService {

	@Inject
	private SpAppealRepository spAppealRepository;

	/**
	 * 이의제기 현황 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 17
	 */
	public List findAppealList(Map param) {
		List<Map<String, Object>> resultList = spAppealRepository.findAppealList(param);
		List<Map<String, Object>> AppealList = new ArrayList<>();

		// 정성평가항목 count 와 계산항목 count 합산
		for(Map<String, Object> appeal : resultList) {
			int submCount = Integer.parseInt(String.valueOf(appeal.get("qfact_subm_count"))) + Integer.parseInt(String.valueOf(appeal.get("cfact_subm_count")));
			int rcptCount = Integer.parseInt(String.valueOf(appeal.get("qfact_rcpt_count"))) + Integer.parseInt(String.valueOf(appeal.get("cfact_rcpt_count")));
			int retCount = Integer.parseInt(String.valueOf(appeal.get("qfact_ret_count"))) + Integer.parseInt(String.valueOf(appeal.get("cfact_ret_count")));
			int apvdCount = Integer.parseInt(String.valueOf(appeal.get("qfact_apvd_count"))) + Integer.parseInt(String.valueOf(appeal.get("cfact_apvd_count")));

			appeal.put("subm_count", submCount);  // 이의제기 제출건수
			appeal.put("rcpt_count", rcptCount);  // 이의제기 접수건수
			appeal.put("ret_count", retCount);    // 이의제기 반려건수
			appeal.put("apvd_count", apvdCount);  // 이의제기 처리완료건수

			AppealList.add(appeal);
		}

		return AppealList;
	}

	/**
	 * 평가결과 정성 평가항목 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 20
	 */
	public Map findListPeSubjFactorResult(Map param) {

		Map<String, List<Map<String, Object>>> evalFactorList = new HashMap<>();

		List<Map<String, Object>> quantfactorList = new ArrayList<>();
		List<Map<String, Object>> qualifactorList = new ArrayList<>();

		// 계산항목 list
		quantfactorList = spAppealRepository.findListPeSubjCalcfactorResult(param);

		// 정성항목 list
		qualifactorList = spAppealRepository.findListPeSubjQualifactorResult(param);

		evalFactorList.put("quantfactorList", quantfactorList);
		evalFactorList.put("qualifactorList", qualifactorList);

		return evalFactorList;
	}

	/**
	 * 정성 평가항목 이의제기를 요청한다. (협력사 -> 구매사 이의제기)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 22
	 */
	public ResultMap saveAppealQualiEvalfact(Map param) {
		Map<String, Object> qualifactInfo = (Map<String, Object>) param.get("qualifactInfo");
		String saveType = (String) param.get("saveType");

		String vdAppealQualiEvalfactUuid = (String)qualifactInfo.get("vd_appeal_quali_evalfact_uuid"); // 평가항목 이의제기 uuid

		if(!Strings.isNullOrEmpty(saveType) && saveType.equals("submit")) {
			qualifactInfo.put("appeal_subm_sts_ccd", "APPEAL_SUBM");   // 이의제기 요청
		} else if(!Strings.isNullOrEmpty(saveType) && saveType.equals("temp")) {
			qualifactInfo.put("appeal_req_dt", null);
			qualifactInfo.put("appeal_subm_sts_ccd", "APPEAL_CRNG");   // 이의제기 임시저장
		}

		if(Strings.isNullOrEmpty(vdAppealQualiEvalfactUuid)) { // 신규
			vdAppealQualiEvalfactUuid = (String)UUID.randomUUID().toString();
			qualifactInfo.put("vd_appeal_quali_evalfact_uuid", vdAppealQualiEvalfactUuid);
			spAppealRepository.insertAppealQualiEvalfact(qualifactInfo);
		} else {
			spAppealRepository.updateAppealQualiEvalfact(qualifactInfo);
		}

		return ResultMap.SUCCESS();

	}

	/**
	 * 이의제기 제출 현황 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	public List findAppealSubmList(Map param) {
		return spAppealRepository.findAppealSubmList(param);
	}

	/**
	 * 이의제기를 제출을 취소한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	public ResultMap cancelAppealList(Map param) {

		List<Map<String, Object>> quantList = (List<Map<String, Object>>) param.get("quantList"); // 정량
		List<Map<String, Object>> qualiList = (List<Map<String, Object>>) param.get("qualiList"); // 정성

		for(Map<String, Object> item : quantList) {
			String vdAppealCalcfactUuid = (String) item.get("vd_appeal_fact_uuid");
			item.put("vd_appeal_calcfact_uuid", vdAppealCalcfactUuid);
			spAppealRepository.cancelAppealQuantList(item);
		}

		for(Map<String, Object> item : qualiList) {
			String vdAppealQualiEvalfactUuid = (String) item.get("vd_appeal_fact_uuid");
			item.put("vd_appeal_quali_evalfact_uuid", vdAppealQualiEvalfactUuid);
			spAppealRepository.cancelAppealQualiList(item);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사 관리 그룹 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	public List findListVmg(Map param) {
		return spAppealRepository.findListVmg(param);
	}

	/**
	 * 계산항목 이의제기를 요청한다. (협력사 -> 구매사 이의제기)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 26
	 */
	public ResultMap saveAppealCalcfact(Map param) {
		Map<String, Object> quantfactInfo = (Map<String, Object>) param.get("quantfactInfo");
		String saveType = (String) param.get("saveType");

		String vdAppealCalcfactUuid = (String) quantfactInfo.get("vd_appeal_calcfact_uuid"); // 계산항목 이의제기 uuid

		if(!Strings.isNullOrEmpty(saveType) && saveType.equals("submit")) {
			quantfactInfo.put("appeal_subm_sts_ccd", "APPEAL_SUBM"); // 제출상태(R901) : 이의제기 요청
		} else if(!Strings.isNullOrEmpty(saveType) && saveType.equals("temp")) {
			quantfactInfo.put("appeal_req_dt", null);
			quantfactInfo.put("appeal_subm_sts_ccd", "APPEAL_CRNG"); // 제출상태(R901) : 이의제기 임시저장
		}

		if(Strings.isNullOrEmpty(vdAppealCalcfactUuid)) { // 신규
			vdAppealCalcfactUuid = UUID.randomUUID().toString();
			quantfactInfo.put("vd_appeal_calcfact_uuid", vdAppealCalcfactUuid);
			spAppealRepository.insertAppealCalcfact(quantfactInfo);
		} else { // 수정
			spAppealRepository.updateAppealCalcfact(quantfactInfo);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * (정성 평가항목) 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	public Map findAppealQualiDetail(Map param) {

		String vdAppealQualiEvalfactUuid = (String) param.get("vd_appeal_fact_uuid");
		param.put("vd_appeal_quali_evalfact_uuid", vdAppealQualiEvalfactUuid);

		return spAppealRepository.findAppealQualiDetail(param);
	}

	/**
	 * (계산항목) 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	public Map findAppealCalcDetail(Map param) {
		String vdAppealCalcfactUuid = (String) param.get("vd_appeal_fact_uuid");
		param.put("vd_appeal_calcfact_uuid", vdAppealCalcfactUuid);

		return spAppealRepository.findAppealCalcDetail(param);
	}

	/**
	 * (계산항목) 이의제기(임시저장 건)를 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	public ResultMap deleteAppealCalcfact(Map param) {
		Map<String, Object> quantfactInfo = (Map<String, Object>) param.get("quantfactInfo");
		spAppealRepository.deleteAppealCalcfact(quantfactInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * (정성 평가항목) 이의제기(임시저장 건)를 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	public ResultMap deleteAppealQualiEvalfact(Map param) {
		Map<String, Object> qualifactInfo = (Map<String, Object>) param.get("qualifactInfo");
		spAppealRepository.deleteAppealQualiEvalfact(qualifactInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가그룹 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	public List findListPegByPe(Map param) {
		return spAppealRepository.findListPegByPe(param);

	}
}
