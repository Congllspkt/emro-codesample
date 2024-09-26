package smartsuite.app.bp.onboarding.obdSetup.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdSetup.event.ObdSetupEventPublisher;
import smartsuite.app.bp.onboarding.obdSetup.repository.OegSetupRepository;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalMonitoringService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;

/**
 * oeg setup 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName OegSetupService.java
 * @package smartsuite.app.bp.vs.obdSetup.service
 * @Since 2023. 6. 1
 * @변경이력 : [2023. 6. 1] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class OegSetupService {

	@Inject
	private OegSetupRepository oegSetupRepository;
	
	@Inject
	private SharedService sharedService;

	@Inject
	private ObdEvalshtSetupService obdEvalshtSetupService;

	@Inject
	private OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	@Inject
	ObdSetupEventPublisher obdSetupEventPublisher;

	/**
	 * 온보딩평가그룹 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the oeg list
	 * @Date : 2023. 6. 1
	 * @Method Name : findListOeg
	 */
	public FloaterStream findListOeg(Map<String, Object> param) {
		// 대용량 처리
		return oegSetupRepository.findListOeg(param);
	}

	/**
	 * 온보딩평가그룹 목록을 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteLisOeg
	 */
	public ResultMap deleteListOeg(Map<String, Object> param){
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		// 1. 온보딩평가그룹 사용여부 체크 (OE 생성 여부)
		if (deletes != null && !deletes.isEmpty()) {
			for (Map<String, Object> row : deletes) {
				String isCreateOeYn = onboardingEvalMonitoringService.findCreateOeYnByOeg(row);
				if ("Y".equals(isCreateOeYn)) {  // OE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}
		}

		// 2. DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes){
				oegSetupRepository.updateOegByDelete(row);  // 2-1. 온보딩평가그룹 삭제
				oegSetupRepository.updateVmgStsByDelete(row);  // 2-2. 온보딩평가그룹 협력사관리그룹 삭제
				// 2023.06.15, yjPark, 어느레벨까지 'D' 처리할 지 고민필요
				/*obdEvalshtSetupService.updateObdEvalshtStsByDelete(row);  // 온보딩 시트 삭제
				obdEvalshtSetupService.updateObdEvalshtPrcsStsByDelete(row);  // 온보딩 시트 - 온보딩 프로세스 삭제
				obdEvalshtSetupService.updateObdEvalshtPrcsStsByDelete(row);  // 온보딩 시트 - 온보딩 프로세스 평가자 삭제*/
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩평가그룹 저장 전 validation을 체크한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 02
	 * @Method Name : checkValidBeforeSaveOeg
	 */
	public ResultMap checkValidBeforeSaveOeg(Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();
		Boolean validPass = true;
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");
		List<Map<String, Object>> insertTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("insertTargetVmgList");

		// 1. 등록예정 협력사관리그룹 Validation
		List<Map<String, Object>> insertInvalidByUsingVmgDataList = new ArrayList<>();
		if (insertTargetVmgList != null && !insertTargetVmgList.isEmpty()) {
			// 1-1. 동일 운영조직의 타 온보딩평가그룹에서의 사용여부 체크
			String oorgCd = oegInfo.get("oorg_cd").toString();
			for (Map<String, Object> row : insertTargetVmgList) {
				row.put("oorg_cd", oorgCd);
				Map<String, Object> oegInfoInUse = oegSetupRepository.findOegInfoInUsingVmg(row);

				if(oegInfoInUse != null){  // 존재
					validPass = false;
					insertInvalidByUsingVmgDataList.add(oegInfoInUse);
					break;
				}
			}
		}

		ResultMap resultMap = ResultMap.getInstance();
		if(!validPass){
			resultMap.setResultList(insertInvalidByUsingVmgDataList);
			resultMap.setResultStatus(ResultMap.STATUS.INVALID_STATUS_ERR);
			return resultMap;
		}

		return resultMap.SUCCESS();
	}

	/**
	 * 온보딩평가그룹을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : saveOeg
	 */
	public ResultMap saveOeg(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");

		String oegUuid = oegInfo.get("oeg_uuid") == null ? "" : oegInfo.get("oeg_uuid").toString();
		String oorgCd = oegInfo.get("oorg_cd") == null ? "" : oegInfo.get("oorg_cd").toString();
		if("".equals(oegUuid)){  // 신규
			oegUuid = UUID.randomUUID().toString();
			String oegCd = sharedService.generateDocumentNumber("OEG");
			oegInfo.put("oeg_uuid", oegUuid);
			oegInfo.put("oeg_cd", oegCd);

			// 1. 온보딩평가그룹 Mater 저장
			oegSetupRepository.insertOegMaster(oegInfo);
		}else{  // 수정
			// 1. 온보딩평가그룹 Mater 저장
			this.updateOegMaster(oegInfo);
		}

		// 2. 온보딩평가그룹 - 협력사관리그룹 저장
		Map<String, Object> oegVmgParam = new HashMap<String, Object>();
		oegVmgParam.put("oeg_uuid", oegUuid);
		oegVmgParam.put("oorg_cd", oorgCd);
		oegVmgParam.put("targetVmgInfo", targetVmgInfo);
		this.saveListOegVmg(oegVmgParam);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("oeg_uuid", oegUuid);
		data.put("oorg_cd", oorgCd);

		return ResultMap.SUCCESS(data);
	}

	/**
	 * 온보딩평가그룹 - 협력사관리그룹을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : saveListOegVmg
	 */
	public void saveListOegVmg(Map<String, Object> param) {
		String oegUuid = param.get("oeg_uuid") == null ? "" : param.get("oeg_uuid").toString();
		String oorgCd = param.get("oorg_cd") == null ? "" : param.get("oorg_cd").toString();
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");
		List<Map<String, Object>> insertTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("insertTargetVmgList");
		List<Map<String, Object>> deleteTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("deleteTargetVmgList");

		if (insertTargetVmgList != null && !insertTargetVmgList.isEmpty()) {
			for (Map<String, Object> row : insertTargetVmgList) {
				row.put("oorg_cd", oorgCd);

				// 1. 동일 운영조직의 타 온보딩평가그룹에서 사용 중인 insert 협력사관리그룹 삭제
				oegSetupRepository.deleteOegVmg(row);

				// 2. 온보딩평가그룹 - 협력사관리그룹 저장
				row.put("oeg_uuid", oegUuid);
				oegSetupRepository.insertOegVmg(row);
			}
		}
		if (deleteTargetVmgList != null && !deleteTargetVmgList.isEmpty()) {
			for (Map<String, Object> row : deleteTargetVmgList) {
				oegSetupRepository.deleteOegVmg(row);
			}
		}
	}

	/**
	 * 온보딩평가그룹을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : findOeg
	 */
	public ResultMap findOeg(Map<String, Object> param) {
		Map<String, Object> results = new HashMap<String, Object>();

		// 1. 온보딩평가그룹 Mater 조회
		Map<String, Object> oegInfo = oegSetupRepository.findOegMaster(param);

		// 2. 온보딩평가그룹 - 협력사관리그룹 조회
		param.put("oeg_uuid", oegInfo.get("oeg_uuid"));
		List<Map<String, Object>> oegVmgs = oegSetupRepository.findListOegVmg(param);

		// 3. 온보딩평가그룹 - 평가시트이력 조회
		param.put("oeg_uuid", oegInfo.get("oeg_uuid"));
		List<Map<String, Object>> obdEvalshtHis = obdEvalshtSetupService.findListObdEvalshtHis(param);

		// 5. 퍼포먼스 평가시트 조회
		Map<String, Object> obdEvalshtInfo = obdEvalshtSetupService.findObdCurrentEvalsht(obdEvalshtHis);

		results.put("oegInfo", oegInfo);
		results.put("oegVmgs", oegVmgs);
		results.put("obdEvalshtHis", obdEvalshtHis);
		results.put("obdEvalshtInfo", obdEvalshtInfo);
		return ResultMap.SUCCESS(results);
	}

	/**
	 * 온보딩평가그룹 Mater를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : updateOegMaster
	 */
	public Map<String, Object> updateOegMaster(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		oegSetupRepository.updateOegMaster(param);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
}
