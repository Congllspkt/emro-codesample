package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxVendorRepository;
import smartsuite.security.authentication.PasswordGenerator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxVendorService {
	
	@Inject
	RfxVendorRepository rfxVendorRepository;
	
	@Inject
	RfxVendorItemService rfxVendorItemService;
	
	@Inject
	RfxVendorRcmdService rfxVendorRcmdService;
    
    @Inject
    PasswordGenerator passwordGenerator;
	
	/**
	 * 기존재 견적대상업체 다건 삭제
	 *
	 * @param rfxVendors - 견적대상업체 다건
	 * @return void
	 */
	public void deleteRfxVendor(List<Map> rfxVendors) {
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		this.deleteRcmndVendorByRfxVendor(rfxVendors);
		rfxVendorItemService.deleteRfxVendorItemByRfxVendor(rfxVendors);
		for(Map rfxVendor : rfxVendors) {
			rfxVendorRepository.deleteRfxVendor(rfxVendor);
		}
	}
	
	/**
	 * 기존재 견적대상업체 삭제
	 *
	 * @param rfxVendor - 견적대상업체
	 * @return void
	 */
	public void deleteRfxVendor(Map rfxVendor) {
		if(rfxVendor == null) {
			return;
		}
		
		this.deleteRcmndVendorByRfxVendor(rfxVendor);
		rfxVendorItemService.deleteRfxVendorItemByRfxVendor(rfxVendor);
		rfxVendorRepository.deleteRfxVendor(rfxVendor);
	}
	
	/**
	 * 견적대상업체 삭제 시 추천업체 정보 삭제
	 *
	 * @param rfxVendors - 견적대상업체 다건
	 * @return void
	 */
	private void deleteRcmndVendorByRfxVendor(List<Map> rfxVendors) {
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : rfxVendors) {
			this.deleteRcmndVendorByRfxVendor(rfxVendor);
		}
	}
	
	/**
	 * 견적대상업체 삭제 시 추천업체 정보 삭제
	 *
	 * @param rfxVendor - 견적대상업체
	 * @return void
	 */
	private void deleteRcmndVendorByRfxVendor(Map rfxVendor) {
		rfxVendorRepository.deleteRcmndVendorByRfxVendor(rfxVendor);
	}
	
	/**
	 * 기존재 견적대상업체 다건 변경내용 반영
	 *
	 * @param rfxVendors - 변경발생 견적대상업체 다건
	 * @return void
	 */
	public void updateRfxVendor(List<Map> rfxVendors) {
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : rfxVendors) {
			this.updateRfxVendor(rfxVendor);
		}
	}
	
	/**
	 * 기존재 견적대상업체 변경내용 반영
	 *
	 * @param rfxVendor - 변경발생 견적대상업체
	 * @return void
	 */
	public void updateRfxVendor(Map rfxVendor) {
		rfxVendorRepository.updateRfxVendor(rfxVendor);
	}
	
	/**
	 * 견적대상업체 저장
	 *
	 * @param rfxData    - 견적마스터
	 * @param rfxVendors - 견적대상업체 다건
	 * @return void
	 */
	public void insertRfxVendor(Map rfxData, List<Map> rfxVendors) {
		if(rfxData == null) {
			return;
		}
		if(rfxVendors == null) {
			return;
		}
		if(rfxVendors.isEmpty()) {
			return;
		}
		
		for(Map rfxVendor : rfxVendors) {
			this.insertRfxVendor(rfxData, rfxVendor);
		}
		
		rfxVendorItemService.insertRfxVendorItemByRfxVendor(rfxData, rfxVendors);
	}
	
	/**
	 * 견적대상업체 저장
	 *
	 * @param rfxData   - 견적마스터
	 * @param rfxVendor - 견적대상업체
	 * @return void
	 */
	private void insertRfxVendor(Map rfxData, Map rfxVendor) {
		rfxVendor.put("rfx_vd_uuid", UUID.randomUUID().toString());
		rfxVendor.put("rfx_uuid", rfxData.get("rfx_uuid"));
		rfxVendor.put("rfx_no", rfxData.get("rfx_no"));
		rfxVendor.put("cur_ccd", rfxData.get("cur_ccd"));
		rfxVendor.put("rfx_rnd", rfxData.get("rfx_rnd"));
		
		rfxVendorRepository.insertRfxVendor(rfxVendor);
		this.insertRcmndVendorByRfxVendor(rfxData, rfxVendor);
	}
	
	/**
	 * 자동추천 업체인 경우 매핑정보 저장
	 *
	 * @param rfxData   - 견적마스터
	 * @param rfxVendor - 견적대상업체
	 * @return void
	 */
	private void insertRcmndVendorByRfxVendor(Map rfxData, Map rfxVendor) {
		if(rfxVendor == null) {
			return;
		}
		if(Strings.isNullOrEmpty((String) rfxVendor.get("auto_rcmd_vd_yn"))) {
			return;
		}
		if(!"Y".equals(rfxVendor.get("auto_rcmd_vd_yn"))) {
			return;
		}

		List<String> rcmdList = (List<String>) rfxVendor.get("rcmdList");
		for(String rcmdListData : rcmdList) {
            Map rcmdInfo = Maps.newHashMap();
			rcmdInfo.put("rfx_vd_rcmd_uuid", UUID.randomUUID().toString());
			rcmdInfo.put("rfx_vd_uuid", rfxVendor.get("rfx_vd_uuid"));
			rcmdInfo.put("rfx_uuid", rfxData.get("rfx_uuid"));
			rcmdInfo.put("rfx_no", rfxData.get("rfx_no"));
			rcmdInfo.put("rfx_rnd", rfxData.get("rfx_rnd"));
			rcmdInfo.put("vd_cd", rfxVendor.get("vd_cd"));
            rcmdInfo.put("rcmd_vd_typ_ccd", rcmdListData);
			
			rfxVendorRepository.insertRcmndVendor(rcmdInfo);
		}
	}
	
	/**
	 * vendorList rfx vendor 조회한다.
	 *
	 * @param param the param
	 * @return the vendorList
	 * @author : Yeon-u Kim
	 * @Date : 2016. 5. 11
	 * @Method Name : searchRfxVendor
	 */
	public List searchRfxVendorByRfx(Map param) {
		List<Map> vendors = rfxVendorRepository.searchRfxVendorByRfx(param);
		Map vendorRcmds = rfxVendorRcmdService.searchRfxVendorRcmdByRfx(param);
		if(vendorRcmds != null && !vendorRcmds.isEmpty()) {
			for(Map vendor : vendors) {
				if("Y".equals(vendor.get("auto_rcmd_vd_yn"))) {
					List<String> rcmdList = (List<String>) vendorRcmds.get(vendor.get("vd_cd"));
					vendor.put("rcmd_cnt", rcmdList.size());
					vendor.put("rcmdList", rcmdList);
				}
			}
		}
		
		return vendors;
	}
	
	/**
	 * 견적마스터 대상으로 견적대상업체를 전부 삭제한다.
	 *
	 * @param rfxData - 삭제할 견적마스터
	 */
	public void deleteRfxVendorByRfx(Map rfxData) {
		List rfxVendors = this.searchRfxVendorByRfx(rfxData);
		this.deleteRfxVendor(rfxVendors);
	}
	
	/**
	 * multi round 생성을 위한 견적대상업체 데이터 조회한다.<br>
	 * multi round 가능한 업체만 조회한다.<br><br>
	 * 또한, 견적참여업체가 존재할 경우는 참여업체만,<br>
	 * 견적참여업체가 존재하지 않을 경우는 전체 업체를 조회한다.
	 *
	 * @param rfxId       - 견적요청 ID
	 * @param vdSubmitCnt - 참여업체 건수
	 * @return List - 견적대상업체 다건
	 */
	public List searchRfxVendorByMultiRound(String rfxId, int vdSubmitCnt) {
		if(rfxId == null) {
			return null;
		}
		
		Map param = Maps.newHashMap();
		param.put("rfx_uuid", rfxId);
		param.put("multi_round_yn", "Y");
		
		if(vdSubmitCnt != 0) {
			// 제출한 업체만 조회하기 위함
			param.put("rfx_bid_sts_ccd", "SUBM");
		}
		
		return this.searchRfxVendorByRfx(param);
	}
	
	/**
	 * 견적대상업체 복사를 위한 견적대상업체 클리어 데이터를 반환한다.<br><br>
	 *
	 * @param rfxData - 견적요청 마스터
	 * @return List
	 */
	public List getCopyRfxVendors(Map sourceRfxData) {
		if(sourceRfxData == null) {
			return null;
		}
		
		List rfxVendors = rfxVendorRepository.searchRfxVendorByRfx(sourceRfxData);
		for(Map rfxVendor : (List<Map>) rfxVendors) {
			rfxVendor.put("rfx_vd_uuid", null);
			rfxVendor.put("rfx_uuid", null);
			rfxVendor.put("rfx_no", null);
			rfxVendor.put("rfx_rnd", 1);
			rfxVendor.put("rfx_bid_uuid", null);
			rfxVendor.put("multrnd_subj_xcept_yn", "N");    // 제외여부
			rfxVendor.put("multrnd_xcept_rsn", null);    // 제외 사유
			rfxVendor.put("auto_rcmd_vd_yn", "N");    // 추천 여부
			rfxVendor.put("rcmd_vd_typ_ccd", null);    // 추천코드
		}
		return rfxVendors;
	}
    
    /**
     * 신규 협력사 초청 저장
     * @param param
     */
    public void saveInviteNewVendor(Map param) {
        rfxVendorRepository.saveInviteNewVendor(param);
    }
    
    /**
     * rfx 초대 신규 협력사 조회
     *
     * @param param
     * @return
     */
    public List searchRfxInviteNewVendor(Map param) {
        return rfxVendorRepository.searchRfxInviteNewVendor(param);
    }
    
    /**
     * rfx 업체 중 신규 협력사 조회
     *
     * @param rfxInfo
     * @return
     */
    public List searchRfxNewVendor(Map rfxInfo) {
        return rfxVendorRepository.searchRfxNewVendor(rfxInfo);
    }
    
    /**
     * 신규 협력사 초청 리스트 저장
     *
     * @param vendorList
     */
    public void saveInviteNewVendors(List<Map> vendorList) {
        for(Map vendor : vendorList) {
            vendor.put("rfx_vd_invi_uuid", UUID.randomUUID().toString());
            vendor.put("auth_no", passwordGenerator.generate());
            saveInviteNewVendor(vendor);
        }
        
    }
}
