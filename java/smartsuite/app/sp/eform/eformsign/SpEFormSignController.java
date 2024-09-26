package smartsuite.app.sp.eform.eformsign;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.eform.eformsign.service.SpEFormSignService;

/**
 * 간편서명 관련 처리하는 컨트롤러 Class입니다.
 *
 * @FileName SpEFormSignController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller

@RequestMapping(value = "**/sp/eform/**")
public class SpEFormSignController {

	@Inject
	SpEFormSignService SpEFormSignService;
	
	
	/**
	 * 간편서명 을 서명정보 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findSignTargetInfoFormerN.do")
	public @ResponseBody ResultMap findSignTargetInfoFormerN(@RequestBody Map param) {
		return SpEFormSignService.findSignTargetInfoFormerN(param);
	}

	/**
	 * 간편서명 서명 거부
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "rejectSignDocument.do")
	public @ResponseBody ResultMap rejectSignDocument(@RequestBody Map param) {
		return SpEFormSignService.rejectSignDocument(param);
	}

	/**
	 * 간편서명 서명 제출 및 다음 서명 대상자에게 발송
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "sendNextSignTarget.do")
	public @ResponseBody ResultMap sendNextSignTarget(@RequestBody Map param) {
		return SpEFormSignService.sendNextSignTarget(param);
	}
}