package smartsuite.app.bp.srm.actualresult;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.actualresult.service.ActualResultService;
import smartsuite.app.common.shared.Const;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping(value="**/bp/**")
public class ActualResultController {

	@Inject
	private ActualResultService actualResultService;

	/**
	 * 실적관리 목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findListVdAcres.do")
	public @ResponseBody FloaterStream findListVdAcres(final @RequestBody Map param) {
		// 대용량 처리
		return actualResultService.findListVdAcres(param);
	}

	/**
	 * 협력사 소싱그룹 목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findListSgVendorInfo.do")
	public @ResponseBody FloaterStream findListSgVendorInfo(final @RequestBody Map param) {
		// 대용량 처리
		return actualResultService.findListSgVendorInfo(param);
	}

	/**
	 * 실적관리 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/saveVdAcresList.do")
	public @ResponseBody Map saveVdAcresList(final @RequestBody Map param) {
		return actualResultService.saveVdAcresList(param);
	}

	/**
	 * 실적관리 삭제를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 2
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/deleteVdAcresList.do")
	public @ResponseBody Map deleteVdAcresList(final @RequestBody Map param) {
		return actualResultService.deleteVdAcresList(param);
	}

	/**
	 * 실적관리 excel upload시 소싱그룹명, 협력사명을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 2
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findSgVdNm.do")
	public @ResponseBody Map findSgVdNm(final @RequestBody Map param) {
		return actualResultService.findSgVdNm(param);
	}
}
