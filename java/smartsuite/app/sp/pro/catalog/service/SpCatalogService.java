package smartsuite.app.sp.pro.catalog.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.app.sp.pro.catalog.repository.SpCatalogRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpCatalogService {
	
	@Inject
	private SharedService sharedService;

	@Inject
	private ProStatusService proStatusProcessor;
	
	@Inject
	private SpCatalogRepository spCatalogRepository;

    public List<Map<String, Object>> findCatalogList(Map param) {
		return spCatalogRepository.findCatalogList(param);
    }

	public Map findCatalogInfo(Map param) {
		Map result = Maps.newHashMap();
		result.put("catalogInfo", spCatalogRepository.findCatalogInfo(param));
		result.put("appliedUprcItemList", spCatalogRepository.findListAppliedUprcItem(param));
		return result;
	}

	public ResultMap saveCatalog(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		String ctlgUuid = param.get("ctlg_uuid") == null ? null : param.get("ctlg_uuid").toString();
		if(Strings.isNullOrEmpty(ctlgUuid)) {
			ctlgUuid = UUID.randomUUID().toString();
			param.put("ctlg_uuid", ctlgUuid);
			spCatalogRepository.insertCatalog(param);
		} else {
			spCatalogRepository.updateCatalog(param);
		}

		resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		resultMap.setResultData(param);
		return resultMap;
	}

	public ResultMap deleteCatalog(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>) param.get("deleteList");
		for(Map<String, Object> catalog : deleteList){
			spCatalogRepository.deleteCatalog(catalog);
		}
		return resultMap;
	}

    public ResultMap applyCatalog(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		spCatalogRepository.applyCatalogToUprccntrItem(param);
		return resultMap;
    }

	public ResultMap cancelCatalog(Map param) {
		ResultMap resultMap = ResultMap.getInstance();
		List<Map<String, Object>> cancelList = (List<Map<String, Object>>) param.get("cancelList");
		for(Map<String, Object> catalog : cancelList){
            spCatalogRepository.cancelCatalogFromUprccntrItem(catalog);
        }
		return resultMap;
	}

	public List<Map<String, Object>> findListUprccntrItem(Map param) {
		return spCatalogRepository.findListUprccntrItem(param);
	}

	public Map findUprcInfoWithCatalog(Map param) {
		Map result = spCatalogRepository.findUprcInfoWithCatalog(param);
		if(!Strings.isNullOrEmpty((String)result.get("thnl_athg_uuid"))){
			result.put("thumbnails", spCatalogRepository.findListThumbnail((String)result.get("thnl_athg_uuid")));
		}
		return result;
	}
}
