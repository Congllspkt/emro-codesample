package smartsuite.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.SessionFlashMapManager;
import org.springframework.web.util.WebUtils;
import smartsuite.app.common.shared.CountryLanguage;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.security.userdetails.UserDetailsProxy;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

public class SupplierUsernameOnlyAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	@Inject
	private SharedService sharedService;
	
	// 싱글페이지 URL
	private String singlePageTargetUrl;
    
    @Inject
    AuthenticationPostService authenticationPostService;
	
	// 싱글페이지 여부
	private boolean useSinglePage;
	
    protected final String getSinglePageTargetUrl() {
        return singlePageTargetUrl;
    }
    
    public void setSinglePageTargetUrl(String targetUrl) {
        this.singlePageTargetUrl = targetUrl;
    }
    
    protected boolean getUseSinglePage() {
    	return useSinglePage;
    }
    protected void setUseSinglePage(boolean useSingle){
    	this.useSinglePage = useSingle;
    }
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException{
		
		// 현재 로그인 성공한 user 객체 조회
		Object principal = authentication.getPrincipal();
		UserDetailsProxy proxy = null;
		if (principal instanceof UserDetails) {
			proxy = (UserDetailsProxy)principal;
		}
		
        //로케일 정보 저장
        WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, StringUtils.parseLocaleString(CountryLanguage.getLocaleByCountryCode(request.getParameter("ctry"))));
        
		// 사용자 역할에 따른 메뉴-기능-URL 역할정보 session 저장
		if(proxy != null){
			proxy.setUserRoles(sharedService.findListMenuFunctionAndUrlByUserRoleList());
			// 사용자 운영조직 정보 저장
			proxy.setUserOperationOrganizationCodes(sharedService.findListOperationOrganizationByUser(null));
		}
		
		Map<String, Object> loginInfo = Maps.newHashMap();
		loginInfo.put("log_id", UUID.randomUUID().toString());
		loginInfo.put("usr_typ_ccd", Auth.getCurrentUserInfo().get("usr_typ_ccd"));
		loginInfo.put("login_ip", request.getRemoteAddr());
		sharedService.insertLoginLogInfo(loginInfo);

		
		/*
		 * 필요 시 추가 로직 구현
		 * redirect 시 url에 파라미터가 포함되지 않도록 flashMap을 이용하여 사용한다.
		 */
		
		// Parameter Set
		FlashMap flashMap = new FlashMap();
		Map<String, Object> paramMap = Maps.newHashMap();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()){
			String key = params.nextElement();
		    paramMap.put(key, request.getParameter(key));
		}
		// Json parsing Set
		flashMap.put("paramMap", new ObjectMapper().writeValueAsString(paramMap));
		
		// Parameter MenuId Check
		boolean existsTaskKey = (paramMap.get("task_key") != null);
		setUseSinglePage(existsTaskKey);
		
		FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
		if (flashMapManager == null) {
			flashMapManager = new SessionFlashMapManager();
		}
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
        
        authenticationPostService.authenticationSuccess(request, response, authentication);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
 
	@Override
	protected void handle(HttpServletRequest request, 
		      HttpServletResponse response, Authentication authentication)
		      throws IOException {
		  
        String targetUrl = determineTargetUrl();
 
        if (response.isCommitted()) {
            logger.debug(
              "Response has already been committed. Unable to redirect to "
              + targetUrl);
            return;
        }
 
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
	
	protected String determineTargetUrl() {
        if (getUseSinglePage()) {
            return getSinglePageTargetUrl();
        } else  {
            return getDefaultTargetUrl();
        } 
    }
}
