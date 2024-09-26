package smartsuite.app.bp.onboarding.obdSetup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.onboarding.obdSetup.event.ObdSetupEventPublisher;
import smartsuite.app.bp.onboarding.obdSetup.repository.ObdEvalshtSetupRepository;
import smartsuite.app.bp.onboarding.request.service.OnboardingEvalMonitoringService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * obd evalsht setup 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName ObdEvalshtSetupService.java
 * @package smartsuite.app.bp.vs.obdSetup.service
 * @Since 2023. 6. 1
 * @변경이력 : [2023. 6. 1] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ObdEvalshtSetupService {

	@Inject
	private ObdEvalshtSetupRepository obdEvalshtSetupRepository;

	@Inject
	private SharedService sharedService;

	@Inject
	private OegSetupService oegSetupService;

	@Inject
	private OnboardingEvalMonitoringService onboardingEvalMonitoringService;

	@Inject
	ObdSetupEventPublisher obdSetupEventPublisher;

	/**
	 * 온보딩 평가시트 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd evalsht list
	 * @Date : 2023. 7. 24
	 * @Method Name : findListObdEvalsht
	 */
	public FloaterStream findListObdEvalsht(Map<String, Object> param) {
		// 대용량 처리
		return obdEvalshtSetupRepository.findListObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : findObdEvalsht
	 */
	public ResultMap findObdEvalsht(Map<String, Object> param) {
		Map<String, Object> results = new HashMap<String, Object>();

		// 1. 온보딩 평가시트 조회
		Map<String, Object> obdEvalshtInfo = obdEvalshtSetupRepository.findObdEvalshtInfo(param);

		// 2. 온보딩 평가시트 프로세스 조회
		List<Map<String, Object>> obdEvalshtInfoPrcses = obdEvalshtSetupRepository.findObdEvalshtInfoPrcs(obdEvalshtInfo);
		
		// 3. 온보딩 평가시트 사용여부 체크 (OE 생성 여부)
		String cnfdYn = obdEvalshtInfo == null || obdEvalshtInfo.get("cnfd_yn") == null ? "N" : obdEvalshtInfo.get("cnfd_yn").toString();
		if("Y".equals(cnfdYn)) {
			String isCreateOeYn = onboardingEvalMonitoringService.findCreateOeYnByObdEvalsht(obdEvalshtInfo);
			obdEvalshtInfo.put("isCreateOeYn", isCreateOeYn);
		}

		results.put("obdEvalshtInfo", obdEvalshtInfo);
		results.put("obdEvalshtInfoPrcses", obdEvalshtInfoPrcses);
		return ResultMap.SUCCESS(results);
	}

	/**
	 * 최신 온보딩 평가시트을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 23
	 * @Method Name : findObdCurrentEvalsht
	 */
	public Map<String, Object> findObdCurrentEvalsht(List<Map<String, Object>> obdEvalshtHis) {
		Map<String, Object> obdEvalshtInfo = new HashMap<>();
		Map<String, Object> currentEvalsht = obdEvalshtHis.size() != 0 ? obdEvalshtHis.get(0) : null;

		if(currentEvalsht != null && "Y".equals(currentEvalsht.get("current_evalsht"))){
			Map<String, Object> param = new HashMap<>();
			param.put("oeg_uuid", currentEvalsht.get("oeg_uuid"));
			param.put("obd_evalsht_uuid", currentEvalsht.get("obd_evalsht_uuid"));  // 최신 평가시트 UUID
			obdEvalshtInfo = obdEvalshtSetupRepository.findObdEvalshtInfo(param);
			if(obdEvalshtHis.size() > 1){
				obdEvalshtInfo.put("prev_obd_evalsht_uuid", obdEvalshtHis.get(1).get("obd_evalsht_uuid"));  // 이전 평가시트 UUID
			}
		}

		return obdEvalshtInfo;
	}

	/**
	 * 온보딩 평가시트를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : saveObdEvalsht
	 */
	public ResultMap saveObdEvalsht(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		Map<String, Object> obdEvalshtInfo = (Map<String, Object>)param.get("obdEvalshtInfo");
		List<Map<String, Object>> obdEvalshtPrcs = (List<Map<String, Object>>)param.get("obdEvalshtPrcs");

		String obdEvalshtUuid = obdEvalshtInfo.get("obd_evalsht_uuid") == null ? "" : obdEvalshtInfo.get("obd_evalsht_uuid").toString();
		if("".equals(obdEvalshtUuid)){  // 신규
			obdEvalshtUuid = UUID.randomUUID().toString();
			String evalshtCd = sharedService.generateDocumentNumber("OES");

			// 1. 온보딩 평가시트 저장
			obdEvalshtInfo.put("oeg_uuid", oegInfo.get("oeg_uuid"));
			obdEvalshtInfo.put("obd_evalsht_uuid", obdEvalshtUuid);
			obdEvalshtInfo.put("evalsht_cd", evalshtCd);
			obdEvalshtSetupRepository.insertObdEvalsht(obdEvalshtInfo);
		}else{
			// 1. 온보딩 평가시트 수정
			obdEvalshtSetupRepository.updateObdEvalsht(obdEvalshtInfo);
		}
		
		// 3. 온보딩 시트 - 온보딩 프로세스 저장
		Map<String, Object> evalsheetPrcsParam = new HashMap<String, Object>();
		evalsheetPrcsParam.put("obd_evalsht_uuid", obdEvalshtUuid);
		evalsheetPrcsParam.put("obdEvalshtPrcs", obdEvalshtPrcs);
		this.saveListObdEvalshtPrcs(evalsheetPrcsParam);

		return ResultMap.SUCCESS(oegInfo);
	}

	/**
	 * 온보딩 시트 - 온보딩 프로세스를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : saveListOEGVendorMgmtGrp
	 */
	public void saveListObdEvalshtPrcs(Map<String, Object> param) {
		String obdEvalshtUuid = param.get("obd_evalsht_uuid") == null ? "" : param.get("obd_evalsht_uuid").toString();
		List<Map<String, Object>> obdEvalshtPrcs = (List<Map<String, Object>>)param.get("obdEvalshtPrcs");

		if (obdEvalshtPrcs != null && !obdEvalshtPrcs.isEmpty()) {
			for (Map<String, Object> row : obdEvalshtPrcs) {
				String obdEvalshtPrcsUuid = row.get("obd_evalsht_prcs_uuid") == null ? "" : row.get("obd_evalsht_prcs_uuid").toString();
				if("".equals(obdEvalshtPrcsUuid)){  // 신규
					obdEvalshtPrcsUuid = UUID.randomUUID().toString();
					row.put("obd_evalsht_prcs_uuid", obdEvalshtPrcsUuid);
					row.put("obd_evalsht_uuid", obdEvalshtUuid);

					obdEvalshtSetupRepository.insertObdEvalshtPrcs(row);
				}else{  // 수정
					row.put("obd_evalsht_prcs_uuid", obdEvalshtPrcsUuid);

					String applicationYn = row.get("application_yn") == null ? "N" : row.get("application_yn").toString();
					if(!"Y".equals(applicationYn)){
						row.put("sts", "D");
					}else{
						row.put("sts", "U");
					}
					obdEvalshtSetupRepository.updateObdEvalshtPrcs(row);
				}
			}
		}
	}

	/**
	 * 온보딩 평가시트 프로세스를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 8
	 * @Method Name : saveObdEvalshtPrcs
	 */
	public ResultMap saveObdEvalshtPrcs(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		Map<String, Object> obdEvalshtInfoPrcs = (Map<String, Object>)param.get("obdEvalshtInfoPrcs");
		Map<String, Object> evalTmplInfo = (Map<String, Object>)param.get("evalTmplInfo");  // evaltmpl 정보
		Map<String, Object> evaltrInfo = (Map<String, Object>)param.get("evaltrInfo");  // evaltr 정보

		// 1. 평가 템플릿 저장
		ResultMap createEvalTmplResult = ResultMap.getInstance();
		String evaltmplNm = evalTmplInfo.get("evaltmpl_nm") == null ? "" : evalTmplInfo.get("evaltmpl_nm").toString();
		if(!"".equals(evaltmplNm)){  // 템플릿 생성 정보 존재
			createEvalTmplResult = (ResultMap) obdSetupEventPublisher.saveEvalTmplInfo(evalTmplInfo);
		}

		if(ResultMap.STATUS.SUCCESS.equals(createEvalTmplResult.getResultStatus())) {
			Map<String, Object> evalsheetPrcsParam = new HashMap<String, Object>();  // 평가 프로세스 저장 Param
			Map<String, Object> evaltrParam = new HashMap<String, Object>();         // 평가자 저장 Param
			List<Map<String, Object>> obdEvalshtPrcs = new ArrayList<Map<String, Object>>();

			// 2-1. 온보딩 프로세스 - 평가 템플릿 매핑
			Map<String, Object> createEvalTmplResultData = createEvalTmplResult.getResultData();
			if(createEvalTmplResultData != null){
				Map<String, Object> savedEvalTmplInfo = (Map<String, Object>) createEvalTmplResultData.get("evalTmplInfo");
				obdEvalshtInfoPrcs.put("evaltmpl_uuid", savedEvalTmplInfo.get("evaltmpl_uuid"));
			}

			// 2-2. 온보딩 시트 - 온보딩 프로세스 저장
			String obdEvalshtUuid = oegInfo.get("obd_evalsht_uuid") == null ? "" : oegInfo.get("obd_evalsht_uuid").toString();
			obdEvalshtPrcs.add(obdEvalshtInfoPrcs);

			evalsheetPrcsParam.put("obd_evalsht_uuid", obdEvalshtUuid);
			evalsheetPrcsParam.put("obdEvalshtPrcs", obdEvalshtPrcs);
			this.saveListObdEvalshtPrcs(evalsheetPrcsParam);

			// 3. 온보딩 평가시트 프로세스 항목별 담당자 저장
			evaltrParam.put("obdEvalshtInfoPrcs", obdEvalshtInfoPrcs);
			evaltrParam.put("evaltrInfo", evaltrInfo);
			this.saveListObdFactChrGrpEvaltr(evaltrParam);
		} else {
			return ResultMap.FAIL(oegInfo);
		}

		Map<String, Object> resultInfo = new HashMap<>();
		resultInfo.put("oegInfo", oegInfo);
		resultInfo.put("obdEvalshtInfoPrcs", obdEvalshtInfoPrcs);
		return ResultMap.SUCCESS(resultInfo);
	}

	/**
	 * 온보딩 평가시트 프로세스 항목별 담당자를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 14
	 * @Method Name : saveListObdFactChrGrpEvaltr
	 */
	public ResultMap saveListObdFactChrGrpEvaltr(Map<String, Object> param) {
		Map<String, Object> obdEvalshtInfoPrcs = (Map<String, Object>)param.get("obdEvalshtInfoPrcs");
		Map<String, Object> evaltrInfo = (Map<String, Object>)param.get("evaltrInfo");  // evaltr 정보

		String evaltrTypCcd = obdEvalshtInfoPrcs.get("evaltr_typ_ccd") == null ? "" : obdEvalshtInfoPrcs.get("evaltr_typ_ccd").toString();

		if(!"EVALFACT_AUTHTY_PIC".equals(evaltrTypCcd)){  // EVALFACT_AUTHTY_PIC: 평가항목 권한 담당자
			// 평가항목별 담당자 삭제
			obdEvalshtSetupRepository.updateEvaltrByDelete(obdEvalshtInfoPrcs);
		}else{
			// 평가항목별 담당자 저장
			String obdEvalshtPrcsUuid = obdEvalshtInfoPrcs.get("obd_evalsht_prcs_uuid").toString();
			List<Map<String, Object>> insertEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("insertEvaltrList");
			List<Map<String, Object>> updateEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("updateEvaltrList");
			List<Map<String, Object>> deleteEvaltrList = (List<Map<String, Object>>)evaltrInfo.get("deleteEvaltrList");

			if(insertEvaltrList != null && !insertEvaltrList.isEmpty()){
				for(final Map<String, Object> row : insertEvaltrList){
					row.put("obd_evalsht_prcs_uuid", obdEvalshtPrcsUuid);
					obdEvalshtSetupRepository.insertFactChrGrpEvaltr(row);
				}
			}
			if(updateEvaltrList != null && !updateEvaltrList.isEmpty()){
				for(final Map<String, Object> row : updateEvaltrList){
					obdEvalshtSetupRepository.updateFactChrGrpEvaltr(row);
				}
			}
			if(deleteEvaltrList != null && !deleteEvaltrList.isEmpty()){
				for(final Map<String, Object> row : deleteEvaltrList){
					obdEvalshtSetupRepository.updateFactChrGrpEvaltrByDelete(row);
				}
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩 평가시트 및 프로세스 전체 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 20
	 * @Method Name : saveAllObdEvalshtAndPrcses
	 */
	public ResultMap saveAllObdEvalshtAndPrcses(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>)param.get("oegInfo");
		Map<String, Object> obdEvalshtInfo = (Map<String, Object>)param.get("obdEvalshtInfo");
		List<Map<String, Object>> prcsInfoList = (List<Map<String, Object>>)param.get("prcsInfoList");

		// 1. 온보딩 평가시트 수정
		obdEvalshtSetupRepository.updateObdEvalsht(obdEvalshtInfo);

		// 2. 온보딩 시트 - 온보딩 프로세스 저장
		Map<String, Object> saveObdEvalshtPrcsParam = new HashMap<String, Object>();
		saveObdEvalshtPrcsParam.put("oegInfo", oegInfo);
		for(Map<String, Object> prcsInfo : prcsInfoList){
			saveObdEvalshtPrcsParam.put("obdEvalshtInfoPrcs", prcsInfo.get("obdEvalshtInfoPrcs"));
			saveObdEvalshtPrcsParam.put("evalTmplInfo", prcsInfo.get("evalTmplInfo"));
			saveObdEvalshtPrcsParam.put("evaltrInfo", prcsInfo.get("evaltrInfo"));
			ResultMap saveObdEvalshtPrcsResultMap = this.saveObdEvalshtPrcs(saveObdEvalshtPrcsParam);

			if(!ResultMap.STATUS.SUCCESS.equals(saveObdEvalshtPrcsResultMap.getResultStatus())){
				return ResultMap.FAIL();
			}
		}

		return ResultMap.SUCCESS(oegInfo);
	}

	/**
	 * 온보딩 평가시트/평가템플릿 확정상태를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 20
	 * @Method Name : saveCnfdYnObdEvalsht
	 */
	public ResultMap saveCnfdYnObdEvalsht(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		String cnfdYn = param.get("cnfdYn").toString();

		// 1. 온보딩평가그룹 - 평가시트이력 조회
		List<Map<String, Object>> obdEvalshtHis = this.findListObdEvalshtHis(oegInfo);

		// 2. 확정요청한 온보딩 평가시트 정보 조회 (= 최신 온보딩 평가시트)
		Map<String, Object> obdEvalshtInfo = this.findObdCurrentEvalsht(obdEvalshtHis);

		// 2. 온보딩 평가시트 프로세스 - 평가템플릿 UUID 리스트 조회
		List<String> evaltmplUuidList = obdEvalshtSetupRepository.findListEvaltmplUuidOfObdEvalshtPrcses(obdEvalshtInfo);

		if(obdEvalshtInfo != null && !obdEvalshtInfo.isEmpty()) {
			if("N".equals(cnfdYn)){  // 3-1) '확정취소' 처리인 경우, 온보딩 평가시트 사용여부 체크 (OE 생성 여부)
				String isCreateOeYn = onboardingEvalMonitoringService.findCreateOeYnByObdEvalsht(obdEvalshtInfo);
				if("Y".equals(isCreateOeYn)){  // OE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}

			// 3. 이전 온보딩 평가시트 유효일자 update
			// 4. 온보딩 평가시트 프로세스 이전 평가 템플릿 정보 update
			// 5. 온보딩 평가시트 확정 update
			obdEvalshtInfo.put("cnfd_yn", cnfdYn);
			obdEvalshtSetupRepository.updatePrevObdEvalsht(obdEvalshtInfo);
			obdEvalshtSetupRepository.updatePrevEvaltmplUuidObdEvalshtPrcs(obdEvalshtInfo);
			obdEvalshtSetupRepository.updateCnfdYnObdEvalsht(obdEvalshtInfo);

			if("Y".equals(cnfdYn)){  // '확정' 처리인 경우, evaltmpl에 update
				// 7. 평가템플릿 확정 update
				Map<String, Object>  evalTmplParam = new HashMap<>();
				evalTmplParam.put("evaltmpl_uuids", evaltmplUuidList);
				evalTmplParam.put("cnfd_yn", cnfdYn);
				obdSetupEventPublisher.updateCnfdYnEvalTmpl(evalTmplParam);
			}

			// 8. 온보딩평가그룹 저장
			oegSetupService.updateOegMaster(oegInfo);
		}else{
			return ResultMap.FAIL();
		}

		return ResultMap.SUCCESS(oegInfo);
	}

	/**
	 * 온보딩 시트를 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : updateObdEvalshtStsByDelete
	 */
	public ResultMap updateObdEvalshtStsByDelete(Map<String, Object> param){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		obdEvalshtSetupRepository.updateObdEvalshtStsByDelete(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩 시트 - 온보딩 프로세스를 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : updateObdEvalshtPrcsStsByDelete
	 */
	public ResultMap updateObdEvalshtPrcsStsByDelete(Map<String, Object> param){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		obdEvalshtSetupRepository.updateObdEvalshtPrcsStsByDelete(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 온보딩평가그룹 - 평가시트이력 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd evalsheet list
	 * @Date : 2023. 6. 5
	 * @Method Name : findListObdEvalshtHis
	 */
	public List<Map<String, Object>> findListObdEvalshtHis(Map<String, Object> param) {
		// 대용량 처리
		return obdEvalshtSetupRepository.findListObdEvalshtHis(param);
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
			resultList = obdEvalshtSetupRepository.findListFactChrGrpEvaltr(param);
		}else if("VMG_PIC".equals(evaltrTypCcd)){        // VMG_PIC: 협력사관리그룹 담당자
			param.put("vmg_oorg_uuids", obdEvalshtSetupRepository.findListOegVmgUuid(param));
			resultList = obdSetupEventPublisher.findListVendorManagementGroupEvaltrForView(param);
		}

		return resultList;

	}

	/**
	 * 평가템플릿 사용 여부를 조회한다. (온보딩 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public String findEvalTmplUseYnInObdEvalSht(Map<String, Object> param) {
		return obdEvalshtSetupRepository.findEvalTmplUseYnInObdEvalSht(param);
	}
	
	/**
	 * 평가템플릿 상태값을 조회한다. (온보딩 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public String findEvalTmplStsInObdEvalSht(Map<String, Object> param) {
		List<String> sheetList = obdEvalshtSetupRepository.findEvalTmplStsInObdEvalSht(param);
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
	 * 온보딩 평가시트 확정여부를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public String checkObdEvalShtConfirmYnByEvalTmpl(Map<String, Object> param) {
		return obdEvalshtSetupRepository.checkObdEvalShtConfirmYnByEvalTmpl(param);
	}

	/**
	 * 온보딩 평가시트를 Import한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 23
	 * @Method Name : saveImportObdEvalsht
	 */
	public ResultMap saveImportObdEvalsht(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param.get("oegInfo");
		Map<String, Object> obdEvalshtInfo = (Map<String, Object>) param.get("obdEvalshtInfo");

		// 1. 온보딩 평가시트 copy
		String obdEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("OES");
		obdEvalshtInfo.put("new_obd_evalsht_uuid", obdEvalshtUuid);
		obdEvalshtInfo.put("evalsht_cd", evalshtCd);
		obdEvalshtInfo.put("oeg_uuid", oegInfo.get("oeg_uuid"));
		obdEvalshtSetupRepository.copyObdEvalsht(obdEvalshtInfo);

		// 2. 온보딩 평가시트 프로세스 List copy
		this.copyListObdEvalshtPrcs(obdEvalshtInfo);

		return ResultMap.SUCCESS(oegInfo);
	}

	/**
	 * 온보딩 평가시트를 버전업한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 23
	 * @Method Name : saveVersionupObdEvalsht
	 */
	public ResultMap saveVersionupObdEvalsht(Map<String, Object> param) {
		Map<String, Object> oegInfo = (Map<String, Object>) param;

		// 1. 온보딩 평가시트 copy
		String obdEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("OES");
		oegInfo.put("new_obd_evalsht_uuid", obdEvalshtUuid);
		oegInfo.put("evalsht_cd", evalshtCd);
		obdEvalshtSetupRepository.copyObdEvalsht(oegInfo);

		// 2. 온보딩 평가시트 프로세스 List copy
		this.copyListObdEvalshtPrcs(oegInfo);

		return ResultMap.SUCCESS(oegInfo);
	}
	
	/**
	 * 온보딩 평가시트 프로세스를 버전업한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 23
	 * @Method Name : saveVersionupObdEvalsht
	 */
	public void copyListObdEvalshtPrcs(Map<String, Object> param) {
		// 1. 온보딩 평가시트 프로세스 조회
		List<Map<String, Object>> obdEvalshtInfoPrcses = obdEvalshtSetupRepository.findObdEvalshtInfoPrcs(param);

		for(Map<String, Object> obdEvalshtInfo : obdEvalshtInfoPrcses){
			// 2. 온보딩 평가시트 프로세스 copy
			obdEvalshtInfo.put("new_obd_evalsht_uuid", param.get("new_obd_evalsht_uuid"));
			String obdEvalshtPrcsUuid = UUID.randomUUID().toString();
			obdEvalshtInfo.put("new_obd_evalsht_prcs_uuid", obdEvalshtPrcsUuid);
			obdEvalshtSetupRepository.copyObdEvalshtPrcs(obdEvalshtInfo);

			// 3. 온보딩 평가시트 항목별 담당자 copy
			obdEvalshtSetupRepository.copyListObdFactChrGrpEvaltr(obdEvalshtInfo);
		}
	}

	/**
	 * 온보딩 평가시트를 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 24
	 * @Method Name : deleteObdEvalsht
	 */
	public ResultMap deleteObdEvalsht(Map<String, Object> param){
		Map<String, Object> oegInfo = (Map<String, Object>)param.get("oegInfo");
		Map<String, Object> obdEvalshtInfo = (Map<String, Object>)param.get("obdEvalshtInfo");

		// 1. 온보딩 평가시트 사용여부 체크 (OE 생성 여부)
		String isCreateOeYn = onboardingEvalMonitoringService.findCreateOeYnByObdEvalsht(obdEvalshtInfo);
		if ("Y".equals(isCreateOeYn)) {  // OE 생성 상태
			// param 상태와 불일치
			return ResultMap.INVALID();
		}
		// 2. 온보딩 평가시트 확정여부 체크
		Map<String, Object> gettedObdEvalshtInfo = (Map<String, Object>)obdEvalshtSetupRepository.findObdEvalshtInfo(obdEvalshtInfo);
		String cnfdYn = gettedObdEvalshtInfo.get("cnfd_yn") == null ? "N" : gettedObdEvalshtInfo.get("cnfd_yn").toString();
		if ("Y".equals(cnfdYn)) {  // 확정 상태
			// param 상태와 불일치 (미확정 상태일 때만 삭제 가능)
			return ResultMap.INVALID();
		}

		// 3. DELETE
		this.updateObdEvalshtStsByDelete(obdEvalshtInfo);  // 온보딩 시트 삭제

		return ResultMap.SUCCESS(oegInfo);
	}

	/**
	 * 온보딩 평가시트 프로세스 - 평가템플릿을 매핑한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 24
	 * @Method Name : saveMappingEvaltmplUuidToObdEvalshtPrcs
	 */
	public void saveMappingEvaltmplUuidToObdEvalshtPrcs(Map<String, Object> param){
		obdEvalshtSetupRepository.saveMappingEvaltmplUuidToObdEvalshtPrcs(param);
	}

	/**
	 * 온보딩 평가시트 프로세스 - 수정 전 원본 평가템플릿을 매핑한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 4
	 * @Method Name : saveMappingOrgnObdEvaltmplUuidToObdEvalsht
	 */
	public void saveMappingOrgnObdEvaltmplUuidToObdEvalsht(Map<String, Object> param){
		obdEvalshtSetupRepository.saveMappingOrgnObdEvaltmplUuidToObdEvalsht(param);
	}
}
