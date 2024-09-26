package smartsuite.app.bp.edoc.template.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.bp.edoc.template.repository.MainTemplateRepository;
import smartsuite.app.bp.edoc.template.repository.SubTemplateRepository;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.data.FloaterStream;

/**
 * 계약항목 관련 처리하는 서비스 Class입니다.
 *
 * @FileName SubTemplateService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class SubTemplateService {
	

	@Inject
	SubTemplateRepository subTemplateRepository;
	
	@Inject
	MainTemplateRepository mainTemplateRepository;

	@Inject
	ClauseService clauseService;
	
	@Inject
	AttachService attachService;
	
	/**
	 * 첨부서식 변경이력 조회
	 * @param Map
	 * @return List
	 */
	public List<Map<String,Object>> findListAttformHistory(Map param) {
		return subTemplateRepository.findListAttformHistory(param);
	}
	
	/**
	 * 변경이력 첨부서식내용 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public Map findAppCont(Map param) {
		return subTemplateRepository.findAppCont(param);
	}
	
	/**
	 * 첨부서식 목록 조회
	 *
	 * @param Map
	 * @return List
	 */
	public FloaterStream largeSearchListAppForm(Map<String, Object> param) {
		return subTemplateRepository.largeSearchListAppForm(param);
	}
	
	/**
	 * 첨부서식 목록 저장
	 *
	 * @param Map
	 * @return Map
	 */
	public ResultMap saveListAppForm(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		List insertList = (List) param.get("insertList");
		List updateList = (List) param.get("updateList");

		// UPDATE
		for (Map row : (List<Map>)updateList) {
			
			int nameCheckResult = subTemplateRepository.checkAppFormName(row);

			if (nameCheckResult > 0) {
				return ResultMap.DUPLICATED();
				
			} else {
				subTemplateRepository.updateAppForm(row);
				subTemplateRepository.updateCntrAppForm(row);
			}
		}
		// INSERT
		for (Map row : (List<Map>)insertList) {
			int nameCheckResult = subTemplateRepository.checkAppFormName(row);

			if (nameCheckResult > 0) {
				return ResultMap.DUPLICATED();

			} else {
				String newAttrNo = UUID.randomUUID().toString();
				
				row.put("appx_tmpl_uuid", newAttrNo);
				subTemplateRepository.insertAppForm(row);
				subTemplateRepository.insertAppFormCont(row);
				
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
			}
		}

		return ResultMap.SUCCESS();
	}
	
	/**
	 * 첨부서식 내용 조회
	 *
	 * @param Map
	 * @return Map
	 */
	public Map findAppformCont(Map<String, Object> param) {
		return subTemplateRepository.findAppformCont(param);
	}
	
	/**
	 * 첨부서식 내용 저장(갱신)
	 *
	 * @param Map
	 * @return Map
	 */
	public ResultMap saveAppFormCont(Map param) {
		// 계약 조항 사용 내역 저장
		clauseService.saveCntrClauseUse((String) param.get("appx_tmpl_uuid"), (List) param.get("use_clause_list"));
		
		// 부속서류 템플릿 저장
		subTemplateRepository.updateAppFormCont(param);
		subTemplateRepository.updateAppForm(param);
		
		// 부속서류 템플릿 변경 이력 추가
		String attDocFormModRev = subTemplateRepository.getAttDocFormSeq(param);
		param.put("appx_tmpl_chg_revno", attDocFormModRev);
		subTemplateRepository.insertAttFormHistory(param);

		return ResultMap.SUCCESS();
	}
	
	
	/**
	 * 첨부서식 목록 삭제
	 *
	 * @param Map
	 * @return Map
	 */
	public ResultMap deleteListAppForm(Map<String, Object> param) {

		List deleteList = (List) param.get("deleteList");

		// DELETE
		for (Map row : (List<Map>)deleteList) {
			subTemplateRepository.deleteAttFormHistory(row);
			mainTemplateRepository.deleteCntrAppForm(row);
			subTemplateRepository.deleteAppFormCont(row);
			subTemplateRepository.deleteAppForm(row);
			//계약항목 사용 정보 삭제
			clauseService.deleteCntrClauseUse((String) row.get("appx_tmpl_uuid"));
		}

		return ResultMap.SUCCESS();
	}
	
	
	/**
	 * 첨부서식 파일 저장(갱신) / ep-appform-file 팝업
	 *
	 * @param Map (manager 키가 있다면 es-cntrmanager창에서 호출하는 경우)
	 * @return Map
	 */
	public ResultMap saveAppFormFile(Map<String, Object> param) {
		
		if(param.containsKey("manager")){
			// 실제파일 존재여부 체크
			//String checkYn = sqlSession.selectOne("cntrFormMgt.checkAttFileYn", param);
			
			List findList = attachService.findListAttach(param);
			
			if(findList == null || findList.isEmpty()) {
				param.put("athg_uuid", null);
			}
			subTemplateRepository.updateAppFormInSts(param);
		}else{
			subTemplateRepository.updateAppFormFile(param);
		}
		
		return ResultMap.SUCCESS();
		
	}
	
	/**
	 * 첨부서식 목록 조회 (첨부추가 팝업)
	 * 
	 * @param Map
	 * @return List
	 */
	public List findListEpAppForm(Map param) {
		return subTemplateRepository.findListEpAppForm(param);
	}
	
	/**
	 * 계약서 별 첨부서식
	 * 
	 * @param Map
	 * @return List
	 */
	public List getAppList(Map param) {
		return subTemplateRepository.getAppList(param);
	}
	
	/**
	 * 첨부서식(text) 목록 조회)
	 * 
	 * @param Map
	 * @return List
	 */
	public List getAppFormTextContent(Map param) {
		return subTemplateRepository.getAppFormTextContent(param);
	}
	
	
	
	
}