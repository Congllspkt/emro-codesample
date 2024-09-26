package smartsuite.app.bp.pro.unitprice.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.unitprice.repository.UnitPriceRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.module.ModuleManager;

import javax.inject.Inject;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class UnitPriceSchedulerService {
	@Inject
	UnitPriceRepository unitPriceRepository;
	
	@Inject
	MailService mailService;

	@Inject
	SharedService sharedService;

	/**
	 * 계약 만료기간 메일 알림 스케줄러
	 * @return
	 */
	public void sendMailExpirationTargetList(HashMap<String,Object> param) {
		param.put("setDate", ModuleManager.getModulePropertyValues("PRO", "po.unitprice.setDate", "7, 14"));

		String[] getDates = param.get("setDate").toString().split(",");
		int[] parseStringToInt = new int[getDates.length];

		for(int i=0; i < getDates.length; i++) {
			parseStringToInt[i] = Integer.parseInt(getDates[i].trim());
			param.put("getDate", parseStringToInt[i]);

			//단가계약 건 중, 계약 만료 일자 {0}일 전 데이터 조회
			List<Map<String,Object>> findMailSendTargetList = unitPriceRepository.findMailSendTargetList(param);
			Map<String, List<Map<String, Object>>> sendTargetMap = new HashMap<>();

			if(findMailSendTargetList != null && !findMailSendTargetList.isEmpty()) {
				for(Map<String, Object> targetList : findMailSendTargetList) {
					String cntrNo = (String) targetList.get("cntr_no");
					if(!sendTargetMap.containsKey(cntrNo)) {
						sendTargetMap.put(cntrNo, new ArrayList<>());
					}
					sendTargetMap.get(cntrNo).add(targetList);
				}

				for(Map.Entry<String, List<Map<String, Object>>> entry : sendTargetMap.entrySet()) {
					List<Map<String, Object>> resultArray = entry.getValue();
					for(Map<String, Object> result : resultArray) {
						result.put("oorg_cd", sharedService.findOperationOrganizationName((String) result.get("oorg_cd"), "PO"));
						result.put("cntr_no", result.get("cntr_no"));
						result.put("item_cd", result.get("item_cd"));
						result.put("disp_item_nm", result.get("disp_item_nm"));
						result.put("disp_vd_nm", result.get("disp_vd_nm"));
						result.put("uom_ccd", result.get("uom_ccd"));
						result.put("cur_ccd", result.get("cur_ccd"));
						result.put("uprc_efct_st_dt", result.get("uprc_efct_st_dt"));
						result.put("uprc_efct_exp_dt", result.get("uprc_efct_exp_dt"));
						result.put("usr_id", result.get("cntr_pic_id"));
						result.put("usr_nm", result.get("usr_nm"));
						result.put("eml", result.get("eml"));
					}

					Map<String, Object> mailParam = Maps.newHashMap();
					mailParam.put("cntrList", resultArray);
					mailParam.put("getDate", param.get("getDate"));
					mailService.sendAsync("UPC_PD_EXP_ALRM_MAIL", null, mailParam);
				}
			}
		}
	}
}
