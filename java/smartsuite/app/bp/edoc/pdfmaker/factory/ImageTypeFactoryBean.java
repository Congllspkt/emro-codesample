package smartsuite.app.bp.edoc.pdfmaker.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import smartsuite.app.bp.edoc.pdfmaker.ImageTypeProvider;
import smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy.DBAttachFileTypeStrategy;
import smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy.PropFileDirTypeStrategy;
import smartsuite.app.bp.edoc.pdfmaker.imageTypeStrategy.UrlTypeStrategy;
import smartsuite.exception.CommonException;

public class ImageTypeFactoryBean implements FactoryBean<ImageTypeProvider>, ApplicationContextAware {

	@Value("#{edoc['watermark.type']}")
	private WatermarkType watermarkTyp;

	private ApplicationContext applicationContext;

	private static final Logger LOG = LoggerFactory.getLogger(ImageTypeFactoryBean.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public ImageTypeProvider getObject(){
		LOG.info("watermark type : {}" , watermarkTyp);

		if(watermarkTyp.equals(watermarkTyp.DB)){
			return (ImageTypeProvider)applicationContext.getBean(DBAttachFileTypeStrategy.class);
		}else if(watermarkTyp.equals(watermarkTyp.FILE)){
			return (ImageTypeProvider)applicationContext.getBean(PropFileDirTypeStrategy.class);
		}else if(watermarkTyp.equals(watermarkTyp.URL)){
			return (ImageTypeProvider)applicationContext.getBean(UrlTypeStrategy.class);
		}
		throw new CommonException(this.getClass().getName() + " : fail to load ImageTypeProvider");
	}

	public Class<ImageTypeProvider> getObjectType() {
		return ImageTypeProvider.class;
	}

	public boolean isSingleton() {
		return true;
	}


}
