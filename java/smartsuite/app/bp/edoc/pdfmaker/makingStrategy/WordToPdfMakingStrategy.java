package smartsuite.app.bp.edoc.pdfmaker.makingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfUtil;
import smartsuite.exception.CommonException;
import smartsuite.upload.entity.FileItem;

/**
 * ASPOSE.WORD를 사용하여 WORD를 PDF로 변환
 */
public class WordToPdfMakingStrategy implements MakingStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(WordToPdfMakingStrategy.class);
	
	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;
	@Inject
	private PdfUtil pdfUtil;
	
	
	@Override
	public String makePdf(String param, boolean horizon) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String makePdf(String pdfFullPath, List<Map<String, Object>> appList) {
		throw new UnsupportedOperationException();
	}

	/**
	 * PDF 생성
	 * @param pdfFullPath 계약서 PDF 파일 경로
	 * @param appFileGrpId 첨부문서 파일그룹 uuid
	 * @return fileFullPath
	 */
	@Override
	public String makePdf(String pdfFullPath, String appFileGrpId) {
		com.aspose.pdf.Document doc = new com.aspose.pdf.Document(pdfFullPath);
		
		Map<String, Object> appMap = new HashMap<String, Object>();
		appMap.put("athg_uuid", appFileGrpId);
		
		pdfUtil.mergeFileToPdf(doc, appMap, fileUploadPath);
		doc.save(pdfFullPath);
		return pdfFullPath;
	}
	
	/**
	 * PDF 생성
	 * @param orgnFile PDF로 변환할 fileItem
	 */
	@Override
	public String makePdf(FileItem orgnFile) {
		String fileFullPath = fileUploadPath + UUID.randomUUID().toString();
		try {
			Document doc = new Document(orgnFile.toInputStream());
			doc.save(fileFullPath, SaveFormat.PDF);
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "makePdf error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".makePdf(File orgnFile) : " + e.getMessage() + e.toString());
		}
		return fileFullPath;
	}
}
