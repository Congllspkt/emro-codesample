package smartsuite.app.bp.cms.attrpool.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.bp.cms.attrpool.repository.ItemAttributePoolRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

/**
 * 품목 속성 Pool 관련 service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemAttributePoolService {

	@Inject
	SharedService sharedService;

	@Inject
	ItemAttributePoolRepository itemAttributePoolRepository;

	/**
	 * 속성 Pool 조회
	 *
	 * @param
	 * @return the FloaterStream
	 */
	public FloaterStream findListAttributePool(Map<String, Object> param) {
		return itemAttributePoolRepository.findListAttributePool(param);
	}

	/**
	 * 속성 Pool 삭제(list)
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap deleteListAttributePool(Map<String, Object> param){
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.get("deleteList");

		for(Map<String, Object> row : deleteList){
			this.deleteInfoAttributePool(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 속성 Pool 삭제
	 *
	 * @param
	 * @return the int
	 */
	public int deleteInfoAttributePool(Map<String, Object> param){
		return itemAttributePoolRepository.deleteInfoAttributePool(param);
	}


	/**
	 * 속성 Pool 상세조회
	 *
	 * @param
	 * @return the map
	 */
	public Map<String, Object> findInfoAttributePool(Map<String, Object> param) {
		return itemAttributePoolRepository.findInfoAttributePool(param);
	}

	/**
	 * 속성 Pool 분류배정현황 조회
	 *
	 * @param
	 * @return the list
	 */
	public List<Map<String,Object>> findListAssignedAttributePool(Map<String, Object> param) {
		return itemAttributePoolRepository.findListAssignedAttributePool(param);
	}

	/**
	 * 속성 Pool 저장
	 *
	 * @param
	 * @return the map
	 */
	public ResultMap saveInfoAttributePool(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();

		Map<String, Object> info = (Map<String, Object>)param.get("attrInfo");
		boolean isNew = info.get("is_new") == null ? false : (Boolean)info.get("is_new");

		if(isNew) {
			info.put("iattr_cd", sharedService.generateDocumentNumber("ATTR"));
			this.insertInfoAttributePool(info);
		} else {
			// 상태 체크 (결재 요청중, 승인 인 경우 수정할 수 없다)
			resultMap = this.checkCntAttrPoolWithResult(info);
			if(resultMap.isFail()) {
				return ResultMap.FAIL(resultMap.getResultMessage());
			}

			this.updateInfoAttributePool(info);
		}

		resultMap.setResultData(info);
		return ResultMap.SUCCESS(resultMap.getResultData());
	}

	public ResultMap checkCntAttrPoolWithResult(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();

		if(this.checkCntAttrPool(param)){
			resultMap.setResultMessage("STD.E9400");
			return ResultMap.FAIL(resultMap.getResultMessage());
		}

		return ResultMap.SUCCESS();
	}

	public Boolean checkCntAttrPool(Map<String, Object> param){
		return itemAttributePoolRepository.checkCntAttrPool(param) > 0;
	}

	/**
	 * 속성 Pool insert
	 *
	 * @param
	 * @return the void
	 */
	public void insertInfoAttributePool(Map<String, Object> info) {
		itemAttributePoolRepository.insertInfoAttributePool(info);
	}

	/**
	 * 속성 Pool update
	 *
	 * @param
	 * @return the void
	 */
	public void updateInfoAttributePool(Map<String, Object> item){
		itemAttributePoolRepository.updateInfoAttributePool(item);
	}
}
