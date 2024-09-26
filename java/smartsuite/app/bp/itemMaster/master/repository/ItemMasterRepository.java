package smartsuite.app.bp.itemMaster.master.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class ItemMasterRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "item-master.";

    public List<Map<String, Object>> findListItemMaster(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findListItemMaster", param);
    }

    public Map<String, Object> findInfoItemMaster(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findInfoItemMaster", param);
    }

    public Map<String, Object> findInfoItemMasterForChange(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findInfoItemMasterForChange", param);
    }
    
    public Map findItemByItemCd(Map param) {
        return sqlSession.selectOne(NAMESPACE + "findItemByItemCd", param);
    }
}
