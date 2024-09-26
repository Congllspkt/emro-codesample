package smartsuite.app.bp.pro.gr.validator;

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
public class GrValidator {
	/**
	 * 검수데이터 정합성 확인
	 *
	 * @param param
	 * @param checkResult
	 * @param invalidPoItems
	 * @return
	 */
	public ResultMap validateGr(Map<String, Object> param, Map<String, Object> checkResult, List<Map<String, Object>> invalidPoItems) {
		ResultMap resultMap = ResultMap.getInstance();
		
		// UPDATE 시 : 화면에서 조회한 시점의 검수 진행상태값과 현재 DB에서의 진행상태값을 비교
		resultMap = ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		
		if(!ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			return resultMap;
		}
		
		String grStsCcd = param.get("gr_sts_ccd") == null ? null : param.get("gr_sts_ccd").toString();
		if("GR_CMPLD".equals(grStsCcd)) {    // 검수완료 상태
			// PO 품목의 검수취소 가능여부 체크
			if(null != param.get("gr_chr_flag")) {//외부에서 요청한 검수가 있는 경우 내부에서 요청한 검수를 우선으로 한다
				resultMap = setStatusOfResultMap(ResultMap.STATUS.SUCCESS);
			} else {
				// 유효하지 않은 PO 품목 리스트
//				resultMap = checkPoItemsGrCancelable(invalidPoItems);
			}
			// 유효하지 않은 PO 품목 리스트
			resultMap = checkPoItemsGrCancelable(invalidPoItems);
		}
		return resultMap;
	}
	
	/**
	 * 발주품목 검수등록 정합성 체크
	 *
	 * @param invalidPoItems
	 * @return
	 */
	public ResultMap validatePoItems(List<Map<String, Object>> invalidPoItems) {
		return checkPoItemsGrCreatable(invalidPoItems);
	}
	
	/**
	 * 검수등록 가능여부 체크
	 *
	 * @param invalidPoItems
	 * @return
	 */
	private ResultMap checkPoItemsGrCreatable(List<Map<String, Object>> invalidPoItems) {
		return validateItem(ProConst.PO_STATE_ERR, invalidPoItems);
	}
	
	/**
	 * 검수등록 수량 체크
	 *
	 * @param param
	 * @return
	 */
	public ResultMap checkPoItemsGrQuantity(Map<String, Object> param, List<Map<String, Object>> checkQuantityItems) {
		return validateItem(ProConst.GR_QTY_ERR, getInvalidQuantityItems(getPoItemIds(param), checkQuantityItems));
	}
	
	/**
	 * 무효한 품목을 반환
	 *
	 * @param poItems
	 * @param checkQuantityItems
	 * @return
	 */
	private List<Map<String, Object>> getInvalidQuantityItems(Map<String, Object> poItems, List<Map<String, Object>> checkQuantityItems) {
		List<Map<String, Object>> invalidQuantityItems = Lists.newArrayList();
		for(Map<String, Object> checkQuantityItem : checkQuantityItems) {
			Map<String, Object> item = (Map<String, Object>) poItems.get(checkQuantityItem.get("po_item_uuid"));
			
			//발주수량
			BigDecimal poItemQty = (checkQuantityItem.get("po_qty") == null) ? BigDecimal.ZERO : (BigDecimal) checkQuantityItem.get("po_qty");
			//입고수량
			BigDecimal grCompQty = (checkQuantityItem.get("gr_comp_qty") == null) ? BigDecimal.ZERO : (BigDecimal) checkQuantityItem.get("gr_comp_qty");
			//검수등록할 수량
			BigDecimal grQty = (item.get("gr_qty") == null) ? BigDecimal.ZERO : new BigDecimal(item.get("gr_qty").toString());
			//발주잔량 (발주수량-입고수량)
			BigDecimal remainQty = poItemQty.subtract(grCompQty);
			
			// 검수등록할 수량이 발주잔량보다 큰 경우 invalid
			if(grQty.compareTo(remainQty) > 0) {
				invalidQuantityItems.add(checkQuantityItem);
			}
		}
		return invalidQuantityItems;
	}
	
	/**
	 * 발주 품목정보를 담아서 반환
	 *
	 * @param param
	 * @return
	 */
	private Map<String, Object> getPoItemIds(Map<String, Object> param) {
		List<Map<String, Object>> checkItems = (List<Map<String, Object>>) param.get("checkItems");    //모든 품목
		
		Map<String, Object> poItems = Maps.newHashMap();
		for(Map<String, Object> item : checkItems) {
			poItems.put(item.get("po_item_uuid").toString(), item);
		}
		return poItems;
	}
	
	/**
	 * 검수취소 가능여부를 확인한다.
	 *
	 * @return
	 */
	private ResultMap checkPoItemsGrCancelable(List<Map<String, Object>> invalidPoItems) {
		return validateItem(ProConst.PO_STATE_ERR, invalidPoItems);
	}
	
	/**
	 * 품목 검증
	 *
	 * @param errorCode
	 * @param invalidItems
	 * @return
	 */
	private ResultMap validateItem(String errorCode, List<Map<String, Object>> invalidItems) {
		return invalidItems.isEmpty()
				? setStatusOfResultMap(ResultMap.STATUS.SUCCESS)
				: setInvalidItemsOfResultMap(errorCode, invalidItems);
	}
	
	/**
	 * ResultMap Status를 설정한다.
	 *
	 * @param statusCode
	 * @return
	 */
	private ResultMap setStatusOfResultMap(String statusCode) {
		ResultMap resultMap = ResultMap.getInstance();
		resultMap.setResultStatus(statusCode);
		return resultMap;
	}
	
	/**
	 * 검증되지 않은 데이터를 ResultMap에 담아서 반환한다.
	 *
	 * @param errorCode
	 * @param invalidItems
	 * @return
	 */
	private ResultMap setInvalidItemsOfResultMap(String errorCode, List<Map<String, Object>> invalidItems) {
		ResultMap resultMap = setStatusOfResultMap(errorCode);
		resultMap.setResultList(invalidItems);
		return resultMap;
	}
}
