package smartsuite.app.util;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.ktnet.ets.hub.exception.EtsConfigurationException;
import com.ktnet.ets.hub.inf.manager.EtsHubManager;

import smartsuite.app.common.cert.pki.SignatureUtil;
import smartsuite.exception.CommonException;

@Service
public class EstampTaxUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(EstampTaxUtil.class);
	
	private String etsClientPropertiesPath;
	EtsHubManager etsHubManager = null;
	
	public EstampTaxUtil() {
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		try {
			etsClientPropertiesPath = pathMatchingResourcePatternResolver.getResource("smartsuite/properties/ets-client-config.properties").getFile().getAbsolutePath();
			try {
				LOG.info("***etsClientPropertiesPath:" + etsClientPropertiesPath);
				etsHubManager = new EtsHubManager(etsClientPropertiesPath);
				//etsHubManager = new EtsHubManager("D:\\ets-client-config.properties");
			} catch (EtsConfigurationException e) {
				LOG.error("class : " + this.getClass().toString() + "EstampTaxUtil error : " + e.toString());
				throw new CommonException(e.toString());
			}
		} catch (IOException e) {
			LOG.error("class : " + this.getClass().toString() + "EstampTaxUtil error : " + e.toString());
			throw new CommonException(e.toString());
		}
	}
	
    /**
     * 발급신청번호 요청
     */
    public Map<String, String> getNewIssueReqNo(String contractNo) throws Exception {
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("contractNo", contractNo);            //계약번호

        Map<String, String> resultMap = etsHubManager.getIssueReqNumber(paramMap); //인터페이스 받은 정보
        SignatureUtil.printMap(resultMap); //map안에 내용 log 찍기

        return resultMap;
    }
    
	/**
	 * 연계툴킷 변경발급
	 */
	public Map<String, String> stampTaxIssuePdfFile(Map<String, Object> param) throws Exception {
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("issueBizNo", param.get("bizregno").toString());
        paramMap.put("issueDivisionCode", param.get("issueDivisionCode").toString());
        paramMap.put("contractNo", param.get("cntr_no").toString());
        //paramMap.put("contractTitle", param.get("cntr_nm").toString());
		paramMap.put("contractTitle", "12313");
        paramMap.put("contractType", "030");
        paramMap.put("contractDate", param.get("cntr_dt").toString());
        paramMap.put("contractAmount", param.get("cntr_amt").toString());                
        paramMap.put("contractFilePath", param.get("eStampTaxFilePath").toString());
        paramMap.put("stampPdfPath", param.get("stampPdfPath").toString());
        
        Map<String, String> resultMap = etsHubManager.stampIssueChange(paramMap); //인터페이스 받은 정보
        SignatureUtil.printMap(resultMap); //map안에 내용 log 찍기
        
	    return resultMap;
	}
	
	/**
	 * 납부내역 확인
	 */
	public Map<String,String> getIssueCfrmPayList(String cntrNo, String cntrRev) throws Exception {
		String contractNo  = cntrNo;
		String contractNoSeq = cntrRev;
		
		Map<String,String> paramMap = Maps.newHashMap();
		paramMap.put("contractNo", contractNo);
		paramMap.put("contractNoSeq", contractNoSeq);
		
		Map<String,String> resultMap = etsHubManager.getIssueCfrmPayList(paramMap);
		SignatureUtil.printMap(resultMap);
		
		return resultMap;
	}

	/**
	 * 납부내역 확인(리팩토링)
	 */
	public Map<String,String> getIssueCfrmPayList(Map cntrInfo) throws Exception {
		String contractNo  = String.valueOf(cntrInfo.get("cntr_no"));
		String contractNoSeq = String.valueOf(cntrInfo.get("cntr_revno"));

		Map<String,String> paramMap = Maps.newHashMap();

		paramMap.put("contractNo", contractNo);
		paramMap.put("contractNoSeq", contractNoSeq);

		Map<String,String> resultMap = etsHubManager.getIssueCfrmPayList(paramMap);
		SignatureUtil.printMap(resultMap);

		return resultMap;
	}
	
	/**
	 * 구매요청서 URL 파라미터 구성
	 * */
	
	public static String getPurchaseRequestUrlParam(Map paramMap) {
		String url = "";
		
		url = "infToolMngNo=" + paramMap.get("infToolMngNo") 
				+ "&infToolBizNo=" + paramMap.get("infToolBizNo")
				+ "&issueBizNo=" + paramMap.get("issueBizNo")
				+ "&issueDivisionCode="+ paramMap.get("issueDivisionCode") 
				+ "&issueReqNo=" + paramMap.get("issueReqNo") 
				+ "&contractTitle=" + paramMap.get("contractTitle") 
				+ "&contractDate=" + paramMap.get("contractDate") 
				+ "&contractNo=" + paramMap.get("contractNo") 
				+ "&contractNoSeq=" + paramMap.get("contractNoSeq")
				+ "&contractParties=" + paramMap.get("contractParties") 
				+ "&contractAmount=" + paramMap.get("contractAmount") 
				+ "&contractType=" + paramMap.get("contractType") 
				+ "&mobileNumber=" + paramMap.get("mobileNumber") 
				+ "&email=" + paramMap.get("eml")
				+ "&issueAmount=" + paramMap.get("issueAmount");
		
		return url;
	}
	
}
