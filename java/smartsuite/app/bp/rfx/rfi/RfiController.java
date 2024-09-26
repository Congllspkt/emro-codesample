package smartsuite.app.bp.rfx.rfi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.rfi.service.RfiService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/rfi/**/")
public class RfiController {

	@Inject
	RfiService rfiService;
	
	/**
	 * RFI 현황 목록 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfi.do")
	public @ResponseBody FloaterStream findListRfi(@RequestBody Map param) {
		// 대용량 처리
		return rfiService.findListRfi(param);
	}
	
	/**
	 * RFI 제출업체 목록 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfiSubmitVendor.do")
	public @ResponseBody FloaterStream findListRfiSubmitVendor(@RequestBody Map param) {
		// 대용량 처리
		return rfiService.findListRfiSubmitVendor(param);
	}
	
	/**
	 * RFI 다중 건 삭제
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteListRfi.do")
	public @ResponseBody ResultMap deleteListRfi(@RequestBody Map param) {
		return rfiService.deleteListRfi(param);
	}
	
	/**
	 * RFI 다중 건 종료 처리
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "completeListRfi.do")
	public @ResponseBody ResultMap completeListRfi(@RequestBody Map param) {
		return rfiService.completeListRfi(param);
	}
	
	/**
	 * RFI 상세정보 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfi.do")
	public @ResponseBody Map findRfi(@RequestBody Map param) {
		return rfiService.findRfi(param);
	}
	
	@RequestMapping(value = "findRfiDefaultDataByReqItemIds.do")
	public @ResponseBody Map findRfiDefaultDataByReqItemIds(@RequestBody Map param) {
		return rfiService.findRfiDefaultDataByReqItemIds(param);
	}
	
	/**
	 * RFI 저장
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "temporarySaveRfi.do")
	public @ResponseBody ResultMap temporarySaveRfi(@RequestBody Map param) {
		return rfiService.temporarySaveRfi(param);
	}
	
	/**
	 * RFI 요청
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "requestRfi.do")
	public @ResponseBody ResultMap requestRfi(@RequestBody Map param) {
		return rfiService.requestRfi(param);
	}
	
	/**
	 * RFI 삭제
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteRfi.do")
	public @ResponseBody ResultMap deleteRfi(@RequestBody Map param) {
		return rfiService.deleteRfi(param);
	}

	/**
	 * RFI 품목 상태 체크
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "checkPrStatus.do")
	public @ResponseBody ResultMap checkPrStatus(@RequestBody Map param) {
		return rfiService.checkPrStatus(param);
	}
}
