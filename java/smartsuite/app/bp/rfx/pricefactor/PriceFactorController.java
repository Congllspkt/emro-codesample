package smartsuite.app.bp.rfx.pricefactor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.pricefactor.service.PriceFactorService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * CostFactor 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author JongKyu Kim
 * @see
 * @since 2016. 2. 2
 * @FileName PriceFactorController.java
 * @package smartsuite.app.bp.rfx.pricefactor
 * @변경이력 : [2016. 2. 2] JongKyu Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping (value = "**/rfx/pricefactor/")
public class PriceFactorController {

	@Inject
	PriceFactorService priceFactorService;

	/**
	 * CostFactor 목록 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the CostFactor list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceFactor
	 */
	@RequestMapping (value = "factor/findListPriceFactor.do")
	public @ResponseBody FloaterStream findListPriceFactor(@RequestBody Map param) {
		// 대용량 처리
		return priceFactorService.findListPriceFactor(param);
	}

	/**
	 * 가격군 목록 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the price group list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceGroup
	 */
	@RequestMapping (value = "group/findListPriceGroup.do")
	public @ResponseBody FloaterStream findListPriceGroup(@RequestBody Map param) {
		// 대용량 처리
		return priceFactorService.findListPriceGroup(param);
	}

	/**
	 * 가격군 상세 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the price group
	 * @Date : 2016. 2. 2
	 * @Method Name : findPriceGroup
	 */
	@RequestMapping (value = "group/findPriceGroup.do")
	public @ResponseBody Map findPriceGroup(@RequestBody Map param) {
		return priceFactorService.findPriceGroup(param);
	}

	/**
	 * 가격군 Factor 목록 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the price group factor list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPriceGroupFactor
	 */
	@RequestMapping (value = "group/findListPriceGroupFactor.do")
	public @ResponseBody List findListPriceGroupFactor(@RequestBody Map param) {
		return priceFactorService.findListPriceGroupFactor(param);
	}

	/**
	 * CostFactor 목록 저장을 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : saveListPriceFactor
	 */
	@RequestMapping (value = "factor/saveListPriceFactor.do")
	public @ResponseBody ResultMap saveListPriceFactor(@RequestBody Map param) {
		return priceFactorService.saveListPriceFactor(param);
	}

	/**
	 * 가격군 상세 및 가격군 Factors 저장을 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : savePriceGroupWithFactors
	 */
	@RequestMapping (value = "group/savePriceGroupWithFactors.do")
	public @ResponseBody ResultMap savePriceGroupWithFactors(@RequestBody Map param) {
		return priceFactorService.savePriceGroupWithFactors(param);
	}

	/**
	 * CostFactor 목록 삭제를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : deleteListPriceFactor
	 */
	@RequestMapping (value = "factor/deleteListPriceFactor.do")
	public @ResponseBody ResultMap deleteListPriceFactor(@RequestBody Map param) {
		return priceFactorService.deleteListPriceFactor(param);
	}

	/**
	 * 가격군 목록 삭제를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : deleteListPriceGroup
	 */
	@RequestMapping (value = "group/deleteListPriceGroup.do")
	public @ResponseBody ResultMap deleteListPriceGroup(@RequestBody Map param) {
		return priceFactorService.deleteListPriceGroup(param);
	}

}