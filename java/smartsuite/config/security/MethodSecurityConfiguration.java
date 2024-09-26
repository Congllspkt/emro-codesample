package smartsuite.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import smartsuite.security.account.spring.AccountMethodSecurityExpressionHandler;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

	@Bean(name = "methodSecurityExpressionHandler")
	public AccountMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		return new AccountMethodSecurityExpressionHandler();
	}	
}
