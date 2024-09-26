package smartsuite.app.sp.contract.common;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.sp.contract.common.service.SpContractService;
import smartsuite.data.FloaterStream;

/**
 * 계약 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName SpContractController.java
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/contract/**")
public class SpContractController {
	
	@Inject
	SpContractService spContractService;
	
	
	/**
	 * 계약 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "largeFindListSpContract.do")
	public @ResponseBody FloaterStream largeFindListSpContract(@RequestBody Map param) {
		// 대용량 처리
		return spContractService.largeFindListSpContract(param);
	}

	/**
	 * 계약 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findContract.do")
	public @ResponseBody ResultMap findContract(@RequestBody Map param) {
		ResultMap resultMap = spContractService.findContractDetail(param);
		
		Map cntrInfo = resultMap.getResultData();
		String cntrStsCcd = (String) cntrInfo.get("cntr_sts_ccd");
		if(CntrConst.CNTR_STATUS.SEND.equals(cntrStsCcd)) {
			// 계약서 최초 조회시 계약서 확인 상태로 변경
			spContractService.confirmContractDocument(cntrInfo);
		}
		return resultMap;
	}
	
	/** 
	 * 부속서류 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveAppendix.do")
	public @ResponseBody ResultMap saveAppendix(@RequestBody Map<String,Object> param){
		return spContractService.saveAppendix(param);
	}

	/**
	 * 반려
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "rejectContract.do")
	public @ResponseBody ResultMap rejectContract(@RequestBody Map param) {
		return spContractService.rejectContract(param);
	}

	/**
	 * 계약 이력 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListCntrHistory.do")
	protected @ResponseBody List findListCntrHistory(@RequestBody Map param) {
		return spContractService.findListCntrHistory(param);
	}

	/**
	 * Docusign Envelope 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findDocusignEnvelope.do")
	protected @ResponseBody ResultMap findDocusignEnvelope(@RequestBody Map param) {
		return spContractService.findDocusignEnvelope(param);
	}

	/**
	 * AdobeSign 협력사 서명 url 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "getSpAdobeSignUrlInfo.do")
	public @ResponseBody ResultMap getSpAdobeSignUrlInfo(@RequestBody Map param) {
		return spContractService.getSpAdobeSignUrlInfo(param);
	}

	/**
	 * AdobeSign 진행상태 체크
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "checkAdobeSignStatus.do")
	public @ResponseBody ResultMap checkAdobeSignStatus(@RequestBody Map param) {
		return spContractService.checkAdobeSignStatus(param);
	}
}