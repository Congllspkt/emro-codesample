package smartsuite.app.bp.pro.asn;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.pro.asn.service.DlvySchedService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

/**
 * 납품 일정 관련 처리를 하는 컨트롤러
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/bp/pro/asn/**/")
public class DlvySchedController {

    @Inject
    DlvySchedService dlvySchedService;

    /**
     * 구매사 납품 일정 정보 리스트 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value="findDlvySchedByPoItemUuids.do")
    public @ResponseBody ResultMap findDlvySchedByPoItemUuids(@RequestBody Map param){
        return dlvySchedService.findDlvySchedByPoItemUuids(param);
    }

    /**
     * 구매사 납품 일정 정보 단일 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "findDlvySchedByDlvySchedUuid.do")
    public @ResponseBody ResultMap findDlvySchedByDlvySchedUuid(@RequestBody Map<String, Object> param){
        return dlvySchedService.findDlvySchedByDlvySchedUuid(param);
    }

    /**
     * 구매사 납품 일정 변경 요청 리스트 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value="findDlvySchedChgReqByDlvySchedUuids.do")
    public @ResponseBody ResultMap findDlvySchedChgReqByDlvySchedUuids(@RequestBody Map<String, Object> param){
        return dlvySchedService.findDlvySchedChgReqByDlvySchedUuids(param);
    }

    /**
     * 구매사 납품 일정 변경 요청 단일 조회
     *
     * @param param
     * @return
     */
    @RequestMapping(value="findDlvySchedChgReqDetail.do")
    public @ResponseBody ResultMap findDlvySchedChgReqDetail(@RequestBody Map<String, Object> param){
        return dlvySchedService.findDlvySchedChgReqDetail(param);
    }

    /**
     * 구매사 납품 일정 변경 요청 등록
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "saveDlvySchedChgReq.do")
    public @ResponseBody ResultMap saveDlvySchedChgReq(@RequestBody Map<String, Object> param){
        return dlvySchedService.saveDlvySchedChgReq(param);
    }

}