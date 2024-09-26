package smartsuite.app.bp.vendorMaster.vendorImprove.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vendorImprove.repository.VendorImproveRepository;
import smartsuite.app.bp.vendorMaster.vendorUser.repository.VendorUserRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class VendorImproveService {

	@Inject
	private VendorImproveRepository vendorImproveRepository;

	/**
	 * 개선요청관리 목록 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the list
	 * @Date : 2023. 07. 05
	 * @Method Name : findListImprove
	 */
	public List findListImprove(Map param) {
		return vendorImproveRepository.findListImprove(param);
	}

	/**
	 * 개선요청관리 저장을 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 05
	 * @Method Name : saveImproveInfo
	 */
	public ResultMap saveImproveInfo(Map param) {
		// 게시물
		Map<String, Object> info = (Map<String, Object>)param.get("improveInfo");

		String vdImpReqId = (String)info.get("vd_improv_uuid");
		String reqType = (String)param.get("reqType");
		String saveType = (String)param.get("saveType");
		String nextStsCd = this.getImproveNextStsCd(param);
		info.put("req_type", reqType);
		info.put("improv_sts_ccd", nextStsCd);

		if (vdImpReqId == null || "".equals(vdImpReqId)) {  //신규
			String uuid = (String) UUID.randomUUID().toString();
			info.put("vd_improv_uuid", uuid);
			vendorImproveRepository.insertImproveReqInfo(info);
		} else {
			vendorImproveRepository.updateImproveReqInfo(info);
		}

		return ResultMap.SUCCESS();
	}

	/* 협력사 개선관리 트랜잭션 시 상태값 변경 코드 */
	private String getImproveNextStsCd(Map<String, Object> param) {
		String reqType = (String)param.get("reqType");
		String saveType = (String)param.get("saveType");
		String nextStsCd = "10"; // 기본값 : 개선제안 임시저장

		if(reqType == null || "request".equals(reqType)) {
			if(saveType == null || "temp".equals(saveType)) {
				nextStsCd = "IMPROV_REQ_CRNG"; // 임시저장
			} else {
				nextStsCd = "IMPROV_REQG"; // 개선요청
			}
		} else if("plan".equals(reqType)) {
			if(saveType == null || "reject".equals(saveType)) {
				nextStsCd = "IMPROV_PLAN_SUPP_REQG"; // 개선계획 반려
			} else {
				nextStsCd = "IMPROV_CMPLD_CRNG"; // 개선계획 승인 시 개선완료 임시저장상태로 변경
			}
		}  else {
			if(saveType == null || "reject".equals(saveType)) {
				nextStsCd = "IMPROV_CMPLD_SUPP_REQG"; // 개선완료 반려
			} else {
				nextStsCd = "IMPROV_CMPLD"; // 개선완료 승인
			}
		}

		return nextStsCd;
	}

	/**
	 * 개선요청관리 상세 조회를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 05
	 * @Method Name : findImproveDetail
	 */
	public Map findImproveDetail(Map param) {
		return vendorImproveRepository.findImproveDetail(param);
	}

	/**
	 * 개선요청관리(다건) 삭제를 요청한다.
	 *
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 07. 07
	 * @Method Name : deleteImproveList
	 */
	public ResultMap deleteImproveList(Map param) {
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");

		for (Map<String, Object> row : deletes) {
			vendorImproveRepository.deleteImproveInfo(row);
		}

		return ResultMap.SUCCESS();
	}

	// 퍼포먼스 평가대상 결과의 개선등록 데이터를 조회한다.
	public List findListPeSubjVdImprove(Map param) {
		return vendorImproveRepository.findListPeSubjVdImprove(param);
	}

	// 퍼포먼스 평가대상 결과의 개선등록 데이터를 생성한다.
	public void createPeSubjVdImprove(Map param) {
		vendorImproveRepository.insertPeSubjVdImprove(param);
	}
}
