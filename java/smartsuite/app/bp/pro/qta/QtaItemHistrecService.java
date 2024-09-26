package smartsuite.app.bp.pro.qta;

import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.qta.repository.QtaItemHistrecRepository;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class QtaItemHistrecService {

    @Inject
    private QtaItemHistrecRepository qtaItemHistrecRepository;

    public List<Map<String, Object>> findListQtaHistrecByApplDt(Map<String,Object> param){
        return qtaItemHistrecRepository.findListQtaHistrecByApplDt(param);
    }

    /*private static List<Map<String, Object>> getMinQtaHistrecList(List<Map<String, Object>> originalList) {
        List<Map<String, Object>> resultList = Lists.newArrayList();

        if (originalList.isEmpty()) {
            return resultList; // Return an empty list if the original list is empty
        }

        // Find the minimum value using Collections.min
        Object minValue = Collections.min(originalList, (map1, map2) ->
                ((Comparable<Object>) map1.get("appl_exp_dt")).compareTo(map2.get("appl_exp_dt"))
        ).get("appl_exp_dt");

        // Filter the list to include only maps with the minimum value
        for (Map<String, Object> map : originalList) {
            if (map.get("appl_exp_dt").equals(minValue)) {
                resultList.add(map);
            }
        }

        return resultList;
    }*/

    public void deleteQtaItemHistrec(Map<String, Object> row) {
        qtaItemHistrecRepository.deleteQtaItemHistrec(row);
    }

    public void updateQtaItemHistrec(Map<String, Object> row) {
        qtaItemHistrecRepository.updateQtaItemHistrec(row);
    }

    public void insertQtaItemHistrec(Map<String, Object> qtaItem) {
        qtaItemHistrecRepository.insertQtaItemHistrec(qtaItem);
    }
}
