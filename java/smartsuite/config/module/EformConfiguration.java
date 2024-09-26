package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class EformConfiguration {

	@Bean("eform")
	public Properties eformProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/eform.properties");
	}
}
