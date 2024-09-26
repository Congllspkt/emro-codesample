package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.app.webservice.AuthenticationInterceptor;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class CommonConfiguration {

    @Bean("globalProperties")
    public Properties globalProperties() {
        return ModulePropertiesUtils.loadProperties("smartsuite/properties/global.properties");
    }

    @Bean("loggingProperties")
    public Properties loggingProperties() {
        return ModulePropertiesUtils.loadProperties("smartsuite/properties/logging.properties");
    }

    @Bean("quartzProperties")
    public Properties quartzProperties() {
        return ModulePropertiesUtils.loadProperties("smartsuite/properties/quartz.properties");
    }

    @Bean("smartsuiteProperties")
    public Properties smartsuiteProperties() {
        return ModulePropertiesUtils.loadProperties("smartsuite/properties/smartsuite.properties");
    }

    @Bean("mail")
    public Properties mail() {
        return ModulePropertiesUtils.loadEncryptedProperties("smartsuite/properties/encrypt/mail.properties");
    }

    @Bean("sftp")
    public Properties sftp() {
        return ModulePropertiesUtils.loadEncryptedProperties("smartsuite/properties/encrypt/sftp.properties");
    }
}
