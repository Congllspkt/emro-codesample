package smartsuite.app.bp.edoc.pdfmaker.signStrategy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aspose.pdf.Document;
import com.google.common.collect.Maps;

import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.repository.CertMgtRepository;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.exception.CommonException;

@Service
public class PdfSignStrategy implements SignStrategy{
	
	private static final Logger LOG = LoggerFactory.getLogger(PdfSignStrategy.class);

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;
	@Inject
	private PdfUtil pdfUtil;
	@Inject
	ImageTypeProvider imageTypeProvider;

	/***************************************************************************************
	 * Aspose Sign순서
	 * 1. 첨부서식 분리 (.pdf, .doc, .docx)
	 * 2. PDF 병합 (첨부서식이 PDF인 경우 본문에 병합, 워드파일인 경우 PDF 변환 후 병합)
	 * 3. 첨부파일 첨부
	 * 4. watermark 삽입 (백그라운드 이미지, header, footer 글귀)
	 * 5. 인증서 삽입
	 ***************************************************************************************/
	
	public PdfUtil getAsposeUtil() {
		return pdfUtil;
	}

	@Override
	public String signPdf(String filePath, Map<String, Object> mappingInfo, CertInfo certInfo, List<Map<String, Object>> appList) {
		String filePathCopy = filePath;
		Document doc = new Document(filePathCopy);
		File stampWatermarkSmallImg = null;

		try {
			for(Map<String, Object> appMap:appList){
				pdfUtil.mergePdf(doc, appMap, fileUploadPath);
			}
			imageTypeProvider.setParam(mappingInfo);
			String certImageName = imageTypeProvider.getCertName();
			String ext = EdocStringUtil.getExtension(certImageName);
			String signDate = EdocStringUtil.getTodayDate("yyyy/MM/dd");

			pdfUtil.setWatermark(doc, imageTypeProvider.getWatermark(), mappingInfo);

			String stampWatermarkSmallImgPath = pdfUtil.stampTextOnImage(fileUploadPath, certImageName, ext, imageTypeProvider.getCert(), signDate);

			//이미지에 서명일자 넣기
			stampWatermarkSmallImg = new File(stampWatermarkSmallImgPath);
			pdfUtil.setCertificate(doc, stampWatermarkSmallImg, certInfo.getPfx(), certInfo.getCertPw()).save(filePathCopy);
			
		} catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "signPdf error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".signPdf(String filePath, Map<String, Object> mappingInfo, CertInfo certInfo, List<Map<String, Object>> appList) : " + e.getMessage() + e.toString());
		} finally {
			if(stampWatermarkSmallImg.exists()) stampWatermarkSmallImg.delete(); //임시 파일 삭제
		}
		
		return filePathCopy;
	}

	@Override
	public String signPdf(String filePath, Map<String, Object> mappingInfo, CertInfo certInfo, String appFileGrpCd) {
		String filePathCopy = filePath;
		Document doc = new Document(filePathCopy);
		File stampWatermarkSmallImg = null;

		try {
			Map<String, Object> appMap = new HashMap<String, Object>();
			appMap.put("athg_uuid", appFileGrpCd);
			pdfUtil.mergeFileToPdf(doc, appMap, fileUploadPath);
			imageTypeProvider.setParam(mappingInfo);
			String certImageName = imageTypeProvider.getCertName();
			String ext = EdocStringUtil.getExtension(certImageName);
			String signDate = EdocStringUtil.getTodayDate("yyyy/MM/dd");

			pdfUtil.setWatermark(doc, imageTypeProvider.getWatermark(), mappingInfo);
			String stampWatermarkSmallImgPath = pdfUtil.stampTextOnImage(fileUploadPath, certImageName, ext, imageTypeProvider.getCert(), signDate);

			//이미지에 서명일자 넣기
			stampWatermarkSmallImg = new File(stampWatermarkSmallImgPath);
			pdfUtil.setCertificate(doc, stampWatermarkSmallImg, certInfo.getPfx(), certInfo.getCertPw()).save(filePathCopy);
			
		} catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "signPdf error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".signPdf(String filePath, Map<String, Object> mappingInfo, CertInfo certInfo, String appFileGrpCd) : " + e.getMessage() + e.toString());
		} finally {
			if(stampWatermarkSmallImg.exists()) stampWatermarkSmallImg.delete(); //임시 파일 삭제
		}
		
		return filePathCopy;
	}

}
