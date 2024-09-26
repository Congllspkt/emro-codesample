package smartsuite.app.bp.rfx.rfx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.approval.service.ApprovalService;
import smartsuite.app.bp.rfx.rfx.service.RfxBidService;
import smartsuite.app.bp.rfx.rfx.service.RfxEvalService;
import smartsuite.app.bp.rfx.rfx.service.RfxService;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Rfx 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author Yeon-u Kim
 * @see
 * @since 2016. 5. 3
 * @FileName RfxController.java
 * @package smartsuite.app.bp.rfx.rfx
 * @변경이력 : [2016. 5. 3] Yeon-u Kim 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/rfx/rfx/**/")
public class RfxController {

	/** The rfx service. */
	@Inject
	RfxService rfxService;

	/** The rfx eval service. */
	@Inject
	RfxEvalService rfxEvalService;

	@Inject
	RfxBidService rfxBidService;
	
	@Inject
	ApprovalService approvalService;

	@Inject
	AttachService attachService;

	/**
	 * list rfx 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 3
	 */
	@RequestMapping(value = "findListRfx.do")
	public @ResponseBody FloaterStream findListRfx(@RequestBody Map param){
		// 대용량 처리
		return rfxService.findListRfx(param);
	}

	/**
	 * rfx 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 11
	 */
	@RequestMapping(value = "findRfx.do")
	public @ResponseBody Map findRfx(@RequestBody Map param){
		return rfxService.findRfx(param);
	}

	/**
	 * rfx 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map (rfxItems, rfxVendors 포함)
	 * @Date : 2016. 5. 11
	 */
	@RequestMapping(value = "findRfxData.do")
	public @ResponseBody Map findRfxData(@RequestBody Map param){
		return rfxService.findRfxData(param);
	}
	
	@RequestMapping(value = "findRfxDefaultDataByReqItemIds.do")
	public @ResponseBody Map findRfxDefaultDataByReqItemIds(@RequestBody Map param) {
		return rfxService.findRfxDefaultDataByReqItemIds(param);
	}

	@RequestMapping(value = "findRfxDefaultDataByAiItemIds.do")
	public @ResponseBody Map findRfxDefaultDataByAiItemIds(@RequestBody Map param) {
		return rfxService.findRfxDefaultDataByAiItemIds(param);
	}

	/**
	 * rfx 추천 대상 업체 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxRcmdTargVendor.do")
	public @ResponseBody List findListRfxRcmdTargVendor(@RequestBody Map param){
		return rfxService.findListRfxRcmdTargVendor(param);
	}

	/**
	 * rfx 추천 옵션 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxRcmdOption.do")
	public @ResponseBody List findListRfxRcmdOption(@RequestBody Map param){
		return rfxService.findListRfxRcmdOption(param);
	}

	/**
	 * rfx by result 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 6. 23
	 */
	@RequestMapping(value = "findRfxByResult.do")
	public @ResponseBody Map findRfxByResult(@RequestBody Map param){
		return rfxService.findRfxByResult(param);
	}

	/**
	 * rfx 임시 저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 24
	 */
	@RequestMapping(value = "saveDraftRfx.do")
	public @ResponseBody ResultMap saveDraftRfx(@RequestBody Map param){
		return rfxService.saveDraftRfx(param);
	}

	/**
	 * 업체 전송.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "directRfxBid.do")
	public @ResponseBody ResultMap directRfxBid(@RequestBody Map param) {
		return rfxService.directRfxBid(param);
	}

	/**
	 * 평가기준 설정, 원가구성항목 설정 확인
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping (value = "checkBeforeRequestRfx.do")
	public @ResponseBody ResultMap checkBeforeRequestRfx(@RequestBody Map param) {
		return rfxService.checkBeforeRequestRfx(param);
	}

	/**
	 * list rfx 삭제를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 11
	 */
	@RequestMapping(value = "deleteListRfx.do")
	public @ResponseBody ResultMap deleteListRfx(@RequestBody Map saveParam) {
		return rfxService.deleteListRfx(saveParam);
	}

	/**
	 * rfx 삭제를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 12
	 */
	@RequestMapping(value = "deleteRfx.do")
	public @ResponseBody ResultMap deleteRfx(@RequestBody Map saveParam) {
		return rfxService.deleteRfx(saveParam);
	}

	/**
	 * rfx close dt 수정을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 20
	 */
	@RequestMapping(value = "updateRfxCloseDt.do")
	public @ResponseBody ResultMap updateRfxCloseDt(@RequestBody Map saveParam) {
		return rfxService.updateRfxCloseDt(saveParam);
	}

	/**
	 * 여러건 조기 마감.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "byPassCloseRfxs.do")
	public @ResponseBody ResultMap byPassCloseRfxs(@RequestBody Map saveParam) {
		return rfxService.byPassCloseRfxs(saveParam);
	}

	/**
	 * 평가 강제마감
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "forceCloseRfxEval.do")
	public @ResponseBody ResultMap forceCloseRfxEval(@RequestBody Map param) {
		return rfxService.forceCloseRfxEval(param);
	}

	/**
	 * 품목별 최저가 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 24
	 */
	@RequestMapping(value = "findListRfxItemRfxAttends.do")
	public @ResponseBody List findListRfxItemRfxAttends(@RequestBody Map param){
		return rfxService.findListRfxItemRfxAttends(param);
	}

	/**
	 * 총액별 최저가 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 25
	 */
	@RequestMapping(value = "findListRfxAmountRfxAttends.do")
	public @ResponseBody Map findListRfxAmountRfxAttends(@RequestBody Map param){
		return rfxService.findListRfxAmountRfxBidAttends(param);
	}

	/**
	 * 종합평가 랭킹 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 9
	 */
	@RequestMapping(value = "findListRankingTotalEvalAmount.do")
	public @ResponseBody Map findListRankingTotalEvalAmount(@RequestBody Map param){
		return rfxService.findListRankingTotalEvalAmount(param);
	}

	/**
	 * rfx qta 수정을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 25
	 */
	@RequestMapping(value = "selectRfxBids.do")
	public @ResponseBody ResultMap selectRfxBids(@RequestBody Map saveParam){
		return rfxService.selectRfxBids(saveParam);
	}

	/**
	 * rfx item의 선정 견적서 정보 저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param saveParam the save param
	 * @return the map
	 * @Date : 2016. 5. 24
	 */
	@RequestMapping(value = "selectRfxBidItems.do")
	public @ResponseBody ResultMap selectRfxBidItems(@RequestBody Map saveParam){
		return rfxService.selectRfxBidItems(saveParam);
	}

	/**
	 * 개찰 처리.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "bypassOpenRfx.do")
	public @ResponseBody ResultMap bypassOpenRfx(@RequestBody Map saveParam){
		return rfxService.bypassOpenRfx(saveParam);
	}

	/**
	 * RFx결과품의 존재여부 확인.
	 *
	 * @param param
	 * @return map
	 */
	@RequestMapping(value = "checkRfxResultApproval.do")
	public @ResponseBody ResultMap checkRfxResultApproval(@RequestBody Map param){
		return rfxService.checkRfxResultApproval(param);
	}

	/**
	 * RFx 결과 품의 삭제
	 *
	 * @param rfxData
	 * @return map
	 */
	@RequestMapping(value = "deleteRfxResultApproval.do")
	public @ResponseBody ResultMap deleteRfxResultApproval(@RequestBody Map param) {
		return rfxService.deleteRfxResultApproval(param);
	}

	/**
	 * rfx 유찰처리.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "dropRfx.do")
	public @ResponseBody ResultMap dropRfx(@RequestBody Map saveParam) {
		return rfxService.dropRfx(saveParam);
	}

	/**
	 * rfx 취소처리.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "cancelRfx.do")
	public @ResponseBody ResultMap cancelRfx(@RequestBody Map saveParam) {
		return rfxService.cancelRfx(saveParam);
	}

	/**
	 * rfx 결과 선정 처리.
	 *
	 * @param saveParam the save param
	 * @return the map
	 */
	@RequestMapping(value = "bypassApprovalRfxResult.do")
	public @ResponseBody ResultMap bypassApprovalRfxResult(@RequestBody Map saveParam) {
		return rfxService.bypassApprovalRfxResult(saveParam);
	}

	/**
	 *  rfx 재견적 저장을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 24
	 */
	@RequestMapping(value = "saveMultiRoundRfx.do")
	public @ResponseBody ResultMap saveMultiRoundRfx(@RequestBody Map param) {
		return rfxService.saveMultiRoundRfx(param);
	}

	/**
	 * 평가자 리스트 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 30
	 */
	@RequestMapping(value = "findListRfxEvaltr.do")
	public @ResponseBody List findListRfxEvaltr(@RequestBody Map param){
		return rfxEvalService.findListRfxEvaltr(param);
	}

	/**
	 * rfx pri eval set linear 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 6. 23
	 */
	@RequestMapping(value = "findRfxPriEvalSetLinear.do")
	public @ResponseBody Map findRfxPriEvalSetLinear(@RequestBody Map param){
		// TODO 비가격평가 설정 부분 재구성
		//return rfxEvalService.findRfxPriEvalSetLinear(param);
		return null;
	}

	/**
	 * 평가기준 설정 저장 (가격평가 점수 평가기준, 비가격 평가 설정, 평가자 설정)
	 *
	 * @author kh_lee
	 * @param param
	 * @return map
	 * @Date : 2017. 5. 16
	 */
	@RequestMapping(value = "saveRfxEvalSet.do")
	public @ResponseBody ResultMap saveRfxEvalSet(@RequestBody Map param) {
		return rfxEvalService.saveRfxNpeFact(param);
	}


	/**
	 * 견적서 작성 (업체대행) 목록 조회를 요청한다.
	 *
	 * @author kh_lee
	 * @param param the param
	 * @return list : 견적서 작성 (업체대행) 목록
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "findListRfxBidBuyer.do")
	public @ResponseBody FloaterStream findListRfxBidBuyer(@RequestBody Map param) {
		// 대용량 처리
		return rfxBidService.findListRfxBidBuyer(param);
	}

	/**
	 * 견적서 작성 (업체대행) 상세정보 조회를 요청한다.
	 *4
	 * @author kh_lee
	 * @param param the param
	 * @return map : 견적서 작성 (업체대행) 헤더 및 품목 정보
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "findRfxBidBuyer.do")
	public @ResponseBody Map findRfxBidInfo(@RequestBody Map param) {
		return rfxBidService.findRfxBidInfo(param);
	}

	/**
	 * 견적서 작성 (업체대행) 임시저장을 요청한다.
	 *
	 * @author kh_lee
	 * @param param the param
	 * @return resultMap : 견적서 작성 (업체대행) 저장 후 처리 결과를 담은 map
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "saveRfxBidBuyer.do")
	public @ResponseBody ResultMap temporarySaveRfxBid(@RequestBody Map param) {
		return rfxBidService.temporarySaveRfxBid(param);
	}

	/**
	 * 견적서 작성 (업체대행) 제출을 요청한다.
	 *
	 * @author kh_lee
	 * @param param the param
	 * @return resultMap : 견적서 작성 (업체대행) 제출 후 처리 결과를 담은 map
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "submitRfxBidBuyer.do")
	public @ResponseBody ResultMap submitRfxBid(@RequestBody Map param) {
		return rfxBidService.submitRfxBid(param);
	}

	/**
	 * 견적서 작성 (업체대행) 포기를 요청한다.
	 *
	 * @author kh_lee
	 * @param param the param
	 * @return resultMap : 견적서 작성 (업체대행) 포기 후 처리 결과를 담은 map
	 * @Date : 2016. 6.17
	 */
	@RequestMapping(value = "abandonRfxBidBuyer.do")
	public @ResponseBody ResultMap abandonRfxBid(@RequestBody Map param) {
		return rfxBidService.abandonRfxBid(param);
	}

	/**
	 * list rfx qta 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 22
	 */
	@RequestMapping(value = "findListRfxBid.do")
	public @ResponseBody List findListRfxBid(@RequestBody Map param) {
		return rfxBidService.findListRfxBid(param);
	}

	/**
	 * rfx요청업체의 견적서 리스트를 조회를 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 6. 22
	 */
	@RequestMapping(value = "findListRfxVdBid.do")
	public @ResponseBody List findListRfxVdBid(@RequestBody Map param){
		return rfxService.findListRfxVdBid(param);
	}

	/**
	 * Copy rfx.
	 *
	 * @param param the param
	 * @return the map
	 */
	@RequestMapping(value = "copyRfx.do")
	public @ResponseBody ResultMap copyRfx(@RequestBody Map param) {
		return rfxService.copyRfx(param);
	}

	/**
	 * RFx Ranking 구하기
	 * @param param
	 */
	@RequestMapping(value = "computeRanking.do")
	public @ResponseBody void computeRanking(@RequestBody Map param) {
		rfxService.computeRanking(param);
	}

	/**
	 * 평가 재수행
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "reExecuteNpeEval.do")
	public @ResponseBody ResultMap reExecuteNpeEval(@RequestBody Map param) {
		return rfxEvalService.reExecuteNpeEval(param);
	}

	/**
	 * RFx 정보 및 품목 목록(가격군 포함) 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxAndListRfxItemWithPriceGroup.do")
	public @ResponseBody Map findRfxAndListRfxItemWithPriceGroup(@RequestBody Map param) {
		return rfxService.findRfxAndListRfxItemWithPriceGroup(param);
	}

	/**
	 * 품목군 별 가격군 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListPriceGroupByItemGrpTyp.do")
	public @ResponseBody List findListPriceGroupByItemGrpTyp(@RequestBody Map param) {
		return rfxService.searchPriceGroupByItemGrpTyp(param);
	}

	/**
	 * RFx 품목 별 원가구성항목 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListPriceFactorByRfxItem.do")
	public @ResponseBody List findListPriceFactorByRfxItem(@RequestBody Map param) {
		return rfxService.searchPriceFactorByRfxItem(param);
	}

	/**
	 * RFx 품목 별 원가구성항목 저장
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveRfxItemPriceFactor.do")
	public @ResponseBody ResultMap saveRfxItemPriceFactor(@RequestBody Map param) {
		return rfxService.saveRfxItemPriceFactor(param);
	}


	/**
	 * 원가구성항목 작성 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxBidItemPriceFactor.do")
	public @ResponseBody List findListBidItemPriceFactor(@RequestBody Map param){
		return rfxBidService.findListBidItemPriceFactorForAgent(param);
	}

	/**
	 * 원가구성 작성 비교 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxBidItemPriceFactorCompare.do")
	public @ResponseBody Map findListRfxBidItemPriceFactorCompare(@RequestBody Map param){
		return rfxService.findListRfxBidItemPriceFactorCompare(param);
	}

	/**
	 * RFX 평가종류 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRFxEvalKind.do")
	public @ResponseBody List findListRFxEvalKind(@RequestBody Map param){
		// TODO 평가 모니터링 재구성
		//return rfxEvalService.findListRFxEvalKind(param);
		return null;
	}

	/**
	 * 평가 모니터링 평가자 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListEvalMonEvaltr.do")
	public @ResponseBody List findListEvalMonEvaltr(@RequestBody Map param){
		return rfxEvalService.findListEvalMonEvaltr(param);
	}

	/**
	 * 견적비교 by rfx_uuid
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxBidCompareByRfxId.do")
	public @ResponseBody List findListRfxBidCompareByRfxId(@RequestBody Map param){
		return rfxBidService.findListRfxBidCompareByRfx(param);
	}

	/**
	 * 견적비교 by rfx_no
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListRfxBidCompareByRfxNo.do")
	public @ResponseBody List findListRfxBidCompareByRfxNo(@RequestBody Map param) {
		return rfxBidService.findListRfxBidCompareByRfxNo(param);
	}
	
	@RequestMapping(value = "findListRfxBidItemComparePriceDoctor.do")
	public @ResponseBody List findListRfxBidItemComparePriceDoctor(@RequestBody Map param) {
		return rfxBidService.findListRfxBidItemComparePriceDoctor(param);
	}

	@RequestMapping(value = "rfxSendMail.do")
	public @ResponseBody ResultMap rfxSendMail(@RequestBody Map param) {
		return rfxService.rfxSendMail(param);
	}

	/**
	 * 해당 RFx의 공동수급협정서 현황 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListCsByRfx.do")
	public @ResponseBody List findListCsByRfx(@RequestBody Map param) {
		return rfxService.findListCsByRfx(param);
	}

	/**
	 * 공동수급협정서 상세 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxCsData.do")
	public @ResponseBody Map findRfxCsData(@RequestBody Map param) {
		return rfxService.findRfxCsData(param);
	}
	
	@RequestMapping(value = "checkRfxCreateRfxBid.do")
	public @ResponseBody ResultMap checkRfxCreateRfxBid(@RequestBody Map param){
		return rfxBidService.checkRfxPreInspCreateRfxBid(param);
	}
	
	@RequestMapping(value = "findRfxNpeFact.do")
	public @ResponseBody Map findRfxNpeFact(@RequestBody Map param){
		return rfxEvalService.findRfxNpeFact(param);
	}
	
	@RequestMapping(value = "findListEvalTmpl.do")
	public @ResponseBody List findListEvalTmpl(@RequestBody Map param) {
		return rfxEvalService.findListEvalTmpl(param);
	}
	
	@RequestMapping(value = "findListPreNonPriRfxDetail.do")
	public @ResponseBody List findListPreNonPriRfxDetail(@RequestBody Map param) {
		return rfxEvalService.findListPreNonPriRfxDetail(param);
	}
	
	@RequestMapping(value = "saveRfxNpeFact.do")
	public @ResponseBody ResultMap saveRfxNpeFact(@RequestBody Map param) {
		return rfxEvalService.saveRfxNpeFact(param);
	}
	
	@RequestMapping(value = "confirmRfxNpeFact.do")
	public @ResponseBody ResultMap confirmRfxNpeFact(@RequestBody Map param) {
		return rfxEvalService.confirmRfxNpeFact(param);
	}
	
	@RequestMapping(value = "cancelConfirmRfxNpeFact.do")
	public @ResponseBody ResultMap cancelConfirmRfxNpeFact(@RequestBody Map param) {
		return rfxEvalService.cancelConfirmRfxNpeFact(param);
	}
	
	@RequestMapping(value = "deleteRfxNpeFact.do")
	public @ResponseBody ResultMap deleteRfxNpeFact(@RequestBody Map param) {
		return rfxEvalService.clearRfxNpeFact(param);
	}
	
	@RequestMapping(value = "findListNpePerform.do")
	public @ResponseBody List findListNpePerform(@RequestBody Map param) {
		return rfxEvalService.findListNpePerform(param);
	}
	
	@RequestMapping(value = "findNpeEvalSubjectInfo.do")
	public @ResponseBody ResultMap findNpeEvalSubjectInfo(@RequestBody Map param) {
		return rfxEvalService.findNpeEvalSubjectInfo(param);
	}

	@RequestMapping(value = "findNpeEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findNpeEvalfactFulfillInfo(@RequestBody Map param) {
		return rfxEvalService.findNpeEvalfactFulfillInfo(param);
	}

	@RequestMapping(value = "saveNpeEvalFulfillment.do")
	public @ResponseBody ResultMap saveNpeEvalFulfillment(@RequestBody Map param) {
		return rfxEvalService.saveNpeEvalFulfillment(param);
	}

	@RequestMapping(value = "completeNpeEval.do")
	public @ResponseBody ResultMap completeNpeEval(@RequestBody Map param) {
		return rfxService.completeNpeEval(param);
	}

	@RequestMapping(value = "findListCompareNpeEvalfactEvaltr.do")
	public @ResponseBody Map findListCompareNpeEvalfactEvaltr(@RequestBody Map param) {
		return rfxEvalService.findListCompareNpeEvalfactEvaltr(param);
	}

	@RequestMapping(value = "findRfxBidForBidEffective.do")
	public @ResponseBody Map findRfxBidForBidEffective(@RequestBody Map param) {
		return rfxBidService.findRfxBidForBidEffective(param);
	}

	/**
	 * RFI -> RFX
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findRfxDefaultDataByRfiId.do")
	public @ResponseBody Map findRfxDefaultDataByRfiId(@RequestBody Map param){
		return rfxService.findRfxDefaultDataByRfiId(param);
	}

	/**
	 * 평가대상 평가자 아이디로 npe eval 정보 확인 (workplace link 용)
	 * @param param the param {eval_subj_evaltr_res_uuid : xxx}
	 * @return the map {rfx_uuid : xxx, npe_sts_ccd : xxx} npe 평가 정보 조회에 필요한 데이터
	 * */
	@RequestMapping(value = "findNpeEvalByEvalSubjEvaltrResId.do")
	public @ResponseBody Map findNpeEvalByEvalSubjEvaltrResId(@RequestBody Map param) {
		return rfxEvalService.findNpeEvalByEvalSubjEvaltrResId(param);
	}
	
	@RequestMapping(value = "findListRfxSlctnVd.do")
	public @ResponseBody List findListRfxSlctnVd(@RequestBody Map param) {
		return rfxService.findListRfxSlctnVd(param);
	}
	
	@RequestMapping(value = "saveRfxSlctnNxtPrcs.do")
	public @ResponseBody ResultMap saveRfxSlctnNxtPrcs(@RequestBody Map param) {
		return rfxService.saveRfxSlctnNxtPrcs(param);
	}
	
	@RequestMapping(value = "cancelSlctnRfx.do")
	public @ResponseBody ResultMap cancelSlctnRfx(@RequestBody Map param) {
		return rfxService.cancelSlctnRfx(param);
	}
	
	@RequestMapping(value = "findListRfxResult.do")
	public @ResponseBody List findListRfxResult(@RequestBody Map param) {
		return rfxService.findListRfxResult(param);
	}
}