package smartsuite.app.bp.edoc.pdfmaker.makingStrategy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.aspose.pdf.Document;
import com.emro.edc.EDConverter;

import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfUtil;
import smartsuite.exception.CommonException;
import smartsuite.upload.entity.FileItem;

/**
 * PD4ML을 사용하여 HTML을 PDF로 변환
 */
public class HtmlToPdfMakingStrategy implements MakingStrategy{
	
	private static final Logger LOG = LoggerFactory.getLogger(HtmlToPdfMakingStrategy.class);

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;
	@Inject
	private PdfUtil pdfUtil;

	/**
	 * PDF 생성
	 * @param cntrContent PDF 내용 (html)
	 * @param horizaon 수평,수직방향
	 * @return fileFullPath
	 */
	@Override
	public String makePdf(String cntrContent, boolean horizon) {
		String content = cntrContent;
		EDConverter converter = pdfUtil.setPdfOptions(horizon);
		
		String fileFullPath = fileUploadPath + UUID.randomUUID().toString();
		File pdfFile = new File(fileFullPath);
		
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			LOG.info("########### Make a PDF using PD4ML ###########");
			converter.useTTF("java:pdfFonts", true);
			baos = new ByteArrayOutputStream();
			converter.render(new StringReader(content), baos);
			LOG.info("########### PDF size : " + baos.size() +" bytes ###########");
			
			fos = new FileOutputStream(pdfFile);
			bos = new BufferedOutputStream(fos);
			bos.write(baos.toByteArray());
			
			LOG.info("########### complete ###########");
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "makePdf error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".makePdf() : fail.. html converter to pdf : " + e.getMessage() + e.toString());
		} finally{
			try{
				if(bos != null){
					bos.close();
				}
				if(fos != null){
					fos.close();
				}
				if(baos != null){
					baos.close();
				}
			}catch(Exception e){
				LOG.error("class : " + this.getClass().toString() + "makePdf error : " + e.toString());
				throw new CommonException(this.getClass().getName() + ".makePdf() : fail to close resources : " + e.getMessage() + e.toString());
			}
		}
		
		return fileFullPath;
	}
	
	/**
	 * PDF 생성
	 * @param pdfFullPath 계약서 PDF 파일 경로
	 * @param appList 첨부문서리스트
	 * @return fileFullPath
	 */
	@Override
	public String makePdf(String pdfFullPath, List<Map<String, Object>> appList) {
		Document doc = new Document(pdfFullPath);
		
		if(!appList.isEmpty()){
			for (Map<String, Object> appMap : appList) {
				pdfUtil.mergePdf(doc, appMap, fileUploadPath);
			}
		}
		doc.save(pdfFullPath);
		return pdfFullPath;
	}

	@Override
	public String makePdf(String pdfFullPath, String appFileGrpId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String makePdf(FileItem orgnFile) {
		throw new UnsupportedOperationException();
	}
}
