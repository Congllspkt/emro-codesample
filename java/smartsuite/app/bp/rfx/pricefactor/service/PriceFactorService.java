package smartsuite.app.bp.rfx.pricefactor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.pricefactor.repository.PriceFactorRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * CostFactor 관련 처리하는 서비스 Class입니다.
 *
 * @author JongKyu Kim
 * @FileName PriceFactorService.java
 * @package smartsuite.app.bp.rfx.pricefactor
 * @Since 2016. 2. 2
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 * @see
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class PriceFactorService {

	@Inject
	PriceFactorRepository priceFactorRepository;

	/**
	 * CostFactor 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list CostFactor
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceFactor
	 */
	public FloaterStream findListPriceFactor(Map<String, Object> param) {
		// 대용량 처리
		return priceFactorRepository.findListPriceFactor(param);
	}

	/**
	 * 가격군 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list price group
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceGroup
	 */
	public FloaterStream findListPriceGroup(Map<String, Object> param) {
		// 대용량 처리
		return priceFactorRepository.findListPriceGroup(param);
	}

	/**
	 * 가격군 CostFactor 목록을 조회한다.
	 *
	 * @param param the param
	 * @return the list price group
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceGroupFactor
	 */
	public List<Map<String, Object>> findListPriceGroupFactor(Map<String, Object> param) {
		return priceFactorRepository.findListPriceGroupFactor(param);
	}

	/**
	 * 가격군 상세정보를 조회한다.
	 *
	 * @param param the param
	 * @return map
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findPriceGroup
	 */
	public Map<String, Object> findPriceGroup(Map<String, Object> param) {
		return priceFactorRepository.findPriceGroup(param);
	}

	/**
	 * CostFactor 목록을 등록/수정한다.
	 *
	 * @param param {"insertPriceFactors", "updatePriceFactors"}
	 * @return the map< string, object>
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : saveListPriceFactor
	 */
	public ResultMap saveListPriceFactor(Map<String, Object> param) {
		List<Map<String, Object>> inserts = (List<Map<String, Object>>) param.get("insertPriceFactors");
		List<Map<String, Object>> updates = (List<Map<String, Object>>) param.get("updatePriceFactors");

		if(inserts != null && !inserts.isEmpty()) {
			boolean exist = false;
			for(Map<String, Object> row : inserts) {
				if(priceFactorRepository.getCountPriceFactor(row) > 0) { // CostFactor 중복 체크
					exist = true;
					break;
				}
			}
			if(exist) {
				return ResultMap.builder()
				                .resultStatus(ResultMap.STATUS.DUPLICATED)
				                .build();
			}

			for(Map<String, Object> row : inserts) {
				priceFactorRepository.insertPriceFactor(row);
			}
		}
		if(updates != null && !updates.isEmpty()) {
			for(Map<String, Object> row : updates) {
				priceFactorRepository.updatePriceFactor(row);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 가격군과 가격군 Factors를 등록/수정한다.
	 *
	 * @param param {"priceGroup", "insertPriceGroupFactors", "deletePriceGroupFactors"}
	 * @return the map< string, object>
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : savePriceGroupWithFactors
	 */
	public ResultMap savePriceGroupWithFactors(Map<String, Object> param) {
		Map<String, Object> info = (Map<String, Object>) param.get("priceGroup");
		List<Map<String, Object>> inserts = (List<Map<String, Object>>) param.get("insertPriceGroupFactors");
		List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deletePriceGroupFactors");

		boolean isNew = false;
		if(info.get("is_new") != null) {
			isNew = (Boolean) info.get("is_new");
		}

		if(isNew) {
			if(info != null && !info.isEmpty()) {
				if(priceFactorRepository.getCountPriceGroup(info) > 0) { // 가격군 중복 체크
					return ResultMap.builder()
					                .resultStatus(ResultMap.STATUS.DUPLICATED)
					                .build();
				}
				priceFactorRepository.insertPriceGroup(info);
			}
		} else {
			if(info != null && !info.isEmpty()) {
				priceFactorRepository.updatePriceGroup(info);
			}
		}
		if(inserts != null && !inserts.isEmpty()) {
			for(Map<String, Object> row : inserts) {
				if(priceFactorRepository.getCountPriceGroupFactor(row) == 0) { // 가격군 Factor 중복 체크
					priceFactorRepository.insertPriceGroupFactor(row);
				}
			}
		}
		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes) {
				priceFactorRepository.deletePriceGroupFactor(row);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * CostFactor 목록을 삭제한다.
	 *
	 * @param param {"deletePriceFactors"}
	 * @return the map< string, object>
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : deleteListPriceFactor
	 */
	public ResultMap deleteListPriceFactor(Map<String, Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deletePriceFactors");

		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes) {
				priceFactorRepository.deletePriceFactor(row);
				priceFactorRepository.deletePriceGroupFactor(row);
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 가격군 목록을 삭제한다.
	 *
	 * @param param {"deletePriceGroups"}
	 * @return the map< string, object>
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : deleteListPriceGroup
	 */
	public ResultMap deleteListPriceGroup(Map<String, Object> param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deletePriceGroups");

		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes) {
				priceFactorRepository.deletePriceGroupFactorByPriceGroup(row);
				priceFactorRepository.deletePriceGroup(row);
			}
		}
		return ResultMap.SUCCESS();
	}
}
