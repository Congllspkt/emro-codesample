package smartsuite.app.bp.stamptax.event;

import com.google.common.base.Strings;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.stamptax.service.StampTaxService;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class StampTaxEventListener {

	@Inject
	StampTaxService stampTaxService;

	/**
	 * 타 모듈에서 계약정보를 받아서 인지세 납부 이력을 조회한다.<br><br>
	 * <b>Option:</b><br>
	 * event.data.map - cntr_uuid, cntrr_typ_ccd<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'findListStampTaxPayHistory'")
	public void findListStampTaxPayHistory(CustomSpringEvent event) {
		Map<String, Object> param = (Map<String, Object>) event.getData();
		String cntrUuid = (String) param.get("cntr_uuid");
		String cntrrTypCcd = (String) param.get("cntrr_typ_ccd");

		if(Strings.isNullOrEmpty(cntrUuid)) {
			throw new CommonException(this.getClass() + " findListStampTaxPayHistory EventListener :" + "There is no cntr_uuid");
		}

		if(Strings.isNullOrEmpty(cntrrTypCcd)) {
			throw new CommonException(this.getClass() + " findListStampTaxPayHistory EventListener :" + "There is no cntrr_typ_ccd");
		}

		event.setResult(stampTaxService.findListStampTaxPayHistory(param));
	}



}
