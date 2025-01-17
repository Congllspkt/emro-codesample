package smartsuite.security.web.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import smartsuite.exception.CommonException;
import smartsuite.security.core.crypto.CipherUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;

public class DecryptRequestWrapper extends HttpServletRequestWrapper {

    CipherUtil cipherUtil;
    
    static final Logger LOG = LoggerFactory.getLogger(DecryptRequestWrapper.class);

    private String secParamEncrypt;
    
	private InputStream is;

	public DecryptRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		this.is = super.getInputStream();
	}
	
	public DecryptRequestWrapper(HttpServletRequest request, CipherUtil cipher, String property) throws IOException {
		super(request);
		this.is = super.getInputStream();
		this.cipherUtil = cipher;
		this.secParamEncrypt = property;
		decrypt(is);
	}
	
	protected boolean isMultipart() {
		// Same check as in Commons FileUpload...
		HttpServletRequest request = (HttpServletRequest) this.getRequest();
		if (!"post".equals(request.getMethod().toLowerCase(LocaleContextHolder.getLocale()))) {
			return false;
		}
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase(LocaleContextHolder.getLocale()).startsWith("multipart/"));
	}

	protected boolean isAjax() {
		// Same check as in Commons FileUpload...
		HttpServletRequest request = (HttpServletRequest) this.getRequest();
		if (!"post".equals(request.getMethod().toLowerCase(LocaleContextHolder.getLocale()))) {
			return false;
		}
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase(LocaleContextHolder.getLocale()).contains("json"));
	}	
	
	private boolean isEncryptParameter(){
		if("skip".equals(secParamEncrypt))
			return false;
		
		HttpServletRequest request = (HttpServletRequest) this.getRequest();
		String isEncypt = request.getHeader("content-chipher");
		String menucode = request.getHeader("menucode");
		boolean isEncrypt = Boolean.valueOf(isEncypt); 
		if(!isEncrypt)
			if(menucode != null && !"undefined".equals(menucode)) isEncrypt = true;
		
		return isEncrypt;
	}
	
    @SuppressWarnings("resource")
	public void decrypt(InputStream in) {
    	if(isEncryptParameter() && !isMultipart() && isAjax() && in != null) {
        	Scanner s = new Scanner(in).useDelimiter("\\A");
        	String encryptedText = s.hasNext() ? s.next() : "";
//        	System.out.println(" original ::: "+ encryptedText); //"1111"
        	if(StringUtils.isNotEmpty(encryptedText) && !"{}".equals(encryptedText) && encryptedText.length() > 10){
        		try {
    	            String decryptedText = cipherUtil.decrypt(encryptedText);
    	            is = IOUtils.toInputStream(decryptedText, "UTF-8");
//    	            System.out.println(" decrypt ::: "+ decryptedText); //"1111"
        		} catch (Exception e) {
        			LOG.error(e.getMessage());
        		}
        	}else{
        		try {
					is = IOUtils.toInputStream(encryptedText, "UTF-8");
				} catch (Exception e){
					LOG.error(e.getMessage());
				}
        	}
    	}
    	

        /*
         * ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			// StandardCharsets.UTF_8.name() > JDK 7
			return result.toString("UTF-8");
         * 
         */
        
    }


	private String decryptHeaderValue(String name, String value) {
        if ("true".equals(secParamEncrypt) && "menucode".equals(name) && value != null && !"undefined".equals(value)) {
            try {
                return cipherUtil.decrypt(value);
            } catch (Exception e) {
				LOG.error(e.getMessage());
                throw new CommonException("Decryption failed");
            }
        }
        return value;
    }

    @Override
    public String getHeader(String name) {
        return decryptHeaderValue(name, super.getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("true".equals(secParamEncrypt) && "menucode".equals(name)) {
            Vector<String> headers = new Vector<>();
            Enumeration<String> originalHeaders = super.getHeaders(name);
            while (originalHeaders.hasMoreElements()) {
                headers.add(decryptHeaderValue(name, originalHeaders.nextElement()));
            }
            return headers.elements();
        }
        return super.getHeaders(name);
    }


	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new DecryptServletInputStream(is);
	}

}
