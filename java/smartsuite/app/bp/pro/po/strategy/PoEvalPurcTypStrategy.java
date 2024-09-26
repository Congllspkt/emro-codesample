package smartsuite.app.bp.pro.po.strategy;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.strategy.PoGrEvalStrategy;
import smartsuite.app.bp.pro.po.repository.PoEvalRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PoEvalPurcTypStrategy implements PoGrEvalStrategy {
	
	@Inject
	PoEvalRepository poEvalRepository;
	
	@Override
	public List<String> findListGemgValues(String uuid) {
		Map param = Maps.newHashMap();
		param.put("po_uuid", uuid);
		
		return poEvalRepository.findPoPurcTypByPoUuid(param);
	}
}
