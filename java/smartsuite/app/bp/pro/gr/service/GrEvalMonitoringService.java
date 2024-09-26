package smartsuite.app.bp.pro.gr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.gr.repository.GrEvalMonitoringRepository;

import javax.inject.Inject;
import java.util.Map;

@Service
@Transactional
public class GrEvalMonitoringService {
	
	@Inject
	GrEvalMonitoringRepository grEvalMonitoringRepository;
	
	public String findCreateGeYnByGrEvalsht(Map grEvalshtInfo) {
		return grEvalMonitoringRepository.findCreateGeYnByGrEvalsht(grEvalshtInfo);
	}
	
	public String findCreateGeYnByGeg(Map row) {
		return grEvalMonitoringRepository.findCreateGeYnByGeg(row);
	}
}
