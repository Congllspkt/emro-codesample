package smartsuite.app.bp.edoc.contract.event;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.event.CustomSpringEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class EcontractEventListener {
	
	@Inject
	EcontractService econtractService;

	@Value("#{edoc['watermark.type']}")
	String watermarkType;

	
	@EventListener(condition = "#event.eventId == 'generatePdfUsingHtml'")
	public void generatePdfUsingHtml(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		Map cntrInfo = (Map) param.get("cntrInfo");
		List<Map<String,Object>> appList = (List<Map<String,Object>>)param.get("appList");
		boolean sign = (boolean) param.get("sign");
		CertInfo certInfo = (CertInfo) param.get("certInfo");
		boolean horizon = (boolean) param.get("horizon");

		event.setResult(econtractService.generatePdfUsingHtml(cntrInfo, appList, sign, certInfo, horizon));
	}

	@EventListener(condition = "#event.eventId == 'createEcontract'")
	public void createEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.createEcontract(param));
	}
	
	@EventListener(condition = "#event.eventId == 'findEcontract'")
	public void findEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.findEcontract(param));
	}

	@EventListener(condition = "#event.eventId == 'deleteEcontract'")
	public void deleteEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.deleteEcontract(param));
	}

	@EventListener(condition = "#event.eventId == 'signEcontract'")
	public void signEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.signEcontract(param));
	}

	@EventListener(condition = "#event.eventId == 'getSignOrder'")
	public void getSignOrder(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		String cntrdocTmplId = (String) param.get("cntrdoc_tmpl_uuid");
		event.setResult(econtractService.getSignOrder(cntrdocTmplId));
	}

	@EventListener(condition = "#event.eventId == 'findWatermarkType'")
	public void findWatermarkType(CustomSpringEvent event) {
		event.setResult(watermarkType);
	}

	@EventListener(condition = "#event.eventId == 'withdrawalEcontract'")
	public void withdrawalEcontract(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.withdrawalEcontract(param));
	}

	@EventListener(condition = "#event.eventId == 'findWdPossYnByLckdSts'")
	public void findSignLockStatus(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.findWdPossYnByLckdSts(param));
	}

	@EventListener(condition = "#event.eventId == 'downloadAllCntr'")
	public void downloadAllCntr(CustomSpringEvent event) {
		Map param = (Map) event.getData();
		event.setResult(econtractService.downloadAllCntr(param));
	}
}
