package smartsuite.app.bp.pro.catalog.validator;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.pro.catalog.repository.CatalogRepository;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings("unchecked")
@Service
public class CatalogValidator extends ProValidator {
	
	@Inject
	private CatalogRepository invoiceRepository;
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();

		return resultMap;
	}
}
