package smartsuite.app.common.glossary.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import smartsuite.app.common.glossary.repository.GlossaryRepository;
import smartsuite.app.common.shared.ResultMap;


@Service
@Transactional
@SuppressWarnings ({ "unchecked" })
public class GlossaryService {
	
	@Inject
	GlossaryRepository glossaryRepository;
	
	/**
	 * 용어사전 정보 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListGlossary(Map<String, Object> param) {
		return glossaryRepository.findListGlossary(param);
	}
	
	/**
	 * 용어사전 정보 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveListGlossary(Map<String, Object> param) {
		List<Map<String, Object>> insertList = (List<Map<String, Object>>)param.getOrDefault("insertList",Lists.newArrayList());
		List<Map<String, Object>> updateList = (List<Map<String, Object>>)param.getOrDefault("updateList",Lists.newArrayList());
		// 추가
		if(insertList != null && !insertList.isEmpty()) {
			for(Map<String, Object> row : insertList) {
				this.insertGlossary(row);
			}
		}
		// 수정
		if(updateList != null && !updateList.isEmpty()) {
			for(Map<String, Object> row : updateList) {
				this.updateGlossary(row);
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 용어사전 정보 수정
	 * @param param
	 */
	public void updateGlossary(Map<String, Object> param) {
		glossaryRepository.updateGlossary(param);
	}
	
	/**
	 * 용어사전 정보 추가
	 * @param param
	 */
	public void insertGlossary(Map<String, Object> param) {
		glossaryRepository.insertGlossary(param);
	}
	
	/**
	 * 용어사전 정보 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteGlossary(Map<String, Object> param){
		List<Map<String, Object>> deleteList = (List<Map<String, Object>>)param.getOrDefault("deleteList",Lists.newArrayList());
		// 삭제
		if(deleteList != null && !deleteList.isEmpty()) {
			for(Map<String, Object> row : deleteList) {
				this.deleteGlossaryInfo(row);
			}
		}
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 용어사전 정보 삭제
	 * @param param
	 */
	public void deleteGlossaryInfo(Map<String, Object> param) {
		glossaryRepository.deleteGlossary(param);
	}
}
