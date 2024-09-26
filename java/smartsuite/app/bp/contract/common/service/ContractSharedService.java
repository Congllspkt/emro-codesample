package smartsuite.app.bp.contract.common.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.common.repository.ContractSharedRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class ContractSharedService {
    @Inject
    ContractSharedRepository contractSharedRepository;


    public Map findContractTypeCount(Map param) {
        return contractSharedRepository.findContractTypeCount(param);
    }

    public Map findNonStandardContractPercent() {
        Map<String,Object> result = Maps.newHashMap();
        Map<String, Object> param  = Maps.newHashMap();
        int totalCount = contractSharedRepository.findNonStandardContractCount(param);
        if(totalCount ==  0){
            result.put("nonStandardContractPercent",0);
            return result;
        }
        param.put("cntr_tmpl_typ_ccd","USR_FILE");
       int count = contractSharedRepository.findNonStandardContractCount(param);
       if(count ==  0){
            result.put("nonStandardContractPercent",0);
            return result;
        }
       double percent = ((double) count / totalCount) * 100;
       int intValue = (int) Math.round(percent);
       result.put("nonStandardContractPercent",intValue);
        return result;
    }

    public Map<String, Object> findContractExpirationNotification() {
        return contractSharedRepository.findContractExpirationNotification();
    }
}
