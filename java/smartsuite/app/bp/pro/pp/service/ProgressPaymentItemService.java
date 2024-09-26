package smartsuite.app.bp.pro.pp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.pp.repository.ProgressPaymentItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProgressPaymentItemService {
	@Inject
	private ProgressPaymentItemRepository progressPaymentItemRepository;
	
	/**
	 * 기성 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchProgressPaymentItemByGrUuid(Map<String, Object> param) {
		return progressPaymentItemRepository.searchProgressPaymentItemByGrUuid(param);
	}
	
	/**
	 * 검수 품목 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentItemByGrUuid(Map<String, Object> param) {
		return progressPaymentItemRepository.searchAdvancePaymentItemByGrUuid(param);
	}
}
