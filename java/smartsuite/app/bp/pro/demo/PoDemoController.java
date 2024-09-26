package smartsuite.app.bp.pro.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.demo.service.PoDemoService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * PO 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @see
 * @FileName PoController.java
 * @package smartsuite.app.bp.pro.po
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping (value = "**/pro/po/**")
public class PoDemoController {

	/** The po service. */
	@Inject
	PoDemoService poDemoService;


	@RequestMapping (value = "**/findListPoDemo.do")
	public @ResponseBody FloaterStream findListPoDemo(@RequestBody Map param) {
		// 대용량 처리
		return poDemoService.findListPoDemo(param);
	}

	@RequestMapping(value = "**/saveInfoPoIfSts.do")
	public @ResponseBody ResultMap saveInfoPoIfSts(@RequestBody Map param) {
        return poDemoService.saveInfoPoIfSts(param);
    }

	@RequestMapping(value = "**/findInfoIfPoDemo.do")
	public @ResponseBody ResultMap findInfoIfPoDemo(@RequestBody Map param) {
        return poDemoService.findInfoIfPoDemo(param);
    }
	@RequestMapping(value = "**/updatePoIfError.do")
	public @ResponseBody void updatePoIfError(@RequestBody Map param) {
        poDemoService.updatePoIfError(param);
    }
	@RequestMapping(value = "**/updateCntrIfError.do")
	public @ResponseBody void updateCntrIfError(@RequestBody Map param) {
        poDemoService.updateCntrIfError(param);
    }

	/**
	 * 문서 출력물을 위한 po정보(단건) 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoDocumentOutputPo.do")
	public @ResponseBody ResultMap findInfoDocumentOutputPo(@RequestBody Map param){
		return poDemoService.findInfoDocumentOutputPo(param);
	}

	/**
	 * 문서 출력물을 위한 po정보(복수건) 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListDocumentOutputPo.do")
	public @ResponseBody ResultMap findListDocumentOutputPo(@RequestBody Map<String, Object> param){
		return poDemoService.findListDocumentOutputPo(param);
	}
}