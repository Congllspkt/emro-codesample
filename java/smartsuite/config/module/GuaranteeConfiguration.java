package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class GuaranteeConfiguration {
	@Bean("guar")
	public Properties guarProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/guarantee-if.properties");
	}
}
