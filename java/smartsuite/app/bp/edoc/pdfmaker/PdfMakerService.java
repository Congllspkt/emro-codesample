package smartsuite.app.bp.edoc.pdfmaker;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;
import smartsuite.upload.entity.SimpleMultipartFileItem;
import smartsuite.upload.util.AthfServiceUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PDF 생성관련 서비스
 */
@Service
public class PdfMakerService {
	
	private static final Logger LOG = LoggerFactory.getLogger(PdfMakerService.class);
	
	@Inject
	HtmlToPdfMaker htmlToPdfMaker;  // HTML -> PDF
	@Inject
	WordToPdfMaker wordToPdfMaker;  // WORD -> PDF
    @Inject
    StdFileService fileService;
    @Value("#{smartsuiteProperties['smartsuite.upload.path']}")
    private String fileUploadPath;
    
    public Map<String, Object> generateCntrFormPdf(Map<String, Object> formInfo, List<Map<String, Object>> appTxtList) {
        String orgnFileNm = (String) formInfo.get("orgn_file_nm");
        
        // pdf 생성
        String pdfFullPath = this.makeCntrFormPdf(formInfo, appTxtList);

        // 최종 수정된 pdf 파일 저장
        Map<String, Object> pdfInfo = this.savePdfFile(pdfFullPath, orgnFileNm);
        return pdfInfo;
    }
    
    public String makeCntrFormPdf(Map<String, Object> formInfo, List<Map<String, Object>> appTxtList) {
        String formContent = (String) formInfo.get("cntrdoc_tmpl_cont"); // 서식 HTML

        // pdf 생성
        String pdfFullPath = htmlToPdfMaker.makePdf(formContent, false);
        if(appTxtList != null && !appTxtList.isEmpty()) {
            htmlToPdfMaker.makePdf(pdfFullPath, appTxtList);
        }
        htmlToPdfMaker.setPageNumberStamp(pdfFullPath, formInfo);
        
        return pdfFullPath;
    }
	
	/**
	 * WORD파일을 사용하여 PDF를 생성한다.
	 * @param : filePath WORD파일 경로
	 * @return
	 */
    public Map<String, Object> generatePdfUsingWord(String orgnFileGrpCd, String orgnFileNm) {
        String pdfFullPath = this.makePdfUsingWord(orgnFileGrpCd, orgnFileNm);
        
        // 최종 수정된 pdf 파일 저장
        Map<String, Object> pdfInfo = this.savePdfFile(pdfFullPath, orgnFileNm);
        return pdfInfo;
    }
    
    public String makePdfUsingWord(String orgnFileGrpCd, String orgnFileNm) {
        FileItem orgnFile = this.getFileItemByGrpCd(orgnFileGrpCd);
        String pdfFullPath = wordToPdfMaker.makePdf(orgnFile);
        return pdfFullPath;
    }
	
	/**
	 * 비표준계약서에 서명을 한다.
	 * @param cntrInfo      계약정보
	 * @param orgnFileGrpCd 계약서 PDF 파일그룹코드
	 * @param appFileGrpCd  기타첨부 파일그룹코드
	 * @param orgnFileNm    PDF 파일명
	 * @param certInfo      인증서정보
	 * @return PDF 파일그룹코드 grp_cd 리턴
	 */
	public Map<String,Object> signNonStandardCntr(Map<String,Object> cntrInfo, String orgnFileGrpCd, String appFileGrpCd, String orgnFileNm, CertInfo certInfo) {
        String pdfFullPath = fileUploadPath + UUID.randomUUID().toString();
        FileItem orgnFile = this.getFileItemByGrpCd(orgnFileGrpCd);
        File outFile = new File(pdfFullPath);
        try {
            FileUtils.copyInputStreamToFile(orgnFile.toInputStream(), outFile);
        } catch (IOException e) {
        	LOG.error("class : " + this.getClass().toString() + "signNonStandardCntr error : " + e.toString());
            throw new CommonException(this.getClass().getName() + ".signNonStandardCntr(Map<String,Object> cntrInfo, String orgnFileGrpCd, String appFileGrpCd, String orgnFileNm, CertInfo certInfo) : " + e.getMessage(), e.toString());
        }
        
        pdfFullPath = wordToPdfMaker.signPdf(pdfFullPath, cntrInfo, certInfo, appFileGrpCd);
        
        // 최종 수정된 pdf 파일 저장
        Map<String, Object> pdfInfo = this.savePdfFile(pdfFullPath, orgnFileNm);
        return pdfInfo;
    }
	
	/**
	 * File을 사용하여 PDF를 생성한다.
	 * @param cntrInfo      계약정보
	 * @param orgnFileGrpId 계약서 파일그룹 uuid
	 * @param appFileGrpId  부속서류 파일그룹 uuid
	 * @param sign          서명 여부
	 * @param certInfo      인증서정보
	 * @return
	 */
	public Map<String,Object> generatePdfUsingFile(Map<String,Object> cntrInfo, String orgnFileGrpId, String appFileGrpId, boolean sign, CertInfo certInfo) {
        String pdfFullPath = fileUploadPath + UUID.randomUUID().toString();
        FileItem orgnFile = this.getFileItemByGrpCd(orgnFileGrpId);
        File outFile = new File(pdfFullPath);
        try {
            FileUtils.copyInputStreamToFile(orgnFile.toInputStream(), outFile);
        } catch (IOException e) {
        	LOG.error("class : " + this.getClass().toString() + "generatePdfUsingFile error : " + e.toString());
            throw new CommonException(this.getClass().getName() + ".generatePdfUsingFile : " + e.getMessage(), e.toString());
        }
        
        String orgnFileNm = "";
        if(sign) {
            // 서명
            pdfFullPath = wordToPdfMaker.signPdf(pdfFullPath, cntrInfo, certInfo, appFileGrpId);
            orgnFileNm = cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + "_signed.pdf";
        } else {
            pdfFullPath = wordToPdfMaker.makePdf(pdfFullPath, appFileGrpId);
			htmlToPdfMaker.setPdfStamp(pdfFullPath, cntrInfo);
            orgnFileNm = cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + ".pdf";
        }
        
        // 최종 수정된 pdf 파일 저장
        Map<String, Object> pdfInfo = this.savePdfFile(pdfFullPath, orgnFileNm);
        return pdfInfo;
    }

	/**
     * HTML을 사용하여 PDF를 생성한다. (첨부문서 FILE, TEXT 동시에 처리하도록)
     * @param cntrInfo 계약정보
     * @param appList  첨부문서리스트
     * @param sign     서명여부
     * @param certInfo 인증서정보
     * @param horizon  수평여부
     * @return PDF 파일그룹코드 grp_cd 리턴
     */
    public Map<String,Object> generatePdfUsingHtml(Map<String,Object> cntrInfo, List<Map<String,Object>> appList, boolean sign, CertInfo certInfo, boolean horizon) {
        String pdfFullPath = "";
        String orgnFileNm = "";
        
        if(sign) {
            // 서명
	        pdfFullPath = this.makeSignedPdfUsingHtml(cntrInfo, appList, certInfo, horizon);
            orgnFileNm = cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + "_signed.pdf";
        } else {
            pdfFullPath = this.makePdfUsingHtml(cntrInfo, appList, horizon);
            orgnFileNm = cntrInfo.get("cntr_no") + "-" + cntrInfo.get("cntr_revno") + ".pdf";
        }
        
        // 최종 수정된 pdf 파일 저장
        Map<String, Object> pdfInfo = this.savePdfFile(pdfFullPath, orgnFileNm);
        return pdfInfo;
    }
    
    public String makeSignedPdfUsingHtml(Map<String,Object> cntrInfo, List<Map<String,Object>> appList, CertInfo certInfo, boolean horizon) {
        String cntrContent = (String) cntrInfo.get("content");    // 계약서 HTML
        
        // pdf 생성
        String pdfFullPath = htmlToPdfMaker.makePdf(cntrContent, horizon);
        htmlToPdfMaker.signPdf(pdfFullPath, cntrInfo, certInfo, appList);

        return pdfFullPath;
    }
    
    public String makePdfUsingHtml(Map<String,Object> cntrInfo, List<Map<String,Object>> appList, boolean horizon) {
        String cntrContent = (String) cntrInfo.get("content");    // 계약서 HTML
        
        // pdf 생성
        String pdfFullPath = htmlToPdfMaker.makePdf(cntrContent, horizon);
        htmlToPdfMaker.makePdf(pdfFullPath, appList);
        htmlToPdfMaker.setPdfStamp(pdfFullPath, cntrInfo);

        return pdfFullPath;
    }
    
    /**
     * PDF 파일 저장
     * @param pdfFullPath
     * @param orgnFileNm
     * @return 파일그룹코드 grp_cd
     */
    public Map<String, Object> savePdfFile(String pdfFullPath, String orgnFileNm) {
        Map<String, Object> pdfInfo = new HashMap<String, Object>();
        File pdfFile = new File(pdfFullPath);
        
        try {
            String grpCd = UUID.randomUUID().toString();
            
            SimpleMultipartFileItem fileItem = AthfServiceUtil.newMultipartFileItem (
                    UUID.randomUUID().toString(),
                    grpCd,
                    orgnFileNm,
                    pdfFile
            );
            fileService.createWithMultipart(fileItem);
            
            pdfInfo.put("size", fileItem.getSize());
            pdfInfo.put("orgn_file_name", fileItem.getName());
            pdfInfo.put("athg_uuid", grpCd);
            pdfInfo.put("filePath", pdfFullPath);
            
        } catch (Exception e){
        	LOG.error("class : " + this.getClass().toString() + "savePdfFile error : " + e.toString());
            throw new CommonException(this.getClass().getName() + ".savePdfFile(String pdfFullPath, String orgnFileNm) : " + e.getMessage(), e.toString());
        } finally {
            try{
                if(pdfFile.exists()) pdfFile.delete(); // 임시파일 삭제
            }catch (Exception e){
                LOG.error("class : " + this.getClass().toString() + "savePdfFile error : " + e.toString());
                throw new CommonException(this.getClass().getName() + ".savePdfFile(String pdfFullPath, String orgnFileNm) : " + e.getMessage(), e.toString());
            }
        }
        
        return pdfInfo;
    }
    
    /**
     * 그룹 코드로 파일 조회 (1건)
     * @param grpCd
     * @return
     */
    private FileItem getFileItemByGrpCd(String grpCd) {
        FileList fileList;
        FileItem fileItem;
        try {
            fileList = fileService.findFileListWithContents(grpCd);
            fileItem = fileList.getItems().get(0);
        } catch (Exception e) {
        	LOG.error("class : " + this.getClass().toString() + "getFileByGrpCd error : " + e.toString());
            throw new CommonException(this.getClass().getName() + ".getFileByGrpCd(String grpCd) : " + e.getMessage(), e.toString());
        }
        return fileItem;
    }
}
