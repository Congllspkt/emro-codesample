package smartsuite.app.sp.rfx.sitebriefing.service;

import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.sitebriefing.repository.SpSiteBriefingRepository;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpSiteBriefingService {
	
	@Inject
	SpSiteBriefingRepository spSiteBriefingRepository;
	
	// 조회
	public FloaterStream findListSpFieldIntro(Map param) {
		return spSiteBriefingRepository.findListSpFieldIntro(param);
	}
	
	// 상세조회
	public Map findInfoSpFieldIntro(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("fiData", spSiteBriefingRepository.findInfoSpFieldIntro(param));
		resultMap.put("fiItems", spSiteBriefingRepository.findListSpFiItem(param));
		return resultMap;
	}
	
	// 참석 예정 여부 제출
	public ResultMap submitSpFieldIntro(Map param) {
		spSiteBriefingRepository.submitSpFieldIntro(param);
		return ResultMap.SUCCESS(param);
	}
	
}
