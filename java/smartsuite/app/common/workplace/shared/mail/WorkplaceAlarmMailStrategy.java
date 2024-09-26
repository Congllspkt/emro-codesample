package smartsuite.app.common.workplace.shared.mail;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.workplace.dashboard.service.WorkplaceService;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.mail.strategy.MailStrategy;
import smartsuite.app.common.workplace.shared.service.WorkplaceSharedService;

import javax.inject.Inject;
import javax.mail.Message;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WorkplaceAlarmMailStrategy implements MailStrategy {

	@Inject
	WorkplaceSharedService workplaceSharedService;
	@Inject
	WorkplaceService workplaceService;

	private final SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

	@Override
	public String getEmailTemplateId() {
		return "WORKPLACE_ALARM_MAIL";
	}

	@Override
	public TemplateMailData getTemplateMailData(String appId, Map<String, Object> data) throws Exception {
		Map<String, Object> findParam = Maps.newHashMap(data);

		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

		TemplateMailData.Receiver receiver = TemplateMailData.Receiver.getInstance(
				Message.RecipientType.TO,
				(String) data.get("eml"),
				(String) data.get("usr_nm"));
		receivers.add(receiver);

		Map<String, Object> mailData = Maps.newHashMap();
		findParam.put("search_typ", "scheduler");

		List<Map<String, Object>> mainTaskList = workplaceSharedService.findListMainTaskCount(findParam);
		List<Map<String, Object>> taskList = workplaceService.findListTask(findParam);
		mailData.put("mainTaskList", mainTaskList);

		if(mainTaskList != null) {
			Integer delayTotCnt = 0;
			Integer immntTotCnt = 0;
			Integer normTotCnt = 0;
			Integer newTotCnt = 0;
			for(Map<String, Object> mainTask : mainTaskList) {
				delayTotCnt += ((BigDecimal)mainTask.getOrDefault("delay_tot_cnt", 0)).intValue();
				immntTotCnt += ((BigDecimal)mainTask.getOrDefault("immnt_tot_cnt", 0)).intValue();
				normTotCnt += ((BigDecimal)mainTask.getOrDefault("norm_tot_cnt", 0)).intValue();
				newTotCnt += ((BigDecimal)mainTask.getOrDefault("new_tot_cnt", 0)).intValue();
			}
			mailData.put("delay_tot_cnt", delayTotCnt);
			mailData.put("immnt_tot_cnt", immntTotCnt);
			mailData.put("norm_tot_cnt", normTotCnt);
			mailData.put("new_tot_cnt", newTotCnt);
		}
		mailData.put("taskList", taskList);

		String today = fm.format(new Date());
		mailData.put("today", today);

		TemplateMailData templateMailData = TemplateMailData.getInstance(
				null,
				receivers,
				null,
				null,
				mailData
		);

		return templateMailData;
	}
}
