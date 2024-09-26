package smartsuite.config;

import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import smartsuite.config.application.*;
import smartsuite.security.captcha.CaptchaGenerator;

@Configuration
@Import({
	MybatisConfiguration.class,
	AttachmentConfiguration.class,
	ModuleConfiguration.class,
	CacheConfiguration.class,
    MailConfiguration.class,
    WebServiceConfiguration.class
})
@ComponentScan(
	basePackages = { "smartsuite.config.module" },
        useDefaultFilters = false,
	includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
)
//@ImportResource(value = { "classpath*:smartsuite/module/*-context.xml" })
@ComponentScan(
    basePackages = {
        "smartsuite.*.web",
        "smartsuite.security",
        "smartsuite.tenancy",
        "smartsuite.app",
        "smartsuite.mybatis",
        "smartsuite.config.workplace",
        "smartsuite5.*.web"
    },
    useDefaultFilters = false,
    lazyInit = true,
    includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public class ApplicationConfiguration {

	// moduleManager
	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean() {
		MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
		factoryBean.setStaticMethod("smartsuite.module.ModuleManager.setLocalProperties");
		factoryBean.setArguments("classpath*:smartsuite/module/*.properties");
		return factoryBean;
	}

	// objectMapper
    @Bean("objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // captcha 인증
    @Bean("captchaGenerator")
    public CaptchaGenerator captchaGenerator() {
        return new CaptchaGenerator();
    }

    // restTemplate
    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Gson
    @Bean("gsonBuilder")
    public GsonBuilder gsonBuilder() {
        return new GsonBuilder().disableHtmlEscaping();
    }

    @Bean("gson")
    public Gson gson() {
        return gsonBuilder().create();
    }
}
