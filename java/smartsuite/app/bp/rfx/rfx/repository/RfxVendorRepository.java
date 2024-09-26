package smartsuite.app.bp.rfx.rfx.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
public class RfxVendorRepository {

	@Inject
	SqlSession sqlSession;

	private static final String NAMESPACE = "rfx-vendor.";
	
	/**
	 * RFx 업체 신규생성
	 * @param rfxVendor
	 */
	public void insertRfxVendor(Map rfxVendor) {
		sqlSession.insert(NAMESPACE + "insertRfxVendor", rfxVendor);
	}
	
	/**
	 * RFx 업체 수정
	 * @param rfxVendor
	 */
	public void updateRfxVendor(Map rfxVendor) {
		sqlSession.update(NAMESPACE + "updateRfxVendor", rfxVendor);
	}
	
	/**
	 * RFx 업체 삭제
	 * @param rfxVendor
	 */
	public void deleteRfxVendor(Map rfxVendor) {
		sqlSession.delete(NAMESPACE + "deleteRfxVendor", rfxVendor);
	}

	public void deleteRcmndVendorByRfxVendor(Map rfxVendor) {
		sqlSession.delete(NAMESPACE + "deleteRcmndVendorByRfxVendor", rfxVendor);
	}

	/**
	 * 추천업체 매핑 저장
	 * @param rcmdInfo
	 */
	public void insertRcmndVendor(Map rcmdInfo) {
		sqlSession.insert(NAMESPACE + "insertRcmndVendor", rcmdInfo);
	}

	/**
	 * 견적대상업체 조회
	 * @param param
	 */
	public List searchRfxVendorByRfx(Map param) {
		return sqlSession.selectList(NAMESPACE + "searchRfxVendorByRfx", param);
	}
    
    /**
     * 신규 협력사 초청 저장
     *
     * @param param
     */
    public void saveInviteNewVendor(Map param) {
        sqlSession.insert(NAMESPACE + "saveInviteNewVendor", param);
    }
    
    /**
     * rfx 초대 신규 협력사 조회
     *
     * @param param
     * @return
     */
    public List searchRfxInviteNewVendor(Map param) {
        return sqlSession.selectList(NAMESPACE + "searchRfxInviteNewVendor", param);
    }
    
    /**
     * rfx 업체 중 신규 협력사 조회
     *
     * @param rfxInfo
     * @return
     */
    public List searchRfxNewVendor(Map rfxInfo) {
        return sqlSession.selectList(NAMESPACE + "searchRfxNewVendor", rfxInfo);
    }
}
