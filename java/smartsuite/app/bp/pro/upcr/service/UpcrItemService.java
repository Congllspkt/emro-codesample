package smartsuite.app.bp.pro.upcr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.upcr.repository.UpcrItemRepository;
import smartsuite.app.bp.pro.upcr.validator.UpcrValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class UpcrItemService {

	@Inject
	UpcrItemRepository upcrItemRepository;

	@Inject
	SharedService sharedService;

	@Inject
	UpcrValidator upcrValidator;

	/**
	 * 다건의 구매요청 품목을 삭제한다.
	 *
	 * @param upcrItems - 삭제할 구매요청 품목 다건
	 */
	public void deleteUpcrItem(List upcrItems) {
		if(upcrItems == null) {
			return;
		}
		if(upcrItems.isEmpty()) {
			return;
		}

		for(Map upcrItem : (List<Map>) upcrItems) {
			this.deleteUpcrItem(upcrItem);
		}
	}

	/**
	 * 구매요청 품목을 삭제한다.
	 *
	 * @param upcrItem - 삭제할 구매요청 품목
	 */
	public void deleteUpcrItem(Map upcrItem) {
		if(upcrItem == null) {
			return;
		}

		upcrItemRepository.deleteUpcrItem(upcrItem);
	}

	/**
	 * 다건의 구매요청 품목을 신규 저장한다.
	 *
	 * @param upcrData - 구매요청 마스터
	 * @param upcrItems - 신규 저장할 구매요청 품목 다건
	 */
	public void insertUpcrItem(Map upcrData, List upcrItems) {
		if(upcrItems == null) {
			return;
		}
		if(upcrItems.isEmpty()) {
			return;
		}

		for(Map upcrItem : (List<Map>) upcrItems) {
			this.insertUpcrItem(upcrData, upcrItem);
		}
	}

	/**
	 * 구매요청 품목을 신규 저장한다.
	 *
	 * @param upcrData - 구매요청 마스터
	 * @param upcrItem - 신규 저장할 구매요청 품목
	 */
	public void insertUpcrItem(Map upcrData, Map upcrItem) {
		if(upcrItem == null) {
			return;
		}

		upcrItem.put("upcr_item_uuid"     , UUID.randomUUID().toString());
		upcrItem.put("upcr_uuid"          , upcrData.get("upcr_uuid"));
		upcrItem.put("upcr_no"          , upcrData.get("upcr_no"));
		upcrItem.put("upcr_revno"         , upcrData.get("upcr_revno"));
		upcrItem.put("purc_typ_ccd"       , upcrData.get("purc_typ_ccd"));
		upcrItem.put("upcr_purp_ccd"   , upcrData.get("upcr_purp_ccd"));
		upcrItem.put("upcr_chg_yn", upcrData.get("upcr_chg_yn"));

		//단가계약 품목
		if(upcrItem.get("uprccntr_no") != null) {
			if("SPTPURC".equals(upcrData.get("upcr_purp_ccd"))) { //일반구매 요청
				upcrItem.put("upcr_purp_ccd", "UPRCCNTR_PURC"); //단가구매 요청
			} else if("UPRCCNTR_SGNG".equals(upcrData.get("upcr_purp_ccd"))) {	//단가계약 요청
				upcrItem.put("uprccntr_item_uuid" , null);
				upcrItem.put("uprccntr_lno", null);
				upcrItem.put("uprccntr_no"      , null);
				upcrItem.put("uprccntr_revno"     , null);
			}
		} else {
			upcrItem.put("cur_ccd", upcrData.get("cur_ccd"));
		}

		// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
		if(this.isNoCodeItem(upcrItem)) {
			upcrItem.put("item_nm", upcrItem.get("disp_item_nm"));
			upcrItem.put("item_nm_en", upcrItem.get("disp_item_nm"));
			//upcrItem.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
		}

		upcrItemRepository.insertUpcrItem(upcrItem);
	}

	/**
	 * 무코드 품목여부 판단
	 *
	 * @param upcrItem - 구매요청품목
	 * @return boolean
	 */
	private boolean isNoCodeItem(Map upcrItem) {
		boolean result = true;
		if(!Strings.isNullOrEmpty((String) upcrItem.get("item_cd"))) {
			result = false;
		} else if(!"CDLS".equals(upcrItem.get("item_cd_crn_typ_ccd"))) {
			result = false;
		}
		return result;
	}

	/**
	 * 다건의 구매요청 품목의 정보를 변경한다.
	 *
	 * @param upcrData - 구매요청 마스터
	 * @param upcrItems - 변경할 구매요청 품목 다건
	 */
	public void updateUpcrItem(Map upcrData, List upcrItems) {
		if(upcrData == null) {
			return;
		}
		if(upcrItems == null) {
			return;
		}
		if(upcrItems.isEmpty()) {
			return;
		}

		for(Map upcrItem : (List<Map>) upcrItems) {
			this.updateUpcrItem(upcrData, upcrItem);
		}
	}

	/**
	 * 구매요청 품목의 정보를 변경한다.
	 *
	 * @param upcrData - 구매요청 마스터
	 * @param upcrItem - 변경할 구매요청 품목
	 */
	private void updateUpcrItem(Map upcrData, Map upcrItem) {
		if(upcrData == null) {
			return;
		}
		if(upcrItem == null) {
			return;
		}

		upcrItem.put("upcr_uuid"   , upcrData.get("upcr_uuid"));
		upcrItem.put("upcr_no"   , upcrData.get("upcr_no"));
		upcrItem.put("purc_typ_ccd", upcrData.get("purc_typ_ccd"));

		//단가계약 품목
		if(upcrItem.get("uprccntr_no") != null) {
			if("SPTPURC".equals(upcrData.get("upcr_purp_ccd"))) { //일반구매 요청
				upcrItem.put("upcr_purp_ccd", "UPRCCNTR_PURC"); //단가구매 요청
			} else if("UPRCCNTR_SGNG".equals(upcrData.get("upcr_purp_ccd"))) {	//단가계약 요청
				upcrItem.put("uprccntr_item_uuid" , null);
				upcrItem.put("uprccntr_lno", null);
				upcrItem.put("uprccntr_no"      , null);
				upcrItem.put("uprccntr_revno"     , null);
			}
		} else {
			upcrItem.put("cur_ccd", upcrData.get("cur_ccd"));
		}

		upcrItemRepository.updateUpcrItem(upcrItem);
	}

	/**
	 * 구매요청 마스터 대상으로 구매요청 품목을 전부 삭제한다.
	 *
	 * @param upcrData - 삭제할 구매요청 마스터
	 */
	public void deleteUpcr(Map upcrData) {
		List<Map> upcrItems = upcrItemRepository.findUpcrItemByUpcr(upcrData);
		this.deleteUpcrItem(upcrItems);
	}

	/**
	 * 구매요청 품목을 조회한다.
	 *
	 * @param upcrData - 구매요청 마스터
	 * @return List - 구매요청 품목
	 */
	public List findUpcrItemByUpcr(Map upcrData) {
		return upcrItemRepository.findUpcrItemByUpcr(upcrData);
	}

	/**
	 * 구매요청 품목을 복사하여 기존 구매요청 ID,번호 등 삭제 후 반환한다.
	 *
	 * @param param - 구매요청 품목 조회하기 위한 파라미터
	 * @return
	 */
	public List copyUpcr(Map param) {
		List newUpcrItems = Lists.newArrayList();

		List upcrItems = upcrItemRepository.findUpcrItemByUpcr(param);
		if(upcrItems != null && !upcrItems.isEmpty()) {
			for(Map upcrItem : (List<Map>) upcrItems) {
				newUpcrItems.add(this.clearUpcrItem(upcrItem));
			}
		}

		return newUpcrItems;
	}

	/**
	 * 구매요청 품목에서 구매요청임을 결정하는 정보 제거
	 *
	 * @param upcrItem - 정보 제거할 구매요청 품목
	 * @return Map - 정보 제거된 구매요청 품목
	 */
	private Map clearUpcrItem(Map upcrItem) {
		Map userInfo = Auth.getCurrentUserInfo();

		upcrItem.put("upcr_uuid" , null);
		upcrItem.put("upcr_no" , null);
		upcrItem.put("upcr_revno", 1);
		if("CDLS".equals(upcrItem.get("item_cd_crn_typ_ccd"))) {
			upcrItem.put("item_cd", null);
		}
		upcrItem.put("upcr_realusr_id"      , userInfo.get("usr_id"));		// 구매요청의뢰자
		upcrItem.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);	// 변경요청진행상태 (T : 변경가능)
		return upcrItem;
	}

	/**
	 * 구매요청 변경 시 구매요청 품목의 상태 정합성 체크
	 *
	 * @param upcrItems
	 * @return
	 */
	public ResultMap validateSaveModUpcrItem(List upcrItems) {
		List upcrItemIds = Lists.newArrayList();

		for(Map upcrItem : (List<Map>) upcrItems) {
			if(upcrItem.get("upcr_item_uuid") != null) {
				upcrItemIds.add((String) upcrItem.get("upcr_item_uuid"));
			}
		}

		/***** PR Item 변경 가능여부 체크 *****/
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);		// 구매요청접수대기 (ZER-341 이슈에 따라 접수하지 않아도 다음 프로세스 진행가능)
		availableStsList.add(ProConst.PR_STS_CCD_RV);		// 구매요청접수
		availableStsList.add(ProConst.PR_STS_CCD_RD);		// 구매반려

		Map checkParam = Maps.newHashMap();
		checkParam.put("upcr_item_uuids", upcrItemIds);
		checkParam.put("availableStsList", availableStsList);

		return upcrValidator.validate(checkParam);
	}

	public List findListRfxItemDefaultDataByUpcrItemIds(List upcrItemIds) {
		return upcrItemRepository.findListRfxItemDefaultDataByUpcrItemIds(upcrItemIds);
	}

	public List findListRfiItemDefaultDataByUpcrItemIds(Map<String,Object> upcrItemIds) {
		return upcrItemRepository.findListRfiItemDefaultDataByUpcrItemIds(upcrItemIds);
	}

	public FloaterStream findListUpcrItemProg(Map param) {
		return upcrItemRepository.findListUpcrItemProg(param);
	}

	public List<Map> findListUpcrItem(Map upcrData) {
		return upcrItemRepository.findListUpcrItem(upcrData);
	}

	public FloaterStream findListUpcrAndUpcrItems(Map param) {
		return upcrItemRepository.findListUpcrAndUpcrItems(param);
	}

	public void updateUpcrItemPurcGrp(Map updateParam) {
		upcrItemRepository.updateUpcrItemPurcGrp(updateParam);
	}

	public List<Map> findListUpcrItemHistory(Map param) {
		return upcrItemRepository.findListUpcrItemHistory(param);
	}

	public List<Map> findListCompareUpcrItem(Map param) {
		return upcrItemRepository.findListCompareUpcrItem(param);
	}

	public List<Map> findPreviousUpcrItems(Map param) {
		return upcrItemRepository.findPreviousUpcrItems(param);
	}

	public void updateUpcrItemUpcrPurp(Map param) {
		upcrItemRepository.updateUpcrItemUpcrPurp(param);
	}

	public Map findUpcrByUpcrItemId(Map param) {
		return upcrItemRepository.findUpcrByUpcrItemId(param);
	}

    public List<Map<String, Object>> findListUpcrItemIdsByUpcrId(Map<String, Object> param) {
		return upcrItemRepository.findListUpcrItemIdsByUpcrId(param);
    }

    public List<Map<String,Object>> findListUpcrItemAutoPoY(Map upcrData) {
		return upcrItemRepository.findListUpcrItemAutoPoY(upcrData);
    }

    public List<Map<String, Object>> findListUpcrItemByUpcrItemUuids(Map<String, Object> param) {
		return upcrItemRepository.findListUpcrItemByUpcrItemUuids(param);
    }
}
