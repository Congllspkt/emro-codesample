package smartsuite.app.bp.edoc.contract.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings ({ "rawtypes", "unchecked" })
@Service
@Transactional
public class EcontractRepository {

	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "econtract.";
	
	public Map getBasicVdInfo(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getBasicVdInfo", param);
	}
	
	public void insertCntrMaster(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrMaster", param);
	}
	
	public void insertContractor(Map param) {
		sqlSession.insert(NAMESPACE + "insertContractor", param);
	}
	
	public void insertCntrContent(Map param) {
		sqlSession.insert(NAMESPACE + "insertCntrContent", param);
	}
	
	public List getCntrAppFormListInSts(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCntrAppFormListInSts", param);
	}
	
	public void updateCntrFileHash(Map param) {
		sqlSession.update(NAMESPACE + "updateCntrFileHash", param);
	}
	
	public void insertSignValue(Map param) {
		sqlSession.insert(NAMESPACE + "insertSignValue", param);
	}
	
	public Map getContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getContract", param);
	}

	public Map cntrView(Map param) {
		return sqlSession.selectOne(NAMESPACE + "cntrView", param);
	}
	
	public void deleteContractor(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContractor", param);
	}
	
	public void deleteContractDocument(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContractDocument", param);
	}
	
	public void deleteAttachDocument(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAttachDocument", param);
	}
	
	public void deleteAttachDocumentMgt(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAttachDocumentMgt", param);
	}
	
	public void deleteAttachDocumentByAppxId(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAttachDocumentByAppxId", param);
	}
	
	public void deleteAttachDocumentMgtByAppxId(Map param) {
		sqlSession.delete(NAMESPACE + "deleteAttachDocumentMgtByAppxId", param);
	}
	
	public void deleteSignatureValue(Map param) {
		sqlSession.delete(NAMESPACE + "deleteSignatureValue", param);
	}
	
	public void deleteContract(Map param) {
		sqlSession.delete(NAMESPACE + "deleteContract", param);
	}
	
	public Map getAppData(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getAppData", param);
	}
	
	public void updateAttachDocumentMgt(Map param) {
		sqlSession.update(NAMESPACE + "updateAttachDocumentMgt", param);
	}
	
	public void updateAttachDocument(Map param) {
		sqlSession.update(NAMESPACE + "updateAttachDocument", param);
	}
	
	public Map getCntrContent(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getCntrContent", param);
	}
	
	public void updateContractDocument(Map param) {
		sqlSession.update(NAMESPACE + "updateContractDocument", param);
	}

	public Map appFormView(Map param) {
		return sqlSession.selectOne(NAMESPACE + "appFormView", param);
	}
	
	public void insertApp(Map param) {
		sqlSession.insert(NAMESPACE + "insertApp", param);
	}
	
	public void insertAppContent(Map param) {
		sqlSession.insert(NAMESPACE + "insertAppContent", param);
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

	public void udpateCntrEtcFile(Map param) {
		sqlSession.update(NAMESPACE + "udpateCntrEtcFile", param);
	}
	
	public List getAppContent(Map param) {
		return sqlSession.selectList(NAMESPACE + "getAppContent", param);
	}
	
	public void updateBeforeSignContent(Map param) {
		sqlSession.update(NAMESPACE + "updateBeforeSignContent", param);
	}
	
	public String getCntrNo(String refCd) {
		return sqlSession.selectOne(NAMESPACE + "getCntrNo", refCd);
	}
	
	public String getCntrRev(String cntrNo) {
		return sqlSession.selectOne(NAMESPACE + "getCntrRev", cntrNo);
	}
	
	public Map getNonStandardSignContent(Map param) {
		return sqlSession.selectOne(NAMESPACE + "getNonStandardSignContent", param);
	}
	
	public List getCntrAppList(Map param) {
		return sqlSession.selectList(NAMESPACE + "getCntrAppList", param);
	}

	public List mappingVendorList(Map param) {
		return sqlSession.selectList(NAMESPACE + "mappingVendorList", param);
	}
	
	public List getSupplierList(Map param) {
		return sqlSession.selectList(NAMESPACE + "getSupplierList", param);
	}
	
	public void updateAppSortSeq(Map param) {
		sqlSession.update(NAMESPACE + "updateAppSortSeq", param);
	}
	
	public Map findDocumentConts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findDocumentConts", param);
	}

	public Map findContract(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findContract", param);
	}

	public String getEcntrUUID(String cntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getEcntrUUID", cntrUUID);
	}

	public String getCntrUUID(String ecntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getCntrUUID", ecntrUUID);
	}
	
	public String getSignState(String ecntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getSignState", ecntrUUID);
	}

	public String getSignOrderByCntrId(String ecntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getSignOrderByCntrId", ecntrUUID);
	}

	public void updateTemplateUnusedCntrdoc(Map param) {
		sqlSession.update(NAMESPACE + "updateTemplateUnusedCntrdoc", param);
	}
	public void updateBuyerSignYN(Map param) {
		sqlSession.update(NAMESPACE + "updateBuyerSignYN", param);
	}

	public void updateSgncmpldAthgUuid(Map param) {
		sqlSession.update(NAMESPACE + "updateSgncmpldAthgUuid", param);
	}

	public String findWdPossYnByLckdSts(Map param) {
		return sqlSession.selectOne(NAMESPACE + "findWdPossYnByLckdSts", param);
	}

	public String getCntrPdfList(String cntrUUID) {
		return sqlSession.selectOne(NAMESPACE + "getCntrPdfList", cntrUUID);
	}
}
