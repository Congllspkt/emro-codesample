package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class GrEvalSetupRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "gr-eval-setup.";
	
	public List findListGemt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGemt", param);
	}
	
	public void insertGemt(Map insert) {
		sqlSession.insert(NAMESPACE + "insertGemt", insert);
	}
	
	public void updateGemt(Map update) {
		sqlSession.update(NAMESPACE + "updateGemt", update);
	}
	
	public void deleteGemt(Map delete) {
		sqlSession.delete(NAMESPACE + "deleteGemt", delete);
	}
	
	public boolean isExistsGemtByOorgAndCode(Map insert) {
		return sqlSession.selectOne(NAMESPACE + "isExistsGemtByOorgAndCode", insert);
	}
	
	public boolean isExistGemtAtGeg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isExistGemtAtGeg", param);
	}
	
	public boolean isExistGemtAtGemg(Map param) {
		return sqlSession.selectOne(NAMESPACE + "isExistGemtAtGemg", param);
	}
	
	public List findListOorgGemt(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListOorgGemt", param);
	}
	
	public List findListGemg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGemg", param);
	}
	
	public void insertGemg(Map insert) {
		sqlSession.insert(NAMESPACE + "insertGemg", insert);
	}
	
	public void updateGemg(Map update) {
		sqlSession.update(NAMESPACE + "updateGemg", update);
	}
	
	public void deleteGemg(Map delete) {
		sqlSession.update(NAMESPACE + "deleteGemg", delete);
	}
	
	public List findListGeg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGeg", param);
	}
	
	public Map findGegMaster(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGegMaster", param);
	}
	
	public List<Map> findListGegGemg(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGegGemg", param);
	}
	
	public Map findGegInfoInUsingGemg(Map row) {
		return sqlSession.selectOne(NAMESPACE + "findGegInfoInUsingGemg", row);
	}
	
	public void insertGegMaster(Map gegInfo) {
		sqlSession.insert(NAMESPACE + "insertGegMaster", gegInfo);
	}
	
	public void updateGegMaster(Map gegInfo) {
		sqlSession.update(NAMESPACE + "updateGegMaster", gegInfo);
	}
	
	public void deleteGegGemg(Map row) {
		sqlSession.delete(NAMESPACE + "deleteGegGemg", row);
	}
	
	public void insertGegGemg(Map row) {
		sqlSession.insert(NAMESPACE + "insertGegGemg", row);
	}
	
	public void updateGegByDelete(Map row) {
		sqlSession.update(NAMESPACE + "updateGegByDelete", row);
	}
	
	public void updateGegGemgStsByDelete(Map row) {
		sqlSession.update(NAMESPACE + "updateGegGemgStsByDelete", row);
	}
	
	public List findListGemgEvaltr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGemgEvaltr", param);
	}
	
	public void deleteGemgEvaltr(Map deleteInfo) {
		sqlSession.delete(NAMESPACE + "deleteGemgEvaltr", deleteInfo);
	}
	
	public void insertGemgEvaltr(Map insertInfo) {
		sqlSession.insert(NAMESPACE + "insertGemgEvaltr", insertInfo);
	}
}
