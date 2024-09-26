package smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.exception.CommonException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
@Service
public class UrlTypeStrategy implements ImageTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(UrlTypeStrategy.class);
	@Value("#{edoc['watermark.img']}")
	private String watermarkImage;
	@Value("#{edoc['watermark.s.img']}")
	private String watermarkCertificateImage;
	@Value("#{edoc['watermark.s.img.name']}")
	private String watermarkCertificateImageName;
	private String watermarkImagePath;
	private String watermarkCertificateImagePath;
	@Override
	public InputStream getWatermark() {
		return getInputStream(watermarkImagePath);
	}

	@Override
	public BufferedImage getCert() {
		try {
			BufferedImage bufferdImage = ImageIO.read(getInputStream(watermarkCertificateImagePath));
			return bufferdImage;
		} catch (IOException e) {
			LOG.info("class :" + this.getClass() + ", getCert" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
			throw new CommonException("class :" + this.getClass() + ", getCert" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
		}
	}

	@Override
	public void setParam(Map param) {
		watermarkCertificateImagePath = EdocStringUtil.getCurrentReqUri() + watermarkCertificateImage;
		watermarkImagePath = EdocStringUtil.getCurrentReqUri() + watermarkImage;
	}

	@Override
	public String getCertName() {
		return watermarkCertificateImageName;
	}

	private InputStream getInputStream(String imagePath){
		URL url = null;
		try {
			url = new URL(imagePath);
			URLConnection conn = url.openConnection();
			return conn.getInputStream();
		} catch (MalformedURLException e) {
			LOG.info("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
			throw new CommonException("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
		} catch (IOException e) {
			LOG.info("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
			throw new CommonException("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
		}
	}
}
