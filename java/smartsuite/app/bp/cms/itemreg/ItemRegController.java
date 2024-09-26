package smartsuite.app.bp.cms.itemreg;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.cms.cmsCommon.service.CmsCommonService;
import smartsuite.app.bp.cms.itemreg.service.ItemRegService;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 품목 등록 관련 controller
 */
@Controller
@RequestMapping(value = "**/itemreg/**/")
public class ItemRegController {

    @Inject
    ItemRegService itemRegService;

    @Inject
    CmsCommonService cmsCommonService;

    /**
     * 품목 등록 관리 조회
     *
     * @param
     * @return the FloaterStream
     */
    @RequestMapping(value = "findListItemReg.do")
    public @ResponseBody FloaterStream findListItemReg(@RequestBody Map<String, Object> param) {
        return itemRegService.findListItemReg(param);
    }

    /**
     * 품목 등록 관리 상세 조회
     *
     * @param
     * @return the map
     */
    @RequestMapping(value = "findInfoAllItemReg.do")
    public @ResponseBody Map<String, Object> findInfoAllItemReg(@RequestBody Map<String, Object> param) {
        return itemRegService.findInfoAllItemReg(param);
    }

    /**
     * 품목 등록 관리 배정 속성 조회
     *
     * @param
     * @return the list
     */
    @RequestMapping(value = "findListItemAsgnAttrByItemreg.do")
    public @ResponseBody List<Map<String, Object>> findListItemAsgnAttrByItemreg(@RequestBody Map<String, Object> param) {
        return cmsCommonService.findListItemAsgnAttr(param);
    }

    /**
     * 품목 등록 관리 저장
     *
     * @param
     * @return the list
     */
    @RequestMapping(value = "saveInfoItemReg.do")
    public @ResponseBody ResultMap saveInfoItemReg(@RequestBody Map<String, Object> param) {
        return itemRegService.saveInfoItemReg(param);
    }

    /**
     * Item-doctor 연동 유사도 조회
     *
     * @param
     * @return the resultmap
     */
    @RequestMapping(value = "findListItemSimilarity.do")
    public @ResponseBody ResultMap findListItemSimilarity(@RequestBody Map<String, Object> param) {
        return cmsCommonService.findListItemSimilarity(param);
    }
}