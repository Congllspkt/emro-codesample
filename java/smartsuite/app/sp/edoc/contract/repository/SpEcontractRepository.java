package smartsuite.app.sp.edoc.contract.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class SpEcontractRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "sp-econtract.";
	
	public List getCntrAppFormListInSts(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCntrAppFormListInSts", param);
	}
	
	public String getSignCheck(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getSignCheck", param);
	}
	
	public Map cntrView(Map param) {
		return sqlSession.selectOne(NAMESPACE + "cntrView", param);
	}

	public Map getSignCntrContent(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getSignCntrContent", param);
	}
	
	public void updateBeforeSignContent(Map param) {
		sqlSession.update(NAMESPACE + "updateBeforeSignContent", param);
	}
	
	public String getVdBizRegNo() {
		return sqlSession.selectOne(NAMESPACE + "getVdBizRegNo");
	}
	
	public Map findCertManagerInfo() {
		return sqlSession.selectOne(NAMESPACE + "findCertManagerInfo");
	}
	
	public void insertSignValue(Map param) {
		sqlSession.insert(NAMESPACE + "insertSignValue", param);
	}
	
	public String getVendorsSignState(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getVendorsSignState", param);
	}
	
	public void updateCntrFileHash(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrFileHash", param);
	}
	
	public void deleteSignValue(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSignValue", param);
	}
	
	public List getAppContent(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAppContent", param);
	}

	public Map getAppData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getAppData", param);
	}
	
	public List findListCntrItem(Map param) {
		return sqlSession.selectList(NAMESPACE + "findListCntrItem", param);
	}
	
	public List getAttrNum(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAttrNum", param);
	}
	
	public String checkAttFileYn(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkAttFileYn", param);
	}
	
	public void updateAppFormInSts(Map param) {
		sqlSession.update(NAMESPACE + "updateAppFormInSts", param);
	}
	
	public void updateAppFormFile(Map param) {
		sqlSession.update(NAMESPACE + "updateAppFormFile", param);
	}

	public List selectCheckedSignForOwner(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectCheckedSignForOwner", param);
	}
	
	public List selectCheckedSignForVendor(Map param) {
		return sqlSession.selectList(NAMESPACE + "selectCheckedSignForVendor", param);
	}
	
	public String getSignValue(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getSignValue", param);
	}

	public Map getNonStandardSignContent(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getNonStandardSignContent", param);
	}
	
	public int findSignLockCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSignLockCnt", param);
	}
	
	public String findSignLock(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSignLock", param);
	}
	
	public void updateSignLock(Map param) {
		sqlSession.update(NAMESPACE + "updateSignLock", param);
	}
	
	public int findSignPdfFileCnt(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findSignPdfFileCnt", param);
	}

	public Map getCntrContent(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCntrContent", param);
	}
	
	public void updateAttachDocumentMgt(Map param) {
		sqlSession.update(NAMESPACE + "updateAttachDocumentMgt", param);
	}
	
	public void updateAttachDocument(Map param) {
		sqlSession.update(NAMESPACE + "updateAttachDocument", param);
	}

	public String getEcntrUUID(String cntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getEcntrUUID", cntrUUID);
	}

	public String getCntrUUID(String EcntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getCntrUUID", EcntrUUID);
	}

	public Map findContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContract", param);
	}
	
	public boolean checkSignableContractor(Map param) {
		return sqlSession.selectOne(NAMESPACE + "checkSignableContractor", param);
	}

	public void updateVendorSignYN(Map param) {
		sqlSession.update(NAMESPACE + "updateVendorSignYN", param);
	}

	public void updateAllSignN(Map param) {
		sqlSession.update(NAMESPACE + "updateAllSignN", param);
	}

	public void updateSignPdfNull(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrFileHash", param);
	}

}
