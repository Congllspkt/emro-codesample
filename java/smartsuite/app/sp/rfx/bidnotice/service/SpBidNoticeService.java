package smartsuite.app.sp.rfx.bidnotice.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.sp.rfx.bidnotice.repository.SpBidNoticeRepository;
import smartsuite.app.sp.rfx.bidnotice.validator.SpBidNoticeValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpBidNoticeService {
	
	@Inject
	SpBidNoticeRepository spBidNoticeRepository;
	
	@Inject
	SpBidNoticeValidator spBidNoticeValidator;
	
	/** The shared service. */
	@Inject
	SharedService sharedService;

	/**
	 * 협력사 입찰공고현황 조회를 요청한다.
	 * 
	 * @param param : 검색조건
	 * @return list : 입찰공고현황(협력사)
	 * @Date : 2021. 5. 17
	 * @Method Name : findListSpBidNoti
	 */
	public List<Map> findListSpBidNoti(Map param) {
		return spBidNoticeRepository.findListSpBidNoti(param);
	}
	
	/**
	 * 협력사 입찰공고현황 상세 조회를 요청한다.
	 * 
	 * @param param : rfx_prentc_uuid
	 * @return list : 입찰공고현황(협력사)
	 * @Date : 2021. 5. 17
	 * @Method Name : findDetailSpBidNoti
	 */
	public Map findDetailSpBidNoti(Map param) {
		return spBidNoticeRepository.findInfoSpBidPart(param);
	}
	
	/**
	 * 입찰참가 신청 정보를 조회한다.
	 * 
	 * @return list : 입찰참가신청 정보 조회
	 * @Date : 2021. 5. 17
	 * @Method Name : findInfoSpBidNoti
	 */
	public Map findInfoSpBidNoti(Map param) {
		Map resultMap = Maps.newHashMap();
		
		String notiPartAppId = (String) param.get("rfx_prentc_afp_uuid");
		if(!Strings.isNullOrEmpty(notiPartAppId)) {
			Map vendorChrInfo = spBidNoticeRepository.findInfoSpBidPartVendorChr(param);
			resultMap.put("vendorChrInfo", vendorChrInfo);
			
			List<Map> attachList;
			boolean existsAttach = spBidNoticeRepository.isExistsSpNotiPartAttach(param);
			if(existsAttach) {
				attachList = spBidNoticeRepository.findListSpBidPartedAttach(param);
			} else {
				attachList = spBidNoticeRepository.findListSpBidPartAttach(param);
			}
			resultMap.put("attachList", attachList);
		} else {
			List<Map> attachList = spBidNoticeRepository.findListSpBidPartAttach(param);
			resultMap.put("attachList", attachList);
		}
		
		Map notiInfo = spBidNoticeRepository.findInfoSpBidPart(param);
		notiInfo.put("rfx_prentc_afp_uuid", notiPartAppId);

		resultMap.put("notiInfo", notiInfo);
		resultMap.put("vendorInfo", spBidNoticeRepository.findInfoSpBidPartVendor(param)); 
		return resultMap;
	}
	
	/**
	 * bid noti 저장한다.
	 *
	 * @param deleteBidNoti the delete bid noti
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : saveSpBidNoti
	 */
	public ResultMap saveSpBidNoti(Map param){
		Map notiInfo = (Map) param.get("bidNotiData");
		List<Map> attachList = (List<Map>) param.get("attachList");
		
		ResultMap validator = spBidNoticeValidator.validateNotiEnd(notiInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		validator = spBidNoticeValidator.validateNotiEndTime(notiInfo);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		boolean isExist = spBidNoticeRepository.isExistSpBindNotiPart(notiInfo);
		if(isExist) {
			//update
			spBidNoticeRepository.updateSpBidNoti(notiInfo);
			//첨부파일 저장
			this.saveSpBidNotiAttach(notiInfo, attachList);
		} else {
			//신규 insert
			notiInfo.put("rfx_prentc_afp_uuid", UUID.randomUUID().toString());
			spBidNoticeRepository.insertSpBidNoti(notiInfo);
			//첨부파일 insert 
			this.saveSpBidNotiAttach(notiInfo, attachList);
		}
		
		return ResultMap.SUCCESS(notiInfo);
	}
	
	public void saveSpBidNotiAttach(Map notiInfo, List<Map> attachList) {
		if(attachList == null) {
			return;
		}
		if(attachList.isEmpty()) {
			return;
		}
		
		for(Map attach : attachList) {
			String sbmtDocId = (String) attach.get("rfx_prentc_submddoc_uuid");
			if(!Strings.isNullOrEmpty(sbmtDocId)) {
				spBidNoticeRepository.updateSpBidNotiAttach(attach);
			} else {
				attach.put("rfx_prentc_submddoc_uuid", UUID.randomUUID().toString());
				attach.put("rfx_prentc_afp_uuid", notiInfo.get("rfx_prentc_afp_uuid"));
				attach.put("vd_cd", notiInfo.get("vd_cd"));
				attach.put("rfx_prentc_uuid", notiInfo.get("rfx_prentc_uuid"));
				attach.put("rfx_prentc_no", notiInfo.get("rfx_prentc_no"));
				attach.put("rfx_prentc_rnd", notiInfo.get("rfx_prentc_rnd"));
				
				spBidNoticeRepository.insertSpBidNotiAttach(attach);
			}
		}
	}
	
	/**
	 * bid noti 삭제한다.
	 *
	 * @param deleteBidNoti the delete bid noti
	 * @return the map< string, object>
	 * @Date : 2021. 5. 14
	 * @Method Name : deleteSpBidNoti
	 */
	public ResultMap deleteSpBidNoti(Map param) {
		// validate
		ResultMap validator = spBidNoticeValidator.validateNotiEnd(param);
		if(!validator.isSuccess()) {
			return validator;
		}
		
		spBidNoticeRepository.deleteSpBidNotiAtt(param);
		spBidNoticeRepository.deleteSpBidNoti(param);
		return ResultMap.SUCCESS();
	}
}