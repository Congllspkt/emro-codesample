package smartsuite.app.bp.pro.gr.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.asn.service.AsnItemService;
import smartsuite.app.bp.pro.asn.service.AsnService;
import smartsuite.app.bp.pro.gr.event.GrEvalEventPublisher;
import smartsuite.app.bp.pro.gr.repository.GrRepository;
import smartsuite.app.bp.pro.gr.validator.GrValidator;
import smartsuite.app.bp.pro.po.service.PoItemService;
import smartsuite.app.bp.pro.po.service.PoService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;
import smartsuite.data.FloaterStream;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 입고(검수) 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName GrService.java
 * @package smartsuite.app.bp.pro.gr
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class GrService {
	
	@Inject
	private SharedService sharedService;
	
	@Inject
	private PoService poService;
	
	@Inject
	private PoItemService poItemService;
	
	@Inject
	private GrValidator grValidator;
	
	@Inject
	private ProStatusService proStatusService;
	
	private static final String DATE_FORMAT = "yyyyMMdd";
	
	@Inject
	private GrItemService grItemService;
	
	@Inject
	private GrRepository grRepository;
	
	@Inject
	public GrEvalEventPublisher grEventPublisher;
	
	@Inject
	private AsnItemService asnItemService;
	@Inject
	private AsnService asnService;
	
	@Inject
	private GrEvalService grEvalService;
	
	/**
	 * 입고(검수) 헤더를 등록한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : insertGrHeader
	 */
	public void insertGr(Map<String, Object> param) {
		grRepository.insertGr(param);
	}
	
	/**
	 * 입고(검수) 품목을 등록한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : insertGrItem
	 */
	public void insertGrItem(Map<String, Object> param) {
		grItemService.insertGrItem(param);
	}
	
	/**
	 * 입고(검수) 헤더를 수정한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updateGrHeader
	 */
	public void updateGrByGrUuid(Map<String, Object> param) {
		grRepository.updateGrByGrUuid(param);
	}
	
	/**
	 * 입고(검수) 품목을 수정한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updateGrItem
	 */
	public void updateGrItem(Map<String, Object> param) {
		grItemService.updateGrItem(param);
	}
	
	/**
	 * 입고(검수) 품목을 삭제한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 */
	public void deleteGrItemByGrItem(Map<String, Object> param) {
		grItemService.deleteGrItemByGrItem(param);
	}
	
	/**
	 * 입고(검수) 헤더를 삭제한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 */
	public void deleteGrByGrUuid(Map<String, Object> param) {
		grRepository.deleteGrByGrUuid(param);
	}
	
	/**
	 * 입고(검수) 품목을 삭제한다.
	 *
	 * @param param the param
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : deleteListGrItemByGr
	 */
	public void deleteGrItemByGrUuid(Map<String, Object> param) {
		grItemService.deleteGrItemByGrUuid(param);
	}
	
	/**
	 * 입고 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public FloaterStream searchGr(Map<String, Object> param) {
		// 대용량 처리
		return grRepository.searchGr(param);
	}
	
	/**
	 * 입고(검수) 별 입고(검수) 품목 목록을 조회한다.
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> searchGrItemByGrUuid(Map<String, Object> param) {
		return grItemService.searchGrItemByGrUuid(param);
	}
	
	/**
	 * 입고(검수) 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findGr
	 */
	public Map<String, Object> findGr(Map<String, Object> param) {
		Map<String, Object> grData = grRepository.findGrByGrId(param);
		grData.put("items", this.searchGrItemByGrUuid(param));
		return grData;
	}
	
	/**
	 * 발주품목 아이디로 검수등록(입고) 초기화
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findGrInitialDataByPoItemUuid(Map<String, Object> param) {
		List<Map<String, Object>> grItems = poItemService.findGrItemByPoItemUuid(param);
		Map<String, Object> poEvalSetData = poItemService.findPoEvalSetInfoByPoItemUuid(param);
		int maxNo = 10;
		boolean hasNoCdItem = false;
		for(Map<String, Object> grItem : grItems) {
			grItem.put("gr_lno", maxNo);
			maxNo += 10;
			if("CDLS".equals(grItem.get("item_cd_crn_typ_ccd"))) {
				hasNoCdItem = true;
			}
		}
		
		Map<String, Object> firstGrItem = grItems.get(0);
		Map<String, Object> grData = Maps.newHashMap();
		grData.put("oorg_cd", firstGrItem.get("oorg_cd"));
		grData.put("vd_cd", firstGrItem.get("vd_cd"));
		grData.put("purc_typ_ccd", firstGrItem.get("purc_typ_ccd"));
		grData.put("pymtmeth_ccd", firstGrItem.get("pymtmeth_ccd"));
		grData.put("cur_ccd", firstGrItem.get("cur_ccd"));
		grData.put("purc_grp_cd", firstGrItem.get("purc_grp_cd"));
		grData.put("gr_pic_id", Auth.getCurrentUserName());
		grData.put("gr_dt", firstGrItem.get("gr_dt"));
		grData.put("dely_date", firstGrItem.get("gr_dt"));
		grData.put("gr_sts_ccd", "CRNG");
		grData.put("part_dely_yn", "N");
		grData.put("delay_imut_yn", "N");
		grData.put("has_no_cd_item", hasNoCdItem ? "Y" : "N");
		grData.put("ge_subj_yn", firstGrItem.get("ge_subj_yn"));
		
		if(poEvalSetData != null) {
			grData.put("ge_subj_yn", "Y");
			grData.put("po_ge_uuid", poEvalSetData.get("po_ge_uuid"));
		} else {
			grData.put("ge_subj_yn", "N");
		}
		
		grData.put("grItems", grItems);
		return grData;
	}
	
	/**
	 * 납품예정 아이디로 검수등록(입고) 초기화
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findGrInitialDataByAsnUuid(Map<String, Object> param) {
		Map<String, Object> grData = asnService.findGrByAsnUuid(param);
		Map<String, Object> poEvalSetData = asnService.findPoEvalSetInfo(param);
		if(poEvalSetData != null) {
			grData.put("ge_subj_yn", "Y");
			grData.put("po_ge_uuid", poEvalSetData.get("po_ge_uuid"));
		} else {
			grData.put("ge_subj_yn", "N");
		}
		
		List<Map<String, Object>> grItems = asnItemService.searchGrItemByAsnUuid(param);
		
		int maxNo = 10;
		for(Map<String, Object> grItem : grItems) {
			grItem.put("gr_lno", maxNo);
			maxNo += 10;
		}
		grData.put("grItems", grItems);
		return grData;
	}
	
	/**
	 * 검수 저장
	 *
	 * @param param {"grData", "insertItems", "updateItems", "removeItems"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveGr
	 */
	public String saveGr(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		List<Map<String, Object>> insertItems = (List<Map<String, Object>>) param.get("insertItems");
		List<Map<String, Object>> updateItems = (List<Map<String, Object>>) param.get("updateItems");
		List<Map<String, Object>> removeItems = (List<Map<String, Object>>) param.get("removeItems");
		
		String grUuid = (String) grData.get("gr_uuid");            // 입고 아이디
		String grNo = (String) grData.get("gr_no");            // 입고 문서번호
		Integer grRevno = grData.get("gr_ordn") == null ? 1 : Integer.parseInt(grData.get("gr_ordn").toString()); // 입고 회차
		String oorgCd = (String) grData.get("oorg_cd");    // 운영조직코드
		String purcTypCcd = (String) grData.get("purc_typ_ccd");        // 구매유형
		String curCcd = (String) grData.get("cur_ccd");            // 통화
		String vdCd = (String) grData.get("vd_cd");            // 협력사코드
		
		DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
		String grCrnDt = format.format(new Date());
		
		if(Strings.isNullOrEmpty(grUuid)) { // 신규
			grUuid = UUID.randomUUID().toString();
			grData.put("gr_uuid", grUuid);
			grData.put("gr_crn_dt", grCrnDt); // 입고 생성일
			
			// 문서번호 생성
			if(Strings.isNullOrEmpty(grNo)) {
				grNo = sharedService.generateDocumentNumber("GR");
			}
			grData.put("gr_no", grNo);
			
			if("QTY".equals(purcTypCcd)) {    // 물품인 경우는 차수가 의미 없음
				grRevno = 1;
			} else {                    // 공사/용역의 경우 PO헤더단위로 기성이 진행되므로 기성 차수가 증가 (최종 승인 차수 +1)
				grRevno = grRepository.findGrIncrementRevisionByPoNo(grData);
			}
			grData.put("gr_ordn", grRevno);    // 차수가 아닌 회차의 의미

			//담당자가 없는 경우 입고작성담당자를 현재 작성자로 작성 (RFX작성건 or touchless)
			if(grData.containsKey("gr_pic_id") || grData.get("gr_pic_id") == null || "".equals(grData.get("gr_pic_id"))){
				grData.put("gr_pic_id", Auth.getCurrentUserName());
			}
			this.insertGr(grData);
			
			if(grData.get("asn_uuid") != null) {
				asnService.updateGrPicByAsnUuid(grData);
			}
		} else {
			this.updateGrByGrUuid(grData);
		}
		
		if(insertItems != null && !insertItems.isEmpty()) {
			for(Map<String, Object> row : insertItems) {
				row.put("gr_item_uuid", UUID.randomUUID().toString());
				row.put("gr_uuid", grUuid);
				row.put("gr_no", grNo);
				row.put("gr_ordn", grRevno);
				row.put("oorg_cd", oorgCd);
				row.put("purc_typ_ccd", purcTypCcd);
				row.put("cur_ccd", curCcd);
				row.put("vd_cd", vdCd);
				row.put("gr_crn_dt", grCrnDt); // 입고 생성일
				row.put("gr_dt", grData.get("gr_dt"));
				this.insertGrItem(row);
			}
		}
		if(updateItems != null && !updateItems.isEmpty()) {
			for(Map<String, Object> row : updateItems) {
				this.updateGrItem(row);
			}
		}
		if(removeItems != null && !removeItems.isEmpty()) {
			for(Map<String, Object> row : removeItems) {
				this.deleteGrItemByGrItem(row);
			}
		}
		return grUuid;
	}
	
	/**
	 * 임시저장 한다.
	 *
	 * @param param {"grData", "insertItems", "updateItems", "removeItems"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveDraftGr
	 */
	public ResultMap saveDraftGr(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		List<Map<String, Object>> checkItems = (List<Map<String, Object>>) param.get("checkItems");    //모든 품목
		
		List<String> poItemIds = Lists.newArrayList();
		for(Map<String, Object> row : checkItems) {
			poItemIds.add(row.get("po_item_uuid").toString());
		}
		
		Map<String, Object> checkParam = Maps.newHashMap(grData);
		checkParam.put("po_item_uuids", poItemIds);
		checkParam.put("checkItems", checkItems);
		
		// validate
		ResultMap resultMap = this.validate(checkParam);
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = this.saveGr(param); // 데이터 저장
			
			this.deleteGrEvalByGr(grData);
			this.copyGeEvalSetByPoEvalProcess(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.saveDraftGr(keyParam); // 진행상태 수정
			
			Map<String, Object> tempResultData = Maps.newHashMap();
			tempResultData.put("grUuid", grUuid);
			resultMap.setResultData(tempResultData);
		}
		return resultMap;
	}
	
	/**
	 * 입고평가 설정 후 대상 여부 '아니오' 인 경우 입고평가 설정 정보 삭제
	 * @param grData
	 */
	private void deleteGrEvalByGr(Map<String, Object> grData) {
		if(grData == null) {
			return;
		}
		if("Y".equals(grData.get("ge_subj_yn"))) {
			return;
		}
		if(Strings.isNullOrEmpty((String) grData.get("ge_uuid"))) {
			return;
		}
		grEvalService.deleteGrEval(grData);
	}
	
	/**
	 * 발주 평가 설정 -> 입고평가 설정 복사
	 * @param grData
	 * @param grUuid
	 * @return
	 */
	protected void copyGeEvalSetByPoEvalProcess(Map<String, Object> grData, String grUuid) {
		if(grData == null) {
			return;
		}
		if(grUuid == null) {
			return;
		}
		// 입고평가 대상여부 '아니오' 인 경우 스킵
		if("N".equals(grData.get("ge_subj_yn"))) {
			return;
		}
		// 발주, 납품예정에서 넘어오지 않은 경우 복사하지 않고 스킵
		if(Strings.isNullOrEmpty((String) grData.get("po_ge_uuid"))) {
			return;
		}
		
		// 발주에서 설정한 평가정보 존재하는 경우 입고/기성 평가 설정 정보로 복사한다.
		this.copyGeEvalSetByPoEval((String) grData.get("po_ge_uuid"), grUuid);
	}
	
	protected void copyGeEvalSetByPoEval(String poGeUuid, String grUuid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ge_uuid", poGeUuid);
		
		Map<String, Object> geInfoResult = grEvalService.findGeInfo(param);
		
		Map<String, Object> ge = (Map<String, Object>) geInfoResult.get("gegInfo");
		List<Map<String, Object>> gePrcses = grEvalService.findListGePrcsByGe(param);
		List<Map<String, Object>> gePrcsEvaltrs = (List<Map<String, Object>>) geInfoResult.get("gePrcsEvaltrs");
		
		String geUuid = UUID.randomUUID().toString();
		ge.put("ge_uuid", geUuid);
		
		//입고평가 프로세스 초기화
		for(Map<String, Object> gePrcs : gePrcses) {
			String gePrcsUuid = UUID.randomUUID().toString();
			gePrcs.put("ge_uuid", geUuid);
			
			for(Map<String, Object> gePrcsEvaltr : gePrcsEvaltrs) {
				if(gePrcs.get("ge_prcs_uuid").equals(gePrcsEvaltr.get("ge_prcs_uuid"))) {
					gePrcsEvaltr.put("ge_prcs_evaltr_uuid", UUID.randomUUID().toString());
					gePrcsEvaltr.put("ge_prcs_uuid", gePrcsUuid);
					
					// 담당자 프로세스 인 경우 입고 진행 담당자 아이디로 평가자를 변경
					if("PURC_PIC".equals(gePrcs.get("evaltr_typ_ccd"))) {
						gePrcsEvaltr.put("evaltr_id", Auth.getCurrentUser().getUsername());
					}
				}
			}
			gePrcs.put("ge_prcs_uuid", gePrcsUuid);
		}
		
		grEvalService.copyGeEvalSetByPoEval(grUuid, ge, gePrcses, gePrcsEvaltrs);
	}
	
	/**
	 * 검수완료 한다.
	 *
	 * @param param {"grData", "insertItems", "updateItems", "removeItems"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveSubmitGr
	 */
	public ResultMap saveSubmitGr(Map<String, Object> param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		List<Map<String, Object>> checkItems = (List<Map<String, Object>>) param.get("checkItems");    //모든 품목
		
		List<String> poItemIds = Lists.newArrayList();
		for(Map<String, Object> row : checkItems) {
			poItemIds.add(row.get("po_item_uuid").toString());
		}
		
		Map<String, Object> checkParam = Maps.newHashMap(grData);
		checkParam.put("po_item_uuids", poItemIds);
		checkParam.put("checkItems", checkItems);
		//외부에서 요청한 검수가 있는 경우 내부에서 요청한 검수가 우선으로 하는 flag
		checkParam.put("gr_chr_flag", grData.get("gr_chr_flag"));
		
		// validate
		ResultMap resultMap = this.validate(checkParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = this.saveGr(param); // 데이터 저장
			
			this.deleteGrEvalByGr(grData);
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.bypassApprovalGr(keyParam); // 진행상태 수정

			this.updatePoItemAsn(keyParam); // po item의 검수요청중인수량, 입고수량 및 발주완료여부 수정 / 발주의 발주완료여부 수정
			
			//grEvalService.closeEval(keyParam); // 검수완료 시 평가 마감 처리
			//grEventPublisher.closeEval(keyParam);
			
			
			Map<String, Object> tempResultData = Maps.newHashMap();
			tempResultData.put("grUuid", grUuid);
			resultMap.setResultData(tempResultData);
		}
		return resultMap;
	}
	
	/**
	 * po item의 검수요청수량, 입고수량 및 발주완료여부 수정 / 발주의 발주완료여부 수정
	 *
	 * @param param {"gr_uuid"}
	 * @return grUuid
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : updatePoItemAsn
	 */
	public void updatePoItemAsn(Map<String, Object> param) {
		List<Map<String, Object>> items = this.searchGrItemByGrUuid(param);
		
		List<String> poIds = Lists.newArrayList();
		List<String> poItemIds = Lists.newArrayList();
		List<String> asnItemUuids = Lists.newArrayList();
		for(Map<String, Object> row : items) {
			String poId = (String) row.get("po_uuid");
			String poItemId = (String) row.get("po_item_uuid");
			String asnItemUuid = (String) row.get("asn_item_uuid");
			
			// ar item의 합격수량 및 반품수량 수정을 위한 asn_item_uuid
			if(!Strings.isNullOrEmpty(asnItemUuid) && asnItemUuids.indexOf(asnItemUuid) < 0) {
				asnItemUuids.add(asnItemUuid);
			}
			
			// 발주품목의 발주완료여부 수정을 위한 po_item_uuid
			if(poItemIds.indexOf(poItemId) < 0) {
				poItemIds.add(poItemId);
			}
			
			// 발주의 발주완료여부 수정을 위한 po_uuid
			if(poIds.indexOf(poId) < 0) {
				poIds.add(poId);
			}
		}
		
		// 납품예정 건이 있는 경우
		if(!asnItemUuids.isEmpty()) {
			Map<String, Object> arItemParam = Maps.newHashMap();
			arItemParam.put("asn_item_uuids", asnItemUuids);
			
			// 납품예정 품목의 합격수량 및 반품수량 수정
			// merge to select and update
			List<Map<String, Object>> asnItems = grItemService.searchAsnItemPassReturnQuantity(arItemParam);
			if(!asnItems.isEmpty()) {
				for(Map<String, Object> arItem : asnItems) {
					asnItemService.updateAsnItemPassReturnQuantity(arItem);
				}
				// 발주품목의 납품예정 수량 업데이트(수정)
				List<Map<String, Object>> asnItemQtyList = asnItemService.searchAsnItemQty(arItemParam);
				if(!asnItemQtyList.isEmpty()) {
					for(Map<String, Object> arItem : asnItemQtyList) {
						poItemService.updatePoItemAsnQuantity(arItem);
					}
				}
			}
		}
		
		Map<String, Object> poItemParam = Maps.newHashMap();
		poItemParam.put("po_item_uuids", poItemIds);
		
		// 발주품목의 검수요청중인수량/입고수량 수정
		this.updatePoItemGr(poItemIds);

		// 발주품목 발주완료여부 수정(발주수량과 입고수량 비교)한다.
		poItemService.updatePoItemCompleteByQty(poItemParam);
		
		Map<String, Object> poParam = Maps.newHashMap();
		poParam.put("po_uuids", poIds);
		
		// 발주의 발주완료여부 수정
		poService.updatePoComplete(poParam);
	}

	/**
	 * 발주품목의 검수요청중인수량/입고수량 수정
	 *
	 * @param poItemIds
	 */
	private void updatePoItemGr(List<String> poItemIds) {
		Map<String, Object> poItemParam = Maps.newHashMap();
		poItemParam.put("po_item_uuids", poItemIds);
		poItemService.updatePoItemGrQuantity(poItemParam);
	}

	/**
	 * 검수등록 삭제
	 *
	 * @param param
	 * @return
	 */
	public ResultMap deleteGr(Map<String, Object> param) {
		// validate
		ResultMap resultMap = this.validate(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}
		
		this.deleteGrProcess(param);
		return resultMap;
	}
	
	protected void deleteGrProcess(Map<String, Object> param) {
		this.deleteGrByGrUuid(param);
		this.deleteGrItemByGrUuid(param);
		proStatusService.deleteGr(param);
	}
	
	/**
	 * 검수 취소
	 *
	 * @param param
	 * @return
	 */
	public ResultMap cancelGr(Map<String, Object> param) {
		// validate
		ResultMap resultMap = this.validate(param);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			proStatusService.cancelGr(param);

			this.updatePoItemAsn(param);     // po item의 검수요청중인수량, 입고수량 및 발주완료여부 수정 / 발주의 발주완료여부 수정
		}
		return resultMap;
	}
	
	
	/**
	 * 검수 정합성 체크로직
	 *
	 * @param param
	 * @return
	 */
	public ResultMap validate(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		List<Map<String, Object>> invalidPoItems = Lists.newArrayList();
		List<String> poItemUuids = (List<String>) (param.get("po_item_uuids") == null ? Lists.newArrayList() : param.get("po_item_uuids"));
		
		if(!poItemUuids.isEmpty()) {
			// PO 품목의 검수등록 가능여부 체크
			if(null != param.get("gr_chr_flag")) {//외부에서 요청한 검수가 있는 경우 내부에서 요청한 검수를 우선으로 한다
				resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
			} else {
				// 유효하지 않은 PO 품목 리스트
				invalidPoItems = grRepository.checkGrCreatable(param);
				resultMap = grValidator.validatePoItems(invalidPoItems);
				
				if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus()) && param.get("checkItems") != null) {
					// PO 품목의 검수등록 수량 체크
					List<Map<String, Object>> checkQuantityList = poItemService.checkQuantityList(param);
					resultMap = grValidator.checkPoItemsGrQuantity(param, checkQuantityList);
				}
				
				if(!ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
					return resultMap;
				}
			}
		}
		
		String grUuid = param.get("gr_uuid") == null ? null : param.get("gr_uuid").toString();
		
		if(Strings.isNullOrEmpty(grUuid)) {
			// 신규 생성 시
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
			return resultMap;
		}
		
		Map<String, Object> checkResult = grRepository.compareGrHeaderStatus(param);
		invalidPoItems = grItemService.checkGrCancelable(param);
		return grValidator.validateGr(param, checkResult, invalidPoItems);
	}
	
	public ResultMap submitGrEvalByGr(Map param) {
		Map<String, Object> grData = (Map<String, Object>) param.get("grData");
		List<Map<String, Object>> checkItems = (List<Map<String, Object>>) param.get("checkItems");    //모든 품목
		
		List<String> poItemIds = Lists.newArrayList();
		for(Map<String, Object> row : checkItems) {
			poItemIds.add(row.get("po_item_uuid").toString());
		}
		
		Map<String, Object> checkParam = Maps.newHashMap(grData);
		checkParam.put("po_item_uuids", poItemIds);
		checkParam.put("checkItems", checkItems);
		//외부에서 요청한 검수가 있는 경우 내부에서 요청한 검수가 우선으로 하는 flag
		checkParam.put("gr_chr_flag", grData.get("gr_chr_flag"));
		
		// validate
		ResultMap resultMap = this.validate(checkParam);
		
		if(ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			String grUuid = this.saveGr(param); // 데이터 저장
			
			this.copyGeEvalSetByPoEvalProcess(grData, grUuid);
			
			Map<String, Object> keyParam = Maps.newHashMap();
			keyParam.put("gr_uuid", grUuid);
			proStatusService.saveDraftGr(keyParam); // 진행상태 수정
			
			ResultMap submitResultMap = grEvalService.submitGrEvalByGr(grUuid);
			if(submitResultMap.isSuccess()) {
				proStatusService.submitGrEval(keyParam);
				
				Map<String, Object> tempResultData = Maps.newHashMap();
				tempResultData.put("grUuid", grUuid);
				return ResultMap.builder()
				                .resultData(tempResultData)
				                .build();
			} else if(submitResultMap.isFail()){
				return submitResultMap;
			}
		}
		return resultMap;
	}
	
	public ResultMap evalForceClose(Map param) {
		List<Map<String, Object>> checkedItems = (List<Map<String, Object>>) param.get("checkedItems");
		for(Map<String, Object> checkedItem : checkedItems) {
			grEvalService.evalForceClose(checkedItem);
		}
		
		return ResultMap.SUCCESS();
	}
}
