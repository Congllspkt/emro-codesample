package smartsuite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import smartsuite.log.core.override.SqlIdLoggingListener;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:smartsuite/properties/datasource.properties")
//@ImportResource(value = { "classpath:smartsuite/datasource-context.xml" })
public class DatasourceConfiguration {

	private Environment environment;
	
    public DatasourceConfiguration(Environment environment) {
        this.environment = environment;
    }
	
    private HikariConfig defaultConfig() {
    	HikariConfig config = new HikariConfig();
		config.setDriverClassName(environment.getProperty("default.datasource.driverclassname"));
		config.setJdbcUrl(environment.getProperty("default.datasource.url"));
		config.setUsername(environment.getProperty("default.datasource.username"));
		config.setPassword(environment.getProperty("default.datasource.password"));
		config.setMaximumPoolSize(Integer.parseInt(environment.getProperty("default.datasource.maximum-pool-size")));
		config.setMaxLifetime(30000);
		config.setIdleTimeout(30000);
		return config;
    }

	@Bean("actualDataSource")
    public DataSource actualDataSource() {
		String dataSourceType = environment.getProperty("default.datasource.dataSourceType","Hikari");
        if ("jndi".equalsIgnoreCase(dataSourceType)) {
            return createJndiDataSource();
        } else {
            return createHikariDataSource();
        }
    }

	public HikariDataSource createHikariDataSource() {
		HikariDataSource actualDataSource = new HikariDataSource(this.defaultConfig());
		return actualDataSource;
	}

	public DataSource createJndiDataSource() {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        return dataSourceLookup.getDataSource("jdbc/Databasename");
    }
	
	@Bean("actualMySQLDataSource")
	public HikariDataSource actualMySQLDataSource() {
		HikariDataSource actualDataSource = new HikariDataSource(this.defaultConfig());
		return actualDataSource;
	}
	
	@Bean("sqlIdLoggingListener")
	public SqlIdLoggingListener sqlIdLoggingListener() {
		SqlIdLoggingListener sqlIdLoggingListener = new SqlIdLoggingListener();
		sqlIdLoggingListener.setIncludeParameter(true);
		sqlIdLoggingListener.setUseReplaceParameter(true);
		sqlIdLoggingListener.setUseReplaceWhitespace(false);
		sqlIdLoggingListener.setUseExcludeSqlLogging(true);
		sqlIdLoggingListener.setLogLevel(SLF4JLogLevel.INFO);
		sqlIdLoggingListener.setIncludeSqlId(true);
		
		// Query ElapsedTime Level (millisecond)
		sqlIdLoggingListener.setTimeLevelNormal(1000);
		sqlIdLoggingListener.setTimeLevelWarning(5000);
		sqlIdLoggingListener.setTimeLevelCritical(10000);
		
		return sqlIdLoggingListener;
	}
	
	@Bean("proxyMySQLDataSource")
	public ProxyDataSource proxyMySQLDataSource() {
		ProxyDataSource proxyMySQLDataSource = new ProxyDataSource();
		
		proxyMySQLDataSource.setDataSource(this.actualMySQLDataSource());
		proxyMySQLDataSource.setDataSourceName("default");
		
		return proxyMySQLDataSource;
	}
	
	
	@Bean("proxyDataSource")
	public ProxyDataSource proxyDataSource() {
		ProxyDataSource proxyDataSource = new ProxyDataSource();
		
		proxyDataSource.setDataSource(this.actualDataSource());
		proxyDataSource.setDataSourceName("default");
		proxyDataSource.setListener(this.sqlIdLoggingListener());
		
		return proxyDataSource;
	}
	
	@Bean("dataSource")
	public LazyConnectionDataSourceProxy dataSource() {
		LazyConnectionDataSourceProxy dataSource = new LazyConnectionDataSourceProxy();
		dataSource.setTargetDataSource(this.proxyDataSource());
		/* DB 접근 권한이 없는 DataSource의 경우
		dataSource.setDefaultAutoCommit(false);
		dataSource.setDefaultTransactionIsolationName("TRANSACTION_READ_COMMITTED");
		*/
		return dataSource;
	}
	
	@Bean("mysqlDataSource")
	public LazyConnectionDataSourceProxy mysqlDataSource() {
		LazyConnectionDataSourceProxy mysqlDataSource = new LazyConnectionDataSourceProxy();
		mysqlDataSource.setTargetDataSource(this.proxyMySQLDataSource());
		return mysqlDataSource;
	}
	
}
