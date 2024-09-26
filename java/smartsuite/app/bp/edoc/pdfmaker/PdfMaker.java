package smartsuite.app.bp.edoc.pdfmaker;

import java.util.List;
import java.util.Map;

import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.HtmlToPdfMakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.makingStrategy.MakingStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.PdfSignStrategy;
import smartsuite.app.bp.edoc.pdfmaker.signStrategy.SignStrategy;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.upload.entity.FileItem;

public class PdfMaker {
	
	protected MakingStrategy makingStrategy; // PDF생성관련 전략 객체
	protected SignStrategy signStrategy;	 // PDF서명관련 전략 객체

	public PdfMaker(MakingStrategy makingStrategy, SignStrategy signStrategy) {
		this.makingStrategy = makingStrategy;
		this.signStrategy = signStrategy;
	}

	public void setSignStrategy(SignStrategy signStrategy) {
		this.signStrategy = signStrategy;
	}

	public void setMakingStrategy(MakingStrategy strategy) {
		this.makingStrategy = strategy;
	}
	
	public String makePdf(String param, boolean horizon){
		return makingStrategy.makePdf(param, horizon);
	}

	public String makePdf(String pdfFullPath, List<Map<String, Object>> appList){
		return makingStrategy.makePdf(pdfFullPath, appList);
	}
	
	public String makePdf(String pdfFullPath, String appFileGrpId){
		return makingStrategy.makePdf(pdfFullPath, appFileGrpId);
	}
	
	public String makePdf(FileItem orgnFile){
		return makingStrategy.makePdf(orgnFile);
	}
	
	public String signPdf(String filePath, Map<String,Object> mappingInfo, CertInfo certInfo, List<Map<String,Object>> appList){
		return signStrategy.signPdf(filePath, mappingInfo, certInfo, appList);
	}
	
	public String signPdf(String filePath, Map<String,Object> mappingInfo, CertInfo certInfo, String appFileGrpCd){
		return signStrategy.signPdf(filePath, mappingInfo, certInfo, appFileGrpCd);
	}
}
