package smartsuite.app.sp.pro.asn;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.asn.service.SpAsnItemService;
import smartsuite.app.sp.pro.asn.service.SpAsnService;
import smartsuite.data.FloaterStream;

/**
 * 업체 납품예정 관련 처리를 하는 컨트롤러
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/sp/pro/asn/**/")
public class SpAsnController {

    @Inject
    private SpAsnService spAsnService;
    @Autowired
    private SpAsnItemService spAsnItemService;

    /**
     * 납품예정 목록 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "searchAsn.do")
    public @ResponseBody FloaterStream searchAsn(@RequestBody Map param) {
        // 대용량 처리
        return spAsnService.searchAsn(param);
    }

    /**
     * 납품예정 생성가능한지 검증
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "validateAdvancedShippingNoticeCreatable.do")
    public @ResponseBody ResultMap validateAdvancedShippingNoticeCreatable(@RequestBody Map param) {
        return spAsnService.validateAdvancedShippingNoticeCreatable(param);
    }

    /**
     * 납품예정 임시저장
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "saveDraftAsn.do")
    public @ResponseBody ResultMap saveDraftAsn(@RequestBody Map param) {
        return spAsnService.saveDraftAsn(param);
    }

    /**
     * 납품예정 제출
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "submitAsn.do")
    public @ResponseBody ResultMap submitAsn(@RequestBody Map param) {
        return spAsnService.submitAsn(param);
    }

    /**
     * 납품예정 삭제
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "deleteAsn.do")
    public @ResponseBody ResultMap deleteAsn(@RequestBody Map param) {
        return spAsnService.deleteAsn(param);
    }

    /**
     * 납품예정 데이터와 품목 데이터를 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findAsn.do")
    public @ResponseBody ResultMap findAsn(@RequestBody Map param) {
        return spAsnService.findAsn(param);
    }

    /**
     * poItem으로 신규 납품예정 데이터 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findInitAsnByPoItem.do")
    public @ResponseBody ResultMap findInitAsnByPoItem(@RequestBody Map param) {
        return spAsnService.findInitAsnByPoItem(param);
    }
    
    /**
	 * 문서 출력물을 위한 spAsn정보(단건) 조회 
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findInfoDocumentOutputSpAsn.do")
	public @ResponseBody ResultMap findInfoDocumentOutputSpAsn(@RequestBody Map<String, Object> param){
		return spAsnService.findInfoDocumentOutputSpAsn(param);
	}
    
    /**
	 * 문서 출력물을 위한 spAsn정보(복수건) 조회
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListDocumentOutputSpAsn.do")
	public @ResponseBody ResultMap findListDocumentOutputSpAsn(@RequestBody Map<String, Object> param){
		return spAsnService.findListDocumentOutputSpAsn(param);
	}
}