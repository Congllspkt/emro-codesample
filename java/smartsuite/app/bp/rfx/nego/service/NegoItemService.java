package smartsuite.app.bp.rfx.nego.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.nego.repository.NegoItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NegoItemService {
	@Inject
	NegoItemRepository negoItemRepository;
	
	public List findNegoDetail(Map param){
		return negoItemRepository.findNegoDetail(param);
	}
	
	public List findNegoBidDetail(Map param) {
		return negoItemRepository.findNegoBidDetail(param);
	}
	
	public List searchNegoRfxBidItemStl(Map rfxBid){
		return negoItemRepository.searchNegoRfxBidItemStl(rfxBid);
	}
	
	public void insertNegoDetail(Map item) {
		negoItemRepository.insertNegoDetail(item);
	}
	
	public void deleteNegoTargetDetailSts(Map item) {
		negoItemRepository.deleteNegoTargetDetailSts(item);
	}
	
	public void updateNegoPrice(Map item) {
		negoItemRepository.updateNegoPrice(item);
	}
}
