package smartsuite.app.shared.mail.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.auth.repository.UserRepository;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class ContractMailRepository {

	@Inject
	SqlSession sqlSession;

	@Inject
	UserRepository userRepository;

	private static final String NAMESPACE = "contract-mail.";

	public Map findUserInfo(Map param) {
		return userRepository.findUserByUserId(param);
	}

}
