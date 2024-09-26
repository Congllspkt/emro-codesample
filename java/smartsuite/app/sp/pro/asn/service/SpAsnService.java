package smartsuite.app.sp.pro.asn.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.admin.printout.service.PrintoutService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.sp.pro.asn.repository.SpAsnRepository;
import smartsuite.app.sp.pro.asn.validator.SpAsnValidator;
import smartsuite.app.sp.pro.po.service.SpPoItemService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Asn 관련 처리하는 서비스 Class입니다.
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpAsnService {

    @Inject
    private SpAsnValidator spAsnValidator;
    @Inject
    private SpAsnRepository spAsnRepository;
    @Inject
    private SpAsnItemService spAsnItemService;
    /**
     * The shared service.
     */
    @Inject
    private SharedService sharedService;
    /**
     * The pro status processor.
     */
    @Inject
    private ProStatusService proStatusProcessor;
	
	@Inject
	private SpPoItemService spPoItemService;
	
	@Inject
	PrintoutService printoutService;

    @Inject
    MailService mailService;
	
	private static final String DOC_ID = "dlvy_doc";
	private static final String PROJECT_NAME = "printout";
	private static final String FORM_NAME = "dlvy_doc";

    /**
     * 납품예정 목록 조회
     *
     * @param param
     * @return
     */
    public FloaterStream searchAsn(Map<String, Object> param) {
        // 대용량 처리
        return spAsnRepository.searchAsn(param);
    }

    /**
     * 납품예정 데이터 생성
     *
     * @param param
     */
    public void insertAsnData(Map<String, Object> param) {
        spAsnRepository.insertAsnData(param);
    }

    /**
     * 납품예정 데이터 수정
     *
     * @param param
     */
    public void updateAsnData(Map<String, Object> param) {
        spAsnRepository.updateAsnData(param);
    }

    /**
     * 납품예정 데이터 삭제상태로 변경
     *
     * @param param
     */
    public void updateDeleteAsn(Map<String, Object> param) {
        spAsnRepository.updateDeleteAsn(param);
    }

    /**
     * 납품예정 품목 생성
     *
     * @param param
     */
    public void insertAsnItem(Map<String, Object> param) {
        spAsnItemService.insertAsnItem(param);
    }


    /**
     * 납품예정 품목 수정
     *
     * @param param
     */
    public void updateAsnItem(Map<String, Object> param) {
        spAsnItemService.updateAsnItem(param);
    }

    /**
     * 납품예정 품목상세를 헤더 아이디로 삭제한다.
     *
     * @param param
     */
    public void updateDeleteAsnItemByAsn(Map<String, Object> param) {
        spAsnItemService.updateDeleteAsnItemByAsn(param);
    }

    /**
     * 납품예정 생성가능한지 검증
     *
     * @param validationData {po_item_uuids}
     * @return
     */
    public ResultMap validateAdvancedShippingNoticeCreatable(Map<String, Object> validationData) {
        // 납품예정 헤더 검증
        ResultMap resultMap = validateAsnData(validationData);
        // 발주품목 검증
        if (isSuccess(resultMap) && hasPoItem(validationData)) {
            resultMap = checkAsnPossiblePoItems(validationData);
            resultMap = validateQuantityOfPoItem(validationData, resultMap);
        }

        return resultMap;
    }

    /**
     * resultMap이 성공된 상태인지 확인
     *
     * @param resultMap
     * @return
     */
    private boolean isSuccess(ResultMap resultMap) {
        return ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus());
    }

    /**
     * 발주품목의 수량 검증
     *
     * @param validationData
     * @return
     */
    private ResultMap validateQuantityOfPoItem(Map<String, Object> validationData, ResultMap resultMap) {
        if(isSuccess(resultMap) && validationData.get("checkItems") != null){
            List<Map<String, Object>> quantityOfPoItems = spAsnItemService.checkQuantityOfPoItem(validationData);
            return spAsnValidator.checkPossibleAsnQuantityOfPoItems(validationData, quantityOfPoItems);
        }
        return resultMap;
    }

    /**
     * 발주품목이 검수요청이 가능한 상태인지 확인
     *
     * @param targetAsnData
     * @return
     */
    private ResultMap checkAsnPossiblePoItems(Map<String, Object> targetAsnData) {
        List<Map<String, Object>> invalidPoItems = spPoItemService.checkAsnCreatableOfPoItem(targetAsnData);
        return spAsnValidator.getResultMapOfInvalidPoItems(invalidPoItems);
    }

    /**
     * 검수요청이 유효하고 po item을 가지고 있는지
     *
     * @param targetAsnData
     * @return
     */
    private boolean hasPoItem(Map<String, Object> targetAsnData) {
        return spAsnValidator.hasPoItem(targetAsnData);
    }

    /**
     * 납품예정 정합성 확인
     *
     * @param validationData
     * @return
     */
    private ResultMap validateAsnData(Map<String, Object> validationData) {
        if(spAsnValidator.existsAdvancedShippingNotice(validationData)){
            Map<String, Object> comparedResult = spAsnRepository.compareAsnStatus(validationData);
            return spAsnValidator.checkValidationResultOfAsnData(validationData, comparedResult);
        }
        return ResultMap.SUCCESS();
    }

    /**
     * 납품예정 삭제
     *
     * @param param
     * @return
     */
    public ResultMap deleteAsn(Map<String, Object> param) {
        ResultMap resultMap = this.validateAdvancedShippingNoticeCreatable(param);
	    if(!resultMap.isSuccess()) {
			return resultMap;
	    }
		this.deleteAsnProcess(param);
        return resultMap;
    }
	
	protected void deleteAsnProcess(Map<String, Object> param) {
		this.updateDeleteAsnItemByAsn(param);
		this.updateDeleteAsn(param);
	}

    /**
     * PO ITEM으로 신규 납품예정 데이터 조회
     *
     * @param param
     * @return
     */
    public ResultMap findInitAsnByPoItem(Map<String, Object> param) {
        List<Map<String, Object>> asnItems = spAsnItemService.searchAsnItemByPoItem(param);
        setAsnItemLineNumber(asnItems);

        Map<String, Object> firstAsnItem = asnItems.get(0);
        Map<String, Object> asnInfo = Maps.newHashMap();
        asnInfo.put("oorg_cd", firstAsnItem.get("oorg_cd"));
        asnInfo.put("asn_sts_ccd", "CRNG");
        asnInfo.put("vd_cd", firstAsnItem.get("vd_cd"));
        asnInfo.put("disp_vd_nm", firstAsnItem.get("disp_vd_nm"));
        asnInfo.put("asn_typ_ccd", "QTY");
        asnInfo.put("purc_grp_cd", firstAsnItem.get("purc_grp_cd"));
        asnInfo.put("purc_typ_ccd", firstAsnItem.get("purc_typ_ccd"));
        asnInfo.put("cur_ccd", firstAsnItem.get("cur_ccd"));
        asnInfo.put("pymtmeth_ccd", firstAsnItem.get("pymtmeth_ccd"));
        asnInfo.put("pymtmeth_expln", firstAsnItem.get("pymtmeth_expln"));
        asnInfo.put("dlvymeth_ccd", firstAsnItem.get("dlvymeth_ccd"));
        asnInfo.put("dlvymeth_expln", firstAsnItem.get("dlvymeth_expln"));
        asnInfo.put("gr_pic_id", firstAsnItem.get("gr_pic_id"));
        asnInfo.put("gr_pic_nm", firstAsnItem.get("gr_pic_nm"));
         asnInfo.put("po_uuid", firstAsnItem.get("po_uuid"));
        asnInfo.put("has_no_cd_item", hasNoCodeItem(asnItems) ? "Y" : "N");

        return ResultMap.builder().resultData(this.makeAsnInfoResultData(asnInfo, asnItems)).build();
    }

    /**
     * 납품예정 품목 리스트에 무코드가 존재하는지
     *
     * @param asnItems
     * @return
     */
    private boolean hasNoCodeItem(List<Map<String, Object>> asnItems) {
        for (Map<String, Object> asnItem : asnItems) {
            if ("CDLS".equals(asnItem.get("item_cd_crn_typ_ccd"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 검수 품목 항번 설정
     *
     * @param asnItems
     */
    private void setAsnItemLineNumber(List<Map<String, Object>> asnItems) {
        int maxNo = 10;
        for (Map<String, Object> asnItem : asnItems) {
            asnItem.put("asn_lno", maxNo);
            maxNo += 10;
        }
    }

    /**
     * 납품예정 데이터와 품목 데이터를 조회한다.
     *
     * @param param
     * @return
     */
    public ResultMap findAsn(Map param) {
        return ResultMap.builder().resultData(this.makeAsnInfoResultData(findAsnData(param), searchAsnItem(param))).build();
    }

    /**
     * 납품예정 조회결과데이터 만듬.
     *
     * @param asnData
     * @param asnItems
     * @return
     */
    private Map<String, Object> makeAsnInfoResultData(Map<String, Object> asnData, List<Map<String, Object>> asnItems) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("asnHdInfo", asnData);
        resultMap.put("asnDtList", asnItems);
        return resultMap;
    }

    /**
     * 납품예정 품목 리스트 조회
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> searchAsnItem(Map param) {
        return spAsnItemService.searchAsnItem(param);
    }

    /**
     * 납품예정 조회
     *
     * @param param
     * @return
     */
    public Map<String, Object> findAsnData(Map param) {
        return spAsnRepository.findAsnData(param);
    }

    /**
     * 납품예정 임시 저장한다.
     *
     * @param param
     * @return
     */
    public ResultMap saveDraftAsn(Map<String, Object> param) {
        // 데이터 저장
        ResultMap resultMap = this.saveAsn(param);

        if (isSuccess(resultMap)) {
            // 납품예정 진행상태 update
            Map<String, Object> asnData = Maps.newHashMap();
            asnData.put("asn_uuid", resultMap.getResultData().get("asnUuid"));

            proStatusProcessor.saveDraftAsn(asnData);
        }
        return resultMap;
    }

    /**
     * 납품예정 제출
     *
     * @param param
     * @return
     */
    public ResultMap submitAsn(Map<String, Object> param) {

        Map<String, Object> userInfo = Auth.getCurrentUserInfo();
        // 데이터 저장
        ResultMap resultMap = this.saveAsn(param);
        if (isSuccess(resultMap)) {
            Map<String, Object> asnItem = Maps.newHashMap();
            asnItem.put("asn_uuid", resultMap.getResultData().get("asnUuid"));
            asnItem.put("po_item_uuids", resultMap.getResultData().get("po_item_uuids"));

            // 납품예정 진행상태 update
            proStatusProcessor.requestAsn(asnItem);

            // 발주 품목의 납품예정 수량 update
            this.updateAsnQtyOfPoItems(asnItem);

        }
        return resultMap;
    }

    /**
     * 납품예정 제출 -> 납품예정 수량 업데이트
     *
     * @param asnItem
     * @return
     */
    public void updateAsnQtyOfPoItems(Map<String, Object> asnItem) {
        // 발주 품목의 납품예정 수량 update
        List<Map<String, Object>> poItems = spAsnItemService.searchAsnQuantityOfPoItem(asnItem);
        if (!poItems.isEmpty()) {
            for (Map<String, Object> poItem : poItems) {
                spPoItemService.updateAsnQuantityOfPoItem(poItem);
            }
        }
    }

    /**
     * 검수요청을 저장한다.
     *
     * @param param
     * @return
     */
    public ResultMap saveAsn(Map<String, Object> param) {
        // 세션정보
        Map<String, Object> userInfo = Auth.getCurrentUserInfo();

        // 헤더 data
        Map<String, Object> asnData = (Map<String, Object>) param.get("asnHdInfo");

        // 디테일 data
        List<Map<String, Object>> insertItems = (List<Map<String, Object>>) param.get("insertItems");
        List<Map<String, Object>> updateItems = (List<Map<String, Object>>) param.get("updateItems");
        List<Map<String, Object>> removeItems = (List<Map<String, Object>>) param.get("removeItems");
        List<Map<String, Object>> checkItems = (List<Map<String, Object>>) param.get("checkItems");    //모든 품목

        List<String> poItemIds = Lists.newArrayList();
        for (Map<String, Object> row : checkItems) {
            poItemIds.add(row.get("po_item_uuid").toString());
        }

        // 발주 상태 체크 (발주종료 / 발주변경 진행중 -> 납품예정 불가)
        Map<String, Object> checkParam = Maps.newHashMap(asnData);
        checkParam.put("po_item_uuids", poItemIds);
        checkParam.put("checkItems", checkItems);

        // validate
        ResultMap resultMap = this.validateAdvancedShippingNoticeCreatable(checkParam);

        if (isSuccess(resultMap)) {
            // 헤더 INSERT or UPDATE
            if (Strings.isNullOrEmpty((String) asnData.get("asn_uuid"))) {
                this.createAsnData(asnData, userInfo);
            } else {
                this.updateAsnData(asnData);
            }

            // 품목 cud
            insertAsnItems(insertItems, asnData);

            updateAsnItems(updateItems);

            deleteAsnItems(removeItems);

            Map<String, Object> savedAsnData = Maps.newHashMap();
            savedAsnData.put("asnUuid", asnData.get("asn_uuid"));
            savedAsnData.put("po_item_uuids", poItemIds);
            resultMap.setResultData(savedAsnData);
        }
        return resultMap;
    }

    /**
     * 납품예정 데이터 생성
     *
     * @param asnData
     * @param userInfo
     */
    private void createAsnData(Map<String, Object> asnData, Map<String, Object> userInfo) {
        asnData.put("asn_uuid", UUID.randomUUID().toString());
        String asnNo = (String) asnData.get("asn_no"); // 발주 문서번호
        int asnRevno = asnData.get("asn_ordn") == null ? 1 : Integer.parseInt(asnData.get("asn_ordn").toString()); // 검수 차수

        // 문서번호 및 차수 생성
        if (Strings.isNullOrEmpty(asnNo)) {
            asnNo = sharedService.generateDocumentNumber("AR");
            asnRevno = 1;
        }
        asnData.put("asn_no", asnNo);                    // 검수요청번호
        asnData.put("asn_ordn", asnRevno);                    // 차수
        asnData.put("vd_cd", userInfo.get("vd_cd"));    // 협력사 코드

        Date nowDate = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        asnData.put("asn_dt", transFormat.format(nowDate));    // 납품예정 일자
        asnData.put("asn_cre_date", transFormat.format(nowDate));    // 납품예정 생성일자

        this.insertAsnData(asnData);
    }

    /**
     * 납품예정 품목 일괄 생성
     *
     * @param insertItems
     * @param arData
     */
    private void insertAsnItems(List<Map<String, Object>> insertItems, Map<String, Object> asnData) {
        // 디테일 INSERT
        if (insertItems != null && !insertItems.isEmpty()) {
            for (Map<String, Object> row : insertItems) {
                row.put("asn_item_uuid", UUID.randomUUID().toString());    // 납품예정 품목 아이디
                row.put("asn_uuid", asnData.get("asn_uuid"));
                row.put("asn_no", asnData.get("asn_no"));                            // 납품예정 번호
                row.put("asn_ordn", asnData.get("asn_ordn"));                            // 납품예정 회차
                row.put("oorg_cd", asnData.get("oorg_cd"));
                row.put("vd_cd", asnData.get("vd_cd"));            // 협력사 코드

                this.insertAsnItem(row);
            }
        }
    }

    /**
     * 납품예정 품목 일괄 수정
     *
     * @param updateItems
     */
    private void updateAsnItems(List<Map<String, Object>> updateItems) {
        // 디테일 UPDATE
        if (updateItems != null && !updateItems.isEmpty()) {
            for (Map<String, Object> row : updateItems) {
                this.updateAsnItem(row);
            }
        }
    }

    /**
     * 납품예정 품목 일괄 삭제
     *
     * @param removeItems
     */
    private void deleteAsnItems(List<Map<String, Object>> removeItems) {
        if (removeItems != null && !removeItems.isEmpty()) {
            for (Map<String, Object> removeItem : removeItems) {
                spAsnItemService.deleteAsnItem(removeItem);
            }
        }
    }
	
	/**
	 * 발주아이디에 따른 기성차수의 값을 반환한다.
	 *
	 * @param param the param
	 * @return new ar rev
	 */
	public Integer getNewAsnRev(Map<String, Object> param){
		return spAsnRepository.getNewAsnRev(param);
	}
	
	/**
	 * 기성요청 아이디로 선급금여부를 조회한다.
	 * @param taskUuid
	 * @return
	 */
	public String findAdvancePaymentYnByAsnUuid(String taskUuid) {
		return spAsnRepository.findAdvancePaymentYnByAsnUuid(taskUuid);
	}
	
	/**
     * 기성요청 헤더상태
     * @param param
     * @return
     */
	public Map<String, Object> getProgressPaymentRequestStatus(Map<String, Object> param) {
		return spAsnRepository.getProgressPaymentRequestStatus(param);
	}
	
	/**
     * 기성헤더 조회
     * @param param
     * @return
     */
    public Map<String, Object> findProgressPaymentRequest(Map<String, Object> param) {
	    return spAsnRepository.findProgressPaymentRequest(param);
    }
    
    /**
	 * 출력물 조회를 위한 파라미터 셋팅
	 * @param param
	 * @return
	 */
    public Map<String,Object> documentOutputParameterSet(Map<String,Object> param){
        param.put("doc_id", DOC_ID);
        param.put("projectName", PROJECT_NAME);
        param.put("formName", FORM_NAME);
        return param;
    }
    
    /**
     * 문서 출력을 위한 asn정보(단건) 조회 
     *
     * @param param
     * @return
     */
	public ResultMap findInfoDocumentOutputSpAsn(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
    	Map<String, Object> parameter = this.documentOutputParameterSet(param);
        Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> documentOutputDataByAsnHeader = Lists.newArrayList();
		List<Map<String, Object>> documentOutputDataByAsnDetail = Lists.newArrayList();
		
		Map<String, Object> callDocumentOutputDataInfo = printoutService.findDocumentOutputManagement(parameter);			// 문서 출력물 데이터 조회
		Map<String, Object> paramGroupList = callDocumentOutputDataInfo.get("paramGroupList") == null? new HashMap<String, Object>() : (Map<String, Object>) callDocumentOutputDataInfo.get("paramGroupList");			// DATASET 조회
		
    	Map<String, Object> spAsnHeader = spAsnRepository.findInfoDocumentOutputSpAsnHeader(param);
    	List<Map<String, Object>> spAsnDetail = spAsnRepository.findListDocumentOutputSpAsnDetail(param);
    	
    	for(String documentGroupName : paramGroupList.keySet()) {
    		if(("asnHeader").equals(documentGroupName)) {
    			documentOutputDataByAsnHeader.add(spAsnHeader);
    		} else if(("asnDetail").equals(documentGroupName)) {
    			documentOutputDataByAsnDetail.addAll(spAsnDetail);
    		}
    	}
    	
    	// Converting JSON
		Gson gson = new Gson();
        String documentOutputDataInfoByAsnHeader = gson.toJson(documentOutputDataByAsnHeader);
        String documentOutputDataInfoByAsnDetail = gson.toJson(documentOutputDataByAsnDetail);
        
        Map<String, Object> documentOutputInfo = Maps.newHashMap();
        documentOutputInfo.put("asnHeader", documentOutputDataInfoByAsnHeader);
        documentOutputInfo.put("asnDetail", documentOutputDataInfoByAsnDetail);
        
        Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);			// 유비폼 호출 파라미터 셋팅
        documentOutputDataInfo.put("datasetList", documentOutputInfo);
        documentOutputDataInfo.put("param", responseInfo.get("param"));
		
        resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}
	
	/**
     * 문서 출력을 위한 asn정보(복수건) 조회
     * 
     * @param param
     * @return
     */
	public ResultMap findListDocumentOutputSpAsn(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> documentOutputDataByAsnHeader = Lists.newArrayList();
		List<Map<String, Object>> documentOutputDataByAsnDetail = Lists.newArrayList();
		
		List<String> asnUuidList = (List<String>) param.get("asn_uuid");
        for (String asnUuid : asnUuidList) {
        	Map<String,Object> asnUuidInfo = Maps.newHashMap();
        	asnUuidInfo.put("asn_uuid", asnUuid);
	    	
            Map<String, Object> callDocumentOutputDataInfo = printoutService.findDocumentOutputManagement(parameter);
    		Map<String, Object> paramGroupList = callDocumentOutputDataInfo.get("paramGroupList") == null? new HashMap<String, Object>() : (Map<String, Object>) callDocumentOutputDataInfo.get("paramGroupList");
    		
	    	Map<String, Object> spAsnHeader = spAsnRepository.findInfoDocumentOutputSpAsnHeader(asnUuidInfo);
	    	List<Map<String, Object>> spAsnDetail = spAsnRepository.findListDocumentOutputSpAsnDetail(asnUuidInfo);
	    	
	    	for(String documentGroupName : paramGroupList.keySet()) {
	    		if(("asnHeader").equals(documentGroupName)) {
	    			documentOutputDataByAsnHeader.add(spAsnHeader);
	    		} else if(("asnDetail").equals(documentGroupName)) {
	    			documentOutputDataByAsnDetail.addAll(spAsnDetail);
	    		}
	    	}
        }
		
        // Converting JSON
		Gson gson = new Gson();
        String documentOutputDataInfoByAsnHeader = gson.toJson(documentOutputDataByAsnHeader);
        String documentOutputDataInfoByAsnDetail = gson.toJson(documentOutputDataByAsnDetail);
        
        Map<String, Object> documentOutputInfo = Maps.newHashMap();
        documentOutputInfo.put("asnHeader", documentOutputDataInfoByAsnHeader);
        documentOutputInfo.put("asnDetail", documentOutputDataInfoByAsnDetail);
        
        Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);
        
        documentOutputDataInfo.put("datasetList", documentOutputInfo);
        documentOutputDataInfo.put("param", responseInfo.get("param"));
		
        resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}
	
	/**
	 * 요청한 대상의 지급 유형 공통코드 상태값을 수정한다.
	 * @param asnHdInfo
	 */
	public void updateAsnPaymentTypeCommonCode(Map<String, Object> updateTargetHeader, String paymentType) {
		updateTargetHeader.put("pymt_typ_ccd", paymentType);
		spAsnRepository.updateAsnPaymentTypeCommonCode(updateTargetHeader);
	}
	
	public String getAsnPaymentTypeCommonCode(Map<String, Object> asnHdInfo) {
		Map asnInfo = spAsnRepository.getProgressPaymentTypeRelatedInfo(asnHdInfo);
		String paymentType = "";
		BigDecimal poAmt = new BigDecimal(String.valueOf(asnInfo.get("po_amt")));
		BigDecimal asnAmt = new BigDecimal(String.valueOf(asnInfo.get("asn_amt")));
		BigDecimal ttlGrAmt = new BigDecimal(String.valueOf(asnInfo.get("ttl_gr_amt")));
		
		if("Y".equals(asnInfo.get("exists_apymt")) && "N".equals(asnInfo.get("exists_apymt_cmpld")) && "1".equals(String.valueOf(asnInfo.get("asn_ordn")))) {
			paymentType = "APYMT";
		}else if(poAmt.subtract(ttlGrAmt.add(asnAmt)).compareTo(BigDecimal.ZERO) == 0) {
			paymentType = "BAL";
		} else {
			paymentType = "IPYMT";
		}
		
		return paymentType;
	}
}
