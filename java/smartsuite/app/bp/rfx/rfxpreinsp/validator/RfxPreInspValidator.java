package smartsuite.app.bp.rfx.rfxpreinsp.validator;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import smartsuite.app.common.shared.ResultMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class RfxPreInspValidator {
	
	public ResultMap passRfxPreInsp(Map rfxPreInspInfo, List selectedList) {
		String rfxPreInspId = (String) rfxPreInspInfo.get("rfx_presn_uuid");
		String rfxPreInspCompYn = (String) rfxPreInspInfo.get("rfxPreInspCompYn");
		
		// id가없는 경우 오류
		if(Strings.isNullOrEmpty(rfxPreInspId)) {
			return ResultMap.NOT_EXISTS();
		}
		if(selectedList == null || selectedList.isEmpty()) {
			return ResultMap.NOT_EXISTS();
		}
		// 사전심사 완료인 경우
		if("Y".equals(rfxPreInspCompYn)) {
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
			                .build();
		}
		
		return ResultMap.SUCCESS();
	}
	
	public ResultMap unPassRfxPreInsp(Map rfxPreInspInfo, List selectedList) {
		// 부적격인 경우 적격과 동일한 validation 수행
		return this.passRfxPreInsp(rfxPreInspInfo, selectedList);
	}
	
	public ResultMap compRfxPreInsp(Map rfxPreInspInfo) {
		String rfxPreInspId = (String) rfxPreInspInfo.get("rfx_presn_uuid");
		
		// id가없는 경우 오류
		if(Strings.isNullOrEmpty(rfxPreInspId)) {
			return ResultMap.NOT_EXISTS();
		}
		
		Date rfxPreInspStartDt = (Date) rfxPreInspInfo.get("rfx_presn_st_dttm");
		Date rfxPreInspEndDt = (Date) rfxPreInspInfo.get("rfx_presn_clsg_dttm");
		Date now = new Date();
		
		// 사전심사 시작일이 현재일보다 작거나 같으면 0 or -1
		int startCompare = rfxPreInspStartDt.compareTo(now);
		// 사전심사 종료일이 현재일보다 크거나 같으면 0 or 1
		int endCompare = rfxPreInspEndDt.compareTo(now);
		// 결과적으로 현재시점이 시작, 종료일 사이면 오류 발생
		if(startCompare <= 0 && endCompare >= 0) {
			return ResultMap.builder()
			                .resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
			                .resultData(rfxPreInspInfo)
			                .build();
		}
		
		return ResultMap.SUCCESS();
	}
}
