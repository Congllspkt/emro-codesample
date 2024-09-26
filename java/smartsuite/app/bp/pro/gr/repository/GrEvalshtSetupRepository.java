package smartsuite.app.bp.pro.gr.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
public class GrEvalshtSetupRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "gr-evalsht-setup.";
	
	public List<Map> findListGrEvalshtHis(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGrEvalshtHis", param);
	}
	
	public Map findGrEvalshtInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findGrEvalshtInfo", param);
	}
	
	public List<Map> findGrEvalshtInfoPrcs(Map grEvalshtInfo) {
		return sqlSession.selectList(NAMESPACE + "findGrEvalshtInfoPrcs", grEvalshtInfo);
	}
	
	public void insertGrEvalsht(Map grEvalshtInfo) {
		sqlSession.insert(NAMESPACE + "insertGrEvalsht", grEvalshtInfo);
	}
	
	public void updateGrEvalsht(Map grEvalshtInfo) {
		sqlSession.update(NAMESPACE + "updateGrEvalsht", grEvalshtInfo);
	}
	
	public void insertGrEvalshtPrcs(Map row) {
		sqlSession.insert(NAMESPACE + "insertGrEvalshtPrcs", row);
	}
	
	public void updateGrEvalshtPrcs(Map row) {
		sqlSession.update(NAMESPACE + "updateGrEvalshtPrcs", row);
	}
	
	public void copyGrEvalsht(Map grEvalshtInfo) {
		sqlSession.insert(NAMESPACE + "copyGrEvalsht", grEvalshtInfo);
	}
	
	public void copyGrEvalshtPrcs(Map grEvalshtInfoPrcs) {
		sqlSession.insert(NAMESPACE + "copyGrEvalshtPrcs", grEvalshtInfoPrcs);
	}
	
	public void copyListGrFactChrGrpEvaltr(Map grEvalshtInfoPrcs) {
		sqlSession.insert(NAMESPACE + "copyListGrFactChrGrpEvaltr", grEvalshtInfoPrcs);
	}
	
	public void updateGrEvalshtStsByDelete(Map param) {
		sqlSession.update(NAMESPACE + "updateGrEvalshtStsByDelete", param);
	}
	
	public void updateEvaltrByDelete(Map grEvalshtInfoPrcs) {
		sqlSession.update(NAMESPACE + "updateEvaltrByDelete", grEvalshtInfoPrcs);
	}
	
	public void insertGrEvalshtPrcsEvaltr(Map row) {
		sqlSession.insert(NAMESPACE + "insertGrEvalshtPrcsEvaltr", row);
	}
	
	public void updateGrEvalshtPrcsEvaltr(Map row) {
		sqlSession.update(NAMESPACE + "updateGrEvalshtPrcsEvaltr", row);
	}
	
	public void updateGrEvalshtPrcsEvaltrByDelete(Map row) {
		sqlSession.update(NAMESPACE + "updateGrEvalshtPrcsEvaltrByDelete", row);
	}
	
	public List<String> findListEvaltmplUuidOfGrEvalshtPrcses(Map grEvalshtInfo) {
		return sqlSession.selectList(NAMESPACE + "findListEvaltmplUuidOfGrEvalshtPrcses", grEvalshtInfo);
	}
	
	public void updatePrevGrEvalsht(Map grEvalshtInfo) {
		sqlSession.update(NAMESPACE + "updatePrevGrEvalsht", grEvalshtInfo);
	}
	
	public void updatePrevEvaltmplUuidGrEvalshtPrcs(Map grEvalshtInfo) {
		sqlSession.update(NAMESPACE + "updatePrevEvaltmplUuidGrEvalshtPrcs", grEvalshtInfo);
	}
	
	public void updateCnfdYnGrEvalsht(Map grEvalshtInfo) {
		sqlSession.update(NAMESPACE + "updateCnfdYnGrEvalsht", grEvalshtInfo);
	}
	
	public FloaterStream findListGrEvalsht(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findListGrEvalsht", param);
	}
	
	public void copyGrEvalshtPrcsEvaltrByDefaultGemgQlyPic(Map grEvalshtInfo) {
		sqlSession.insert(NAMESPACE + "copyGrEvalshtPrcsEvaltrByDefaultGemgQlyPic", grEvalshtInfo);
	}
	
	public List findListGrEvalshtPrcsEvaltr(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListGrEvalshtPrcsEvaltr", param);
	}
	
	public void saveMappingEvaltmplUuidToGrEvalshtPrcs(Map data) {
		sqlSession.update(NAMESPACE + "saveMappingEvaltmplUuidToGrEvalshtPrcs", data);
	}

	public List findEvalTmplStsInGeEvalSht(Map param) {
		return sqlSession.selectList(NAMESPACE + "findEvalTmplStsInGeEvalSht", param);
	}
}
