package smartsuite.app.bp.pro.gr.strategy;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.pro.gr.repository.GrEvalRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class GrEvalSgStrategy implements PoGrEvalStrategy {
	
	@Inject
	GrEvalRepository grEvalRepository;
	
	@Override
	public List<String> findListGemgValues(String grUuid) {
		Map param = Maps.newHashMap();
		param.put("gr_uuid", grUuid);
		
		return grEvalRepository.findGrSgByGrUuid(param);
	}
}
