package smartsuite.app.bp.pro.gr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.gr.service.GrEvalService;
import smartsuite.app.bp.pro.gr.service.GrService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * 입고 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName GrController.java
 * @package smartsuite.app.bp.pro.gr
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 * @since 2016. 2. 2
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/pro/gr/")
public class GrController {
	
	@Inject
	private GrService grService;
	
	/**
	 * 입고 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListGr.do")
	public @ResponseBody FloaterStream searchGr(@RequestBody Map param) {
		// 대용량 처리
		return grService.searchGr(param);
	}
	
	/**
	 * 입고 상세 조회를 요청한다.
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findGr.do")
	public @ResponseBody Map findGr(@RequestBody Map param) {
		return grService.findGr(param);
	}
	
	/**
	 * 발주품목 아이디로 검수등록(입고) 초기화
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findGrInitialDataByPoItemUuid.do")
	public @ResponseBody Map findGrInitialDataByPoItemUuid(@RequestBody Map param) {
		return grService.findGrInitialDataByPoItemUuid(param);
	}
	
	/**
	 * 납품예정 아이디로 검수등록(입고) 초기화
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findGrInitialDataByAsnUuid.do")
	public @ResponseBody Map findGrInitialDataByAsnUuid(@RequestBody Map param) {
		return grService.findGrInitialDataByAsnUuid(param);
	}
	
	/**
	 * 입고 임시저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftGr
	 */
	@RequestMapping(value = "saveDraftGr.do")
	public @ResponseBody ResultMap saveDraftGr(@RequestBody Map param) {
		return grService.saveDraftGr(param);
	}
	
	@RequestMapping(value = "submitGrEvalByGr.do")
	public @ResponseBody ResultMap submitGrEvalByGr(@RequestBody Map param) {
		return grService.submitGrEvalByGr(param);
	}
	
	/**
	 * 입고 검수요청을 한다.
	 *
	 * @param param the param
	 * @return the map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitGr
	 */
	@RequestMapping(value = "saveSubmitGr.do")
	public @ResponseBody ResultMap saveSubmitGr(@RequestBody Map param) {
		return grService.saveSubmitGr(param);
	}
	
	/**
	 * 검수등록 삭제
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteGr.do")
	public @ResponseBody ResultMap deleteGr(@RequestBody Map param) {
		return grService.deleteGr(param);
	}
	
	/**
	 * 검수 취소
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "cancelGr.do")
	public @ResponseBody ResultMap cancelGr(@RequestBody Map param) {
		return grService.cancelGr(param);
	}
	
	@RequestMapping(value = "evalForceClose.do")
	public @ResponseBody ResultMap evalForceClose(@RequestBody Map param) {
		return grService.evalForceClose(param);
	}
}