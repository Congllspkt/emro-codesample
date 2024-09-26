package smartsuite.app.bp.pro.pr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.pr.event.PrEventPublisher;
import smartsuite.app.bp.pro.pr.repository.PrRepository;
import smartsuite.app.bp.pro.pr.validator.PrValidator;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.message.MessageUtil;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.AvailAbleStatus;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Pr 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see
 * @FileName PrService.java
 * @package smartsuite.app.bp.pro
 * @Since 2016. 3. 8
 * @변경이력 : [2016. 3. 8] Yeon-u Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PrService {

	@Inject
	PrRepository prRepository;

	@Inject
	SharedService sharedService;

	@Inject
	PrItemService prItemService;

	@Inject
	StdFileService stdFileService;

	@Inject
	MessageUtil messageUtil;

	@Inject
	private ProStatusService proStatusProcessor;

	@Inject
	PrValidator prValidator;

	@Inject
	PrEventPublisher prEventPublisher;

	/**
	 * 구매요청 목적 코드를 조회한다.
	 */
	public List getPrPurpCcd(String param) {
		return prRepository.getPrPurpCcd(param);
	}

	/**
	 * list pr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 8
	 * @Method Name : findListPr
	 */
	public FloaterStream findListPr(Map param) {
		return prRepository.findListPr(param);
	}

	/**
	 * pr 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2016. 3. 10
	 * @Method Name : findPr
	 */
	public Map findPrData(Map param) {
		Map resultMap = Maps.newHashMap();

		Map prData = this.findPr(param);
		resultMap.put("prData", prData);
		resultMap.put("prItems", prItemService.findPrItemByPr(prData));
		return resultMap;
	}

	/**
	 * 구매요청 마스터 조회
	 * 구매요청의 마지막 차수도 포함하여 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map findPr(Map param) {
		Map prData =  prRepository.findPr(param);
		prData.put("last_pr_rev", prRepository.findPrRevMax(prData));
		return prData;
	}

	public ResultMap savePr(Map param) {
		Map prData = (Map) param.get("prData");
		List deletePrItems = (List) param.get("deletePrItems");
		List updatePrItems = (List) param.get("updatePrItems");
		List insertPrItems = (List) param.get("insertPrItems");

		ResultMap validator = prValidator.validate(prData);
		if(!validator.isSuccess()) {
			return validator;
		}

		if(Strings.isNullOrEmpty((String) prData.get("pr_uuid"))) {
			// 신규 id생성
			prData.put("pr_uuid", UUID.randomUUID().toString());
			// 구매요청 번호 채번
			if(!ProConst.PR_CHG_YN_Y.equals(prData.getOrDefault("pr_chg_yn",""))){
				prData.put("pr_no", sharedService.generateDocumentNumber("PR"));
			}
			// 구매요청 변경인 경우 차수 정보 존재
			if(prData.get("pr_revno") == null) {
				prData.put("pr_revno", 1);
			}
			// 구매요청 변경인 경우 변경요청진행상태 존재
			if(prData.get("pr_chg_yn") == null) {
				prData.put("pr_chg_yn", ProConst.PR_CHG_YN_N);    // 변경요청진행상태 (T : 변경가능)
			}
			prData.put("pr_chg_rsn", "");

			prRepository.insertPr(prData);
		} else {
			prRepository.updatePr(prData);
		}

		// PrItem 삭제
		prItemService.deletePrItem(deletePrItems);
		// PrItem 신규저장
		prItemService.insertPrItem(prData, insertPrItems);
		// PrItem 변경
		prItemService.updatePrItem(prData, updatePrItems);

		//resultData 셋팅
		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("pr_uuid", prData.get("pr_uuid"));
		resultDataMap.put("pr_chg_yn", prData.get("pr_chg_yn"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	public ResultMap saveDraftPr(Map param) {
		ResultMap resultMap = this.savePr(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}

		Map resultData = resultMap.getResultData();
		proStatusProcessor.saveDraftPr(resultData);
		return resultMap;
	}

	/**
	 * list pr 삭제한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the save param
	 * @return the map< string, object>
	 * @Date : 2016. 3. 8
	 * @Method Name : deleteListPr
	 */
	public ResultMap deleteListPr(Map param) {
		List invalidPrs = Lists.newArrayList();
		List notExistPrs = Lists.newArrayList();
		List deletePrs = (List) param.get("deletePrs");

		for(Map deletePr : (List<Map>) deletePrs) {
			Map<String,Object> deleteParam = Maps.newHashMap();
			deleteParam.put("deletePr",deletePr);
			ResultMap resultMap = this.deletePr(deleteParam);

			if(Const.INVALID_STATUS_ERR.equals(resultMap.getResultStatus())) {
				invalidPrs.add(resultMap.getResultData());
			} else if(Const.NOT_EXIST.equals(resultMap.getResultStatus())) {
				notExistPrs.add(deletePr);
			}
		}

		return ValidatorUtil.setupDataListForValidationDataList(deletePrs, invalidPrs, notExistPrs);
	}

	/**
	 * 단건의 구매요청 삭제한다.<br><br>
	 * <b>Required:</b><br>
	 * param.deletePr - 삭제할 구매요청 마스터
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deletePr(Map param) {
		if(param.get("deletePr") == null) {
			return ResultMap.NOT_EXISTS();
		}
		Map deletePr = (Map) param.get("deletePr");
		ResultMap validator = prValidator.validate(deletePr);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		int prRevno = ConvertUtils.convertBigDecimal(deletePr.get("pr_revno")).intValue();
		if(prRevno > 1) {
			Map preParam = Maps.newHashMap();
			preParam.put("req_doc_typ_ccd", "PR");
			preParam.put("req_no", deletePr.get("pr_no"));
			preParam.put("req_revno", prRevno - 1);
			prEventPublisher.recoveryRequestReqInfoByChangeReq(preParam);
		}

		this.deletePrProcess(deletePr);
		return ResultMap.SUCCESS();
	}
	
	protected void deletePrProcess(Map deletePr) {
		prItemService.deletePr(deletePr);
		prRepository.deletePr(deletePr);
	}

	/**
	 * 구매요청 접수
	 *
	 * @param param
	 * @return
	 */
	public ResultMap receivePrs(Map param) {
		List<String> prItemIds = (List<String>) param.get("pr_item_uuids");
		List<String> availableStsList = Lists.newArrayList(ProConst.PR_STS_CCD_RW);

		Map checkParam = Maps.newHashMap();
		checkParam.put("pr_item_uuids", prItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = prValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validPrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validPrItemIds != null && validPrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("pr_item_uuids", validPrItemIds);

			proStatusProcessor.receivePr(updateParam);
		}
		return validator;
	}

	/**
	 * 구매요청 반송
	 *
	 * @param param
	 * @return
	 */
	public ResultMap returnPrs(Map param) {
		List<String> prItemIds = (List<String>) param.get("pr_item_uuids");

		// 반송 가능한 진행상태
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);    // 구매요청 접수대기
		availableStsList.add(ProConst.PR_STS_CCD_RV);    // 구매요청 접수
		availableStsList.add(ProConst.PR_PROG_STS_IC);    // RFI 완료

		Map checkParam = Maps.newHashMap();
		checkParam.put("pr_item_uuids", prItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = prValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validPrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validPrItemIds != null && validPrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("pr_item_uuids" , validPrItemIds);
			updateParam.put("pr_ret_rsn", param.get("pr_ret_rsn"));    //반송사유

			proStatusProcessor.returnPr(updateParam);
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
		List<String> prItemIds = (List<String>)param.get("pr_item_uuids");

		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);
		availableStsList.add(ProConst.PR_STS_CCD_RV);

		Map checkParam = Maps.newHashMap();
		checkParam.put("pr_item_uuids", prItemIds);
		checkParam.put("availableStsList", availableStsList);

		// validate 수행
		ResultMap validator = prValidator.validate(checkParam);
		Map resultData = validator.getResultData();

		List<String> validPrItemIds = (List<String>) resultData.get(ValidatorConst.VALID_IDS);
		// 유효한 id 리스트
		if(validPrItemIds != null && validPrItemIds.size() > 0) {
			Map updateParam = Maps.newHashMap();
			updateParam.put("pr_item_uuids", validPrItemIds);
			updateParam.put("purc_grp_cd", param.get("purc_grp_cd"));

			prItemService.updatePrItemPurcGrp(updateParam);
		}
		return validator;
	}

	/**
	 * 결재 없이 구매요청을 수행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.pr_uuid - 구매요청 ID
	 *
	 * @param param
	 * @return the map
	 */
	public ResultMap directReqPr(Map param) {
		Map prData = this.findPr((Map) param.get("prData"));
		if(prData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		// pr상태 업데이트
		proStatusProcessor.bypassApprovalPr(prData);

		int prRev = Integer.parseInt(String.valueOf(prData.get("pr_revno")));

		// 이전차수 아이템정리
		if(prRev > 1) {
			this.updateLastRevPr((String) prData.get("pr_uuid"));
		}
		
		// RFX 요청
		this.requestRfx(prData);

		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("pr_uuid", prData.get("pr_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}

	/**
	 * 이전차수 PR 의 PR_PROG_STS를 수정한다.
	 *
	 * @param prId - 구매요청 ID
	 */
	public void updateLastRevPr(String prId){
		String prevPrId = prRepository.findPrevPrId(prId);

		if(!Strings.isNullOrEmpty(prevPrId)) {
			Map statusParam = Maps.newHashMap();
			statusParam.put("prev_pr_id", prevPrId);
			statusParam.put("pr_uuid"     , prId);

			proStatusProcessor.closePrevPrByModPr(statusParam);
		}
	}

	/**
	 * 내담당구매그룹 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListMyPurcGrpCd
	 */
	public List findListMyPurcGrpCd(Map param) {
		return prRepository.findListMyPurcGrpCd(param);
	}

	/**
	 * 구매진행현황을 조회한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListPrItemProg
	 */
	public FloaterStream findListPrItemProg(Map param) {
		return prItemService.findListPrItemProg(param);
	}

	/**
	 * 구매변경 가능여부를 확인한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the map
	 * @Date : 2017. 4. 14
	 * @Method Name : checkModifiablePrItems
	 */
	public ResultMap checkModifiablePrItems(Map param) {
		Map prData = (Map)param.get("prData");

		List<Map> prItems = prItemService.findListPrItem(prData);
		List<String> prItemIds = ListUtils.getArrayValuesByList(prItems, "pr_item_uuid");

		/***** PR Item 변경 가능여부 체크 시작 *****/
		List<String> availableStsList = Lists.newArrayList();
		availableStsList.add(ProConst.PR_STS_CCD_RW);        // 구매요청접수대기
		availableStsList.add(ProConst.PR_STS_CCD_RV);        // 구매요청접수
		availableStsList.add(ProConst.PR_STS_CCD_RD);        // 구매반려

		Map checkParam = Maps.newHashMap();
		checkParam.put("pr_item_uuids", prItemIds);
		checkParam.put("availableStsList", availableStsList);

		return prValidator.validate(checkParam);
	}

	/**
	 * 구매요청 변경 이력 조회
	 *
	 * @author : kh_lee
	 * @param param
	 * @return
	 * @Date : 2017. 5. 19
	 * @Method Name : findListPrHistory
	 */
	public ResultMap findListPrHistory(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("prList"    , prRepository.findListPrHistory(param));
		resultMap.put("prItemList", prItemService.findListPrItemHistory(param));
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 구매요청 변경 비교 조회
	 *
	 * @author : kh_lee
	 * @param param
	 * @return
	 * @Date : 2017. 5. 19
	 * @Method Name : findListPrHistory
	 */
	public Map findPrCompare(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("comparePrData" , prRepository.findComparePrData(param));
		resultMap.put("comparePrItems", prItemService.findListComparePrItem(param));

		return resultMap;
	}

	/**
	 * 결재서식에서 이전차수 구매요청정보 조회하는 함수
	 *
	 * @author : LMS
	 * @param param( pr_no: 구매요청 번호 , pr_revno: 현재차수-1한 값)
	 * @return
	 * @Date : 2017. 05. 29
	 * @Method Name : findPreviousPrInfo
	 */
	public Map findPreviousPrInfo(Map param) {
		return prRepository.findPreviousPrInfo(param);
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
		ResultMap validator = prValidator.validate(cntrInfo);
		if(!validator.isSuccess()) {
			return validator;
		}

		prItemService.updatePrItemPrPurp(cntrInfo);
		List<Map> itemList = prItemService.findListPrItem(cntrInfo);
		BigDecimal prTotAmt = BigDecimal.ZERO;
		for(Map item : itemList) {
			// PR 헤더의 총 금액 업데이트
			prTotAmt = prTotAmt.add((BigDecimal) item.get("pr_amt"));
		}
		cntrInfo.put("pr_amt", prTotAmt);
		cntrInfo.put("pr_purp_ccd", "UPRCCNTR_PURC"); //단가구매

		prRepository.updatePrTotAmt(cntrInfo);
		return ResultMap.SUCCESS();
	}

	public ResultMap changePrBpa(Map<String,Object> param){
		Map cntrInfo = (Map) param.get("cntrInfo");

		// validate
		ResultMap validator = prValidator.validate(cntrInfo);
		if(!validator.isSuccess()) {
			return validator;
		}

		prItemService.updatePrItemPrPurp(cntrInfo);
		List<Map> itemList = prItemService.findListPrItem(cntrInfo);
		BigDecimal prTotAmt = BigDecimal.ZERO;
		for(Map item : itemList) {
			// PR 헤더의 총 금액 업데이트
			prTotAmt = prTotAmt.add((BigDecimal) item.get("pr_amt"));
		}
		cntrInfo.put("pr_amt", prTotAmt);
		cntrInfo.put("pr_purp_ccd", "UPRCCNTR_PURC"); //단가구매

		prRepository.updatePrTotAmt(cntrInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * 구매요청접수 화면에서 RFx/PO 등 다음 step으로 진행하려는 경우 validation check를 수행한다.
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkPrItemsForNextStep(Map param) {
		param.put("availableStsList", AvailAbleStatus.prCreateRfxPoStatusList());
		return prValidator.validate(param);
	}

	/**
	 * APP ID로 PR HD 정보 가져오기 (workplace)
	 *
	 * @param param
	 * @return
	 */
	public Map findPrByPrItemId(Map param) {
		return prItemService.findPrByPrItemId(param);
	}

	/**
	 * 구매요청 변경을 위한 데이터 조회
	 *
	 * @param param
	 */
	public Map findPrByChangePr(Map param) {
		Map resultMap = Maps.newHashMap();
		Map prData = this.findPr(param);
		if(prData == null) {
			return null;
		}

		prData.put("modify_yn", "Y");
		prData.put("pr_revno", Integer.parseInt(prData.get("pr_revno").toString()) + 1);
		// 첨부파일 복사
		prData.put("athg_uuid", stdFileService.copyFile((String) prData.get("athg_uuid")));

		resultMap.put("prData", prData);
		resultMap.put("prItems", prItemService.findPrItemByPr(prData));
		return resultMap;
	}

	/**
	 * 구매요청의 마지막 차수 조회
	 *
	 * @param param
	 * @return Integer - 마지막 차수
	 */
	public Map findPrRevMax(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("last_pr_rev", prRepository.findPrRevMax(param));
		return resultMap;
	}

	/**
	 * 구매요청 전체 데이터 복사
	 *
	 * @param param
	 * @return
	 */
	public ResultMap copyPr(Map param) {
		Map prData = prRepository.findPr(param);
		if(prData == null) {
			return ResultMap.NOT_EXISTS();
		}

		String copyName = messageUtil.getCodeMessage("STD.N2101", prData.get("pr_tit"), "복사본_{0}");

		prData.put("pr_uuid", null);
		prData.put("pr_no", null);
		prData.put("pr_revno", 1);
		prData.put("pr_tit", copyName);
		prData.put("pr_req_dt", null);
		prData.put("pr_chg_yn",ProConst.PR_CHG_YN_N);

		// 세션정보
		Map userInfo = Auth.getCurrentUserInfo();
		prData.put("pr_crtr_id"  , userInfo.get("usr_id"));    // 구매요청자
		prData.put("pr_crtr_dept_cd", userInfo.get("dept_cd"));    // 구매요청 부서

		// 첨부파일 복사
		prData.put("athg_uuid", stdFileService.copyFile((String) prData.get("athg_uuid")));

		param.put("prData", prData);
		param.put("insertPrItems", prItemService.copyPr(param));

		// 신규 구매요청 저장
		return this.saveDraftPr(param);
	}

	/**
	 * 구매요청 변경건을 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.prData - 변경될 구매요청 마스터<br>
	 * param.insertPrItems - 변경될 구매요청 품목
	 *
	 * @param param - 구매요청 변경을 위한 데이터
	 * @return the map - 구매요청 변경 결과
	 */

	public ResultMap saveModPr(Map param) {
		Map prData = (Map) param.get("prData");
		if(prData == null) {
			return ResultMap.NOT_EXISTS();
		}

		ResultMap validator = prValidator.validate(prData);
		if(!validator.isSuccess()) {
			return validator;
		}

		List<Map<String,Object>> insertPrItems = (List) param.get("insertPrItems");
		// 구매요청 품목의 정합성 체크 확인
		validator = prItemService.validateSaveModPrItem(insertPrItems);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map preParam = Maps.newHashMap();
		preParam.put("req_doc_typ_ccd", "PR");
		preParam.put("req_no", prData.get("pr_no"));
		preParam.put("req_revno", ConvertUtils.convertBigDecimal(prData.get("pr_revno")).subtract(BigDecimal.ONE).intValue());
		
		validator = prEventPublisher.validateDeleteRequestReqInfoByChangeReq(preParam);
		if(!validator.isSuccess()) {
			return ResultMap.builder()
					.resultStatus("PR_ITEM_STS_ERR")
					.build();
		}

		Map<String, Object> prInfo = prRepository.findPr(prData);

		//신규 id생성
		prData.put("pr_uuid", UUID.randomUUID().toString());
		prData.put("pr_chg_yn", ProConst.PR_CHG_YN_Y);    // 변경요청진행상태(R : 변경건)
		//차수 증가
		int prRevno = Integer.parseInt(prInfo.get("pr_revno").toString()) + 1;
		prData.put("pr_revno", prRevno);

		// Pr 추가
		prRepository.insertPr(prData);

		if(insertPrItems != null){
			prItemService.insertPrItem(prData,insertPrItems);
		}

		//resultData 셋팅
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("pr_uuid", prData.get("pr_uuid"));
		resultDataMap.put("pr_chg_yn", prData.get("pr_chg_yn"));

		prEventPublisher.deleteRequestReqInfoByChangeReq(preParam);
		proStatusProcessor.saveDraftPr(resultDataMap);

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

	public ResultMap deletePrItems(Map param) {
		prItemService.deletePrItem((List) param.get("deletePrItems"));
		return ResultMap.SUCCESS();
	}

	public List<Map<String, Object>> findListPrByPrItems(Map param) {
		return prRepository.findListPrByPrItems(param);
	}
	
	public void requestRfx(Map param) {
		Map prInfo = prRepository.findPr(param);
		List<Map> prItemList = prItemService.findPrItemByPr(param);
		
		List<Map> reqList = Lists.newArrayList();
		for(Map prItem : prItemList) {
			Map reqItem = Maps.newHashMap(prItem);
			reqItem.put("req_tit", prInfo.get("pr_tit"));
			reqItem.put("purc_typ_ccd", prItem.get("purc_typ_ccd"));
			reqItem.put("req_purp_ccd", prItem.get("pr_purp_ccd"));
			reqItem.put("req_qty", prItem.get("pr_qty"));
			reqItem.put("req_uprc", prItem.get("pr_uprc"));
			reqItem.put("req_amt", prItem.get("pr_amt"));
			reqItem.put("req_pic_id", prInfo.get("pr_crtr_id"));
			reqItem.put("req_item_uuid", prItem.get("pr_item_uuid"));
			reqItem.put("req_uuid", prItem.get("pr_uuid"));
			reqItem.put("req_no", prItem.get("pr_no"));
			reqItem.put("req_revno", prItem.get("pr_revno"));
			reqItem.put("req_lno", prItem.get("pr_lno"));
			reqList.add(reqItem);
		}
		prEventPublisher.requestRfx(reqList);
	}

	// 데모 시연용 임시 조치
	public Map findPrItemUuid(Map param) {
		Map prItemInfo = prRepository.findPrItemUuid(param);
		if(prItemInfo == null) {
			Map prInfo = findPr(param);
			return prInfo;
		} else {
			Map sendParam = Maps.newHashMap();
			sendParam.put("pr_uuid", prItemInfo.get("pr_uuid"));
			Map prInfo = this.findPr(sendParam);
			return prInfo;
		}
	}
}