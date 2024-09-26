package smartsuite.app.common.shoppingcart.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class ShoppingCartRepository {

    @Inject
    private SqlSession sqlSession;

    /** The NAMESPACE. */
    private static final String NAMESPACE = "shpcart.";
    public Map countShoppingCart(Map param) {
        return sqlSession.selectOne(NAMESPACE + "countShoppingCart");
    }

    public void insertUprcItemToShoppingCart(Map param) {
        sqlSession.insert(NAMESPACE + "insertUprcItemToShoppingCart", param);
    }
    public Object findListThumbnail(String thnlAthfUuid) {
        return sqlSession.selectList(NAMESPACE + "findListThumbnail", thnlAthfUuid);
    }

    public Map findUprcInfoWithCatalog(Map param) {
        return sqlSession.selectOne(NAMESPACE + "findUprcInfoWithCatalog", param);
    }


    public void updateUprcItemToShoppingCart(Map param) {
        sqlSession.update(NAMESPACE + "updateUprcItemToShoppingCart", param);
    }

    public List<Map<String, Object>> findListShoppingCartList(Map param) {
        return sqlSession.selectList(NAMESPACE + "findListShoppingCartList", param);
    }

    public void deleteShoppingCartItem(Map delete) {
        sqlSession.update(NAMESPACE + "deleteShoppingCartItem", delete);
    }

    public void updateShoppingCartItemStatusD(Map delete) {
        sqlSession.update(NAMESPACE + "updateShoppingCartItemStatusD", delete);
    }
}
