package smartsuite.app.bp.contract.demo.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.demo.repository.ContractDemoRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;


/**
 * 계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName ContractService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ContractDemoService {

	@Inject
	ContractDemoRepository contractDemoRepository;

	/**
	 * 계약 목록 조회
	 * @param param
	 * @return
	 */
	public FloaterStream largeFindListContractDemo(Map param) {
		return contractDemoRepository.largeFindListContractDemo(param);
	}

	public ResultMap saveInfoCntrIfSts(Map param) {
		contractDemoRepository.saveInfoCntrIfSts(param);
		return ResultMap.SUCCESS();
	}

	public ResultMap findInfoIfCntrDemo(Map param) {
		Map resultMap = Maps.newHashMap();
		Map result = contractDemoRepository.findInfoIfCntrDemo(param);
		resultMap.put("cntrInfo", result);

		Map sendInfo = Maps.newHashMap();
		Map cntrData = contractDemoRepository.findInfoIfCntrHeaderDemo(param);
		sendInfo.put("cntrData", removeEmptyValues(cntrData));
		resultMap.put("sendInfo", sendInfo);

		Map rcptInfo = Maps.newHashMap();
		rcptInfo.put("RESPONSE_CODE", "FAIL");
		rcptInfo.put("RESPONSE_DATA", null);
		rcptInfo.put("RESPONSE_MESSAGE", cntrData.get("if_msg"));
		resultMap.put("rcptInfo", rcptInfo);

		return ResultMap.SUCCESS(resultMap);
	}

	private Map<String, Object> removeEmptyValues(Map<String, Object> param) {
		Map<String, Object> returnMap = Maps.newHashMap(param);
		Iterator<Map.Entry<String, Object>> iterator = returnMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			Object value = entry.getValue();
			if (value == null || (value instanceof String && ((String) value).isEmpty()) ||
					(value instanceof List && ((List<?>) value).isEmpty())) {
				iterator.remove();
			}
		}

		return returnMap;
	}

}