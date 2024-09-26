package smartsuite.app.common.cert.event;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


import com.google.common.collect.Maps;

import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.event.CustomSpringEvent;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class CertEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	/**
	 * pdf 생성 요청
	 * 
	 * @param cntrInfo
	 * @return void
	 */
	public Map generatePdfUsingHtml(Map cntrInfo, List<Map<String,Object>> appList, boolean sign, CertInfo certInfo, boolean horizon) {
		Map eventParam = Maps.newHashMap();
		
		eventParam.put("cntrInfo", cntrInfo); //계약정보
		eventParam.put("appList", appList); //첨부서 정보
		eventParam.put("sign", sign); //서명 기능
		eventParam.put("certInfo", certInfo); //인증서정보
		eventParam.put("horizon", horizon); //pdf 가로세로 중 세로
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("generatePdfUsingHtml", eventParam);
		publisher.publishEvent(event);
		return (Map)event.getResult();
	}
	
	
	/**
	 * wartermark type
	 * 
	 * @param
	 * @return void
	 */
	public String findWatermarkType() {

		Map eventParam = Maps.newHashMap();

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findWatermarkType", eventParam);
		publisher.publishEvent(event);

		return (String)event.getResult();
	}

}