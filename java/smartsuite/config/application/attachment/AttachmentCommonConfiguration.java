package smartsuite.config.application.attachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import smartsuite5.attachment.core.conn.StorageConn;
import smartsuite5.attachment.core.conn.impl.LocalStorageConn;
import smartsuite5.attachment.core.crypto.impl.BasicAthfCryptoUtil;
import smartsuite5.attachment.core.exception.handler.impl.BasicStorageAthfExceptionHandler;
import smartsuite5.attachment.core.mgr.conn.StorageConnMgr;
import smartsuite5.attachment.core.prcs.impl.AccessLogPrcs;
import smartsuite5.attachment.core.prcs.impl.AthUuidCryptoPrcs;
import smartsuite5.attachment.core.prcs.impl.ThumbnailPrcs;
import smartsuite5.attachment.core.util.MimeTypeUtil;
import smartsuite5.attachment.web.service.StorageAthfService;

@Configuration
public class AttachmentCommonConfiguration {
	
	@Bean("storageConnMgr")
	public StorageConnMgr storageConnMgr(LocalStorageConn fsStorageConn) {
		StorageConnMgr storageConnMgr = new StorageConnMgr();
		storageConnMgr.setDefaultStorageId("fsStorageConn");
		
		Map<String, StorageConn> connCache = new HashMap<>();
		connCache.put("fsStorageConn", fsStorageConn);
		storageConnMgr.setConnCache(connCache);
		
		return storageConnMgr;
	}
	
	@Bean("storageAthfService")
	public StorageAthfService storageAthfService(LocalStorageConn fsStorageConn) {
		StorageAthfService storageAthfService = new StorageAthfService();
		storageAthfService.setStorageConnMgr(this.storageConnMgr(fsStorageConn));
		return storageAthfService;
	}
	
	@Bean("athfCryptoPrcs")
	public AthUuidCryptoPrcs athfCryptoPrcs() {
		return new AthUuidCryptoPrcs();
	}
	
	@Bean("thumbnailPrcs")
	public ThumbnailPrcs thumbnailPrcs() {
		ThumbnailPrcs thumbnailPrcs = new ThumbnailPrcs();
		thumbnailPrcs.setThumbnailPostfix(ThumbnailPrcs.DEFAULT_THUMBNAIL_POSTFIX);
		thumbnailPrcs.setWidth(120);
		thumbnailPrcs.setHeight(120);
		thumbnailPrcs.setThumbnailFormat(ThumbnailPrcs.DEFAULT_THUMBNAIL_FORMAT);
		
		List<String> targetMimeTypes = new ArrayList<>();
		targetMimeTypes.add("png");
		targetMimeTypes.add("jpg");
		thumbnailPrcs.setTargetMimeTypes(targetMimeTypes);
		
		return thumbnailPrcs;
	}
	
	@Bean("accessLogPrcs")
	public AccessLogPrcs accessLogPrcs() {
		return new AccessLogPrcs();
	}
	
	@Bean("mimeTypeUtil")
	public MimeTypeUtil mimeTypeUtil() {
		return new MimeTypeUtil();
	}
	
	@Bean("athfCryptoUtil")
	public BasicAthfCryptoUtil athfCryptoUtil() {
		return new BasicAthfCryptoUtil();
	}
	
	@Bean("storageExceptionHandler")
	public BasicStorageAthfExceptionHandler storageExceptionHandler() {
		return new BasicStorageAthfExceptionHandler();
	}
	
}
