package smartsuite.app.bp.eform.seal;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.eform.seal.service.EFormSealService;
import smartsuite.app.common.shared.ResultMap;

/**
 * 간편서명 관련 처리하는 컨트롤러 Class입니다.
 *
 * @FileName EFormSealController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/eform/**")
public class EFormSealController {

	@Inject
	EFormSealService eFormSealService;

	/**
	 * 인장 목록 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListSeal.do")
	public @ResponseBody List findListSeal(@RequestBody Map param) {
		return eFormSealService.findListSeal(param);
	}

	/**
	 * 인장 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findSeal.do")
	public @ResponseBody Map findSeal(@RequestBody Map param) {
		return eFormSealService.findSeal(param);
	}

	/**
	 * 인장 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveSeal.do")
	public @ResponseBody ResultMap saveSeal(@RequestBody Map param) {
		return eFormSealService.saveSeal(param);
	}

	/**
	 * 인장 갱신
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "updateSeal.do")
	public @ResponseBody ResultMap updateSeal(@RequestBody Map param) {
		return eFormSealService.updateSeal(param);
	}

	/**
	 * 인장 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteSeal.do")
	public @ResponseBody ResultMap deleteSeal(@RequestBody Map param) {
		return eFormSealService.deleteSeal(param);
	}

}