package smartsuite.app.sp.login.bidding.event;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import smartsuite.app.event.CustomSpringEvent;

import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
public class SupplierRootEventPublisher {

	@Autowired
	ApplicationEventPublisher publisher;


	/**
	 * PKI 툴킷 설치 확인
	 *
	 * @param : void
	 * @return : String
	 */
	public boolean verifyClientToolkitInstallComplete(String installStatus) {
		Map eventParam = Maps.newHashMap();
		eventParam.put("installStatus", installStatus);
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("verifyClientToolkitInstallComplete", eventParam);
		publisher.publishEvent(event);
		return (boolean)event.getResult();
	}

	/**
	 * Client 툴킷 설치 페이지
	 *
	 * @param : void
	 * @return : String
	 */
	public String clientToolkitInstallUrl() {
		Map eventParam = Maps.newHashMap();
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("clientToolkitInstallUrl", eventParam);
		publisher.publishEvent(event);
		return (String)event.getResult();
	}

	/**
	 * Client 툴킷 페이지
	 *
	 * @param : void
	 * @return : String
	 */
	public String clientToolkitUrl() {
		Map eventParam = Maps.newHashMap();
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("clientToolkitUrl", eventParam);
		publisher.publishEvent(event);
		return (String)event.getResult();
	}
    
    /**
     * rfx 신규 협력사 조회 (rfx 진행중이고 인증번호 일치)
     * @param requestMap
     * @return
     */
    public Map spAuthNoVerify(Map requestMap) {
         CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("spAuthNoVerify", requestMap);
         publisher.publishEvent(event);
         
         return (Map)event.getResult();
    }
}
