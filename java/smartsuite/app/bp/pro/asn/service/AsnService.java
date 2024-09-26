package smartsuite.app.bp.pro.asn.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.admin.printout.service.PrintoutService;
import smartsuite.app.bp.pro.asn.repository.AsnRepository;
import smartsuite.app.bp.pro.asn.validator.AsnValidator;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;

import java.util.*;

/**
 * 납품예정 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName AsnService.java
 * @package smartsuite.app.bp.pro.ar
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class AsnService {
	
	@Inject
	private AsnValidator asnValidator;
	
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private AsnRepository asnRepository;
	
	@Inject
	private AsnItemService asnItemService;
	
	@Inject
	private PoItemService poItemService;
	
	@Inject
	private PrintoutService printoutService;

	@Inject
	private DlvySchedService dlvySchedService;

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
		return asnRepository.searchAsn(param);
	}
	
	/**
	 * 검수요청의 상세품목 목록조회
	 *
	 * @param param the param
	 * @return the list
	 */
	public List<Map<String, Object>> searchAsnItemByAsnUuid(Map<String, Object> param) {
		return asnItemService.searchAsnItemByAsnUuid(param);
		
	}
	
	/**
	 * 납품예정 상세정보를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAsn(Map<String, Object> param) {
		Map<String, Object> asnData = asnRepository.findAsn(param);
		List<Map<String, Object>> asnItems = this.searchAsnItemByAsnUuid(param);

		asnData.put("items", asnItems);
		asnData.put("hiddenAsnSched", dlvySchedService.findListDlvySchedByPoItemUuids(dlvySchedService.makeParamForFindDlvySched(asnItems)).isEmpty());
		// arData.put("services", this.findListAsnServiceByAsnId(param));
		return asnData;
	}
	
	/**
	 * 납품예정 반려 처리한다.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap updateAsnReject(Map<String, Object> param) {
		// validate
		ResultMap resultMap = this.validateAsnData(param);
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			// 납품예정 반려 사유를 수정한다
			asnRepository.updateAsnRejectOpinion(param);
			
			// 발주품목의 납품예정수량 초기화
			poItemService.updateInitAsnQuantityOfPoItem(param);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("asn_uuid", param.get("asn_uuid"));
			proStatusProcessor.rejectRequestAsn(keyParam); // 반려
		}
		return resultMap;
	}
	
	/**
	 * 납품예정 정합성 확인
	 *
	 * @param targetAsnData
	 * @return
	 */
	public ResultMap validateAsnData(Map<String, Object> targetAsnData) {
		if(asnValidator.existsGoodsInspectionRequest(targetAsnData)) {
			Map<String, Object> comparedResult = asnRepository.compareAsnStatus(targetAsnData);
			return asnValidator.checkValidationResultOfAsnData(targetAsnData, comparedResult);
		}
		return ResultMap.getInstance();
		
	}
	
	/**
	 * 기성 반려사유를 수정한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updateAsnRejectOpn
	 */
	public void updateAsnRejectOpn(Map<String, Object> param) {
		asnRepository.updateAsnRejectOpn(param);
	}
	
	/**
	 * 기성요청 아이디로 선급금 여부 조회
	 *
	 * @param appId
	 * @return
	 */
	public String findAdvancePaymentYnByAsnUuid(String appId) {
		return asnRepository.findAdvancePaymentYnByAsnUuid(appId);
	}
	
	/**
	 * 기성요청 상세정보
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPaymentRequest(Map<String, Object> param) {
		return asnRepository.findProgressPaymentRequest(param);
	}
	
	/**
	 * 기성승인 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findProgressPaymentByAsnUuId(Map<String, Object> param) {
		return asnRepository.findProgressPaymentByAsnUuId(param);
	}
	
	/**
	 * 납품예정 아이디로 입고대상 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findGrByAsnUuid(Map<String, Object> param) {
		return asnRepository.findGrByAsnUuid(param);
	}
	
	public Map<String, Object> findPoEvalSetInfo(Map<String, Object> param) {
		return asnRepository.findPoEvalSetInfo(param);
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
    public ResultMap findInfoDocumentOutputAsn(Map<String, Object> param) {
    	ResultMap resultMap = ResultMap.getInstance();
    	Map<String, Object> parameter = this.documentOutputParameterSet(param);
        Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> documentOutputDataByAsnHeader = Lists.newArrayList();
		List<Map<String, Object>> documentOutputDataByAsnDetail = Lists.newArrayList();
		
		Map<String, Object> callDocumentOutputDataInfo = printoutService.findDocumentOutputManagement(parameter);			// 문서 출력물 데이터 조회
		Map<String, Object> paramGroupList = callDocumentOutputDataInfo.get("paramGroupList") == null? new HashMap<String, Object>() : (Map<String, Object>) callDocumentOutputDataInfo.get("paramGroupList");			// DATASET 조회
		
    	Map<String, Object> asnHeader = asnRepository.findInfoDocumentOutputAsnHeader(param);
    	List<Map<String, Object>> asnDetail = asnRepository.findListDocumentOutputAsnDetail(param);
    	
    	for(String documentGroupName : paramGroupList.keySet()) {
    		if(("asnHeader").equals(documentGroupName)) {
    			documentOutputDataByAsnHeader.add(asnHeader);
    		} else if(("asnDetail").equals(documentGroupName)) {
    			documentOutputDataByAsnDetail.addAll(asnDetail);
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
	public ResultMap findListDocumentOutputAsn(Map<String, Object> param) {
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
    		
	    	Map<String, Object> asnHeader = asnRepository.findInfoDocumentOutputAsnHeader(asnUuidInfo);
	    	List<Map<String, Object>> asnDetail = asnRepository.findListDocumentOutputAsnDetail(asnUuidInfo);
	    	
	    	for(String documentGroupName : paramGroupList.keySet()) {
	    		if(("asnHeader").equals(documentGroupName)) {
	    			documentOutputDataByAsnHeader.add(asnHeader);
	    		} else if(("asnDetail").equals(documentGroupName)) {
	    			documentOutputDataByAsnDetail.addAll(asnDetail);
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
	
	public void updateGrPicByAsnUuid(Map<String, Object> grData) {
		if(grData.get("asn_uuid") == null) {
			return;
		}
		asnRepository.updateGrPicByAsnUuid(grData);
		
	}
}
