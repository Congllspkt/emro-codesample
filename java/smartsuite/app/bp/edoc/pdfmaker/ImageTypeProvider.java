package smartsuite.app.bp.edoc.pdfmaker;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;

public interface ImageTypeProvider {

	InputStream getWatermark();
	BufferedImage getCert();
	void setParam(Map param);
	String getCertName();

}
