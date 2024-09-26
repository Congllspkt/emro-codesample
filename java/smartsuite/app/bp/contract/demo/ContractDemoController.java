package smartsuite.app.bp.contract.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.contract.demo.service.ContractDemoService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * 계약 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName ContractController.java
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/contract/**")
public class ContractDemoController {

	@Inject
	ContractDemoService contractDemoService;


	/**
	 * 계약 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "**/largeFindListContractDemo.do")
	public @ResponseBody FloaterStream largeFindListContractDemo(@RequestBody Map param) {
		// 대용량 처리
		return contractDemoService.largeFindListContractDemo(param);
	}

	@RequestMapping(value = "**/saveInfoCntrIfSts.do")
	public @ResponseBody ResultMap saveInfoCntrIfSts(@RequestBody Map param) {
		return contractDemoService.saveInfoCntrIfSts(param);
	}

	@RequestMapping(value = "**/findInfoIfCntrDemo.do")
	public @ResponseBody ResultMap findInfoIfCntrDemo(@RequestBody Map param) {
		return contractDemoService.findInfoIfCntrDemo(param);
	}
}