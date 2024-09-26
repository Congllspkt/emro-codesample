package smartsuite.app.bp.srm.performance.appeal.service;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.appeal.repository.AppealRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.status.service.PerformanceEvalStatusService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AppealService {

	@Inject
	private AppealRepository appealRepository;

	@Inject
	private PerformanceEvalStatusService pfmcEvalStatusService;

	/** The mail service. */
	@Inject
	private MailService mailService;

	/**
	 * 이의제기 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public Map findAppealNoticeInfo(Map param) {
		return appealRepository.findAppealNoticeInfo(param);
	}

	/**
	 * 이의제기 통보를 요청한다. (구매사 -> 협력사 이의제기 통보)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public ResultMap noticeAppeal(Map param) {

		String appealUuid = (String) param.get("appeal_uuid");

		param.put("appeal_sts_ccd", "APPEAL_PRGSG");   // 진행상태 : 이의제기 통보

		if(Strings.isNullOrEmpty(appealUuid)) { // 신규
			appealUuid = UUID.randomUUID().toString();
			param.put("appeal_uuid", appealUuid);
			appealRepository.insertAppeal(param);
			pfmcEvalStatusService.noticeAppeal(param);    //  이의제기 상태를 "이의제기 통보"로 수정
		} else {
			appealRepository.updateAppeal(param);
		}

		// 공급사 이메일 발송
		this.sendMailAppeal(param);

		return ResultMap.SUCCESS();
	}

	// 공급사 이메일 발송
	private void sendMailAppeal(Map param) {

		Map<String, Object> mailList = new HashMap<>();
		// 이의제기 통보메일 발송대상 공급사 목록 조회
		List<Map<String, Object>>  appealVendorList = appealRepository.findListAppealVendorForMail(param);

		mailList.put("appealVendorList", appealVendorList);
		// 메일 발송
		mailService.sendAsync("NOTICE_OBJECTION_RAISE", null, mailList);

	}

	/**
	 * 이의제기 현황 목록을 조회한다. (협력사 -> 구매사 이의제기 제출 현황)
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 18
	 */
	public List findAppealList(Map param) {
		return appealRepository.findAppealList(param);
	}

	/**
	 * (정성 평가항목)이의제기 현황 상세조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 22
	 */
	public Map findAppealQualiDetail(Map param) {
		return appealRepository.findAppealQualiDetail(param);
	}

	/**
	 * (정성 평가항목)이의제기 상세정보 저장을 요청한다.
	 * 반려 or 합의 or 처리완료
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 23
	 */
	public ResultMap saveAppealQualiDetail(Map param) {
		Map<String, Object> saveParam = (Map<String, Object>) param.get("appealInfo");
		String saveType = (String) param.get("saveType");

		if (!Strings.isNullOrEmpty(saveType) && saveType.equals("reject")) { // 이의제기 반려
			String appealRetDt = (String) param.get("appealRetDt");
			saveParam.put("appeal_ret_dt", appealRetDt); // 이의제기 반려일자
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_RET"); // 이의제기 제출상태(반려)
		} else if(!Strings.isNullOrEmpty(saveType) && saveType.equals("receipt")){ // 이의제기 접수
			String appealRcptDt = (String) param.get("appealRcptDt");
			saveParam.put("appeal_rcpt_dt", appealRcptDt); // 이의제기 접수일자
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_RCPT"); // 이의제기 제출상태(접수)
		} else { // 이의제기 처리완료
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_APVD"); // 이의제기 제출상태(처리완료)
		}

		appealRepository.updateAppealQualiDetail(saveParam);

		return ResultMap.SUCCESS();
	}

	/**
	 * 이의제기 통보 임시저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 25
	 */
	public ResultMap tempSaveNoticeAppeal(Map param) {
		String appealUuid = (String) param.get("appeal_uuid");

		param.put("appeal_sts_ccd", "APPEAL_CRNG");   // 진행상태 : 이의제기 통보 대기

		if(Strings.isNullOrEmpty(appealUuid)) { // 신규
			appealUuid = UUID.randomUUID().toString();
			param.put("appeal_uuid", appealUuid);
			appealRepository.insertAppeal(param);
		} else { // 수정
			appealRepository.updateAppeal(param);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 이의제기를 종료(마감)한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 26
	 */
	public ResultMap closeAppeal(Map param) {
		appealRepository.closeAppeal(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * (계산항목) 이의제기 현황 상세조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	public Map findAppealCalcDetail(Map param) {

		String vdAppealCalcfactUuid = (String) param.get("vd_appeal_fact_uuid");
		param.put("vd_appeal_calcfact_uuid", vdAppealCalcfactUuid);

		return appealRepository.findAppealCalcDetail(param);
	}

	/**
	 * (계산항목) 이의제기 상세정보 저장을 요청한다.
	 * 반려 or 합의 or 처리완료
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 7. 27
	 */
	public ResultMap saveAppealCalcDetail(Map param) {

		Map<String, Object> saveParam = (Map<String, Object>) param.get("appealInfo");
		String saveType = (String) param.get("saveType");

		if (!Strings.isNullOrEmpty(saveType) && saveType.equals("reject")) { // 이의제기 반려
			String appealRetDt = (String) param.get("appealRetDt");
			saveParam.put("appeal_ret_dt", appealRetDt); // 이의제기 반려일자
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_RET"); // 이의제기 제출상태(반려)
			appealRepository.updateAppealCalcDetail(saveParam);
		} else if(!Strings.isNullOrEmpty(saveType) && saveType.equals("receipt")){ // 이의제기 접수
			String appealRcptDt = (String) param.get("appealRcptDt");
			saveParam.put("appeal_rcpt_dt", appealRcptDt); // 이의제기 접수일자
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_RCPT"); // 이의제기 제출상태(접수)
			appealRepository.updateAppealCalcDetail(saveParam);
		} else { // 이의제기 처리완료
			saveParam.put("appeal_subm_sts_ccd", "APPEAL_APVD"); // 이의제기 제출상태(처리완료)
			appealRepository.updateAppealCalcDetail(saveParam);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스 평가 결과 > 평가 결과 상세화면 > 이의제기 종료
	 * 퍼포먼스 평가 이의제기를 종료한다.
	 * @author : hj.jang
	 * @param peInfo the param
	 * @return the map
	 * @Date : 2023. 08. 14
	 * @Method Name : endPfmcEvalAppeal
	 */
	public ResultMap endPfmcEvalAppeal(Map<String, Object> peInfo) {
		pfmcEvalStatusService.endPfmcEvalAppeal(peInfo);
		return ResultMap.SUCCESS();
	}
}
