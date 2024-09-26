package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxBidMailWorkRepository;
import smartsuite.app.common.mail.mailWorkExcel.MailWorkReSendService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.service.SharedService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class RfxBidMailWorkService {
	
	static final Logger LOG = LoggerFactory.getLogger(RfxBidMailWorkService.class);
	
	@Inject
	RfxBidMailWorkRepository rfxBidMailWorkRepository;
	
	@Inject
	RfxBidService rfxBidService;
	
	@Autowired
	private MailWorkReSendService mailWorkReSendService;
	
	@Inject
	SharedService sharedService;
	
	@Inject
	FormatterProvider formatterProvider;
	
	@Inject
	MessageSource messageSource;
	
	private String makeFailMessage(String failMessage, String code, Object arg, String defaultMessage) {
		String result = failMessage;
		if(!StringUtils.isEmpty(result)) {
			result += ":";
		}
		if(arg == null) {
			result += messageSource.getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
		} else {
			Object[] args = new Object[]{arg};
			result += messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
		}
		return result;
	}
	
	public void excelParsingService(Map dataResultMap) throws Exception {
		Map resultDataMap = Maps.newHashMap();
		String failMessage = "";
		
		/************************************************************
		 * 견적 헤더
		 ************************************************************/
		Map rfxBidData = (Map) dataResultMap.get("rfxBidData");
		if(rfxBidData == null) {
			String msg = messageSource.getMessage("STD.MW0014", null, "RFX 정보가 누락되어 발송 되었습니다. 계속적으로 이슈가 발생 하였을 경우 담당자에게 문의해주세요.", LocaleContextHolder.getLocale());
			dataResultMap.put("resnd_rsn", msg);
			mailWorkReSendService.reSendEmailWork(dataResultMap);
			return;
		}
		
		// 협력사 코드
		Map emailInfo = (Map) dataResultMap.get("sendEmailInfo");
		rfxBidData.put("rfx_uuid", emailInfo.get("task_uuid"));
		rfxBidData.put("vd_cd", emailInfo.get("eml_task_dtl_uuid"));
		
		// RFX 투찰 유효 일자
		String qtaEffDateStr = (String) rfxBidData.get("rfx_bid_efct_dt");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		SimpleDateFormat getFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		try {
			// 견적유효일자가 안들어온 경우
			if(StringUtils.isEmpty(qtaEffDateStr)) {
				failMessage = this.makeFailMessage(failMessage, "STD.MW0001", null, "견적유효일자를 입력하지 않았습니다. 정상적으로 다시 기입해주세요.");
			} else {
				getFormat.setLenient(false);
				Date qtaEffDate = getFormat.parse(qtaEffDateStr);
				
				String resultQtaEffDate = format.format(qtaEffDate);
				
				rfxBidData.put("rfx_bid_efct_dt", resultQtaEffDate);
			}
		} catch(ParseException parseE) {
			LOG.info("qtaEffDate validation ERROR ");
			parseE.printStackTrace();
			failMessage = this.makeFailMessage(failMessage, "STD.MW0002", qtaEffDateStr, "[{0}]은 정상적인 날짜가 아닙니다. 정상적으로 다시 기입해주세요.");
		} catch(Exception e) {
			LOG.info("qtaEffDate validation ERROR ");
			e.printStackTrace();
			failMessage = this.makeFailMessage(failMessage, "STD.MW0002", qtaEffDateStr, "[{0}]은 정상적인 날짜가 아닙니다. 정상적으로 다시 기입해주세요.");
		}
		
		// 통화
		String cur_ccd = rfxBidData.get("cur_ccd") == null ? "" : rfxBidData.get("cur_ccd").toString();
		if(!StringUtils.isEmpty(cur_ccd)) {
			boolean validCur = false;
			List<Map> curCodes = sharedService.findCommonCode("C004");
			for(Map curCode : curCodes) {
				String curCd = (String) curCode.get("data");
				if(curCd.equals(cur_ccd)) {
					validCur = true;
					break;
				}
			}
			if(!validCur) {
				failMessage = this.makeFailMessage(failMessage, "STD.MW0003", cur_ccd, "[{0}]은 유효한 통화가 아닙니다. 입력하신 통화를 확인하세요.");
			}
		} else {
			failMessage = this.makeFailMessage(failMessage, "STD.MW0004", null, "투찰 통화를 입력하지 않았습니다. 투찰 통화를 확인하세요.");
		}
		
		/************************************************************
		 * 견적 품목
		 ************************************************************/
		List<Map> rfxBidItems = Lists.newArrayList();
		if(dataResultMap.get("list") != null) {
			Map listInfo = (Map) dataResultMap.get("list");
			if(listInfo.get("rfxBidItems") != null) {
				// 총 RFX 투찰 금액
				BigDecimal totQtaAmt = BigDecimal.ZERO;
				
				for(Map item : (List<Map>) listInfo.get("rfxBidItems")) {
					
					// RFX 품목 제출 단가
					String itemPriceStr = (String) item.get("rfx_item_subm_uprc");
					
					if(StringUtils.isEmpty(itemPriceStr)) {
						failMessage = this.makeFailMessage(failMessage, "STD.MW0006", null, "투찰 단가가 입력되지 않았습니다. 정상적으로 입력해주세요.");
					} else if(!StringUtils.isNumeric(itemPriceStr)) {
						failMessage = this.makeFailMessage(failMessage, "STD.MW0011", itemPriceStr, "[{0}]은 정상적인 입력값이 아닙니다. 투찰 단가는 숫자로만 입력해야합니다. 정상적으로 입력해주세요.");
					} else if(itemPriceStr.length() > 20) {
						//failMessage = this.makeFailMessage(failMessage, "STD.MW0012", null, "투찰 단가의 입력값이 너무 큽니다. 확인해주세요.");
						//TODO 다국어 등록 완료 사용 여부 확인 ( 2024-02-29 최가람 )
					} else {
						// 견적통화에 따른 단가 포맷 적용
						BigDecimal itemPrice = formatterProvider.getPrecFormat("amt", new BigDecimal(itemPriceStr), cur_ccd);
						item.put("rfx_item_subm_uprc", itemPrice);
						
						// RFX 수량
						String itemQty = (String) item.get("rfx_qty");
						
						// RFX 품목 제출 금액 (RFX 품목 제출 단가 * RFX 수량)
						BigDecimal itemAmt = formatterProvider.getPrecFormat("amt", itemPrice.multiply(new BigDecimal(itemQty)), cur_ccd);
						item.put("rfx_item_subm_amt", itemAmt);
						
						// RFX 품목 제출 금액 합산
						totQtaAmt = totQtaAmt.add(itemAmt);
					}
					// 견적 헤더에 총 RFX 투찰 금액 적용
					rfxBidData.put("rfx_bid_amt", totQtaAmt);
					
					// 납품 리드타임 (일반구매일 경우만 해당, 단가계약의 경우 양식에 없음)
					if(item.containsKey("dlvy_ldtm")) {
						String ldDay = (String) item.get("dlvy_ldtm");
						
						if(StringUtils.isEmpty(ldDay)) {
							failMessage = this.makeFailMessage(failMessage, "STD.MW0008", null, "납품 리드타임이 입력되지 않았습니다. 정상적으로 입력해주세요.");
						} else if(!StringUtils.isNumeric(ldDay)) {
							failMessage = this.makeFailMessage(failMessage, "STD.MW0009", ldDay, "[{0}]은 정상적인 값이 아닙니다. 납품 리드타임을 숫자로만 입력해야합니다. 정상적으로 입력해주세요.");
						} else if(ldDay.length() > 20) {
							//failMessage = this.makeFailMessage(failMessage, "STD.MW0013", null, "납품 리드타임의 입력값이 너무 큽니다. 확인해주세요.");
							//TODO 다국어 등록 완료, 사용여부 확인해야함 2024-02-29
						}
					}
					rfxBidItems.add(item);
				}
			}
		}
		
		if(StringUtils.isNotEmpty(failMessage)) {
			dataResultMap.put("resnd_rsn", failMessage);
			mailWorkReSendService.reSendEmailWork(dataResultMap);
			return;
		}
		
		resultDataMap.put("rfxBidData", rfxBidData);
		resultDataMap.put("rfxBidItems", rfxBidItems);
		this.mailWorkSubmitRfxQta(resultDataMap);
	}
	
	/**
	 * RFX QTA 제출
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void mailWorkSubmitRfxQta(Map param) throws Exception {
		Map rfxBidExcelData = (Map) param.get("rfxBidData");
		String vdCd = (String) rfxBidExcelData.get("vd_cd");
		
		//협력사 코드 체크
		if(StringUtils.isEmpty(vdCd)) {
			return;
		}
		
		Map rfxBidData = rfxBidMailWorkRepository.findRfxBidByMailWork(rfxBidExcelData);
		List<Map> rfxBidItems = rfxBidMailWorkRepository.findRfxBidItemsByMailWork(rfxBidExcelData);
		
		if(rfxBidData == null) {
			return;
		}
		Map rfxBidItemsMap = Maps.newHashMap();
		for(Map rfxBidItem : rfxBidItems) {
			rfxBidItemsMap.put(rfxBidItem.get("rfx_lno"), rfxBidItem);
		}
		
		rfxBidData.put("rfx_bid_efct_dt", rfxBidExcelData.get("rfx_bid_efct_dt"));
		rfxBidData.put("cur_ccd", rfxBidExcelData.get("cur_ccd"));
		rfxBidData.put("rfx_bid_amt", rfxBidExcelData.get("rfx_bid_amt"));
		
		List<Map> rfxBidExcelItems = (List<Map>) param.get("rfxBidItems");
		for(Map rfxBidExcelItem : rfxBidExcelItems) {
			Map rfxBidItem = (Map) rfxBidItemsMap.get(rfxBidExcelItem.get("rfx_lno"));
			
			rfxBidExcelItem.put("rfx_bid_uuid", rfxBidData.get("rfx_bid_uuid"));
			rfxBidExcelItem.put("rfx_bid_no", rfxBidData.get("rfx_bid_no"));
			rfxBidExcelItem.put("rfx_bid_revno", rfxBidData.get("rfx_bid_revno"));
			rfxBidExcelItem.put("rfx_no", rfxBidData.get("rfx_no"));
			rfxBidExcelItem.put("rfx_rnd", rfxBidData.get("rfx_rnd"));
			rfxBidExcelItem.put("rfx_bid_efct_yn", "Y");
			rfxBidExcelItem.put("rfx_item_uuid", rfxBidItem.get("rfx_item_uuid"));
			
			rfxBidExcelItem.put("item_cd", rfxBidItem.get("item_cd"));
			rfxBidExcelItem.put("item_nm", rfxBidItem.get("item_nm"));
			rfxBidExcelItem.put("item_nm_en", rfxBidItem.get("item_nm_en"));
			rfxBidExcelItem.put("item_spec", rfxBidItem.get("item_spec"));
			rfxBidExcelItem.put("item_spec_dtl", rfxBidItem.get("item_spec_dtl"));
			rfxBidExcelItem.put("item_cd_crn_typ_ccd", rfxBidItem.get("item_cd_crn_typ_ccd"));
		}
		
		Map saveParam = Maps.newHashMap();
		saveParam.put("rfxBidData", rfxBidData);
		saveParam.put("rfxBidItems", rfxBidExcelItems);
		rfxBidService.submitRfxBid(saveParam);
	}
}
