package smartsuite.app.bp.srm.performance.request;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.srm.performance.request.service.PerformanceEvalRequestService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.annotation.AuthCheck;

import javax.inject.Inject;
import java.util.Map;
import java.util.List;

/**
 * Performance Request 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author hj.jang
 * @see
 * @since 2023. 06. 10
 * @FileName PerformanceEvalRequestController.java
 * @package smartsuite.app.bp.performance.req
 * @변경이력 : [2023. 6. 10] hj.jang 최초작성
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/performance/**/")
public class PerformanceEvalRequestController {

	@Inject
	PerformanceEvalRequestService pfmcEvalReqService;


	/**
	 * 퍼포먼스평가 요청 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 10
	 * @Method Name : findReqInfo
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findPfmcEvalReq.do")
	public @ResponseBody Map findPfmcEvalReq(@RequestBody Map param){

		return pfmcEvalReqService.findPfmcEvalReq(param);
	}

	/**
	 * 퍼포먼스평가 평가그룹 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the List
	 * @Date : 2023. 6. 10
	 * @Method Name : findListPfmcEvalPeg
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPfmcEvalPeg.do")
	public @ResponseBody List findListPfmcEvalPeg(@RequestBody Map param){
		return pfmcEvalReqService.findListPfmcEvalPeg(param);
	}
	
	/**
	 * 퍼포먼스평가 요청 정보를 저장한다.
	 *
	 * @author : hj.jang
	 * @param : pfmcEvalReq 평가요청 정보
	 * @param : pfmcPegList 평가요청 대상되는 평가그룹 목록
	 * @return the map
	 * @Date : 2023. 6. 10
	 * @Method Name : savePfmcReq
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "savePfmcEvalReq.do")
	public @ResponseBody ResultMap savePfmcEvalReq(@RequestBody Map param){
		return pfmcEvalReqService.savePfmcEvalReq(param);
	}

	/**
	 * 퍼포먼스평가 평가그룹을 조회한다.
	 *
	 * @author : hj.jang
	 * @param : param 평가요청 평가그룹
	 * @return the map
	 * @Date : 2023. 6. 10
	 * @Method Name : findPfmcReqPegInfo
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findPfmcReqPegInfo.do")
	public @ResponseBody Map findPfmcReqPegInfo(@RequestBody Map param){
		return pfmcEvalReqService.findPfmcReqPegInfo(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상을 조회한다.
	 *
	 * @author : hj.jang
	 * @param : param 퍼포먼스평가 요청대상 조회 파라미터
	 * @return the list
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPeSubjByPePeg
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeSubjByPePeg.do")
	public @ResponseBody List findListPeSubjByPePeg(@RequestBody Map param){
		return pfmcEvalReqService.findListPeSubjByPePeg(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상을 저장한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청 정보
	 * @param : param pegInfo 퍼포먼스 평가그룹 정보
	 * @param : param pfmcEvalshtInfo 퍼포먼스 평가그룹 평가시트 정보
	 * @param : param savePeSubjList 저장할 퍼포먼스평가 대상 목록
	 * @return the ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : savePeSubj
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "savePeSubj.do")
	public @ResponseBody ResultMap savePeSubj(@RequestBody Map param){
		return pfmcEvalReqService.savePeSubj(param);
	}


	/**
	 * 퍼포먼스평가 평가대상 평가자 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param : param 퍼포먼스평가 평가대상 평가자 조회 파라미터
	 * @return the list
	 * @Date : 2023. 6. 23
	 * @Method Name : findListPeSubjEvaltr
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPeSubjEvaltr.do")
	public @ResponseBody List findListPeSubjEvaltr(@RequestBody Map param){
		return pfmcEvalReqService.findListPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상 평가자를 저장한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청 정보
	 * @param : param pegInfo 퍼포먼스 평가그룹 정보
	 * @param : param pfmcEvalshtInfo 퍼포먼스 평가그룹 평가시트 정보
	 * @param : param insertPeSubjEvaltrList 신규 생성할 퍼포먼스평가 대상 평가자 목록
	 * @param : param udpatePeSubjEvaltrList 수정할 퍼포먼스평가 대상 평가자 목록
	 * @param : param deletePeSubjEvaltrList 삭제할 퍼포먼스평가 대상 평가자 목록
	 * @return the ResultMap
	 * @Date : 2023. 6. 23
	 * @Method Name : savePeSubjEvaltr
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "savePeSubjEvaltr.do")
	public @ResponseBody ResultMap savePeSubjEvaltr(@RequestBody Map param){
		return pfmcEvalReqService.savePeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스평가를 요청한다.
	 *
	 * @author : hj.jang
	 * @param : param pfmcEvalReq 퍼포먼스평가 요청
	 * @return the ResultMap
	 * @Date : 2023. 6. 25
	 * @Method Name : requestPfmcEval
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "requestPfmcEval.do")
	public @ResponseBody ResultMap requestPfmcEval(@RequestBody Map param){
		return pfmcEvalReqService.requestPfmcEval(param);
	}

	/**
	 * 퍼포먼스평가 요청 목록 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 29
	 * @Method Name : findListPfmcEvalPrgs
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPfmcEvalPrgs.do")
	public @ResponseBody List findListPfmcEvalPrgs(@RequestBody Map param){

		return pfmcEvalReqService.findListPfmcEvalPrgs(param);
	}

	/**
	 * 퍼포먼스평가 요청 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : findPfmcEvalPrgsInfo
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findPfmcEvalPrgsInfo.do")
	public @ResponseBody Map findPfmcEvalPrgsInfo(@RequestBody Map param){

		return pfmcEvalReqService.findPfmcEvalPrgsInfo(param);
	}

	/**
	 * 퍼포먼스평가 요청 대상 별 담당자 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 6. 30
	 * @Method Name : findListPrgsPeSubjEvaltr
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPrgsPeSubjEvaltr.do")
	public @ResponseBody List findListPrgsPeSubjEvaltr(@RequestBody Map param){

		return pfmcEvalReqService.findListPrgsPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스평가 진행관리 : 퍼포먼스평가 요청 대상 추가 목록을 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 2
	 * @Method Name : findListPrgsPeSubjAdd
	 */
	@AuthCheck(authCode= Const.READ)
	@RequestMapping(value = "findListPrgsPeSubjAdd.do")
	public @ResponseBody List findListPrgsPeSubjAdd(@RequestBody Map param){

		return pfmcEvalReqService.findListPrgsPeSubjAdd(param);
	}
	/**
	 * 퍼포먼스평가 진행관리 : 퍼포먼스평가 요청 대상을 추가적으로 저장한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 2
	 * @Method Name : savePrgsPeSubjAdd
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "savePrgsPeSubjAdd.do")
	public @ResponseBody ResultMap savePrgsPeSubjAdd(@RequestBody Map param){
		return pfmcEvalReqService.savePrgsPeSubjAdd(param);
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스평가 요청 대상 별 담당자 정보를 조회한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 5
	 * @Method Name : deleteListPrgsPeSubj
	 */
	@AuthCheck(authCode= Const.SAVE)
	@RequestMapping(value = "deleteListPrgsPeSubj.do")
	public @ResponseBody ResultMap deleteListPrgsPeSubj(@RequestBody Map param){

		return pfmcEvalReqService.deleteListPrgsPeSubj(param);
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 신규 저장을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : saveListPrgsPeSubjEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveListPrgsPeSubjEvaltr.do")
	public @ResponseBody ResultMap saveListPrgsPeSubjEvaltr(final @RequestBody Map param) {
		return pfmcEvalReqService.saveListPrgsPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 변경을 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : saveChangedPrgsPeSubjEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "saveChangedPrgsPeSubjEvaltr.do")
	public @ResponseBody ResultMap saveChangedPrgsPeSubjEvaltr(final @RequestBody Map param) {
		return pfmcEvalReqService.saveChangedPrgsPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스 진행관리 : 퍼포먼스 평가대상 평가자 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 07. 05
	 * @Method Name : deleteListPrgsPeSubjEvaltr
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "deleteListPrgsPeSubjEvaltr.do")
	public @ResponseBody ResultMap deleteListPrgsPeSubjEvaltr(final @RequestBody Map param) {
		return pfmcEvalReqService.deleteListPrgsPeSubjEvaltr(param);
	}

	/**
	 * 퍼포먼스 요청 상세 : 퍼포먼스 평가 삭제를 요청한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 07. 08
	 * @Method Name : deletePfmcEval
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "deletePfmcEval.do")
	public @ResponseBody ResultMap deletePfmcEval(final @RequestBody Map param) {
		return pfmcEvalReqService.deletePfmcEval(param);
	}

	/**
	 * 퍼포먼스 요청 상세 : 퍼포먼스 평가 요청을 취소한다.
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 08. 08
	 * @Method Name : cancelRequestPfmcEval
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "cancelRequestPfmcEval.do")
	public @ResponseBody ResultMap cancelRequestPfmcEval(final @RequestBody Map param) {
		return pfmcEvalReqService.cancelRequestPfmcEval(param);
	}

	/**
	 * 퍼포먼스 요청 시  요청 가능 상태인지 확인한다. (퍼포먼스 평가 요청정보 유효성 검사)
	 *
	 * @author : hj.jang
	 * @param param the param
	 * @return the ResultMap
	 * @Date : 2023. 08. 09
	 * @Method Name : checkValidStateRequestPfmcEval
	 */
	@AuthCheck (authCode = Const.SAVE)
	@RequestMapping (value = "checkValidStateRequestPfmcEval.do")
	public @ResponseBody ResultMap checkValidStateRequestPfmcEval(final @RequestBody Map param) {
		return pfmcEvalReqService.checkValidStateRequestPfmcEval(param);
	}
}
