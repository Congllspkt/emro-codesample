package smartsuite.app.bp.rfx.rfi.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.bp.rfx.rfi.event.RfiEventPublisher;
import smartsuite.app.bp.rfx.rfi.repository.RfiRepository;
import smartsuite.app.bp.rfx.rfi.scheduler.RfiSchedulerService;
import smartsuite.app.bp.rfx.rfi.validator.RfiValidator;
import smartsuite.app.bp.rfx.rfx.event.RfxEventPublisher;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.RfxStatusService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfiService {
	
	@Inject
	RfiRepository rfiRepository;
	
	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfiItemService rfiItemService;
	
	@Inject
	RfiVendorService rfiVendorService;
	
	@Inject
	RfiEventPublisher rfiEventPublisher;
	
	@Inject
	RfxEventPublisher rfxEventPublisher;

	/**
	 * The shared service.
	 */
	@Inject
	SharedService sharedService;
	
	/**
	 * The pro status processor.
	 */
	@Inject
	RfxStatusService rfxStatusService;
	
	@Inject
	RfiSchedulerService rfiSchedulerService;
	
	@Inject
	RfiValidator rfiValidator;

	@Inject
	MailService mailService;

	/**
	 * RFI 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfi(Map param) {
		// 대용량 처리
		return rfiRepository.findListRfi(param);
	}
	
	/**
	 * RFI 제출 업체 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfiSubmitVendor(Map param) {
		// 대용량 처리
		return rfiRepository.findListRfiSubmitVendor(param);
	}
	
	/**
	 * RFI 상세 조회
	 *
	 * @param param
	 * @return
	 */
	public Map findRfi(Map param) {
		Map resultMap = Maps.newHashMap();
		Map rfiData = rfiRepository.findRfi(param);
		
		if(rfiData != null) {
			resultMap.put("rfiData", rfiData);
			resultMap.put("rfiItems", rfiItemService.findListRfiItem(param));
			resultMap.put("rfiVendors", rfiVendorService.findListRfiVendor(param));
		}
		return resultMap;
	}
	
	public Map findRfiDefaultDataByReqItemIds(Map param) {
		Map rfiData = Maps.newHashMap();
		
		List rfiItems = rfxReceiptService.findRfxDefaultDataByReqRcpts((List) param.get("rfx_req_rcpt_uuids"));
		if(rfiItems == null) {
			return null;
		}
		
		int maxNo = 10;
		for(Map rfiItem : (List<Map>) rfiItems) {
			rfiItem.put("rfi_qty", rfiItem.get("req_qty"));
			rfiItem.put("rfi_lno", maxNo);
			maxNo += 10;
		}

		Map firstRfiItem = (Map) rfiItems.get(0);
		rfiData.put("oorg_cd", firstRfiItem.get("oorg_cd"));
		rfiData.put("rfi_sts_ccd", "CRNG");

		if("PR".equals(firstRfiItem.get("req_doc_typ_ccd"))) {
			rfiData.put("has_pr_item_yn", "Y");
			rfiData.put("has_upcr_item_yn", "N");
		} else if("UPCR".equals(firstRfiItem.get("req_doc_typ_ccd"))) {
			rfiData.put("has_pr_item_yn", "N");
			rfiData.put("has_upcr_item_yn", "Y");
		}
		rfiData.put("has_no_cd_item", this.hasNoCdItems(rfiItems) ? "Y" : "N");
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfiData", rfiData);
		resultMap.put("rfiItems", rfiItems);
		return resultMap;
	}

	private Boolean hasNoCdItems(List rfiItems) {
		boolean hasNoCdItem = false;
		for(Map rfiItem : (List<Map>) rfiItems) {
			if("CDLS".equals(rfiItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
				break;
			}
		}
		return hasNoCdItem;
	}

	/**
	 * RFI 정보 저장
	 *
	 * @param param
	 * @return
	 */
	private ResultMap saveRfi(Map param) {
		Map rfiData = (Map) param.get("rfiData");
		List insertRfiItems = (List) param.get("insertRfiItems");
		List updateRfiItems = (List) param.get("updateRfiItems");
		List deleteRfiItems = (List) param.get("deleteRfiItems");
		List insertRfiVendors = (List) param.get("insertRfiVendors");
		List updateRfiVendors = (List) param.get("updateRfiVendors");
		List deleteRfiVendors = (List) param.get("deleteRfiVendors");
		
		String rfiId = (String) rfiData.get("rfi_uuid");
		if(Strings.isNullOrEmpty(rfiId)) {
			rfiData.put("rfi_uuid", UUID.randomUUID().toString());
			rfiData.put("rfi_no", sharedService.generateDocumentNumber("RI"));
			
			rfiRepository.insertRfi(rfiData);
		} else {
			rfiRepository.updateRfi(rfiData);
		}
		
		// Rfi 품목 작업
		rfiItemService.deleteRfiItem(deleteRfiItems);
		rfiItemService.insertRfiItem(rfiData, insertRfiItems);
		rfiItemService.updateRfiItem(updateRfiItems);
		
		// Rfi 업체 작업
		rfiVendorService.deleteRfiVendor(deleteRfiVendors);
		rfiVendorService.insertRfiVendor(rfiData, insertRfiVendors);
		rfiVendorService.updateRfiVendor(updateRfiVendors);

		Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("rfi_uuid", rfiData.get("rfi_uuid"));
		return ResultMap.SUCCESS(resultDataMap);
	}
	
	/**
	 * RFI 저장
	 *
	 * @param param
	 * @return
	 */
	public ResultMap temporarySaveRfi(Map param) {
		Map rfiData = (Map) param.get("rfiData");
		List prItemIds = (List) param.get("prItemIds");
		List upcrItemIds = (List) param.get("upcrItemIds");
		
		String rfiId = (String) rfiData.get("rfi_uuid");
		ResultMap validator;
		if(Strings.isNullOrEmpty(rfiId) && !prItemIds.isEmpty()) {
			// 구매요청접수 화면에서 넘어와 신규 RFx 생성하는 경우
			validator = rfxEventPublisher.validateCreateRfxByPr(prItemIds);
			if(validator.isSuccess()) {
				// PR 모듈에 해당 품목들 접수되었음을 알린다
				rfxEventPublisher.receivePr(prItemIds);
			}
		} else if(Strings.isNullOrEmpty(rfiId) && !upcrItemIds.isEmpty()) {
			validator = rfxEventPublisher.validateCreateRfxByUpcr(upcrItemIds);
			if(validator.isSuccess()) {
				// 단가계약요청품목 존재 시 단가계약요청품목 접수 처리
				rfxEventPublisher.receiveUpcr(upcrItemIds);
			}
		} else {
			// 화면에서 넘어온 진행상태와 서버의 진행상태 비교
			validator = rfiValidator.compareRfiProgSts(rfiData);
		}
		
		if(!validator.isSuccess()) {
			return validator;
		}
		
		ResultMap resultMap = this.saveRfi(param);
		if(resultMap.isSuccess()) {
			rfiData = resultMap.getResultData();
			// 저장 상태
			rfxStatusService.temporarySaveRfi(rfiData);
		}
		
		return resultMap;
	}
	
	/**
	 * RFI 요청
	 *
	 * @param param
	 * @return
	 */
	public ResultMap requestRfi(Map param) {
		Map rfiData = (Map) param.get("rfiData");
		List prItemIds = (List) param.get("prItemIds");
		List upcrItemIds = (List) param.get("upcrItemIds");

		String rfiId = (String) rfiData.get("rfi_uuid");
		ResultMap validator;
		if(Strings.isNullOrEmpty(rfiId) && !prItemIds.isEmpty()) {
			// 구매요청접수 화면에서 넘어와 신규 RFx 생성하는 경우
			validator = rfxEventPublisher.validateCreateRfxByPr(prItemIds);
			if(validator.isSuccess()) {
				// PR 모듈에 해당 품목들 접수되었음을 알린다
				rfxEventPublisher.receivePr(prItemIds);
			}
		} else if(Strings.isNullOrEmpty(rfiId) && !upcrItemIds.isEmpty()) {
			validator = rfxEventPublisher.validateCreateRfxByUpcr(upcrItemIds);
			if(validator.isSuccess()) {
				// 단가계약요청품목 존재 시 단가계약요청품목 접수 처리
				rfxEventPublisher.receiveUpcr(upcrItemIds);
			}
		} else {
			// 화면에서 넘어온 진행상태와 서버의 진행상태 비교
			validator = rfiValidator.compareRfiProgSts(rfiData);
		}
		
		if(!validator.isSuccess()) {
			return validator;
		}
		
		ResultMap resultMap = this.saveRfi(param);
		if(resultMap.isSuccess()) {
			rfiData = resultMap.getResultData();
			
			// 요청 상태
			rfxStatusService.requestRfi(rfiData);
			//스케줄러 등록
			rfiSchedulerService.requestRfi(rfiData);
			
			// RFI 요청 메일
			mailService.sendAsync("RFI_REQUEST", (String) rfiData.get("rfi_uuid"));
		}
		
		return resultMap;
	}
	
	/**
	 * RFI 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteRfi(Map param) {
		// validate
		ResultMap validator = rfiValidator.compareRfiProgSts(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		this.deleteRfiProcess(param);
		return ResultMap.SUCCESS();
	}
	
	protected void deleteRfiProcess(Map param) {
		rfxStatusService.deleteRfi(param);
		rfiVendorService.deleteRfiVendorByRfi(param);
		rfiItemService.deleteRfiItemByRfi(param);
		rfiRepository.deleteRfi(param);
	}
	
	/**
	 * RFI 다중 건 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteListRfi(Map param) {
		List invalidRfis = Lists.newArrayList();
		List notExistRfis = Lists.newArrayList();
		
		List deleteRfis = (List) param.get("deleteRfis");
		for(Map rfi : (List<Map>) deleteRfis) {
			ResultMap result = this.deleteRfi(rfi);
			
			if(Const.INVALID_STATUS_ERR.equals(result.getResultStatus())) {
				invalidRfis.add(result.getResultData());
			} else if(Const.NOT_EXIST.equals(result.getResultStatus())) {
				notExistRfis.add(rfi);
			}
		}
		
		return ValidatorUtil.setupDataListForValidationDataList(deleteRfis, invalidRfis, notExistRfis);
	}
	
	/**
	 * RFI 마감 (마감일자 시 schedule 수행)
	 *
	 * @param param
	 */
	public void closeRfi(HashMap<String, Object> param) {
		rfxStatusService.closeRfi(param);
	}
	
	/**
	 * RFI 종료 처리
	 *
	 * @param param
	 * @return
	 */
	public ResultMap completeListRfi(Map param) {
		List invalidRfis = Lists.newArrayList();
		List notExistRfis = Lists.newArrayList();
		
		List closeRfis = (List) param.get("closeRfis");
		for(Map rfi : (List<Map>) closeRfis) {
			ResultMap result = this.completeRfi(rfi);
			
			if(Const.INVALID_STATUS_ERR.equals(result.getResultStatus())) {
				invalidRfis.add(result.getResultData());
			} else if(Const.NOT_EXIST.equals(result.getResultStatus())) {
				notExistRfis.add(rfi);
			}
		}
		
		return ValidatorUtil.setupDataListForValidationDataList(closeRfis, invalidRfis, notExistRfis);
	}
	
	public ResultMap completeRfi(Map param) {
		ResultMap validator = rfiValidator.compareRfiProgSts(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		// RFI 상태가 진행중 인 경우(아직 마감되지 않음) -> 마감스케줄러 삭제처리
		rfiSchedulerService.completeRfi(param);
		// 상태 업데이트
		rfxStatusService.completeRfi(param);

		// RFI 종료 메일 발송
		String rfiUuid = param.get("rfi_uuid") == null ? "" : (String) param.get("rfi_uuid");
		mailService.sendAsync("RFI_COMPLETE", rfiUuid, param);
		
		return ResultMap.SUCCESS();
	}

	/**
	 * RFI -> RFX
	 *
	 * @param param
	 * @return
	 */
	public Map findDefaultDataByRfi(Map param) {
		Map resultMap = Maps.newHashMap();
		Map rfiData = rfiRepository.findRfi(param);

		List<Map> rfiVendors = rfiVendorService.findListRfiVendor(param);
		if(rfiVendors.size() > 0) {
			for(Map data : rfiVendors) {
				data.put("disp_vd_cd", data.get("erp_vd_cd"));
				data.put("auto_rcmd_vd_yn", "N");
				data.put("multrnd_subj_xcept_yn", "N");
			}
		}

		if(rfiData != null) {
			resultMap.put("rfiData", rfiData);
			resultMap.put("rfiVendors", rfiVendors);
		}
		return resultMap;
	}

	/**
	 * RFI 연계 데이터 확인
	 *
	 * @param param
	 * @return
	 */
	public boolean checkRfiData(Map param) {
		return rfiRepository.checkRfiData(param);
	}

	/**
	 * RFI -> RFX
	 *
	 * @param param
	 */
	public void updateRfiByRfxUuid(Map param) {
		Map rfxData = Maps.newHashMap();

		if(param.get("rfi_uuid") != null) {
			rfxData.put("rfi_uuid", param.get("rfi_uuid"));
		} else {
			rfxData.put("rfx_uuid", param.get("rfx_uuid"));
		}

		Map rfiData = rfiRepository.getRfiData(rfxData);

		String rfiId = (String) rfiData.get("rfi_uuid");
		String rfxId = (String) rfiData.get("rfx_uuid");

		if(rfxId == null) {
			rfxData.put("rfx_uuid", param.get("rfx_uuid"));
		} else {
			rfxData.put("rfi_uuid", rfiId);
			rfxData.put("rfx_uuid", "");
		}

		rfiRepository.updateRfiByRfxUuid(rfxData);
	}

	/**
	 * RFI 품목 상태 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkPrStatus(Map param) {
		List<String> prItemIds = (List) param.get("prItemIds");
		List<String> upcrItemIds = (List<String>) param.get("upcrItemIds");

		if(prItemIds.size() > 0) {
			// validate
			ResultMap validator = rfiEventPublisher.validateCheckPrStatus(prItemIds);
			if(!validator.isSuccess()) {
				return validator;
			}
		}

		if(upcrItemIds.size() > 0) {
			// validate
			ResultMap validator = rfiEventPublisher.validateCheckUpcrStatus(upcrItemIds);
			if(!validator.isSuccess()) {
				return validator;
			}
		}

		return ResultMap.SUCCESS();
	}

}
