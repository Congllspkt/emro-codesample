package smartsuite.app.sp.rfx.nego;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.nego.service.SpNegoService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 협상관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping("**/sp/rfx/nego/")
public class SpNegoController {
	
	@Inject
	SpNegoService spNegoService;
	
	/**
	 * 협상대상 조회 (RFX 기준)
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findNegoTargetList.do")
	public @ResponseBody List findNegoTargetList(@RequestBody Map param){
		return spNegoService.findNegoTargetList(param);
	}
	
	/**
	 * 협상상세 품목 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findNegoDetailInfo.do")
	public @ResponseBody Map findNegoDetailInfo(@RequestBody Map param){
		return spNegoService.findNegoDetailInfo(param);
	}
	
	/**
	 * 협상 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "tempSaveNego.do")
	public @ResponseBody void tempSaveNego(@RequestBody Map param){
		spNegoService.temporarySaveNego(param);
	}
	
	/**
	 * 협상 제출
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "submitNego.do")
	public @ResponseBody void submitNego(@RequestBody Map param){
		spNegoService.submitNego(param);
	}
	
	/**
	 * 협상 포기
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "rejectNego.do")
	public @ResponseBody void rejectNego(@RequestBody Map param){
		spNegoService.rejectNego(param);
	}

}
