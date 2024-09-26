package smartsuite.app.bp.vendorMaster.vendorUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.vendorMaster.vendorUser.service.VendorUserService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.annotation.AuthCheck;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/vendorMaster/vendorUser/**/")
public class VendorUserController {

    @Inject
    VendorUserService vendorUserService;
    
	/**
	 * 협력사 담당자를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 30
	 * @Method Name : savePicInfo
	 */
    @AuthCheck (authCode = Const.VENDOR_SAVE)
    @RequestMapping(value = "savePicInfo.do")
    public @ResponseBody ResultMap savePicInfo(@RequestBody Map param) {
        return vendorUserService.savePicInfo(param);
    }
    
	/**
	 * 협력사 담당자를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @Method Name : deleteListPic
	 */
    @AuthCheck (authCode = Const.VENDOR_SAVE)
    @RequestMapping(value = "deleteListPic.do")
    public @ResponseBody ResultMap deleteListPic(@RequestBody Map param) {
        return vendorUserService.deleteListPic(param);
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
        return vendorUserService.findListPic(param);
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
    	return vendorUserService.findPicInfo(param);
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
		return vendorUserService.initPassword(param);
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
		return vendorUserService.saveInfoAcctLockYn(param);
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
		return vendorUserService.findListRoleByPicUser(param);
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
		vendorUserService.saveRoleByPicUser(param);
	}

	/**
	 * 협력사 정보 갱신 요청을 위해 담당자를 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7.27
	 * @author cyhwang
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListVendorPicToRenew.do")
	public @ResponseBody ResultMap findListVendorPicToRenew(@RequestBody Map param) {
		return vendorUserService.findListVendorPicToRenew(param);
	}

}