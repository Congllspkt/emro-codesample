package smartsuite.app.bp.commonEval.evalSetup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.evalSetup.event.EvalSetupEventPublisher;
import smartsuite.app.bp.commonEval.evalSetup.repository.EvalTmplRepository;
import smartsuite.app.bp.commonEval.evalSetup.service.EvalFactorService;
import smartsuite.app.bp.commonEval.evalResult.service.EvalResultService;
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
public class EvalTmplService {

//	private static final Log LOG = LogFactory.getLog(VendorInfoService.class);

	@Inject
	private EvalTmplRepository evalTmplRepository;
	@Inject
	private EvalFactorService evalFactorService;
	@Inject
	private EvalResultService evalResultService;

	@Inject
	private EvalSetupEventPublisher evalSetupEventPublisher;

	@Inject
	private transient SharedService sharedService;

    private final SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	/**
	 * 평가템플릿 목록을 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public FloaterStream findListEvalTmpl(final Map<String, Object> param) {
		if (param.containsKey("md_cls_cd") && "S".equals(param.get("md_cls_cd"))) {
			// 모듈구분이 S인경우 - SG분석인 경우 대표 평가시트 헤더 지정
			param.put("es_hd_cd", "RAX");
		}
		// 대용량 처리
		return evalTmplRepository.findListEvalTmpl(param);
	}

	/**
	 * 평가템플릿 정보를 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap findEvalTmplInfo(final Map<String, Object> param) {
		final Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
		// 평가 템플릿 정보 조회
		Map evalTmplInfo = evalTmplRepository.findEvalTmplInfo(param);

		// 평가 템플릿 평가항목군 조회
		List evalTmplFactorGrpList = evalTmplRepository.findListEvalTmplEfactgList(param);
		
		// 평가 템플릿 평가항목 조회
		List evalTmplFactorList = evalTmplRepository.findListEvalTmplFactor(param);

		// 평가 템플릿 평가항목 스케일 조회
		List evalTmplFactorScaleList = evalTmplRepository.findListEvalTmplEvalFactScale(param);

		resultMap.put("evalTmplInfo", evalTmplInfo);
		resultMap.put("evalTmplFactorGrpList", evalTmplFactorGrpList);
		resultMap.put("evalTmplFactorList", evalTmplFactorList);
		resultMap.put("evalTmplFactorScaleList", evalTmplFactorScaleList);
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 평가템플릿 정보를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 1
	 */
	public ResultMap saveEvalTmplInfo(final Map<String, Object> evalTmplInfo) {
		final Map<String, Object> resultInfo = new ConcurrentHashMap<String, Object>();

		final Map<String, Object> evalTmplFactInfo = (Map<String, Object>)evalTmplInfo.get("evalTmplFactInfo");
		final List<Map<String, Object>> insertEvalTmplFactList = (List<Map<String, Object>>)evalTmplFactInfo.get("insertEvalTmplFactList");
		final List<Map<String, Object>> updateEvalTmplFactList = (List<Map<String, Object>>)evalTmplFactInfo.get("updateEvalTmplFactList");
		final List<Map<String, Object>> deleteEvalTmplFactList = (List<Map<String, Object>>)evalTmplFactInfo.get("deleteEvalTmplFactList");

		List<Map<String,Object>> insertedList = new ArrayList<Map<String,Object>>();

		String evaltmplUuid = evalTmplInfo.get("evaltmpl_uuid") == null ? "" : evalTmplInfo.get("evaltmpl_uuid").toString();
		if ("".equals(evaltmplUuid)) {
			evaltmplUuid = UUID.randomUUID().toString();
			final String evaltmplCd = sharedService.generateDocumentNumber("ES");

			evalTmplInfo.put("evaltmpl_uuid", evaltmplUuid);
			evalTmplInfo.put("evaltmpl_cd", evaltmplCd);
			evalTmplInfo.put("tmp_yn", "Y");

			// 평가템플릿 신규 저장
			evalTmplRepository.insertEvalTmpl(evalTmplInfo);
		} else {
			evalTmplRepository.updateEvalTmpl(evalTmplInfo);
		}

		// 평가템플릿 평가항목 삭제 - 삭제할 대상을 먼처 처리
		if (deleteEvalTmplFactList != null && !deleteEvalTmplFactList.isEmpty()) {
			for (final Map<String, Object> deleteRow : deleteEvalTmplFactList) {
				deleteRow.put("evaltmpl_uuid", evalTmplInfo.get("evaltmpl_uuid"));
				deleteRow.put("evaltmpl_cd", evalTmplInfo.get("evaltmpl_cd"));

				String etefgUuid = deleteRow.get("etefg_uuid") == null ? "" : deleteRow.get("etefg_uuid").toString();
				if(etefgUuid.equals("ROOT")){ // 평가항목군인 경우
					// 평가템플릿 평가항목군에 매핑된 평가항목 및 평가항목 스케일 삭제
					List<Map<String, Object>> deleteList = evalTmplRepository.findListEvalTmplFactor(deleteRow);
					if (deleteList != null && !deleteList.isEmpty()) {
						for (final Map<String, Object> row : deleteList) {
							evalTmplRepository.deleteEvalTmplEvalFactorScale(row);
							evalTmplRepository.deleteEvalTmplEvalFactorByEfactg(row);
						}
					}
					evalTmplRepository.deleteEvalTmplEfactgByEfactg(deleteRow);
				} else { // 평가항목인 경우
					// 평가시트 평가항목 스케일 삭제
					evalTmplRepository.deleteEvalTmplEvalFactorScale(deleteRow);
					// 평가시트 평가항목 삭제
					evalTmplRepository.deleteEvalTmplEvalFactorByEvalFact(deleteRow);
					
				}
			}
		}
		// 평가템플릿 평가항목 신규 저장
		if (insertEvalTmplFactList != null && !insertEvalTmplFactList.isEmpty()) {
			String efactgUuid = "";
			String evaltmplEfactgUuid = "";
			for (final Map<String, Object> insertRow : insertEvalTmplFactList) {
				// 항목군인지 항목인지 판별
				if(insertRow.get("evalfact_uuid") == null){
					// 항목군인 경우
					efactgUuid = insertRow.get("efactg_uuid").toString();
					evaltmplEfactgUuid = UUID.randomUUID().toString(); // 평가템플릿 평가항목군 uuid 생성
					insertRow.put("evaltmpl_efactg_uuid", evaltmplEfactgUuid);
					insertRow.put("evaltmpl_uuid", evalTmplInfo.get("evaltmpl_uuid")); // 평가템플릿 uuid
					evalTmplRepository.insertEvalTmplEfactg(insertRow); // 평가템플릿 평가항목군 추가

				} else {
					// 항목
					String existEvalTmplEvactgUuid = insertRow.get("evaltmpl_efactg_uuid") == null ? "" : insertRow.get("evaltmpl_efactg_uuid").toString();
					if(!"".equals(existEvalTmplEvactgUuid)){
						// 기존에 추가된 평가템플릿 평가항목군이 있는 경우
						insertRow.put("evaltmpl_efactg_uuid", existEvalTmplEvactgUuid); // 기존의 평가템플릿 평가항목군 uuid
						final String evaltmplEvalfactUuid = UUID.randomUUID().toString(); // 평가템플릿 평가항목 uuid 생성
						insertRow.put("evaltmpl_evalfact_uuid", evaltmplEvalfactUuid);
						insertRow.put("evaltmpl_uuid", evalTmplInfo.get("evaltmpl_uuid")); // 평가템플릿 uuid
						evalTmplRepository.insertEvalTmplEvalfact(insertRow); // 평가템플릿 평가항목 추가

						final List<Map<String, Object>> evalfactScaleList = evalFactorService.findListEvalfactScale(insertRow);
						if (evalfactScaleList != null && !evalfactScaleList.isEmpty()) {
							for (final Map<String, Object> evalScaleRow : evalfactScaleList) {
								final String evaltmplEvalfactScaleUuid = UUID.randomUUID().toString(); // 평가템플릿 평가항목 스케일 uuid 생성
								evalScaleRow.put("evaltmpl_evalfact_scale_uuid", evaltmplEvalfactScaleUuid);
								evalScaleRow.put("evaltmpl_evalfact_uuid", evaltmplEvalfactUuid);

								// 평가템플릿 평가항목 스케일 추가
								evalTmplRepository.insertEvalTmplEvalfactScale(evalScaleRow); // 평가템플릿 평가항목 스케일 추가
							}
						}
					} else {
						String newEfactgUuid = insertRow.get("efactg_uuid") == null ? "" : insertRow.get("efactg_uuid").toString();
						if(!"".equals(newEfactgUuid) && efactgUuid.equals(newEfactgUuid)){
							// 같은 평가항목군
							insertRow.put("evaltmpl_efactg_uuid", evaltmplEfactgUuid); // 평가템플릿 평가항목군 uuid
							final String evaltmplEvalfactUuid = UUID.randomUUID().toString(); // 평가템플릿 평가항목 uuid 생성
							insertRow.put("evaltmpl_evalfact_uuid", evaltmplEvalfactUuid);
							insertRow.put("evaltmpl_uuid", evalTmplInfo.get("evaltmpl_uuid")); // 평가템플릿 uuid
							evalTmplRepository.insertEvalTmplEvalfact(insertRow); // 평가템플릿 평가항목 추가

							final List<Map<String, Object>> evalfactScaleList = evalFactorService.findListEvalfactScale(insertRow);
							if (evalfactScaleList != null && !evalfactScaleList.isEmpty()) {
								for (final Map<String, Object> evalScaleRow : evalfactScaleList) {
									final String evaltmplEvalfactScaleUuid = UUID.randomUUID().toString(); // 평가템플릿 평가항목 스케일 uuid 생성
									evalScaleRow.put("evaltmpl_evalfact_scale_uuid", evaltmplEvalfactScaleUuid);
									evalScaleRow.put("evaltmpl_evalfact_uuid", evaltmplEvalfactUuid);

									// 평가템플릿 평가항목 스케일 추가
									evalTmplRepository.insertEvalTmplEvalfactScale(evalScaleRow); // 평가템플릿 평가항목 스케일 추가
								}
							}
						}
					}
					// 신규추가한 평가항목 리턴 위해 리스트에 추가
					insertedList.add(insertRow);
				}
			}
		}

		// 평가템플릿 평가항목 수정
		if (updateEvalTmplFactList != null && !updateEvalTmplFactList.isEmpty()) {
			for (final Map<String, Object> updateRow : updateEvalTmplFactList) {
				if(updateRow.get("evaltmpl_evalfact_uuid") == null || "".equals(updateRow.get("evaltmpl_evalfact_uuid"))){
					// 평가템플릿 평가항목군 수정
					evalTmplRepository.updateEvalTmplEfactg(updateRow);
	 			} else {
					// 평가템플릿 평가항목 수정
					evalTmplRepository.updateEvalTmplEvalfact(updateRow);
				}
			}
		}

		resultInfo.put("evalTmplInfo", evalTmplInfo);
		resultInfo.put("insertEvalTmplFactList", insertedList);
		return ResultMap.SUCCESS(resultInfo);
	}

	public List findListEvalTmplEvalFactScale(Map param){
		if(param.get("evaltmpl_evalfact_uuid") != null){
			// 평가템플릿 평가항목 스케일 조회
			return evalTmplRepository.findListEvalTmplEvalFactScale(param);
		} else {
			// 평가항목 스케일 조회
			return evalFactorService.findListEvalfactScale(param);
		}
	}


	/**
	 * 평가템플릿 정보를 삭제한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 2
	 */
	public ResultMap deleteListEvalTmpl(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");
		if (deletes != null && !deletes.isEmpty()) {
			// 템플릿 사용여부 조회
			final String useYn = evalResultService.selectEvalTmplUsedYnInEvalResult(param);

			// 평가템플릿 사용여부 조회 (평가시트)
			final String stsInSht = evalSetupEventPublisher.findEvalTmplSts(param);
			// stsInSht
			// E : 유효한 시트가 있어서 템플릿 삭제 불가
			// D : 유효한 시트 없어서 템플릿 삭제 가능
			// U : 평가시트가 존재하지만 D상태라 평가템플릿 D처리
			if("U".equals(stsInSht)){
				this.updateEvalTmplStsByDelete(param);
				return ResultMap.SUCCESS();
			}


			if ("Y".equals(useYn) || "E".equals(stsInSht)) {
				return ResultMap.INVALID();
			}
			for (final Map<String, Object> row : deletes) {
				// 평가템플릿 평가항목 조회
				final List<Map<String, Object>> tmplFactList = evalTmplRepository.findListEvalTmplFactor(row);

				for(final Map<String, Object> tmplFact : tmplFactList){
					// 평가템플릿 평가항목 스케일 삭제
					evalTmplRepository.deleteEvalTmplEvalFactorScale(tmplFact);
				}
			}
			for (final Map<String, Object> row : deletes) {
				// 평가템플릿 평가항목 삭제
				evalTmplRepository.deleteEvalTmplEvalFactor(row);
			}
			for (final Map<String, Object> row : deletes) {
				// 평가템플릿 평가항목군 삭제
				evalTmplRepository.deleteEvalTmplEfactg(row);
			}
			for (final Map<String, Object> row : deletes) {
				// 평가템플릿 삭제
				evalTmplRepository.deleteEvalTmpl(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가템플릿을 삭제한다. (update STS = D)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 8. 25
	 */
	public ResultMap updateEvalTmplStsByDelete(final Map<String, Object> param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		for (final Map<String, Object> row : deletes) {
			// 평가템플릿 평가항목 조회
			final List<Map<String, Object>> tmplFactList = evalTmplRepository.findListEvalTmplFactor(row);

			for(final Map<String, Object> tmplFact : tmplFactList){
				// 평가템플릿 평가항목 스케일 삭제 (STS = D)
				evalTmplRepository.updateEvalTmplEvalFactorScaleStsByDelete(tmplFact);
			}
		}
		for (final Map<String, Object> row : deletes) {
			// 평가템플릿 평가항목 삭제 (STS = D)
			evalTmplRepository.updateEvalTmplEvalFactorStsByDelete(row);
		}
		for (final Map<String, Object> row : deletes) {
			// 평가템플릿 평가항목군 삭제 (STS = D)
			evalTmplRepository.updateEvalTmplEfactgStsByDelete(row);
		}
		for (final Map<String, Object> row : deletes) {
			// 평가템플릿 삭제 (STS = D)
			evalTmplRepository.updateEvalTmplStsByDelete(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 평가템플릿 확정상태(확정/취소)를 저장한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 7
	 */
	public ResultMap saveCnfdYnEvalTmpl(final Map<String, Object> param) {
		String cnfdYn = param.get("cnfd_yn") == null ? "N" : param.get("cnfd_yn").toString();

		if("Y".equals(cnfdYn)){  // 확정 요청
			// 확정 가능 여부 판단
			final String checkYn = evalSetupEventPublisher.checkEvalTmplConfirmYn(param);
			/*if("N".equals(checkYn)){  // 해당 평가템플릿을 사용중인 평가시트가 미확정 상태
				return ResultMap.FAIL();
			}*/
		}else{  // 확정취소 요청
			// 템플릿 사용여부 조회
			final String useYn = evalResultService.selectEvalTmplUsedYnInEvalResult(param);

			// 평가템플릿 사용여부 조회 (평가시트)
			final String useYnInSht = evalSetupEventPublisher.findEvalTmplUseYn(param);
			if ("Y".equals(useYn) || "Y".equals(useYnInSht)) {
				return ResultMap.INVALID();
			}
		}

		// 평가템플릿 확정 여부 update
		this.updateCnfdYnEvalTmpl(param);
		return ResultMap.SUCCESS(param);
	}
	/**
	 * 평가템플릿을 복사한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 7
	 */
	public ResultMap saveCopyEvalTmpl(final Map<String, Object> param) {
		final Map<String, Object> resultData = new ConcurrentHashMap<String, Object>();
		// INSERT
		if (param != null && !param.isEmpty()) {
			final String evalTmplUuid = UUID.randomUUID().toString();
			final String evalTmplCd = sharedService.generateDocumentNumber("ES");

			param.put("evaltmpl_new_uuid", evalTmplUuid);
			param.put("evaltmpl_new_cd", evalTmplCd);

			// 평가템플릿 복사
			evalTmplRepository.insertEvalTmplCopy(param);

			final List<Map<String, Object>> efactgList = evalTmplRepository.findListEvalTmplEfactgList(param);
			if(efactgList != null && !efactgList.isEmpty()){
				for(final Map<String, Object> efactgData : efactgList){
					final String evaltmplEfactgNewUuid = UUID.randomUUID().toString();
					efactgData.put("evaltmpl_efactg_new_uuid", evaltmplEfactgNewUuid);  // 평가템플릿 평가항목군 UUID
					efactgData.put("evaltmpl_new_uuid", evalTmplUuid); // 평가템플릿 UUID

					// 평가템플릿 평가항목군 복사
					evalTmplRepository.insertEvalTmplEfactgCopy(efactgData);
					final List<Map<String, Object>> tmplEvalFactList = evalTmplRepository.findListEvalTmplFactor(efactgData);
					if(tmplEvalFactList != null && !tmplEvalFactList.isEmpty()){
						for(final Map<String, Object> evalFactData : tmplEvalFactList){
							final String evaltmplEvalFactNewUuid = UUID.randomUUID().toString();
							evalFactData.put("evaltmpl_evalfact_new_uuid", evaltmplEvalFactNewUuid);    // 평가템플릿 평가항목 UUID
							evalFactData.put("evaltmpl_efactg_new_uuid", evaltmplEfactgNewUuid);    // 평가템플릿 평가항목군 UUID
							evalFactData.put("evaltmpl_new_uuid", evalTmplUuid); // 평가템플릿 UUID

							// 평가템플릿 평가항목 복사
							evalTmplRepository.insertEvalTmplEvalfactCopy(evalFactData);
							final List<Map<String, Object>> tmplEvalFactScaleList = evalTmplRepository.findListEvalTmplEvalFactScale(evalFactData);
							if(tmplEvalFactScaleList != null && !tmplEvalFactScaleList.isEmpty()) {
								for (final Map<String, Object> evalFactScaleData : tmplEvalFactScaleList) {
									final String evaltmplEvalFactScaleNewUuid = UUID.randomUUID().toString();
									evalFactScaleData.put("evaltmpl_evalfact_scale_new_uuid", evaltmplEvalFactScaleNewUuid); // 평가템플릿 평가항목 스케일 UUID
									evalFactScaleData.put("evaltmpl_evalfact_new_uuid", evaltmplEvalFactNewUuid);    // 평가템플릿 평가항목 UUID

									// 평가템플릿 평가항목 스케일 복사
									evalTmplRepository.insertEvalTmplEvalfactScaleCopy(evalFactScaleData);
								}
							}
						}
					}
				}
			}

			resultData.put("evaltmpl_uuid", param.get("evaltmpl_new_uuid"));
			resultData.put("evaltmpl_cd", param.get("evaltmpl_new_cd"));
		}
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 평가템플릿 평가항목별 평가자를 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @Date : 2023. 6. 11
	 */
	public List findListFactChrGrpEvaltr(Map<String, Object> param) {
		return evalSetupEventPublisher.findListFactChrGrpEvaltr(param);
	}
	/**
	 * 평가항목이 평가 템플릿에서 사용중인지 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 13
	 */
	public String selectEvalFactUsedYnInEvalTmpl(Map<String, Object> param){
		return evalTmplRepository.selectEvalFactUsedYnInEvalTmpl(param);
	}
	/**
	 * 평가항목군이 평가 템플릿에서 사용중인지 조회한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 14
	 */
	public String selectEfactgUsedYnInEvalTmpl(Map<String, Object> param){
		return evalTmplRepository.selectEfactgUsedYnInEvalTmpl(param);
	}

	/**
	 * 평가템플릿 평가항목 스케일을 수정한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 6. 15
	 */
	public ResultMap saveEvalTmplEvalFactScale(Map<String, Object> param){
		final List<Map<String, Object>> scaleList = (List<Map<String, Object>>)param.get("scaleList");
		if (scaleList != null && !scaleList.isEmpty()) {
			for (final Map<String, Object> cztznRow : scaleList) {
				evalTmplRepository.saveEvalTmplEvalFactScale(cztznRow);
			}
		}
		return ResultMap.SUCCESS();
	}
	/**
	 * 평가템플릿 확정 여부를 수정한다.
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 20
	 */
	public ResultMap updateCnfdYnEvalTmpl(Map<String, Object> param){
		evalTmplRepository.updateCnfdYnEvalTmpl(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 평가템플릿을 Import로 전환한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	public ResultMap saveTransformImportEvaltmpl(Map<String, Object> param){
		// 1. 업무 - 평가템플릿 매핑
		evalSetupEventPublisher.saveMappingEvaltmplUuidToWork(param);
		return ResultMap.SUCCESS(param);
	}

	/**
	 * 평가템플릿을 수정모드로 전환한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 7. 24
	 */
	public ResultMap saveTransformModModeEvaltmpl(Map<String, Object> param){
		// 1. 템플릿 사용여부 조회
		final String useYn = evalResultService.selectEvalTmplUsedYnInEvalResult(param);
		// 2. 현재 평가시트 외, 다른 평가시트 에서의 평가템플릿 사용여부 조회
		final String useYnInOtherSht = evalSetupEventPublisher.findEvalTmplUseYn(param);

		if ("Y".equals(useYn) || "Y".equals(useYnInOtherSht)) {  // 평가템플릿 확정취소 불가
			// 2. 평가템플릿 copy
			ResultMap resultMap = this.saveCopyEvalTmpl(param);

			if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
				// 3. 업무 - 평가템플릿 매핑
				param.put("evaltmpl_uuid", resultMap.getResultData().get("evaltmpl_uuid"));
				evalSetupEventPublisher.saveMappingEvaltmplUuidToWork(param);
			}

			return resultMap;
		}else{
			// 2. 평가템플릿 확정취소 update
			param.put("cnfd_yn", "N");
			this.updateCnfdYnEvalTmpl(param);
			return ResultMap.SUCCESS(param);
		}
	}

	/**
	 * 평가템플릿 수정 전 평가템플릿으로 전환한다.
	 *
	 * @author : yjpark
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	public ResultMap saveTransformModCancelEvaltmpl(Map<String, Object> param){
		// 1. 업무 - 평가템플릿 매핑
		param.put("pre_evaltmpl_uuid", null);
		evalSetupEventPublisher.saveMappingOrgnEvaltmplUuidToWork(param);
		return ResultMap.SUCCESS(param);
	}
}