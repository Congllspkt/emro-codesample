package smartsuite.app.bp.edoc.pdfmaker.signStrategy;

import java.util.List;
import java.util.Map;

import smartsuite.app.common.cert.pki.CertInfo;

public interface SignStrategy {
	
	public String signPdf(String filePath, Map<String,Object> mappingInfo, CertInfo certInfo, List<Map<String,Object>> appList);
	
	public String signPdf(String filePath, Map<String,Object> mappingInfo, CertInfo certInfo, String appFileGrpCd);
}
