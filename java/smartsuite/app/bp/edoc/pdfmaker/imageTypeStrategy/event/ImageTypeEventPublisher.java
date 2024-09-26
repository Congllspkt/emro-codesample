package smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy.event;

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
public class ImageTypeEventPublisher {
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	
	/**
	 * 인증서 정보 요청
	 * @param Map
	 * @return ResultMap
	 */
	public Map findOrgCertInfo(Map param) {
		Map eventParam = Maps.newHashMap();
		
		eventParam.put("logic_org_cd", param.get("logic_org_cd")); //논리조직코드
		eventParam.put("logic_org_typ_ccd", param.get("logic_org_typ_ccd")); //조직유형공통코드

		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findOrgCertInfo", eventParam);
		publisher.publishEvent(event);
		ResultMap resultMap = (ResultMap)event.getResult();
		return (Map)resultMap.getResultData().get("certInfo");
	}
	

}