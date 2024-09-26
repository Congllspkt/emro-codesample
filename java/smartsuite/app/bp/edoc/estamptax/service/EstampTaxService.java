package smartsuite.app.bp.edoc.estamptax.service;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

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
import smartsuite.app.bp.edoc.estamptax.event.EStampTaxEventPublisher;
import smartsuite.app.bp.edoc.estamptax.repository.EstampTaxRepository;
import smartsuite.app.bp.stamptax.repository.StampTaxRepository;
import smartsuite.app.bp.stamptax.service.StampTaxService;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.AttachUtils;
import smartsuite.app.common.util.ConvertUtils;
import smartsuite.app.shared.EstampConst;
import smartsuite.app.util.EstampTaxUtil;
import smartsuite.exception.CommonException;


/**
 * 전자수입인지 관련 처리하는 서비스 Class입니다.
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class EstampTaxService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EstampTaxService.class);

	@Inject
	EstampTaxUtil estampTaxUtil;
	@Inject
	EstampTaxRepository estampTaxRepository;
	@Inject
	StampTaxRepository stampTaxRepository;
	@Inject
	StampTaxService stampTaxService;
	@Inject
	AttachService attachService;
	@Inject
	EcontractService econtractService;
	@Inject
	PdfMakerService pdfMakerService;

	@Inject
	EStampTaxEventPublisher estampTaxEventPublisher;

	public Map<String,Object> findEstampTaxInfo(Map<String,Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String,Object> stampTax = stampTaxRepository.findStampTax(param);
		Map<String, Object> preStampTax = Maps.newHashMap();

		synchronizeEstampTax(stampTax); //순서 유의 싱크 후 전자수입인지 납부내역을 조회해야함.

		// 전자수입인지 납부 목록 조회
		List<Map<String,Object>> eStampTaxListBP = estampTaxRepository.findStampTaxList(param);

		// 최소 차수 확인 후 기납부 정보 조회
		String cntrNo = param.get("cntr_no").toString();
		int cntrRevNo = Integer.parseInt(param.get("cntr_revno").toString());
		boolean notMinRevNo = checkNotMinRevNo(cntrNo, cntrRevNo);
		BigDecimal preTotalSttpymtAmt;
		BigDecimal preTotalRefundAmt;
		int preToTalEStampTaxAmt = 0;
		if(notMinRevNo) {
			// 기납부 내역 조회(기납부 총액, 환급 총액)
			preStampTax = stampTaxRepository.findTotalPreStampTaxPay(param);
			preTotalSttpymtAmt = ConvertUtils.convertBigDecimal(preStampTax.get("pre_total_sttpymt_amt"));
			preTotalRefundAmt = ConvertUtils.convertBigDecimal(preStampTax.get("pre_total_refund_amt"));
			preToTalEStampTaxAmt = preTotalSttpymtAmt.intValue() - preTotalRefundAmt.intValue() ;
		}

		if( ! eStampTaxListBP.isEmpty() ) {

			int currentRevNoToTalEStampTaxAmt = calculateEstampTaxAmt(eStampTaxListBP);//납부한 인지세금액

			int toTalEStampTaxAmt = currentRevNoToTalEStampTaxAmt + preToTalEStampTaxAmt;

			int staxAmt = Integer.parseInt(stampTax.get("stax_amt").toString());    //납부해야할 인지세금액
			String sttpymtStsCcd = stampTaxService.checkPaymentSts(staxAmt,toTalEStampTaxAmt);

			Map stampTaxParam = Maps.newHashMap();
			stampTaxParam.put("sttpymt_amt", currentRevNoToTalEStampTaxAmt);
			stampTaxParam.put("sttpymt_sts_ccd", sttpymtStsCcd);
			stampTaxParam.put("stax_uuid", stampTax.get("stax_uuid"));
			stampTaxRepository.updateStampTax(stampTaxParam);
		}





		// 이전 인지세 정보 중 최대 차수 조회
		// ******** null exception 발생 가능성 있는지 체크필요 **********
		// 이전 인지세 정보 없을 때 에러 발생하여 주석처리함
		/*List<Map<String, Object>> allPreStaxList = stampTaxRepository.findListStaxByCntrNo(param);
		BigDecimal preStaxMaxRevNo = (BigDecimal) allPreStaxList.get(0).get("cntr_revno");
		stampTax.put("pre_max_stax_revno", preStaxMaxRevNo);*/

		// 계약의 최대 차수 조회
		int staxMaxRevNo = stampTaxRepository.findMaxStaxCntrRevNo(param);
		stampTax.put("max_stax_revno", staxMaxRevNo);

		resultMap.put("stampTax", stampTax);
		resultMap.put("eStampTaxListBP", eStampTaxListBP);
		resultMap.put("preStampTax", preStampTax);

		return resultMap;
	}

	private String checkRefundYn(Map totalTax, int maxRevstaxAmt) {
		String result = "N";

		int totalSttpymtAmt = Integer.parseInt(totalTax.get("total_sttpymt_amt").toString());
		int totalStaxRefundAmt = Integer.parseInt(totalTax.get("total_stax_refund_amt").toString());

		//BigDecimal totalPayment = totalSttpymtAmt.subtract(totalStaxRefundAmt);
		int totalPayment = totalSttpymtAmt - totalStaxRefundAmt;

		// 최종 차수의 납부해야 할 인지세액 < 총 납부액(기납부 총액-기환급 총액)
		if(maxRevstaxAmt < totalPayment) {
			result = "Y";
		}

		return result;
	}

	private boolean checkNotMinRevNo(String cntrNo, int cntrRevNo) {
		// 최소 차수가 아닌지 확인
		boolean notMinRevNo = true;

		int minCntrRevNo = estampTaxRepository.checkMinRevNoByCntrNo(cntrNo);

		if(minCntrRevNo == cntrRevNo) {
			notMinRevNo = false;
		}

		return notMinRevNo;
	}

	private int calculateEstampTaxAmt(List<Map<String, Object>> eStampTaxListBP) {
		int totalAmt = 0;
		for( Map estampTax : eStampTaxListBP){
			int taxpymtAmt = Integer.parseInt(estampTax.get("taxpymt_amt").toString());
			totalAmt += taxpymtAmt;
		}
		return totalAmt;
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
				estampTaxRepository.updateEstampTax(eStampTax);
			} else {
				estampTaxRepository.insertEstampTax(eStampTax);
			}
		}
	}

	private boolean isExistsEstampTax(Map eStampTax) {
		int cnt = estampTaxRepository.findCountEstampData(eStampTax);
		if( cnt == 1 ) {
			return true;
		}else if ( cnt == 0) {
			return false;
		}else{
			throw new CommonException(this.getClass().getName() + ".isExistsEstampTax : IssueBizNo is not unique." );
		}
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
			Map reqEstampTax = estampTaxRepository.findReqEstampTax(param);

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

	public ResultMap downloadStampTaxFile(Map<String, Object> param) {

		Map<String,Object> eStamptaxInfo = estampTaxRepository.findReqEstampTax(param);

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
			stampTaxRepository.updateStampTax(eStamptaxInfo);
		}
		Map<String,Object> result = Maps.newHashMap();
		result.put("sttpymt_athg_uuid", sttpymtAthgUuid);

		return ResultMap.SUCCESS(result);
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

	private boolean isExistsEstampTaxFile(Map<String, Object> stamptaxInfo) {//파일 확인
		List fileList = attachService.findListAttach(stamptaxInfo);
		if(fileList.size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public ResultMap findListEStampTaxPayHistory(Map param) {
		Map<String, Object> cntrInfo = Maps.newHashMap(param);
		List<Map<String, Object>> stampTaxHistoryList = Lists.newArrayList();

		stampTaxHistoryList = estampTaxEventPublisher.findListEStampTaxPayHistory(cntrInfo);

		return ResultMap.SUCCESS(stampTaxHistoryList);

	}

	public ResultMap saveRefundStampTax(Map param) {
		return stampTaxService.saveRefundStampTax(param);
	}
}