package smartsuite.app.sp.rfx.rfi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfi.service.SpRfiService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/sp/rfx/rfi/**/")
public class SpRfiController {

	@Inject
	SpRfiService spRfiService;
	
	/**
	 * RFI 현황 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfiBid.do")
	public @ResponseBody FloaterStream findListRfi(@RequestBody Map param){
		// 대용량 처리
		return spRfiService.findListRfi(param);
	}
	
	/**
	 * RFI 상세 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfiBid.do")
	public @ResponseBody Map findRfi(@RequestBody Map param){
		return spRfiService.findRfi(param);
	}
	
	/**
	 * RFI 견적 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "temporarySaveRfiBid.do")
	public @ResponseBody ResultMap temporarySaveRfiBid(@RequestBody Map param) {
		return spRfiService.temporarySaveRfiBid(param);
	}
	
	/**
	 * RFI 견적 제출
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "submitRfiBid.do")
	public @ResponseBody ResultMap submitRfiBid(@RequestBody Map param) {
		return spRfiService.submitRfiBid(param);
	}
	
	/**
	 * RFI 거부
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "abandonRfiBid.do")
	public @ResponseBody ResultMap abandonRfiBid(@RequestBody Map param) {
		return spRfiService.abandonRfiBid(param);
	}
}