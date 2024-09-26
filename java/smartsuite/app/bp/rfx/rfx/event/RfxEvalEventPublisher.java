package smartsuite.app.bp.rfx.rfx.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class RfxEvalEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	public String saveEvalTmplInfo(Map evalTmplInfo) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvalTmplInfo", evalTmplInfo);
		publisher.publishEvent(event);
		
		String evaltmplUuid = null;
		ResultMap result = (ResultMap) event.getResult();
		if(result.isSuccess()) {
			Map resultData = result.getResultData();
			Map resultEvalTmplInfo = (Map) resultData.get("evalTmplInfo");
			evaltmplUuid = (String) resultEvalTmplInfo.get("evaltmpl_uuid");
		}
		return evaltmplUuid;
	}
	
	public void confirmedEvalTmpl(String evaltmplUuid, boolean confirmed) {
		Map data = Maps.newHashMap();
		data.put("evaltmpl_uuid", evaltmplUuid);
		data.put("cnfd_yn", confirmed ? "Y" : "N");
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("updateCnfdYnEvalTmpl", data);
		publisher.publishEvent(event);
	}
	
	/**
	 * standard-eval 모듈에 비가격평가 생성 요청
	 * 해당 행위는 견적서 마감 후 제출 견적서와 평가담당자 매핑 정보를 통해 생성
	 *
	 * @param npeFactSetup
	 * @param rfxBidEvaltrs
	 */
	public ResultMap createReqNpeEval(String rfxUuid, Map npeFactSetup, List<Map> rfxBidEvaltrs) {
		Map data = Maps.newHashMap();
		data.put("ten_id", rfxBidEvaltrs.get(0).get("ten_id"));
		data.put("eval_req_uuid", rfxUuid);
		
		// 견적서 ID - rfx_bid_uuid 로 그룹핑
		Map<String, Map<String, Object>> rfxBidGroup = Maps.newHashMap();
		for(Map rfxBidEvaltr : rfxBidEvaltrs) {
			String rfxBidUuid = (String) rfxBidEvaltr.get("rfx_bid_uuid");
			if(rfxBidGroup.get(rfxBidUuid) == null) {
				Map evalSubjInfo = Maps.newHashMap();
				evalSubjInfo.put("ten_id", rfxBidEvaltr.get("ten_id"));
				evalSubjInfo.put("evaltmpl_uuid", npeFactSetup.get("evaltmpl_uuid"));
				evalSubjInfo.put("eval_task_typ_ccd", "NPE");
				evalSubjInfo.put("eval_req_uuid", rfxUuid);
				evalSubjInfo.put("vd_cd", rfxBidEvaltr.get("vd_cd"));
				evalSubjInfo.put("vmg_cd", null);
				
				// 업무 Key
				evalSubjInfo.put("rfx_bid_uuid", rfxBidUuid);
				
				rfxBidGroup.put(rfxBidUuid, evalSubjInfo);
			}
		}
		
		// 그룹핑한 Map 내부에 각 그룹핑 별 평가자 List 매핑
		for(Map rfxBidEvaltr : rfxBidEvaltrs) {
			String rfxBidUuid = (String) rfxBidEvaltr.get("rfx_bid_uuid");
			Map<String, Object> evalSubjInfo = rfxBidGroup.get(rfxBidUuid);
			
			List<Map<String, Object>> evalSubjEvaltrList;
			if(evalSubjInfo.get("evalSubjEvaltrList") == null) {
				evalSubjEvaltrList = Lists.newArrayList();
				evalSubjInfo.put("evalSubjEvaltrList", evalSubjEvaltrList);
			} else {
				evalSubjEvaltrList = (List<Map<String, Object>>) evalSubjInfo.get("evalSubjEvaltrList");
			}
			
			Map evalSubjEvaltrInfo = Maps.newHashMap();
			evalSubjEvaltrInfo.put("ten_id", rfxBidEvaltr.get("ten_id"));
			evalSubjEvaltrInfo.put("evalfact_evaltr_authty_ccd", rfxBidEvaltr.get("evalfact_evaltr_authty_ccd"));
			evalSubjEvaltrInfo.put("evaltr_id", rfxBidEvaltr.get("eval_pic_id"));
			
			// 업무 Key
			evalSubjEvaltrInfo.put("rfx_bid_uuid", rfxBidUuid);
			evalSubjEvaltrList.add(evalSubjEvaltrInfo);
		}
		
		List<Map> evalSubjList = Lists.newArrayList();
		for(String key : rfxBidGroup.keySet()) {
			evalSubjList.add(rfxBidGroup.get(key));
		}
		data.put("evalSubjList", evalSubjList);
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createEvalSubjResEvalReq", data);
		publisher.publishEvent(event);
		
		return (ResultMap) event.getResult();
	}
	
	public void deleteReqNpeEval(String tenId, String evalReqUuid) {
		Map data = Maps.newHashMap();
		data.put("ten_id", tenId);
		data.put("eval_req_uuid", evalReqUuid);
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteEvalByEvalReqUuid", data);
		publisher.publishEvent(event);
	}
	
	/**
	 * 평가수행용 평가항목 조회
	 *
	 * Required:
	 * param.ten_id - 시스템아이디
	 * param.eval_task_typ_ccd - 업무유형 공통코드
	 * param.evaltmpl_uuid - 평가템플릿 아이디
	 * param.eval_subj_res_uuid - 평가대상 평가 아이디
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 아이디
	 * param.evalfact_evaltr_authty_ccd - 평가항목 평가자 권한 공통코드
	 *
	 * @return ResultMap
	 */
	public ResultMap findNpeEvalfactFulfillInfo(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findEvalfactFulfillInfo", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 평가 수행 후 저장 및 제출
	 *
	 * @param param
	 * @return
	 */
	public ResultMap saveNpeEvalFulfillment(Map param) {
		param.put("ten_id", Auth.getCurrentTenantId());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvaltrEvalfactRes", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 평가 점수 비교를 위한 데이터 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map> findListEvalSubjEvalfactRes(Map param) {
		Map data = Maps.newHashMap();
		data.put("ten_id", Auth.getCurrentTenantId());
		data.put("eval_req_uuid", param.get("rfx_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListEvalSubjEvalfactRes", data);
		publisher.publishEvent(event);
		
		List<Map> evalfactList = (List<Map>) event.getResult();
		return evalfactList;
	}
	
	/**
	 * 평가 점수 비교를 위한 데이터 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map> findListEvaltrEvalfactResSc(Map param) {
		Map data = Maps.newHashMap();
		data.put("ten_id", Auth.getCurrentTenantId());
		data.put("eval_req_uuid", param.get("rfx_uuid"));
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findListEvaltrEvalfactResSc", data);
		publisher.publishEvent(event);
		
		List<Map> evaltrEvalfactList = (List<Map>) event.getResult();
		return evaltrEvalfactList;
	}
}
