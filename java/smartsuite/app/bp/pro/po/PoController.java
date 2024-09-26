package smartsuite.app.bp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.po.service.PoEvalService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
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
@RequestMapping (value = "**/pro/po/")
public class PoController {

	/** The po service. */
	@Inject
	PoService poService;
	
	@Inject
	PoEvalService poEvalService;

	/**
	 * PO 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the po list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPo
	 */
	@RequestMapping (value = "findListPo.do")
	public @ResponseBody FloaterStream findListPo(@RequestBody Map param) {
		// 대용량 처리
		return poService.findListPo(param);
	}

	@RequestMapping (value = "findListCntr.do")
	public @ResponseBody FloaterStream findListCntr(@RequestBody Map param) {
		// 대용량 처리
		return poService.findListCntr(param);
	}

	/**
	 * PO 품목 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the po list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPoItem
	 */
	@RequestMapping (value = "findListPoItem.do")
	public @ResponseBody FloaterStream findListPoItem(@RequestBody Map param) {
		return poService.findListPoItem(param);
	}
	
	/**
	 * 검수등록 가능여부 체크
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "checkGrCreatable.do")
	public @ResponseBody ResultMap checkGrCreatable(@RequestBody Map param){
		return poService.checkGrCreatable(param);
	}

	/**
	 * PO 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the po
	 * @Date : 2016. 2. 2
	 * @Method Name : findPo
	 */
	@RequestMapping (value = "findPo.do")
	public @ResponseBody Map findPo(@RequestBody Map param) {
		return poService.findPo(param);
	}
	
	@RequestMapping(value = "findDefaultDataByPoReqRcpt.do")
	public @ResponseBody Map findDefaultDataByPoReqRcpt(@RequestBody Map param) {
		return poService.findDefaultDataByPoReqRcpt(param);
	}

	/**
	 * PO 변경을 위한 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the po
	 * @Date : 2016. 2. 2
	 * @Method Name : findModifyPo
	 */
	@RequestMapping (value = "findModifyPo.do")
	public @ResponseBody Map findModifyPo(@RequestBody Map param) {
		return poService.findModifyPo(param);
	}

	/**
	 * PO 임시저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftPo
	 */
	@RequestMapping (value = "saveDraftPo.do")
	public @ResponseBody ResultMap saveDraftPo(@RequestBody Map param) {
		return poService.saveDraftPo(param);
	}

	/**
	 * PO 발주 변경요청을 임시저장 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReqDraftPo
	 */
	@RequestMapping (value = "modifyReqDraftPo.do")
	public @ResponseBody ResultMap modifyReqDraftPo(@RequestBody Map param) {
		return poService.modifyReqDraftPo(param);
	}

	/**
	 * PO 발주 변경을 임시저장 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReqDraftPo
	 */
	@RequestMapping (value = "modifyDraftPo.do")
	public @ResponseBody ResultMap modifyDraftPo(@RequestBody Map param) {
		return poService.modifyDraftPo(param);
	}

	/**
	 * PO 발주생성을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitPo
	 */
	@RequestMapping (value = "saveSubmitPo.do")
	public @ResponseBody ResultMap saveSubmitPo(@RequestBody Map param) {
		return poService.saveSubmitPo(param);
	}

	/**
	 * PO 발주변경요청을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReqSubmitPo
	 */
	@RequestMapping (value = "modifyReqSubmitPo.do")
	public @ResponseBody ResultMap modifyReqSubmitPo(@RequestBody Map param) {
		return poService.modifyReqSubmitPo(param);
	}

	/**
	 * PO 발주변경승인을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifySubmitPo
	 */
	@RequestMapping (value = "modifySubmitPo.do")
	public @ResponseBody ResultMap modifySubmitPo(@RequestBody Map param) {
		return poService.modifySubmitPo(param);
	}

	/**
	 * PO 발주 해지승인을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyClosePo
	 */
	@RequestMapping (value = "modifyClosePo.do")
	public @ResponseBody ResultMap modifyClosePo(@RequestBody Map param) {
		return poService.modifyClosePo(param);
	}

	/**
	 * PO 발주 변경요청을 반송 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : modifyReturnPo
	 */
	@RequestMapping (value = "modifyReturnPo.do")
	public @ResponseBody ResultMap modifyReturnPo(@RequestBody Map param) {
		return poService.modifyReturnPo(param);
	}

	/**
	 * PO 발주를 삭제 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : deletePo
	 */
	@RequestMapping (value = "deletePo.do")
	public @ResponseBody ResultMap deletePo(@RequestBody Map param) {
		return poService.deletePo(param);
	}

	/**
	 * 발주품목 발주종료(강제) 처리한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : saveListPoCrcEnd
	 */
	@RequestMapping (value = "saveListPoCrcEnd.do")
	public @ResponseBody ResultMap saveListPoCrcEnd(@RequestBody Map param) {
		return poService.saveListPoCrcEnd(param);
	}

	/**
	 * 발주변경/해지 시 발주품목 상태 체크
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping (value = "checkPoItemState.do")
	public @ResponseBody ResultMap checkPoItemState(@RequestBody Map param) {
		return poService.checkPoItemState(param);
	}
	
	/**
	 * 현재 유효한 발주의 정보를 조회한다.
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findCurrentPo.do")
	public @ResponseBody Map findCurrentPo(@RequestBody Map param) {
		return poService.findCurrentPo(param);
	}
	
	/**
	 * 발주 변경 이력 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findListPoHistory.do")
	public @ResponseBody ResultMap findListPoHistory(@RequestBody Map param) {
		return poService.findListPoHistory(param);
	}
	
	/**
	 * 발주 변경 비교 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findPoCompare.do")
	public @ResponseBody ResultMap findPoCompare(@RequestBody Map param) {
		return poService.findPoCompare(param);
	}
	
	/**
	 * 인지세 금액 구간 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findStamptaxAmtRange.do")
	public @ResponseBody List<Map<String,Object>> findStamptaxAmtRange(@RequestBody Map param) {
		return poService.findStamptaxAmtRange(param);
	}
	
	/**
	 * 문서 출력물을 위한 po정보(단건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoDocumentOutputPo.do")
	public @ResponseBody ResultMap findInfoDocumentOutputPo(@RequestBody Map param){
		return poService.findInfoDocumentOutputPo(param);
	}
	
	/**
	 * 문서 출력물을 위한 po정보(복수건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListDocumentOutputPo.do")
	public @ResponseBody ResultMap findListDocumentOutputPo(@RequestBody Map<String, Object> param){
		return poService.findListDocumentOutputPo(param);
	}
}