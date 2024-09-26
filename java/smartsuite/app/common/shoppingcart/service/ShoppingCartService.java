package smartsuite.app.common.shoppingcart.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shoppingcart.event.ShoppingCartEventPublisher;
import smartsuite.app.common.shoppingcart.repository.ShoppingCartRepository;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileGroup;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@SuppressWarnings({ "rawtypes" })
@Service
@Transactional
public class ShoppingCartService {
    @Inject
    ShoppingCartRepository shoppingCartRepository;

    @Inject
    ShoppingCartEventPublisher shoppingCartEventPublisher;

    @Inject
    StdFileService stdFileService;
    public Map countShoppingCart(Map param) {
        return shoppingCartRepository.countShoppingCart(param);
    }

    public Map findUprcInfoWithCatalog(Map param) {
        Map result = shoppingCartRepository.findUprcInfoWithCatalog(param);
        if(!Strings.isNullOrEmpty((String)result.get("thnl_athg_uuid"))){
            result.put("thumbnails", shoppingCartRepository.findListThumbnail((String)result.get("thnl_athg_uuid")));
        }
        return result;
    }

    public ResultMap saveUprcItemToShoppingCart(Map param) {
        ResultMap resultMap = ResultMap.getInstance();
        String shpcartUuid = param.get("shpcart_uuid") == null ? null : param.get("shpcart_uuid").toString();
        if(Strings.isNullOrEmpty(shpcartUuid)) {
            param.put("shpcart_uuid", UUID.randomUUID().toString());
            shoppingCartRepository.insertUprcItemToShoppingCart(param);
        }else{
            shoppingCartRepository.updateUprcItemToShoppingCart(param);
        }

        return resultMap.SUCCESS();
    }

    public ResultMap saveUprcItemToShoppingCartList(Map param) {
        ResultMap resultMap = ResultMap.getInstance();
        List<Map<String, Object>> uprcItemList = (List<Map<String, Object>>)param.get("uprc_item_list");

        for(Map uprcItem : uprcItemList){
            this.saveUprcItemToShoppingCart(uprcItem);
        }
        return resultMap.SUCCESS();
    }
    public Map<String, Object> findListSearchRequester(Map param) {
        HashMap result = Maps.newHashMap();

        String startOfPaging = createPagingStartQuery();
        String endOfPaging = createPagingEndQuery(param);
        String startOfCounting = startOfCounting();
        String endOfCounting = endOfCounting();
        String orderByQuery =  createOrderByQuery(param);


        if("Y".equals((String)param.get("newSearchYn"))){
            param.put("start_of_paging", startOfPaging);
            param.put("end_of_paging", endOfPaging + orderByQuery);

            result.put("ctlg_list", shoppingCartEventPublisher.findListUprcItemWithCatalog(param));
            result.put("uprc_item_list", shoppingCartEventPublisher.findListUprcItemWithoutCatalog(param));


            param.put("end_of_paging", endOfPaging);
            result.put("pr_item_list", shoppingCartEventPublisher.findListPrePrItemList(param));
            result.put("item_list", Lists.newArrayList());


            param.put("start_of_paging", startOfCounting);
            param.put("end_of_paging", endOfCounting);
            result.put("ctlg_list_total_count", shoppingCartEventPublisher.findListUprcItemWithCatalog(param));
            result.put("uprc_item_list_total_count", shoppingCartEventPublisher.findListUprcItemWithoutCatalog(param));
            result.put("pr_item_list_total_count", shoppingCartEventPublisher.findListPrePrItemList(param));
            result.put("item_list_total_count", Lists.newArrayList());
        }else{
            String index = (String)param.get("index");
            param.put("start_of_paging", startOfPaging);


            if("pr_item_list".equals(index)){
                param.put("end_of_paging", endOfPaging);
                result.put("pr_item_list", shoppingCartEventPublisher.findListPrePrItemList(param));

                param.put("start_of_paging", startOfCounting);
                param.put("end_of_paging", endOfCounting);
                result.put("pr_item_list_total_count", shoppingCartEventPublisher.findListPrePrItemList(param));
            }else if("ctlg_list".equals(index)){
                param.put("end_of_paging", endOfPaging + orderByQuery);
                result.put("ctlg_list", shoppingCartEventPublisher.findListUprcItemWithCatalog(param));

                param.put("start_of_paging", startOfCounting);
                param.put("end_of_paging", endOfCounting);
                result.put("ctlg_list_total_count", shoppingCartEventPublisher.findListUprcItemWithCatalog(param));

            }else if("uprc_item_list".equals(index)){
                param.put("end_of_paging", endOfPaging + orderByQuery);
                result.put("uprc_item_list", shoppingCartEventPublisher.findListUprcItemWithoutCatalog(param));

                param.put("start_of_paging", startOfCounting);
                param.put("end_of_paging", endOfCounting);
                result.put("uprc_item_list_total_count", shoppingCartEventPublisher.findListUprcItemWithoutCatalog(param));
            }else if("item_list".equals(index)){
                param.put("end_of_paging", endOfPaging);
                result.put("item_list", Lists.newArrayList());

                param.put("start_of_paging", startOfCounting);
                param.put("end_of_paging", endOfCounting);
                result.put("item_list_total_count", Lists.newArrayList());
            }
        }
        return result;

    }
    private String startOfCounting(){
        return "SELECT COUNT(1) AS CNT FROM ( ";
    }
    private String endOfCounting(){
        return " ) TMP_COUNT";
    }
    private String createPagingStartQuery() {
        return "SELECT * FROM ( SELECT ROW_.*, ROWNUM ROWNUM_ FROM (";
    }
    private String createPagingEndQuery(Map<String, Object> param) {
        return ") ROW_ ) WHERE ROWNUM_ <= " +  Integer.sum((Integer)param.get("from"), (Integer)param.get("size")) + " AND ROWNUM_ >= " + Integer.sum((Integer)param.get("from"),1);
    }
    private String createOrderByQuery(Map<String, Object> param) {
        if ("score".equals(param.get("sort"))) {
            return " ORDER BY uprc_efct_st_dt desc";
        } else {
            return " ORDER BY " + param.get("sort");
        }
    }
    public List<Map<String, Object>> findListUprcItemWithCatalog(Map<String, Object> param){
        param.put("start_of_paging", createPagingStartQuery());
        param.put("end_of_paging", createPagingEndQuery(param) + createOrderByQuery(param));
        return shoppingCartEventPublisher.findListUprcItemWithCatalog(param);
    }
    public List<Map<String, Object>> findListUprcItemWithoutCatalog(Map<String, Object> param){
        param.put("start_of_paging", createPagingStartQuery());
        param.put("end_of_paging", createPagingEndQuery(param) + createOrderByQuery(param));
        return shoppingCartEventPublisher.findListUprcItemWithoutCatalog(param);
    }
    public List<Map<String, Object>> findListPrePrItemList(Map<String, Object> param){
        param.put("start_of_paging", createPagingStartQuery());
        param.put("end_of_paging", createPagingEndQuery(param));
        return shoppingCartEventPublisher.findListPrePrItemList(param);
    }


    public Map<String, Object> findItemListSearchRequester(Map param) {
        HashMap result = Maps.newHashMap();
        param.put("start_of_paging", createPagingStartQuery());
        param.put("end_of_paging", createPagingEndQuery(param));

        result.put("item_list", shoppingCartEventPublisher.findListCateItemWithUprcCntr(param));

        param.put("start_of_paging", startOfCounting());
        param.put("end_of_paging", endOfCounting());

        result.put("item_list_total", shoppingCartEventPublisher.findListCateItemWithUprcCntr(param));

        return result;
    }

    public ResultMap directPrByShoppingCart(Map param) {
        ResultMap resultOfSavePr = shoppingCartEventPublisher.saveDraftPr(param);

        if(!resultOfSavePr.isSuccess()) {
            return resultOfSavePr;
        }

        ResultMap resultOfDirectReqPr = shoppingCartEventPublisher.directReqPr(param);
        return resultOfDirectReqPr;
    }

    public List<Map<String, Object>> findListShoppingCartList(Map param) throws RuntimeException {
        List<Map<String, Object>> result = shoppingCartRepository.findListShoppingCartList(param);
        for(Map data : result){
            //썸네일 이미지를 가져오기위해 fileGroup에서 첫번째 저장된 이미지 주소를 가져온다.
            if(data.get("thnl_athg_uuid") != null && !"".equals(data.get("thnl_athg_uuid"))){
                FileGroup fg = null;
                try {
                    fg = stdFileService.findFileGroup((String) data.get("thnl_athg_uuid"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                data.put("thnl_img_addr", fg.getFileList().getItems().get(0).getId());
            }
        }
        return result;
    }

    public ResultMap deleteShoppingCartList(Map param) {
        ResultMap resultMap = ResultMap.getInstance();
        List<Map<String, Object>> deleteList = (List<Map<String, Object>>) param.get("deleteList");
        for(Map delete : deleteList){
            shoppingCartRepository.deleteShoppingCartItem(delete);
        }

        resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
        return resultMap;
    }

    public ResultMap updateShoppingCartItemStatusD(Map param) {
        ResultMap resultMap = ResultMap.getInstance();
        List<Map<String, Object>> deleteList = (List<Map<String, Object>>) param.get("deleteList");
        for(Map delete : deleteList){
            shoppingCartRepository.updateShoppingCartItemStatusD(delete);
        }

        resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
        return resultMap;
    }
}
