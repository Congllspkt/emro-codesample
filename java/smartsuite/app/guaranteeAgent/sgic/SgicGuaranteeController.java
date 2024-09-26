package smartsuite.app.guaranteeAgent.sgic;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.guaranteeAgent.sgic.service.SgicGuaranteeService;
import smartsuite.app.guaranteeAgent.sgic.validator.SgicValidator;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class SgicGuaranteeController {
	private static final Logger LOG = LoggerFactory.getLogger(SgicGuaranteeController.class);
	
	@Inject
	SgicGuaranteeService sgicGuaranteeService;
	
	@Inject
	SgicValidator sgicValidator;

	/**
	 * 업체 보증보험 신청
	 * 
	 * @param param
	 * @return the map
	 * @throws IOException 
	 */
	@RequestMapping(value = "**/sgic/sendGuarInfo.do")
	public @ResponseBody ResultMap sendGuarInfo(@RequestBody Map param) throws IOException{
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;
		} else {
			result = sgicGuaranteeService.sendGuarInfo(param);
		}
		return result;
	}

	/**
	 * 업체 보증보험 취소
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/sgic/cancelGuar.do")
	public @ResponseBody ResultMap cancelGuar(@RequestBody Map param) {
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;
		} else {
			result = sgicGuaranteeService.cancelGuar(param);
		}
		
		return result;
	}

	/**
	 * 업체 보증보험 승인
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/sgic/approveGuar.do")
	public @ResponseBody ResultMap approveGuar(@RequestBody Map param) {
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;

		} else {
			result = sgicGuaranteeService.approveGuar(param);
		}
		return result;
	}

	/**
	 * 보증보험 반려
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/sgic/rejectGuar.do")
	public @ResponseBody ResultMap rejectGuar(@RequestBody Map param) {
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;
		} else {
			result = sgicGuaranteeService.rejectGuar(param);
		}
		return result;
	}

	/**
	 * 보증보험 파기
	 * 
	 * @param param
	 * @return the map
	 */
	@RequestMapping(value = "**/sgic/destroyGuar.do")
	public @ResponseBody ResultMap destroyGuar(@RequestBody Map param) {
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;
		} else {
			result = sgicGuaranteeService.destroyGuar(param);
		}
		return result;
	}

	/**
	 * 응답서 수신
	 * 
	 * @param req
	 * @param res
	 * @return void
	 */
	@RequestMapping(value = "**/sgic/recv.do")
		public @ResponseBody void recv(HttpServletRequest req, HttpServletResponse res) {
			LOG.info("===============recv 호출=============");
		sgicGuaranteeService.recv(req,res);
	}
	
	/**
	 * 응답서 수신
	 * 
	 * @param : Map
	 * @return : ResultMap
	 */
	@RequestMapping(value = "**/sgic/sgicRecvTest.do")
	public @ResponseBody ResultMap recvTest(@RequestBody Map param) {
		ResultMap result = sgicValidator.validateById(param);
		
		if(!result.isSuccess()) {
			return result;
		} else {
			result = sgicGuaranteeService.recvSgicTest(param);
		}
		return result;
	}

	/**
	 * 보증서 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value = "**/sgic/getGuarDetail.do")
	public @ResponseBody Map getGuarDetail(@RequestBody Map param) {

		return sgicGuaranteeService.getGuarDetail(param);
	}

	/**
	 * 서을보증보험 증권 보기
	 *
	 * @param : gur_uuid
	 * @param : print
	 * @return ModelAndView
	 */
	@RequestMapping(value = "**/sgic/sgicBond.do" , method = RequestMethod.GET)
	public ModelAndView sgicBond(@RequestParam(value="gur_uuid", required =true) String gurUuid,
	                             @RequestParam(value="print") String print) throws Exception{

		ModelAndView mav = new ModelAndView();

		mav.addObject("print",print);

		Map<String,Object> param = Maps.newHashMap();
		param.put("gur_uuid", gurUuid);

		Map<String,Object> bondInfo = sgicGuaranteeService.getRecvBondInfo(param);
		mav.addObject("bondInfo", bondInfo);
		mav.setViewName("eguarantee/sgicBondAtDB"); // 서울보증보험 jsp경로

		return mav;
	}
}