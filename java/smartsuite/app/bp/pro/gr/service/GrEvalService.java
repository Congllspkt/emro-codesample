package smartsuite.app.bp.pro.gr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.event.GrEvalEventPublisher;
import smartsuite.app.bp.pro.gr.repository.GrEvalRepository;
import smartsuite.app.bp.pro.gr.strategy.PoGrEvalFactory;
import smartsuite.app.bp.pro.gr.strategy.PoGrEvalStrategy;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class GrEvalService {
	
	@Inject
	GrEvalRepository grEvalRepository;
	
	@Inject
	PoGrEvalFactory poGrEvalFactory;
	
	@Inject
	GrEvalEventPublisher grEvalEventPublisher;
	
	@Inject
	ProStatusService proStatusService;
	
	public Map findGrInfo(Map param) {
		return grEvalRepository.findGrInfo(param);
	}
	
	public Map findGeInfo(Map param) {
		Map result = Maps.newHashMap();
		
		Map gegInfo = grEvalRepository.findGeInfo(param);
		Map grEvalshtInfo = grEvalRepository.findGrEvalshtInfo(gegInfo);
		List<Map> gePrcsEvaltrs = grEvalRepository.findListGePrcsEvaltr(gegInfo);
		
		result.put("gegInfo", gegInfo);
		result.put("grEvalshtInfo", grEvalshtInfo);
		result.put("gePrcsEvaltrs", gePrcsEvaltrs);
		return result;
	}
	
	public List findListGemt(Map param) {
		return grEvalRepository.findListGemt(param);
	}
	
	public Map findListGegByGemt(Map param) {
		Map result = Maps.newHashMap();
		
		String type = (String) param.get("type");
		String uuid = (String) param.get("uuid");
		String oorgCd = (String) param.get("oorg_cd");
		String gemtCd = (String) param.get("gemt_cd");
		PoGrEvalStrategy strategy = poGrEvalFactory.getStrategy(type, gemtCd);
		
		List<String> gemgValues = strategy.findListGemgValues(uuid);
		List<Map> gegs;
		
		if(gemgValues == null || gemgValues.isEmpty()) {
			gegs = null;
		} else {
			Map gemgParam = Maps.newHashMap();
			gemgParam.put("oorg_cd", oorgCd);
			gemgParam.put("gemgValues", gemgValues);
			gegs = grEvalRepository.findListGegByGemgValues(gemgParam);
		}
		
		result.put("gemgValues", gemgValues);
		result.put("gegs", gegs);
		return result;
	}
	
	public ResultMap saveGrEval(Map param) {
		Map grData = (Map) param.get("grData");
		Map geg = (Map) param.get("geg");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		List<Map> grEvalshtInfoPrcses = (List<Map>) param.get("grEvalshtInfoPrcses");
		List<Map> evalshtPrcsEvaltrs = (List<Map>) param.get("evalshtPrcsEvaltrs");
		
		String geUuid = (String) grData.get("ge_uuid");
		// 저장 시 기존 저장 설정 전부 삭제 후 재저장
		if(!Strings.isNullOrEmpty(geUuid)) {
			List<Map> gePrcses = grEvalRepository.findListGePrcsByGe(grData);
			this.deleteGePrcsEvaltrByGePrcses(gePrcses);
			grEvalRepository.deleteGePrcsByGe(grData);
		}
		
		// 입고평가 데이터 생성 위한 매핑작업
		Map geData = this.makeGeData(grData, geg, grEvalshtInfo);
		
		// 신규 저장
		if(Strings.isNullOrEmpty(geUuid)) {
			geData.put("ge_uuid", UUID.randomUUID().toString());
			grEvalRepository.insertGe(geData);
			
			grData.put("ge_uuid", geData.get("ge_uuid"));
			grEvalRepository.insertGrGeMapping(grData);
		} else {
			grEvalRepository.updateGe(geData);
		}
		
		// 입고평가 프로세스 및 평가자
		this.insertGePrcses(geData, grEvalshtInfoPrcses, evalshtPrcsEvaltrs);
		return ResultMap.SUCCESS(geData);
	}
	
	private void insertGePrcses(Map geData, List<Map> grEvalshtInfoPrcses, List<Map> evalshtPrcsEvaltrs) {
		if(geData == null) {
			return;
		}
		if(grEvalshtInfoPrcses == null) {
			return;
		}
		if(grEvalshtInfoPrcses.isEmpty()) {
			return;
		}
		
		List<Map> gePrcsEvaltrs = Lists.newArrayList();
		for(Map grEvalshtInfoPrcs : grEvalshtInfoPrcses) {
			grEvalshtInfoPrcs.put("ge_uuid", geData.get("ge_uuid"));
			grEvalshtInfoPrcs.put("ge_prcs_uuid", UUID.randomUUID().toString());
			
			// 평가자 구분 '담당자'인 경우 세션 담당자를 평가자로 강제 지정
			if("PURC_PIC".equals(grEvalshtInfoPrcs.get("evaltr_typ_ccd"))) {
				gePrcsEvaltrs.add(this.makePurcPicEvaltr(grEvalshtInfoPrcs));
			} else {
				for(Map evalshtPrcsEvaltr : evalshtPrcsEvaltrs) {
					String grEvalshtPrcsUuid = (String) evalshtPrcsEvaltr.get("gr_evalsht_prcs_uuid");
					if(grEvalshtPrcsUuid.equals(grEvalshtInfoPrcs.get("gr_evalsht_prcs_uuid"))) {
						evalshtPrcsEvaltr.put("ge_prcs_uuid", grEvalshtInfoPrcs.get("ge_prcs_uuid"));
						gePrcsEvaltrs.add(evalshtPrcsEvaltr);
					}
				}
			}
			
			grEvalRepository.insertGePrcs(grEvalshtInfoPrcs);
		}
		for(Map gePrcsEvaltr : gePrcsEvaltrs) {
			gePrcsEvaltr.put("ge_prcs_evaltr_uuid", UUID.randomUUID().toString());
			grEvalRepository.insertGePrcsEvaltr(gePrcsEvaltr);
		}
	}
	
	private Map makePurcPicEvaltr(Map grEvalshtInfoPrcs) {
		Map sessionUserInfo = Auth.getCurrentUserInfo();
		Map gePrcsEvaltr = Maps.newHashMap();
		gePrcsEvaltr.put("ge_prcs_uuid", grEvalshtInfoPrcs.get("ge_prcs_uuid"));
		gePrcsEvaltr.put("evaltr_id", sessionUserInfo.get("usr_id"));
		gePrcsEvaltr.put("evalfact_evaltr_authty_ccd", "ALL");
		
		return gePrcsEvaltr;
	}
	
	private Map makeGeData(Map grData, Map geg, Map grEvalshtInfo) {
		if(grData == null) {
			return null;
		}
		if(geg == null) {
			return null;
		}
		if(grEvalshtInfo == null) {
			return null;
		}
		
		Map geData = Maps.newHashMap();
		geData.put("ge_uuid", grData.get("ge_uuid"));
		geData.put("gr_uuid", grData.get("gr_uuid"));
		geData.put("oorg_cd", grData.get("oorg_cd"));
		geData.put("gemt_uuid", geg.get("gemt_uuid"));
		geData.put("geg_uuid", geg.get("geg_uuid"));
		geData.put("gr_evalsht_uuid", geg.get("gr_evalsht_uuid"));
		geData.put("vd_cd", grData.get("vd_cd"));
		geData.put("ge_sts_ccd", "CRNG");
		return geData;
	}
	
	public ResultMap submitGrEval(Map param) {
		ResultMap saveResult = this.saveGrEval(param);
		Map geData = saveResult.getResultData();
		
		this.submitGrEvalProcess(geData);
		return ResultMap.SUCCESS();
	}
	
	protected void submitGrEvalProcess(Map geData) {
		grEvalRepository.updateGrEvalStart(geData);
		
		//입고평가 프로세스 조회
		List<Map> gePrcses = grEvalRepository.findListGePrcsByGe(geData);
		Map firstGePrcs = gePrcses.get(0);
		//첫번째 프로세스 수행 시작
		this.executeGrEvalProcess(firstGePrcs);
		//첫번째 이후 프로세스 평가 대기중
		this.updateGrEvalNextProcessWtg(firstGePrcs, gePrcses);
	}
	
	private void updateGrEvalNextProcessWtg(Map firstGePrcs, List<Map> gePrcses) {
		for(Map gePrcs : gePrcses) {
			if(!firstGePrcs.get("ge_prcs_uuid").equals(gePrcs.get("ge_prcs_uuid"))) {
				grEvalRepository.updateGePrcsWtg(gePrcs);
			}
		}
	}
	
	public ResultMap submitGrEvalByGr(String grUuid) {
		Map param = Maps.newHashMap();
		param.put("gr_uuid", grUuid);
		
		Map grData = grEvalRepository.findGrInfo(param);
		Map geData = grEvalRepository.findGeInfo(grData);
		
		if(MapUtils.isEmpty(geData)) {
			return ResultMap.FAIL();
		}
		
		this.submitGrEvalProcess(geData);
		return ResultMap.SUCCESS();
	}
	
	public void executeGrEvalProcess(Map gePrcs) {
		List<Map> gePrcsEvaltrs = grEvalRepository.findListGePrcsEvaltrByGePrcs(gePrcs);
		
		ResultMap result = grEvalEventPublisher.createGrEvalPrcs(gePrcs, gePrcsEvaltrs);
		if(result.isSuccess()) {
			List<Map<String, Object>> resultGePrcses = result.getResultList();
			for(Map<String, Object> resultGePrcs : resultGePrcses) {
				List<Map<String, Object>> evalSubjEvaltrList = (List<Map<String, Object>>) resultGePrcs.get("evalSubjEvaltrList");
				for(Map<String, Object> evalSubjEvaltrInfo : evalSubjEvaltrList) {
					grEvalRepository.startGePrcsEvaltr(evalSubjEvaltrInfo);
				}
				grEvalRepository.startGePrcs(resultGePrcs);
			}
		}
	}
	
	public void nextStepGrEvalProcess(Map geData) {
		//입고평가 프로세스 조회
		List<Map> gePrcses = grEvalRepository.findListGePrcsByGe(geData);
		int endCount = 0;
		int gePrcsesSize = gePrcses.size();
		for(int i = 0; i < gePrcsesSize; i++) {
			Map gePrcs = gePrcses.get(i);
			
			//종료된 평가프로세스인 경우
			if("EVAL_ED".equals(gePrcs.get("ge_prcs_sts_ccd"))) {
				endCount++;
				continue;
			}
			//진행중인 평가프로세스인 경우
			if("EVAL_PRGSG".equals(gePrcs.get("ge_prcs_sts_ccd"))) {
				//모든 평가자 평가완료 여부
				boolean isCompleteEvaltrEval = grEvalRepository.isCompleteEvaltrEval(gePrcs);
				if(isCompleteEvaltrEval) {
					endCount++;
					grEvalRepository.updateGePrcsEndEval(gePrcs);
					
					//다음 프로세스 존재여부 확인
					if(i < gePrcsesSize - 1) {
						Map nextGePrcs = gePrcses.get(i+1);
						this.executeGrEvalProcess(nextGePrcs);
					}
				} else {
					//평가자 평가가 남아있으므로 여기서 중지
					break;
				}
			}
		}
		
		//모든 입고평가 프로세스 완료
		if(endCount == gePrcsesSize) {
			Map calcGrEvalPrcsScore = grEvalRepository.calcGrEvalPrcsScore(geData);
			grEvalRepository.completeGrEval(calcGrEvalPrcsScore);
			
			Map grInfo = grEvalRepository.findGrInfoByGe(geData);
			proStatusService.completeGrEval(grInfo);
		}
	}
	
	private void deleteGePrcsEvaltrByGePrcses(List<Map> gePrcses) {
		if(gePrcses == null) {
			return;
		}
		if(gePrcses.isEmpty()) {
			return;
		}
		for(Map gePrcs : gePrcses) {
			grEvalRepository.deleteGePrcsEvaltrByGePrcs(gePrcs);
		}
	}
	
	public ResultMap deleteGrEval(Map param) {
		List<Map> gePrcses = grEvalRepository.findListGePrcsByGe(param);
		// 모든 입고평가 프로세스 평가자 삭제
		this.deleteGePrcsEvaltrByGePrcses(gePrcses);
		// 입고평가 프로세스 삭제
		grEvalRepository.deleteGePrcsByGe(param);
		// 입고평가 매핑 삭제
		grEvalRepository.deleteGrGeMapping(param);
		// 입고평가 마스터 삭제
		grEvalRepository.deleteGe(param);
		
		return ResultMap.SUCCESS();
	}
	
	public List findListGePerform(Map param) {
		return grEvalRepository.findListGePerform(param);
	}
	
	public Map findGeEvalSubjectEvaltrInfo(Map param) {
		return grEvalRepository.findGeEvalSubjectEvaltrInfo(param);
	}
	
	public ResultMap findGeEvalfactFulfillInfo(Map param) {
		return grEvalEventPublisher.findGeEvalfactFulfillInfo(param);
	}
	
	public ResultMap saveGeEvalFulfillment(Map param) {
		Map evalInfo = (Map) param.get("evalInfo");
		ResultMap saveResultMap = grEvalEventPublisher.saveGeEvalFulfillment(param);
		
		if(!saveResultMap.isSuccess()) {
			return saveResultMap;
		}
		
		Map<String, Object> evalSubjMap = saveResultMap.getResultData();
		// 평가자 점수 및 평가상태 update
		grEvalRepository.updateEvaltrScore(evalSubjMap);
		// 평가대상 점수 update
		grEvalRepository.updateEvalTargetScore(evalSubjMap);
		
		this.nextStepGrEvalProcess(evalInfo);
		return saveResultMap;
	}
	
	public List findListMonitoringGePrcsEvaltr(Map param) {
		return grEvalRepository.findListMonitoringGePrcsEvaltr(param);
	}
	
	public List<Map<String, Object>> findListGePrcsByGe(Map<String, Object> param) {
		return grEvalRepository.findListGePrcsByGe(param);
	}
	
	public void copyGeEvalSetByPoEval(String grUuid, Map<String, Object> ge, List<Map<String, Object>> gePrcses, List<Map<String, Object>> gePrcsEvaltrs) {
		grEvalRepository.insertGe(ge);
		for(Map<String, Object> gePrcs : gePrcses) {
			grEvalRepository.insertGePrcs(gePrcs);
		}
		for(Map<String, Object> gePrcsEvaltr : gePrcsEvaltrs) {
			grEvalRepository.insertGePrcsEvaltr(gePrcsEvaltr);
		}
		
		Map<String, Object> param = Maps.newHashMap();
		param.put("gr_uuid", grUuid);
		param.put("ge_uuid", ge.get("ge_uuid"));
		grEvalRepository.insertGrGeMapping(param);
	}
	
	public void evalForceClose(Map<String, Object> checkedItem) {
		if(checkedItem == null) {
			return;
		}
		
		List<Map<String, Object>> gePrcses = grEvalRepository.findListGePrcsByGe(checkedItem);
		for(Map<String, Object> gePrcs : gePrcses) {
			if(gePrcs.get("ge_prcs_sts_ccd") == null) {
				continue;
			}
			if("EVAL_ED".equals(gePrcs.get("ge_prcs_sts_ccd"))) {
				continue;
			}
			grEvalRepository.forceCloseGePrcs(gePrcs);
		}
		Map calcGrEvalPrcsScore = grEvalRepository.calcGrEvalPrcsScore(checkedItem);
		grEvalRepository.completeGrEval(calcGrEvalPrcsScore);
		proStatusService.completeGrEval(checkedItem);
	}

	/**
	 * 평가대상 평가자 아이디로 ge eval 정보 확인 (workplace link 용)
	 * @param data {eval_subj_evaltr_res_uuid : xxx}
	 * @return the map {ge_prcs_uuid : xxx, ge_prcs_sts_ccd : xxx} ge 평가 정보 조회에 필요한 데이터
	 * */
	public Map findGeEvalByEvalSubjEvaltrResId(Map data) {
		return grEvalRepository.findGeEvalByEvalSubjEvaltrResId(data);
	}
}
