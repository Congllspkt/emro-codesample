package smartsuite.app.bp.commonEval.evalResult.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.repository.CalculateCalcfactRepository;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional
public class CalculateCalcfactService {

	@Inject
	CalculateCalcfactRepository calcfactRepository;

	/**
	 * 실적항목의 결과를 집계한다.
	 *
	 * @param param the param <br/><br/>
	 * <b>Required</b> <br/>
	 * param.ten_id <br/>
	 * param.eval_req_uuid <br/>
	 * param.eval_subj_req_uuid <br/>
	 * <b>Optional</b> <br/>
	 * param.req_type  (SUBJ / ALL) <br/>
	 * param.eval_subj_res_uuid   <br/>
	 * param.xcept_calcfact_list ( 집계에서 제외할 평가항목 목록 ) <br/>
	 */
	public void calculateCalcfactValResult(Map<String, Object> param) {

		// 정량 실적항목 실측값 집계 (프로시저인 경우, 집계대상을 지정 가능한지?
		this.calculateCalfactVal(param);
		// 무실적 처리
		calcfactRepository.updateNullValueYn(param);
	}

	/**
	 * 실적항목의 결과를 집계한다.
	 *
	 * @param param the param <br/><br/>
	 * <b>Required</b> <br/>
	 * param.ten_id <br/>
	 * param.eval_req_uuid <br/>
	 * param.eval_subj_req_uuid <br/>
	 * <b>Optional</b> <br/>
	 * param.req_type  (SUBJ / ALL) <br/>
	 * param.eval_subj_res_uuid   <br/>
	 * param.xcept_calcfact_list ( 집계에서 제외할 평가항목 목록 ) <br/>
	 */
	public void calculateCalfactVal(Map<String, Object> param) {
		// 집계 대상 계산항목 목록 조회
		List<Map<String, Object>> calcfactList = this.findListEvalSubjCalcfact(param);

		if(calcfactList != null && !calcfactList.isEmpty()) {
			for(Map<String, Object> calcfact : calcfactList) {
				String datCollMethCcd = (String)calcfact.getOrDefault("dat_collmeth_ccd", "");
				// 집계 프로시저 실행
				if("PRCR".equals(datCollMethCcd)) {
					Map<String,Object> procedureMap = this.getCalcfactProcedureSql(calcfact, datCollMethCcd);
					calcfactRepository.updateCalcfactValByProcedure(procedureMap);
				}
				// 집계 테이블
				else if("DAT_TBL".equals(datCollMethCcd)){
					final String[] delKeys = this.setCalcfactDbTableParam(calcfact);
					calcfactRepository.updateCalcfactValByTable(calcfact);
					this.removeMapkey(param, delKeys);
				}
				// 평가결과
				else if("EVAL_SC".equals(datCollMethCcd)){
					calcfactRepository.updateCalcfactValByEvalSc(calcfact);
				}
				// 평가항목결과
				else if("EVAL_FACT_SC".equals(datCollMethCcd)){
					calcfactRepository.updateCalcfactValByEvalfactSc(calcfact);
				}
			}
		}
	}


	/**
	 * 집계 테이블 실행 parameter를 설정한다.
	 *
	 * @author : Meonggyu Park
	 * @param param the param
	 * @return the string[]
	 * @Date : 2017. 8. 3
	 * @Method Name : setCalcfactDbTableParam
	 */
	public String[] setCalcfactDbTableParam(final Map<String, Object> param){
		final List<Map<String, Object>> condColList = calcfactRepository.findListCalcfactCondCol(param);

		for(final Map<String, Object> cond : condColList){
			final String calcDtCol = (String)cond.get("cnd_div_ccd");
			// 조건 컬럼 목록 중 집계기간의 대상 컬럼 설정
			if("DT".equals(calcDtCol)){
				param.put("calc_dt_col", calcDtCol);
			}
		}
		// 조건 컬럼 목록 설정
		param.put("conditions", condColList);

		final String[] addKeys = {"calc_date", "conditions"};
		return addKeys;
	}

	/**
	 * 정량 평가 항목의 계산식 결과를 계산한다.
	 * 정량평가 항목 평가결과 집계
	 * @param param the param
	 */
	public void calculateQuantEvalfactResult(final Map<String, Object> param){
		// 정량평가 항목의 계산식 수행 결과 계산
		this.calculateQuantEvalfactQuantFmlaVal(param);

		/* 스케일 점수 처리 */
		// 평가항목 스케일 해당없음 스케일  저장
		calcfactRepository.updateQuantEvalfactRealApply(param);

		// 평가항목 스케일 절대 스케일 점수(이상-미만) 저장
		calcfactRepository.updateQuantEvalfactScaleAvdUd(param);
		
		// 평가항목 스케일 절대 스케일 점수(초과-이하) 저장
		calcfactRepository.updateQuantEvalfactScaleOvBlw(param);


		/* 평가항목 스케일 결과 NO DATA 처리 */
		// 평가항목 스케일 결과 null 값 처리 기본점수
		calcfactRepository.updateQuantEvalfactScaleScNullvElemSc(param);

		// 평가항목 스케일 결과 null 값 처리 평균 점수
		calcfactRepository.updateQuantEvalfactScaleScNullvAvgSc(param);

		// 평가항목 스케일 결과  null 값 DATA 처리 SG 평균
		//sqlSession.update(NAMESPACE + "updateQuantEfScNoDataSG", param);

		// 평가항목 스케일 결과 null 값 처리 스케일 선택 적용
		calcfactRepository.updateQuantEvalfactScaleScNullvScaleSc(param);

		// 정량항목 가중치 (가/감점) 계산
		calcfactRepository.updateQuantEvalfactScWgt(param);

		// 정량 항목 결과 집계 완료 시 계산항목결과 계산필요여부 'N'로 변경
		calcfactRepository.updateCalcfactCalcRequiredYn(param);
	}


	/**
	 * 정량 평가 항목의 계산식 결과를 계산한다.
	 *
	 * @param param the param
	 */
	public void calculateQuantEvalfactQuantFmlaVal(final Map<String, Object> param){
		String tenId = (String)param.getOrDefault(EvalConst.TEN_ID, "");
		// 정량평가 항목의 실적항목 결과 목록 조회
		final List<Map<String, Object>> quantEvalfactResList = calcfactRepository.findListQuantEvalfactCalcfactRes(param);

		if(quantEvalfactResList != null && !quantEvalfactResList.isEmpty()){
			final List<Map<String, Object>> calcfactValList = Lists.newArrayList(); // 계산값 저장 대상 목록
			String key = "";	// 평가항목결과 key
			String quantFmla = "";	// 계산식 문자열

			for(int i = 0; i < quantEvalfactResList.size(); i++){
				final Map<String, Object> quantEvalfactRes = (Map<String, Object>) quantEvalfactResList.get(i);
				final String evalSubjEvalfactResUuId = (String) quantEvalfactRes.get("eval_subj_evalfact_res_uuid");

				// 평가항목결과 key가 없거나 달라지는 경우
				if(key == null || !key.equals(evalSubjEvalfactResUuId)){
					if(i > 0){	// 최초 데이터가 아닌 경우 계산식 계산
						final BigDecimal quantFmlaVal = this.executeCalculus(quantFmla);
						quantEvalfactResList.get(i-1).put("quant_fmla_val", quantFmlaVal);
						calcfactValList.add(quantEvalfactResList.get(i-1));
					}
					// 평가항목 결과 key, 계산식 문자열 변경
					key = evalSubjEvalfactResUuId;
					quantFmla = (String) quantEvalfactRes.get("quant_fmla");
				}

				// 계산식의 실적항목 아이디를 실적항목의 집계 결과 값으로 치환
				final String calcfactValStr = ((BigDecimal)quantEvalfactRes.getOrDefault("calcfact_val", "")).toString();
				quantFmla = this.replaceAll(quantFmla, "[" + (String)quantEvalfactRes.get("calcfact_uuid") +"]", calcfactValStr);

				if(i == quantEvalfactResList.size() - 1){	// 마지막 데이터인 경우 계산식 계산
					final BigDecimal quantFmlaVal = this.executeCalculus(quantFmla);
					quantEvalfactRes.put("quant_fmla_val", quantFmlaVal);
					calcfactValList.add(quantEvalfactRes);
				}
			}
			// 평가항목 계산식 결과 값 저장
			for(final Map<String, Object> row : calcfactValList){
				calcfactRepository.updateQuantEvalFactQuantFmlaVal(row);
			}
			// 계산식 결과 값 NO DATA 여부 저장
			calcfactRepository.updateQuantEvalfactQuantFmlaValNullv(param);
		}
	}

	public String replaceAll(final String str, final String searchVal, final String newVal){
		String retVal = str;
		final String nVal = (newVal == null) ? "" : newVal;
		if(retVal != null && searchVal != null){
			retVal = retVal.replace(searchVal, nVal);
			if(retVal.indexOf(searchVal) > -1){
				return this.replaceAll(retVal, searchVal, nVal);
			}
		}
		return retVal;
	}

	/**
	 * 실적항목의 procedure 를 반환한다.
	 *
	 * @Date 2024.08.28 - 다중 DB 방식 지원을 위해서 로직 변경
	 * @param param the param
	 * @param datCollMethCcd the dat coll meth ccd
	 * @return calcfact proc sql
	 */
	public Map<String, Object> getCalcfactProcedureSql(final Map<String, Object> param, final String datCollMethCcd){
		Map<String, Object> prcdMap = Maps.newHashMap();


		// 집계 Procedure 파라미터 목록을 조회한다.
		final List<Map<String, Object>> prcrParamList = calcfactRepository.findListProcedureParameter(param);

		String procedureNm = (String)param.getOrDefault("prcr_nm", "");
		procedureNm = this.sqlInjectFilter(procedureNm, true);

		if(procedureNm != null && !procedureNm.isEmpty() && prcrParamList != null && !prcrParamList.isEmpty()){
			prcdMap.put("prcr_nm", procedureNm);

			// 데이터 수집 방법이 프로시저인 경우
			if("PRCR".equals(datCollMethCcd)){
				List<String> valueList = Lists.newArrayList();

				// Procedure 명과 Parameter를 치환한 sql문 생성 PROC_NM('A','B','C')
				for(final Map<String,Object> prfrParam : prcrParamList){
					String parameter = (String)prfrParam.getOrDefault("parameter", "");
					parameter = this.sqlInjectFilter(parameter, false);	// sql injection filtering && uppercase=false

					if(parameter != null && !StringUtils.trimToEmpty(parameter).isEmpty()){
						valueList.add((String)param.getOrDefault(parameter.trim(), ""));
					}
				}

				prcdMap.put("prcr_values", valueList);
			}

			// 마지막 콤마(,) 제거, 우괄호()) 삽입
		}
		return prcdMap;
	}

	/**
	 * 평가대상의 계산항목 목록을 조회한다.
	 *@param param the param
	 *@return list
	 * */
	public List findListEvalSubjCalcfact(Map<String, Object> param) {
		return calcfactRepository.findListEvalSubjCalcfact(param);
	}

	/**
	 * Sql inject filter.
	 * @param str the str
	 * @return the string
	 */
	public String sqlInjectFilter(final String str, final Boolean uppercase){
		String value = "";

		if(str != null){
			final Pattern sCharPattern = Pattern.compile("['\"\\-#()@;=*/+]");
			final Locale locale = Locale.getDefault();

			value = sCharPattern.matcher(str).replaceAll("");
			value = value.replaceAll(" ", "");

			if (value != null && !StringUtils.trimToEmpty(value).isEmpty()) {
				final List<String> keywords = Lists.newArrayList(Arrays.asList(EvalConst.ESCAPE_SQL_STR));
				value = value.toUpperCase(locale);
				for(String keyword : keywords){
					keyword = keyword.toUpperCase(locale);
					if(value.contains(keyword)){
						value = value.replaceAll(keyword, "SQL-INJECT-" + keyword);
					}
				}
				value = uppercase ? value : value.toLowerCase(locale);
			}
		}
		return value;
	}

	/**
	 * Removes the mapkey.
	 *
	 * @param map the map
	 * @param keys the keys
	 */
	private void removeMapkey(final Map<String, Object> map, final String[] keys){
		for(final String key : keys){
			if(map.containsKey(key)){
				map.remove(key);
			}
		}
	}

	/**
	 * 계산식을 수행하고 결과를 반환한다.
	 *
	 * @param str the str
	 * @return the big decimal
	 */
	private BigDecimal executeCalculus(final String str){
		// 계산식 수행 계산 결과 반환
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByName("JavaScript");
		BigDecimal retVal = null;
		// 계산식 구분자 제거
		final String calcStr = str.replaceAll("\\^", "");

		try{
			if(calcStr.isEmpty()){
				retVal = null;
			}else{
				retVal = new BigDecimal(String.valueOf(engine.eval(calcStr)));
			}
		}catch(Exception e){
			// 계산식 수행 오류 발생시 결과값 무실적(null) 처리
			retVal = null;
		}
		return retVal;
	}


}
