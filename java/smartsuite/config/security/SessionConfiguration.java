package smartsuite.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.web.filter.GenericFilterBean;

import smartsuite.security.session.persist.MemorySessionPersister;
import smartsuite.security.session.service.SessionRegistryService;
import smartsuite.security.spring.core.session.SessionRegistryImpl;
import smartsuite.security.spring.session.service.SessionRegistryServiceImpl;
import smartsuite.security.spring.web.authentication.session.ConcurrentSessionControlAuthenticationPersistStrategy;
import smartsuite.security.spring.web.session.ExpiredSessionStrategy;
import smartsuite.security.spring.web.session.SessionInvalidateFilter;
import smartsuite.security.spring.web.session.SimpleExpiredSessionStrategy;
import smartsuite.security.spring.web.session.SimpleInvalidSessionStrategy;

@Configuration
public class SessionConfiguration {

	@Value("#{smartsuiteProperties['smartsuite.session.max-session']}")
	private int maxSession = 1;

	/** [TO-DO] 다국어 처리 필요 */
	private static final String EXPIRED_SESSION_ERROR_MESSAGE = "중복로그인 되었습니다.";
	
	private static final String INVALID_SESSION_ERROR_MESSAGE = "장시간 사용하지 않아 연결이 종료되었습니다.";
	
	// 세션객체와 퍼시스터를 연결하는 스프링 프레임워크 아답터 클래스 
	// SessionRegistryService 인터페이스의 스프링 프레임워크 구현체로  SessionPersister 인터페이스를 이용하여 퍼시스턴스 영역을 프로젝트에 따라 구현할 수 있도록 제공합니다.
	@Bean(name = "sessionRegistryService")
	public SessionRegistryService sessionRegistryService() {
		SessionRegistryServiceImpl service = new SessionRegistryServiceImpl();
		service.setSessionPersister(new MemorySessionPersister());
		return service;
	}

	// 스프링 SessionRegistry 구현체
	@Bean(name = "sessionRegistryImpl")
	public SessionRegistry sessionRegistryImpl(SessionRegistryService sessionRegistryService) {
		SessionRegistryImpl service = new SessionRegistryImpl();
		service.setSessionRegistryService(sessionRegistryService);
		return service;
	}

	@Bean(name = "registerSessionAuthenticationStrategy")
	public SessionAuthenticationStrategy registerSessionAuthenticationStrategy(SessionRegistryImpl sessionRegistryImpl) {
		return new RegisterSessionAuthenticationStrategy(sessionRegistryImpl);
	}
	
	@Bean(name = "expiredSessionStrategy")
	public ExpiredSessionStrategy expiredSessionStrategy() {
		SimpleExpiredSessionStrategy strategy = new SimpleExpiredSessionStrategy();
		strategy.setExpiredSessionUrl("/sessionExpired.do");
		strategy.setExpiredSessionErrorMessage(EXPIRED_SESSION_ERROR_MESSAGE);
		return strategy;
	}
	
	// 세션 무효화 처리 필터
	@Bean(name = "sessionInvalidateFilter")
	public GenericFilterBean sessionInvalidateFilter() {
		SessionInvalidateFilter sessionInvalidateFilter = new SessionInvalidateFilter();
		SimpleInvalidSessionStrategy strategy = new SimpleInvalidSessionStrategy(); // InvalidSessionStrategy 구현체로 요청유형에 따라 invalidate 방식이 다름
		strategy.setInvalidSessionUrl("/invalidSession.do"); // 일반적인 페이지 요청에 대한 처리
		strategy.setInvalidSessionErrorMessage(INVALID_SESSION_ERROR_MESSAGE); // 세션 무효화 기본 메세지
		sessionInvalidateFilter.setInvalidSessionStrategy(strategy);
		return sessionInvalidateFilter;
	}	
	
	@Bean(name = "concurrentSessionControlAuthenticationPersistStrategy")
	public SessionAuthenticationStrategy concurrentSessionControlAuthenticationPersistStrategy(
			SessionRegistry sessionRegistryImpl,
			SessionRegistryService sessionRegistryService) {
		ConcurrentSessionControlAuthenticationPersistStrategy strategy = new ConcurrentSessionControlAuthenticationPersistStrategy(sessionRegistryImpl, sessionRegistryService);
		strategy.setMaximumSessions(maxSession);
		strategy.setExceptionIfMaximumExceeded(false);
		return strategy; 
	}
	
	/**
	 * 세션 인증처리
	 */
	@Bean(name = "sessionAuthenticationStrategy")
	public CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy(
			SessionAuthenticationStrategy concurrentSessionControlAuthenticationPersistStrategy,
			SessionAuthenticationStrategy registerSessionAuthenticationStrategy) {
		CompositeSessionAuthenticationStrategy strategy = new CompositeSessionAuthenticationStrategy(
			Arrays.asList(
				concurrentSessionControlAuthenticationPersistStrategy,
				new SessionFixationProtectionStrategy(),
				registerSessionAuthenticationStrategy)
			);
		return strategy;
	}
}
