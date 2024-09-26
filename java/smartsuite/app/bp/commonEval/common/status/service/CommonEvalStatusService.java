package smartsuite.app.bp.commonEval.common.status.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.commonEval.common.status.repository.CommonEvalStatusRepository;

import javax.inject.Inject;
import java.util.Map;

/**
 * Eval 상태를 관리하는 서비스입니다.
 *
 * @author hj.jang
 * @FileName CommonEvalStatusService.java
 * @package smartsuite.app.bp.commonEval.common.status.service
 * @변경이력 : [2023. 7. 2] hj.jang 최초작성
 * @see
 * @since 2023. 07. 02
 */
@Service
@Transactional
public class CommonEvalStatusService {

	@Inject
	CommonEvalStatusRepository evalStatusRepository;

	/**
	 * 평가대상 평가자 결과 신규 저장 시 정성평가 진행상태를 '대기'로 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 2
	 * @Method Name : insertEvalSubjEvaltrRes
	 */
	public void insertEvalSubjEvaltrRes(Map<String, Object> param) {
		evalStatusRepository.insertEvalSubjEvaltrRes(param);
	}

	/**
	 * 정성평가 진행상태 "제출" 상태를 취소한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 2
	 * @Method Name : updateCancleSubmEvaltrPrgsSts
	 */
	public void updateCancleSubmEvaltrPrgsSts(Map<String, Object> param) {
		evalStatusRepository.updateCancleSubmEvaltrPrgsSts(param);
	}

	/**
	 * 평가자 평가항목 결과 저장 시 정성평가 진행상태를 저장으로 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveEvalSubjEvaltrFulfill
	 */
	public void saveEvalSubjEvaltrFulfill(Map<String, Object> param) {
		evalStatusRepository.saveEvalSubjEvaltrFulfill(param);
	}


}
