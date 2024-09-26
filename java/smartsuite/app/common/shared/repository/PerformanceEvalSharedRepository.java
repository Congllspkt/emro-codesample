package smartsuite.app.common.shared.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.PerformanceEvalException;
import smartsuite.app.common.PfmcConst;

import javax.inject.Inject;
import java.util.Map;

/**
 * SRM shared 서비스 Class입니다.
 *
 * @author mgPark
 * @see
 * @FileName PerformanceEvalSharedService.java
 * @package smartsuite.app.common.shared.service
 * @Since 2016. 6. 11
 * @변경이력 : [2016. 6. 11] mgPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "unchecked" })
public class PerformanceEvalSharedRepository {

	@Inject
	private SqlSession sqlSession;

	private static final String NAMESPACE = "pfmc-shared.";

	/**
	 * 에러 로그를 저장한다.
	 *
	 * @param srmErr the srm err
	 */
	public void insertEvalErrorLog(Map<String, Object> errInfo) {
		sqlSession.insert(NAMESPACE + "insertEvalErrorLog", errInfo);
	}

}
