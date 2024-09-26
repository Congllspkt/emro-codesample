package smartsuite.app.sp.rfx.rfxpreinsp.validator;

import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorConst;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.rfxpreinsp.repository.SpRfxPreInspRepository;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class SpRfxPreInspValidator {
	
	@Inject
	SpRfxPreInspRepository spRfxPreInspRepository;

	public ResultMap validate(Map param) {
		// 화면에서 조회한 시점의 업체 제출 시간과 현재 DB에서의 업체 제출 시간을 비교
		boolean isValid = spRfxPreInspRepository.validRfxPreInspNoClose(param);
		if(!isValid) {
			return ResultMap.builder()
							.resultStatus(RfxConst.NO_REG_RFX_PRE_INSP)
							.build();
		}
		
		// rfx상태확인 : P 진행중일때만 작성가능
		Map checkResult = spRfxPreInspRepository.checkRfxHdStsP(param);
		if(checkResult == null) {
			return ResultMap.builder()
							.resultStatus(Const.NOT_EXIST)
							.resultData(param)
							.build();
		}
		if(!"Y".equals(checkResult.get(ValidatorConst.VALID_YN))) {
			return ResultMap.builder()
							.resultStatus(RfxConst.RFX_END)
							.build();
		}
		
		return ResultMap.SUCCESS();
	}

	public ResultMap deleteRfxPreInspApp(Map rfxPreInspAppInfo) {
		if(rfxPreInspAppInfo == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		if(!"CRNG".equals(rfxPreInspAppInfo.get("subm_sts_ccd"))) {
			return ResultMap.INVALID();
		}
		
		return ResultMap.SUCCESS();
	}

}
