package smartsuite.app.sp.rfx.nego.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpNegoItemRepository {
	private static final String NAMESPACE = "sp-nego-item.";
	
	@Inject
	SqlSession sqlSession;
	

	public List findNegoQtaDetail(Map param) {
		return sqlSession.selectList(NAMESPACE + "findNegoQtaDetail", param);
	}

	public void saveNegoDetail(Map item) {
		sqlSession.update(NAMESPACE + "saveNegoDetail", item);
	}
	
	/**
	 * 협상품목 가격(단가, 금액) 초기화
	 * @param param
	 */
	public void initNegoItemPrice(Map param) {
		sqlSession.update(NAMESPACE + "initNegoItemPrice", param);
	}
}
