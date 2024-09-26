package smartsuite.app.bp.pro.qta.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class QtaItemHistrecRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "qta-item-histrec.";

    public List<Map<String, Object>> findListQtaHistrecByApplDt(Map<String,Object> param){
        return sqlSession.selectList(NAMESPACE+"findListQtaHistrecByApplDt",param);
    }

    public void deleteQtaItemHistrec(Map<String, Object> row) {
        sqlSession.delete(NAMESPACE+"deleteQtaItemHistrec",row);
    }

    public void updateQtaItemHistrec(Map<String, Object> row) {
        sqlSession.update(NAMESPACE+"updateQtaItemHistrec",row);
    }

    public void insertQtaItemHistrec(Map<String, Object> qtaItem) {
        sqlSession.insert(NAMESPACE+"insertQtaItemHistrec",qtaItem);
    }
}
