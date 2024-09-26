package smartsuite.config.workplace.lib;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;
import smartsuite.config.workplace.WorkplaceProvider;


public class WorkplaceCacheInitializer implements InitializingBean{
	@Inject
	WorkplaceProvider workplaceProvider;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		workplaceProvider.load();
	}
}
