package smartsuite.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.GenericFilterBean;

import smartsuite.config.security.CryptoConfiguration;
import smartsuite.config.security.FilterConfiguration;
import smartsuite.config.security.MethodSecurityConfiguration;
import smartsuite.config.security.SessionConfiguration;
import smartsuite.security.authentication.DefaultAuthenticationSuccessHandler;
import smartsuite.security.authentication.SupplierAuthenticationSuccessHandler;
import smartsuite.security.spring.web.util.matcher.DefaultRequiresCsrfMatcher;
import smartsuite.security.spring.web.util.matcher.NegatedOrRequestMatcher;
import smartsuite.security.web.filter.advanced.XssRequestFilter;

@Configuration
@EnableWebSecurity
//@ImportResource(value = { "classpath:smartsuite/security-context.xml" })
@Import({CryptoConfiguration.class, SessionConfiguration.class, FilterConfiguration.class, MethodSecurityConfiguration.class})
public class SecurityConfiguration {

	@Value("#{smartsuiteProperties['smartsuite.jsessionid']}")
	private String jsessionid = "JSESSIONID";

	private RequestMatcher noSessionManageRequest = new OrRequestMatcher(new RequestMatcher[] {
		new AntPathRequestMatcher("/index.do"),
		new AntPathRequestMatcher("/login.do"),
		new AntPathRequestMatcher("/loginKr.do"),
		new AntPathRequestMatcher("/loginEn.do"),
		new AntPathRequestMatcher("/spLogin.do"),
		new AntPathRequestMatcher("/spLoginKr.do"),
		new AntPathRequestMatcher("/spLoginEn.do"),
		new AntPathRequestMatcher("/newVendor.do"),
		new AntPathRequestMatcher("/loginFailure.do"),
		new AntPathRequestMatcher("/spLoginFailure.do"),
		new AntPathRequestMatcher("/spLogoutSuccess.do"),
		new AntPathRequestMatcher("/spLogoutSuccessEn.do"),
		new AntPathRequestMatcher("/logoutSuccess.do"),
		new AntPathRequestMatcher("/logoutSuccessEn.do"),
		new AntPathRequestMatcher("/invalidSession.do"),
		new AntPathRequestMatcher("/sessionExpired.do"),
		new AntPathRequestMatcher("/docs.do"),
		new AntPathRequestMatcher("/**/sessionTimeUpdate.do"),

		new AntPathRequestMatcher("/multipart/importexcel.do"), // 엑셀 업로드 url
		new AntPathRequestMatcher("/mobileLogin.do"), // 모바일 로그인
		new AntPathRequestMatcher("/mobile/**"), // 모바일 요청
		new AntPathRequestMatcher("/pro/fi/**.do"),
		new AntPathRequestMatcher("/eformSignLogin.do"), // 간편서명
				new AntPathRequestMatcher("/eformSignPwLogin.do"),
			new AntPathRequestMatcher("/eformSignPwLoginProcess.do"),
		new AntPathRequestMatcher("/noticeLink.do"),
		new AntPathRequestMatcher("/login/**"),
		new AntPathRequestMatcher("/sp/vendorMaster/vendorReg/**"),
		new AntPathRequestMatcher("/ui/sp/vendorMaster/vendorReg/**"),
		new AntPathRequestMatcher("/i18n/getByKeys.do"),
		new AntPathRequestMatcher("/i18n/updateI18nKeys.do"),
		new AntPathRequestMatcher("/i18n/getByLastUpdated.do"),
		new AntPathRequestMatcher("/i18n/getAvailableLocalizedLocales.do"),
		new AntPathRequestMatcher("/completeCertLogin.do"),
		new AntPathRequestMatcher("/openCertSelectionPage.do"),
		new AntPathRequestMatcher("/findListFormatter.do"),
		new AntPathRequestMatcher("/getModules.do"),
		new AntPathRequestMatcher("/findListMenu.do"),
		new AntPathRequestMatcher("/findListCommonCodeByNoSession.do"),
		new AntPathRequestMatcher("/supplierAuthNumberVerify.do"),
		new AntPathRequestMatcher("/supplierAuthNumberVerifyProcess.do")
	});
	
	// 서울보증보험 온라인 연계시 사용
	private RequestMatcher guaranteeManageRequest = new OrRequestMatcher(new RequestMatcher[] {
		new AntPathRequestMatcher("/sgic/recv.do"),
	});
	
	// OAUTH2
	private RequestMatcher sessionManagedRequest = new NegatedOrRequestMatcher(new RequestMatcher[] {
	    new AntPathRequestMatcher("/oauth2/**"),
	    new AntPathRequestMatcher("/api/**"),          
	});
	
	// csrf 필터 예외처리 url
	private AndRequestMatcher csrfRequestMatcher = new AndRequestMatcher(
		new NegatedOrRequestMatcher(new RequestMatcher[] {
			new AntPathRequestMatcher("/index.do"),
			new AntPathRequestMatcher("/sp/edoc/contract/findSignContract.do"), 
			new AntPathRequestMatcher("/sp/edoc/contract/saveSign.do"), 
			new AntPathRequestMatcher("/rest/api/**"), 
			new AntPathRequestMatcher("/sgic/recv.do"), // 서울보증보험 보증서 수신 url
			new AntPathRequestMatcher("/ssoLoginProcess.do"),  // SSO 표준화 URL(POST 방식) CSRF 필터 예외 처리
			new AntPathRequestMatcher("/mobileLogin.do"), // 가온소프트 모바일 POC용 (테스트 입니다.!)
			new AntPathRequestMatcher("/mobile/**"), // 가온소프트 모바일 POC용 (테스트 입니다.!)
			new AntPathRequestMatcher("/pro/fi/**.do"), 
			new AntPathRequestMatcher("/eformSignLogin.do"), // 간편서명
				new AntPathRequestMatcher("/eformSignPwLogin.do"),
				new AntPathRequestMatcher("/eformSignPwLoginProcess.do"),
				new AntPathRequestMatcher("/oauth2/**"), // OAUTH2
		    new AntPathRequestMatcher("/api/**"), // OAUTH2
            new AntPathRequestMatcher("/openCertSelectionPage.do"),
			new AntPathRequestMatcher("/newVendor.do"),
			new AntPathRequestMatcher("/supplierAuthNumberVerifyProcess.do")
		}),
		new DefaultRequiresCsrfMatcher() // Spring Security CSRF Matcher GET, HEAD, TRACE, OPTIONS
	);
	
	@Bean
	public SecurityFilterChain noSessionFilterChain(HttpSecurity http, 
			AuthenticationEntryPoint delegatingAuthenticationEntryPoint,
			@Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager,
			HttpSessionRequestCache requestCache) throws Exception {
		String contentSecurityPolicy = "default-src 'self' https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 'unsafe-inline' 'unsafe-eval' data:; script-src 'self' https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 'unsafe-inline' 'unsafe-eval' data:; object-src 'self' 'unsafe-inline' 'unsafe-eval' data:; img-src 'self' https://shopping-phinf.pstatic.net https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 'unsafe-inline' 'unsafe-eval' data:";
		
		http
			.securityMatcher(noSessionManageRequest)
			.httpBasic(basic -> basic
				.authenticationEntryPoint(delegatingAuthenticationEntryPoint)
				.and()
				.authenticationManager(defaultAuthenticationManager)
			)
			.csrf(csrf -> csrf.requireCsrfProtectionMatcher(csrfRequestMatcher))
			.requestCache().requestCache(requestCache)
			.and()
			.headers(headers -> headers
				.contentSecurityPolicy(contentSecurityPolicy)
				.and()
				.frameOptions().sameOrigin()
			);
		return http.build();
	}
	
	// 서울보증보험 연계시 url 제외(csrf, session 제외)
	@Bean
	public SecurityFilterChain guaranteeFilterChain(HttpSecurity http, 
			AuthenticationEntryPoint delegatingAuthenticationEntryPoint,
			@Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager,
			HttpSessionRequestCache requestCache) throws Exception {
		String contentSecurityPolicy = "default-src 'self' 'unsafe-inline' 'unsafe-eval' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' data:; object-src 'self' 'unsafe-inline' 'unsafe-eval' data:; img-src 'self' 'unsafe-inline' 'unsafe-eval' data:";
		
		http
		.securityMatcher(guaranteeManageRequest)
		.httpBasic(basic -> basic
			.authenticationEntryPoint(delegatingAuthenticationEntryPoint)
			.and()
			.authenticationManager(defaultAuthenticationManager)
		)
		.csrf(csrf -> csrf.requireCsrfProtectionMatcher(csrfRequestMatcher))
		.requestCache().requestCache(requestCache)
		.and()
		.headers(headers -> headers
			.contentSecurityPolicy(contentSecurityPolicy)
		);
		return http.build();
	}
	
	@Bean
	public SecurityFilterChain defaultFilterChain(HttpSecurity http,
			AuthenticationEntryPoint delegatingAuthenticationEntryPoint,
			@Qualifier("defaultAuthenticationManager") AuthenticationManager defaultAuthenticationManager,
			HttpSessionRequestCache requestCache,
			DefaultAuthenticationSuccessHandler authenticationSuccessHandler,
			SimpleUrlAuthenticationFailureHandler authenticationFailureHandler,
			SimpleUrlLogoutSuccessHandler logoutSuccessHandler,
			CompositeFilter customAuthenticationProcessingFilters,
			SupplierAuthenticationSuccessHandler supplierAuthenticationSuccessHandler,
			SimpleUrlAuthenticationFailureHandler supplierAuthenticationFailureHandler,
			@Qualifier("tenantFilter") GenericFilterBean tenantFilter,
			CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy,
			GenericFilterBean concurrencyFilter,
			GenericFilterBean sessionInvalidateFilter,
			Filter decrytRequestFilter,
			XssRequestFilter xssRequestFilter
			) throws Exception {
		String contentSecurityPolicy = "default-src 'self' wss://rum.beusable.net wss://script.beusable.net https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 data: 'unsafe-inline' 'unsafe-eval' ; script-src 'self' http://rum.beusable.net https://script.beusable.net https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 data: 'unsafe-inline' 'unsafe-eval' blob:; object-src 'self' data: 'unsafe-inline' 'unsafe-eval' ; img-src 'self' https://shopping-phinf.pstatic.net *.beusable.net http://www.unisign.co.kr https://127.0.0.1:15018 https://127.0.0.1:14315 http://127.0.0.1:14319 http://www.sgic.co.kr data: 'unsafe-inline' 'unsafe-eval' blob:;";
		http
			.securityMatcher(sessionManagedRequest) // OAUTH2
			.httpBasic(basic -> basic
				.authenticationEntryPoint(delegatingAuthenticationEntryPoint)
				.and()
				.authenticationManager(defaultAuthenticationManager)
			)		
			.authorizeHttpRequests(req -> req
				.requestMatchers(new AntPathRequestMatcher("/logoutSuccess.do"), new AntPathRequestMatcher("/logoutSuccessEn.do")).permitAll()
				.anyRequest().authenticated()
			)	
			.csrf(csrf -> csrf.requireCsrfProtectionMatcher(csrfRequestMatcher))
			.headers(headers -> headers
				.contentSecurityPolicy(contentSecurityPolicy)
				.and()
				.frameOptions().sameOrigin()
			)
			.requestCache().requestCache(requestCache)
			.and()
			.formLogin(form -> form
				.loginPage("/loginKr.do")
				.loginProcessingUrl("/loginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
				.permitAll()
			)					
			.formLogin(form -> form
				.loginPage("/loginEn.do")
				.loginProcessingUrl("/loginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
			)
	
			.formLogin(form -> form
				.loginPage("/spLogin.do")
				.loginProcessingUrl("/spLoginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(supplierAuthenticationSuccessHandler)
				.failureHandler(supplierAuthenticationFailureHandler)
				.permitAll()
			)
			.formLogin(form -> form
				.loginPage("/spLoginKr.do")
				.loginProcessingUrl("/spLoginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(supplierAuthenticationSuccessHandler)
				.failureHandler(supplierAuthenticationFailureHandler)
				.permitAll()
			)
			.formLogin(form -> form
				.loginPage("/spLoginEn.do")
				.loginProcessingUrl("/spLoginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(supplierAuthenticationSuccessHandler)
				.failureHandler(supplierAuthenticationFailureHandler)
				.permitAll()
			)
			// 403 발생시, 리다이렉트할 URL을 가장 마지막에 위치
			.formLogin(form -> form
				.loginPage("/login.do")
				.loginProcessingUrl("/loginProcess.do")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
			)			
			.logout(logout -> logout
				.logoutUrl("/logoutProcess.do")
				.logoutSuccessHandler(logoutSuccessHandler)
				.invalidateHttpSession(true)
				.deleteCookies(jsessionid)
			)
			.addFilterBefore(tenantFilter, DisableEncodeUrlFilter.class)
			.addFilterBefore(decrytRequestFilter, SecurityContextPersistenceFilter.class)
			.addFilterAfter(xssRequestFilter, SecurityContextPersistenceFilter.class)
			.addFilterAt(concurrencyFilter, ConcurrentSessionFilter.class)
			.addFilterBefore(customAuthenticationProcessingFilters, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(sessionInvalidateFilter, SessionManagementFilter.class)
			.sessionManagement(session -> session.sessionAuthenticationStrategy(sessionAuthenticationStrategy))
			;
			
		return http.build();
	}
}
