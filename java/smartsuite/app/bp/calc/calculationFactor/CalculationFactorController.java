package smartsuite.app.bp.calc.calculationFactor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.calc.calculationFactor.service.CalculationFactorService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "**/bp/**/")
public class CalculationFactorController {

	@Inject
	private CalculationFactorService calculationFactorService;

	/**
	 * 계산항목 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListCalcFactor.do")
	public @ResponseBody FloaterStream findListCalcFactor(final @RequestBody Map param) {
		// 대용량 처리
		return calculationFactorService.findListCalcFactor(param);
	}

	/**
	 * 계산항목 상세정보를 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveCalcFactor.do")
	public @ResponseBody ResultMap saveCalcFactor(final @RequestBody Map param) {
		return calculationFactorService.saveCalcFactor(param);
	}
	
	/**
	 * 계산항목 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping (value = "**/findCalcFactorInfo.do")
	public @ResponseBody Map findCalcFactorInfo(final @RequestBody Map param) {
		return calculationFactorService.findCalcFactorInfo(param);
	}

	/**
	 * 계산항목 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListCalcFactor.do")
	public @ResponseBody ResultMap deleteListCalcFactor(final @RequestBody Map param) {
		return calculationFactorService.deleteListCalcFactor(param);
	}

	/**
	 * 프로시저 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListProcedureCode.do")
	public @ResponseBody FloaterStream findListProcedureCode(final @RequestBody Map param) {
		// 대용량 처리
		return calculationFactorService.findListProcedureCode(param);
	}

	/**
	 * 프로시저 목록을 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListProcedureCode.do")
	public @ResponseBody ResultMap saveListProcedureCode(final @RequestBody Map param) {
		return calculationFactorService.saveListProcedureCode(param);
	}

	/**
	 * 프로시저 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListProcedureCode.do")
	public @ResponseBody ResultMap deleteListProcedureCode(final @RequestBody Map param) {
		return calculationFactorService.deleteListProcedureCode(param);
	}

	/**
	 * 파라미터 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListParameterCode.do")
	public @ResponseBody FloaterStream findListParameterCode(final @RequestBody Map param) {
		// 대용량 처리
		return calculationFactorService.findListParameterCode(param);
	}

	/**
	 * 파라미터 목록을 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/saveListParameterCode.do")
	public @ResponseBody ResultMap saveListParameterCode(final @RequestBody Map param) {
		return calculationFactorService.saveListParameterCode(param);
	}

	/**
	 * 파라미터 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "**/deleteListParameterCode.do")
	public @ResponseBody ResultMap deleteListParameterCode(final @RequestBody Map param) {
		return calculationFactorService.deleteListParameterCode(param);
	}

	/**
	 * 실적 대상 테이블/집계컬럼/조건컬럼 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListDbTblCol.do")
	public @ResponseBody List findListDbTblCol(final @RequestBody Map param) {
		return calculationFactorService.findListDbTblCol(param);
	}

	/**
	 * 실적 대상 테이블/집계컬럼/조건컬럼 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	@AuthCheck(authCode = Const.SAVE)
	@RequestMapping(value="**/saveDbTblColInfo.do")
	public @ResponseBody ResultMap saveDbTblColInfo(final @RequestBody Map param) {
		return calculationFactorService.saveDbTblColInfo(param);
	}

	/**
	 * 실적 대상 테이블 조건목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	@AuthCheck(authCode = Const.READ)
	@RequestMapping(value="**/findListDbTblCondition.do")
	public @ResponseBody Map findListDbTblCondition(final @RequestBody Map param) {
		return calculationFactorService.findListDbTblCondition(param);
	}

}
