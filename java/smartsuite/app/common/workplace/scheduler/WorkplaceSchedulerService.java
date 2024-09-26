package smartsuite.app.common.workplace.scheduler;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.mail.data.TemplateMailData;
import smartsuite.app.common.workplace.shared.repository.TaskSharedRepository;
import smartsuite.app.common.workplace.shared.repository.WorkplaceMailRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
@Service
@Transactional
public class WorkplaceSchedulerService {

	@Inject
	MailService mailService;

	@Inject
	WorkplaceMailRepository workplaceMailRepository;
	@Inject
	TaskSharedRepository taskSharedRepository;

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void sendTaskStatusAlarmMail(HashMap<String,Object> param){


		// 메일 발송 대상 여부 조회
		List<Map<String, Object>> toUsrList = workplaceMailRepository.findListWorkplaceAlarmReceiverUsr(param);

		// TemplateMailData 구성
		List<TemplateMailData.Receiver> receivers = Lists.newArrayList();

		if(toUsrList != null) {
			for (Map usr : toUsrList) {

				mailService.send("WORKPLACE_ALARM_MAIL", null, usr);
			}
		}
	}

	@Transactional(propagation= Propagation.REQUIRES_NEW)
	public void saveMonitoringTaskStatus(HashMap<String,Object> param){

		// "사용 중"인 워크플레이스 업무상태 목록 (task_sts.use_yn = 'Y')
		List<Map<String, Object>> taskStatusList = taskSharedRepository.findListTaskStatusUsed(param);
		for(Map<String, Object> taskStatus : taskStatusList) {
			List<Map<String, Object>> taskList = taskSharedRepository.findListDataSourceTaskData(taskStatus);
			for(Map<String, Object> taskData : taskList) {
				// task 생성

			}
		}
	}


}
