package smartsuite.app.sp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.po.service.SpPoService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

/**
 * Po 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @author JuEung Kim
 * @see
 * @since 2016. 5. 27
 * @FileName PoController.java
 * @package smartsuite.app.sp.pro.po
 * @변경이력 : [2016. 5. 27] JuEung Kim 최초작성
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping (value = "**/sp/pro/po/**/")
public class SpPoController {

	/** The po service. */
	@Inject
    SpPoService spPoService;

	/**
	 * PO 품목 목록 조회를 요청한다.
	 *
	 * @author : JuEung Kim
	 * @param param the param
	 * @return the list
	 * @Date : 2016. 5. 27
	 * @Method Name : findListPoItem
	 */
	@RequestMapping (value = "findListPsPoItem.do")
	public @ResponseBody FloaterStream findListPsPoItem(@RequestBody Map param) {
		param.put("purc_typ_ccd", "QTY"); // 구매 유형 공통코드 물품

		// 대용량 처리
		return spPoService.findListPoItem(param);
	}

	/**
	 * PO 목록 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the po list
	 * @Date : 2016. 2. 2
	 * @Method Name : findListPo
	 */
	@RequestMapping (value = "findListSpPo.do")
	public @ResponseBody FloaterStream findListSpPo(@RequestBody Map param) {
		// 대용량 처리
		return spPoService.findListPo(param);
	}

	/**
	 * PO 상세 조회를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the po
	 * @Date : 2016. 2. 2
	 * @Method Name : findPo
	 */
	@RequestMapping (value = "findSpPo.do")
	public @ResponseBody Map findSpPo(@RequestBody Map param) {
		return spPoService.findPo(param);
	}

	/**
	 * PO(복수건) 접수처리를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : acceptSpPos
	 */
	@RequestMapping (value = "acceptSpPos.do")
	public @ResponseBody ResultMap acceptSpPos(@RequestBody Map param) {
		return spPoService.acceptPos(param);
	}

	/**
	 * PO(단건) 접수처리를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : acceptSpPo
	 */
	@RequestMapping (value = "acceptSpPo.do")
	public @ResponseBody ResultMap acceptSpPo(@RequestBody Map param) {
		return spPoService.acceptPo(param);
	}
	
	/**
	 * PO(복수건) 거부처리를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectSpPos
	 */
	@RequestMapping (value = "rejectSpPos.do")
	public @ResponseBody ResultMap rejectSpPos(@RequestBody Map param) {
		return spPoService.rejectPos(param);
	}
	
	/**
	 * PO(단건) 거부처리를 요청한다.
	 *
	 * @author : JongKyu Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 2. 2
	 * @Method Name : rejectSpPo
	 */
	@RequestMapping (value = "rejectSpPo.do")
	public @ResponseBody ResultMap rejectSpPo(@RequestBody Map param) {
		return spPoService.rejectPo(param);
	}
	
	/**
	 * 문서 출력물을 위한 spPo정보(단건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoDocumentOutputSpPo.do")
	public @ResponseBody ResultMap findInfoDocumentOutputSpPo(@RequestBody Map param){
		return spPoService.findInfoDocumentOutputSpPo(param);
	}
	
	/**
	 * 문서 출력물을 위한 spPo정보(복수건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListDocumentOutputSpPo.do")
	public @ResponseBody ResultMap findListDocumentOutputSpPo(@RequestBody Map<String, Object> param){
		return spPoService.findListDocumentOutputSpPo(param);
	}

	@RequestMapping(value = "findPoItemByPoUuid.do")
	public @ResponseBody ResultMap findPoItemByPoUuid(@RequestBody Map param){
		return spPoService.findPoItemByPoUuid(param);
	}
}