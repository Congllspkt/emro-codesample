package smartsuite.app.bp.edoc.pdfmaker.makingStrategy;

import java.util.List;
import java.util.Map;

import smartsuite.upload.entity.FileItem;

public interface MakingStrategy {

	public String makePdf(String param, boolean horizon);
	
	public String makePdf(String orgnFilePath, List<Map<String, Object>> appList);

	public String makePdf(String pdfFullPath, String appFileGrpId);

	public String makePdf(FileItem orgnFile);
}
