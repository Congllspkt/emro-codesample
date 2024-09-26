package smartsuite.app.common.status.repository;

import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class InvStatusRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "inv-status.";
	
	public void saveDraftInvoice(Map param) {
		sqlSession.update(NAMESPACE + "saveDraftInvoice", param);
	}
	public void confirmInvoice(Map param) {
		sqlSession.update(NAMESPACE + "confirmInvoice", param);
	}
	public void cancelInvoice(Map param) {
		sqlSession.update(NAMESPACE + "cancelInvoice", param);
	}
	public void requestInvoice(Map param){
		sqlSession.update(NAMESPACE + "requestInvoice", param);
	}
	public void returnInvoice(Map param) {
		sqlSession.update(NAMESPACE + "returnInvoice", param);
	}
}
