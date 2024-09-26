package smartsuite.app.bp.pro.taxbill.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.taxbill.repository.TaxBillItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaxBillItemService {
	
	@Inject
	TaxBillItemRepository taxBillItemRepository;
	
	/**
	 * 세금계산서 품목 삭제
	 *
	 * @param param
	 */
	public void deleteTaxBillItem(Map<String, Object> param) {
		taxBillItemRepository.deleteTaxBillItem(param);
	}
	
	/**
	 * 세금계산서 품목 데이터 생성
	 *
	 * @param taxbillItem
	 */
	public void insertTaxBillItem(Map<String, Object> taxbillItem) {
		taxBillItemRepository.insertTaxBillItem(taxbillItem);
	}
	
	/**
	 * 세금계산서 발행현황 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map> searchTaxBillItem(Map<String, Object> param) {
		return taxBillItemRepository.searchTaxBillItem(param);
	}
}
