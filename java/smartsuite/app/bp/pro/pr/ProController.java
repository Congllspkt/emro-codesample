package smartsuite.app.bp.pro.pr;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.pr.service.PrItemService;
import smartsuite.app.bp.pro.pr.service.PrService;
import smartsuite.app.bp.pro.upcr.service.UpcrService;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/pro/**/")
public class ProController {

    @Inject
    PrService prService;

	@Inject
	PrItemService prItemService;

	@Inject
    UpcrService upcrService;

	/**
	 * purc grp cd 수정을 요청한다.
	 *
	 * @author : Yeon-u Kim
	 * @param param the param
	 * @return the map
	 * @Date : 2016. 5. 9
	 * @Method Name : updatePurcGrpCd
	 */
	@RequestMapping(value = "updatePurcGrpCd.do")
	public @ResponseBody ResultMap updatePurcGrpCd(@RequestBody Map param){
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			return prService.updatePurcGrpCd(param);
		}else if(param.containsKey("upcr_item_uuids")&& !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			return upcrService.updatePurcGrpCd(param);
		}else{
			return null;
		}
	}

	/**
	 * 요청 접수
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "receivePrsAndUpcrs.do")
	public @ResponseBody ResultMap receivePrsAndUpcrs(@RequestBody Map param){
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			return prService.receivePrs(param);
		}else if(param.containsKey("upcr_item_uuids")&& !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			return upcrService.receiveUpcrs(param);
		}else{
			return null;
		}
	}

	/**
	 * 요청 반송
	 *
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "returnPrsAndUpcrs.do")
	public @ResponseBody ResultMap returnPrsAndUpcrs(@RequestBody Map param){
		ResultMap resultMap = ResultMap.getInstance();
		if(param.containsKey("pr_item_uuids") && !ObjectUtils.isEmpty(param.get("pr_item_uuids"))){
			resultMap = prService.returnPrs(param);

		}
		if(!resultMap.isSuccess()){
			return resultMap;
		}
		if(param.containsKey("upcr_item_uuids") && !ObjectUtils.isEmpty(param.get("upcr_item_uuids"))){
			resultMap =  upcrService.returnUpcrs(param);
		}
		return resultMap;
	}
}
