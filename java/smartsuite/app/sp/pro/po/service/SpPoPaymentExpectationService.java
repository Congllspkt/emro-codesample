package smartsuite.app.sp.pro.po.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.pro.po.repository.SpPoPaymentExpectationRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class SpPoPaymentExpectationService {

    @Inject
    SpPoPaymentExpectationRepository spPoPaymentExpectationRepository;

    public Object findListPaymentPlanByPoId(Map<String, Object> param) {
        return  spPoPaymentExpectationRepository.findListPaymentPlanByPoId(param);
    }
}
