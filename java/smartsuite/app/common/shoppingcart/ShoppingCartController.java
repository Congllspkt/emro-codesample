package smartsuite.app.common.shoppingcart;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shoppingcart.service.ShoppingCartService;
import smartsuite.security.annotation.AuthCheck;
import smartsuite.security.annotation.FixedMenuCodeConst;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Controller
public class ShoppingCartController {
    @Inject
    ShoppingCartService shoppingCartService;
    @AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/countShoppingCart.do")
    public @ResponseBody Map countShoppingCart(@RequestBody Map param) {
        return shoppingCartService.countShoppingCart(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findListSearchRequester.do")
    public @ResponseBody Map<String, Object> findListSearchRequester (@RequestBody Map param) {
        return shoppingCartService.findListSearchRequester(param);
    }
	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findItemListSearchRequester.do")
    public @ResponseBody Map<String, Object> findItemListSearchRequester (@RequestBody Map param) {
        return shoppingCartService.findItemListSearchRequester(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findUprcInfoWithCatalog.do")
    public @ResponseBody Map findUprcInfoWithCatalog(@RequestBody Map param) {
        return shoppingCartService.findUprcInfoWithCatalog(param);
    }
	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/saveUprcItemToShoppingCart.do")
    public @ResponseBody ResultMap saveUprcItemToShoppingCart(@RequestBody Map param) {
        return shoppingCartService.saveUprcItemToShoppingCart(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/saveUprcItemToShoppingCartList.do")
    public @ResponseBody ResultMap saveUprcItemToShoppingCartList(@RequestBody Map param) {
        return shoppingCartService.saveUprcItemToShoppingCartList(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findListUprcItemWithoutCatalog.do")
    public @ResponseBody List<Map<String, Object>> findListUprcItemWithoutCatalog (@RequestBody Map param) {
        return shoppingCartService.findListUprcItemWithoutCatalog(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findListUprcItemWithCatalog.do")
    public @ResponseBody List<Map<String, Object>> findListUprcItemWithCatalog (@RequestBody Map param) {
        return shoppingCartService.findListUprcItemWithCatalog(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping (value = "**/directPrByShoppingCart.do")
    public @ResponseBody ResultMap directPrByShoppingCart(@RequestBody Map param) {
        return shoppingCartService.directPrByShoppingCart(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping(value = "**/findListPrePrItemList.do")
    public @ResponseBody List<Map<String, Object>> findListPrePrItemList (@RequestBody Map param) {
        return shoppingCartService.findListPrePrItemList(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping (value = "**/findListShoppingCart.do")
    public @ResponseBody List<Map<String, Object>> findListShoppingCart(@RequestBody Map param) {
        return shoppingCartService.findListShoppingCartList(param);
    }

	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping (value = "**/deleteShoppingCartList.do")
    public @ResponseBody ResultMap deleteShoppingCartList(@RequestBody Map param) {
        return shoppingCartService.deleteShoppingCartList(param);
    }
	@AuthCheck(fixedMenuCode = FixedMenuCodeConst.SHOPPINGCART)
    @RequestMapping (value = "**/updateShoppingCartItemStatusD.do")
    public @ResponseBody ResultMap updateShoppingCartItemStatusD(@RequestBody Map param) {
        return shoppingCartService.updateShoppingCartItemStatusD(param);
    }

}
