package smartsuite.app.sp.pro.po;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.sp.pro.po.service.SpPoConsortiumService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.Map;

@Controller
@RequestMapping (value = "**/sp/pro/po/**/")
public class SpPoConsortiumController {

    @Inject
    SpPoConsortiumService spPoConsortiumService;
    /**
     * 컨소시엄 정보 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findSpPoCsData.do")
    public @ResponseBody Map findSpPoCsData(@RequestBody Map param){
        return spPoConsortiumService.findPoCsData(param);
    }

    /**
     * 컨소시엄 발주 현황 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findListSpPoByCsVd.do")
    public @ResponseBody FloaterStream findListSpPoByCsVd(@RequestBody Map param){
        return spPoConsortiumService.findListPoByCsVd(param);
    }
}
