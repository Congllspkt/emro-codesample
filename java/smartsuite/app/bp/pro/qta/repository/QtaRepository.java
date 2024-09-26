package smartsuite.app.bp.pro.qta.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class QtaRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "qta.";

    public void insertQtaHeader(Map<String, Object> qtaData) {
        sqlSession.insert(NAMESPACE+"insertQtaHeader",qtaData);
    }

    public void updateQtaHeader(Map<String, Object> qtaData) {
        sqlSession.update(NAMESPACE+"updateQtaHeader",qtaData);
    }

    public void deleteQtaHeader(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deleteQtaHeader",param);
    }

    public Map<String, Object> findQta(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"findQta",param);
    }

    public List<Map<String, Object>> findListQta(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE+"findListQta",param);
    }
}
