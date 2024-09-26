package smartsuite.security.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;

import smartsuite.exception.CommonException;
import smartsuite.security.core.crypto.CipherUtil;

public class DecryptRequestFilter implements Filter {

    CipherUtil cipherUtil;
    
    
	@Value("#{globalProperties['sec.param.encrypt']}")
	private String secParamEncrypt;
    
    public void setCipherUtil(CipherUtil c){
    	this.cipherUtil = c;
    }
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//EMPTY METHOD
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequestWrapper requestWrapper = null;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse	 httpServletResponse = (HttpServletResponse) response;
		try {
				requestWrapper = new DecryptRequestWrapper(httpServletRequest, this.cipherUtil, secParamEncrypt);

				// filter 전처리  수행
				chain.doFilter(requestWrapper, response);

			}catch (IOException ex) {
				throw new IOException(ex);
			} catch (ServletException e) {
            throw new ServletException(e);
			}catch (CommonException e) {
				httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Decryption failed");
				throw new RuntimeException(e);
        	}

    }
}
