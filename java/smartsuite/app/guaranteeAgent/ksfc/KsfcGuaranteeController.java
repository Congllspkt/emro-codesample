package smartsuite.app.guaranteeAgent.ksfc;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.guaranteeAgent.ksfc.service.KsfcGuaranteeService;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class KsfcGuaranteeController {
	
	private static final Logger LOG = LoggerFactory.getLogger(KsfcGuaranteeController.class);
	
	@Inject
	KsfcGuaranteeService ksfcGuaranteeService;

	/**
	 * 업체 보증보험 신청
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/ksfc/sendGuarInfo.do")
	public @ResponseBody ResultMap sendGuarInfo(@RequestBody Map param) {
		if(isNotExist(param)) {
			return ResultMap.NOT_EXISTS();
		}
		return ksfcGuaranteeService.sendGuarInfo(param);
	}

	/**
	 * 업체 보증보험 취소
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/ksfc/cancelGuar.do")
	public @ResponseBody ResultMap cancelGuar(@RequestBody Map param) {
		if(isNotExist(param)) {
			return ResultMap.NOT_EXISTS();
		}
		return ksfcGuaranteeService.cancelGuar(param);
	}

	/**
	 * 보증 승인
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "**/ksfc/approveGuar.do")
	public @ResponseBody ResultMap approveGuar(@RequestBody Map param) {
		if(isNotExist(param)) {
			return ResultMap.NOT_EXISTS();
		}
		return ksfcGuaranteeService.approveGuar(param);
	}

	/**
	 * 보증 반려
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "**/ksfc/rejectGuar.do")
	public @ResponseBody ResultMap rejectGuar(@RequestBody Map param) {
		if(isNotExist(param)) {
			return ResultMap.NOT_EXISTS();
		}
		return ksfcGuaranteeService.rejectGuar(param);
	}

	/**
	 * 보증 파기
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "**/ksfc/destroyGuar.do")
	public @ResponseBody ResultMap destroyGuar(@RequestBody Map param) {
		if(isNotExist(param)) {
			return ResultMap.NOT_EXISTS();
		}
		return ksfcGuaranteeService.destroyGuar(param);
	}

	/**
	 * 보증서 수신
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "**/recvKSFC.do")
	public @ResponseBody void recv(HttpServletRequest req, HttpServletResponse res) {
		LOG.info("++++ recv.do ++++++++++");
		ksfcGuaranteeService.recv(req, res);
	}
	
	/**
	 * 데이터 존재 여부 validator
	 */
	private Boolean isNotExist(Map param) {
		String ecntrUuidrId = (String)param.get("ecntr_uuid");
		String cntrEgurUuid = (String)param.get("cntr_egur_uuid");
		
		if(Strings.isNullOrEmpty(ecntrUuidrId) || Strings.isNullOrEmpty(cntrEgurUuid)) {
			return true; 
		} else {
			return false;
		}
	}
}