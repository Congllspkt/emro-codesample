package smartsuite.app.bp.edoc.contract;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.edoc.template.service.CntrTemplateService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 전자계약 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName EcontractController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/bp/edoc/**")
public class EcontractController {
	
	private static final Logger LOG = LoggerFactory.getLogger(EcontractController.class);
	
	@Inject
	EcontractService econtractService;
	
	@Inject
	CntrTemplateService cntrTemplateService;


	/**
	 * 1건 파일 다운로드 
	 * @param : athg_uuid
	 * @return : void
	 */
	@RequestMapping(value = "downloadPdf.do")
	public void downloadPdf(HttpServletRequest request, HttpServletResponse response,
								   @RequestParam (value = "sgncmpld_cntrdoc_athg_uuid", required = false) String fileGrpCd){
		Map<String, Object> param = Maps.newHashMap();
		param.put("athg_uuid", fileGrpCd);
		econtractService.download(request, response, param, null);
	}
	
	/**
	 * 1. 계약이력 서식내용조회, 첨부서식 내용 조회(계약서식 화면)
	 * 2. 기본계약서, 동반성장계약서 미리보기
	 * @param : Map
	 * @return : Map
	 */
	@RequestMapping(value="findFormCont.do")
	public @ResponseBody Map findFormCont(@RequestBody Map param) throws Exception{
		String mode = (String) param.get("mode");
		Map resultMap = Maps.newHashMap();
		if ("cntrForm".equals(mode)){
			// 계약서식내용 조회
			resultMap = cntrTemplateService.findFormCont(param);
		}
		else if ("appForm".equals(mode)){
			// 첨부서식내용 조회
			resultMap = cntrTemplateService.findAppformCont(param);
			resultMap.put("content", resultMap.get("appx_tmpl_cont"));
		}
		else if("appFormHis".equals(mode)) {
			// 첨부서식 변경이력 내용조회
			resultMap = cntrTemplateService.findAppCont(param);
		}
		else if("preview".equals(mode)) {
			// 일괄 계약 작성 미리보기
			resultMap = cntrTemplateService.cntrFormPreview(param);
		}
		return resultMap;
	}

	/**
	 * 계약서에 붙어있는 첨부서류 내용 조회
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value="findAppData.do")
	public @ResponseBody Map findAppData(@RequestBody Map param){
		return econtractService.findAppData(param);
	}

	/**
	 * 계약서에 붙어있는 첨부서류 업데이트(입력)
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value="updateAppFormOrgn.do")
	public @ResponseBody Map updateAppFormOrgn(@RequestBody Map param) throws Exception{
		return econtractService.updateAppFormOrgn(param);
	}

	/**
	 * 계약서내용 업데이트(입력)
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value="updateCntrFormOrgn.do")
	public @ResponseBody Map updateCntrFormOrgn(@RequestBody Map param) throws Exception{
		return econtractService.updateCntrFormOrgn(param);
	}

	/**
	 * 계약서 첨부서식 추가
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value="addAppFormInSts.do")
	public @ResponseBody Map addAppFormInSts(@RequestBody Map param) throws Exception{
		return econtractService.addAppFormInSts(param);
	}

	/**
	 * 계약서식에 해당하는 첨부서식 조회
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "searchAtt.do")
	public @ResponseBody List searchAtt(@RequestBody Map param) throws Exception{
		return econtractService.searchAtt(param);
	}

	/**
	 * 계약서 첨부서식 삭제
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value="delAppFormInSts.do")
	public @ResponseBody Map delAppFormInSts(@RequestBody Map param){
		return econtractService.delAppFormInSts(param);
	}

	/**
	 * 서명조회
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "checkedSign.do")
	public @ResponseBody Map checkedSign(@RequestBody Map param){
		return econtractService.checkedSign(param);
	}

	/**
	 *실제서명조회
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "getCertInfoView.do")
	public @ResponseBody ResultMap getCertInfoView(@RequestBody Map param){
		return econtractService.getCertInfoView(param);
	}

	/**
	 * 기타첨부문서 저장
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveCntrEtcFile.do")
	public @ResponseBody ResultMap saveCntrEtcFile(@RequestBody Map param){
		return econtractService.saveCntrEtcFile(param);
	}

	/**
	 * 첨부파일 용량 체크
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "checkAppFileSize.do")
	public @ResponseBody Map<String,Object> checkAppFileSize(@RequestBody Map<String,Object> param){
		return econtractService.checkAppFileSize(param);
	}

	/**
	 * 전자계약 조회
	 * @param : Map
	 * @return void
	 */
	@RequestMapping(value = "findElecContract.do")
	public @ResponseBody Map<String,Object> findElecContract(@RequestBody Map param) {
		return econtractService.findElecContract(param);
	}

	/**
	 * 첨부서 조회
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "findCntrAppCont.do")
	public @ResponseBody Map findCntrAppCont(@RequestBody Map param) {
		return econtractService.findCntrAppCont(param);
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
		econtractService.getNonStandardCntrPdf(request, response, cntrdocAthgUuid);
	}

	/**
	 * 전자계약 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value ="findEcontract.do")
		public @ResponseBody Map findEcontract(@RequestBody Map param) {
		return econtractService.findEcontractDetail(param);
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
		econtractService.previewPdfByEcntrId(request, response, ecntrId);
	}
	
	/**
	 * 부속서류 순서 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value ="saveCntrAppxOrd.do")
	public @ResponseBody ResultMap saveCntrAppxOrd(@RequestBody Map param) {
		return econtractService.saveCntrAppxOrd(param);
	}
}