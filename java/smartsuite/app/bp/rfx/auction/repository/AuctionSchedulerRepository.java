package smartsuite.app.bp.rfx.auction.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.scheduler.AuctionJobConst;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuctionSchedulerRepository {

	/** The sql session. */
	@Inject
	SqlSession sqlSession;
	
	/** The NAMESPACE. */
	private static final String NAMESPACE = "auction.";


	public Map findAuction(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAuction", param);
	}
}
