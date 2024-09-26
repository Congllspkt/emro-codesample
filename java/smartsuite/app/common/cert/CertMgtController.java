package smartsuite.app.common.cert;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import smartsuite.app.common.cert.CertConst;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.cert.service.CertMgtService;
import smartsuite.app.common.cert.service.CertTestPdfService;
import smartsuite.app.common.cert.service.CertTestService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 인증서 관리 컨트롤러 Class입니다.
 *
 * @author daesung lee
 * @see 
 * @FileName CertMgtController.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/edoc/cert/**")
public class CertMgtController {

	private static final Logger LOG = LoggerFactory.getLogger(CertMgtController.class);
	
	@Inject
	CertMgtService certMgtService;
	
	@Inject
	CertTestPdfService certTestPdfService;
	
	@Inject
	CertTestService certTestService;

	@Inject
	VerificationProvider verificationProvider;
	
	/**
	 * 인증서 대상 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the list
	 * @Date : 2019. 2. 27
	 * @Method Name : largeFindCertTargetList
	 */
	@RequestMapping(value = "largeFindCertTargetList.do")
	public @ResponseBody FloaterStream largeFindCertTargetList(@RequestBody Map param) {
		return certMgtService.largeFindCertTargetList(param);
	}
	
	/**
	 * 인증서 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the list
	 * @Date : 2019. 2. 27
	 * @Method Name : largeFindCertList
	 */
	@RequestMapping(value = "largeFindCertList.do")
	public @ResponseBody FloaterStream largeFindCertList(@RequestBody Map param) {
		return certMgtService.largeFindCertList(param);
	}
	
	/**
	 * 인증서 저장
	 *
	 * @author : daesung lee
	 * @param : map
	 * @return map
	 * @Date : 2019. 2. 27
	 * @Method Name : saveCert
	 */
	@RequestMapping(value = "saveCert.do")
	public @ResponseBody ResultMap saveCert(@RequestBody Map param) throws IOException{
		return certMgtService.saveCert(param);
	}
	
	/**
	 * 인증서 삭제
	 *
	 * @author : daesung lee
	 * @param  : map
	 * @return  map
	 * @Date : 2019. 3.04
	 * @Method Name : deleteCertInfo
	 */
	@RequestMapping(value = "deleteCertInfo.do")
	public @ResponseBody ResultMap deleteCertInfo(@RequestBody Map param) throws IOException{
		return certMgtService.deleteCertInfo(param);
	}
	
	/**
	 * 인증서 검증
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the list
	 * @Date : 2019. 3.05
	 * @Method Name : verifyCertInfo
	 */
	@RequestMapping(value = "verifyCertInfo.do")
	public @ResponseBody ResultMap verifyCertInfo(@RequestBody Map param) throws IOException{
		return certMgtService.verifyCertInfo(param);
	}
	
	/**
	 * 전자계약 페이지 open (KICA, CrossCert) 테스트용
	 *
	 * @author : daesung lee
	 * @return ModelAndView
	 * @Date : 2019. 3. 19
	 * @Method Name : openCertSelectionPage
	 */
	@RequestMapping(value="openCertSelectionTestPage.do", method = RequestMethod.POST)
	public ModelAndView openCertSelectionTestPage(@RequestParam Map<String, Object> paramMap) {
		String installStatus = (String) paramMap.get("installStatus");
		String testMethod = (String) paramMap.get("testMethod");
		ModelAndView mv = null;
		try{

			if( verificationProvider.verifyClientToolkitInstallComplete(installStatus) ){
				String clientToolkitUrl = "";
				ResultMap resultMap = null;
				if(CertConst.LOCAL_SIGN_TEST.equals(testMethod)) { //로컬 인증서 검증 테스트
					resultMap = certTestService.getTestSignSource(paramMap);
					clientToolkitUrl = verificationProvider.getClientToolkitUrl();
				}else if(CertConst.LOCAL_MULTI_SIGN_TEST.equals(testMethod)) { //로컬 멀티서명 테스트
					resultMap = certTestService.getTestMultiSignSource(paramMap);
					clientToolkitUrl = verificationProvider.getClientToolkitMultiUrl();
				}
				Map resultData = resultMap.getResultData();
				mv = new ModelAndView(clientToolkitUrl,resultData);
			}else{
				String clientToolkitInstallUrl = verificationProvider.getClientToolkitInstallUrl();
				mv = new ModelAndView(clientToolkitInstallUrl, paramMap);
			}

		}catch(Exception e){
			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"    ,e.getMessage());
			LOG.error("Exception cause : {}" , e.getCause());
			LOG.error("#################ERROR#################");
			mv = new ModelAndView(CertConst.SIGN_COMPLETE);
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}
	
	/**
	 * 전자계약 페이지 open (KICA, CrossCert)
	 *
	 * @author : daesung lee
	 * @return ModelAndView
	 * @Date : 2019. 3. 19
	 * @Method Name : testSignComplete
	 */
	@RequestMapping(value="testSignComplete.do", method = RequestMethod.POST)
	public ModelAndView testSignComplete(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(CertConst.SIGN_COMPLETE);
		try {
			
			Map cntrInfo = EdocStringUtil.getRequestParamToMap(request);
			ResultMap resultMap = ResultMap.getInstance();
			if( CertConst.LOCAL_SIGN_TEST.equals((String)cntrInfo.get("testMethod")) ){
				resultMap = certTestService.testSignComplete(cntrInfo);
			}else if( CertConst.LOCAL_MULTI_SIGN_TEST.equals((String)cntrInfo.get("testMethod")) ){
				List<Map<String,Object>> signList= EdocStringUtil.getRequestParamToList(request,"sign_value");
				resultMap = certTestService.testMultiSignComplete(cntrInfo, signList);
			}
			
			mv.addObject(Const.RESULT_STATUS, resultMap.getResultStatus());
			//서명값 검증
			//certMgtService.verifySignValue(paramMap);
		}catch(Exception e){
			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"    ,e.getMessage());
			LOG.error("Exception cause : {}" , e.getCause());
			LOG.error("#################ERROR#################");
			mv = new ModelAndView(CertConst.SIGN_COMPLETE);
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}
	
	/**
	 * 전자계약 세금계산서 페이지 open (KICA, CrossCert)
	 *
	 * @author : daesung lee
	 * @return ModelAndView
	 * @Date : 2021. 7. 20
	 * @Method Name : openCertSelectionTaxTestPage
	 */
	@RequestMapping(value="openCertSelectionTaxTestPage.do", method = RequestMethod.POST)
	public ModelAndView openCertSelectionTaxTestPage(@RequestParam Map<String, Object> paramMap) {
		String returnUrl = "";
		ModelAndView mv = null;
		Map<String,Object> result = Maps.newHashMap();
		String installStatus    = (String) paramMap.get("installStatus"); // 한국전자인증 호출 순서
		try{

			if( verificationProvider.verifyClientToolkitInstallComplete( installStatus)) {
				returnUrl = CertConst.KICA_TOOLKIT_XML_URL;
				result = certTestService.getTestTaxMultiSignSource(paramMap);
			}else{
				returnUrl = verificationProvider.getClientToolkitInstallUrl();
				result = paramMap;
			}

			mv = new ModelAndView(returnUrl,result);
		}catch(Exception e){
			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"    ,e.getMessage());
			LOG.error("Exception cause : {}" , e.getCause());
			LOG.error("#################ERROR#################");
			mv = new ModelAndView(CertConst.SIGN_COMPLETE);
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}
	
	/**
	 * 멀티 전자세금계산서 서명 완료 (KICA, CrossCert)
	 *
	 * @author : daesung lee
	 * @return ModelAndView
	 * @Date : 2021. 7. 21
	 * @Method Name : testTaxMultiSignComplete
	 */
	@RequestMapping(value="testTaxMultiSignComplete.do", method = RequestMethod.POST)
	public ModelAndView testTaxMultiSignComplete(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView(CertConst.SIGN_COMPLETE);
		try{
			Map cntrInfo = EdocStringUtil.getRequestParamToMap(request);
			Map result = Maps.newHashMap();
			
			result = certTestService.testTaxMultiSignComplete(cntrInfo);
			mv.addObject(Const.RESULT_STATUS, result.get(Const.RESULT_STATUS));
		}catch(Exception e){
			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"    ,e.getMessage());
			LOG.error("Exception cause : {}" , e.getCause());
			LOG.error("#################ERROR#################");
			mv = new ModelAndView(CertConst.SIGN_COMPLETE);
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}
	
	@RequestMapping(value = "watermarkTestPdf.do")
	public void watermarkTestPdf(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam (value = "logicOrgCd", required = true) String logicOrgCd,
			@RequestParam (value = "logicOrgTypCCD", required = true) String logicOrgTypCCD) throws Exception {
		certTestPdfService.watermarkTestPdf(request, response, logicOrgCd, logicOrgTypCCD);
	}
	
	/**
	 * 인증서 정보 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the list
	 * @Date : 2019. 2. 27
	 * @Method Name : findOrgCertInfo
	 */
	@RequestMapping(value = "findOrgCertInfo.do")
	public @ResponseBody ResultMap findOrgCertInfo(@RequestBody Map param) {
		return certMgtService.findOrgCertInfo(param);
	}

	/**
	 * 사업자번호 저장
	 *
	 * @param param the param
	 * @return the list
	 * @Method Name : saveBizRegNo
	 */
	@RequestMapping(value = "saveBizRegNo.do")
	public @ResponseBody void saveBizRegNo(@RequestBody Map param) {
		certMgtService.saveBizRegNo(param);
	}
	
}