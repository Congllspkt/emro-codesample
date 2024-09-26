package smartsuite.app.bp.contract.cntrreq;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.contract.cntrreq.service.ContractReqService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 계약 요청 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName ContractReqController.java
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/contract/cntrreq/**")
public class ContractReqController {
	
	@Inject
	ContractReqService contractReqService;
	
	
	/**
	 * 계약 요청 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value="largeFindListContractReq.do")
	public @ResponseBody FloaterStream largeFindListContractReq(@RequestBody Map param) {
		// 대용량 처리
		return contractReqService.largeFindListContractReq(param);
	}
	
	/**
	 * 계약 요청 접수
	 * @param param
	 * @return
	 */
	@RequestMapping(value="receiveContractReq.do")
	public @ResponseBody ResultMap receiveContractReq(@RequestBody Map param) {
		return contractReqService.receiveContractReq(param);
	}
	
	/**
	 * 계약 요청 반려
	 * @param param
	 * @return
	 */
	@RequestMapping(value="returnContractReq.do")
	public @ResponseBody ResultMap returnContractReq(@RequestBody Map param) {
		return contractReqService.returnContractReq(param);
	}
	
	/**
	 * 계약 요청 목록으로 반환
	 * @param param
	 * @return
	 */
	@RequestMapping(value="returnContract.do")
	public @ResponseBody ResultMap returnContract(@RequestBody Map param) {
		return contractReqService.returnContract(param);
	}

	@RequestMapping(value="findContractReqWk.do")
	public @ResponseBody Map<String, Object> findContractReqWk(@RequestBody Map param) {
		return contractReqService.findContractReqWk(param);
	}
}