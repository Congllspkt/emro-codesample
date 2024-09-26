package smartsuite.app.bp.onboarding.result.service;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.result.event.OnboardingEvalResultEventPublisher;
import smartsuite.app.bp.onboarding.result.repository.OnboardingEvalResultRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * obd eval result 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName OnboardingEvalResultService.java
 * @package smartsuite.app.bp.onboarding.result.service
 * @Since 2023. 6. 18
 * @변경이력 : [2023. 6. 18] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class OnboardingEvalResultService {

	@Inject
	private OnboardingEvalResultRepository onboardingEvalResultRepository;

	@Inject
	private OnboardingEvalResultEventPublisher onboardingEvalResultEventPublisher;

	/**
	 * 온보딩평가 완료 처리대기 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 25
	 * @Method Name : findListCompleteOnboardingEval
	 */
	public FloaterStream findListCompleteOnboardingEval(Map param) {
		// 대용량 처리
		return onboardingEvalResultRepository.findListCompleteOnboardingEval(param);
	}

	/**
     * 온보딩평가 품의 - 미등록완료 한다.
     *
	 * @author : yjPark
     * @param param the param
     * @return the map
	 * @Date : 2023. 6. 27
	 * @Method Name : saveListOnboardingEvalUnRegComplete
     */
    public ResultMap saveListOnboardingEvalUnRegComplete(Map<String, Object> param) {
        List<Map<String, Object>> lists = (List<Map<String, Object>>)param.get("checkList");

        for(Map<String, Object> row : lists){
            onboardingEvalResultRepository.saveListOnboardingEvalUnRegCompleteOe(row);       // 1. 온보딩평가 미등록완료 저장 (sts = 'D')
        }

        return ResultMap.SUCCESS(lists.get(0));
    }

	/**
	 * 협력사 변경 상태 조회 (findVendorModifyStatus)
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the vdHistrecInfo
	 * @Date : 2023. 7. 14
	 * @Method Name : findVendorModifyStatus
	 */
	public Map findVendorModifyStatus(Map param) {
		return onboardingEvalResultEventPublisher.findVendorModifyStatus(param);
	}

	/**
	 * 온보딩평가 완료 처리대기 목록 조회를 요청한다.
	 *
	 * @author : jsKim
	 * @param param the param
	 * @return the obd eval record list
	 * @Date : 2023. 7. 10
	 * @Method Name : findListOnboardingEvalhistrec
	 */
	public FloaterStream findListOnboardingEvalHistrec(Map param) {
		// 대용량 처리
		return onboardingEvalResultRepository.findListOnboardingEvalHistrec(param);
	}

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID를 저장한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 7
	 * @Method Name : saveOnboardingEvalVendorAprvInfo
	 */
	public void saveOnboardingEvalVendorAprvInfo(Map<String, Object> param) {
		onboardingEvalResultRepository.saveOnboardingEvalVendorAprvInfo(param);
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상여부를 변경한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 7
	 * @Method Name : saveOnboardingEvalVmgAprvSubj
	 */
	public void saveOnboardingEvalVmgAprvSubj(Map<String, Object> param) {
		onboardingEvalResultRepository.saveOnboardingEvalVmgAprvSubj(param);
	}

	/**
	 * 온보딩평가 요청 처리완료한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 7
	 * @Method Name : saveOnboardingEvalPrcsgEnd
	 */
	public void saveOnboardingEvalPrcsgEnd(Map<String, Object> param) {
		onboardingEvalResultRepository.saveOnboardingEvalPrcsPrcsgEnd(param);
		onboardingEvalResultRepository.saveOnboardingEvalPrcsgEnd(param);
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상 취소한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 7
	 * @Method Name : saveCancelOnboardingEvalVmgAprvSubj
	 */
	public void saveCancelOnboardingEvalVmgAprvSubj(Map<String, Object> param) {
		onboardingEvalResultRepository.saveCancelOnboardingEvalVmgAprvSubj(param);
	}

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID을 초기화한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 7
	 * @Method Name : saveCancelOnboardingEvalVendorAprvInfo
	 */
	public void saveCancelOnboardingEvalVendorAprvInfo(Map<String, Object> param) {
		onboardingEvalResultRepository.saveCancelOnboardingEvalVendorAprvInfo(param);
	}

	/**
	 * 협력사별 온보딩 프로세스 결과 리스트를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the List<<map< string, object>>
	 * @Date : 2023. 6. 29
	 * @Method Name : findVendorObdEvalInfoList
	 */
	public List findVendorObdEvalInfoList(Map param) {
    	return onboardingEvalResultRepository.findVendorObdEvalInfoList(param);
	}

	/**
	 * 협력사 VMT, VMG 정보를 조죄한다.
	 *
	 * @author : kim
	 * @param param {vd_cd, oorg_cd}
	 * @return the map< string, object>
	 * @Date : 2024. 4. 11
	 * @Method Name : findVendorVMTVMGInfo
	 */
	public Map findVendorVMTVMGInfo(Map param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("vendorManagementTypeList", onboardingEvalResultRepository.findListVendorManagementTypeInfo(param)); // 협력사-운영조직 : 협력사관리유형 정보
		resultMap.put("obdEdMgmtGrpList", onboardingEvalResultRepository.findListVendorManagementGroup(param)); // 협력사-운영조직 : 거래가능 협력사관리그룹 정보

		return resultMap;
	}

	/**
	 * 협력사 운영조직별 미등록 SG 목록 조회 (단, 한 번 통과된 유효한 OEG에 속한 SG여야 한다.)
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String, Object>> findListUnregisteredSgByOeg(Map<String, Object> param) {
		return onboardingEvalResultRepository.findListUnregisteredSgByOeg(param);
	}

	/**
	 * 협력사 운영조직별 등록 SG 목록 조회
	 *
	 * @param param {vd_cd, oorg_cd}
	 * @return List
	 */
	public List<Map<String, Object>> findListRegisteredSg(Map<String, Object> param) {
		return onboardingEvalResultRepository.findListVendorManagementGroup(param);
	}
}
