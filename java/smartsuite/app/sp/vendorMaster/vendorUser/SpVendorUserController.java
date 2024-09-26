package smartsuite.app.sp.vendorMaster.vendorUser;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorUser.service.SpVendorUserService;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/vendorMaster/vendorUser/**/")
public class SpVendorUserController {

    @Inject
    SpVendorUserService spVendorUserService;

	/**
	 * 협력사 담당자를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 5
	 * @Method Name : savePicInfo
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "savePicInfo.do")
	public @ResponseBody ResultMap savePicInfo(@RequestBody Map param) {
		return spVendorUserService.savePicInfo(param);
	}

	/**
	 * 협력사 담당자를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 5
	 * @Method Name : deleteListPic
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping(value = "deleteListPic.do")
	public @ResponseBody ResultMap deleteListPic(@RequestBody Map param) {
		return spVendorUserService.deleteListPic(param);
	}

	/**
	 * 담당자 목록을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 21
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findListPic.do")
	public @ResponseBody List findListPic(@RequestBody Map param) {
		return spVendorUserService.findListPic(param);
	}

	/**
	 * 담당자 상세 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 21
	 * @author cyhwang
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findPicInfo.do")
	public @ResponseBody Map findPicInfo(@RequestBody Map param){
		return spVendorUserService.findPicInfo(param);
	}

	/**
	 * 비밀번호 초기화 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Method Name : saveInfoPwReset
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveInfoPwReset.do")
	public @ResponseBody ResultMap saveInfoPwReset(@RequestBody Map param) {
		return spVendorUserService.initPassword(param);
	}


	/**
	 * 계정 잠김해제를 요청한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveInfoAcctLockYn.do")
	public @ResponseBody ResultMap saveInfoAcctLockYn(@RequestBody Map param) {
		return spVendorUserService.saveInfoAcctLockYn(param);
	}
	/**
	 * 협력사 담당자별 역할을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListRoleByPicUser.do")
	public @ResponseBody List findListRoleByPicUser(@RequestBody Map param) {
		return spVendorUserService.findListRoleByPicUser(param);
	}
	/**
	 * 협력사 담당자별 역할을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveRoleByPicUser.do")
	public @ResponseBody void saveRoleByUser(@RequestBody Map param) {
		spVendorUserService.saveRoleByPicUser(param);
	}

}