package smartsuite.app.sp.edoc.estamptax;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ktnet.ets.hub.common.util.JsonUtil;

import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.stamptax.repository.StampTaxRepository;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.AttachUtils;
import smartsuite.app.shared.EstampConst;
import smartsuite.app.sp.edoc.contract.SpEcontractService;
import smartsuite.app.sp.edoc.estamptax.event.SpEstampTaxEventPublisher;
import smartsuite.app.sp.edoc.estamptax.repository.SpEstampTaxRepository;
import smartsuite.app.sp.stamptax.repository.SpStampTaxRepository;
import smartsuite.app.sp.stamptax.service.SpStampTaxService;
import smartsuite.app.util.EstampTaxUtil;
import smartsuite.exception.CommonException;


/**
 * 전자수입인지 관련 처리하는 서비스 Class입니다.
 *
 * @author DaeSung Lee
 * @see 
 * @FileName EstampTaxService.java
 * @package smartsuite.app.bp.edoc.estamptax
 * @since 2020. 4. 7
 * @변경이력 : [2020. 4. 7] DaeSung Lee 최초작성
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class SpEstampTaxService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpEstampTaxService.class);
	
	@Inject
	SpEstampTaxRepository spEstampTaxRepository;
	@Inject
	SpStampTaxRepository spStampTaxRepository;
	@Inject
	SpEcontractService spContractService;
	@Inject
	EstampTaxUtil estampTaxUtil;
	@Inject
	PdfMakerService pdfMakerService;
	@Inject
	SpStampTaxService spStampTaxService;
	@Inject
	AttachService attachService;
	@Inject
	SpEstampTaxEventPublisher spEstampTaxEventPublisher;
	@Inject
	EcontractService econtractService;

	public Map<String,Object> findEstampTaxInfo(Map<String,Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String,Object> stampTax = spStampTaxRepository.findStampTax(param);
		Map<String, Object> preStampTax = Maps.newHashMap();

		synchronizeEstampTax(stampTax); //순서 유의 싱크 후 전자수입인지 납부내역을 조회해야함.

		// 전자수입인지 납부 목록 조회
		List<Map<String,Object>> eStampTaxListSP = spEstampTaxRepository.findStampTaxList(param);

		if( ! eStampTaxListSP.isEmpty() ) {
			int toTalEStampTaxAmt = calculateEstampTaxAmt(eStampTaxListSP);         //납부한 인지세금액
			int staxAmt = Integer.parseInt(stampTax.get("stax_amt").toString());    //납부해야할 인지세금액
			String sttpymtStsCcd = spStampTaxService.checkPaymentSts(staxAmt,toTalEStampTaxAmt);

			Map stampTaxParam = Maps.newHashMap();
			stampTaxParam.put("sttpymt_amt", toTalEStampTaxAmt);
			stampTaxParam.put("sttpymt_sts_ccd", sttpymtStsCcd);
			stampTaxParam.put("stax_uuid", stampTax.get("stax_uuid"));
			spStampTaxRepository.updateStampTax(stampTaxParam);
		}

		// 최소 차수 확인 후 기납부 정보 조회
		String cntrNo = param.get("cntr_no").toString();
		int cntrRevNo = Integer.parseInt(param.get("cntr_revno").toString());
		if(notMinRevNo(cntrNo, cntrRevNo)) {
			// 기납부 내역 조회(기납부 총액, 환급 총액)
			preStampTax = spStampTaxRepository.findTotalPreStampTaxPay(param);
		}

		// 이전 인지세 정보 중 최대 차수 조회
		// ******** null exception 발생 가능성 있는지 체크필요 **********
		// 이전 인지세 정보 없을 때 에러 발생하여 주석처리함
		/*List<Map<String, Object>> allPreStaxList = stampTaxRepository.findListStaxByCntrNo(param);
		BigDecimal preStaxMaxRevNo = (BigDecimal) allPreStaxList.get(0).get("cntr_revno");
		stampTax.put("pre_max_stax_revno", preStaxMaxRevNo);*/

		// 계약의 최대 차수 조회
		int staxMaxRevNo = spStampTaxRepository.findMaxStaxCntrRevNo(param);
		stampTax.put("max_stax_revno", staxMaxRevNo);

		resultMap.put("stampTax", stampTax);
		resultMap.put("eStampTaxListSP", eStampTaxListSP);
		resultMap.put("preStampTax", preStampTax);

		return resultMap;
	}

	private void synchronizeEstampTax(Map<String, Object> param) {
		String cntrNo = (String)param.get("cntr_no");
		String cntrRevno = param.get("cntr_revno").toString();
		Map<String,String> cfrmPayInfo = Maps.newHashMap();
		ArrayList responseResultList = Lists.newArrayList();
		try{
			cfrmPayInfo = estampTaxUtil.getIssueCfrmPayList(cntrNo, cntrRevno);
			responseResultList = JsonUtil.toObject(cfrmPayInfo.get("resultList"), ArrayList.class);
		}catch (Exception e) {
			throw new CommonException(this.getClass().getName() + ".getIssueCfrmPayList : " + e.getMessage(), e.toString());
		}

		for(int i = 0; i < responseResultList.size(); i++) {
			Map eStampTax = (Map) responseResultList.get(i);

			eStampTax.put("estax_uuid", UUID.randomUUID().toString());
			eStampTax.put("stax_uuid", param.get("stax_uuid"));
			eStampTax.put("ecntr_uuid", param.get("ecntr_uuid"));

			if (isExistsEstampTax(eStampTax)) {
				spEstampTaxRepository.updateEstampTax(eStampTax);
			} else {
				spEstampTaxRepository.insertEstampTax(eStampTax);
			}
		}
	}

	private boolean isExistsEstampTax(Map eStampTax) {
		int cnt = spEstampTaxRepository.findCountEstampData(eStampTax);
		if( cnt == 1 ) {
			return true;
		}else if ( cnt == 0) {
			return false;
		}else{
			throw new CommonException(this.getClass().getName() + ".isExistsEstampTax : IssueBizNo is not unique." );
		}
	}

	private int calculateEstampTaxAmt(List<Map<String, Object>> stampTaxListSP) {
		int totalAmt = 0;
		for( Map estampTax : stampTaxListSP){
			int taxpymtAmt = Integer.parseInt(estampTax.get("taxpymt_amt").toString());
			totalAmt += taxpymtAmt;
		}
		return totalAmt;
	}

	/**
	 * 전자수입인지 구매팝업
	 *
	 * @param : 전자수입인지 구매파업 호출에 필요한 정보
	 * @return : 전자수입인지 url 정보
	 */
	public Map<String, Object> findStampTaxBpUrlInfo(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();
		String purchaseRequestUrl = ""; //구매요청서 url
		try {
			Map reqEstampTax = spEstampTaxRepository.findReqEstampTax(param);

			String issueBizNo = reqEstampTax.get("bizregno").toString(); 		// 구매 사업자 번호
			String issueReqNo = "";												//발급신청 번호이며 unique_id같은 존재임
			int staxAmtmt = Integer.parseInt(reqEstampTax.get("stax_amt").toString());		//전자수입인지 금액
			int sttpymtAmt = Integer.parseInt(reqEstampTax.get("sttpymt_amt").toString());
			int sissueAmount = staxAmtmt - sttpymtAmt;

			String cntrrMob = (String)reqEstampTax.get("cntrr_mob");		//연락처
			String cntrrEml = (String)reqEstampTax.get("cntrr_eml");					//이메일
			String contractTitle = reqEstampTax.get("cntr_nm").toString();		//계약명
			String contractParties = reqEstampTax.get("comp_nm").toString();	  //기업명
			String contractDate = reqEstampTax.get("cntr_dt").toString();	   //계약 체결일
			String contractNo = reqEstampTax.get("cntr_no").toString();		   //계약 번호
			String contractNoSeq = reqEstampTax.get("cntr_revno").toString();		//계약 번호 차수
			String contractAmount = reqEstampTax.get("cntr_amt").toString();		//계약 금액
			String contractType = "030"; 	//계약 타입
			String inAmtDisable = "Y";  	//N 결제금액 수정가능 , Y 결제금액 수정 불가능
			String individualYn = "N"; 		// N 사업자결제 우선  Y  개인결제 우선

			contractTitle = URLEncoder.encode(contractTitle,"UTF-8");
			contractParties = URLEncoder.encode(contractParties,"UTF-8");

			Map newIssueReqInfo = estampTaxUtil.getNewIssueReqNo(contractNo); //발급신청번호 요청
			String resultCode = (String) newIssueReqInfo.get("resultCode");
			String resultMessage = (String) newIssueReqInfo.get("resultMessage");

			if(EstampConst.RESPONSE_CODE_SUCCESS.equals(resultCode)) {
				purchaseRequestUrl = (String) newIssueReqInfo.get("purchaseUrl");
				String issueDivisionCode = (String) newIssueReqInfo.get("issueDivisionCode");
				issueReqNo = (String) newIssueReqInfo.get("issueReqNo");

				Map paramMap = Maps.newHashMap();
				paramMap.put("infToolMngNo", newIssueReqInfo.get("infToolMngNo"));
				paramMap.put("infToolBizNo", newIssueReqInfo.get("infToolBizNo"));
				paramMap.put("issueBizNo", issueBizNo);
				paramMap.put("issueDivisionCode", issueDivisionCode);
				paramMap.put("issueReqNo", issueReqNo);
				paramMap.put("contractTitle", contractTitle);
				paramMap.put("contractDate", contractDate);
				paramMap.put("contractNo", contractNo);
				paramMap.put("contractNoSeq", contractNoSeq);
				paramMap.put("contractParties", contractParties);
				paramMap.put("contractAmount", contractAmount);
				paramMap.put("contractType", contractType);
				paramMap.put("mobileNumber", cntrrMob);
				paramMap.put("eml", cntrrEml);
				paramMap.put("issueAmount", sissueAmount);

				String urlParam = EstampTaxUtil.getPurchaseRequestUrlParam(paramMap);

				purchaseRequestUrl +="?"+urlParam;

				resultMap.put("purchaseRequestUrl", purchaseRequestUrl);
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);

				LOG.info("purchaseRequestUrl : " + purchaseRequestUrl);
			} else {
				resultMap.put(Const.RESULT_STATUS, Const.FAIL);
				resultMap.put(Const.RESULT_MSG, resultMessage);
				LOG.info("errocode : " + resultCode +" , error resultMessage : " + resultMessage);
				return resultMap;
			}
		} catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "findStampTaxBpUrlInfo error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".findStampUrlTaxInfo() : " + e.getMessage() + e.toString());
		}
		return resultMap;
	}

	/**
	 * 인지세 납부정보 확인
	 *
	 * @author : DaeSung Lee
	 * @param map
	 * @return the map
	 * @Date : 2020. 3. 30
	 * @Method Name : saveIssuePayList
	 */
	public Map saveIssuePayList(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();

		String cntrId = (String)param.get("ecntr_uuid");
		String cntrNo = (String)param.get("ecntr_no");
		String cntrRev = param.get("ecntr_revno").toString();
		String bpBizRegNo = (String)param.get("bp_biz_reg_no");

		ArrayList responseResultList = Lists.newArrayList();
		String taxamount = "";
		int nTaxamount = 0;

		try {
			Map<String,String> cfrmPayInfo = estampTaxUtil.getIssueCfrmPayList(cntrNo, cntrRev);
			responseResultList = JsonUtil.toObject(cfrmPayInfo.get("resultList"), ArrayList.class);
			taxamount = cfrmPayInfo.get("taxamount");
			nTaxamount = Integer.parseInt(taxamount);
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "saveIssuePayList error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".saveIssuePayList error : " + e.getMessage() + e.toString());
		}

		if(responseResultList.size() < 1) {
			resultMap.put(Const.RESULT_STATUS, Const.NOT_EXIST); //구매내역이 없습니다.
			resultMap.put("elec_stamptax_all_pay_yn", "N");
			return resultMap;
		}
		int totalPaymentAmount = 0;
		for(int i = 0; i < responseResultList.size(); i++) {
			Map row = (Map)responseResultList.get(i);

			String paymentAmount = (String)row.get("paymentAmount");
			int nPaymentAmount = Integer.parseInt(paymentAmount);
			totalPaymentAmount += nPaymentAmount;

			row.put("sttpymt_uuid", UUID.randomUUID().toString() );
			row.put("ecntr_uuid", cntrId);

			if(bpBizRegNo.equals(row.get("issueBizNo"))) { //구매사 사업자번호와 같으면
				row.put("cntrr_typ_ccd", "Y");
			}else {
				row.put("cntrr_typ_ccd", "N");
			}
			spEstampTaxRepository.insertStamptaxPurchaseInfo(row);
		}
		if(responseResultList.size() > 0) {
			spEstampTaxRepository.updateEstampTaxSts(param);

			//변경계약으로 인한 전자수입인지 정보가 있는지 확인
			int cnt = spEstampTaxRepository.getPreviousElecStampInfo(param);
			int previousPaymentAmount=0;
			int nCntrRev = Integer.parseInt(cntrRev);
			if(cnt > 0) { //이전 전자수입인지 정보가 존재함.
				for(int i = (nCntrRev-1); i >= 0; i--) {
					previousPaymentAmount += getElecPaymentAmount(cntrNo, i);
				}
				totalPaymentAmount += previousPaymentAmount;
			}
		}

		resultMap.put("taxamount", taxamount);
		resultMap.put("total_pay_amount", totalPaymentAmount);
		if(nTaxamount == totalPaymentAmount) {
			resultMap.put("elec_stamptax_all_pay_yn", "Y");
		}else {
			resultMap.put("elec_stamptax_all_pay_yn", "N");
		}
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	/*public Map<String,Object> findEstampTaxInfo(Map<String,Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> cntrInfo = spContractService.getContract(param);

		param.put("cntrr_typ_ccd", "Y"); //구매사
		List<Map<String,Object>> stampTaxListBP = spEstampTaxRepository.findStampTaxList(param);
		param.put("cntrr_typ_ccd", "N"); //협력사
		List<Map<String,Object>> stampTaxListSP = spEstampTaxRepository.findStampTaxList(param);

		Map<String,Object> paramMap = Maps.newHashMap();
		paramMap.put("ecntr_no", cntrInfo.get("ecntr_no"));
		paramMap.put("ecntr_revno", cntrInfo.get("ecntr_revno"));
		paramMap.put("ecntr_uuid", cntrInfo.get("ecntr_uuid"));

		String bpBizRegNo = spEstampTaxRepository.getBuyerBizRegNo(param);
		cntrInfo.put("bp_biz_reg_no", bpBizRegNo);

		Map payInfo = saveIssuePayList(cntrInfo); //납부내역 있는지 확인
		cntrInfo.put("elec_stamptax_all_pay_yn",payInfo.get("elec_stamptax_all_pay_yn") );

		cntrInfo.put("total_pay_amount", payInfo.get("total_pay_amount"));
		cntrInfo.put("taxamount", payInfo.get("taxamount"));

		resultMap.put("cntrInfo", cntrInfo);
		resultMap.put("stampTaxListBP", stampTaxListBP);
		resultMap.put("stampTaxListSP", stampTaxListSP);

		return resultMap;
	}*/

	public ResultMap downloadStampTaxFile(Map<String, Object> param) {

		Map<String,Object> eStamptaxInfo = spEstampTaxRepository.findReqEstampTax(param);

		//인지세 정보 조회
		if(eStamptaxInfo.get("cntr_revno").toString().equals("0")) {
			eStamptaxInfo.put("issueDivisionCode", "01");
		} else {
			eStamptaxInfo.put("issueDivisionCode", "04"); //변경
		}
		String sttpymtAthgUuid = "";
		if( isExistsEstampTaxFile(eStamptaxInfo) ) {
			sttpymtAthgUuid = (String)eStamptaxInfo.get("sttpymt_athg_uuid");
		}else{
			sttpymtAthgUuid = createEstampTaxFile(eStamptaxInfo);
			eStamptaxInfo.put("sttpymt_athg_uuid", sttpymtAthgUuid);
			spStampTaxRepository.updateStampTax(eStamptaxInfo);
		}
		Map<String,Object> result = Maps.newHashMap();
		result.put("sttpymt_athg_uuid", sttpymtAthgUuid);

		return ResultMap.SUCCESS(result);
	}
	
	private Map<String, Object> generateCntrPdf(Map<String, Object> param) {
		Map<String,Object> cntrInfo = spContractService.getCntrContent(param);
		List<Map<String, Object>> appList = spContractService.getAppContent(param);
		
		String pdfFullPath = pdfMakerService.makePdfUsingHtml(cntrInfo, appList, false);
		String orgnFileNm = cntrInfo.get("ecntr_no") + "-" + cntrInfo.get("ecntr_revno") + "_signed.pdf";
		
		Map pdfAttach = Maps.newHashMap();
		pdfAttach.put("pdf_full_path", pdfFullPath);
		pdfAttach.put("orgn_file_nm", orgnFileNm);
		return pdfAttach;
	}

	public List<Map<String,Object>> findEstampTaxHistoryInfo(Map<String,Object> param){
		return spEstampTaxRepository.findStampTaxHistoryList(param);
	}
	
	private int getElecPaymentAmount(String cntrNo, int cntrRev) {
		int totalPaymentAmount = 0;
		ArrayList responseResultList;
		try {
			Map<String,String> cfrmPayInfo = estampTaxUtil.getIssueCfrmPayList(cntrNo, Integer.toString(cntrRev));
			responseResultList = JsonUtil.toObject(cfrmPayInfo.get("resultList"), ArrayList.class);
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getElecPaymentAmount error : " + e.toString());
			throw new CommonException(this.getClass().getName()+".getElecPaymentAmount error : " + e.getMessage() + e.toString());
		}
		
		if(responseResultList.size() < 1) {
			return totalPaymentAmount;
		}
		
		for(int i = 0; i < responseResultList.size(); i++) {
			Map row = (Map)responseResultList.get(i);
			
			String paymentAmount = (String)row.get("paymentAmount");
			int nPaymentAmount = Integer.parseInt(paymentAmount);
			totalPaymentAmount += nPaymentAmount;
		}
		return totalPaymentAmount;
	}

	public ResultMap findListEStampTaxPayHistory(Map param) {
		Map<String, Object> cntrInfo = Maps.newHashMap(param);
		List<Map<String, Object>> stampTaxHistoryList = Lists.newArrayList();

		stampTaxHistoryList = spEstampTaxEventPublisher.findListEStampTaxPayHistory(cntrInfo);

		return ResultMap.SUCCESS(stampTaxHistoryList);

	}

	private boolean notMinRevNo(String cntrNo, int cntrRevNo) {
		// 최소 차수가 아닌지 확인
		boolean notMinRevNo = true;

		int minCntrRevNo = spEstampTaxRepository.checkMinRevNoByCntrNo(cntrNo);

		if(minCntrRevNo == cntrRevNo) {
			notMinRevNo = false;
		}

		return notMinRevNo;
	}

	private boolean isExistsEstampTaxFile(Map<String, Object> stamptaxInfo) {//파일 확인
		List fileList = attachService.findListAttach(stamptaxInfo);
		if(fileList.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	private String createEstampTaxFile(Map<String, Object> stamptaxInfo) {
		String sttpymtAthgUuid  = "";
		String ecntrUUID = (String) stamptaxInfo.get("ecntr_uuid");
		Map cntrPdfParam = Maps.newHashMap();
		cntrPdfParam.put("ecntr_uuid", ecntrUUID);

		String stampFileAddName = "ST"; //전자수입인지 추가할 파일명
		String pdfFilePath = econtractService.generateCntrPdf(cntrPdfParam);
		String eStampTaxFilePath = pdfFilePath +"_" + stampFileAddName + ".pdf"; //인지세 표시할 pdf 파일

		AttachUtils.copyFile(new File(pdfFilePath), new File(eStampTaxFilePath)); //파일 복사
		AttachUtils.deleteFile(pdfFilePath); //임시파일 삭제

		stamptaxInfo.put("eStampTaxFilePath", eStampTaxFilePath);
		stamptaxInfo.put("stampPdfPath", eStampTaxFilePath);

		try {
			//전자수입인지 pdf생성기능은   contractFilePath, stampPdfPath 값에 확장자까지 넣어주어야 작동함.
			Map stamptaxResultMap = estampTaxUtil.stampTaxIssuePdfFile(stamptaxInfo); //전자수입인지 PDF 발급 요청

			if( EstampConst.RESPONSE_CODE_SUCCESS.equals(stamptaxResultMap.get("resultCode")) ) {

				int ep = eStampTaxFilePath.lastIndexOf(stampFileAddName + ".pdf"); //확장자 제거

				String removeExtensionPath = eStampTaxFilePath.substring(0, ep);
				AttachUtils.copyFile(new File(eStampTaxFilePath), new File(removeExtensionPath)); //파일 복사
				AttachUtils.deleteFile(eStampTaxFilePath); //확장자 붙은 파일삭제

				ep = eStampTaxFilePath.lastIndexOf(stampFileAddName + ".pdf");
				eStampTaxFilePath = eStampTaxFilePath.substring(0,ep);

				String orgnFileName = stampFileAddName + "_" + stamptaxInfo.get("cntr_no") + "-" + stamptaxInfo.get("cntr_revno") + ".pdf";

				Map<String,Object> fileInfo = pdfMakerService.savePdfFile(eStampTaxFilePath, orgnFileName);
				sttpymtAthgUuid = (String) fileInfo.get("athg_uuid");

			} else {
				LOG.error("class : " + this.getClass().toString() + ".createEstampTaxFile error:" + stamptaxResultMap.get("resultMessage"));
				throw new CommonException(this.getClass().getName()+ ".createEstampTaxFile error:" + stamptaxResultMap.get("resultMessage"));
			}
		} catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + ".createEstampTaxFile error:" + e.toString());
			throw new CommonException(this.getClass().getName()+".createEstampTaxFile error: " + e.getMessage() + e.toString());
		}


		return sttpymtAthgUuid;
	}

	public ResultMap saveRefundStampTax(Map param) {
		return spStampTaxService.saveRefundStampTax(param);
	}

}