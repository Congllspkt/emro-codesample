package smartsuite.app.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.ByteStreams;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartsuite.exception.CommonException;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

public class AttachUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AttachUtils.class);
	private AttachUtils(){}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		
		if(file.exists()){
			file.delete();
		}
	}
	
	public static String getZipFile(String fileUploadPath, FileList attFileList) {
		String outputFileName = UUID.randomUUID().toString() + ".zip";
		String outputFilePath = fileUploadPath + outputFileName;
		byte[] buf = new byte[4096];
		FileInputStream fis = null;
		ZipOutputStream zop = null;
		
		try {
			zop = new ZipOutputStream(new FileOutputStream(outputFilePath));
			for(FileItem fileItem : attFileList.getItems()) {
				String orgnFileName = fileItem.getName();
				String destFileName = fileUploadPath + orgnFileName;
				
				File destFile = new File(destFileName);
				
				FileUtils.copyInputStreamToFile(fileItem.toInputStream(), destFile);
				
				fis = new FileInputStream(destFileName);
				
				ZipEntry ze = new ZipEntry(orgnFileName);
				zop.putNextEntry(ze);
				
				int len;
				while ((len = fis.read(buf)) > 0) {
					zop.write(buf, 0, len);
				}
				zop.closeEntry();
				fis.close();
				destFile.delete(); // 임시파일 삭제
			}
			zop.close();
			
		} catch (Exception e) {
			throw new CommonException("AttachUtils.getZipFile : " + e.getMessage() + e.toString());
		} finally {
			try {
				if (zop != null)
					zop.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) {
				LOG.error("AttachUtils.getZipFile error : " + e.toString());
				throw new CommonException("AttachUtils.getZipFile: " + e.getMessage(), e.toString());
			}
		}
		return outputFilePath;
	}
	
	public static void copyFile(File fOri, File fTarget) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try{
			//파일 복사 처리 프로세서
			//1. File로 원본파일 객체 생성
			File orFileObj = fOri;
			
			//2. 원파일의 크기를 지정 length()
		    //   copy할 byte 데이터 범위를 설정 new byte[원본파일 크기] - 임시로 보관할 배열객체
			int size = (int)orFileObj.length();
			//임시로 보관할 데이터 공간
			byte[] temp = new byte[size];
			
			//3. 원본파일을 넘겨주는 작업 FileInputStream read()
		    //   ==> byte[] 임시메모리에 저장
			fis = new FileInputStream(orFileObj);
			
			//임시메모리에 저장
			fis.read(temp);
			
			//4. 복사할 파일 객체 생성
			File tgFileObj = fTarget;
			
			//5. FileOutputStream write()을 통해서 복사할 파일에 입력처리
			fos = new FileOutputStream(tgFileObj);
			
			//임시의 데이터를 쓰기
			fos.write(temp);
		}catch(FileNotFoundException e){
			throw new CommonException("AttachUtils.copyFile() : " + e.getMessage() + ", File doesn't exist, OriginalFile : " + fOri.getPath() + ", TargetFile : " + fTarget.getPath());
		}catch(IOException e){
			throw new CommonException("AttachUtils.copyFile() : " + e.getMessage() + ", IOException, OriginalFile : " + fOri.getPath() + ", TargetFile : " + fTarget.getPath());
		}finally{
			try{
				if(fis != null){
					fis.close();
				}
				if(fos != null){
					fos.close();
				}
			}catch(IOException e){
				throw new CommonException("AttachUtils.copyFile() : " + e.getMessage() + ", Exception, OriginalFile : " + fOri.getPath() + ", TargetFile : " + fTarget.getPath());
			}
		}
	}

	public static byte[] getFileByte(File file){
		if(!file.exists()){
			throw new CommonException("AttachUtils.getFileByte() : " + " file is not exists");
		}

		byte[] fileByte = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			fileByte = ByteStreams.toByteArray(fis);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new CommonException("AttachUtils.getFileByte() : error :" + e.toString() + " , " + e.getStackTrace() + ", " + e.getMessage());
		}

		return fileByte;
	}
	
}
