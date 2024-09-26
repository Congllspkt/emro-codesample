package smartsuite.app.sp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.common.util.ListUtils;
import smartsuite.app.sp.rfx.rfx.repository.SpRfxBidRepository;
import smartsuite.app.sp.rfx.rfx.validator.SpRfxValidator;
import smartsuite.app.sp.rfx.rfxpreinsp.service.SpRfxPreInspService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RfxVendor 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @FileName SpRfxService.java
 * @package smartsuite.app.bp.rfx.rfx
 * @Since 2016. 5. 17
 * @변경이력 : [2016. 5. 17] Yeon-u Kim 최초작성
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpRfxBidService {
	
	@Inject
	SpRfxBidRepository spRfxBidRepository;
	
	@Inject
	SpRfxBidItemService spRfxBidItemService;
	
	@Inject
	SpRfxBidItemPriceFactorService spRfxBidItemPriceFactorService;
	
	@Inject
	SpRfxPreInspService spRfxPreInspService;
	
	@Inject
	SpRfxValidator spRfxValidator;
	
	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;
	
	@Inject
	BidRSACipherUtil bidRSACipherUtil;
	
	@Inject
	StdFileService stdFileService;
	
	/**
	 * vdcd의 값을 반환한다.
	 *
	 * @return vdcd
	 */
	protected String getSessionVendorCode() {
		if(Auth.getCurrentUserInfo() == null)
			return null;
		else
			return String.valueOf(Auth.getCurrentUserInfo().get("vd_cd"));
	}
	
	private Map saveRfxBid(boolean isNew, Map rfxBid, Map rfxBidData, List rfxBidItems, List bidPriceFactors) {
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
				prevRfxBid.put("rfx_bid_efct_yn", "N");
				prevRfxBid.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
				
				// 기존 견적 무효
				spRfxBidRepository.updateRfxBidValidYn(prevRfxBid);
				
				//신규 id생성
				rfxBidData.put("rfx_bid_uuid", UUID.randomUUID().toString());
				rfxBidData.put("rfx_bid_no", rfxBid.get("rfx_bid_no"));
				rfxBidData.put("rfx_bid_revno", Integer.parseInt(rfxBid.get("rfx_bid_revno").toString()) + 1);
			}
			
			spRfxBidRepository.insertRfxBid(rfxBidData);
			spRfxBidItemService.insertRfxBidItem(rfxBidData, rfxBidItems, bidPriceFactors);
		} else {
			// 기존 견적 존재 시
			rfxBidData.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			rfxBidData.put("rfx_bid_no", rfxBid.get("rfx_bid_no"));
			rfxBidData.put("rfx_bid_revno", rfxBid.get("rfx_bid_revno"));
			
			spRfxBidRepository.updateRfxBid(rfxBidData);
			spRfxBidItemService.updateRfxBidItem(rfxBidData, rfxBidItems, bidPriceFactors);
		}
		
		return rfxBidData;
	}
	
	/**
	 * temp rfx qta 저장한다.
	 *
	 * @param param the save param
	 * @return the map< string, object>
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 23
	 */
	public ResultMap temporarySaveRfxBid(Map param) {
		// 협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "CRNG"); // 저장
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		// 기존 작성된 유효 견적 조회
		Map rfxBid = spRfxBidRepository.findRfxBid(rfxBidData);
		
		// 저장 가능한지 여부 판단
		ResultMap validator = spRfxValidator.saveRfxBid(rfxBid);
		if(!validator.isSuccess()) {
			Map<String,Object> resultDataMap = validator.getResultData() == null? Maps.newHashMap() : (Map<String,Object>) validator.getResultData();
			resultDataMap.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			resultDataMap.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			resultDataMap.put("rfx_bid_sts_ccd", rfxBid.get("rfx_bid_sts_ccd"));
			resultDataMap.put("rfx_sts_ccd", rfxBid.get("rfx_sts_ccd"));
			validator.setResultData(resultDataMap);
			
			return validator;
		}
		
		boolean isNew = false;
		if(rfxBid.get("rfx_bid_uuid") == null) {
			isNew = true;
		}
		
		rfxBidData = this.saveRfxBid(isNew,
		                             rfxBid,
		                             rfxBidData,
		                             (List) param.get("rfxBidItems"),
		                             (List) param.get("qtaPriceFactors"));
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
		
	}
	
	/**
	 * 견적 제출한다.
	 *
	 * @param param the save param
	 * @return the map< string, object>
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 18
	 */
	public ResultMap submitRfxBid(Map param) {
		// 협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "SUBM"); // 제출
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		Map rfxBid = spRfxBidRepository.findRfxBid(rfxBidData);
		
		// 견적대행 제출 가능한지 여부 판단
		ResultMap validator = spRfxValidator.submitRfxBid(rfxBid);
		if(!validator.isSuccess()) {
			Map<String,Object> resultDataMap = validator.getResultData() == null? Maps.newHashMap() : (Map<String,Object>) validator.getResultData();
			resultDataMap.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			resultDataMap.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			resultDataMap.put("rfx_bid_sts_ccd", rfxBid.get("rfx_bid_sts_ccd"));
			resultDataMap.put("rfx_sts_ccd", rfxBid.get("rfx_sts_ccd"));
			validator.setResultData(resultDataMap);

			return validator;
		}
		
		boolean isNew = false;
		// 제출인 경우 신규이거나 기존 제출건이 존재하더라도 가능
		if(rfxBid.get("rfx_bid_uuid") == null || "SUBM".equals(rfxBid.get("rfx_bid_sts_ccd"))) {
			isNew = true;
		}
		rfxBidData = this.saveRfxBid(isNew,
		                             rfxBid,
		                             rfxBidData,
		                             (List) param.get("rfxBidItems"),
		                             (List) param.get("qtaPriceFactors"));
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}
	
	/**
	 * 견적 포기
	 *
	 * @param param
	 * @return
	 */
	public ResultMap abandonRfxBid(Map param) {
		// 협력사 코드 체크
		if(getSessionVendorCode() == null) {
			return ResultMap.FAIL();
		}
		
		Map rfxBidData = (Map) param.get("rfxBidData");
		rfxBidData.put("rfx_bid_sts_ccd", "GUP"); // 포기
		rfxBidData.put("rfx_bid_efct_yn", "Y");
		
		// 기존 작성된 유효 견적 조회
		Map rfxBid = spRfxBidRepository.findRfxBid(rfxBidData);
		
		// 포기 가능한지 여부 판단
		ResultMap validator = spRfxValidator.abandonRfxBid(rfxBid);
		if(!validator.isSuccess()) {
			Map<String,Object> resultDataMap = validator.getResultData() == null? Maps.newHashMap() : validator.getResultData();
			resultDataMap.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			resultDataMap.put("rfx_bid_uuid", rfxBid.get("rfx_bid_uuid"));
			resultDataMap.put("rfx_bid_sts_ccd", rfxBid.get("rfx_bid_sts_ccd"));
			resultDataMap.put("rfx_sts_ccd", rfxBid.get("rfx_sts_ccd"));
			validator.setResultData(resultDataMap);

			return validator;
		}
		
		// 포기인 경우 신규일 때만 저장로직을 수행한다.
		// 신규가 아닌 경우 견적서 헤더에 포기 값만 업데이트한다.
		if(rfxBid.get("rfx_bid_uuid") == null) {
			// 신규인 경우 화면에서 품목정보가 넘어오지 않으므로 견적요청 품목 기반으로 조회 후 저장한다.
			List rfxBidItems = spRfxBidItemService.findListRfxItemWithPrevRevBidItem(rfxBidData);
			rfxBidData = this.saveRfxBid(true,
			                             rfxBid,
			                             rfxBidData,
			                             rfxBidItems,
			                             null);
		} else {
			// 기존 견적 저장/제출 건이 존재하는 경우 update
			Map updateBid = Maps.newHashMap();
			updateBid.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			updateBid.put("rfx_bid_sts_ccd", "GUP"); // 포기
			updateBid.put("gup_rsn", rfxBidData.get("gup_rsn"));
			
			spRfxBidRepository.updateRfxBidByAbandon(updateBid);
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfx_uuid", rfxBidData.get("rfx_uuid"));
		resultMap.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
		return ResultMap.SUCCESS(resultMap);
	}
	
	/**
	 * 선정취소 사유 조회
	 *
	 * @param
	 * @return
	 * @author : Chaerin yu
	 * @Date : 2017. 07. 28
	 * @Method Name : findInfoCancelCause
	 */
	public Map findInfoCancelCause(Map param) {
		return spRfxBidRepository.findInfoCancelCause(param);
	}
	
	/**
	 * 공동수급협정서 현황 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfxCs(Map param) {
		return spRfxBidRepository.findListRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map findRfxCs(Map param) {
		Map rfxData = spRfxBidRepository.findRfxBid(param);
		Map rfxCsData = spRfxBidRepository.findRfxCs(param);
		List rfxCsVdList = spRfxBidRepository.findListRfxCsVd(rfxCsData);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", rfxData);
		resultMap.put("csData", rfxCsData);
		resultMap.put("csVdList", rfxCsVdList);
		return resultMap;
	}
	
	/**
	 * 공동수급협정서 대상 협력사 추가 팝업 조회
	 *
	 * @param param
	 * @return
	 */
	public List findListRfxCsVdTarget(Map param) {
		return spRfxBidRepository.findListRfxCsVdTarget(param);
	}
	
	/**
	 * 견적 생성을 위한 공동수급협정서 협력사 유효성 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkBidCreatableByRfxCs(Map param) {
		Map resultData = Maps.newHashMap();
		
		// 견적서 존재 확인
		Map checkBid = spRfxBidRepository.checkRfxBids(param);
		if(checkBid == null) {
			Map checkCsData = spRfxBidRepository.checkRfxCsVds(param);
			if(checkCsData == null) {
				// 견적서 작성 진행 가능
				resultData.put("new_yn", "Y");
			} else {
				// CS 존재 시 해당 CS 정보 리턴
				resultData = checkCsData;
			}
		} else {
			// 이미 작성한 견적서 존재 시 해당 견적서 정보 리턴
			resultData = checkBid;
		}
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 공동수급협정서 협력사 유효성 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkRfxCsVd(Map param) {
		
		// 견적서 존재 확인
		Map checkBid = spRfxBidRepository.checkRfxBids(param);
		if(checkBid == null) {
			// 대표업체 아닌 구성원으로 CS 존재 여부 확인
			param.put("rep_vd_yn", "N");
			Map checkCsData = spRfxBidRepository.checkRfxCsVds(param);
			if(checkCsData == null) {
				Map resultData = Maps.newHashMap();
				resultData.put("new_yn", "Y");
				
				// 신규 CS 작성 가능
				return ResultMap.SUCCESS(resultData);
			} else {
				// 다른 대표업체의 구성원으로 CS 존재 시 해당 CS 정보 리턴
				return ResultMap.FAIL(checkCsData);
			}
		} else {
			// 이미 작성한 견적서 존재 시 해당 견적서 정보 리턴
			return ResultMap.FAIL(checkBid);
		}
	}
	
	/**
	 * 공동수급협정서 (복수)협력사 유효성 체크
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkRfxCsVds(String rfxId, List vdCds) {
		if(rfxId == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(vdCds == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(vdCds.isEmpty()) {
			return ResultMap.NOT_EXISTS();
		}
		
		List checkBidList = Lists.newArrayList();
		List checkCsList = Lists.newArrayList();
		for(String vdCd : (List<String>) vdCds) {
			Map param = Maps.newHashMap();
			param.put("rfx_uuid", rfxId);
			param.put("vd_cd", vdCd);
			
			Map checkBid = spRfxBidRepository.checkRfxBids(param);
			if(checkBid != null) {
				checkBidList.add(checkBid);
			}
			Map checkCs = spRfxBidRepository.checkRfxCsVds(param);
			if(checkCs != null) {
				checkCsList.add(checkCs);
			}
		}
		
		if(checkBidList.isEmpty() && checkCsList.isEmpty()) {
			return ResultMap.SUCCESS();
		} else {
			Map resultData = Maps.newHashMap();
			resultData.put("existsQtaVds", checkBidList);
			resultData.put("dupCsVds", checkCsList);
			
			return ResultMap.FAIL(resultData);
		}
	}
	
	private ResultMap saveRfxCsData(Map param) {
		Map csData = (Map) param.get("csData");
		Map rfxCsData = spRfxBidRepository.findRfxCs(csData);
		
		if(!"N".equals(rfxCsData.get("cstm_ptcp_confm_yn"))) {
			Map resultData = Maps.newHashMap();
			resultData.put("cstm_ptcp_confm_yn", rfxCsData.get("cstm_ptcp_confm_yn"));
			resultData.put("cstm_ptcp_req_snd_yn", rfxCsData.get("cstm_ptcp_req_snd_yn"));
			
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
			                .resultData(resultData)
			                .build();
		}
		
		List insertCsVds = (List) param.get("insertCsVds");
		List updateCsVds = (List) param.get("updateCsVds");
		List deleteCsVds = (List) param.get("deleteCsVds");
		
		// 신규 추가 구성업체에 대해 동일 RFx내의 협정서 중복 체크
		if(insertCsVds.size() > 0) {
			List vdCds = ListUtils.getArrayValuesByList(insertCsVds, "vd_cd");
			ResultMap validator = this.checkRfxCsVds((String) csData.get("rfx_uuid"), vdCds);
			if(!validator.isSuccess()) {
				return validator;
			}
		}
		
		this.deleteCsVds(deleteCsVds);
		this.insertCsVds(csData, insertCsVds);
		this.updateCsVds(csData, updateCsVds);
		
		return ResultMap.SUCCESS();
	}
	
	private void updateCsVds(Map csData, List csVds) {
		if(csVds == null) {
			return;
		}
		if(csVds.isEmpty()) {
			return;
		}
		
		for(Map csVd : (List<Map>) csVds) {
			csVd.put("cstm_typ_ccd", csData.get("cstm_typ_ccd"));
			spRfxBidRepository.updateRfxCs(csVd);
		}
	}
	
	private void insertCsVds(Map csData, List csVds) {
		if(csVds == null) {
			return;
		}
		if(csVds.isEmpty()) {
			return;
		}
		
		for(Map csVd : (List<Map>) csVds) {
			csVd.put("rfx_uuid", csData.get("rfx_uuid"));
			csVd.put("rep_vd_cd", csData.get("rep_vd_cd"));
			csVd.put("rfx_no", csData.get("rfx_no"));
			csVd.put("rfx_rnd", csData.get("rfx_rnd"));
			csVd.put("cstm_typ_ccd", csData.get("cstm_typ_ccd"));
			spRfxBidRepository.insertRfxCs(csVd);
		}
	}
	
	private void deleteCsVds(List csVds) {
		if(csVds == null) {
			return;
		}
		if(csVds.isEmpty()) {
			return;
		}
		
		for(Map csVd : (List<Map>) csVds) {
			spRfxBidRepository.deleteRfxCs(csVd);
		}
	}
	
	/**
	 * 공동수급협정서 저장
	 *
	 * @param param
	 * @return
	 */
	public ResultMap saveRfxCs(Map param) {
		return saveRfxCsData(param);
	}
	
	/**
	 * 공동수급협정서 승인
	 *
	 * @param param
	 * @return
	 */
	public ResultMap approveRfxCs(Map param) {
		ResultMap resultMap = saveRfxCsData(param);
		if(!resultMap.isSuccess()) {
			return ResultMap.FAIL();
		}
		
		Map csData = (Map) param.get("csData");
		spRfxBidRepository.updateRfxCsSbmtY(csData);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 공동수급협정서 발송
	 *
	 * @param param
	 * @return
	 */
	public ResultMap sendRfxCs(Map param) {
		Map rfxCsData = spRfxBidRepository.findRfxCs(param);
		if("N".equals(rfxCsData.get("cstm_ptcp_confm_yn")) || "Y".equals(rfxCsData.get("cstm_ptcp_req_snd_yn"))) {
			Map resultData = Maps.newHashMap();
			resultData.put("cstm_ptcp_confm_yn", rfxCsData.get("cstm_ptcp_confm_yn"));
			resultData.put("cstm_ptcp_req_snd_yn", rfxCsData.get("cstm_ptcp_req_snd_yn"));
			resultData.put("new_yn", rfxCsData.get("new_yn"));
			
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
			                .resultData(resultData)
			                .build();
		}
		
		spRfxBidRepository.updateRfxCsSendY(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 공동수급협정서 작성취소
	 *
	 * @param param
	 * @return
	 */
	public ResultMap cancelRfxCs(Map param) {
		Map checkBid = spRfxBidRepository.checkRfxBids(param);
		if(checkBid != null) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.HAS_QTA)
			                .resultData(checkBid)
			                .build();
		}
		
		spRfxBidRepository.deleteRfxCsByCancel(param);
		return ResultMap.SUCCESS();
	}
	
	public ResultMap checkRfxPreInspCompYByRfx(Map param) {
		return spRfxPreInspService.checkRfxPreInspCompYByRfx(param);
	}
	
	public FloaterStream findListRfxVd(Map param) {
		return spRfxBidRepository.findListRfxVd(param);
	}
	
	public List findListBidItemPriceFactor(Map param) {
		return spRfxBidItemPriceFactorService.findListBidItemPriceFactor(param);
	}
	
	public Map findRfxBidInfo(Map param) {
		// Workplace 에서 지명 협력사 rfx_vd_uuid 활용하여 견적서 상세 조회 시
		if(param.get("rfx_vd_uuid") != null) {
			Map rfxData = spRfxBidRepository.findRfxByRfxVdUuid(param);
			if(rfxData != null) {
				param.put("rfx_uuid", rfxData.get("rfx_uuid"));
			}
		}
		Map rfxBidData = spRfxBidRepository.findRfxBid(param);
		
		String rfxProgSts = (String) rfxBidData.get("rfx_sts_ccd");
		int rfxRev = Integer.parseInt(rfxBidData.get("rfx_rnd").toString());
		
		this.decryptRfxBidPriceAmt(rfxBidData);
		
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
		
		List rfxBidItems = spRfxBidItemService.findListRfxBidItem(rfxBidData);
		List bidPriceFactors = spRfxBidItemPriceFactorService.findListBidItemPriceFactor(rfxBidData);
		
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
		Map prevRfxBidData = spRfxBidRepository.findRfxBidPrevRev(param);
		//견적첨부 복사
		prevRfxBidData.put("athg_uuid", stdFileService.copyFile((String) rfxBidData.get("athg_uuid")));
		
		List prevRfxBidItems = spRfxBidItemService.findListRfxItemWithPrevRevBidItem(param);
		List prevBidPriceFactors = spRfxBidItemPriceFactorService.findListPrevRevBidItemPriceFactor(param);
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxBidData", prevRfxBidData);
		resultMap.put("rfxBidItems", prevRfxBidItems);
		resultMap.put("qtaPriceFactors", prevBidPriceFactors);
		return resultMap;
	}
	
	/**
	 * 견적 금액 관련 필드 복호화<br><br>
	 *
	 * @param rfxBidData - 견적서
	 */
	private void decryptRfxBidPriceAmt(Map rfxBidData) {
		if(rfxBidData == null) {
			return;
		}
		
		ResultMap validator = spRfxValidator.isSameVendor(rfxBidData);
		if(!validator.isSuccess()) {
			return;
		}
		
		String encBidAmt = (String) rfxBidData.get("encpt_rfx_bid_amt");
		rfxBidData.put("rfx_bid_amt", bidRSACipherUtil.decrypt(encBidAmt));
	}
}
