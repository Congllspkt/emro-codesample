package smartsuite.config.module.utils;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import smartsuite5.security.jasypt.EncPropertiesFactoryBean;

import java.io.IOException;
import java.util.Properties;

public class ModulePropertiesUtils {
    public static Properties loadEncryptedProperties(String path) {
        try {
            Resource resource = new ClassPathResource(path);
             //Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            propertiesFactoryBean.setLocation(resource);
            propertiesFactoryBean.afterPropertiesSet();
            Properties properties = propertiesFactoryBean.getObject();

            EncPropertiesFactoryBean encPropertiesFactoryBean = new EncPropertiesFactoryBean();
            encPropertiesFactoryBean.setProperties(properties);
            encPropertiesFactoryBean.afterPropertiesSet();

            return encPropertiesFactoryBean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Properties loadProperties(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
            propertiesFactoryBean.setLocation(resource);
            propertiesFactoryBean.afterPropertiesSet();
            return propertiesFactoryBean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
