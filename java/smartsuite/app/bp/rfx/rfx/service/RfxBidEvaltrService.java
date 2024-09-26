package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxBidEvaltrRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxBidEvaltrService {
	
	@Inject
	RfxBidEvaltrRepository rfxBidEvaltrRepository;
	
	public void insertRfxBidEvaltr(List<Map> submRfxBids, List<Map> npeFactEvaltrs) {
		if(submRfxBids == null) {
			return;
		}
		if(submRfxBids.isEmpty()) {
			return;
		}
		if(npeFactEvaltrs == null) {
			return;
		}
		if(npeFactEvaltrs.isEmpty()) {
			return;
		}
		
		for(Map submRfxBid : submRfxBids) {
			this.insertRfxBidEvaltr(submRfxBid, npeFactEvaltrs);
		}
	}
	
	private void insertRfxBidEvaltr(Map submRfxBid, List<Map> npeFactEvaltrs) {
		Map param = null;
		for(Map npeFactEvaltr : npeFactEvaltrs) {
			param = Maps.newHashMap();
			param.put("rfx_bid_uuid", submRfxBid.get("rfx_bid_uuid"));
			param.put("eval_pic_id", npeFactEvaltr.get("eval_pic_id"));
			param.put("rfx_uuid", submRfxBid.get("rfx_uuid"));
			param.put("dept_cd", npeFactEvaltr.get("dept_cd"));
			param.put("evalfact_evaltr_authty_ccd", npeFactEvaltr.get("evalfact_evaltr_authty_ccd"));
			
			rfxBidEvaltrRepository.insertRfxBidEvaltr(param);
		}
	}
	
	public List<Map> findListRfxBidEvaltr(Map rfxInfo) {
		return rfxBidEvaltrRepository.findListRfxBidEvaltr(rfxInfo);
	}
	
	public void deleteRfxBidEvaltrByRfxBidUuid(List<Map> submRfxBids) {
		if(submRfxBids == null) {
			return;
		}
		if(submRfxBids.isEmpty()) {
			return;
		}
		for(Map submRfxBid : submRfxBids) {
			this.deleteRfxBidEvaltrByRfxBidUuid(submRfxBid);
		}
	}
	
	private void deleteRfxBidEvaltrByRfxBidUuid(Map submRfxBid) {
		rfxBidEvaltrRepository.deleteRfxBidEvaltrByRfxBidUuid(submRfxBid);
	}
}
