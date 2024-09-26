package smartsuite.app.bp.onboarding.result.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.result.service.OnboardingEvalResultService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class OnboardingEvalResultEventListener {
	
	@Inject
	OnboardingEvalResultService onboardingEvalResultService;

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID를 저장한다.
	 * @param param oeInfo
	 */
	@EventListener(condition ="#event.eventId == 'saveOnboardingEvalVendorAprvInfo'")
	public void saveOnboardingEvalVendorAprvInfo(CustomSpringEvent event){
		onboardingEvalResultService.saveOnboardingEvalVendorAprvInfo((Map) event.getData());
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상여부를 변경한다.
	 * @param param oeVmgInfo
	 */
	@EventListener(condition ="#event.eventId == 'saveOnboardingEvalVmgAprvSubj'")
	public void saveOnboardingEvalVmgAprvSubj(CustomSpringEvent event){
		onboardingEvalResultService.saveOnboardingEvalVmgAprvSubj((Map) event.getData());
	}

	/**
	 * 온보딩평가 요청 처리완료한다.
	 * @param param vdHistrecInfo
	 */
	@EventListener(condition ="#event.eventId == 'saveOnboardingEvalPrcsgEnd'")
	public void saveOnboardingEvalPrcsgEnd(CustomSpringEvent event){
		onboardingEvalResultService.saveOnboardingEvalPrcsgEnd((Map) event.getData());
	}

	/**
	 * 등록대상 협력사관리그룹에 결재대상 취소한다.
	 * @param param oeVmgInfo
	 */
	@EventListener(condition ="#event.eventId == 'saveCancelOnboardingEvalVmgAprvSubj'")
	public void saveCancelOnboardingEvalVmgAprvSubj(CustomSpringEvent event){
		onboardingEvalResultService.saveCancelOnboardingEvalVmgAprvSubj((Map) event.getData());
	}

	/**
	 * 대상 온보딩평가그룹에 협력사 마스터 변경이력 UUID을 초기화한다.
	 * @param param oeInfo
	 */
	@EventListener(condition ="#event.eventId == 'saveCancelOnboardingEvalVendorAprvInfo'")
	public void saveCancelOnboardingEvalVendorAprvInfo(CustomSpringEvent event){
		onboardingEvalResultService.saveCancelOnboardingEvalVendorAprvInfo((Map) event.getData());
	}

	/**
	 *
	 * 협력사별 온보딩평가 결과 목록을 조회한다.
	 * @param param 협력사 정보
	 */
	@EventListener(condition ="#event.eventId == 'findVendorObdEvalInfoList'")
	public void findVendorObdEvalInfoList(CustomSpringEvent event){
		List result = onboardingEvalResultService.findVendorObdEvalInfoList((Map) event.getData());
		event.setResult(result);
	}

	/**
	 * 협력사관리그룹 평가를 미등록완료 한다.
	 * @param vmgList 평가취소요청 대상 협력사관리그룹 리스트
	 */
	@EventListener(condition ="#event.eventId == 'saveListOnboardingEvalUnRegComplete'")
	public void saveListOnboardingEvalUnRegComplete(CustomSpringEvent event){
		ResultMap result = onboardingEvalResultService.saveListOnboardingEvalUnRegComplete((Map) event.getData());
		event.setResult(result);
	}

	/**
	 * 협력사 VMT, VMG 리스트 조회 (findVendorVMTVMGInfo)
	 *
	 * @param VendorInfo
	 */
	@EventListener(condition ="#event.eventId == 'findVendorVMTVMGInfo'")
	public void findVendorVMTVMGInfo(CustomSpringEvent event){
		Map<String, Object> resultMap = onboardingEvalResultService.findVendorVMTVMGInfo((Map) event.getData());
		event.setResult(resultMap);
	}

	/**
	 * 협력사 운영조직별 미등록 SG 목록 조회 (단, 한 번 통과된 유효한 OEG에 속한 SG여야 한다.)
	 *
	 * @param event event.data: {vd_cd, oorg_cd}
	 */
	@EventListener(condition = "#event.eventId == 'findListUnregisteredSgByOeg'")
	public void findListUnregisteredSgByOeg(CustomSpringEvent event) {
		List<Map<String, Object>> resultMap = onboardingEvalResultService.findListUnregisteredSgByOeg((Map<String, Object>) event.getData());
		event.setResult(resultMap);
	}

	/**
	 * 협력사 운영조직별 등록 SG 목록 조회
	 *
	 * @param event event.data: {vd_cd, oorg_cd}
	 */
	@EventListener(condition = "#event.eventId == 'findListRegisteredSg'")
	public void findListRegisteredSg(CustomSpringEvent event) {
		List<Map<String, Object>> resultMap = onboardingEvalResultService.findListRegisteredSg((Map<String, Object>) event.getData());
		event.setResult(resultMap);
	}
}
