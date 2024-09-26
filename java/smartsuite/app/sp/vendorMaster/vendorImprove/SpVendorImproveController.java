package smartsuite.app.sp.vendorMaster.vendorImprove;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.vendorMaster.vendorImprove.service.VendorImproveService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorImprove.service.SpVendorImproveService;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "**/sp/vendorMaster/vendorImprove/**/")
public class SpVendorImproveController {

	@Inject
	private SpVendorImproveService spVendorImproveService;

	/**
	 * 개선요청관리 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 05
	 * @Method Name : findListImprove
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findListImprove.do")
	public @ResponseBody List findListImprove(@RequestBody Map param) {
		return spVendorImproveService.findListImprove(param);
	}

	/**
	 * 개선요청관리 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 06
	 * @Method Name : findImproveDetail
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findImproveDetail.do")
	public @ResponseBody Map findImproveDetail(@RequestBody Map param) {
		return spVendorImproveService.findImproveDetail(param);
	}

	/**
	 * 개선요청관리 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 06
	 * @Method Name : saveImproveInfo
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "saveImproveInfo.do")
	public @ResponseBody ResultMap saveImproveInfo(@RequestBody Map param) {
		return spVendorImproveService.saveImproveInfo(param);
	}
}
