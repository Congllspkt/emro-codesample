package smartsuite.config.application;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.zaxxer.hikari.HikariDataSource;

import smartsuite.mybatis.CustomizedObjectWrapperFactory;
import smartsuite.mybatis.DefaultParameterInjector;
import smartsuite.mybatis.ReconfigurableSqlSessionFactoryBean;
import smartsuite.mybatis.ResultSetLoggingInterceptor;
import smartsuite.mybatis.SqlIdLoggingInterceptor;
import smartsuite.mybatis.dialect.Dialect;
import smartsuite.mybatis.dialect.OracleDialect;
import smartsuite.mybatis.plugin.page.interceptor.PageInterceptor;
import smartsuite.mybatis.plugin.parameter.ParameterInjector;
import smartsuite.mybatis.plugin.parameter.interceptor.ParameterInjectInterceptor;
import smartsuite.mybatis.reflection.PageableObjectFactory;

@Configuration
@PropertySource("classpath:smartsuite/properties/datasource.properties")
//@ImportResource(value = { "classpath:smartsuite/mybatis-context.xml" })
public class MybatisConfiguration {
	
	private Environment environment;
	
    public MybatisConfiguration(Environment environment) {
        this.environment = environment;
    }
    
    Properties mybatisConfigurationProperties() {
    	Properties configurationProperties = new Properties();
		configurationProperties.setProperty("dialectClass", environment.getProperty("mybatisConfigurationProperties.dialectClass"));
		return configurationProperties;
    }
    
	@Bean("sqlSessionFactory")
	public ReconfigurableSqlSessionFactoryBean sqlSessionFactory(@Qualifier("dataSource") DataSource datasource) throws IOException {
		ReconfigurableSqlSessionFactoryBean sqlSessionFactory = new ReconfigurableSqlSessionFactoryBean();
		
		sqlSessionFactory.setConfigurationProperties(this.mybatisConfigurationProperties());
		sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis.xml"));
		sqlSessionFactory.setDataSource(datasource);
		
		sqlSessionFactory.setMapperLocations(this.getMapperLocations());
		sqlSessionFactory.setDatabaseIdProvider(this.databaseIdProvider());
		
		return sqlSessionFactory;
	}
	
    private Resource[] getMapperLocations() throws IOException {
    	PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(String.format("classpath*:smartsuite/mappers/**/**/*.xml"));
    	Resource[] resourcesRnd = resolver.getResources(String.format("classpath*:smartsuite/mappers/**/**/**/*.xml"));
        
    	return mergeArrays(resources, resourcesRnd);
    }
    
    private Resource[] mergeArrays(Resource[] arr1, Resource[] arr2) {
        int length = arr1.length + arr2.length;
        Resource[] merged = new Resource[length];
        System.arraycopy(arr1, 0, merged, 0, arr1.length);
        System.arraycopy(arr2, 0, merged, arr1.length, arr2.length);
        return merged;
    }
    
	@Bean("sqlSession") 
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		SqlSessionTemplate sqlSession = new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
		return sqlSession;
	}
	
	@Bean("noLoggingSqlSessionFactory") 
	public SqlSessionFactoryBean noLoggingSqlSessionFactory(@Qualifier("actualDataSource") HikariDataSource actualDataSource) throws IOException {
		SqlSessionFactoryBean noLoggingSqlSessionFactory = new SqlSessionFactoryBean();
		
		noLoggingSqlSessionFactory.setConfigurationProperties(this.mybatisConfigurationProperties());
		noLoggingSqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis.xml"));
		noLoggingSqlSessionFactory.setDataSource(actualDataSource);
		
		Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources(String.format("classpath:smartsuite/log/mapper/logging-access.xml"));
		noLoggingSqlSessionFactory.setMapperLocations(mapperLocations);
		
		noLoggingSqlSessionFactory.setDatabaseIdProvider(this.databaseIdProvider());
		
		return noLoggingSqlSessionFactory;
	}
	
	/** Logging 제외  (use actualDataSource) */
	@Bean("noLoggingSqlSession") 
	public SqlSessionTemplate noLoggingSqlSession(SqlSessionFactory noLoggingSqlSessionFactory) {
		SqlSessionTemplate noLoggingSqlSession = new SqlSessionTemplate(noLoggingSqlSessionFactory, ExecutorType.BATCH);
		return noLoggingSqlSession;
	}
	
	@Bean("databaseIdProvider")
	public VendorDatabaseIdProvider databaseIdProvider() {
		Properties properties = new Properties();
		properties.setProperty("Oracle"		, "oracle");
		properties.setProperty("SQL Server"	, "mssql");
		properties.setProperty("MySQL"		, "mysql");
		properties.setProperty("Postgre"	, "postgresql");
		properties.setProperty("PostgreSql"	, "postgresql");
		properties.setProperty("Tibero"		, "oracle");
		/*
		properties.setProperty("DB2"		, "db2");
		properties.setProperty("Derby"		, "derby");
		properties.setProperty("HSQL"		, "hsqldb");
		properties.setProperty("H2"			, "h2");

		*/
		
		VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
		databaseIdProvider.setProperties(properties);
		return databaseIdProvider;
	}
	
}
