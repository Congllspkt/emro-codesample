package smartsuite.app.bp.pro.shared.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.shared.event.ProSharedEventPublisher;
import smartsuite.app.bp.pro.shared.repository.ProSharedRepository;
import smartsuite.app.common.shared.Const;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PrShared 관련 처리하는 서비스 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @FileName ProSharedService.java
 * @package smartsuite.app.bp.pro.shared
 * @Since 2016. 3. 11
 * @변경이력 : [2016. 3. 11] Yeon-u Kim 최초작성
 */
@SuppressWarnings ("unchecked")
@Service
@Transactional
public class ProSharedService {

	/** The sql session. */
	@Inject
	private ProSharedRepository proSharedRepository;


	@Inject
	private ProSharedEventPublisher proSharedEventPublishser;

	/**
	 * list cate item 조회한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 11
	 * @Method Name : findListCateItem
	 */
	public List<Map<String, Object>> findListCateItemAndBpa(Map<String, Object> param) {
		return proSharedRepository.findListCateItemAndBpa(param);
	}

	/**
	 * 품목 별 계약 및 발주 이력 조회
	 *
	 * @param param : {oorg_cds, item_cd, search_typ}
	 * @return list
	 */
	public Map<String, Object> findListItemContractHistory(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if("PO".equals(param.get("search_typ"))) {
			result.put("cntrItems", findListPoHistory(param));
		} else {
			result.put("cntrItems", findListUnitPriceContractHistory(param));
			result.put("cntrItemsByPrice", findListCntrItemGroupByPrice(param));
		}
		return result;
	}
	
	/**
	 * 협력사 별 계약 및 발주 이력 조회
	 *
	 * @param param : {oorg_cds, vd_cd, search_typ}
	 * @return list
	 */
	public Map<String, Object> findListVendorContractHistory(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if("PO".equals(param.get("search_typ"))) {
			result.put("cntrItems", findListPoHistory(param));
			result.put("annualPoAmts", findListMonthlyPoAmt(param));
		} else {
			result.put("cntrItems", findListUnitPriceContractHistory(param));
			result.put("cntrItemsByPrice", findListCntrItemGroupByPrice(param));
		}
		return result;
	}
	
	private List<Map<String, Object>> findListPoHistory(Map<String, Object> param) {
		return proSharedRepository.findListPoHistory(param);
	}
	
	private List<Map<String, Object>> findListUnitPriceContractHistory(Map<String, Object> param) {
		return proSharedRepository.findListUnitPriceContractHistory(param);
	}
	
	private List<Map<String, Object>> findListCntrItemGroupByPrice(Map<String, Object> param) {
		return proSharedRepository.findListCntrItemGroupByPrice(param);
	}
	
	private List<Map<String, Object>> findListMonthlyPoAmt(Map<String, Object> param) {
		return proSharedRepository.findListMonthlyPoAmt(param);
	}

	/**
	 * 품목 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findItemBasicInfo(Map<String, Object> param) {
		return proSharedRepository.findItemBasicInfo(param);
	}

	/**
	 * 특정 품목에 대한 협력사 별 발주금액 합계 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListVendorPoTotAmtByItem(Map<String, Object> param) {
		return proSharedRepository.findListVendorPoTotAmtByItem(param);
	}

	/**
	 * 특정 품목에 대한 협력사 별 발주단가 목록 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListVendorPoItemPriceByItem(Map<String, Object> param) {
		return proSharedRepository.findListVendorPoItemPriceByItem(param);
	}

	/**
	 * 협력사 기본정보 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findVendorBasicInfo(Map<String, Object> param) {
		return proSharedRepository.findVendorBasicInfo(param);
	}

	/**
	 * 특정 협력사에 대한 연도별 구매품목 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListYearlyPoItemByVendor(Map<String, Object> param) {
		return proSharedRepository.findListYearlyPoItemByVendor(param);
	}

	/**
	 * 특정 협력사에 대한 연도별 RFx 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListYearlyRfxItemByVendor(Map<String, Object> param) {
		return proSharedEventPublishser.findListYearlyRfxItemByVendor(param);
	}

	/**
	 * 특정 협력사에 대한 연도별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListYearlyPoTotAmtByVendor(Map<String, Object> param) {
		return proSharedRepository.findListYearlyPoTotAmtByVendor(param);
	}

	/**
	 * 특정 협력사에 대한 년월 별 구매이력 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListMonthlyPoTotAmtByVendor(Map<String, Object> param) {
		return proSharedRepository.findListMonthlyPoTotAmtByVendor(param);
	}
}
