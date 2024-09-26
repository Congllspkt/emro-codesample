package smartsuite.app.bp.pro.gr.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class GrEvalEventPublisher {
	
	@Inject
	ApplicationEventPublisher publisher;
	
	/**
	 * standard-eval 모듈에 입고평가 생성 요청
	 *
	 * @param gePrcs
	 * @param gePrcsEvaltrs
	 */
	public ResultMap createGrEvalPrcs(Map gePrcs, List<Map> gePrcsEvaltrs) {
		String evalReqUuid = (String) gePrcs.get("ge_prcs_uuid");
		
		Map data = Maps.newHashMap();
		data.put("ten_id", Auth.getCurrentTenantId());
		data.put("eval_req_uuid", evalReqUuid);
		
		List<Map> evalSubjList = Lists.newArrayList();
		
		Map evalSubjInfo = Maps.newHashMap();
		evalSubjInfo.put("ten_id", Auth.getCurrentTenantId());
		evalSubjInfo.put("evaltmpl_uuid", gePrcs.get("evaltmpl_uuid"));
		evalSubjInfo.put("eval_task_typ_ccd", "GE");
		evalSubjInfo.put("eval_req_uuid", evalReqUuid);
		evalSubjInfo.put("vd_cd", gePrcs.get("vd_cd"));
		evalSubjInfo.put("vmg_cd", null);
		evalSubjInfo.put("ge_prcs_uuid", gePrcs.get("ge_prcs_uuid"));
		evalSubjInfo.put("evalSubjEvaltrList", gePrcsEvaltrs);
		
		evalSubjList.add(evalSubjInfo);
		data.put("evalSubjList", evalSubjList);
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("createEvalSubjResEvalReq", data);
		publisher.publishEvent(event);
		
		return (ResultMap) event.getResult();
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
	public ResultMap findGeEvalfactFulfillInfo(Map param) {
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("findEvalfactFulfillInfo", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
	
	public ResultMap saveGeEvalFulfillment(Map param) {
		param.put("ten_id", Auth.getCurrentTenantId());
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("saveEvaltrEvalfactRes", param);
		publisher.publishEvent(event);
		return (ResultMap) event.getResult();
	}
}
