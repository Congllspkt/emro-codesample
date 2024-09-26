package smartsuite.app.sp.pro.catalog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.catalog.service.SpCatalogService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/sp/pro/catalog/")
public class SpCatalogController {
	
	@Inject
	SpCatalogService spCatalogService;
	
	/**
	 * 카탈로그 리스트 조회
	 *
	 * @param param 검색조건
	 * @return
	 */
	@RequestMapping(value = "findCatalogList.do")
	public @ResponseBody List<Map<String, Object>> findCatalogList(@RequestBody Map param) {
		// 대용량 처리
		return spCatalogService.findCatalogList(param);
	}
	

	/**
	 * 카탈로그 정보 조회
	 *
	 * @param param {inv_uuid}
	 * @return
	 */
	@RequestMapping(value = "findCatalogInfo.do")
	public @ResponseBody Map findCatalogInfo(@RequestBody Map param) {
		return spCatalogService.findCatalogInfo(param);
	}
	
	/**
	 * 카탈로그 저장
	 *
	 * @param param {catalogInfo}
	 * @return
	 */
	@RequestMapping(value = "saveCatalog.do")
	public @ResponseBody ResultMap saveCatalog(@RequestBody Map param) {
		return spCatalogService.saveCatalog(param);
	}

	/**
	 * 카탈로그 적용
	 *
	 * @param param {catalogInfo}
	 * @return
	 */
	@RequestMapping(value = "applyCatalog.do")
	public @ResponseBody ResultMap applyCatalog(@RequestBody Map param) {
		return spCatalogService.applyCatalog(param);
	}

	/**
	 * 카탈로그 해제
	 *
	 * @param param {catalogInfo}
	 * @return
	 */
	@RequestMapping(value = "cancelCatalog.do")
	public @ResponseBody ResultMap cancelCatalog(@RequestBody Map param) {
		return spCatalogService.cancelCatalog(param);
	}


	/**
	 * 카탈로그 삭제
	 *
	 * @param param {ctlg_id}
	 * @return
	 */
	@RequestMapping(value = "deleteCatalog.do")
	public @ResponseBody ResultMap deleteSpInvoice(@RequestBody Map param) {
		return spCatalogService.deleteCatalog(param);
	}

	@RequestMapping(value = "findListUprccntrItem.do")
	public @ResponseBody List<Map<String, Object>> findListUprccntrItem(@RequestBody Map param) {
		return spCatalogService.findListUprccntrItem(param);
	}

	@RequestMapping(value = "findUprcInfoWithCatalog.do")
	public @ResponseBody Map findUprcInfoWithCatalog(@RequestBody Map param) {
		return spCatalogService.findUprcInfoWithCatalog(param);
	}


}
