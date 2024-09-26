package smartsuite.app.bp.pro.gr.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.event.GrEvalSetupEventPublisher;
import smartsuite.app.bp.pro.gr.repository.GrEvalshtSetupRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GrEvalshtSetupService {
	
	@Inject
	GrEvalMonitoringService grEvalMonitoringService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	GrEvalSetupEventPublisher grEvalSetupEventPublisher;
	
	@Inject
	GrEvalshtSetupRepository grEvalshtSetupRepository;
	
	public List<Map> findListGrEvalshtHis(Map param) {
		return grEvalshtSetupRepository.findListGrEvalshtHis(param);
	}
	
	public Map findGrCurrentEvalsht(List<Map> grEvalshtHis) {
		Map grEvalshtInfo = Maps.newHashMap();
		Map currentEvalsht = grEvalshtHis.size() != 0 ? grEvalshtHis.get(0) : null;
		
		if(currentEvalsht != null && "Y".equals(currentEvalsht.get("current_evalsht"))) {
			Map param = new HashMap<>();
			param.put("geg_uuid", currentEvalsht.get("geg_uuid"));
			param.put("gr_evalsht_uuid", currentEvalsht.get("gr_evalsht_uuid"));  // 최신 평가시트 UUID
			grEvalshtInfo = grEvalshtSetupRepository.findGrEvalshtInfo(param);
			if(grEvalshtHis.size() > 1) {
				grEvalshtInfo.put("prev_gr_evalsht_uuid", grEvalshtHis.get(1).get("gr_evalsht_uuid"));  // 이전 평가시트 UUID
			}
		}
		
		return grEvalshtInfo;
	}
	
	public ResultMap findGrEvalsht(Map param) {
		Map results = Maps.newHashMap();
		
		// 1. 입고 평가시트 조회
		Map grEvalshtInfo = grEvalshtSetupRepository.findGrEvalshtInfo(param);
		
		// 2. 입고 평가시트 프로세스 조회
		List<Map> grEvalshtInfoPrcses = grEvalshtSetupRepository.findGrEvalshtInfoPrcs(grEvalshtInfo);
		
		// 3. 입고 평가시트 사용여부 체크 (GE 생성 여부)
		String cnfdYn = grEvalshtInfo == null || grEvalshtInfo.get("cnfd_yn") == null ? "N" : grEvalshtInfo.get("cnfd_yn").toString();
		if("Y".equals(cnfdYn)) {
			String isCreateGeYn = grEvalMonitoringService.findCreateGeYnByGrEvalsht(grEvalshtInfo);
			grEvalshtInfo.put("isCreateGeYn", isCreateGeYn);
		}
		
		results.put("grEvalshtInfo", grEvalshtInfo);
		results.put("grEvalshtInfoPrcses", grEvalshtInfoPrcses);
		return ResultMap.SUCCESS(results);
	}
	
	public ResultMap saveGrEvalsht(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		List<Map> grEvalshtPrcs = (List<Map>) param.get("grEvalshtPrcs");
		
		String grEvalshtUuid = grEvalshtInfo.get("gr_evalsht_uuid") == null ? "" : grEvalshtInfo.get("gr_evalsht_uuid").toString();
		if("".equals(grEvalshtUuid)) {  // 신규
			grEvalshtUuid = UUID.randomUUID().toString();
			String evalshtCd = sharedService.generateDocumentNumber("GES");
			
			// 1. 입고 평가시트 저장
			grEvalshtInfo.put("geg_uuid", gegInfo.get("geg_uuid"));
			grEvalshtInfo.put("gr_evalsht_uuid", grEvalshtUuid);
			grEvalshtInfo.put("evalsht_cd", evalshtCd);
			grEvalshtSetupRepository.insertGrEvalsht(grEvalshtInfo);
		} else {
			// 1. 입고 평가시트 수정
			grEvalshtSetupRepository.updateGrEvalsht(grEvalshtInfo);
		}
		
		// 3. 입고 평가시트 - 입고평가 프로세스 저장
		Map evalsheetPrcsParam = Maps.newHashMap();
		evalsheetPrcsParam.put("gr_evalsht_uuid", grEvalshtUuid);
		evalsheetPrcsParam.put("grEvalshtPrcs", grEvalshtPrcs);
		evalsheetPrcsParam.put("gegInfo", gegInfo);
		evalsheetPrcsParam.put("grEvalshtInfo", grEvalshtInfo);
		this.saveListGrEvalshtPrcs(evalsheetPrcsParam);
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	private void saveListGrEvalshtPrcs(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		List<Map> grEvalshtPrcs = (List<Map>) param.get("grEvalshtPrcs");
		String grEvalshtUuid = param.get("gr_evalsht_uuid") == null ? "" : param.get("gr_evalsht_uuid").toString();
		
		if(grEvalshtPrcs != null && !grEvalshtPrcs.isEmpty()) {
			for(Map row : grEvalshtPrcs) {
				String grEvalshtPrcsUuid = row.get("gr_evalsht_prcs_uuid") == null ? "" : row.get("gr_evalsht_prcs_uuid").toString();
				if("".equals(grEvalshtPrcsUuid)) {  // 신규
					grEvalshtPrcsUuid = UUID.randomUUID().toString();
					row.put("gr_evalsht_prcs_uuid", grEvalshtPrcsUuid);
					row.put("gr_evalsht_uuid", grEvalshtUuid);
					row.put("geg_uuid", gegInfo.get("geg_uuid"));
					row.put("evaltmpl_uuid", param.get("evaltmpl_uuid"));
					
					grEvalshtSetupRepository.insertGrEvalshtPrcs(row);
					
					// 입고평가관리그룹 담당자 프로세스의 신규 생성인 경우 입고평가관리그룹의 기본 평가 담당자 매핑
					if("PRC_0002".equals(row.get("prcs_ccd"))) {
						Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
						if(grEvalshtInfo != null) {
							grEvalshtInfo.put("gr_evalsht_prcs_uuid", grEvalshtPrcsUuid);
							grEvalshtSetupRepository.copyGrEvalshtPrcsEvaltrByDefaultGemgQlyPic(grEvalshtInfo);
						}
					}
				} else {  // 수정
					row.put("gr_evalsht_prcs_uuid", grEvalshtPrcsUuid);
					row.put("evaltmpl_uuid", param.get("evaltmpl_uuid"));
					
					String applicationYn = row.get("application_yn") == null ? "N" : row.get("application_yn").toString();
					if(!"Y".equals(applicationYn)) {
						row.put("sts", "D");
					} else {
						row.put("sts", "U");
					}
					grEvalshtSetupRepository.updateGrEvalshtPrcs(row);
				}
			}
		}
	}
	
	public ResultMap saveImportGrEvalsht(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		
		// 1. 입고 평가시트 copy
		String grEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("GES");
		grEvalshtInfo.put("new_gr_evalsht_uuid", grEvalshtUuid);
		grEvalshtInfo.put("evalsht_cd", evalshtCd);
		grEvalshtInfo.put("geg_uuid", gegInfo.get("geg_uuid"));
		grEvalshtSetupRepository.copyGrEvalsht(grEvalshtInfo);
		
		// 2. 입고 평가시트 프로세스 List copy
		this.copyListGrEvalshtPrcs(grEvalshtInfo);
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	private void copyListGrEvalshtPrcs(Map param) {
		// 1. 입고 평가시트 프로세스 조회
		List<Map> grEvalshtInfoPrcses = grEvalshtSetupRepository.findGrEvalshtInfoPrcs(param);
		
		for(Map grEvalshtInfoPrcs : grEvalshtInfoPrcses) {
			// 2. 입고 평가시트 프로세스 copy
			grEvalshtInfoPrcs.put("new_gr_evalsht_uuid", param.get("new_gr_evalsht_uuid"));
			String grEvalshtPrcsUuid = UUID.randomUUID().toString();
			grEvalshtInfoPrcs.put("new_gr_evalsht_prcs_uuid", grEvalshtPrcsUuid);
			grEvalshtSetupRepository.copyGrEvalshtPrcs(grEvalshtInfoPrcs);
			
			// 3. 입고 평가시트 항목별 담당자 copy
			grEvalshtSetupRepository.copyListGrFactChrGrpEvaltr(grEvalshtInfoPrcs);
		}
	}
	
	public ResultMap saveVersionupGrEvalsht(Map param) {
		Map gegInfo = (Map) param;
		
		// 1. 입고 평가시트 copy
		String grEvalshtUuid = UUID.randomUUID().toString();
		String evalshtCd = sharedService.generateDocumentNumber("GES");
		gegInfo.put("new_gr_evalsht_uuid", grEvalshtUuid);
		gegInfo.put("evalsht_cd", evalshtCd);
		grEvalshtSetupRepository.copyGrEvalsht(gegInfo);
		
		// 2. 입고 평가시트 프로세스 List copy
		this.copyListGrEvalshtPrcs(gegInfo);
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	public ResultMap deleteGrEvalsht(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		
		// 1. 입고 평가시트 사용여부 체크 (GE 생성 여부)
		String isCreateGeYn = grEvalMonitoringService.findCreateGeYnByGrEvalsht(grEvalshtInfo);
		if("Y".equals(isCreateGeYn)) {  // GE 생성 상태
			// param 상태와 불일치
			return ResultMap.INVALID();
		}
		// 2. 입고 평가시트 확정여부 체크
		Map gettedGrEvalshtInfo = grEvalshtSetupRepository.findGrEvalshtInfo(grEvalshtInfo);
		String cnfdYn = gettedGrEvalshtInfo.get("cnfd_yn") == null ? "N" : gettedGrEvalshtInfo.get("cnfd_yn").toString();
		if("Y".equals(cnfdYn)) {  // 확정 상태
			// param 상태와 불일치 (미확정 상태일 때만 삭제 가능)
			return ResultMap.INVALID();
		}
		
		// 3. DELETE
		this.updateGrEvalshtStsByDelete(grEvalshtInfo);  // 입고 평가시트 삭제
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	private ResultMap updateGrEvalshtStsByDelete(Map param) {
		Map resultMap = Maps.newHashMap();
		
		grEvalshtSetupRepository.updateGrEvalshtStsByDelete(param);
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveGrEvalshtPrcs(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map grEvalshtInfoPrcs = (Map) param.get("grEvalshtInfoPrcs");
		Map evalTmplInfo = (Map) param.get("evalTmplInfo");  // evaltmpl 정보
		Map evaltrInfo = (Map) param.get("evaltrInfo");  // evaltr 정보
		
		// 1. 평가 템플릿 저장
		ResultMap createEvalTmplResult = grEvalSetupEventPublisher.saveEvalTmplInfo(evalTmplInfo);
		
		if(ResultMap.STATUS.SUCCESS.equals(createEvalTmplResult.getResultStatus())) {
			Map evalsheetPrcsParam = Maps.newHashMap();  // 평가 프로세스 저장 Param
			Map evaltrParam = Maps.newHashMap();         // 평가자 저장 Param
			List<Map> grEvalshtPrcs = Lists.newArrayList();
			
			// 2-1. 입고 평가시트 프로세스 - 평가 템플릿 매핑
			Map createEvalTmplResultData = createEvalTmplResult.getResultData();
			Map savedEvalTmplInfo = (Map) createEvalTmplResultData.get("evalTmplInfo");
			evalsheetPrcsParam.put("evaltmpl_uuid", savedEvalTmplInfo.get("evaltmpl_uuid"));
			
			// 2-2. 입고 평가시트 - 입고 평가시트 프로세스 저장
			String grEvalshtUuid = gegInfo.get("gr_evalsht_uuid") == null ? "" : gegInfo.get("gr_evalsht_uuid").toString();
			grEvalshtPrcs.add(grEvalshtInfoPrcs);
			
			evalsheetPrcsParam.put("gr_evalsht_uuid", grEvalshtUuid);
			evalsheetPrcsParam.put("grEvalshtPrcs", grEvalshtPrcs);
			this.saveListGrEvalshtPrcs(evalsheetPrcsParam);
			
			// 3. 온보딩 평가시트 프로세스 항목별 담당자 저장
			evaltrParam.put("grEvalshtInfoPrcs", grEvalshtInfoPrcs);
			evaltrParam.put("evaltrInfo", evaltrInfo);
			this.saveListGrEvalshtPrcsEvaltr(evaltrParam);
		} else {
			return ResultMap.FAIL(gegInfo);
		}
		
		Map resultInfo = Maps.newHashMap();
		resultInfo.put("gegInfo", gegInfo);
		resultInfo.put("grEvalshtInfoPrcs", grEvalshtInfoPrcs);
		return ResultMap.SUCCESS(resultInfo);
	}
	
	public List findListGrEvalshtPrcsEvaltr(Map param) {
		return grEvalshtSetupRepository.findListGrEvalshtPrcsEvaltr(param);
	}
	
	public ResultMap saveListGrEvalshtPrcsEvaltr(Map param) {
		Map grEvalshtInfoPrcs = (Map) param.get("grEvalshtInfoPrcs");
		Map evaltrInfo = (Map) param.get("evaltrInfo");  // evaltr 정보
		
		// 평가항목별 담당자 저장
		String grEvalshtPrcsUuid = grEvalshtInfoPrcs.get("gr_evalsht_prcs_uuid").toString();
		List<Map> insertEvaltrList = (List<Map>) evaltrInfo.get("insertEvaltrList");
		List<Map> updateEvaltrList = (List<Map>) evaltrInfo.get("updateEvaltrList");
		List<Map> deleteEvaltrList = (List<Map>) evaltrInfo.get("deleteEvaltrList");
		
		if(insertEvaltrList != null && !insertEvaltrList.isEmpty()) {
			for(final Map row : insertEvaltrList) {
				row.put("gr_evalsht_prcs_uuid", grEvalshtPrcsUuid);
				grEvalshtSetupRepository.insertGrEvalshtPrcsEvaltr(row);
			}
		}
		if(updateEvaltrList != null && !updateEvaltrList.isEmpty()) {
			for(final Map row : updateEvaltrList) {
				grEvalshtSetupRepository.updateGrEvalshtPrcsEvaltr(row);
			}
		}
		if(deleteEvaltrList != null && !deleteEvaltrList.isEmpty()) {
			for(final Map row : deleteEvaltrList) {
				grEvalshtSetupRepository.updateGrEvalshtPrcsEvaltrByDelete(row);
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveAllGrEvalshtAndPrcses(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		List<Map> prcsInfoList = (List<Map>) param.get("prcsInfoList");
		
		// 1. 입고 평가시트 수정
		grEvalshtSetupRepository.updateGrEvalsht(grEvalshtInfo);
		
		// 2. 입고 평가시트 - 입고 평가시트 프로세스 저장
		Map saveGrEvalshtPrcsParam = Maps.newHashMap();
		saveGrEvalshtPrcsParam.put("gegInfo", gegInfo);
		for(Map prcsInfo : prcsInfoList) {
			saveGrEvalshtPrcsParam.put("grEvalshtInfoPrcs", prcsInfo.get("grEvalshtInfoPrcs"));
			saveGrEvalshtPrcsParam.put("evalTmplInfo", prcsInfo.get("evalTmplInfo"));
			saveGrEvalshtPrcsParam.put("evaltrInfo", prcsInfo.get("evaltrInfo"));
			ResultMap saveGrEvalshtPrcsResultMap = this.saveGrEvalshtPrcs(saveGrEvalshtPrcsParam);
			
			if(!ResultMap.STATUS.SUCCESS.equals(saveGrEvalshtPrcsResultMap.getResultStatus())) {
				return ResultMap.FAIL();
			}
		}
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	public ResultMap saveCnfdYnGrEvalsht(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		String cnfdYn = param.get("cnfdYn").toString();
		
		// 1. 입고평가그룹 - 평가시트이력 조회
		List<Map> grEvalshtHis = this.findListGrEvalshtHis(gegInfo);
		
		// 2. 확정요청한 입고 평가시트 정보 조회 (= 최신 입고 평가시트)
		Map grEvalshtInfo = this.findGrCurrentEvalsht(grEvalshtHis);
		
		// 2. 입고 평가시트 프로세스 - 평가템플릿 UUID 리스트 조회
		List<String> evaltmplUuidList = grEvalshtSetupRepository.findListEvaltmplUuidOfGrEvalshtPrcses(grEvalshtInfo);
		
		if(grEvalshtInfo != null && !grEvalshtInfo.isEmpty()) {
			if("N".equals(cnfdYn)) {  // 3-1) '확정취소' 처리인 경우, 입고 평가시트 사용여부 체크 (GE 생성 여부)
				String isCreateGeYn = grEvalMonitoringService.findCreateGeYnByGrEvalsht(grEvalshtInfo);
				if("Y".equals(isCreateGeYn)) {  // GE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}
			
			// 3. 이전 입고 평가시트 유효일자 update
			// 4. 입고 평가시트 프로세스 이전 평가 템플릿 정보 update
			// 5. 입고 평가시트 확정 update
			grEvalshtInfo.put("cnfd_yn", cnfdYn);
			grEvalshtSetupRepository.updatePrevGrEvalsht(grEvalshtInfo);
			grEvalshtSetupRepository.updatePrevEvaltmplUuidGrEvalshtPrcs(grEvalshtInfo);
			grEvalshtSetupRepository.updateCnfdYnGrEvalsht(grEvalshtInfo);
			
			if("Y".equals(cnfdYn)) {  // '확정' 처리인 경우, evaltmpl에 update
				// 7. 평가템플릿 확정 update
				Map evalTmplParam = Maps.newHashMap();
				evalTmplParam.put("evaltmpl_uuids", evaltmplUuidList);
				evalTmplParam.put("cnfd_yn", cnfdYn);
				grEvalSetupEventPublisher.updateCnfdYnEvalTmpl(evalTmplParam);
			}
		} else {
			return ResultMap.FAIL();
		}
		
		return ResultMap.SUCCESS(gegInfo);
	}
	
	public FloaterStream findListGrEvalsht(Map param) {
		// 대용량 처리
		return grEvalshtSetupRepository.findListGrEvalsht(param);
	}
	
	public void saveMappingEvaltmplUuidToGrEvalshtPrcs(Map data) {
		grEvalshtSetupRepository.saveMappingEvaltmplUuidToGrEvalshtPrcs(data);
	}

	/**
	 * 평가템플릿 상태값을 조회한다. (입고/기성평가 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 */
	public String findEvalTmplStsInGeEvalSht(Map<String, Object> param) {
		List<String> sheetList = grEvalshtSetupRepository.findEvalTmplStsInGeEvalSht(param);
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
}