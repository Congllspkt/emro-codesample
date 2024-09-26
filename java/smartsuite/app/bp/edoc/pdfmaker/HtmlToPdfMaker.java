package smartsuite.app.bp.edoc.pdfmaker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aspose.pdf.Document;
import com.aspose.pdf.Page;
import com.aspose.pdf.PageNumberStamp;
import com.aspose.pdf.TextStamp;

import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.HtmlToPdfMakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfSignStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfUtil;

public class HtmlToPdfMaker extends PdfMaker{
	public HtmlToPdfMaker(HtmlToPdfMakingStrategy makingStrategy, PdfSignStrategy signStrategy) {
		super(makingStrategy, signStrategy);
	}

	/**
	 * MakingStrategy => Pd4ml
	 * SignStrategy   => Aspose
	 */
	
	/**
	 * 계약서와 첨부서식사이에 PD4ML 페이지 넘김 태그 <pd4ml:page.break> 삽입해서 하나의 html로 반환
	 * @param cntrContent : 계약서 html 문자열
	 * @param appCntrContent : 첨부서식 TXT html 문자열
	 * @return 병합된 html 문자열 (cntrContent + appCntrContent)
	 */
	public String mergeHtml(String cntrContent, List<String> appCntrContent){
		StringBuilder contentSb = new StringBuilder(cntrContent);
		
		for(String appContent : appCntrContent){
			contentSb.append("<pd4ml:page.break>");
			contentSb.append(appContent);
		}
		return contentSb.toString();
	}
	
	
	/**
	 * PD4ML로 생성된 PDF에 Header, Footer 텍스트를 삽입한다.
	 * @param filePath : PD4ML로 생성된 PDF 파일
	 * @param mappingInfo : 매핑 정보
	 * @return filePath (stamp가 찍힌 PDF의 경로) (같은파일에 저장)
	 */
	public String setPdfStamp(String filePath, Map<String,Object> mappingInfo){
		PdfSignStrategy asposeStrategy = (PdfSignStrategy) signStrategy; // default로 asposeStrategy를 사용
		PdfUtil pdfUtil = asposeStrategy.getAsposeUtil();
		
		Document doc = new Document(filePath);
		
		TextStamp headerStamp = pdfUtil.makeHeaderStamp(mappingInfo);
		TextStamp footerStamp = pdfUtil.makeFooterStamp(mappingInfo);
		PageNumberStamp numberStamp = pdfUtil.makePageNumberStamp(doc);
		
		// add stamp in all pages
		Iterator<Page> pages = doc.getPages().iterator();
		while(pages.hasNext()){
			Page page = pages.next();
			page.addStamp(headerStamp);
			page.addStamp(footerStamp);
			page.addStamp(numberStamp);
		}
		
		doc.save(filePath);
		return filePath;
	}
	
	/**
	 * 페이지 넘버 stamp를 찍고 저장한다.
	 * @param filePath
	 * @return filePath
	 */
	public String setPageNumberStamp(String filePath, Map<String,Object> mappingInfo){
		PdfSignStrategy asposeStrategy = (PdfSignStrategy) signStrategy; // default로 asposeStrategy를 사용
		PdfUtil pdfUtil = asposeStrategy.getAsposeUtil();
		
		Document doc = new Document(filePath);
		
		PageNumberStamp numberStamp = pdfUtil.makePageNumberStamp(doc);
		TextStamp headerStamp = pdfUtil.makeHeaderStamp("서식명 : " + mappingInfo.get("cntrdoc_tmpl_nm"));
		
		Iterator<Page> pages = doc.getPages().iterator();
		while(pages.hasNext()){
			Page page = pages.next();
			page.addStamp(headerStamp);
			page.addStamp(numberStamp);
		}
		
		doc.save(filePath);
		return filePath;
	}
}
