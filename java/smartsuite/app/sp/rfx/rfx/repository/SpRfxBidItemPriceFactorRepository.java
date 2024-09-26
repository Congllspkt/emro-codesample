package smartsuite.app.sp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpRfxBidItemPriceFactorRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-rfx-bid-item-price-factor.";
	
	public Map findRfxBid(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxBid", param);
	}
	
	/**
	 * 견적 품목 별 원가구성항목 목록 조회
	 * 
	 * @param param
	 * @return
	 */
	public List findListBidItemPriceFactor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListBidItemPriceFactor", param);
	}
	
	/**
	 * 이전차수의 견적 품목 별 원가구성항목 목록 조회
	 * @param param
	 * @return
	 */
	public List findListPrevRevBidItemPriceFactor(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListPrevRevBidItemPriceFactor", param);
	}
	
	/**
	 * 원가구성항목 내역 작성 신규생성
	 * @param param
	 */
	public void insertBidPriceFactor(Map param) {
		sqlSession.insert(NAMESPACE + "insertBidPriceFactor", param);
	}
	
	/**
	 * 원가구성항목 내역 작성 수정
	 * @param param
	 */
	public void updateBidPriceFactor(Map param) {
		sqlSession.update(NAMESPACE + "updateBidPriceFactor", param);
	}
}
