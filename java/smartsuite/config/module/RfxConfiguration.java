package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class RfxConfiguration {
    @Bean("rfx")
    public Properties smartsuiteProperties() {
        return ModulePropertiesUtils.loadProperties("smartsuite/module/rfx.properties");
    }
}
