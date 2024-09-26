package smartsuite.config.application.attachment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import smartsuite5.attachment.core.conn.impl.LocalStorageConn;
import smartsuite5.attachment.core.dat.StorageAccessInfo;
import smartsuite5.attachment.core.dat.StorageAthfLmtInfo;
import smartsuite5.attachment.core.dat.impl.DateFormatBasedStorageLocationInfo;
import smartsuite5.attachment.core.exception.handler.StorageAthfExceptionHandler;
import smartsuite5.attachment.core.exception.handler.impl.BasicStorageAthfExceptionHandler;
import smartsuite5.attachment.core.mgr.descriptor.StorageAthfDescriptorMgr;
import smartsuite5.attachment.core.mgr.descriptor.impl.StandardDBMSStorageAthfDescriptorMgr;
import smartsuite5.attachment.core.prcs.StorageAthfDescriptorMgrPostPrcs;
import smartsuite5.attachment.core.prcs.StorageAthfPostPrcs;
import smartsuite5.attachment.core.prcs.StorageAthfPrePrcs;
import smartsuite5.attachment.core.prcs.impl.AccessLogPrcs;
import smartsuite5.attachment.core.prcs.impl.AthUuidCryptoPrcs;
import smartsuite5.attachment.core.prcs.impl.ThumbnailPrcs;

@Configuration
@PropertySource("classpath:smartsuite/properties/smartsuite.properties")
public class AttachmentStorageFsConfiguration {

	private Environment environment;

    public AttachmentStorageFsConfiguration(Environment environment) {
        this.environment = environment;
    }
	
	@Bean("storageAccessInfo")
	public StorageAccessInfo storageAccessInfo() {
		String uploadPath = environment.getProperty("smartsuite.upload.path");
		StorageAccessInfo storageAccessInfo = new StorageAccessInfo();
		storageAccessInfo.setUrl(uploadPath);
		return storageAccessInfo;
	}
	
	@Bean("storageLocInfo")
	public DateFormatBasedStorageLocationInfo storageLocInfo() {
		DateFormatBasedStorageLocationInfo storageLocInfo = new DateFormatBasedStorageLocationInfo();
		storageLocInfo.setSubPathPattern("yyyy/MM/dd");
		return storageLocInfo;
	}
	
	@Bean("limitInfo")
	public StorageAthfLmtInfo limitInfo() {
		
		String maxFileSize = environment.getProperty("smartsuite.attachment.max-file-size");
		String minFileSize = "0bytes";
		String maxTotalFileSize = environment.getProperty("smartsuite.attachment.max-total-file-size");
		long maxTotalFileCount = 100;
		String extension = environment.getProperty("smartsuite.attachment.extension");
		String restrictFileName = "html,jsp,java,class,js,exe";
		Boolean preventDuplicate = false;
		
		return new StorageAthfLmtInfo(maxFileSize, minFileSize, maxTotalFileSize, maxTotalFileCount, 
										extension, restrictFileName, preventDuplicate);
	}
	
	@Bean("dbmsDescMgr")
	public StandardDBMSStorageAthfDescriptorMgr dbmsDescMgr (ThumbnailPrcs thumbnailPrcs) {
		StandardDBMSStorageAthfDescriptorMgr dbmsDescMgr = new StandardDBMSStorageAthfDescriptorMgr();
		
		List<StorageAthfDescriptorMgrPostPrcs> postPrcsList = new ArrayList<>();
		postPrcsList.add(thumbnailPrcs);
		dbmsDescMgr.setPostPrcsList(postPrcsList);
		
		return dbmsDescMgr;
	}
	
	@Bean("fsStorageConn")
	public LocalStorageConn fsStorageConn(
			ThumbnailPrcs thumbnailPrcs,
			AthUuidCryptoPrcs athfCryptoPrcs,
			AccessLogPrcs accessLogPrcs,
			BasicStorageAthfExceptionHandler storageExceptionHandler) {
		LocalStorageConn fsStorageConn = new LocalStorageConn();
		fsStorageConn.setStorageName("파일 시스템 저장소");
		fsStorageConn.setStorageDescription("파일 시스템에 접근하는 저장소");
		fsStorageConn.setAccessInfo(this.storageAccessInfo());
		fsStorageConn.setLocationInfo(this.storageLocInfo());
		fsStorageConn.setAthfLmtInfo(this.limitInfo());
		
		List<StorageAthfDescriptorMgr> athfDescriptorMgrList = new ArrayList<>();
		athfDescriptorMgrList.add(this.dbmsDescMgr(thumbnailPrcs));
		fsStorageConn.setAthfDescriptorMgrList(athfDescriptorMgrList);
		
		List<StorageAthfPrePrcs> prePrcsList = new ArrayList<>();
		prePrcsList.add(athfCryptoPrcs);
		prePrcsList.add(accessLogPrcs);
		fsStorageConn.setPrePrcsList(prePrcsList);
		
		List<StorageAthfPostPrcs> postPrcsList = new ArrayList<>();
		postPrcsList.add((StorageAthfPostPrcs) athfCryptoPrcs);
		postPrcsList.add(accessLogPrcs);
		postPrcsList.add(thumbnailPrcs);
		fsStorageConn.setPostPrcsList(postPrcsList);
		
		
		List<StorageAthfExceptionHandler> exceptionHandlerList = new ArrayList<>();
		exceptionHandlerList.add(storageExceptionHandler);
		fsStorageConn.setExceptionHandlerList(exceptionHandlerList);
		
		return fsStorageConn;
	}
	
	
}
