package smartsuite.app.common.cert.pki;

import java.io.File;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartsuite.exception.CommonException;
import smartsuite.upload.entity.FileItem;

/**
 * @since 2018. 8. 29.
 * @author sunghyun
 * ------------------------
 * 개정이력
 * 2018. 8. 29. sunghyun : 최초작성
 * 서명관련 유틸 클래스
 */
public class SignatureUtil {
	private static final String DEFAULT_ALGORITHM = "SHA-256";
	private static final Logger LOG = LoggerFactory.getLogger(SignatureUtil.class);
	
	private SignatureUtil(){}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : byte[]
	 * @Return : String
	 * base64인코딩
	 */
	public static String encodeBase64(byte[] param){
		String encodedStr = Base64.encodeBase64String(param);
		return encodedStr;
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : byte[]
	 * @Return : byte[]
	 * base64 디코딩
	 */
	public static byte[] decodeBase64(byte[] param){
		return Base64.decodeBase64(param);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : String 
	 * @Return : String
	 * filePath에 존재하는 file을 SHA-256 해쉬값을 구한 후 Base64 인코딩하여 반환
	 */
	public static String getHashValueFromFile(String filePath){
		byte[] fileByteArray = null;
		byte[] hashByte = null;
		
		try{
			fileByteArray = FileUtils.readFileToByteArray(new File(filePath));
			MessageDigest md = MessageDigest.getInstance(DEFAULT_ALGORITHM);
			md.update(fileByteArray);
			hashByte = md.digest();
		} catch (Exception e) {
			LOG.error("error : 해쉬값 변환 실패 :" + "SignatureUtil.java getHashValueFromFile "+ e.toString());
			throw new CommonException("getHashValueFromFile(String filePath) :" + e.getMessage(), "fail to getHashValue");
		}
		
		return encodeBase64(hashByte);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : String String
	 * @Return : String
	 * filePath에 존재하는 file을 입력받은 해쉬함수로 값을 구한 후 Base64 인코딩하여 반환
	 */
	public static String getHashValueFromFile(String filePath, String hashAlgorithm){
		byte[] fileByteArray = null;
		byte[] hashByte = null;
		
		try{
			fileByteArray = FileUtils.readFileToByteArray(new File(filePath));
			MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
			md.update(fileByteArray);
			hashByte = md.digest();
		} catch (Exception e) {
			LOG.error("error : hash값 변환 실패" + "SignatureUtil.java getHashValueFromFile(String filePath, String hashAlgorithm)"  + e.toString());
			throw new CommonException("getHashValueFromFile(String filePath, String hashAlgorithm) :" + e.getMessage(), "fail to getHashValue");
		}
		
		return encodeBase64(hashByte);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : String
	 * @Return : String
	 * 문자열의 SHA-256 해쉬값을 구한 후 Base64 인코딩하여 반환
	 */
	public static String getHashValueFromStr(String str){
		byte[] strBytes = str.getBytes();
		byte[] hashByte = null;
		
		try{
			MessageDigest md = MessageDigest.getInstance(DEFAULT_ALGORITHM);
			md.update(strBytes);
			hashByte = md.digest();
		} catch (Exception e) {
			LOG.error("ERROR : HASH값 변환 실패 " + "getHashValueFromStr(String Str) :" + e.getMessage(), "fail to getHashValue");
			throw new CommonException("getHashValueFromStr(String Str) :" + e.getMessage(), "fail to getHashValue");
		}
		
		return encodeBase64(hashByte);
	}
	
	/**
	 * @Author : sunghyun
	 * @Date   : 2018. 8. 29.
	 * @Param  : Map<String,String>
	 * @Return : void
	 * Map안의 내용을 Log로 찍어줌
	 */
	public static void printMap(Map<String,String> certInfo){
		Iterator<Map.Entry<String,String>> iter = certInfo.entrySet().iterator();
		
		LOG.info("======== map extraction ========");
		while(iter.hasNext()){
			Map.Entry<String, String> entry = iter.next();
			LOG.info("{} : {}", entry.getKey(), entry.getValue());
		}
		LOG.info("======== map extraction ========");
	}
	
	
	/**
	 * filed의 SHA-256 해쉬값을 구한 후 Base64 인코딩하여 반환
	 * @param file
	 * @return
	 */
    public static String getHashValueFromFile(File file) {
        byte[] fileByteArray = null;
        byte[] hashByte = null;
        
        try{
            fileByteArray = FileUtils.readFileToByteArray(file);
            MessageDigest md = MessageDigest.getInstance(DEFAULT_ALGORITHM);
            md.update(fileByteArray);
            hashByte = md.digest();
        } catch (Exception e) {
        	LOG.error("SignatureUtil.getHashValueFromFile error : " + e.toString());
            throw new CommonException("getHashValueFromFile(File file) : " + e.getMessage(), "fail to getHashValue");
        }
        
        return encodeBase64(hashByte);
    }

    /**
     * file의 SHA-256 해쉬값을 구한 후 Base64 인코딩하여 반환
     * @param fileItem
     * @return
     */
    public static String getHashValueFromFile(FileItem fileItem) {
        byte[] fileByteArray = null;
        byte[] hashByte = null;
        
        try{
            fileByteArray = fileItem.toByteArray();
            MessageDigest md = MessageDigest.getInstance(DEFAULT_ALGORITHM);
            md.update(fileByteArray);
            hashByte = md.digest();
        } catch (Exception e) {
        	LOG.error("SignatureUtil.getHashValueFromFile error : " + e.toString());
            throw new CommonException("getHashValueFromFile(FileItem fileItem) : " + e.getMessage(), "fail to getHashValue");
        }
        
        return encodeBase64(hashByte);
    }
}
