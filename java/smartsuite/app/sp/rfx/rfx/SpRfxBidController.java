package smartsuite.app.sp.rfx.rfx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfx.service.SpRfxBidService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/sp/rfx/rfx/**/")
public class SpRfxBidController {
	
	/** The rfx vendor service. */
	@Inject
	SpRfxBidService spRfxBidService;
	
	/**
	 * list rfx vd 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 17
	 * @Method Name : findListRfxVd
	 */
	@RequestMapping(value = "findListRfxVd.do")
	public @ResponseBody FloaterStream findListRfxVd(@RequestBody Map param){
		// 대용량 처리
		return spRfxBidService.findListRfxVd(param);
	}
	
	/**
	 * rfx 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 11
	 * @Method Name : findRfx
	 */
	@RequestMapping(value = "findRfxBid.do")
	public @ResponseBody Map findRfxBidInfo(@RequestBody Map param){
		return spRfxBidService.findRfxBidInfo(param);
	}



	/**
	 * rfx qta 임시 저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 23
	 * @Method Name : saveTempRfxQta
	 */
	@RequestMapping(value = "saveTempRfxBid.do")
	public @ResponseBody ResultMap saveTempRfxBid(@RequestBody Map param) {
		return spRfxBidService.temporarySaveRfxBid(param);
	}
	/**
	 * rfx qta 제출을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 23
	 * @Method Name : submitRfxQta
	 */
	@RequestMapping(value = "submitRfxBid.do")
	public @ResponseBody ResultMap submitRfxBid(@RequestBody Map param) {
		return spRfxBidService.submitRfxBid(param);
	}
	
	/**
	 * 견적포기
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 6. 3
	 * @Method Name : abandonRfxQta
	 */
	@RequestMapping(value = "abandonRfxBid.do")
	public @ResponseBody ResultMap abandonRfxBid(@RequestBody Map param) {
		return spRfxBidService.abandonRfxBid(param);
	}
	
	/**
	 * 견적품목 별 원가구성항목 작성 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListBidItemPriceFactor.do")
	public @ResponseBody List findListBidItemPriceFactor(@RequestBody Map param){
		return spRfxBidService.findListBidItemPriceFactor(param);
	}
	
	/**
	 * 선정취소 사유 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoCancelCause.do")
	public @ResponseBody Map findInfoCancelCause(@RequestBody Map param) {
		return spRfxBidService.findInfoCancelCause(param);
	}
	
	/**
	 * 공동수급협정서 현황 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxCs.do")
	public @ResponseBody FloaterStream findListRfxCs(@RequestBody Map param) {
		// 대용량 처리
		return spRfxBidService.findListRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 상세 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxCs.do")
	public @ResponseBody Map findRfxCs(@RequestBody Map param) {
		return spRfxBidService.findRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 대상 협력사 추가 팝업 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxCsVdTarget.do")
	public @ResponseBody List findListRfxCsVdTarget(@RequestBody Map param) {
		return spRfxBidService.findListRfxCsVdTarget(param);
	}
	
	/**
	 * 견적 생성을 위한 공동수급협정서 협력사 유효성 체크
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "checkBidCreatableByRfxCs.do")
	public @ResponseBody ResultMap checkBidCreatableByRfxCs(@RequestBody Map param) {
		return spRfxBidService.checkBidCreatableByRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 협력사 유효성 체크
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "checkRfxCsVd.do")
	public @ResponseBody ResultMap checkRfxCsVd(@RequestBody Map param) {
		return spRfxBidService.checkRfxCsVd(param);
	}
	
	/**
	 * 공동수급협정서 저장
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveRfxCs.do")
	public @ResponseBody ResultMap saveRfxCs(@RequestBody Map param) {
		return spRfxBidService.saveRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 승인
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "approveRfxCs.do")
	public @ResponseBody ResultMap approveRfxCs(@RequestBody Map param) {
		return spRfxBidService.approveRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 발송
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "sendRfxCs.do")
	public @ResponseBody ResultMap sendRfxCs(@RequestBody Map param) {
		return spRfxBidService.sendRfxCs(param);
	}
	
	/**
	 * 공동수급협정서 작성취소
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "cancelRfxCs.do")
	public @ResponseBody ResultMap cancelRfxCs(@RequestBody Map param) {
		return spRfxBidService.cancelRfxCs(param);
	}
	
	@RequestMapping(value="checkRfxPreInspCompYByRfx.do")
	public @ResponseBody ResultMap checkRfxPreInspCompYByRfx(@RequestBody Map param){
		return spRfxBidService.checkRfxPreInspCompYByRfx(param);
	}

}
