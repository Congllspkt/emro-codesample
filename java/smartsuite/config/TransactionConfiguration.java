package smartsuite.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
//@ImportResource(value = { "classpath:smartsuite/transaction-context.xml" })
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class TransactionConfiguration {

	@Bean("jpaTransactionManager")
	public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory);
		return jpaTransactionManager;
	}

	@Bean("dataSourceTransactionManager")
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(dataSource);
		return dataSourceTransactionManager;
	}

	@Primary
	@Bean("transactionManager")
	public ChainedTransactionManager transactionManager(DataSourceTransactionManager dataSourceTransactionManager,
			JpaTransactionManager jpaTransactionManager) {
		ChainedTransactionManager chainedTransactionManager = new ChainedTransactionManager(jpaTransactionManager, dataSourceTransactionManager);
		return chainedTransactionManager;
	}

	@Bean
	public TransactionInterceptor txAdvice(TransactionManager transactionManager) {
		TransactionInterceptor txAdvice = new TransactionInterceptor();
		txAdvice.setTransactionManager(transactionManager);
		Properties transactionAttributes = new Properties();
		transactionAttributes.setProperty("*", "PROPAGATION_REQUIRED");
		txAdvice.setTransactionAttributes(transactionAttributes);
		return txAdvice;
	}

}
