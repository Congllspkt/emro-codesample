package smartsuite.app.common.cert.pki.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import smartsuite.app.common.cert.pki.verification.CrossCertVerificationProvider;
import smartsuite.app.common.cert.pki.verification.KICAVerificationProvider;
import smartsuite.app.common.cert.pki.verification.UsCrossCertVerificationProvider;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.exception.CommonException;

public class VerificationFactoryBean implements FactoryBean<VerificationProvider>, ApplicationContextAware {

	@Value("#{cert['pki.module']}")
	private SignModule signModule;

	ApplicationContext applicationContext;

	private static final Logger LOG = LoggerFactory.getLogger(VerificationFactoryBean.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public VerificationProvider getObject(){
		LOG.info("MODULE : {}" , signModule);
		
		if(signModule.equals(SignModule.CrossCert)){
			return (VerificationProvider)applicationContext.getBean(CrossCertVerificationProvider.class);
		}else if(signModule.equals(SignModule.KICA)){
			return (VerificationProvider)applicationContext.getBean(KICAVerificationProvider.class);
		}else if(signModule.equals(SignModule.USCrossCert)){
			return (VerificationProvider)applicationContext.getBean(UsCrossCertVerificationProvider.class);
		}
		throw new CommonException(this.getClass().getName() + " : fail to load VerificationProvider");
	}

	public Class<VerificationProvider> getObjectType() {
		return VerificationProvider.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
