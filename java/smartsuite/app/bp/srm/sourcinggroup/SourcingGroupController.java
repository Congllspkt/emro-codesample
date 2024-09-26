package smartsuite.app.bp.srm.sourcinggroup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.sourcinggroup.service.SourcingGroupService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/sourcinggroup/")
public class SourcingGroupController {
	@Inject
	private transient SourcingGroupService sourcingGroupService;

	/**
	 * 소싱그룹 목록 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "findListSourcingGroup.do")
	public @ResponseBody FloaterStream findListSourcingGroup(final @RequestBody Map param) {
		// 대용량 처리
		return sourcingGroupService.findListSourcingGroup(param);
	}
	/**
	 * 소싱그룹 목록 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveListSourcingGroup.do")
	public @ResponseBody ResultMap saveListSourcingGroup(final @RequestBody Map param) {
		return sourcingGroupService.saveListSourcingGroup(param);
	}

	/**
	 * 소싱그룹 목록 삭제를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "deleteListSourcingGroup.do")
	public @ResponseBody ResultMap deleteListSourcingGroup(final @RequestBody Map param) {
		return sourcingGroupService.deleteListSourcingGroup(param);
	}

	/**
	 * 소싱그룹 상세정보 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findSourcingGroupInfo.do")
	public @ResponseBody Map findSourcingGroupInfo(final @RequestBody Map param) {
		return sourcingGroupService.findSourcingGroupInfo(param);
	}

	/**
	 * 소싱그룹 상세정보 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveSourcingGroupInfo.do")
	public @ResponseBody ResultMap saveSgInfo(final @RequestBody Map param) {
		return sourcingGroupService.saveSourcingGroupInfo(param);
	}

	/**
	 * 소싱그룹 협력사 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListSourcingGroupVendor.do")
	public @ResponseBody FloaterStream findListSourcingGroupVendor(final @RequestBody Map param) {
		// 대용량 처리
		return sourcingGroupService.findListSourcingGroupVendor(param);
	}

	/**
	 * 소싱그룹 협력사 목록 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveListSourcingGroupVendor.do")
	public @ResponseBody ResultMap saveListSourcingGroupVendor(final @RequestBody Map param) {
		return sourcingGroupService.saveListSourcingGroupVendor(param);
	}

	/**
	 * 소싱그룹 담당자 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListSourcingGroupUser.do")
	public @ResponseBody List findListSourcingGroupUser(final @RequestBody Map param) {
		// 대용량 처리
		return sourcingGroupService.findListSourcingGroupUser(param);
	}

	/**
	 * 소싱그룹 담당자 목록 삭제를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "deleteListSourcingGroupUser.do")
	public @ResponseBody ResultMap deleteListSourcingGroupUser(final @RequestBody Map param) {
		return sourcingGroupService.deleteListSourcingGroupUser(param);
	}

	/**
	 * 소싱그룹 담당자 목록 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveListSourcingGroupUser.do")
	public @ResponseBody ResultMap saveListSourcingGroupUser(final @RequestBody Map param) {
		return sourcingGroupService.saveListSourcingGroupUser(param);
	}

	/**
	 * 소싱그룹 품목 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "findListSourcingGroupItem.do")
	public @ResponseBody FloaterStream findListSourcingGroupItem(final @RequestBody Map param) {
		// 대용량 처리
		return sourcingGroupService.findListSourcingGroupItem(param);
	}

	/**
	 * 소싱그룹 품목 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveListSourcingGroupItem.do")
	public @ResponseBody ResultMap saveListSourcingGroupItem(final @RequestBody Map param) {
		return sourcingGroupService.saveListSourcingGroupItem(param);
	}

	/**
	 * 소싱그룹 품목 삭제를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "deleteListSourcingGroupItem.do")
	public @ResponseBody ResultMap deleteListSourcingGroupItem(final @RequestBody Map param) {
		return sourcingGroupService.deleteListSourcingGroupItem(param);
	}

	/**
	 * 품목분류관리 목록 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck(authCode=Const.READ)
	@RequestMapping(value = "findListCate.do")
	public @ResponseBody FloaterStream findListCate(@RequestBody Map param) {
		// 대용량 처리
		return sourcingGroupService.findListCate(param);
	}

	/**
	 * 품목 목록 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@RequestMapping (value = "findListCateItem.do")
	public @ResponseBody List findListCateItem(final @RequestBody Map param) {
		return sourcingGroupService.findListCateItem(param);
	}
	/**
	 * EO 협력사 리스트를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 29
	 */
	@RequestMapping (value = "findListVendorInfoEO.do")
	public @ResponseBody List findListVendorInfoEO(final @RequestBody Map param) {
		return sourcingGroupService.findListVendorInfoEO(param);
	}
}