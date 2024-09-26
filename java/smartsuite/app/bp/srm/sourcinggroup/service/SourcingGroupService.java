package smartsuite.app.bp.srm.sourcinggroup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.sourcinggroup.event.SourcingGroupEventPublisher;
import smartsuite.app.bp.srm.sourcinggroup.repository.SourcingGroupRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SourcingGroupService {

	@Inject
	SourcingGroupRepository sourcingGroupRepository;

	@Inject
	SourcingGroupEventPublisher sourcingGroupEventPublisher;

	@Inject
	private transient SharedService sharedService;

	/**
	 * 소싱그룹 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public FloaterStream findListSourcingGroup(final Map<String, Object> param) {
		// 대용량 처리
		return sourcingGroupRepository.findListSourcingGroup(param);
	}


	/**
	 * 소싱그룹 목록을 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap saveListSourcingGroup(final Map<String, Object> param) {
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		final List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");

		if(inserts != null && !inserts.isEmpty()){
			for (final Map<String, Object> row : inserts) {
				String sgCode = sharedService.generateDocumentNumber("SG");
				row.put("sg_cd", sgCode);
				sourcingGroupRepository.insertSourcingGroupInfo(row);	// 소싱그룹 신규 저장
			}
		}

		if(updates != null && !updates.isEmpty()){
			for (final Map<String, Object> row : updates) {
				sourcingGroupRepository.updateSourcingGroup(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 목록을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap deleteListSourcingGroup(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				sourcingGroupRepository.updateStatusSourcingGroupForDelete(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 상세정보를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	public Map<String, Object> findSourcingGroupInfo(final Map<String, Object> param) {
		return sourcingGroupRepository.findSourcingGroupInfo(param);
	}


	/**
	 * 소싱그룹 정보를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap saveSourcingGroupInfo(final Map<String, Object> param) {
		sourcingGroupRepository.updateSourcingGroup(param);

		// 협력사관리그룹 수정
		sourcingGroupEventPublisher.updateVenderManagementGroupNameBySourcingGroup(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 협력사 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public FloaterStream findListSourcingGroupVendor(final Map<String, Object> param) {
		return sourcingGroupRepository.findListSourcingGroupVendor(param);
	}

	/**
	 * 소싱그룹 협력사 목록을 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap saveListSourcingGroupVendor(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");

		if (inserts != null && !inserts.isEmpty()) {
			for(Map<String, Object> row : inserts) {
				sourcingGroupRepository.insertSourcingGroupVendor(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 담당자 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public List findListSourcingGroupUser(final Map<String, Object> param) {
		return sourcingGroupRepository.findListSourcingGroupUser(param);
	}

	/**
	 * 소싱그룹 담당자 목록을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap deleteListSourcingGroupUser(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");
		final Map<String, Object> sgInfo = (Map<String, Object>)param.get("sgInfo");

//		2024.06.20 주석처리 (FT-SMARTTEN-268)
//		String vmgYn = sgInfo.get("vmg_yn").toString();
//		String vmgOorgUuid = sgInfo.get("vmg_oorg_uuid") == null ? null : sgInfo.get("vmg_oorg_uuid").toString();

		// DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				sourcingGroupRepository.deleteListSourcingGroupUser(row);

				// 협력사관리그룹 평가자 삭제
				/*if("Y".equals(vmgYn)){
					row.put("vmg_oorg_uuid", vmgOorgUuid);
					row.put("evaltr_id", row.get("pic_id"));
					sourcingGroupEventPublisher.deleteVendorManagementGroupEvaltr(row);
				}*/
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 담당자 목록을 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap saveListSourcingGroupUser(final Map<String, Object> param) {
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		final List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");
		final Map<String, Object> sgInfo = (Map<String, Object>)param.get("sgInfo");

		// 2024.06.20 주석처리 (FT-SMARTTEN-268)
//		String vmgYn = sgInfo.get("vmg_yn").toString();
//		String vmgOorgUuid = sgInfo.get("vmg_oorg_uuid") == null ? null : sgInfo.get("vmg_oorg_uuid").toString();

		if (inserts != null && !inserts.isEmpty()) {
			for (final Map<String, Object> row : inserts) {
				sourcingGroupRepository.insertSourcingGroupUser(row);

				// 평가자여부 Y인 담당자는 협력사관리그룹 평가자에 추가
				/*String evaltrYn = row.get("evaltr_yn") == null ? "N" : row.get("evaltr_yn").toString();
				if("Y".equals(vmgYn) && "Y".equals(evaltrYn)){
					row.put("vmg_oorg_uuid", vmgOorgUuid);
					row.put("evaltr_id", row.get("pic_id"));
					sourcingGroupEventPublisher.insertVendorManagementGroupEvaltr(row);
				}*/
			}
		}

		if (updates != null && !updates.isEmpty()) {
			for (final Map<String, Object> row : updates) {
				sourcingGroupRepository.updateSourcingGroupUser(row);
				String evaltrYn = row.get("evaltr_yn") == null ? "N" : row.get("evaltr_yn").toString();

				// 담당자의 평가자여부에 따라 협력사관리그룹 평가자 추가/삭제
				/*if("Y".equals(vmgYn)){
					row.put("vmg_oorg_uuid", vmgOorgUuid);
					row.put("evaltr_id", row.get("pic_id"));
					if("Y".equals(evaltrYn)){
						sourcingGroupEventPublisher.insertVendorManagementGroupEvaltr(row);
					} else {
						sourcingGroupEventPublisher.deleteVendorManagementGroupEvaltr(row);
					}
				}*/
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 협력사관리그룹 평가자 저장/삭제 정보, 소싱그룹 담당자 정보에 동기화한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 8. 09
	 */
	public void mergeSourcingGroupPic(final Map<String, Object> param) {
		sourcingGroupRepository.mergeSourcingGroupPic(param);
	}

	/**
	 * 소싱그룹 품목 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public FloaterStream findListSourcingGroupItem(final Map<String, Object> param) {
		return sourcingGroupRepository.findListSourcingGroupItem(param);
	}

	/**
	 * 소싱그룹 품목 목록을 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap saveListSourcingGroupItem(final Map<String, Object> param) {
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertItems");

		if (inserts != null && !inserts.isEmpty()) {
			for (final Map<String, Object> row : inserts) {
				sourcingGroupRepository.insertSourcingGroupItem(row);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 소싱그룹 품목 목록을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public ResultMap deleteListSourcingGroupItem(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteItems");

		if (deletes != null && !deletes.isEmpty()) {
			for (final Map<String, Object> row : deletes) {
				sourcingGroupRepository.deleteSourcingGroupItem(row);
			}
		}
		return ResultMap.SUCCESS();
	}
	/**
	 * 품목분류관리 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public FloaterStream findListCate(Map<String, Object> param) {
		return sourcingGroupRepository.findListCate(param);
	}

	/**
	 * list cate item 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public List<Map<String, Object>> findListCateItem(Map<String, Object> param) {
		return sourcingGroupRepository.findListCateItem(param);
	}
	/**
	 * EO 협력사 리스트를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 29
	 */
	public List<Map<String, Object>> findListVendorInfoEO(Map<String, Object> param) {
		return sourcingGroupRepository.findListVendorInfoEO(param);
	}
}

