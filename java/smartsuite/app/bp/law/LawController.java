package smartsuite.app.bp.law;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.law.service.LawService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

@Controller
@RequestMapping(value = "**/bp/law/**")
@SuppressWarnings("rawtypes")
public class LawController {
	
	@Inject
	LawService lawService;
	
	/**
	 * 계약검토요청 목록 조회
	 */
	@RequestMapping(value = "largeFindReviewReqList.do")
	public @ResponseBody FloaterStream largeFindReviewReqList(@RequestBody Map param){
		// 대용량 처리
		return lawService.largeFindReviewReqList(param);
	}
	
	/**
	 * 계약검토요청
	 */
	@RequestMapping(value = "requestReview.do")
	public @ResponseBody ResultMap requestReview(@RequestBody Map param){
		return lawService.requestReview(param);
	}
	
	/**
	 * 계약검토요청상세 조회
	 */
	@RequestMapping(value = "findReqReviewDetail.do")
	public @ResponseBody ResultMap findReqReviewDetail(@RequestBody Map param){
		return lawService.findReqReviewDetail(param);
	}
	
	/**
	 * 검토요청 정보 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveReqReview.do")
	public @ResponseBody ResultMap saveReqReview(@RequestBody Map param){
		return lawService.saveReqReview(param);
	}
	
	/**
	 * 계약검토 삭제
	 */
	@RequestMapping(value = "delReqReview.do")
	public @ResponseBody ResultMap delReqReview(@RequestBody Map param){
		return lawService.delReqReview(param);
	}
	
	/**
	 * ROLE_CD에 해당하는 사용자 정보 조회
	 */
	@RequestMapping(value = "findUserByRole.do")
	public @ResponseBody List<Map<String, Object>> findUserByRole(@RequestBody Map param){
		return lawService.findUserByRole(param);
	}
	
	/**
	 * 검토자 지정
	 */
	@RequestMapping(value = "regReviewer.do")
	public @ResponseBody ResultMap regReviewer(@RequestBody Map param){
		return lawService.regReviewer(param);
	}
	
	/**
	 * 검토 정보 저장
	 */
	@RequestMapping(value = "saveReqConfirm.do")
	public @ResponseBody ResultMap saveReqConfirm(@RequestBody Map param){
		return lawService.saveReqConfirm(param);
	}
	
	/**
	 * 검토승인 요청
	 */
	@RequestMapping(value = "reqConfirm.do")
	public @ResponseBody ResultMap reqConfirm(@RequestBody Map param){
		return lawService.reqConfirm(param);
	}
	
	/**
	 * 검토 반려
	 */
	@RequestMapping(value = "rejectReview.do")
	public @ResponseBody ResultMap rejectReview(@RequestBody Map param){
		return lawService.rejectReview(param);
	}
	
	/**
	 * 검토 승인
	 */
	@RequestMapping(value = "confirmReview.do")
	public @ResponseBody ResultMap confirmReview(@RequestBody Map param){
		return lawService.confirmReview(param);
	}
	
	/**
	 * 참조자 계약검토목록 조회
	 */
	@RequestMapping(value = "largeFindReviewReqListByRef.do")
	public @ResponseBody FloaterStream largeFindReviewReqListByRef(@RequestBody Map param){
		// 대용량 처리
		return lawService.largeFindReviewReqListByRef(param);
	}
	
	/**
	 * 재검토 요청 정보 조회
	 */
	@RequestMapping(value = "findReReqReviewDetail.do")
	public @ResponseBody ResultMap findReReqReviewDetail(@RequestBody Map param){
		return lawService.findReReqReviewDetail(param);
	}
	
	/** 
	 * review_req_id에 해당하는 계약건 조회
	 */
	@RequestMapping(value = "getCntrListByRvUuid.do")
	public @ResponseBody List<Map<String, Object>> getCntrListByRvUuid(@RequestBody Map param){
		return lawService.getCntrListByRvUuid(param);
	}
	
	/**
	 * 검토요청 템플릿 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findReqReviewTmpl.do")
	public @ResponseBody ResultMap findReqReviewTmpl(@RequestBody Map param){
		return lawService.findReqReviewTmpl(param);
	}
	
	/**
	 * 검토요청 템플릿 내용 비교 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findReqReviewTmplDiff.do")
	public @ResponseBody ResultMap findReqReviewTmplDiff(@RequestBody Map param){
		return lawService.findReqReviewTmplDiff(param);
	}
	
	/**
	 * 검토요청 템플릿 내용 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveReqReviewTmplCont.do")
	public @ResponseBody ResultMap saveReqReviewTmplCont(@RequestBody Map param){
		return lawService.saveReqReviewTmplCont(param);
	}
	
	/**
	 * 검토요청 템플릿 검토 의견 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveReqReviewTmplOpn.do")
	public @ResponseBody ResultMap saveReqReviewTmplOpn(@RequestBody Map param){
		return lawService.saveReqReviewTmplOpn(param);
	}
	
	/**
	 * 검토 템플릿으로 계약 작성 화면 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findCntrContByRvTmpl.do")
	public @ResponseBody Map findCntrContByRvTmpl(@RequestBody Map param){
		return lawService.findCntrContByRvTmpl(param);
	}

	/**
	 * 검토 완료된 게약 작성
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveTemplateContract.do")
	public @ResponseBody ResultMap saveTemplateContract(@RequestBody Map param) {
		return lawService.saveTemplateContract(param);
	}
}
