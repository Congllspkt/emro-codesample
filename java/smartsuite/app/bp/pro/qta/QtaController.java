package smartsuite.app.bp.pro.qta;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.mybatis.data.impl.PageResult;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="**/bp/pro/**/")
public class QtaController {

    @Inject
    private QtaService qtaService;

    @Inject
    private QtaItemService qtaItemService;


    @RequestMapping("findListQtaItem.do")
    public @ResponseBody PageResult findListQtaItem(@RequestBody Map<String, Object> param){
        List result = qtaItemService.findListQtaItem(param);
        return new PageResult(result);
    }

    @RequestMapping("findListQta.do")
    public @ResponseBody List<Map<String,Object>> findListQta(@RequestBody Map<String, Object> param){
        return qtaService.findListQta(param);
    }

    @RequestMapping("saveDraftQta.do")
    public @ResponseBody ResultMap saveDraftQta(@RequestBody Map<String, Object> param){
        return qtaService.saveDraftQta(param);
    }

    @RequestMapping("findQta.do")
    public @ResponseBody Map<String, Object> findQta(@RequestBody Map<String, Object> param){
        return qtaService.findQta(param);
    }

    @RequestMapping("directQta.do")
    public @ResponseBody ResultMap directQta(@RequestBody Map<String, Object> param){
        return qtaService.directQta(param);
    }

    @RequestMapping("deleteListQta.do")
    public @ResponseBody ResultMap deleteListQta(@RequestBody Map<String, Object> param){
        return qtaService.deleteListQta(param);
    }

    @RequestMapping("findListQtaItemByItemCd.do")
    public @ResponseBody List<Map<String,Object>> findListQtaItemByItemCd(@RequestBody Map<String, Object> param){
        return qtaItemService.findListQtaItemByItemCd(param);
    }
    @RequestMapping("checkValidateCreatableQtaItem.do")
    public @ResponseBody ResultMap checkValidateCreatableQtaItem(@RequestBody Map<String, Object> param){
        return qtaItemService.checkValidateCreatableQtaItem(param);
    }
    
    @RequestMapping(value = "deleteQta.do")
    public @ResponseBody ResultMap deleteQta(@RequestBody Map<String, Object> param){
        return qtaService.deleteQta(param);
    }
}
