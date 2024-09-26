package smartsuite.app.bp.edoc.pdfmaker.signStrategy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aspose.pdf.Document;
import com.aspose.pdf.FileParams;
import com.aspose.pdf.FileSpecification;
import com.aspose.pdf.FontRepository;
import com.aspose.pdf.FontStyles;
import com.aspose.pdf.HorizontalAlignment;
import com.aspose.pdf.ImageStamp;
import com.aspose.pdf.PKCS7;
import com.aspose.pdf.Page;
import com.aspose.pdf.PageNumberStamp;
import com.aspose.pdf.TextStamp;
import com.aspose.pdf.VerticalAlignment;
import com.aspose.pdf.facades.FormattedText;
import com.aspose.pdf.facades.PdfFileSignature;
import com.emro.edc.EDCConstants;
import com.emro.edc.EDCPageMark;
import com.emro.edc.EDConverter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.app.bp.edoc.pdfmaker.WaterMarkText;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

/**
 *  Aspose PDF 관련 메소드
 */
@Service
public class PdfUtil {
	private static final Logger LOG = LoggerFactory.getLogger(PdfUtil.class);
	@Value("#{cert['watermark.type']}")
	private String watermarkType;
	@Inject
	StdFileService fileService;

	/**
	 * @param imagePath
	 * @return
	 * BackGround에 들어갈 imageStamp 생성
	 */
	public ImageStamp makeImageStamp(InputStream imageInputStream){
		ImageStamp imageStamp = new ImageStamp(imageInputStream);
		imageStamp.setBackground(false);
		imageStamp.setOpacity(0.3);
		imageStamp.setHorizontalAlignment(HorizontalAlignment.Center);
		imageStamp.setVerticalAlignment(VerticalAlignment.Center);
		imageStamp.setWidth(220);
		imageStamp.setHeight(220);

		return imageStamp;
	}

	/**
	 * @param filePath
	 * @param imagePath
	 * @param mappingInfo
	 * @return
	 * PDF에 WaterMark를 삽입
	 */
	public Document setWatermark(Document document, InputStream imageInputStram, Map<String,Object> mappingInfo) {

		// create stamp
		ImageStamp imageStamp = makeImageStamp(imageInputStram);
		TextStamp headerStamp = makeHeaderStamp(mappingInfo);
		TextStamp footerStamp = makeFooterStamp(mappingInfo);
		PageNumberStamp numberStamp = makePageNumberStamp(document);

		// add stamp in all pages
		Iterator<Page> pages = document.getPages().iterator();
		while(pages.hasNext()){
			Page page = pages.next();
			page.addStamp(imageStamp);
			page.addStamp(headerStamp);
			page.addStamp(footerStamp);
			page.addStamp(numberStamp);
		}
		imageStamp.close();

		return document;
	}

	/**
	 * @param mappingInfo
	 * @return
	 * Header에 들어갈 WaterMark Text 생성
	 */
	public TextStamp makeHeaderStamp(Map<String,Object> mappingInfo){
		String headerText = "";

		String cntrNo = mappingInfo.get("cntr_no") == null? "" : mappingInfo.get("cntr_no").toString();
		if(("null").equals(cntrNo)) cntrNo = "";
		
		if("KR".equals(mappingInfo.get("ctry_ccd"))){
			headerText = WaterMarkText.HEADERTEXT_KR.replace("@cntr_no@", cntrNo);
		}else{
			headerText = WaterMarkText.HEADERTEXT_EN.replace("@cntr_no@", cntrNo);
		}
		
		return setTextStamp(new TextStamp(headerText), HorizontalAlignment.Left, VerticalAlignment.Top);
	}
	
	/**
	 * @param text
	 * @return
	 * Header에 들어갈 WaterMark Text 생성
	 */
	public TextStamp makeHeaderStamp(String text){
		String headerText = (Strings.isNullOrEmpty(text)) ? "" : text;
		return setTextStamp(new TextStamp(headerText), HorizontalAlignment.Left, VerticalAlignment.Top);
	}
	
	/**
	 * @param mappingInfo
	 * @return
	 * Footer에 들어갈 WaterMark text stamp를 생성
	 */
	public TextStamp makeFooterStamp(Map<String,Object> mappingInfo){
		String footerText = "";
		String orgNm = (String) mappingInfo.get("logic_org_nm");
		String orgEnNm = (String) mappingInfo.get("logic_org_nm_en");
		
		if("KR".equals(mappingInfo.get("ctry_ccd"))){
			footerText = WaterMarkText.FOOTERTEXT_KR.replace("@org_nm@", orgNm);
		}else{
			footerText = WaterMarkText.FOOTERTEXT_EN.replace("@org_nm@", orgEnNm);
		}
		
		FormattedText footerFormattedText = new FormattedText();
		String[] lines = footerText.split("\n");
		for(String line : lines){
			footerFormattedText.addNewLineText(line);
		}
		
		return setTextStamp(new TextStamp(footerFormattedText), HorizontalAlignment.Left, VerticalAlignment.Bottom);
	}
	
	/**
	 * @param text
	 * @return
	 * Footer에 들어갈 WaterMark text stamp를 생성
	 */
	public TextStamp makeFooterStamp(String text){
		String footerText = (Strings.isNullOrEmpty(text)) ? "" : text;
		FormattedText footerFormattedText = new FormattedText();
		String[] lines = footerText.split("\n");
		for(String line : lines){
			footerFormattedText.addNewLineText(line);
		}
		return setTextStamp(new TextStamp(footerFormattedText), HorizontalAlignment.Left, VerticalAlignment.Bottom);
	}
	
	/**
	 * @param document
	 * @return
	 * Footer에 들어갈 WaterMark pageNumber stamp를 생성
	 */
	public PageNumberStamp makePageNumberStamp(Document document){
		PageNumberStamp stamp = new PageNumberStamp();
		stamp.setFormat("Page # of " + document.getPages().size());
		stamp.setBottomMargin(20);
		stamp.setRightMargin(15);
		stamp.setHorizontalAlignment(HorizontalAlignment.Right);
		stamp.setStartingNumber(1);
		
		// set text properties
		stamp.getTextState().setFont(new FontRepository().findFont("gulim", true));
		stamp.getTextState().setFontSize(10);
		stamp.getTextState().setFontStyle(FontStyles.Bold);
		return stamp;
	}
	
	/**
	 * @param stamp
	 * @param horizontalAlignment
	 * @param verticalAlignment
	 * @return
	 * TextStamp properties 설정
	 */
	public TextStamp setTextStamp(TextStamp stamp, int horizontalAlignment, int verticalAlignment){
		if(verticalAlignment == VerticalAlignment.Bottom){
			stamp.setBottomMargin(20); // footer
		}else{
			stamp.setTopMargin(20);    // header
		}
		stamp.setLeftMargin(15);
		stamp.setHorizontalAlignment(horizontalAlignment);
		stamp.setVerticalAlignment(verticalAlignment);
		
		// set text properties
		stamp.getTextState().setFont(new FontRepository().findFont("gulim", true));
		stamp.getTextState().setFontSize(10);
		stamp.getTextState().setFontStyle(FontStyles.Bold);
		return stamp;
	}
	
	public Document addAttachment(Document document, List<Map<String,Object>> attachList, String uploadRoot) {
		
		for(Map<String,Object> attach : attachList){
			String fileName = attach.get("athf_orig_nm").toString();
			String filePath = uploadRoot + attach.get("athf_path");
			LOG.info("filePath : " + filePath);
			FileSpecification fileSpec = makeFileSpec(filePath, fileName);
			document.getEmbeddedFiles().add(fileSpec);
			
			// annotation 사용시(Table 안에 attach image가 들어갈경우 고려 필요)
			//Page page = document.getPages().get_Item(document.getPages().size()); 
			//FileAttachmentAnnotation annotation = makeFileAnnotation(filePath, fileName, page);
		}
		
		return document;
	}
	
	/**
	 * FileSpec 객체 생성
	 * @param filePath
	 * @param fileName
	 * @return
	 */
	public FileSpecification makeFileSpec(String filePath, String fileName){
		FileInputStream fis = null;
		FileSpecification fileSpecification = null;
		try {
			fis = new FileInputStream(filePath);
			fileSpecification = new FileSpecification(fis, fileName);
		} catch (FileNotFoundException e) {
			LOG.error("class : " + this.getClass().toString() + "makeFileSpec error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".makeFileSpec() : fileNotFoundException" + "filePath : " + filePath);
		}finally{
			try {
				if(null != fis) fis.close();
			} catch (IOException io) {
				LOG.error("class : " + this.getClass().toString() + "makeFileSpec error : " + io.toString());
				throw new CommonException(this.getClass().getName() + ".makeFileSpec() : fileNotFoundException" + "filePath : " + filePath);
			} catch (Exception e){
				LOG.error("class : " + this.getClass().toString() + "makeFileSpec error : " + e.toString());
				throw new CommonException(this.getClass().getName() + ".makeFileSpec() : fileNotFoundException" + "filePath : " + filePath);
			}
		}
		
		return fileSpecification;
	}
	
	/**
	 * FileSpec 객체 생성 (NEW)
	 * @param fileItem
	 * @return
	 */
	public FileSpecification makeFileSpec(FileItem fileItem) {
		FileSpecification fileSpecification = null;
		try {
			fileSpecification = new FileSpecification(fileItem.toInputStream(), fileItem.getName());
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "makeFileSpec error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".makeFileSpec(File file, String fileName) : " + e.getMessage() + e.toString());
		}
		return fileSpecification;
	}
	
	/**
	 * FileAnnotation 객체 생성
	 * @param filePath 파일경로
	 * @param fileName 파일이름
	 * @param page     파일이 첨부될 페이지
	 * @return
	 */
	/*public FileAttachmentAnnotation makeFileAnnotation(String filePath, String fileName, Page page){ //NOPMD, 추후 사용될 수 있어서 만들어놓음
		FileSpecification fileSpec = makeFileSpec(filePath, fileName);
		com.aspose.pdf.Rectangle rect = new com.aspose.pdf.Rectangle(100, 100, 50, 50);
		FileAttachmentAnnotation annotation = new FileAttachmentAnnotation(page, rect, fileSpec);
		annotation.setIcon(FileIcon.Paperclip);
		return annotation;
	}*/
	
	/**
	 * PDF merge
	 * @param doc
	 * @param pdfPathList
	 * @return
	 */
	public Document mergePdf(Document mergedPdf, List<String> pdfPathList){
		
		for(String pdfPath : pdfPathList){
			Document doc = new Document(pdfPath);
			mergedPdf.getPages().add(doc.getPages());
			doc.save(pdfPath);
		}
		
		return mergedPdf;
	}
	
	/**
	 * @param filePath
	 * @param imagePath
	 * @param certByte
	 * @param certPw
	 * @return
	 * PDF에 인증서를 삽입
	 */
	public PdfFileSignature setCertificate(String filePath, File image, byte[] certByte, String certPw) {
		// 1. open document and save document to stream object
		Document document = new Document(filePath);
		return setCertificate(document, image, certByte, certPw);
	}
	
	/**
	 * @param doc
	 * @param imagePath
	 * @param certByte
	 * @param certPw
	 * @return
	 * PDF에 인증서를 삽입
	 */
	public PdfFileSignature setCertificate(Document doc, File image, byte[] certByte, String certPw) {
		OutputStream out = new ByteArrayOutputStream();
		doc.save(out);
		
		double height = doc.getPages().get_Item(1).getRect().getHeight() - 10;
		double width  = doc.getPages().get_Item(1).getRect().getWidth() - 10;
		
		LOG.info("height : " + height);
		LOG.info("width : " + width);
		
		// 2. create PdfFileSignature instance
		PdfFileSignature pdfSign = new PdfFileSignature();
		
		// 3. bind the source PDF by reading contents of Stream
		PKCS7 pkcs7 = new PKCS7(new ByteArrayInputStream(certByte), certPw);
		pkcs7.setShowProperties(false);
		pdfSign.bindPdf(new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray()));
		pdfSign.sign(1, true, new Rectangle((int)(width-70), (int)(height-70), 65, 65), pkcs7); // 수직시 사용
		//pdfSign.sign(1, true, new Rectangle((int)(height-70), (int)(width-70), 65, 65), pkcs7); // 수평시 사용
		try {
			pdfSign.setSignatureAppearanceStream(new FileInputStream(image));
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "setCertificate error : " + e.toString());
			throw new  CommonException(this.getClass().getName()+".setCertificate : "+ e.getMessage() + e.toString() + "watermarkType : " + watermarkType);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		
		return pdfSign;
	}
	
	/**
	 * 첨부파일리스트에서 PDF 분리 (merge시 사용)
	 * @param attachList 첨부파일 리스트
	 * @return pdfList   PDF 리스트
	 */
	public List<String> separatePdfAttach(String fileUploadPath, List<Map<String,Object>> attachList){
		List<String> pdfList = Lists.newArrayList();
		Iterator<Map<String,Object>> iter = attachList.iterator();
		while(iter.hasNext()){
			Map<String,Object> attach = iter.next();
			
			String attachName = attach.get("athf_orig_nm").toString().toLowerCase();
			String extension = EdocStringUtil.getExtension(attachName);
			
			if("pdf".equals(extension)){
				// PDF
				pdfList.add(fileUploadPath + attach.get("athf_path"));
				iter.remove(); // PDF는 attachList에서 삭제
			}else if("doc".equals(extension) || "docx".equals(extension)){ 
				// WORD
				try{
					String convertedPdfPath = fileUploadPath + UUID.randomUUID().toString();
					com.aspose.words.Document wordDoc = new com.aspose.words.Document(fileUploadPath + attach.get("athf_path"));
					wordDoc.save(convertedPdfPath, com.aspose.words.SaveFormat.PDF);
					pdfList.add(convertedPdfPath);
					iter.remove();
				}catch(Exception e){
					LOG.error("###WORD CONVERT TO PDF ERROR###");
					throw new  CommonException(this.getClass().getName()+".separatePdfAttach : "+ e.getMessage() + "WORD_PATH :" +  attach.get("athf_path"));
				}
			}
		}
		
		return pdfList;
	}

	/**
	 * @param doc
	 * @param imagePath
	 * @param certPath
	 * @param certPw
	 * @return
	 * PDF에 인증서를 삽입
	 */
	public PdfFileSignature setCertificate(Document doc, String imagePath, String certPath, String certPw) {
		OutputStream out = new ByteArrayOutputStream();
		doc.save(out);
		
		double height = doc.getPages().get_Item(1).getPageInfo().getHeight();
		double width  = doc.getPages().get_Item(1).getPageInfo().getWidth();
		
		LOG.info("height : " + height);
		LOG.info("width : " + width);
		
		// 2. create PdfFileSignature instance
		PdfFileSignature pdfSign = new PdfFileSignature();
		
		// 3. bind the source PDF by reading contents of Stream
		PKCS7 pkcs7 = new PKCS7(certPath, certPw);
		pkcs7.setShowProperties(false);
		pdfSign.bindPdf(new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray()));
		pdfSign.sign(1, true, new Rectangle((int)(width-70), (int)(height-70), 65, 65), pkcs7); // 수직시 사용
		//pdfSign.sign(1, true, new Rectangle((int)(height-70), (int)(width-70), 65, 65), pkcs7); // 수평시 사용
		try {
			pdfSign.setSignatureAppearanceStream(new FileInputStream(new File(imagePath)));
			//pdfSign.setSignatureAppearanceStream(new URL(imagePath).openConnection().getInputStream());
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "setCertificate error : " + e.toString());
			throw new  CommonException(this.getClass().getName()+".setCertificate : "+ e.getMessage() + e.toString() + "imagePath(URL) : " +  imagePath);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
		
		return pdfSign;
	}
	
	public String stampTextOnImage(String fileUploadPath, String imageName, String imageFormat, BufferedImage bufferedImage, String text) {
		String stampImagePath = "";

		try{
			BufferedImage image = bufferedImage;

			Graphics g = image.getGraphics();
			Color c = new Color(0,0,0); //검정색
			g.setColor(c);
			int height = image.getHeight() / 2; 
			int width = image.getWidth() / 2;
			g.drawString(text, width-30, height + 27); 
			stampImagePath = fileUploadPath + "stamp_" +imageName;
			g.dispose(); ImageIO.write(image,imageFormat, new File(stampImagePath)); 
		} catch (MalformedURLException e){ 
			LOG.error("class : " + this.getClass().toString() + "stampTextOnImage error : " + e.toString());
			throw new  CommonException(this.getClass().getName()+".StampTextOnImage : "+ e.getMessage() + e.toString());
		} catch (IOException e) {
			LOG.error("class : " + this.getClass().toString() + "stampTextOnImage error : " + e.toString());
			throw new  CommonException(this.getClass().getName()+".StampTextOnImage : "+ e.getMessage() + e.toString());
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "stampTextOnImage error : " + e.toString());
		    throw new  CommonException(this.getClass().getName()+".StampTextOnImage : "+ e.getMessage() + e.toString());
        }
		
		return stampImagePath;
	}

	public Document mergePdf(Document mergedPdf, String pdfPath){
		Document doc = new Document(pdfPath);
		mergedPdf.getPages().add(doc.getPages());
		//doc.save(pdfPath);
		return mergedPdf;
	}
	
	public Document mergePdf(Document mergedPdf, InputStream io){
        Document doc = new Document(io);
        mergedPdf.getPages().add(doc.getPages());
        return mergedPdf;
    }
	
	/**
	 * PDF에 첨부서류 파일 add (NEW)
	 * @param document
	 * @param fileItem
	 * @return
	 */
	public Document addAttachment(Document document, FileItem fileItem) {
		FileSpecification fileSpec = this.makeFileSpec(fileItem);
		document.getEmbeddedFiles().add(fileSpec);
		
		FileParams fileParam = new FileParams(fileSpec);
		Date nowDate = new Date();
		fileParam.setModDate(nowDate);
		fileSpec.setParams(fileParam);
		
		// annotation 사용시(Table 안에 attach image가 들어갈경우 고려 필요)
		//Page page = document.getPages().get_Item(document.getPages().size()); 
		//FileAttachmentAnnotation annotation = makeFileAnnotation(filePath, fileName, page);
		
		return document;
	}
	
	/**
	 * file을 pdf와 합친다.
	 * @param doc
	 * @param appMap
	 * @param fileUploadPath
	 */
	public void mergeFileToPdf(Document doc, Map<String, Object> appMap, String fileUploadPath) {
		InputStream fis = null;
		
		if(MapUtils.isNotEmpty(appMap) && appMap.containsKey("athg_uuid") && null != appMap.get("athg_uuid")) {
			try {
				FileList fileList = fileService.findFileListWithContents((String) appMap.get("athg_uuid"));
				
				for (FileItem fileItem : fileList.getItems()) {
					fis = fileItem.toInputStream();
					String extension = fileItem.getExtension();
					
					if("pdf".equals(extension)) {
						// PDF
						mergePdf(doc, fis);
						
					} else if("doc".equals(extension) || "docx".equals(extension)) { 
						// WORD
						String convertedPdfPath = fileUploadPath + UUID.randomUUID().toString();
						com.aspose.words.Document wordDoc = new com.aspose.words.Document(fis);
						wordDoc.save(convertedPdfPath, com.aspose.words.SaveFormat.PDF);
						mergePdf(doc, convertedPdfPath);
						
					}else {
						this.addAttachment(doc, fileItem);
					}
				}
			} catch (Exception e) {
				LOG.error("class : " + this.getClass().toString() + "mergeFileToPdf error : " + e.toString());
				throw new CommonException(this.getClass().getName() + ".mergeFileToPdf(Document doc, Map<String, Object> appMap, String fileUploadPath) : " + e.getMessage() + e.toString());
			} finally {
				try{
					if(fis != null) fis.close();
				}catch (Exception e){
					LOG.error("class : " + this.getClass().toString() + "mergeFileToPdf error : " + e.toString());
					throw new CommonException(this.getClass().getName() + ".mergeFileToPdf(Document doc, Map<String, Object> appMap, String fileUploadPath) : " + e.getMessage() + e.toString());
				}
			}
		}
	}
	
	/**
	 * Text와 pdf를 합친다.
	 * @param doc
	 * @param appMap
	 * @param converter
	 * @param fileUploadPath
	 */
	public void mergeTextToPdf(Document doc, Map<String, Object> appMap, EDConverter converter, String fileUploadPath) {
		String content = (String) appMap.get("app_content");
		File tempFile = makeTempPdfFile(content, converter, fileUploadPath);
		
		if(null != tempFile && tempFile.exists()){
			mergePdf(doc, tempFile.getPath());
			tempFile.delete(); // 즉시삭제
		}
	}

	/**
	 * @param appMap
	 * @param converter
	 * @param fileUploadPath
	 * @return
	 */
	public File makeTempPdfFile(String content, EDConverter converter, String fileUploadPath) {
		File tempFile = null;
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		String fileName = UUID.randomUUID().toString();
		try {
			tempFile =File.createTempFile("temp", fileName, new File(fileUploadPath));
			LOG.info("########### Make a PDF using PD4ML ###########");
			converter.useTTF("java:pdfFonts", true);
			baos = new ByteArrayOutputStream();
			
			converter.render(new StringReader(content), baos);
			LOG.info("########### PDF size : " + baos.size() +" bytes ###########");
			
			fos = new FileOutputStream(tempFile);
			bos = new BufferedOutputStream(fos);
			bos.write(baos.toByteArray());
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "makeTempPdfFile error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".makeTempPdfFile() : fail.. html converter to pdf : " + e.getMessage() + e.toString());
		} finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(bos != null){
					bos.close();
				}
				if(fos != null){
					fos.close();
				}
			}catch(Exception e){
				LOG.error("class : " + this.getClass().toString() + "makeTempPdfFile error : " + e.toString());
				throw new CommonException(this.getClass().getName() + ".makeTempPdfFile() : fail to close resources : " + e.getMessage() + e.toString());
			}
		}
		return tempFile;
	}
	
	public void mergePdf(Document doc, Map<String, Object> appMap, String fileUploadPath) {
		String docType = (String) appMap.get("doc_type");
		EDConverter converter = setPdfOptions(false);
		if("FILE".equals(docType) || "FILE_LIST".equals(docType)){
			//첨부서식 구분 FILE
			mergeFileToPdf(doc, appMap, fileUploadPath);
		}else if("TXT".equals(docType)){
			//첨부서식 구분 TEXT
			mergeTextToPdf(doc, appMap, converter, fileUploadPath);
		}
	}
	
	public EDConverter setPdfOptions(boolean horizon){
		EDConverter converter = new EDConverter();
		converter.enableDebugInfo(); // enables an output of debug messages.
		converter.setHtmlWidth(830);
		
		if(horizon){
			converter.setPageSize(converter.changePageOrientation(EDCConstants.A4));
		}else{
			converter.setPageSize(EDCConstants.A4);
		}
		
		EDCPageMark header = new EDCPageMark();
		EDCPageMark footer = new EDCPageMark();
		
		header.setTitleTemplate("");
		header.setAreaHeight(20);
		footer.setTitleTemplate("");
		footer.setAreaHeight(30);
		
		converter.setPageHeader(header);
		converter.setPageFooter(footer);
		return converter;
	}
}
