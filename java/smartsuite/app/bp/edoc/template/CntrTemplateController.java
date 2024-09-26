package smartsuite.app.bp.edoc.template;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.edoc.template.service.ClauseService;
import smartsuite.app.bp.edoc.template.service.MainTemplateService;
import smartsuite.app.bp.edoc.template.service.SubTemplateService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 전자계약 서식관리(계약서식,첨부서식,계약항목) 관련 처리를 하는 컨트롤러 Class입니다.
 *
 * @FileName TemplateController.java
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value = "**/edoc/**")
public class CntrTemplateController {

	@Inject
	MainTemplateService mainTemplateService;    //계약서식

	@Inject
	SubTemplateService subTemplateService;    //첨부서식

	@Inject
	ClauseService clauseService;    //계약항목

	/**
	 * 계약서식 목록 조회
	 *
	 * @param : Map
	 * @return List
	 */
	@RequestMapping(value = "largeFindListCntrForm.do")
	public @ResponseBody FloaterStream largeFindListCntrForm(@RequestBody Map param) {
		// 대용량 처리
		return mainTemplateService.largeFindListCntrForm(param);
	}

	/**
	 * 계약서식 저장
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "saveListCntrForm.do")
	public @ResponseBody ResultMap saveListCntrForm(@RequestBody Map param) {
		return mainTemplateService.saveListCntrForm(param);
	}

	/**
	 * 계약서식 삭제
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "deleteListCntrForm.do")
	public @ResponseBody ResultMap deleteListCntrForm(@RequestBody Map param) {
		return mainTemplateService.deleteListCntrForm(param);
	}

	/**
	 * 계약서식에 해당하는 첨부서식 조회
	 *
	 * @param : Map
	 * @return List
	 */
	@RequestMapping(value = "findListCntrAppForm.do")
	public @ResponseBody List findListCntrAppForm(@RequestBody Map param) {
		return mainTemplateService.findListCntrAppForm(param);
	}

	/**
	 * 첨부서식 목록 조회 (첨부추가 팝업)
	 *
	 * @param : Map
	 * @return List
	 */
	@RequestMapping(value = "findListEpAppForm.do")
	public @ResponseBody List findListEpAppForm(@RequestBody Map param) {
		return subTemplateService.findListEpAppForm(param);
	}

	/**
	 * 계약서식에 첨부서식 등록
	 *
	 * @param param the param
	 * @return the map
	 * @author : SungHyun Kang
	 * @Date : 2016. 3. 9
	 * @Method Name : saveListCntrAppForm
	 */
	@RequestMapping(value = "saveListCntrAppForm.do")
	public @ResponseBody ResultMap saveListCntrAppForm(@RequestBody Map param) {
		return mainTemplateService.saveListCntrAppForm(param);
	}

	/**
	 * 계약서식의 첨부서식 삭제
	 *
	 * @param param the param
	 * @return the map
	 * @author : SungHyun Kang
	 * @Date : 2016. 3. 9
	 * @Method Name : deleteListCntrAppForm
	 */
	@RequestMapping(value = "deleteListCntrAppForm.do")
	public @ResponseBody ResultMap deleteListCntrAppForm(@RequestBody Map param) {
		return mainTemplateService.deleteListCntrAppForm(param);
	}

	/**
	 * 계약서식의 첨부서식 정렬순서 저장
	 *
	 * @param param the param
	 * @return the map
	 * @author : SungHyun Kang
	 * @Date : 2016. 3. 9
	 * @Method Name : deleteListCntrAppForm
	 */
	@RequestMapping(value = "saveListSortSeqAppForm.do")
	public @ResponseBody Map saveListSortSeqAppForm(@RequestBody Map param) {
		return mainTemplateService.saveListSortSeqAppForm(param);
	}

	/**
	 * 계약서식 변경이력 조회
	 *
	 * @param param the param
	 * @return the list
	 * @author : SungHyun Kang
	 * @Date : 2016. 3. 9
	 * @Method Name : findListCntrFormHistory
	 */
	@RequestMapping(value = "findListCntrFormHistory.do")
	public @ResponseBody List findListCntrFormHistory(@RequestBody Map param) {
		return mainTemplateService.findListCntrFormHistory(param);
	}

	/**
	 * 첨부서식 변경이력 조회
	 */
	@RequestMapping(value = "findListAttformHistory.do")
	public @ResponseBody List findListAttformHistory(@RequestBody Map param) {
		return subTemplateService.findListAttformHistory(param);
	}

	/**
	 * 계약서식 PDF 다운로드
	 *
	 * @param : request
	 * @param : response
	 * @param : cntrdoc_tmpl_uuid
	 */
	@RequestMapping(value = "downloadFormPdf.do")
	public void downloadFormPdf(HttpServletRequest request, HttpServletResponse response,
	                            @RequestParam(value = "cntrdoc_tmpl_uuid", required = false) String cntrdocFormNo) {
		mainTemplateService.downloadFormPdf(request, response, cntrdocFormNo);
	}

	/**
	 * 계약서식 내용 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "findCntrformCont.do")
	public @ResponseBody Map findCntrFormCont(@RequestBody Map param) {
		return mainTemplateService.findCntrFormCont(param);
	}

	/**
	 * 계약서식 내용 내용 저장(갱신)
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "saveCntrFormCont.do")
	public @ResponseBody ResultMap saveCntrFormCont(@RequestBody Map param) {
		return mainTemplateService.saveCntrFormCont(param);
	}

	/**
	 * 첨부서식 목록 조회
	 *
	 * @param : Map
	 * @return FloaterStream (대용량)
	 */
	@RequestMapping(value = "largeFindListAppForm.do")
	public @ResponseBody FloaterStream largeFindListAppForm(@RequestBody Map param) {
		// 대용량 처리
		return subTemplateService.largeSearchListAppForm(param);
	}

	/**
	 * 첨부서식 목록 저장
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveListAppForm.do")
	public @ResponseBody ResultMap saveListAppForm(@RequestBody Map param) {
		return subTemplateService.saveListAppForm(param);
	}

	/**
	 * 첨부서식 목록 삭제
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "deleteListAppForm.do")
	public @ResponseBody ResultMap deleteListAppForm(@RequestBody Map param) {
		return subTemplateService.deleteListAppForm(param);
	}

	/**
	 * 첨부서식 파일 저장(갱신)
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveAppFormFile.do")
	public @ResponseBody ResultMap saveAppFormFile(@RequestBody Map param) {
		return subTemplateService.saveAppFormFile(param);
	}

	/**
	 * 첨부서식 내용 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "findAppformCont.do")
	public @ResponseBody Map findAppformCont(@RequestBody Map param) {
		return subTemplateService.findAppformCont(param);
	}

	/**
	 * 첨부서식 내용 내용 저장(갱신)
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveAppFormCont.do")
	public @ResponseBody ResultMap saveAppFormCont(@RequestBody Map param) {
		return subTemplateService.saveAppFormCont(param);
	}


	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return FloaterStream  (대용량)
	 */
	@RequestMapping(value = "largeSearchListCntrItem.do")
	public @ResponseBody FloaterStream largeSearchListCntrItem(@RequestBody Map param) {
		// 대용량 처리
		return clauseService.largeSearchListCntrItem(param);
	}

	/**
	 * 계약항목 저장
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveListCntrItem.do")
	public @ResponseBody ResultMap saveListCntrItem(@RequestBody Map param) {
		return clauseService.saveListCntrItem(param);
	}

	/**
	 * 계약항목 삭제
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "deleteListCntrItem.do")
	public @ResponseBody ResultMap deleteListCntrItem(@RequestBody Map param) {
		return clauseService.deleteListCntrItem(param);
	}

	/**
	 * 계약서 동적생성 템플릿 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	@RequestMapping(value = "findDynamicTmpById.do")
	public @ResponseBody Map findDynamicTmpById(@RequestBody Map param) {
		return clauseService.findDynamicTmpById(param);
	}

	/**
	 * 계약서 동적생성 템플릿 저장
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveDynamicTmp.do")
	public @ResponseBody ResultMap saveDynamicTmp(@RequestBody Map param) {
		return clauseService.saveDynamicTmp(param);
	}

	/**
	 * 계약서 템플릿 목록 조회
	 *
	 * @param param
	 * @return FloaterStream
	 */
	@RequestMapping(value = "largeFindListCntrTmpl.do")
	public @ResponseBody FloaterStream largeFindListCntrTmpl(@RequestBody Map param) {
		// 대용량 처리
		return mainTemplateService.largeFindListCntrTmpl(param);
	}

	/**
	 * 계약서 템플릿 저장
	 *
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveCntrTmpl.do")
	public @ResponseBody ResultMap saveCntrTmpl(@RequestBody Map param) {
		return mainTemplateService.saveCntrTmpl(param);
	}

	/**
	 * 계약 조항 목록 조회
	 *
	 * @param param
	 * @return FloaterStream
	 */
	@RequestMapping(value = "largeFindListCntrClause.do")
	public @ResponseBody FloaterStream largeFindListCntrClause(@RequestBody Map param) {
		// 대용량 처리
		return clauseService.largeFindListCntrClause(param);
	}

	/**
	 * 계약 조항 저장
	 *
	 * @param param
	 * @return ResultMap
	 */
	@RequestMapping(value = "saveCntrClause.do")
	public @ResponseBody ResultMap saveCntrClause(@RequestBody Map param) {
		return clauseService.saveCntrClause(param);
	}

	/**
	 * 계약 조항을 사용중인 템플릿 목록 조회
	 *
	 * @param param ( use_clause_list )
	 * @return
	 */
	@RequestMapping(value = "findListCntrClauseUse.do")
	public @ResponseBody List findListCntrClauseUse(@RequestBody Map param) {
		return clauseService.findListCntrClauseUse(param);
	}

	/**
	 * 계약 조항 tag 생성
	 *
	 * @param param ( use_clause_list )
	 * @return
	 */
	@RequestMapping(value = "findCntrClauseTag.do")
	public @ResponseBody String findCntrClauseTag(@RequestBody Map param) {
		return clauseService.findCntrClauseTag(param);
	}


}