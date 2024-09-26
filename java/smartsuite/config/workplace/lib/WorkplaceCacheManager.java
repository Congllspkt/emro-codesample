package smartsuite.config.workplace.lib;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Maps;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import smartsuite.config.workplace.vo.WorkTaskMethodSetting;

@Service
public class WorkplaceCacheManager {
	
	private static Logger logger = LoggerFactory.getLogger(WorkplaceCacheManager.class);
	
	/**
	 * cacheName은 ehcache.xml의 workplace cache이름과 동일하게 맞춰야 한다.
	 */
	final static String CACHENAME = "workplace-setup";

	@Inject
	CacheManager cacheManager;
	
	/**
	 * workplace-setup cache 정보를 가져온다.
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public WorkTaskMethodSetting getWorkTaskMethodSetting(String tenId, String key){
		WorkTaskMethodSetting setting = null;
		Element e = getCache(tenId);
		if(e != null){
			Map<String, Object> cacheMap = (Map<String, Object>)e.getObjectValue();
			setting = (WorkTaskMethodSetting)cacheMap.get(key);
		}
		return setting;
	}
	
	private Element getCache(String key){
		Cache cache = getCacheInstance();
		Element e = cache.get(key);
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public void printCache(){
		Cache cache = getCacheInstance();
		logger.info("PRINT CACHE CHECK :: "+ObjectUtils.getDisplayString(cache.getSize()));
		for(Object key : cache.getKeys()){
			logger.info("WORKPLACE SETTING CACHE TENANT INFO ::::::::::::::::::::: "+key);
			if(cache.get(key) != null){
				Map<String, Object> works = (Map<String, Object>) cache.get(key).getObjectValue();
				for(String mapKey : works.keySet()){
					WorkTaskMethodSetting setting = (WorkTaskMethodSetting)works.get(mapKey);
					logger.info("\r\n"+mapKey+" :::::::::: "+ObjectUtils.getDisplayString(setting.toString()));
				}
			}
			logger.info("WORKPLACE SETTING CACHE TENANT END ::::::::::::::::::::: "+key);
		}
	}
	
	/**
	 * workplace-setup cache를 가져온다.
	 * @return
	 */
	private Cache getCacheInstance() {
		Cache cache = cacheManager.getCache(CACHENAME);
		return cache;
	}
	
	/**
	 * workplace-setup cache를 넣는다.
	 * @param key
	 * @param value
	 */
	public void putCache(String key, Object value){
		Cache cache = getCacheInstance();
		Element ele = new Element(key, value);
		cache.put(ele);
	}

	/**
	 * workplace-setup cache를 지운다.
	 * @param key
	 */
	public void removeCache(String key){
		Cache cache = getCacheInstance();
		cache.remove(key);
	}
	
	/**
	 * workplace setting cache에 모두 넣는다.
	 * @param elements
	 */
	@SuppressWarnings("unchecked")
	private void putAllCache(Element ele){
		Cache cache = getCacheInstance();
		String tenId = (String)ele.getObjectKey();
		Element works = cache.get(tenId);
		if(works != null){
			Map<String, Object> setting = (Map<String, Object>)works.getObjectValue();
			setting.putAll((Map<String, Object>)ele.getObjectValue());
		}else{
			cache.put(ele);
		}
	}
	
	/**
	 * workplace setting를 cache에 모두 넣기위해서 Element로 변화시며 모두 넣는다.
	 * @param datas
	 */
	public void putAllCache(String tenId, List<WorkTaskMethodSetting> allSettings){
		Element ele = new Element(tenId, makeCacheObject(allSettings));
		putAllCache(ele);
	}
	
	public Map<String, Object> makeCacheObject(List<WorkTaskMethodSetting> settings){
		Map<String, Object> workSettingMap = Maps.newHashMap();
		for(WorkTaskMethodSetting setting : settings){
			String key = makeCacheKey(setting);
			workSettingMap.put(key, setting);
		}
		return workSettingMap;
	}
	
	/**
	 * workplace setting cache를 모두 지운다.
	 */
	public void removeAllCache(){
		Cache cache = getCacheInstance();
		cache.removeAll();
	}
	
	/**
	 * workplace setting cache에 넣기위해 key을 생성한다.
	 * @param settings
	 * @return
	 */
	private String makeCacheKey(WorkTaskMethodSetting settings){
		String key = WorkplaceCacheConst.PREFIX_CACHE_KEY.concat("_"+settings.getTechCls()).concat("_"+settings.getTaskClassNm()).concat("_"+settings.getTaskMethodNm());
		key = key.toUpperCase();
		logger.debug("###################### makeCacheKey ::: "+key);
		return key;
	}
}
