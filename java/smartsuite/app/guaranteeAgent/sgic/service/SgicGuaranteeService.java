package smartsuite.app.guaranteeAgent.sgic.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import kica.sgic.util.DataToXml;
import kica.sgic.util.SGIxLinker;
import kica.sgic.util.XmlToData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import signgate.crypto.util.RandomUtil;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.guaranteeAgent.sgic.repository.SgicRepository;
import smartsuite.app.guaranteeAgent.sgic.validator.SgicValidator;
import smartsuite.app.shared.ElecGuaranteeConst;
import smartsuite.app.shared.GuarConst;
import smartsuite.app.util.CalendarUtil;
import smartsuite.app.util.ElecGuaranteeUtil;
import smartsuite.exception.CommonException;
import smartsuite.upload.StdFileService;
import smartsuite.upload.entity.FileItem;
import smartsuite.upload.entity.FileList;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 서울보증보험 상품 구분
enum GuarProductCode {
	// BID:입찰, CON:계약, FLR:하자, PRE:선금
	BID("001"), CON("002"), FLR("003"), PRE("004"), PAY("006");
		
	final private String value;
		
	private GuarProductCode(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}

@Service
@Transactional
@SuppressWarnings({ "unchecked" })
public class SgicGuaranteeService{
	private static final Logger LOG = LoggerFactory.getLogger(SgicGuaranteeService.class);
	
	@Value("#{guar['func.code']}")
	private String funcCode;
	
	@Value("#{guar['head.orga.code']}")
	private String headOrgaCode;
	
	@Value("#{guar['sender.id']}")
	private String senderId;
	
	@Value("#{guar['sgic.id']}")
	private String sgicId;
	
	@Value("#{guar['sgic.module.path']}")
	private String sgicModulePath;
	
	@Value("#{guar['sgic.test.module.path']}")
	private String sgicTestModulePath;
	
	@Value("#{globalProperties['server.cls']}")
	private String serverCls;
	
	GuarProductCode guarProductCode; // 서울보증보험 상품 구분
	
	@Inject
	StdFileService fileService;
	
	@Inject
	SgicRepository sgicRepository;
	
	@Inject
	SgicValidator sgicValidator;
	
	/**
	 * 업체 보증보험 신청
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap sendGuarInfo(Map param) throws IOException {
		byte[] seedKey = RandomUtil.genRand(16);
		LOG.info("SEED KEY Length : " + seedKey.length );

		String fileEncoding = System.getProperty("file.encoding");
		LOG.info("==============================file.encoding = " + fileEncoding);

		String headMesgType = sgicRepository.getBondType(param); // 보증종류 확인
		param.put("head_mesg_type", headMesgType);
		if(Strings.isNullOrEmpty(headMesgType)) {
			LOG.info("result msg : 데이터가 존재하지 않습니다.");
			return ResultMap.FAIL();
		}
		
		Map bondInfo = Maps.newHashMap();
		Map attachFileMap = null;
		
		if(ElecGuaranteeConst.PAYINF.equals(headMesgType)) {
			bondInfo = sgicRepository.getPayBondInfo(param); // 보증서 상세조회
			guarProductCode = GuarProductCode.PAY;	// 보증 상품 코드 지급이행 006
			bondInfo.put("egurdoc_bond_type", "PAYGUA");
			
			Map credInfo = sgicRepository.getCredInfo(bondInfo); // 채권자 담당자 정보조회(지급보증에서 채권자는 협력사 정보)
			if(credInfo != null) {
				bondInfo.putAll(credInfo);
			}
			
			// 선급금 정보 조회
			bondInfo.put("prePayList", sgicRepository.getPrePaymentList(bondInfo));
			
			// 계약서 갑지 파일
			try {
				FileList fileList = fileService.findFileListWithContents((String) bondInfo.get("contract_file_athg_uuid"));
				FileItem fileItem = fileList.getItems().get(0);
				attachFileMap = Maps.newHashMap();
				attachFileMap.put(fileItem.getName(), fileItem.toByteArray()); // 계약서 갑지 파일 첨부
				
				// 기성금 정산 확인원 첨부파일
				if("Y".equals((String)bondInfo.get("unde_acco_curc"))) {
					fileList = fileService.findFileListWithContents((String) bondInfo.get("kisung_confirm_file_athg_uuid"));
					fileItem = fileList.getItems().get(0);
					attachFileMap.put(fileItem.getName(), fileItem.toByteArray()); // 기성금 정산 확인원 파일 첨부
				}
			} catch (Exception e) {
				throw new CommonException(this.getClass().getName() + ".sendGuarInfo : " + e.getMessage(), e.toString());
			}
		} else {
			bondInfo = sgicRepository.getBondInfo(param);
			
			if(ElecGuaranteeConst.CONINF.equals(headMesgType)) { // 계약이행
				guarProductCode = GuarProductCode.CON;	// 보증 상품 코드 계약이행 002
				bondInfo.put("egurdoc_bond_type", "CONGUA");
			} else if(ElecGuaranteeConst.PREINF.equals(headMesgType)) {	// 선급금이행
				guarProductCode = GuarProductCode.PRE;	// 보증 상품 코드 선급금이행 003
				bondInfo.put("egurdoc_bond_type", "PREGUA");
			} else if(ElecGuaranteeConst.FLRINF.equals(headMesgType)) {	// 하자이행
				guarProductCode = GuarProductCode.FLR;	// 보증 상품 코드 하자이행 004
				bondInfo.put("egurdoc_bond_type", "FLRGUA");
			}
		}
		
		bondInfo.put("bond_kind_code", guarProductCode.getValue());
		
		// 보증서를 발급받은 내용이 있는지 확인
		int cnt = sgicRepository.getCountEgurdoc(bondInfo);
		String xmlDoc = "";	// 통보서 xml 변수
		
		if(cnt > 0) { // 해당 건으로 보증서가 존재 하면
			LOG.info("result msg : 이미 발급된 보증서가 존재합니다.");
			return ResultMap.FAIL();
		} else {
			// 통보서 xml 구성
			Map resultMap = ElecGuaranteeUtil.setBondDataToIFNoticDevinition(bondInfo);

			if(Const.FAIL.equals(resultMap.get(Const.RESULT_STATUS))) {
				return ResultMap.FAIL();
			}
				
			xmlDoc = this.composeXML(bondInfo);

			LOG.info("\n\n++++++++++xmlDoc start++++++++++++\n");
			LOG.info(xmlDoc == null ? "empty" : xmlDoc);
			LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");

			if (xmlDoc == null) {
				return ResultMap.FAIL();
			} else {
				resultMap = this.sendGuarXml(xmlDoc, attachFileMap);

				if("LOCAL".equals(serverCls)){ // guarantee-if.properties에 LOCAL 테스트 인 경우만 
					resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
				}
				
				if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) { // 서울보증보험사에서 응담을 받은 경우
					bondInfo.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_REQUEST);
					bondInfo.put("gur_insco_ccd", ElecGuaranteeConst.SGI); // 서울보증보험
					sgicRepository.saveInsuranceForm(bondInfo); // 보증 신청정보 저장
					bondInfo.put("gur_ath_typ_ccd", GuarConst.ATTACH_TYPE.ELECTRONIC_GUARANTEE);
					sgicRepository.updateCtgrSts(bondInfo); // 보증 진행상태 변경
					return ResultMap.SUCCESS();
				} else { // 서울보증보험에서 응답을 못받은 경우 (방화벽이나 응답받은 정보를 xml로 만들지 못햇거나 기타 등등...
					return ResultMap.FAIL();
				}
			}
		}
	}

	/**
	 * 업체 보증보험 취소
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap cancelGuar(Map param) {
		Map guarData = sgicRepository.cancelEgur(param); // 보증보험 취소
		
		if(guarData == null) {
			LOG.info("result msg : 취소 대상이 존재하지 않습니다.");
			return ResultMap.FAIL();
		}
		try {
			guarData = ElecGuaranteeUtil.setBondDataToIFResponseDevinition(guarData);
			String xmlDoc = this.makeFinalReponse(guarData);
			LOG.info("\n\n++++++++++xmlDoc++++++++++++\n");
			LOG.info(xmlDoc);
			LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");
			if(Strings.isNullOrEmpty(xmlDoc)) {
				return ResultMap.FAIL();
			}
			
			Map resultMap = this.sendGuarXml(xmlDoc, null);
			
			if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
			}
			
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				guarData.putAll(resultMap);
				guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_READY); // 발행대기

				sgicRepository.saveFinalResponse(guarData); // 취소 응답서 저장
				sgicRepository.deleteNofndoc(guarData); // 통보서 삭제
				sgicRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
				sgicRepository.deleteEgurdoc(guarData); // 받은 보증서 삭제
				
				return ResultMap.SUCCESS();
			} else {
				LOG.info("error:" + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ResultMap.FAIL();
		}
	}

	/**
	 * 승인
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap approveGuar(Map param) {
		String headFuncCode = funcCode;
		param.put("head_func_code", headFuncCode);
		Map guarData = sgicRepository.approveEgur(param);
		
		if(guarData == null) {
			LOG.info("result msg : 승인 대상이 존재하지 않습니다.");
			return ResultMap.FAIL();
		}
		
		try {
			guarData = ElecGuaranteeUtil.setBondDataToIFResponseDevinition(guarData);
			String xmlDoc = this.makeFinalReponse(guarData);
			LOG.info("\n\n++++++++++xmlDoc++++++++++++\n");
			LOG.info(xmlDoc);
			LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");
			if(Strings.isNullOrEmpty(xmlDoc)) {
				return ResultMap.FAIL();
			}
			
			Map resultMap = this.sendGuarXml(xmlDoc, null);
			
			if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
			}
			
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_APPROVE);
				
				sgicRepository.saveFinalResponse(guarData); // 최종응답서 저장
				sgicRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
				
				return ResultMap.SUCCESS();
			} else {
				LOG.info("error:" + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ResultMap.FAIL();
		}
	}

	/**
	 * 반려
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap rejectGuar(Map param) {
		String headFuncCcode = funcCode;
		param.put("head_func_code", headFuncCcode);
		Map guarData = sgicRepository.rejectEgur(param);
		
		if(guarData == null) {
			LOG.info("result msg : 반려 대상이 존재하지 않습니다.");
			return ResultMap.FAIL();
		}

		try {
			guarData = ElecGuaranteeUtil.setBondDataToIFResponseDevinition(guarData);
			String xmlDoc = this.makeFinalReponse(guarData);
			LOG.info("\n\n++++++++++xmlDoc++++++++++++\n");
			LOG.info(xmlDoc);
			LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");
			if(Strings.isNullOrEmpty(xmlDoc)) {
				return ResultMap.FAIL();
			}
			
			Map resultMap = this.sendGuarXml(xmlDoc, null);
			
			if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
			}
			
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				guarData.putAll(resultMap);
				guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_REJECT); // 반려
				
				sgicRepository.saveFinalResponse(guarData); // 취소 응답서 저장
				sgicRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
				sgicRepository.deleteEgurdoc(guarData); // 받은 보증서 삭제

				return ResultMap.SUCCESS();
			} else {
				LOG.info("error:" + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ResultMap.FAIL();
		}
	}

	/**
	 * 파기
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap destroyGuar(Map param) {
		String headFuncCode = funcCode;
		param.put("head_func_code", headFuncCode);
		Map guarData = sgicRepository.destroyEgur(param);
		
		if(guarData == null) {
			LOG.info("result msg : 파기 대상이 존재하지 않습니다.");
			return ResultMap.FAIL();
		}
		try {
			guarData = ElecGuaranteeUtil.setBondDataToIFResponseDevinition(guarData);
			String xmlDoc = this.makeFinalReponse(guarData);
			LOG.info("\n\n++++++++++xmlDoc++++++++++++\n");
			LOG.info(xmlDoc);
			LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");
			if(Strings.isNullOrEmpty(xmlDoc)) {
				return ResultMap.FAIL();
			}
			
			Map resultMap = this.sendGuarXml(xmlDoc, null);
			
			if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
				resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
			}
			
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_DESTROY);
				
				sgicRepository.saveFinalResponse(guarData); // 최종응답서 저장
				sgicRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
				
				return ResultMap.SUCCESS();
			} else {
				LOG.info("error:" + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return ResultMap.FAIL();
		}
	}
	
	/**
	 * 보증 통보서 xml 데이터 생성
	 * 통보서를 만들기 위해서는 서울보증보험 XML전문 문서를 참조하여 만들어야함
	 * 문서는 전자인증팀에 요청하며 전자보증_송수신_전문 문서 폴더 아래 01.통보서(주계약정보통보) 폴더 아래 계약정보통보서(계약,하자,선금)_XML전문 excel 파일 문서 참조
	 * @param guarData : 보증 xml 만들기 위한 정보
	 * @return 보증 통보서 xml
	 */
	public String composeXML(Map guarData) {
		
		String revisionNo = (String)guarData.get("revision_no"); // 계약차수 2자리로 만들기 예) 1차수면 01
		String contNumbText = (String)guarData.get("cont_numb_text"); /* [필수] 계약번호 피보험자 측 계약번호 */
		
		/**** 통보서 HEADER 시작 ****/
		String headMesgSend = senderId.trim(); 	/* [필수] 전문송신기관 송신 ID값은 edoc.properties sender.id 값을 변경해주어야함*/
		String headMesgRecv = sgicId.trim(); 	/* [필수] 수신자 서울보증보험 고정값 */
		String headFuncCode = funcCode.trim(); 	/* [필수] 문서기능 9:원본 , 1: 취소 , 53 테스트 53이어도 상관없음(운영일때도) */
		String headMesgType = (String) guarData.get("head_mesg_type"); /* [필수] 문서코드 */
		String headMesgName = (String) guarData.get("head_mesg_name"); /* [필수] 문서명 */
		String headMesgVers = "1.0"; /* [선택] 전자문서버전 */
		String headDocuNumb = "004" + guarData.get("bond_kind_code") + contNumbText + " " + revisionNo; /* [필수] 문서번호 */
		String headMangNumb = headMesgSend + "." + CalendarUtil.formatNow("yyyyMMddHHmmss") + ".12345." + headDocuNumb; /* [필수] 문서관리번호 */
		String headRefrNumb = headDocuNumb; /* [필수] 참조번호 */
		String headTitlName = headMesgName; /* [필수] 문서개요 */
		String headOrgaCodeStr = headOrgaCode.trim(); /* [필수] 연계기관코드 */
		/**** 통보서 HEADER 끝 ****/
		
		/**** 통보서 보험계약정보 시작 ****/
		String bondKindCode = (String) guarData.get("bond_kind_code");	 /* [필수] 보험종목(상품구분) */
		String bondBegnDate = (String) guarData.get("bond_begn_date");	 /* [필수] 보험시작일자 */
		String bondFnshDate = (String) guarData.get("bond_fnsh_date");	 /* [필수] 보험종료일자 */
		String bondCurcCode = (String) guarData.get("bond_curc_code"); 	 /* [필수] 보험가입금액 통화코드(WON, USD …) */
		String bondPenlAmnt = guarData.get("bond_penl_amnt").toString(); /* [필수] 보험가입금액 */
		String bondOperCode = "INSERT"; 	                             /* [선택] 조회등록 업무구분(SELECT,INSERT) */
		String bondApplCode = (String) guarData.get("bond_appl_code");
		/**** 통보서 보험계약정보 끝 ****/
		
		/**** 통보서 주계약정보(공통) 시작 ****/
		String contNameText = (String) guarData.get("cont_name_text");	 /* [필수] 계약명 특수문자 제외 */
		String contProcType = (String) guarData.get("cont_proc_type"); 	 /* [필수] 계약구분코드 계약구분코드 시트참조. 계약서를 서울보증보험에 제출하여 받은 코드로 보내야함 */
		String contTypeIden = (String) guarData.get("cont_type_iden"); 	 /* [필수] 계약방식(1:공동, 2:단독, 3:분담) */
		String contNewsDivs = (String) guarData.get("cont_news_divs"); 	 /* [필수] 신규/갱신 계약구분(1:신규계약, 2:갱신계약) */
		String contCurcCode = (String) guarData.get("cont_curc_code"); 	 /* [필수] 계약금액(원화) 통화코드(WON) */
		String contMainAmnt = guarData.get("cont_main_amnt").toString(); /* [필수] 원화 계약금액 예) 150000*/
		String fornCurcCode = (String) guarData.get("forn_curc_code"); 	 /* [선택] 계약금액(외화)/통화코드(USD …) */
		String fornMainAmnt = (guarData.get("forn_main_amnt") != null) ? guarData.get("forn_main_amnt").toString()
				: ""; /* [선택] 계약금액(외화)/계약금액 */
		String histBondNumb = (guarData.get("hist_bond_numb") != null) ? guarData.get("hist_bond_numb").toString()
				: ""; /* [선택] 배서대상 증권번호 */
		/**** 통보서 주계약정보(공통) 끝 ****/
		
		/**** 통보서 계약정보(이행 계약 통보 정보) 시작 ****/
		String contBegnDate = (String) guarData.get("cont_begn_date");	 /* [필수] 계약시작일자 */
		String contFnshDate = (String) guarData.get("cont_fnsh_date");	 /* [필수] 계약종료일자*/
		String contTermText = (String) guarData.get("cont_term_text");	 /* [선택] 계약기간 (계약종료일자-계약시작일자) 총 일수*/
		String contPricRate = guarData.get("cont_pric_rate").toString(); /* [필수] 계약보증금율 -단가계약일 경우 0으로 입력 */
		String contUnitDivs = (String) guarData.get("cont_unit_divs");	 /* [필수] 단가계약여부 1.단가계약 2.일반계약*/
		/**** 통보서 계약정보(이행 계약 통보 정보) 끝 ****/
		
		/**** 통보서 계약정보(이행 하자 통보 정보) 시작 ****/
		String morgBegnDate = (String) guarData.get("morg_begn_date");	 /* [필수] 하자보수 책임시작일*/
		String morgFnshDate = (String) guarData.get("morg_fnsh_date");	 /* [필수] 하자보수 책임종료일*/
		String morgTermText = (String) guarData.get("morg_term_text");	 /* [필수] 하자보수 책임기간 (하자보수책임 종료일자-하자보수책임 시작일자) 총일수*/
		String morgPricRate = guarData.get("morg_pric_rate").toString(); /* [필수] 하자보증금율*/
		String morgCnfmCode = (String) guarData.get("morg_cnfm_code");	 /* [선택] 무사고 확인*/
		String morgCnfmDate = (String) guarData.get("morg_cnfm_date");	 /* [선택] 무사고 확인일*/
		/**** 통보서 계약정보(이행 하자 통보 정보) 끝 ****/
		
		/**** 통보서 계약정보(이행 선금 통보 정보) 시작 ****/
		String prepPaymType = (guarData.get("prep_paym_type") != null) ? guarData.get("prep_paym_type").toString() 
				: "1";	/* [필수] 지급구분 1.직불(수요자) 2.대지급(채권자) */
		String prepBegnDate = (String) guarData.get("prep_begn_date");	/* [필수] 계약시작일자 */
		String prepFnshDate = (String) guarData.get("prep_fnsh_date");	/* [필수] 계약종료일자 */
		String prepTermText = (String) guarData.get("prep_term_text");	/* [선택] 계약기간(계약종료일자-계약시작일자) 총 일수*/
		String prepPaymDate = (String) guarData.get("prep_paym_date");	/* [필수] 선금지급(예정일자)*/
		String prepCurcCode = (String) guarData.get("prep_curc_code");	/* [필수] 선금지급(예정)금액(원화) 통화코드*/
		String prepPaymAmnt = (guarData.get("prep_paym_amnt") != null) ? guarData.get("prep_paym_amnt").toString() 
				: "0";	/* [필수] 선금지급(예정)금액(원화)*/
		String prepPricRate = (guarData.get("prep_pric_rate") != null) ? guarData.get("prep_pric_rate").toString() 
				: "0";	/* [선택] 선금율*/
		String fpreCurcCode = (String) guarData.get("fpre_curc_code");	/* [선택] 선금지급(예정)금액(외화) 통화코드 - USD(미달러), EUR(유로화) 등*/
		String fprePaymAmnt = "0";	/* [선택] 선금지급(예정)금액(외화) - 예)260.55*/
		/**** 통보서 계약정보(이행 선금 통보 정보) 끝 ****/
		
		/**** 통보서 채권자 정보 시작 ****/
		String credOrgaName = guarData.get("cred_orga_name").toString(); /* [필수] 기관명 */
		String credOrpsDivs = guarData.get("cred_orps_divs").toString(); /* [필수] 개인/사업자 구분 코드 O.사업자 P.개인*/
		String credOrgaNumb = (guarData.get("cred_orga_numb") != null) ? guarData.get("cred_orga_numb").toString()
				: ""; /* [필수] 법인등록번호 */
		String credOrpsIden = guarData.get("cred_orps_iden").toString(); /* [필수] 사업자/주민번호 */
		String credOwnrNumb = guarData.get("cred_ownr_numb").toString(); /* [필수] 대표자 주민등록번호 1111111111111 고정값*/
		String credOwnrName = guarData.get("cred_ownr_name").toString(); /* [필수] 성명(대표자명) */
		String credBondHold = guarData.get("cred_bond_hold").toString(); /* [필수] 채권자명 */
		String credOrgaPost = guarData.get("cred_orga_post").toString(); /* [필수] 회사 우편번호 */
		String credOrgaAddr = guarData.get("cred_orga_addr").toString(); /* [필수] 회사 주소 */
		String credChrgName = guarData.get("cred_chrg_name").toString(); /* [필수] 담당자명 */
		String credDeptName = guarData.get("cred_dept_name").toString(); /* [필수] 소속부서 */
		String credPhonNumb = guarData.get("cred_phon_numb").toString(); /* [필수] 전화번호 */
		String credCellPhon = guarData.get("cred_cell_phon").toString(); /* [필수] 핸드폰번호 */
		String credSendMail = guarData.get("cred_send_mail").toString(); /* [필수] 이메일 */
		String credUserIden = headMesgSend;    /* [필수] 수신처ID */
		String credUserType = headOrgaCodeStr; /* [필수] 수신처TYPE */
		/**** 통보서 채권자 정보 끝 ****/
		
		/**** 통보서 계약자 정보 시작 ****/
		String applOrgaName = guarData.get("appl_orga_name").toString(); /* [필수] 기관명 */
		String applOrpsDivs = guarData.get("appl_orps_divs").toString(); /* [필수] 개인/사업자 구분 코드 O.사업자 P.개인*/
		String applOrgaNumb = (guarData.get("appl_orga_numb") != null) ? guarData.get("appl_orga_numb").toString()
				: ""; /* [선택] 법인등록번호 */
		String applOrpsIden = guarData.get("appl_orps_iden").toString(); /* [필수] 사업자/주민번호 */
		String applOwnrNumb = guarData.get("appl_ownr_numb").toString(); /* [필수] 대표자 주민등록번호 1111111111111 고정값*/
		String applOwnrName = guarData.get("appl_ownr_name").toString(); /* [필수] 성명(대표자명) */
		String applOrgaPost = (guarData.get("appl_orga_post") != null) ? guarData.get("appl_orga_post").toString()
				: "";	// [선택] 회사 우편번호 */
		String applOrgaAddr = (guarData.get("appl_orga_addr") != null) ? guarData.get("appl_orga_addr").toString()
				: "";	// [선택] 회사 주소 */
		String applChrgName = (guarData.get("appl_chrg_name") != null) ? guarData.get("appl_chrg_name").toString()
				: ""; /* [선택] 담당자명 */
		String applDeptName = (guarData.get("appl_dept_name") != null) ? guarData.get("appl_dept_name").toString()
				: ""; /* [선택] 소속부서 */
		String applOffcPhon = (guarData.get("appl_offc_phon") != null) ? guarData.get("appl_offc_phon").toString()
				: ""; /* [선택] 전화번호 */
		String applCellPhon = (guarData.get("appl_cell_phon") != null) ? guarData.get("appl_cell_phon").toString()
				: ""; /* [선택] 핸드폰번호 */
		String applSendMail = (guarData.get("appl_send_mail") != null) ? guarData.get("appl_send_mail").toString()
				: ""; /* [선택] 이메일 */
		String applUserIden = headMesgSend;    /* [필수] 수신처ID */
		String applUserType = headOrgaCodeStr; /* [필수] 수신처TYPE */
		/**** 통보서 계약자 정보 끝 ****/

		/* 통보서 테이블에 저장하기위해 보증데이터 put함*/
		guarData.put("head_mesg_send", headMesgSend);
		guarData.put("head_mesg_recv", headMesgRecv);
		guarData.put("head_func_code", headFuncCode);
		guarData.put("head_mesg_vers", headMesgVers);
		guarData.put("head_docu_numb", headDocuNumb);
		guarData.put("head_mang_numb", headMangNumb);
		guarData.put("head_refr_numb", headRefrNumb);
		guarData.put("head_titl_name", headTitlName);
		guarData.put("head_orga_code", headOrgaCodeStr);
		guarData.put("bond_oper_code", bondOperCode);
		guarData.put("fpre_paym_amnt", fprePaymAmnt);
		
		// 매핑 정보 파일, XML 탬플릿 정보 파일
		DataToXml dataToXml = new DataToXml(sgicModulePath + "/templates/", (String)guarData.get("head_mesg_type"));
		if (dataToXml.getErrorCode() != 0) {
			if (LOG.isErrorEnabled())
				LOG.error("DataToXML error code *===================\n" + dataToXml.getErrorCode());
				LOG.error("DataToXML error occur *===================\n" + dataToXml.getErrorMsg());
			return null;
		}
		/* 계약정보통보서(CONINF) */
		/* HEADER */
		boolean xmlFlag = false;
		String xmlDoc = null;
		/* Begin of Header */
		/* End of 수요자 정보 */
		try {
			if(dataToXml.setData("head_mesg_send", headMesgSend)
					&& dataToXml.setData("head_mesg_recv", headMesgRecv)
					&& dataToXml.setData("head_func_code", headFuncCode)
					&& dataToXml.setData("head_mesg_type", headMesgType)
					&& dataToXml.setData("head_mesg_name", headMesgName)
					&& dataToXml.setData("head_mesg_vers", headMesgVers)
					&& dataToXml.setData("head_docu_numb", headDocuNumb)
					&& dataToXml.setData("head_mang_numb", headMangNumb)
					&& dataToXml.setData("head_refr_numb", headRefrNumb)
					&& dataToXml.setData("head_titl_name", headTitlName)
					&& dataToXml.setData("head_orga_code", headOrgaCodeStr)
					/* End of Header */
					/* Begin of 보험계약정보 */
					&& dataToXml.setData("bond_kind_code", bondKindCode)
					&& dataToXml.setData("bond_begn_date", bondBegnDate)
					&& dataToXml.setData("bond_fnsh_date", bondFnshDate)
					&& dataToXml.setData("bond_curc_code", bondCurcCode)
					&& dataToXml.setData("bond_penl_amnt", bondPenlAmnt)
					&& dataToXml.setData("bond_oper_code", bondOperCode)
					&& dataToXml.setData("bond_appl_code", bondApplCode)
					/* End of 보험계약정보 */
					/* Begin of 주요계약정보 */
					&& dataToXml.setData("cont_numb_text", contNumbText)
					&& dataToXml.setData("cont_name_text", contNameText)
					&& dataToXml.setData("cont_proc_type", contProcType)
					&& dataToXml.setData("cont_type_iden", contTypeIden)
					// && dataToXml.setData("cont_asgn_rate", cont_asgn_rate)
					&& dataToXml.setData("cont_news_divs", contNewsDivs)
					// && dataToXml.setData("cont_plan_date", cont_plan_date)
					// && dataToXml.setData("cont_main_date", cont_main_date)
					&& dataToXml.setData("cont_curc_code", contCurcCode)
					&& dataToXml.setData("cont_main_amnt", contMainAmnt)
					&& dataToXml.setData("forn_curc_code", fornCurcCode)
					&& dataToXml.setData("forn_main_amnt", fornMainAmnt)
					&& dataToXml.setData("hist_bond_numb", histBondNumb)) {
				xmlFlag = true;
			}
//			if("PAYINF".equals(headMesgType)) {
//				
//				/**** 지급보증인 경우 추가 주계약정보  ****/
//				String contCommDivs = (String) guarData.get("cont_comm_divs");	/* [필수] 발주구분 - 1:일반계약(피보험자), 2:하도급(보험계약자), 3:일반보험계약자 */
//				String contPldgYsno = (String) guarData.get("cont_pldg_ysno");	/* [필수] 질권 설정 여부 – Y:설정,N:없음 */
//				
//				String contPldgDivs = (String) guarData.get("cont_pldg_divs");	/* [선택] 설정자 구분 - O:사업자, P:개인 */
//				String contPldgIden = (String) guarData.get("cont_pldg_iden");	/* [선택] 설정자 사업자/주민번호 - O면 사업자번호 P면 주민등록번호 */
//				String contPldgName = (String) guarData.get("cont_pldg_name");	/* [선택] 설정자 상호명/성명 */
//				String contPremYsno = (String) guarData.get("cont_prem_ysno");	/* [선택] 제3자 보험료납부 여부 - 1:보험계약자, 2:제3자납부 */
//				String contPremDivs = (String) guarData.get("cont_prem_divs");	/* [선택] 제3자 구분 - O:사업자번호, P:주민등록번호 */
//				String contPremIden = (String) guarData.get("cont_prem_iden");	/* [선택] 제3자 사업자/주민번호 - O면 사업자번호 P면 주민등록번호 */
//				String contPremName = (String) guarData.get("cont_prem_name");	/* [선택] 제3자 상호명/성명 */
//				/**** 지급보증인 경우 추가 주계약정보  ****/
//				
//				if(dataToXml.setData("cont_comm_divs", contCommDivs)
//					&& dataToXml.setData("cont_pldg_ysno", contPldgYsno)
//					
//					&& dataToXml.setData("cont_pldg_divs", contPldgDivs)
//					&& dataToXml.setData("cont_pldg_iden", contPldgIden)
//					
//					&& dataToXml.setData("cont_pldg_name", contPldgName)
//					&& dataToXml.setData("cont_prem_ysno", contPremYsno)
//					&& dataToXml.setData("cont_prem_divs", contPremDivs)
//					&& dataToXml.setData("cont_prem_iden", contPremIden)
//					&& dataToXml.setData("cont_prem_name", contPremName)) {
//					xmlFlag = true;
//				}else {
//					xmlFlag = false;
//				}
//			}
					/* End of 주요계약정보 */
					/* Begin of 계약정보 */
					/*
					 * && dataToXml.setData("cont_begn_date", cont_begn_date) &&
					 * dataToXml.setData("cont_fnsh_date", cont_fnsh_date) &&
					 * dataToXml.setData("cont_term_text", cont_term_text) &&
					 * dataToXml.setData("cont_pric_rate", cont_pric_rate) &&
					 * dataToXml.setData("cont_unit_divs", cont_unit_divs)
					 */
					/* End of 계약정보 */
					/* Begin of 채권자 정보 */
			if(dataToXml.setData("cred_orga_name", credOrgaName)
				&& dataToXml.setData("cred_orps_divs", credOrpsDivs)
				&& dataToXml.setData("cred_orga_numb", credOrgaNumb)
				&& dataToXml.setData("cred_orps_iden", credOrpsIden)
				&& dataToXml.setData("cred_ownr_numb", credOwnrNumb)
				&& dataToXml.setData("cred_ownr_name", credOwnrName)
				&& dataToXml.setData("cred_bond_hold", credBondHold)
				// && dataToXml.setData("cred_addn_name", cred_addn_name)
				&& dataToXml.setData("cred_orga_post", credOrgaPost)
				&& dataToXml.setData("cred_orga_addr", credOrgaAddr)
				&& dataToXml.setData("cred_chrg_name", credChrgName)
				&& dataToXml.setData("cred_dept_name", credDeptName)
				&& dataToXml.setData("cred_phon_numb", credPhonNumb)
				&& dataToXml.setData("cred_cell_phon", credCellPhon)
				&& dataToXml.setData("cred_send_mail", credSendMail)
				&& dataToXml.setData("cred_user_iden", credUserIden)
				&& dataToXml.setData("cred_user_type", credUserType)
				/* End of 채권자 정보 */
				/* Begin of 계약자 정보 */
				&& dataToXml.setData("appl_orga_name", applOrgaName)
				&& dataToXml.setData("appl_orps_divs", applOrpsDivs)
				&& dataToXml.setData("appl_orga_numb", applOrgaNumb)
				&& dataToXml.setData("appl_orps_iden", applOrpsIden)
				&& dataToXml.setData("appl_ownr_numb", applOwnrNumb)
				&& dataToXml.setData("appl_ownr_name", applOwnrName)
				// && dataToXml.setData("appl_addn_name", appl_addn_name)
				&& dataToXml.setData("appl_orga_post", applOrgaPost)
				&& dataToXml.setData("appl_orga_addr", applOrgaAddr)
				&& dataToXml.setData("appl_chrg_name", applChrgName)
				&& dataToXml.setData("appl_dept_name", applDeptName)
				&& dataToXml.setData("appl_offc_phon", applOffcPhon)
				&& dataToXml.setData("appl_cell_phon", applCellPhon)
				&& dataToXml.setData("appl_send_mail", applSendMail)
				// && dataToXml.setData("appl_home_post", appl_home_post)
				// && dataToXml.setData("appl_home_addr", appl_home_addr)
				// && dataToXml.setData("appl_home_phon", appl_home_phon)
				&& dataToXml.setData("appl_user_iden", applUserIden)
				&& dataToXml.setData("appl_user_type", applUserType)
			/* End of 계약자 정보 */
			/* Begin of 수요자 정보 */
			// && dataToXml.setData("mang_orga_name", mang_orga_name)
			// && dataToXml.setData("mang_orps_divs", mang_orps_divs)
			// && dataToXml.setData("mang_orga_numb", mang_orga_numb)
			// && dataToXml.setData("mang_orps_iden", mang_orps_iden)
			// && dataToXml.setData("mang_ownr_numb", mang_ownr_numb)
			// && dataToXml.setData("mang_ownr_name", mang_ownr_name)
			// && dataToXml.setData("mang_addn_name", mang_addn_name)
			// && dataToXml.setData("mang_orga_post", mang_orga_post)
			// && dataToXml.setData("mang_orga_addr", mang_orga_addr)
			// && dataToXml.setData("mang_bond_hold", mang_bond_hold)
			// && dataToXml.setData("mang_chrg_name", mang_chrg_name)
			// && dataToXml.setData("mang_dept_name", mang_dept_name)
			// && dataToXml.setData("mang_phon_numb", mang_phon_numb)
			// && dataToXml.setData("mang_cell_phon", mang_cell_phon)
			// && dataToXml.setData("mang_send_mail", mang_send_mail)
			// && dataToXml.setData("mang_user_iden", mang_user_iden)
			// && dataToXml.setData("mang_user_type", mang_user_type)
			) {
				if(ElecGuaranteeConst.CONINF.equals(headMesgType)) {
					// 계약이행
					if(dataToXml.setData("cont_begn_date", contBegnDate)
							&& dataToXml.setData("cont_fnsh_date", contFnshDate)
							&& dataToXml.setData("cont_term_text", contTermText)
							&& dataToXml.setData("cont_pric_rate", contPricRate)
							&& dataToXml.setData("cont_unit_divs", contUnitDivs)) {
						xmlFlag = true;
					}
				} else if(ElecGuaranteeConst.PREINF.equals(headMesgType)) {
					// 선금이행
					if(dataToXml.setData("prep_begn_date", prepBegnDate)
							&& dataToXml.setData("prep_fnsh_date", prepFnshDate)
							&& dataToXml.setData("prep_term_text", prepTermText)
							&& dataToXml.setData("prep_curc_code", prepCurcCode)
							&& dataToXml.setData("prep_paym_amnt", prepPaymAmnt)
							&& dataToXml.setData("fpre_curc_code", fpreCurcCode)
							&& dataToXml.setData("fpre_paym_amnt", fprePaymAmnt)
							&& dataToXml.setData("prep_pric_rate", prepPricRate)
							&& dataToXml.setData("prep_paym_type", prepPaymType) 
							&& dataToXml.setData("prep_paym_date", prepPaymDate)) {
						xmlFlag = true;
					}
				} else if(ElecGuaranteeConst.FLRINF.equals(headMesgType)) {
					// 하자이행
					if (dataToXml.setData("morg_begn_date", morgBegnDate)
							&& dataToXml.setData("morg_fnsh_date", morgFnshDate)
							&& dataToXml.setData("morg_term_text", morgTermText)
							&& dataToXml.setData("morg_pric_rate", morgPricRate)
							&& dataToXml.setData("morg_cnfm_code", morgCnfmCode)
							&& dataToXml.setData("morg_cnfm_date", morgCnfmDate)) {
						xmlFlag = true;
					}
				} else if (ElecGuaranteeConst.PAYINF.equals(headMesgType)) {
					/**** 지급보증인 경우 추가 주계약정보  ****/
					String contCommDivs = (String) guarData.get("cont_comm_divs");	/* [필수] 발주구분 - 1:일반계약(피보험자), 2:하도급(보험계약자), 3:일반보험계약자 */
					String contPldgYsno = (String) guarData.get("cont_pldg_ysno");	/* [필수] 질권 설정 여부 – Y:설정,N:없음 */
					
					String contPldgDivs = (String) guarData.get("cont_pldg_divs");	/* [선택] 설정자 구분 - O:사업자, P:개인 */
					String contPldgIden = (String) guarData.get("cont_pldg_iden");	/* [선택] 설정자 사업자/주민번호 - O면 사업자번호 P면 주민등록번호 */
					String contPldgName = (String) guarData.get("cont_pldg_name");	/* [선택] 설정자 상호명/성명 */
					String contPremYsno = (String) guarData.get("cont_prem_ysno");	/* [선택] 제3자 보험료납부 여부 - 1:보험계약자, 2:제3자납부 */
					String contPremDivs = (String) guarData.get("cont_prem_divs");	/* [선택] 제3자 구분 - O:사업자번호, P:주민등록번호 */
					String contPremIden = (String) guarData.get("cont_prem_iden");	/* [선택] 제3자 사업자/주민번호 - O면 사업자번호 P면 주민등록번호 */
					String contPremName = (String) guarData.get("cont_prem_name");	/* [선택] 제3자 상호명/성명 */
					/**** 지급보증인 경우 추가 주계약정보  ****/
					
					/**** 통보서 계약정보(이행 지급 통보 정보) 시작 ****/
					String paymBegnDate = (String) guarData.get("paym_begn_date");	 /* [필수] 계약 시작일자 - YYYYMMDD */
					String paymFnshDate = (String) guarData.get("paym_fnsh_date");	 /* [필수] 계약 종료일자 - YYYYMMDD */
					String paymTermText =  guarData.get("paym_term_text").toString();/* [선택] 계약기간 = 계약종료일자-계약시작일자(계약기간 총 일수) */
					String paymBefrCurc = (String) guarData.get("paym_befr_curc");	 /* [필수] 보험기간 개시전 채무액(원화) 통화코드 - WON */
					String paymBefrAmnt = guarData.get("paym_befr_amnt").toString(); /* [필수] 보험기간 개시전 채무액(원화) : 하도급인 경우 0 */
//					String fpayBefrCurc = (String) guarData.get("fpay_befr_curc");	 /* [선택] 보험기간 개시전 채무액(외화) 통화코드 - USD(미달러), EUR(유로화) 등 */
//					String fpayBefrAmnt = guarData.get("fpay_befr_amnt").toString(); /* [선택] 보험기간 개시전 채무액(외화) - 예)2500.59 */
					String paymDebtDate = (String) guarData.get("paym_debt_date");	 /* [선택] 채무액산정 기준일자 - YYYYMMDD */
					String paymAcntMeth = (String) guarData.get("paym_acnt_meth");	 /* [선택] 결재수단 - 1:현금, 2:어음(수표), 3:현금+어음(수표), 4:기타 */
					String undeLastDate = (String) guarData.get("unde_last_date");	 /* [선택] 최종 계약 체결일자 - YYYYMMDD */
					
					String undeTotaCurc = (String) guarData.get("unde_tota_curc");	 /* [필수] 총계약금액(원화) 통화코드 - WON(원화) */
					String undeTotaAmnt = guarData.get("unde_tota_amnt").toString(); /* [필수] 총계약금액(원화) 예)1500000 */
					String undeIndiCurc = (String) guarData.get("unde_indi_curc");	 /* [필수] 선금지급(예정)여부 - Y: 있음, N: 없음 */
					String undeNumbCont = guarData.get("unde_numb_cont").toString();	/* [필수] 선금지급(예정) 전체회수 */
					//String undeSequCont = guarData.get("unde_sequ_cont").toString();	/* [필수] 선금지급(예정) 현재회차 */
					//String undeSequTime = (String) guarData.get("unde_sequ_time");	/* [필수] 선금지급(예정)일자 - YYYYMMDD */
					//String undeSequAmnt = guarData.get("unde_sequ_amnt").toString();	/* [필수] 선금지급(예정)금액 */
					//String undeSequCurc = (String) guarData.get("unde_sequ_curc");	/* [필수] 선금지급 완료여부 - Y:지급완료, N:지급예정 */
					String undeFiniTime = (String) guarData.get("unde_fini_time");	 /* [필수] 최종 결제기일 - YYYYMMDD */
					String undeMontNume = (String) guarData.get("unde_mont_nume");	 /* [필수] 기성금 지급주기(월수) */
					String undeAccoCurc = (String) guarData.get("unde_acco_curc");	 /* [필수] 기성금 정산 확인원 첨부여부 - Y:첨부, N:첨부없음 */
					String undeContCurc = (String) guarData.get("unde_cont_curc");	 /* [필수] 이행 계약서 또는 하도급 계약서 첨부여부 - Y:첨부, N:첨부없음 */
					
					if(dataToXml.setData("cont_comm_divs", contCommDivs)
						&& dataToXml.setData("cont_pldg_ysno", contPldgYsno)
						
						&& dataToXml.setData("cont_pldg_divs", contPldgDivs)
						&& dataToXml.setData("cont_pldg_iden", contPldgIden)
						
						&& dataToXml.setData("cont_pldg_name", contPldgName)
						&& dataToXml.setData("cont_prem_ysno", contPremYsno)
						&& dataToXml.setData("cont_prem_divs", contPremDivs)
						&& dataToXml.setData("cont_prem_iden", contPremIden)
						&& dataToXml.setData("cont_prem_name", contPremName)
						&& dataToXml.setData("paym_begn_date", paymBegnDate)
						&& dataToXml.setData("paym_fnsh_date", paymFnshDate)
						&& dataToXml.setData("paym_term_text", paymTermText)

						&& dataToXml.setData("paym_befr_curc", paymBefrCurc)
						&& dataToXml.setData("paym_befr_amnt", paymBefrAmnt)
						
						//&& dataToXml.setData("fpay_befr_curc", fpayBefrCurc)
						//&& dataToXml.setData("fpay_befr_amnt", fpayBefrAmnt)
						&& dataToXml.setData("paym_debt_date", paymDebtDate)
						&& dataToXml.setData("paym_acnt_meth", paymAcntMeth)
						&& dataToXml.setData("unde_last_date", undeLastDate)

						&& dataToXml.setData("unde_tota_curc", undeTotaCurc)
						&& dataToXml.setData("unde_tota_amnt", undeTotaAmnt)
						
						&& dataToXml.setData("unde_indi_curc", undeIndiCurc)
						&& dataToXml.setData("unde_numb_cont", undeNumbCont)) {
						
						// 지급이행 반복구간
						// for(int i = 0; i <)
						if("Y".equals(undeIndiCurc)) {
							List<Map<String,Object>> prePayList = (List<Map<String,Object>>)guarData.get("prePayList");
							dataToXml.initLoopDataWithChild("advn_info_max", null);
							for(Map row : prePayList) {
								if(dataToXml.setLoopDataWithChild("unde_sequ_cont", row.get("unde_sequ_cont").toString())
									&& dataToXml.setLoopDataWithChild("unde_sequ_time", (String)row.get("unde_sequ_time"))
									&& dataToXml.setLoopDataWithChild("unde_sequ_amnt", row.get("unde_sequ_amnt").toString())
									&& dataToXml.setLoopDataWithChild("unde_sequ_curc", (String)row.get("unde_sequ_curc"))) {
									LOG.info("XML 정상 셋팅");
								}else {
									LOG.info("XML error : " + dataToXml.getErrMessage());
								}
							}
							dataToXml.closeLoopDataWithChild();
						} else if("N".equals(undeIndiCurc)) {
							dataToXml.initLoopDataWithChild("advn_info_max", null);
							
							// 임의 값 셋팅
							if(dataToXml.setData("unde_sequ_cont", "1")	
									&& dataToXml.setData("unde_sequ_time", "20210930") 
									&& dataToXml.setData("unde_sequ_amnt", "1111") 
									&& dataToXml.setData("unde_sequ_curc", "N")) {
								LOG.info("XML 정상 셋팅");
							} else {
								LOG.info("XML error : " + dataToXml.getErrMessage());
							}
							dataToXml.closeLoopDataWithChild();
						}
					}
					
					if(dataToXml.setData("unde_fini_time", undeFiniTime)
							&& dataToXml.setData("unde_mont_nume", undeMontNume)
							&& dataToXml.setData("unde_acco_curc", undeAccoCurc)
							&& dataToXml.setData("unde_cont_curc", undeContCurc)) {
						xmlFlag = true;
					}
				}
			}

			if (xmlFlag) {
				xmlDoc = dataToXml.getxmlData();
			} else {
				LOG.error("DataToXML error code *===================\n" + dataToXml.getErrorCode());
				LOG.error("dataToXML error occur2 ===============================" + dataToXml.getErrorMsg());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		if(Strings.isNullOrEmpty(xmlDoc)){
			LOG.error("DataToXML error code *===================\n" + dataToXml.getErrorCode());
			LOG.error("dataToXML error occur2 ===============================" + dataToXml.getErrorMsg());
		}

		return xmlDoc;
	}

	/**
	 * 보증 XML데이터 전송
	 * 
	 * @param xmlDoc
	 * @param guarData
	 * @return
	 */
	public Map sendGuarXml(String xmlDoc, Map attachFileMap) {
		String recvXml = "";
		Map resultMap = Maps.newHashMap();
		try {
			
			SGIxLinker xLinker = new SGIxLinker(sgicModulePath + "/conf/sendinfo.properties",
					headOrgaCode, false);
			LOG.info("sendGuarXml start --------------------------------------------------------------------");

			boolean isOK = xLinker.doSendProcess(xmlDoc, (HashMap) attachFileMap);
			
			if (isOK) {
				LOG.info("1. Reply Document 응답 문서");
				LOG.info("--------------------------------------------------------------------");
				recvXml = xLinker.getRecvXmlData();
				LOG.info(recvXml);
				LOG.info("--------------------------------------------------------------------");
				

				// xml 응답서 문서 정보를 XmltoData로
				// 추출하여 상태값 DB에 저장
				// 매핑 정보 파일, XML 탬플릿 정보 파일
				XmlToData xmlToData = null;
				LOG.info("2. Reply Document 응답 문서 파싱");
				LOG.info("--------------------------------------------------------------------");
				xmlToData = new XmlToData(xLinker.getTempPath(), "RESPONSE", recvXml);
				if (xmlToData.getErrorCode() != 0) {
					if (LOG.isInfoEnabled())
						LOG.error("error : " + xmlToData.getErrorMsg());
					return null;
				}

				String resContNum = xmlToData.getData("res_cont_num");
				// String res_docu_num = xmlToData.getData("res_docu_num");
				String respTypeCode = xmlToData.getData("res_info_code");
				String respTypeName = xmlToData.getData("res_info_typename");
				String respMesgText = xmlToData.getData("res_info_result");
				
				LOG.info("3. 응답 결과");
				LOG.info("--------------------------------------------------------------------");
				LOG.info("계약번호 = " + resContNum);
				LOG.info("응답코드 = " + respTypeCode);
				LOG.info("응답코드명 = " + respTypeName);
				LOG.info("응답메시지 = " + respMesgText);
				
				resultMap.put("xml_data", xmlDoc);
				resultMap.put("ack_cont_numb", resContNum);
				resultMap.put("ack_resp_type_cd", respTypeCode);
				resultMap.put("ack_resp_type_nm", respTypeName);
				resultMap.put("ack_resp_mesg", respMesgText);
				resultMap.put("ack_resp_xml", recvXml);
				resultMap.put("errmsg", respMesgText);
				
				if(ElecGuaranteeConst.ACK_CD_FAIL.equals(respTypeCode)) {
					resultMap.put(Const.RESULT_STATUS,Const.FAIL);
				}else if(ElecGuaranteeConst.ACK_CD_SUCCESS.equals(respTypeCode)) {
					resultMap.put(Const.RESULT_STATUS,Const.SUCCESS);
				}
				return resultMap;
			} else {
				String errmsg = xLinker.getErrorCode() + ":" + xLinker.getErrorMsg();
				LOG.error(errmsg);
				resultMap.put(Const.RESULT_STATUS, Const.FAIL);
				resultMap.put(Const.RESULT_MSG, errmsg);
				return resultMap;
			}
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "sendGuarXml error : " + e.toString());
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, e.toString());
		}
		return resultMap;
	}
	
	/**
	 * 보증 최종응답서 XML데이터 생성
	 * 
	 * @param guarData
	 * @return
	 * @throws Exception
	 */
	public String makeFinalReponse(Map guarData) throws Exception {
		/**** 최종응답서 HEADER 시작 ****/
		String headMesgSend = guarData.get("head_mesg_send").toString();
		String headMesgRecv = guarData.get("head_mesg_recv").toString();
		String headFuncCode = guarData.get("head_func_code").toString();
		String headMesgType = guarData.get("head_mesg_type").toString();
		String headMesgName = guarData.get("head_mesg_name").toString();
		String headMesgVers = guarData.get("head_mesg_vers").toString();
		String headDocuNumb = guarData.get("head_docu_numb").toString();
		String headMangNumb = guarData.get("head_mang_numb").toString(); // 계약번호
		String headRefrNumb = guarData.get("head_refr_numb").toString();
		String headTitlName = guarData.get("head_titl_name").toString();
		String headOrgaCodeStr = headOrgaCode;
		guarData.put("head_orga_code", headOrgaCodeStr);
		/**** 최종응답서 HEADER 끝 ****/
		
		/**** 최종응답서 문서정보 시작 ****/
		String docuNumbText = guarData.get("docu_numb_text").toString();
		String docuKindCode = guarData.get("docu_kind_code").toString();
		String docuIssuDate = guarData.get("docu_issu_date").toString();
		String docuUserType = guarData.get("docu_user_type").toString();
		/**** 최종응답서 문서정보 끝 ****/
		
		/**** 최종응답서 주요계약정보 시작 ****/
		String contNumbText = guarData.get("cont_numb_text").toString();
		String contMainName = guarData.get("cont_main_name").toString();
		String contCurcCode = guarData.get("cont_curc_code").toString();
		String contMainAmnt = guarData.get("cont_main_amnt").toString();
		String applOrgaName = guarData.get("appl_orga_name").toString();
		String applOrpsDivs = guarData.get("appl_orps_divs").toString();
		String applOrpsIden = guarData.get("appl_orps_iden").toString();
		String applOwnrName = guarData.get("appl_ownr_name").toString();
		/**** 최종응답서 주요계약정보 끝 ****/
		
		/**** 최종응답서 채권자 정보 시작 ****/
		String credBondHold = guarData.get("cred_bond_hold").toString();
		/**** 최종응답서 채권자 정보 끝 ****/
		
		/**** 최종응답서 응답 정보 시작 ****/
		String respTypeCode = guarData.get("resp_type_code").toString();
		String respTypeName = guarData.get("resp_type_name").toString();
		String respMesgText = guarData.get("resp_mesg_text").toString();
		/**** 최종응답서 응답 정보 끝 ****/

		String xmlStr = null;
		// 매핑 정보 파일, XML 탬플릿 정보 파일
		DataToXml datatoxml = new DataToXml(sgicModulePath + "/templates/", "RBONGU");

		if(datatoxml.getErrorCode() != 0) {
			LOG.debug(datatoxml.getErrorMsg());
			return "";
		}
		
		/* 계약정보통보서(CONINF) */
		/* HEADER */
		try {
			if (
			/* Begin of Header */
			datatoxml.setData("head_mesg_send", headMesgSend) && datatoxml.setData("head_mesg_recv", headMesgRecv)
					&& datatoxml.setData("head_func_code", headFuncCode)
					&& datatoxml.setData("head_mesg_type", headMesgType)
					&& datatoxml.setData("head_mesg_name", headMesgName)
					&& datatoxml.setData("head_mesg_vers", headMesgVers)
					&& datatoxml.setData("head_docu_numb", headDocuNumb)
					&& datatoxml.setData("head_mang_numb", headMangNumb)
					&& datatoxml.setData("head_refr_numb", headRefrNumb)
					&& datatoxml.setData("head_titl_name", headTitlName)
					&& datatoxml.setData("head_orga_code", headOrgaCodeStr)
					/* End of Header */
					&& datatoxml.setData("docu_numb_text", docuNumbText)
					&& datatoxml.setData("docu_kind_code", docuKindCode)
					&& datatoxml.setData("docu_issu_date", docuIssuDate)
					&& datatoxml.setData("docu_user_type", docuUserType)
					&& datatoxml.setData("cont_numb_text", contNumbText)
					&& datatoxml.setData("cont_main_name", contMainName)
					&& datatoxml.setData("cont_curc_code", contCurcCode)
					&& datatoxml.setData("cont_main_amnt", contMainAmnt)
					&& datatoxml.setData("appl_orga_name", applOrgaName)
					&& datatoxml.setData("appl_orps_divs", applOrpsDivs)
					&& datatoxml.setData("appl_orps_iden", applOrpsIden)
					&& datatoxml.setData("appl_ownr_name", applOwnrName)
					&& datatoxml.setData("cred_bond_hold", credBondHold)
					&& datatoxml.setData("resp_type_code", respTypeCode)
					&& datatoxml.setData("resp_type_name", respTypeName)
					&& datatoxml.setData("resp_mesg_text", respMesgText)) {
				xmlStr = datatoxml.getxmlData();
			} else {
				LOG.error(datatoxml.getErrorMsg());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		/////////////////////// xml문서 생성 end ////////////////////////
		if (xmlStr == null) {
			LOG.error("xml문서 생성 실패");
			return "";
		}
		////////////////////// xml 문서 송신 start ///////////////////////
		return xmlStr;
	}
	
	/**
	 * 보증서 수신
	 * 
	 * @param param
	 * @return the map
	 */
	private Map receiveGuarantee(HttpServletRequest request, HttpServletResponse response){
		LOG.info(":::::::::::::::::::::::::::::::::receiveGuarantee start:::::::::::::::::::::::::::::::::");
		
		byte[] buf = null;
		FileInputStream fis = null;
		Map recvData = Maps.newHashMap();
		SGIxLinker xLinker = new SGIxLinker(sgicModulePath + "/conf/recvinfo.properties", headOrgaCode, true);
		// xLinker.setXmlEncoding("euc-kr");
		String recvDocCode = "";
		String recvXmlDoc = "";
		String recvXmlPath = "";
		String docuNumb = "";
		String err = "";
		Map result = Maps.newHashMap();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String resultMsg = "";

			while((resultMsg = br.readLine()) != null) {
				sb.append(resultMsg);
			}
			LOG.info("***************sb to String :" + sb.toString());
		} catch(Exception e) {
			LOG.error("***************error :" + e.toString());
		}
		
		try {
			/* 송신자가 보내는 전문을 대기하여 수신 받는다. */
			boolean isOK = xLinker.doRecvProcess(request, response);
	
			if (!isOK) {
				// super.getServletContext().log("isOK is false");
				err = "정보인증 정보수신 API 오류입니다 : [" + xLinker.getErrorCode() + "] : " + xLinker.getErrorMsg() + "\n";
				LOG.error("error : " + err);
				throw new CommonException("정보인증 정보수신 API 오류입니다 : [" + xLinker.getErrorCode() + "] : " + xLinker.getErrorMsg()); // NOPMD Exception
			} else {
				LOG.info("정보인증 정보수신 API 수신 성공");
				/* 전문코드 */
				recvDocCode = xLinker.getRecvDocCode();
				/* 수신 전문데이터 */
				recvXmlDoc = xLinker.getRecvXmlData();
	
				if (recvXmlDoc.equals("")) {
					/* 수신 전문 경로+파일명 */
					recvXmlPath = xLinker.getDecXmlPath();
					fis = new FileInputStream(recvXmlPath);
					buf = new byte[fis.available()];
					fis.read(buf);
					fis.close();
					recvXmlDoc = new String(buf);
				}
				XmlToData xmlToData = new XmlToData(sgicModulePath + "/templates/", recvDocCode,
						recvXmlDoc);
	
				if(xmlToData.getErrorCode() != 0) {
					LOG.error("xmlToData getErrorCode : " + xmlToData.getErrorCode() + "\n");
					LOG.error("xmlToData getErrorMsg : " + xmlToData.getErrorMsg() + "\n");
					LOG.error("xmlToData getErrMessage : " + xmlToData.getErrMessage() + "\n");
					LOG.error("보증서 정보를 가져오는데 실패하였습니다.");
					result.put(Const.RESULT_STATUS, Const.FAIL);
					result.put(Const.RESULT_MSG, xmlToData.getErrorMsg());
					return result;
				} else {
					LOG.info("보증서 정보를 가져오는데 성공하였습니다.");
					LOG.info("==============recvDocCode : " + recvDocCode);
					// 보증데이터 셋팅 함수
					recvData = makeRecvData(xmlToData,recvDocCode);
					
					if(Const.FAIL.equals(recvData.get(Const.RESULT_STATUS))) { // 보증 데이터 셋팅 실패
						LOG.error("보증서 데이터 셋팅 실패");
						result.put(Const.RESULT_STATUS, Const.FAIL);
						result.put(Const.RESULT_MSG, recvData.get(Const.RESULT_MSG));
						return result;
					}
					docuNumb = (String)recvData.get("docu_numb");
				}
			}
	
			/*
			 * 일반적인 Ack성 Response를 보낼때의 샘플 코드임
			 */
			String responseXml = "";
			String mesgType = "";
	
			if (recvDocCode.equals("CONGUA") || recvDocCode.equals("CONINF")) {
				mesgType = ElecGuaranteeConst.CONINF;
			} else if (recvDocCode.equals("PREGUA") || recvDocCode.equals("PREINF")) {
				mesgType = ElecGuaranteeConst.PREINF;
			} else if (recvDocCode.equals("FLRGUA") || recvDocCode.equals("FLRINF")) {
				mesgType = ElecGuaranteeConst.FLRINF;
			} else if (recvDocCode.equals("PAYGUA") || recvDocCode.equals("PAYINF")) {
				mesgType = ElecGuaranteeConst.PAYINF;
			}
	
			recvData.put("mesg_type", mesgType);
			
			// 통보서를 보낸적이 있는지 확인
			int cnt = sgicRepository.checkGuarNofndoc(recvData);
			if(cnt == 0) { // 통보서를 보낸적이 없음
				responseXml = xLinker.responseAck(ElecGuaranteeConst.ACK_CD_FAIL, "보증 통보서가 존재하지 않습니다.", docuNumb);
				LOG.info("보증 통보서가 존재하지 않습니다."); // ESEGRIF에 데이터가 없음. 협력사에서 온라인보증요청 할 때 생성되는 데이터임
				LOG.info("response_xml : " + responseXml); // ESEGRIF에 데이터가 없음. 협력사에서 온라인보증요청 할 때 생성되는 데이터임
				result.put(Const.RESULT_STATUS, Const.FAIL);
				result.put(Const.RESULT_MSG, "보증 통보서가 존재하지 않습니다.");
				result.put("response_result", ElecGuaranteeConst.ACK_CD_FAIL);
				result.put("response_xml", responseXml);
				return result;
			}
			
			// 수신받은 보증서가 있는지 확인
			cnt = sgicRepository.checkCntEgur(recvData);
			if(cnt > 0) { // 이미 보증서가 존재함  - 현재상태가 거부인 경우만 보증서를 재 수신 받을 수 있음 (prog_sts : Z 거부임 거부할때는 esegrhd 데이터 삭제함)
				responseXml = xLinker.responseAck(ElecGuaranteeConst.ACK_CD_FAIL, "동일한 계약번호의 보증서가 이미 발급되었습니다.", docuNumb);
				LOG.info("동일한 계약번호의 보증서가 이미 발급되었습니다.");
				LOG.info("response_xml : " + responseXml); 
				result.put(Const.RESULT_STATUS, Const.FAIL);
				result.put(Const.RESULT_MSG, "동일한 계약번호의 보증서가 이미 발급되었습니다.");
				result.put("response_result", ElecGuaranteeConst.ACK_CD_FAIL);
				result.put("response_xml", responseXml);
				return result;
			}
			
			Map guarInfo = sgicRepository.getEgurInfo(recvData); // 보증보험 조회
			recvData.put("ecntr_uuid", (String)guarInfo.get("ecntr_uuid"));
			recvData.put("ten_id", guarInfo.get("ten_id"));
			recvData.put("gur_uuid", guarInfo.get("gur_uuid"));
			if(recvXmlDoc != null && !recvXmlDoc.equals("")) {
				responseXml = xLinker.responseAck(ElecGuaranteeConst.ACK_CD_SUCCESS, "정보 수신업무가 정상적으로 수행되었습니다. ", docuNumb);
				LOG.info("정보 수신업무가 정상적으로 수행되었습니다.");
				LOG.info("response_xml : " + responseXml);
				result.put(Const.RESULT_STATUS, Const.SUCCESS);
				result.put(Const.RESULT_MSG, "정보 수신업무가 정상적으로 수행되었습니다.");
				result.put("response_result", ElecGuaranteeConst.ACK_CD_SUCCESS);
				result.put("response_xml", responseXml);
				
				recvData.put("xml_data", recvXmlDoc);
				recvData.put("xml_path", recvXmlPath);
				recvData.put("recvDocCode", recvDocCode);
				recvData.put("ack_resp_xml", responseXml);
				recvData.put("tenant", (String)guarInfo.get("sys_id"));
				
				result.put("recv_data", recvData);
				return result;
			} else {
				responseXml = xLinker.responseAck(ElecGuaranteeConst.ACK_CD_FAIL, "xml데이터 생성에 실패했습니다.", docuNumb);
				LOG.info("xml데이터 생성에 실패했습니다."); 
				LOG.info("response xml : " + responseXml); 
				result.put(Const.RESULT_STATUS, Const.FAIL);
				result.put(Const.RESULT_MSG, "xml데이터 생성에 실패했습니다.");
				result.put("response_result", ElecGuaranteeConst.ACK_CD_FAIL);
				result.put("response_xml", responseXml);
				return result;
			}
		} catch(Exception e) {
			LOG.error("error : " + e.toString());
			LOG.error("error message : " + e.getMessage());
			LOG.error("error cause : " + e.getCause());
			result.put(Const.RESULT_STATUS, Const.FAIL);
			result.put(Const.RESULT_MSG, e.toString());
			return result;
		} finally {
			if(fis !=null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOG.error("error cause : " + e.toString());
				}
			}
		}
	}
	
	/**
	 * 보증서 수신
	 * 
	 * @param param
	 * @return the map
	 */
	public void recv(HttpServletRequest request, HttpServletResponse response) {
		Map result = this.receiveGuarantee(request, response);
		String responseResult = (String)result.get("response_result");
		String responseXml = (String)result.get("response_xml");
		
		if(responseXml == null) { // xml 형태로 응답을 보내는 경우가 아닌 경우 (우리쪽에서 에러가 발생함)
			responseXml = (String)result.get(Const.RESULT_MSG);
		}
		
		if(ElecGuaranteeConst.ACK_CD_SUCCESS.equals(responseResult)) { // (SA : 정상적으로 보증서 수신받음)
			Map recvData = (Map)result.get("recv_data");
			this.recvAfterProcess(recvData); // 정상적으로 보증서 수신 받은 후 처리
		}
		
		ServletOutputStream output = null;
		try {
			output = response.getOutputStream();
			output.write(responseXml.getBytes());
			output.flush();
			if (output != null)
				output.close();
		} catch (IOException e) {
			LOG.error("class : " + this.getClass().toString() + "recv error : " + e.toString());
		} finally {
			if(output != null) {
				try {
					output.close();
				} catch (IOException e) {
					LOG.error("class : " + this.getClass().toString() + "recv error : " + e.toString());
				}
			}
		}
	}

	/**
	 * XML형테의 보증서 수신 데이터를  map으로 변환
	 * 
	 * @param param
	 * @return the map
	 */
	private Map makeRecvData(XmlToData xmlToData, String recvDocCode){
		Map recvData = Maps.newHashMap();
		try {
			if (recvDocCode.equals("CONGUA") || recvDocCode.equals("PREGUA") || recvDocCode.equals("FLRGUA") || recvDocCode.equals("PAYGUA") 
					|| recvDocCode.equals("CONINF") || recvDocCode.equals("PREINF") || recvDocCode.equals("FLRINF") || recvDocCode.equals("PAYINF") ) {
				/* Begin of Header */
				recvData.put("head_mesg_send", xmlToData.getData("head_mesg_send")); // 송신자ID
				recvData.put("head_mesg_recv", xmlToData.getData("head_mesg_recv")); // 수신자ID
				recvData.put("head_func_code", xmlToData.getData("head_func_code")); // 문서기능코드
				recvData.put("head_mesg_type", xmlToData.getData("head_mesg_type")); // 문서코드
				recvData.put("head_mesg_name", xmlToData.getData("head_mesg_name")); // 문서명
				recvData.put("head_mesg_vers", xmlToData.getData("head_mesg_vers")); // 문서버젼
				recvData.put("head_docu_numb", xmlToData.getData("head_docu_numb")); // 문서번호
				recvData.put("head_mang_numb", xmlToData.getData("head_mang_numb")); // 문서관리번호
				recvData.put("head_refr_numb", xmlToData.getData("head_refr_numb")); // 참조번호
				recvData.put("head_titl_name", xmlToData.getData("head_titl_name")); // 문서명과 동일
				// recvData.put("head_orga_code",
				// xmlToData.getData("head_orga_code"));
				String headOrgaCodeStr = headOrgaCode;
				recvData.put("head_orga_code", headOrgaCodeStr); // 연계기관코드
				
				//docu_numb = xmlToData.getData("head_docu_numb");
				recvData.put("docu_numb", xmlToData.getData("head_docu_numb"));
				/* End of Header */

				/* Begin of 보증기관 */
				recvData.put("bond_orga_name", xmlToData.getData("bond_orga_name")); // 보증기관명
				recvData.put("bond_orga_iden", xmlToData.getData("bond_orga_iden")); // 보증기관 사업자번호
				recvData.put("bond_orga_addn", xmlToData.getData("bond_orga_addn")); // 보증기관 부가정보
				recvData.put("bond_orga_ceo", xmlToData.getData("bond_orga_ceo"));   // 서울보증보험 대표자 성명
				/* End of 보증기관 */
				
				/* Begin of 공공기관 */
				/*
				 * 피보험자 세부정보(수요자 정보) - G2B 인경우만 다를 수 있음. - 일반적인 경우는 채권자
				 * 정보와 같은 값이 세팅됨
				 * 
				 */
				recvData.put("govn_orga_name", xmlToData.getData("govn_orga_name")); // 피보험자 상호
				recvData.put("govn_orga_iden", xmlToData.getData("govn_orga_iden")); // 피보험자 사업자번호
				recvData.put("govn_orga_addn", xmlToData.getData("govn_orga_addn")); // 부가정보 : 피보험자 사업자번호
				recvData.put("govn_ownr_name", xmlToData.getData("govn_ownr_name")); // 피보험자 대표자 성명
				/* End of 공공기관 */

				/* Begin of 작성일자 */
				recvData.put("docu_issu_date", xmlToData.getData("docu_issu_date")); // 증권발급일자
				/* End of 작성일자 */
				
				/* Begin of 업무구분 */
				recvData.put("busi_clas_code", xmlToData.getData("busi_clas_code")); // 업무구분코드
				recvData.put("busi_clas_name", xmlToData.getData("busi_clas_name")); // 업무구분내용
				/* End of 업무구분 */
				
				/* Begin of 보증서(증권) */
				recvData.put("bond_numb_text", xmlToData.getData("bond_numb_text")); // 증권구분번호
				/* End of 보증서(증권) */
				
				/* Begin of 보증내용 */
				recvData.put("bond_penl_text", xmlToData.getData("bond_penl_text")); // 보증금액(한글)
				recvData.put("bond_penl_amnt", xmlToData.getData("bond_penl_amnt")); // 보증금액(숫자)
				recvData.put("penl_curc_code", xmlToData.getData("penl_curc_code")); // 통화코드 : WON(원화),USD(미달러),EUR(유로화)등
				recvData.put("bond_begn_date", xmlToData.getData("bond_begn_date")); // 보험기간 시작일 : YYYYMMDD
				recvData.put("bond_fnsh_date", xmlToData.getData("bond_fnsh_date")); // 보험기간  종료일 : YYYYMMDD
				recvData.put("bond_prem_amnt", xmlToData.getData("bond_prem_amnt")); // 보험료(숫자)
				recvData.put("prem_curc_code", xmlToData.getData("prem_curc_code")); // 통화코드 : WON(원화) - 보험료는 항상 원하임
				recvData.put("bond_pric_rate", xmlToData.getData("bond_pric_rate")); // 보증금율
				recvData.put("appl_orga_name", xmlToData.getData("appl_orga_name")); // 보험계약자 상호(이름)
				recvData.put("appl_orps_divs", xmlToData.getData("appl_orps_divs")); // 보험계약자 사업자/주민등록번호 구분코드
				recvData.put("appl_orps_iden", xmlToData.getData("appl_orps_iden")); // O인 경우 사업자번호, P인 경우 주민등록번호
				recvData.put("appl_ownr_name", xmlToData.getData("appl_ownr_name")); // 보험계약자 대표자 성명
				recvData.put("cred_orga_name", xmlToData.getData("cred_orga_name")); // 채권자 기관명 - 피보험자 상호/이름으로 함
				recvData.put("cred_orps_divs", xmlToData.getData("cred_orps_divs")); // O인 경우 사업자번호, P인 경우 주민등록번호
				recvData.put("cred_orps_iden", xmlToData.getData("cred_orps_iden")); // 채권자 사업자번호 또는 주민번호
				recvData.put("bond_hold_name", xmlToData.getData("bond_hold_name")); // 채권자명 : 증권상 기재된 피보험자명
				/* End of 보증내용 */
				
				/* Begin of 보증계약(주) */
				recvData.put("cont_name_text", xmlToData.getData("cont_name_text")); // 계약건명
				recvData.put("cont_main_amnt", xmlToData.getData("cont_main_amnt")); // 계약금액
				recvData.put("cont_curc_code", xmlToData.getData("cont_curc_code")); // 통화코드:WON(원화),  USD(미달러), EUR(유로화) 등
				recvData.put("cont_numb_text", xmlToData.getData("cont_numb_text")); // 계약번호
				recvData.put("cont_main_date", xmlToData.getData("cont_main_date")); // 계약일자 :  YYYYMMDD
				recvData.put("cont_begn_date", xmlToData.getData("cont_begn_date")); // 계약시작일 : YYYYMMDD
				recvData.put("cont_fnsh_date", xmlToData.getData("cont_fnsh_date")); // 계약종료일 YYYYMMDD
				/* End of 보증계약(주) */

				/* Begin of 특기사항 */
				recvData.put("spcl_cond_text", xmlToData.getData("spcl_cond_text")); // 특기사항
				/* End of 특기사항 */
				/* Begin of 특별약관 */
				recvData.put("spcl_prov_text", xmlToData.getData("spcl_prov_text")); // 특별약관
				/* End of 특별약관 */
				/* Begin of 보증문 */
				recvData.put("bond_stat_text", xmlToData.getData("bond_stat_text")); // 보증문
				/* End of 보증문 */
				/* Begin of 발행자 */

				recvData.put("issu_addr_txt1", xmlToData.getData("issu_addr_txt1")); // 주소 1 : 보증기관 주소
				recvData.put("issu_addr_txt2", xmlToData.getData("issu_addr_txt2")); // 주소 2 : 보증기관 상세주소
				recvData.put("issu_orga_name", xmlToData.getData("issu_orga_name")); // 보증기관 상호
				recvData.put("issu_ownr_name", xmlToData.getData("issu_ownr_name")); // 보증기관 대표자 이름
				recvData.put("issu_dept_name", xmlToData.getData("issu_dept_name")); // 대리인 지점-부서(또는 대리점)
				recvData.put("issu_dept_ownr", xmlToData.getData("issu_dept_ownr")); // 대리인성명(지점장 또는 부서장 성명)
				recvData.put("chrg_name_text", xmlToData.getData("chrg_name_text")); // 지점(부서) 담당자 성명
				recvData.put("chrg_phon_text", xmlToData.getData("chrg_phon_text")); // 지점(부서) 담당자 전화번호
				recvData.put("chrg_faxs_text", xmlToData.getData("chrg_faxs_text")); // 지점(부서) 담당자 FAX 번호

				// recvData.put("invi_agnc_name",
				// xmlToData.getData("invi_agnc_name")); //모집 대리점 명
				// recvData.put("invi_ownr_name",
				// xmlToData.getData("invi_ownr_name")); //모집 대리점 대표자 성명
				// recvData.put("invi_phon_text",
				// xmlToData.getData("invi_phon_text")); //모집 대리점 전화번호
				/* End of 발행자 */
				/* Begin of 비고 */
				// recvData.put("gene_note_text",
				// xmlToData.getData("gene_note_text"));
				/* End of 비고 */
			}
			
			if (recvDocCode.equals("PREGUA") || recvDocCode.equals("PREINF")) {
				/* Begin of 부수계약 */
				recvData.put("cont_paym_amnt", xmlToData.getData("cont_paym_amnt")); // 선금액(숫자)
				recvData.put("paym_curc_code", xmlToData.getData("paym_curc_code")); // 통화코드:WON(원화),USD(미달러),EUR(유로화) 등
				recvData.put("cont_paym_date", xmlToData.getData("cont_paym_date")); // 지급예정일 : YYYYMMDD
				recvData.put("cont_perf_date", xmlToData.getData("cont_perf_date")); // 계약 이행 기일(YYYYMMDD)
				/* End of 부수계약 */
			} else if (recvDocCode.equals("FLRGUA") || recvDocCode.equals("FLRINF")) {
				/* Begin of 부수계약 */
				recvData.put("morg_begn_date", xmlToData.getData("morg_begn_date")); // 하자담보 책임 시작일(YYYYMMDD)
				recvData.put("morg_fnsh_date", xmlToData.getData("morg_fnsh_date")); // 하자담보 책임 종료일(YYYYMMDD)
				recvData.put("cont_perf_date", xmlToData.getData("cont_perf_date")); // 계약 이행 기일(YYYYMMDD)
				/* End of 부수계약 */
			} else if (recvDocCode.equals("PAYGUA") || recvDocCode.equals("PAYINF")) {
				if( recvDocCode.equals("PAYGUA") ) {
					recvData.put("obli_main_amnt", xmlToData.getData("obli_main_amnt")); // 보험기간 개시전 발생된 채무액(숫자)
					recvData.put("obli_curc_code", xmlToData.getData("obli_curc_code")); // 보험기간 개시전 발생된 채무액 통화코드:WON(원화), USD(미달러), EUR(유로화) 등
				}
				/* End of 부수계약 */
			}
		} catch(Exception e) {
			LOG.error("error :" +e.toString());
			LOG.error("error message :" +e.getMessage());
			LOG.error("error cause :" +e.getCause());
			recvData.put(Const.RESULT_STATUS, Const.FAIL);
			recvData.put(Const.RESULT_MSG, e.toString());
		}
		return recvData;
	}

	/**
	 * 보증서 수신 후 처리
	 * 
	 * @param param
	 * @return the map
	 */
	public void recvAfterProcess(Map recvData) {
		// 기존 보증서 삭제
		sgicRepository.deleteRecvEgurdoc(recvData);

		ElecGuaranteeUtil.setBondDataStringToInteger(recvData);

		// 보증서 내용 저장
		sgicRepository.insertEgurdoc(recvData);

		ElecGuaranteeUtil.setRecvBondData(recvData);

		// 보증신청 테이블 상태값 변경
		sgicRepository.updateCtgr(recvData);
	}
	
	
	/**
	 * 보증서 수신 테스트(통보서데이터를 조회 후 에 보증서로 값을 변경하고, 전자보증용 서버에 전송하는 함수)
	 * 
	 * @param param 
	 * @return the map
	 */
	public ResultMap recvSgicTest(Map param) {

		String headMesgType = sgicRepository.getBondType(param); // 보증종류 확인
		param.put("head_mesg_type", headMesgType);

		Map bondInfo = sgicRepository.getBondInfo(param);
		
		if(bondInfo == null) {
			return ResultMap.FAIL();
		}
		
		if(ElecGuaranteeConst.CONINF.equals(headMesgType)) { // 계약이행
			bondInfo.put("egurdoc_bond_type", "CONGUA");
		}else if(ElecGuaranteeConst.PREINF.equals(headMesgType)) {	// 선급금이행
			bondInfo.put("egurdoc_bond_type", "PREGUA");
		}else if(ElecGuaranteeConst.FLRINF.equals(headMesgType)) {	// 하자이행
			bondInfo.put("egurdoc_bond_type", "FLRGUA");
		}else if(ElecGuaranteeConst.PAYINF.equals(headMesgType)) {	// 지급이행
			bondInfo.put("egurdoc_bond_type", "PAYGUA");
		}
		
		String xmlDoc = "";	// 통보서 xml 변수
		// 통보서 xml 구성
		ElecGuaranteeUtil.setBondDataToIFNoticDevinition(bondInfo);
		xmlDoc = this.composeXMLTest(bondInfo);
		LOG.info("\n\n++++++++++xmlDoc start++++++++++++\n");
		LOG.info("xmlDoc : " + xmlDoc);
		LOG.info("\n++++++++++xmlDoc end++++++++++++\n\n");

		if (xmlDoc == null) {
			return ResultMap.FAIL();
		}
		
		Map resultMap = this.sendGuarXmlTest(xmlDoc);
		
		if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS)) ) {
			if(ElecGuaranteeConst.ACK_CD_SUCCESS.equals(resultMap.get("ack_resp_type_cd"))) {
				return ResultMap.SUCCESS();
			}else {
				LOG.info("error:" + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
		} else {
			return ResultMap.FAIL();
		}
	}
	
	/**
	 * 보증서 수신 테스트(통보서데이터를 조회 후 에 보증서로 값을 변경하여 XML을 만듬)
	 * 
	 * @param param 
	 * @return the map
	 */
	public String composeXMLTest(Map guarData) {
		String cntrNo = (String)guarData.get("cntr_no"); // 계약번호
		Object cntrRevNo = guarData.get("cntr_revno");
		int intRevNo = 0 ;
		if(Integer.class == cntrRevNo.getClass()) {
			intRevNo = (Integer)cntrRevNo;
		} else if(BigDecimal.class == cntrRevNo.getClass()) {
			intRevNo = ((BigDecimal)cntrRevNo).intValue();
		}
		String revisionNo = EdocStringUtil.fill(String.valueOf(intRevNo), 2, '0');	// 계약차수 2자리로 만들기 예) 1차수면 01
		String policyNo = EdocStringUtil.fill(cntrNo, 18, '0');	// 계약차수 2자리로 만들기 예) 1차수면 01
		String cntrNoSgic = cntrNo + revisionNo;	// 약번호 체계 + 계약차수 2자리 예) CNTR190500021 + 01 = CNTR19050002101 (서울보증사에서 인지하는 계약번호)
		/**** 보증서 HEADER 시작 ****/
		String headMesgSend = sgicId.trim(); 	/* [필수] 전문송신기관 송신 ID값은 edoc.properties sender.id 값을 변경해주어야함*/
		//String headMesgSend = null; 			/* [필수] 전문송신기관 송신 ID값은 edoc.properties sender.id 값을 변경해주어야함*/
		String headMesgRecv = senderId.trim(); 	/* [필수] 수신자 서울보증보험 고정값 */
		String headFuncCode = funcCode.trim(); 	/* [필수] 문서기능 9:원본 , 1: 취소 , 53 테스트 53이어도 상관없음(운영일때도) */
		String headMesgType = (String) guarData.get("egurdoc_bond_type"); /* [필수] 문서코드 */
		String headMesgName = (String) guarData.get("head_mesg_name");    /* [필수] 문서명 */
		String headMesgVers = "1.0"; /* [선택] 전자문서버전 */
		//String head_docu_numb = "004" + guarData.get("bond_kind_code") + cntr_no_sgic + " " + revision_no; /* [필수] 문서번호 */
		String headDocuNumb = "004" + guarData.get("bond_kind_code") + policyNo + " " + revisionNo; /* [필수] 문서번호 */
		
		String headMangNumb = headMesgSend + "." + CalendarUtil.formatNow("yyyyMMddHHmmss") + ".12345." + headDocuNumb; /* [필수] 문서관리번호 */
		String headRefrNumb = headDocuNumb; /* [필수] 참조번호 */
		String headTitlName = headMesgName; /* [필수] 문서개요 */
		String headOrgaCodeStr = headOrgaCode.trim(); /* [필수] 연계기관코드 */
		
		/**** 통보서 보험계약정보 시작 ****/
		String bondOrgaName = "서울보증보험";
		String bondOrgaIden = "1208113002";
		String bondOrgaCeo = "test대성";
		
		String govnOrgaName = (String) guarData.get("cred_orga_name");
		String govnOrgaIden = (String) guarData.get("cred_orps_iden");
		String govnOwnrName = (String) guarData.get("cred_ownr_name");
		String docuIssuDate = (String) guarData.get("cont_begn_date");
		String bondNumbText = headDocuNumb;
		//String bondPenlAmnt = guarData.get("cont_main_amnt").toString();
		String bondPenlAmnt = guarData.get("bond_penl_amnt").toString();
		String contMainAmnt = guarData.get("cont_main_amnt").toString();
		//String bondPenlText = EdocStringUtil.convertNumToKr(Long.parseLong(contMainAmnt));
		String bondPenlText = EdocStringUtil.convertNumToKr(Long.parseLong(bondPenlAmnt));

		String penlCurcCode = (String) guarData.get("bond_curc_code");
		String bondBegnDate = (String) guarData.get("bond_begn_date");
		String bondFnshDate = (String) guarData.get("bond_fnsh_date");
		String bondPremAmnt = guarData.get("bond_penl_amnt").toString();
		String premCurcCode = (String) guarData.get("bond_curc_code");
		String bondPricRate = (String) guarData.get("gur_ro").toString();
		
		String applOrgaName = guarData.get("appl_orga_name").toString(); 	/* [필수] 기관명 */
		String applOrpsDivs = guarData.get("appl_orps_divs").toString();	/* [필수] 개인/사업자 구분 코드 O.사업자 P.개인*/
		String applOrpsIden = guarData.get("appl_orps_iden").toString();	/* [필수] 사업자/주민번호 */
		String applOwnrNumb = guarData.get("appl_ownr_numb").toString();	/* [필수] 대표자 주민등록번호 1111111111111 고정값*/
		String credOrgaName = guarData.get("cred_orga_name").toString(); 	/* [필수] 기관명 */
		String credOrpsDivs = guarData.get("cred_orps_divs").toString();	/* [필수] 개인/사업자 구분 코드 O.사업자 P.개인*/
		String credOrpsIden = guarData.get("cred_orps_iden").toString(); /* [필수] 사업자/주민번호 */
		String bondHoldName = guarData.get("cred_ownr_name").toString();
		
		String contNameText = (String) guarData.get("cont_name_text");	/* [필수] 계약명 특수문자 제외 */
		String contCurcCode = (String) guarData.get("cont_curc_code"); 	/* [필수] 계약금액(원화) 통화코드(WON) */
		String contNumbText = cntrNoSgic;	/* [필수] 계약번호 피보험자 측 계약번호 */
		String contMainDate = (String) guarData.get("cont_main_date"); 	/* [선택] 계약체결일 - YYYYMMDD */
		String contBegnDate = (String) guarData.get("cont_begn_date");	/* [필수] 계약시작일자 */
		String contFnshDate = (String) guarData.get("cont_fnsh_date");	/* [필수] 계약종료일자*/
		
		String morgBegnDate = (String) guarData.get("morg_begn_date");	/* [필수] 하자보수 책임시작일*/
		String morgFnshDate = (String) guarData.get("morg_fnsh_date");	/* [필수] 하자보수 책임종료일*/
		
		String contPaymAmnt = (String) guarData.get("prep_paym_amnt").toString();	/* [필수] 선금액*/
		String paymCurcCode = (String) guarData.get("prep_curc_code");	/* [필수] 통화코드*/
		String contPaymDate = (String) guarData.get("prep_paym_date");	/* [필수] 지급예정일*/
		String contPerfDate = (String) guarData.get("prep_paym_date");	/* [필수] 계약이행 기일*/
		String applOwnrName = (String) guarData.get("appl_ownr_name");	/* [필수] 보험계약자 대표자 성명*/
		
		String spclCondText = "없음";	
		String bondStatText = "없음";
		String spclProvText = "없음";
		String issuAddrTxt1 = "서울특별시 영등포구";	
		String issuAddrTxt2 = "당산로 SK V1";	
		String issuOrgaName = "서울보증보험";			
		String issuOwnrName = "이대성";			
		String issuDeptName = "당산대리점";		
		String issuDeptOwnr = "대성이";			
		String chrgNameText = "성대리";			
		String chrgPhonText = "01011111111";	
		String chrgFaxsText = "3452-1111";	
//		String inviAgncName = "당산대리점";	
//		String inviOwnrName = "이대성";		
//		String inviPhonText = "03111111111";	
		String geneNoteText = "없음";		
		String obliMainAmnt = "1000";
		String obliCurcCode = "WON";

		/* 통보서 테이블에 저장하기위해 보증데이터 put함*/
		guarData.put("head_mesg_send", headMesgSend);
		guarData.put("head_mesg_recv", headMesgRecv);
		guarData.put("head_func_code", headFuncCode);
		guarData.put("head_mesg_vers", headMesgVers);
		guarData.put("head_docu_numb", headDocuNumb);
		guarData.put("head_mang_numb", headMangNumb);
		guarData.put("head_refr_numb", headRefrNumb);
		guarData.put("head_titl_name", headTitlName);
		guarData.put("head_orga_code", headOrgaCodeStr);
		
		LOG.info("sgic test module Path : " + sgicTestModulePath);
		LOG.info("headMesgType : " + headMesgType);
		// 매핑 정보 파일, XML 탬플릿 정보 파일
		DataToXml dataToXml = new DataToXml(sgicTestModulePath + "/templates/", headMesgType);
		
		if(dataToXml.getErrorCode() != 0) {
			LOG.error("DataToXML error code *===================\n" + dataToXml.getErrorCode());
			LOG.error("DataToXML error occur *===================\n" + dataToXml.getErrorMsg());
			LOG.error("DataToXML error occur *===================\n" + dataToXml.getErrMessage());
			return null;
		} else {
			LOG.info("DataToXML Data :" +dataToXml.getxmlData());
		}
		
		/* 계약정보통보서(CONINF) */
		/* HEADER */
		boolean xmlFlag = false;
		String xmlDoc = null;
		/* Begin of Header */
		/* End of 수요자 정보 */
		try {
			if (dataToXml.setData("head_mesg_send", headMesgSend)
					&& dataToXml.setData("head_mesg_recv", headMesgRecv)
					&& dataToXml.setData("head_func_code", headFuncCode)
					&& dataToXml.setData("head_mesg_type", headMesgType)
					&& dataToXml.setData("head_mesg_name", headMesgName)
					&& dataToXml.setData("head_mesg_vers", headMesgVers)
					&& dataToXml.setData("head_docu_numb", headDocuNumb)
					&& dataToXml.setData("head_mang_numb", headMangNumb)
					&& dataToXml.setData("head_refr_numb", headRefrNumb)
					&& dataToXml.setData("head_titl_name", headTitlName)
					//&& dataToXml.setData("head_orga_code", head_orga_code)
					&& dataToXml.setData("bond_stat_text", bondStatText)
					&& dataToXml.setData("spcl_prov_text", spclProvText)
					
					
					&& dataToXml.setData("bond_orga_name",bondOrgaName)
					&& dataToXml.setData("bond_orga_iden",bondOrgaIden)
					&& dataToXml.setData("bond_orga_ceo", bondOrgaCeo)
					&& dataToXml.setData("govn_orga_name",govnOrgaName)
					&& dataToXml.setData("govn_orga_iden",govnOrgaIden)
					&& dataToXml.setData("govn_ownr_name",govnOwnrName)
					&& dataToXml.setData("docu_issu_date",docuIssuDate)
					&& dataToXml.setData("bond_numb_text",bondNumbText)
					&& dataToXml.setData("bond_penl_text",bondPenlText)
					&& dataToXml.setData("penl_curc_code",penlCurcCode)
					&& dataToXml.setData("bond_prem_amnt",bondPremAmnt)
					&& dataToXml.setData("prem_curc_code",premCurcCode)
					&& dataToXml.setData("bond_pric_rate",bondPricRate)
					&& dataToXml.setData("bond_hold_name",bondHoldName)
					&& dataToXml.setData("cont_main_date",contMainDate)
					&& dataToXml.setData("spcl_cond_text",spclCondText)
					&& dataToXml.setData("issu_addr_txt1",issuAddrTxt1)
					&& dataToXml.setData("issu_addr_txt2",issuAddrTxt2)
					&& dataToXml.setData("issu_orga_name",issuOrgaName)
					&& dataToXml.setData("issu_ownr_name",issuOwnrName)
					&& dataToXml.setData("issu_dept_name",issuDeptName)
					&& dataToXml.setData("issu_dept_ownr",issuDeptOwnr)
					&& dataToXml.setData("chrg_name_text",chrgNameText)
					&& dataToXml.setData("chrg_phon_text",chrgPhonText)
					&& dataToXml.setData("chrg_faxs_text",chrgFaxsText)
					
					//&& dataToXml.setData("invi_agnc_name",invi_agnc_name)
					//&& dataToXml.setData("invi_ownr_name",invi_ownr_name)
					//&& dataToXml.setData("invi_phon_text",invi_phon_text)
					&& dataToXml.setData("gene_note_text",geneNoteText)
					/* End of Header */
					
					/* Begin of 보험계약정보 */
					//&& dataToXml.setData("bond_kind_code", bond_kind_code)
					&& dataToXml.setData("bond_begn_date", bondBegnDate)
					&& dataToXml.setData("bond_fnsh_date", bondFnshDate)
					//&& dataToXml.setData("bond_curc_code", bond_curc_code)
					&& dataToXml.setData("bond_penl_amnt", bondPenlAmnt)
					//&& dataToXml.setData("bond_appl_code", bond_appl_code)
					/* End of 보험계약정보 */
					
					/* Begin of 주요계약정보 */
					&& dataToXml.setData("cont_numb_text", contNumbText)
					&& dataToXml.setData("cont_name_text", contNameText)
					&& dataToXml.setData("cont_curc_code", contCurcCode)
					&& dataToXml.setData("cont_main_amnt", contMainAmnt)
					&& dataToXml.setData("cont_begn_date", contBegnDate)
					&& dataToXml.setData("cont_fnsh_date", contFnshDate)
					/* End of 주요계약정보 */
					
					/* Begin of 계약정보 */
					/* End of 계약정보 */

					/* Begin of 채권자 정보 */
					&& dataToXml.setData("cred_orga_name", credOrgaName)
					&& dataToXml.setData("cred_orps_divs", credOrpsDivs)
					&& dataToXml.setData("cred_orps_iden", credOrpsIden)
					/* End of 채권자 정보 */
					
					/* Begin of 계약자 정보 */
					&& dataToXml.setData("appl_orga_name", applOrgaName)
					&& dataToXml.setData("appl_orps_divs", applOrpsDivs)
					&& dataToXml.setData("appl_orps_iden", applOrpsIden)
					&& dataToXml.setData("appl_ownr_name", applOwnrName)) {
				
				if("CONGUA".equals(headMesgType)) {
					xmlFlag = true;
					// 계약이행
					/*if (dataToXml.setData("cont_begn_date", cont_begn_date)
							&& dataToXml.setData("cont_fnsh_date", cont_fnsh_date)) {
						xmlFlag = true;
					}*/
				} else if("PREGUA".equals(headMesgType)) {
					// 선금이행
					if(dataToXml.setData("cont_paym_amnt", contPaymAmnt)
							&& dataToXml.setData("paym_curc_code", paymCurcCode)
							&& dataToXml.setData("cont_paym_date", contPaymDate)
							&& dataToXml.setData("cont_perf_date", contPerfDate)) {
						xmlFlag = true;
					}
				} else if("FLRGUA".equals(headMesgType)) {
					// 하자이행
					if(dataToXml.setData("morg_begn_date", morgBegnDate)
							&& dataToXml.setData("morg_fnsh_date", morgFnshDate)) {
						xmlFlag = true;
					}
				} else if("PAYGUA".equals(headMesgType)) {
					if(dataToXml.setData("obli_main_amnt", obliMainAmnt)
							&& dataToXml.setData("obli_curc_code", obliCurcCode)) {
						xmlFlag = true;
					}
				}
			}

			if (xmlFlag) {
				xmlDoc = dataToXml.getxmlData();
				LOG.info("xmlDoc : " + xmlDoc);
				LOG.info("dataToXml.getErrorCode() : " + dataToXml.getErrorCode());
				LOG.info("dataToXml.getErrMessage() : " + dataToXml.getErrMessage());
				LOG.info("dataToXml.getErrorMsg() : " + dataToXml.getErrorMsg());
			} else {
				LOG.error("실패");
				LOG.error("DataToXML error code  *===================\n" + dataToXml.getErrorCode());
				LOG.error("dataToXML error occur2 ===============================" + dataToXml.getErrorMsg());
				LOG.error("dataToXML error occur2 ===============================" + dataToXml.getErrMessage());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return xmlDoc;
	}
	
	
	/**
	 * 보증 XML데이터 전송 (본사 테스트 용)
	 * 
	 * @param xmlDoc
	 * @param guarData
	 * @return
	 */
	public Map sendGuarXmlTest(String xmlDoc) {
		String recvXml = "";
		Map resultMap = Maps.newHashMap();
		try {
			
			SGIxLinker xLinker = new SGIxLinker(sgicTestModulePath + "/conf/sendinfo.properties",
					headOrgaCode, false);
			LOG.info(" sendGuarXml start --------------------------------------------------------------------");

			boolean isOK = xLinker.doSendProcess(xmlDoc, null);
			
			if (isOK) {
				LOG.info("1. Reply Document 응답 문서");
				LOG.info("--------------------------------------------------------------------");
				recvXml = xLinker.getRecvXmlData();
				LOG.info(recvXml);
				LOG.info("--------------------------------------------------------------------");

				// xml 응답서 문서 정보를 XmltoData로
				// 추출하여 상태값 DB에 저장
				// 매핑 정보 파일, XML 탬플릿 정보 파일
				XmlToData xmlToData = null;
				LOG.info("2. Reply Document 응답 문서 파싱");
				LOG.info("--------------------------------------------------------------------");
				xmlToData = new XmlToData(xLinker.getTempPath(), "RESPONSE", recvXml);
				if (xmlToData.getErrorCode() != 0) {
					if (LOG.isInfoEnabled())
						LOG.error("error : " + xmlToData.getErrorMsg());
					return null;
				}

				String resContNum = xmlToData.getData("res_cont_num");
				// String res_docu_num = xmlToData.getData("res_docu_num");
				String respTypeCode = xmlToData.getData("res_info_code");
				String respTypeName = xmlToData.getData("res_info_typename");
				String respMesgText = xmlToData.getData("res_info_result");
				
				LOG.info("3. 응답 결과");
				LOG.info("--------------------------------------------------------------------");
				LOG.info("계약번호 = " + resContNum);
				LOG.info("응답코드 = " + respTypeCode);
				LOG.info("응답코드명 = " + respTypeName);
				LOG.info("응답메시지 = " + respMesgText);
				
				resultMap.put("xml_data", xmlDoc);
				resultMap.put("ack_cont_numb", resContNum);
				resultMap.put("ack_resp_type_cd", respTypeCode);
				resultMap.put("ack_resp_type_nm", respTypeName);
				resultMap.put("ack_resp_mesg", respMesgText);
				resultMap.put("ack_resp_xml", recvXml);
				resultMap.put("errmsg", respMesgText);
				
				if(ElecGuaranteeConst.ACK_CD_FAIL.equals(respTypeCode)) {
					resultMap.put(Const.RESULT_STATUS,Const.FAIL);
				} else if(ElecGuaranteeConst.ACK_CD_SUCCESS.equals(respTypeCode)) {
					resultMap.put(Const.RESULT_STATUS,Const.SUCCESS);
				}
				return resultMap;
			} else {
				String errmsg = xLinker.getErrorCode() + ":" + xLinker.getErrorMsg();
				LOG.error(errmsg);
				resultMap.put(Const.RESULT_STATUS, Const.FAIL);
				resultMap.put(Const.RESULT_MSG, errmsg);
				return resultMap;
			}
		} catch (Exception e) {
			LOG.error("class : " + this.getClass().toString() + "sendGuarXmlTest error : " + e.toString());
			resultMap.put(Const.RESULT_STATUS, Const.FAIL);
			resultMap.put(Const.RESULT_MSG, e.toString());
		}
		return resultMap;
	}
	
	// request body에 내용이 있는지 확인하는 함수
	private boolean checkRequestBody(HttpServletRequest request) {
		boolean ckReqBody = false;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String resultMsg = "";

			while ((resultMsg = br.readLine()) != null) {
				sb.append(resultMsg);
			}
			LOG.info("***************sb to String :" + sb.toString());
			if(sb.toString() == "") {
				ckReqBody = false;
			} else {
				ckReqBody = true;
			}
		} catch(Exception e) {
			LOG.error("***************error :" + e.toString());
			LOG.error("***************error getMessage :" + e.getMessage());
			LOG.error("***************error  getCause:" + e.getCause());
			ckReqBody = false;
		}
		return ckReqBody;
	}

	public Map getGuarDetail(Map param) {
		return sgicRepository.getGuarDetail(param);
	}

	/**
	 * 보증증권 수신받은 데이터 조회
	 *
	 * @author : lee daesung
	 * @param param the param
	 * @return the map
	 * @Date : 2019. 12. 05
	 * @Method Name : getRecvBondInfo
	 */
	public Map<String,Object> getRecvBondInfo(Map<String,Object> param){

		Map<String,Object> result = sgicRepository.getGuarDetail(param);

		if(result.get("bond_penl_amnt") != null) {
			String bondPenlAmnt = result.get("bond_penl_amnt").toString();
			bondPenlAmnt = EdocStringUtil.formatNum(bondPenlAmnt);
			result.put("bond_penl_amnt", bondPenlAmnt);
		}

		if(result.get("bond_prem_amnt") != null) {
			String bondPremAmnt = result.get("bond_prem_amnt").toString();
			bondPremAmnt = EdocStringUtil.formatNum(bondPremAmnt);
			result.put("bond_prem_amnt", bondPremAmnt);
		}

		if(result.get("cont_main_amnt") != null) {
			String contMainAmnt = result.get("cont_main_amnt").toString();
			contMainAmnt = EdocStringUtil.formatNum(contMainAmnt);
			result.put("cont_main_amnt", contMainAmnt);
		}

		if(result.get("cont_paym_amnt") != null) {
			String contPaymAmnt = result.get("cont_paym_amnt").toString();
			contPaymAmnt = EdocStringUtil.formatNum(contPaymAmnt);
			result.put("cont_paym_amnt", contPaymAmnt);
		}

		if(result.get("cont_begn_date") != null && ((String)result.get("cont_begn_date")).length() == 8) {
			String contBegnDate = (String)result.get("cont_begn_date");
			contBegnDate = contBegnDate.substring(0,4) + "년 " + contBegnDate.substring(4,6) + "월 " + contBegnDate.substring(6,8) + "일";
			result.put("cont_begn_date", contBegnDate);
		}

		if(result.get("cont_fnsh_date") != null && ((String)result.get("cont_fnsh_date")).length() == 8) {
			String contBegnDate = (String)result.get("cont_fnsh_date");
			contBegnDate = contBegnDate.substring(0,4) + "년 " + contBegnDate.substring(4,6) + "월 " + contBegnDate.substring(6,8) + "일";
			result.put("cont_fnsh_date", contBegnDate);
		}

		if(result.get("cont_main_date") != null && ((String)result.get("cont_main_date")).length() == 8) {
			String contBegnDate = (String)result.get("cont_main_date");
			contBegnDate = contBegnDate.substring(0,4) + "년 " + contBegnDate.substring(4,6) + "월 " + contBegnDate.substring(6,8) + "일";
			result.put("cont_main_date", contBegnDate);
		}

		if(result.get("cont_paym_date") != null && ((String)result.get("cont_paym_date")).length() == 8) {
			String contPaymDate = (String)result.get("cont_paym_date");
			contPaymDate = contPaymDate.substring(0,4) + "년 " + contPaymDate.substring(4,6) + "월 " + contPaymDate.substring(6,8) + "일";
			result.put("cont_paym_date", contPaymDate);
		}

		if(result.get("docu_issu_date") != null && ((String)result.get("docu_issu_date")).length() == 8) {
			String docuIssuDate = (String)result.get("docu_issu_date");
			docuIssuDate = docuIssuDate.substring(0,4) + "년 " + docuIssuDate.substring(4,6) + "월 " + docuIssuDate.substring(6,8) + "일";
			result.put("docu_issu_date", docuIssuDate);
		}

		return result;
	}
}
