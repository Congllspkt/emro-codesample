package smartsuite.app.common.cert.pki.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import smartsuite.app.common.cert.pki.signature.CrossCertSignatureProvider;
import smartsuite.app.common.cert.pki.signature.KICASignatureProvider;
import smartsuite.app.common.cert.pki.signature.SignatureProvider;
import smartsuite.app.common.cert.pki.signature.UsCrossCertSignatureProvider;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.exception.CommonException;

public class SignatureFactoryBean implements FactoryBean<SignatureProvider>, ApplicationContextAware {
	
	@Value("#{cert['pki.module']}")
	private SignModule signModule;

	private ApplicationContext applicationContext;

	private static final Logger LOG = LoggerFactory.getLogger(SignatureFactoryBean.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public SignatureProvider getObject(){
		LOG.info("MODULE : {}" , signModule);
		
		if(signModule.equals(SignModule.CrossCert)){
			return (SignatureProvider)applicationContext.getBean(CrossCertSignatureProvider.class);
		}else if(signModule.equals(SignModule.KICA)){
			return (SignatureProvider)applicationContext.getBean(KICASignatureProvider.class);
		}else if(signModule.equals(SignModule.USCrossCert)){
			return (SignatureProvider)applicationContext.getBean(UsCrossCertSignatureProvider.class);
		}
		throw new CommonException(this.getClass().getName() + " : fail to load SignatureProvider");
	}

	public Class<SignatureProvider> getObjectType() {
		return SignatureProvider.class;
	}

	public boolean isSingleton() {
		return true;
	}


}
