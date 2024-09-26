package smartsuite.app.sp.docusign;

import java.util.Enumeration;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import smartsuite.app.sp.docusign.service.SpDocuSignService;

/**
 * DocuSign 관련 처리하는 컨트롤러 Class입니다.
 *
 * @FileName SpDocuSignController.java
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/docusign/**")
public class SpDocuSignController {

	private static final Logger LOG = LoggerFactory.getLogger(SpDocuSignController.class);

	@Inject
	SpDocuSignService spDocuSignService;


	/**
	 * 봉투 팝업 이벤트 처리
	 * envelope 팝업에서 발생하는 이벤트를 콜백으로 받아서 수행하는 함수
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "returnRecipientView.do")
	public ModelAndView returnRecipientView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("econtract/docusignResult");
		String returnUiId = request.getParameter("return_ui_id");
		String event = request.getParameter("event");
		String usrCls = request.getParameter("cntrr_typ_ccd");
		String docusignId = request.getParameter("dsgn_uuid");

		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String value = request.getParameter(name);
			LOG.info("name:" + name + ",value:" + value);
		}
		
		Map result = spDocuSignService.returnRecipientView(event, docusignId);

		mv.addObject("uiId", returnUiId);
		mv.addObject("event", event);
		mv.addObject("usrCls", usrCls);
		mv.addObject("procType", result.get("sgnord_typ_ccd"));
		mv.addObject("cntrId", result.get("cntr_uuid"));
		return mv;
	}
}