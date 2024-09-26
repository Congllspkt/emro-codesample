package smartsuite.app.bp.pro.po.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.po.service.PoReceiptService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.MapUtils;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class PoReceiptEventListener {
	
	static final Logger LOGGER = LoggerFactory.getLogger(PoReceiptEventListener.class);
	
	@Inject
	PoReceiptService poReceiptService;
	
	/**
	 * PO 요청데이터 적재
	 *
	 * @param event event.data : Map<br>
	 * ----------------------------------<br>
	 * oorg_cd : 운영조직 코드<br>
	 * req_tit : 요청 제목<br>
	 * purc_typ_ccd : 구매 유형 공통코드<br>
	 * req_pic_id : 요청 담당자 아이디<br>
	 * purc_grp_cd : 구매 그룹 코드<br>
	 * req_doc_typ_ccd : 요청 문서 유형 공통코드<br>
	 * po_typ_ccd : 발주 유형 공통코드<br>
	 * po_chg_typ_ccd : 발주 변경 유형 공통코드<br>
	 * po_req_uuid : 발주 요청 UUID<br>
	 * po_req_no : 발주 요청 번호<br>
	 * po_cnd_uuid : 발주 조건 UUID<br>
	 * vd_cd : 협력사 코드<br>
	 * ----------------------------------<br>
	 * req_doc_typ_ccd.RFX - RFX 모듈에서 호출<br>
	 * req_doc_typ_ccd.CNTR - 계약 모듈에서 호출<br>
	 * po_typ_ccd.URGPURC - 긴급 발주<br>
	 * po_typ_ccd.SPOTCNTR - 일반계약 발주 (발주 계약서 작성 후)<br>
	 * po_typ_ccd.SPOTPURC - 일반 발주 (견적 선정 후 발주)<br>
	 * po_chg_typ_ccd.NEW - 신규 요청<br>
	 * po_chg_typ_ccd.CHG - 변경 요청<br>
	 * po_chg_typ_ccd.TRMN - 해지 요청<br>
	 */
	@EventListener(condition = "#event.eventId == 'requestPo'")
	public void requestPo(CustomSpringEvent event) {
		Map<String, Object> data = (Map<String, Object>) event.getData();
		poReceiptService.receiptReqPo(data);
	}
	
	/**
	 * 단가계약 기반 발주 요청데이터 적재
	 * @param event
	 *
	 * event.data : List<br>
	 * ----------------------------------<br>
	 * oorg_cd : 운영조직 코드<br>
	 * req_doc_typ_ccd : 요청 문서 유형 공통코드<br>
	 * req_tit : 요청 제목<br>
	 * po_req_uuid : 발주 요청 UUID<br>
	 * purc_typ_ccd : 구매 유형 공통코드<br>
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
	 * ----------------------------------<br>
	 * req_doc_typ_ccd.RFX - RFX 모듈에서 호출<br>
	 */
	@EventListener(condition = "#event.eventId == 'requestPoByUprcItem'")
	public void requestPoByUprcItem(CustomSpringEvent event) {
		ResultMap resultMap = poReceiptService.receiptReqPoByUprcItem((List<Map>) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 견적으로부터 들어온 요청 건 삭제 가능여부 Validation
	 * <p>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'validateDeleteRequestReqInfoByRfxChangeReq'")
	public void validateDeleteRequestReqInfoByRfxChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = poReceiptService.validateDeleteRequestReqInfoByRfxChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 견적으로부터 들어온 요청 건 삭제
	 * <p>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 *
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteRequestReqInfoByRfxChangeReq'")
	public void deleteRequestReqInfoByRfxChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = poReceiptService.deleteRequestReqInfoByRfxChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}
	
	/**
	 * 견적으로부터 들어온 요청 건 복구
	 * <p>
	 * req_no : 요청 번호<br>
	 * req_revno : 요청 차수<br>
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'recoveryRequestReqInfoByRfxChangeReq'")
	public void recoveryRequestReqInfoByRfxChangeReq(CustomSpringEvent event) {
		ResultMap resultMap = poReceiptService.recoveryRequestReqInfoByRfxChangeReq((Map) event.getData());
		event.setResult(resultMap);
	}
}