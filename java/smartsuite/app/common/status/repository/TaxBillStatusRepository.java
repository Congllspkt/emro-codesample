package smartsuite.app.common.status.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class TaxBillStatusRepository {
	@Inject
	private SqlSession sqlSession;
	
	private static final String NAMESPACE = "taxbill-status.";
	public void requestTaxBillTxHd(Map param) {
		sqlSession.update(NAMESPACE + "requestTaxBillTxHd", param);
	}
	public void requestTaxBillTxDt(Map param) {
		sqlSession.update(NAMESPACE + "requestTaxBillTxDt", param);
	}
}
