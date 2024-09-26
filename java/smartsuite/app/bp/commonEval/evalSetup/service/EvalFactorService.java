package smartsuite.app.bp.commonEval.evalSetup.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.EvalConst;
import smartsuite.app.bp.commonEval.evalResult.service.EvalResultService;
import smartsuite.app.bp.commonEval.evalSetup.repository.EvalFactorRepository;
import smartsuite.app.bp.commonEval.evalSetup.service.EvalTmplService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class EvalFactorService {

//	private static final Log LOG = LogFactory.getLog(VendorInfoService.class);

	@Inject
	private EvalFactorRepository evalFactorRepository;

	@Inject
	private EvalTmplService evalTmplService;

	@Inject
	private EvalResultService evalResultService;

	@Inject
	private transient SharedService sharedService;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());


	/**
	 * 평가항목 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public FloaterStream findListEvalFactor(final Map param) {
		// 대용량 처리
		return evalFactorRepository.findListEvalFactor(param);
	}


	/**
	 * 평가항목 목록을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap deleteListEvalFactor(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		// DELETE
		if (deletes != null && !deletes.isEmpty()) {
			// 평가항목 확정여부 조회
			final String cnfdYn = evalFactorRepository.selectEvalFactorCnfdYnForDelete(param);
			final String tmplFactYn = evalTmplService.selectEvalFactUsedYnInEvalTmpl(param);

			if ("Y".equals(cnfdYn) || "Y".equals(tmplFactYn)) { // 확정 상태 || 템플릿에서 사용하는 항목
				return ResultMap.INVALID();
			} else {
				for (final Map<String, Object> row : deletes) {
					// 평가 항목에 매핑된 모든 스케일 삭제
					evalFactorRepository.deleteEvalfactScale(row);
				}
				for (final Map<String, Object> row : deletes) {
					// 평가항목에 매핑된 모든 계산항목 삭제
					evalFactorRepository.deleteEvalFactCalcFact(row);
				}
				for (final Map<String, Object> row : deletes) {
					// 평가항목 평가 업무 유형 삭제
					evalFactorRepository.deleteEvalFactEvalTask(row);
				}
				for (final Map<String, Object> row : deletes) {
					// 평가항목 삭제
					evalFactorRepository.deleteEvalFactor(row);
				}
				return ResultMap.SUCCESS();
			}
		} else {
			return ResultMap.FAIL();
		}
	}

	/**
	 * 평가항목 상세정보를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap findEvalFactorInfo(final Map<String, Object> param) {
		final Map<String, Object> resultMap = Maps.newHashMap();

		// data : 평가항목 정보 조회
		final Map<String, Object> data = evalFactorRepository.findEvalFactor(param);
		List<Map<String, Object>> quantFactList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> quantScaleList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> qualiScaleList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> evalTaskList = new ArrayList<Map<String, Object>>();

		if (data != null && !data.isEmpty()) {

			if ("QUANT".equals(data.get("evalfact_typ_ccd"))) { // 정량항목
				quantFactList= evalFactorRepository.findListEvalFactRecFact(data);
				if( !"N".equals(data.get("scale_appl_yn"))){
					// quantScaleList : 정량 스케일 목록 조회
					quantScaleList = evalFactorRepository.findListEvalfactScale(data);
				}
			} else if (!"QUALI_SC_INP".equals(data.get("evalfact_typ_ccd"))) { // 정성항목 점수입력이 아닌 경우
				// qualiScaleList : 정성 스케일 목록 조회
				qualiScaleList = evalFactorRepository.findListEvalfactScale(data);
			} else if("QUALI_SC_INP".equals(data.get("evalfact_typ_ccd"))){
				// 점수 입력인 경우 스케일 항목 1개
				qualiScaleList = evalFactorRepository.findListEvalfactScale(data);
				data.put("st_val", qualiScaleList.get(0).get("st_val"));
				data.put("stp_val", qualiScaleList.get(0).get("stp_val"));


			}
			data.put("calc_key", quantFactList);
			
			// 적용 평가 업무 유형 조회
			evalTaskList = evalFactorRepository.findListApplyEvalTask(param);
		}

		resultMap.put("data", data);
		resultMap.put("quantScaleList", quantScaleList);
		resultMap.put("qualiScaleList", qualiScaleList);
		resultMap.put("evalTaskList", evalTaskList);

		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 평가항목을 복사한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap saveCopyEvalFactor(final Map<String, Object> param) {
		final Map<String, Object> copyParam = Maps.newHashMap(param);
		// 복사항목 신규 아이디 채번
		final String evalFactNewId = UUID.randomUUID().toString();
		final String evalFactNewCode = sharedService.generateDocumentNumber("EF");

		copyParam.put("evalfact_new_uuid", evalFactNewId);
		copyParam.put("evalfact_new_cd", evalFactNewCode);

		// 평가항목 복사
		evalFactorRepository.insertEvalFactorCopy(copyParam);

		// 평가항목 업무유형 복사
		evalFactorRepository.insertEvalTaskTypCcdCopy(copyParam);

		// 평가항목에 매핑된 평가항목 스케일목록 조회
		final List<Map<String, Object>> scaleList = evalFactorRepository. selectEvalfactScaleByEvalfactCopy(copyParam);
		if (scaleList != null && !scaleList.isEmpty()) {
			for (final Map<String, Object> scaleData : scaleList) {
				final String evalfactScaleUuid = UUID.randomUUID().toString();
				final String scaleCd = sharedService.generateDocumentNumber("SC");
				scaleData.put("evalfact_scale_uuid", evalfactScaleUuid);
				scaleData.put("scale_cd", scaleCd);
				// 평가항목 스케일 복사
				evalFactorRepository.insertEvalfactScale(scaleData);
			}
		}
		final Map<String, Object> resultData = Maps.newHashMap();
		resultData.put("evalfact_uuid", copyParam.get("evalfact_new_uuid"));
		resultData.put("evalfact_cd", copyParam.get("evalfact_new_cd"));
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 평가항목 확정여부를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 07
	 */
	public ResultMap saveCnfdYnEvalFactor(final Map<String, Object> param) {
		final Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();

		if ("N".equals((String)param.get("cnfd_yn"))) { // 확정 취소인 경우 평가항목 사용여부 확인.
			// EVALTMPL_EVALFACT(평가템플릿 평가항목), REQ_EVALFACT_RES (평가항목 결과) 테이블 체크
			String tmplUsedYn = evalTmplService.selectEvalFactUsedYnInEvalTmpl(param);
			String resultUsedYn = evalResultService.selectEvalFactorUsedYnInEvalResult(param);
			if("Y".equals(tmplUsedYn) || "Y".equals(resultUsedYn)){
				return ResultMap.INVALID();
			}
		}
		evalFactorRepository.updateCnfdYnEvalFactor(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 평가항목 상세정보를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap saveEvalFactor(final Map<String, Object> param) {
		final Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
		// 평가항목 정보 저장
		final Map<String, Object> saveParam = (Map<String, Object>)param.get("saveParam");
		if (!saveParam.containsKey("evalfact_uuid") || saveParam.get("evalfact_uuid") == null) {
			// 신규
			final String evalFactId = UUID.randomUUID().toString();
			final String evalFactCode = sharedService.generateDocumentNumber("EF");

			saveParam.put("evalfact_uuid", evalFactId);
			saveParam.put("evalfact_cd", evalFactCode);
			evalFactorRepository.insertEvalFactor(saveParam);

			// 평가업무 유형 공통코드 저장
			final List<Map<String, Object>> evalTaskList = (List<Map<String, Object>>)param.get("saveListEvalTask");
			for (final Map<String, Object> evalTask : evalTaskList) {
				if("Y".equals(evalTask.get("reg_yn"))){
					evalTask.put("evalfact_uuid", evalFactId);
					evalFactorRepository.insertEvalTaskTypCcd(evalTask);
				}
			}

		} else {
			// 수정
			evalFactorRepository.updateEvalFactor(saveParam);

			// 평가 업무 유형 초기화
			evalFactorRepository.deleteEvalTaskByEvalFact(saveParam);
			
			// 평가 업무 유형 리스트 저장
			final List<Map<String, Object>> evalTaskList = (List<Map<String, Object>>)param.get("saveListEvalTask");
			for (final Map<String, Object> evalTask : evalTaskList) {
				if("Y".equals(evalTask.get("reg_yn"))){
					evalTask.put("evalfact_uuid", saveParam.get("evalfact_uuid"));
					evalFactorRepository.insertEvalTaskTypCcd(evalTask);
				}
			}
		}

		// 매핑정보 초기화
		// 평가 항목에 매핑된 모든 스케일 삭제
		evalFactorRepository.deleteEvalfactScale(saveParam);
		// 평가항목에 매핑된 모든 계산항목 삭제
		evalFactorRepository.deleteEvalFactCalcFact(saveParam);

		// 정성/정량 스케일 항목 저장
		final String evalfactTyp = (String) saveParam.get("evalfact_typ_ccd");	// 정량/정성구분
		final Boolean isQuantFact = "QUANT".equals(evalfactTyp);	// 정량항목 여부
		if ("QUALI_SC_INP".equals(evalfactTyp)) {	// 정성 점수입력
			// 정성평가 직접입력항목인 경우 스케일항목 단일 건 생성
			final String evalfactScaleUuid = UUID.randomUUID().toString();
			final String scaleCd = sharedService.generateDocumentNumber("SC");
			saveParam.put("evalfact_scale_uuid", evalfactScaleUuid);
			saveParam.put("scale_cd", scaleCd);

			final String addCndCcd = saveParam.get("add_cnd_ccd").toString();

			if("NA".equals(addCndCcd)){ // 추가 조건이 일반인 경우 시작값 0, 멈춤값 100 세팅
				saveParam.put("st_val", 0);
				saveParam.put("stp_val", 100);
			}

			evalFactorRepository.insertEvalfactScale(saveParam);
		}else{
			// 정량 정성 스케일 항목 저장
			final List<Map<String, Object>> scaleList = (List<Map<String, Object>>)param.get("saveListScale");
			for (final Map<String, Object> scale : scaleList) {

				final String evalfactScaleUuid = UUID.randomUUID().toString();
				final String scaleCd = sharedService.generateDocumentNumber("SC");
				scale.put("evalfact_scale_uuid", evalfactScaleUuid);
				scale.put("scale_cd", scaleCd);

				scale.put("evalfact_uuid", saveParam.get("evalfact_uuid"));

				// 정성평가인 경우는 스케일 적용여부 Y로 고정
				scale.put("scale_appl_yn", isQuantFact ? saveParam.get("scale_appl_yn") : "Y");

				evalFactorRepository.insertEvalfactScale(scale);
			}

			// 정량 계산식 계산항목 저장
			final List<Map<String, Object>> calcList = (List<Map<String, Object>>)param.get("saveListCalc");
			if(isQuantFact && calcList !=null && !calcList.isEmpty()) {
				for (final Map<String, Object> calcData : calcList) {
					calcData.put("evalfact_uuid", saveParam.get("evalfact_uuid"));
					calcData.put("evalfact_cd", saveParam.get("evalfact_cd"));
					// 계산식에 포함된 실적항목 저장
					evalFactorRepository.mergeEvalFactRecFact(calcData);
				}
			}
		}

		// 확정처리
		if ("Y".equals(saveParam.get("cnfd_yn"))) {
			evalFactorRepository.updateCnfdYnEvalFactor(saveParam);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가항목 목록을 복사한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap saveListCopyFactor(final Map<String, Object> param) {
		final Map<String, Object> saveParam = (Map<String, Object>)param.get("saveParam");
		final List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");

		// INSERT
		// 평가 항목 복사
		if (inserts != null && !inserts.isEmpty()) {
			for (final Map<String, Object> row : inserts) {
				final String evalFactId = UUID.randomUUID().toString();
				final String evalFactCode = sharedService.generateDocumentNumber("EF");
				String evalfactTypCcd = (String)row.getOrDefault("evalfact_typ_ccd", "QUALI");

				row.put("copy_nm", saveParam.get("copy_nm"));
				row.put("eval_task_typ_ccds", saveParam.get("eval_task_typ_ccds"));

				row.put("evalfact_new_uuid", evalFactId);
				row.put("evalfact_new_cd", evalFactCode);

				// 평가항목 복사
				evalFactorRepository.insertEvalFactorCopy(row);
				// 평가항목유형이 정량항목인 경우, 정량항목 하위 계산항목 정보 복사
				if(evalfactTypCcd.equals(EvalConst.QUANT)) {
					evalFactorRepository.insertEvalFactCalcFactCopy(row);
				}

				// 평가항목 업무유형 복사
				row.put("eval_task_typ_ccd", saveParam.get("eval_task_typ_ccd"));
				evalFactorRepository.insertEvalTaskTypCcdCopy(row);

				// 평가항목에 매핑된 평가항목 스케일목록 조회
				final List<Map<String, Object>> scaleList = evalFactorRepository. selectEvalfactScaleByEvalfactCopy(row);

				if (scaleList != null && !scaleList.isEmpty()) {
					for (final Map<String, Object> scaleData : scaleList) {
						final String evalfactScaleUuid = UUID.randomUUID().toString();
						final String scaleCd = sharedService.generateDocumentNumber("SC");
						scaleData.put("evalfact_scale_uuid", evalfactScaleUuid);
						scaleData.put("scale_cd", scaleCd);
						// 평가항목 스케일 복사
						evalFactorRepository.insertEvalfactScale(scaleData);
					}
				}
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 평가항목군을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public List findEvalFactorGrp(final Map<String, Object> param) {
		return evalFactorRepository.findEvalFactorGrp(param);
	}

	/**
	 * 평가항목군을 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap saveEvalFactorGrp(final Map<String, Object> param) {
		List<Map> insertList = (List<Map>)param.get("insertList");
		List<Map> updateList= (List<Map>)param.get("updateList");

		for(final Map insertRow : insertList){
			// 중복된 평가항목군 명이 있는지 조회
			String efactgNmDupYn = evalFactorRepository.checkEvalFactorGrpNm(insertRow);
			if("Y".equals(efactgNmDupYn)){
				return ResultMap.DUPLICATED();
			}

			// 평가항목군 신규 저장
			final String efactgUuid = UUID.randomUUID().toString();
			final String efactgCd = sharedService.generateDocumentNumber("EF");

			insertRow.put("efactg_uuid", efactgUuid);
			insertRow.put("efactg_cd", efactgCd);
			evalFactorRepository.insertEfactg(insertRow);

		}

		for(final Map updateRow : updateList){
			// 중복된 평가항목군 명이 있는지 조회
			String efactgNmDupYn = evalFactorRepository.checkEvalFactorGrpNm(updateRow);
			if("Y".equals(efactgNmDupYn)){
				return ResultMap.DUPLICATED();
			}

			// 수정
			evalFactorRepository.updateEfactg(updateRow);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 평가항목군을 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap deleteEvalFactorGrp(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");
		// 평가 항목군 삭제 가능 여부 판별 (평가 템플릿 사용여부)
		final String tmplUseYn = evalTmplService.selectEfactgUsedYnInEvalTmpl(param);
		if ("Y".equals(tmplUseYn)) { // 템플릿에서 사용하는 항목
			return ResultMap.INVALID();
		} else {
			for (final Map<String, Object> row : deletes) {
				// 평가 항목군 삭제
				evalFactorRepository.deleteEvalFactorGrp(row);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 평가항목 스케일을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 13
	 */
	public List findListEvalfactScale(Map<String, Object> param){
		return evalFactorRepository.findListEvalfactScale(param);
	}

	/**
	 * 평가항목에 적용된 평가 업무 유형을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	public List findListApplyEvalTask(Map<String, Object> param){
		return evalFactorRepository.findListApplyEvalTask(param);
	}

	/**
	 * 평가항목 팝업을 위한 정보를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	public ResultMap findEvalFactorApplyInfo(Map<String, Object> param){
		final Map<String, Object> resultData = new HashMap<>();

		// 평가항목이 적용된 평가 업무, 평가템플릿, 평가항목군 조회
		final List<Map<String, Object>> evalFactorApplyList =  evalFactorRepository.findEvalFactorApplyInfo(param);
		resultData.put("evalFactorApplyList", evalFactorApplyList);

		return ResultMap.SUCCESS(resultData);
	}
}