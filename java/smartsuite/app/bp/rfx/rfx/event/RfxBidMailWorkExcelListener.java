package smartsuite.app.bp.rfx.rfx.event;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.rfx.rfx.service.RfxBidMailWorkService;
import smartsuite.app.common.mail.MailWorkExcelHandler;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxBidMailWorkExcelListener implements MailWorkExcelHandler {

	@Autowired(required = false)
	private RfxBidMailWorkService rfxBidMailWorkService;

	@Override
	public void excelParsingAfterAppUploadService(Map dataResultMap) throws Exception {
		String workCd = (String) dataResultMap.get("eml_task_typ_ccd");
		
		//result된 workCd를 가지고 각각의 save를 우선 호출한다.
		// "RFX_" 로 시작하는 workCd인 경우, 모두 동일 서비스 수행
		if(workCd.indexOf("RFX_") == 0) {
			rfxBidMailWorkService.excelParsingService(dataResultMap);
		}
	}
}
