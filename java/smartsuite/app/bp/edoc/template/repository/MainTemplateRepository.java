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
public class MainTemplateRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "main-template.";
	
	public FloaterStream largeFindListCntrForm(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCntrForm",param);
	}
	
	public int checkCntrFormName(Map param) {
		return sqlSession.selectOne( NAMESPACE + "checkCntrFormName",param);
	}
	
	public void updateCntrRepForm(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrRepForm", param);
	}
	
	public void insertCntrForm(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrForm", param);
	}
	
	public void insertCntrFormCont(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrFormCont", param);
	}
	
	public void deleteCntrFormHistory(Map param) {
		sqlSession.delete(NAMESPACE + "deleteCntrFormHistory", param);
	}
	
	public void deleteCntrFormCont(Map param) {
		sqlSession.delete(NAMESPACE + "deleteCntrFormCont", param);
	}
	
	public void deleteCntrFrom(Map param) {
		sqlSession.delete(NAMESPACE + "deleteCntrFrom", param);
	}
	
	public List<Map<String,Object>> findListCntrFormHistory(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrFormHistory", param);
	}
	
	public Map findFormCont(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findFormCont",param);
	}
	
	public Map findCntrFormCont(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrFormCont",param);
	}
	
	public void updateCntrFormCont(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrFormCont",param);
	}
	
	public String getCntrDocFormSeq(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCntrDocFormSeq",param);
	}
	
	public void insertCntrFormHistory(Map param) {
		sqlSession.selectOne(NAMESPACE + "insertCntrFormHistory",param);
	}
	
	public void deleteCntrAppForm(Map param) {
		sqlSession.delete(NAMESPACE + "deleteCntrAppForm", param);
	}
	
	public List<Map<String,Object>> findListCntrAppForm(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrAppForm",param);
	}
	
	public void insertCntrAppxTmpl(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrAppxTmpl",param);
	}
	
	public void saveListSortSeqAppForm(Map param) {
		sqlSession.update(NAMESPACE + "saveListSortSeqAppForm",param);
	}
	
	public Map cntrFormView(Map param) {
		return sqlSession.selectOne(NAMESPACE + "cntrFormView",param);
	}
	
	public List findCntrdocFormList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findCntrdocFormList",param);
	}

	public FloaterStream largeFindListCntrTmpl(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListCntrTmpl",param);
	}
	
	public void updateCntrAppxTmpl(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrAppxTmpl", param);
	}

	public List<Map<String,Object>> findListCntrForm(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrForm",param);
	}
	
}
