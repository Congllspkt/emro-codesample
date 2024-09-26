package smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.exception.CommonException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
@Service
public class PropFileDirTypeStrategy implements ImageTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(PropFileDirTypeStrategy.class);
	@Value("#{edoc['watermark.root.path']}")
	private String watermarkRootPath;

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
		watermarkCertificateImagePath = watermarkRootPath + watermarkCertificateImage;
		watermarkImagePath = watermarkRootPath + watermarkImage;
	}

	@Override
	public String getCertName() {
		return watermarkCertificateImageName;
	}

	private InputStream getInputStream(String filePath){
		File file = new File(filePath);
		try {
			FileInputStream fileInputStream  = new FileInputStream(file);
			return fileInputStream;
		} catch (FileNotFoundException e) {
			LOG.info("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
			throw new CommonException("class :" + this.getClass() + ", getInputStream" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
		}
	}

}
