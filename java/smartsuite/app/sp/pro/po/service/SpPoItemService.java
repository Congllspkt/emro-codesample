package smartsuite.app.sp.pro.po.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.pro.po.repository.SpPoItemRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpPoItemService {

    @Inject
    SpPoItemRepository spPoItemRepository;

    public List findListPoItemByPoId(Map<String, Object> param) {
        return spPoItemRepository.findListPoItemByPoId(param);
    }

    public FloaterStream findListPoItem(Map<String, Object> param) {
        return spPoItemRepository.findListPoItem(param);
    }
	
	/**
	 * 초기 기성요청 품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchInitProgressPaymentRequestItemByPoUuid(Map<String, Object> param) {
		return spPoItemRepository.searchInitProgressPaymentRequestItemByPoUuid(param);
	}
	
	/**
     * 발주품목의 납품예정 생성여부 체크
     *
     * @param param
     * @return
     */
	public List<Map<String, Object>> checkAsnCreatableOfPoItem(Map<String, Object> targetAsnData) {
		return spPoItemRepository.checkAsnCreatableOfPoItem(targetAsnData);
	}
	
	/**
     * 발주품목의 납품예정 수량 수정
     *
     * @param poItem
     */
    public void updateAsnQuantityOfPoItem(Map<String, Object> poItem) {
        spPoItemRepository.updateAsnQuantityOfPoItem(poItem);
    }
}
