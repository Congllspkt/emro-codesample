package smartsuite.app.bp.pro.po.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.service.GrEvalService;
import smartsuite.app.bp.pro.po.repository.PoEvalRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class PoEvalService {

	@Inject
	PoEvalRepository poEvalRepository;
	
	@Inject
	GrEvalService grEvalService;
	
	public Map<String, Object> findPoInfo(Map param) {
		return poEvalRepository.findPoInfo(param);
	}
	
	public Map findGeInfo(Map param) {
		return grEvalService.findGeInfo(param);
	}
	
	public ResultMap savePoEval(Map param) {
		Map poData = (Map) param.get("poData");
		Map geg = (Map) param.get("geg");
		Map grEvalshtInfo = (Map) param.get("grEvalshtInfo");
		List<Map> grEvalshtInfoPrcses = (List<Map>) param.get("grEvalshtInfoPrcses");
		List<Map> evalshtPrcsEvaltrs = (List<Map>) param.get("evalshtPrcsEvaltrs");
		
		String geUuid = (String) poData.get("ge_uuid");
		// 저장 시 기존 저장 설정 전부 삭제 후 재저장
		if(!Strings.isNullOrEmpty(geUuid)) {
			List<Map> gePrcses = poEvalRepository.findListGePrcsByGe(poData);
			this.deleteGePrcsEvaltrByGePrcses(gePrcses);
			poEvalRepository.deleteGePrcsByGe(poData);
		}
		
		// 입고평가 데이터 생성 위한 매핑작업
		Map geData = this.makeGeData(poData, geg, grEvalshtInfo);
		
		// 신규 저장
		if(Strings.isNullOrEmpty(geUuid)) {
			geData.put("ge_uuid", UUID.randomUUID().toString());
			poEvalRepository.insertGe(geData);
			
			poData.put("ge_uuid", geData.get("ge_uuid"));
			poEvalRepository.insertPoGeMapping(poData);
		} else {
			poEvalRepository.updateGe(geData);
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
			
			poEvalRepository.insertGePrcs(grEvalshtInfoPrcs);
		}
		for(Map gePrcsEvaltr : gePrcsEvaltrs) {
			gePrcsEvaltr.put("ge_prcs_evaltr_uuid", UUID.randomUUID().toString());
			poEvalRepository.insertGePrcsEvaltr(gePrcsEvaltr);
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
	
	private void deleteGePrcsEvaltrByGePrcses(List<Map> gePrcses) {
		if(gePrcses == null) {
			return;
		}
		if(gePrcses.isEmpty()) {
			return;
		}
		for(Map gePrcs : gePrcses) {
			poEvalRepository.deleteGePrcsEvaltrByGePrcs(gePrcs);
		}
	}
	
	private Map makeGeData(Map poData, Map geg, Map grEvalshtInfo) {
		if(poData == null) {
			return null;
		}
		if(geg == null) {
			return null;
		}
		if(grEvalshtInfo == null) {
			return null;
		}
		
		Map geData = Maps.newHashMap();
		geData.put("ge_uuid", poData.get("ge_uuid"));
		geData.put("oorg_cd", poData.get("oorg_cd"));
		geData.put("gemt_uuid", geg.get("gemt_uuid"));
		geData.put("geg_uuid", geg.get("geg_uuid"));
		geData.put("gr_evalsht_uuid", geg.get("gr_evalsht_uuid"));
		geData.put("vd_cd", poData.get("vd_cd"));
		return geData;
	}
	
	public ResultMap deletePoEval(Map param) {
		List<Map> gePrcses = poEvalRepository.findListGePrcsByGe(param);
		// 모든 입고평가 프로세스 평가자 삭제
		this.deleteGePrcsEvaltrByGePrcses(gePrcses);
		// 입고평가 프로세스 삭제
		poEvalRepository.deleteGePrcsByGe(param);
		// 발주 입고평가 매핑 삭제
		poEvalRepository.deletePoGeMapping(param);
		// 입고평가 마스터 삭제
		poEvalRepository.deleteGe(param);
		
		return ResultMap.SUCCESS();
	}
	
	public void insertPoGeMapping(Map<String, Object> poData) {
		poEvalRepository.insertPoGeMapping(poData);
	}
}
