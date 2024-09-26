package smartsuite.app.sp.pro.pp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.pp.service.SpProgressPaymentService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * SpProgressPayment 관련 처리를 하는 컨트롤러 Class입니다.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping (value = "**/sp/pro/pp/**/")
public class SpProgressPaymentController {

	/** The sp payment bill service. */
	@Inject
	SpProgressPaymentService spProgressPaymentService;
	
	/**
	 * 기성요청 가능여부 체크
	 *
	 * @param param the param
	 * @return the map
	 * @Method Name : validateProgressPaymentRequestStatus
	 */
	@RequestMapping (value = "validateProgressPaymentRequestStatus.do")
	public @ResponseBody ResultMap validateProgressPaymentRequestStatus(@RequestBody Map param) {
		return spProgressPaymentService.validateProgressPaymentRequestStatus(param);
	}
	
	/**
	 * 발주기준 기성현황 목록 조회를 요청한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 7
	 * @Method Name : findListPoForProgressPaymentAsn
	 */
	@RequestMapping (value = "searchPoForProgressPayment.do")
	public @ResponseBody FloaterStream searchPoForProgressPayment(@RequestBody Map param) {
		// 대용량 처리
		return spProgressPaymentService.searchPoForProgressPayment(param);
	}
	
	/**
	 * 기성현황 목록 조회를 요청한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 7
	 * @Method Name : findListPamentBillDt
	 */
	@RequestMapping (value = "searchProgressPaymentRequest.do")
	public @ResponseBody FloaterStream searchProgressPaymentRequest(@RequestBody Map param) {
		// 대용량 처리
		return spProgressPaymentService.searchProgressPaymentRequest(param);
	}
	
	/**
	 * 기성요청을 위한 발주정보 조회를 요청한다.
	 */
	@RequestMapping (value = "initProgressPaymentRequestInfoByPoUuid.do")
	public @ResponseBody Map initProgressPaymentRequestInfoByPoUuid(@RequestBody Map param) {
		return spProgressPaymentService.initProgressPaymentRequestInfoByPoUuid(param);
	}
	
	/**
	 * 기성요청 임시저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 9
	 * @Method Name : saveDraftAsn
	 */
	@RequestMapping (value = "saveProgressPaymentDraft.do")
	public @ResponseBody ResultMap saveProgressPaymentDraft(@RequestBody Map param) {
		return spProgressPaymentService.saveProgressPaymentDraft(param);
	}
	
	/**
	 * 기성요청 제출
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 9
	 * @Method Name : submitProgressPayment
	 */
	@RequestMapping (value = "submitProgressPayment.do")
	public @ResponseBody ResultMap submitProgressPayment(@RequestBody Map param) {
		return spProgressPaymentService.submitProgressPayment(param);
	}
	
	/**
	 * 기성요청 삭제를 요청한다.
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "deleteProgressPaymentRequest.do")
	public @ResponseBody ResultMap deleteProgressPaymentRequest(@RequestBody Map param) {
		return spProgressPaymentService.deleteProgressPaymentRequest(param);
	}
	
	/**
	 * 기성 헤더 및 상세 조회를 요청한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 6. 9
	 * @Method Name : findSpAsnHdDt
	 */
	@RequestMapping (value = "findProgressPaymentRequestInfo.do")
	public @ResponseBody Map findProgressPaymentRequestInfo(@RequestBody Map param) {
		return spProgressPaymentService.findProgressPaymentRequestInfo(param);
	}

	/**
	 * 기성요청 아이디로 선급금여부를 조회한다.
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "findAdvancePaymentYnByAsnUuid.do")
	public @ResponseBody String findAdvancePaymentYnByAsnUuid(@RequestBody Map param) {
		return spProgressPaymentService.findAdvancePaymentYnByAsnUuid(param);
	}
}