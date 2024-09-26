package smartsuite.app.bp.oauthapi;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smartsuite.app.common.glossary.service.GlossaryService;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/apitest")
public class OAuthApiTestController {

    @Inject
    GlossaryService glossaryService;

    @RequestMapping(value = "/ss10dic/search", method = RequestMethod.GET)
    public @ResponseBody List getSs10DicSearch(@RequestParam String logicNm) {
        Map<String,Object> param = Maps.newHashMap();
        param.put("logic_nm", logicNm);
        List<Map<String,Object>> resultList = glossaryService.findListGlossary(param);
        return resultList;
    }
}