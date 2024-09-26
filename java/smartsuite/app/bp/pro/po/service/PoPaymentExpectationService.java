package smartsuite.app.bp.pro.po.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.po.repository.PoPaymentExptRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PoPaymentExpectationService {

	@Inject
	PoPaymentExptRepository poPaymentExpectationRepository;
	
	/**
	 * 발주 지급계획 목록을 조회한다.
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListPaymentExpectationByPoId(Map<String, Object> param) {
		return poPaymentExpectationRepository.findListPaymentExpectationByPoId(param);
	}

	public void deletePaymentExpectationsByPoId(Map<String, Object> delParam) {
		poPaymentExpectationRepository.deletePaymentExpectationsByPoId(delParam);
	}

	public void insertPaymentExpectation(Map<String, Object> paymentPlan) {
		poPaymentExpectationRepository.insertPaymentExpectation(paymentPlan);
	}
	
	public List<Map<String, Object>> findListPaymentExpectationByPoNoandPoRevno(Map<String, Object> param) {
		return poPaymentExpectationRepository.findListPaymentExpectationByPoNoandPoRevno(param);
	}
}
