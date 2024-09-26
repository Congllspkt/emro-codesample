package smartsuite.app.common.shared.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.PerformanceEvalException;
import smartsuite.app.common.PfmcConst;
import smartsuite.app.common.shared.repository.PerformanceEvalSharedRepository;

import javax.inject.Inject;
import java.util.Map;

/**
 * SRM shared 서비스 Class입니다.
 *
 * @author mgPark
 * @see
 * @FileName PerformanceEvalSharedService.java
 * @package smartsuite.app.common.shared.service
 * @Since 2016. 6. 11
 * @변경이력 : [2016. 6. 11] mgPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "unchecked" })
public class PerformanceEvalSharedService {
	@Inject
	PerformanceEvalSharedRepository pfmcEvalSharedRepository;

	/**
	 * 에러 로그를 저장한다.
	 *
	 * @param PfmcErr the srm err
	 */
	@Transactional (propagation = Propagation.REQUIRES_NEW)
	public void writeEvalErrorLog(final PerformanceEvalException peException) {
		final Map<String, Object> errInfo = peException.errInfo;
		final String userErrMsg = (String)errInfo.getOrDefault(errInfo, PfmcConst.RESULT_MSG);
		final String userErrCode = (String)errInfo.getOrDefault(errInfo, "usr_err_cd");
		final StackTraceElement[] stacks = peException.getStackTrace();
		if(stacks != null && stacks.length > 0){
			final StackTraceElement stack = stacks[0];
			final String objName = stack.getMethodName();
			errInfo.put("obj_nm", objName);
		}
		String sysErrCode = "PfmcErrException";
		if(userErrCode != null && !userErrCode.isEmpty()){
			sysErrCode += " (" + userErrCode + ")";
		}
		errInfo.put("sys_err_cd", sysErrCode);
		errInfo.put("usr_err_msg", userErrMsg);
		pfmcEvalSharedRepository.insertEvalErrorLog(errInfo);
	}

}
