package smartsuite.app.common.workplace.shared.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.workplace.shared.event.WorkplaceEventPublisher;
import smartsuite.app.common.workplace.shared.repository.WorkplaceSharedRepository;
import smartsuite.app.event.CustomSpringEvent;
import smartsuite.security.authentication.Auth;

@SuppressWarnings({"rawtypes"})
@Service
@Transactional
public class WorkplaceSharedService {
	
	@Inject
	WorkplaceSharedRepository workplaceSharedRepository;


	@Inject
	WorkplaceEventPublisher workplaceEventPublisher;

	/**
	 * Main Work Status > Main Work 별 지연/임박/일반/총계 count (cc-task-bar-chart, cc-main-work-status에서 사용)
	 * 
	 * @param param {}
	 * @return
	 */
	public List findListMainTaskCount(Map<String, Object> param) {
		return workplaceSharedRepository.findListMainTaskCount(param);
	}

	/**
	 * Sub Work Status > Main Work 리스트 (cc-work-line-chart에서 상단 탭 생성 시 사용)
	 * 
	 * @param param {}
	 * @return
	 */
	public List findListMainTask(Map<String, Object> param) {
		return workplaceSharedRepository.findListMainTask(param);
	}
	
	/**
	 * Sub Work Status > Sub Work 별 지연/임박/총계 count (cc-work-line-chart에서 사용)
	 * 
	 * @param param {up_work_id}
	 * @return
	 */
	public List findListTaskStatusCount(Map<String, Object> param) {
		return workplaceSharedRepository.findListTaskStatusCount(param);
	}
	
	/**
	 * Sub Work Status > Sub Work 별 알람/메모/통보/제외/지연/임박/일반/신규 여부 조회 (cc-sub-work-status에서 사용)
	 * 
	 * @param param {}
	 * @return
	 */
	public List findListTaskStatus(Map<String, Object> param) {
		return workplaceSharedRepository.findListTaskStatus(param);
	}

	public Map findReturnedPoCount(Map<String, Object> param) {
		return workplaceEventPublisher.findReturnedPoCount(param);
	}

	public Map findPoTypeCount(Map<String, Object> param) {
		return workplaceEventPublisher.findPoTypeCount(param);
	}

	public List findListWorkplaceDashboardPoData(Map<String, Object> param) {
		return workplaceEventPublisher.findListWorkplaceDashboardPoData(param);
	}

	public Map findUnitPriceAdequacyCount(Map<String, Object> param) {
		List<Map<String,Object>> resultList = workplaceEventPublisher.findUnitPriceAdequacyCount(param);

		Map<String,Object> result = Maps.newHashMap();
		if(resultList.size() > 0) {
			for(Map<String,Object> map : resultList) {
				String color = map.get("color") == null ? "" : (String) map.get("color");
				BigDecimal count = map.get("cnt") == null ? BigDecimal.ZERO : (BigDecimal) map.get("cnt");
				result.put(color, count);
            }
        }
        return result;
	}

	public Map findItemDoctorData(Map<String, Object> param) {

		BigDecimal duplicateItemCount = workplaceSharedRepository.findItemSimilarityByItemDoctor(param);
		BigDecimal recommendPoItemCount = workplaceSharedRepository.findAutoPoItemRecommendByPriceDoctor(param);

		duplicateItemCount = duplicateItemCount == null ? BigDecimal.ZERO : duplicateItemCount;
		recommendPoItemCount = recommendPoItemCount == null ? BigDecimal.ZERO : recommendPoItemCount;

		Map<String,Object> result = Maps.newHashMap();
		result.put("duplicateItemCount", duplicateItemCount);
		result.put("recommendPoItemCount", recommendPoItemCount);
		return result;
	}

	public List findListWorkplaceDashboardUnitPrice(Map<String, Object> param) {
		return workplaceEventPublisher.findListWorkplaceDashboardUnitPrice(param);
	}

	public Map findContractTypeStatusByDashboard(Map<String, Object> param) {
		param.put("cntr_pic_id", Auth.getCurrentUser().getUsername());
		return workplaceEventPublisher.findContractTypeCount(param);
	}

	public Map findNonStandardContractPercent(Map<String, Object> param) {
		return workplaceEventPublisher.findNonStandardContractPercent(param);
	}

	public Map findContractExpirationNotification(Map<String, Object> param) {
		return workplaceEventPublisher.findContractExpirationNotification(param);
	}
}
