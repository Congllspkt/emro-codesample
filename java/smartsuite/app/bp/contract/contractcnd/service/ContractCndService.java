package smartsuite.app.bp.contract.contractcnd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;

import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public interface ContractCndService {
	
	public Map find(Map param);
	
	public String save(Map param);
	
	public ResultMap delete(String cntrCndUuid);
	
	public Map getTemplate(String cntrCndUuid);
	
	public String copy(String cntrCndUuid);

	Map copyData(String cntrCndUuid);
}
