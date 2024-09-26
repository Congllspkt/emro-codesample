package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxItemRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-item.";
	
	/**
	 * RFx 품목 신규생성
	 * @param rfxItem
	 */
	public void insertRfxItem(Map rfxItem) {
		sqlSession.insert(NAMESPACE + "insertRfxItem", rfxItem);
	}
	
	/**
	 * RFx 품목 수정
	 * @param rfxItem
	 */
	public void updateRfxItem(Map rfxItem) {
		sqlSession.update(NAMESPACE + "updateRfxItem", rfxItem);
	}
	
	/**
	 * RFx 품목 삭제
	 * @param rfxItem
	 */
	public void deleteRfxItem(Map rfxItem) {
		sqlSession.delete(NAMESPACE + "deleteRfxItem", rfxItem);
	}
	
	/**
	 * list rfx item 조회한다.
	 *
	 * @param param the param
	 */
	public List searchRfxItemByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchRfxItemByRfx", param);
	}
	
	public List selectRfxItemStlInfo(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectRfxItemStlInfo", param);
	}
	
	public void updateRfxItemStlInfo(Map rfxItem) {
		sqlSession.update(NAMESPACE + "updateRfxItemStlInfo", rfxItem);
	}
	
	public List findListRfxItemDefaultDataByAiItemIds(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxItemDefaultDataByAiItemIds", param);
	}
}
