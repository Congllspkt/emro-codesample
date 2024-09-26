package smartsuite.app.bp.rfx.nego;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.rfx.nego.service.NegoService;

import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 협상관련 controller
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping("**/bp/rfx/nego/**/")
public class NegoController {
	
	@Inject
	NegoService negoService;
	
	/**
	 * 협상대상 지정
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveNegoTarget.do")
	public @ResponseBody ResultMap saveNegoTarget(@RequestBody Map param){
		return negoService.saveNegoTarget(param);
	}
	
	/**
	 * 협상대상 조회 (RFX 기준)
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findNegoTargetList.do")
	public @ResponseBody List findNegoTargetList(@RequestBody Map param){
		return negoService.findNegoTargetList(param);
	}
	
	/**
	 * 협상 취소 리스트
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "cancelNegoTargetList.do")
	public @ResponseBody ResultMap cancelNegoTargetList(@RequestBody Map param) {
		return negoService.cancelNegoTargetList(param);
	}
	/**
	 * 협상 취소 단건
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "cancelNego.do")
	public @ResponseBody ResultMap cancelNego(@RequestBody Map param) {
		return negoService.cancelNego(param);
	}

	/**
	 * 협상 헤더 조회
	 * @return
	 */
	@RequestMapping(value = "findNegoInfo.do")
	public @ResponseBody Map findNegoInfo(@RequestBody Map param){
		return negoService.findNegoInfo(param);
	}
	
	/**
	 * 협상상세 품목 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findNegoDetailInfo.do")
	public @ResponseBody Map findNegoDetailInfo(@RequestBody Map param){
		return negoService.findNegoDetailInfo(param);
	}
	
	/**
	 * 협상헤더 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "requestNego.do")
	public @ResponseBody ResultMap requestNego(@RequestBody Map param){
		return negoService.requestNego(param);
	}
	
	/**
	 * 협상헤더 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "negoSuccess.do")
	public @ResponseBody ResultMap negoSuccess(@RequestBody Map param){
		return negoService.negoSuccess(param);
	}
	
	/**
	 * 협상헤더 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "negoFailure.do")
	public @ResponseBody ResultMap negoFailure(@RequestBody Map param){
		return negoService.negoFailure(param);
	}
	
	/**
	 * 협상헤더 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "requestReNego.do")
	public @ResponseBody ResultMap requestReNego(@RequestBody Map param){
		return negoService.requestReNego(param);
	}
	
	/**
	 * 여러건 조기 마감.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "byPassCloseNegos.do")
	public @ResponseBody ResultMap byPassCloseNegos(@RequestBody Map saveParam) {
		return negoService.byPassCloseNegos(saveParam);
	}
	
	/**
	 * 낙찰자 선정
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "selectNegoRfxBids.do")
	public @ResponseBody ResultMap selectNegoRfxBids(@RequestBody Map param) {
		return negoService.selectNegoRfxBids(param);
	}

	/**
	 * 협상 품의서 존재여부 확인.
	 *
	 * @param param
	 * @return map
	 */
	@RequestMapping(value = "checkNegoApproval.do")
	public @ResponseBody ResultMap checkNegoApproval(@RequestBody Map param){
		return negoService.checkNegoApproval(param);
	}
	
	/**
	 * 협상 선정품의 삭제
	 *
	 * @param rfxData
	 * @return map
	 */
	@RequestMapping(value = "deleteNegoApproval.do")
	public @ResponseBody ResultMap deleteNegoApproval(@RequestBody Map param) {
		return negoService.deleteNegoApproval(param);
	}
	
	/**
	 * 협상 선정 가능한 대상 조회 (RFX 기준)
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "getNegotiableTargets.do")
	public @ResponseBody ResultMap getNegotiableTargets(@RequestBody Map param) {
		return negoService.getNegotiableTargets(param);
	}
}
