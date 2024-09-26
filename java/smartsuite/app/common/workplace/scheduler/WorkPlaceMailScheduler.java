package smartsuite.app.common.workplace.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.mail.MailService;


@Service
@Transactional
@Deprecated
/**
 * 사용안함.
 */
public class WorkPlaceMailScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(WorkPlaceMailScheduler.class);
	private static final String NAMESPACE = "workplace.";
	
	@Inject
	SqlSession sqlSession;
	@Inject
	MailService mailService;

	/**
	 * 마감, 도래, 신규 work 건에 대한 정보를 
	 * 담당자에게 전송
	 */
	public void sendWokrplaceAlarmMail(HashMap<String, Object> param){
		// 메일 발송 대상 여부 조회
		List<Map<String,Object>> mailTargList = sqlSession.selectList(NAMESPACE + "getMailTargList");
		
		for(Map<String,Object> target : mailTargList){
			LOG.debug("mailInfo : {}", target);
			// 메일 수신인 조회
			List<Map<String,Object>> mailReceiverList = getReceiverList(target);
			
			LOG.debug("mailReceiverList : {}" , mailReceiverList);
			
			// task관련 정보 조회
			Map<String,Object> workplaceMailContents = sqlSession.selectOne(NAMESPACE + "getWorkplaceMailContents", target);
			for(Map<String,Object> mailReceiver : mailReceiverList){
				workplaceMailContents.put("usr_nm", mailReceiver.get("to_nm"));
				mailReceiver.put("data", workplaceMailContents);
			}

			// 메일 발송
			//mailService.sendAsync("WORKPLACE_ALARM_MAIL", null);
			
			// 메일 발송 업데이트
			if("Y".equals(target.get("close_targ_yn"))){ // 지연
				target.put("close_mail_snd_yn", "Y");
			}
			if("Y".equals(target.get("arr_targ_yn"))){   // 도래
				target.put("arr_mail_snd_yn", "Y");
			}
			if("Y".equals(target.get("new_targ_yn"))){   // 신규
				target.put("new_mail_snd_yn", "Y");
			}
			
			sqlSession.update("updateMailSendYn", target);
		}
	}
	
	// 수신인 리스트 구하기
	private List<Map<String,Object>> getReceiverList(Map<String,Object> mailInfo){
		Map<String,Object> receiverInfo = sqlSession.selectOne(NAMESPACE + "getMailReceiver", mailInfo);
		String authTypCd = (String) receiverInfo.get("auth_typ_cd");
		String authCd    = (String) receiverInfo.get("auth_cd");
		
		List<Map<String,Object>> receiverList = null;
		
		if("VD".equals(authTypCd) || "UG".equals(authTypCd)){
			// 협력사, 유저
			// ESAUSER 테이블에서 조회
			receiverList = sqlSession.selectList(NAMESPACE + "getUserMailInfo", authCd);
		}else if("OG".equals(receiverInfo.get("auth_typ_cd"))){
			// 운영조직
			// ESAOOLM 테이블에서 해당 운영조직에 포함된 모든 사람을 조회
			receiverList = sqlSession.selectList(NAMESPACE + "getOperMailInfo", authCd);
		}else{
			receiverList = new ArrayList<Map<String, Object>>();
		}
		
		return receiverList;
	}
}
