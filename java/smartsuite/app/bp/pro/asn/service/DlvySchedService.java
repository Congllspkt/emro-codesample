package smartsuite.app.bp.pro.asn.service;

import com.google.common.collect.Maps;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.pro.asn.repository.DlvySchedRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.security.authentication.Auth;

import javax.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 납품 일정 관련 처리하는 서비스 Class입니다.
 *
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class DlvySchedService {

    @Inject
    DlvySchedRepository dlvySchedRepository;
    @Inject
    MailService mailService;

    /**
     * 구매사 납품 일정 정보 리스트 조회
     *
     * @param param
     * @return
     */
    public ResultMap findDlvySchedByPoItemUuids(Map param) {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("schedItems", this.findListDlvySchedByPoItemUuids(param));
        return ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 구매사 납품 일정 정보 리스트 조회
     *
     * @param param
     * @return
     */
    public ResultMap findDlvySchedByDlvySchedUuid(Map<String, Object> param) {
        List<Map<String, Object>> dlvySchedList = dlvySchedRepository.findDlvySchedByDlvySchedUuid(param);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("schedItems", dlvySchedList);
        return ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 구매사 납품 일정 변경 요청 리스트 조회
     *
     */
    public ResultMap findDlvySchedChgReqByDlvySchedUuids(Map<String, Object> param) {
        List<Map<String, Object>> ret = dlvySchedRepository.findDlvySchedChgReqByDlvySchedUuids(param);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dlvyChgReqItems", ret);
        return ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 구매사 납품 일정 변경 요청 단일 조회
     *
     */
    public ResultMap findDlvySchedChgReqDetail(Map<String, Object> param) {
        List<Map<String, Object>> dlvySchedItems = dlvySchedRepository.findDlvySchedChgReqDetail(param);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dlvySchedItems", dlvySchedItems);
        return ResultMap.builder().resultData(resultMap).build();
    }

    /**
     * 구매사 납품 일정 변경 요청 등록
     *
     */
    public ResultMap saveDlvySchedChgReq(Map<String, Object> param) {
        // UUID for DLVY_SCHED_CHG_REQ
        String dlvySchedChgReqUuid = UUID.randomUUID().toString();
        param.put("dlvy_sched_chg_req_uuid", dlvySchedChgReqUuid);
        dlvySchedRepository.insertDlvySchedChgReq(param);

        List<Map<String, Object>> data = makeParamForSaveDlvySchedChgReq(param, dlvySchedChgReqUuid);
        data.stream().forEach(item -> {
            dlvySchedRepository.insertDlvySchedChgPoItem(item);
        });

        Map<String, Object> paramForStsChange = Maps.newHashMap();
        paramForStsChange.put(
                "dlvy_sched_uuids"
                , data.stream().map(map -> map.get("dlvy_sched_uuid"))
                        .filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        paramForStsChange.put("dlvy_sched_sts", "CHG_REQ"); // 변경 요청

        dlvySchedRepository.updateDlvySchedSts(paramForStsChange);

        mailService.sendAsync("DLVY_SCHED_CHG_REQ_NOTI_MAIL", null, param);
        return ResultMap.SUCCESS();
    }

    /**
     * 구매사 납품 일정 정보 리스트 조회 By PO_ITEM_UUID
     *
     */
    public List<Map<String, Object>> findListDlvySchedByPoItemUuids(Map param) {
        return dlvySchedRepository.findListDlvySchedByPoItemUuids(param);
    }

    /**
     * po_item_uuid 추출
     *
     */
    public Map<String, Object> makeParamForFindDlvySched (List<Map<String, Object>> param) {
        Map<String, Object> ret = new HashMap<>();
        List<Object> poItemUuids = new ArrayList<>();
        param.stream().forEach(map-> {
            poItemUuids.add(map.get("po_item_uuid"));
        });
        ret.put("po_item_uuids", poItemUuids);
        return ret;
    }

    /**
     * dlvy_sched_chg_req_uuid 추출
     *
     */
    private List<Map<String, Object>> makeParamForSaveDlvySchedChgReq (Map<String, Object> param, String dlvySchedChgReqUuid) {
        List<Map<String, Object>> tempParam = (List<Map<String, Object>>) param.get("items");
        return tempParam.stream().map(item -> {
            item.put("dlvy_sched_chg_req_uuid", dlvySchedChgReqUuid);
            return item;
        }).collect(Collectors.toList());
    }
}
