package smartsuite.app.bp.pro.unitprice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.bp.pro.unitprice.service.UnitPriceService;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Controller
@RequestMapping (value = "**/pro/unitprice/")
public class UnitPriceController {

	@Inject
	UnitPriceService unitPriceService;
	
	@RequestMapping(value = "findListUnitPriceHistrec.do")
	public @ResponseBody Map findListUnitPriceHistrec(@RequestBody Map param) {
		return unitPriceService.findListUnitPriceHistrec(param);
	}
}