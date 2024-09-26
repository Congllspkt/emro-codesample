package smartsuite.app.bp.contract.common.event;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.app.shared.SignOrder;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	
	/**
	 * 전자계약 정보 생성
	 * @param cntrInfo
	 * @return
	 */
	public ResultMap createEcontract(Map param) {
		String eventId = "createEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		eventParam.put("bat_cntr_grp_uuid", param.get("bat_cntr_grp_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 전자계약 정보 조회
	 * @param param
	 * @return
	 */
	public Map findEcontract(Map param) {
		String eventId = "findEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (Map) event.getResult();
	}
	
	/**
	 * 전자계약 정보 삭제
	 * @param param
	 */
	public ResultMap deleteEcontract(Map param) {
		String eventId = "deleteEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 전자서명 (PKI)
	 * @param param
	 * @return
	 */
	public ResultMap signEcontract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("signEcontract", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 서명 순서 조회
	 * @param param
	 * @return
	 */
	public SignOrder getSignOrder(Map param) {
		String eventId = "getSignOrder";

		Map eventParam = Maps.newHashMap();
		eventParam.put("cntrdoc_tmpl_uuid", param.get("cntrdoc_tmpl_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (SignOrder) event.getResult();
	}

	/**
	 * 발주 요청
	 * @param param
	 */
	public void requestPo(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestPo", param);
		publisher.publishEvent(event);
	}

	/**
	 * 발주 변경 요청
	 * @param param
	 */
	public void requestChangePo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		eventParam.put("req_uuid", param.get("cntr_req_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("completeCntrChgReqByPo", eventParam);
		publisher.publishEvent(event);
	}

	/**
	 * 단가 데이터 생성
	 * @param param
	 */
	public void createUnitPrice(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createUnitPrice", eventParam);
		publisher.publishEvent(event);
	}

	/**
	 * RFX 요청 계약 완료 후처리
	 * @param param
	 */
	public void completeReqRfx(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("req_uuid", param.get("cntr_req_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("completeReqRfx", eventParam);
		publisher.publishEvent(event);
	}

	/**
	 * 기본거래계약 체결 완료 후처리
	 * @param param
	 */
	public void completeElementaryContract(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("vd_cd", param.get("vd_cd"));
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("completeElementaryContract", param);
		publisher.publishEvent(event);
	}

	/**
	 * Docusign Template 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignTemplate(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createDocusignTemplate", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Docusign Template 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignTemplate(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		eventParam.put("return_ui_id", param.get("return_ui_id"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findDocusignTemplate", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Docusign Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignTemplate(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteDocusignTemplate", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Docusign Envelope 생성
	 * @param param
	 * @return
	 */
	public ResultMap createDocusignEnvelope(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createDocusignEnvelope", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	public ResultMap findDocusignEnvelope(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		eventParam.put("return_ui_id", param.get("return_ui_id"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findDocusignEnvelope", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * Docusign Envelope 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteDocusignEnvelope(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("dsgn_uuid", param.get("dsgn_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteDocusignEnvelope", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 간편 서명 Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteEFormTemplate(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteEFormTemplate", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 보증 요청
	 * @param : param
	 * @return
	 */
	public ResultMap requestGuarantee(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestGuarantee", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 계약서 생성
	 * @param param
	 * @return
	 */
	public ResultMap onCreateAdobeSign(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("onCreateAdobeSign", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 계약서 재생성
	 * @param param
	 * @return
	 */
	public ResultMap onReCreateAdobeSign(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("asgn_uuid", param.get("asgn_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("onReCreateAdobeSign", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 계약서 보기
	 * @param param
	 * @return
	 */
	public ResultMap getAdobeSignUrlInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("agreement_id", param.get("agreement_id"));
		eventParam.put("popup_type", param.get("popup_type"));
		eventParam.put("view_name", param.get("view_name"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getAdobeSignUrlInfo", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 진행상태 체크
	 * @param param
	 * @return
	 */
	public ResultMap checkAdobeSignStatus(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("agreement_id", param.get("agreement_id"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("popup_type", param.get("popup_type"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("checkAdobeSignStatus", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * AdobeSign 계약서 삭제
	 * @param param
	 * @return
	 */
	public ResultMap onDeleteAdobeSign(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("asgn_uuid", param.get("asgn_uuid"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("agreement_id", param.get("agreement_id"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("onDeleteAdobeSign", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	public ResultMap getBpAdobeSignUrlInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("asgn_uuid", param.get("asgn_uuid"));
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));
		eventParam.put("agreement_id", param.get("agreement_id"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("getBpAdobeSignUrlInfo", eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 계약서 회수
	 * @param param
	 */
	public ResultMap withdrawalEcontract(Map param) {
		String eventId = "withdrawalEcontract";
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntr_uuid", param.get("cntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent(eventId, eventParam);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}

	/**
	 * 서명잠금상태 기준 회수가능여부 체크
	 *
	 * @param param
	 * @return ResultMap
	 */
	public String findWdPossYnByLckdSts(Map param) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("ecntr_uuid", param.get("ecntr_uuid"));

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findWdPossYnByLckdSts", eventParam);
		publisher.publishEvent(event);

		return (String) event.getResult();
	}

	/**
	 * 계약 일괄 다운로드 > pdf 리스트 가져오기
	 */
	public File downloadAllCntr(String[] cntrUUIDs) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("cntrList", cntrUUIDs);

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("downloadAllCntr", eventParam);
		publisher.publishEvent(event);

		return (File) event.getResult();
	}
}