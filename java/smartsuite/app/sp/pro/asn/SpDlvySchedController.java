package smartsuite.app.sp.pro.asn;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.asn.service.SpDlvySchedService;

/**
 * 업체 납품 일정 관련 처리를 하는 컨트롤러
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/sp/pro/asn/**/")
public class SpDlvySchedController {

    @Autowired
    private SpDlvySchedService spDlvySchedService;

    /**
     * 협력사 납품 일정 등록
     *
     * @param param (created, updated, deleted)
     * @return
     */
    @RequestMapping(value = "saveDraftDlvySched.do")
    public @ResponseBody ResultMap saveDraftDlvySched(@RequestBody Map<String, Object> param) {
        return spDlvySchedService.saveDraftDlvySched(param);
    }

    /**
     * 협력사 납품 일정 리스트 조회
     *
     * @param param (po_item_uuids)
     * @return
     */
    @RequestMapping(value = "findDlvySchedByPoItemUuids.do")
    public @ResponseBody ResultMap findDlvySchedByPoItemUuids(@RequestBody Map<String, Object> param){
        return spDlvySchedService.findDlvySchedByPoItemUuids(param);
    }

    /**
     * 협력사 납품 일정 변경 요청 리스트 조회
     *
     * @param param (po_item_uuids)
     * @return
     */
    @RequestMapping(value="findDlvySchedChgReqByDlvySchedUuids.do")
    public @ResponseBody ResultMap findDlvySchedChgReqByDlvySchedUuids(@RequestBody Map<String, Object> param){
        return spDlvySchedService.findDlvySchedChgReqByDlvySchedUuids(param);
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 및 상세 조회
     *
     * @param param (dlvy_sched_chg_req_uuid, po_item_uuids)
     * @return
     */
    @RequestMapping(value="findDlvySchedChgReqDetail.do")
    public @ResponseBody ResultMap findDlvySchedChgReqDetail(@RequestBody Map<String, Object> param){
        return spDlvySchedService.findDlvySchedChgReqDetail(param);
    }

    /**
     * 협력사 납품 일정 상태, 납품 일정 변경 요청 상태 수정
     *
     * @param param (dlvy_sched_uuid)
     * @return
     */
    @RequestMapping(value="updateDlvySchedSts.do")
    public @ResponseBody ResultMap updateDlvySchedSts(@RequestBody Map<String, Object> param){
        return spDlvySchedService.updateDlvySchedSts(param);
    }

    /**
     * 협력사 납품 일정 변경 요청 최종값 등록 (수정)
     *
     * @param param (dlvy_sched_uuid)
     * @return
     */
    @RequestMapping(value="updateDlvyScheds.do")
    public @ResponseBody ResultMap updateDlvyScheds(@RequestBody Map<String, Object> param){
        return spDlvySchedService.updateDlvyScheds(param);
    }

    /**
     * 협력사 납품 일정 상제
     *
     * @param param (dlvy_sched_uuids)
     * @return
     */
    @RequestMapping(value="removeDlvyScheds.do")
    public @ResponseBody ResultMap removeDlvyScheds(@RequestBody Map<String, Object> param){
        return spDlvySchedService.removeDlvyScheds(param);
    }
}