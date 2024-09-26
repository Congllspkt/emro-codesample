package smartsuite.app.bp.rfx.receipt.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptEventService;
import smartsuite.app.bp.rfx.receipt.service.RfxReceiptService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxReceiptEventListener {
	
	@Inject
	RfxReceiptService rfxReceiptService;

	@Inject
	RfxReceiptEventService rfxReceiptEventService;
	
	/**
	 * RFX 요청데이터 적재
	 * @param event
	 *
	 * event.data : List<br>
	 * ----------------------------------<br>
	 * oorg_cd : 운영조직 코드<br>
	 * req_tit : 요청 제목<br>
	 * purc_typ_ccd : 구매 유형 공통코드<br>
	 * req_purp_ccd : 요청 목적 공통코드<br>
	 * plt_cd : 플랜트 코드<br>
	 * item_oorg_cd : 품목 운영조직 코드<br>
	 * item_cd : 품목 코드<br>
	 * item_nm : 품목 명<br>
	 * item_nm_en : 품목 명 영문<br>
	 * item_spec : 품목 규격<br>
	 * item_spec_dtl : 품목 규격 상세<br>
	 * item_cd_crn_typ_ccd : 품목 코드 생성 유형 공통코드<br>
	 * uom_ccd : uom 공통코드<br>
	 * cur_ccd : 통화 공통코드<br>
	 * req_qty : 요청 수량<br>
	 * req_uprc : 요청 단가<br>
	 * req_amt : 요청 금액<br>
	 * wh_ccd : 창고 공통코드<br>
	 * req_dlvy_dt : 요청 납품 일자<br>
	 * const_st_dt : 공사 시작 일자<br>
	 * const_exp_dt : 공사 만료 일자<br>
	 * dlvy_plc : 납품 장소<br>
	 * req_pic_id : 요청 담당자 아이디<br>
	 * purc_grp_cd : 구매 그룹 코드<br>
	 * gr_pic_id : 입고 담당자 아이디<br>
	 * req_item_uuid : 요청 품목 uuid<br>
	 * req_uuid : 요청 uuid<br>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 * req_lno : 요청 항번<br>
	 * cntr_uuid : 계약 UUID<br>
	 * cntr_no : 계약 번호<br>
	 * cntr_revno : 계약 차수<br>
	 * purc_cntr_uuid : 구매 계약 UUID<br>
	 * purc_cntr_info_uuid : 구매 계약 정보 UUID<br>
	 * purc_cntr_item_uuid : 구매 계약 품목 UUID<br>
	 * purc_cntr_item_lno : 구매 계약 품목 항번<br>
	 * ----------------------------------
	 */
	@EventListener(condition = "#event.eventId == 'requestRfx'")
	public void requestRfx(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.receiptReqRfx((List<Map>) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 발주 요청 목록으로 스킵했던 단가 계약 품목이 발주 요청에서 반려난 경우 RFX 요청 목록에서 적재
	 *
	 * @param event
	 * event.data : List<br>
	 * ----------------------------------<br>
	 * oorg_cd : 운영조직 코드<br>
	 * req_tit : 요청 제목<br>
	 * purc_typ_ccd : 구매 유형 공통코드<br>
	 * req_purp_ccd : 요청 목적 공통코드<br>
	 * plt_cd : 플랜트 코드<br>
	 * item_oorg_cd : 품목 운영조직 코드<br>
	 * item_cd : 품목 코드<br>
	 * item_nm : 품목 명<br>
	 * item_nm_en : 품목 명 영문<br>
	 * item_spec : 품목 규격<br>
	 * item_spec_dtl : 품목 규격 상세<br>
	 * item_cd_crn_typ_ccd : 품목 코드 생성 유형 공통코드<br>
	 * uom_ccd : uom 공통코드<br>
	 * cur_ccd : 통화 공통코드<br>
	 * req_qty : 요청 수량<br>
	 * req_uprc : 요청 단가<br>
	 * req_amt : 요청 금액<br>
	 * wh_ccd : 창고 공통코드<br>
	 * req_dlvy_dt : 요청 납품 일자<br>
	 * const_st_dt : 공사 시작 일자<br>
	 * const_exp_dt : 공사 만료 일자<br>
	 * dlvy_plc : 납품 장소<br>
	 * req_pic_id : 요청 담당자 아이디<br>
	 * purc_grp_cd : 구매 그룹 코드<br>
	 * gr_pic_id : 입고 담당자 아이디<br>
	 * req_item_uuid : 요청 품목 uuid<br>
	 * req_uuid : 요청 uuid<br>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 * req_lno : 요청 항번<br>
	 * ----------------------------------
	 */
	@EventListener(condition = "#event.eventId == 'returnUprcItemReq'")
	public void returnUprcItemReq(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.returnUprcItemReq((List<Map>) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 요청 데이터 삭제 가능여부 Validation
	 *
	 * <p>
	 * req_doc_typ_ccd : 요청 문서 유형<br>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'validateDeleteRequestReqInfoByChangeReq'")
	public void validateDeleteRequestReqInfoByChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.validateDeleteRequestReqInfoByChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 요청 후 삭제가 필요한 경우 해당 이벤트 사용
	 * STS 'D' 처리
	 * <p>
	 * req_doc_typ_ccd : 요청 문서 유형<br>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteRequestReqInfoByChangeReq'")
	public void deleteRequestReqInfoByChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.deleteRequestReqInfoByChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}
	
	@EventListener(condition = "#event.eventId == 'recoveryRequestReqInfoByChangeReq'")
	public void recoveryRequestReqInfoByChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.recoveryRequestReqInfoByChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}

	@EventListener(condition = "#event.eventId == 'createRfxreqFromPcm'")
	public void createRfxreqFromPcm(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptService.createRfxreqFromPcm((List) event.getData());
		event.setResult(resultMap);
	}

	@EventListener(condition = "#event.eventId=='resetSpotPrItem'")
	public void resetSpotPrItem(CustomSpringEvent event) {
		ResultMap resultMap = rfxReceiptEventService.resetSpotPrItem((Map) event.getData());
        event.setResult(resultMap);
	}
}
