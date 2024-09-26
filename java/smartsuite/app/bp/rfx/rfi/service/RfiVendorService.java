package smartsuite.app.bp.rfx.rfi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfi.repository.RfiVendorRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfiVendorService {
	
	@Inject
	RfiVendorRepository rfiVendorRepository;
	
	public void deleteRfiVendor(List rfiVendors) {
		if(rfiVendors == null) {
			return;
		}
		if(rfiVendors.isEmpty()) {
			return;
		}
		
		for(Map rfiVendor : (List<Map>) rfiVendors) {
			this.deleteRfiVendor(rfiVendor);
		}
	}
	
	private void deleteRfiVendor(Map rfiVendor) {
		if(rfiVendor == null) {
			return;
		}
		
		rfiVendorRepository.deleteRfiVendor(rfiVendor);
	}
	
	public void insertRfiVendor(Map rfiData, List rfiVendors) {
		if(rfiData == null) {
			return;
		}
		if(rfiVendors == null) {
			return;
		}
		if(rfiVendors.isEmpty()) {
			return;
		}
		
		for(Map rfiVendor : (List<Map>) rfiVendors) {
			this.insertRfiVendor(rfiData, rfiVendor);
		}
	}
	
	private void insertRfiVendor(Map rfiData, Map rfiVendor) {
		if(rfiData == null) {
			return;
		}
		if(rfiVendor == null) {
			return;
		}
		
		rfiVendor.put("rfi_vd_uuid", UUID.randomUUID().toString());
		rfiVendor.put("rfi_uuid", rfiData.get("rfi_uuid"));
		
		rfiVendorRepository.insertRfiVendor(rfiVendor);
	}
	
	public void updateRfiVendor(List rfiVendors) {
		if(rfiVendors == null) {
			return;
		}
		if(rfiVendors.isEmpty()) {
			return;
		}
		
		for(Map rfiVendor : (List<Map>) rfiVendors) {
			this.updateRfiVendor(rfiVendor);
		}
	}
	
	private void updateRfiVendor(Map rfiVendor) {
		if(rfiVendor == null) {
			return;
		}
		
		rfiVendorRepository.updateRfiVendor(rfiVendor);
	}
	
	public void deleteRfiVendorByRfi(Map param) {
		if(param == null) {
			return;
		}
		
		rfiVendorRepository.deleteRfiVendorByRfi(param);
	}
	
	public List<Map> findListRfiVendor(Map param) {
		return rfiVendorRepository.findListRfiVendor(param);
	}
}
