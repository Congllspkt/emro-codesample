package smartsuite.app.bp.rfx.auction.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.auction.repository.AuctionBidRepository;
import smartsuite.app.bp.rfx.auction.strategy.AuctionAmountRankingStrategy;
import smartsuite.app.bp.rfx.auction.validator.AuctionValidator;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
@Transactional
public class AuctionBidService {

	@Inject
	AuctionBidRepository auctionBidRepository;

	@Inject
	AuctionBidItemService auctionBidItemService;

	@Inject
	RfxStatusService rfxStatusService;

	@Inject
	RfxEventPublisher rfxEventPublisher;

	@Inject
	AuctionValidator auctionValidator;

	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;

	@Inject
	StdFileService stdFileService;

	@Inject
	AuctionAmountRankingStrategy rankingStrategy;

	/**
	 * 역경매 견적서 작성 (업체대행) 목록을 조회한다.
	 *
	 * @param param
	 * @return list : 역경매 견적서 작성 (업체대행) 목록
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 */
	public List findListAuctionBid(Map param) {
		return auctionBidRepository.findListAuctionBid(param);
	}

	/**
	 * 역경매 견적서 작성 (업체대행) 헤더정보를 조회한다.
	 *
	 * @param param
	 * @return map : 역경매 견적서 작성 (업체대행) 헤더정보
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 */
	public Map findAuctionBid(Map param) {
		return auctionBidRepository.findAuctionBid(param);
	}

	private Map saveAuctionBid(boolean isNew, Map auctionBid, Map rfxBidData, List rfxBidItems) {
		// 신규 insert
		if(isNew) {
			if(auctionBid.get("rfx_bid_uuid") == null) {
				rfxBidData.put("rfx_bid_uuid", UUID.randomUUID().toString());
				rfxBidData.put("rfx_bid_no", sharedService.generateDocumentNumber("QT"));
				rfxBidData.put("rfx_bid_revno", 1);
			} else {
				// 기존 견적 제출 건이 존재하는 경우
				Map prevRfxBid = Maps.newHashMap();
				prevRfxBid.put("rfx_bid_uuid", auctionBid.get("rfx_bid_uuid"));
				prevRfxBid.put("rfx_bid_efct_yn", "N");

				// 기존 견적 무효
				auctionBidRepository.updateAuctionBidValidYn(prevRfxBid);

				// 신규 아이디 생성, 차수 증가
				rfxBidData.put("rfx_bid_uuid", UUID.randomUUID().toString());
				rfxBidData.put("rfx_bid_no", auctionBid.get("rfx_bid_no"));
				rfxBidData.put("rfx_bid_revno", Integer.parseInt(auctionBid.get("rfx_bid_revno").toString()) + 1);
			}

			auctionBidRepository.insertAuctionBid(rfxBidData);
			auctionBidItemService.insertAuctionBidItem(rfxBidData, rfxBidItems);
		} else {
			// 기존 견적 존재 시
			rfxBidData.put("rfx_bid_uuid", auctionBid.get("rfx_bid_uuid"));
			rfxBidData.put("rfx_bid_no", auctionBid.get("rfx_bid_no"));
			rfxBidData.put("rfx_bid_revno", auctionBid.get("rfx_bid_revno"));

			auctionBidRepository.updateAuctionBid(rfxBidData);
			auctionBidItemService.updateAuctionBidItem(rfxBidData, rfxBidItems);
		}

		return rfxBidData;
	}

	/**
	 * 역경매 견적서 작성 (업체대행) 임시저장한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 견적서 작성 (업체대행) 저장 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 */
	public ResultMap temporarySaveAuctionBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "CRNG"); // 저장
		rfxBidData.put("rfx_bid_efct_yn", "Y");

		// 기존 작성된 유효 견적 조회
		Map auctionBidInfo = this.findAuctionBid(rfxBidData);

		ResultMap validator = auctionValidator.saveAuctionBidForAgent(auctionBidInfo);
		if(!validator.isSuccess()) {
			Map resultMap = Maps.newHashMap();
			resultMap.put("rfx_uuid", auctionBidInfo.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", auctionBidInfo.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", auctionBidInfo.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", auctionBidInfo.get("rfx_sts_ccd"));

			return ResultMap.builder()
					.resultStatus(validator.getResultStatus())
					.resultData(resultMap)
					.build();
		}

		boolean isNew = false;
		if(auctionBidInfo.get("rfx_bid_uuid") == null) {
			isNew = true;
		}
		rfxBidData = this.saveAuctionBid(isNew,
		                                 auctionBidInfo,
		                                 rfxBidData,
		                                 (List) param.get("rfxBidItems"));

		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 역경매 견적서 작성 (업체대행) 제출한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 견적서 작성 (업체대행) 제출 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 */
	public ResultMap submitAuctionBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "SUBM"); // 제출
		rfxBidData.put("rfx_bid_efct_yn", "Y");

		// 기존 작성된 유효 견적 조회
		Map auctionBidInfo = auctionBidRepository.findAuctionBid(rfxBidData);

		ResultMap validator = auctionValidator.submitAuctionBidForAgent(auctionBidInfo);
		if(!validator.isSuccess()) {
			Map resultMap = Maps.newHashMap();
			resultMap.put("rfx_uuid", auctionBidInfo.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", auctionBidInfo.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", auctionBidInfo.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", auctionBidInfo.get("rfx_sts_ccd"));

			return ResultMap.builder()
					.resultStatus(validator.getResultStatus())
					.resultData(resultMap)
					.build();
		}

		boolean isNew = false;
		// 제출인 경우 신규이거나 기존 제출건이 존재하더라도 가능
		if(auctionBidInfo.get("rfx_bid_uuid") == null || "SUBM".equals(auctionBidInfo.get("rfx_bid_sts_ccd"))) {
			isNew = true;
		}
		rfxBidData = this.saveAuctionBid(isNew,
		                                 auctionBidInfo,
		                                 rfxBidData,
		                                 (List) param.get("rfxBidItems"));

		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}

	/**
	 * 역경매 견적서 작성 (업체대행) 포기한다.
	 *
	 * @param param
	 * @return resultMap : 역경매 견적서 작성 (업체대행) 포기 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 */
	public ResultMap abandonAuctionBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "GUP"); // 포기
		rfxBidData.put("rfx_bid_efct_yn", "Y");

		// 기존 작성된 유효 견적 조회
		Map auctionBidInfo = auctionBidRepository.findAuctionBid(rfxBidData);

		ResultMap validator = auctionValidator.abandonAuctionBidForAgent(auctionBidInfo);
		if(!validator.isSuccess()) {
			Map resultMap = Maps.newHashMap();
			resultMap.put("rfx_uuid", auctionBidInfo.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", auctionBidInfo.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", auctionBidInfo.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", auctionBidInfo.get("rfx_sts_ccd"));

			return ResultMap.builder()
					.resultStatus(validator.getResultStatus())
					.resultData(resultMap)
					.build();
		}

		// 포기인 경우 신규일 때만 저장로직을 수행한다.
		// 신규가 아닌 경우 견적서 헤더에 포기 값만 업데이트한다.
		if(auctionBidInfo.get("rfx_bid_uuid") == null) {
			// 신규인 경우 화면에서 품목정보가 넘어오지 않으므로 견적요청 품목 기반으로 조회 후 저장한다.
			List rfxBidItems = auctionBidItemService.findListAuctionBidItem(rfxBidData);
			rfxBidData = this.saveAuctionBid(true,
			                                 auctionBidInfo,
			                                 rfxBidData,
			                                 rfxBidItems);
		} else {
			// 기존 견적 저장/제출 건이 존재하는 경우 update
			Map updateBid = Maps.newHashMap();
			updateBid.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			updateBid.put("rfx_bid_sts_ccd", "GUP"); // 포기
			updateBid.put("gup_rsn", rfxBidData.get("gup_rsn"));

			auctionBidRepository.updateAuctionBidByAbandon(updateBid);
		}

		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}

	public Map findAuctionBidInfo(Map param) {
		Map auctionBidInfo = this.findAuctionBid(param);
		List rfxBidItems = Lists.newArrayList();

		String rfxProgSts = (String) auctionBidInfo.get("rfx_sts_ccd");
		int rfxRev = Integer.parseInt(auctionBidInfo.get("rfx_rnd").toString());

		// 2차수 부터는 멀티라운드를 통한 견적진행
		if("PRGSG".equals(rfxProgSts) && auctionBidInfo.get("rfx_bid_uuid") == null && rfxRev > 1) {
			Map searchParam = Maps.newHashMap();
			searchParam.put("rfx_uuid", auctionBidInfo.get("rfx_uuid"));
			searchParam.put("rfx_no", auctionBidInfo.get("rfx_no"));
			searchParam.put("rfx_rnd", rfxRev - 1);
			searchParam.put("vd_cd", auctionBidInfo.get("vd_cd"));

			//멀티라운드는 제외품목을 제외한 이전 차수 rfxBid정보 단가를 가져온다.
			auctionBidInfo = auctionBidRepository.findAuctionBidPrevRev(searchParam);
			//견적첨부 복사
			auctionBidInfo.put("athg_uuid", stdFileService.copyFile((String) auctionBidInfo.get("athg_uuid")));

			rfxBidItems = auctionBidItemService.findListAuctionBidItemByRfxNoRfxRev(searchParam);
		} else {
			rfxBidItems = auctionBidItemService.findListAuctionBidItem(auctionBidInfo);
		}

		this.calcRank(auctionBidInfo, rfxBidItems);

		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxBidData", auctionBidInfo);
		resultMap.put("rfxBidItems", rfxBidItems);
		return resultMap;
	}

	/**
	 * 역경매 업체의 랭킹을 계산한다.<br><br>
	 *
	 * @param auctionBidInfo - 역경매 데이터
	 * @param rfxBidItems    - 역경매 품목
	 */
	private void calcRank(Map auctionBidInfo, List rfxBidItems) {
		if(auctionBidInfo == null) {
			return;
		}
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}

		String vdCd = (String) auctionBidInfo.get("vd_cd");
		String vdStlTyp = (String) auctionBidInfo.get("item_slctn_typ_ccd");
		String bidTyp = (String) auctionBidInfo.get("rauc_typ_ccd");
		String amtKey = null;

		if("BYTOT".equals(vdStlTyp)) { // 총액별
			amtKey = "rfx_bid_amt";

			List attends = auctionBidRepository.findListAuctionBidAttend(auctionBidInfo);
			rankingStrategy.computeRanking(attends, amtKey);

			for(Map attend : (List<Map>) attends) {
				boolean isFirstRankAttend = false;

				if((Integer) attend.get("slctn_rank") == 1) {
					if("ENAUC".equals(bidTyp)) {
						// 현재 최저가
						auctionBidInfo.put("best_bid_amt", attend.get(amtKey));
					}
					isFirstRankAttend = true;
				}

				// 조회하는 협력업체
				if(vdCd.equals(attend.get("vd_cd"))) {
					// 나의 입찰가
					auctionBidInfo.put("my_rfx_bid_amt", attend.get(amtKey));

					if("BLBID".equals(bidTyp)) {
						// 나의 현재 순위
						auctionBidInfo.put("my_ranking", isFirstRankAttend ? attend.get("slctn_rank") : "?");
					} else {
						// 나의 현재 순위
						auctionBidInfo.put("my_ranking", attend.get("slctn_rank"));
					}
				}
			}
		} else if("BYITEM".equals(vdStlTyp)) { // 품목별
			for(Map bidItem : (List<Map>) rfxBidItems) {
				amtKey = "rfx_item_subm_amt";

				List itemAttends = auctionBidItemService.findListAuctionBidItemAttend(bidItem);
				rankingStrategy.computeRanking(itemAttends, amtKey);

				for(Map itemAttend : (List<Map>) itemAttends) {
					boolean isFirstRankAttend = false;

					if((Integer) itemAttend.get("slctn_rank") == 1) {
						if("ENAUC".equals(bidTyp)) {
							// 현재 최저가
							bidItem.put("best_item_amt", itemAttend.get(amtKey));
						}
						isFirstRankAttend = true;
					}

					// 조회하는 협력업체
					if(vdCd.equals(itemAttend.get("vd_cd"))) {
						// 나의 입찰가
						bidItem.put("my_item_amt", itemAttend.get(amtKey));

						if("BLBID".equals(bidTyp)) {
							// 나의 현재 순위
							bidItem.put("my_item_ranking", isFirstRankAttend ? itemAttend.get("slctn_rank") : "?");
						} else {
							bidItem.put("my_item_ranking", itemAttend.get("slctn_rank"));
						}
					}
				}
			}
		}
	}

	/**
	 * 역경매 참여 견적 품목 목록을 조회한다. (품목별)
	 *
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 참여 견적 품목 목록
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : findListAllItemAttend
	 */
	public List findListAllItemAttend(Map param) {
		return auctionBidItemService.findListItemAttend(param);
	}

	/**
	 * 역경매 참여견적 목록을 조회한다. (업체별)
	 *
	 * @param param : 검색조건 {rfx_uuid: 아이디}
	 * @return list : 역경매 참여 견적 목록
	 * @author kh_lee
	 * @Date : 2016. 6. 8
	 * @Method Name : findListVdAttend
	 */
	public Map findListVdAttend(Map param) {
		Map result = Maps.newHashMap();
		result.put("auctionQtas", auctionBidRepository.findListVdAttend(param));
		result.put("rfxBidItems", auctionBidItemService.findListVdItemAttend(param));
		return result;
	}

	/**
	 * 총액별 업체선정<br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 *
	 * @param auctionBids
	 */
	public void updateAuctionBidStl(List auctionBids) {
		if(auctionBids == null) {
			return;
		}
		if(auctionBids.isEmpty()) {
			return;
		}

		for(Map auctionBid : (List<Map>) auctionBids) {
			this.updateAuctionBidStl(auctionBid);
		}
	}

	private void updateAuctionBidStl(Map auctionBid) {
		if(auctionBid == null) {
			return;
		}

		auctionBidRepository.updateAuctionBidStl(auctionBid);
		auctionBidItemService.updateAuctionBidStl(auctionBid);
	}

	public void updateAuctionBidItemsStl(Map rfxData, List rfxBidItems) {
		if(rfxData == null) {
			return;
		}
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}

		for(Map auctionBidItem : (List<Map>) rfxBidItems) {
			auctionBidItemService.updateAuctionBidItemStl(auctionBidItem);
		}

		List auctionItems = auctionBidRepository.selectAuctionBids(rfxData);
		for(Map auctionItem : (List<Map>) auctionItems) {
			auctionBidRepository.updateAuctionBidStl(auctionItem);
		}
	}

	/**
	 * 역경매 마감 시 Ranking을 구한다.
	 *
	 * @param rfxData
	 */
	public void computeRanking(Map rfxData) {
		if("BYTOT".equals(rfxData.get("item_slctn_typ_ccd"))) {
			// 총액별 ranking
			this.computeDocRanking(rfxData);
		} else {
			// 품목별 ranking
			this.computeItmRanking(rfxData);
		}
	}

	/**
	 * 총액별 Ranking 구하기
	 *
	 * @param param
	 */
	private void computeDocRanking(Map param) {
		List vdAttends = auctionBidRepository.findListVdAttendAmount(param);
		rankingStrategy.computeRanking(vdAttends, "rfx_bid_amt");

		for(Map attend : (List<Map>) vdAttends) {
			auctionBidRepository.updateAuctionBidRanking(attend);
			auctionBidItemService.updateAuctionBidItemRanking(attend);
		}
	}

	/**
	 * 품목별 Ranking 구하기
	 *
	 * @param param
	 */
	private void computeItmRanking(Map param) {
		List allItemAttends = auctionBidItemService.findListItemAttendAmount(param);
		rankingStrategy.computeRankingByItem(allItemAttends, "rfx_item_uuid", "rfx_item_subm_uprc");

		for(Map itemAttend : (List<Map>) allItemAttends) {
			auctionBidItemService.updateAuctionBidItemRanking(itemAttend);
		}
	}

	/**
	 * 역경매 견적 대상 업체 목록 조회 (제출현황 팝업 조회)
	 *
	 * @param param
	 * @return list
	 * @author kh_lee
	 * @Date : 2017. 4. 5
	 * @Method Name : findListAuctionTargetVd
	 */
	public List findListAuctionTargetVd(Map param) {
		return auctionBidRepository.findListAuctionTargetVd(param);
	}

	/**
	 * 역경매 견적 제출 업체 목록 조회 (제출현황 팝업 조회)
	 *
	 * @param param
	 * @return list
	 * @author kh_lee
	 * @Date : 2017. 4. 5
	 * @Method Name : findListAuctionSubmitVd
	 */
	public List findListAuctionSubmitVd(Map param) {
		return auctionBidRepository.findListAuctionSubmitVd(param);
	}
	
	public void updateAuctionBidStlRsn(Map auctionInfo) {
		auctionBidRepository.updateAuctionBidStlRsn(auctionInfo);
	}
	
	public List<Map> findListAuctionBidSlctnByAuction(Map auctionInfo) {
		return auctionBidRepository.findListAuctionBidSlctnByAuction(auctionInfo);
	}
	
	public List findAuctionBidSlctnItemByAuctionAndVdCd(Map updateItem) {
		return auctionBidRepository.findAuctionBidSlctnItemByAuctionAndVdCd(updateItem);
	}
}
