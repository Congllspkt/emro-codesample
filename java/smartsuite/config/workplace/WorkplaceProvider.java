package smartsuite.config.workplace;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import smartsuite.app.bp.workplace.setup.repository.WorkplaceSetupRepository;
import smartsuite.config.workplace.lib.WorkplaceCacheManager;
import smartsuite.config.workplace.service.WorkplaceSetupDaoService;
import smartsuite.security.authentication.Auth;

/**
 * The Class WorkplaceProvider.
 */
@Service
public class WorkplaceProvider {
	
	/** The workplace setup dao. */
	@Inject
	WorkplaceSetupRepository workplaceSetupRepository;

	@Inject
	WorkplaceSetupDaoService workplaceSetupDao;
	
	/** The workplace cache. */
	@Inject
	WorkplaceCacheManager workplaceCache;
	
	/**
	 * 모든 tenant의 workplace method setting 을 cache에 모두 load 시킨다.
	 */
	public void load(){
		workplaceCache.removeAllCache();
		List<Map<String, Object>> tenants = workplaceSetupRepository.findListAllTenant();
		for(Map<String,Object> tenant : tenants){
			String tenId = (String)tenant.get("ten_id");
			//주석해제필요
			workplaceCache.putAllCache(tenId, workplaceSetupDao.findListAllSetting(tenId));
		}
		workplaceCache.printCache();
	}

	/**
	 * 현재 tenant에 해당되는 workplace method setting 을 cache에 모두 load 시킨다.
	 *
	 * @param
	 */
	public void loadBySys(){
		String sysId = Auth.getCurrentTenantId();
		workplaceCache.removeCache(sysId);
		workplaceCache.putAllCache(sysId, workplaceSetupRepository.findListAllSetting(sysId));
		workplaceCache.printCache();
	}
	
}
