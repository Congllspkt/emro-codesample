package smartsuite.config.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@Configuration
//@ImportResource(value = { "classpath:smartsuite/module-context.xml" })
@ComponentScan(
	basePackages = { "smartsuite5.config" },
	includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),
	})
@ComponentScan(
	basePackages = { "smartsuite5.*.persist.jpa.core" },
	includeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class),
	})
public class ModuleConfiguration {

}
