package smartsuite.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.GenericFilterBean;
import smartsuite.security.SecurityProviderImpl;
import smartsuite.security.account.info.AccountInitializer;
import smartsuite.security.account.spring.DefaultPostAuthenticationChecks;
import smartsuite.security.account.spring.DefaultPreAuthenticationChecks;
import smartsuite.security.account.spring.SupplierPostAuthenticationChecks;
import smartsuite.security.account.spring.SupplierPreAuthenticationChecks;
import smartsuite.security.authentication.CertificateAuthenticationFailureHandler;
import smartsuite.security.authentication.CertificateAuthenticationProcessingFilter;
import smartsuite.security.authentication.CertificateAuthenticationSuccessHandler;
import smartsuite.security.authentication.DefaultAuthenticationFailureHandler;
import smartsuite.security.authentication.DefaultAuthenticationSuccessHandler;
import smartsuite.security.authentication.DefaultLoginAuthenticationProcessingFilter;
import smartsuite.security.authentication.ExistenceOnlyAuthenticationProvider;
import smartsuite.security.authentication.LogoutSuccessHandler;
import smartsuite.security.authentication.SsoAuthenticationFailureHandler;
import smartsuite.security.authentication.SsoAuthenticationProcessingFilter;
import smartsuite.security.authentication.SsoAuthenticationSuccessHandler;
import smartsuite.security.authentication.SupplierAuthenticationFailureHandler;
import smartsuite.security.authentication.SupplierAuthenticationSuccessHandler;
import smartsuite.security.authentication.SupplierLoginAuthenticationProcessingFilter;
import smartsuite.security.authentication.SupplierUsernameOnlyAuthenticationFailureHandler;
import smartsuite.security.authentication.SupplierUsernameOnlyAuthenticationSuccessHandler;
import smartsuite.security.authentication.UsernameOnlyAuthenticationFailureHandler;
import smartsuite.security.authentication.UsernameOnlyAuthenticationProcessingFilter;
import smartsuite.security.authentication.UsernameOnlyAuthenticationSuccessHandler;
import smartsuite.security.core.crypto.AESCipherUtil;
import smartsuite.security.spring.core.session.SessionRegistryImpl;
import smartsuite.security.spring.web.session.ConcurrentSessionFilter;
import smartsuite.security.spring.web.session.ExpiredSessionStrategy;
import smartsuite.security.userdetails.DefaultUserDetailsService;
import smartsuite.security.userdetails.SupplierUserDetailsService;
import smartsuite.security.web.filter.DecryptRequestFilter;
import smartsuite.security.web.filter.advanced.XssRequestFilter;
import smartsuite.security.web.filter.advanced.action.XssReplaceAction;
import smartsuite.security.web.filter.advanced.action.handler.ExistRuleReplaceHandler;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;

@Configuration
public class FilterConfiguration {
	/*
	* filterType WHITELIST default
	* */
	private RequestMatcher xssRequest = new OrRequestMatcher(new RequestMatcher[] {
		new AntPathRequestMatcher("/ui/bp/workflow/**"), // Workflow
		//new AntPathRequestMatcher("/ui/bp/common/mail/**"), // 메일
		new AntPathRequestMatcher("/ui/bp/common/bidtemplate/**"),
		new AntPathRequestMatcher("/ui/bp/common/bidtemplate/**"),
		new AntPathRequestMatcher("/**/ifproxy/**"), // 인터페이스
		// new AntPathRequestMatcher("/ui/bp/common/tmp/**"), // 템플릿
		//new AntPathRequestMatcher("/ui/bp/common/terms/**"), // 이용약관
		// new AntPathRequestMatcher("/ui/bp/common/board/**"), // 게시판
		//new AntPathRequestMatcher("/ui/bp/shared/**"), // shared
		new AntPathRequestMatcher("/ui/bp/**/approval/**"), // 결재
		new AntPathRequestMatcher("/ui/bp/common/approval/**"),
		new AntPathRequestMatcher("/ui/bp/edoc/contract/**"), // 전자계약
		new AntPathRequestMatcher("/i18n/getByKeys.do"), // 다국어
		new AntPathRequestMatcher("/ui/bp/common/code/multilang/**"), // 다국어
			//new AntPathRequestMatcher("/**/popupHelpManual.do"), // 메뉴얼
		//new AntPathRequestMatcher("/ui/bp/common/manual/**"), // 메뉴얼
		new AntPathRequestMatcher("/**/infoRenewRequest.do"), // 예외처리 추가
		new AntPathRequestMatcher("/ui/common/csr/**"), // CSR 관리
		new AntPathRequestMatcher("/pro/fi/**.do"),
		new AntPathRequestMatcher("/smartsuite/oauth2/**"), // OAUTH2
	});
	
	// ******************************* //
	// UserDetailsService
	// ******************************* //
	// 패스워드 로그인 방식에서 사용하는 기본 UserDetailsService
	@Bean(name = "defaultUserDetailsService")
	public UserDetailsService defaultUserDetailsService() {
		DefaultUserDetailsService service = new DefaultUserDetailsService();
		service.setWithoutPassword(false);
		return service;
	}
	
	// 패스워드 없이 로그인 하는 방식에서 사용하는 UserDetailsService (sso / 공동인증서 로그인 등)
	@Bean(name = "withoutPwUserDetailsService")
	public UserDetailsService withoutPwUserDetailsService() {
		DefaultUserDetailsService service = new DefaultUserDetailsService();
		service.setWithoutPassword(true);
		return service;
	}
	
	// Supplier 로그인
	@Bean(name = "supplierUserDetailsService")
	public UserDetailsService supplierUserDetailsService() {
		SupplierUserDetailsService service = new SupplierUserDetailsService();
		service.setWithoutPassword(false);
		return service;
	}
    
    // Supplier 패스워드 없이 로그인
    @Bean(name = "withoutPwSupplierUserDetailsService")
    public UserDetailsService withoutPwSupplierUserDetailsService() {
        SupplierUserDetailsService service = new SupplierUserDetailsService();
        service.setWithoutPassword(true);
        return service;
    }
	
	
	// ******************************* //
	// Provider
	// ******************************* //
	@Bean(name = "defaultAuthenticationProvider")
	public AbstractUserDetailsAuthenticationProvider defaultAuthenticationProvider(
			UserDetailsService defaultUserDetailsService,
			PasswordEncoder passwordEncoder,
			DefaultPreAuthenticationChecks preAuthenticationChecks, 
			DefaultPostAuthenticationChecks postAuthenticationChecks
		) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false);
		provider.setUserDetailsService(defaultUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setPreAuthenticationChecks(preAuthenticationChecks);
		provider.setPostAuthenticationChecks(postAuthenticationChecks);
		return provider;
	}
	
	@Bean(name = "supplierAuthenticationProvider")
	public AbstractUserDetailsAuthenticationProvider supplierAuthenticationProvider(
			@Qualifier("supplierUserDetailsService") UserDetailsService supplierUserDetailsService,
			PasswordEncoder passwordEncoder,
			SupplierPreAuthenticationChecks supplierPreAuthenticationChecks, 
			SupplierPostAuthenticationChecks supplierPostAuthenticationChecks
		) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false);
		provider.setUserDetailsService(supplierUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		provider.setPreAuthenticationChecks(supplierPreAuthenticationChecks);
		provider.setPostAuthenticationChecks(supplierPostAuthenticationChecks);
		return provider;
	}
	

	@Bean(name = "certificateAuthenticationProvider")
	public AbstractUserDetailsAuthenticationProvider certificateAuthenticationProvider(
			@Qualifier("supplierUserDetailsService") UserDetailsService supplierUserDetailsService) {
		ExistenceOnlyAuthenticationProvider provider = new ExistenceOnlyAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false);
		provider.setUserDetailsService(supplierUserDetailsService);
		return provider;
	}
	// 존재하면 인증시키는 AuthenticationProvider	
	@Bean(name = "existenceOnlyAuthenticationProvider")
	public AbstractUserDetailsAuthenticationProvider existenceOnlyAuthenticationProvider(
			@Qualifier("withoutPwUserDetailsService") UserDetailsService withoutPwUserDetailsService) {
		ExistenceOnlyAuthenticationProvider provider = new ExistenceOnlyAuthenticationProvider();
		provider.setHideUserNotFoundExceptions(false);
		provider.setUserDetailsService(withoutPwUserDetailsService);
		return provider;
	}
    
    @Bean(name = "existenceOnlySupplierAuthenticationProvider")
    public AbstractUserDetailsAuthenticationProvider existenceOnlySupplierAuthenticationProvider(
            @Qualifier("withoutPwSupplierUserDetailsService") UserDetailsService withoutPwSupplierUserDetailsService) {
        ExistenceOnlyAuthenticationProvider provider = new ExistenceOnlyAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(withoutPwSupplierUserDetailsService);
        return provider;
    }
	
	// ******************************* //
	// AuthenticationManager
	// ******************************* //
	@Primary
	@Bean(name = "defaultAuthenticationManager")
	public AuthenticationManager defaultAuthenticationManager(@Qualifier("defaultAuthenticationProvider") AbstractUserDetailsAuthenticationProvider defaultAuthenticationProvider) {
		return new ProviderManager(defaultAuthenticationProvider);
	}
	
	@Bean(name = "supplierAuthenticationManager")
	public AuthenticationManager supplierAuthenticationManager(@Qualifier("supplierAuthenticationProvider") AbstractUserDetailsAuthenticationProvider supplierAuthenticationProvider) {
		return new ProviderManager(supplierAuthenticationProvider);
	}
	
	@Bean(name = "certificateAuthenticationManager")
	public AuthenticationManager certificateAuthenticationManager(@Qualifier("certificateAuthenticationProvider") AbstractUserDetailsAuthenticationProvider certificateAuthenticationProvider) {
		return new ProviderManager(certificateAuthenticationProvider);
	}
	
	@Bean(name = "existenceOnlyAuthenticationManager")
	public AuthenticationManager existenceOnlyAuthenticationManager(@Qualifier("existenceOnlyAuthenticationProvider") AbstractUserDetailsAuthenticationProvider existenceOnlyAuthenticationProvider) {
		return new ProviderManager(existenceOnlyAuthenticationProvider);
	}
    
    @Bean(name = "existenceOnlySupplierAuthenticationManager")
    public AuthenticationManager existenceOnlySupplierAuthenticationManager(@Qualifier("existenceOnlySupplierAuthenticationProvider") AbstractUserDetailsAuthenticationProvider existenceOnlySupplierAuthenticationProvider) {
        return new ProviderManager(existenceOnlySupplierAuthenticationProvider);
    }
    
	
	// ******************************* //
	// handler
	// ******************************* //	
	@Bean(name = "authenticationSuccessHandler")
	public DefaultAuthenticationSuccessHandler authenticationSuccessHandler() {
		DefaultAuthenticationSuccessHandler hadler = new DefaultAuthenticationSuccessHandler();
	    hadler.setDefaultTargetUrl("/afterLogin.do");
	    return hadler;
	}
	
	@Bean(name = "authenticationFailureHandler")
	public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
		SimpleUrlAuthenticationFailureHandler hadler = new DefaultAuthenticationFailureHandler();
		hadler.setDefaultFailureUrl("/loginFailure.do");
	    return hadler;
	}

	// logout 성공시 핸들러
	@Bean(name = "logoutSuccessHandler")
	public SimpleUrlLogoutSuccessHandler logoutSuccessHandler() {
		return new LogoutSuccessHandler();
	}
	
	@Bean(name = "supplierAuthenticationSuccessHandler")
	public SupplierAuthenticationSuccessHandler supplierAuthenticationSuccessHandler() {
		SupplierAuthenticationSuccessHandler hadler = new SupplierAuthenticationSuccessHandler();
	    hadler.setDefaultTargetUrl("/afterSpLogin.do");
	    return hadler;
	}
	
	@Bean(name = "supplierAuthenticationFailureHandler")
	public SimpleUrlAuthenticationFailureHandler supplierAuthenticationFailureHandler() {
		SimpleUrlAuthenticationFailureHandler hadler = new SupplierAuthenticationFailureHandler();
		hadler.setDefaultFailureUrl("/spLoginFailure.do");
	    return hadler;
	}
	
	@Bean(name = "usernameOnlyAuthenticationSuccessHandler")
	public UsernameOnlyAuthenticationSuccessHandler usernameOnlyAuthenticationSuccessHandler() {
		UsernameOnlyAuthenticationSuccessHandler handler = new UsernameOnlyAuthenticationSuccessHandler();
		handler.setDefaultTargetUrl("/afterLogin.do");
		handler.setSinglePageTargetUrl("/afterSinglePageLogin.do");
		handler.setAlwaysUseDefaultTargetUrl(false);
	    return handler;
	}
	
	@Bean(name = "usernameOnlyAuthenticationFailureHandler")
	public UsernameOnlyAuthenticationFailureHandler usernameOnlyAuthenticationFailureHandler() {
		UsernameOnlyAuthenticationFailureHandler handler = new UsernameOnlyAuthenticationFailureHandler();
		handler.setDefaultFailureUrl("/loginFailure.do");
		return handler;
	}
	
	@Bean(name = "ssoAuthenticationSuccessHandler")
	public SsoAuthenticationSuccessHandler ssoAuthenticationSuccessHandler() {
		SsoAuthenticationSuccessHandler handler = new SsoAuthenticationSuccessHandler();
		handler.setDefaultTargetUrl("/afterLogin.do");
		handler.setSinglePageTargetUrl("/afterSinglePageLogin.do");
	    return handler;
	}
	
	@Bean(name = "ssoAuthenticationFailureHandler")
	public SsoAuthenticationFailureHandler ssoAuthenticationFailureHandler() {
		SsoAuthenticationFailureHandler handler = new SsoAuthenticationFailureHandler();
		handler.setDefaultFailureUrl("/loginFailure.do");
		return handler;
	}
	
	@Bean(name = "certificateAuthenticationSuccessHandler")
	public CertificateAuthenticationSuccessHandler certificateAuthenticationSuccessHandler() {
		CertificateAuthenticationSuccessHandler handler = new CertificateAuthenticationSuccessHandler();
		handler.setDefaultTargetUrl("/afterLogin.do");
		handler.setSinglePageTargetUrl("/afterSinglePageLogin.do");
		return handler;
	}
	
	@Bean(name = "certificateAuthenticationFailureHandler")
	public CertificateAuthenticationFailureHandler certificateAuthenticationFailureHandler() {
		CertificateAuthenticationFailureHandler handler = new CertificateAuthenticationFailureHandler();
		handler.setDefaultFailureUrl("/loginFailure.do");
		return handler;
	}

    @Bean(name = "supplierUsernameOnlyAuthenticationSuccessHandler")
    public SupplierUsernameOnlyAuthenticationSuccessHandler supplierUsernameOnlyAuthenticationSuccessHandler() {
        SupplierUsernameOnlyAuthenticationSuccessHandler handler = new SupplierUsernameOnlyAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/afterSpLogin.do");
        handler.setSinglePageTargetUrl("/afterSinglePageSpLogin.do");
        return handler;
    }
    
    @Bean(name = "supplierUsernameOnlyAuthenticationFailureHandler")
    public SupplierUsernameOnlyAuthenticationFailureHandler supplierUsernameOnlyAuthenticationFailureHandler() {
        SupplierUsernameOnlyAuthenticationFailureHandler handler = new SupplierUsernameOnlyAuthenticationFailureHandler();
        handler.setDefaultFailureUrl("/spLoginFailure.do");
        return handler;
    }
	
	// ******************************* //
	// Filter
	// ******************************* //
	// XSS 요청 필터
	@Bean(name ="xssRequestFilter")
	public XssRequestFilter xssRequestFilter() {
		XssRequestFilter filter = new XssRequestFilter();
		filter.setRequestMatcher(xssRequest);

		XssReplaceAction action = new XssReplaceAction();
		//default html5 tag attr
		action.setReplaceHandler(new ExistRuleReplaceHandler());
		action.setActionType("WHITELIST");
		action.setTagList(Arrays.asList("div", "sc-*", "*-emro", "object", "html", "head", "body", "meta", "img", "table", "tr", "td", "th", "a", "span", "p", "br"
				, "!doctype html", "style", "title", "col", "colgroup", "tbody", "footer", "article", "h1", "h2", "h3", "h4", "h5", "section", "input", "ol", "ul", "li", "strong", "b", "textarea"));
		action.setUseEscapeHtml(false);
		action.setAttrList(Arrays.asList("style", "onload","colspan","src","charset","type","class","href","rowspan","moznomarginboxes","mozdisallowselectionprint"
				,"http-equiv","content","type","media","onclick","title","name","value","maxlength","width","border","border-collapse","border-spacing","cellspacing","align","target"
				,"cols", "rows"));
		action.setReplaceTagString("[[TAG]]");
		action.setReplaceAttrString("[[ATTR]]");

		//action.setActionType("BALCKLIST ");
		//action.setTagList(Arrays.asList("body", "button", "embed", "iframe", "meta", "object", "script", "link", "base", "detail","details"));
		//action.setUseEscapeHtml(false);
		//action.setAttrList(Arrays.asList("onload", "onmouseover", "onmouseenter", "data", "href", "formaction", "ontoggle"));
//		action.setReplaceTagString("BLOCKEDTAG");
//		action.setReplaceAttrString("BLOCKEDATTR");

		filter.setFilterAction(action);
		return filter;
	}

	// 중복로그인 필터로 SessionRegistry 인터페이스를 구현체 생성자 인자로 추가해야 하며 ExpiredSessionStrategy 인터페이스 구현체를  expiredSessionStrategy 프로퍼티에 주입해야 합니다.
	@Bean(name = "concurrencyFilter")
	public GenericFilterBean concurrencyFilter(SessionRegistryImpl sessionRegistryImpl, ExpiredSessionStrategy expiredSessionStrategy) {
		ConcurrentSessionFilter filter = new ConcurrentSessionFilter(sessionRegistryImpl);
		filter.setExpiredSessionStrategy(expiredSessionStrategy);
		return filter;
	}

	// parameter decryt 필터
	@Bean(name = "decrytRequestFilter")
	public Filter decrytRequestFilter(AESCipherUtil cipherUtil) {
		DecryptRequestFilter filter = new DecryptRequestFilter();
		filter.setCipherUtil(cipherUtil);
		return filter;
	}
	
	@Bean(name = "defaultLoginAuthenticationProcessingFilter")
	public AbstractAuthenticationProcessingFilter defaultLoginAuthenticationProcessingFilter(
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			@Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager,
			DefaultAuthenticationSuccessHandler authenticationSuccessHandler,
			SimpleUrlAuthenticationFailureHandler authenticationFailureHandler
		) {
		/** [TO-DO] DefaultLoginAuthenticationProcessingFilter 생성자 Public 변경 */
		DefaultLoginAuthenticationProcessingFilter filter = new DefaultLoginAuthenticationProcessingFilter("/loginProcess.do");
		filter.setUsernameParameter("username");
		filter.setPasswordParameter("password");
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setAuthenticationManager(defaultAuthenticationManager);
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(authenticationFailureHandler);
		return filter;
	}
	
	@Bean(name = "spLoginAuthenticationProcessingFilter")
	public AbstractAuthenticationProcessingFilter spLoginAuthenticationProcessingFilter(
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			@Qualifier("supplierAuthenticationManager") AuthenticationManager supplierAuthenticationManager,
			SupplierAuthenticationSuccessHandler supplierAuthenticationSuccessHandler,
			SimpleUrlAuthenticationFailureHandler supplierAuthenticationFailureHandler
		) {
		/** [TO-DO] SupplierLoginAuthenticationProcessingFilter 생성자 Public 변경 */
		SupplierLoginAuthenticationProcessingFilter filter = new SupplierLoginAuthenticationProcessingFilter("/spLoginProcess.do");
		filter.setUsernameParameter("username");
		filter.setPasswordParameter("password");
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setAuthenticationManager(supplierAuthenticationManager);
		filter.setAuthenticationSuccessHandler(supplierAuthenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(supplierAuthenticationFailureHandler);
		return filter;
	}
    
    @Bean(name = "usernameOnlySupplierAuthenticationProcessingFilter")
    public UsernameOnlyAuthenticationProcessingFilter usernameOnlySupplierAuthenticationProcessingFilter(
            CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
            @Qualifier("existenceOnlySupplierAuthenticationManager") AuthenticationManager existenceOnlySupplierAuthenticationManager,
            SupplierUsernameOnlyAuthenticationSuccessHandler supplierUsernameOnlyAuthenticationSuccessHandler,
            SupplierUsernameOnlyAuthenticationFailureHandler supplierUsernameOnlyAuthenticationFailureHandler
    ) {
        UsernameOnlyAuthenticationProcessingFilter filter = new UsernameOnlyAuthenticationProcessingFilter("/usernameOnlySpLoginProcess.do");
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        filter.setAuthenticationManager(existenceOnlySupplierAuthenticationManager);
        filter.setAuthenticationSuccessHandler(supplierUsernameOnlyAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(supplierUsernameOnlyAuthenticationFailureHandler);
        return filter;
    }
	
	@Bean(name = "customAuthenticationProcessingFilters")
	public CompositeFilter customAuthenticationProcessingFilters(
			AbstractAuthenticationProcessingFilter defaultLoginAuthenticationProcessingFilter,
			AbstractAuthenticationProcessingFilter spLoginAuthenticationProcessingFilter,
			UsernameOnlyAuthenticationProcessingFilter usernameOnlyAuthenticationProcessingFilter,
			SsoAuthenticationProcessingFilter ssoAuthenticationProcessingFilter,
			CertificateAuthenticationProcessingFilter certificateAuthenticationProcessingFilter,
            AbstractAuthenticationProcessingFilter usernameOnlySupplierAuthenticationProcessingFilter) {
		CompositeFilter filter = new CompositeFilter();
		filter.setFilters(
			Arrays.asList(
				defaultLoginAuthenticationProcessingFilter,
				spLoginAuthenticationProcessingFilter,
				// usernameOnlyAuthenticationProcessingFilter,
				ssoAuthenticationProcessingFilter,
				certificateAuthenticationProcessingFilter,
				usernameOnlySupplierAuthenticationProcessingFilter
			)
		);
		return filter;
	}
	
	@Bean(name = "usernameOnlyAuthenticationProcessingFilter")
	public UsernameOnlyAuthenticationProcessingFilter usernameOnlyAuthenticationProcessingFilter(
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			@Qualifier("existenceOnlyAuthenticationManager") AuthenticationManager existenceOnlyAuthenticationManager,
			UsernameOnlyAuthenticationSuccessHandler usernameOnlyAuthenticationSuccessHandler,
			UsernameOnlyAuthenticationFailureHandler usernameOnlyAuthenticationFailureHandler
		) {
		/** [TO-DO] UsernameOnlyAuthenticationProcessingFilter 생성자 Public 변경 */
		UsernameOnlyAuthenticationProcessingFilter filter = new UsernameOnlyAuthenticationProcessingFilter("/usernameOnlyLoginProcess.do");
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setAuthenticationManager(existenceOnlyAuthenticationManager);
		filter.setAuthenticationSuccessHandler(usernameOnlyAuthenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(usernameOnlyAuthenticationFailureHandler);
		return filter;
	}
	
	@Bean(name = "ssoAuthenticationProcessingFilter")
	public SsoAuthenticationProcessingFilter ssoAuthenticationProcessingFilter(
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			@Qualifier("existenceOnlyAuthenticationManager") AuthenticationManager existenceOnlyAuthenticationManager,
			SsoAuthenticationSuccessHandler ssoAuthenticationSuccessHandler,
			SsoAuthenticationFailureHandler ssoAuthenticationFailureHandler
			) {
		/** [TO-DO] SsoAuthenticationProcessingFilter 생성자 Public 변경 */
		SsoAuthenticationProcessingFilter filter = new SsoAuthenticationProcessingFilter("/ssoLoginProcess.do");
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setAuthenticationManager(existenceOnlyAuthenticationManager);
		filter.setAuthenticationSuccessHandler(ssoAuthenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(ssoAuthenticationFailureHandler);
		return filter;
	}
	
	@Bean(name = "certificateAuthenticationProcessingFilter")
	public CertificateAuthenticationProcessingFilter certificateAuthenticationProcessingFilter(
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			@Qualifier("certificateAuthenticationManager") AuthenticationManager certificateAuthenticationManager,
			CertificateAuthenticationSuccessHandler certificateAuthenticationSuccessHandler,
			CertificateAuthenticationFailureHandler certificateAuthenticationFailureHandler
			) {
		/** [TO-DO] CertificateAuthenticationProcessingFilter 생성자 Public 변경 */
		CertificateAuthenticationProcessingFilter filter = new CertificateAuthenticationProcessingFilter("/certificateLoginProcess.do");
		filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		filter.setAuthenticationManager(certificateAuthenticationManager);
		filter.setAuthenticationSuccessHandler(certificateAuthenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(certificateAuthenticationFailureHandler);
		return filter;
	}
	
	// ******************************* //
	// checks
	// ******************************* //	
	@Bean(name = "preAuthenticationChecks")
	public DefaultPreAuthenticationChecks preAuthenticationChecks() {
		return new DefaultPreAuthenticationChecks();
	}
	
	@Bean(name = "postAuthenticationChecks")
	public DefaultPostAuthenticationChecks postAuthenticationChecks() {
		return new DefaultPostAuthenticationChecks();
	}
	
	@Bean(name = "supplierPreAuthenticationChecks")
	public SupplierPreAuthenticationChecks supplierPreAuthenticationChecks() {
		return new SupplierPreAuthenticationChecks();
	}
	
	@Bean(name = "supplierPostAuthenticationChecks")
	public SupplierPostAuthenticationChecks supplierPostAuthenticationChecks() {
		return new SupplierPostAuthenticationChecks();
	}
	
	// ******************************* //
	// 기타
	// ******************************* //	
	@Bean(name = "delegatingAuthenticationEntryPoint")
	public AuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
		entryPoints.put(new ELRequestMatcher("hasHeader('X-Requested-With','XMLHttpRequest')"), new Http403ForbiddenEntryPoint()); // Ajax 일 때 403 forbidden 처리
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		entryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/login.do"));
		return entryPoint;
	}

	@Bean(name = "requestCache")
	public HttpSessionRequestCache requestCache() {
		return new HttpSessionRequestCache();
	}
	
	@Bean("securityProviderImpl")
	public SecurityProviderImpl securityProviderImpl() {
		return new SecurityProviderImpl();
	}

	@Bean(name = "accountInitializer")
	public AccountInitializer accountInitializer() {
		return new AccountInitializer();
	}
	
	// ******************************* //
	// smartsuite-security-oauth2 신규추가
	// ******************************* //
	@Bean(name = "oauth2AuthenticationManager")
	public AuthenticationManager oauth2AuthenticationManager(@Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager) {
	    return defaultAuthenticationManager;
	}
	@Bean(name = "oauth2UserDetailsService")
	public UserDetailsService oauth2UserDetailsService() {
	    DefaultUserDetailsService service = new DefaultUserDetailsService();
	    service.setWithoutPassword(false);
	    return service;
	}
	@Bean(name = "oauth2DefaultLoginAuthenticationProcessingFilter")
	public AbstractAuthenticationProcessingFilter oauth2DefaultLoginAuthenticationProcessingFilter(
	        CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
	        @Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager,
	        DefaultAuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
	        SimpleUrlAuthenticationFailureHandler oauth2AuthenticationFailureHandler
	    ) {
	    /** [TO-DO] DefaultLoginAuthenticationProcessingFilter 생성자 Public 변경 */
	    DefaultLoginAuthenticationProcessingFilter filter = new DefaultLoginAuthenticationProcessingFilter("/oauth2/loginProcess");
	    filter.setUsernameParameter("username");
	    filter.setPasswordParameter("password");
	    filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
	    filter.setAuthenticationManager(defaultAuthenticationManager);
	    filter.setAuthenticationSuccessHandler(oauth2AuthenticationSuccessHandler);
	    filter.setAuthenticationFailureHandler(oauth2AuthenticationFailureHandler);
	    return filter;
	}
	@Bean(name = "oauth2AuthenticationSuccessHandler")
	public DefaultAuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
	    DefaultAuthenticationSuccessHandler hadler = new DefaultAuthenticationSuccessHandler();
	    hadler.setAlwaysUseDefaultTargetUrl(false);
	    return hadler;
	}
	 
	@Bean(name = "oauth2AuthenticationFailureHandler")
	public SimpleUrlAuthenticationFailureHandler oauth2AuthenticationFailureHandler() {
	    SimpleUrlAuthenticationFailureHandler hadler = new DefaultAuthenticationFailureHandler();
	    hadler.setDefaultFailureUrl("/oauth2/oauth2loginfailure");
	    return hadler;
	}  
}
