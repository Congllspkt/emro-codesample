package smartsuite.app.webservice;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.message.Message;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.Map;


public class AuthenticationInterceptor extends AbstractAuthorizingInInterceptor {
 
    private static final String AUTH_HEADER = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";
    
    private String USER_NAME;
    
    public void setUsername(String username) {
        this.USER_NAME = username;
    }
    
    private String PASSWORD;
    
    public void setPassword(String password) {
        this.PASSWORD = password;
    }
 
    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        List<String> authHeaders = headers.get(AUTH_HEADER);
        
        if (authHeaders == null || authHeaders.isEmpty()) {
            throw new Fault(new IllegalArgumentException("Authorization header is missing"));
        }
 
        String authHeader = authHeaders.get(0);
        if (!authHeader.startsWith(BASIC_PREFIX)) {
            throw new Fault(new IllegalArgumentException("Invalid authorization header"));
        }
 
        String encodedCredentials = authHeader.substring(BASIC_PREFIX.length());
        String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
        String[] credentials = decodedCredentials.split(":", 2);
 
        if (credentials.length != 2) {
            throw new Fault(new IllegalArgumentException("Invalid authorization header format"));
        }
 
        String username = credentials[0];
        String password = credentials[1];
 
        if (!this.USER_NAME.equals(username) || !this.PASSWORD.equals(password)) {
            throw new Fault(new IllegalArgumentException("Invalid username or password"));
        }
    }
 
    @Override
    protected List<String> getExpectedRoles(Method method) {
        System.out.println("method:" + method);
        return null;
    }
}