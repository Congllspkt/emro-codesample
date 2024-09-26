package smartsuite;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import smartsuite.config.ApplicationConfiguration;
import smartsuite.config.DatasourceConfiguration;
import smartsuite.config.SecurityConfiguration;
import smartsuite.config.TransactionConfiguration;

@Configuration
@Import({
	DatasourceConfiguration.class,
	TransactionConfiguration.class,
	ApplicationConfiguration.class,
	SecurityConfiguration.class,
})
public class RootContextConfiguration {

}
