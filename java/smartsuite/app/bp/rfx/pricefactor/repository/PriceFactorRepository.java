package smartsuite.app.bp.rfx.pricefactor.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class PriceFactorRepository {

    @Inject
    SqlSession sqlSession;

    private static final String NAMESPACE = "price-factor.";

    /**
     * CostFactor를 등록한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : insertPriceFactor
     */
    public void insertPriceFactor(Map<String, Object> param) {
        sqlSession.insert(NAMESPACE+"insertPriceFactor", param);
    }

    /**
     * 가격군을 등록한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : insertPriceGroup
     */
    public void insertPriceGroup(Map<String, Object> param) {
        sqlSession.insert(NAMESPACE+"insertPriceGroup", param);
    }

    /**
     * 가격군 CostFactor을 등록한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : insertPriceGroupFactor
     */
    public void insertPriceGroupFactor(Map<String, Object> param) {
        sqlSession.insert(NAMESPACE+"insertPriceGroupFactor", param);
    }

    /**
     * CostFactor를 수정한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : updatePriceFactor
     */
    public void updatePriceFactor(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"updatePriceFactor", param);
    }

    /**
     * 가격군을 수정한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : updatePriceGroup
     */
    public void updatePriceGroup(Map<String, Object> param) {
        sqlSession.update(NAMESPACE+"updatePriceGroup", param);
    }

    /**
     * CostFactor를 삭제한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : deletePriceFactor
     */
    public void deletePriceFactor(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deletePriceFactor", param);
    }

    /**
     * 가격군을 삭제한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : deletePriceGroup
     */
    public void deletePriceGroup(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deletePriceGroup", param);
    }

    /**
     * 가격군 삭제 시 가격군 CostFactor을 삭제한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : deletePriceGroupFactor
     */
    public void deletePriceGroupFactorByPriceGroup(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deletePriceGroupFactorByPriceGroup", param);
    }

    /**
     * 가격군 CostFactor을 삭제한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @Date : 2016. 2. 2
     * @Method Name : deletePriceGroupFactor
     */
    public void deletePriceGroupFactor(Map<String, Object> param) {
        sqlSession.delete(NAMESPACE+"deletePriceGroupFactor", param);
    }

    /**
     * CostFactor를 카운트한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the count CostFactor
     * @Date : 2016. 2. 2
     * @Method Name : getCountPriceFactor
     */
    public int getCountPriceFactor(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"getCountPriceFactor", param);
    }

    /**
     * 가격군을 카운트한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the count price group
     * @Date : 2016. 2. 2
     * @Method Name : getCountPriceGroup
     */
    public int getCountPriceGroup(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"getCountPriceGroup", param);
    }

    /**
     * 가격군 Factor를 카운트한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the count price group factor
     * @Date : 2016. 2. 2
     * @Method Name : getCountPriceGroupFactor
     */
    public int getCountPriceGroupFactor(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"getCountPriceGroupFactor", param);
    }

    /**
     * CostFactor 목록을 조회한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the list CostFactor
     * @Date : 2016. 2. 2
     * @Method Name : findListPriceFactor
     */
    public FloaterStream findListPriceFactor(Map<String, Object> param) {
        // 대용량 처리
        return new QueryFloaterStream(sqlSession, NAMESPACE+"findListPriceFactor", param);
    }

    /**
     * 가격군 목록을 조회한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the list price group
     * @Date : 2016. 2. 2
     * @Method Name : findListPriceGroup
     */
    public FloaterStream findListPriceGroup(Map<String, Object> param) {
        // 대용량 처리
        return new QueryFloaterStream(sqlSession, NAMESPACE+"findListPriceGroup", param);
    }

    /**
     * 가격군 CostFactor 목록을 조회한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return the list price group
     * @Date : 2016. 2. 2
     * @Method Name : findListPriceGroupFactor
     */
    public List<Map<String, Object>> findListPriceGroupFactor(Map<String, Object> param) {
        return sqlSession.selectList(NAMESPACE+"findListPriceGroupFactor", param);
    }

    /**
     * 가격군 상세정보를 조회한다.
     *
     * @author : JongKyu Kim
     * @param param the param
     * @return map
     * @Date : 2016. 2. 2
     * @Method Name : findPriceGroup
     */
    public Map<String, Object> findPriceGroup(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE+"findPriceGroup", param);
    }
}