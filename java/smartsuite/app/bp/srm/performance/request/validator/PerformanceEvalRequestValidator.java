package smartsuite.app.bp.srm.performance.request.validator;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.srm.performance.request.repository.PerformanceEvalRequestRepository;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class PerformanceEvalRequestValidator {
	
	@Inject
	PerformanceEvalRequestRepository pfmcReqRepository;


	/**
	 * 평가요청정보 유효한 상태인지 체크 (퍼포먼스 평가 조회, 저장 시 확인) <br>
	 * 평가가 이미 생성되어 있고 요청하기 전인 경우 체크 대상. 체크 대상 아닌 경우 return SUCCESS
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap (resultList field : EFCT_EVALSHT_YN, EFCT_VMG_YN, EFCT_AUTHTY_YN, EXISTS_PE_SUBJ_YN, EXISTS_PE_EVALTR_YN)
	 * @Date : 2023. 07. 26
	 * @Method Name : validatePeInfo
	 */
	public ResultMap validatePeInfo(Map param) {
		String peUuid = (String) param.get("pe_uuid");
		List<Map<String, Object>> invalidList = Lists.newArrayList();

		final String requestedYn = pfmcReqRepository.getPfmcEvalStatusRequestedYn(param);

		if(peUuid != null && requestedYn != null && PfmcConst.N_STR_VAL.equals(requestedYn)) {

			// (유효 평가시트 존재 체크, 평가대상 협력사관리그룹이 평가그룹에 연결되어 있는지 체크, 평가자 존재 체크, 평가항목권한 담당자 유효성 체크)
			List<Map<String, Object>> checkList = pfmcReqRepository.checkValidPfmcReqBeforeSave(param);
			for(Map<String, Object> checkPeg : checkList) {
				// "_yn" 이 포함된 필드 중 값이 Y 가 아닌 데이터 존재하면 유효성 false로 판단
				List invalidFieldList = this.getInvalidFieldListByFieldValue(checkPeg, "_yn", PfmcConst.Y_STR_VAL);
				if(invalidFieldList.size() > 0) {
					checkPeg.put("invalidFieldList", invalidFieldList);
					invalidList.add(checkPeg);
				}
			}
			return ResultMap.FAIL(invalidList);
		} else {
			return ResultMap.SUCCESS(invalidList);
		}
	}

	public ResultMap validateSavePeSubj(Map pe, Map pePeg, Map pfmcEvalsht, List peSubjList) {
		if(pe == null || pe.isEmpty()) {
			return ResultMap.FAIL(this.getInValidMessage(PfmcConst.NOT_EXISTS, PfmcConst.PE));
		} else if(pePeg == null || pePeg.isEmpty()) {
			return ResultMap.FAIL(this.getInValidMessage(PfmcConst.NOT_EXISTS, PfmcConst.PE_PEG));
		}  else if(pfmcEvalsht == null || pfmcEvalsht.isEmpty()) {
			return ResultMap.FAIL(this.getInValidMessage(PfmcConst.NOT_EXISTS, PfmcConst.PE_PEG));
		} else {
			return ResultMap.SUCCESS(); // 퍼포먼스 평가 저장 가능 상태 확인.
		}
	}

	private String getInValidMessage(String invalidType, String subj) {
		StringBuilder sb = new StringBuilder();
		sb.append(invalidType);
		sb.append(", ");
		sb.append(subj);
		return sb.toString();
	}

	public ResultMap getRequestPfmcEvalSts(String peStsCcd) {
		List requestablePeStsCcd = Arrays.asList(new String[] {PfmcConst.PE_STS_CRNG, PfmcConst.PE_STS_CRN_WTG, PfmcConst.PE_STS_CRN_ERR});
		if(requestablePeStsCcd.contains(peStsCcd)) {
			return ResultMap.SUCCESS();
		} else {
			return ResultMap.FAIL();
		}
	}

	// 평가자 존재여부 판단
	public ResultMap isExistPeSubjEvaltr(Map param) {
		String existPeSubjEvaltrYn = pfmcReqRepository.existPeSubjEvaltrYn(param);
		if(existPeSubjEvaltrYn != null && PfmcConst.Y_STR_VAL.equals(existPeSubjEvaltrYn)) {
			return ResultMap.DUPLICATED();
		} else {
			return ResultMap.NOT_EXISTS();
		}
	}

	// 평가그룹의 협력사관리그룹과 맞지 않는 퍼포먼스 평가대상 협력사관리그룹 조회
	public List findListInvalidPeSubjByPegVmg(Map param) {
		return pfmcReqRepository.findListInvalidPeSubjByPegVmg(param);
	}

	// map의 key 중 특정 문자열을 포함하는 키의 값이 validStr 다른 key 목록 resturn
	private List getInvalidFieldListByFieldValue(Map<String, Object> checkData, String containFieldStr, String validStr) {
		List<String> invalidFields = Lists.newArrayList();
		List keys = Arrays.asList(checkData.keySet().toArray());
		for(Object key : keys) {
			String keyStr = (String)key;
			if(keyStr.contains(containFieldStr) && !validStr.equals(checkData.get(keyStr))) {
				invalidFields.add(keyStr);
			}
		}
		return invalidFields;
	}

}