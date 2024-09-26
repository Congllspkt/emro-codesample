package smartsuite.app.sp.rfx.sitebriefing;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.sitebriefing.remotemeeting.RemoteMeetingCallService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.sitebriefing.service.SpSiteBriefingService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping(value = "**/sp/rfx/sitebriefing/")
public class SpSiteBriefingController {
	
	@Inject
	SpSiteBriefingService spSiteBriefingService;
	
	@Inject
	RemoteMeetingCallService remoteMeetingCallService;
	
	// 목록조회
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "findListSpFieldIntro.do")
	public @ResponseBody FloaterStream findListSpFieldIntro(@RequestBody Map param) {
		return spSiteBriefingService.findListSpFieldIntro(param);
	}
	
	// 상세조회
	@RequestMapping(value = "findInfoSpFieldIntro.do")
	public @ResponseBody Map findInfoSpFieldIntro(@RequestBody Map param) {
		return spSiteBriefingService.findInfoSpFieldIntro(param);
	}
	
	// 참석 예정 여부 제출
	@RequestMapping(value = "submitSpFieldIntro.do")
	public @ResponseBody ResultMap submitSpFieldIntro(@RequestBody Map param) {
		return spSiteBriefingService.submitSpFieldIntro(param);
	}
	
	@RequestMapping(value = "remoteMeetingConferenceVendorJoin.do")
	public @ResponseBody Map remoteMeetingConferenceVendorJoin(@RequestBody Map param) {
		return remoteMeetingCallService.remoteMeetingConferenceVendorJoin(param);
	}
}
