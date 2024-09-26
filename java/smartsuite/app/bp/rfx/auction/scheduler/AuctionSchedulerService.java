package smartsuite.app.bp.rfx.auction.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.rfx.auction.repository.AuctionSchedulerRepository;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

@Service
@Transactional
public class AuctionSchedulerService {

	/** The schedule service. */
	@Inject
	ScheduleService scheduleService;

	@Inject
	AuctionSchedulerRepository auctionSchedulerRepository;
	
	
	/**
	 * 역경매 공고 시
	 * <pre>
	 * 1. 역경매 견적 시작
	 * 2. 역경매 견적 마감 스케쥴러 등록
	 * </pre>
	 * @author kh_lee
	 * @param param {rfx_uuid: 아이디}
	 */
	public void noticeAuction(Map<String, Object> param) {
		if(param.get("rfx_uuid") != null) {
			String rfxId = (String)param.get("rfx_uuid");
			
			// 기존 등록된 역경매 시작, 마감 스케쥴러 삭제
			deleteStartAuctionJob(rfxId);
			deleteCloseAuctionJob(rfxId);
			
			Map<String, Object> auction = auctionSchedulerRepository.findAuction(param);
			if(MapUtils.isNotEmpty(auction)) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("rfx_uuid" , rfxId);
				data.put("rfx_typ_ccd", auction.get("rfx_typ_ccd"));
				
				Object[] args = new Object[]{data};
				Date rfxStartDt = (Date)auction.get("rfx_st_dttm");
				Date rfxCloseDt = (Date)auction.get("rfx_clsg_dttm");
				
				registStartAuctionJob(rfxId, rfxStartDt, args);
				registCloseAuctionJob(rfxId, rfxCloseDt, args);
			}
		}
	}
	
	/**
	 * 역경매 견적 마감 일시 변경 시
	 * <pre>
	 * 1. 기존 마감 스케쥴러 삭제
	 * 2. 신규 마감 스케쥴러 등록
	 * </pre>
	 * @author kh_lee
	 * @param param {rfx_uuid: 아이디}
	 */
	public void changeAuctionCloseTime(Map<String, Object> param) {
		if(param.get("rfx_uuid") != null) {
			String rfxId = (String)param.get("rfx_uuid");
			
			// 기존 등록된 마감 스케쥴러 삭제
			deleteCloseAuctionJob(rfxId);

			Map<String, Object> auction = auctionSchedulerRepository.findAuction(param);
			if(MapUtils.isNotEmpty(auction)) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("rfx_uuid" , rfxId);
				data.put("rfx_typ_ccd", auction.get("rfx_typ_ccd"));
				
				Object[] args = new Object[]{data};
				Date rfxCloseDt = (Date)auction.get("rfx_clsg_dttm");
				
				// 변경된 마감 정보로 마감 스케쥴러 등록
				registCloseAuctionJob(rfxId, rfxCloseDt, args);
			}
		}
	}
	
	/**
	 * 역경매 강제마감 시
	 * <pre>
	 * 1. 기존 견적 마감 스케쥴러 삭제
	 * </pre>
	 * @author kh_lee
	 * @param param {rfx_uuid: 아이디}
	 */
	public void byPassCloseAuction(Map<String, Object> param) {
		if(param.get("rfx_uuid") != null) {
			String rfxId = (String)param.get("rfx_uuid");
			deleteCloseAuctionJob(rfxId);
		}
	}
	
	/**
	 * 역경매 취소 시
	 * <pre>
	 * 1. 기존 견적 시작 스케쥴러 삭제
	 * 2. 기존 견적 마감 스케쥴러 삭제
	 * </pre>
	 * @author kh_lee
	 * @param param {rfx_uuid: 아이디}
	 */
	public void cancelAuction(Map<String, Object> param) {
		if(param.get("rfx_uuid") != null) {
			String rfxId = (String)param.get("rfx_uuid");
			
			deleteStartAuctionJob(rfxId);
			deleteCloseAuctionJob(rfxId);
		}
	}
	
	/**
	 * 견적 시작 스케쥴러 등록
	 * @author kh_lee
	 * @param rfxId : 아이디
	 * @param rfxStartDt : 시작일시
	 * @param args
	 * @throws Exception
	 */
	private void registStartAuctionJob(String rfxId, Date rfxStartDt, Object[] args) {
		try {
			scheduleService.registSchedule(
					Class.forName(AuctionJobConst.AUCTION_SERVICE_CLASS_NAME),
					AuctionJobConst.AUCTION_START_METHOD_NAME,
					args,
					rfxStartDt,
					AuctionJobConst.AUCTION_JOB_GROUP,
					rfxId,
					AuctionJobConst.AUCTION_START_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 견적 마감 스케쥴러 등록
	 * @author kh_lee
	 * @param rfxId : 아이디
	 * @param rfxCloseDt : 마감 일시
	 * @param args
	 * @throws Exception
	 */
	private void registCloseAuctionJob(String rfxId, Date rfxCloseDt, Object[] args) {
		try {
			scheduleService.registSchedule(
					Class.forName(AuctionJobConst.AUCTION_SERVICE_CLASS_NAME),
					AuctionJobConst.AUCTION_CLOSE_METHOD_NAME,
					args,
					rfxCloseDt,
					AuctionJobConst.AUCTION_JOB_GROUP,
					rfxId,
					AuctionJobConst.AUCTION_CLOSE_JOB_NAME,
					null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 견적 시작 스케쥴러 삭제
	 * @author kh_lee
	 * @param rfxId : 아이디
	 * @throws Exception
	 */
	private void deleteStartAuctionJob(String rfxId) {
		try {
			scheduleService.removeScheduleTrigger(
					Class.forName(AuctionJobConst.AUCTION_SERVICE_CLASS_NAME),
					AuctionJobConst.AUCTION_START_METHOD_NAME,
					AuctionJobConst.AUCTION_JOB_GROUP,
					rfxId);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
	
	/**
	 * 견적 마감 스케쥴러 삭제
	 * @author kh_lee
	 * @param rfxId : 아이디
	 * @throws Exception
	 */
	private void deleteCloseAuctionJob(String rfxId) {
		try {
			scheduleService.removeScheduleTrigger(
					Class.forName(AuctionJobConst.AUCTION_SERVICE_CLASS_NAME),
					AuctionJobConst.AUCTION_CLOSE_METHOD_NAME,
					AuctionJobConst.AUCTION_JOB_GROUP,
					rfxId);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}
}
