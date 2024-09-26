package smartsuite.app.sp.rfx.rfx.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class SpRfxBidItemRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-rfx-bid-item.";
	
	/**
	 * list rfx qta item 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 17
	 */
	public List findListRfxBidItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidItem", param);
	}
	
	/**
	 * 이전차수의 list rfx qta item 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 17
	 */
	public List findListRfxItemWithPrevRevBidItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItemWithPrevRevBidItem", param);
	}
	
	/**
	 * list rfx item 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 17
	 * @Method Name : findListRfxItemEXC
	 */
	public List findListRfxItemEXC(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItemEXC", param);
	}
	
	/**
	 * RFx 업체 견적서 품목 신규생성
	 * @param rfxBidItem
	 */
	public void insertRfxBidItem(Map rfxBidItem) {
		sqlSession.insert(NAMESPACE + "insertRfxBidItem", rfxBidItem);
	}
	
	/**
	 * RFx 업체 견적서 품목 수정
	 * @param rfxBidItem
	 */
	public void updateRfxBidItem(Map rfxBidItem) {
		sqlSession.update(NAMESPACE + "updateRfxBidItem", rfxBidItem);
	}
}
