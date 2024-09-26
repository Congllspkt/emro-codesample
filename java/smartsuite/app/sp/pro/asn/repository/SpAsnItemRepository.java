package smartsuite.app.sp.pro.asn.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpAsnItemRepository {
    private static final String NAMESPACE = "sp-asn-item.";

    @Inject
    private SqlSession sqlSession;

    /**
     * 납품예정 품목 생성
     *
     * @param param
     */
    public void insertAsnItem(Map<String, Object> param) {
        sqlSession.insert(NAMESPACE + "insertAsnItem", param);
    }

    /**
     * 납품예정 품목 수정
     *
     * @param arItem
     */
    public void updateAsnItem(Map<String, Object> arItem) {
        sqlSession.update(NAMESPACE + "updateAsnItem", arItem);
    }

    /**
     * 납품예정 품목 삭제처리
     *
     * @param arData
     */
    public void updateDeleteAsnItemByAsn(Map<String, Object> arData) {
        sqlSession.insert(NAMESPACE + "updateDeleteAsnItemByAsn", arData);
    }

    /**
     * 납품예정 품목 삭제
     *
     * @param arData
     */
    public void deleteAsnItem(Map<String, Object> arData) {
        sqlSession.delete(NAMESPACE + "deleteAsnItem", arData);
    }

    /**
     * 발주품목의 납품예정 수량 조회
     *
     * @param poItem
     * @return
     */
    public List<Map<String, Object>> searchAsnQuantityOfPoItem(Map<String, Object> poItem) {
        return sqlSession.selectList(NAMESPACE + "searchAsnQuantityOfPoItem", poItem);
    }

    /**
     * 납품예정 품목 리스트 조회
     *
     * @param arData
     * @return
     */
    public List<Map<String, Object>> searchAsnItem(Map<String, Object> arData) {
        return sqlSession.selectList(NAMESPACE + "searchAsnItem", arData);
    }

    /**
     * PO ITEM으로 검수요청품목 리스트 조회
     *
     * @param poItem
     * @return
     */
    public List<Map<String, Object>> searchAsnItemByPoItem(Map<String, Object> poItem) {
        return sqlSession.selectList(NAMESPACE + "searchAsnItemByPoItem", poItem);
    }

    /**
     * 발주품목별 수량 확인
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> checkQuantityOfPoItem(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "checkQuantityOfPoItem", param);
    }
	
	/**
     * 기성품목 목록 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> searchProgressPaymentRequestItem(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "searchProgressPaymentRequestItem", param);
    }
}
