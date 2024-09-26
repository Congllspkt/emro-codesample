package smartsuite.app.bp.pro.taxbill.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.taxbill.repository.TaxBillCommonRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class TaxBillCommonService {
	@Inject
	private TaxBillCommonRepository taxBillCommonRepository;
	
	/**
	 * 협력사 담당자 조회
	 *
	 * @param vdChrParam
	 * @return
	 */
	public Map<String, Object> findSupplierPersonInCharge(Map<String, Object> vdChrParam) {
		return taxBillCommonRepository.findSupplierPersonInCharge(vdChrParam);
	}
	
	/**
	 * 조직 조회
	 *
	 * @param compParam
	 * @return
	 */
	public Map<String, Object> findCompanyByOorgCd(Map<String, Object> compParam) {
		return taxBillCommonRepository.findCompanyByOorgCd(compParam);
	}
	
}
