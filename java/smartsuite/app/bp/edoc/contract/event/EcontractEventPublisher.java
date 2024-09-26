package smartsuite.app.bp.edoc.contract.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class EcontractEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;


	/**
	 * 블록체인 계약 정보 전송
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap sendCntrTxData(Map param) {
		Map eventParam = Maps.newHashMap();
		
		eventParam.put("ecntr_no", param.get("ecntr_no")); //계약번호
		eventParam.put("ecntr_revno", param.get("ecntr_revno")); //계약차수
		eventParam.put("vd_cd", param.get("vd_cd")); //업체코드
		eventParam.put("cntr_sts_ccd", param.get("cntr_sts_ccd")); //진행상태
		eventParam.put("histRegDate", param.get("histRegDate")); 
		eventParam.put("cntrdoc_typ_ccd", param.get("cntrdoc_typ_ccd")); //계약유형
		eventParam.put("sgnord_typ_ccd", param.get("sgnord_typ_ccd")); //서명유형
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("sendCntrTxData", eventParam);
		publisher.publishEvent(event);
		return (ResultMap)event.getResult();
	}
	

	/**
	 * 블록체인 계약정보 삭제
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap deleteBlockList(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("param", param);
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteBlockList", eventParam);
		publisher.publishEvent(event);
		return (ResultMap)event.getResult();
	}

	/**
	 * 블록체인 검증
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap verifyAndGetBlockInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("verifyAndGetBlockInfo", eventParam);
		publisher.publishEvent(event);
		return (ResultMap)event.getResult();
	}
	
	/**
	 * 블록체인 서명값 조회
	 * 
	 * @param signValue
	 * @return ResultMap
	 */
	public ResultMap getSignSourceFromSignValue(String signValue) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("signValue", signValue);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getSignSourceFromSignValue", eventParam);
		publisher.publishEvent(event);
		return (ResultMap)event.getResult();
	}
	
	/**
	 * 단가품목 조회
	 * 
	 * @param : List
	 * @return ResultMap
	 */
	public List<Map<String,Object>> searchPriceItemList(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("uprccntr_uuid", param.get("ref_cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("searchPriceItemList", eventParam);
		publisher.publishEvent(event);
		return (List<Map<String,Object>>)event.getResult();
	}
	
	/**
	 * 대불지금조건 조회
	 * 
	 * @param : Map
	 * @return ResultMap
	 */
	public List<Map<String, Object>> getPayTermList(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("po_uuid", param.get("ref_cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getPayTermList", eventParam);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>)event.getResult();
	}
	
	/**
	 * 발주 품목 조회
	 * 
	 * @param Map
	 * @return ResultMap
	 */
	public List<Map<String, Object>> getPoItemList(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("po_uuid", param.get("ref_cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getPoItemList", eventParam);
		publisher.publishEvent(event);
		return (List<Map<String, Object>>)event.getResult();
	}

	/**
	 * 단가 정보 조회
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	public Map<String,Object> findPriceCntrInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("uprccntr_uuid", param.get("ref_cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findPriceCntrInfo", eventParam);
		publisher.publishEvent(event);
		return (Map<String,Object>)event.getResult();
	}

	/**
	 * 발주 정보 조회
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	public Map<String,Object> findOrderCntrInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("po_uuid", param.get("ref_cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findOrderCntrInfo", eventParam);
		publisher.publishEvent(event);
		return (Map<String,Object>)event.getResult();
	}

	/**
	 * 법무 검토 계약서의 템플릿 조회
	 * @param param
	 * @return
	 */
	public Map findReviewCntrdocTmpl(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntrdoc_lgl_rv_uuid", param.get("cntrdoc_lgl_rv_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findReviewCntrdocTmpl", eventParam);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}

	/**
	 * 법무 검토 계약서의 부속서류 목록 조회
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> findListReviewAppxTmpl(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntrdoc_lgl_rv_uuid", param.get("cntrdoc_lgl_rv_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListReviewAppxTmpl", eventParam);
		publisher.publishEvent(event);
		return (List<Map<String,Object>>) event.getResult();
	}
	
	/**
	 * Docusign 정보 조회
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public Map findDocusignContract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findDocusignContract", eventParam);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}

	/**
	 * Docusign 계약 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignContract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteDocusignContract", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 계약 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteAdobeSignContract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("onDeleteAdobeSign", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Adobesign 정보 조회
	 *
	 * @param param
	 * @return ResultMap
	 */
	public Map findAdobeSignContract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findAdobeSignContract", eventParam);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}

	/**
	 * adobesign 계약서 삭제
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap deleteAdobeSignInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("onDeleteAdobeSign", eventParam);
		publisher.publishEvent(event);

		return (ResultMap) event.getResult();
	}

	/**
	 * docusign 계약서 정보 수정
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap updateDocusignStatus(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateDocusignStatus", eventParam);
		publisher.publishEvent(event);

		return (ResultMap) event.getResult();
	}

}