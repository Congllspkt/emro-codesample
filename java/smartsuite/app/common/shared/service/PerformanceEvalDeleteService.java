package smartsuite.app.common.shared.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.request.repository.PerformanceEvalRequestRepository;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.event.PerformanceEventPublisher;
import smartsuite.app.bp.srm.performance.result.repository.PerformanceEvalResultRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PerformanceEvalDeleteService {
	@Inject
	PerformanceEvalResultRepository pfmcEvalResRepository;

	@Inject
	PerformanceEvalRequestRepository pfmcEvalReqRepository;

	@Inject
	PerformanceEventPublisher pfmcEventPublisher;


	/**
	 * 기존 퍼포먼스평가 대상 결과 및 평가 데이터를 삭제한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023.06.25
	 * @Method Name : deletePeSubjResDataByPeSubj
	 */
	public void deletePeSubjResDataByPeSubj(final Map<String, Object> param) {
		// 퍼포먼스 평가대상별 퍼포먼스 평가결과 목록 조회
		param.put(PfmcConst.REQ_TYPE, PfmcConst.PE_SUBJ);
		List<Map<String, Object>> peSubjResList = pfmcEvalResRepository.findListPeSubjRes(param);
		if(peSubjResList != null) {
			for(Map<String, Object> peSubjRes : peSubjResList) {
				String peSubjResUuid = (String)peSubjRes.get(PfmcConst.EVAL_SUBJ_RES_UUID);
				if(peSubjResUuid != null && !"".equals(peSubjResUuid)) {
					// 평가결과 데이터 삭제
					pfmcEventPublisher.deleteEvalSubjRes(peSubjRes);
				}
			}
			// 퍼포먼스 평가대상 퍼포먼스 평가결과 삭제
			pfmcEvalResRepository.deletePeSubjRes(param);
		}
	}
	/**
	 * 평가 대상 평가자 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.07.05
	 * @Method Name : deleteEvalSubjEvaltrRes
	 */
	public void deleteEvalSubjEvaltrRes(Map<String, Object> param) {
		// 평가결과 데이터 삭제
		pfmcEventPublisher.deleteEvalSubjEvaltrRes(param);
	}

	/**
	 * 평가 대상 평가자 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.07.05
	 * @Method Name : deletePfmcEval
	 */
	public void deletePfmcEval(Map<String, Object> param) {

		// 1. 평가결과 데이터 전체 삭제
		this.deleteEvalByEvalReqUuid(param);

		param.put(PfmcConst.REQ_TYPE, PfmcConst.PE);
		// 2. 퍼포먼스 평가대상 결과 전체 삭제
		pfmcEvalResRepository.deletePeSubjRes(param);

		// 3. 퍼포먼스 평가대상 평가자 전체 삭제
		pfmcEvalReqRepository.deletePeSubjEvaltrByPeUuid(param);

		// 4. 퍼포먼스 평가대상 전체 삭제
		pfmcEvalReqRepository.deletePeSubjByPeUuid(param);

		// 5. 퍼포먼스 평가그룹 평가등급 전체 삭제
		pfmcEvalReqRepository.deletePeEvalGrd(param);

		// 6. 퍼포먼스평가 오청 아이디로 퍼포먼스 평가그룹 전체 삭제
		pfmcEvalReqRepository.deletePePegByPeUuid(param);

		// 7. 퍼포먼스 요청 삭제
		pfmcEvalReqRepository.deletePe(param);
	}

	/**
	 * 평가 대상 평가자 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.eval_subj_evaltr_res_uuid - 평가대상 평가자 결과 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.07.05
	 * @Method Name : deleteEvalSubjEvaltrRes
	 */
	public void deleteEvalByEvalReqUuid(Map<String, Object> param) {
		// 평가결과 데이터 전체 삭제
		pfmcEventPublisher.deleteEvalByEvalReqUuid(param);
	}

	/**
	 * 퍼포먼스 평가 요청 취소 시 퍼포먼스 평가대상 결과 삭제
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.pe_uuid - 테넌트 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.08.08
	 * @Method Name : deleteRequestPeSubjRes
	 */
	public void deleteRequestPeSubjRes(Map<String, Object> param) {
		param.put(PfmcConst.REQ_TYPE, PfmcConst.PE);
		// 평가결과 데이터 전체 삭제
		pfmcEvalResRepository.deletePeSubjRes(param);
		param.remove(PfmcConst.REQ_TYPE);
	}
	/**
	 * 퍼포먼스 평가 요청 취소 시 퍼포먼스 평가대상의 자체점검 평가자 데이터 삭제
	 *
	 * @author : hj.jang
	 * @param param the param
	 * <br><br>
	 * <b>Required</b><br>
	 * param.pe_uuid - 테넌트 아이디 <br>
	 * @return the map< string, object>
	 * @Date : 2023.08.08
	 * @Method Name : deleteRequestPeSubjSlfckEvaltr
	 */
	public void deleteRequestPeSubjSlfckEvaltr(Map<String, Object> param) {
		param.put(PfmcConst.DELETE_TYPE, PfmcConst.PE);
		param.put("del_slfck_evaltr_yn", PfmcConst.Y_STR_VAL);
		// 평가결과 데이터 전체 삭제
		pfmcEvalReqRepository.deletePeSubjEvaltr(param);
		param.remove(PfmcConst.DELETE_TYPE);
		param.remove("del_slfck_evaltr_yn");
	}


}
