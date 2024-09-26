package smartsuite.app.bp.pro.upcr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.upcr.event.UpcrEventPublisher;
import smartsuite.app.bp.pro.upcr.repository.UpcrRepository;
import smartsuite.app.bp.pro.upcr.validator.UpcrValidator;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.AvailAbleStatus;
import smartsuite.app.common.status.service.UpcrStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * Pr 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see
 * @FileName PrService.java
 * @package smartsuite.app.bp.upcro
 * @Since 2016. 3. 8
 * @변경이력 : [2016. 3. 8] Yeon-u Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class UpcrService {

	@Inject
	UpcrRepository upcrRepository;

	@Inject
	SharedService sharedService;

	@Inject
	UpcrItemService upcrItemService;

	@Inject
	StdFileService stdFileService;

	@Inject
	MessageUtil messageUtil;

	@Inject
	private UpcrStatusService upcrStatusProcessor;

	@Inject
	UpcrValidator upcrValidator;

	@Inject
	UpcrEventPublisher upcrEventPublisher;

	/**
	 * 구매요청 목적 코드를 조회한다.
	 */
	public List getPrPurpCcd(String param) {
		return upcrRepository.getUpcrPurpCcd(param);
	}

	/**
	 * list upcr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 8
	 * @Method Name : findListPr
	 */
	public FloaterStream findListUpcr(Map param) {
		return upcrRepository.findListUpcr(param);
	}

	/**
	 * upcr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2016. 3. 10
	 * @Method Name : findUpcr
	 */
	public Map findUpcrData(Map param) {
		Map resultMap = Maps.newHashMap();

		Map upcrData = this.findUpcr(param);
		resultMap.put("upcrData", upcrData);
		resultMap.put("upcrItems", upcrItemService.findUpcrItemByUpcr(upcrData));
		return resultMap;
	}

	/**
	 * 구매요청 마스터 조회
	 * 구매요청의 마지막 차수도 포함하여 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map findUpcr(Map param) {
		Map upcrData =  upcrRepository.findUpcr(param);
		upcrData.put("last_upcr_rev", upcrRepository.findUpcrRevMax(upcrData));
		return upcrData;
	}

	public ResultMap saveUpcr(Map param) {
		Map upcrData = (Map) param.get("upcrData");
		List deleteUpcrItems = (List) param.get("deleteUpcrItems");
		List updateUpcrItems = (List) param.get("updateUpcrItems");
		List insertUpcrItems = (List) param.get("insertUpcrItems");

		ResultMap validator = upcrValidator.validate(upcrData);
		if(!validator.isSuccess()) {
			return validator;
		}

		if(Strings.isNullOrEmpty((String) upcrData.get("upcr_uuid"))) {
			// 신규 id생성
			upcrData.put("upcr_uuid", UUID.randomUUID().toString());
			// 구매요청 번호 채번
			if(!ProConst.PR_CHG_YN_Y.equals(upcrData.getOrDefault("upcr_chg_yn",""))){
				upcrData.put("upcr_no", sharedService.generateDocumentNumber("UPCR"));
			}
			// 구매요청 변경인 경우 차수 정보 존재
			if(upcrData.get("upcr_revno") == null) {
				upcrData.put("upcr_revno", 1);
			}
			// 구매요청 변경인 경우 변경요청진행상태 존재
			if(upcrData.get("upcr_chg_yn") == null) {
				upcrData.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);    // 변경요청진행상태 (T : 변경가능)
			}
			upcrData.put("upcr_chg_rsn", "");

			upcrRepository.insertUpcr(upcrData);
		} else {
			upcrRepository.updateUpcr(upcrData);
		}

		// PrItem 삭제
		upcrItemService.deleteUpcrItem(deleteUpcrItems);
		// PrItem 신규저장
		upcrItemService.insertUpcrItem(upcrData, insertUpcrItems);
		// PrItem 변경
		upcrItemService.updateUpcrItem(upcrData, updateUpcrItems);

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("upcr_uuid", upcrData.get("upcr_uuid"));
		resultDataMap.put("upcr_chg_yn", upcrData.get("upcr_chg_yn"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	public ResultMap saveDraftUpcr(Map param) {
		ResultMap resultMap = this.saveUpcr(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}

		Map resultData = resultMap.getResultData();
		upcrStatusProcessor.saveDraftUpcr(resultData);
		return resultMap;
	}

	/**
	 * list upcr 삭제한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the save param
	 * @return the map< string, object>
	 * @Date : 2016. 3. 8
	 * @Method Name : deleteListPr
	 */
	public ResultMap deleteListUpcr(Map param) {
		List invalidUpcrs = Lists.newArrayList();
		List notExistUpcrs = Lists.newArrayList();
		List deleteUpcrs = (List) param.get("deleteUpcrs");

		for(Map deleteUpcr : (List<Map>) deleteUpcrs) {
			Map<String,Object> deleteParam = Maps.newHashMap();
			deleteParam.put("deleteUpcr",deleteUpcr);
			ResultMap resultMap = this.deleteUpcr(deleteParam);

			if(Const.INVALID_STATUS_ERR.equals(resultMap.getResultStatus())) {
				invalidUpcrs.add(resultMap.getResultData());
			} else if(Const.NOT_EXIST.equals(resultMap.getResultStatus())) {
				notExistUpcrs.add(deleteUpcr);
			}
		}

		return ValidatorUtil.setupDataListForValidationDataList(deleteUpcrs, invalidUpcrs, notExistUpcrs);
	}

	/**
	 * 단건의 구매요청 삭제한다.<br><br>
	 * <b>Required:</b><br>
	 * param.deleteUpcr - 삭제할 구매요청 마스터
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteUpcr(Map param) {
		if(param.get("deleteUpcr") == null) {
			return ResultMap.NOT_EXISTS();
		}
		Map deleteUpcr = (Map) param.get("deleteUpcr");
		ResultMap validator = upcrValidator.validate(deleteUpcr);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		int upcrRevno = ConvertUtils.convertBigDecimal(deleteUpcr.get("upcr_revno")).intValue();
		if(upcrRevno > 1) {
			Map preParam = Maps.newHashMap();
			preParam.put("req_doc_typ_ccd", "UPCR");
			preParam.put("req_no", deleteUpcr.get("upcr_no"));
			preParam.put("req_revno", upcrRevno - 1);
			upcrEventPublisher.recoveryRequestReqInfoByChangeReq(preParam);
		}

		this.deleteUpcrProcess(deleteUpcr);
		return ResultMap.SUCCESS();
	}

	protected void deleteUpcrProcess(Map deleteUpcr) {
		upcrItemService.deleteUpcr(deleteUpcr);
		upcrRepository.deleteUpcr(deleteUpcr);
	}

	/**
	 * 구매요청 접수에서 사용할 구매요청 및 품목 정보 조회한다.
	 *
	 * @param param - 검색조건
	 * @return the list
	 */
	public FloaterStream findListUpcrAndUpcrItems(Map param) {
		return upcrItemService.findListUpcrAndUpcrItems(param);
	}

	/**
	 * 구매요청 접수
	 *
	 * @param param
	 * @return
	 */
	public ResultMap receiveUpcrs(Map param) {
		List<String> upcrItemIds = (List<String>) param.get("upcr_item_uuids");
		List<String> availableStsList = Lists.newArrayList(ProConst.PR_STS_CCD_RW);

		Map checkParam = Maps.newHashMap();
		checkParam.put("upcr_item_uuids", upcrItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = upcrValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validUpcrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validUpcrItemIds != null && validUpcrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("upcr_item_uuids", validUpcrItemIds);

			upcrStatusProcessor.receiveUpcr(updateParam);
		}
		return validator;
	}

	/**
	 * 구매요청 반송
	 *
	 * @param param
	 * @return
	 */
	public ResultMap returnUpcrs(Map param) {
		List<String> upcrItemIds = (List<String>) param.get("upcr_item_uuids");

		// 반송 가능한 진행상태
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);    // 구매요청 접수대기
		availableStsList.add(ProConst.PR_STS_CCD_RV);    // 구매요청 접수
		availableStsList.add(ProConst.PR_PROG_STS_IC);    // RFI 완료

		Map checkParam = Maps.newHashMap();
		checkParam.put("upcr_item_uuids", upcrItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = upcrValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validUpcrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validUpcrItemIds != null && validUpcrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("upcr_item_uuids" , validUpcrItemIds);
			updateParam.put("upcr_ret_rsn", param.get("upcr_ret_rsn"));    //반송사유

			upcrStatusProcessor.returnUpcr(updateParam);
		}
		return validator;
	}

	/**
	 * purc grp cd 수정한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 9
	 * @Method Name : updatePurcGrpCd
	 */
	public ResultMap updatePurcGrpCd(Map<String,Object> param){
		List<String> upcrItemIds = (List<String>)param.get("upcr_item_uuids");

		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);
		availableStsList.add(ProConst.PR_STS_CCD_RV);

		Map checkParam = Maps.newHashMap();
		checkParam.put("upcr_item_uuids", upcrItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = upcrValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validUpcrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validUpcrItemIds != null && validUpcrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("upcr_item_uuids", validUpcrItemIds);
			updateParam.put("purc_grp_cd", param.get("purc_grp_cd"));

			upcrItemService.updateUpcrItemPurcGrp(updateParam);
		}
		return validator;
	}

	/**
	 * 결재 없이 구매요청을 수행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.upcr_uuid - 구매요청 ID
	 *
	 * @param param
	 * @return the map
	 */
	public ResultMap directReqUpcr(Map param) {
		Map upcrData = this.findUpcr((Map) param.get("upcrData"));
		if(upcrData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		// RFX 요청
		this.requestRfx(upcrData);
		// upcr상태 업데이트
		upcrStatusProcessor.bypassApprovalUpcr(upcrData);

		int upcrRev = Integer.parseInt(String.valueOf(upcrData.get("upcr_revno")));

		// 이전차수 아이템정리
		if(upcrRev > 1) {
			this.updateLastRevUpcr((String) upcrData.get("upcr_uuid"));
		}
		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("upcr_uuid", upcrData.get("upcr_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 이전차수 PR 의 PR_PROG_STS를 수정한다.
	 *
	 * @param upcrId - 구매요청 ID
	 */
	public void updateLastRevUpcr(String upcrId){
		String prevUpcrId = upcrRepository.findPrevUpcrId(upcrId);

		if(!Strings.isNullOrEmpty(prevUpcrId)) {
			Map statusParam = Maps.newHashMap();
			statusParam.put("prev_upcr_id", prevUpcrId);
			statusParam.put("upcr_uuid"     , upcrId);

			upcrStatusProcessor.closePrevUpcrByModUpcr(statusParam);
		}
	}

	/**
	 * 구매변경 가능여부를 확인한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the map
	 * @Date : 2017. 4. 14
	 * @Method Name : checkModifiableUpcrItems
	 */
	public ResultMap checkModifiableUpcrItems(Map param) {
		Map upcrData = (Map)param.get("upcrData");

		List<Map> upcrItems = upcrItemService.findListUpcrItem(upcrData);
		List<String> upcrItemIds = ListUtils.getArrayValuesByList(upcrItems, "upcr_item_uuid");

		/***** PR Item 변경 가능여부 체크 시작 *****/
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);        // 구매요청접수대기
		availableStsList.add(ProConst.PR_STS_CCD_RV);        // 구매요청접수
		availableStsList.add(ProConst.PR_STS_CCD_RD);        // 구매반려

		Map checkParam = Maps.newHashMap();
		checkParam.put("upcr_item_uuids", upcrItemIds);
		checkParam.put("availableStsList", availableStsList);

		return upcrValidator.validate(checkParam);
	}

	/**
	 * 구매요청 변경 이력 조회
	 *
	 * @author : kh_lee
	 * @param param
	 * @return
	 * @Date : 2017. 5. 19
	 * @Method Name : findListUpcrHistory
	 */
	public ResultMap findListUpcrHistory(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("upcrList"    , upcrRepository.findListUpcrHistory(param));
		resultMap.put("upcrItemList", upcrItemService.findListUpcrItemHistory(param));
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 구매요청 변경 비교 조회
	 *
	 * @author : kh_lee
	 * @param param
	 * @return
	 * @Date : 2017. 5. 19
	 * @Method Name : findListUpcrHistory
	 */
	public Map findUpcrCompare(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("compareUpcrData" , upcrRepository.findCompareUpcrData(param));
		resultMap.put("compareUpcrItems", upcrItemService.findListCompareUpcrItem(param));

		return resultMap;
	}

	/**
	 * 결재서식에서 이전차수 구매요청정보 조회하는 함수
	 *
	 * @author : LMS
	 * @param param( upcr_no: 구매요청 번호 , upcr_revno: 현재차수-1한 값)
	 * @return
	 * @Date : 2017. 05. 29
	 * @Method Name : findUpcreviousUpcrInfo
	 */
	public Map findPreviousUpcrInfo(Map param) {
		return upcrRepository.findPreviousUpcrInfo(param);
	}

	/**
	 * 일반구매를 단가구매로 바꾸는 함수
	 *
	 * @author : Chaerin Yu
	 * @param param {cntrInfo}
	 * @return
	 * @Date : 2017. 07. 13
	 * @Method Name : updateNCtoCC
	 */
	public ResultMap updateNCtoCC(Map<String,Object> param){
		Map cntrInfo = (Map) param.get("cntrInfo");

		// validate
		ResultMap validator = upcrValidator.validate(cntrInfo);
		if(!validator.isSuccess()) {
			return validator;
		}

		upcrItemService.updateUpcrItemUpcrPurp(cntrInfo);
		List<Map> itemList = upcrItemService.findListUpcrItem(cntrInfo);
		BigDecimal upcrTotAmt = BigDecimal.ZERO;
		for(Map item : itemList) {
			// PR 헤더의 총 금액 업데이트
			upcrTotAmt = upcrTotAmt.add((BigDecimal) item.get("upcr_amt"));
		}
		cntrInfo.put("upcr_amt", upcrTotAmt);
		cntrInfo.put("upcr_purp_ccd", "UPRCCNTR_PURC"); //단가구매

		upcrRepository.updateUpcrTotAmt(cntrInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * APP ID로 PR HD 정보 가져오기 (workplace)
	 *
	 * @param param
	 * @return
	 */
	public Map findUpcrByUpcrItemId(Map param) {
		return upcrItemService.findUpcrByUpcrItemId(param);
	}

	/**
	 * 구매요청 변경을 위한 데이터 조회
	 *
	 * @param param
	 */
	public Map findUpcrByChangeUpcr(Map param) {
		Map resultMap = Maps.newHashMap();
		Map upcrData = this.findUpcr(param);
		if(upcrData == null) {
			return null;
		}

		upcrData.put("modify_yn", "Y");
		upcrData.put("upcr_revno", Integer.parseInt(upcrData.get("upcr_revno").toString()) + 1);
		// 첨부파일 복사
		upcrData.put("athg_uuid", stdFileService.copyFile((String) upcrData.get("athg_uuid")));

		resultMap.put("upcrData", upcrData);
		resultMap.put("upcrItems", upcrItemService.findUpcrItemByUpcr(upcrData));
		return resultMap;
	}

	/**
	 * 구매요청의 마지막 차수 조회
	 *
	 * @param param
	 * @return Integer - 마지막 차수
	 */
	public Map findUpcrRevMax(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("last_upcr_rev", upcrRepository.findUpcrRevMax(param));
		return resultMap;
	}

	/**
	 * 구매요청 전체 데이터 복사
	 *
	 * @param param
	 * @return
	 */
	public ResultMap copyUpcr(Map param) {
		Map upcrData = upcrRepository.findUpcr(param);
		if(upcrData == null) {
			return ResultMap.NOT_EXISTS();
		}

		String copyName = messageUtil.getCodeMessage("STD.N2101", upcrData.get("upcr_tit"), "복사본_{0}");

		upcrData.put("upcr_uuid", null);
		upcrData.put("upcr_no", null);
		upcrData.put("upcr_revno", 1);
		upcrData.put("upcr_tit", copyName);
		upcrData.put("upcr_req_dt", null);
		upcrData.put("upcr_chg_yn",ProConst.PR_CHG_YN_N);

		// 세션정보
		Map userInfo = Auth.getCurrentUserInfo();
		upcrData.put("upcr_crtr_id"  , userInfo.get("usr_id"));    // 구매요청자
		upcrData.put("upcr_crtr_dept_cd", userInfo.get("dept_cd"));    // 구매요청 부서

		// 첨부파일 복사
		upcrData.put("athg_uuid", stdFileService.copyFile((String) upcrData.get("athg_uuid")));

		param.put("upcrData", upcrData);
		param.put("insertUpcrItems", upcrItemService.copyUpcr(param));

		// 신규 구매요청 저장
		return this.saveDraftUpcr(param);
	}

	/**
	 * 구매요청 변경건을 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.upcrData - 변경될 구매요청 마스터<br>
	 * param.insertUpcrItems - 변경될 구매요청 품목
	 *
	 * @param param - 구매요청 변경을 위한 데이터
	 * @return the map - 구매요청 변경 결과
	 */

	public ResultMap saveModUpcr(Map param) {
		Map upcrData = (Map) param.get("upcrData");
		if(upcrData == null) {
			return ResultMap.NOT_EXISTS();
		}

		ResultMap validator = upcrValidator.validate(upcrData);
		if(!validator.isSuccess()) {
			return validator;
		}

		List<Map<String,Object>> insertUpcrItems = (List) param.get("insertUpcrItems");
		// 구매요청 품목의 정합성 체크 확인
		validator = upcrItemService.validateSaveModUpcrItem(insertUpcrItems);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map preParam = Maps.newHashMap();
		preParam.put("req_doc_typ_ccd", "UPCR");
		preParam.put("req_no", upcrData.get("upcr_no"));
		preParam.put("req_revno", ConvertUtils.convertBigDecimal(upcrData.get("upcr_revno")).subtract(BigDecimal.ONE).intValue());
		
		validator = upcrEventPublisher.validateDeleteRequestReqInfoByChangeReq(preParam);
		if(!validator.isSuccess()) {
			return ResultMap.builder()
					.resultStatus("UPCR_ITEM_STS_ERR")
					.build();
		}

		Map<String, Object> upcrInfo = upcrRepository.findUpcr(upcrData);

		//신규 id생성
		upcrData.put("upcr_uuid", UUID.randomUUID().toString());
		upcrData.put("upcr_chg_yn", ProConst.PR_CHG_YN_Y);    // 변경요청진행상태(R : 변경건)
		//차수 증가
		int upcrRevno = Integer.parseInt(upcrInfo.get("upcr_revno").toString()) + 1;
		upcrData.put("upcr_revno", upcrRevno);

		// Pr 추가
		upcrRepository.insertUpcr(upcrData);

		if(insertUpcrItems != null){
			upcrItemService.insertUpcrItem(upcrData,insertUpcrItems);
		}

		//resultData 셋팅
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("upcr_uuid", upcrData.get("upcr_uuid"));
		resultDataMap.put("upcr_chg_yn", upcrData.get("upcr_chg_yn"));

		upcrEventPublisher.deleteRequestReqInfoByChangeReq(preParam);
		upcrStatusProcessor.saveDraftUpcr(resultDataMap);

		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 구매요청 품목을 삭제한다.<br>
	 * <b>Required:</b><br>
	 * param.deletePrItems - 삭제할 구매요청 품목 다건
	 *
	 * @param param
	 * @return
	 */

	public ResultMap deleteUpcrItems(Map param) {
		upcrItemService.deleteUpcrItem((List) param.get("deleteUpcrItems"));
		return ResultMap.SUCCESS();
	}

	public List<Map<String, Object>> findListUpcrByUpcrItems(Map param) {
		return upcrRepository.findListUpcrByUpcrItems(param);
	}

	public ResultMap checkUpcrItemsForNextStep(Map param) {
		param.put("availableStsList", AvailAbleStatus.prCreateRfxPoStatusList());
		return upcrValidator.validate(param);
	}

	/**
	 * 목표재료비 데이터 조회 - 차후 Target Price로 이관 필요
	 * GATE_RFX Key
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListPriceGateByUpcr(Map param) {
		return upcrRepository.findListPriceGateByUpcr(param);
	}

	public List<Map<String, Object>> findListPriceGateItemsByUpcr(Map param) {
		return upcrRepository.findListPriceGateItemsByUpcr(param);
	}

	public void updatePriceGateByUpcr(Map param) {
		upcrRepository.updatePriceGateByUpcr(param);
	}
	
	public void requestRfx(Map param) {
		Map upcrInfo = upcrRepository.findUpcr(param);
		List<Map> upcrItemList = upcrItemService.findUpcrItemByUpcr(param);
		
		List<Map> reqList = Lists.newArrayList();
		for(Map upcrItem : upcrItemList) {
			Map reqItem = Maps.newHashMap(upcrItem);
			reqItem.put("req_tit", upcrInfo.get("upcr_tit"));
			reqItem.put("purc_typ_ccd", upcrItem.get("purc_typ_ccd"));
			reqItem.put("req_purp_ccd", upcrItem.get("upcr_purp_ccd"));
			reqItem.put("req_qty", upcrItem.get("upcr_qty"));
			reqItem.put("req_uprc", upcrItem.get("upcr_uprc"));
			reqItem.put("req_amt", upcrItem.get("upcr_amt"));
			reqItem.put("req_pic_id", upcrInfo.get("upcr_crtr_id"));
			reqItem.put("req_item_uuid", upcrItem.get("upcr_item_uuid"));
			reqItem.put("req_uuid", upcrItem.get("upcr_uuid"));
			reqItem.put("req_no", upcrItem.get("upcr_no"));
			reqItem.put("req_revno", upcrItem.get("upcr_revno"));
			reqItem.put("req_lno", upcrItem.get("upcr_lno"));
			reqList.add(reqItem);
		}
		upcrEventPublisher.requestRfx(reqList);
	}

	private ResultMap requestCopyPrToUpcr(Map<String, Object> upcrData){
		Map<String, Object> param = Maps.newHashMap();
		if(upcrData == null) {
			return ResultMap.NOT_EXISTS();
		}

		upcrData.put("upcr_uuid", null);
		upcrData.put("upcr_no", null);
		upcrData.put("upcr_revno", 1);
		upcrData.put("upcr_tit",  upcrData.get("pr_tit"));
		upcrData.put("upcr_req_dt", null);
		upcrData.put("upcr_chg_yn",ProConst.PR_CHG_YN_N);
		upcrData.put("upcr_purp_ccd","UPRCCNTR_SGNG"); //단가계약 체결용으로 변경

		// 세션정보
		Map userInfo = Auth.getCurrentUserInfo();
		upcrData.put("upcr_crtr_id"  , userInfo.get("usr_id"));    // 요청자
		upcrData.put("upcr_crtr_dept_cd", userInfo.get("dept_cd"));    // 요청 부서

		// 첨부파일 복사
		upcrData.put("athg_uuid", stdFileService.copyFile((String) upcrData.get("athg_uuid")));


		param.put("upcrData", upcrData);
		List<Map<String, Object>> insertItems = Lists.newArrayList();
		Map<String, Object> upcrItem = upcrData;
		upcrItem.put("upcr_uuid" , null);
		upcrItem.put("upcr_no" , null);
		upcrItem.put("upcr_revno", 1);
		if("CDLS".equals(upcrItem.get("item_cd_crn_typ_ccd"))) {
			upcrItem.put("item_cd", null);
		}
		upcrItem.put("upcr_realusr_id"      , upcrData.get("pr_realusr_id"));		// 구매요청의뢰자
		upcrItem.put("upcr_chg_yn", ProConst.PR_CHG_YN_N);	// 변경요청진행상태 (T : 변경가능)
		upcrItem.put("upcr_lno",10);
		upcrItem.put("upcr_qty",upcrData.get("pr_qty"));
		upcrItem.put("upcr_uprc",upcrData.get("pr_uprc"));
		upcrItem.put("upcr_amt",upcrData.get("pr_amt"));
		insertItems.add(upcrItem);
		param.put("insertUpcrItems", insertItems);

		// 신규 구매요청 저장
		return this.saveDraftUpcr(param);
	}
	public List<Map<String, Object>> requestCopyPrsToUpcrs(List<Map<String, Object>> prItems) {
		List<Map<String, Object>> requestList = Lists.newArrayList();
		for(Map<String, Object> item: prItems){
			ResultMap resultMap = this.requestCopyPrToUpcr(item);
			Map resultData = resultMap.getResultData();
			List upcrItems  =upcrItemService.findUpcrItemByUpcr(resultData);
			Map<String,Object> upcrData = (Map<String, Object>) upcrItems.get(0);
			// upcr상태 업데이트
			Map<String, Object> directReqUpcrParam = Maps.newHashMap();
			directReqUpcrParam.put("upcrData",upcrData);
			this.directReqUpcr(directReqUpcrParam);
			upcrData.put("pr_item_uuid",item.get("pr_item_uuid"));
			requestList.add(upcrData);
		}
		return requestList;
	}
}