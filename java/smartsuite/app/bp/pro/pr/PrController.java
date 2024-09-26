package smartsuite.app.bp.pro.pr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.pr.service.PrService;
import smartsuite.app.common.AttachService;

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
public class PrController {
	
	@Inject
	PrService prService;
	
	@Inject
	PrItemService prItemService;
	
	@Inject
	AttachService attachService;
	
	/* 진행상태 콤보박스 목록 조회 */
	@RequestMapping(value = "getPrPurpCcd.do")
    public @ResponseBody List getPrPurpCcd(@RequestBody String param) {
        return prService.getPrPurpCcd(param);
    }
	
	/**
	 * list pr 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 8
	 * @Method Name : findListPr
	 */
	
	
	@RequestMapping(value = "findListPr.do")
	public @ResponseBody FloaterStream findListPr(@RequestBody Map param){
		// 대용량 처리
		return prService.findListPr(param);
	}

	@RequestMapping(value = "findListPrSptpurc.do")
	public @ResponseBody FloaterStream findListPrSptpurc(@RequestBody Map param){
		param.put("pr_purp_ccd","SPTPURC");
		// 대용량 처리
		return prService.findListPr(param);
	}

	@RequestMapping(value = "findListPrUprccntr.do")
	public @ResponseBody FloaterStream findListPrUprccntr(@RequestBody Map param){
		param.put("pr_purp_ccd","UPRCCNTR_SGNG");
		// 대용량 처리
		return prService.findListPr(param);
	}
	
	/**
	 * pr 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 3. 10
	 * @Method Name : findPr
	 */
	@RequestMapping(value = "findPr.do")
	public @ResponseBody Map findPr(@RequestBody Map param){
		return prService.findPrData(param);
	}
	
	/**
	 * pr rev max 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the string
	 * @Date : 2016. 5. 24
	 * @Method Name : findPrRevMax
	 */
	@RequestMapping(value = "findPrRevMax.do")
	public @ResponseBody Map findPrRevMax(@RequestBody Map param){
		return prService.findPrRevMax(param);
	}
	
	/**
	 * list pr item 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 3. 14
	 * @Method Name : findListPrItem
	 */
	@RequestMapping(value = "findListPrItem.do")
	public @ResponseBody List findListPrItem(@RequestBody Map param){
		return prItemService.findPrItemByPr(param);
	}
	
	/**
	 * list pr 삭제를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 3. 8
	 * @Method Name : deleteListPr
	 */
	@RequestMapping(value = "deleteListPr.do")
	public @ResponseBody ResultMap deleteListPr(@RequestBody Map saveParam){
		return prService.deleteListPr(saveParam);
	}
	
	/**
	 * 구매요청 임시저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 24
	 * @Method Name : saveProductDraftPr
	 */
	@RequestMapping(value = "saveDraftPr.do")
	public @ResponseBody ResultMap saveDraftPr(@RequestBody Map saveParam){
		return prService.saveDraftPr(saveParam);
	}
	
	/**
	 * 구매요청 복사한다.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "copyPr.do")
	public @ResponseBody ResultMap copyPr(@RequestBody Map param){
		return prService.copyPr(param);
	}
	
	/**
	 * 구매요청 변경을 위한 데이터 조회
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 3. 10
	 * @Method Name : findModPr
	 */
	@RequestMapping(value = "findModPr.do")
	public @ResponseBody Map findModPr(@RequestBody Map param){
		return prService.findPrByChangePr(param);
	}
	
	/**
	 * 구매요청 변경건 저장
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "saveModPr.do")
	public @ResponseBody ResultMap saveModPr(@RequestBody Map saveParam){
		return prService.saveModPr(saveParam);
	}
	
	/**
	 * 단건의 구매요청 삭제한다.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "deletePr.do")
	public @ResponseBody ResultMap deletePr(@RequestBody Map param) {
		return prService.deletePr(param);
	}
	
	/**
	 * 즉시 구매 요청
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "directReqPr.do")
	public @ResponseBody ResultMap directReqPr(@RequestBody Map param) {
		return prService.directReqPr(param);
	}
	
	/**
	 * 구매요청 접수
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "receivePrs.do")
	public @ResponseBody ResultMap receivePrs(@RequestBody Map param){
		return prService.receivePrs(param);
	}
	
	/**
	 * 구매요청 반송
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "returnPrs.do")
	public @ResponseBody ResultMap returnPrs(@RequestBody Map param){
		return prService.returnPrs(param);
	}
	
	/**
	 * 나의 담당 구매그룹 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListMyPurcGrpCd
	 */
	@RequestMapping(value = "findListMyPurcGrpCd.do")
	public @ResponseBody List findListMyPurcGrpCd(@RequestBody Map param){
		return prService.findListMyPurcGrpCd(param);
	}
	
	/**
	 * 구매 진행현황 조회를 요청한다.
	 *
	 * @author : kh_lee
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 14
	 * @Method Name : findListPrItemProg
	 */
	@RequestMapping(value = "findListPrItemProg.do")
	public @ResponseBody FloaterStream findListPrItemProg(@RequestBody Map param) {
		// 대용량 처리
		return prService.findListPrItemProg(param);
	}
	
	/**
	 * 구매변경 가능여부를 확인한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2017. 4. 14
	 * @Method Name : checkModifiablePrItems
	 */
	@RequestMapping(value = "checkModifiablePrItems.do")
	public @ResponseBody ResultMap checkModifiablePrItems(@RequestBody Map param) {
		return prService.checkModifiablePrItems(param);
	}
	
	/**
	 * 구매요청 변경 이력 조회
	 * 
	 * @param param
	 * @return map {prList, prItemList}
	 */
	@RequestMapping(value = "findListPrHistory.do")
	public @ResponseBody ResultMap findListPrHistory(@RequestBody Map param) {
		return prService.findListPrHistory(param);
	}
	
	/**
	 * 구매요청 변경 비교 조회
	 * 
	 * @param param
	 * @return map {comparePrData, comparePrItems}
	 */
	@RequestMapping(value = "findPrCompare.do")
	public @ResponseBody Map findPrCompare(@RequestBody Map param) {
		return prService.findPrCompare(param);
	}
	
   /**
    * 일반구매에서 단가구매로 정보업데이트.
    *
    * @author : Chaerin Yu
    * @param param the param
    * @return the list
    * @Date : 2017. 7. 13
    * @Method Name : updateNCtoCC
    */
   @RequestMapping(value = "updateNCtoCC.do")
   public @ResponseBody ResultMap updateNCtoCC(@RequestBody Map param) {
      return prService.updateNCtoCC(param);
   }
   
   /**
	 * APP ID로 PR HD 정보 가져오기 (workplace) 
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value="findPrByPrItemId.do")
	public @ResponseBody Map findPrByPrItemId(@RequestBody Map param) {
		return prService.findPrByPrItemId(param);
	}

	@RequestMapping(value = "changePrBpa.do")
   public @ResponseBody ResultMap changePrBpa(@RequestBody Map param) {
      return prService.changePrBpa(param);
   }

	@RequestMapping(value="findPrItemUuid.do")
	public @ResponseBody Map findPrItemUuid(@RequestBody Map param) {
		return prService.findPrItemUuid(param);
	}
}
