package smartsuite.app.bp.rfx.rfxpreinsp.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfxpreinsp.repository.RfxPreInspRepository;
import smartsuite.app.bp.rfx.rfxpreinsp.scheduler.RfxPreInspSchedulerService;
import smartsuite.app.bp.rfx.rfxpreinsp.validator.RfxPreInspValidator;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.*;

/**
 * RfxPreInsp 관련 처리하는 서비스 Class입니다.
 *
 * @FileName RfxPreInspService.java
 * @package smartsuite.app.bp.rfx.rfxpreinsp
 * @Since 2022. 9. 13
 * @see
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxPreInspService {
	
	@Inject
	RfxPreInspRepository rfxPreInspRepository;
	
	/**
	 * The rfx pre insp scheduler service.
	 */
	@Inject
	RfxPreInspSchedulerService rfxPreInspSchedulerService;
	
	@Inject
	RfxPreInspValidator rfxPreInspValidator;

	@Inject
	MailService mailService;
	
	/**
	 * list rfx pre insp 조회한다.
	 *
	 * @param param the param
	 * @return the list< map< string, object>>
	 * @Date : 2022. 9. 13
	 * @Method Name : findListRfxPreInsp
	 */
	public List findListRfxPreInsp(Map param) {
		return rfxPreInspRepository.findListRfxPreInsp(param);
	}
	
	/**
	 * rfx pre insp 조회한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2022. 9. 13
	 * @Method Name : findRfxPreInsp
	 */
	public Map findRfxPreInsp(Map param) {
		return rfxPreInspRepository.findRfxPreInsp(param);
	}
	
	/**
	 * 견적요청 ID 기준 사전심사 기본정보를 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 *
	 * @param param
	 * @return Map
	 */
	public Map findRfxPreInspByRfxId(Map param) {
		return rfxPreInspRepository.findRfxPreInspByRfxId(param);
	}
	
	/**
	 * Regist rfx pre insp.
	 *
	 * @param param the param
	 */
	public void registRfxPreInsp(Map param) {
		if(param == null) {
			return;
		}
		
		Map rfxInfo = rfxPreInspRepository.findRfx(param);
		if(rfxInfo == null) {
			return;
		}
		
		//rfx_id만으로 rfx_pre_insp 조회
		Map rfxPreInsp = rfxPreInspRepository.findRfxPreInspByRfxId(param);
		
		String rfxPreInspUseYn = (String) rfxInfo.get("rfx_presn_use_yn");
		
		if("Y".equals(rfxPreInspUseYn)) {
			if(rfxPreInsp == null) {
				// 시작일시
				Date startDt = new Date();    // 즉시시작일 경우 현재시간
				if("N".equals(rfxInfo.get("immed_rfx_presn_st_use_yn"))) {
					startDt = (Date) rfxInfo.get("rfx_presn_st_dttm");
				}
				rfxInfo.put("rfx_presn_st_dttm", startDt);
				rfxPreInspRepository.updatePreInspStartDt(rfxInfo);
			}
			// 스케쥴러 등록 - startRfx 실행시
			rfxPreInspSchedulerService.noticeRfxPreInsp(rfxInfo, rfxPreInsp);
		} else {
			//사용안함으로 변경시 삭제처리
			if(rfxPreInsp != null) {
				//삭제처리
				String rfxId = (String) rfxPreInsp.get("rfx_uuid");
				rfxPreInspSchedulerService.deleteRfxPreInsp(rfxId);
				rfxPreInspRepository.deleteRfxPreInsp(rfxPreInsp);
			}
		}
	}
	
	/**
	 * Start rfx pre insp.
	 *
	 * @param param the param
	 */
	public void startRfxPreInsp(HashMap<String, Object> param) {
		//rfx_id만으로 rfx_pre_insp 조회
		Map rfxInfo = rfxPreInspRepository.findRfx(param);
		Map rfxPreInsp = rfxPreInspRepository.findRfxPreInspByRfxId(param);
		
		if(rfxPreInsp == null) {
			Map saveParam = Maps.newHashMap(rfxInfo);
			saveParam.put("rfx_presn_uuid", UUID.randomUUID().toString());
			rfxPreInspRepository.insertRfxPreInsp(saveParam);
		}
	}
	
	/**
	 * End rfx pre insp.
	 *
	 * @param param the param
	 */
	public void endRfxPreInsp(HashMap<String, Object> param) {
		//TODO
	}
	
	
	/**
	 * Cancel rfx.
	 *
	 * @param param the param
	 */
	public void cancelRfxPreInsp(Map param) {
		//마감시간 안된 사전심사건들 조회
		Map rfxPreInsp = rfxPreInspRepository.findNoCloseRfxPreInspByRfxId(param);
		if(rfxPreInsp != null) {
			//삭제처리
			String rfxId = (String) rfxPreInsp.get("rfx_uuid");
			
			//스케줄러에서 삭제처리
			rfxPreInspSchedulerService.deleteRfxPreInsp(rfxId);
			
			//사전심사건 삭제처리
			rfxPreInspRepository.deleteRfxPreInsp(rfxPreInsp);
		}
	}
	
	/**
	 * Close rfx pre insp.
	 * 현재 RFX 마감 시간 기준으로  사전심사 마감시간이 남았으면 현재시간으로 마감처리
	 *
	 * @param param the param
	 */
	public void closeRfxPreInsp(Map<String, Object> param) {
		//사전심사건들 조회
		Map rfxPreInsp = rfxPreInspRepository.findRfxPreInspByRfxId(param);
		if(rfxPreInsp != null) {
			//마감처리
			String rfxId = (String) rfxPreInsp.get("rfx_uuid");
			rfxPreInspSchedulerService.deleteRfxPreInsp(rfxId);
			
			rfxPreInsp.put("ery_clsg_rsn", param.get("ery_clsg_rsn"));
			rfxPreInspRepository.updateRfxPreInspEndDt(rfxPreInsp);
			rfxPreInspRepository.updateCloseRfxPreInsp(rfxPreInsp);
		}
	}
	
	/**
	 * 여러건 조기 마감.
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap byPassCloseRfxPreInsps(Map param) {
		List failRfxList = Lists.newArrayList();
		List rfxPreInspDatas = (List) param.get("rfxPreInspDatas");
		
		for(Map rfxPreInspData : (List<Map>) rfxPreInspDatas) {
			ResultMap result = this.byPassCloseRfxPreInsp(rfxPreInspData);
			
			if(!result.isSuccess()) {
				failRfxList.add(result);
			}
		}
		
		if(!failRfxList.isEmpty()) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFX_CLOSE_BYPASS_ERR)
			                .resultList(failRfxList)
			                .build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 조기 마감
	 *
	 * @param param the param
	 * @return the map
	 */
	private ResultMap byPassCloseRfxPreInsp(Map param) {
		Map rfxPreInspData = this.findRfxPreInsp(param);
		
		if(rfxPreInspData == null) {
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.NOT_EXIST)
			                .resultData(param)
			                .build();
		}
		
		rfxPreInspData.put("ery_clsg_rsn", param.get("ery_clsg_rsn"));
		this.closeRfxPreInsp(rfxPreInspData);

		// 적격/비적격 메일 발송
		this.bidQualificationMailSend(rfxPreInspData);
		
		return ResultMap.SUCCESS(rfxPreInspData);
	}
	
	/**
	 * 사전심사참가신청 제출서류 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_presn_afp_uuid - 사전심사 참가신청 ID<br>
	 *
	 * @param param
	 * @return List
	 */
	public List findListRfxPreInspAppAttach(Map param) {
		return rfxPreInspRepository.findListRfxPreInspAppAttach(param);
	}
	
	public Map findListRfxPreInspDetail(Map param) {
		Map rfxPreInspInfo = param;
		rfxPreInspInfo.putAll(this.findRfxPreInsp(param));
		List list = Lists.newArrayList();
		
		// 경쟁 유형 공개인 경우
		if("OBID".equals(rfxPreInspInfo.get("comp_typ_ccd"))) {
			list = rfxPreInspRepository.findListRfxPreInspDetailByOpen(rfxPreInspInfo);
		} else {
			list = rfxPreInspRepository.findListRfxPreInspDetail(rfxPreInspInfo);
		}
		
		Map resultData = Maps.newHashMap();
		resultData.put("rfxPreInspInfo", rfxPreInspInfo);
		resultData.put("vdList", list);
		return resultData;
	}
	
	public Map findRfxPreInspApp(Map param) {
		if(param == null) {
			return null;
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxPreInspInfo", rfxPreInspRepository.findRfxPreInspApp(param));
		resultMap.put("attachList", rfxPreInspRepository.findListRfxPreInspAppedAttach(param));
		return resultMap;
	}
	
	/**
	 * 사전심사 적격 진행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxPreInspInfo - 사전심사 정보<br>
	 * param.selectedList - 선택된 업체 다건
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap passRfxPreInsp(Map param) {
		Map rfxPreInspInfo = rfxPreInspRepository.findRfxPreInsp((Map) param.get("rfxPreInspInfo"));
		List selectedList = (List) param.get("selectedList");
		
		//validation
		ResultMap validator = rfxPreInspValidator.passRfxPreInsp(rfxPreInspInfo, selectedList);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// eval_rst_cd : 적격여부
		param.put("presn_res_ccd", "QUAL");
		rfxPreInspRepository.updateRfxPreInspEvalRstCd(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 사전심사 부적격 진행한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxPreInspInfo - 사전심사 정보<br>
	 * param.selectedList - 선택된 업체 다건
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap unPassRfxPreInsp(Map param) {
		Map rfxPreInspInfo = rfxPreInspRepository.findRfxPreInsp((Map) param.get("rfxPreInspInfo"));
		List selectedList = (List) param.get("selectedList");
		
		//validation
		ResultMap validator = rfxPreInspValidator.unPassRfxPreInsp(rfxPreInspInfo, selectedList);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// eval_rst_cd : 적격여부
		param.put("presn_res_ccd", "DQ");
		rfxPreInspRepository.updateRfxPreInspEvalRstCd(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 사전심사 완료 처리한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_presn_uuid - RFx 사전심사 ID<br>
	 *
	 * @param param - 사전심사 정보
	 * @return ResultMap
	 */
	public ResultMap compRfxPreInsp(Map param) {
		Map rfxPreInspInfo = rfxPreInspRepository.findRfxPreInsp(param);
		
		//validation
		ResultMap validator = rfxPreInspValidator.compRfxPreInsp(rfxPreInspInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// 완료 처리
		rfxPreInspRepository.compRfxPreInsp(param);

		// 적격 / 비적격 메일 발송
		this.bidQualificationMailSend(rfxPreInspInfo);

		return ResultMap.SUCCESS();
	}

	/**
	 * 적격/비적격 메일 발송
	 * @param rfxPreInspInfo
	 */
	private void bidQualificationMailSend(Map rfxPreInspInfo) {
		//비 적격 메일 발송
		mailService.sendAsync("BID_DIS_NOTI_QUAL_EVAL", (String) rfxPreInspInfo.get("rfx_presn_uuid"),rfxPreInspInfo);
		// 적격 메일 발송
		mailService.sendAsync("BID_NOTI_QUAL_EVAL", (String) rfxPreInspInfo.get("rfx_presn_uuid"),rfxPreInspInfo);
	}

	public ResultMap checkRfxPreInspCreateRfxBid(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map rfxInfo = rfxPreInspRepository.findRfx(param);
		if(rfxInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		// 사전심사 사용안하는 경우 통과
		if("N".equals(rfxInfo.get("rfx_presn_use_yn"))) {
			return ResultMap.SUCCESS();
		}
		
		// 견적서 사전심사 적격 통과 여부 체크
		Map vdPreInspInfo = rfxPreInspRepository.findVdRfxPreInspApp(param);
		if(vdPreInspInfo == null) {
			// 심사중
			return ResultMap.builder()
			                .resultStatus(RfxConst.BEFORE_RFX_PRE_INSP)
			                .build();
		}
		
		String validYn = (String) vdPreInspInfo.get("rfx_bid_efct_yn");
		if(validYn == null) {
			// 심사중
			return ResultMap.builder()
			                .resultStatus(RfxConst.BEFORE_RFX_PRE_INSP)
			                .build();
		}
		if("DQ".equals(validYn)) {
			// 부적격
			return ResultMap.builder()
			                .resultStatus(RfxConst.NO_PASS)
			                .build();
		}
		
		//적격
		return ResultMap.SUCCESS(param);
	}
}
