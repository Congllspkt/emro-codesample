package smartsuite.app.bp.pro.qta;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.qta.repository.QtaRepository;
import smartsuite.app.bp.pro.qta.validator.QtaValidator;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.status.service.ProStatusService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class QtaService {

    @Inject
    private QtaRepository qtaRepository;

    @Inject
    private QtaItemService qtaItemService;

    @Inject
    private QtaItemHistrecService qtaItemHistrecService;

    @Inject
    private SharedService sharedService;

    @Inject
    private QtaValidator qtaValidator;

    @Inject
    private ProStatusService proStatusProcessor;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final static String QTA_STS_CCD_APVD = "APVD";

    public ResultMap saveDraftQta(Map param) {
		ResultMap resultMap = this.saveQta(param);
		if(!resultMap.isSuccess()) {
			return resultMap;
		}

		Map resultData = resultMap.getResultData();
		proStatusProcessor.saveDraftQta(resultData);
		return resultMap;
	}

    private ResultMap saveQta(Map<String, Object> param) {
        Map<String,Object> qtaData = (Map<String, Object>) param.get("qtaData");
        List<Map<String, Object>> insertItems = (List<Map<String, Object>>) param.get("insertItems");
        List<Map<String, Object>> deleteItems = (List<Map<String, Object>>) param.get("deleteItems");
		List<Map<String, Object>> updateItems = (List<Map<String, Object>>) param.get("updateItems");

        String qtaUuid = (String)qtaData.get("qta_uuid"); // 발주 아이디
        String qtaNo = (String) qtaData.get("qta_no");

        if (Strings.isNullOrEmpty(qtaNo)) { // 문서번호 및 차수 생성
			qtaNo = sharedService.generateDocumentNumber("QTA");
		}
		qtaData.put("qta_no", qtaNo); // 문서번호

		if (Strings.isNullOrEmpty(qtaUuid)) { // 신규
			qtaUuid = UUID.randomUUID().toString();
			qtaData.put("qta_uuid", qtaUuid);

			qtaRepository.insertQtaHeader(qtaData);
		} else {
			qtaRepository.updateQtaHeader(qtaData);
		}

		if (deleteItems != null && !deleteItems.isEmpty()) {
			for (Map<String, Object> row : deleteItems) {
				qtaItemService.deleteQtaItem(row);
			}
		}
		if (insertItems != null && !insertItems.isEmpty()) {
            for (Map<String, Object> row : insertItems) {
                String qtaItemUuid = UUID.randomUUID().toString(); // 품목 아이디
                row.put("qta_item_uuid",qtaItemUuid);
                row.put("qta_uuid",qtaUuid);
                row.put("qta_no",qtaNo);
                qtaItemService.insertQtaItem(row);
            }
        }
        if (updateItems != null && !updateItems.isEmpty()) {
			for (Map<String, Object> row : updateItems) {
				qtaItemService.updateQtaItem(row);
			}
		}
        Map resultDataMap = Maps.newHashMap();
		resultDataMap.put("qta_uuid", qtaUuid);
		return ResultMap.SUCCESS(resultDataMap);
    }

    public ResultMap deleteQta(Map<String, Object> param) {
        if(param.get("deleteQta") == null) {
			return ResultMap.NOT_EXISTS();
		}
		Map deleteQta = (Map) param.get("deleteQta");
		ResultMap validator = qtaValidator.validate(deleteQta);
		if(!validator.isSuccess()) {
			return validator;
		}

        qtaItemService.deleteQtaItems(deleteQta);
        qtaRepository.deleteQtaHeader(deleteQta);
        return ResultMap.SUCCESS();
    }

    public ResultMap directQta(Map<String, Object> param){
        Map<String,Object> qtaParam = this.findQta(param);
        Map<String, Object> qta = (Map<String, Object>) qtaParam.get("qtaData");
        List<Map<String,Object>> qtaItems = (List<Map<String, Object>>) qtaParam.get("qtaItems");
        List<Map<String, Object>> qtaHistrects = qtaItemHistrecService.findListQtaHistrecByApplDt(qtaParam);

        String applDtStr = (String)qta.get("appl_dt");
        LocalDate applDate = LocalDate.parse(String.valueOf(applDtStr),formatter);
        String applExpStr = "99991231";


        List<Map<String, Object>> updates = Lists.newArrayList();
        List<Map<String, Object>> deletes = Lists.newArrayList();

         for(Map<String,Object> qtaHistrect: qtaHistrects){
                String infoSd = (String)qtaHistrect.get("appl_st_dt");
                String infoEd = (String)qtaHistrect.get("appl_exp_dt");
                LocalDate startDate = LocalDate.parse(String.valueOf(infoSd),formatter);
                LocalDate endDate = LocalDate.parse(String.valueOf(infoEd),formatter);

                if (applDate.isBefore(startDate)) {
                    applExpStr =  formatter.format(startDate.minusDays(1));
                } else if (applDate.isEqual(startDate)) {
                    deletes.add(qtaHistrect);
                } else if (applDate.isEqual(endDate)){
                    Map<String, Object> update = Maps.newHashMap();
                    update.putAll(qtaHistrect);
                    // Add records for start_dt ~ inputDate - 1 and inputDate ~ end_dt
                    update.put("appl_exp_dt", formatter.format(applDate.minusDays(1)));
                    updates.add(update);
                } else if (applDate.isAfter(startDate) && applDate.isBefore(endDate)){
                    Map<String, Object> update = Maps.newHashMap();
                    update.putAll(qtaHistrect);
                    // Add records for start_dt ~ inputDate - 1 and inputDate ~ end_dt
                    update.put("appl_exp_dt", formatter.format(applDate.minusDays(1)));
                    updates.add(update);
                }
         }// end for qtaHistrects


        if (deletes != null && !deletes.isEmpty()) {
            for (Map<String, Object> row : deletes) {
                qtaItemHistrecService.deleteQtaItemHistrec(row);
            }
        }
        if (updates != null && !updates.isEmpty()) {
            for (Map<String, Object> row : updates) {
                qtaItemHistrecService.updateQtaItemHistrec(row);
            }
        }

        for(Map<String,Object> qtaItem: qtaItems){
            //default set
            qtaItem.put("appl_st_dt",applDtStr);
            qtaItem.put("appl_exp_dt",applExpStr);

            qtaItemHistrecService.insertQtaItemHistrec(qtaItem);
        }
        this.bypassApprovalQta(qta);
		return ResultMap.SUCCESS();
    }

    private void bypassApprovalQta(Map<String, Object> qta) {
        proStatusProcessor.bypassApprovalQta(qta);
    }

    public Map<String, Object> findQta(Map<String, Object> param) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Map<String,Object> qtaData = qtaRepository.findQta(param);
        resultMap.put("qtaData",qtaData);
		resultMap.put("qtaItems",qtaItemService.findListQtaItemByQta(param));
        return resultMap;
    }

    public List<Map<String, Object>> findListQta(Map<String, Object> param) {
        return qtaRepository.findListQta(param);
    }

    public ResultMap deleteListQta(Map param) {
		List invalidPrs = Lists.newArrayList();
		List notExistPrs = Lists.newArrayList();
		List deletePrs = (List) param.get("deleteQtas");

		for(Map deletePr : (List<Map>) deletePrs) {
			Map<String,Object> deleteParam = Maps.newHashMap();
			deleteParam.put("deleteQta",deletePr);
			ResultMap resultMap = this.deleteQta(deleteParam);

			if(Const.INVALID_STATUS_ERR.equals(resultMap.getResultStatus())) {
				invalidPrs.add(resultMap.getResultData());
			} else if(Const.NOT_EXIST.equals(resultMap.getResultStatus())) {
				notExistPrs.add(deletePr);
			}
		}

		return ValidatorUtil.setupDataListForValidationDataList(deletePrs, invalidPrs, notExistPrs);
	}
}
