package smartsuite.app.sp.edoc.contract.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.transaction.Transactional;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpEcontractEventPublisher {
	
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
	 * 블록체인 인증서 검증
	 * 
	 * @param vdCd
	 * @return ResultMap
	 */
	public ResultMap verifyCertificate(String vdCd) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("vd_cd", vdCd);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("verifyCertificate", eventParam);
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
	 * docusign 정보 조회
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
	
}