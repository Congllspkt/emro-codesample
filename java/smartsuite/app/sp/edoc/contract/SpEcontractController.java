package smartsuite.app.sp.edoc.contract;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.shared.Const;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.EdocConst;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 전자계약 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName SpEcontractController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/edoc/**/**/")
public class SpEcontractController {

	private static final Logger LOG = LoggerFactory.getLogger(SpEcontractController.class);
	
	@Inject
	SpEcontractService spEcontractService;

	@Inject
	VerificationProvider verificationProvider;
	
	
	/** 첨부서류 내용조회 **/
	@RequestMapping(value="findAppData.do")
	protected @ResponseBody Map findAppData(@RequestBody Map param){
		return spEcontractService.findAppData(param);
	}
	
	/** 첨부서식 파일 저장 **/
	@RequestMapping(value = "saveAppFormFile.do")
	public @ResponseBody Map saveAppFormFile(@RequestBody Map param) {
		return spEcontractService.saveAppFormFile(param);
	}
	
	/** 서명조회 **/
	@RequestMapping(value = "checkedSign.do")
	protected @ResponseBody Map checkedSign(@RequestBody Map param){
		return spEcontractService.checkedSign(param);
	}
	
	/** 실제서명조회 **/
	@RequestMapping(value = "getCertInfoView.do")
	protected @ResponseBody Map getCertInfoView(@RequestBody Map param){
		return spEcontractService.getCertInfoView(param);
	}

	/** 계약서식에 해당하는 첨부서식 조회 **/
	@RequestMapping(value = "searchAtt.do")
	public @ResponseBody List<Map<String,Object>> searchAtt(@RequestBody Map<String,Object> param) throws Exception{
		return spEcontractService.searchAtt(param);
	}

	/**
	 * UnsatisfiedLinkError crosscert 설정 실패시(dll, so), error처리
	 */
	@ExceptionHandler(UnsatisfiedLinkError.class)
	public @ResponseBody Map<String,Object> handlLinkError(UnsatisfiedLinkError e){
		LOG.error("#################UnsatisfiedLinkError#################");
		LOG.error("Check CrossCert Configuration");
		LOG.error("error msg : {}"   , e.getMessage());
		LOG.error("error string: {}" , e.toString());
		LOG.error("#################UnsatisfiedLinkError#################");
		
		Map<String,Object> result = Maps.newHashMap();
		result.put(Const.RESULT_STATUS, Const.FAIL);
		return result;
	}
	
	/**
	 * 비표준계약서 PDF 화면 출력
	 * @param request
	 * @param response
	 * @param cntrdocAthgUuid
	 */
	@RequestMapping(value = "getNonStandardCntrPdf.do")
	public void getNonStandardCntrPdf(HttpServletRequest request, HttpServletResponse response,
	                                  @RequestParam(value = "cntrdoc_athg_uuid", required = false) String cntrdocAthgUuid) {
		spEcontractService.getNonStandardCntrPdf(request, response, cntrdocAthgUuid);
	}
	
	/**
	 * 협력사 전자계약 페이지 open (KICA, CrossCert)
	 *
	 * @return ModelAndView
	 * @Date : 2018. 1. 13
	 * @Method Name : openCertSelectionPage
	 */
	@RequestMapping(value="openCertificateSelectionPage.do", method = RequestMethod.POST)
	public ModelAndView openCertificateSelectionPage(@RequestParam Map<String, Object> paramMap) {
		String installStatus    = (String) paramMap.get("installStatus");
		ModelAndView mv = null;
		try{

			if(verificationProvider.verifyClientToolkitInstallComplete(installStatus)){

				//전자서명 진행 시 다른 업체가 전자서명을 진행하지 못하도록 lock 설정한다.
				String signableSts = spEcontractService.controlSignatureProgress(paramMap);

				if(CntrConst.SGN_LCKD_STS.SOMEONE_ELSE_SIGN_USING.equals(signableSts)) { //다른 협력사가 서명일 진행 중일때
					mv = new ModelAndView("econtract/signComplete");
					mv.addObject(Const.RESULT_STATUS, "ES");
					return mv;
				}

				String pdfFileStatus = spEcontractService.checkSignPdfFileStatus(paramMap);
				Map<String,Object> result = Maps.newHashMap();

				if( EdocConst.EXSIT_PDF_FILE.equals(pdfFileStatus) ) { //이미서명을 진행하고 서명한 파일이 존재하는 경우 (다자간계약일 경우)
					result = spEcontractService.findExistSignEContractInfo(paramMap);
				}else if(EdocConst.NOT_EXSIT_PDF_FILE.equals(pdfFileStatus) ) {
					/**
					 * PDF 서명
					 * (PDF 안에 계약서,첨부TEXT,첨부FILE 모두 포함하여 서명)
					 */
					result = spEcontractService.findSignEContractInfo(paramMap);
					/*** PDF 서명 소스 끝*/
				}

				String clientToolkiturl = verificationProvider.getClientToolkitUrl();
				mv = new ModelAndView(clientToolkiturl, result);
			}else{
				String clientToolkitInstallurl = verificationProvider.getClientToolkitInstallUrl();
				mv = new ModelAndView(clientToolkitInstallurl, paramMap);
			}

		}catch(Exception e){

			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"    ,e.getMessage());
			LOG.error("Exception cause : {}" , e.getCause());
			LOG.error("Exception trace : {}" , e.toString());
			LOG.error("error stacktrace : {}", e.getStackTrace());
			LOG.error("#################ERROR#################");
			mv = new ModelAndView("econtract/signComplete");
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}

	/**
	 * 협력사 서명값 저장
	 */
	@RequestMapping(value="saveSignatureValue.do", method=RequestMethod.POST)
	public ModelAndView saveSignatureValue(@RequestParam Map<String,Object> paramMap) {
		ModelAndView mv = new ModelAndView("econtract/signComplete");
		try{
			Map<String,Object> result = spEcontractService.saveSignatureValue(paramMap);
			mv.addObject(Const.RESULT_STATUS, result.get(Const.RESULT_STATUS));
		}catch(Exception e){
			LOG.error("#################ERROR#################");
			LOG.error("Exception msg : {}"   , e.getMessage());
			LOG.error("Exception trace : {}" , e.toString());
			LOG.error("Exception cause : {}"    , e.getCause());
			LOG.error("#################ERROR#################");
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}catch(UnsatisfiedLinkError e){
			LOG.error("#################UnsatisfiedLinkError#################");
			LOG.error("Check CrossCert Configuration");
			LOG.error("error msg : {}"   , e.getMessage());
			LOG.error("error string: {}" , e.toString());
			LOG.error("#################UnsatisfiedLinkError#################");
			mv.addObject(Const.RESULT_STATUS, Const.FAIL);
		}
		return mv;
	}

	/**
	 * 전자계약 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value ="findEcontract.do")
	public @ResponseBody Map findEcontract(@RequestBody Map param) {
		return spEcontractService.findEcontractDetail(param);
	}
	
	/**
	 * PDF 미리보기
	 * @param request
	 * @param response
	 * @param ecntrId
	 */
	@RequestMapping(value = "previewPdfByEcntrId.do")
	public void previewPdfByEcntrId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "ecntr_uuid", required = true) String ecntrId) {
		spEcontractService.previewPdfByEcntrId(request, response, ecntrId);
	}
}