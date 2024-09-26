package smartsuite.app.bp.srm.performance.appeal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.performance.appeal.service.AppealService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "**/bp/**/")
public class AppealController {

	@Inject
	private AppealService appealService;

	/**
	 * 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findAppealNoticeInfo.do")
	public @ResponseBody Map findAppealNoticeInfo(final @RequestBody Map param) {
		return appealService.findAppealNoticeInfo(param);
	}

	/**
	 * 이의제기 통보를 요청한다. (구매사 -> 협력사 이의제기 통보)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/noticeAppeal.do")
	public @ResponseBody ResultMap noticeAppeal(final @RequestBody Map param) {
		return appealService.noticeAppeal(param);
	}

	/**
	 * 이의제기 현황 목록을 조회한다. (협력사 -> 구매사 이의제기 제출 현황)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findAppealList.do")
	public @ResponseBody List findAppealList(final @RequestBody Map param) {
		return appealService.findAppealList(param);
	}

	/**
	 * (정성 평가항목) 이의제기 현황 상세조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 22
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findAppealQualiDetail.do")
	public @ResponseBody Map findAppealQualiDetail(final @RequestBody Map param) {
		return appealService.findAppealQualiDetail(param);
	}

	/**
	 * (정성 평가항목) 이의제기 상세정보 저장을 요청한다.
	 * 반려 or 합의 or 처리완료
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/saveAppealQualiDetail.do")
	public @ResponseBody ResultMap saveAppealQualiDetail(final @RequestBody Map param) {
		return appealService.saveAppealQualiDetail(param);
	}

	/**
	 * 이의제기 통보 임시저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 25
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/tempSaveNoticeAppeal.do")
	public @ResponseBody ResultMap tempSaveNoticeAppeal(final @RequestBody Map param) {
		return appealService.tempSaveNoticeAppeal(param);
	}

	/**
	 * 이의제기를 종료(마감)한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 26
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/closeAppeal.do")
	public @ResponseBody ResultMap closeAppeal(final @RequestBody Map param) {
		return appealService.closeAppeal(param);
	}

	/**
	 * (계산항목) 이의제기 현황 상세조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value = "**/findAppealCalcDetail.do")
	public @ResponseBody Map findAppealCalcDetail(final @RequestBody Map param) {
		return appealService.findAppealCalcDetail(param);
	}

	/**
	 * (계산항목) 이의제기 상세정보 저장을 요청한다.
	 * 반려 or 합의 or 처리완료
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value = "**/saveAppealCalcDetail.do")
	public @ResponseBody ResultMap saveAppealCalcDetail(final @RequestBody Map param) {
		return appealService.saveAppealCalcDetail(param);
	}


	/**
	 * 퍼포먼스 평가 결과 > 평가 결과 상세화면 > 이의제기 종료
	 * 퍼포먼스 평가 이의제기를 종료한다.
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 08. 14
	 * @Method Name : endPfmcEvalAppeal
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="endPfmcEvalAppeal.do")
	public @ResponseBody ResultMap endPfmcEvalAppeal(@RequestBody Map param){
		return appealService.endPfmcEvalAppeal(param);
	}
}
