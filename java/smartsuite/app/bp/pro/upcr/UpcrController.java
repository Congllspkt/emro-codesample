package smartsuite.app.bp.pro.upcr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.upcr.service.UpcrItemService;
import smartsuite.app.bp.pro.upcr.service.UpcrService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;


/**
 * Pr 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author Yeon-u Kim
 * @see 
 * @since 2016. 3. 8
 * @FileName PrController.java
 * @package smartsuite.app.bp.pro
 * @변경이력 : [2016. 3. 8] Yeon-u Kim 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/pro/**/")
public class UpcrController {
	
	@Inject
	UpcrService upcrService;
	
	@Inject
	UpcrItemService upcrItemService;
	
	/* 진행상태 콤보박스 목록 조회 */
	@RequestMapping(value = "getUpcrPurpCcd.do")
    public @ResponseBody List getUpcrPurpCcd(@RequestBody String param) {
        return upcrService.getPrPurpCcd(param);
    }

	/**
	 * findListUpcr
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListUpcr.do")
	public @ResponseBody FloaterStream findListUpcr(@RequestBody Map param){
		param.put("pr_purp_ccd","UPRCCNTR_SGNG");
		// 대용량 처리
		return upcrService.findListUpcr(param);
	}

	/**
	 * upcr 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Method Name : upcr
	 */
	@RequestMapping(value = "findUpcr.do")
	public @ResponseBody Map findUpcr(@RequestBody Map param){
		return upcrService.findUpcrData(param);
	}
	
	/**
	 * upcr rev max 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the string
	 */
	@RequestMapping(value = "findUpcrRevMax.do")
	public @ResponseBody Map findUpcrRevMax(@RequestBody Map param){
		return upcrService.findUpcrRevMax(param);
	}
	
	/**
	 * list pr item 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Method Name : findListPrItem
	 */
	@RequestMapping(value = "findListUpcrItem.do")
	public @ResponseBody List findListUpcrItem(@RequestBody Map param){
		return upcrItemService.findUpcrItemByUpcr(param);
	}
	
	/**
	 * list pr 삭제를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "deleteListUpcr.do")
	public @ResponseBody ResultMap deleteListUpcr(@RequestBody Map saveParam){
		return upcrService.deleteListUpcr(saveParam);
	}
	
	/**
	 * 구매요청 임시저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "saveDraftUpcr.do")
	public @ResponseBody ResultMap saveDraftUpcr(@RequestBody Map saveParam){
		return upcrService.saveDraftUpcr(saveParam);
	}
	
	/**
	 * 구매요청 복사한다.
	 *
	 * @return the map
	 */
	@RequestMapping(value = "copyUpcr.do")
	public @ResponseBody ResultMap copyUpcr(@RequestBody Map param){
		return upcrService.copyUpcr(param);
	}
	
	/**
	 * 구매요청 변경을 위한 데이터 조회
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 3. 10
	 * @Method Name : findModPr
	 */
	@RequestMapping(value = "findModUpcr.do")
	public @ResponseBody Map findModUpcr(@RequestBody Map param){
		return upcrService.findUpcrByChangeUpcr(param);
	}
	
	/**
	 * 구매요청 변경건 저장
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "saveModUpcr.do")
	public @ResponseBody ResultMap saveModUpcr(@RequestBody Map saveParam){
		return upcrService.saveModUpcr(saveParam);
	}
	
	/**
	 * 단건의 구매요청 삭제한다.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "deleteUpcr.do")
	public @ResponseBody ResultMap deleteUpcr(@RequestBody Map param) {
		return upcrService.deleteUpcr(param);
	}
	
	/**
	 * 즉시 구매 요청
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "directReqUpcr.do")
	public @ResponseBody ResultMap directReqUpcr(@RequestBody Map param) {
		return upcrService.directReqUpcr(param);
	}
	
	/**
	 * list pr items 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 3
	 * @Method Name : findListPrItems
	 */
	@RequestMapping(value = "findListUpcrItems.do")
	public @ResponseBody FloaterStream findListUpcrItems(@RequestBody Map param){
		return upcrService.findListUpcrAndUpcrItems(param);
	}
	
	/**
	 * 구매요청 접수
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "receiveUpcrs.do")
	public @ResponseBody ResultMap receiveUpcrs(@RequestBody Map param){
		return upcrService.receiveUpcrs(param);
	}
	
	/**
	 * 구매요청 반송
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "returnUpcrs.do")
	public @ResponseBody ResultMap returnUpcrs(@RequestBody Map param){
		return upcrService.returnUpcrs(param);
	}
	
	/**
	 * purc grp cd 수정을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 9
	 * @Method Name : updatePurcGrpCd
	 */
	/*@RequestMapping(value = "updatePurcGrpCd.do")
	public @ResponseBody ResultMap updatePurcGrpCd(@RequestBody Map param){
		return upcrService.updatePurcGrpCd(param);
	}*/
	
	/**
	 * 나의 담당 구매그룹 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListMyPurcGrpCd
	 */
	/*@RequestMapping(value = "findListMyPurcGrpCd.do")
	public @ResponseBody List findListMyPurcGrpCd(@RequestBody Map param){
		return upcrService.findListMyPurcGrpCd(param);
	}*/
	
	/**
	 * 구매 진행현황 조회를 요청한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListPrItemProg
	 */
	/*@RequestMapping(value = "findListPrItemProg.do")
	public @ResponseBody FloaterStream findListPrItemProg(@RequestBody Map param) {
		// 대용량 처리
		return upcrService.findListPrItemProg(param);
	}*/
	
	/**
	 * 구매변경 가능여부를 확인한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2017. 4. 14
	 * @Method Name : checkModifiablePrItems
	 */
	@RequestMapping(value = "checkModifiableUpcrItems.do")
	public @ResponseBody ResultMap checkModifiableUpcrItems(@RequestBody Map param) {
		return upcrService.checkModifiableUpcrItems(param);
	}
	
	/**
	 * 구매요청 변경 이력 조회
	 * 
	 * @param param
	 * @return map {prList, prItemList}
	 */
	@RequestMapping(value = "findListUpcrHistory.do")
	public @ResponseBody ResultMap findListUpcrHistory(@RequestBody Map param) {
		return upcrService.findListUpcrHistory(param);
	}
	
	/**
	 * 구매요청 변경 비교 조회
	 * 
	 * @param param
	 * @return map {comparePrData, comparePrItems}
	 */
	@RequestMapping(value = "findUpcrCompare.do")
	public @ResponseBody Map findUpcrCompare(@RequestBody Map param) {
		return upcrService.findUpcrCompare(param);
	}
}
