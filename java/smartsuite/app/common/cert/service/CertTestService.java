package smartsuite.app.common.cert.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.common.cert.CertConst;
import smartsuite.app.common.cert.pki.CertInfo;
import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.app.common.cert.pki.signature.SignatureProvider;
import smartsuite.app.common.cert.pki.verification.VerificationProvider;
import smartsuite.app.common.cert.validator.CertValidator;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.AttachUtils;
import smartsuite.security.core.crypto.AESIvParameterGenerator;
import smartsuite.security.core.crypto.AESSecretKeyGenerator;
import smartsuite.security.web.crypto.AESCipherKey;

/**
 * 전자계약 서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @author daesung lee
 * @see 
 * @FileName CertService.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class CertTestService {

	private static final Logger LOG = LoggerFactory.getLogger(CertTestService.class);
	
	@Value("#{cert['cert.verifiable']}")
	private boolean certVerifiable;
	@Inject
	private SignatureProvider signatureProvider;
	
	@Inject
	AESSecretKeyGenerator keyGenerator;
	@Inject
	AESIvParameterGenerator parameterGenerator;
	@Inject
	CertMgtService certMgtService;
	
	@Inject
	CertValidator certValidator;

	@Inject
	VerificationProvider verificationProvider;
	
	/**
	 * 로컬 테스트 서명하기 위한 서명 대상 가져오기 (테스트 용)
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the String
	 * @Date : 2020. 3. 11
	 * @Method Name : getTestSignSource
	 */
	public ResultMap getTestSignSource(Map<String,Object> paramMap){
		Map<String,Object> signContentInfo = Maps.newHashMap();
		Map resultData = Maps.newHashMap();
		
		String bizRegNo = (String) paramMap.get("bizRegNo");
		signContentInfo.put("hash_value", SignatureUtil.getHashValueFromStr(bizRegNo));
		signContentInfo.put("callbackUrl", "bp/edoc/contract/cert/testSignComplete.do");
		signContentInfo.put("testMethod", (String)paramMap.get("testMethod"));
		signContentInfo.put("bizregno", bizRegNo);
		
		resultData.put("signContentInfo", signContentInfo);
		resultData.put("_aesCipherKey", new AESCipherKey(keyGenerator.getKey(), keyGenerator.getPassPhrase(), keyGenerator.getIterationCount(), parameterGenerator.getIv()));
		
		return ResultMap.SUCCESS(resultData);
	}
	/**
	 * 로컬 멀티 서명하기 위한 서명 대상 가져오기 (테스트 용)
	 * 
	 * @author : daesung lee
	 * @param : String
	 * @return the String
	 * @Date : 2020. 3. 11
	 * @Method Name : getTestMultiSignSource
	 */
	public ResultMap getTestMultiSignSource(Map<String,Object> paramMap){
		Map<String,Object> signContentInfo = Maps.newHashMap();
		Map<String,Object> resultData = Maps.newHashMap();
		String bizRegNo = (String)paramMap.get("bizRegNo");
		
		signContentInfo.put("hash_value", "test111");
		signContentInfo.put("callbackUrl", "bp/edoc/contract/cert/testSignComplete.do");
		signContentInfo.put("testMethod", (String)paramMap.get("testMethod"));
		signContentInfo.put("bizregno", bizRegNo);
		signContentInfo.put("cntr_no","CNTR20200103");
		signContentInfo.put("cntr_revno","1");
		signContentInfo.put("sign_source","contract1");
		signContentInfo.put("doc_type","TX");
		signContentInfo.put("sign_target","0");
		
		List<Map<String,Object>> signAppTextInfo = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> signAppFileInfo = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> appTextInfo1 = Maps.newHashMap();
		Map<String,Object> appTextInfo2 = Maps.newHashMap();
		
		appTextInfo1.put("hash_value", "apptext1");
		appTextInfo1.put("sign_target", "1");
		appTextInfo1.put("doc_type", "TX");
		
		appTextInfo2.put("hash_value", "apptext2");
		appTextInfo2.put("sign_target", "2");
		appTextInfo2.put("doc_type", "TX");
		
		
		Map<String,Object> appFileInfo1 = Maps.newHashMap();
		Map<String,Object> appFileInfo2 = Maps.newHashMap();
		
		appFileInfo1.put("hash_value", "appfile1");
		appFileInfo1.put("sign_target", "3");
		appFileInfo1.put("doc_type", "FL");
		
		appFileInfo2.put("hash_value", "appfile2");
		appFileInfo2.put("sign_target", "4");
		appFileInfo2.put("doc_type", "FL");
		
		signAppTextInfo.add(appTextInfo1);
		signAppTextInfo.add(appTextInfo2);
		
		signAppFileInfo.add(appFileInfo1);
		signAppFileInfo.add(appFileInfo2);
		
		resultData.put("signContentInfo", signContentInfo);
		resultData.put("signAppTextInfo", signAppTextInfo);
		resultData.put("signAppFileInfo", signAppFileInfo);
		resultData.put("_aesCipherKey", new AESCipherKey(keyGenerator.getKey(), keyGenerator.getPassPhrase(), keyGenerator.getIterationCount(), parameterGenerator.getIv()));
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 테스트 멀티 서명 완료
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the String
	 * @Date : 2020. 3. 11
	 * @Method Name : testMultiSignComplete
	 */
	public ResultMap testMultiSignComplete(Map<String,Object> cntrInfo, List<Map<String,Object>> signList){
		for(Map<String, Object> row : signList) {
			row.put("ssn", cntrInfo.get("ssn"));
			row.put("rvalue", cntrInfo.get("rvalue"));
			verifySignValue(row); //서명검증,신원확인,인증서검증
			LOG.info("decoding value :"+ verificationProvider.getSource((String)row.get("sign_value")));
			LOG.info("sign_target :"+ row.get("sign_target"));
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 로컬 인증서 서명 완료 (테스트)
	 * 
	 * @author : daesung lee
	 * @param : the String
	 * @return the String
	 * @Date : 2020. 3. 11
	 * @Method Name : testSignComplete
	 */
	public ResultMap testSignComplete(Map<String,Object> cntrInfo){
		return verifySignValue(cntrInfo); //서명검증,신원확인,인증서검증
	}
	
	/**
	 * 멀티 세금계산서 서명대상 조회
	 * 
	 * @author : daesung lee
	 * @param param the map
	 * @return the map
	 * @Date : 2021. 7. 20
	 * @Method Name : getTestTaxMultiSignSource
	 */
	public Map<String,Object> getTestTaxMultiSignSource(Map<String,Object> paramMap){
		Map<String,Object> signContentInfo = Maps.newHashMap();
		Map<String,Object> result = Maps.newHashMap();
		
		List<Map<String,Object>> taxXmlDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> taxXmlDataInfo = Maps.newHashMap();
		taxXmlDataInfo.put("tax_xml_data", (String)paramMap.get("tax_xml_data0"));
		taxXmlDataInfo.put("tax_xml_data_code", "test_tax_001");
		taxXmlDataInfo.put("tax_xml_data_type", "01");
		
		Map<String,Object> taxXmlDataInfo2 = Maps.newHashMap();
		taxXmlDataInfo2.put("tax_xml_data", (String)paramMap.get("tax_xml_data0"));
		taxXmlDataInfo2.put("tax_xml_data_code", "test_tax_002");
		taxXmlDataInfo2.put("tax_xml_data_type", "02");
		
		
		Map<String,Object> taxXmlDataInfo3 = Maps.newHashMap();
		taxXmlDataInfo3.put("tax_xml_data", (String)paramMap.get("tax_xml_data0"));
		taxXmlDataInfo3.put("tax_xml_data_code", "test_tax_003");
		taxXmlDataInfo3.put("tax_xml_data_type", "03");
		
		taxXmlDataList.add(taxXmlDataInfo);
		taxXmlDataList.add(taxXmlDataInfo2);
		taxXmlDataList.add(taxXmlDataInfo3);
		
		signContentInfo.put("tax_xml_data_list", taxXmlDataList);
		signContentInfo.put("callbackUrl", "bp/edoc/contract/cert/testTaxMultiSignComplete.do");
		signContentInfo.put("testMethod", (String)paramMap.get("testMethod"));
		//signContentInfo.put("bizregno", "1111111119");	//테스트 사업자번호
		signContentInfo.put("bizregno", "1234567890");	//테스트 사업자번호
		
		CertInfo certInfo = certMgtService.getCertInfo((String)paramMap.get("logic_org_cd"));	//서버인증서 정보 조회 운영조직 코드 예시 : POH139
		signContentInfo.put("logic_org_cd", (String)paramMap.get("logic_org_cd") );
		String kmCert = verificationProvider.getCertPem(certInfo);	//암호화용 서버인증서 조회
		signContentInfo.put("kmCert", kmCert);
		result.put("signContentInfo", signContentInfo);
				
		return result;
	}
	
	/**
	 * 멀티 세금계산서 전자서명 완료(테스트)
	 * 
	 * @author : daesung lee
	 * @param : the map
	 * @return the map
	 * @Date : 2021. 7. 21
	 * @Method Name : testTaxMultiSignComplete
	 */
	public Map<String,Object> testTaxMultiSignComplete(Map<String,Object> cntrInfo){
		Map<String,Object> result = Maps.newHashMap();
		String signedXml = "";
		String[] signedXmlList = null;
		String[] taxXmlDataTypeList = null;
		String[] taxXmlDataCodeList = null;
		String[] taxXmlDataList = null;
		 
		if(String.class == cntrInfo.get("signed_xml").getClass()) { //단일 건인 경우 
			signedXmlList = new String[]{(String)cntrInfo.get("signed_xml")};	//서명한 xml 데이터
			taxXmlDataTypeList = new String[]{(String)cntrInfo.get("tax_xml_data_type")};
			taxXmlDataCodeList = new String[]{(String)cntrInfo.get("tax_xml_data_code")};
		 	taxXmlDataList = new String[]{(String)cntrInfo.get("tax_xml_data")};
		}else {	//배열인 경우
			signedXmlList = (String[])cntrInfo.get("signed_xml");	//서명한 xml 데이터
			taxXmlDataTypeList = (String[])cntrInfo.get("tax_xml_data_type");
			taxXmlDataCodeList = (String[])cntrInfo.get("tax_xml_data_code");
			taxXmlDataList = (String[])cntrInfo.get("tax_xml_data");
		}
		
		String rvalue = (String)cntrInfo.get("rvalue");	//신원확인 하기 위한 값
		String ssn = (String)cntrInfo.get("ssn");	//신원확인 값 - 사업자번호
		String logicOrgCd = (String)cntrInfo.get("logic_org_cd");
		
		CertInfo certInfo = certMgtService.getCertInfo(logicOrgCd);

		File kmCertFile = new File(certInfo.getKmCertFilePath());
		File kmPriFile = new File(certInfo.getKmPriFilePath());
		 
		String kmCertFilePath = kmCertFile.getParent() + File.separator + CertConst.KM_CERT_DER;
		String kmPriFilePath = kmPriFile.getParent() + File.separator + CertConst.KM_PRI_KEY;
		 
		//파일 확장자 붙여서 파일 복사하기
		AttachUtils.copyFile(kmCertFile, new File(kmCertFilePath));
		AttachUtils.copyFile(kmPriFile, new File(kmPriFilePath));
		
		String certPw = certInfo.getCertPw();
		 
		LOG.info("signed_xml : " + signedXml);
		 
		for(int i = 0; i < signedXmlList.length; i++) {
			LOG.info("signedXml :" + signedXmlList[i]);
			LOG.info("taxXmlDataType :" + taxXmlDataTypeList[i]);
			LOG.info("taxXmlDataCode :" + taxXmlDataCodeList[i]);
			LOG.info("taxXmlData :" + taxXmlDataList[i]);
			signedXml = signedXmlList[i];
			verificationProvider.verifySignXml(signedXml,  kmCertFilePath, kmPriFilePath, certPw, rvalue, ssn); 
		}
		String xml = taxXmlDataList[0];
		byte[] signCertFile = certInfo.getSignCert();
		byte[] signPriFile = certInfo.getSignPri();
		//서버인증서로 XML 서명 값 추출
		String serverCertSignXml = signatureProvider.getXmlSignValue(xml, signCertFile, signPriFile, certPw);
		String serverCertRValue = signatureProvider.getRvalue(certInfo);
		 
		verificationProvider.verifySignXml(serverCertSignXml); 
		LOG.info("serverCertSignXml : " + serverCertSignXml); LOG.info("serverCertRValue : " + serverCertRValue); 
		result.put(Const.RESULT_STATUS,Const.SUCCESS); 
		return result; 
	}
	
	/**
	 * 인증서 검증
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the list
	 * @Date : 2019. 3.07
	 * @Method Name : verifySignValue
	 */
	public ResultMap verifySignValue(Map<String,Object> param){
		
		ResultMap resultMap = certValidator.verifySignValue(param);
		
		return resultMap;
	}
	
}
	

