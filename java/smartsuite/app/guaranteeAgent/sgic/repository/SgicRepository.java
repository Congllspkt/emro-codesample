package smartsuite.app.guaranteeAgent.sgic.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SgicRepository {
	
	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "elecGuarantee.";
	
	/**
	 * 보증종류 확인
	 */
	public String getBondType(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getBondType", param);
	}
	
	/**
	 * 보증서 상세조회
	 */
	public Map getPayBondInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getPayBondInfo", param);
	}
	
	/**
	 * 채권자 담당자 정보조회
	 */
	public Map getCredInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCredInfo", param);
	}
	
	/**
	 * 지급이행보증용 선급금 목록 조회
	 */
	public List getPrePaymentList(Map param) { return sqlSession.selectList(NAMESPACE + "getPrePaymentList", param); }
	
	/**
	 * 보증서 상세조회
	 */
	public Map getBondInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getBondInfo", param);
	}
	
	/**
	 * 보증서 조회
	 */
	public int getCountEgurdoc(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCountEgurdoc", param);
	}
	
	/**
	 * 보증 신청정보 저장
	 */
	public void saveInsuranceForm(Map param) {
		sqlSession.insert(NAMESPACE+"saveInsuranceForm", param);
	}
	
	/**
	 * 보증 진행상태 변경
	 */
	public void updateCtgrSts(Map param) {
		sqlSession.update(NAMESPACE+"updateCtgrSts", param);
	}
	
	/**
	 * 보증보험 취소
	 */
	public Map cancelEgur(Map param) {
		return sqlSession.selectOne(NAMESPACE + "cancelEgur", param);
	}
	
	/**
	 * 최종응답서 저장
	 */
	public void saveFinalResponse(Map param) {
		sqlSession.insert(NAMESPACE + "saveFinalResponse", param);
	}
	
	/**
	 * 통보서 삭제
	 */
	public void deleteNofndoc(Map param) {
		sqlSession.delete(NAMESPACE + "deleteNofndoc", param);
	}
	
	/**
	 * 보증서 삭제
	 */
	public void deleteEgurdoc(Map param) {
		sqlSession.delete(NAMESPACE + "deleteEgurdoc", param);
	}
	
	/**
	 * 보증보험 승인
	 */
	public Map approveEgur(Map param) {
		return sqlSession.selectOne(NAMESPACE + "approveEgur", param);
	}
	
	/**
	 * 보증보험 반려
	 */
	public Map rejectEgur(Map param) {
		return sqlSession.selectOne(NAMESPACE + "rejectEgur", param);
	}
	
	/**
	 * 보증보험 파기
	 */
	public Map destroyEgur(Map param) {
		return sqlSession.selectOne(NAMESPACE + "destroyEgur", param);
	}
	
	/**
	 * 보낸 통보서가 있는지 확인
	 */
	public int checkGuarNofndoc(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkGuarNofndoc", param);
	}
	
	/**
	 * 수신받은 보증서가 있는지 확인
	 */
    public int checkCntEgur(Map param) {
    	return sqlSession.selectOne(NAMESPACE + "checkCntEgur", param);
    }
	
	/**
	 * 보증보험 조회
	 */
    public Map getEgurInfo(Map param) {
    	return sqlSession.selectOne(NAMESPACE + "getEgurInfo", param);
    }
    
    /**
     * 보증서 삭제
     */
    public void deleteRecvEgurdoc(Map param) {
    	sqlSession.delete(NAMESPACE + "deleteRecvEgurdoc", param);
    }
    
    /**
     * 보증서 내용 저장
     */
    public void insertEgurdoc(Map param) {
    	sqlSession.insert(NAMESPACE + "insertEgurdoc", param);
    }
    
    /** 
     * 보증신청 테이블 상태값 변경
     */
    public void updateCtgr(Map param) {
		sqlSession.update(NAMESPACE + "updateCtgr", param);
    }
	/**
	 * 보증신청 테이블 상태값 변경
	 */
	public Map getGuarDetail(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getGuarDetail", param);
	}

}
