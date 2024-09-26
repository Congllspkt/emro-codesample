package smartsuite.config;

import org.springframework.context.annotation.*;
import smartsuite.app.webservice.AuthenticationInterceptor;

@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml"}) // 반드시 있어야 함
@ComponentScan(
	basePackages = { "smartsuite.config.cxf" },
        useDefaultFilters = false,
	includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
)
public class WebServiceConfiguration {

    @Bean
    public AuthenticationInterceptor getAuthenticationInterceptor() {
        AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor();
        authInterceptor.setUsername("cxf");
        authInterceptor.setPassword("emro1004!");
        return authInterceptor;
    }
}