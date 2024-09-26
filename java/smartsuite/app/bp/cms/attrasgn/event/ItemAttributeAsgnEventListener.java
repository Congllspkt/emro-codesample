package smartsuite.app.bp.cms.attrasgn.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.cms.attrasgn.service.ItemAttributeAsgnService;
import smartsuite.app.bp.cms.cmsCommon.service.CmsCommonService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class ItemAttributeAsgnEventListener {
	
	@Inject
	ItemAttributeAsgnService itemAttributeAsgnService;

	/**
	 * 품목분류에서 넘어온 속성 배정 값 삭제하기
	 * 
	 * @param event
	 */
	@EventListener(condition = "#event.eventId == 'deleteInfoItemcatIattrFromItemcat'")
	public void deleteInfoItemcatIattrFromItemcat(CustomSpringEvent event) {
		itemAttributeAsgnService.deleteInfoItemcatIattrFromItemcat((Map<String, Object>) event.getData());
	}
}
