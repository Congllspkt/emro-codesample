package smartsuite.app.common.cert.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.common.cert.event.CertEventPublisher;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

/**
 * 전자계약 서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @author daesung lee
 * @see
 * @FileName CertService.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class CertTestPdfService {

	private static final Logger LOG = LoggerFactory.getLogger(CertTestPdfService.class);

	@Inject
	CertEventPublisher certEventPublisher;

	@Inject
	CertMgtService certMgtService;

	@Inject
	StdFileService fileService;

	public void watermarkTestPdf(HttpServletRequest request, HttpServletResponse response, String logicOrgCd, String logicOrgTypCCD) throws Exception {
		// TEST용 계약 정보
		List<Map<String,Object>> appList = new ArrayList<Map<String,Object>>();

		CertInfo certInfo = certMgtService.getCertInfo(logicOrgCd, logicOrgTypCCD);

		Map cntrInfo = Maps.newHashMap();
		cntrInfo.put("content", "워터마크 TEST");
		cntrInfo.put("cntr_no", "TEST");
		cntrInfo.put("cntr_revno", 1);
		cntrInfo.put("ctry_ccd", "KR");
		cntrInfo.put("logic_org_nm", "(주)엠로");
		cntrInfo.put("logic_org_cd", logicOrgCd);
		cntrInfo.put("logic_org_typ_ccd", logicOrgTypCCD);

		Map<String,Object> pdfAttach = certEventPublisher.generatePdfUsingHtml(cntrInfo, appList, true, certInfo, false);

		BufferedInputStream bin = null;
		BufferedOutputStream bos = null;
		OutputStream pdfOs = null;

		try {
			FileList fileList = fileService.findFileListWithContents((String) pdfAttach.get("athg_uuid"));
			FileItem fileItem = fileList.getItems().get(0);

			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			response.setHeader("Content-Disposition", "attachment; filename="+fileItem.getName());
			response.setHeader("Content-Length", "" + fileItem.getSize());
			pdfOs = response.getOutputStream();
			
			byte[] fileBytes = fileItem.toByteArray();
			pdfOs.write(fileBytes);
			pdfOs.flush();

		}catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + " watermarkTestPdf error:" + e.toString());
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=utf-8");
			throw new CommonException(this.getClass().getName()+".watermarkTestPdf : " + e.getMessage(), e.toString());
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bin != null)
					bin.close();
				if (pdfOs != null)
					pdfOs.close();
			} catch (Exception e) {
				LOG.error("class : " + this.getClass().toString() + " watermarkTestPdf error:" + e.toString());
				throw new CommonException(this.getClass().getName()+".watermarkTestPdf : " + e.getMessage(), e.toString());
			}
		}
	}
}