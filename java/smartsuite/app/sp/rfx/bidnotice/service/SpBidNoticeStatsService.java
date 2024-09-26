package smartsuite.app.sp.rfx.bidnotice.service;

import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.template.service.TemplateGeneratorService;
import smartsuite.app.sp.rfx.bidnotice.repository.SpBidNoticeStatsRepository;
import smartsuite.mybatis.data.impl.PageResult;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpBidNoticeStatsService {
	
	@Inject
	SpBidNoticeStatsRepository spBidNoticeStatsRepository;
	
	@Inject
	TemplateGeneratorService templateGeneratorService;
	
	@Inject
	SharedService sharedService;
	
	/**
	 * 진행중인 입찰공고 전체 갯수를 반환한다.
	 *
	 * @return int
	 */
	public int getBidNoticeTotalCount() {
		return spBidNoticeStatsRepository.getBidNoticeTotalCount(Maps.newHashMap());
	}
	
	/**
	 * 입찰공고 헤더 정보 조회한다.
	 *
	 * @param param
	 * @return Map
	 */
	private Map findRfxInfo(Map param) {
		Map rfxInfo = spBidNoticeStatsRepository.findRfxInfo(param);
		rfxInfo.put("rfx_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("rfx_typ_ccd"), "P033"));
		rfxInfo.put("p2p_purc_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("purc_typ_ccd"), "P045"));
		rfxInfo.put("comp_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("comp_typ_ccd"), "B104"));
		rfxInfo.put("dlvymeth_ccd_nm", sharedService.findCodeName(rfxInfo.get("dlvymeth_ccd"), "P010"));
		rfxInfo.put("pymtmeth_ccd_nm", sharedService.findCodeName(rfxInfo.get("pymtmeth_ccd"), "P009"));
		rfxInfo.put("cur_ccd_nm", sharedService.findCodeName(rfxInfo.get("cur_ccd"), "C004"));
		rfxInfo.put("domovrs_div_ccd_nm", sharedService.findCodeName(rfxInfo.get("domovrs_div_ccd"), "C024"));
		rfxInfo.put("slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("slctn_typ_ccd"), "P007"));
		rfxInfo.put("item_slctn_typ_ccd_nm", sharedService.findCodeName(rfxInfo.get("item_slctn_typ_ccd"), "P002"));
		
		return rfxInfo;
	}
	
	/**
	 * 입찰공고 품목정보 조회한다.
	 *
	 * @param param
	 * @return List
	 */
	public List<Map> findRfxItemList(Map param) {
		List<Map> rfxItems = spBidNoticeStatsRepository.findRfxItemList(param);
		for(Map rfxItem : rfxItems) {
			rfxItem.put("uom_ccd_nm", sharedService.findCodeName(rfxItem.get("uom_ccd"), "C007"));
		}
		
		return rfxItems;
	}
	
	/**
	 * 협력사포탈 입찰공고 목록 조회
	 *
	 * @param pageInfo the param
	 * @return PageResult
	 */
	public PageResult findPagingBidNoticeList(Map pageInfo) {
		List<Map> bidNoticeList = spBidNoticeStatsRepository.findPagingBidNoticeList(pageInfo);
		for(Map bidNoticeInfo : bidNoticeList) {
			bidNoticeInfo.put("p2p_purc_typ_ccd_nm", sharedService.findCodeName(bidNoticeInfo.get("purc_typ_ccd"), "P045"));
			bidNoticeInfo.put("rfx_typ_ccd_nm", sharedService.findCodeName(bidNoticeInfo.get("rfx_typ_ccd"), "P033"));
			bidNoticeInfo.put("rfx_sts_ccd_nm", sharedService.findCodeName(bidNoticeInfo.get("rfx_sts_ccd"), "P013"));
		}
		
		return new PageResult(bidNoticeList);
	}
	
	/**
	 * 입찰 공고 Template 데이터 binding
	 *
	 * @param param
	 * @return String
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String getBidNoticeTemplateContent(Map param) throws TemplateException, IOException {
		//상세
		Map biddingInfo = Maps.newHashMap();
		Map rfxHdData = this.findRfxInfo(param);
		List<Map> rfxDtData = this.findRfxItemList(param);
		
		if(rfxHdData.containsKey("vd_athg_uuid") && null != rfxHdData.get("vd_athg_uuid")) {
			Map fileSearchParam = Maps.newHashMap();
			fileSearchParam.put("athg_uuid", rfxHdData.get("vd_athg_uuid"));
			biddingInfo.put("attData", sharedService.findAttList(fileSearchParam));
		}
		biddingInfo.put("rfxHdData", rfxHdData);
		biddingInfo.put("rfxDtData", rfxDtData);
		
		return templateGeneratorService.tmpGenerate("RFX_NOTICE", biddingInfo);
	}
}
