package smartsuite.app.bp.pro.asn.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.asn.repository.AsnItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AsnItemService {
	@Inject
	private AsnItemRepository asnItemRepository;
	
	/**
	 * 검수요청의 상세품목 목록조회
	 *
	 * @param param the param
	 * @return the list
	 */
	public List<Map<String, Object>> searchAsnItemByAsnUuid(Map<String, Object> param) {
		return asnItemRepository.searchAsnItemByAsnUuid(param);
	}
	
	/**
	 * 기성요청별 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchProgressPaymentRequestItemByAsnUuid(Map<String, Object> param) {
		return asnItemRepository.searchProgressPaymentRequestItemByAsnUuid(param);
	}
	
	/**
	 * 선급금 요청 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentRequestItem(Map<String, Object> param) {
		return asnItemRepository.searchAdvancePaymentRequestItem(param);
	}
	
	/**
	 * 납품예정 아이디로 입고품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchGrItemByAsnUuid(Map<String, Object> param) {
		return asnItemRepository.searchGrItemByAsnUuid(param);
	}
	
	/**
	 * AR 품목의 합격수량/반품수량 수정
	 *
	 * @param arItem
	 */
	public void updateAsnItemPassReturnQuantity(Map<String, Object> arItem) {
		asnItemRepository.updateAsnItemPassReturnQuantity(arItem);
	}

	/**
	 * 발주 품목의 ASN 요청/진행중 수량
	 *
	 * @param param
	 */
	public List<Map<String, Object>> searchAsnItemQty(Map<String, Object> param) {
		return asnItemRepository.searchAsnItemQty(param);
	}

}
