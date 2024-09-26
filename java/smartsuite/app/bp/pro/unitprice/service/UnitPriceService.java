package smartsuite.app.bp.pro.unitprice.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.organizationManager.service.OrganizationManagerService;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.contract.contractcnd.service.PurcContractCndService;
import smartsuite.app.bp.pro.unitprice.event.UnitPriceEventPublisher;
import smartsuite.app.bp.pro.unitprice.repository.UnitPriceRepository;
import smartsuite.exception.CommonException;
import smartsuite.exception.ErrorCode;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class UnitPriceService {
	
	private static final String DATE_FORMAT = "yyyyMMdd";
	
	private final DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
	
	@Inject
	ContractService contractService;

	@Inject
	PurcContractCndService purcContractCndService;
	
	@Inject
	UnitPriceRepository unitPriceRepository;
	
	@Inject
	OrganizationManagerService organizationManagerService;

	@Inject
	UnitPriceEventPublisher unitPriceEventPublisher;
	
	/**
	 * 날짜 비교
	 *
	 * @param from the from
	 * @param to the to
	 * @param d the d
	 * @return the int : -2: d < from, -1: d == from, 0: from < d < to, 1: to == d, 2: to < d
	 */
	private int betweenDate(Date from, Date to, Date d) {
		if (d.before(from)) {
			return -2;
		}
		if (d.equals(from)) {
			return -1;
		}
		if (d.equals(to)) {
			return 1;
		}
		if (d.after(to)) {
			return 2;
		}
		return 0;
	}
	
	public void createUnitPrice(Map<String, Object> param) {
		if(param == null) {
			return;
		}
		
		Map cntrInfo = contractService.findContract(param);

		Map cndParam = Maps.newHashMap();
		cndParam.put("purc_cntr_uuid", cntrInfo.get("cntr_cnd_uuid"));
		
		// 구매 계약 정보
		Map purcCntr = purcContractCndService.find(cndParam);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map<String,Object>> purcCntrItemList = (List<Map<String,Object>>) purcCntr.get("purcCntrItemList");


		int upcrReqItemCnt = 0;
 			for(Map<String,Object> insertItem: purcCntrItemList){
				if (insertItem.get("upcr_item_uuid") != null && !"".equals(insertItem.get("upcr_item_uuid"))){
					upcrReqItemCnt++;
				}
			}

			if(upcrReqItemCnt > 0) {
				//prItem의 운영조직코드를 조회
				// 특정 키 "keyName"에 해당하는 값을 추출하여 List<String>로 변환
				List<String> upcrItemUuids = purcCntrItemList.stream()
						.map(map -> (String) map.get("upcr_item_uuid"))
						.collect(Collectors.toList());
				Map<String, Object> uprcParam = Maps.newHashMap();
				uprcParam.put("upcr_item_uuids", upcrItemUuids);
				List<Map<String, Object>> itemOorgCds = unitPriceEventPublisher.findListUpcrItemByUpcrItemUuids(uprcParam);


				// 그룹화할 맵 생성
				Map<Object, List<Map<String, Object>>> groupedMap = Maps.newHashMap();
				// prItemOorgCds 리스트를 맵으로 변환하여 효율적인 검색을 가능하게 함
				Map<String, Map<String, Object>> prItemMap = Maps.newHashMap();
				for (Map<String, Object> upcrItem : itemOorgCds) {
					prItemMap.put((String) upcrItem.get("upcr_item_uuid"), upcrItem);
				}

				// items 순회하면서 처리
				for (Map<String, Object> insertItem : purcCntrItemList) {
					String upcrItemUuid = (String) insertItem.get("upcr_item_uuid");
					// prItemMap에서 해당 UUID에 해당하는 항목을 찾아 oorg_cd 값을 설정
					Map<String, Object> upcrItem = prItemMap.get(upcrItemUuid);
					if (upcrItem != null) {
						insertItem.put("oorg_cd", upcrItem.get("oorg_cd"));
					}

					// oorg_cd 값을 기준으로 그룹화
					Object key = insertItem.get("oorg_cd");
					groupedMap.computeIfAbsent(key, k -> Lists.newArrayList()).add(insertItem);
				}


				// 결과 리스트로 변환
				// 1: items , 2:items

				// 결과 출력
				int index = 1;
				//화면과 동일하게 공급금액을 기준으로 계산
				BigDecimal poTotAmt = (BigDecimal) purcCntrData.get("sup_amt");
				for (Map.Entry<Object, List<Map<String, Object>>> entry : groupedMap.entrySet()) {
					Object key = entry.getKey();
					List<Map<String, Object>> group = entry.getValue();

					List<Map<String, Object>> newInsertItems = group;
					for(Map<String, Object> item : newInsertItems) {
						item.put("oorg_cd", key);
						//구매운영 조직별로 item 분리
						this.createUnitPrice(cntrInfo, purcCntrData, purcCntrInfoData, item);
					}
				}
			}else{
				for(Map<String, Object> item : purcCntrItemList) {
					this.createUnitPrice(cntrInfo, purcCntrData, purcCntrInfoData, item);
				}
			}
	}
	
	private void createUnitPrice(Map cntrInfo, Map purcCntrData, Map purcCntrInfoData, Map item) {
		item.put("cntr_uuid", cntrInfo.get("cntr_uuid"));
		item.put("cntr_no", cntrInfo.get("cntr_no"));
		item.put("cntr_revno", cntrInfo.get("cntr_revno"));
		item.put("purc_cntr_uuid", purcCntrData.get("purc_cntr_uuid"));
		item.put("purc_cntr_info_uuid", purcCntrInfoData.get("purc_cntr_info_uuid"));
		item.put("purc_cntr_item_lno", item.get("item_lno"));
		item.put("vd_cd", cntrInfo.get("vd_cd"));
		//통합구매
		if (!item.containsKey("oorg_cd") || "".equals(item.get("oorg_cd"))) {
			item.put("oorg_cd", cntrInfo.get("oorg_cd"));
		}
		item.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
		item.put("purc_grp_cd", cntrInfo.get("purc_grp_cd"));
		item.put("uprccntr_dt", cntrInfo.get("cntr_dt"));
		item.put("pymtmeth_ccd", purcCntrInfoData.get("pymtmeth_ccd"));
		item.put("pymtmeth_expln", purcCntrInfoData.get("pymtmeth_expln"));
		item.put("dlvymeth_ccd", purcCntrInfoData.get("dlvymeth_ccd"));
		item.put("dlvymeth_expln", purcCntrInfoData.get("dlvymeth_expln"));
		item.put("uprccntr_qty", item.get("item_qty"));
		item.put("cur_ccd", purcCntrData.get("cur_ccd"));
		item.put("uprccntr_uprc", item.get("item_uprc"));
		
		
		String itemSd = (String) item.get("cntr_st_dt");
		String itemEd = (String) item.get("cntr_exp_dt");
		item.put("uprc_efct_st_dt", itemSd);
		item.put("uprc_efct_exp_dt", itemEd);
		
		Date itemSdate = null;
		Date itemEdate = null;
		try {
			itemSdate = format.parse(itemSd);
			itemEdate = format.parse(itemEd);
		} catch(ParseException e) {
			throw new CommonException(ErrorCode.FAIL, e);
		}
		
		Map infoParam = Maps.newHashMap();
		infoParam.put("oorg_cd", item.get("oorg_cd"));
		infoParam.put("item_cd", item.get("item_cd"));
		infoParam.put("vd_cd", cntrInfo.get("vd_cd"));
		infoParam.put("compare_apply_sd", itemSd);
		List<Map> infos = unitPriceRepository.findListUnitPrice(infoParam);
		
		List<Map> updates = Lists.newArrayList();
		List<Map> deletes = Lists.newArrayList();
		
		if(infos != null && !infos.isEmpty()) {
			for(Map info : infos) {
				String infoSd = (String) info.get("uprc_efct_st_dt");
				String infoEd = (String) info.get("uprc_efct_exp_dt");
				Date infoSdate = null;
				Date infoEdate = null;
				
				try {
					infoSdate = format.parse(infoSd);
					infoEdate = format.parse(infoEd);
				} catch(ParseException e) {
					throw new CommonException(ErrorCode.FAIL, e);
				}
				
				switch(betweenDate(infoSdate, infoEdate, itemSdate)) {
					case -2: // 시작일이 기간에 포함되지 않음
						if(betweenDate(infoSdate, infoEdate, itemEdate) >= -1) { // 종료일이 기간과 중첩됨.
							deletes.add(info);
						}
						break;
					case -1: // 시작일이 기간의 시작일과 같음
						deletes.add(info);
						break;
					case 0: // 시작일이 기간 내에 있음
					case 1: // 시작일이 기간의 종료일과 같음
						info.put("uprc_efct_exp_dt", format.format(DateUtils.addDays(itemSdate, -1)));
						updates.add(info);
						break;
					case 2:
					default:
						// no action.
						break;
				}
			}
		}
		
		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes) {
				unitPriceRepository.deleteUnitPrice(row);
			}
		}
		if(updates != null && !updates.isEmpty()) {
			for(Map<String, Object> row : updates) {
				row.put("uprccntr_st_dt", itemSdate);
				row.put("uprccntr_exp_dt", itemEdate);
				unitPriceRepository.updateUnitPriceTerms(row);
			}
		}
		unitPriceRepository.updateUnitPriceLastN(item);
		unitPriceRepository.insertUnitPrice(item);
	}
	
	public Map findListUnitPriceHistrec(Map param) {
		List<Map> histrecList = unitPriceRepository.findListUnitPriceHistrec(param);
		String coCd = (String) Auth.getCurrentUserInfo().get("co_cd");
		if(coCd != null) {
			Map companyInfo = organizationManagerService.findLogicOrganizationByCompanyCode(coCd);
			if(companyInfo.get("cur_ccd") != null) {
				param.put("bas_cur", companyInfo.get("cur_ccd"));
			}
		}
		if(param.get("bas_cur") == null) {
			param.put("bas_cur", "KRW");
		}
		Map histrecSummary = unitPriceRepository.findListUnitPriceHistrecSummary(param);
		histrecSummary.put("bas_cur", param.get("bas_cur"));
		
		BigDecimal poAmount = BigDecimal.ZERO;
		for(Map histrec : histrecList) {
			poAmount = poAmount.add((BigDecimal) histrec.get("po_tot_amt"));
		}
		poAmount = poAmount.divide(BigDecimal.valueOf(1000000));
		histrecSummary.put("po_amount", poAmount.doubleValue());
		
		Map result = Maps.newHashMap();
		result.put("histrecList", histrecList);
		result.put("histrecSummary", histrecSummary);
		return result;
	}
	
	public List<String> findListVendorRcmdItemUprccntr(Map param) {
		return unitPriceRepository.findListVendorRcmdItemUprccntr(param);
	}

	public List<String> findListVendorRcmdSgUprccntr(Map param) {
		return unitPriceRepository.findListVendorRcmdSgUprccntr(param);
	}

	public List<Map<String,Object>> findListBpaYnAndMutlVdYnByItemCd(Map param) {
		return unitPriceRepository.findListBpaYnAndMutlVdYnByItemCd(param);
	}
	
	public List<Map> findListUnitPriceByItemAndOorg(Map param) {
		return unitPriceRepository.findListUnitPriceByItemAndOorg(param);
	}
	
	public List<Map> findListUnitPriceQtaInfoByItemAndOorg(Map param) {
		return unitPriceRepository.findListUnitPriceQtaInfoByItemAndOorg(param);
	}

	public void resetSpotPrItem(Map param){
		if(param == null) {
			return;
		}

		Map cntrInfo = contractService.findContract(param);

		Map cndParam = Maps.newHashMap();
		cndParam.put("purc_cntr_uuid", cntrInfo.get("cntr_cnd_uuid"));

		// 구매 계약 정보
		Map purcCntr = purcContractCndService.find(cndParam);
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map<String,Object>> purcCntrItemList = (List<Map<String,Object>>) purcCntr.get("purcCntrItemList");

		unitPriceEventPublisher.resetSpotPrItem(purcCntr);
	}
}
