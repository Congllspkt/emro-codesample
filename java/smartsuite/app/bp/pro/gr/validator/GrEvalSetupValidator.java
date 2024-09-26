package smartsuite.app.bp.pro.gr.validator;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.pro.gr.repository.GrEvalSetupRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class GrEvalSetupValidator {
	
	@Inject
	GrEvalSetupRepository grEvalSetupRepository;
	
	public ResultMap insertGemt(List<Map> insertList) {
		if(insertList == null) {
			return ResultMap.SKIP();
		}
		if(insertList.isEmpty()) {
			return ResultMap.SKIP();
		}
		
		for(Map insert : insertList) {
			if(grEvalSetupRepository.isExistsGemtByOorgAndCode(insert)) {
				return ResultMap.builder()
				                .resultStatus(ResultMap.STATUS.USED)
				                .build();
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap deleteGemt(List<Map> deleteList) {
		if(deleteList == null) {
			return ResultMap.NOT_EXISTS();
		}
		if(deleteList.isEmpty()) {
			return ResultMap.NOT_EXISTS();
		}
		
		Map param = Maps.newHashMap();
		param.put("deleteList", deleteList);
		
		if(grEvalSetupRepository.isExistGemtAtGeg(param)) {
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.USED)
			                .build();
		}
		
		if(grEvalSetupRepository.isExistGemtAtGemg(param)) {
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.USED)
			                .build();
		}
		
		return ResultMap.SUCCESS();
	}
}
