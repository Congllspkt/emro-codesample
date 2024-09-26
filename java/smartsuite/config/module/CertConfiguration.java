package smartsuite.config.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.app.common.cert.pki.factory.SignatureFactoryBean;
import smartsuite.app.common.cert.pki.factory.VerificationFactoryBean;
import smartsuite.app.common.cert.CertInitSettings;
import smartsuite.config.module.utils.ModulePropertiesUtils;

import java.util.Properties;

@Configuration
public class CertConfiguration {

	@Bean("cert")
	public Properties certProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/cert.properties");
	}

	@Bean("crosscert_config")
	public Properties configProperties() {
		return ModulePropertiesUtils.loadProperties("smartsuite/properties/config.properties");
	}

	@Bean("signatureProvider")
	public SignatureFactoryBean signatureFactoryBean() {
		return new SignatureFactoryBean();
	}

	@Bean("verificationProvider")
	public VerificationFactoryBean verificationFactoryBean() {
		return new VerificationFactoryBean();
	}

	@Bean("certInitSettings")
	public CertInitSettings certInitSettings() {
		return new CertInitSettings();
	}
}
