package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class DocusignConfiguration {
	@Bean("docusign")
	public Properties docusignProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/docusign.properties");
	}
}
