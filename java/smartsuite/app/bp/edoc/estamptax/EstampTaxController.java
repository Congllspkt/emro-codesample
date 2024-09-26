package smartsuite.app.bp.edoc.estamptax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.edoc.estamptax.service.EstampTaxService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

/**
 * 전자수입인지 관련 클래스입니다.
 *
 * @author DaeSung Lee
 * @see 
 * @FileName EstampTaxController.java
 * @package smartsuite.app.bp.edoc.estamptax
 * @since 2020. 3. 20
 * @변경이력 : [2020. 4. 9] DaeSung Lee 최초작성
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/edoc/stamptax/**")
public class EstampTaxController {
	
	@Inject
	EstampTaxService estampTaxService;


	/**
	 * 인지세 정보 가져오기
	 *
	 * @author : 인지세 가져오기 위한 파라미터
	 * @return : 인지세 정보
	 */
	@RequestMapping(value = "findEstampTaxInfo.do")
	public @ResponseBody Map findEstampTaxInfo(@RequestBody Map param) {
		return estampTaxService.findEstampTaxInfo(param);
	}

	/**
	 * 전자인지세 구매 팝업
	 *
	 * @param : 전자인지세 구매팝업 호출 시 필요한 파라미터
	 * @return : 전자인지세 구매팝업 url
	 */
	@RequestMapping(value = "findStampTaxBpUrlInfo.do")
	public @ResponseBody Map findStampTaxBpUrlInfo(@RequestBody Map param) {
		return estampTaxService.findStampTaxBpUrlInfo(param);
	}

	/**
	 * @param : 전자인지세 pdf 다운로드
	 * @return the map
	 * @Date : 2020. 4. 07
			* @Method Name : downloadStampTaxFile
	 */
	@RequestMapping(value = "downloadStampTaxFile.do")
	public @ResponseBody ResultMap downloadStampTaxFile(@RequestBody Map param) {
		return estampTaxService.downloadStampTaxFile(param);
	}

	/**
	 * 인지세 납부 이력 조회
	 *
	 * @param : Map
	 * @return : ResultMap
	 */
	@RequestMapping(value = "findEstampTaxHistoryInfo.do")
	public @ResponseBody ResultMap findEstampTaxHistoryInfo(@RequestBody Map param) {
		return estampTaxService.findListEStampTaxPayHistory(param);
	}

	/**
	 * 인지세 환급
	 *
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value = "saveRefundStampTax.do")
	public @ResponseBody ResultMap saveRefundStampTax(@RequestBody Map param) {
		return estampTaxService.saveRefundStampTax(param);
	}

}