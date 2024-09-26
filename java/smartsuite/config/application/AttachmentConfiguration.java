package smartsuite.config.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import smartsuite.config.application.attachment.AttachmentCommonConfiguration;
import smartsuite.config.application.attachment.AttachmentStorageFsConfiguration;
import smartsuite.upload.StdFileService;
import smartsuite5.attachment.core.mgr.conn.StorageConnMgr;
import smartsuite5.attachment.web.service.StorageAthfService;

@Configuration
//@ImportResource(value = { 
//		"classpath:smartsuite/attachment/attachment-context-common.xml", 
//		"classpath:smartsuite/attachment/attachment-context-storage-fs.xml" })
//@ComponentScan(
//	basePackages = { "smartsuite.config.application.attachment" },
//	useDefaultFilters = false,
//	includeFilters = {
//		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),
//	})
@Import({
	AttachmentCommonConfiguration.class,
	AttachmentStorageFsConfiguration.class
})
public class AttachmentConfiguration {

	@Bean("stdFileService")
	public StdFileService stdFileService(StorageAthfService storageAthfService, StorageConnMgr storageConnMgr) {
		return new StdFileService(storageAthfService, storageConnMgr);
	}
}
