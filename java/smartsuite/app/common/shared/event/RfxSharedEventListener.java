package smartsuite.app.common.shared.event;

import com.google.common.collect.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.service.RfxSharedService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RfxSharedEventListener {

    @Inject
    private RfxSharedService rfxSharedService;

    @EventListener(condition = "#event.eventId == 'findListYearlyRfxItemByVendor'")
    public void findListYearlyRfxItemByVendor(CustomSpringEvent event){
        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> param = (Map<String, Object>) event.getData();
        List<Map> searchList = rfxSharedService.findListYearlyRfxItemByVendor(param);
        event.setResult(searchList);
     }
    
    /**
     * rfx 신규 협력사 조회 (rfx 진행중이고 인증번호 일치)
     * @param event
     */
    @EventListener(condition = "#event.eventId == 'spAuthNoVerify'")
    public void spAuthNoVerify(CustomSpringEvent event) {
        Map param = (Map) event.getData();
        Map returnParam  = rfxSharedService.findRfxInviteVendor(param);
        event.setResult(returnParam);
    }
}
