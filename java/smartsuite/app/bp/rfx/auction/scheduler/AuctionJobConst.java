package smartsuite.app.bp.rfx.auction.scheduler;

public class AuctionJobConst {

	public static final String AUCTION_SERVICE_CLASS_NAME	= "smartsuite.app.bp.rfx.auction.service.AuctionService";
	
	public static final String AUCTION_JOB_GROUP			= "AUCTION";
	
	public static final String AUCTION_START_JOB_NAME		= "AUCTION_START";
	public static final String AUCTION_START_METHOD_NAME	= "startAuction";
	
	public static final String AUCTION_CLOSE_JOB_NAME		= "AUCTION_CLOSE";
	public static final String AUCTION_CLOSE_METHOD_NAME	= "closeAuction";
	
	/** 자동연장 최대 횟수 */
	public static final int MAX_AUTO_EXT_COUNT = 3;
	
}
