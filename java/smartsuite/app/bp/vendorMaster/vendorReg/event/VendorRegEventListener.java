package smartsuite.app.bp.vendorMaster.vendorReg.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vendorReg.service.VendorRegService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorRegEventListener {
    
    @Inject
    VendorRegService vendorRegService;
    
    /**
     * 협력사 중복체크
     * @param e
     */
    @EventListener(condition = "#e.eventId =='checkDuplicatedVdInfo'")
    public void checkDuplicatedVdInfo(CustomSpringEvent e) {
        Map param = (Map) e.getData();
        e.setResult(vendorRegService.checkDuplicatedVdInfo(param));
    }
    
    /**
     * 협력사 기본 정보 저장
     * @param e
     */
    @EventListener(condition = "#e.eventId =='saveBasicVdInfo'")
    public void saveBasicVdInfo(CustomSpringEvent e) {
        Map param = (Map) e.getData();
        vendorRegService.saveBasicVdInfo(param);
        e.setResult(ResultMap.SUCCESS(vendorRegService.checkDuplicatedVdInfo(param)));
    }
    
    /**
     * 신규 협력사, 운영조직 저장
     * @param e
     */
    @EventListener(condition = "#e.eventId =='saveNewVdOorg'")
    public void saveNewVdOorg(CustomSpringEvent e) {
        Map param = (Map) e.getData();
        List<Map<String,Object>> existsVendors = vendorRegService.checkDuplicatedVdInfo(param);
        if(existsVendors != null && !existsVendors.isEmpty()) {
            e.setResult(ResultMap.builder().resultStatus(ResultMap.STATUS.DUPLICATED).resultList(existsVendors).build());
        } else{
            vendorRegService.saveNewVdOorg(param);
            e.setResult(ResultMap.SUCCESS(vendorRegService.checkDuplicatedVdInfo(param)));
        }
    }
}
