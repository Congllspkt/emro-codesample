package smartsuite.app.bp.edoc.contract.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.organizationManager.operationUnit.service.OperationUnitManagerService;
import smartsuite.app.bp.admin.organizationManager.service.OrganizationManagerService;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.edoc.pdfmaker.PdfMakerService;
import smartsuite.app.bp.edoc.contract.doc.ContractDocument;
import smartsuite.app.bp.edoc.contract.doc.GetValueType;
import smartsuite.app.bp.edoc.contract.event.EcontractEventPublisher;
import smartsuite.app.bp.edoc.contract.repository.EcontractRepository;
import smartsuite.app.bp.edoc.template.service.CntrTemplateService;
import smartsuite.app.common.AttachService;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.app.common.cert.pki.signature.SignatureProvider;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.cert.service.CertMgtService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.AttachUtils;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.shared.*;
import smartsuite.app.util.DocumentUtil;
import smartsuite.app.util.DownloadUtil;
import smartsuite.exception.CommonException;
import smartsuite.security.authentication.Auth;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 전자계약 관련 처리하는 서비스 Class입니다.
 *
 * @FileName EcontractService.java
 */
@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class EcontractService {

	private static final Logger LOG = LoggerFactory.getLogger(EcontractService.class);
	private static final String DATE_FORMAT = "yyyyMMdd";

	@Value("#{smartsuiteProperties['smartsuite.upload.path']}")
	private String fileUploadPath;

	@Value("#{globalProperties['blockchain.use']}")
	private String blockChainUseYn;
	@Value("#{cert['cert.verifiable']}")
	private boolean certVerifiable;
	@Inject
	SignatureProvider signatureProvider;
	@Inject
	VerificationProvider verificationProvider;
	@Inject
	PdfMakerService pdfMakerService;
	@Inject
	CertMgtService certMgtService;
	@Inject
	SharedService sharedService;
	@Inject
	StdFileService fileService;
	@Inject
	EcontractEventPublisher econtractEventPublisher;
	@Inject
	EcontractRepository econtractRepository;
	@Inject
	AttachService attachService;
	@Inject
	CntrTemplateService cntrTemplateService;
	@Inject
	OrganizationManagerService organizationManagerService;
	@Inject
	OperationUnitManagerService operationUnitManagerService;
	@Inject
	SignOrderService signOrderService;
	@Inject
	ContractService contractService;


	/**
	 * 1건 파일 다운로드 (계약서식,첨부서식,계약서,첨부서,등..)
	 * @param resquest
	 * @param response
	 * @param param
	 * @param fileName
	 */
	public void download(HttpServletRequest resquest, HttpServletResponse response, Map<String, Object> param, String fileName) {
		try {
			FileList fileList = fileService.findFileListWithContents((String) param.get("athg_uuid"));
			FileItem fileItem = fileList.getItems().get(0);

			if(fileName == null || fileName.isEmpty()) {
				DownloadUtil.downloadFile(resquest, response, fileItem);
			} else {
				DownloadUtil.downloadFile(resquest, response, fileItem, fileName);
			}
		} catch(Exception e) {
			LOG.error("class : " + this.getClass().toString() + "download error : " + e.toString());
			throw new CommonException(this.getClass().toString() + "download : " + e.getMessage() + e.toString());
		}
	}

	/**
	 * 첨부서류 내용 조회
	 * @param param
	 * @return
	 */
	public Map findAppData(Map param) {
		Map result = Maps.newHashMap();

		result.put("appData", econtractRepository.getAppData(param));

		Map itemParam = Maps.newHashMap();
		itemParam.put("tmpl_uuid", param.get("appx_tmpl_uuid"));

		List<Map<String, Object>> useClauseList = cntrTemplateService.findUseByCntrClause(itemParam);
		result.put("itemList", useClauseList);

		return result;
	}

	/**
	 * 계약서에 붙어있는 첨부서류 업데이트(입력)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> updateAppFormOrgn(Map<String,Object> param) throws Exception {
		Map<String,Object> resultMap = Maps.newHashMap();
		Map<String,Object> info      = (Map) param.get("form");
		Map<String,Object> etcMap    = (Map) param.get("etcInfo");

		Map<String,Object> appxCont = econtractRepository.getAppData(info);
		String cntrdocCrngCont = (String)appxCont.get("cntr_appx_crng_cont");

		Map itemParam = Maps.newHashMap();
		itemParam.put("tmpl_uuid", info.get("appx_tmpl_uuid"));

		List<Map<String, Object>> useClauseList = cntrTemplateService.findUseByCntrClause(itemParam);

		ContractDocument contractDocument = new ContractDocument(cntrdocCrngCont, useClauseList);
		contractDocument.putValueToTemplate(etcMap, GetValueType.CNTR_CL_ID);
		contractDocument.activateEdit();
		cntrdocCrngCont = contractDocument.getDocument();
		String cntrdocCcmpldCont = contractDocument.getDisAbleDocument();
		appxCont.put("cntr_appx_crng_cont", cntrdocCrngCont);
		appxCont.put("cntr_appx_ccmpld_cont", cntrdocCcmpldCont);

		appxCont.put("cntr_appx_uuid", info.get("cntr_appx_uuid"));
		econtractRepository.updateAttachDocumentMgt(appxCont);
		econtractRepository.updateAttachDocument(appxCont);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	public Map<String,Object> updateCntrFormOrgn(Map<String,Object> param) throws Exception {
		Map<String,Object> resultMap = Maps.newHashMap();
		Map<String,Object> info      = (Map) param.get("cntrInfo");
		Map<String,Object> userInputMap    = (Map) param.get("etcInfo");

		Map<String,Object> result = econtractRepository.getCntrContent(info);

		Map itemParam = Maps.newHashMap();
		itemParam.put("tmpl_uuid", result.get("cntrdoc_tmpl_uuid"));

		List<Map<String, Object>> useClauseList = cntrTemplateService.findUseByCntrClause(itemParam);

		String cntrdocCrngCont  = (String)result.get("cntrdoc_crng_cont");
		String cntrdocCcmpldCont = (String)result.get("cntrdoc_ccmpld_cont");

		ContractDocument contractDocument = new ContractDocument(cntrdocCrngCont, useClauseList);
		contractDocument.putValueToTemplate(userInputMap, GetValueType.CNTR_CL_ID);
		cntrdocCrngCont = contractDocument.getDocument();
		cntrdocCcmpldCont = contractDocument.getDisAbleDocument();
		result.put("cntrdoc_crng_cont", cntrdocCrngCont);
		result.put("cntrdoc_ccmpld_cont", cntrdocCcmpldCont);

		econtractRepository.updateContractDocument(result);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	/**
	 * 계약서 첨부서식 추가
	 * @param param
	 * @return
	 */
	public Map addAppFormInSts(Map param) {
		Map resultMap = Maps.newHashMap();
		String attDocNo = UUID.randomUUID().toString();
		param.put("cntr_appx_uuid", attDocNo);

		if(("FILE".equals(param.get("appx_typ_ccd")) || "FILE_LIST".equals(param.get("appx_typ_ccd"))) && param.get("athg_uuid") != null && !"".equals(param.get("athg_uuid"))){
			List findList = attachService.findListAttach(param);
			if(findList != null && findList.size() > 0) {
				String grpCd = fileService.copyFile((String) param.get("athg_uuid"));
				param.put("cntr_athg_uuid", grpCd);
				param.put("cntr_appx_sts_ccd","Y");
			}
		}

		//text app input check
		if("TXT".equals(param.get("appx_typ_ccd"))) {

			// 기본계약서 첨부서 내용 등록
			Map result = econtractRepository.appFormView(param);

			// 첨부서식 셋팅시 필요한 정보
			Map<String,Object> settingParam = econtractRepository.getContract(param);

			Map<String,Object> vendorInfo   = econtractRepository.getBasicVdInfo(settingParam);
			settingParam = mapMerge(settingParam, vendorInfo);
			settingParam.put("edit_content",result.get("content"));

			String editContent = (String)settingParam.get("edit_content");

			Map dynamicInfo = Maps.newHashMap();

			Map clauseTmplParam = Maps.newHashMap();
			clauseTmplParam.put("tmpl_uuid", param.get("appx_tmpl_uuid"));
			clauseTmplParam.put("dat_typ_ccd", "TMPL");
			List<Map<String,Object>> useDynamicTemplateClause = cntrTemplateService.findUseByCntrClause(clauseTmplParam);

			if(useDynamicTemplateClause.size() > 0){
				if(CntrConst.CNTRDOC_TYPE.PO.equals( (String)settingParam.get("cntrdoc_typ_ccd"))) {
					Map<String, Object> orderInfo = econtractEventPublisher.findOrderCntrInfo(settingParam);
					dynamicInfo = getOrderDynamicInfo(orderInfo, useDynamicTemplateClause);
				} else if(CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals( (String)settingParam.get("cntrdoc_typ_ccd"))) {
					Map<String, Object> priceInfo = econtractEventPublisher.findPriceCntrInfo(settingParam);
					dynamicInfo = getOrderDynamicInfo(priceInfo, useDynamicTemplateClause);
				}
				settingParam = mapMerge(settingParam, dynamicInfo);
			}

			Map clauseParam = Maps.newHashMap();
			clauseParam.put("tmpl_uuid", param.get("appx_tmpl_uuid"));
			List<Map<String,Object>> useClauseList = cntrTemplateService.findUseByCntrClause(clauseParam);
			// 첨부서식 생성
			ContractDocument contractDocument = new ContractDocument(editContent, useClauseList);
			contractDocument.putValueToTemplate(settingParam, GetValueType.CNTR_CL_AKA);
			contractDocument.activateEdit();
			String cntrAppxCrngCont = contractDocument.getDocument();
			String cntrAppxCcmpldCont = contractDocument.getDisAbleDocument();
			//입력해야할 항목이 있는지 확인한다.
			boolean isInputValue = contractDocument.isInputValue();
			String cntrAppxStsCcd = "";
			if(isInputValue){
				cntrAppxStsCcd = "N";
			}else{
				cntrAppxStsCcd = "Y";
			}
			param.put("cntr_appx_sts_ccd"  , cntrAppxStsCcd);
			param.put("cntr_appx_crng_cont", cntrAppxCrngCont);
			param.put("cntr_appx_ccmpld_cont", cntrAppxCcmpldCont);
		}

		param.put("cntr_appx_nm", param.get("appx_tmpl_nm"));
		param.put("cntr_appx_typ_ccd", param.get("appx_typ_ccd"));
		param.put("cntr_appx_crtr_typ_ccd", param.get("appx_crtr_typ_ccd"));

		econtractRepository.insertApp(param);
		econtractRepository.insertAppContent(param);

		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	/**
	 * 계약서식에 해당하는 첨부서식 조회
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List searchAtt(Map param) {
		//계약서식에 첨부된 리스트(현황)
		return econtractRepository.getCntrAppFormListInSts(param);
	}

	/**
	 * 계약서 첨부서식 삭제(계약현황)
	 * @param param
	 * @return
	 */
	public Map delAppFormInSts(Map param) {
		List<Map<String,Object>> delList = (List) param.get("delList");
		Map resultMap = Maps.newHashMap();
		for(Map row : delList){
			econtractRepository.deleteAttachDocumentByAppxId(row);
			econtractRepository.deleteAttachDocumentMgtByAppxId(row);
		}
		resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);
		return resultMap;
	}

	public Map<String, Object> mapMerge(Map<String, Object> resultMap, Map<String, Object> valueMap) {
		//
		Iterator<String> keys = valueMap.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			//MyBatis Number타입은 BigDecimal 타입으로 반환되기 오류가 발생할수도있음 - ex)Sybase 에서 integer 값이 BigDecimal 로 넘어오면서 DB 가 끈기는 현상이 발생
			String value="";
			if(valueMap.get(key) != null) {
				value = valueMap.get(key).toString();
			}
			//String value = String.valueOf(valueMap.get(key));
			if(Strings.isNullOrEmpty(value) || "null".equals(value)){
				value = "";
			}
			resultMap.put(key, value);
		}

		return resultMap;
	}

	public Map checkedSign(Map param) {
		Map result = Maps.newHashMap();
		//업체서명조회
		result.put("ownerList", econtractRepository.selectCheckedSignForOwner(param));
		//협력사서명조회
		result.put("vendorList", econtractRepository.selectCheckedSignForVendor(param));

		return result;
	}

	/**
	 * 서명한 인증서 정보 가져오기(서명값 및 위변조 검증 포함)
	 *
	 * @author : sunghyun kang
	 * @param param the param
	 * @return the map
	 * @throws Exception
	 * @Date : 2018. 01. 09
	 * @Method Name : getCertInfoView
	 */
	public ResultMap getCertInfoView(Map<String, Object> param) {
		Map result = Maps.newHashMap();
		param.put("signValue", econtractRepository.getSignValue(param));
		param.put("signedFileGrpCd", param.get("athg_uuid"));

		try{
			// DB 검증
			result = verifyAndGetCertInfoFromDB(param);
			result.put("ctry_ccd"         , (String) param.get("ctry_ccd"));
			result.put("sgndusr_typ_ccd"   , (String) param.get("sgndusr_typ_ccd"));
			result.put("blockChainUseYn", "N");

			// BLOCK 검증
			if("Y".equals(blockChainUseYn) && "Y".equals(param.get("ecntr_use_yn")) && CntrConst.TMPL_TYPE.TEMPLATE.equals(param.get("cntr_tmpl_typ_ccd"))){
				ResultMap eventResultMap = econtractEventPublisher.verifyAndGetBlockInfo(param);
				return eventResultMap;
			}
		}catch(Exception e){
			LOG.error("class : " + this.getClass().toString() + "getCertInfoView error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".getCertInfoView() : " + e.getMessage()+ "fail to verify");
		}
		
		return ResultMap.SUCCESS(result);
	}

	/**
	 * DB에 있는 서명값 검증
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> verifyAndGetCertInfoFromDB(Map<String,Object> param) throws Exception{
		Map<String, String> certInfo = Maps.newHashMap();
		String signValue = (String) param.get("signValue");
		String signedFileGrpCd = (String) param.get("signedFileGrpCd");
		String hashValueFromSignValue = "";

		FileItem fileItem = this.getFileItemByGrpCd(signedFileGrpCd);
		String hashValueFromFile = SignatureUtil.getHashValueFromFile(fileItem);

		// 서명값 검증
		if("BUYER".equals(param.get("sgndusr_typ_ccd")) || "N".equals(blockChainUseYn)) {
			// 갑 또는 을(국내)인 경우
			certInfo = verificationProvider.getCertInfo(signValue);
			hashValueFromSignValue = verificationProvider.getSource(signValue);

			if(certInfo == null || !hashValueFromSignValue.equals(hashValueFromFile)){
				throw new CommonException(this.getClass().getName() + ".verifyAndGetCertInfoFromDB() : fail to verify(KR)" + "hashValues aren't eqaul"+
						"hashValue from signValue(DB) : " + hashValueFromSignValue + " , " +
						"hashValue from file : " + hashValueFromFile);
			}
		} else {
			// 을(해외)인 경우
			ResultMap eventResultMap = econtractEventPublisher.getSignSourceFromSignValue(signValue);
			Map resultData = eventResultMap.getResultData();
			hashValueFromSignValue = (String)resultData.get("signValue");

			if(!hashValueFromSignValue.equals(hashValueFromFile)){
				throw new CommonException(this.getClass().getName() + ".verifyAndGetCertInfoFromDB() : fail to verify(not KR)", "hashValues aren't eqaul"+
						"hashValue from signValue(DB) : " + hashValueFromSignValue + " , " +
						"hashValue from file : " + hashValueFromFile);
			}
		}
		return certInfo;
	}

	/**
	 * 기타첨부문서 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveCntrEtcFile(Map param) {
		econtractRepository.udpateCntrEtcFile(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 계약서 PDF 생성하기(PD4ML + Aspose Lib 이용)
	 * @param param   : 계약정보(cntr_no, cntr_rev 필수)
	 * @param sign    : 서명여부
	 * @param horizon : 수평생성여부
	 * @return
	 */
	public Map<String,Object> generateCntrPdf(Map<String,Object> param, boolean sign, boolean horizon){
		Map<String,Object> cntrInfo = econtractRepository.getCntrContent(param);
		List<Map<String, Object>> appList = econtractRepository.getAppContent(param);
		CertInfo certInfo = null;

		if(sign) {
			String cntrdocCcmpldCont = replaceSignDate((String)cntrInfo.get("cntrdoc_ccmpld_cont"));

			cntrInfo.put("sign_source", cntrdocCcmpldCont);	//변경된 계약서 서명일자를 업데이트
			cntrInfo.put("content", cntrdocCcmpldCont);		//변경된 계약서 서명일자를 업데이트

			econtractRepository.updateBeforeSignContent(cntrInfo);

			String logicOrgCd = (String)cntrInfo.get("logic_org_cd");
			certInfo = certMgtService.getCertInfo(logicOrgCd);
		}

		Map<String,Object> pdfAttach = pdfMakerService.generatePdfUsingHtml(cntrInfo, appList, sign, certInfo, horizon);
		return pdfAttach;
	}

	/**
	 * @param : param(Map) 계약서식정보(cntrdoc_tmpl_uuid, cntr_no, cntr_rev 필수)
	 * @Param attrList   : 계약항목
	 * 첨부서식 저장
	 */
	public void insertAppList(Map<String,Object> cntrInfo, Map<String,Object> etcInfo, List<Map<String,Object>> appList, List<Map<String,Object>> attrList){
		if(cntrInfo.get("cntrdoc_tmpl_uuid") == null ){
			throw new CommonException(this.getClass().getName() + ".insertAppList()" + "cntrdoc_tmpl_uuid is required " + cntrInfo.toString());
		}

		Map<String,Object> userInfo = Auth.getCurrentUserInfo();
		if(!appList.isEmpty()){ //첨부서류가 있는 경우
			for(int j=0 ; j  < appList.size() ; j++){
				Map app = appList.get(j);

				app.put("cntr_appx_typ_ccd", app.get("appx_typ_ccd"));
				app.put("cntr_appx_crtr_typ_ccd", app.get("appx_crtr_typ_ccd"));
				app.put("cntr_appx_ord", app.get("cntrdoc_tmpl_ord"));

				app.put("ecntr_uuid", cntrInfo.get("ecntr_uuid"));
				app.put("ecntr_no" , cntrInfo.get("ecntr_no"));
				app.put("ecntr_revno", cntrInfo.get("ecntr_revno"));
				app.put("usr_id"  , userInfo.get("usr_id"));

				String appNo = UUID.randomUUID().toString();
				app.put("cntr_appx_uuid", appNo);

				if("TXT".equals(app.get("cntr_appx_typ_ccd"))){

					Map clauseParam = Maps.newHashMap();
					clauseParam.put("tmpl_uuid", app.get("appx_tmpl_uuid"));
					List<Map<String,Object>> useClauseList = cntrTemplateService.findUseByCntrClause(clauseParam);
					Map dynamicInfo = Maps.newHashMap();
					clauseParam.put("dat_typ_ccd", "TMPL");
					List<Map<String,Object>> useDynamicTemplateClause = cntrTemplateService.findUseByCntrClause(clauseParam);
					if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrInfo.get("cntrdoc_typ_ccd"))) {
						dynamicInfo =  getOrderDynamicInfo(cntrInfo, useDynamicTemplateClause);
					}else if(CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(cntrInfo.get("cntrdoc_typ_ccd"))) {
						dynamicInfo =  getPriceDynamicInfo(cntrInfo, useDynamicTemplateClause);
					}
					Map bindingObject = mapMerge(etcInfo,dynamicInfo);

					String appEditContent = (String) app.get("edit_content");
					ContractDocument contractDocument = new ContractDocument(appEditContent, useClauseList);
					contractDocument.putValueToTemplate(bindingObject, GetValueType.CNTR_CL_AKA);
					contractDocument.activateEdit();
					String cntrAppxCrngCont = contractDocument.getDocument();
					String cntrAppxCcmpldCont = contractDocument.getDisAbleDocument();

					app.put("cntr_appx_ccmpld_cont", cntrAppxCcmpldCont);
					app.put("cntr_appx_crng_cont", cntrAppxCrngCont);
					//입력해야할 항목이 있는지 확인한다.
					boolean isInputValue = contractDocument.isInputValue();
					String cntrAppxStsCcd = "";
					if(isInputValue){
						cntrAppxStsCcd = "N";
					}else{
						cntrAppxStsCcd = "Y";
					}
					app.put("cntr_appx_sts_ccd", cntrAppxStsCcd);

				}else{
					app.put("app_content","");
					app.put("content","");
				}

				//첨부서식(파일)이 있는 경우
				if("FILE".equals(app.get("cntr_appx_typ_ccd")) || "FILE_LIST".equals(app.get("cntr_appx_typ_ccd"))) {
					String grpCd = null;

					if(app.get("athg_uuid") != null &&  !"".equals(app.get("athg_uuid"))) {
						List<Map<String,Object>> fileList = attachService.findListAttach(app);
						if(fileList != null && fileList.size() > 0) {
							grpCd = fileService.copyFile((String) app.get("athg_uuid"));
							app.put("cntr_appx_sts_ccd"  , "Y");
						} else {
							app.put("cntr_appx_sts_ccd"  , "N");
						}
					} else {
						app.put("cntr_appx_sts_ccd"  , "N");
					}
					app.put("cntr_athg_uuid" , grpCd);
				}

				econtractRepository.insertApp(app);
				econtractRepository.insertAppContent(app);
			}
		}
	}

	private Map getPriceDynamicInfo(Map<String, Object> priceInfo, List<Map<String, Object>> useDynamicTemplateClauseList) {

		Map<String, Object> dynamicBindingObject = Maps.newHashMap();
		for (Map<String, Object> useDynamicTemplateClause : useDynamicTemplateClauseList) {
			String cntrClAka = (String) useDynamicTemplateClause.get("cntr_cl_aka");

			if ("dynamic_price_item".equals(cntrClAka)) {
				List<Map<String, Object>> payTermList = econtractEventPublisher.searchPriceItemList(priceInfo);
				Map data = Maps.newHashMap();
				data.put("PRICE_ITEM_LIST", payTermList);
				dynamicBindingObject.put(cntrClAka, data);
			}
		}
		return dynamicBindingObject;
	}

	private Map getOrderDynamicInfo(Map<String, Object> orderInfo, List<Map<String,Object>> useDynamicTemplateClauseList) {

		Map<String, Object> dynamicBindingObject = Maps.newHashMap();
		for (Map<String, Object> useDynamicTemplateClause : useDynamicTemplateClauseList) {
			String cntrClAka = (String) useDynamicTemplateClause.get("cntr_cl_aka");

			if ("dynamic_pay_contidion".equals(cntrClAka)) {
				List<Map<String, Object>> payTermList = econtractEventPublisher.getPayTermList(orderInfo);
				Map data = Maps.newHashMap();
				Map payInfo = Maps.newHashMap();
				data.put("pay_term_list", payTermList);
				data.put("pay_term_info", payInfo);
				dynamicBindingObject.put(cntrClAka, data);
			} else if ("dynamic_po_item".equals(cntrClAka)) {
				List<Map<String, Object>> poItemList = econtractEventPublisher.getPoItemList(orderInfo);
				Map data = Maps.newHashMap();
				Map poInfo = Maps.newHashMap();
				data.put("po_item_list", poItemList);
				data.put("po_info", poInfo);
				dynamicBindingObject.put(cntrClAka, data);
			} else if ("dynamic_vendor_list".equals(cntrClAka)) {
				List<Map<String, Object>> vendorList = econtractRepository.mappingVendorList(orderInfo);
				Map data = Maps.newHashMap();
				data.put("MAPPING_VENDOR_LIST", vendorList);
				dynamicBindingObject.put(cntrClAka, data);
			} else if ("dynamic_rem".equals(cntrClAka)) {
				List<Map<String, Object>> vendorList = econtractRepository.mappingVendorList(orderInfo);
				Map data = Maps.newHashMap();
				data.put("CONSORTIUM_REM", vendorList);
				dynamicBindingObject.put(cntrClAka, data);
			}
		}
		return dynamicBindingObject;
	}

	/**
	 * @param cntrInfo(Map) : (cntr_dt, cntr_st_dt, cntr_exp_dt)
	 * 계약일, 계약시작일, 계약만료일을 체크하여 null일 경우 오늘날짜로 표시
	 */
	public Map<String,Object> checkAndInputDate(Map<String,Object> cntrInfo){
		DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
		Date date = new Date();

		//계약일
		if(cntrInfo.get("cntr_dt") == null) {
			cntrInfo.put("cntr_dt", format.format(date).toString());
		}
		//계약시작일
		if(cntrInfo.get("cntr_st_dt") == null) {
			cntrInfo.put("cntr_st_dt", format.format(date).toString());
		}
		//종료일
		if(cntrInfo.get("cntr_exp_dt") == null) {
			Calendar cal = Calendar.getInstance();

			cal.setTime(date);
			cal.add(Calendar.DATE, 364);

			cntrInfo.put("cntr_exp_dt", format.format(cal.getTime()));		// cntr_exp_dt 는 SYSDATE+364
		}

		return cntrInfo;
	}

	/**
	 * @param cntrInfo : 계약정보
	 * @return : 채번된 계약번호(cntr_no), 계약차수(cntr_rev)
	 */
	public Map<String,Object> getCntrDocNo(Map<String,Object> cntrInfo){
		String refCd = (String) cntrInfo.get("ref_cntr_no");
		String uuid = UUID.randomUUID().toString();
		cntrInfo.put("ecntr_uuid", uuid);

		if(StringUtils.isNotEmpty(refCd)){
			// 발주, 단가 (참조차수 존재시) 계약
			String cntrNo = econtractRepository.getCntrNo(refCd);

			if(StringUtils.isEmpty(cntrNo)) {
				// 전자계약 미존재시
				String ecntrNo = (String) cntrInfo.get("ecntr_no");
				if(StringUtils.isEmpty(ecntrNo)) {
					ecntrNo = sharedService.generateDocumentNumber("CTR");
				}

				cntrInfo.put("ecntr_no", ecntrNo);
				cntrInfo.put("ecntr_revno", 1);
			} else {
				// 전자계약 존재시 차수 + 1
				String cntrRevStr = econtractRepository.getCntrRev(cntrNo);

				cntrInfo.put("ecntr_no" , cntrNo);
				cntrInfo.put("ecntr_revno", Integer.parseInt(cntrRevStr) + 1);
			}

		} else {
			// 일반, 기본, 동반성장 계약
			cntrInfo.put("ecntr_no" , sharedService.generateDocumentNumber("CTR"));
			cntrInfo.put("ecntr_revno", "1");
		}

		return cntrInfo;
	}

	/** 전자서명 **/
	private String getSignValue(String signSource, String LogicOrgCd){
		CertInfo certInfo = certMgtService.getCertInfo(LogicOrgCd);

		String signValue = signatureProvider.getSignValue(signSource, certInfo);
		verificationProvider.verifySignValue(signValue, certVerifiable);
		return signValue;
	}

	private Map<String,Object> makeResultMap(String code){
		Map<String,Object> resultMap = Maps.newHashMap();
		resultMap.put(Const.RESULT_STATUS, code);
		return resultMap;
	}

	/**
	 * 비표준계약서 PDF 화면 출력
	 * @param request
	 * @param response
	 * @param cntrdocAthgUuid
	 */
	public void getNonStandardCntrPdf(HttpServletRequest request, HttpServletResponse response, String cntrdocAthgUuid) {
		OutputStream ops = null;
		Writer writer = null;

		try {
			FileList fileList = fileService.findFileListWithContents(cntrdocAthgUuid);

			if(fileList != null && fileList.getSize() > 0) {
				FileItem fileItem = fileList.getItems().get(0);
				byte[] pdfFileBytes  = fileItem.toByteArray();

				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename="
						+ URLEncoder.encode(fileItem.getName(), "UTF-8").replaceAll("\\+", "%20"));
				ops = response.getOutputStream();
				ops.write(pdfFileBytes);
				ops.flush();

			} else {
				writer = response.getWriter();
				response.setContentType("text/html");
				writer.write("PDF파일이 존재하지 않습니다.");
				writer.flush();
			}
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".getNonStandardCntrPdf() : Exception", e);
			throw new CommonException(this.getClass().getName() + ".getNonStandardCntrPdf() : " + e.toString());
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
				if(ops != null) {
					ops.close();
				}
			} catch (Exception e) {
				LOG.error(this.getClass().getName() + ".getNonStandardCntrPdf() : Exception", e);
			}
		}
	}

	/** 첨부파일 용량 체크**/
	public Map<String, Object> checkAppFileSize(Map<String, Object> param) {
		int fileSizeSum = 0;
		List<Map> cntrAppList = econtractRepository.getCntrAppList(param);
		for(Map cntrAppInfo : cntrAppList) {
			List<Map<String,Object>> fileList = attachService.findListAttach(cntrAppInfo);
			for(Map fileInfo : fileList) {
				if(fileInfo.get("athf_size") != null){
					fileSizeSum += Integer.parseInt(fileInfo.get("athf_size").toString());
				}else{
					LOG.error("class:" + this.getClass() + " checkAppFileSize() error : 계약서 내용이 없습니다.");
					throw new CommonException("계약서 내용이 없습니다.");
				}
			}
		}

//		List<Integer> fileSizeList = sqlSession.selectList("contract.getAppFileSizeList", param);
		boolean excess = false;

//		int fileSizeSum = 0;
//		for(int i=0; i<fileSizeList.size(); i++){
//			fileSizeSum += fileSizeList.get(i);
//		}

		int limitSize = 52428800; // 50 MB
		if(fileSizeSum > limitSize){
			excess = true; // 초과
		}

		Map<String,Object> resultMap = makeResultMap(Const.SUCCESS);
		resultMap.put("excess", excess);
		return resultMap;
	}

	/**
	 * 계약서에 계약일자를 서명일자로 변경
	 *
	 * @author : lee daesung
	 * @param : param the param
	 * @return the string
	 * @Date : 2020. 3. 05
	 * @Method Name : replaceSignDate
	 */
	private String replaceSignDate(String content) {

		if( Strings.isNullOrEmpty(content) ) {
			throw new CommonException("계약서 내용이 없습니다.");
		}
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		Date date = new Date();
		String signDate = format.format(date);

		/* 계약서 계약일자를 서명일자로 변환*/
		signDate = signDate.substring(0, 4) +  "년 " + signDate.substring(4, 6) + "월 " + signDate.substring(6, 8) + "일";
		// 계약서에 내용에 계약일자를 서명일자로 변경함. 예시: <span name="[[!@#sgn_dttm!@#]]">2019년 2월 3일</span>  -> <span name="[[!@#sgn_dttm!@#]]">2020년 3월 5일</span>
		result = DocumentUtil.replaceTagContent(content, "span", "[[!@#sgn_dttm!@#]]", signDate);

		return result;
	}

	/**
	 * 협력사 목록 정보조회(계약자)
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 11. 18
	 * @Method Name : getSupplierList
	 */
	public List<Map<String,Object>> getSupplierList(Map<String,Object> param){
		return econtractRepository.getSupplierList(param);
	}

	/**
	 * 계약서 내용 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 11. 18
	 * @Method Name : getCntrContent
	 */
	public Map<String,Object> getCntrContent(Map<String,Object> param){
		return econtractRepository.getCntrContent(param);
	}

	/**
	 * 첨부서류 내용 조회
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> getAppContent(Map<String,Object> param) {
		return econtractRepository.getAppContent(param);
	}

	/**
	 * 첨부서류 정보 가져오기
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the map
	 * @Date : 2020. 11. 18
	 * @Method Name : getCntrAppList
	 */
	public List<Map<String,Object>> getCntrAppList(Map<String,Object> param){
		return econtractRepository.getCntrAppList(param);
	}

	public Map<String,Object> findElecContract(Map<String,Object> param){
		return econtractRepository.getContract(param);
	}

	/**
	 * 첨부서 정보 조회
	 *
	 * @author :
	 * @param param the param
	 * @return the map
	 * @Date : 2022. 09. 30
	 * @Method Name : findCntrAppCont
	 */
	public Map<String,Object> findCntrAppCont(Map<String,Object> param) {
		Map<String,Object> result = Maps.newHashMap();
		Map<String,Object> cntrInfo = econtractRepository.getContract(param);
		//갑,을 회사 정보
		Map<String,Object> vdInfo = econtractRepository.getBasicVdInfo(cntrInfo);

		Map<String,Object> app = cntrTemplateService.findAppformCont(param);

		String appEditContent = (String) app.get("appx_tmpl_cont");
		if(Strings.isNullOrEmpty(appEditContent)){
			result.put(Const.RESULT_STATUS, Const.NOT_EXIST);
			return result;
		}
		Map clauseParam = Maps.newHashMap();
		clauseParam.put("tmpl_uuid", app.get("appx_tmpl_uuid"));
		List<Map<String,Object>> useClauseList = cntrTemplateService.findUseByCntrClause(clauseParam);
		ContractDocument contractDocument = new ContractDocument(appEditContent, useClauseList);

		Map bindingObject = mapMerge(cntrInfo, vdInfo);
		clauseParam.put("dat_typ_ccd", "TMPL");
		List<Map<String,Object>> useDynamicTemplateClause = cntrTemplateService.findUseByCntrClause(clauseParam);

		if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrInfo.get("cntrdoc_typ_ccd"))) {
			if(!useDynamicTemplateClause.isEmpty()) {
				Map dynamicInfo = getOrderDynamicInfo(cntrInfo, useDynamicTemplateClause);
				bindingObject = mapMerge(cntrInfo, dynamicInfo);
			}
		}else if(CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(cntrInfo.get("cntrdoc_typ_ccd"))) {
			if(!useDynamicTemplateClause.isEmpty()) {
				Map dynamicInfo = Maps.newHashMap();
				dynamicInfo =  getPriceDynamicInfo(cntrInfo, useDynamicTemplateClause);
				bindingObject = mapMerge(cntrInfo, dynamicInfo);
			}
		}

		contractDocument.putValueToTemplate(bindingObject, GetValueType.CNTR_CL_AKA);
		contractDocument.activateEdit();
		String cntrAppxCrngCont = contractDocument.getDocument();
		String cntrAppxCcmpldCont = contractDocument.getDisAbleDocument();
		result.put("cntr_appx_crng_cont", cntrAppxCrngCont);
		result.put("cntr_appx_ccmpld_cont", cntrAppxCcmpldCont);

		return result;
	}

	/**
	 * 그룹 코드로 파일 조회 (1건)
	 * @param grpCd
	 * @return
	 */
	public FileItem getFileItemByGrpCd(String grpCd) {
		FileList fileList;
		FileItem fileItem;
		try {
			fileList = fileService.findFileListWithContents(grpCd);
			fileItem = fileList.getItems().get(0);
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "getFileByGrpCd error : " + e.toString());
			throw new CommonException(this.getClass().getName() + ".getFileByGrpCd(String grpCd) : " + e.getMessage() + e.toString());
		}
		return fileItem;
	}

	/**
	 * 첨부서류 내용 업데이트
	 * @param : Map
	 */
	public Map<String,Object> generatePdfUsingHtml(Map<String,Object> cntrInfo, List<Map<String,Object>> appList, boolean sign, CertInfo certInfo, boolean horizon) {
		return pdfMakerService.generatePdfUsingHtml(cntrInfo, appList, sign, certInfo, horizon);
	}

	private String removeOperUnit(String oorgCd){
		if(oorgCd.indexOf("PO") != -1){
			oorgCd = oorgCd.replace("PO","");
		}else if(oorgCd.indexOf("EO") != -1){
			oorgCd = oorgCd.replace("EO","");
		}
		return oorgCd;
	}

	/**
	 * 계약서 작성 대상 구매사,협력사 업체정보 조회
	 * @param param
	 * @return
	 */
	public Map findBasicVdInfo(Map<String, Object> param) {
		//상위 company 코드를 찾아서 넣는다.
		String oorgCd = (String) param.get("oorg_cd");
		String logicOrgCd = this.removeOperUnit(oorgCd);
		Map rootLogicOrgCdMap = organizationManagerService.findRootLogicOrganizationInfo(logicOrgCd);

		param.put("co_cd", rootLogicOrgCdMap.get("logic_org_cd"));
		return econtractRepository.getBasicVdInfo(param);
	}

	public ResultMap createEcontract(Map param) {
		Map<String,Object> cntrParam = Maps.newHashMap();
		cntrParam.put("cntr_uuid", param.get("cntr_uuid"));
		
		Map cntrInfo = econtractRepository.findContract(cntrParam);
		List<Map> vendorList = contractService.findListContractor(cntrParam);
		
		/** 계약서 작성 **/
		cntrInfo.put("bat_cntr_grp_uuid", param.get("bat_cntr_grp_uuid"));
		ResultMap resultMap = this.createEContract(cntrInfo, vendorList);
		return resultMap;
	}

	private ResultMap createEContract(Map param, List<Map> vendorList) {
		Map userInfo = Auth.getCurrentUserInfo();

		// 계약정보 셋팅
		Map cntrInfo = Maps.newHashMap();
		String uuid = UUID.randomUUID().toString();
		cntrInfo.put("ecntr_uuid", uuid);
		cntrInfo.put("cntr_uuid", param.get("cntr_uuid"));
		cntrInfo.put("bat_cntr_grp_uuid", param.get("bat_cntr_grp_uuid"));

		cntrInfo.put("usr_id"  , userInfo.get("usr_id"));
		cntrInfo.put("usr_nm"  , userInfo.get("usr_nm"));
		cntrInfo.put("dept_cd" , userInfo.get("dept_cd"));
		cntrInfo.put("dept_nm" , userInfo.get("dept_nm"));
		cntrInfo.put("oorg_cd", param.get("oorg_cd"));
		cntrInfo.put("cntrdoc_tmpl_uuid", param.get("cntrdoc_tmpl_uuid"));
		cntrInfo.put("logic_org_cd", param.get("logic_org_cd"));
		cntrInfo.put("sgn_meth_ccd", param.get("cntr_sgnmeth_ccd"));
		cntrInfo.put("sgnord_typ_ccd", param.get("sgnord_typ_ccd"));
		cntrInfo.put("vd_cd", param.get("vd_cd"));
		cntrInfo.put("cntr_sts_ccd", CntrConst.CNTR_STATUS.CREATION); //TODO 확인

		String cntrTmplTypCcd = (String) param.get("cntr_tmpl_typ_ccd");
		cntrInfo.put("cntr_tmpl_typ_ccd", (String) param.get("cntr_tmpl_typ_ccd"));
		cntrInfo.put("dsgn_cntr_yn", "N"); //TODO 확인
		cntrInfo.put("orgn_tmpl_unud_athg_uuid", param.get("orgn_tmpl_unud_athg_uuid"));
		cntrInfo.put("cntrdoc_typ_ccd", param.get("cntrdoc_typ_ccd"));
		cntrInfo.put("cntr_cnd_uuid", param.get("cntr_cnd_uuid"));
		cntrInfo.put("cntr_nm", param.get("cntr_nm"));

		cntrInfo = this.checkAndInputDate(cntrInfo); // 계약날짜 셋팅

		// 계약마스터 저장
		econtractRepository.insertCntrMaster(cntrInfo);

		// 계약 내용 저장
		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(cntrTmplTypCcd)) {
			this.makeTemplateCntrDoc(cntrInfo);
		} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(cntrTmplTypCcd)) {
			this.makeUserFileCntrDoc(cntrInfo);
		}

		// 갑/을 정보 저장
		Map<String, Object> compInfo = Maps.newHashMap();
		compInfo.put("ecntr_uuid", uuid);
		compInfo.put("vd_cd", userInfo.get("co_cd"));
		compInfo.put("cntrr_typ_ccd", "Y");
		compInfo.put("usr_id", userInfo.get("usr_id"));
		compInfo.put("cntrr_eml", userInfo.get("eml"));
		compInfo.put("cntrr_mob", userInfo.get("mob"));
		compInfo.put("cntrr_nm", userInfo.get("usr_nm"));
		econtractRepository.insertContractor(compInfo);
		
		for(Map row : vendorList) {
			row.put("ecntr_uuid", uuid);
			row.put("cntrr_typ_ccd", "N");
			row.put("cntrr_nm", row.get("vd_nm"));
			econtractRepository.insertContractor(row);
		}
		
		return ResultMap.SUCCESS(cntrInfo);
	}

	private void makeTemplateCntrDoc(Map cntrInfo) {
		// 계약서에 맵핑 될 정보 조회
		Map bindingObject = Maps.newHashMap();
		bindingObject = mapMerge(bindingObject, cntrInfo);

		// 계약대상 협력사 정보를 가져온다
		Map bvInfo = this.findBasicVdInfo(cntrInfo);
		String vdBizRegNo = EdocStringUtil.formatSSN((String) bvInfo.get("bizregno"));
		bvInfo.put("bizregno", vdBizRegNo);
		String compBizRegNo = EdocStringUtil.formatSSN((String) bvInfo.get("comp_biz_reg_no"));
		bvInfo.put("comp_biz_reg_no", compBizRegNo);
		bindingObject = mapMerge(bindingObject, bvInfo);

		// 계약 조건 데이터 매핑
		String cntrdocTypCcd = (String) cntrInfo.get("cntrdoc_typ_ccd");
		String cntrCndId = (String) cntrInfo.get("cntr_cnd_uuid");
		Map cntrCndInfo = contractService.findCntrCnd(cntrdocTypCcd, cntrCndId);
		if(!MapUtils.isEmpty(cntrCndInfo)) {
			if(CntrConst.CNTRDOC_TYPE.PO.equals(cntrdocTypCcd) || CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(cntrdocTypCcd)) {
				Map purcInfo = Maps.newHashMap();
				Map purcCntrData = (Map) cntrCndInfo.get("purcCntrData");
				Map purcCntrInfoData = (Map) cntrCndInfo.get("purcCntrInfoData");
				List<Map> purcCntrItemList = (List<Map>) cntrCndInfo.get("purcCntrItemList");
				List<Map> purcCntrPymtExptList = (List<Map>) cntrCndInfo.get("purcCntrPymtExptList");

				bindingObject = mapMerge(bindingObject, purcCntrData);

				//purcCntrInfoData re-setting
				purcCntrInfoData = this.setPurcCntrInfoData(purcCntrInfoData);
				bindingObject = mapMerge(bindingObject, purcCntrInfoData);

				//purcCntrItemList re-setting
				if(purcCntrItemList.size() > 0) {
					Map<String, Object> purcItemInfo =  this.setPurcCntrItem(purcCntrItemList);
					bindingObject = mapMerge(bindingObject, purcItemInfo);
				}

				purcInfo.put("supl_amt_kr", EdocStringUtil.parseNumToKor(String.valueOf(purcCntrData.get("sup_amt")), ""));
				purcInfo.put("tax_kr"     , EdocStringUtil.parseNumToKor(String.valueOf(purcCntrData.get("vat_amt")), ""));
				purcInfo.put("cntr_amt_kr", EdocStringUtil.parseNumToKor(String.valueOf(purcCntrData.get("cntr_amt")), ""));
				bindingObject = mapMerge(bindingObject, purcInfo);

			} else if(CntrConst.CNTRDOC_TYPE.GENERAL.equals(cntrdocTypCcd)) {
				Map otrsInfo = Maps.newHashMap();
				Map otrsCntrData = (Map) cntrCndInfo.get("otrsCntrData");
				Map otrsCntrInfoData = (Map) cntrCndInfo.get("otrsCntrInfoData");
				List<Map> otrsCntrPymtExptList = (List<Map>) cntrCndInfo.get("otrsCntrPymtExptList");

				bindingObject = mapMerge(bindingObject, otrsCntrData);
				bindingObject = mapMerge(bindingObject, otrsCntrInfoData);

				otrsInfo.put("supl_amt_kr", EdocStringUtil.parseNumToKor(String.valueOf(otrsCntrData.get("sup_amt")), ""));
				otrsInfo.put("tax_kr"     , EdocStringUtil.parseNumToKor(String.valueOf(otrsCntrData.get("vat_amt")), ""));
				otrsInfo.put("cntr_amt_kr", EdocStringUtil.parseNumToKor(String.valueOf(otrsCntrData.get("cntr_amt")), ""));
				bindingObject = mapMerge(bindingObject, otrsInfo);
			}
		}

		/*Map clauseParam = Maps.newHashMap();
		clauseParam.put("tmpl_uuid", templateParamInfo.get("cntrdoc_tmpl_uuid"));

		clauseParam.put("dat_typ_ccd", "TMPL");
		List<Map<String,Object>> useDynamicTemplateClause = cntrTemplateService.findUseByCntrClause(clauseParam);
		Map dynamicInfo = getOrderDynamicInfo(makerInfo, useDynamicTemplateClause);

		bindingObject = mapMerge(bindingObject, dynamicInfo);*/
		
		// 계약서 템플릿 정보 조회
		String cntrdocLglRvUuid = (String) cntrInfo.get("cntrdoc_lgl_rv_uuid");
		Map mainTemplateInfo;
		if(Strings.isNullOrEmpty(cntrdocLglRvUuid)) {
			mainTemplateInfo = cntrTemplateService.findCntrFormCont(cntrInfo);
		} else {
			// 법무검토 템플릿 내용 조회
			mainTemplateInfo = econtractEventPublisher.findReviewCntrdocTmpl(cntrInfo);
		}
		String cntrdocCrngCont = (String) mainTemplateInfo.get("cntrdoc_tmpl_cont");
		cntrInfo.put("cntrdoc_crng_cont", cntrdocCrngCont);
		
		Map paramMap = Maps.newHashMap();
		paramMap.put("cntrInfo", cntrInfo);
		paramMap.put("etcInfo", bindingObject);
		this.insertCntrAndAppForm(paramMap);
	}

	private Map<String, Object> setPurcCntrInfoData(Map param) {
		Map<String, Object> purcCntrInfoData = param;

		//defpgur_pd_typ_ccd 하자보증이행기간, dlvymeth_ccd 납품방법, pymtmeth_ccd 지급방법
		String[] ccdArr = {"defpgur_pd_typ_ccd", "dlvymeth_ccd", "pymtmeth_ccd"};

		for(int i=0; i<ccdArr.length; i++) {
			String currKey = ccdArr[i];
			if(purcCntrInfoData.containsKey(currKey) && purcCntrInfoData.get(currKey) != null) {
				String ccd = this.getPurcCntrInfoCcd(currKey);
				String dtlcd = (String) param.get(currKey);

				String dtlcdNm = sharedService.findCodeName(dtlcd, ccd);

				if(currKey.equals("defpgur_pd_typ_ccd")) {
					BigDecimal defpgurPd = (BigDecimal) purcCntrInfoData.get("defpgur_pd");
					dtlcdNm = dtlcdNm + "로부터 " + defpgurPd + "개월";
					purcCntrInfoData.put("defpgur_period", dtlcdNm);
				}else if(currKey.equals("dlvymeth_ccd")) {
					String dlvymethExpln = (String) purcCntrInfoData.get("dlvymeth_expln");
					if(dlvymethExpln != null) {
						dtlcdNm += "(" + dlvymethExpln + ")";
					}
					purcCntrInfoData.put("trans_condition", dtlcdNm);
				} else if(currKey.equals("pymtmeth_ccd")) {
					String pymtmethExpln = (String) purcCntrInfoData.get("pymtmeth_expln");
					if(pymtmethExpln != null) {
						dtlcdNm += "(" + pymtmethExpln + ")";
					}
					purcCntrInfoData.put("pay_condition", dtlcdNm);
				}
			}
		}

		return purcCntrInfoData;
	}

	private String getPurcCntrInfoCcd(String key) {
		String ccd = "";
		if(key.equals("defpgur_pd_typ_ccd")) {
			ccd = CntrConst.CCD.DEFPGUR_PD_TYP_CCD; // 하자이행 보증 기간
		} else if(key.equals("dlvymeth_ccd")) {
			ccd = CntrConst.CCD.DLVYMETH_CCD;   // 납품방법
		} else if(key.equals("pymtmeth_ccd")) {
			ccd = CntrConst.CCD.PYMTMETH_CCD;   // 지급방법
		}
		return ccd;
	}

	private Map<String, Object> setPurcCntrItem(List<Map> list) {
		Map<String, Object> purcItemListInfo = new HashMap<>();
		Map<String, Object> purcRepItem = list.get(0);                          /* 대표 품목 LNO ASC */

		purcItemListInfo.put("dlvy_plc", purcRepItem.get("dlvy_plc"));          /* 납품장소 */
		purcItemListInfo.put("req_dlvy_dt", purcRepItem.get("req_dlvy_dt"));    /* 요청 납품일자 */
		purcItemListInfo.put("item_spec", purcRepItem.get("item_spec"));        /* 규격 */

		String itemNm = (String) purcRepItem.get("item_nm");                    /* 품목 명 */
		BigDecimal itemQty = (BigDecimal) purcRepItem.get("item_qty");          /* 수량 */

		// 품목 2건 이상
		if(list.size() > 1) {
			BigDecimal qty = BigDecimal.ZERO;
			for(Map item : list) {
				qty = qty.add((BigDecimal) item.get("item_qty"));
			}

			itemQty = qty;
			itemNm = itemNm + " 외 " + (list.size()-1) + "건";
		}

		purcItemListInfo.put("item_nm", itemNm);
		purcItemListInfo.put("item_qty", itemQty);

		return purcItemListInfo;
	}

	/**
	 * 계약서 및 첨부서식 생성 및 저장
	 * @param param
	 * @return
	 */
	public Map<String,Object> insertCntrAndAppForm(Map param) {
		Map<String,Object> cntrInfo = (Map<String, Object>) param.get("cntrInfo"); // 계약정보
		Map<String,Object> etcInfo  = (Map<String, Object>) param.get("etcInfo");  // 계약항목정보

		// 계약서 생성 및 저장
		String cntrdocCrngCont = (String)cntrInfo.get("cntrdoc_crng_cont");

		Map<String,Object> itemParam = Maps.newHashMap();
		itemParam.put("tmpl_uuid", cntrInfo.get("cntrdoc_tmpl_uuid"));
		List<Map<String, Object>> useClauseList = cntrTemplateService.findUseByCntrClause(itemParam);

		ContractDocument contractDocument = new ContractDocument(cntrdocCrngCont, useClauseList);
		contractDocument.putValueToTemplate(etcInfo, GetValueType.CNTR_CL_AKA);
		contractDocument.activateEdit();

		cntrdocCrngCont = contractDocument.getDocument();
		String cntrdocCcmpldCont = contractDocument.getDisAbleDocument();

		cntrInfo.put("cntrdoc_crng_cont", cntrdocCrngCont);
		cntrInfo.put("cntrdoc_ccmpld_cont", cntrdocCcmpldCont);
		econtractRepository.insertCntrContent(cntrInfo);

		// 첨부서식 생성 및 저장
		String cntrdocLglRvUuid = (String) cntrInfo.get("cntrdoc_lgl_rv_uuid");
		List<Map<String, Object>> attrList = cntrTemplateService.getAttrAll(param);
		List<Map<String,Object>> appList = Lists.newArrayList();

		if(Strings.isNullOrEmpty(cntrdocLglRvUuid)) {
			appList = cntrTemplateService.getAppList(cntrInfo);
			this.insertAppList(cntrInfo, etcInfo, appList, attrList);
		} else {
			// 법무검토 부속서류 조회
			appList = econtractEventPublisher.findListReviewAppxTmpl(cntrInfo);
			this.insertAppList(cntrInfo, etcInfo, appList, attrList);
		}

		return cntrInfo;
	}

	private void makeUserFileCntrDoc(Map cntrInfo) {
		try {
			String orgnAthgUuid = (String) cntrInfo.get("orgn_tmpl_unud_athg_uuid");
			FileList fileList = fileService.findFileListWithContents(orgnAthgUuid);

			if(fileList != null && fileList.getCount() > 0) {
				FileItem fileItem = fileList.getItems().get(0);
				String extension = fileItem.getExtension();
				String orgnFileNm = fileItem.getName();

				if("doc".equals(extension) || "docx".equals(extension) || "pdf".equals(extension)) {
					if("doc".equals(extension) || "docx".equals(extension)) {
						//Word 파일일 경우 PDF 파일로 변경하여 업데이트
						Map pdfFile = pdfMakerService.generatePdfUsingWord(orgnAthgUuid, orgnFileNm+".pdf");
						cntrInfo.put("tmpl_unud_cntrdoc_athg_uuid", pdfFile.get("athg_uuid"));
					} else {
						cntrInfo.put("tmpl_unud_cntrdoc_athg_uuid", fileService.copyFile(orgnAthgUuid));
					}
					econtractRepository.updateTemplateUnusedCntrdoc(cntrInfo);
					econtractRepository.insertCntrContent(cntrInfo);
				} else {
					throw new CommonException("STD.EDO1094"); // 비표준 계약서 파일 확장자는 doc, docx, pdf 만 가능 합니다.<br>파일 확장자 확인바랍니다.
				}
			} else {
				throw new CommonException("STD.E9999");	//오류가 발생하였습니다.<br/>관리자에게 문의하세요.
			}
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".makeUserFileCntrDoc() : Exception", e);
			throw new CommonException("STD.E9999");	//오류가 발생하였습니다.<br/>관리자에게 문의하세요.
		}
	}
	
	public Map findContractByEcntrId(Map param) {
		String ecntrId = (String) param.get("ecntr_uuid");
		String cntrId = econtractRepository.getCntrUUID(ecntrId);
		
		Map searchParam = Maps.newHashMap();
		searchParam.put("cntr_uuid", cntrId);
		Map cntrInfo = econtractRepository.findContract(searchParam);
		
		return cntrInfo;
	}

	public Map findEcontract(Map param) {
		//계약정보조회
		Map cntrInfo = econtractRepository.findContract(param);
		String wdPossYn = econtractRepository.findWdPossYnByLckdSts(cntrInfo);
		cntrInfo.put("wd_poss_yn", wdPossYn);
		
		String sgnMethCcd = (String) cntrInfo.get("cntr_sgnmeth_ccd");
		if(CntrConst.SIGN_METHOD.DOCUSIGN.equals(sgnMethCcd)) {
			//docusign 정보 조회
			Map docusignInfo = econtractEventPublisher.findDocusignContract(cntrInfo);
			if(docusignInfo != null) {
				cntrInfo.put("dsgn_uuid", docusignInfo.get("dsgn_uuid"));
				cntrInfo.put("dsgn_sts_ccd", docusignInfo.get("dsgn_sts_ccd"));
			}
		} else if(CntrConst.SIGN_METHOD.EFORM.equals(sgnMethCcd)) {
			//eformsign 정보 조회
			Map eformsignInfo = econtractRepository.findDocumentConts(cntrInfo);
			if(eformsignInfo != null) {
				cntrInfo.put("dgtlsgn_cntrdoc_tmpl_athg_uuid", eformsignInfo.get("dgtlsgn_cntrdoc_tmpl_athg_uuid"));
			}
		} else if(CntrConst.SIGN_METHOD.ADOBESIGN.equals(sgnMethCcd)) {
			//adobesign 정보 조회
			Map adobeSignInfo = econtractEventPublisher.findAdobeSignContract(cntrInfo);
			if(adobeSignInfo != null) {
				cntrInfo.put("asgn_uuid", adobeSignInfo.get("asgn_uuid"));
				cntrInfo.put("asgn_sts_ccd", adobeSignInfo.get("asgn_sts_ccd"));
				cntrInfo.put("agreement_id", adobeSignInfo.get("agt_id"));
			}
		}
		
		return cntrInfo;
	}

	public ResultMap deleteEcontract(Map param) {
		Map cntrInfo = econtractRepository.findContract(param);

		// 계약 서명방법 별 계약 정보 삭제
		String sgnMethCcd = (String) cntrInfo.get("cntr_sgnmeth_ccd");
		if(CntrConst.SIGN_METHOD.DOCUSIGN.equals(sgnMethCcd)) {
			econtractEventPublisher.deleteDocusignContract(cntrInfo);
		} else if(CntrConst.SIGN_METHOD.ADOBESIGN.equals(sgnMethCcd)) {
			econtractEventPublisher.deleteAdobeSignContract(cntrInfo);
		}

		// 전자 계약 삭제
		econtractRepository.deleteContractor(cntrInfo);
		econtractRepository.deleteContractDocument(cntrInfo);
		econtractRepository.deleteAttachDocument(cntrInfo);
		econtractRepository.deleteAttachDocumentMgt(cntrInfo);
		econtractRepository.deleteSignatureValue(cntrInfo);
		econtractRepository.deleteContract(cntrInfo);
		
		return ResultMap.SUCCESS();
	}

	public Map findEcontractDetail(Map param) {
		Map result = Maps.newHashMap();
		
		//계약서 내용
		Map cntrCont = econtractRepository.cntrView(param);
		//부속서류 리스트
		List cntrAppFormList = econtractRepository.getCntrAppFormListInSts(param);

		result.put("cntrdoc_ccmpld_cont", cntrCont.get("cntrdoc_ccmpld_cont"));
		result.put("cntrdoc_crng_cont", cntrCont.get("cntrdoc_crng_cont"));
		if(cntrAppFormList.size() > 0) {
			result.put("appFormList", cntrAppFormList);
		}

		Map itemParam = Maps.newHashMap();
		itemParam.put("use_yn", "Y");
		itemParam.put("mod_poss_yn", "Y");
		itemParam.put("mand_yn", "Y");
		itemParam.put("tmpl_uuid", cntrCont.get("cntrdoc_tmpl_uuid"));
		result.put("itemList", cntrTemplateService.findUseByCntrClause(itemParam));

		return result;
	}
	
	public ResultMap signEcontract(Map param) {
		ResultMap resultMap = this.signEcontractDoc(param);
		
		if(resultMap.isSuccess()) {
			String cntrUUID = (String) param.get("cntr_uuid");
			String ecntrUUID = econtractRepository.getEcntrUUID(cntrUUID);
			String signState = this.checkVendorsSignState(ecntrUUID);

			if(CntrConst.VENDORS_SIGN_STATUS.ALL_SIGN.equals(signState)) {
				contractService.completeContract(cntrUUID);
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 협력사 서명상태 체크
	 * @param ecntrId
	 * @return
	 */
	public String checkVendorsSignState(String ecntrId) {
		return econtractRepository.getSignState(ecntrId);
	}

	public ResultMap signEcontractDoc(Map param) {
		Map userInfo = Auth.getCurrentUserInfo();
		Map cntrInfo = econtractRepository.findContract(param);
		String sgncmpldCntrdocAthgUuid = (String) cntrInfo.get("sgncmpld_cntrdoc_athg_uuid");

		if( Strings.isNullOrEmpty(sgncmpldCntrdocAthgUuid) ) {
			boolean sign = true;
			Map<String,Object> signSource = Maps.newHashMap();

			String tmplTypCcd = (String) cntrInfo.get("cntr_tmpl_typ_ccd");
			if(CntrConst.TMPL_TYPE.TEMPLATE.equals(tmplTypCcd)) {
				signSource = this.generateCntrPdf(cntrInfo, sign, false);
			} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(tmplTypCcd)) {
				signSource = this.generateCntrPdfByUserFile(cntrInfo, sign);
			}
			sgncmpldCntrdocAthgUuid = (String) signSource.get("athg_uuid");
			cntrInfo.put("sgncmpld_cntrdoc_athg_uuid", sgncmpldCntrdocAthgUuid);
			econtractRepository.updateCntrFileHash(cntrInfo);
		}

		FileItem fileItem = this.getFileItemByGrpCd(sgncmpldCntrdocAthgUuid);
		String hashValue = SignatureUtil.getHashValueFromFile(fileItem);

		String operOrgCd = (String) cntrInfo.get("oorg_cd");
		List operUnitList = operationUnitManagerService.findListOperationUnit(null);
		String logicOrgCd = DocumentUtil.removeOunitCd(operOrgCd,operUnitList);

		String signValue = this.getSignValue(hashValue, logicOrgCd);

		Map<String, Object> compInfo = this.findBasicVdInfo(cntrInfo);

		Map signValueMap = Maps.newHashMap();
		signValueMap.put("joint_cert_sgn_val_uuid", UUID.randomUUID().toString());
		signValueMap.put("ecntr_uuid", cntrInfo.get("ecntr_uuid"));
		signValueMap.put("sign_value", signValue);
		signValueMap.put("usr_id", userInfo.get("usr_id"));
		signValueMap.put("sgndusr_typ_ccd", "BUYER");
		signValueMap.put("signer_biz_reg_no", compInfo.get("comp_biz_reg_no"));
		econtractRepository.insertSignValue(signValueMap);

		this.updateBuyerSignYN((String) cntrInfo.get("ecntr_uuid"));

		return ResultMap.SUCCESS();
	}

	/**
	 * 사용자 파일 유형의 계약서 PDF 생성
	 * @param param
	 * @param sign
	 * @return
	 */
	public Map<String, Object> generateCntrPdfByUserFile(Map param, boolean sign) {
		Map<String, Object> cntrInfo = econtractRepository.getNonStandardSignContent(param);
		String cntrdocAthgUuid = (String) cntrInfo.get("tmpl_unud_cntrdoc_athg_uuid");
		String appxAthgUuid = (String) cntrInfo.get("tmpl_unud_appx_athg_uuid");

		CertInfo certInfo = null;
		if(sign) {
			List operUnitList = operationUnitManagerService.findListOperationUnit(null);
			String logicOrgCd = DocumentUtil.removeOunitCd((String) cntrInfo.get("oorg_cd"), operUnitList );
			certInfo = certMgtService.getCertInfo(logicOrgCd);
		}

		Map<String, Object> pdfInfo = pdfMakerService.generatePdfUsingFile(cntrInfo, cntrdocAthgUuid, appxAthgUuid, sign, certInfo);
		return pdfInfo;
	}

	/**
	 * PDF 미리보기
	 * @param request
	 * @param response
	 * @param ecntrId
	 */
	public void previewPdfByEcntrId(HttpServletRequest request, HttpServletResponse response, String ecntrId) {
		Map<String, Object> pdfInfo = new HashMap<String, Object>();
		pdfInfo.put("ecntr_uuid", ecntrId);

		Map<String,Object> cntrInfo = econtractRepository.getCntrContent(pdfInfo);
		List<Map<String, Object>> appList = econtractRepository.getAppContent(pdfInfo);

		String orgnFileNm = cntrInfo.get("ecntr_no") + "-" + cntrInfo.get("ecntr_revno") + ".pdf";

		OutputStream ops = null;
		Writer writer = null;
		FileInputStream fis = null;
		byte[] pdfFileBytes;
		File pdfFile = null;

		try {
			String pdfFilePath = pdfMakerService.makePdfUsingHtml(cntrInfo, appList, false);
			pdfFile = new File(pdfFilePath);

			if(pdfFile.exists()) {
				fis = new FileInputStream(pdfFile);
				pdfFileBytes = IOUtils.toByteArray(fis);

				response.setContentType("application/pdf; charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename="
						+ URLEncoder.encode(orgnFileNm, "UTF-8").replaceAll("\\+", "%20"));
				ops = response.getOutputStream();
				ops.write(pdfFileBytes);
				ops.flush();
			} else{
				writer = response.getWriter();
				response.setContentType("text/html");
				writer.write("PDF파일이 존재하지 않습니다.");
				writer.flush();
			}

		} catch (Exception e) {
			response.setHeader("Set-Cookie", "fileDownload=false; path=/");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Content-Type","text/html; charset=utf-8");
			throw new CommonException(this.getClass().getName()+".previewPdfByEcntrId : " + e.getMessage(), e.toString());
		} finally {
			try{
				if(null != fis) fis.close();
				if(null != writer) writer.close();
				if(null != ops) ops.close();
				if(pdfFile.exists()) pdfFile.delete(); // 임시파일 삭제
			} catch (Exception e) {
				throw new CommonException(this.getClass().getName() + ".previewPdfByEcntrId() : Exception", e.getMessage());
			}
		}
	}

	/**
	 * 서명 순서 조회
	 * @param cntrdocTmplId
	 * @return
	 */
	public SignOrder getSignOrder(String cntrdocTmplId) {
		SignOrder signOrder = signOrderService.getSignOrder(GetSignOrderType.BY_SETUP, cntrdocTmplId);
		return signOrder;
	}

	/**
	 * 구매사 서명 여부 업데이트
	 * @param ecntrId
	 */
	public void updateBuyerSignYN(String ecntrId) {
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", ecntrId);
		paramMap.put("sgn_yn", "Y");
		econtractRepository.updateBuyerSignYN(paramMap);
	}

	/**
	 * 계약서 PDF 생성하기(PD4ML + Aspose Lib 이용)
	 * @param : 계약정보(ecntr_uuid)
	 * @return : pdf 파일 경로
	 */
	public String generateCntrPdf(Map<String,Object> param){
		Map<String,Object> cntrInfo = econtractRepository.getCntrContent(param);
		List<Map<String, Object>> appList = econtractRepository.getAppContent(param);
		return pdfMakerService.makePdfUsingHtml(cntrInfo, appList, false);
	}
	
	/**
	 * 부속서류 순서 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveCntrAppxOrd(Map param) {
		List<Map> appxList = (List<Map>) param.get("appxList");
		for(Map appx : appxList) {
			econtractRepository.updateAppSortSeq(appx);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 전자 계약서 회수
	 * @param param
	 * @return
	 */
	public ResultMap withdrawalEcontract(Map param) {
		Map<String, Object> cntrInfo = econtractRepository.findContract(param);

		// 구매사 서명여부 상태 변경
		cntrInfo.put("sgn_yn", "N");
		econtractRepository.updateBuyerSignYN(cntrInfo);

		String cntrSgnmethCcd = (String) cntrInfo.get("cntr_sgnmeth_ccd");
		if(CntrConst.SIGN_METHOD.PKI.equals(cntrSgnmethCcd)) {
			this.modifyCntrSignInfo(cntrInfo);
		} else if(CntrConst.SIGN_METHOD.DOCUSIGN.equals(cntrSgnmethCcd)) {
			// docusign 상태 변경
			econtractEventPublisher.updateDocusignStatus(cntrInfo);
		} else if(CntrConst.SIGN_METHOD.ADOBESIGN.equals(cntrSgnmethCcd)) {
			// adobesign 계약서 삭제
			econtractEventPublisher.deleteAdobeSignInfo(cntrInfo);
		} else if(CntrConst.SIGN_METHOD.EFORM.equals(cntrSgnmethCcd)) {
			this.modifyCntrSignInfo(cntrInfo);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 전자계약 서명 정보 수정
	 * @param param
	 * @return
	 */
	private void modifyCntrSignInfo(Map cntrInfo) {
		econtractRepository.deleteSignatureValue(cntrInfo);     // 서명 값 삭제
		econtractRepository.updateSgncmpldAthgUuid(cntrInfo);   // 서명완료 계약서 첨부그룹 reset
	}

	/**
	 * 서명잠금상태 조회
	 * @param param
	 * @return
	 */
	public String findWdPossYnByLckdSts(Map param) {
		return econtractRepository.findWdPossYnByLckdSts(param);
	}

	/**
	 * 계약 일괄 다운로드 > pdf 리스트 가져오기
	 */
	public File downloadAllCntr(Map param) {
		String[] cntrList = (String[]) param.get("cntrList");
		FileList pdfList = new FileList();

		for(int i=0; i< cntrList.length; i++) {
			String athgUuid = econtractRepository.getCntrPdfList((String) cntrList[i]);
			try {
				FileList fileList = fileService.findFileListWithContents(athgUuid);
				pdfList.add(fileList.getItems().get(0));
			} catch(Exception e) {
				LOG.error("class : " + this.getClass().toString() + "downloadAllCntr error : " + e.toString());
				throw new CommonException(this.getClass().toString() + "downloadAllCntr : " + e.getMessage() + e.toString());
			}
		}

		File file = new File(AttachUtils.getZipFile(fileUploadPath, pdfList));
		return file;
	}
}