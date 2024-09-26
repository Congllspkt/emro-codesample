package smartsuite.app.sp.stamptax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.stamptax.service.SpStampTaxService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * (협력사)인지세 관련 처리를 하는 컨트롤러 Class입니다.
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/stamptax/**")
public class SpStampTaxController {

	private static final Logger LOG = LoggerFactory.getLogger(SpStampTaxController.class);

	@Inject
	SpStampTaxService spStampTaxService;

	/**
	 * 인지세 목록 조회
	 *
	 * @param : 인지세 목록을 조회 조건에 해당하는 인자
	 * @return : 인지세 목록
	 */
	@RequestMapping(value = "largeFindListStampTax.do")
	public @ResponseBody FloaterStream largeFindListStampTax(@RequestBody Map param) {
		// 대용량 처리
		return spStampTaxService.largeFindListStampTax(param);
	}

	/**
	 * (오프라인)인지세 상세 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value = "findOfflStampTaxInfo.do")
	public @ResponseBody ResultMap findOfflStampTaxInfo(@RequestBody Map param) {
		return spStampTaxService.findOfflStampTaxInfo(param);
	}

	/**
	 * (오프라인)인지세 파일 저장
	 *
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value = "saveStampTaxFile.do")
	public @ResponseBody ResultMap saveStampTaxFile(@RequestBody Map param) {
		return spStampTaxService.saveStampTaxFile(param);
	}

	/**
	 * 인지세 납부 이력 조회
	 *
	 * @param : Map
	 * @return : List
	 */
	@RequestMapping(value ="findListStampTaxPayHistory.do")
	public @ResponseBody List findListStampTaxPayHistory(@RequestBody Map param) {
		return spStampTaxService.findListStampTaxPayHistory(param);
	}
}
