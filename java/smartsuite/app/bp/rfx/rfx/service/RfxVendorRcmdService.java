package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.repository.RfxVendorRcmdRepository;
import smartsuite.app.common.shared.service.SharedService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class RfxVendorRcmdService {
	
	@Inject
	SharedService sharedService;
	
	@Inject
	RfxVendorRcmdRepository rfxVendorRcmdRepository;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	public List findListRfxRcmdOption(Map param) {
		return rfxVendorRcmdRepository.findListRfxRcmdOption(param);
	}
	
	/**
	 * rfx 추천업체 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxRcmdTargVendor(Map param) {
		List<String> rcmdCds = (List<String>) param.get("rcmd_cds");
		List<String> itemCds = (List<String>) param.get("item_cds");
		List<String> sgCds = (List<String>) param.get("sg_cds");
		String oorgCd = (String) param.get("oorg_cd");
		String purcTypCcd = (String) param.get("purc_typ_ccd");
		
		List<String> rfxModuleRecommandList = Lists.newArrayList("ITEM_BID_HISTREC", "ITEM_SLCTN_HISTREC", "SG_RFX_BID_HISTREC", "SG_RFX_SLCTN_HISTREC");
		
		Map<String, List<String>> allRcmdVendorMap = Maps.newHashMap();
		for(String rcmdCd : rcmdCds) {
			List<String> rcmdVendors = Lists.newArrayList();
			// 견적에서 직접 조회해야 하는 추천 유형 외에는 전부 Spring event
			if(rfxModuleRecommandList.contains(rcmdCd)) {
				if("ITEM_BID_HISTREC".equals(rcmdCd)) {
					if(itemCds.size() > 0) {
						rcmdVendors = rfxVendorRcmdRepository.findListVendorByQtaItem(param);
					}
				} else if("ITEM_SLCTN_HISTREC".equals(rcmdCd)) {
					if(itemCds.size() > 0) {
						rcmdVendors = rfxVendorRcmdRepository.findListVendorByStlItem(param);
					}
				} else if("SG_RFX_BID_HISTREC".equals(rcmdCd)) {
					if(sgCds.size() > 0) {
						rcmdVendors = rfxVendorRcmdRepository.findListVendorByQtaSG(param);
					}
				} else if("SG_RFX_SLCTN_HISTREC".equals(rcmdCd)) {
					if(sgCds.size() > 0) {
						rcmdVendors = rfxVendorRcmdRepository.findListVendorByStlSG(param);
					}
				}
			} else {
				rcmdVendors = rfxEventPublisher.findRecommandVendors(rcmdCd, itemCds, sgCds, oorgCd, purcTypCcd);
			}
			
			for(String rcmdVendor : rcmdVendors) {
				if(allRcmdVendorMap.get(rcmdVendor) == null) {
					List<String> vendorRcmdCds = Lists.newArrayList();
					vendorRcmdCds.add(rcmdCd);
					allRcmdVendorMap.put(rcmdVendor, vendorRcmdCds);
				} else {
					List<String> vendorRcmdCds = allRcmdVendorMap.get(rcmdVendor);
					vendorRcmdCds.add(rcmdCd);
				}
			}
		}
		
		// 추천업체 중복제거
		Map tempParam;
		for(String key : allRcmdVendorMap.keySet()) {
			tempParam = Maps.newHashMap();
			tempParam.put("task_id", key);
			sharedService.insertTempQueryId(tempParam);
		}
		
		List<Map> resultList = rfxVendorRcmdRepository.findListRfxRcmdTargVendor(param);
		for(Map result : resultList) {
			List<String> vendorRcmdCds = allRcmdVendorMap.get(result.get("vd_cd"));
			result.put("rcmd_cnt", vendorRcmdCds.size());
			result.put("rcmdList", vendorRcmdCds);
		}
		
		sharedService.deleteTempQueryId();
		
		return resultList;
	}
	
	public Map searchRfxVendorRcmdByRfx(Map param) {
		List<Map> resultList = rfxVendorRcmdRepository.searchRfxVendorRcmdByRfx(param);
		Map<String, List<String>> allRcmdVendorMap = Maps.newHashMap();
		for(Map result : resultList) {
			if(allRcmdVendorMap.get(result.get("vd_cd")) == null) {
				List<String> vendorRcmdCds = Lists.newArrayList();
				vendorRcmdCds.add((String) result.get("rcmd_vd_typ_ccd"));
				allRcmdVendorMap.put((String) result.get("vd_cd"), vendorRcmdCds);
			} else {
				List<String> vendorRcmdCds = allRcmdVendorMap.get(result.get("vd_cd"));
				vendorRcmdCds.add((String) result.get("rcmd_vd_typ_ccd"));
			}
		}
		
		return allRcmdVendorMap;
	}
}
