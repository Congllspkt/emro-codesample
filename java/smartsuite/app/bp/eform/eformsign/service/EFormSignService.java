package smartsuite.app.bp.eform.eformsign.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.eform.eformsign.repository.EFormSignRepository;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.shared.CntrConst;
import smartsuite.exception.CommonException;
import smartsuite.security.core.crypto.AESCipherUtil;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;
import smartsuite.upload.entity.SimpleMultipartFileItem;
import smartsuite.upload.util.AthfServiceUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 간편서명 관련 처리하는 서비스 Class입니다.
 *
 * @FileName EFormSignService.java
 */
@Service
@Transactional
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EFormSignService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EFormSignService.class);
	
	@Value("#{mail['mail.session.sign.url']}")
	private String sessionSignUrl;
	@Value("#{mail['mail.nosession.sign.url']}")
	private String nosessionSignUrl;
	@Inject
	EFormSignRepository eFormSignRepository;
	@Inject
	ContractService contractService;
	@Inject
	EcontractService econtractService;
	@Inject
	StdFileService fileService;
	@Inject
	PdfFlattenService pdfFlattenService;
	@Inject
	MailService mailService;

	
	/**
	 * 간편서명 서식 대상 파일(PDF) 조회
	 * 
	 * @param resquest
	 * @param response
	 * @param grpCd 파일 그룹 코드
	 */
	public void getEFormPdf(HttpServletRequest resquest, HttpServletResponse response, String grpCd) {
		OutputStream ops = null;
		Writer writer = null;
		byte[] pdfFileBytes = null;

		try {
			FileList fileList = fileService.findFileListWithContents(grpCd);
			
			if(fileList.getSize() > 0) {
				FileItem fileItem = fileList.getItems().get(0);
				pdfFileBytes = fileItem.toByteArray();
				
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
			LOG.error(this.getClass().getName() + ".getEFormPdf() : Exception", e);
			throw new CommonException(this.getClass().getName() + ".getEformPdf() : " + e.toString());
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
				if(ops != null) {
					ops.close();
				}
			} catch (Exception e) {
				LOG.error(this.getClass().getName() + ".getEFormPdf() : Exception", e);
			}
		}
	}

	/**
	 * Json Data에서 서명 대상자 추출
	 * 
	 * @param param
	 * @param signTarget
	 * @return
	 */
	public List<Map<String, Object>> findSignTargetFromJsonData(Map<String, Object> param, Map<String, Object> signTarget) {
		Map<String, Object> jsonData = getMapFromJsonObject((String) param.get("dgtlsgn_cntrdoc_lyt_attr")); // Json Parsing
		List<Map<String, Object>> ownerList = (List<Map<String, Object>>) jsonData.get("ownerList");
		List<Map<String, Object>> jsonList = Lists.newArrayList();

		// target ID 값 동일한 것 추출
		for (Map owner : ownerList) {
			String cntorNo = signTarget.get("eform_usr_id").toString();
			if (cntorNo.equals(owner.get("eform_usr_id"))) {
				jsonList.add(owner);
			}
		}

		return jsonList;
	}

	/**
	 * Json Data 를 Map으로 반환
	 * 
	 * @param sjson
	 * @return
	 */
	private static Map getMapFromJsonObject(String sjson) {
		Map map = null;
		try {
			map = new ObjectMapper().readValue(sjson, Map.class);
		} catch (JsonParseException e) {
			LOG.error("EFormSignService.getMapFromJsonObject error : " + e.toString());
			e.printStackTrace();
		} catch (JsonMappingException e) {
			LOG.error("EFormSignService.getMapFromJsonObject error : " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("EFormSignService.getMapFromJsonObject error : " + e.toString());
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 간편 서명 Template 조회
	 * @param param
	 * @return
	 */
	public ResultMap findEFormSignTemplate(Map param) {
		// 계약 정보
		Map cntrInfo = econtractService.findContractByEcntrId(param);
		// 간편서명 계약서 내용
		Map documentConts = eFormSignRepository.findDocumentConts(param);
		// 서명자 리스트
		List<Map> signerList = eFormSignRepository.findDocumentUserList(param);
		// 계약 항목 리스트
		List<Map> clauseList = eFormSignRepository.findListUseClause(cntrInfo);
		
		String tmplTypCcd = (String) cntrInfo.get("cntr_tmpl_typ_ccd");
		String tmplAthgId = (String) documentConts.get("dgtlsgn_cntrdoc_tmpl_athg_uuid");

		// 간편서명 계약서 템플릿 PDF 파일이 없는 경우 생성
		if(Strings.isNullOrEmpty(tmplAthgId)) {
			Map<String,Object> pdfInfo = Maps.newHashMap();
			if(CntrConst.TMPL_TYPE.TEMPLATE.equals(tmplTypCcd)) {
				pdfInfo = econtractService.generateCntrPdf(cntrInfo, false, false);
			} else if(CntrConst.TMPL_TYPE.USR_FILE.equals(tmplTypCcd)) {
				pdfInfo = econtractService.generateCntrPdfByUserFile(cntrInfo, false);
			} else {
				return ResultMap.FAIL();
			}
			
			tmplAthgId = (String) pdfInfo.get("athg_uuid");
			documentConts.put("dgtlsgn_cntrdoc_tmpl_athg_uuid", tmplAthgId);
			eFormSignRepository.updateDgtlsgnCntrdocTmpl(documentConts);
		}
		
		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("signerList", signerList);
		resultData.put("documentConts", documentConts);
		resultData.put("clauseList", clauseList);
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 간편 서명 Template 작성
	 * @param param
	 * @return
	 */
	public ResultMap saveEFormSignTemplate(Map param) {
		Map cntrInfo = (Map) param.get("cntrInfo"); // 문서정보
		
		// 계약 Conts update
		cntrInfo.put("dgtlsgn_cntrdoc_lyt_attr", param.get("cntrFormAttr"));
		eFormSignRepository.updateDocCntrConts(cntrInfo);

		return ResultMap.SUCCESS();
	}

	/**
	 * 간편 서명 Template 삭제
	 * @param param
	 * @return
	 */
	public ResultMap deleteEFormTemplate(Map param) {
		param.put("dgtlsgn_cntrdoc_tmpl_athg_uuid", "");
		param.put("dgtlsgn_cntrdoc_lyt_attr", "");
		eFormSignRepository.updateDgtlsgnCntrdocTmpl(param);
		eFormSignRepository.updateDocCntrConts(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 간편서명 갑 서명대상 조회
	 * 
	 * @param param
	 * @return
	 */
	public ResultMap findSignTargetInfoFormerY(Map param) {
		// 계약 정보
		Map cntrInfo = econtractService.findContractByEcntrId(param);
		// 간편서명 계약서 내용
		Map documentConts = eFormSignRepository.findDocumentConts(param);
		// '갑' 서명 대상자 검색
		cntrInfo.put("cntrr_typ_ccd", "Y");
		Map signTarget = eFormSignRepository.findDocumentSignTarget(cntrInfo);
		// 서명 타겟에 맞는 Json Data 추출
		List<Map> jsonList = this.findSignTargetFromJsonData(documentConts, signTarget);

		Map resultData = Maps.newHashMap();
		resultData.put("cntrInfo", cntrInfo);
		resultData.put("documentConts", documentConts);
		resultData.put("signTarget", signTarget);
		resultData.put("signJsonList", jsonList);
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 간편서명 계약서 발송
	 * @param param
	 * @return
	 */
	public ResultMap sendEFormSignContract(Map param) {
		// 1. 계약 정보 조회
		Map cntrInfo = econtractService.findContractByEcntrId(param);
		Map compInfo = (Map) param.get("signTarget");
		Map documentConts = eFormSignRepository.findDocumentConts(param);

		// 2. 서명 합친 PDF 파일 생성
		cntrInfo.put("dgtlsgn_cntrdoc_tmpl_athg_uuid", documentConts.get("dgtlsgn_cntrdoc_tmpl_athg_uuid"));
		cntrInfo.put("dgtlsgn_cntrdoc_lyt_attr", param.get("cntrFormAttr"));
		Map pdfInfo = this.makeEFormDocPdf(cntrInfo);

		if(!Const.SUCCESS.equals(pdfInfo.get(Const.RESULT_STATUS))) {
			return ResultMap.FAIL();
		}
		
		// 3. 계약 master에 계약서 파일그룹코드 변경
		cntrInfo.put("sgncmpld_cntrdoc_athg_uuid", pdfInfo.get("athg_uuid"));
		eFormSignRepository.updateCntrGrpCd(cntrInfo);

		// 4. 서명값 vector이미지 가져와서 저장
		Map signValueInfo = Maps.newHashMap();
		signValueInfo.put("joint_cert_sgn_val_uuid", UUID.randomUUID().toString());
		signValueInfo.put("ecntr_uuid", (String) param.get("ecntr_uuid"));
		signValueInfo.put("bizregno", compInfo.get("cntr_sgndusr_uuid"));
		signValueInfo.put("sgndusr_typ_ccd", "BUYER");
		signValueInfo.put("usr_id", compInfo.get("cntr_sgndusr_uuid"));

		if (compInfo.get("signImageYn") != null && "Y".equals(compInfo.get("signImageYn"))) { // SVG일때
			String signImage = "data:image/svg+xml;base64," + compInfo.get("signImage").toString();
			signValueInfo.put("sgn_val", signImage);

		} else if (compInfo.get("signImageYn") != null && "N".equals(compInfo.get("signImageYn"))) { // 이미지일때
			signValueInfo.put("sgn_val", compInfo.get("signImage"));
		}

		eFormSignRepository.insertEFormSignValue(signValueInfo);

		// 5. 서명 여부 업데이트
		econtractService.updateBuyerSignYN((String) cntrInfo.get("ecntr_uuid"));
		
		// 6. 간편서명문서 서명순서로 발송
		Map paramMap = Maps.newHashMap();
		paramMap.put("ecntr_uuid", cntrInfo.get("ecntr_uuid"));
		paramMap.put("cntrr_typ_ccd", "N");
		Map signTarget = eFormSignRepository.findDocumentSignTarget(paramMap); //서명 대상자
		this.sendMailEFormSignTarget(cntrInfo, signTarget);

		// 7. 계약서 발송 처리
		contractService.sendContract(cntrInfo);
		
		return ResultMap.SUCCESS(cntrInfo);
	}
	
	/**
	 * 간편서명 서명 내용 PDF파일로 생성 후 물리 파일 저장
	 * 
	 * @param param
	 * @return
	 */
	public Map makeEFormDocPdf(Map param) {
		FileList fileList = null;
		FileItem fileItem = null;
		Map pdfInfo = Maps.newHashMap();
		
		try {
			// PDF 파일 가져오기
			String cntrGrpCd = (String) param.get("dgtlsgn_cntrdoc_tmpl_athg_uuid");
			fileList = fileService.findFileListWithContents(cntrGrpCd);
			fileItem = fileList.getItems().get(0);
			
			String cntrFormAttr = (String) param.get("dgtlsgn_cntrdoc_lyt_attr"); // Json Data
			String flattendPdfPath = pdfFlattenService.writeDataToPdf(fileItem, cntrFormAttr);

			// 생성된 pdf 저장
			String orgnFileNm = param.get("cntr_no") + "-" + param.get("cntr_revno") + ".pdf";
			pdfInfo = this.savePdfFile(flattendPdfPath, orgnFileNm);
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".makeEFormDocPdf(Map param) : Exception", e);
			throw new CommonException(this.getClass().getName() + ".makeEFormDocPdf(Map param) : " + e.toString());
		}

		pdfInfo.put(Const.RESULT_STATUS, Const.SUCCESS);
		return pdfInfo;
	}
	
	/**
	 * PDF 파일 저장
	 * 
	 * @param pdfFullPath pdf 파일 경로
	 * @param orgnFileNm 저장할 파일명
	 * @return grp_cd 파일그룹코드
	 */
	private Map savePdfFile(String pdfFullPath, String orgnFileNm) {
		Map pdfInfo = Maps.newHashMap();
		File pdfFile = new File(pdfFullPath);
		
		try {
			String grpCd = UUID.randomUUID().toString();
			
			SimpleMultipartFileItem fileItem = AthfServiceUtil.newMultipartFileItem (
					UUID.randomUUID().toString(),
					grpCd,
					orgnFileNm,
					pdfFile
			);
			fileService.createWithMultipart(fileItem);
			
			pdfInfo.put("athg_uuid", grpCd);
			pdfInfo.put("real_file_name", fileItem.getName());
			pdfInfo.put("att_file_siz", fileItem.getSize());
			
		} catch (Exception e){
			LOG.error(this.getClass().getName() + ".savePdfFile() : Exception", e);
			throw new CommonException(this.getClass().getName() + ".savePdfFile(String pdfFullPath, String orgnFileNm) : " + e.toString());
		} finally {
			try{
				if(pdfFile.exists()) pdfFile.delete(); // 임시파일 삭제
			}catch (Exception e){
				LOG.error(this.getClass().getName() + ".savePdfFile() : Exception", e);
				throw new CommonException(this.getClass().getName() + ".savePdfFile(String pdfFullPath, String orgnFileNm) : " + e.toString());
			}
		}
		
		return pdfInfo;
	}
	
	/**
	 * 간편서명 메일 발송
	 * 
	 * @param cntrInfo 계약 정보 (cntr_nm, cntr_pic_nm)
	 * @param signTarget 서명대상자 정보 (usr_id, usr_typ_ccd, cntr_sgndusr_uuid, cntrr_nm, cntrr_eml)
	 */
	public void sendMailEFormSignTarget(Map cntrInfo, Map signTarget) {
		if(cntrInfo != null && signTarget != null) {
			String signLinkUrl = this.getSignLinkUrl(signTarget);
			signTarget.put("sign_url", signLinkUrl);
			signTarget.put("cntr_nm", cntrInfo.get("cntr_nm"));
			signTarget.put("cntr_pic_nm", cntrInfo.get("cntr_pic_nm"));
			// 이메일
			mailService.sendAsync("REQ_SIGN_EFORM", null, signTarget);
		}
	}
	
	/**
	 * 메일에 첨부할 화면 url 생성
	 * 
	 * @param signTarget
	 * @return
	 */
	private String getSignLinkUrl(Map signTarget) {
		String usrId = signTarget.get("usr_id") != null ? signTarget.get("usr_id").toString() : "";
		String usrCls = signTarget.get("usr_typ_ccd") != null ? signTarget.get("usr_typ_ccd").toString() : "VD";
		String menuId = "SCTR10000";
		String signLinkParam = "contractor_no=" + signTarget.get("cntr_sgndusr_uuid").toString();
		String signLinkUrl = "";
		
		// usrID 여부로 url, param 추가
		if (!Strings.isNullOrEmpty(usrId)) {
			if ("BUYER".equals(usrCls)) { // BP문서보관함
				menuId = "CT20000";
			}
			signLinkParam = signLinkParam + "&usr_id=" + usrId + "&menuId=" + menuId + "&usrCls=" + usrCls;
			signLinkUrl = EdocStringUtil.getCurrentReqUri() + sessionSignUrl;
		} else {
			signLinkUrl = EdocStringUtil.getCurrentReqUri() + nosessionSignUrl;
		}

		// url 암호화
		AESCipherUtil aesCipherUtil = new AESCipherUtil();
		signLinkParam = aesCipherUtil.encrypt(signLinkParam + "");
		signLinkParam = signLinkParam.replace("+", "%2B");
		signLinkUrl = signLinkUrl + "?p_link=" + signLinkParam;
		LOG.info("signLinkParam : " + signLinkParam);
		
		return signLinkUrl;
	}
	
}