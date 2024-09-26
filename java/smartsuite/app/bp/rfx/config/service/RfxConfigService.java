package smartsuite.app.bp.rfx.config.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.service.AuctionService;
import smartsuite.app.bp.rfx.config.repository.RfxConfigRepository;
import smartsuite.app.bp.rfx.rfx.service.RfxService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxConfigService {

	@Inject
	RfxConfigRepository rfxConfigRepository;

	@Inject
	RfxService rfxService;

	@Inject
	AuctionService auctionService;

	public List findListRfxCopy(Map param) {
		return rfxConfigRepository.findListRfxCopy(param);
	}
	
	public Map findDataByReqItemIdsFromConfig(Map param) {
		Map resultMap = Maps.newHashMap();
		String rfxTypCcd = param.get("rfx_typ_ccd") != null ? (String) param.get("rfx_typ_ccd") : "";
		if(!Strings.isNullOrEmpty(rfxTypCcd)) {
			if("RAUC".equals(rfxTypCcd)) {
				resultMap = auctionService.findRfxDefaultDataByReqItemIds(param);
			} else {
				resultMap = rfxService.findRfxDefaultDataByReqItemIds(param);
			}
		}
		return resultMap;
	}
}