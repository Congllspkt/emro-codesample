package smartsuite.app.bp.srm.performance.pfmcSetup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PfmcEvalshtSetupService;
import smartsuite.app.bp.srm.performance.pfmcSetup.service.PegSetupService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.Map;

/**
 * pfmc setup 컨트롤러 Class입니다.
 *
 * @author yjPark
 * @see
 * @since 2023. 6. 13
 * @FileName PfmcSetupController.java
 * @package smartsuite.app.bp.performance.pfmcSetup
 * @변경이력 : [2023. 6. 13] yjPark 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/**/")
public class PfmcSetupController {

	/** pfmc grp setup service. */
	@Inject
	PegSetupService pegSetupService;

	/** pfmc evalsheet setup service. */
	@Inject
	PfmcEvalshtSetupService pfmcEvalshtSetupService;

	/**
	 * 퍼포먼스평가그룹 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the peg list
	 * @Date : 2023. 6. 1
	 * @Method Name : findListPeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListPeg.do")
	public @ResponseBody FloaterStream findListPeg(@RequestBody Map param) {
		// 대용량 처리
		return pegSetupService.findListPeg(param);
	}
	
	/**
	 * 퍼포먼스 평가시트 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the pfmc evalsht list
	 * @Date : 2023. 7. 24
	 * @Method Name : findListPfmcEvalsht
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListPfmcEvalsht.do")
	public @ResponseBody FloaterStream findListPfmcEvalsht(@RequestBody Map param) {
		// 대용량 처리
		return pfmcEvalshtSetupService.findListPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스평가그룹 목록 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteListPeg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListPeg.do")
	public @ResponseBody ResultMap deleteListPeg(@RequestBody Map param) {
		return pegSetupService.deleteListPeg(param);
	}

	/**
	 * 퍼포먼스 평가시트 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 24
	 * @Method Name : deletePfmcEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deletePfmcEvalsht.do")
	public @ResponseBody ResultMap deletePfmcEvalsht(@RequestBody Map param) {
		return pfmcEvalshtSetupService.deletePfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스평가그룹 저장 전 validation 체크를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 8. 02
	 * @Method Name : checkValidBeforeSavePeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/checkValidBeforeSavePeg.do")
	public @ResponseBody ResultMap checkValidBeforeSavePeg(@RequestBody Map param) {
		return pegSetupService.checkValidBeforeSavePeg(param);
	}

	/**
	 * 퍼포먼스평가그룹 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : savePeg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/savePeg.do")
	public @ResponseBody ResultMap savePeg(@RequestBody Map param) {
		return pegSetupService.savePeg(param);
	}

	/**
	 * 퍼포먼스평가그룹 조회을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : findPeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findPeg.do")
	public @ResponseBody ResultMap findPeg(@RequestBody Map param) {
		return pegSetupService.findPeg(param);
	}

	/**
	 * 퍼포먼스 평가시트 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 5
	 * @Method Name : savePfmcEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/savePfmcEvalsht.do")
	public @ResponseBody ResultMap savePfmcEvalsht(@RequestBody Map param) {
		return pfmcEvalshtSetupService.savePfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트 Import를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 24
	 * @Method Name : saveImportPfmcEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveImportPfmcEvalsht.do")
	public @ResponseBody ResultMap saveImportPfmcEvalsht(@RequestBody Map param) {
		return pfmcEvalshtSetupService.saveImportPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트 버전업을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 21
	 * @Method Name : saveVersionupPfmcEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveVersionupPfmcEvalsht.do")
	public @ResponseBody ResultMap saveVersionupPfmcEvalsht(@RequestBody Map param) {
		return pfmcEvalshtSetupService.saveVersionupPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트/평가템플릿 확정 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 5
	 * @Method Name : saveCnfdYnPfmcEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCnfdYnPfmcEvalsht.do")
	public @ResponseBody ResultMap saveCnfdYnPfmcEvalsht(@RequestBody Map param) {
		return pfmcEvalshtSetupService.saveCnfdYnPfmcEvalsht(param);
	}

	/**
	 * 퍼포먼스 평가시트 평가담당자 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 20
	 * @Method Name : saveListPfmcFactChrGrpEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListPfmcFactChrGrpEvaltr.do")
	public @ResponseBody ResultMap saveListPfmcFactChrGrpEvaltr(@RequestBody Map param) {
		return pfmcEvalshtSetupService.saveListPfmcFactChrGrpEvaltr(param);
	}
}