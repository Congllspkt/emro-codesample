package smartsuite.app.bp.pro.pp.validator;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.admin.validator.ValidatorUtil;
import smartsuite.app.bp.pro.shared.ProConst;
import smartsuite.app.bp.pro.shared.validator.ProValidator;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.Map;

@Service
public class ProgressPaymentValidator extends ProValidator {
	
	@Inject
	SqlSession sqlSession;
	
	private static final String NAMESPACE = "pp.";
	
	private static final String ASN_NAMESPACE = "asn.";
	
	private static final String PO_NAMESPACE = "po.";
	
	@Override
	public ResultMap validate(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		
		String grUuid = param.get("gr_uuid") == null ? null : param.get("gr_uuid").toString();
		String asnUuid = param.get("asn_uuid") == null ? null : param.get("asn_uuid").toString();
		String poId = param.get("po_uuid") == null ? null : param.get("po_uuid").toString();
		String prePayYn = param.get("apymt_yn") == null ? "N" : param.get("apymt_yn").toString();
		
		// 선급금 AR 없이 저장하는 경우
		if("Y".equals(prePayYn) && !Strings.isNullOrEmpty(poId) && Strings.isNullOrEmpty(asnUuid)) {
			// PO 상태를 체크 (선급금 등록 가능여부)
			resultMap = checkPoStatus(param);
		} else if("N".equals(prePayYn) && !Strings.isNullOrEmpty((poId)) && Strings.isNullOrEmpty(asnUuid)) {
			resultMap = checkCreatableProgressPayment(param);
		}
		
		if(StringUtils.isNotBlank(resultMap.getResultStatus()) && !ResultMap.STATUS.SUCCESS.equals(resultMap.getResultStatus())) {
			return resultMap;
		}
		
		// AR 건으로 신규 기성 생성 또는 기성반려 시
		if(Strings.isNullOrEmpty(grUuid)) {
			if(!Strings.isNullOrEmpty(asnUuid)) {
				// AR 상태 체크
				Map<String, Object> checkResult = sqlSession.selectOne(ASN_NAMESPACE + "compareAsnHdSts", param);
				
				resultMap = ValidatorUtil.getResultMapByCheckResult(param, checkResult);
			}
		} else {
			// UPDATE 시 또는 선급금 DELETE 시 : 화면에서 조회한 시점의 PO 진행상태값과 현재 DB에서의 진행상태값을 비교
			Map<String, Object> checkResult = sqlSession.selectOne(NAMESPACE + "compareGrHdSts", param);
			
 			resultMap = ValidatorUtil.getResultMapByCheckResult(param, checkResult);
		}
		return resultMap;
	}
	
	/**
	 * 선급금 작성 가능여부 체크
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkPoStatus(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		
		// PO 상태 체크
		Map<String, Object> invalidPo = sqlSession.selectOne(PO_NAMESPACE + "checkAdvancePaymentRequestable", param);
		
		if(invalidPo == null) {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			resultMap.setResultStatus(ProConst.PO_STATE_ERR);
			resultMap.setResultData(invalidPo);
		}
		return resultMap;
	}
	/**
	 * 기성 작성 가능여부 체크
	 *
	 * @param param
	 * @return
	 */
	private ResultMap checkCreatableProgressPayment(Map<String, Object> param) {
		ResultMap resultMap = ResultMap.getInstance();
		
		// PO 상태 체크
		Map<String, Object> invalidPo = sqlSession.selectOne(PO_NAMESPACE + "checkCreatableProgressPayment", param);
		
		if(invalidPo == null) {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			resultMap.setResultStatus(ProConst.PO_STATE_ERR);
			resultMap.setResultData(invalidPo);
		}
		return resultMap;
	}
	
}
