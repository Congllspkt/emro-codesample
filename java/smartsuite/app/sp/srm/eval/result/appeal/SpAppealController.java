package smartsuite.app.sp.srm.eval.result.appeal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.srm.eval.result.appeal.service.SpAppealService;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "**/sp/**/")
public class SpAppealController {

	@Inject
	private SpAppealService spAppealService;

	/**
	 * 이의제기 현황 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 17
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findAppealList.do")
	public @ResponseBody List findAppealList(final @RequestBody Map param) {
		return spAppealService.findAppealList(param);
	}

	/**
	 * 평가결과 정성 평가항목/계산항목 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 20
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListPeSubjFactorResult.do")
	public @ResponseBody Map findListPeSubjFactorResult(final @RequestBody Map param) {
		return spAppealService.findListPeSubjFactorResult(param);
	}

	/**
	 * 정성 평가항목 이의제기를 요청한다. (협력사 -> 구매사 이의제기)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 22
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/saveAppealQualiEvalfact.do")
	public @ResponseBody ResultMap saveAppealQualiEvalfact(final @RequestBody Map param) {
		return spAppealService.saveAppealQualiEvalfact(param);
	}

	/**
	 * 이의제기 제출 현황 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findAppealSubmList.do")
	public @ResponseBody List findAppealSubmList(final @RequestBody Map param) {
		return spAppealService.findAppealSubmList(param);
	}

	/**
	 * 이의제기를 제출을 취소한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/cancelAppealList.do")
	public @ResponseBody ResultMap cancelAppealList(final @RequestBody Map param) {
		return spAppealService.cancelAppealList(param);
	}

	/**
	 * 협력사 관리 그룹 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListVmg.do")
	public @ResponseBody List findListVmg(final @RequestBody Map param) {
		return spAppealService.findListVmg(param);
	}

	/**
	 * 계산항목 이의제기를 요청한다. (협력사 -> 구매사 이의제기)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 26
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/saveAppealCalcfact.do")
	public @ResponseBody ResultMap saveAppealCalcfact(final @RequestBody Map param) {
		return spAppealService.saveAppealCalcfact(param);
	}

	/**
	 * (정성 평가항목) 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findAppealQualiDetail.do")
	public @ResponseBody Map findAppealQualiDetail(final @RequestBody Map param) {
		return spAppealService.findAppealQualiDetail(param);
	}

	/**
	 * (계산항목) 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findAppealCalcDetail.do")
	public @ResponseBody Map findAppealCalcDetail(final @RequestBody Map param) {
		return spAppealService.findAppealCalcDetail(param);
	}

	/**
	 * (계산항목) 이의제기(임시저장 건)를 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/deleteAppealCalcfact.do")
	public @ResponseBody ResultMap deleteAppealCalcfact(final @RequestBody Map param) {
		return spAppealService.deleteAppealCalcfact(param);
	}

	/**
	 * (정성 평가항목) 이의제기(임시저장 건)를 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/deleteAppealQualiEvalfact.do")
	public @ResponseBody ResultMap deleteAppealQualiEvalfact(final @RequestBody Map param) {
		return spAppealService.deleteAppealQualiEvalfact(param);
	}

	/**
	 * 퍼포먼스 평가그룹 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 17
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListPegByPe.do")
	public @ResponseBody List findListPegByPe(final @RequestBody Map param) {
		return spAppealService.findListPegByPe(param);
	}
}
