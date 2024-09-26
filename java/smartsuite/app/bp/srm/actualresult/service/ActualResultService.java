package smartsuite.app.bp.srm.actualresult.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.actualresult.respository.ActualResultRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ActualResultService {

	@Inject
	private ActualResultRepository actualResultRepository;

	/**
	 * 실적관리 목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	public FloaterStream findListVdAcres(Map param) {
		return actualResultRepository.findListVdAcres(param);
	}

	/**
	 * 협력사 소싱그룹 목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	public FloaterStream findListSgVendorInfo(Map param) {
		return actualResultRepository.findListSgVendorInfo(param);
	}

	/**
	 * 실적관리 목록 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 1
	 */
	public Map saveVdAcresList(Map param) {
		Map<String, Object> resultMap = new HashMap<>();
		List<Map<String, Object>> inserts = (List<Map<String, Object>>)param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>)param.get("updateList");
		//List<Map<String, Object>> validList = new ArrayList<Map<String, Object>>();

		// 신규
		for(Map<String, Object> row : inserts){
			String vdQuantDatRegUuid = (String) UUID.randomUUID().toString();
			row.put("vd_quant_dat_reg_uuid", vdQuantDatRegUuid);
			actualResultRepository.insertVdAcresList(row);
		}

		// 수정
		for(Map<String, Object> row : updates){
			actualResultRepository.updateVdAcresList(row);
		}

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;

	}

	/**
	 * 실적관리 목록 삭제를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 2
	 */
	public Map deleteVdAcresList(Map param) {
		Map<String, Object> resultMap = new HashMap<>();

		List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deletedList");

		for(Map<String, Object> row : deletes) {
			actualResultRepository.deleteVdAcresList(row);
		}

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	/**
	 * 실적관리 excel upload시 소싱그룹명, 협력사명을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 2
	 */
	public Map findSgVdNm(Map param) {
		return actualResultRepository.findSgVdNm(param);
	}
}
