package smartsuite.app.bp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.po.service.PoConsortiumVendorService;

import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping (value = "**/pro/po/**/")
public class PoConsortiumController {

	@Inject
	PoConsortiumVendorService poConsortiumService;
	
	/**
	 * 컨소시엄 정보 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findPoCsData.do")
	public @ResponseBody Map findPoCsData(@RequestBody Map param){
		return poConsortiumService.findPoConsortium(param);
	}

	/**
	 * 컨소시엄 구성원 정보 저장
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "savePoCsData.do")
	public @ResponseBody ResultMap savePoCsData(@RequestBody Map param) {
		return poConsortiumService.savePoConsortiumData(param);
	}
}
