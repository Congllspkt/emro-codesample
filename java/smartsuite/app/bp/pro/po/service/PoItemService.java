package smartsuite.app.bp.pro.po.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.po.repository.PoItemRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PoItemService {

	@Inject
	PoItemRepository poItemRepository;

	/**
	 * 발주협력사 정보를 Update 한다.
	 *
	 * @param param the param
	 * @Date : 2018. 8. 27
	 */
	public void updatePoVendor(Map<String, Object> param) {
		poItemRepository.updatePoVendor(param);
	}

	public FloaterStream findListPoItem(Map param) {
		return poItemRepository.findListPoItem(param);
	}


	public List<Map<String, Object>> findPreviousListPoItemByPoId(Map<String, Object> param) {
		return poItemRepository.findPreviousListPoItemByPoId(param);
	}


	public List<Map<String, Object>> findListComparePoItem(Map<String, Object> param) {
		return poItemRepository.findListComparePoItem(param);
	}


	public List<Map<String, Object>> findPrevPoItemByModification(Map<String, Object> param) {
		return poItemRepository.findPrevPoItemByModification(param);
	}


	public void updatePrevPoItemByModification(Map<String, Object> modInfo) {
		poItemRepository.updatePrevPoItemByModification(modInfo);
	}


	public List<Map<String, Object>> findListPoItemModifyByPoId(Map<String, Object> param) {
		return poItemRepository.findListPoItemModifyByPoId(param);
	}


	public List<Map<String, Object>> findListPoItemByPoId(Map<String, Object> param) {
		return poItemRepository.findListPoItemByPoId(param);
	}


	public List<Map<String, Object>> findPoItemCompleteByAmt(Map<String, Object> param) {
		return poItemRepository.findPoItemCompleteByAmt(param);
	}


	public void updatePoItemCompleteByAmt(Map<String, Object> param) {
		List<Map<String, Object>> infos = findPoItemCompleteByAmt(param);
		if(!infos.isEmpty()) {
			for(Map<String, Object> info : infos) {
				poItemRepository.updatePoItemCompleteByAmt(info);
			}
		}
	}


	public void deletePoItem(Map<String, Object> row) {
		poItemRepository.deletePoItem(row);
	}


	public void insertPoItem(Map<String, Object> row) {
		poItemRepository.insertPoItem(row);
	}

	public void updatePoItem(Map<String, Object> row) {
		poItemRepository.updatePoItem(row);
	}

	public void deletePoItemByPoId(Map<String, Object> param) {
		poItemRepository.deletePoItemByPoId(param);
	}

	public void updatePoItemCrcEnd(Map<String, Object> param) {
		poItemRepository.updatePoItemCrcEnd(param);
	}

	public List<Map<String, Object>> checkGrCreatable(Map<String, Object> param) {
		return poItemRepository.checkGrCreatable(param);
	}

	public List<Map<String,Object>> findListPoItemHistory(Map<String, Object> param) {
		return poItemRepository.findListPoItemHistory(param);
	}

	public void updatePoItemCompleteByQty(Map<String, Object> data) {
		poItemRepository.updatePoItemCompleteByQty(data);
	}

	public List<Map<String, Object>> findListPoByRfxItemIds(Map<String, Object> data) {
		return poItemRepository.findListPoByRfxItemIds(data);
	}
	
	/**
	 * 선급금요청을 위한 발주품목 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchAdvancePaymentItemByPoUuid(Map<String, Object> param) {
		return poItemRepository.searchAdvancePaymentItemByPoUuid(param);
	}
	
	
	/**
	 * 발주품목의 납품예정수량 초기화
	 *
	 * @param param the param
	 */
	public void updateInitAsnQuantityOfPoItem(Map<String, Object> param) {
		poItemRepository.updateInitAsnQuantityOfPoItem(param);
	}
	
	/**
	 * 발주품목 검수수량 확인
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> checkQuantityList(Map<String, Object> param) {
		return poItemRepository.checkQuantityList(param);
	}
	
	/**
	 * 발주품목으로 입고품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findGrItemByPoItemUuid(Map<String, Object> param) {
		return poItemRepository.findGrItemByPoItemUuid(param);
	}
	
	public Map<String, Object> findPoEvalSetInfoByPoItemUuid(Map<String, Object> param) {
		return poItemRepository.findPoEvalSetInfoByPoItemUuid(param);
	}

	public List<Map<String, Object>> searchProgressPaymentItemByPo(Map param) {
		return poItemRepository.searchProgressPaymentItemByPo(param);
	}

	/**
	 * 발주 품목의 입고 수량 업데이트
	 *
	 * @param param
	 */
	public int updatePoItemGrQuantity(Map<String, Object> param) {
		return poItemRepository.updatePoItemGrQuantity(param);
	}

	/**
	 * 발주 품목의 납품예정 요청/진행중 수량 업데이트
	 *
	 * @param param
	 */
	public int updatePoItemAsnQuantity(Map<String, Object> param) {
		return poItemRepository.updatePoItemAsnQuantity(param);
	}
}
