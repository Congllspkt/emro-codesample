package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.bp.rfx.rfx.repository.RfxBidRepository;
import smartsuite.app.bp.rfx.rfx.validator.RfxValidator;
import smartsuite.app.bp.rfx.rfxpreinsp.service.RfxPreInspService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.data.FloaterStream;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxBidService {
	
	@Inject
	RfxBidRepository rfxBidRepository;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfxBidItemService rfxBidItemService;
	
	@Inject
	RfxBidItemPriceFactorService rfxBidItemPriceFactorService;
	
	@Inject
	RfxPreInspService rfxPreInspService;
	
	@Inject
	BidRSACipherUtil bidRSACipherUtil;
	
	@Inject
	FormatterProvider formatterProvider;
	
	@Inject
	StdFileService stdFileService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	RfxValidator rfxValidator;

	@Inject
	MailService mailService;

	/**
	 * 업체대행으로 견적서 작성을 위한 목록을 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.vd_cd - 업체 코드<br><br>
	 *
	 * <b>Option:</b><br>
	 * param.rfx_bid_uuid - 견적서 ID
	 *
	 * @param param
	 * @return FloaterStream
	 */
	public FloaterStream findListRfxBidBuyer(Map param) {
		return rfxBidRepository.findListRfxBidBuyer(param);
	}
	
	public Map findRfxBidInfo(Map param) {
		Map rfxBidData = rfxBidRepository.findRfxBid(param);
		
		String rfxProgSts = (String) rfxBidData.get("rfx_sts_ccd");
		int rfxRev = Integer.parseInt(rfxBidData.get("rfx_rnd").toString());

		String rfxOperationOrgCode = rfxBidData.get("oorg_cd") == null? "" : rfxBidData.get("oorg_cd").toString();

		if(rfxBidItemService.existRfxOperationOrganizationByUser(rfxOperationOrgCode)){
			String encriptRfxBidAmt = rfxBidData.get("encpt_rfx_bid_amt") ==null? "0" : rfxBidData.get("encpt_rfx_bid_amt").toString();
			if(StringUtils.isNotEmpty(encriptRfxBidAmt) && !("0").equals(encriptRfxBidAmt)) rfxBidData.put("rfx_bid_amt",bidRSACipherUtil.decrypt(encriptRfxBidAmt));
		}

		
		//멀티라운드 진행중인 경우, 저장된 데이터가 없을 때에는 이전차수의 견적내역을 조회한다.
		if("PRGSG".equals(rfxProgSts) && rfxBidData.get("rfx_bid_uuid") == null && rfxRev > 1) {
			return this.findRfxBidInfoPrevRev(rfxBidData);
		} else {
			return this.findRfxBidInfoCurrRev(rfxBidData);
		}
	}
	
	/**
	 * 현재 다수 견적서의 정보로 구성한다.<br><br>
	 * <b>Required:</b><br>
	 * rfxBidData.rfx_uuid - 견적요청 ID<br>
	 * rfxBidData.rfx_bid_uuid - 견적서 ID<br>
	 * rfxBidData.vd_cd - 업체 코드<br><br>
	 *
	 * @param rfxBidData
	 * @return Map
	 */
	private Map findRfxBidInfoCurrRev(Map rfxBidData) {
		if(rfxBidData == null) {
			return null;
		}
		
		List rfxBidItems = rfxBidItemService.findListRfxBidItemForAgent(rfxBidData);
		List bidPriceFactors = rfxBidItemPriceFactorService.findListBidItemPriceFactor(rfxBidData);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxBidData", rfxBidData);
		resultMap.put("rfxBidItems", rfxBidItems);
		resultMap.put("qtaPriceFactors", bidPriceFactors);
		return resultMap;
	}
	
	/**
	 * 이전 차수 견적서의 정보로 구성한다.<br><br>
	 * <b>Required:</b><br>
	 * rfxBidData.rfx_uuid - 견적요청 ID<br>
	 * rfxBidData.rfx_rnd - 견적요청 이전차수<br>
	 * rfxBidData.vd_cd - 업체 코드<br><br>
	 *
	 * @param rfxBidData - 현재 차수 견적서
	 * @return Map
	 */
	private Map findRfxBidInfoPrevRev(Map rfxBidData) {
		if(rfxBidData == null) {
			return null;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		param.put("rfx_no", rfxBidData.get("rfx_no"));
		param.put("rfx_rnd", Integer.parseInt(rfxBidData.get("rfx_rnd").toString()) - 1);
		param.put("vd_cd", rfxBidData.get("vd_cd"));
		
		//이전차수의 견적헤더 조회
		Map prevRfxBidData = this.findRfxBidPrevRev(param);
		//견적첨부 복사
		prevRfxBidData.put("athg_uuid", stdFileService.copyFile((String) rfxBidData.get("athg_uuid")));
		
		List prevRfxBidItems = rfxBidItemService.findListRfxItemWithPrevRevBidItemForAgent(param);
		List prevBidPriceFactors = rfxBidItemPriceFactorService.findListPrevRevBidItemPriceFactorForAgent(param);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxBidData", prevRfxBidData);
		resultMap.put("rfxBidItems", prevRfxBidItems);
		resultMap.put("qtaPriceFactors", prevBidPriceFactors);
		return resultMap;
	}
	
	/**
	 * 견적서 헤더정보 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.vd_cd - 업체 코드<br><br>
	 *
	 * <b>Option:</b><br>
	 * param.rfx_bid_uuid - 견적서 ID
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxBid(Map param) {
		return rfxBidRepository.findRfxBid(param);
	}
	
	/**
	 * 견적서 작성(업체대행) 이전차수의 헤더정보를 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.rfx_rnd - 견적요청 이전차수<br>
	 * param.vd_cd - 업체 코드<br><br>
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxBidPrevRev(Map param) {
		return rfxBidRepository.findRfxBidPrevRev(param);
	}
	
	/**
	 * RFQ 품목별 견적서를 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * rfxInfo.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param rfxInfo
	 * @return List
	 */
	public List findListRfxItemWithBidItems(Map rfxInfo) {
		if(rfxInfo == null) {
			return null;
		}
		List rfxBidItems = rfxBidItemService.findListRfxItemWithBidItems(rfxInfo);
		
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			BigDecimal tgtRate = BigDecimal.ZERO;
			BigDecimal tgtPrice = (BigDecimal) rfxBidItem.get("rfx_trg_uprc");
			BigDecimal itemPrice = (BigDecimal) rfxBidItem.get("exch_item_price");
			
			if(tgtPrice != null && itemPrice != null) {
				tgtRate = new BigDecimal(100).multiply(itemPrice).divide(tgtPrice, 2, RoundingMode.HALF_UP);
			}
			rfxBidItem.put("rfx_trg_uprc_rate", tgtRate);
		}
		return rfxBidItems;
	}
	
	/**
	 * 견적대행 견적서 정보를 저장한다.<br>
	 * 저장 및 제출인 경우 사용한다.<br>
	 *
	 * @param isNew           - 신규 여부
	 * @param rfxBid          - 기존 견적서
	 * @param rfxBidData      - 저장할 견적서
	 * @param rfxBidItems     - 저장할 견적서 품목 다건
	 * @param bidPriceFactors - 저장할 견적서 품목 원가구성항목 다건
	 * @return
	 */
	private ResultMap saveRfxBid(boolean isNew, Map rfxBid, Map rfxBidData, List rfxBidItems, List bidPriceFactors) {
		boolean isPriFactSet = "Y".equals(rfxBidData.get("coststr_use_yn"));

		// 저장하려는 견적대행이 정상적인지 처리
		ResultMap tempSaveValidator = rfxValidator.saveRfxQta(rfxBidData);

		if(!tempSaveValidator.isSuccess()) {
			Map resultMap = tempSaveValidator.getResultData() == null? Maps.newHashMap() : (Map) tempSaveValidator.getResultData();
			resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", rfxBidData.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", rfxBidData.get("rfx_sts_ccd"));
			tempSaveValidator.setResultData(resultMap);
			return tempSaveValidator;
		}

		// 암호화 처리
		String rfxBidAmt = ConvertUtils.convertStringNullZero(rfxBidData.get("rfx_bid_amt"));
		rfxBidData.put("encpt_rfx_bid_amt", bidRSACipherUtil.encrypt(rfxBidAmt));
		rfxBidData.put("rfx_bid_amt", 0);
		
		// 신규 insert
		if(isNew) {
			if(rfxBid.get("rfx_bid_uuid") == null) {
				rfxBidData.put("rfx_bid_uuid", UUID.randomUUID().toString());
				rfxBidData.put("rfx_bid_no", sharedService.generateDocumentNumber("QT"));
				rfxBidData.put("rfx_bid_revno", 1);
			} else {
				// 기존 견적 제출 건이 존재하는 경우
				Map prevRfxBid = Maps.newHashMap();
				prevRfxBid.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
				prevRfxBid.put("rfx_bid_efct_yn", "N");
				
				// 기존 견적 무효
				rfxBidRepository.updateRfxBidValidYn(prevRfxBid);
				
				// 신규 아이디 생성, 차수 증가
				rfxBidData.put("rfx_bid_uuid", UUID.randomUUID().toString());
				rfxBidData.put("rfx_bid_no", rfxBid.get("rfx_bid_no"));
				rfxBidData.put("rfx_bid_revno", Integer.parseInt(rfxBid.get("rfx_bid_revno").toString()) + 1);
			}
			rfxBidRepository.insertRfxBid(rfxBidData);
			rfxBidItemService.insertRfxBidItem(rfxBidData, rfxBidItems, bidPriceFactors);
		} else {
			// 기존 견적 존재 시
			rfxBidData.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			rfxBidData.put("rfx_bid_no", rfxBid.get("rfx_bid_no"));
			rfxBidData.put("rfx_bid_revno", rfxBid.get("rfx_bid_revno"));
			
			rfxBidRepository.updateRfxBid(rfxBidData);
			rfxBidItemService.updateRfxBidItem(rfxBidData, rfxBidItems, bidPriceFactors);
		}
		
		//원가구성항목 설정인 경우 금액 계산
		if(isPriFactSet) {
			this.updateBidAmtByPriceFactors(rfxBidData);
		}
		
		return ResultMap.SUCCESS(rfxBidData);
	}


	/**
	 * 견적서 작성 (업체대행) 임시저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.xxx - 속성 설명<br>
	 * param.zzz - 속성 설명
	 *
	 * @param param
	 * @return
	 */
	public ResultMap temporarySaveRfxBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "CRNG"); // 저장
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		// 기존 작성된 유효 견적 조회
		Map rfxBid = this.findRfxBid(rfxBidData);
		
		// 견적대행 저장 가능한지 여부 판단
		ResultMap validator = rfxValidator.saveRfxBidForAgent(rfxBid);
		if(!validator.isSuccess()) {
			Map resultMap = validator.getResultData();
			resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", rfxBidData.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", rfxBidData.get("rfx_sts_ccd"));
			validator.setResultData(resultMap);
			return validator;
		}
		
		boolean isNew = false;
		if(rfxBid.get("rfx_bid_uuid") == null) {
			isNew = true;
		}

		ResultMap resultRfxData = this.saveRfxBid(isNew,
		                             rfxBid,
		                             rfxBidData,
		                             (List) param.get("rfxBidItems"),
		                             (List) param.get("qtaPriceFactors"));

		Map resultMap = resultRfxData.getResultData();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		resultRfxData.setResultData(resultMap);
		return resultRfxData;
	}
	
	/**
	 * 견적서 ID 기준으로 품목별 원가구성금액 합하여 견적서 품목에 update하고,<br>
	 * 품목별 금액 합하여 견적서에 update한다.<br>
	 *
	 * @param rfxBidData - 견적서
	 */
	public void updateBidAmtByPriceFactors(Map rfxBidData) {
		List rfxBidItems = rfxBidItemPriceFactorService.selectSumPriceFactorBidItems(rfxBidData);
		rfxBidItemService.updateRfxBidItemAmt(rfxBidItems);
		
		Map rfxBid = rfxBidItemService.selectSumPriceBid(rfxBidData);
		rfxBidRepository.updateRfxBidAmt(rfxBid);
	}
	
	/**
	 * 견적서 작성 (업체대행) 제출한다.
	 *
	 * @param param the param
	 * @return resultMap : 견적서 작성 (업체대행) 제출 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 * @Method Name : submitRfxQta
	 */
	public ResultMap submitRfxBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "SUBM"); // 제출
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		// 기존 작성된 유효 견적 조회
		Map rfxBid = this.findRfxBid(rfxBidData);
		
		// 견적대행 제출 가능한지 여부 판단
		ResultMap validator = rfxValidator.submitRfxBidForAgent(rfxBid);
		if(!validator.isSuccess()) {
			Map resultMap = Maps.newHashMap();
			resultMap.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", rfxBid.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", rfxBid.get("rfx_sts_ccd"));
			
			return ResultMap.builder()
			                .resultStatus(validator.getResultStatus())
			                .resultData(resultMap)
			                .build();
		}
		
		boolean isNew = false;
		// 제출인 경우 신규이거나 기존 제출건이 존재하더라도 가능
		if(rfxBid.get("rfx_bid_uuid") == null || "SUBM".equals(rfxBid.get("rfx_bid_sts_ccd"))) {
			isNew = true;
		}

		ResultMap resultRfxData = this.saveRfxBid(isNew,
				rfxBid,
				rfxBidData,
				(List) param.get("rfxBidItems"),
				(List) param.get("qtaPriceFactors"));

		Map resultMap = resultRfxData.getResultData();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		resultRfxData.setResultData(resultMap);
		return resultRfxData;
	}
	
	/**
	 * 견적서 작성 (업체대행) 포기한다.
	 *
	 * @param param the param
	 * @return resultMap : 견적서 작성 (업체대행) 포기 후 처리 결과를 담은 map
	 * @author kh_lee
	 * @Date : 2016. 6.17
	 * @Method Name : abandonRfxQta
	 */
	public ResultMap abandonRfxBid(Map param) {
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "GUP"); // 포기
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		// 기존 작성된 유효 견적 조회
		Map rfxBid = this.findRfxBid(rfxBidData);
		
		// 견적대행 포기 가능한지 여부 판단
		ResultMap validator = rfxValidator.abandonRfxBidForAgent(rfxBid);
		if(!validator.isSuccess()) {
			Map resultMap = Maps.newHashMap();
			resultMap.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			resultMap.put("rfx_bid_sts_ccd", rfxBid.get("rfx_bid_sts_ccd"));
			resultMap.put("rfx_sts_ccd", rfxBid.get("rfx_sts_ccd"));
			
			return ResultMap.builder()
			                .resultStatus(validator.getResultStatus())
			                .resultData(resultMap)
			                .build();
		}
		
		// 포기인 경우 신규일 때만 저장로직을 수행한다.
		// 신규가 아닌 경우 견적서 헤더에 포기 값만 업데이트한다.
		if(rfxBid.get("rfx_bid_uuid") == null) {
			// 신규인 경우 화면에서 품목정보가 넘어오지 않으므로 견적요청 품목 기반으로 조회 후 저장한다.
			List rfxBidItems = rfxBidItemService.findListRfxItemWithPrevRevBidItemForAgent(rfxBidData);
			ResultMap resultRfxData = this.saveRfxBid(true,
					rfxBid,
					rfxBidData,
					rfxBidItems,
					null);

			Map resultMap = resultRfxData.getResultData();
			resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
			resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			resultRfxData.setResultData(resultMap);
			return resultRfxData;
		} else {
			// 기존 견적 저장/제출 건이 존재하는 경우 update
			Map updateBid = Maps.newHashMap();
			updateBid.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			updateBid.put("rfx_bid_sts_ccd", "GUP"); // 포기
			updateBid.put("gup_rsn", rfxBidData.get("gup_rsn"));
			
			rfxBidRepository.updateRfxBidByAbandon(updateBid);
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}
	
	public Map findListRfxBidWithBidItems(Map rfxInfo) {
		List rfxBids = rfxBidRepository.findListRfxBidWithBidItems(rfxInfo);
		List rfxBidItems = Lists.newArrayList();
		
		for(Map rfxBid : (List<Map>) rfxBids) {
			// 견적서 별 품목
			List bidItems = (List) rfxBid.get("bidItems");
			BigDecimal totTgtAmt = BigDecimal.ZERO;
			BigDecimal totLmtAmt = BigDecimal.ZERO;
			
			// 개찰전(rfx_res_sts_ccd='OPEN_WTG')이 아닌 경우에만 목표가 대비비율 계산
			if(!"OPEN_WTG".equals(rfxInfo.get("rfx_res_sts_ccd"))) {
				// 목표 금액, 제한적최저금액 계산
				for(Map bidItem : (List<Map>) bidItems) {
					if(bidItem.get("tgt_amt") != null) {
						BigDecimal tgtAmt = (BigDecimal) bidItem.get("tgt_amt");
						totTgtAmt = totTgtAmt.add(tgtAmt);
					}
					if(bidItem.get("lmt_amt") != null) {
						BigDecimal lmtAmt = (BigDecimal) bidItem.get("lmt_amt");
						totLmtAmt = totLmtAmt.add(lmtAmt);
					}
				}
				
				//목표 금액 목표가 대비비율 계산
				BigDecimal bidAmt = (BigDecimal) rfxBid.get("exch_rfx_bid_amt");
				BigDecimal tgtRate = BigDecimal.ZERO;
				if(totTgtAmt.compareTo(BigDecimal.ZERO) > 0) {
					if(bidAmt == null) {
						bidAmt = BigDecimal.ZERO;
					}
					tgtRate = new BigDecimal(100).multiply(bidAmt).divide(totTgtAmt, 2, RoundingMode.HALF_UP);
				}
				
				rfxBid.put("tgt_amt", totTgtAmt);
				rfxBid.put("lmt_amt", totLmtAmt);
				rfxBid.put("tgt_amt_rate", tgtRate);
			}
			
			//업체 견적서 별 견적내역 목록 전체리스트
			rfxBidItems.addAll(bidItems);
		}
		
		//SNINEQA-291
		Map result = Maps.newHashMap();
		result.put("rfxBids", rfxBids);
		result.put("rfxBidItems", rfxBidItems);
		return result;
	}
	
	/**
	 * 다건의 견적서 선정 여부 값을 업데이트한다.<br><br>
	 *
	 * @param rfxBids - 견적서 다건
	 */
	public void updateRfxBidStl(List rfxBids) {
		if(rfxBids == null) {
			return;
		}
		if(rfxBids.isEmpty()) {
			return;
		}
		
		for(Map rfxBid : (List<Map>) rfxBids) {
			this.updateRfxBidStl(rfxBid);
		}
	}
	
	/**
	 * 견적서 선정 여부 값을 업데이트한다.<br><br>
	 *
	 * @param rfxBid - 견적서
	 */
	public void updateRfxBidStl(Map rfxBid) {
		if(rfxBid == null) {
			return;
		}
		
		rfxBidRepository.updateRfxBidStl(rfxBid);
		rfxBidItemService.updateRfxBidStl(rfxBid);
	}
	
	/**
	 * 견적서 품목별 선정 여부 업데이트한다.<br>
	 * 품목별 선정 여부 업데이트 후 해당 품목이 포함된 견적서도 업데이트한다.<br><br>
	 *
	 * @param rfxData     - 견적요청 마스터
	 * @param rfxBidItems - 선정할 견적서 품목
	 */
	public void updateRfxBidItemsStl(Map rfxData, List rfxBidItems) {
		if(rfxData == null) {
			return;
		}
		if(rfxBidItems == null) {
			return;
		}
		if(rfxBidItems.isEmpty()) {
			return;
		}
		
		// 품목별 선정결과 업데이트
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			rfxBidItemService.updateRfxBidItemStl(rfxBidItem);
		}
		
		// 선정된 견적품목이 포함된 견적서 선정결과 업데이트
		List rfxBids = rfxBidRepository.selectRfxBidsStl(rfxData);
		for(Map rfxBid : (List<Map>) rfxBids) {
			rfxBidRepository.updateRfxBidStl(rfxBid);
		}
	}
	
	public List findListRfxBid(Map param) {
		return rfxBidRepository.findListRfxBid(param);
	}
	
	public List findListRfxBidItemPriceFactorCompare(Map rfxInfo) {
		return rfxBidItemPriceFactorService.findListRfxBidItemPriceFactorCompare(rfxInfo);
	}
	
	/**
	 * 암호화로 저장된 견적서 관련 금액들 복호화 후 관련 필드에 업데이트한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return void
	 */
	public void updateDecryptAmount(Map param) {
		//개찰 시 암호화 값을 update 처리함.
		this.updateDecryptBidAmount(param);
		rfxBidItemPriceFactorService.updateDecryptBidPriceFactorAmount(param);
	}
	
	/**
	 * 견적서 및 견적서품목의 암호화된 금액 전부 복호화 수행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return void
	 */
	private void updateDecryptBidAmount(Map param) {
		if(param == null) {
			return;
		}
		
		List rfxBidItems = rfxBidRepository.findListRfxBidHeaderAndList(param);
		Map<String, Map> vendorRfxBidHdList = Maps.newHashMap();
		for(Map rfxBidItem : (List<Map>) rfxBidItems) {
			// 견적서 헤더는 한번만 업데이트하면 되므로 vd_cd 별 한번만 수행
			if(vendorRfxBidHdList.get(rfxBidItem.get("vd_cd")) == null) {
				Map rfxBidHeader = Maps.newHashMap();
				String encBidAmt = (String) rfxBidItem.get("encpt_rfx_bid_amt");
				if(encBidAmt == null) {
					encBidAmt = "0";
				}
				if(!"0".equals(encBidAmt)) {
					rfxBidHeader.put("rfx_bid_amt", bidRSACipherUtil.decrypt(encBidAmt));
					rfxBidHeader.put("rfx_bid_uuid", rfxBidItem.get("rfx_bid_uuid"));
					
					vendorRfxBidHdList.put((String) rfxBidItem.get("vd_cd"), rfxBidHeader);
				}
			}
			
			// 견적서 품목 단가 복호화
			String encItemPrice = (String) rfxBidItem.get("encpt_rfx_item_subm_uprc");
			if(encItemPrice == null) {
				encItemPrice = "0";
			}
			if(!"0".equals(encItemPrice)) {
				rfxBidItem.put("rfx_item_subm_uprc", bidRSACipherUtil.decrypt(encItemPrice));
			}
			// 견적서 품목 금액 복호화
			String encItemAmt = (String) rfxBidItem.get("encpt_rfx_item_subm_amt");
			if(encItemAmt == null) {
				encItemAmt = "0";
			}
			if(!"0".equals(encItemAmt)) {
				rfxBidItem.put("rfx_item_subm_amt", bidRSACipherUtil.decrypt(encItemAmt));
			}
			
			rfxBidItemService.updateDecryptBidItemAmount(rfxBidItem);
		}
		
		// 견적서 헤더 투찰금액 복호화 수행
		for(String key : vendorRfxBidHdList.keySet()) {
			rfxBidRepository.updateDecryptBidAmount(vendorRfxBidHdList.get(key));
		}
	}
	
	/**
	 * 견적요청 기준으로 제출된 견적서 별 점수 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return List
	 */
	public List findListRfxBidScore(Map param) {
		if(param == null) {
			return null;
		}
		
		Map rfxInfo = rfxBidRepository.findRfx(param);
		return rfxBidRepository.findListRfxBidScore(rfxInfo);
	}
	
	/**
	 * 견적품목 투찰금액 목록을 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return List
	 */
	public List findListRfxBidItemAmt(Map param) {
		if(param == null) {
			return null;
		}
		
		Map rfxInfo = rfxBidRepository.findRfx(param);
		return rfxBidRepository.findListRfxBidItemAmt(rfxInfo);
	}
	
	/**
	 * 견적서 별(총액별) Ranking 업데이트<br><br>
	 * <b>Required:</b><br>
	 * param.slctn_rank - 견적서 별 순위<br>
	 * param.rfx_bid_uuid - 견적서 ID<br><br>
	 *
	 * <b>Option:</b><br>
	 * param.rfx_bid_item_uuid - 견적서 품목 ID<br>
	 * param.npe_conv_sc - 기술평가 합계 점수<br>
	 * param.npe_avg_sc - 기술평가 평균 점수<br>
	 * param.cbe_conv_sc - 가격 계산 점수<br>
	 * param.cbe_sc - 가격 점수<br>
	 * param.eval_ttl_sc - 총 점수<br>
	 *
	 * @param param
	 */
	public void updateRfxBidRanking(Map param) {
		if(param == null) {
			return;
		}
		if(param.get("rfx_bid_uuid") == null) {
			return;
		}
		
		rfxBidRepository.updateRfxBidScoreAndRanking(param);
		this.updateRfxBidItemRanking(param);
	}
	
	/**
	 * 견적 품목 별(품목별) Ranking 업데이트<br><br>
	 * <b>Required:</b><br>
	 * param.slctn_rank - 견적서 별 순위<br>
	 * param.rfx_bid_uuid - 견적서 ID<br><br>
	 *
	 * <b>Option:</b><br>
	 * param.rfx_bid_item_uuid - 견적서 품목 ID<br>
	 *
	 * @param param
	 */
	public void updateRfxBidItemRanking(Map param) {
		if(param == null) {
			return;
		}
		rfxBidRepository.updateRfxBidItemRanking(param);
	}
	
	/**
	 * 견적요청 ID 기준으로 제출된 견적 비교를 위한 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxBidCompareByRfx(Map param) {
		if(param == null) {
			return null;
		}
		
		Map rfxInfo = rfxBidRepository.findRfx(param);
		return rfxBidRepository.findListRfxBidCompareByRfx(rfxInfo);
	}
	
	/**
	 * 견적요청 번호 기준으로 제출된 견적 비교를 위한 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxBidCompareByRfxNo(Map param) {
		if(param == null) {
			return null;
		}
		
		Map rfxInfo = rfxBidRepository.findRfx(param);
		return rfxBidRepository.findListRfxBidCompareByRfxNo(rfxInfo);
	}
	
	public List findListRfxBidItemComparePriceDoctor(Map param) {
		if(param == null) {
			return null;
		}
		
		return rfxBidRepository.findListRfxBidItemComparePriceDoctor(param);
	}
	
	public List findListBidItemPriceFactorForAgent(Map param) {
		return rfxBidItemPriceFactorService.findListBidItemPriceFactorForAgent(param);
	}
	
	public ResultMap checkRfxPreInspCreateRfxBid(Map param) {
		return rfxPreInspService.checkRfxPreInspCreateRfxBid(param);
	}
	
	public List findListRfxBidItemForAgent(Map rfxBidData) {
		return rfxBidItemService.findListRfxBidItemForAgent(rfxBidData);
	}

	public void updateRfxStlRsn(Map rfxBidData) {
		rfxBidRepository.updateRfxStlRsn(rfxBidData);
	}
	
	/**
	 * 협상가능한 견적서 조회
	 * @param rfxData
	 * @return
	 */
	public List findListNegotiableRfxBid(Map rfxData) {
		return rfxBidRepository.findListNegotiableRfxBid(rfxData);
	}

	/**
	 * 협력사 투찰 여부 확인
	 */
	public Map findRfxBidForBidEffective(Map rfxData){
		return rfxBidRepository.findRfxBidForBidEffective(rfxData);
	}
	
	public List<Map> findListRfxBidSlctnByRfx(Map rfxInfo) {
		return rfxBidRepository.findListRfxBidSlctnByRfx(rfxInfo);
	}
	
	public List findRfxBidSlctnItemByRfxAndVdCd(Map param) {
		return rfxBidRepository.findRfxBidSlctnItemByRfxAndVdCd(param);
	}
	
	public List findListCsByBid(Map param) {
		return rfxBidRepository.findListCsByBid(param);
	}
}
