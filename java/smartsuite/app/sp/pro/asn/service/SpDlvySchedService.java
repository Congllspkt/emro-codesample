package smartsuite.app.sp.pro.asn.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.pro.asn.repository.SpDlvySchedRepository;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 업체 납품 일정 관련 처리하는 서비스 Class입니다.
 *
 */
@Service
@Transactional
public class SpDlvySchedService {

    @Inject
    SpDlvySchedRepository spDlvySchedRepository;
    @Inject
    SpAsnItemService spAsnItemService;
    @Inject
    MailService mailService;

    /**
     * 협력사 납품 일정 등록
     *
     * @param param (created, updated, deleted)
     * @return
     */
    public ResultMap saveDraftDlvySched(Map<String, Object> param){

        Map<String, Object> userInfo = Auth.getCurrentUserInfo();

        List<Map<String, Object>> items = (List<Map<String, Object>>) param.get("items");
        List<Map<String, Object>> deleted = (List<Map<String, Object>>) param.get("deleted");
        String dlvy_sched_sts = (String) param.get("dlvy_sched_sts");


        List<Map<String, Object>> created = new ArrayList<>();
        List<Map<String, Object>> updated = new ArrayList<>();

        items.stream().forEach(item -> {
            if(item.get("dlvy_sched_sts").equals("CRNG")) {
                item.put("dlvy_sched_sts", dlvy_sched_sts);
            }
            if(item.get("dlvy_sched_uuid") == null) {
                created.add(item);
            } else {
                updated.add(item);
            }
        });

        if(!created.isEmpty()) {
            this.insertDlvySched(created,  userInfo);
        }
        if(!updated.isEmpty()) {
            this.updateDlvySched(updated, userInfo);
        }
        if(!deleted.isEmpty()) {
            this.deleteDlvySched(deleted);
        }
        if(dlvy_sched_sts.equals("CCMPLD")) {
            Map<String, Object> paramForMail = Maps.newHashMap();
            paramForMail.put("dlvyScheds", items);
            paramForMail.put("userInfo", userInfo);
            mailService.sendAsync("DLVY_SCHED_NOTI_MAIL", null, paramForMail);
        }

        return ResultMap.SUCCESS();
    }

    /**
     * 협력사 납품 일정 리스트 조회
     *
     * @param param (po_item_uuids)
     * @return
     */
    public ResultMap findDlvySchedByPoItemUuids(Map<String, Object> param) {
        List<Map<String, Object>> asnItemList = spAsnItemService.searchAsnItemByPoItem(param);
        List<Map<String, Object>> dlvySchedList = spDlvySchedRepository.findDlvySchedByPoItemUuids(param);

        return ResultMap.builder().resultData(this.makeAsnSchedResultData(asnItemList, dlvySchedList)).build();
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 및 상세 조회
     *
     * @param param (dlvy_sched_chg_req_uuid, po_item_uuids)
     * @return
     */
    public ResultMap findDlvySchedChgReqDetail(Map<String, Object> param) {
        List<Map<String, Object>> ret = (List<Map<String, Object>>) spDlvySchedRepository.findDlvySchedChgRetItems(param);
        Map<String, Object> dlvyChgReq = (Map<String, Object>) spDlvySchedRepository.findOneDlvySchedChgReq(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("asnSchedItems", ret.stream().map(item -> {
            if(item.get("fnl_qty") == null) {
                if(item.get("req_qty") == null) {
                    if(item.get("asis_qty") != null) {
                        item.put("fnl_qty", new BigDecimal(String.valueOf(item.get("asis_qty"))));
                    }
                } else {
                    item.put("fnl_qty", new BigDecimal(String.valueOf(item.get("req_qty"))));
                }
            }
            if(item.get("fnl_dt") == null) {
                if(item.get("req_dt") == null) {
                    if(item.get("asis_dt") != null) {
                        item.put("fnl_dt", item.get("asis_dt"));
                    }
                } else {
                    item.put("fnl_dt", item.get("req_dt"));
                }
            }
            return item;
        }).collect(Collectors.toList()));
        resultMap.put("dlvyChgReq", dlvyChgReq);

        return ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 협력사 납품 일정 변경 요청 리스트 조회
     *
     * @param param (po_item_uuids)
     * @return
     */
    public ResultMap findDlvySchedChgReqByDlvySchedUuids(Map<String, Object> param) {
        List<Map<String, Object>> ret = spDlvySchedRepository.findDlvySchedChgReqByDlvySchedUuids(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dlvy_chg_req_items", ret);

        return  ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 협력사 납품 일정 상태, 납품 일정 변경 요청 상태 수정
     *
     * @param param (dlvy_sched_uuid)
     * @return
     */
    public ResultMap updateDlvySchedSts(Map<String, Object> param) {
        this.updateDlvySchedChgReqSts(param);

        return ResultMap.SUCCESS();
    }

    /**
     * 협력사 납품 일정 변경 요청 최종값 등록 (수정)
     *
     * @param param (dlvy_sched_uuid)
     * @return
     */
    public ResultMap updateDlvyScheds(Map<String, Object> param){
        Map<String, Object> userInfo = Auth.getCurrentUserInfo();
        List<Map<String, Object>> updated = (List<Map<String, Object>>) param.get("updated");

        updated.stream().forEach(item -> {
            if(item.get("fnl_qty") == null) {
                BigDecimal dlvyQty = new BigDecimal(item.get("asis_qty").toString());
                item.put("fnl_qty", dlvyQty);
                item.put("dlvy_qty", dlvyQty);
            } else {
                item.put("dlvy_qty", new BigDecimal(item.get("fnl_qty").toString()));
            }

            if(item.get("fnl_dt") == null) {
                item.put("fnl_dt", item.get("asis_dt"));
                item.put("dlvy_dt", item.get("asis_dt"));
            } else {
                item.put("dlvy_dt", item.get("fnl_dt"));
            }

        });

        this.updateDlvySchedChgPoItem(updated, userInfo);
        this.updateDlvySched(updated, userInfo);
        this.updateDlvySchedChgReqSts(param);

        Map<String, Object> paramForMail = Maps.newHashMap();
        paramForMail.put("dlvyScheds", updated);
        paramForMail.put("userInfo", userInfo);

        mailService.sendAsync("DLVY_SCHED_NOTI_MAIL", null, paramForMail);

        return ResultMap.SUCCESS();
    }

    /**
     * 협력사 납품 일정 상제 (삭제예정)
     *
     * @param param (dlvy_sched_uuids)
     * @return
     */
    public ResultMap removeDlvyScheds(Map<String, Object> param) {
        this.deleteDlvySched((List<Map<String, Object>>) param.get("deleted"));

        return ResultMap.SUCCESS();
    }

    /**
     * 협력사 납품 일정 등록
     *
     */
    private void insertDlvySched(List<Map<String, Object>> created, Map<String, Object> userInfo) {
        // 디테일 INSERT
        if (created != null && !created.isEmpty()) {
            for (Map<String, Object> row : created) {
                row.put("dlvy_sched_uuid", UUID.randomUUID().toString());
                row.put("regr_id", userInfo.get("usr_id"));

                spDlvySchedRepository.insertDlvySched(row);
            }
        }
    }

    /**
     * 협력사 납품 일정 변경 요청 품목 최종값 등록 (수정)
     *
     */
    private void updateDlvySchedChgPoItem(List<Map<String, Object>> updated, Map<String, Object> userInfo) {
        if (updated != null && !updated.isEmpty()) {
            for (Map<String, Object> row : updated) {
                row.put("modr_id", userInfo.get("usr_id"));
                row.put("chg_cmpld_yn", 'Y');
                spDlvySchedRepository.updateDlvySchedChgPoItem(row);
            }
        }
    }

    /**
     * 협력사 납품 일정 수정
     *
     */
    private void updateDlvySched(List<Map<String, Object>> updated, Map<String, Object> userInfo) {
        if (updated != null && !updated.isEmpty()) {
            for (Map<String, Object> row : updated) {
                row.put("modr_id", userInfo.get("usr_id"));
                spDlvySchedRepository.updateDlvySched(row);
            }
        }
    }

    /**
     * 협력사 납품 일정 변경 요청 상태 수정
     *
     */
    private void updateDlvySchedChgReqSts(Map<String, Object> param) {
        if(!param.get("dlvy_sched_chg_req_sts").equals("CHG_CMPLD") || spDlvySchedRepository.checkChgReqCmpld(param)) {
            Map<String, Object> tempParam = Maps.newHashMap();
            tempParam.put("dlvy_sched_chg_req_uuid", param.get("dlvy_sched_chg_req_uuid"));
            tempParam.put("dlvy_sched_chg_req_sts", param.get("dlvy_sched_chg_req_sts"));
            spDlvySchedRepository.updateDlvySchedChgReqSts(tempParam);
        }
    }

    /**
     * 협력사 납품 일정 삭제
     *
     */
    private void deleteDlvySched(List<Map<String, Object>> deleted) {
        if(deleted != null && !deleted.isEmpty()) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("deleted", deleted);
            spDlvySchedRepository.deleteDlvySched(param);
            spDlvySchedRepository.deleteDlvySchedChgPoItem(param);
        }
    }

    /**
     * 협력사 납품 일정 리스트 resultMap 변환
     *
     */
    private Map<String, Object> makeAsnSchedResultData(List<Map<String, Object>> AsnItemList, List<Map<String, Object>> dlvySchedList) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("asnItemList", AsnItemList);
        resultMap.put("dlvySchedList", dlvySchedList);

        return resultMap;
    }

}
