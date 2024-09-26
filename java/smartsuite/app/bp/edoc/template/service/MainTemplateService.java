package smartsuite.app.bp.edoc.template.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.bp.edoc.contract.doc.ContractDocument;
import smartsuite.app.bp.edoc.contract.doc.GetValueType;
import smartsuite.app.bp.edoc.template.repository.MainTemplateRepository;
import smartsuite.app.bp.edoc.template.repository.SubTemplateRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.util.MakingCntrForm;
import smartsuite.data.FloaterStream;
import smartsuite.exception.CommonException;

/**
 * 전자계약 계약서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @FileName MainTemplateService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class MainTemplateService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainTemplateService.class);

	@Inject
	PdfMakerService pdfMakerService;
	
	@Inject
	MakingCntrForm makingCntrForm;
	
	@Inject
	MainTemplateRepository mainTemplateRepository;
	
	@Inject
	SubTemplateRepository subTemplateRepository;
	
	@Inject
	ClauseService clauseService;
	
	/**
	 * 계약서식 목록 조회
	 * 
	 * @param : Map
	 * @return List
	 */
	public FloaterStream largeFindListCntrForm(Map<String,Object> param){
		return mainTemplateRepository.largeFindListCntrForm(param);
	}
	/**
	 * 계약서식 목록 저장
	 * 
	 * @param : Map
	 * @return Map
	 */
	public ResultMap saveListCntrForm(Map param) {
		Map<String,Object> resultData = Maps.newHashMap();

		List<Map<String,Object>> updateList = (List) param.get("updateList");
		List<Map<String,Object>> insertList = (List) param.get("insertList");
		
		//UPDATE
		for(Map row : updateList) {
			//계약서식 이름 중복체크
			int result = mainTemplateRepository.checkCntrFormName(row);
			if(result > 0){
				return ResultMap.DUPLICATED();
			}
			//계약서 수정
			mainTemplateRepository.updateCntrRepForm(row);
		}
		//INSERT
		for(Map row : insertList){
			// 계약서식 이름 중복체크 (duplicate)
			int result = mainTemplateRepository.checkCntrFormName(row);
			if(result > 0){
				return ResultMap.DUPLICATED();
			}
			//계약서식번호 채번
			String no = UUID.randomUUID().toString();
			row.put("cntrdoc_tmpl_uuid", no);
			//계약서식 저장
			mainTemplateRepository.insertCntrForm(row);
			//sqlSession.insert("cntrFormMgt.insertCntrForm", row);
			//계약서식내용 저장
			mainTemplateRepository.insertCntrFormCont(row);
		}
		
		return ResultMap.SUCCESS();
	}
	/**
	 * 계약서식 목록 삭제
	 * 
	 * @param : Map
	 * @return ResultMap
	 */
	public ResultMap deleteListCntrForm(Map param) {
		
		List<Map<String,Object>> deleteList = (List<Map<String,Object>>)param.get("deleteList");
		
		for(Map row : deleteList){
			//계약서식 내용 히스토리(변경이력) 삭제
			mainTemplateRepository.deleteCntrFormHistory(row);
			//계약서식 내용정보 삭제
			mainTemplateRepository.deleteCntrFormCont(row);
			//계약서식에 해당하는 첨부서식 삭제
			mainTemplateRepository.deleteCntrAppForm(row);
			//계약서식 삭제
			mainTemplateRepository.deleteCntrFrom(row);
			//계약항목 사용 정보 삭제
			clauseService.deleteCntrClauseUse((String) row.get("cntrdoc_tmpl_uuid"));
		}
		
		return ResultMap.SUCCESS();
	}
	/**
	 * 계약서식에 해당하는 첨부서식 조회
	 * 
	 * @param : Map
	 * @return List
	 */
	public List<Map<String,Object>> findListCntrAppForm(Map param) {
		return mainTemplateRepository.findListCntrAppForm(param);
	}
	/**
	 * 계약서식에 첨부서식 등록
	 * 
	 * @param : Map
	 * @return ResultMap
	 */
	public ResultMap saveListCntrAppForm(Map param) {
		List<Map<String,Object>> insertList = (List) param.get("insertList");
		List<Map<String,Object>> updateList = (List) param.get("updateList");
		
		//UPDATE
		for(Map row : updateList) {
			mainTemplateRepository.updateCntrAppxTmpl(row);
		}
		//INSERT
		for(Map row : insertList){
			mainTemplateRepository.insertCntrAppxTmpl(row);
		}
		
		return ResultMap.SUCCESS();
	}
	/**
	 * 계약서식의 첨부서식 삭제
	 * 
	 * @param : Map
	 * @return Map
	 */
	public ResultMap deleteListCntrAppForm(Map param) {
		
		List<Map<String,Object>> deleteList = (List) param.get("deleteList");
		for(Map row : deleteList){
			mainTemplateRepository.deleteCntrAppForm(row);
		}
		return ResultMap.SUCCESS();
	}
	/**
	 * 계약서식의 첨부서식 정렬순서 저장
	 * 
	 * @param : Map
	 * @return Map
	 */
	public Map saveListSortSeqAppForm(Map param) {
		Map<String,Object> resultMap = Maps.newHashMap();
		
		List<Map<String,Object>> saveList = (List) param.get("saveList");
		for(Map row : saveList){
			mainTemplateRepository.saveListSortSeqAppForm(row);
		}
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	/**
	 * 계약서식 변경이력 조회
	 * 
	 * @param : Map
	 * @return Map
	 * 
	 */
	public List<Map<String,Object>> findListCntrFormHistory(Map param) {
		return mainTemplateRepository.findListCntrFormHistory(param);
	}
	/**
	 * 변경이력 서식내용 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	public Map findFormCont(Map param) {
		return mainTemplateRepository.findFormCont(param);
	}
	
	/**
	 * 계약서식 PDF 다운로드
	 * @param resquest
	 * @param response
	 * @param : cntrdoc_tmpl_uuid
	 */
	public void downloadFormPdf(HttpServletRequest resquest, HttpServletResponse response, String cntrdocFormNo){
        Map<String, Object> pdfInfo = Maps.newHashMap();
		pdfInfo.put("cntrdoc_tmpl_uuid", cntrdocFormNo);
		
		FileInputStream fis = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bos = null;
		OutputStream pdfOs = null;
		File pdfFile = null;

		try {
			//계약서식 가져오기
			Map<String, Object> cntrMap = mainTemplateRepository.findCntrFormCont(pdfInfo);
			cntrMap.put("content", cntrMap.get("edit_content"));

			//항목 가져오기
			//List<Map<String,Object>> attrList = clauseService.getAttrAll(null);
			pdfInfo.put("tmpl_uuid", cntrdocFormNo);
			List<Map<String, Object>> useClauseList = clauseService.findClauseCntrUseByCntrClUuid(pdfInfo);

			ContractDocument contractDocument = new ContractDocument((String)cntrMap.get("edit_content"), useClauseList);
			//contractDocument.putValueToTemplate(Maps.newHashMap(), GetValueType.CNTR_CL_ID);
			String cntrdocCcmpldCont = contractDocument.getDisAbleDocument();

			cntrMap.put("cntrdoc_tmpl_cont", cntrdocCcmpldCont);
			
			List<Map<String,Object>> appList =  subTemplateRepository.getAppFormTextContent(pdfInfo);
			
			for(Map row : appList) {
				String appEditContent = (String)row.get("appx_tmpl_cont");

				ContractDocument appContractDocument = new ContractDocument(appEditContent, useClauseList);
				//contractDocument.putValueToTemplate(Maps.newHashMap(), GetValueType.CNTR_CL_ID);
				appEditContent = contractDocument.getDisAbleDocument();

				row.put("app_content", appEditContent);
			}

			String orgnFileNm = cntrMap.get("cntrdoc_tmpl_nm") + ".pdf";
			String pdfFullPath = pdfMakerService.makeCntrFormPdf(cntrMap, appList);
			pdfFile = new File(pdfFullPath);

			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			response.setHeader("Content-Disposition", "attachment; filename="
						+ URLEncoder.encode(orgnFileNm, "UTF-8").replaceAll("\\+", "%20"));
			response.setHeader("Content-Length", "" + pdfFile.length());
			pdfOs = response.getOutputStream();
			fis = new FileInputStream(pdfFile);

			byte b[] = new byte[(int) pdfFile.length()];
			int leng = 0;
			while ((leng = fis.read(b)) > 0) {
				pdfOs.write(b, 0, leng);
			}
			pdfOs.close();

		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=utf-8");
			LOG.error("class : " + this.getClass().toString() + "downloadFormPdf error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".downloadFormPdf : " + e.getMessage() + e.toString());
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bin != null)
					bin.close();
				if (fis != null)
					fis.close();
				if (pdfOs != null)
					pdfOs.close();
				if (pdfFile.exists())
					pdfFile.delete();
			} catch (Exception e) {
				LOG.error("class : " + this.getClass().toString() + "downloadFormPdf error : " + e.toString());
				e.printStackTrace();
			}
		}
    }
	/**
	 * 계약서식 내용 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	public Map findCntrFormCont(Map<String, Object> param) {
		
		return mainTemplateRepository.findCntrFormCont(param);
	}
	/**
	 * 계약서식 내용 저장(갱신)
	 *
	 * @param : Map
	 * @return Map
	 */
	public ResultMap saveCntrFormCont(Map<String, Object> param) {
		// 계약 조항 사용 내역 저장
		clauseService.saveCntrClauseUse((String) param.get("cntrdoc_tmpl_uuid"), (List) param.get("use_clause_list"));
				
		// 계약 서식 내용 업데이트
		mainTemplateRepository.updateCntrFormCont(param);
		
		// 변경이력 추가
		String cntrDocFormSeq = mainTemplateRepository.getCntrDocFormSeq(param);
		param.put("cntrdoc_tmpl_revno", cntrDocFormSeq);
		mainTemplateRepository.insertCntrFormHistory(param);

		return ResultMap.SUCCESS();
	}
	
	/**
	 * 계약서 첨부서류 조회 (text,file)
	 *
	 * @param : Map
	 * @return List
	 */
	public List<Map<String,Object>> getAppList(Map<String,Object> param) {
		return subTemplateRepository.getAppList(param);
	}
	
	/**
	 * 계약서식 조회
	 *
	 * @param : Map
	 * @return Map
	 */
	public Map cntrFormView(Map<String,Object> param) {
		return mainTemplateRepository.cntrFormView(param);
	}
	
	/**
	 * 계약서식 콤보용 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List findCntrdocFormList(Map<String,Object> param) {
		return mainTemplateRepository.findCntrdocFormList(param);
	}
	
	/**
	 * 계약서 템플릿 목록 조회
	 * 
	 * @param param
	 * @return FloaterStream
	 */
	public FloaterStream largeFindListCntrTmpl(Map<String,Object> param){
		return mainTemplateRepository.largeFindListCntrTmpl(param);
	}
	
	/**
	 * 계약서 템플릿 저장
	 * 
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap saveCntrTmpl(Map param) {
		// 계약서식 이름 중복체크 (duplicate)
		int result = mainTemplateRepository.checkCntrFormName(param);
		if(result > 0){
			return ResultMap.DUPLICATED();
		}
		
		//계약서식번호 채번
		String no = UUID.randomUUID().toString();
		param.put("cntrdoc_tmpl_uuid", no);
		
		//계약서식 저장
		mainTemplateRepository.insertCntrForm(param);
		//계약서식내용 저장
		mainTemplateRepository.insertCntrFormCont(param);
	
		return ResultMap.SUCCESS();
	}
}