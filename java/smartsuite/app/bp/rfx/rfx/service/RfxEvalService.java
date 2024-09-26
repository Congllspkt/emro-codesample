package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEvalEventPublisher;
import smartsuite.app.bp.rfx.rfx.repository.RfxEvalRepository;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RfxEval 관련 처리하는 서비스 Class입니다.
 *
 * @FileName RfxEvalService.java
 * @package smartsuite.app.bp.rfx.rfx
 * @Since 2016. 5. 30
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxEvalService {
	
	@Inject
	RfxEvalRepository rfxEvalRepository;
	
	@Inject
	RfxEvalEventPublisher rfxEvalEventPublisher;
	
	@Inject
	RfxBidService rfxBidService;
	
	@Inject
	RfxBidEvaltrService rfxBidEvaltrService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	/**
	 * RFP 종합평가 1차수 신규 생성 시에만 비가격평가 항목 초기화 수행
	 * @param rfxData
	 */
	public void initializedRfxNpeFact(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		int rfx_rnd = Integer.parseInt(rfxData.get("rfx_rnd").toString());
		if(rfx_rnd > 1) {
			return;
		}
		if(!"RFP".equals(rfxData.get("rfx_typ_ccd"))) {
			return;
		}
		
		String npefactUuid = UUID.randomUUID().toString();
		Map npeFactSetup = Maps.newHashMap();
		npeFactSetup.put("npefact_uuid", npefactUuid);
		npeFactSetup.put("rfx_uuid", rfxData.get("rfx_uuid"));
		npeFactSetup.put("cnfd_yn", "N");
		rfxEvalRepository.insertRfxNpeFactSetup(npeFactSetup);
	}
	
	public Map findRfxNpeFactSetup(Map param) {
		return rfxEvalRepository.findRfxNpeFactSetup(param);
	}
	
	public Map findRfxNpeFact(Map param) {
		Map result = Maps.newHashMap();
		result.put("rfxData", rfxEvalRepository.findRfx(param));
		result.put("npeFactSetup", rfxEvalRepository.findRfxNpeFactSetup(param));
		result.put("npeFactEvaltr", rfxEvalRepository.findListRfxNpeFactEvaltr(param));
		return result;
	}
	
	public List findListEvalTmpl(Map param) {
		return rfxEvalRepository.findListEvalTmpl(param);
	}
	
	public List findListPreNonPriRfxDetail(Map param) {
		return rfxEvalRepository.findListPreNonPriRfxDetail(param);
	}
	
	public ResultMap clearRfxNpeFact(Map rfxData) {
		if(rfxData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		rfxEvalRepository.deleteRfxNpeFactEvaltrByRfx(rfxData);
		rfxEvalRepository.clearRfxNpeFactSetupByRfx(rfxData);
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap deleteRfxNpeFact(Map rfxData) {
		if(rfxData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		rfxEvalRepository.deleteRfxNpeFactEvaltrByRfx(rfxData);
		rfxEvalRepository.deleteRfxNpeFactSetup(rfxData);
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap saveRfxNpeFact(Map param) {
		Map npeFactSetup = (Map) param.get("npeFactSetup");
		List<Map> insertNpeFactEvaltr = (List<Map>) param.get("insertNpeFactEvaltr");
		List<Map> updateNpeFactEvaltr = (List<Map>) param.get("updateNpeFactEvaltr");
		List<Map> removeNpeFactEvaltr = (List<Map>) param.get("removeNpeFactEvaltr");
		
		Map evalTmplInfo = (Map) param.get("evalTmplInfo");
		String evaltmplUuid = rfxEvalEventPublisher.saveEvalTmplInfo(evalTmplInfo);
		if(Strings.isNullOrEmpty(evaltmplUuid)) {
			return ResultMap.FAIL("save eval template failed");
		}
		
		npeFactSetup.put("evaltmpl_uuid", evaltmplUuid);
		if(npeFactSetup.get("npefact_uuid") == null) {
			npeFactSetup.put("npefact_uuid", UUID.randomUUID().toString());
			rfxEvalRepository.insertRfxNpeFactSetup(npeFactSetup);
		} else {
			rfxEvalRepository.updateRfxNpeFactSetup(npeFactSetup);
		}
		
		this.insertRfxNpeFactEvaltr(insertNpeFactEvaltr, (String) npeFactSetup.get("npefact_uuid"), (String) npeFactSetup.get("rfx_uuid"));
		this.updateRfxNpeFactEvaltr(updateNpeFactEvaltr);
		this.deleteRfxNpeFactEvaltr(removeNpeFactEvaltr);
		
		return ResultMap.SUCCESS(npeFactSetup);
	}
	
	private void deleteRfxNpeFactEvaltr(List<Map> deleteNpeFactEvaltr) {
		if(deleteNpeFactEvaltr == null) {
			return;
		}
		for(Map npeFactEvaltr : deleteNpeFactEvaltr) {
			rfxEvalRepository.deleteRfxNpeFactEvaltr(npeFactEvaltr);
		}
	}
	
	private void updateRfxNpeFactEvaltr(List<Map> updateNpeFactEvaltr) {
		if(updateNpeFactEvaltr == null) {
			return;
		}
		for(Map npeFactEvaltr : updateNpeFactEvaltr) {
			rfxEvalRepository.updateRfxNpeFactEvaltr(npeFactEvaltr);
		}
	}
	
	private void insertRfxNpeFactEvaltr(List<Map> insertNpeFactEvaltr, String npefactUuid, String rfxUuid) {
		if(insertNpeFactEvaltr == null) {
			return;
		}
		for(Map npeFactEvaltr : insertNpeFactEvaltr) {
			npeFactEvaltr.put("npefact_uuid", npefactUuid);
			npeFactEvaltr.put("rfx_uuid", rfxUuid);
			rfxEvalRepository.insertRfxNpeFactEvaltr(npeFactEvaltr);
		}
	}
	
	public ResultMap confirmRfxNpeFact(Map param) {
		ResultMap resultMap = this.saveRfxNpeFact(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		
		Map npeFactSetup = resultMap.getResultData();
		npeFactSetup.put("cnfd_yn", "Y");
		rfxEvalRepository.updateConfirmRfxNpeFact(npeFactSetup);
		
		// 평가템플릿 확정 처리
		this.confirmedEvaltmpl(npeFactSetup, true);
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap cancelConfirmRfxNpeFact(Map param) {
		Map npeFactSetup = (Map) param.get("npeFactSetup");
		npeFactSetup.put("cnfd_yn", "N");
		rfxEvalRepository.updateConfirmRfxNpeFact(npeFactSetup);
		
		int evaltmplCnt = rfxEvalRepository.countRfxEvalempl(npeFactSetup);
		if(evaltmplCnt == 0) {
			// 다른 비가격평가 항목 설정에 템플릿이 사용되지 않은 경우 확정 취소 처리
			this.confirmedEvaltmpl(npeFactSetup, false);
		}
		
		return ResultMap.SUCCESS();
	}
	
	public void copyRfxNpeSetupByRfx(String sourceRfxId, String newRfxId) {
		if(sourceRfxId == null) {
			return;
		}
		if(newRfxId == null) {
			return;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", newRfxId);
		Map newNpeFactSetup = rfxEvalRepository.findRfxNpeFactSetup(param);
		if(newNpeFactSetup != null) {
			// 복사 시 초기화 생성에 의해 생긴 기본 형태의 비가격평가 설정 삭제한다.
			rfxEvalRepository.deleteRfxNpeFactSetup(param);
		}
		
		param.put("rfx_uuid", sourceRfxId);
		Map npeFactSetup = rfxEvalRepository.findRfxNpeFactSetup(param);
		if(npeFactSetup == null) {
			return;
		} else {
			List<Map> npeFactEvaltrs = this.findListRfxEvaltr(param);
			
			String newNpefactUuid = UUID.randomUUID().toString();
			
			npeFactSetup.put("npefact_uuid", newNpefactUuid);
			npeFactSetup.put("rfx_uuid", newRfxId);
			
			rfxEvalRepository.insertRfxNpeFactSetup(npeFactSetup);
			this.insertRfxNpeFactEvaltr(npeFactEvaltrs, newNpefactUuid, newRfxId);
		}
	}
	
	public List<Map> findListRfxEvaltr(Map param) {
		return rfxEvalRepository.findListRfxNpeFactEvaltr(param);
	}
	
	public void deleteEval(Map rfxInfo) {
		rfxEvalEventPublisher.deleteReqNpeEval(Auth.getCurrentTenantId(), (String) rfxInfo.get("rfx_uuid"));
	}
	
	/**
	 * 평가 생성
	 * @param rfxInfo
	 * @return
	 */
	public ResultMap createEval(Map rfxInfo) {
		if(rfxInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		// RFP 여부 확인 후 스킵
		if(!"RFP".equals(rfxInfo.get("rfx_typ_ccd"))) {
			return ResultMap.SKIP();
		}
		// 종합평가 여부 확인 후 스킵
		if(!"COMPREVAL".equals(rfxInfo.get("slctn_typ_ccd"))) {
			return ResultMap.SKIP();
		}
		
		// 제출 업체 목록 조회
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
		param.put("rfx_bid_sts_ccd", "SUBM");
		List<Map> submRfxBids = rfxBidService.findListRfxBid(param);
		if(submRfxBids.isEmpty()) {
			return ResultMap.builder()
			                .resultStatus(Const.FAIL)
			                .resultMessage("submit vendor zero")
			                .build();
		}
		
		// 평가자 설정 정보 조회
		List<Map> npeFactEvaltrs = rfxEvalRepository.findListRfxNpeFactEvaltr(rfxInfo);
		if(npeFactEvaltrs.isEmpty()) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFP_NO_EVALUATOR)
			                .build();
		}
		
		// 혹시 이미 매핑되어 있는 비가격평가자 매핑 삭제
		rfxBidEvaltrService.deleteRfxBidEvaltrByRfxBidUuid(submRfxBids);
		// 제출 견적서에 비가격평가자 매핑
		rfxBidEvaltrService.insertRfxBidEvaltr(submRfxBids, npeFactEvaltrs);
		// 매핑한 견적서 별 비가격평가자 정보 조회
		List<Map> rfxBidEvaltrs = rfxBidEvaltrService.findListRfxBidEvaltr(rfxInfo);
		// 비가격평가 설정 정보 조회
		Map npeFactSetup = rfxEvalRepository.findRfxNpeFactSetup(rfxInfo);
		
		ResultMap result = rfxEvalEventPublisher.createReqNpeEval((String) rfxInfo.get("rfx_uuid"), npeFactSetup, rfxBidEvaltrs);
		
		if(result.isSuccess()) {
			List<Map<String, Object>> resultEvalSubjList = result.getResultList();
			List<Map<String, Object>> allResultEvalSubjEvaltrList = Lists.newArrayList();
			for(Map<String, Object> resultEvalSubjInfo : resultEvalSubjList) {
				allResultEvalSubjEvaltrList.addAll((List<Map<String, Object>>) resultEvalSubjInfo.get("evalSubjEvaltrList"));
			}
			
			for(Map<String, Object> resultEvalSubjInfo : resultEvalSubjList) {
				rfxEvalRepository.updateEvalSubjResUuid(resultEvalSubjInfo);
			}
			
			for(Map<String, Object> allResultEvalSubjEvaltrInfo : allResultEvalSubjEvaltrList) {
				allResultEvalSubjEvaltrInfo.put("eval_pic_id", allResultEvalSubjEvaltrInfo.get("evaltr_id"));
				rfxEvalRepository.updateEvalSubjEvaltrResUuid(allResultEvalSubjEvaltrInfo);
			}
			
			rfxStatusService.startRfxEval(rfxInfo);
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * RFP 비가격평가인 경우 연결중인 평가시트 확정
	 *
	 * @param rfxInfo
	 */
	public ResultMap confirmedEvaltmpl(Map npeFactSetup, boolean confirmed) {
		if(npeFactSetup == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		String evaltmplUuid = (String) npeFactSetup.get("evaltmpl_uuid");
		if(Strings.isNullOrEmpty(evaltmplUuid)) {
			return ResultMap.NOT_EXISTS("Eval Template ID not exists");
		}
		
		rfxEvalEventPublisher.confirmedEvalTmpl(evaltmplUuid, confirmed);
		return ResultMap.SUCCESS();
	}
	
	public List findListNpePerform(Map param) {
		return rfxEvalRepository.findListNpePerform(param);
	}
	
	public ResultMap findNpeEvalSubjectInfo(Map param) {
		Map data = Maps.newHashMap();
		data.put("data", rfxEvalRepository.findRfxNpeEvalInfo(param));
		data.put("list", rfxEvalRepository.findNpeEvalSubjectInfo(param));
		
		return ResultMap.SUCCESS(data);
	}
	
	public ResultMap findNpeEvalfactFulfillInfo(Map param) {
		return rfxEvalEventPublisher.findNpeEvalfactFulfillInfo(param);
	}
	
	public ResultMap saveNpeEvalFulfillment(Map param) {
		ResultMap saveResultMap = rfxEvalEventPublisher.saveNpeEvalFulfillment(param);
		
		if(!saveResultMap.isSuccess()) {
			return saveResultMap;
		}
		
		Map<String, Object> evalSubjMap = saveResultMap.getResultData();
		// 평가자 점수 및 평가상태 update
		rfxEvalRepository.updateEvaltrScore(evalSubjMap);
		// 평가대상 점수 update
		rfxEvalRepository.updateEvalTargetScore(evalSubjMap);
		
		// 평가의 모든 평가 완료 여부 확인
		boolean isCompleteEval = rfxEvalRepository.validateNpeEvalComplete(evalSubjMap);
		evalSubjMap.put("complete_eval", isCompleteEval);
		
		// 평가 완료인 경우
		if(isCompleteEval) {
			rfxEvalRepository.updateCompleteNpeEval(evalSubjMap);
		}
		
		return saveResultMap;
	}
	
	/**
	 * 평가 재수행
	 * 평가상태 마감 -> 진행중으로 되돌린다.
	 * 이 경우 제출하는 순간 다시 순위 및 평가금액 재계산 수행한다.
	 *
	 * @param param
	 * @return
	 */
	public ResultMap reExecuteNpeEval(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		rfxStatusService.reExecuteNpeEval(param);
		
		return ResultMap.SUCCESS();
	}
	
	public void updateCompleteNpeEval(Map rfxInfo) {
		rfxEvalRepository.updateCompleteNpeEval(rfxInfo);
	}
	
	/**
	 * 평가 모니터링 데이터 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListEvalMonEvaltr(Map param) {
		return rfxEvalRepository.findNpeEvalSubjectInfo(param);
	}
	
	/**
	 * 평가 복사
	 * @param rfxInfo
	 * @return
	 */
	public ResultMap copyEval(Map rfxInfo) {
		ResultMap resultMap = ResultMap.SUCCESS();
		
		Map param = Maps.newHashMap();
		param.put("isMonitor", true);
		// 이전차수 대상별 평가자 정보 조회
		param.put("rfx_uuid", rfxInfo.get("pre_rfx_uuid"));
		List<Map> preEvalSubjectList = rfxEvalRepository.findNpeEvalSubjectInfo(param);
		
		// 현재차수 대상별 평가자 정보 조회
		param.put("rfx_uuid", rfxInfo.get("rfx_uuid"));
		List<Map> evalSubjectList = rfxEvalRepository.findNpeEvalSubjectInfo(param);
		
		for(int i = 0; i < preEvalSubjectList.size(); i++) {
			Map preEvalSubjectInfo = preEvalSubjectList.get(i);

			for(int j = 0; j < evalSubjectList.size(); j++) {
				Map evalSubjectInfo = evalSubjectList.get(j);

				// 이전 평가의 평가대상 및 평가자가 현재 평가에도 존재하는 경우 복사
				if(preEvalSubjectInfo.get("disp_vd_cd").equals(evalSubjectInfo.get("disp_vd_cd")) &&
					preEvalSubjectInfo.get("evaltr_id").equals(evalSubjectInfo.get("evaltr_id"))) {
					
					ResultMap preNpeEvalfactFulfillInfo = rfxEvalEventPublisher.findNpeEvalfactFulfillInfo(preEvalSubjectInfo);
					ResultMap npeEvalfactFulfillInfo = rfxEvalEventPublisher.findNpeEvalfactFulfillInfo(evalSubjectInfo);
					
					Map saveNpeEvalFulfillment = this.copyNpeEvalfactFulfillInfo(rfxInfo, evalSubjectInfo, preNpeEvalfactFulfillInfo, npeEvalfactFulfillInfo);
					ResultMap saveResultMap = this.saveNpeEvalFulfillment(saveNpeEvalFulfillment);
					
					Map<String, Object> evalSubjMap = saveResultMap.getResultData();
					// 모든 평가 대상의 모든 평가자 제출상태이므로 자동 개찰 수행
					if((Boolean) evalSubjMap.get("complete_eval")) {
						resultMap = saveResultMap;
					}
					
				}
			}
		}
		
		return resultMap;
	}
	
	private Map copyNpeEvalfactFulfillInfo(Map rfxInfo, Map evalSubjectInfo, ResultMap preNpeEvalfactFulfillInfo, ResultMap npeEvalfactFulfillInfo) {
		Map result = Maps.newHashMap();
		
		Map preResultData = preNpeEvalfactFulfillInfo.getResultData();
		Map resultData = npeEvalfactFulfillInfo.getResultData();
		
		// 이전 평가의 항목 조회
		List<Map> preEvalTemplateList = (List<Map>) preResultData.get("evalTemplateList");
		Map preEvalTemplateRmkMap = Maps.newHashMap();
		for(Map preEvalTemplateInfo : preEvalTemplateList) {
			preEvalTemplateRmkMap.put(preEvalTemplateInfo.get("evaltmpl_uuid"), preEvalTemplateInfo.get("rmk"));
		}
		
		// 선택된 항목만 보관
		List<Map> preFactScaleList = (List<Map>) preResultData.get("factScaleList");
		List<Map> selectPreFactScaleList = Lists.newArrayList();
		for(Map preFactScaleInfo : preFactScaleList) {
			// 선택된 스케일 코드가 존재하는 경우
			if("Y".equals(preFactScaleInfo.get("select_yn")) && preFactScaleInfo.get("orgn_slctd_scale_cd") != null) {
				selectPreFactScaleList.add(preFactScaleInfo);
			}
		}
		
		// 현재 평가의 항목 조회
		List<Map> evalTemplateList = (List<Map>) resultData.get("evalTemplateList");
		for(Map evalTemplateInfo : evalTemplateList) {
			String rmk = (String) preEvalTemplateRmkMap.get(evalTemplateInfo.get("evaltmpl_uuid"));
			evalTemplateInfo.put("rmk", rmk);
		}
		
		List<Map> evalfactEvalList = (List<Map>) resultData.get("evalfactEvalList");
		
		// 항목 코드 key, 항목 정보 value
		List<Map> factScaleList = (List<Map>) resultData.get("factScaleList");
		Map factScaleMap = Maps.newHashMap();
		for(Map factScaleInfo : factScaleList) {
			factScaleMap.put(factScaleInfo.get("scale_cd"), factScaleInfo);
		}
		
		List<Map> selectFactScaleList = Lists.newArrayList();
		HashSet<String> evalfactCds = Sets.newHashSet();
		for(Map selectPreFactScaleInfo : selectPreFactScaleList) {
			Map selectFactScaleInfo = (Map) factScaleMap.get(selectPreFactScaleInfo.get("scale_cd"));
			if(selectFactScaleInfo != null) {
				evalfactCds.add((String) selectFactScaleInfo.get("evalfact_cd"));
				selectFactScaleInfo.put("slctd_scale_cd", selectPreFactScaleInfo.get("orgn_slctd_scale_cd")); // 이전 평가에서 선택된 스케일 코드
				selectFactScaleInfo.put("slctd_scale_sc", selectPreFactScaleInfo.get("orgn_slctd_scale_sc"));  // 이전 평가에서 선택된 스케일 점수
				selectFactScaleList.add(selectFactScaleInfo);
			}
		}

		// 모든 항목 선택되었는지 체크
		boolean sameEvalfact = true;
		if(evalfactEvalList.size() != evalfactCds.size()) {
			// 복사 시 항목 변경에 의해 모든 항목이 선택되지 않은 경우 임시저장으로 마무리한다.
			sameEvalfact = false;
		}
		
		result.put("evalSubjMap", this.addSaveNpeEvalStatus(rfxInfo, evalSubjectInfo, sameEvalfact));
		result.put("saveEvaltmplList", evalTemplateList);
		result.put("saveEvalfactList", evalfactEvalList);
		result.put("saveEvalfactScaleList", selectFactScaleList);
		return result;
	}
	
	private Map addSaveNpeEvalStatus(Map rfxInfo, Map evalSubjectInfo, boolean sameEvalfact) {
		Map result = Maps.newHashMap(evalSubjectInfo);
		
		result.put("batch_apply_yn", "N");
		
		// 재평가 여부
		String npeReevalYn = (String) rfxInfo.getOrDefault("npe_reexec_yn", "N");
		if("Y".equals(npeReevalYn) || !sameEvalfact) {
			// 재평가 여부 Y 이거나 이전 비가격평가 항목과 달라져서 모든 항목 평가가 진행되지 않은 경우
			// 재평가를 수행해야 하므로 임시저장 상태로 복사
			result.put("eval_prgs_sts_ccd", "SAVE");
			result.put("eval_sts_ccd", "CRNG");
		} else {
			// 재평가 필요 없는 경우
			result.put("eval_prgs_sts_ccd", "SUBM");
			result.put("eval_sts_ccd", "SUBM");
		}
		
		return result;
	}
	
	public Map findListCompareNpeEvalfactEvaltr(Map param) {
		param.put("ten_id", Auth.getCurrentTenantId());
		
		List<Map> evalfactList = rfxEvalEventPublisher.findListEvalSubjEvalfactRes(param);
		List<Map> evaltrEvalfactList = rfxEvalEventPublisher.findListEvaltrEvalfactResSc(param);
		
		List<Map> vendorList = rfxEvalRepository.findListTargetVendorByRfxUuid(param);
		List<Map> evaltrList = rfxEvalRepository.findListRfxNpeFactEvaltr(param);
		
		// evalfact_uuid 그룹핑
		Map<String, List<Map>> evalfactGroup = Maps.newHashMap();
		// evalfact_uuid 기준 항목 정보
		Map evalfactMap = Maps.newHashMap();
		for(Map evalfactInfo : evalfactList) {
			String evalfactUuid = (String) evalfactInfo.get("evalfact_uuid");
			List<Map> values;
			if(evalfactGroup.get(evalfactUuid) == null) {
				values = Lists.newArrayList();
				evalfactGroup.put(evalfactUuid, values);
			} else {
				values = evalfactGroup.get(evalfactUuid);
			}
			values.add(evalfactInfo);
			
			if(evalfactMap.get(evalfactUuid) == null) {
				evalfactMap.put(evalfactUuid, evalfactInfo);
			}
		}
		
		Map<String, List<Map>> evaltrEvalfactGroup = Maps.newHashMap();
		for(Map evaltrEvalfactInfo : evaltrEvalfactList) {
			String evalfactUuid = (String) evaltrEvalfactInfo.get("evalfact_uuid");
			List<Map> values;
			if(evaltrEvalfactGroup.get(evalfactUuid) == null) {
				values = Lists.newArrayList();
				evaltrEvalfactGroup.put(evalfactUuid, values);
			} else {
				values = evaltrEvalfactGroup.get(evalfactUuid);
			}
			values.add(evaltrEvalfactInfo);
		}
		
		List<Map> evalfactValueList = Lists.newArrayList();
		for(String key : evalfactGroup.keySet()) {
			Map evalfactVendorInfo = Maps.newHashMap();
			Map evalfactInfo = (Map) evalfactMap.get(key);
			List<Map> evalfactValues = evalfactGroup.get(key);
			List<Map> evaltrValues = evaltrEvalfactGroup.get(key);
			
			evalfactVendorInfo.put("eval_req_uuid", evalfactInfo.get("eval_req_uuid"));
			evalfactVendorInfo.put("efactg_nm", evalfactInfo.get("efactg_nm"));
			evalfactVendorInfo.put("evalfact_nm", evalfactInfo.get("evalfact_nm"));
			
			for(Map value : evalfactValues) {
				evalfactVendorInfo.put(value.get("eval_subj_res_uuid") + "_avg_sco", value.get("evalfact_sc"));
			}
			
			if(evaltrValues != null) {
				for(Map value : evaltrValues) {
					String evalSubjResUuid = (String) value.get("eval_subj_res_uuid");
					String evaltrId = (String) value.get("evaltr_id");
					
					evalfactVendorInfo.put(evalSubjResUuid + "_" + evaltrId, value.get("evaltr_evalfact_sc"));
				}
			}
			evalfactValueList.add(evalfactVendorInfo);
		}
		
		Map result = Maps.newHashMap();
		result.put("vendorList", vendorList);
		result.put("evaltrList", evaltrList);
		result.put("evalfactValueList", evalfactValueList);
		return result;
	}
	
	public ResultMap checkNonPriEvalSet(Map rfxData) {
		if(rfxData == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map npeFactSet = rfxEvalRepository.findRfxNpeFactSetup(rfxData);
		if(npeFactSet == null) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFP_NON_PRI_EVAL_SET_INCOMPELETED)
			                .build();
		}
		if("N".equals(npeFactSet.get("cnfd_yn"))) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFP_NON_PRI_EVAL_SET_NOT_CONFIRM)
			                .build();
		}
		
		List<Map> npeFactEvaltrs = rfxEvalRepository.findListRfxNpeFactEvaltr(rfxData);
		if(npeFactEvaltrs.isEmpty()) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFP_NO_EVALUATOR)
			                .build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public void saveMappingEvaltmplUuidToNpeEvalshtPrcs(Map data) {
		if(data == null) {
			return;
		}
		String npefactUuid = (String) data.get("work_evaltmpl_uuid");
		String evaltmplUuid = (String) data.get("evaltmpl_uuid");
		
		Map param = Maps.newHashMap();
		param.put("npefact_uuid", npefactUuid);
		param.put("evaltmpl_uuid", evaltmplUuid);
		rfxEvalRepository.saveMappingEvaltmplUuidByNpeFact(param);
	}

	/**
	* 평가대상 평가자 아이디로 npe eval 정보 확인 (workplace link 용)
	* @param data {eval_subj_evaltr_res_uuid : xxx}
	 *             @return the map {rfx_uuid : xxx, npe_sts_ccd : xxx} npe 평가 정보 조회에 필요한 데이터
	* */
	public Map findNpeEvalByEvalSubjEvaltrResId(Map data) {
		return rfxEvalRepository.findNpeEvalByEvalSubjEvaltrResId(data);
	}

	/**
	 * 평가템플릿 상태값을 조회한다. (비가격평가 평가시트)
	 *
	 * @author : cyhwang
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public String findEvalTmplStsInNpeEvalSht(Map<String, Object> param) {
		List<String> sheetList = rfxEvalRepository.findEvalTmplStsInNpeEvalSht(param);
		String deleteValid = "U";	// 삭제 가능 여부 초기값 U (update sts D)
		if(sheetList.size() == 0){
			// 삭제 가능
			deleteValid = "D";
		} else {
			// STS 'D' 확인
			for(String sts : sheetList){
				if(!"D".equals(sts)){	// 존재하는 평가시트의 상태가 D가 아니라면 삭제 불가.
					deleteValid = "E";
					break;
				}
			}
		}
		return deleteValid;
	}


}