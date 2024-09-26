package smartsuite.app.bp.eform.eformsign;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.eform.eformsign.service.EFormSignService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 간편서명 관련 처리하는 컨트롤러 Class입니다.
 *
 * @FileName EFormSignController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/eform/**")
public class EFormSignController {

	@Inject
	EFormSignService eFormSignService;


	/**
	 * 간편서명 계약서 파일(PDF) 조회
	 * 
	 * @param request
	 * @param response
	 * @param athgId 계약서 파일 그룹 코드
	 */
	@RequestMapping(value = "getEFormPdf.do")
	public void getEFormPdf(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "athg_uuid", required = false) String athgId) {
		eFormSignService.getEFormPdf(request, response, athgId);
	}
	
	/**
	 * 간편서명 Template 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findEFormSignTemplate.do")
	public @ResponseBody ResultMap findEFormSignTemplate(@RequestBody Map param) {
		return eFormSignService.findEFormSignTemplate(param);
	}
	
	/**
	 * 간편서명 Template 작성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveEFormSignTemplate.do")
	public @ResponseBody ResultMap saveEFormSignTemplate(@RequestBody Map param) {
		return eFormSignService.saveEFormSignTemplate(param);
	}

	/**
	 * 간편서명 갑 서명대상 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findSignTargetInfoFormerY.do")
	public @ResponseBody ResultMap findSignTargetInfoFormerY(@RequestBody Map param) {
		return eFormSignService.findSignTargetInfoFormerY(param);
	}
	
	/**
	 * 간편서명 계약서 발송
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "sendEFormSignContract.do")
	public @ResponseBody ResultMap sendEFormSignContract(@RequestBody Map param) {
		return eFormSignService.sendEFormSignContract(param);
	}
}