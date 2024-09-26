package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class RfxMailRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "rfx-mail.";

	public Map findRfxResultInfo(Map rfxData) {
		return sqlSession.selectOne(NAMESPACE + "findRfxResultInfo", rfxData);
	}

	public List findListRfxResultPassVd(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxResultPassVd", rfxInfo);
	}

	public List findListRfxResultNoPassVd(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxResultNoPassVd", rfxInfo);
	}

	public List findListRfxBidSubmit(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxBidSubmit", rfxInfo);
	}

	public List findListRfxVdBidSubmit(Map rfxInfo) {
		return sqlSession.selectList(NAMESPACE + "findListRfxVdBidSubmit", rfxInfo);
	}
}
