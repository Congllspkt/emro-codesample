package smartsuite.app.bp.itemMaster.item.repository;

import javax.inject.Inject;
import javax.print.attribute.standard.MediaSize;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ItemRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "item.";

    public List<Map<String, Object>> findListItem(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findListItem", param);
    }

    public Map<String, Object> findInfoItem(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findInfoItem", param);
    }
}
