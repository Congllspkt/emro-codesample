package smartsuite.app.bp.eform.eformsign;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.eform.seal.service.EFormSealService;
import smartsuite.app.bp.eform.seal.service.TextSignatureMaker;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.eform.eformsign.service.SpEFormSignService;

/**
 * 사용자 정보가 없는 경우, 간편서명 관련 처리하는 컨트롤러 Class입니다.
 *
 * @FileName NotUserEFormSignController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller

@RequestMapping(value = "**/bp/edoc/eform/notUser/**")
public class NotUserEFormSignController {

	@Inject
	SpEFormSignService spEFormSignService;
	@Inject
	EFormSealService eFormSealService;
	@Inject
	TextSignatureMaker textSignatureMaker;
	
	/**
	 * 간편서명 을 서명정보 조회 (Mail 서명)
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findSignTargetInfoFormerNByMail.do")
	public @ResponseBody ResultMap findSignTargetInfoFormerNByMail(@RequestBody Map param) {
		return spEFormSignService.findSignTargetInfoFormerNByMail(param);
	}

	/**
	 * 간편서명 서명 거부 (Mail 서명)
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "rejectSignDocumentByMail.do")
	public @ResponseBody ResultMap rejectSignDocumentByMail(@RequestBody Map param) {
		return spEFormSignService.rejectSignDocument(param);
	}

	/**
	 * 간편서명 서명 제출 및 다음 서명 대상자에게 발송 (Mail 서명)
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "sendNextSignTargetByMail.do")
	public @ResponseBody ResultMap sendNextSignTargetByMail(@RequestBody Map param) {
		return spEFormSignService.sendNextSignTarget(param);
	}
	
	/**
	 * 회사 등록 인장 리스트
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findCompanyStampListByMail.do")
	public @ResponseBody List<Map<String, Object>> findCompanyStampListByMail(@RequestBody Map param) {
		return eFormSealService.findCompanySealList(param);
	}	

	/**
	 * 유저 등록 인장 리스트
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findUserStampListByMail.do")
	public @ResponseBody List<Map<String, Object>> findUserStampListByMail(@RequestBody Map param) {
		return eFormSealService.findUserSealList(param);
	}
	
	/**
	 * 간편서명 텍스트 파일 검색
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "createTextSignaturesByMail.do")
	public @ResponseBody Map createTextSignaturesByMail(@RequestBody Map param) {
		return textSignatureMaker.makeTextSignature(param.get("signatureText").toString(), param.get("fontIndex").toString());
	}
}