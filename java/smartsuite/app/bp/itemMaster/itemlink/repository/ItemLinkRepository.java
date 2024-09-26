package smartsuite.app.bp.itemMaster.itemlink.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class ItemLinkRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "item-link.";

    public List<Map<String, Object>> findListItemLink(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE + "findListItemLink", param);
    }
}
