package smartsuite.app.bp.pro.gr.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.repository.GrEvalSetupRepository;
import smartsuite.app.bp.pro.gr.validator.GrEvalSetupValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GrEvalSetupService {
	
	@Inject
	GrEvalshtSetupService grEvalshtSetupService;
	
	@Inject
	GrEvalMonitoringService grEvalMonitoringService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	GrEvalSetupRepository grEvalSetupRepository;
	
	@Inject
	GrEvalSetupValidator grEvalSetupValidator;
	
	public List findListGemt(Map param) {
		return grEvalSetupRepository.findListGemt(param);
	}
	
	public ResultMap saveListGemt(Map param) {
		List<Map> insertList = (List<Map>) param.get("insertList");
		List<Map> updateList = (List<Map>) param.get("updateList");
		
		ResultMap validator = grEvalSetupValidator.insertGemt(insertList);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		this.insertGemt(insertList);
		this.updateGemt(updateList);
		
		return ResultMap.SUCCESS();
	}
	
	private void updateGemt(List<Map> updateList) {
		if(updateList == null) {
			return;
		}
		if(updateList.isEmpty()) {
			return;
		}
		for(Map update : updateList) {
			this.updateGemt(update);
		}
	}
	
	private void updateGemt(Map update) {
		grEvalSetupRepository.updateGemt(update);
	}
	
	private void insertGemt(List<Map> insertList) {
		if(insertList == null) {
			return;
		}
		if(insertList.isEmpty()) {
			return;
		}
		for(Map insert : insertList) {
			this.insertGemt(insert);
		}
	}
	
	private void insertGemt(Map insert) {
		insert.put("gemt_uuid", UUID.randomUUID().toString());
		grEvalSetupRepository.insertGemt(insert);
	}
	
	public ResultMap deleteListGemt(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		ResultMap validator = grEvalSetupValidator.deleteGemt(deleteList);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		this.deleteGemt(deleteList);
		return ResultMap.SUCCESS();
	}
	
	private void deleteGemt(List<Map> deleteList) {
		if(deleteList == null) {
			return;
		}
		if(deleteList.isEmpty()) {
			return;
		}
		for(Map delete : deleteList) {
			this.deleteGemt(delete);
		}
	}
	
	private void deleteGemt(Map delete) {
		grEvalSetupRepository.deleteGemt(delete);
	}
	
	public List findListOorgGemt(Map param) {
		return grEvalSetupRepository.findListOorgGemt(param);
	}
	
	public List findListGemg(Map param) {
		return grEvalSetupRepository.findListGemg(param);
	}
	
	public ResultMap saveListGemg(Map param) {
		List<Map> insertList = (List<Map>) param.get("insertList");
		List<Map> updateList = (List<Map>) param.get("updateList");
		
		this.insertGemg(insertList);
		this.updateGemg(updateList);
		
		return ResultMap.SUCCESS();
	}
	
	private void insertGemg(List<Map> insertList) {
		if(insertList == null) {
			return;
		}
		if(insertList.isEmpty()) {
			return;
		}
		for(Map insert : insertList) {
			this.insertGemg(insert);
		}
	}
	
	private void insertGemg(Map insert) {
		insert.put("gemg_uuid", UUID.randomUUID().toString());
		grEvalSetupRepository.insertGemg(insert);
	}
	
	private void updateGemg(List<Map> updateList) {
		if(updateList == null) {
			return;
		}
		if(updateList.isEmpty()) {
			return;
		}
		for(Map update : updateList) {
			this.updateGemg(update);
		}
	}
	
	private void updateGemg(Map update) {
		grEvalSetupRepository.updateGemg(update);
	}
	
	public ResultMap deleteListGemg(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		
		/*ResultMap validator = grEvalSetupValidator.deleteGemt(deleteList);
		if(!validator.isSuccess()) {
			return validator;
		}*/
		
		this.deleteGemg(deleteList);
		return ResultMap.SUCCESS();
	}
	
	private void deleteGemg(List<Map> deleteList) {
		if(deleteList == null) {
			return;
		}
		if(deleteList.isEmpty()) {
			return;
		}
		for(Map delete : deleteList) {
			this.deleteGemg(delete);
		}
	}
	
	private void deleteGemg(Map delete) {
		grEvalSetupRepository.deleteGemg(delete);
	}
	
	public List findListGeg(Map param) {
		return grEvalSetupRepository.findListGeg(param);
	}
	
	public ResultMap findGeg(Map param) {
		Map results = new HashMap();
		
		// 1. 입고평가그룹 Mater 조회
		Map gegInfo = grEvalSetupRepository.findGegMaster(param);
		
		// 2. 입고평가그룹 - 입고평가관리그룹 조회
		List<Map> gegGemgs = grEvalSetupRepository.findListGegGemg(param);
		
		// 3. 입고평가그룹 - 평가시트이력 조회
		List<Map> grEvalshtHis = grEvalshtSetupService.findListGrEvalshtHis(param);
		
		// 5. 입고 평가시트 조회
		Map grEvalshtInfo = grEvalshtSetupService.findGrCurrentEvalsht(grEvalshtHis);
		
		results.put("gegInfo", gegInfo);
		results.put("gegGemgs", gegGemgs);
		results.put("grEvalshtHis", grEvalshtHis);
		results.put("grEvalshtInfo", grEvalshtInfo);
		return ResultMap.SUCCESS(results);
	}
	
	public ResultMap saveGeg(Map param) {
		Map gegInfo = (Map) param.get("gegInfo");
		Map targetGemgInfo = (Map) param.get("targetGemgInfo");
		
		String gegUuid = gegInfo.get("geg_uuid") == null ? "" : gegInfo.get("geg_uuid").toString();
		String oorgCd = gegInfo.get("oorg_cd") == null ? "" : gegInfo.get("oorg_cd").toString();
		if("".equals(gegUuid)) {  // 신규
			gegUuid = UUID.randomUUID().toString();
			String gegCd = sharedService.generateDocumentNumber("GEG");
			gegInfo.put("geg_uuid", gegUuid);
			gegInfo.put("geg_cd", gegCd);
			
			// 1. 온보딩평가그룹 Mater 저장
			grEvalSetupRepository.insertGegMaster(gegInfo);
		} else {  // 수정
			// 1. 온보딩평가그룹 Mater 저장
			grEvalSetupRepository.updateGegMaster(gegInfo);
		}
		
		// 2. 입고평가그룹 - 입고평가관리그룹 저장
		Map gegGemgParam = Maps.newHashMap();
		gegGemgParam.put("geg_uuid", gegUuid);
		gegGemgParam.put("targetGemgInfo", targetGemgInfo);
		this.saveListGegGemg(gegGemgParam);
		
		Map data = Maps.newHashMap();
		data.put("geg_uuid", gegUuid);
		data.put("oorg_cd", oorgCd);
		
		return ResultMap.SUCCESS(data);
	}
	
	private void saveListGegGemg(Map param) {
		String gegUuid = param.get("geg_uuid") == null ? "" : param.get("geg_uuid").toString();
		Map targetGemgInfo = (Map) param.get("targetGemgInfo");
		List<Map> insertTargetGemgList = (List<Map>) targetGemgInfo.get("insertTargetGemgList");
		List<Map> deleteTargetGemgList = (List<Map>) targetGemgInfo.get("deleteTargetGemgList");
		
		if(insertTargetGemgList != null && !insertTargetGemgList.isEmpty()) {
			for(Map row : insertTargetGemgList) {
				// 1. 타 퍼포먼스평가그룹에서 사용 중인 insert 협력사 관리그룹 삭제
				grEvalSetupRepository.deleteGegGemg(row);
				
				// 2. 퍼포먼스평가그룹 - 협력사 관리 그룹 저장
				row.put("geg_uuid", gegUuid);
				grEvalSetupRepository.insertGegGemg(row);
			}
		}
		if(deleteTargetGemgList != null && !deleteTargetGemgList.isEmpty()) {
			for(Map row : deleteTargetGemgList) {
				grEvalSetupRepository.deleteGegGemg(row);
			}
		}
	}
	
	public ResultMap checkValidBeforeSaveGeg(Map param) {
		Map resultInfo = Maps.newHashMap();
		Boolean validPass = true;
		Map gegInfo = (Map) param.get("gegInfo");
		Map targetGemgInfo = (Map) param.get("targetGemgInfo");
		List<Map> insertTargetGemgList = (List<Map>) targetGemgInfo.get("insertTargetGemgList");
		
		if(insertTargetGemgList != null && !insertTargetGemgList.isEmpty()) {
			for(Map row : insertTargetGemgList) {
				Map gegInfoInUse = grEvalSetupRepository.findGegInfoInUsingGemg(row);
				
				if(gegInfoInUse != null) {  // 존재
					validPass = false;
					break;
				}
			}
		}
		
		ResultMap resultMap = ResultMap.getInstance();
		if(!validPass) {
			return resultMap.INVALID();
		}
		
		return resultMap.SUCCESS();
	}
	
	public ResultMap saveCnfdYnGrEvalsht(Map param) {
		ResultMap result = grEvalshtSetupService.saveCnfdYnGrEvalsht(param);
		
		if(!result.isSuccess()) {
			return result;
		}
		
		Map gegInfo = result.getResultData();
		
		// 8. 입고평가그룹 저장
		grEvalSetupRepository.updateGegMaster(gegInfo);
		return ResultMap.SUCCESS(gegInfo);
	}
	
	public ResultMap deleteListGeg(Map param) {
		List<Map> deletes = (List<Map>) param.get("deleteList");
		
		// 1. 입고평가그룹 사용여부 체크 (GEG 생성 여부)
		if(deletes != null && !deletes.isEmpty()) {
			for(Map row : deletes) {
				String isCreateGeYn = grEvalMonitoringService.findCreateGeYnByGeg(row);
				if("Y".equals(isCreateGeYn)) {  // GE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}
		}
		
		// 2. DELETE
		if(deletes != null && !deletes.isEmpty()) {
			for(Map row : deletes) {
				grEvalSetupRepository.updateGegByDelete(row);  // 온보딩평가그룹 삭제
				grEvalSetupRepository.updateGegGemgStsByDelete(row);  // 온보딩평가그룹 협력사 관리 그룹 삭제
				// 2023.06.15, yjPark, 어느레벨까지 'D' 처리할 지 고민필요
				/*obdEvalshtSetupService.updateObdEvalshtStsByDelete(row);  // 온보딩 시트 삭제
				obdEvalshtSetupService.updateObdEvalshtPrcsStsByDelete(row);  // 온보딩 시트 - 온보딩 프로세스 삭제
				obdEvalshtSetupService.updateObdEvalshtPrcsStsByDelete(row);  // 온보딩 시트 - 온보딩 프로세스 평가자 삭제*/
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	public List findListGemgEvaltr(Map param) {
		return grEvalSetupRepository.findListGemgEvaltr(param);
	}
	
	public ResultMap deleteListGemgEvaltr(Map param) {
		List<Map> deleteList = (List<Map>) param.get("deleteList");
		this.deleteGemgEvaltr(deleteList);
		
		return ResultMap.SUCCESS();
	}
	
	private void deleteGemgEvaltr(List<Map> deleteList) {
		if(deleteList == null) {
			return;
		}
		if(deleteList.isEmpty()) {
			return;
		}
		for(Map deleteInfo : deleteList) {
			this.deleteGemgEvaltr(deleteInfo);
		}
	}
	
	private void deleteGemgEvaltr(Map deleteInfo) {
		if(deleteInfo == null) {
			return;
		}
		grEvalSetupRepository.deleteGemgEvaltr(deleteInfo);
	}
	
	public ResultMap saveListGemgEvaltr(Map param) {
		List<Map> insertList = (List<Map>) param.get("insertList");
		this.insertGemgEvaltr(insertList);
		
		return ResultMap.SUCCESS();
	}
	
	private void insertGemgEvaltr(List<Map> insertList) {
		if(insertList == null) {
			return;
		}
		if(insertList.isEmpty()) {
			return;
		}
		for(Map insertInfo : insertList) {
			this.insertGemgEvaltr(insertInfo);
		}
	}
	
	private void insertGemgEvaltr(Map insertInfo) {
		if(insertInfo == null) {
			return;
		}
		insertInfo.put("gemg_qly_pic_uuid", UUID.randomUUID().toString());
		grEvalSetupRepository.insertGemgEvaltr(insertInfo);
	}
}
