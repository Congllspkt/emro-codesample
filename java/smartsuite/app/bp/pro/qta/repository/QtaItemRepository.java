package smartsuite.app.bp.pro.qta.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class QtaItemRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "qta-item.";

    public List<Map<String, Object>> findListQtaItem(Map<String, Object> param){
        return sqlSession.selectList(NAMESPACE+"findListQtaItem",param);
    }

    public void insertQtaItem(Map<String, Object> row) {
        sqlSession.insert(NAMESPACE+"insertQtaItem",row);
    }

    public void deleteQtaItems(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deleteQtaItems",param);
    }

    public void deleteQtaItem(Map<String, Object> row) {
        sqlSession.delete(NAMESPACE+"deleteQtaItem",row);
    }

    public void updateQtaItem(Map<String, Object> row) {
        sqlSession.update(NAMESPACE+"updateQtaItem",row);
    }

    public List<Map<String, Object>> findListQtaItemByItemCd(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE+"findListQtaItemByItemCd",param);
    }

    public List<Map<String, Object>> findListQtaItemByQta(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE+"findListQtaItemByQta",param);
    }
}
