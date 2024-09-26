package smartsuite.app.sp.eform.eformsign.event;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.app.sp.eform.eformsign.service.SpEFormSignService;

/**
 * 간편 서명 관련 처리하는 이벤트 리스터 Class입니다.
 *
 * @FileName SpEFromSignEventListener.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpEFormSignEventListener {

	@Inject
	SpEFormSignService spEFormSignService;
	
	
}
