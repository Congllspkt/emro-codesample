package smartsuite.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import smartsuite.exception.CommonException;
import smartsuite.upload.entity.FileItem;

public class DownloadUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(DownloadUtil.class);
	
	public static void downloadFile(HttpServletRequest resquest, HttpServletResponse response, FileItem fileItem) {
		downloadFile(resquest, response, fileItem, null);
	}
	

	public static void downloadFile(HttpServletRequest resquest, HttpServletResponse response, FileItem fileItem, String fileName) {
		String attachFileName = fileName;
		if(Strings.isNullOrEmpty(fileName)) {
			attachFileName = fileItem.getName();
		}
		
		BufferedInputStream bin = null;
		BufferedOutputStream bos = null;
		OutputStream pdfOs = null;
		
		try{
			response.setContentType("application/octet-stream; charset=" + StandardCharsets.UTF_8.toString());
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode(attachFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"));
			response.setHeader("Content-Length", "" + fileItem.getSize());
			pdfOs = response.getOutputStream();
			
			byte[] fileBytes = fileItem.toByteArray();
			pdfOs.write(fileBytes);
			pdfOs.flush();
			
		} catch(Exception e) {
			LOG.error("class : DownloadUtil" + " downloadFile error:" + e.toString());
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=" + StandardCharsets.UTF_8.toString());
			throw new CommonException("DownloadUtil.downloadFile : " + e.getMessage(), e.toString());
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bin != null)
					bin.close();
				if (pdfOs != null)
					pdfOs.close();
			} catch (Exception e) {
				LOG.error("DownloadUtil.downloadFile error : " + e.toString());
				e.printStackTrace();
				throw new CommonException("DownloadUtil.java downloadFile: " + e.getMessage(), e.toString());
			}
		}
	}
	
	public static void  downloadFile(HttpServletRequest resquest, HttpServletResponse response, File file, String fileName) {
		String attachFileName = fileName;
		if(Strings.isNullOrEmpty(fileName)) {
			attachFileName = file.getName();
		}
		
		FileInputStream fis = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bos = null;
		OutputStream pdfOs = null;
		
		try{
			response.setContentType("application/octet-stream; charset=" + StandardCharsets.UTF_8.toString());
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode(attachFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"));
			response.setHeader("Content-Length", "" + file.length());
			pdfOs = response.getOutputStream();
			fis = new FileInputStream(file);

			byte b[] = new byte[(int) file.length()];
			int leng = 0;
			while ((leng = fis.read(b)) > 0) {
				pdfOs.write(b, 0, leng);
			}
			pdfOs.flush();
			
		} catch(Exception e) {
			LOG.error("class : DownloadUtil" + " downloadFile error:" + e.toString());
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=" + StandardCharsets.UTF_8.toString());
			throw new CommonException("DownloadUtil.downloadFile : " + e.getMessage(), e.toString());
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (bin != null)
					bin.close();
				if (fis != null)
					fis.close();
				if (pdfOs != null)
					pdfOs.close();
				if (file.exists())
					file.delete(); // 임시파일 삭제
			} catch (Exception e) {
				LOG.error("DownloadUtil.downloadFile error : " + e.toString());
				e.printStackTrace();
				throw new CommonException("DownloadUtil.java downloadFile: " + e.getMessage(), e.toString());
			}
		}
	}
}
