package smartsuite.app.bp.vendorMaster.vdMgmtSetup.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.transform.Result;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.vendorMaster.vdMgmtSetup.event.VdMgmtSetupEventPublisher;
import smartsuite.app.bp.vendorMaster.vdMgmtSetup.repository.VdMgmtSetupRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.Floater;
import smartsuite.data.FloaterStream;
import smartsuite.module.ModuleManager;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import static java.util.stream.Collectors.toList;

/**
 * vendor mgmt setup 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName VdMgmtSetupService.java
 * @package smartsuite.app.bp.vendorMaster.vdMgmtSetup.service
 * @Since 2023. 5. 30
 * @변경이력 : [2023. 5. 30] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class VdMgmtSetupService {

	@Inject
	private VdMgmtSetupRepository vdMgmtSetupRepository;

	@Inject
	private VdMgmtSetupEventPublisher vdMgmtSetupEventPublisher;

	@Inject
	private SharedService sharedService;

	/**
	 * 협력사관리유형 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the list vendor mgmt type
	 * @Date : 2023. 5. 30
	 * @Method Name : findListVmt
	 */
	public FloaterStream findListVmt(Map<String, Object> param) {
		// 대용량 처리
		return vdMgmtSetupRepository.findListVmt(param);
	}

	/**
	 * 미등록 협력사관리유형 조회 (findListUnregisteredVendorManagementType)
	 *
	 * @param param
	 * @Date : 2023. 09. 05
	 * @author yjPark
	 */
	public List findListUnregisteredVendorManagementType(Map<String, Object> param) {
		return vdMgmtSetupRepository.findListUnregisteredVendorManagementType(param);
	}

	/**
	 * 협력사관리유형 목록을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 5. 30
	 * @Method Name : saveListVmt
	 */
	public ResultMap saveListVmt(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		if (inserts != null && !inserts.isEmpty()) {
			for (Map<String, Object> row : inserts) {
				vdMgmtSetupRepository.insertListVmt(row);
			}
		}

		if (updates != null && !updates.isEmpty()) {
			for (Map<String, Object> row : updates) {
				vdMgmtSetupRepository.updateListVmt(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사관리유형 목록을 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 5. 30
	 * @Method Name : deleteListVmt
	 */
	public ResultMap deleteListVmt(Map<String, Object> param){
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		// DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes){
				vdMgmtSetupRepository.updateVmtStsByDelete(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 사용대상 협력사관리유형 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the list using vendor mgmt type
	 * @Date : 2023. 5. 31
	 * @Method Name : findListVmtUsing
	 */
	public FloaterStream findListVmtUsing(Map<String, Object> param) {
		param.put("use_yn", "Y");
		// 대용량 처리
		return vdMgmtSetupRepository.findListVmt(param);
	}

	/**
	 * 협력사관리그룹 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the list vendor mgmt grp
	 * @Date : 2023. 6. 1
	 * @Method Name : findListVmg
	 */
	public FloaterStream findListVmg(Map<String, Object> param) {
		// 대용량 처리
		return vdMgmtSetupRepository.findListVmg(param);
	}

	/**
	 * 협력사관리그룹 목록을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : saveListVmg
	 */
	public ResultMap saveListVmg(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		//SG와 VMG가 통합 됨에 따른 SG 중복 validation 제거
		//ResultMap validResultMap = this.checkValidBeforeSaveListVmg(inserts);

		if (inserts != null && !inserts.isEmpty()) {
			for (Map<String, Object> row : inserts) {
				// VMG Type 사라짐
				row.put("vmg_cd", sharedService.generateDocumentNumber("SG")); // 소싱그룹 코드 채번

				// 1. 협력사관리그룹 저장
				row.put("vmg_uuid", UUID.randomUUID().toString());
				vdMgmtSetupRepository.insertListVmg(row);
			}
		}

		if (updates != null && !updates.isEmpty()) {
			for (Map<String, Object> row : updates) {
				vdMgmtSetupRepository.updateListVmg(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 상세 저장
	 *
	 * @author : kim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2024. 7. 23
	 * @Method Name : saveSgDetail
	 */
	public ResultMap saveSgDetail(Map<String, Object> param) {
		vdMgmtSetupRepository.updateListVmg(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 담당자 중 평가자, 협력사관리그룹 평가자로 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 9
	 * @Method Name : saveVmgEvaltrByUsingSgPic
	 */
	public void saveVmgEvaltrByUsingSgPic(Map<String, Object> param) {
		// 1. 소싱그룹 담당자 중 평가자 목록 조회
		List<Map<String, Object>> SgPicEvaltrList = vdMgmtSetupEventPublisher.findListSourcingGroupUser(param);

		for (final Map<String, Object> row : SgPicEvaltrList) {
			row.put("vmg_uuid", param.get("vmg_uuid"));
			row.put("evaltr_id", row.get("pic_id"));
			vdMgmtSetupRepository.insertVendorManagementGroupEvaltr(row);
		}
	}

	/**
	 * 협력사관리그룹 목록을 저장 전 validation을 체크한다.
	 *  -> SG와 VMG 통합에 따른 Validation 제거 예정
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 4
	 * @Method Name : checkValidBeforeSaveListVmg
	 */
	@Deprecated
	public ResultMap checkValidBeforeSaveListVmg(List<Map<String, Object>> param) {
		ResultMap resultMap = ResultMap.getInstance();

		if (param != null && !param.isEmpty()) {
			for (Map<String, Object> row : param) {
				Map<String, Object> validResult = vdMgmtSetupRepository.checkValidBeforeSaveListVmg(row);

				String validResultTyp = validResult.get("valid_result_typ").toString();
				if(!"VALID_PASS".equals(validResultTyp)){
					validResult.put("validDataMap", row);
					resultMap.setResultData(validResult);
					resultMap.setResultStatus(ResultMap.STATUS.INVALID_STATUS_ERR);
					return resultMap;
				}
			}
		}

		return resultMap.SUCCESS();
	}

	/**
	 * 협력사관리그룹 목록을 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteListVmg
	 */
	public ResultMap deleteListVmg(Map<String, Object> param){
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		//OORG VMG에 데이터가 존재하는지 한번 더 검증 로직
		Map<String, Object> validateParameter = new HashMap<String, Object>();
		validateParameter.put("vmg_uuid", deletes.stream().map(delete -> delete.get("vmg_uuid")).collect(toList()));
		int usingVmgOorg =  vdMgmtSetupRepository.validateDeleteVmg(validateParameter);
		if(usingVmgOorg > 0 ) {
			return ResultMap.USED();
		}

		// DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes){
				vdMgmtSetupRepository.updateVmgStsByDelete(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 수정된 소싱그룹 명으로 협력사관리그룹 명을 수정한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 8. 7
	 * @Method Name : updateVenderManagementGroupNameBySourcingGroup
	 */
	public void updateVenderManagementGroupNameBySourcingGroup(Map<String, Object> param) {
		vdMgmtSetupRepository.updateVenderManagementGroupNameBySourcingGroup(param);
	}
	
	/**
	 * 소싱그룹 상세정보를 조회한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 7. 23
	 * @Method Name : findSourcingGroupDetail
	 */
	public Map<String, Object> findSourcingGroupDetail(Map<String, Object> param) {
		return vdMgmtSetupRepository.findSourcingGroupDetail(param);
	}

	/**
	 * 협력사관리그룹 품목 분류 목록을 조회한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 7. 22
	 * @Method Name : findSourcingGroupItemCategoryList
	 */
	public List<Map<String, Object>> findSourcingGroupItemCategoryList(Map<String, Object> param) {
		return vdMgmtSetupRepository.findSourcingGroupItemCategoryList(param);
	}

	/**
	 * 협력사관리그룹 품목 분류 저장
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 7. 22
	 * @Method Name : saveSgItemCategoryList
	 */
	public ResultMap saveSgItemCategoryList(Map<String, Object> param) {
		List<Map<String, Object>> itemList = (List<Map<String, Object>>)param.getOrDefault("itemList", toList());

		itemList.stream().forEach(item -> {
			item.put("sg_cd", param.get("sg_cd"));
			vdMgmtSetupRepository.saveSgItemCategoryList(item);
		});

		return ResultMap.SUCCESS();
	}

	/**
	 * 운영조직 협력사관리그룹을 조회한다
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 05. 28
	 * @Method Name : findListOorgVmg
	 */
	public FloaterStream findListOorgVmg(Map<String, Object> param) {
		return vdMgmtSetupRepository.findListOorgVmg(param);
	}

	/**
	 * 운영조직 협력사관리그룹을 저장한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 05. 28
	 * @Method Name : saveListOorgVmg
	 */
	public ResultMap saveListOrogVmg(Map<String, Object> param) {
		List<Map<String, Object>> insertList = (List<Map<String, Object>>) param.get("insertList");
		List<Map<String, Object>> updateList = (List<Map<String, Object>>) param.get("updateList");

		if(insertList != null && insertList.size() > 0) {
			for(Map<String, Object> insert : insertList) {
				insert.put("vmg_oorg_uuid", UUID.randomUUID().toString());
				vdMgmtSetupRepository.insertListOorgVmg(insert);
			}
		}

		if(updateList != null && updateList.size() > 0) {
			for(Map<String, Object> update : updateList) {
				vdMgmtSetupRepository.updateListOorgVmg(update);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 운영조직 협력사관리그룹을 삭제한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 05. 28
	 * @Method Name : deleteListOorgVmg
	 */
	public ResultMap deleteListOorgVmg(Map<String, Object> param) {
		ModuleManager moduleManager = new ModuleManager();
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>) param.get("deleteList");

		//사용 여부 check validation 추가
		//VS 또는 SRM 모듈이 존재하는 지 check
		if(moduleManager.exist("vs") || moduleManager.exist("srm")) {
			Map<String, Object> validateParam = new HashMap<String, Object>();
			validateParam.put("vmg_oorg_uuid", deleteList.stream().map(row -> row.get("vmg_oorg_uuid")).collect(toList()));
			List<Map<String, Object>> usingXeg = vdMgmtSetupRepository.validateDeleteVmgOorg(validateParam);
			//PEG 혹은 OEG 에서 사용 중
			if(usingXeg.size() > 0) {
				return ResultMap.USED();
			}
		}

		if(deleteList != null && deleteList.size() > 0) {
			for(Map<String, Object> delete : deleteList) {
				vdMgmtSetupRepository.deleteListOorgVmg(delete);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 운영조직과 매핑되지 않은 협력사관리그룹을 조회한다.
	 *
	 * @author : kim
	 * @param param the param
	 * @Date : 2024. 05. 28
	 * @Method Name : findNonMappingOorgVmg
	 */
	public FloaterStream findListNonMappingOorgVmg(Map<String, Object> param) {
		return vdMgmtSetupRepository.findListNonMappingOorgVmg(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 7
	 * @Method Name : findListVendorManagementGroupEvaltr
	 */
	public FloaterStream findListVendorManagementGroupEvaltr(final Map<String, Object> param) {
		return vdMgmtSetupRepository.findListVendorManagementGroupEvaltr(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록을 조회한다. (업무 평가그룹 설정 조회용)
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 10
	 * @Method Name : findListVendorManagementGroupEvaltrForView
	 */
	public List findListVendorManagementGroupEvaltrForView(final Map<String, Object> param) {
		return vdMgmtSetupRepository.findListVendorManagementGroupEvaltrForView(param);
	}

	/**
	 * 협력사관리그룹 평가자 목록을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 7
	 * @Method Name : saveListVendorManagementGroupEvaltr
	 */
	public ResultMap saveListVendorManagementGroupEvaltr(final Map<String, Object> param) {
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");

		if (inserts != null && !inserts.isEmpty()) {
			for (final Map<String, Object> row : inserts) {
				vdMgmtSetupRepository.insertVendorManagementGroupEvaltr(row);

				if("SG".equals(row.get("vmg_typ_ccd"))) {  // 협력사관리그룹유형이 '소싱그룹' 인 경우,
					vdMgmtSetupEventPublisher.saveSyncVendorManagementGroupEvaltrToSourcingGroupPic(row);  // SG 담당자 정보와 동기화
				}
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사관리그룹 평가자 목록을 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 7
	 * @Method Name : deleteListVendorManagementGroupEvaltr
	 */
	public ResultMap deleteListVendorManagementGroupEvaltr(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		// DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				vdMgmtSetupRepository.deleteListVendorManagementGroupEvaltr(row);

				if("SG".equals(row.get("vmg_typ_ccd"))){  // 협력사관리그룹유형이 '소싱그룹' 인 경우,
					vdMgmtSetupEventPublisher.deleteSyncVendorManagementGroupEvaltrToSourcingGroupPic(row);  // SG 담당자 정보와 동기화
				}
			}
		}

		return ResultMap.SUCCESS();
	}
	
	/**
	 * 소싱그룹 담당자의 평가자여부에 따라 협력사관리그룹 평가자를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 10
	 * @Method Name : insertVendorManagementGroupEvaltrBySourcingGroup
	 */
	public void insertVendorManagementGroupEvaltrBySourcingGroup(final Map<String, Object> param) {
		vdMgmtSetupRepository.insertVendorManagementGroupEvaltr(param);
	}

	/**
	 * 소싱그룹 담당자의 평가자여부에 따라 협력사관리그룹 평가자를 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 10
	 * @Method Name : deleteVendorManagementGroupEvaltrBySourcingGroup
	 */
	public void deleteVendorManagementGroupEvaltrBySourcingGroup(final Map<String, Object> param) {
		vdMgmtSetupRepository.deleteVendorManagementGroupEvaltrBySourcingGroup(param);
	}
}
