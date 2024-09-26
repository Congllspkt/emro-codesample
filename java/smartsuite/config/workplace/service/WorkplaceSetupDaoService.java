package smartsuite.config.workplace.service;

import org.springframework.stereotype.Service;
import smartsuite.config.workplace.repository.WorkplaceSetupDaoRepository;
import smartsuite.config.workplace.vo.WorkTaskMethodSetting;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * The Interface WorkplaceSetupDAO.
 */
@Service
public class WorkplaceSetupDaoService {
	@Inject
	WorkplaceSetupDaoRepository workplaceSetupDaoRepository;

	/**
	 * list all tenant 조회한다.
	 *
	 * @return the list< map< string, object>>
	 * @Date : 2019. 4. 22
	 * @Method Name : findListAllTenant
	 */
	public List<Map<String, Object>> findListAllTenant(){
		return workplaceSetupDaoRepository.findListAllTenant();
	}

	/**
	 * Select list all settings (work method - 선행/후행 여부 조회)
	 *
	 * @param sysId
	 * @return the list
	 */
	public List<WorkTaskMethodSetting> findListAllSetting(String sysId) {
		return workplaceSetupDaoRepository.findListAllSetting(sysId);
	}
}
