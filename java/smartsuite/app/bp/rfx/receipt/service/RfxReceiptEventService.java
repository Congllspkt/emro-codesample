package smartsuite.app.bp.rfx.receipt.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.receipt.repository.RfxReceiptRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RfxReceiptEventService {

    @Inject
    RfxReceiptRepository rfxReceiptRepository;

    @Inject
    RfxReceiptService rfxReceiptService;

    public ResultMap resetSpotPrItem(Map purcCntr) {
		//Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		//Map purcCntrInfoData = (Map) purcCntr.get("purcCntrInfoData");
		List<Map<String,Object>> purcCntrItemList = (List<Map<String,Object>>) purcCntr.get("purcCntrItemList");

        List<Map> itemList = rfxReceiptRepository.findListRfxRcptItemSpotPrItemByItem(purcCntr);

		List<Map> reqList = Lists.newArrayList();
		for(Map item : itemList) {
			BigDecimal uprc = BigDecimal.ZERO;
			for(Map purcCntrItem: purcCntrItemList){
				if(item.get("item_oorg_cd").equals(purcCntrItem.get("item_oorg_cd")) &&
                        item.get("item_cd").equals(purcCntrItem.get("item_cd"))){
					uprc = (BigDecimal) purcCntrItem.get("item_uprc");
					break;
				}
			}
			//요청단가, 합계 재계산
			Map reqItem = Maps.newHashMap(item);
			BigDecimal reqQty = (BigDecimal) reqItem.get("req_qty");
			BigDecimal amt = uprc.multiply(reqQty);
			reqItem.put("req_amt", amt);
			reqItem.put("req_uprc", uprc);
			reqItem.put("req_item_uuid", reqItem.get("pr_item_uuid"));
			reqItem.put("req_uuid", reqItem.get("pr_uuid"));
			reqItem.put("req_no", reqItem.get("pr_no"));
			reqItem.put("req_revno", reqItem.get("pr_revno"));
			reqItem.put("req_lno", reqItem.get("pr_lno"));
			reqList.add(reqItem);
            //기존 rfx요청접수는 삭제처리
            rfxReceiptRepository.deleteRequestReqInfoByReqUuid(item);
		}

        if(reqList.size() > 0){
            //새로운 접수로 처리
            rfxReceiptService.receiptReqRfx(reqList);
        }
		 return ResultMap.SUCCESS();
    }
}
