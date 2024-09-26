package smartsuite.app.sp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpPoItemRepository {
    /** The sql session. */
    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "sp-po-item.";

    public FloaterStream findListPoItem(Map<String, Object> param) {
        return new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoItem", param);
    }

    public List findListPoItemByPoId(Map<String, Object> param) {
        return  sqlSession.selectList(NAMESPACE + "findListPoItemByPoId", param);
    }
	
	/**
     * 초기 기성요청 품목 조회
     * @param param
     * @return
     */
    public List<Map<String, Object>> searchInitProgressPaymentRequestItemByPoUuid(Map<String, Object> param) {
        return  sqlSession.selectList(NAMESPACE + "searchInitProgressPaymentRequestItemByPoUuid", param);
    }
	
	/**
     * 발주품목의 납품예정 생성여부 체크
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> checkAsnCreatableOfPoItem(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "checkAsnCreatableOfPoItem", param);
    }
	
	/**
     * 발주품목의 납품예정 수량 수정
     *
     * @param poItem
     */
    public void updateAsnQuantityOfPoItem(Map<String, Object> poItem) {
        sqlSession.update(NAMESPACE + "updateAsnQuantityOfPoItem", poItem);
    }
}
