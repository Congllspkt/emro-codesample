package smartsuite.app.sp.pro.asn.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.pro.asn.repository.SpAsnItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpAsnItemService {
    @Inject
    private SpAsnItemRepository spAsnItemRepository;

    /**
     * 납품예정 품목 생성
     *
     * @param param
     */
    public void insertAsnItem(Map<String, Object> param) {
        spAsnItemRepository.insertAsnItem(param);
    }

    /**
     * 납품예정 품목 수정
     *
     * @param param
     */
    public void updateAsnItem(Map<String, Object> param) {
        spAsnItemRepository.updateAsnItem(param);
    }

    /**
     * 납품예정 품목 삭제처리
     *
     * @param param
     */
    public void updateDeleteAsnItemByAsn(Map<String, Object> param) {
        spAsnItemRepository.updateDeleteAsnItemByAsn(param);
    }

    /**
     * 납품예정 품목 삭제
     *
     * @param param
     */
    public void deleteAsnItem(Map<String, Object> param) {
        spAsnItemRepository.deleteAsnItem(param);
    }

    /**
     * 발주품목의 납품예정 수량 조회
     *
     * @param keyParam
     * @return
     */
    public List<Map<String, Object>> searchAsnQuantityOfPoItem(Map<String, Object> keyParam) {
        return spAsnItemRepository.searchAsnQuantityOfPoItem(keyParam);
    }

    /**
     * 납품예정 품목 리스트 조회
     *
     * @param arData
     * @return
     */
    public List<Map<String, Object>> searchAsnItem(Map<String, Object> arData) {
        return spAsnItemRepository.searchAsnItem(arData);
    }

    /**
     * PO ITEM으로 검수요청품목 리스트 조회
     *
     * @param poItem
     * @return
     */
    public List<Map<String, Object>> searchAsnItemByPoItem(Map<String, Object> poItem) {
        return spAsnItemRepository.searchAsnItemByPoItem(poItem);
    }

    /**
     * 발주품목별 수량 확인
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> checkQuantityOfPoItem(Map<String, Object> param) {
        return spAsnItemRepository.checkQuantityOfPoItem(param);
    }
	
	/**
     * 기성품목 목록 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> searchProgressPaymentRequestItem(Map<String, Object> param) {
        return spAsnItemRepository.searchProgressPaymentRequestItem(param);
    }

}
