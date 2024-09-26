package smartsuite.app.sp.pro.asn.validator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.common.shared.ResultMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class SpAsnValidator {
    /**
     * 납품예정 헤더 검증결과 확인
     *
     * @param targetAsnData
     * @param comparedResult
     * @return
     */
    public ResultMap checkValidationResultOfAsnData(Map<String, Object> targetAsnData, Map<String, Object> comparedResult) {
        return ValidatorUtil.getResultMapByCheckResult(targetAsnData, comparedResult);
    }

    /**
     * 발주품목의 수량이 납품예정 가능한 상태인지 확인
     *
     * @param validationData
     * @param quantityOfPoItems
     * @return
     */
    public ResultMap checkPossibleAsnQuantityOfPoItems(Map<String, Object> validationData, List<Map<String, Object>> quantityOfPoItems) {
        ResultMap resultMap = ResultMap.getInstance();
        return validationData.get("checkItems") != null
                ? this.compareQuantityOfItems(validationData, quantityOfPoItems)
                : resultMap;
    }

    /**
     * po item을 가지고 있는지
     *
     * @param param
     * @return
     */
    public Boolean hasPoItem(Map<String, Object> param) {
        List<String> poItemIds = (List<String>) (param.get("po_item_uuids") == null ? Lists.newArrayList() : param.get("po_item_uuids"));
        return !poItemIds.isEmpty();
    }

    /**
     * 검수요청이 존재하는지
     *
     * @param targetAsnData
     * @return
     */
    public Boolean existsAdvancedShippingNotice(Map<String, Object> targetAsnData) {
        String asnUuid = targetAsnData.get("asn_uuid") == null ? null : targetAsnData.get("asn_uuid").toString();
        return !Strings.isNullOrEmpty(asnUuid);
    }

    /**
     * 검수요청에 무효화된 po item들의 결과
     *
     * @param invalidPoItems
     * @return
     */
    public ResultMap getResultMapOfInvalidPoItems(List<Map<String, Object>> invalidPoItems) {
        ResultMap resultMap = ResultMap.getInstance();
        if (invalidPoItems.isEmpty()) {
            resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
        } else {
            resultMap.setResultStatus(ProConst.PO_STATE_ERR);
            resultMap.setResultList(invalidPoItems);
        }
        return resultMap;
    }

    /**
     * 검수 요청 수량 검사 (발주잔량보다 검수요청수량이 많은 경우 납품예정 불가)
     * po item
     *
     * @param validationData
     * @return
     */
    private ResultMap compareQuantityOfItems(Map<String, Object> validationData, List<Map<String, Object>> quantityOfPoItems) {
        List<Map<String, Object>> checkItems = (List<Map<String, Object>>) validationData.get("checkItems");    //확인할 품목
        List<Map<String, Object>> invalidQuantityItems = returnInvalidQuantityOfPoItem(quantityOfPoItems, checkItems);    //무효한 품목
        return getResultMapOfInvalidQuantityItems(invalidQuantityItems);
    }

    /**
     * 발주품목의 수량을 비교하여 무효한 수량을 가진 품목을 반환한다.
     *
     * @param quantityOfPoItems
     * @param checkItems
     * @return
     */
    private List<Map<String, Object>> returnInvalidQuantityOfPoItem(List<Map<String, Object>> quantityOfPoItems, List<Map<String, Object>> checkItems) {
        List<Map<String, Object>> invalidQtyList = Lists.newArrayList();
        Map<String, Object> poItems = Maps.newHashMap();
        //PO ITEM ID에 값을 넣는다.
        for (Map<String, Object> item : checkItems) {
            poItems.put(item.get("po_item_uuid").toString(), item);
        }

        for (Map<String, Object> checkQtaData : quantityOfPoItems) {
            Map<String, Object> item = (Map<String, Object>) poItems.get(checkQtaData.get("po_item_uuid"));

            //검수요청수량
            BigDecimal arQty = (item.get("asn_qty") == null) ? BigDecimal.ZERO : new BigDecimal(item.get("asn_qty").toString());
            //발주수량
            BigDecimal poItemQty = (checkQtaData.get("po_qty") == null) ? BigDecimal.ZERO : (BigDecimal) checkQtaData.get("po_qty");
            //검수수량
            BigDecimal grQty = (checkQtaData.get("gr_qty") == null) ? BigDecimal.ZERO : (BigDecimal) checkQtaData.get("gr_qty");
            //기존 요청수량
            BigDecimal asnReqQty = (checkQtaData.get("asn_req_qty") == null) ? BigDecimal.ZERO : (BigDecimal) checkQtaData.get("asn_req_qty");
            //발주잔량
            BigDecimal remainQty = poItemQty.subtract(grQty).subtract(asnReqQty);

            // 납품예정 수량이 발주잔량보다 큰 경우 invalid
            if (arQty.compareTo(remainQty) > 0) {
                invalidQtyList.add(checkQtaData);
            }
        }
        return invalidQtyList;
    }

    /**
     * 수량이 정합하지 않은 품목들의 존재에 따른 resultMap 반환
     *
     * @param invalidQuantityItems
     * @return
     */
    private ResultMap getResultMapOfInvalidQuantityItems(List<Map<String, Object>> invalidQuantityItems) {
        ResultMap resultMap = ResultMap.getInstance();
        if (invalidQuantityItems.isEmpty()) {
            resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
        } else {
            resultMap.setResultStatus(ProConst.ASN_QTY_ERR);
            resultMap.setResultList(invalidQuantityItems);
        }
        return resultMap;
    }
}