package smartsuite.app.common.glossary;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.common.glossary.service.GlossaryService;

import smartsuite.app.common.shared.ResultMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@Controller
@RequestMapping(value="**/bp/**/")
public class GlossaryController {
	
	@Inject
	GlossaryService glossaryService;
	
	/**
	 * 용어집 정보 조회
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "findListGlossary.do")
	public @ResponseBody List findListGlossary(@RequestBody Map param) {
		return glossaryService.findListGlossary(param);
	}
	
	/**
	 * 용어집 정보 저장
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "saveListGlossary.do")
	public @ResponseBody ResultMap saveListGlossary(@RequestBody Map<String, Object> param) {
		return glossaryService.saveListGlossary(param);
	}
	
	/**
	 * 용어집 정보 삭제
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "deleteGlossary.do")
	public @ResponseBody ResultMap deleteGlossary(@RequestBody Map<String, Object> param) {
		return glossaryService.deleteGlossary(param);
	}
}
