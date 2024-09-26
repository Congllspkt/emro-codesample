package smartsuite.app.sp.workplace.task.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpWorkTaskRepository {

	@Inject
	SqlSession sqlSession;

	public static final String NAMESPACE = "sp-work-task.";

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
	public List<Map<String, Object>> findListRfxEvalSubjEvaltrRes(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListRfxEvalSubjEvaltrRes", param);
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
	 * @return PO list
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
	 * INHD list 조회
	 *
	 * @param param {cntr_uuids}
	 * @return INHD list
	 */
	public List<Map<String, Object>> findListINHD(Map<String, Object> param) {
		return sqlSession.selectList(NAMESPACE + "findListINHD", param);
	}

	/**
	 * INHD 조회
	 *
	 * @param param {cntr_uuids or cntr_uuid}
	 * @return INHD
	 */
	public Map<String, Object> findINHD(Map<String, Object> param) {
		return sqlSession.selectOne(NAMESPACE + "findINHD", param);
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
}
