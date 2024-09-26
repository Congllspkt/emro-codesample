package smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy.event.ImageTypeEventPublisher;
import smartsuite.app.common.AttachService;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileList;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class DBAttachFileTypeStrategy implements ImageTypeProvider {
	private static final Logger LOG = LoggerFactory.getLogger(DBAttachFileTypeStrategy.class);
	private String logicOrgCd;
	private String logicOrgTypCcd;

	private static String WTM_CT_IMG_ATHG_UUID = "wtm_ct_img_athg_uuid";
	private static String WTM_AUTH_IMG_ATHG_UUID = "wtm_auth_img_athg_uuid";
	private Map watermarkImageAttachMap = Maps.newHashMap();
	private Map watermarkCertificateImageAttachMap = Maps.newHashMap();
	@Value("#{globalProperties['server.cls']}")
	private String serverCls;
	@Inject
	StdFileService fileService;
	@Inject
	ImageTypeEventPublisher imageTypeEventPublisher;

	@Inject
	AttachService attachService;

	private InputStream getInputStream(Map watermarkImageAttachMap) {
		Map param = Maps.newHashMap();
		param.put("logic_org_cd", logicOrgCd);
		param.put("logic_org_typ_ccd", logicOrgTypCcd);
		Map data = imageTypeEventPublisher.findOrgCertInfo(param);
		FileList fileList = null; // grp_cd

		param.put("athg_uuid", (String)watermarkImageAttachMap.get("athg_uuid"));
		List<Map<String,Object>> attachList = attachService.findListAttach(param);
		Map<String,Object>attach = attachService.findAttachByAttId(attachList.get(0));
		byte[] fileContent = (byte[]) attach.get("athf_orig_dat");
		return new ByteArrayInputStream(fileContent);

	}
	@Override
	public void setParam(Map param) {
		logicOrgCd = (String)param.get("logic_org_cd");
		logicOrgTypCcd = (String)param.get("logic_org_typ_ccd");

		if( Strings.isNullOrEmpty(logicOrgCd)
			|| Strings.isNullOrEmpty(logicOrgTypCcd)) {
			LOG.info("class :" + this.getClass() + ", setParam fuction logic_org_cd, logic_org_typ_ccd is null");
			throw new CommonException("class :" + this.getClass() + ", setParam fuction logic_org_cd, logic_org_typ_ccd is null");
		}
		Map data = imageTypeEventPublisher.findOrgCertInfo(param);
		param.put("athg_uuid", data.get(WTM_AUTH_IMG_ATHG_UUID));
		List<Map<String,Object>> watermarkImageAttachList = attachService.findListAttach(param);
		watermarkImageAttachMap = attachService.findAttachByAttId(watermarkImageAttachList.get(0));


		param.put("athg_uuid", data.get(WTM_CT_IMG_ATHG_UUID));
		List<Map<String,Object>> watermarkCertificateImageAttachList = attachService.findListAttach(param);
		watermarkCertificateImageAttachMap = attachService.findAttachByAttId(watermarkCertificateImageAttachList.get(0));

	}

	@Override
	public String getCertName() {
		return (String) watermarkCertificateImageAttachMap.get("athf_orig_nm");
	}

	@Override
	public InputStream getWatermark(){

		return this.getInputStream(watermarkImageAttachMap);
	}
	@Override
	public BufferedImage getCert(){
		try {
			BufferedImage bufferdImage = ImageIO.read(this.getInputStream(watermarkCertificateImageAttachMap));
			return bufferdImage;
		} catch (IOException e) {
			LOG.info("class :" + this.getClass() + ", getCert" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
			throw new CommonException("class :" + this.getClass() + ", getCert" + e.toString() + ", " + e.getMessage() + ", " + e.getStackTrace());
		}
	}

}
