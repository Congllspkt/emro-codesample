package smartsuite.app.bp.itemMaster.shared;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.itemMaster.shared.service.ItemSharedService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/itemMaster/shared/**/")
public class ItemSharedController {
	
	@Inject
	ItemSharedService itemSharedService;
	
	@RequestMapping(value="findListCate.do")
	public @ResponseBody List findListCate(@RequestBody Map param){
		return itemSharedService.findListCate(param);
	}
	
	@RequestMapping(value="findListCateItem.do")
	public @ResponseBody List findListCateItem(@RequestBody Map param){
		return itemSharedService.findListCateItem(param);
	}
	
	@RequestMapping(value="saveMyItemList.do")
	public @ResponseBody Map saveMyItemList(@RequestBody Map param){
		return itemSharedService.saveMyItemList(param);
	}
}
