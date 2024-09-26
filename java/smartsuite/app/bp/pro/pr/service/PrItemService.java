package smartsuite.app.bp.pro.pr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.pr.repository.PrItemRepository;
import smartsuite.app.bp.pro.pr.validator.PrValidator;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PrItemService {
	
	@Inject
	PrItemRepository prItemRepository;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	PrValidator prValidator;

	/**
	 * 다건의 구매요청 품목을 삭제한다.
	 * 
	 * @param prItems - 삭제할 구매요청 품목 다건
	 */
	public void deletePrItem(List prItems) {
		if(prItems == null) {
			return;
		}
		if(prItems.isEmpty()) {
			return;
		}
		
		for(Map prItem : (List<Map>) prItems) {
			this.deletePrItem(prItem);
		}
	}
	
	/**
	 * 구매요청 품목을 삭제한다.
	 * 
	 * @param prItem - 삭제할 구매요청 품목
	 */
	public void deletePrItem(Map prItem) {
		if(prItem == null) {
			return;
		}
		
		prItemRepository.deletePrItem(prItem);
	}

	/**
	 * 다건의 구매요청 품목을 신규 저장한다.
	 * 
	 * @param prData - 구매요청 마스터
	 * @param prItems - 신규 저장할 구매요청 품목 다건
	 */
	public void insertPrItem(Map prData, List prItems) {
		if(prItems == null) {
			return;
		}
		if(prItems.isEmpty()) {
			return;
		}
		
		for(Map prItem : (List<Map>) prItems) {
			this.insertPrItem(prData, prItem);
		}
	}
	
	/**
	 * 구매요청 품목을 신규 저장한다.
	 * 
	 * @param prData - 구매요청 마스터
	 * @param prItem - 신규 저장할 구매요청 품목
	 */
	public void insertPrItem(Map prData, Map prItem) {
		if(prItem == null) {
			return;
		}
		
		prItem.put("pr_item_uuid"     , UUID.randomUUID().toString());
		prItem.put("pr_uuid"          , prData.get("pr_uuid"));
		prItem.put("pr_no"          , prData.get("pr_no"));
		prItem.put("pr_revno"         , prData.get("pr_revno"));
		prItem.put("purc_typ_ccd"       , prData.get("purc_typ_ccd"));
		prItem.put("pr_purp_ccd"   , prData.get("pr_purp_ccd"));
		prItem.put("pr_chg_yn", prData.get("pr_chg_yn"));
		
		//단가계약 품목
		if(prItem.get("uprccntr_no") != null) {
			if("SPTPURC".equals(prData.get("pr_purp_ccd"))) { //일반구매 요청
				prItem.put("pr_purp_ccd", "UPRCCNTR_PURC"); //단가구매 요청
			} else if("UPRCCNTR_SGNG".equals(prData.get("pr_purp_ccd"))) {	//단가계약 요청
				prItem.put("uprccntr_item_uuid" , null);
				prItem.put("uprccntr_lno", null);
				prItem.put("uprccntr_no"      , null);
				prItem.put("uprccntr_revno"     , null);
			}
		} else {
			prItem.put("cur_ccd", prData.get("cur_ccd"));
		}

		// 미등록 품목(무코드 품목)인 경우, 무코드품목번호 채번
		if(this.isNoCodeItem(prItem)) {
			prItem.put("item_nm", prItem.get("disp_item_nm"));
			prItem.put("item_nm_en", prItem.get("disp_item_nm"));
			//prItem.put("item_cd", sharedService.generateDocumentNumber("NOCDMT"));
		}
		
		prItemRepository.insertPrItem(prItem);
	}
	
	/**
	 * 무코드 품목여부 판단
	 * 
	 * @param prItem - 구매요청품목
	 * @return boolean
	 */
	private boolean isNoCodeItem(Map prItem) {
		boolean result = true;
		if(!Strings.isNullOrEmpty((String) prItem.get("item_cd"))) {
			result = false;
		} else if(!"CDLS".equals(prItem.get("item_cd_crn_typ_ccd"))) {
			result = false;
		}
		return result;
	}

	/**
	 * 다건의 구매요청 품목의 정보를 변경한다.
	 * 
	 * @param prData - 구매요청 마스터
	 * @param prItems - 변경할 구매요청 품목 다건
	 */
	public void updatePrItem(Map prData, List prItems) {
		if(prData == null) {
			return;
		}
		if(prItems == null) {
			return;
		}
		if(prItems.isEmpty()) {
			return;
		}
		
		for(Map prItem : (List<Map>) prItems) {
			this.updatePrItem(prData, prItem);
		}
	}

	/**
	 * 구매요청 품목의 정보를 변경한다.
	 * 
	 * @param prData - 구매요청 마스터
	 * @param prItem - 변경할 구매요청 품목
	 */
	private void updatePrItem(Map prData, Map prItem) {
		if(prData == null) {
			return;
		}
		if(prItem == null) {
			return;
		}
		
		prItem.put("pr_uuid"   , prData.get("pr_uuid"));
		prItem.put("pr_no"   , prData.get("pr_no"));
		prItem.put("purc_typ_ccd", prData.get("purc_typ_ccd"));
		
		//단가계약 품목
		if(prItem.get("uprccntr_no") != null) {
			if("SPTPURC".equals(prData.get("pr_purp_ccd"))) { //일반구매 요청
				prItem.put("pr_purp_ccd", "UPRCCNTR_PURC"); //단가구매 요청
			} else if("UPRCCNTR_SGNG".equals(prData.get("pr_purp_ccd"))) {	//단가계약 요청
				prItem.put("uprccntr_item_uuid" , null);
				prItem.put("uprccntr_lno", null);
				prItem.put("uprccntr_no"      , null);
				prItem.put("uprccntr_revno"     , null);
			}
		} else {
			prItem.put("cur_ccd", prData.get("cur_ccd"));
		}

		prItemRepository.updatePrItem(prItem);
	}

	/**
	 * 구매요청 마스터 대상으로 구매요청 품목을 전부 삭제한다.
	 * 
	 * @param prData - 삭제할 구매요청 마스터
	 */
	public void deletePr(Map prData) {
		List<Map> prItems = prItemRepository.findPrItemByPr(prData);
		this.deletePrItem(prItems);
	}

	/**
	 * 구매요청 품목을 조회한다.
	 * 
	 * @param prData - 구매요청 마스터
	 * @return List - 구매요청 품목
	 */
	public List findPrItemByPr(Map prData) {
		return prItemRepository.findPrItemByPr(prData);
	}
	
	public Map findPrItemByUuid(String prItemUuid) {
		Map param = Maps.newHashMap();
		param.put("pr_item_uuid", prItemUuid);
		return prItemRepository.findPrItemByUuid(param);
	}

	/**
	 * 구매요청 품목을 복사하여 기존 구매요청 ID,번호 등 삭제 후 반환한다.
	 * 
	 * @param param - 구매요청 품목 조회하기 위한 파라미터
	 * @return
	 */
	public List copyPr(Map param) {
		List newPrItems = Lists.newArrayList();
		
		List prItems = prItemRepository.findPrItemByPr(param);
		if(prItems != null && !prItems.isEmpty()) {
			for(Map prItem : (List<Map>) prItems) {
				newPrItems.add(this.clearPrItem(prItem));
			}
		}
		
		return newPrItems;
	}
	
	/**
	 * 구매요청 품목에서 구매요청임을 결정하는 정보 제거
	 * 
	 * @param prItem - 정보 제거할 구매요청 품목
	 * @return Map - 정보 제거된 구매요청 품목
	 */
	private Map clearPrItem(Map prItem) {
		Map userInfo = Auth.getCurrentUserInfo();
		
		prItem.put("pr_uuid" , null);
		prItem.put("pr_no" , null);
		prItem.put("pr_revno", 1);
		if("CDLS".equals(prItem.get("item_cd_crn_typ_ccd"))) {
			prItem.put("item_cd", null);
		}
		prItem.put("pr_realusr_id"      , userInfo.get("usr_id"));		// 구매요청의뢰자
		prItem.put("pr_chg_yn", ProConst.PR_CHG_YN_N);	// 변경요청진행상태 (T : 변경가능)
		return prItem;
	}

	/**
	 * 구매요청 변경 시 구매요청 품목의 상태 정합성 체크
	 * 
	 * @param prItems
	 * @return
	 */
	public ResultMap validateSaveModPrItem(List prItems) {
		List prItemIds = Lists.newArrayList();
		
		for(Map prItem : (List<Map>) prItems) {
			if(prItem.get("pr_item_uuid") != null) {
				prItemIds.add((String) prItem.get("pr_item_uuid"));
			}
		}

		/***** PR Item 변경 가능여부 체크 *****/
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);		// 구매요청접수대기 (ZER-341 이슈에 따라 접수하지 않아도 다음 프로세스 진행가능)
		availableStsList.add(ProConst.PR_STS_CCD_RV);		// 구매요청접수
		availableStsList.add(ProConst.PR_STS_CCD_RD);		// 구매반려

		Map checkParam = Maps.newHashMap();
		checkParam.put("pr_item_uuids", prItemIds);
		checkParam.put("availableStsList", availableStsList);

		return prValidator.validate(checkParam);
	}

	public FloaterStream findListPrItemProg(Map param) {
		return prItemRepository.findListPrItemProg(param);
	}

	public List<Map> findListPrItem(Map prData) {
		return prItemRepository.findListPrItem(prData);
	}

	public void updatePrItemPurcGrp(Map updateParam) {
		prItemRepository.updatePrItemPurcGrp(updateParam);
	}

	public List<Map> findListPrItemHistory(Map param) {
		return prItemRepository.findListPrItemHistory(param);
	}

	public List<Map> findListComparePrItem(Map param) {
		return prItemRepository.findListComparePrItem(param);
	}

	public List<Map> findPreviousPrItems(Map param) {
		return prItemRepository.findPreviousPrItems(param);
	}

	public void updatePrItemPrPurp(Map param) {
		prItemRepository.updatePrItemPrPurp(param);
	}

	public Map findPrByPrItemId(Map param) {
		return prItemRepository.findPrByPrItemId(param);
	}

    public List<Map<String, Object>> findListPrItemIdsByPrId(Map<String, Object> param) {
		return prItemRepository.findListPrItemIdsByPrId(param);
    }

    public List<Map<String,Object>> findListPrItemAutoPoY(Map prData) {
		return prItemRepository.findListPrItemAutoPoY(prData);
    }

	public void updatePrItemQtainfo(Map<String, Object> insertQta) {
		prItemRepository.updatePrItemQtaInfo(insertQta);
	}

	public List<Map<String, Object>> findListMultiBapCntAndQtaYnByPr(Map<String, Object> pr) {
		return prItemRepository.findListMultiBapCntAndQtaYnByPr(pr);
	}

	public List<Map<String, Object>> findListPrePrItemList(Map<String, Object> param) {
		return prItemRepository.findListPrePrItemList(param);
	}

	public List<Map<String,Object>> findListPrItemByPrItemUuids(Map<String, Object> param) {
		return prItemRepository.findListPrItemByPrItemUuids(param);
	}
}
