package smartsuite.app.bp.commonEval.evalSetup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.commonEval.evalSetup.event.EvalSetupEventPublisher;
import smartsuite.app.bp.commonEval.evalSetup.service.EvalFactorService;
import smartsuite.app.bp.commonEval.evalSetup.service.EvalTmplService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/**/")
public class EvalSetupController {

	@Inject
	EvalFactorService evalFactorService;
    @Inject
    EvalTmplService evalTmplService;

	@Inject
	EvalSetupEventPublisher evalSetupEventPublisher;

	/**
	 * 평가항목 목록 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListEvalFactor.do")
	public @ResponseBody FloaterStream findListEvalFactor(final @RequestBody Map param) {
		// 대용량 처리
		return evalFactorService.findListEvalFactor(param);
	}


	/**
	 * 평가항목 목록 삭제를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListEvalFactor.do")
	public @ResponseBody ResultMap deleteListEvalFactor(final @RequestBody Map param) {
		return evalFactorService.deleteListEvalFactor(param);
	}

	/**
	 * 평가항목 상세정보 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findEvalFactorInfo.do")
	public @ResponseBody ResultMap findEvalFactorInfo(final @RequestBody Map param) {
		return evalFactorService.findEvalFactorInfo(param);
	}

	/**
	 * 평가항목 복사를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCopyEvalFactor.do")
	public @ResponseBody ResultMap saveCopyEvalFactor(final @RequestBody Map param) {
		return evalFactorService.saveCopyEvalFactor(param);
	}

	/**
	 * 평가항목 확정여부 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCnfdYnEvalFactor.do")
	public @ResponseBody ResultMap saveCnfdYnEvalFactor(final @RequestBody Map param) {
		return evalFactorService.saveCnfdYnEvalFactor(param);
	}

	/**
	 * 평가항목 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveEvalFactor.do")
	public @ResponseBody ResultMap saveEvalFactor(final @RequestBody Map param) {
		return evalFactorService.saveEvalFactor(param);
	}

	/**
	 * 평가항목 목록 복사를 요청한다.
	 *
	 * @author : mgPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2016. 5. 3
	 * @Method Name : saveListCopyFactor
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListCopyFactor.do")
	public @ResponseBody ResultMap saveListCopyFactor(final @RequestBody Map param) {
		return evalFactorService.saveListCopyFactor(param);
	}

	/**
	 * 평가항목군 리스트 조회를 요청한다.
	 *
	 * @author : mgPark
	 * @param param the param
	 * @return the eval factor group
	 * @Date : 2016. 5. 3
	 * @Method Name : findEvalFactorGrp
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findEvalFactorGrp.do")
	public @ResponseBody List findEvalFactorGrp(final @RequestBody Map param) {
		return evalFactorService.findEvalFactorGrp(param);
	}

	/**
	 * 평가항목군 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @return the Map
	 * @Date : 2016. 5. 3
	 * @Method Name : saveEvalFactorGrp
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveEvalFactorGrp.do")
	public @ResponseBody ResultMap saveEvalFactorGrp(final @RequestBody Map param) {
		return evalFactorService.saveEvalFactorGrp(param);
	}


	/**
	 * 평가항목군 삭제를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteEvalFactorGrp.do")
	public @ResponseBody ResultMap deleteEvalFactorGrp(final @RequestBody Map param) {
		return evalFactorService.deleteEvalFactorGrp(param);
	}

	/**
	 * 평가템플릿 목록 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/evalTmpl/findListEvalTmpl.do")
	public @ResponseBody FloaterStream findListEvalTmpl(final @RequestBody Map param) {
		// 대용량 처리
		return evalTmplService.findListEvalTmpl(param);
	}

	/**
	 * 평가템플릿 정보 조회를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findEvalTmplInfo.do")
	public @ResponseBody ResultMap findEvalTmplInfo(final @RequestBody Map param) {
		return evalTmplService.findEvalTmplInfo(param);
	}

	/**
	 * 평가템플릿 정보 저장을 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/evalTmpl/saveEvalTmplInfo.do")
	public @ResponseBody ResultMap saveEvalTmplInfo(final @RequestBody Map param) {
		return evalTmplService.saveEvalTmplInfo(param);
	}

	/**
	 * 평가템플릿 평가항목 스케일을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 2
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListEvalTmplEvalFactScale.do")
	public @ResponseBody List findListEvalTmplEvalFactScale(final @RequestBody Map param) {
		return evalTmplService.findListEvalTmplEvalFactScale(param);
	}

	/**
	 * 평가템플릿을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 2
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/evalTmpl/deleteListEvalTmpl.do")
	public @ResponseBody ResultMap deleteListEvalTmpl(final @RequestBody Map param) {
		return evalTmplService.deleteListEvalTmpl(param);
	}

	/**
	 * 평가템플릿 확정상태(확정/취소)를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 7
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/evalTmpl/saveCnfdYnEvalTmpl.do")
	public @ResponseBody ResultMap saveCnfdYnEvalTmpl(final @RequestBody Map param) {
		return evalTmplService.saveCnfdYnEvalTmpl(param);
	}

	/**
	 * 평가템플릿 복사를 요청한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 7
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCopyEvalTmpl.do")
	public @ResponseBody ResultMap saveCopyEvalTmpl(final @RequestBody Map param) {
		return evalTmplService.saveCopyEvalTmpl(param);
	}

	/**
	 * 평가템플릿 평가항목별 평가자 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @Date : 2023. 6. 11
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListFactChrGrpEvaltr.do")
	public @ResponseBody List findListFactChrGrpEvaltr(final @RequestBody Map param) {
		return evalTmplService.findListFactChrGrpEvaltr(param);
	}

	/**
	 * 평가템플릿 평가항목 스케일을 수정한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 15
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveEvalTmplEvalFactScale.do")
	public @ResponseBody ResultMap saveEvalTmplEvalFactScale(final @RequestBody Map param) {
		return evalTmplService.saveEvalTmplEvalFactScale(param);
	}

	/**
	 * 평가템플릿 확정 가능 여부를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 19
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/checkEvalTmplConfirmYn.do")
	public @ResponseBody String checkEvalTmplConfirmYn(final @RequestBody Map param) {
		return evalSetupEventPublisher.checkEvalTmplConfirmYn(param);
	}

	/**
	 * 평가템플릿 Import로 전환을 요청한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveTransformImportEvaltmpl.do")
	public @ResponseBody ResultMap saveTransformImportEvaltmpl(final @RequestBody Map param) {
		return evalTmplService.saveTransformImportEvaltmpl(param);
	}
	/**
	 * 평가템플릿 수정모드로 전환을 요청한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveTransformModModeEvaltmpl.do")
	public @ResponseBody ResultMap saveTransformModModeEvaltmpl(final @RequestBody Map param) {
		return evalTmplService.saveTransformModModeEvaltmpl(param);
	}
	/**
	 * 평가템플릿 수정 전 평가템플릿으로 전환을 요청한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveTransformModCancelEvaltmpl.do")
	public @ResponseBody ResultMap saveTransformModCancelEvaltmpl(final @RequestBody Map param) {
		return evalTmplService.saveTransformModCancelEvaltmpl(param);
	}

	/**
	 * 평가항목에 적용된 평가 업무 유형을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListApplyEvalTask.do")
	public @ResponseBody List findListApplyEvalTask(final @RequestBody Map param) {
		return evalFactorService.findListApplyEvalTask(param);
	}

	/**
	 * 평가항목 팝업을 위한 정보를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findEvalFactorApplyInfo.do")
	public @ResponseBody ResultMap findEvalFactorApplyInfo(final @RequestBody Map param) {
		return evalFactorService.findEvalFactorApplyInfo(param);
	}

	/**
	 * 평가항목 스케일을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 31
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListEvalfactScale.do")
	public @ResponseBody List findListEvalfactScale(final @RequestBody Map param) {
		return evalFactorService.findListEvalfactScale(param);
	}
}
