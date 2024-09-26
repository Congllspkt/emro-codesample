package smartsuite.app.bp.cms.itemreg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.cms.cmsCommon.service.CmsCommonService;
import smartsuite.app.bp.cms.itemreq.service.ItemReqService;
import smartsuite.app.bp.itemMaster.itemcommon.service.ItemCommonService;
import smartsuite.app.bp.cms.itemreg.repository.ItemRegRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

/**
 * 품목 등록 관리 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemRegService {
	
	@Inject
	ItemRegRepository itemRegRepository;

	@Inject
	ItemReqService itemReqService;

	@Inject
	CmsCommonService cmsCommonService;

	@Inject
	ItemCommonService itemCommonService;

	@Inject
	SharedService sharedService;

	/**
	 * 품목 등록 관리 조회
	 *
	 * @param
	 * @return the FloaterStream
	 */
	public FloaterStream findListItemReg(Map<String, Object> param) {
		return itemRegRepository.findListItemReg(param);
	}

	/**
	 * 품목 등록 관리 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	public Map<String, Object> findInfoAllItemReg(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Map<String, Object>> asgnAttrList = Lists.newArrayList();

		String reqTyp = param.get("req_typ_ccd") == null ? "" : (String) param.get("req_typ_ccd");
		String reqSts = param.get("item_reg_req_sts_ccd") == null ? "" : (String) param.get("item_reg_req_sts_ccd");
		if(!Strings.isNullOrEmpty(reqTyp) && !Strings.isNullOrEmpty(reqSts)) {
			if(reqTyp.equals("CHG") && reqSts.equals("APVL_REQG")) {
				asgnAttrList = cmsCommonService.findListItemAsgnAttrByItemIattr(param);
			} else {
				asgnAttrList = itemReqService.findListItemAsgnAttrByItemIattrRegReq(param);
			}
		}

		resultMap.put("regInfo", this.findInfoItemReg(param));
		resultMap.put("oorgList", cmsCommonService.findListItemRegReqOorg(param));
		resultMap.put("asgnAttrList", cmsCommonService.addInfoUomList(asgnAttrList));
		return resultMap;
	}

	/**
	 * 품목 등록 관리 상세 조회
	 *
	 * @param
	 * @return the map
	 */
	public Map<String,Object> findInfoItemReg(Map<String, Object> param) {
		return itemRegRepository.findInfoItemReg(param);
	}

	/**
	 * 품목 등록 관리 저장
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap saveInfoItemReg(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();

		// 기본 정보
		Map<String, Object> regInfo = (Map<String, Object>) param.getOrDefault("regInfo", Maps.newHashMap());
		// 운영조직 정보
		List<Map<String, Object>> oorgList = (List<Map<String, Object>>) param.getOrDefault("oorgList", Lists.newArrayList());
		// 속성 정보
		List<Map<String, Object>> asgnAttrList = (List<Map<String, Object>>) param.getOrDefault("asgnAttrList", Lists.newArrayList());
		// 등록요청 번호
		Object itemRegReqNo = regInfo.get("item_reg_req_no");

		// 표준코드가 기존에 사용되지않은 신규인경우
		boolean isNew = false;

		//품목 코드가 존재 하고, 변경요청 내용 값이 있을경우 요청 유형은 변경(C)
		if (cmsCommonService.checkNullObject(regInfo.get("item_cd")) || cmsCommonService.checkNullObject(regInfo.get("item_chg_req_cont"))) {
			regInfo.put("req_typ_ccd", "NEW");
		} else {
			regInfo.put("req_typ_ccd", "CHG");
		}

		// 상태 체크 (결재 요청중, 승인 인 경우 수정할 수 없다)
		resultMap = this.checkExistedApvdAndRetWithResult(regInfo);
		if(resultMap.isFail()) {
			return ResultMap.FAIL(resultMap.getResultMessage());
		}

		if (cmsCommonService.checkStsApvd(regInfo) && cmsCommonService.checkReqTypNew(regInfo)) {
			// 품목등록 승인이면
			resultMap = this.setNewApprovedItem(regInfo);

			if (resultMap.isFail()) {
				return ResultMap.FAIL(resultMap.getResultMessage());
			} else {
				Map<String, Object> successMap = resultMap.getResultData();
				isNew = (Boolean) successMap.get("is_new");
				regInfo = (Map<String, Object>) successMap.getOrDefault("regInfo", Maps.newHashMap());
			}
		}

		// 결재 승인이면
		if (cmsCommonService.checkStsApvd(regInfo)) {
			// 변경이면
			if (cmsCommonService.checkReqTypChg(regInfo)) {
				this.saveModApprovedItem(regInfo, oorgList, asgnAttrList);
			} else {
				this.saveApprovedItem(regInfo, oorgList, asgnAttrList, isNew);
			}
		}

		//등록 요청 업데이트
		cmsCommonService.updateItemRegReqWithOorg(regInfo, oorgList);
		cmsCommonService.insertItemIattrRegReqAfterDelete(regInfo, asgnAttrList, itemRegReqNo);

		return ResultMap.SUCCESS(regInfo);
	}

	/**
	 * 품목 등록 관리 저장
	 * 새로 승인되는 품목 setting
	 *
	 * @param
	 * @return the resultmap
	 */
	public ResultMap setNewApprovedItem(Map<String, Object> regInfo) {
		ResultMap resultmap = ResultMap.getInstance();
		Map<String, Object> result = Maps.newHashMap();
		// 세션정보
		Map<String, Object>userInfo = Auth.getCurrentUserInfo();

		//표준품목마스터 정보 존재 여부 체크
		if(cmsCommonService.checkNullObject(regInfo.get("mdl_no"))){
			regInfo.put("mdl_no", "");
		}

		if(cmsCommonService.checkNullObject(regInfo.get("mfgr_nm"))){
			regInfo.put("mfgr_nm", "");
		}

		resultmap = itemCommonService.checkItemMasterDuplicateWithResult(regInfo);
		// 기존에 생성된 동일 품목이 존재 하면
		if(resultmap.isFail()){
			return ResultMap.FAIL(resultmap.getResultMessage());
		} else {
			result.put("is_new", true);
			regInfo.put("item_cd", resultmap.getResultData().get("item_cd") ); // 표준 품목 코드
			regInfo.put("apvr_id", userInfo.get("usr_id") );
		}


		Object itemCd = regInfo.get("item_cd");
		if(itemCd == null){
			if(cmsCommonService.checkNullObject(regInfo.get("req_item_cd"))){
				itemCd = sharedService.generateDocumentNumber("IC");
			}else{
				itemCd = regInfo.get("req_item_cd");
			}

			regInfo.put("item_cd", itemCd);
			regInfo.put("req_item_cd", itemCd);
		}

		result.put("regInfo", regInfo);
		return ResultMap.SUCCESS(result);
	}

	/**
	 * 품목 등록 관리 저장
	 * 변경 승인되는 품목 setting
	 *
	 * @param
	 * @return the map
	 */
	public void saveModApprovedItem(Map<String, Object> regInfo, List<Map<String, Object>> oorgList, List<Map<String, Object>> asgnAttrList) {
		//변경이력을 추출하여 changeList를 저장
		List<Map<String, Object>> itemChangeList = this.findListItemChangeHistory(regInfo);
		List<Map> changeList = new ArrayList<Map>();

		for(Map<String, Object> item : itemChangeList){
			// 조회된 row에 모든 value가 null이면 list에 null로 담겨있어서 null 체크를 해주어야 함
			if(item != null) {
				for(Map.Entry<String, Object> entry : item.entrySet() ){
					String key = entry.getKey();
					Object value = entry.getValue();

					if(value != null){
						Map row = new HashMap<String, Object>();
						row.put("item_cd", regInfo.get("item_cd") );
						row.put("chg_typ_ccd", key);
						row.put("mod_cont", value);

						changeList.add(row);
					}
				}
			}
		}

		for(Map row : changeList){
			itemCommonService.insertItemHistrecWithSeq(row);
		}

		//실제 품목 테이블에 반영
		regInfo.put("use_yn", "Y");
		itemCommonService.updateItemWithOorg(regInfo, oorgList);
		//품목 속성 정보를 모두 삭제 후 저장
		cmsCommonService.insertItemIattrAfterDelete(regInfo, asgnAttrList);
	}

	/**
	 * 품목 변경 이력 조회
	 *
	 * @param
	 * @return the map
	 */
	public List findListItemChangeHistory(Map<String, Object> param) {
		return itemRegRepository.findListItemChangeHistory(param);
	}

	/**
	 * 승인 된 품목 저장
	 *
	 * @param
	 * @return the map
	 */
	public void saveApprovedItem(Map<String, Object> regInfo, List<Map<String, Object>> oorgList, List<Map<String, Object>> asgnAttrList, Boolean isNew) {
		if(isNew){
			itemCommonService.insertItemWithOorg(regInfo, oorgList);
			cmsCommonService.insertItemIattrAfterDelete(regInfo, asgnAttrList);
		}
		//등록 요청 업데이트
		cmsCommonService.updateItemRegReq(regInfo);
	}

	public ResultMap checkExistedApvdAndRetWithResult(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();

		if(this.checkExistedApvdAndRet(param)){
			resultMap.setResultMessage("STD.E9400");
			return ResultMap.FAIL(resultMap.getResultMessage());
		}

		return ResultMap.SUCCESS();
	}

	public Boolean checkExistedApvdAndRet(Map<String, Object> param){
		return itemRegRepository.checkExistedApvdAndRet(param) > 0;
	}
}
