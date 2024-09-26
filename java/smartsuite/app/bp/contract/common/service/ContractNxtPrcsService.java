package smartsuite.app.bp.contract.common.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.bp.contract.common.event.ContractEventPublisher;
import smartsuite.app.bp.contract.common.repository.ContractRepository;
import smartsuite.app.bp.contract.contractcnd.factory.ContractCndFactory;
import smartsuite.app.bp.contract.contractcnd.service.ContractCndService;
import smartsuite.app.common.shared.FormatterProvider;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.shared.CntrConst;


/**
 * 계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName ContractService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ContractNxtPrcsService {

	private static final Logger LOG = LoggerFactory.getLogger(ContractNxtPrcsService.class);

	@Inject
	ContractRepository contractRepository;
	@Inject
	ContractEventPublisher contractEventPublisher;
	@Inject
	FormatterProvider formatterProvider;
	@Inject
	ContractCndFactory contractCndFactory;
	
	public void completeContract(String cntrId) {
		Map searchParam = Maps.newHashMap();
		searchParam.put("cntr_uuid", cntrId);
		Map cntrInfo = contractRepository.findContract(searchParam);
		
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");

		if(CntrConst.CNTRDOC_TYPE.ELEMENTARY.equals(cntrdocTypCcd)) {
			contractEventPublisher.completeElementaryContract(cntrInfo);
			
		} else if(CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(cntrdocTypCcd)) {
			this.requestUnitPrice(cntrInfo);
			
		} else if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrdocTypCcd)) {
			String cntrTypCcd = (String) cntrInfo.get("cntr_typ_ccd");
			Map cntrCndInfo = this.findCntrCnd(cntrInfo);
			
			if(CntrConst.CNTR_TYPE.NEW.equals(cntrTypCcd)) {
				this.requestPo(cntrInfo, cntrCndInfo);
			} else {
				contractEventPublisher.requestChangePo(cntrInfo);
			}

			if(CntrConst.CNTR_TYPE.NEW.equals(cntrTypCcd) || CntrConst.CNTR_TYPE.CHANGE.equals(cntrTypCcd)) {
				if(this.isExistsGuaranteeReqData(cntrCndInfo)) this.requestGuarantee(cntrInfo, cntrCndInfo);
			}
		}
	}

	public void terminationContract(Map param) {
		Map cntrInfo = contractRepository.findContract(param);
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");

		if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrdocTypCcd)) {
			contractEventPublisher.requestChangePo(param);
		}
	}
	
	/**
	 * 계약 조건 조회
	 * @param cntrInfo
	 * @return
	 */
	private Map findCntrCnd(Map cntrInfo) {
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");
		String cntrCndId = (String) cntrInfo.get("cntr_cnd_uuid");
		
		ContractCndService cndService = contractCndFactory.getModule(cntrdocTypCcd);
		
		Map cntrCndInfo = Maps.newHashMap();
		if(cndService != null) {
			cntrCndInfo = cndService.getTemplate(cntrCndId);
		}
		return cntrCndInfo;
	}

	/**
	 * 인지세 요청 정보 존재 여부 확인
	 * @param cntrCndInfo
	 * @return
	 */
	private boolean isExistsStampTaxReqData(Map cntrCndInfo) {
		Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");
		String staxYn = (String) purcCntrInfoData.get("stax_yn");
		if( "Y".equals(staxYn) ) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 보증보험 요청 정보 존재 여부 확인
	 * @param cntrCndInfo
	 * @return
	 */
	private boolean isExistsGuaranteeReqData(Map cntrCndInfo) {
		Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");

		String cpgurTypCcd = (String) purcCntrInfoData.get("cpgur_typ_ccd");
		String defpgurTypCcd = (String) purcCntrInfoData.get("defpgur_typ_ccd");
		String apymtgurTypCcd = (String) purcCntrInfoData.get("apymtgur_typ_ccd");

		if( CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(cpgurTypCcd)
			|| CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(defpgurTypCcd)
				|| CntrConst.GUARANTEE_KIND.GUARANTEE_INSURANCE.equals(apymtgurTypCcd)) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 보증보험 요청
	 * @param cntrInfo
	 * @param cntrCndInfo
	 */
	private void requestGuarantee(Map cntrInfo, Map cntrCndInfo) {
		Map requestGuarantee = Maps.newHashMap();

		Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");

		requestGuarantee.put("cntr_uuid", cntrInfo.get("cntr_uuid"));

		requestGuarantee.put("cpgur_typ_ccd", purcCntrInfoData.get("cpgur_typ_ccd"));
		requestGuarantee.put("cpgur_ro", purcCntrInfoData.get("cpgur_ro"));
		requestGuarantee.put("cpgur_st_dt", purcCntrInfoData.get("cpgur_st_dt"));
		requestGuarantee.put("cpgur_exp_dt", purcCntrInfoData.get("cpgur_exp_dt"));

		requestGuarantee.put("defpgur_typ_ccd", purcCntrInfoData.get("defpgur_typ_ccd"));
		requestGuarantee.put("defpgur_ro", purcCntrInfoData.get("defpgur_ro"));
		requestGuarantee.put("defpgur_pd_typ_ccd", purcCntrInfoData.get("defpgur_pd_typ_ccd"));
		requestGuarantee.put("defpgur_pd", purcCntrInfoData.get("defpgur_pd"));

		requestGuarantee.put("apymtgur_typ_ccd", purcCntrInfoData.get("apymtgur_typ_ccd"));
		requestGuarantee.put("apymtgur_ro", purcCntrInfoData.get("apymtgur_ro"));
		requestGuarantee.put("apymtgur_st_dt", purcCntrInfoData.get("apymtgur_st_dt"));
		requestGuarantee.put("apymtgur_exp_dt", purcCntrInfoData.get("apymtgur_exp_dt"));

		List<Map> purcCntrPymtExptList = (List<Map>) cntrCndInfo.get("purcCntrPymtExptList");

		for(Map purcCntrPymtExpt : purcCntrPymtExptList) {
			String pymtTypCcd = (String)purcCntrPymtExpt.get("pymt_typ_ccd");

			if(CntrConst.PAYMENT_TYPE.ADVANCE_PAYMENT.equals(pymtTypCcd)) {
				requestGuarantee.put("pymt_expt_dt", purcCntrPymtExpt.get("pymt_expt_dt"));
				requestGuarantee.put("apymt_pymt_amt", purcCntrPymtExpt.get("pymt_amt"));   // 선급금 지급 금액
				break;
			}
		}

		contractEventPublisher.requestGuarantee(requestGuarantee);
	}

	/**
	 * 단가 생성 요청
	 * @param cntrInfo
	 * @return
	 */
	private ResultMap requestUnitPrice(Map cntrInfo) {
		contractEventPublisher.createUnitPrice(cntrInfo);
		this.completeReqRfx(cntrInfo);
		return ResultMap.SUCCESS();
	}

	/**
	 * 발주 요청
	 * @param cntrInfo
	 * @param purcCntr
	 * @return
	 */
	private ResultMap requestPo(Map cntrInfo, Map purcCntr) {
		Map purcCntrData = (Map) purcCntr.get("purcCntrData");
		
		Map eventParam = Maps.newHashMap();
		eventParam.put("oorg_cd", cntrInfo.get("oorg_cd"));
		eventParam.put("req_tit", cntrInfo.get("cntr_nm"));
		eventParam.put("purc_typ_ccd", purcCntrData.get("purc_typ_ccd"));
		eventParam.put("req_pic_id", cntrInfo.get("cntr_pic_id"));
		eventParam.put("purc_grp_cd", cntrInfo.get("purc_grp_cd"));
		eventParam.put("req_doc_typ_ccd", "CNTR");
		eventParam.put("po_typ_ccd", "SPOTCNTR");
		eventParam.put("po_chg_typ_ccd", "NEW");
		eventParam.put("po_req_uuid", cntrInfo.get("cntr_uuid"));
		eventParam.put("po_req_no", cntrInfo.get("cntr_no"));
		eventParam.put("po_cnd_uuid", cntrInfo.get("cntr_cnd_uuid"));
		eventParam.put("vd_cd", purcCntrData.get("vd_cd"));
		
		contractEventPublisher.requestPo(eventParam);
		
		this.completeReqRfx(cntrInfo);
		
		return ResultMap.SUCCESS();
	}

	/**
	 * RFX 요청 계약 완료 후처리
	 * @param cntrInfo
	 */
	private void completeReqRfx(Map cntrInfo) {
		String reqId = (String) cntrInfo.get("cntr_req_uuid");
		if(!Strings.isNullOrEmpty(reqId)) {
			contractEventPublisher.completeReqRfx(cntrInfo);
		}
	}

}
