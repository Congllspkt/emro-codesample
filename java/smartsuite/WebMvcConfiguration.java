package smartsuite;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;

import smartsuite.excel.core.exporter.CustomDataExportUtil;
import smartsuite.excel.spring.largeexporter.LargeExportUtil;
import smartsuite.exception.ExceptionManagerImpl;
import smartsuite.exception.SpringWebExceptionHandler;
import smartsuite.log.spring.accesslog.AccessLogInterceptor;
import smartsuite.log.spring.accesslog.ExcelExportLogInterceptor;
import smartsuite.mybatis.plugin.page.aop.PageAdvisor;
import smartsuite.security.interceptor.AuthCheckInterceptor;
import smartsuite.security.jackson.ExtendedObjectMapperFactoryBean;
import smartsuite.spring.jackson.FloaterStreamJackson2HttpMessageConverter;
import smartsuite.spring.jackson.ISO8601CompatibleBeanDeserializerFactory;
import smartsuite5.spring.aop.annotation.RequestBodyAdviceImpl;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
@ComponentScan(
	basePackages = { "smartsuite.*.web", "smartsuite.web", "smartsuite.app", "smartsuite.security", "smartsuite.config.workplace", "smartsuite5.*.web", },
	lazyInit = true,
	useDefaultFilters = false,
	includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
	})
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Bean
	public LocaleResolver localeResolver(@Qualifier("messagesourceLocaleResolver") LocaleResolver messagesourceLocaleResolver) {
		return messagesourceLocaleResolver;
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, byteArrayHttpMessageConverter());
		converters.add(1, floaterStreamJackson2HttpMessageConverter());
		converters.add(2, mappingJackson2HttpMessageConverter());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new DeviceHandlerMethodArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authCheckInterceptor());
		registry.addInterceptor(accessLogInterceptor());
		registry.addInterceptor(exportLogInterceptor(jacksonObjectMapper())).addPathPatterns("/**/largeexport.do");
	}
	
	@Bean("byteArrayConverter")
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
		ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
		byteArrayConverter.setSupportedMediaTypes(Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.parseMediaType("video/mp4")));
		return byteArrayConverter;
	}

	@Bean("floaterStreamJackson2HttpMessageConverter")
	public FloaterStreamJackson2HttpMessageConverter floaterStreamJackson2HttpMessageConverter() {
		smartsuite.spring.jackson.FloaterStreamJackson2HttpMessageConverter converter = new FloaterStreamJackson2HttpMessageConverter();
		converter.setObjectMapper(jacksonObjectMapper());
		return converter;
	}
	
	@Bean("mappingJackson2HttpMessageConverter")
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		return new MappingJackson2HttpMessageConverter(jacksonObjectMapper());
	}
	
	@Bean("requestBodyAdviceImpl")
	public RequestBodyAdviceImpl requestBodyAdviceImpl() {
		return new RequestBodyAdviceImpl();
	}

	@Bean("pageAdvisor")
	public PageAdvisor pageAdvisor() {
		return new PageAdvisor();
	}

	@Bean("viewResolver")
	public ViewResolver viewResolver() {
		UrlBasedViewResolver resolver = new UrlBasedViewResolver();
		resolver.setOrder(1);
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean("extendedObjectMapperFactoryBean")
	public ExtendedObjectMapperFactoryBean extendedObjectMapperFactoryBean() {
		ExtendedObjectMapperFactoryBean factoryBean = new ExtendedObjectMapperFactoryBean();
		factoryBean.setDeserializationContext(new DefaultDeserializationContext.Impl(new ISO8601CompatibleBeanDeserializerFactory(new DeserializerFactoryConfig())));
		factoryBean.setDisableSerializationFeatures(Arrays.asList(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
		return factoryBean;
	}
	
	@Bean("jacksonObjectMapper")
	public ObjectMapper jacksonObjectMapper() {
		try {
			return extendedObjectMapperFactoryBean().getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    @Bean("themeSource")
    public ResourceBundleThemeSource themeSource() {
        ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource();
        themeSource.setBasenamePrefix("themes/theme-");
        return themeSource;
    }

    @Bean("themeResolver")
    public CookieThemeResolver themeResolver() {
        CookieThemeResolver themeResolver = new CookieThemeResolver();
        themeResolver.setDefaultThemeName("default");
        return themeResolver;
    }

    @Bean("springWebExceptionHandler")
    public SpringWebExceptionHandler springWebExceptionHandler(ExceptionManagerImpl exceptionManager) {
        SpringWebExceptionHandler exceptionHandler = new SpringWebExceptionHandler();
        exceptionHandler.setExceptionManager(exceptionManager);
        return exceptionHandler;
    }

    @Bean("exceptionManager")
    public ExceptionManagerImpl exceptionManager() {
        ExceptionManagerImpl exceptionManager = new ExceptionManagerImpl();
        exceptionManager.setDebug(true);
        exceptionManager.setRecordable(true);
        return exceptionManager;
    }

    @Bean("largeExportManager")
    public LargeExportUtil largeExportManager(ObjectMapper jacksonObjectMapper) {
        LargeExportUtil largeExportManager = new LargeExportUtil();
        largeExportManager.setObjmapper(jacksonObjectMapper);
        return largeExportManager;
    }

    @Bean("customDataExportManager")
    public CustomDataExportUtil customDataExportManager(ObjectMapper jacksonObjectMapper) {
        CustomDataExportUtil customDataExportManager = new CustomDataExportUtil();
        customDataExportManager.setObjmapper(jacksonObjectMapper);
        return customDataExportManager;
    }
    
    @Bean("authCheckInterceptor")
    public AuthCheckInterceptor authCheckInterceptor() {
    	return new AuthCheckInterceptor();
    }
    
	@Bean("accessLogInterceptor")
	public AccessLogInterceptor accessLogInterceptor() {
		return new AccessLogInterceptor();
	}
	
	@Bean("exportLogInterceptor")
	public ExcelExportLogInterceptor exportLogInterceptor(ObjectMapper jacksonObjectMapper) {
		ExcelExportLogInterceptor exportLogInterceptor = new ExcelExportLogInterceptor();
		exportLogInterceptor.setMapper(jacksonObjectMapper);
		return exportLogInterceptor;
	}
}
