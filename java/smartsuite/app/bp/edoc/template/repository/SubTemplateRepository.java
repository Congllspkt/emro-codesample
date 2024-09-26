package smartsuite.app.bp.edoc.template.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SubTemplateRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sub-template.";
	
	public List<Map<String,Object>> findListEpAppForm(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListEpAppForm",param);
	}
	
	public Map findAppCont(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppCont",param);
	}
	
	public List getAppFormTextContent(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAppFormTextContent",param);
	}
	
	public FloaterStream largeSearchListAppForm(Map<String, Object> param) {
		// 대용량 처리
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListAppForm", param);
	}
	
	public int checkAppFormName(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "checkAppFormName",param);
	}
	
	public void updateAppForm(Map param) {
		sqlSession.update(NAMESPACE + "updateAppForm",param);
	}
	
	public void updateCntrAppForm(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrAppForm",param);
	}
	
	public void insertAppForm(Map param) {
		sqlSession.insert(NAMESPACE + "insertAppForm",param);
	}
	
	public void insertAppFormCont(Map param) {
		sqlSession.insert(NAMESPACE + "insertAppFormCont",param);
	}
	
	public void deleteAttFormHistory(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAttFormHistory",param);
	}
	
	public void deleteAppFormCont(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAppFormCont",param);
	}
	
	public List findListAttformHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListAttformHistory",param);
	}
	
	public void deleteAppForm(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAppForm",param);
	}
	
	public void updateAppFormInSts(Map param) {
		sqlSession.update(NAMESPACE + "updateAppFormInSts",param);
	}
	
	public void updateAppFormFile(Map param) {
		sqlSession.update(NAMESPACE + "updateAppFormFile",param);
	}
	
	public Map findAppformCont(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findAppformCont",param);
	}
	
	public void updateAppFormCont(Map param) {
		sqlSession.update(NAMESPACE + "updateAppFormCont",param);
	}
	
	public String getAttDocFormSeq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getAttDocFormSeq",param);
	}
	
	public void insertAttFormHistory(Map param) {
		sqlSession.insert(NAMESPACE + "insertAttFormHistory",param);
	}
	
	public List getAppList(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAppList",param);
	}

}
