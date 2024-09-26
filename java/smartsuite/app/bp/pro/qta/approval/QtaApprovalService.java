package smartsuite.app.bp.pro.qta.approval;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.approval.service.ApprovalStrategy;
import smartsuite.app.bp.pro.qta.QtaService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QtaApprovalService implements ApprovalStrategy {

    @Inject
    private SharedService sharedService;

    @Inject
    private QtaService qtaService;

    @Inject
    private ProStatusService proStatusProcessor;

    @Override
    public void doApprove(String approvalType, String appId) {
        Map param = Maps.newHashMap();
		param.put("qta_uuid", appId);
		proStatusProcessor.approveApprovalQta(param);
		qtaService.directQta(param);
    }

    @Override
    public void doReject(String approvalType, String appId) {
        Map param = Maps.newHashMap();
		param.put("qta_uuid", appId);
		proStatusProcessor.rejectApprovalQta(param);
    }

    @Override
    public void doCancel(String approvalType, String appId) {
        Map param = Maps.newHashMap();
		param.put("qta_uuid", appId);
		proStatusProcessor.cancelApprovalQta(param);
    }

    @Override
    public void doSubmit(String approvalType, String appId) {
        Map param = Maps.newHashMap();
		param.put("qta_uuid", appId);
		proStatusProcessor.submitApprovalQta(param);
    }

    @Override
    public void doTemporary(String approvalType, String appId) {
        //doTemporary
    }

    @Override
    public Map<String, Object> makeParam(String approvalType, String appId) {
        Map<String, Object> newParam = Maps.newHashMap();
        Map<String, Object> resultMap = Maps.newHashMap();

        newParam.put("qta_uuid", appId);

        Map<String, Object> qta = qtaService.findQta(newParam);
        if (qta != null) {
            List<Map<String, Object>> qtaItems = (List<Map<String, Object>>) qta.get("qtaItems");
            Map<String, Object> qtaInfo = (Map<String, Object>) qta.get("qtaData");
            //코드명으로 변환
            qtaInfo.put("oorg_cd_nm", sharedService.findOperationOrganizationName((String) qtaInfo.get("oorg_cd"), "PO"));        //구매운영조직

            resultMap.put("qtaInfo", qtaInfo);
            resultMap.put("items", qtaItems);
        }
        return resultMap;
    }
}
