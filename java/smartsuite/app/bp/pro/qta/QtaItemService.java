package smartsuite.app.bp.pro.qta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.qta.repository.QtaItemRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QtaItemService {

    @Inject
    private QtaItemRepository qtaItemRepository;

    public List<Map<String, Object>> findListQtaItem(Map<String, Object> param) {
        return qtaItemRepository.findListQtaItem(param);
    }

    public void insertQtaItem(Map<String, Object> row) {
        qtaItemRepository.insertQtaItem(row);
    }

    public void deleteQtaItems(Map<String, Object> param) {
        qtaItemRepository.deleteQtaItems(param);
    }

    public void deleteQtaItem(Map<String, Object> row) {
        qtaItemRepository.deleteQtaItem(row);
    }

    public void updateQtaItem(Map<String, Object> row) {
        qtaItemRepository.updateQtaItem(row);
    }

    public List<Map<String,Object>> findListQtaItemByItemCd(Map<String,Object> param){
        return qtaItemRepository.findListQtaItemByItemCd(param);
    }

    public ResultMap checkValidateCreatableQtaItem(Map<String, Object> param) {
        List<Map<String, Object>> qtaItems = this.findListQtaItemByItemCd(param);
        if(qtaItems != null){
            BigDecimal totQtarate = (BigDecimal) qtaItems.get(0).get("tot_qtarate");
            BigDecimal per = BigDecimal.valueOf(100.0);
            if(totQtarate.compareTo(per) < 0){
                // 100 > totQtarate
                //100 이아님
                return ResultMap.SUCCESS();
            }
        }
        return ResultMap.INVALID();
    }

    public List<Map<String,Object>> findListQtaItemByQta(Map<String, Object> param) {
        return qtaItemRepository.findListQtaItemByQta(param);
    }
}
