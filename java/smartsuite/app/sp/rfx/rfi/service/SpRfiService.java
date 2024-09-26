package smartsuite.app.sp.rfx.rfi.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfi.repository.SpRfiRepository;
import smartsuite.app.sp.rfx.rfi.validator.SpRfiValidator;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpRfiService {
	
	@Inject
	SpRfiRepository spRfiRepository;
	
	@Inject
	SpRfiValidator spRfiValidator;
	
	/**
	 * RFI 현황 조회
	 * @param param
	 * @return
	 */
	public FloaterStream findListRfi(Map param) {
		return spRfiRepository.findListRfi(param);
	}
	
	/**
	 * RFI 상세 조회
	 * @param param
	 * @return
	 */
	public Map findRfi(Map param) {
		Map rfiData = spRfiRepository.findRfi(param);
		if(rfiData == null) {
			return null;
		}
		
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfiData", rfiData);
		resultMap.put("rfiItems", spRfiRepository.findListRfiItem(rfiData));
		return resultMap;
	}
	
	/**
	 * RFI 견적 저장
	 * @param param
	 * @return
	 */
	public ResultMap temporarySaveRfiBid(Map param) {
		Map rfiBidData = (Map)param.get("rfiQtaData");
		
		// validate
		ResultMap validator = spRfiValidator.validate(rfiBidData);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		rfiBidData.put("rfi_subm_sts_ccd", "CRNG");
		spRfiRepository.updateRfiVendor(rfiBidData);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * RFI 견적 제출
	 * @param param
	 * @return
	 */
	public ResultMap submitRfiBid(Map param) {
		Map rfiBidData = (Map)param.get("rfiQtaData");
		
		// validate
		ResultMap validator = spRfiValidator.validate(rfiBidData);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		rfiBidData.put("rfi_subm_sts_ccd", "SUBM_CMPLD");
		spRfiRepository.updateRfiVendor(rfiBidData);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * RFI 거부
	 * @param param
	 * @return
	 */
	public ResultMap abandonRfiBid(Map param) {
		Map rfiBidData = (Map)param.get("rfiQtaData");
		
		// validate
		ResultMap validator = spRfiValidator.validate(rfiBidData);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		Map updateParam = new HashMap();
		updateParam.put("rfi_vd_uuid"        , rfiBidData.get("rfi_vd_uuid"));
		updateParam.put("rfi_subm_sts_ccd"     , "RJCT");
		updateParam.put("rfi_subm_rjct_rsn", param.get("abandonCause"));
		spRfiRepository.updateRfiVendor(updateParam);
		return ResultMap.SUCCESS();
	}
}