package smartsuite.app.sp.pro.po.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.printout.service.PrintoutService;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.sp.pro.po.repository.SpPoRepository;
import smartsuite.app.sp.pro.po.validator.SpPoValidator;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Po 관련 처리하는 서비스 Class입니다.
 *
 * @author JuEung Kim
 * @see
 * @FileName PoService.java
 * @package smartsuite.app.sp.pro.po
 * @Since 2016. 5. 27
 * @변경이력 : [2016. 5. 27] JuEung Kim 최초작성
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpPoService {

	/** The pro status processor. */
	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	SpPoValidator spPoValidator;


	@Inject
	SpPoRepository spPoRepository;

	@Inject
	SpPoItemService spPoItemService;

	@Inject
	SpPoPaymentExpectationService spPoPaymentExpectationService;
	
	@Inject
	PrintoutService printoutService;
	
	private static final String DOC_ID = "po_doc";
	private static final String PROJECT_NAME = "printout";
	private static final String FORM_NAME = "po_doc";

	/**
	 * 발주품목 목록을 조회한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2016. 5. 27
	 * @Method Name : findListPoItem
	 */
	public FloaterStream findListPoItem(Map<String, Object> param) {
		// 대용량 처리
		return spPoItemService.findListPoItem(param);
	}

	/**
	 * 발주 목록을 조회한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the list po
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPo
	 */
	public FloaterStream findListPo(Map<String, Object> param) {
		// 대용량 처리
		return spPoRepository.findListPo(param);
	}

	/**
	 * 발주 상세정보를 조회한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : findPo
	 */
	public Map<String, Object> findPo(Map<String, Object> param) {
		Map<String, Object> poData = spPoRepository.findPoHeader(param);
		poData.put("items", spPoItemService.findListPoItemByPoId(param));
		poData.put("paymentPlans", spPoPaymentExpectationService.findListPaymentPlanByPoId(param));
		return poData;
	}

	/**
	 * 발주(복수건)를 접수 처리한다.
	 *
	 * @author : JongKyu Kim
	 * @param param {"po_uuids"}
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : acceptPos
	 */
	public ResultMap acceptPos(Map<String, Object> param) {
		ResultMap resultMap = spPoValidator.validate(param);
		
		Map<String, Object> resultData = resultMap.getResultData();
		
		// 유효한 id 리스트
		if(resultData.get(ValidatorConst.VALID_IDS) != null && ((List<String>)resultData.get(ValidatorConst.VALID_IDS)).size() > 0) {
			Map<String, Object> acceptParam = new HashMap<String, Object>();
			acceptParam.put("po_uuids", resultData.get(ValidatorConst.VALID_IDS));
			
			proStatusProcessor.acceptPoByVendor(acceptParam); // 접수
		}
		return resultMap;
	}
	
	/**
	 * 발주(단건)를 접수 처리한다.
	 *
	 * @author : JongKyu Kim
	 * @param param {"po_uuid"}
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : acceptPo
	 */
	public ResultMap acceptPo(Map<String, Object> param) {
		ResultMap resultMap = spPoValidator.validate(param);
		
		if(resultMap.isSuccess()) {
			proStatusProcessor.acceptPoByVendor(param); // 접수
		}
		resultMap.setResultMessage("accept");
		return resultMap;
	}

	/**
	 * 발주(복수건)를 거부 처리한다.
	 *
	 * @author : JongKyu Kim
	 * @param param {"po_uuids"}
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectPos
	 */
	public ResultMap rejectPos(Map<String, Object> param) {
		ResultMap resultMap = spPoValidator.validate(param);
		
		Map<String, Object> resultData = resultMap.getResultData();
		
		// 유효한 id 리스트
		if(resultData.get(ValidatorConst.VALID_IDS) != null && ((List<String>)resultData.get(ValidatorConst.VALID_IDS)).size() > 0) {
			Map<String, Object> acceptParam = new HashMap<String, Object>();
			acceptParam.put("po_uuids", resultData.get(ValidatorConst.VALID_IDS));
			acceptParam.put("po_rcpt_rjct_rsn", param.get("po_rcpt_rjct_rsn"));
			acceptParam.put("po_rcpt_rjct_rsn_ccd", param.get("po_rcpt_rjct_rsn_ccd"));

			proStatusProcessor.rejectPoByVendor(acceptParam); // 거부
		}
		return resultMap;
	}
	
	/**
	 * 발주(단건)를 거부 처리한다.
	 *
	 * @author : JongKyu Kim
	 * @param param {"po_uuid"}
	 * @return map
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectPo
	 */
	public ResultMap rejectPo(Map<String, Object> param) {
		ResultMap resultMap = spPoValidator.validate(param);
		
		if(resultMap.isSuccess()) {
			proStatusProcessor.rejectPoByVendor(param); // 거부
		}
		return resultMap;
	}
	
		
	/**
	 * 초기 기성요청 헤더 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findInitProgressPaymentRequestByPoUuid(Map<String, Object> param) {
		return spPoRepository.findInitProgressPaymentRequestByPoUuid(param);
	}
	
	
	/**
	 * 기성요청 가능여부 확인
	 * @param param
	 * @return
	 */
	public Map<String, Object> validateProgressPaymentRequestStatus(Map<String, Object> param) {
		return spPoRepository.validateProgressPaymentRequestStatus(param);
	}
	
	/**
	 * 발주기준 기성현황 목록 조회한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2016. 6. 7
	 * @Method Name : searchPoForProgressPayment
	 */
	public FloaterStream searchPoForProgressPayment(Map<String, Object> param) {
		// 대용량 처리
		return spPoRepository.searchPoForProgressPayment(param);
	}
	
	/**
	 * 문서 출력 정보 조회를 위한 파라미터 셋팅
	 * @param param
	 * @return
	 */
    public Map<String,Object> documentOutputParameterSet(Map<String,Object> param){
        param.put("doc_id", DOC_ID);
        param.put("projectName", PROJECT_NAME);
        param.put("formName", FORM_NAME);
        return param;
    }
	
	public ResultMap findInfoDocumentOutputSpPo(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		List<Map<String, Object>> documentOutputDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> documentOutputDataByPoDetail = Lists.newArrayList();
		
		Map<String, Object> callDocumentOutputDataInfo = printoutService.findDocumentOutputManagement(parameter);			// 문서 출력 정보 조회
		Map<String, Object> paramGroupList = callDocumentOutputDataInfo.get("paramGroupList") == null? new HashMap<String, Object>() : (Map<String, Object>) callDocumentOutputDataInfo.get("paramGroupList");	
		
		Map<String, Object> spPoHeader = spPoRepository.findInfoDocumentOutputSpPoHeader(param);
		List<Map<String, Object>> spPoDetail = spPoRepository.findListDocumentOutputSpPoDetail(param);
		
		for(String documentGroupName : paramGroupList.keySet()) {
			if(("poHeader").equals(documentGroupName)) {
				documentOutputDataByPoHeader.add(spPoHeader);
			} else if(("poDetail").equals(documentGroupName)) {
				documentOutputDataByPoDetail.addAll(spPoDetail);
			}
		}
		
		// Converting JSON
		Gson gson = new Gson();
		String documentOutputDataInfoByPoHeader = gson.toJson(documentOutputDataByPoHeader);
		String documentOutputDataInfoByPoDetail = gson.toJson(documentOutputDataByPoDetail);
		
        Map<String, Object> documentOutputInfo = Maps.newHashMap();
        documentOutputInfo.put("poHeader", documentOutputDataInfoByPoHeader);
        documentOutputInfo.put("poDetail", documentOutputDataInfoByPoDetail);
		
		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);			// 유비폼 호출 파라미터 셋팅
		documentOutputDataInfo.put("datasetList", documentOutputInfo);
		documentOutputDataInfo.put("param", responseInfo.get("param"));

		resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	public ResultMap findListDocumentOutputSpPo(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		Map<String, Object> documentOutputDataInfo = Maps.newHashMap();
		Map<String, Object> parameter = this.documentOutputParameterSet(param);
		List<Map<String, Object>> documentOutputDataByPoHeader = Lists.newArrayList();
		List<Map<String, Object>> documentOutputDataByPoDetail = Lists.newArrayList();
		
		List<String> poUuidList = (List<String>) param.get("po_uuid");
		for (String poUuid : poUuidList) {
			Map<String, Object> poUuidInfo = Maps.newHashMap();
			poUuidInfo.put("po_uuid", poUuid);
			
			Map<String, Object> callDocumentOutputDataInfo = printoutService.findDocumentOutputManagement(parameter);			// 문서 출력 정보 조회
    		Map<String, Object> paramGroupList = callDocumentOutputDataInfo.get("paramGroupList") == null? new HashMap<String, Object>() : (Map<String, Object>) callDocumentOutputDataInfo.get("paramGroupList");	
    		
    		Map<String, Object> spPoHeader = spPoRepository.findInfoDocumentOutputSpPoHeader(poUuidInfo);
    		List<Map<String, Object>> spPoDetail = spPoRepository.findListDocumentOutputSpPoDetail(poUuidInfo);
    		
    		for(String documentGroupName : paramGroupList.keySet()) {
    			if(("poHeader").equals(documentGroupName)) {
    				documentOutputDataByPoHeader.add(spPoHeader);
    			} else if(("poDetail").equals(documentGroupName)) {
    				documentOutputDataByPoDetail.addAll(spPoDetail);
    			}
    		}
		}
		
		// Converting JSON
		Gson gson = new Gson();
		String documentOutputDataInfoByPoHeader = gson.toJson(documentOutputDataByPoHeader);
		String documentOutputDataInfoByPoDetail = gson.toJson(documentOutputDataByPoDetail);
		
        Map<String, Object> documentOutputInfo = Maps.newHashMap();
        documentOutputInfo.put("poHeader", documentOutputDataInfoByPoHeader);
        documentOutputInfo.put("poDetail", documentOutputDataInfoByPoDetail);
		
		Map<String, Object> responseInfo = printoutService.makeUBIFormParameter(parameter);			// 유비폼 호출 파라미터 셋팅
		documentOutputDataInfo.put("datasetList", documentOutputInfo);
		documentOutputDataInfo.put("param", responseInfo.get("param"));
		
		resultMap.setResultData(documentOutputDataInfo);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

    public String findRfxIdByPoId(Map<String, Object> param) {
		return spPoRepository.findRfxIdByPoId(param);
    }


	public ResultMap findPoItemByPoUuid(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		if(param.get("po_uuids") != null) {
			List<Map<String, Object>> ret =spPoItemService.findListPoItemByPoId(param);
			//			List<Map<String, Object>> ret = new ArrayList<>();
//			List<Object> poUuids = (List<Object>) param.get("po_uuids");
//			poUuids.stream().forEach(item -> {
//				Map<String, Object> poUuidInfo = Maps.newHashMap();
//				poUuidInfo.put("po_uuid", item);
//				ret.addAll(spPoItemService.findListPoItemByPoId(poUuidInfo));
//			});
			List<Object> poItemUuids = ret.stream().map(item -> item.get("po_item_uuid")).collect(Collectors.toList());
			Map<String, Object> res = Maps.newHashMap();
			res.put("po_item_uuids", poItemUuids);

			return ResultMap.builder().resultData(res).build();
		} else {
			return ResultMap.FAIL();
		}
	}
}