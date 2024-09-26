package smartsuite.app.sp.vendorMaster.vendorImprove.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vendorImprove.repository.VendorImproveRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorImprove.repository.SpVendorImproveRepository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class SpVendorImproveService {

	@Inject
	private SpVendorImproveRepository spVendorImproveRepository;

	/**
	 * 개선요청관리 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 05
	 * @Method Name : findListImprove
	 */
	public List findListImprove(Map param) {
		return spVendorImproveRepository.findListImprove(param);
	}

	/**
	 * 개선요청관리 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 06
	 * @Method Name : findImproveDetail
	 */
	public Map findImproveDetail(Map param) {
		return spVendorImproveRepository.findImproveDetail(param);
	}

	/**
	 * 개선요청관리 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 06
	 * @Method Name : saveImproveInfo
	 */
	public ResultMap saveImproveInfo(Map param) {
		Map<String, Object> resultMap = new HashMap<>();

		// 게시물
		Map<String, Object> info = (Map<String, Object>)param.get("improveInfo");
		String vdImpId = (String)info.get("vd_improv_uuid"); //개선요청 uuid
		String reqType = (String)param.get("reqType");
		String saveType = (String)param.get("saveType");
		String nextStsCd = this.getImproveNextStsCd(param);

		info.put("req_type", reqType);
		info.put("improv_sts_ccd", nextStsCd);

		if (vdImpId != null && !vdImpId.equals("")) {
			spVendorImproveRepository.updateImproveInfo(info);
			resultMap.put("act", "update");
		}

		return ResultMap.SUCCESS(resultMap);
	}

	/* 협력사 개선관리 트랜잭션 시 상태값 변경 코드 */
	private String getImproveNextStsCd(Map<String, Object> param) {
		String reqType = (String)param.get("reqType");
		String saveType = (String)param.get("saveType");
		String nextStsCd = "IMPROV_REQ_CRNG"; // 기본값 : 개선제안 임시저장

		if("plan".equals(reqType)) {
			if(saveType == null || "temp".equals(saveType)) {
				nextStsCd = "IMPROV_PLAN_CRNG"; // 개선계획 임시저장
			} else {
				nextStsCd = "IMPROV_PLAN_SUBM"; // 개선계획 승인요청
			}
		}  else if("complete".equals(reqType)) {
			if(saveType == null || "temp".equals(saveType)) {
				nextStsCd = "IMPROV_CMPLD_CRNG"; // 개선완료 임시저장
			} else {
				nextStsCd = "IMPROV_CMPLD_SUBM"; // 개선완료 승인요청
			}
		}

		return nextStsCd;
	}
}
