package smartsuite.app.sp.edoc.estamptax;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;

/**
 * (협력사) 전자수입인지 관련 처리를 하는 컨트롤러 Class입니다.
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/edoc/stamptax/**")
public class SpEstampTaxController {
	
	@Inject
	SpEstampTaxService spEstampTaxService;


	/**
	 * 전자수입인지 상세 조회
	 *
	 * @param : 전자수입인지 상세 조회 조건에 해당하는 인자
	 * @return : 전자수입인지 상세 정보
	 */
	@RequestMapping(value = "findEstampTaxInfo.do")
	public @ResponseBody Map findEstampTaxInfo(@RequestBody Map param) {
		return spEstampTaxService.findEstampTaxInfo(param);
	}

	/**
	 * 인지세 URL정보 가져오기
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the list
	 * @Date : 2020. 03. 19
	 * @Method Name : findStampTaxBpUrlInfo
	 */
	@RequestMapping(value = "findStampTaxBpUrlInfo.do")
	public @ResponseBody Map findStampTaxBpUrlInfo(@RequestBody Map param) {
		return spEstampTaxService.findStampTaxBpUrlInfo(param);
	}
	/**
	 * 인지세 납부정보 확인
	 *
	 * @author : DaeSung Lee
	 * @param map
	 * @return the map
	 * @Date : 2020. 3. 30
	 * @Method Name : saveIssuePayList
	 */
	@RequestMapping(value = "saveIssuePayList.do")
	public @ResponseBody Map saveIssuePayList(@RequestBody Map param) {
		return spEstampTaxService.saveIssuePayList(param);
	}

	/**
	 * 인지세 PDF 파일 다운로드
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 4. 07
	 * @Method Name : downloadStampTaxFile
	 */
	@RequestMapping(value = "downloadStampTaxFile.do")
	public @ResponseBody ResultMap downloadStampTaxFile(@RequestBody Map param) {
		return spEstampTaxService.downloadStampTaxFile(param);
	}

	/**
	 * 인지세 납부 이력 조회
	 *
	 * @param : Map
	 * @return : ResultMap
	 */
	@RequestMapping(value = "findEstampTaxHistoryInfo.do")
	public @ResponseBody ResultMap findEstampTaxHistoryInfo(@RequestBody Map param) {
		return spEstampTaxService.findListEStampTaxPayHistory(param);
	}

	/**
	 * 인지세 환급
	 *
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value = "saveRefundStampTax.do")
	public @ResponseBody ResultMap saveRefundStampTax(@RequestBody Map param) {
		return spEstampTaxService.saveRefundStampTax(param);
	}

}