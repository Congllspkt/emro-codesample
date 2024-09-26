package smartsuite.app.common.status.service;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.common.status.repository.AsnStatusRepository;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class AsnStatusService {

	@Inject
	AsnStatusRepository asnStatusRepository;

	public void updateAsnEvalProgSts(Map<String, Object> param) {
		asnStatusRepository.updateAsnEvalProgSts(param);
	}

}
