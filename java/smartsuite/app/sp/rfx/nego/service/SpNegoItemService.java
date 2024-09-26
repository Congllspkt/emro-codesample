package smartsuite.app.sp.rfx.nego.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.rfx.nego.repository.SpNegoItemRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpNegoItemService {
	@Inject
	SpNegoItemRepository spNegoItemRepository;
	
	public List findNegoQtaDetail(Map param) {
		return spNegoItemRepository.findNegoQtaDetail(param);
	}
	
	public void saveNegoDetail(Map item) {
		spNegoItemRepository.saveNegoDetail(item);
	}
	
	public void initNegoItemPrice(Map param) {
		spNegoItemRepository.initNegoItemPrice(param);
	}
}
