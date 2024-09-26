package smartsuite.app.bp.rfx.nego.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalService;
import smartsuite.app.bp.rfx.nego.repository.NegoRepository;
import smartsuite.app.bp.rfx.nego.validator.NegoValidator;
import smartsuite.app.bp.rfx.rfx.service.RfxBidItemService;
import smartsuite.app.bp.rfx.rfx.service.RfxBidService;
import smartsuite.app.bp.rfx.rfx.service.RfxService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.scheduler.core.ScheduleService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

/**
 * 협상관련 service
 * @author user
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class NegoService {
	
	@Inject
	NegoRepository negoRepository;
	
	@Inject
	NegoValidator negoValidator;
	
	@Inject
	RfxService rfxService;
	
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	ScheduleService scheduleService;
	
	@Inject
	MailService mailService;
	
	@Inject
	ApprovalService approvalService;
	
	@Inject
	RfxBidService rfxBidService;
	
	@Inject
	RfxBidItemService rfxBidItemService;
	
	@Inject
	NegoItemService negoItemService;
	
	/**
	 * 협상 헤더 삭제 처리
	 * @param item
	 */
	private void deleteNegoTargetHeaderSts(Map item) {
		negoRepository.deleteNegoTargetHeaderSts(item);
	}

	/**
	 * 협상 상세 삭제 처리
	 * @param item
	 */
	private void deleteNegoTargetDetailSts(Map item) {
		negoItemService.deleteNegoTargetDetailSts(item);
	}

	/**
	 * 협상대상 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveNegoTarget(Map param) {
		Map rfxData         = (Map) param.get("rfxData");
		List rfxBidList     = (List) param.get("rfxBids");
		List negoDetailList = Lists.newArrayList();
		List mailList       = Lists.newArrayList();
		
		negoValidator.validate(rfxData);
		
		//선정여부 초기화
		rfxBidService.updateRfxBidStl(this.searchInitSelectionYnFromBidding(rfxData));
		
		for(Map rfxBid : (List<Map>) rfxBidList) {
			rfxBid.put("nego_uuid", UUID.randomUUID().toString());
			rfxBid.put("nego_rnd", 1);
			rfxBid.put("nego_sts_ccd", "NEGO_WTG");
			
			negoRepository.insertNegoHeader(rfxBid);
			List rfxBidItems = negoRepository.findBidItems(rfxBid);
			negoDetailList.addAll(rfxBidItems);

			//통보기능 추가
			List vendorUserInfo = negoRepository.getUserMailInfo(rfxBid);
			if(vendorUserInfo == null){
				continue;
			}
			
			mailList.add(vendorUserInfo);
		}
		
		for(Map item : (List<Map>) negoDetailList) {
			negoItemService.insertNegoDetail(item);
		}
		
		rfxStatusService.saveNego(rfxData);
		
		Map mailData = Maps.newHashMap();
		mailData.put("parameter", rfxData);
		mailData.put("receivers", mailList);
		mailService.sendAsync("NEGO_ALRAM_MAIL", null, mailData);
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 선정여부 초기화 대상조회
	 * @param rfxData
	 */
	public List<Map<String, Object>> searchInitSelectionYnFromBidding(Map<String, Object> rfxData) {
		return negoRepository.searchInitSelectionYnFromBidding(rfxData);
	}

	/**
	 * 협상대상 조회
	 * @param param
	 * @return
	 */
	public List findNegoTargetList(Map param) {
		return negoRepository.findNegoTargetList(param);
	}

	/**
	 * 협상취소 리스트
	 * @param param
	 */
	public ResultMap cancelNegoTargetList(Map param) {
		List rfxLists = (List) param.get("selectedItems");
		
		for(Map rfx : (List<Map>) rfxLists) {
			negoValidator.checkRfxProgSts(rfx);
			Map nego = this.findNegoTargetListByRfxUuid(rfx);
			this.cancelNegoTarget(nego);
		}
		
		return ResultMap.SUCCESS();
	}
	
	public Map findNegoTargetListByRfxUuid(Map rfx) {
		return negoRepository.findNegoTargetListByRfxUuid(rfx);
	}
	
	/**
	 * 협상취소 단건
	 * @param param
	 */
	public void cancelNegoTarget(Map param) {
		this.deleteNegoTargetDetailSts(param);
		this.deleteNegoTargetHeaderSts(param);
		rfxStatusService.cancelNego(param);
		this.deleteCloseNegoJobList(param);
	}

	/**
	 * 협상헤더 정보 조회
	 * @param param
	 * @return
	 */
	public Map findNegoInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", negoRepository.findNegoRfxData(param));
		resultMap.put("negoDataList", negoRepository.findNegoBidData(param));
		
		return resultMap;
	}

	/**
	 * 협상상세 품목 조회
	 * @param param
	 * @return
	 */
	public Map findNegoDetailInfo(Map param) {
		Map resultMap = this.findNegoInfo(param);
		resultMap.put("resultList", negoItemService.findNegoBidDetail(param));
		
		return resultMap;
	}

	/**
	 * 협상헤더 저장
	 * @param param
	 * @return
	 */
	public ResultMap requestNego(Map param) {
		String negoId = (String) param.get("nego_uuid");
		// 유효성체크
		negoValidator.checkNegoProgSts(param);
		// 협상 스케줄러 삭제
		this.deleteCloseNegoJob(negoId);
		// 협상요청 저장
		negoRepository.requestNego(param);
		
		if("ONLN".equals(param.get("nego_typ_ccd"))) {
			//온라인인 경우 스케줄러 등록
			Map data = Maps.newHashMap();
			data.put("nego_uuid", negoId);
			
			Object[] args = new Object[] {data};
			//마감스케룰러 등록
			this.registCloseNegoJob(negoId, (Date) param.get("clsg_dttm"), args);
			
			mailService.sendAsync("NEGO_REQ_ALRAM_MAIL", null, param);
		}
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 협상타결
	 * @param param
	 * @return
	 */
	public ResultMap negoSuccess(Map<String, Object> param) {
		//타결 협상진행상태 완료 결과 완료로 변경
		Map negoData = (Map) param.get("negoData");
		List updateList = (List) param.get("updateList");
		
		negoValidator.checkNegoProgSts(negoData);
		
		negoRepository.updateNegoCont(negoData);
		rfxStatusService.negoSuccess(negoData);
		
		for(Map item : (List<Map>) updateList) {
			negoItemService.updateNegoPrice(item);
		}
		
		return ResultMap.SUCCESS();
	}


	/**
	 * 협상결렬
	 * @param param
	 * @return
	 */
	public ResultMap negoFailure(Map<String, Object> param) {
		negoValidator.checkNegoProgSts(param);
		
		rfxStatusService.negoFailure(param);
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 재협상 요청(협상복사)
	 * @param param
	 * @return
	 */
	public ResultMap requestReNego(Map param) {
		negoValidator.checkReNegoSts(param);
		
		//기존 헤더에 재협상사유 update
		negoRepository.updateReNegoCause(param);
		
		String nego_uuid = UUID.randomUUID().toString();
		param.put("new_nego_uuid", nego_uuid);
		negoRepository.copyNegoHeader(param);
		
		List detailList = negoItemService.findNegoDetail(param);
		for (Map map : (List<Map>) detailList) {
			map.put("nego_uuid", nego_uuid);
			negoItemService.insertNegoDetail(map);
		}
		
		Map resultMapData = Maps.newHashMap();
		resultMapData.put("rfx_uuid", param.get("rfx_uuid"));
		resultMapData.put("nego_uuid", nego_uuid);
		
		return ResultMap.SUCCESS(resultMapData);
	}

	/**
	 * 협상강제종료
	 * @param param
	 * @return
	 */
	public ResultMap byPassCloseNegos(Map param) {
		List negoDatas = (List) param.get("negoDatas");
		
		for(Map negoData : (List<Map>) negoDatas) {
			negoRepository.updateCloseNegoCause(negoData);
			rfxStatusService.updateCloseNego(negoData);
			
			// 기존 등록 된 스케줄러 삭제
			String negoId = (String) negoData.get("nego_uuid");
			this.deleteCloseNegoJob(negoId);
		}

		return ResultMap.SUCCESS();
	}
	
	/**
     * 마감처리 스케줄러
     *
     * @param param
     */
	public void closeNego(HashMap<String, Object> param) {
		rfxStatusService.updateCloseNego(param);
	}
	
	/**
	 * 우선협상 마감 처리 스케줄러 등록
	 *
	 * @param negoId    협상 아이디
	 * @param endDt     스케줄러 실행 일시
	 * @param args      스케줄러에서 사용될 parameter
	 */
	private void registCloseNegoJob(String negoId, Date endDt, Object[] args) {
		try {
			scheduleService.registSchedule(Class.forName("smartsuite.app.bp.rfx.nego.service.NegoService"), "closeNego", args,
					endDt, "NEGO", negoId, "CLOSE_NEGO", null);
		} catch (ClassNotFoundException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
	}

	/**
	 * 협상 마감 처리 스케줄러 삭제
	 *
	 * @param negoUuid  협상 아이디
	 */
	private void deleteCloseNegoJob(String negoUuid) {
		if (negoUuid != null) {
			try {
				scheduleService.removeScheduleTrigger(Class.forName("smartsuite.app.bp.rfx.nego.service.NegoService"),
						"closeNego", "NEGO", negoUuid);
			} catch (ClassNotFoundException e) {
				throw new CommonException(ErrorCode.FAIL, e);
			}
		}

	}
	
	/**
	 * 여러건의 협상 마감 처리 스케줄러 삭제
	 *
	 * @param negoUuid  협상 아이디
	 */
	private void deleteCloseNegoJobList(Map param) {
		List<String> negoUuids = (List<String>) (null != param.get("nego_uuids") ? param.get("nego_uuids") : Lists.newArrayList());
		for(String negoUuid : negoUuids) {
			//휘발성 스케줄러 삭제
			this.deleteCloseNegoJob(negoUuid);
		}
	}

	/**
	 * 협상헤더 선정 여부 체크
	 * @param param
	 */
	public ResultMap selectNegoRfxBids(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List rfxBids = (List) param.get("rfxBids");
		
		//선정 초기화
		negoRepository.initNegoSelectionYN(rfxData);
		// 협상요청 협상진행중일 경우  협상마감으로 변경
		negoRepository.updateNegoStatusCodeToClosing(rfxData);
		
		for(Map rfxBid : (List<Map>) rfxBids){
			negoRepository.updateNegoStl(rfxBid);
			if(rfxBid.containsKey("nego_typ_ccd") && "ONLN".equals(rfxBid.get("nego_typ_ccd"))){
				//온라인인 경우 존재하는 스케줄러 삭제
				this.deleteCloseNegoJob((String) rfxBid.get("nego_uuid"));
			}
		}
		// 협상에 없는 견적서를 추가함.
		this.addNotExsistBiddingsToNegoBids(rfxBids);
		
		for(Map rfxBid : (List<Map>) rfxBids){
			this.updateRfxBiddingSelectionYn(rfxBid);
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 협상에 포함되지 않은 견적서를 추가한다.
	 * @param rfxBids
	 */
	private void addNotExsistBiddingsToNegoBids(List<Map<String, Object>> rfxBids) {
		Map<String, Object> searchParam = Maps.newHashMap();
		List<String> rfxBidUuids = Lists.newArrayList();
		
		for(Map rfxBid :rfxBids){
			if(searchParam.isEmpty()){
				searchParam.put("rfx_uuid", rfxBid.get("rfx_uuid"));
			}
			rfxBidUuids.add((String) rfxBid.get("rfx_bid_uuid"));
		}
		searchParam.put("rfx_bid_uuids", rfxBidUuids);
		
		List<Map<String, Object>> notExsistBiddings = this.searchInitSelectionYnFromBidding(searchParam);
		
		if(!notExsistBiddings.isEmpty()) {
			rfxBids.addAll(notExsistBiddings);
		}
	}

	/**
	 * 협상취소
	 * @param param
	 */
	public ResultMap cancelNego(Map param) {
		Map rfxData = (Map) param.get("rfxData");
		List items = (List) param.get("items");
		
		negoValidator.checkRfxProgSts(rfxData);
		
		for(Map item : (List<Map>) items) {
			this.deleteNegoTargetDetailSts(item);
			this.deleteNegoTargetHeaderSts(item);
			this.deleteCloseNegoJob((String)item.get("nego_uuid"));
		}
		
		rfxStatusService.cancelNego(rfxData);
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 협상 품의서 존재여부 확인
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap checkNegoApproval(Map param) {
		Map<String, Object> rfxData = (Map<String, Object>) param.get("rfxData");
		
		if(negoRepository.checkNegoApproval(rfxData)) {
			return ResultMap.FAIL();
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 협상 선정품의 삭제
	 *
	 * @param rfxData
	 * @return map
	 */
	public ResultMap deleteNegoApproval(Map param) {
		ResultMap result = ResultMap.getInstance();
		Map<String, Object> rfxData = (Map<String, Object>)param.get("rfxData");
		
		String aprvUuid = negoRepository.findNegoApprovalUuid(rfxData);
		
		if(!Strings.isNullOrEmpty(aprvUuid)) {
			Map negoHeader = negoRepository.findNegoRfxData(rfxData);
			Map<String, Object> deleteApproval = Maps.newHashMap();
			deleteApproval.put("apvl_uuid", aprvUuid);
			deleteApproval.put("task_uuid" , (String)negoHeader.get("rfx_uuid"));
			String slctnApvlStsCcd = (String) ("APVL_CNCL".equals(negoHeader.get("rfx_slctn_apvl_sts_ccd")) ? "CRNG" : negoHeader.get("rfx_slctn_apvl_sts_ccd"));
			deleteApproval.put("prev_apvl_sts_ccd", slctnApvlStsCcd);

			Map<String, Object> deleteParam = Maps.newHashMap();
			deleteParam.put("deleteApproval", deleteApproval);

			result = approvalService.deleteApproval(deleteParam);
		}
		return result;
	}
	
	/**
	 * 단건 견적서 선정여부 업데이트
	 * @param rfxBid
	 */
	public void updateRfxBiddingSelectionYn(Map<String, Object> rfxBid) {
		BigDecimal negoAmt = BigDecimal.ZERO;
		if(null != rfxBid.get("nego_amt")){
			negoAmt = new BigDecimal(String.valueOf(rfxBid.get("nego_amt")));
		}
		
		rfxBid.put("slctn_amt", negoAmt);
		rfxBidService.updateRfxBidStl(rfxBid);
		
		List<Map<String, Object>> bidItems = negoItemService.searchNegoRfxBidItemStl(rfxBid);
		for(Map<String,Object> item : bidItems) {
			rfxBidItemService.updateRfxBidItemStl(item);
		}
	}
	
	/**
	 * 협상 가능한 대상 조회 (RFX 기준)
	 * @param param
	 * @return
	 */
	public ResultMap getNegotiableTargets(Map param) {
		//rfxData조회
		Map<String, Object> rfxData = rfxService.findRfxByResult(param);
		List<Map<String, Object>> rfxBids = rfxBidService.findListNegotiableRfxBid(rfxData);
		
		return ResultMap.builder().resultStatus(ResultMap.STATUS.SUCCESS).resultData(rfxData).resultList(rfxBids).build();
	}
}
