package smartsuite.app.bp.pro.asn;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.asn.service.AsnService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 납품예정 관련 처리를 하는 컨트롤러
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/pro/asn/**/")
public class AsnController {
	
	@Inject
	AsnService asnService;
	
	/**
	 * 납품예정 목록 조회
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "searchAsn.do")
	public @ResponseBody FloaterStream searchAsn(@RequestBody Map param) {
		// 대용량 처리
		return asnService.searchAsn(param);
	}
	
	/**
	 * 납품예정(물품) 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the asn
	 * @author : JongKyu Kim
	 * @Date : 2016. 2. 2
	 * @Method Name : findAsn
	 */
	@RequestMapping(value = "findAsn.do")
	public @ResponseBody Map findAsn(@RequestBody Map param) {
		return asnService.findAsn(param);
	}
	
	/**
	 * 납품예정 반려 처리한다.
	 */
	@RequestMapping(value = "updateAsnReject.do")
	public @ResponseBody ResultMap updateAsnReject(@RequestBody Map param) {
		return asnService.updateAsnReject(param);
	}
	
	/**
	 * 문서 출력물을 위한 asn정보(단건) 조회 
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoDocumentOutputAsn.do")
	public @ResponseBody ResultMap findInfoDocumentOutputAsn(@RequestBody Map<String, Object> param){
		return asnService.findInfoDocumentOutputAsn(param);
	}
	
	/**
	 * 문서 출력물을 위한 asn정보(복수건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListDocumentOutputAsn.do")
	public @ResponseBody ResultMap findListDocumentOutputAsn(@RequestBody Map<String, Object> param){
		return asnService.findListDocumentOutputAsn(param);
	}
}