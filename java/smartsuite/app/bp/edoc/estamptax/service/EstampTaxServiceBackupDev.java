package smartsuite.app.bp.edoc.estamptax.service;

import com.google.common.collect.Maps;
import com.ktnet.ets.hub.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.edoc.estamptax.repository.EstampTaxRepository;
import smartsuite.app.bp.stamptax.repository.StampTaxRepository;
import smartsuite.app.bp.stamptax.service.StampTaxService;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.util.AttachUtils;
import smartsuite.app.shared.EstampConst;
import smartsuite.app.util.EstampTaxUtil;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
public class EstampTaxServiceBackupDev {
	
	private static final Logger LOG = LoggerFactory.getLogger(EstampTaxServiceBackupDev.class);
	
	@Inject
	EstampTaxRepository estampTaxRepository;

	@Inject
	StampTaxRepository stampTaxRepository;

	@Inject
	EcontractService contractService;
	
	@Inject
	AttachService attachService;
	
	@Inject
	EstampTaxUtil estampTaxUtil;
	
	@Inject
	PdfMakerService pdfMakerService;

	@Inject
	StampTaxService stampTaxService;

	/**
	 * 인지세 정보 가져오기
	 *
	 * @author : DaeSung Lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 3. 19
	 * @Method Name : findStampTaxBpUrlInfo
	 */
	public Map<String, Object> findStampTaxBpUrlInfo(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();
		String purchaseRequestUrl = ""; //구매요청서 url
		try {
			Map estampInfo = estampTaxRepository.findEstampUrlInfo(param);
			int buyerIssueRate = Integer.parseInt((String)estampInfo.get("buyer_issue_rate"));
			
			String stamptaxAmt = estampInfo.get("payed_issue_amt").toString(); //납부한 인지세 금액
			String beforeStamptaxAmt = estampInfo.get("before_total_pay_amt").toString(); //이전 계약에서 납부한 인지세 금액
			String issueAmount = estampInfo.get("issue_amt").toString(); //내야 할 인지세 금액
			
			//이전 계약에서 납부한 인지세 금액이  현재 납부해야할 인지세 금액보다 크거나 같다면, 현 계약에 인지세는 납부할 필요 없다.
			if(Integer.parseInt(beforeStamptaxAmt) >= Integer.parseInt(issueAmount)) {
				resultMap.put(Const.RESULT_STATUS, Const.FAIL);
				LOG.info("현재 납부해야할 인지세 금액이 이전 계약에서 납부한 인지세 금액보다 작거나 같다면 인지세 납부를 진행할 필요 없습니다.");
				return resultMap;
			}
			
			int nissueAmount = Integer.parseInt(issueAmount);
			
			int nbeforeStamptaxAmt = Integer.parseInt(beforeStamptaxAmt);
			nissueAmount = nissueAmount - nbeforeStamptaxAmt;
			
			nissueAmount = nissueAmount * buyerIssueRate / 100; 
			nissueAmount = nissueAmount - Integer.parseInt(stamptaxAmt);
			
			String issueBizNo = estampInfo.get("bizregno").toString(); 		// 구매 사업자 번호
			String issueReqNo = "";												//발급신청 번호이며 unique_id같은 존재임
			String sissueAmount = Integer.toString(nissueAmount);		//전자수입인지 금액
			
			String mobileNumber = (String)estampInfo.get("mob");		//연락처
			String eml = (String)estampInfo.get("eml");					//이메일
			String contractTitle = estampInfo.get("cntr_nm").toString();		//계약명
			String contractParties = estampInfo.get("comp_nm").toString();	  //기업명
			String contractDate = estampInfo.get("cntr_dt").toString();	   //계약 체결일
			String contractNo = estampInfo.get("cntr_no").toString();		   //계약 번호
			String contractNoSeq = estampInfo.get("cntr_revno").toString();		//계약 번호 차수
			String contractAmount = estampInfo.get("cntr_amt").toString();		//계약 금액
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
				paramMap.put("mobileNumber", mobileNumber);
				paramMap.put("eml", eml);
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
	 * @param : 인지세 납부정보 확인하기 위한 값
	 * @return : 인지세 납부정보
	 */
	/*public Map saveIssuePayList(Map<String, Object> param) {
		Map resultMap = Maps.newHashMap();
		
		String cntrUuid = (String)param.get("cntr_uuid");
		String ecntrUuid = (String)param.get("ecntr_uuid");
		String staxUuid = (String)param.get("stax_uuid");
		String cntrNo = (String)param.get("cntr_no");
		String cntrRev = param.get("cntr_revno").toString();
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
			
			row.put("estax_uuid", UUID.randomUUID().toString() );
			row.put("cntr_uuid", cntrUuid);
			//ESTAX 테이블에 데이터 있는지 확인
			int cnt = estampTaxRepository.findCountEstampData(row);
			//TODO 2024-03-20
			*//*Map stax = estampTaxRepository.findCountEstampData(row);
			if( cnt > 0){
				estampTaxRepository.insertStamptaxPurchase(row);
			}else {
				estampTaxRepository.updateStamptaxPurchase(row);
			}*//*
			//없다면 INSERT
			//INSERT 할때 정보는 사업자번호를 가지고 CNTRR 테이블에서 데이터를 찾아서 가져와야함.
			//있다면 UPDATE
			
			if(bpBizRegNo.equals(row.get("issueBizNo"))) { //구매사 사업자번호와 같으면
				row.put("ecntr_uuid", ecntrUuid);
				row.put("stax_uuid", staxUuid);
				estampTaxRepository.insertStamptaxPurchaseInfo(row);
				//stampTaxRepository.updateStampTax
			}

		}
		//구매사 총 납부한 금액 구하기
		param.put("cntrr_typ_ccd", CntrConst.USR_TYPE.BUYER);
		int stamptaxAmt = estampTaxRepository.findPaidStampTaxAmt(param);
		param.put("sttpymt_amt", stamptaxAmt);
		param.put("sttpymt_sts_ccd", CntrConst.STTPYMT_STATUS.PROGRESS);
		stampTaxRepository.updateStampTaxSts(param);
		if(responseResultList.size() > 0) {
			//estampTaxRepository.updateEstampTaxSts(param);

			//변경계약으로 인한 전자수입인지 정보가 있는지 확인
			int cnt = estampTaxRepository.getPreviousElecStampInfo(param);
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

		if( paidAllStampTax(staxUuid) ){
			stampTaxService.paidAllStampTax(staxUuid);
		}

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}*/

	private boolean paidAllStampTax(String staxUuid) {
		Map param = Maps.newHashMap();
		boolean paid = false;
		param.put("stax_uuid", staxUuid);
		Map stax = stampTaxRepository.findStampTax(param);
		int staxAmt = Integer.parseInt(stax.get("stax_amt").toString()); //납부해야할 금액
		int sttpymtAmt = Integer.parseInt(stax.get("sttpymt_amt").toString());  //납부한 금액

		if(staxAmt == sttpymtAmt){
			paid = true;
		}else{
			paid = false;
		}

		return paid;
	}

	/*public Map<String,Object> findEstampTaxInfo(Map<String,Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String,Object> contract = estampTaxRepository.findContract(param);

		param.put("cntrr_typ_ccd", CntrConst.USR_TYPE.BUYER); //구매사
		List<Map<String,Object>> stampTaxListBP = estampTaxRepository.findStampTaxList(param);
		param.put("cntrr_typ_ccd", CntrConst.USR_TYPE.VENDOR); //협력사
		List<Map<String,Object>> stampTaxListSP = estampTaxRepository.findStampTaxList(param);

		Map<String,Object> paramMap = Maps.newHashMap();
		paramMap.put("cntr_no", contract.get("cntr_no"));
		paramMap.put("cntr_revno", contract.get("cntr_revno"));
		paramMap.put("cntr_uuid", contract.get("cntr_uuid"));

		String bpBizRegNo = estampTaxRepository.getBuyerBizRegNo();
		contract.put("bp_biz_reg_no", bpBizRegNo);

		Map payInfo = saveIssuePayList(contract); //납부내역 있는지 확인
		contract.put("elec_stamptax_all_pay_yn",payInfo.get("elec_stamptax_all_pay_yn") );

		contract.put("total_pay_amount", payInfo.get("total_pay_amount"));
		contract.put("taxamount", payInfo.get("taxamount"));

		resultMap.put("cntrInfo", contract);
		resultMap.put("stampTaxListBP", stampTaxListBP);
		resultMap.put("stampTaxListSP", stampTaxListSP);

		return resultMap;
	}*/
	
	public Map<String, Object> downloadStampTaxFile(Map<String, Object> param) {
		/*
		 * 1. 전자수입인지 pdf 생성 api 요청하기 위한 인지세 정보 조회
		 * 2. 전자수입인지 pdf 파일을 만들었던적이 있는지 확인 후 한번도 만든적이 없으면, pdf 생성로직 수행
		 * 3. 계약서,첨부서류,첨부내용으로 임시 pdf 생성
		 * 4. 임시 pdf파일로 전자수입인지 pdf로 사용할 파일이름으로 복사
		 * 5. 임시 pdf파일 삭제
		 * 6. 전자수입인지 pdf 생성 api를 통한 pdf 생성 후 esaatth 저장
		 * 7. 전자수입인지 pdf 다운로드 함수 호출 
		 * */
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String,Object> paramMap = Maps.newHashMap();
		Map<String,String> stamptaxResultMap = Maps.newHashMap();
		
		paramMap.put("cntrr_typ_ccd", "Y");
		paramMap.put("ecntr_uuid", param.get("ecntr_uuid"));
		
		//인지세 정보 조회
		Map<String,Object> stamptaxInfo = estampTaxRepository.getBuyerStamptaxInfo(paramMap);
		if(stamptaxInfo.get("ecntr_revno").toString().equals("0")) {
			stamptaxInfo.put("issueDivisionCode", "01");
		} else {
			stamptaxInfo.put("issueDivisionCode", "04"); //변경
		}
		
		//파일 확인
		List fileList = attachService.findListAttach(stamptaxInfo);
		
		if(fileList.size() < 1) {
			Map<String,Object> pdfInfo = this.generateCntrPdf(paramMap);
			
			String stampFileAddName = "ST"; //전자수입인지 추가할 파일명
			String tempAttFilePath = (String) pdfInfo.get("pdf_full_path");
			String contractFilePath = tempAttFilePath + stampFileAddName + ".pdf"; //인지세 표시할 pdf 파일
			
			AttachUtils.copyFile(new File(tempAttFilePath), new File(contractFilePath)); //파일 복사
			AttachUtils.deleteFile(tempAttFilePath); //임시파일 삭제
			
			stamptaxInfo.put("contractFilePath", contractFilePath);
			stamptaxInfo.put("stampPdfPath", contractFilePath);
			
			try {
				//전자수입인지 pdf생성기능은   contractFilePath, stampPdfPath 값에 확장자까지 넣어주어야 작동함.
				stamptaxResultMap = estampTaxUtil.stampTaxIssuePdfFile(stamptaxInfo); //전자수입인지 PDF 발급 요청
				
				if( EstampConst.RESPONSE_CODE_SUCCESS.equals(stamptaxResultMap.get("resultCode")) ) {
					int ep = contractFilePath.lastIndexOf(stampFileAddName + ".pdf"); //확장자 제거
					
					String removeExtensionPath = contractFilePath.substring(0, ep);
					AttachUtils.copyFile(new File(contractFilePath), new File(removeExtensionPath)); //파일 복사
					AttachUtils.deleteFile(contractFilePath); //확장자 붙은 파일삭제
					
					ep = contractFilePath.lastIndexOf(stampFileAddName + ".pdf");
					contractFilePath = contractFilePath.substring(0,ep);
					
					Map<String,Object> fileInfo = pdfMakerService.savePdfFile(contractFilePath, stampFileAddName + "_"  + pdfInfo.get("orgn_file_name"));
					
					fileInfo.put("ecntr_uuid", stamptaxInfo.get("ecntr_uuid"));
					fileInfo.put("cntrr_typ_ccd", "Y");
					estampTaxRepository.updateEstampFileGrpCd(fileInfo);
					
					resultMap.put("athg_uuid", fileInfo.get("athg_uuid"));
				} else {
					LOG.error("class : " + this.getClass().toString() + ".downloadStampTaxFile error:" + stamptaxResultMap.get("resultMessage"));
					throw new CommonException(this.getClass().getName()+ ".downloadStampTaxFile error:" + stamptaxResultMap.get("resultMessage"));
				}
			} catch(Exception e) {
				LOG.error("class : " + this.getClass().toString() + ".downloadStampTaxFile error:" + e.toString());
				throw new CommonException(this.getClass().getName()+".downloadStampTaxFile error: " + e.getMessage() + e.toString());
			}
		} else {
			resultMap.put("athg_uuid", stamptaxInfo.get("athg_uuid"));
		}
		
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}
	
	private Map<String, Object> generateCntrPdf(Map<String, Object> param){
		Map<String,Object> cntrInfo = contractService.getCntrContent(param);
		List<Map<String, Object>> appList = contractService.getAppContent(param);
		
		String pdfFullPath = pdfMakerService.makePdfUsingHtml(cntrInfo, appList, false);
		String orgnFileNm = cntrInfo.get("ecntr_no") + "-" + cntrInfo.get("ecntr_revno") + "_signed.pdf";
		
		Map pdfAttach = Maps.newHashMap();
		pdfAttach.put("pdf_full_path", pdfFullPath);
		pdfAttach.put("orgn_file_nm", orgnFileNm);
		return pdfAttach;
	}

	public List<Map<String,Object>> findEstampTaxHistoryInfo(Map<String,Object> param) {
		return estampTaxRepository.findStampTaxHistoryList(param);
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
}