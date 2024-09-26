package smartsuite.app.bp.itemMaster.itemcat.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.itemcat.event.ItemCatEventPublisher;
import smartsuite.app.bp.itemMaster.itemcat.repository.ItemCatRepository;

import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 품목분류 마스터 관련 Service
 */
@Service
@Transactional
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemCatService {

	@Inject
	ItemCatRepository itemCatRepository;

	@Inject
	ItemCatEventPublisher itemCatEventPublisher;

	/**
	 * 품목분류 마스터 조회
	 *
	 * @param
	 * @return the list
	 */
	public FloaterStream findListItemCat(Map<String, Object> param) {
		return itemCatRepository.findListItemCat(param);
	}

	/**
	 * 품목분류 마스터 조회
	 * 조회조건이 있는 경우
	 *
	 * @param
	 * @return the list
	 */
	public FloaterStream findListItemCatWithCdOrNm(Map<String, Object> param) {
		return itemCatRepository.findListItemCatWithCdOrNm(param);
	}

	/**
	 * 품목분류 삭제
	 *
	 * @param
	 * @return the ResultMap
	 */
	public ResultMap deleteListItemCat(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();

		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.getOrDefault("deleteList", Lists.newArrayList());

		for(Map row : deleteList){
			if(!this.checkInfoUseItemCat(row)){
				//배정속성 삭제 로직 event 추가
				itemCatEventPublisher.deleteInfoItemcatIattrFromItemcat(row);
				// 유사 품목분류 삭제
				this.deleteSimItemCat(row);
				// 품목분류 이력 삭제
				this.deleteItemCatHistRec(row);
				// 품목분류 삭제
				this.deleteItemCat(row);
			}else{
				resultMap.setResultMessage("STD.CMS1006"); // 해당 품목분류는 연결된 품목이 존재하여 삭제할 수 없습니다.
				return ResultMap.FAIL(resultMap.getResultMessage());
			}
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 품목분류 상세조회
	 *
	 * @param
	 * @return the map
	 */
	public Map findInfoItemCat(Map param) {
		Map resultMap = Maps.newHashMap();

		// 분류정보
		resultMap.put("itemCatInfo", this.findInfoItemCatClass(param) );
		// 유사분류목록
		resultMap.put("similarList", this.findListItemCatSimilar(param) );

		return resultMap;
	}

	/**
	 * 품목분류 분류해설
	 *
	 * @param
	 * @return the map
	 */
	public Map findInfoItemCatClass(Map param) {
		return itemCatRepository.findInfoItemCatClass(param);
	}

	/**
	 * 품목분류 유사분류목록
	 *
	 * @param
	 * @return the list
	 */
	public List findListItemCatSimilar(Map param) {
		return itemCatRepository.findListItemCatSimilar(param);
	}

	/**
	 * 품목분류 이력정보
	 *
	 * @param
	 * @return the list
	 */
	public List findListItemCatHistory(Map param) {
		return itemCatRepository.findListItemCatHistory(param);
	}

	/**
	 * 품목분류 저장
	 *
	 * @param
	 * @return the resultMap
	 */
	public ResultMap saveInfoItemCat(Map<String, Object> param){
		ResultMap resultMap = ResultMap.getInstance();

		Map<String, Object> itemCatInfo = (Map<String, Object>)param.getOrDefault("itemCatInfo", Maps.newHashMap());
		boolean isNew = (Boolean) itemCatInfo.getOrDefault("is_new", false);

		if(isNew) {
			//품목분류 코드가 존재하는지 조회
			if(!this.checkInfoItemCatClass(itemCatInfo)) {
				this.insertItemCat(itemCatInfo);
			} else {
				resultMap.setResultMessage("STD.CMS1007"); // 이미 존재하는 분류코드 입니다.
				return ResultMap.DUPLICATED(resultMap.getResultMessage());
			}
		} else {
			this.updateItemCat(itemCatInfo);
		}

		//유사 품목분류 저장
		List<Map<String, Object>> similarList = (List<Map<String, Object>>)param.getOrDefault("similarList", Lists.newArrayList());
		for(Map<String, Object> row : similarList){
			row.put("itemcat_cd", itemCatInfo.get("itemcat_cd") );
			this.insertSimItemCat(row);
		}

		//이력 저장
		List<Map<String, Object>> historyList = (List<Map<String, Object>>)param.getOrDefault("historyList", Lists.newArrayList());
		this.insertListItemCatHistRec(historyList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 유사분류목록 삭제
	 *
	 * @param
	 * @return the ResultMap
	 */
	public ResultMap deleteListSimilar(Map<String, Object> param){
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.getOrDefault("deleteList", Lists.newArrayList());
		for(Map<String, Object> row : deleteList){
			this.deleteSimItemCat(row);
		}

		List<Map<String, Object>> historyList = (List<Map<String, Object>>)param.getOrDefault("historyList", Lists.newArrayList());
		this.insertListItemCatHistRec(historyList);

		return ResultMap.SUCCESS();
	}

	/**
	 * 사용중인 품목코드가 있는 지 확인
	 *
	 * @param
	 * @return Boolean
	 */
	public Boolean checkInfoUseItemCat(Map param) {
		return itemCatRepository.checkCntUseItemCat(param) > 0;
	}

	/**
	 * 동일한 분류코드가 있는 지 확인
	 *
	 * @param
	 * @return Boolean
	 */
	public Boolean checkInfoItemCatClass(Map param) {
		return itemCatRepository.checkCntItemCatClass(param) > 0;
	}

	/**
	 * 품목분류 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertItemCat(Map param) {
		itemCatRepository.insertItemCat(param);
	}

	/**
	 * 품목분류 update
	 *
	 * @param
	 * @return void
	 */
	public void updateItemCat(Map param) {
		itemCatRepository.updateItemCat(param);
	}

	/**
	 * 품목분류 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteItemCat(Map param) {
		itemCatRepository.deleteItemCat(param);
	}

	/**
	 * 유사품목분류 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertSimItemCat(Map param) {
		itemCatRepository.insertSimItemCat(param);
	}

	/**
	 * 유사품목분류 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteSimItemCat(Map param) {
		itemCatRepository.deleteSimItemCat(param);
	}

	/**
	 * 품목 분류 이력 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertListItemCatHistRec(List<Map<String, Object>> param) {
		for(Map<String, Object> row : param) {
			this.insertItemCatHistRec(row);
		}
	}
	/**
	 * 품목분류이력 insert
	 *
	 * @param
	 * @return void
	 */
	public void insertItemCatHistRec(Map param) {
		itemCatRepository.insertItemCatHistRec(param);
	}

	/**
	 * 품목분류이력 delete
	 *
	 * @param
	 * @return void
	 */
	public void deleteItemCatHistRec(Map param) {
		itemCatRepository.deleteItemCatHistRec(param);
	}
}
