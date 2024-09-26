package smartsuite.app.bp.onboarding.obdSetup;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.onboarding.obdSetup.service.ObdEvalshtSetupService;
import smartsuite.app.bp.onboarding.obdSetup.service.OegSetupService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

/**
 * obd setup 컨트롤러 Class입니다.
 *
 * @author yjPark
 * @see
 * @since 2023. 5. 30
 * @FileName ObdSetupController.java
 * @package smartsuite.app.bp.vs.obdSetup
 * @변경이력 : [2023. 5. 30] yjPark 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value="**/bp/**/")
public class ObdSetupController {

	/** obd grp setup service. */
	@Inject
	OegSetupService oegSetupService;

	/** obd evalsheet setup service. */
	@Inject
	ObdEvalshtSetupService obdEvalshtSetupService;

	/**
	 * 온보딩평가그룹 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the oeg list
	 * @Date : 2023. 6. 1
	 * @Method Name : findListOeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListOeg.do")
	public @ResponseBody FloaterStream findListOeg(@RequestBody Map param) {
		// 대용량 처리
		return oegSetupService.findListOeg(param);
	}

	/**
	 * 온보딩 평가시트 목록 조회를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the obd evalsht list
	 * @Date : 2023. 7. 24
	 * @Method Name : findListObdEvalsht
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findListObdEvalsht.do")
	public @ResponseBody FloaterStream findListObdEvalsht(@RequestBody Map param) {
		// 대용량 처리
		return obdEvalshtSetupService.findListObdEvalsht(param);
	}

	/**
	 * 온보딩평가그룹 목록 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteListOeg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListOeg.do")
	public @ResponseBody ResultMap deleteListOeg(@RequestBody Map param) {
		return oegSetupService.deleteListOeg(param);
	}

	/**
	 * 온보딩 평가시트 삭제를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 24
	 * @Method Name : deleteObdEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteObdEvalsht.do")
	public @ResponseBody ResultMap deleteObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.deleteObdEvalsht(param);
	}

	/**
	 * 온보딩평가그룹 저장 전 validation 체크를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 8. 02
	 * @Method Name : checkValidBeforeSaveOeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/checkValidBeforeSaveOeg.do")
	public @ResponseBody ResultMap checkValidBeforeSaveOeg(@RequestBody Map param) {
		return oegSetupService.checkValidBeforeSaveOeg(param);
	}

	/**
	 * 온보딩평가그룹 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : saveOeg
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveOeg.do")
	public @ResponseBody ResultMap saveOeg(@RequestBody Map param) {
		return oegSetupService.saveOeg(param);
	}

	/**
	 * 온보딩평가그룹 조회을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 1
	 * @Method Name : findOeg
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findOeg.do")
	public @ResponseBody ResultMap findOeg(@RequestBody Map param) {
		return oegSetupService.findOeg(param);
	}

	/**
	 * 온보딩 평가시트 조회을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 5
	 * @Method Name : findObdEvalsht
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findObdEvalsht.do")
	public @ResponseBody ResultMap findObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.findObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 5
	 * @Method Name : saveObdEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveObdEvalsht.do")
	public @ResponseBody ResultMap saveObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.saveObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트 Import를 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 24
	 * @Method Name : saveImportObdEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveImportObdEvalsht.do")
	public @ResponseBody ResultMap saveImportObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.saveImportObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트 버전업을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 23
	 * @Method Name : saveVersionupObdEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveVersionupObdEvalsht.do")
	public @ResponseBody ResultMap saveVersionupObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.saveVersionupObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트 프로세스 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 6. 8
	 * @Method Name : saveObdEvalshtPrcs
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveObdEvalshtPrcs.do")
	public @ResponseBody ResultMap saveObdEvalshtPrcs(@RequestBody Map param) {
		return obdEvalshtSetupService.saveObdEvalshtPrcs(param);
	}

	/**
	 * 온보딩 평가시트 및 프로세스 전체 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 20
	 * @Method Name : saveAllObdEvalshtAndPrcses
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveAllObdEvalshtAndPrcses.do")
	public @ResponseBody ResultMap saveAllObdEvalshtAndPrcses(@RequestBody Map param) {
		return obdEvalshtSetupService.saveAllObdEvalshtAndPrcses(param);
	}

	/**
	 * 온보딩 평가시트/평가템플릿 확정 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7. 20
	 * @Method Name : saveCnfdYnObdEvalsht
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCnfdYnObdEvalsht.do")
	public @ResponseBody ResultMap saveCnfdYnObdEvalsht(@RequestBody Map param) {
		return obdEvalshtSetupService.saveCnfdYnObdEvalsht(param);
	}

	/**
	 * 온보딩 평가시트 프로세스 평가담당자 저장을 요청한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the Map
	 * @Date : 2023. 7.20
	 * @Method Name : saveListObdFactChrGrpEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListObdFactChrGrpEvaltr.do")
	public @ResponseBody ResultMap saveListObdFactChrGrpEvaltr(@RequestBody Map param) {
		return obdEvalshtSetupService.saveListObdFactChrGrpEvaltr(param);
	}
}