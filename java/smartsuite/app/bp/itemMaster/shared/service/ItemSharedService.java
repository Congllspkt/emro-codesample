package smartsuite.app.bp.itemMaster.shared.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.itemMaster.shared.repository.ItemSharedRepository;
import smartsuite.app.common.shared.Const;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class ItemSharedService {
	
	@Inject
	ItemSharedRepository itemSharedRepository;
	
	public List<Map> findListCate(Map param) {
		return itemSharedRepository.findListCate(param);
	}
	
	public List<Map> findListCateItem(Map param) {
		return itemSharedRepository.findListCateItem(param);
	}
	
	public Map saveMyItemList(Map param){
		Map resultMap = new HashMap();
		if(param.containsKey("insertMyItems")){
			List<Map> insertMyItems = (List<Map>) param.get("insertMyItems");
			
			for(Map insertMyItem : insertMyItems){
				itemSharedRepository.insertMyItem(insertMyItem);
			}
		}
		if(param.containsKey("deleteMyItems")){
			List<Map> deleteMyItems = (List<Map>) param.get("deleteMyItems");
			
			if(!deleteMyItems.isEmpty()){
				for(Map deleteMyItem : deleteMyItems){
					itemSharedRepository.deleteMyItem(deleteMyItem);
				}
			}
		}
		
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	public List<Map> findListCateItemWithUprcCntr(Map param) {
		return itemSharedRepository.findListCateItemWithUprcCntr(param);
	}
}