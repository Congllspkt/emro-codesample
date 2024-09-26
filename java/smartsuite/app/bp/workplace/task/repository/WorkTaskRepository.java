package smartsuite.app.bp.workplace.task.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class WorkTaskRepository {

	@Inject
	SqlSession sqlSession;

	public static final String NAMESPACE = "work-task.";

	/**
	 * PR 조회
	 *
	 * @param param {pr_uuid}
	 * @return PR
	 */
	public Map<String, Object> findPr(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPr", param);
	}

	/**
	 * PR ITEM 리스트 조회
	 *
	 * @param param {pr_item_uuids or pr_uuid or pr_item_uuid}
	 * @return PR ITEM list
	 */
	public List<Map<String, Object>> findListPrItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPrItem", param);
	}

	/**
	 * RFI 조회
	 *
	 * @param param {rfi_uuid}
	 * @return RIHD
	 */
	public Map<String, Object> findRfi(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfi", param);
	}

	/**
	 * Rfi Vendor 리스트 조회
	 *
	 * @param param {rfi_uuid}
	 * @return Rfi Vendor list
	 */
	public List<Map<String, Object>> findListRfiVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListRfiVendor", param);
	}

	/**
	 * Rfi Vendor 조회
	 *
	 * @param param {rfi_vd_uuid}
	 * @return Rfi Vendor
	 */
	public Map<String, Object> findRfiVendor(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfiVendor", param);
	}

	/**
	 * RFX 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFX
	 */
	public Map<String, Object> findRfx(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfx", param);
	}

	public Map<String, Object> findRfxReceipt(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxReceipt", param);
	}

	/**
	 * RFx Vendor list 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFx Vendor list
	 */
	public List<Map<String, Object>> findListRfxVendor(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxVendor", param);
	}

	/**
	 * RFx Vendor 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFx Vendor
	 */
	public Map<String, Object> findRfxVendor(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRfxVendor", param);
	}

	/**
	 * RFx EVAL_SUBJ_EVALTR_RES list 조회
	 *
	 * @param param {rfx_uuid}
	 * @return RFX EVAL_SUBJ_EVALTR_RES list
	 */
	public List<Map<String, Object>> findListNpeEvalSubjEvaltrRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListNpeEvalSubjEvaltrRes", param);
	}

	/**
	 * EVAL_SUBJ_EVALTR_RES 조회
	 *
	 * @param param {eval_subj_evaltr_res_uuid}
	 * @return EVAL_SUBJ_EVALTR_RES
	 */
	public Map<String, Object> findEvalSubjEvaltrRes(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findEvalSubjEvaltrRes", param);
	}

	/**
	 * PO list 조회
	 *
	 * @param param {po_uuids or po_uuid}
	 * @return Po hd list
	 */
	public List<Map<String, Object>> findListPo(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPo", param);
	}

	/**
	 * PO ITEM list 조회
	 *
	 * @param param {po_item_uuids or po_uuids or gr_uuid or asn_uuid or po_uuid}
	 * @return PO ITEM list
	 */
	public List<Map<String, Object>> findListPoItem(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListPoItem", param);
	}

	/**
	 * 이전차수 PO 조회
	 *
	 * @param param {po_uuid}
	 * @return 이전차수 PO
	 */
	public Map<String, Object> findPrevPo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPrevPo", param);
	}

	/**
	 * CNTR list 조회
	 *
	 * @param param {cntr_uuids}
	 * @return INHD list
	 */
	public List<Map<String, Object>> findListCntr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListCntr", param);
	}

	/**
	 * CNTR 조회
	 *
	 * @param param {cntr_uuids or cntr_uuid}
	 * @return INHD
	 */
	public Map<String, Object> findCntrByReq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findCntrByReq", param);
	}

	/**
	 * ASN 조회
	 *
	 * @param param {asn_uuid or gr_uuid}
	 * @return ASN
	 */
	public Map<String, Object> findAsn(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findAsn", param);
	}

	/**
	 * GRHD 조회
	 *
	 * @param param {gr_uuid}
	 * @return
	 */
	public Map<String, Object> findGr(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findGr", param);
	}

	/**
	 * GR EVAL_SUBJ_EVALTR_RES list 조회
	 *
	 * @param param {gr_no, gr_rev}
	 * @return EVAL_SUBJ_EVALTR_RES list
	 */
	public List<Map<String, Object>> findListGrEvalSubjEvaltrRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListGrEvalSubjEvaltrRes", param);
	}

	/**
	 * GR EVAL_SUBJ_EVALTR_RES list 조회
	 *
	 * @param param {eval_typ_cd, eval_kind_cd, gr_no, gr_rev, evaltr_uuid}
	 * @return
	 */
	public List<Map<String, Object>> findListGrEvalSubjEvaltrResByEvaltr(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListGrEvalSubjEvaltrResByEvaltr", param);
	}

	public void errorTest(Map<String, Object> param) {
		sqlSession.selectOne(NAMESPACE + "errorTest", param);
	}

	/**
	 * Request Contract 조회 (rfx 후속 프로세스)
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findRequestContract(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findRequestContract", param);
	}

	/**
	 * Contract By Request 조회 (rfx 후속 프로세스)
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findContractByReq(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findContractByReq", param);
	}

	/**
	 * RFX Request 조회
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findReqRfx(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReqRfx", param);
	}

	/**
	 * Po Request 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findReqPo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReqPo", param);
	}

	/**
	 * RFX 요청 데이터 적재 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListReceiptReqRfx(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListReceiptReqRfx", param);
	}

	/**
	 * RFX 요청 데이터 조회
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findListReqRfx(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findListReqRfx", param);
	}

	public List<Map<String, Object>> findListReqRfxByReq(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListReqRfxByReq", param);
	}

	public Map<String, Object> findReqRfxByUuid(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findReqRfxByUuid", param);
	}

	public Map<String, Object> findPoInfo(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findPoInfo", param);
	}
}
