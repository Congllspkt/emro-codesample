package smartsuite.app.bp.law.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class LawRepository {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "law.";
	
	/**
	 * 계약검토요청 목록 조회
	 */
	public FloaterStream largeFindReviewReqList(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findReviewReqList", param);
	}
	
	/**
	 * 검토건 저장
	 */
	public void insertReqReview(Map param) {
		sqlSession.insert(NAMESPACE + "insertReqReview", param);
	}
	
	/**
	 * 참조자 리스트 저장
	 */
	public void insertRef(Map param) {
		sqlSession.insert(NAMESPACE + "insertRef", param);
	}
	
	/**
	 * 계약검토 상세 조회
	 */
	public Map findReqReviewDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findReqReviewDetail", param);
	}
	
	/**
	 * 참조자 리스트 조회
	 */
	public List findRefList(Map param) {
		return sqlSession.selectList(NAMESPACE + "findRefList", param);
	}
	
	/**
	 * 계약검토 수정
	 */
	public void updateReqReview(Map param) {
		sqlSession.update(NAMESPACE + "updateReqReview", param);
	}
	
	/**
	 * 참조자 삭제
	 */
	public void deleteRefList(Map param) {
		sqlSession.delete(NAMESPACE + "deleteRefList", param);
	}
	
	/**
	 * 계약검토 삭제
	 */
	public void delReqReview(Map param) {
		sqlSession.delete(NAMESPACE + "delReqReview", param);
	}
	
	/**
	 * ROLE_CD에 해당하는 사용자 정보 조회
	 */
	public List findUserByRole(Map param) {
		return sqlSession.selectList(NAMESPACE + "findUserByRole", param);
	}
	
	/**
	 * 계약 검토자 지정
	 */
	public void regReviewer(Map param) {
		sqlSession.update(NAMESPACE + "regReviewer", param);
	}
	
	/**
	 * 검토진행상태 업데이트
	 */
	public void updateRvSts(Map param) {
		sqlSession.update(NAMESPACE + "updateRvSts", param);
	}
	
	/**
	 * 검토의견 업데이트
	 */
	public void updateRvOpn(Map param) {
		sqlSession.update(NAMESPACE + "updateRvOpn", param);
	}
	
	/**
	 * 팀장의견 업데이트
	 */
	public void updateTmldRvOpn(Map param) {
		sqlSession.update(NAMESPACE + "updateTmldRvOpn", param);
	}
	
	/**
	 * 참조자 계약검토목록 조회
	 */
	public FloaterStream largeFindReviewReqListByRef(Map param) {
		return new QueryFloaterStream(sqlSession, NAMESPACE + "findReviewReqListByRef", param);
	}
	
	/**
	 * 계약건 조회
	 */
	public List getCntrListByRvUuid(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCntrListByRvUuid", param);
	}
	
	public List findReqReviewCntrDocTmpl(Map param) {
		return sqlSession.selectList(NAMESPACE + "findReqReviewCntrDocTmpl", param);
	}
	
	public List findReqReviewAppxTmpl(Map param) {
		return sqlSession.selectList(NAMESPACE + "findReqReviewAppxTmpl", param);
	}
	
	public Map findReqReviewTmplCont(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findReqReviewTmplCont", param);
	}
	
	public void insertReqReviewTmpl(Map param) {
		sqlSession.insert(NAMESPACE + "insertReqReviewTmpl", param);
	}

	public void updateReqReviewTmpl(Map param) {
		sqlSession.update(NAMESPACE + "updateReqReviewTmpl", param);
	}
	
	public void updateReqReviewTmplCont(Map param) {
		sqlSession.update(NAMESPACE + "updateReqReviewTmplCont", param);
	}
	
	public void updateReqReviewTmplOpn(Map param) {
		sqlSession.update(NAMESPACE + "updateReqReviewTmplOpn", param);
	}
	
	public void deleteReqReviewTmplRvId(Map param) {
		sqlSession.delete(NAMESPACE + "deleteReqReviewTmplRvId", param);
	}
	
	public void deleteReqReviewTmplByRvTmplId(Map param) {
		sqlSession.delete(NAMESPACE + "deleteReqReviewTmplByRvTmplId", param);
	}

	public List<Map<String, Object>> findListReviewAppxTmpl(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListReviewAppxTmpl", param);
	}
}
