package smartsuite.app.bp.guarantee.event;

import com.google.common.base.Strings;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.guarantee.service.GuaranteeService;
import smartsuite.app.bp.stamptax.service.StampTaxService;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GuaranteeEventListener {



	@Inject
	GuaranteeService guaranteeService;

	@EventListener(condition = "#event.eventId == 'requestGuarantee'")
	public void requestGuarantee(CustomSpringEvent event) {
		Map<String, Object> resultData = (Map<String, Object>) event.getData();
		String cntrUuid = (String)resultData.get("cntr_uuid");
		if(Strings.isNullOrEmpty(cntrUuid)) {
			throw new CommonException(this.getClass() + " requestGuarantee EventListener :" + "There is no cntr_uuid.");
		}
		guaranteeService.requestGuarantee(resultData);
	}
	
}
