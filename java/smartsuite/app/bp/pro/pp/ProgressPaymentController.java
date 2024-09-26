package smartsuite.app.bp.pro.pp;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.pp.service.ProgressPaymentService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * 입고 관련 처리를 하는 컨트롤러 Class입니다.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/pro/pp/")
public class ProgressPaymentController {
	
	/**
	 * The payment bill service.
	 */
	@Inject
	ProgressPaymentService progressPaymentService;
	
	/**
	 * 기성요청대상 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the payment bill list
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListProgressPayment
	 */
	@RequestMapping(value = "searchProgressPaymentRequestTarget.do")
	public @ResponseBody FloaterStream searchProgressPaymentRequestTarget(@RequestBody Map param) {
		// 대용량 처리
		return progressPaymentService.searchProgressPaymentRequestTarget(param);
	}
	
	/**
	 * 기성요청목록 조회를 요청한다.
	 * <pre>
	 *  업체가 임시저장한 내역까지 조회가 되도록 함. (단, 상세는 조회 불가)
	 *  업체가 임시저장한 기성이 존재하는 경우 이전의 최종 유효한 기성을 취소할 수 없어야 함.
	 * </pre>
	 *
	 * @param param the param
	 * @return the ar list
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : searchProgressPaymentRequestByPoNo
	 */
	@RequestMapping(value = "searchProgressPaymentRequestByPoNo.do")
	public @ResponseBody FloaterStream searchProgressPaymentRequestByPoNo(@RequestBody Map param) {
		// 대용량 처리
		return progressPaymentService.searchProgressPaymentRequestByPoNo(param);
	}
	
	/**
	 * 기성요청/기성등록 아이디로 선급금여부를 조회한다.
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findAdvancePaymentYnByAppId.do")
	public @ResponseBody String findAdvancePaymentYnByAppId(@RequestBody Map param) {
		return progressPaymentService.findAdvancePaymentYnByAppId(param);
	}
	
	/**
	 * 기성내역정보 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the ar
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findAsnProgressPayment
	 */
	@RequestMapping(value = "findProgressPaymentRequest.do")
	public @ResponseBody Map findProgressPaymentRequest(@RequestBody Map param) {
		return progressPaymentService.findProgressPaymentRequest(param);
	}
	
	/**
	 * 기성등록정보 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the gr
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findGrProgressPayment
	 */
	@RequestMapping(value = "findProgressPayment.do")
	public @ResponseBody Map findProgressPayment(@RequestBody Map param) {
		return progressPaymentService.findProgressPayment(param);
	}
	
	/**
	 * 선급금 승인 기본정보 조회
	 */
	@RequestMapping(value = "findAdvancePaymentGeneralInfomationByAsnUuid.do")
	public @ResponseBody Map findAdvancePaymentGeneralInfomationByAsnUuid(@RequestBody Map param) {
		return progressPaymentService.findAdvancePaymentGeneralInfomationByAsnUuid(param);
	}
	
	/**
	 * 기성승인 임시저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftProgressPayment
	 */
	@RequestMapping(value = "saveDraftProgressPayment.do")
	public @ResponseBody ResultMap saveDraftProgressPayment(@RequestBody Map param) {
		return progressPaymentService.saveDraftProgressPayment(param);
	}
	
	@RequestMapping(value = "submitGrEvalByGr.do")
	public @ResponseBody ResultMap submitGrEvalByGr(@RequestBody Map param) {
		return progressPaymentService.submitGrEvalByGr(param);
	}
	
	/**
	 * 기성승인을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitProgressPayment
	 */
	@RequestMapping(value = "saveSubmitProgressPayment.do")
	public @ResponseBody ResultMap saveSubmitProgressPayment(@RequestBody Map param) {
		return progressPaymentService.saveSubmitProgressPayment(param);
	}
	
	/**
	 * 기성반려 처리한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectAdvancePaymentRequest
	 */
	@RequestMapping(value = "rejectAdvancePaymentRequest.do")
	public @ResponseBody ResultMap rejectAdvancePaymentRequest(@RequestBody Map param) {
		return progressPaymentService.rejectAdvancePaymentRequest(param);
	}
	
	/**
	 * 지불 작성 가능여부 체크
	 *
	 * @param param the param
	 * @return the map
	 * @Method Name : checkCreatablePayment
	 */
	@RequestMapping(value = "checkCreatablePayment.do")
	public @ResponseBody ResultMap checkCreatablePayment(@RequestBody Map param) {
		return progressPaymentService.checkCreatablePayment(param);
	}
	
	/**
	 * 발주 기준 선급금 등록 기본정보 조회
	 */
	@RequestMapping(value = "findAdvancePaymentGeneralInfomationByPoUuid.do")
	public @ResponseBody Map findAdvancePaymentGeneralInfomationByPoUuid(@RequestBody Map param) {
		return progressPaymentService.findAdvancePaymentGeneralInfomationByPoUuid(param);
	}
	
	/**
	 * 선급금 임시 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : saveAdvancePaymentDraft
	 */
	@RequestMapping(value = "saveAdvancePaymentDraft.do")
	public @ResponseBody ResultMap saveAdvancePaymentDraft(@RequestBody Map param) {
		return progressPaymentService.saveAdvancePaymentDraft(param);
	}
	
	/**
	 * 선급금 승인 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : saveAdvancePaymentSubmit
	 */
	@RequestMapping(value = "saveAdvancePaymentSubmit.do")
	public @ResponseBody ResultMap saveAdvancePaymentSubmit(@RequestBody Map param) {
		return progressPaymentService.saveAdvancePaymentSubmit(param);
	}
	
	/**
	 * 기성취소 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : cancelProgressPayment
	 */
	@RequestMapping(value = "cancelProgressPayment.do")
	public @ResponseBody ResultMap cancelProgressPayment(@RequestBody Map param) {
		return progressPaymentService.cancelProgressPayment(param);
	}
	
	/**
	 * 선급금 입고헤더 및 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JuEung Kim
	 * @Date : 2016. 6. 14
	 * @Method Name : findAdvancePayment
	 */
	@RequestMapping(value = "findAdvancePayment.do")
	public @ResponseBody Map findAdvancePayment(@RequestBody Map param) {
		return progressPaymentService.findAdvancePayment(param);
	}
	
	/**
	 * 선급금 등록(임시저장) 삭제
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteAdvancePayment.do")
	public @ResponseBody ResultMap deleteAdvancePayment(@RequestBody Map param) {
		return progressPaymentService.deleteAdvancePayment(param);
	}
	
	/**
	 * 기성 상세(임시저장) 삭제
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteProgressPayment.do")
	public @ResponseBody ResultMap deleteProgressPayment(@RequestBody Map param) {
		return progressPaymentService.deleteProgressPayment(param);
	}
	
	@RequestMapping(value = "evalForceClose.do")
	public @ResponseBody ResultMap evalForceClose(@RequestBody Map param) {
		return progressPaymentService.evalForceClose(param);
	}
	
	/**
	 * 발주기준 기성작성 기본 정보 조회
	 */
	@RequestMapping(value = "findPpDefaultDataByPo.do")
	public @ResponseBody Map findPpDefaultDataByPo(@RequestBody Map param) {
		return progressPaymentService.findPpDefaultDataByPo(param);
	}
}