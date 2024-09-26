package smartsuite.app.bp.edoc.estamptax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import smartsuite.app.bp.edoc.estamptax.service.EstampTaxServiceBackupDev;

import javax.inject.Inject;

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
@RequestMapping(value = "**/bp/edoc/**")
public class EstampTaxControllerBackupDev {
	
	@Inject
	EstampTaxServiceBackupDev estampTaxServiceDev;

	/**
	 * 인지세 URL정보 가져오기
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the list
	 * @Date : 2020. 03. 19
	 * @Method Name : findStampTaxBpUrlInfo
	 */
	/*@RequestMapping(value = "findStampTaxBpUrlInfo.do")
	public @ResponseBody Map findStampTaxBpUrlInfo(@RequestBody Map param) {
		return estampTaxServiceDev.findStampTaxBpUrlInfo(param);
	}
	*//**
	 * 인지세 납부정보 확인
	 *
	 * @author : DaeSung Lee
	 * @param map
	 * @return the map
	 * @Date : 2020. 3. 30
	 * @Method Name : saveIssuePayList
	 *//*
	@RequestMapping(value = "saveIssuePayList.do")
	public @ResponseBody Map saveIssuePayList(@RequestBody Map param) {
		return estampTaxServiceDev.saveIssuePayList(param);
	}
	
	*//**
	 * 인지세 정보 가져오기
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 4. 01
	 * @Method Name : findStampTaxInfo
	 *//*
	@RequestMapping(value = "findEstampTaxInfo.do")
	public @ResponseBody Map findEstampTaxInfo(@RequestBody Map param) {
		return estampTaxServiceDev.findEstampTaxInfo(param);
	}
	
	*//**
	 * 
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 4. 07
	 * @Method Name : downloadStampTaxFile
	 *//*
	@RequestMapping(value = "downloadStampTaxFile.do")
	public @ResponseBody Map downloadStampTaxFile(@RequestBody Map param) {
		return estampTaxServiceDev.downloadStampTaxFile(param);
	}
	
	
	*//**
	 * 인지세 이력정보 가져오기
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 4. 14
	 * @Method Name : findEstampTaxInfo
	 *//*
	@RequestMapping(value = "findEstampTaxHistoryInfo.do")
	public @ResponseBody List findEstampTaxHistoryInfo(@RequestBody Map param) {
		return estampTaxServiceDev.findEstampTaxHistoryInfo(param);
	}*/

}