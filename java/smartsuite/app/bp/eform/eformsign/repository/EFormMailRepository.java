package smartsuite.app.bp.eform.eformsign.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 간편서명 메일 관련 처리하는 레파지토리 Class입니다.
 *
 * @FileName EFormSignRepository.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes" })
public class EFormMailRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "eformsign-mail.";
	
	public List findCntrSgndVdUserList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findCntrSgndVdUserList", param);
	}

}