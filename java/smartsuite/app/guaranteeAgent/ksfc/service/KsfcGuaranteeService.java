package smartsuite.app.guaranteeAgent.ksfc.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.guaranteeAgent.ksfc.repository.KsfcRepository;
import smartsuite.app.guaranteeAgent.sgic.service.SgicGuaranteeService;
import smartsuite.app.shared.ElecGuaranteeConst;
import smartsuite.app.util.ElecGuaranteeUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@SuppressWarnings({ "unchecked" })
public class KsfcGuaranteeService{
	private static final Logger LOG = LoggerFactory.getLogger(SgicGuaranteeService.class);

	@Value("#{guar['ksfc.sender.id']}")
	private String ksfcSenderId;
	
	@Value("#{guar['ksfc.id']}")
	private String ksfcId;
	
	@Value("#{guar['func.code']}")
	private String funcCode;
	
	@Value("#{guar['ksfc.url']}")
	private String ksfcUrl;
	
	@Value("#{globalProperties['server.cls']}")
	private String serverCls;
	
	@Inject
	KsfcRepository ksfcRepository;
	
	/**
	 * 업체 보증보험 신청
	 * 
	 * @param param
	 * @return the map
	 */
	public ResultMap sendGuarInfo(Map param) {
		Map resultMap = Maps.newHashMap();

		String fileEncoding = System.getProperty("file.encoding");
		LOG.info("==============================file.encoding = " + fileEncoding);
		
		Map bondInfo = ksfcRepository.getBondInfo(param);
		
		if(bondInfo == null) {
			return ResultMap.FAIL();
		}
		
		LOG.info("\n\n++++++++++bondInfo++++++++++++\n");
		LOG.info(bondInfo == null ? "empty" : bondInfo.toString());
		LOG.info("\n++++++++++bondInfo end++++++++++++\n\n");
		
		resultMap = ElecGuaranteeUtil.setBondDataToIFNoticDevinition(bondInfo); // 통보서에 맞게 셋팅
		
		if(Const.FAIL.equals(resultMap.get(Const.RESULT_STATUS))) {
			LOG.info("result msg : " + resultMap.get(Const.RESULT_MSG));
			return ResultMap.FAIL();
		}
		
		// 통보서 구성
		resultMap = this.composeNoticeData(bondInfo);
		
		// 소프트공제조합 설치된 모듈로 전송
		resultMap = this.interfaceTiger(resultMap);
		
		if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
		}
		
		try {
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				bondInfo.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_REQUEST);
				bondInfo.put("gur_insco_ccd", ElecGuaranteeConst.KSFC); //소프트공제조합
				ksfcRepository.saveInsuranceForm(bondInfo); // 보증 신청정보 저장
				ksfcRepository.updateCtgrSts(bondInfo);
				return ResultMap.SUCCESS();
			} else {
				LOG.info("result msg : " + resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
        } catch(Exception e) {
        	LOG.error("class : " + this.getClass().toString() + "sendGuarInfo error : " + e.toString());
        	return ResultMap.FAIL();
        }
	}
	
	/**
	 * SW공제조합 보증보험 매핑
	 * 
	 * @param param 
	 * @return the map
	 */
	public Map composeNoticeData(Map bondInfo) {
	    LOG.info("\n+++++++++getCertDB++++++++++\n");
	    
	    Map noticeData = Maps.newHashMap();
	    String contNumbText = (String)bondInfo.get("cont_numb_text");

		SimpleDateFormat simpledateformat1 = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String yyyymmdd = simpledateformat1.format(new Date());
		SimpleDateFormat simpledateformat2 = new SimpleDateFormat("HHmmss", Locale.KOREA);
		String hhmmss = simpledateformat2.format(new Date());

        String kindCode = (String) bondInfo.get("bond_kind_code");
		String headMesgType = (String) bondInfo.get("head_mesg_type");
		String headMesgName = (String) bondInfo.get("head_mesg_name");
		
		String headMesgSend = ksfcSenderId;	 // 송신자 id
		String headMesgRecv = ksfcId;        // sw공제조합
		String headFuncCode = funcCode;      // 문서기능(9:원본,1:취소,53:테스트)
		
		/*
		if(!contract_no.equals(ori_contract_no)) {
			if("Y".equals(entity.get("is_amt")) && "Y".equals(entity.get("is_dat"))){
				bond_appl_code	= "62";//62:연장증액
				conindicode		= "2"; //2:연장
			}else if("Y".equals(entity.get("is_amt")) && "N".equals(entity.get("is_dat"))){
				bond_appl_code	= "60";//60:증액
				conindicode		= "3"; //3:증액
			}else if("N".equals(entity.get("is_amt")) && "Y".equals(entity.get("is_dat"))){
				bond_appl_code	= "20";//20:연장
				conindicode		= "2"; //2:연장
			}else{
				bond_appl_code	= "90";//90:기타(그 외)
				conindicode		= "5"; //5:기타
			}
			cont_news_divs	= "2";	   // 갱신
		}*/
        
		/* HEADER */
		noticeData.put("head_mesg_recv_cont", 1);
		noticeData.put("head_mesg_send", headMesgSend);
		noticeData.put("head_mesg_recv", headMesgRecv);
		noticeData.put("head_mesg_recv1", headMesgRecv);
		noticeData.put("head_func_code", headFuncCode);
		noticeData.put("head_mesg_type", headMesgType);
		noticeData.put("head_mesg_name", headMesgName);
		noticeData.put("head_mesg_vers", "1.0");
		// 문서번호 : 004+상품구분코드(3)+피보험자 측 계약번호(차수 포함)+' '+00
		noticeData.put("head_docu_numb", "004" + kindCode + contNumbText + " " + "00");
		// 문서관리번호 : 송신기관코드(13)+'.'+SYSDATE(8)+SYSTIME(6)+'.12345.'+004+상품구분코드(3)+피보험자 측 계약번호(차수 포함)+' '+00
		noticeData.put("head_mang_numb", headMesgSend + yyyymmdd + hhmmss + ".12345." + "004" + kindCode + contNumbText + " " + "00");
		// 참조번호 : 004+상품구분코드(3)+피보험자 측 계약번호(차수 포함)+' '+00
		noticeData.put("head_refr_numb", "004" + kindCode + contNumbText + " " + "00");
		noticeData.put("head_titl_name", headMesgName);
		noticeData.put("head_docu_func", headMesgType);
		noticeData.put("head_docu_issu", yyyymmdd + hhmmss);
		
		/* 계약정보 */
		noticeData.put("cont_unin_numb", bondInfo.get("cont_numb_text"));
		noticeData.put("cont_rfrn_numb", bondInfo.get("cont_numb_text"));
		noticeData.put("cont_numb_idnr", bondInfo.get("cont_numb_text"));
		noticeData.put("cont_seqn_numb", 0);
		noticeData.put("cont_ibid_indr", "N");
		noticeData.put("cont_name_text", bondInfo.get("cont_name_text"));
		noticeData.put("cont_mean_code", 1);
		noticeData.put("cont_paym_code", "");
		noticeData.put("cont_clas_code", 1);
		noticeData.put("cont_dcls_code", bondInfo.get("cntr_type"));
		noticeData.put("cont_acls_code", "");
		noticeData.put("cont_ctyp_indr", "");
		noticeData.put("cont_rnum_idnr", bondInfo.get("cont_numb_text"));
		noticeData.put("cont_cprn_indr", "N");
		noticeData.put("cont_date_time", bondInfo.get("cntr_date"));
		noticeData.put("cont_strt_date", bondInfo.get("cntr_start_date"));
		noticeData.put("cont_ends_date", bondInfo.get("cntr_end_date"));
		noticeData.put("cont_perd_text", "");
		noticeData.put("cont_cmpt_date", bondInfo.get("cntr_end_date"));
		noticeData.put("cont_blaw_code", "");
		noticeData.put("cont_lcls_code", "");
		noticeData.put("cont_bcla_text", "");
		noticeData.put("cont_bdtl_text", "");
		noticeData.put("cont_acls_code", "");
		noticeData.put("cont_gnot_text", "");
		noticeData.put("cont_amnt_cont", bondInfo.get("cntr_amt"));
		noticeData.put("cont_this_amnt", bondInfo.get("cntr_amt"));
		noticeData.put("cont_amnt_rate", bondInfo.get("grnt_rate"));
		
		if(ElecGuaranteeConst.FLR.equals(kindCode)) {
			noticeData.put("cont_pric_rate", bondInfo.get("grnt_rate")); // 하자보증금율
			noticeData.put("cont_mrtg_perd", bondInfo.get("grnt_term")); // 하자보수책임기간
		} else {
			noticeData.put("cont_pric_rate", "");
			noticeData.put("cont_mrtg_perd", "");
		}
		noticeData.put("cont_ntfy_info", "");
		noticeData.put("cont_reqt_idnr", "");
		noticeData.put("cont_curl_link", "");
		
		/* 채권자정보 */
		noticeData.put("coor_item_cont" , 1);
		noticeData.put("coor_line_numb1", 1);
		noticeData.put("coor_orgn_cccd1", "");
		noticeData.put("coor_orgn_idnr1", "");
		noticeData.put("coor_orgn_addi1", bondInfo.get("cred_orps_iden"));
		noticeData.put("coor_orgn_addd1", "");
		noticeData.put("coor_orgn_name1", bondInfo.get("cred_orga_name"));
		noticeData.put("coor_orgn_gccd1", "");
		noticeData.put("coor_empl_cred1", bondInfo.get("cred_ownr_name"));
		noticeData.put("coor_empl_dept1", bondInfo.get("cred_dept_name"));
		noticeData.put("coor_empl_idnr1", "");
		noticeData.put("coor_empl_idnn1", "");
		noticeData.put("coor_empl_name1", bondInfo.get("cred_chrg_name"));
		noticeData.put("coor_tele_numb1", bondInfo.get("cred_phon_numb"));
		noticeData.put("coor_faxs_numb1", "");
		
		/* 계약자정보 */
        noticeData.put("suor_item_cont" , 1);
		noticeData.put("suor_line_numb1", 1);
		noticeData.put("suor_orgn_cccd1", "");
		noticeData.put("suor_orgn_ctcd1", "");
		noticeData.put("suor_orgn_idnr1", bondInfo.get("appl_orps_iden"));
		noticeData.put("suor_orgn_addi1", bondInfo.get("appl_orps_iden"));
		noticeData.put("suor_orgn_addd1", "");
		noticeData.put("suor_orgn_name1", bondInfo.get("appl_orga_name"));
		noticeData.put("suor_orgn_cred1", bondInfo.get("appl_ownr_name"));
		noticeData.put("suor_empl_dept1", bondInfo.get("appl_dept_name"));
		noticeData.put("suor_empl_idnr1", "");
		noticeData.put("suor_empl_idnn1", "");
		noticeData.put("suor_empl_name1", bondInfo.get("appl_chrg_name"));
		noticeData.put("suor_tele_numb1", bondInfo.get("appl_offc_phon"));
		noticeData.put("suor_faxs_numb1", "");
		noticeData.put("suor_ctyp_code1", "");
		noticeData.put("suor_cons_rate1", "");
		
		/* 기타 */
        noticeData.put("svce_line_numb", "");
        noticeData.put("svce_repr_indr", "");
        noticeData.put("svce_clas_code", "");
        noticeData.put("svce_cons_tycd", "");
        noticeData.put("svce_cons_locd", "");
        noticeData.put("svce_amnt_cont", "");
        noticeData.put("svce_rela_dtls", "");
        noticeData.put("svce_genl_note", "");
        
		/* 기타 */
		/*noticeData.put("head_orga_code", head_orga_code);
        noticeData.put("bond_kind_code", kind_code);
        noticeData.put("bond_curc_code", waers);
        noticeData.put("bond_appl_code", bond_appl_code);
        noticeData.put("cont_type_iden", "2");      // 계약방식(1:공동, 2:단독, 3:분담)
        noticeData.put("cont_news_divs", cont_news_divs);
        noticeData.put("cont_curc_code", waers);
        noticeData.put("cont_asgn_rate", "100");    // 지분율
        noticeData.put("cred_user_iden", head_mesg_send);
        noticeData.put("cred_user_type", head_orga_code);
        noticeData.put("appl_user_iden", head_mesg_send);
        noticeData.put("appl_user_type", head_orga_code);
        noticeData.put("cont_unit_divs", "2");      //단가계약 여부(1:단가계약, 2:일반계약)
        noticeData.put("prep_curc_code", waers);*/
        
		if(bondInfo.get("hist_bond_numb") != null && bondInfo.get("cont_numb_text") != null) {
			noticeData.put("docu_numb_text", EdocStringUtil.replace((String) bondInfo.get("hist_bond_numb")," ","") + " " + this.stringCutAfter((String) bondInfo.get("cont_numb_text"), "-"));
		}
		
		//noticeData.put("docu_issu_date", YYYYMMDD);
		//noticeData.put("docu_user_type", head_orga_code);
		//noticeData.put("cont_main_name", entity.get("cont_name_text"));
		//noticeData.put("conindicode", conindicode);

		LOG.info("\n++++++++++entity++++++++++++\n");
        LOG.info(noticeData == null ? "empty" : noticeData.toString());
        LOG.info("\n++++++++++entity end++++++++++++\n");

		return noticeData;
	}
	
	/** 문자열을 받아서 gubun 뒤의 문자만 return **/
    public String stringCutAfter(String data, String gubun){
        if(data == null) {
        	return "";
        }

        if(data.indexOf(gubun) > 0) {
        	return data.substring(data.indexOf(gubun)+1);
        } else {
        	return data;
        }
    }
    
    /**
	 * SW공제조합 보증보험 연계
	 * 
	 * @param param 
	 * @return the map
	 */
	public Map interfaceTiger(Map param) {
	    Map resultMap = Maps.newHashMap();
	    resultMap.put(Const.RESULT_STATUS, Const.SUCCESS);

	    InputStreamReader isr = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        
		try {
		    LOG.info("\n+++++++++interfaceTiger++++++++++\n");
		    
			String urlParam = this.mapToUrlParam(param);
			
			URL url = new URL(ksfcUrl + "/mps/sendUnicon.jsp");
			URLConnection urlConn = url.openConnection();
			HttpURLConnection httpUrlConn = (HttpURLConnection)urlConn;
			
			httpUrlConn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpUrlConn.setRequestMethod("POST");
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setDefaultUseCaches(false);
			// request
			// osw = new OutputStreamWriter(httpUrlConn.getOutputStream(), "UTF-8"); //UTF-8로 설정시 sendUnicon.jsp 에 value값을 UTF-8처리 함수 수행해야함.( 함수명 : asc2utf )
			osw = new OutputStreamWriter(httpUrlConn.getOutputStream(), "EUC-KR"); //EUC-KR로 설정시 sendUnicon.jsp 에 value값을 EUC-KR처리 함수 수행해야함.( 함수명 : asc2ksc )
			osw.write(urlParam);
			osw.flush();
			
			// response
			isr = new InputStreamReader(httpUrlConn.getInputStream());
			br = new BufferedReader(isr);
            String respTypeCode = "";
            String respMesgText = "";
			String line = "";
			int i = 0;
			
			while((line = br.readLine())!= null) {
			    LOG.info("+++++" + line);
				if((i = line.indexOf("resp_type_code`=")) > -1) {
				    String ackResponse = line.substring(i);
				    ackResponse = ackResponse.substring(0, ackResponse.indexOf("&"));
					
					LOG.info("\n+++++ackResponse : " + ackResponse);
					
					if(ackResponse.indexOf(ElecGuaranteeConst.ACK_CD_SUCCESS) > -1) {
					    respTypeCode = ElecGuaranteeConst.ACK_CD_SUCCESS;
					} else if(ackResponse.indexOf(ElecGuaranteeConst.ACK_CD_FAIL) > -1) {
					    respTypeCode = ElecGuaranteeConst.ACK_CD_FAIL;
					    respMesgText = line.substring(line.indexOf("resp_mesg_text") + "resp_mesg_text".length() + 1);
					}
					break;
				}
			} 
			
			if(respTypeCode.isEmpty()) {
			    respTypeCode = "EMPTY";
                respMesgText = "보증통보서 발송중 오류가 발생하였습니다.";
                LOG.error("error : 응답이 없습니다.");
                resultMap.put(Const.RESULT_STATUS, Const.FAIL);
                resultMap.put(Const.RESULT_MSG, "error : 응답이 없습니다.");
                return resultMap;
			}
			
            if(LOG.isInfoEnabled()) {
                LOG.info("응답코드 = " + respTypeCode);
                LOG.info("응답메시지 = " + respMesgText);
            }

//            guarData.put("xml_data", xmlDoc);
//            guarData.put("ack_cont_numb", res_cont_num);
//            guarData.put("ack_resp_type_nm", respTypeName);
//            guarData.put("ack_resp_xml", recvXml);
            
            resultMap.put("ack_resp_type_cd", respTypeCode);
            resultMap.put("ack_resp_mesg", respMesgText);
            resultMap.put("errmsg", respMesgText);
            
		} catch (Exception e) {
            LOG.error("error:" + e.getMessage(), e);
            resultMap.put(Const.RESULT_STATUS, Const.FAIL);
            resultMap.put(Const.RESULT_MSG, e.toString());
            return resultMap;
            
        } finally {
            if(br != null) {
            	try {
            		br.close();
            	} catch(IOException e) {
            		LOG.error(e.getMessage(), e);
            	}
            }
            if(isr != null) {
            	try { 
            		isr.close();
            	} catch(IOException e) {
            		LOG.error(e.getMessage(), e);
            	}
            }
            if(osw != null) {
            	try {
            		osw.close();
            	} catch(IOException e) {
            		LOG.error(e.getMessage(), e);
            	}
            }
        }
		return resultMap;
	}
	
	/**
	 * Map param -> url param(소프트웨어공제조합 전송시)
	 * @param param
	 * @return
	 */
	public String mapToUrlParam(Map param) {
		
		String urlParam = "";
		Iterator<String> keys = param.keySet().iterator();

		while(keys.hasNext()) {
			String key = keys.next();
			String value = String.valueOf(param.get(key));
			
			if(value == null || "null".equals(value)){
				value = "";
			}
			urlParam += "&" + key + "=" + value;
		}
		urlParam = urlParam.substring(1);
		
		return urlParam;
	}
	
	public ResultMap cancelGuar(Map param) {
		Map guarData = ksfcRepository.getBondInfo(param);
		
		if(guarData == null) {
			return ResultMap.FAIL();
		}
		
		guarData.put("head_docu_func", "DD"); // 취소 시 해당 값만  DD 변경하여 요청
		guarData = ElecGuaranteeUtil.setBondDataToIFNoticDevinition(guarData); // 통보서에 맞게 셋팅
		
		//통보서 구성
		Map resultMap = this.composeNoticeData(guarData);
		
		// 소프트공제조합 설치된 모듈로 전송
		resultMap = this.interfaceTiger(resultMap);
		
		if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
		}
		
		try {
			if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {
				guarData.putAll(resultMap);
				guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_READY); //발행대기
				
				ksfcRepository.saveFinalResponse(guarData); // 취소 응답서 저장
				ksfcRepository.deleteNofndoc(guarData); // 통보서 삭제
				ksfcRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
				ksfcRepository.deleteEgurdoc(guarData); // 받은 보증서 삭제
				
				return ResultMap.SUCCESS();
			} else {
				LOG.error("error:" +resultMap.get(Const.RESULT_MSG));
				return ResultMap.FAIL();
			}
        } catch(Exception e) {
			LOG.error(e.getMessage(), e);
			return ResultMap.FAIL();
        }
	}

	public ResultMap approveGuar(Map param) {
		Map resultMap = Maps.newHashMap();
		String headFuncCode = funcCode;
		
		param.put("head_func_code", headFuncCode);
		Map guarData = ksfcRepository.approveEgur(param);

		if(guarData == null) {
			return ResultMap.FAIL();
		}

		// 최종응답서생성
		guarData.put("resp_type_code", "AP");
		guarData.put("resp_type_name", "접수");
		guarData.put("resp_mesg_text", "접수");
		
		resultMap = this.interfaceTiger(guarData);
		
		if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
		}
		
		if(Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) { // 시스템수용
			guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_APPROVE);
			
			ksfcRepository.saveFinalResponse(guarData); // 최종응답서 저장
			ksfcRepository.updateCtgrSts(guarData); // 보증 진행상태 변경

			return ResultMap.SUCCESS();
		} else {
			LOG.error("error:" +resultMap.get(Const.RESULT_MSG));
			return ResultMap.FAIL();
		}
	}

	public ResultMap rejectGuar(Map param) {
		Map resultMap = Maps.newHashMap();

		String headFuncCode = funcCode;
		param.put("head_func_code", headFuncCode);
		
		Map guarData = ksfcRepository.rejectEgur(param);
		
		if(guarData == null) {
			return ResultMap.FAIL();
		}
		
		//최종응답서생성
		guarData.put("resp_type_code", "RE");
		guarData.put("resp_type_name", "반려");
		guarData.put("resp_mesg_text", "반려");
		
		resultMap = this.interfaceTiger(guarData);
		
		if("LOCAL".equals(serverCls)) { // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
		}
		
		if (Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) {  // 시스템수용
			guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_REJECT);
			
			ksfcRepository.saveFinalResponse(guarData); // 최종응답서 저장
			ksfcRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
			
			return ResultMap.SUCCESS();
		} else {
			LOG.error("error:" +resultMap.get(Const.RESULT_MSG));
			return ResultMap.FAIL();
		}
	}

	public ResultMap destroyGuar(Map param) {
		Map resultMap = Maps.newHashMap();
		String headFuncCode = funcCode;
		
		param.put("head_func_code", headFuncCode);
		Map guarData = ksfcRepository.destroyGuar(param); // 보증보험 파기
		
		if(guarData == null) {
			return ResultMap.FAIL();
		}
		
		// 최종응답서생성
		guarData.put("resp_type_code", "DE");
		guarData.put("resp_type_name", "파기");
		guarData.put("resp_mesg_text", "파기");
		
		resultMap = this.interfaceTiger(guarData);
		
		if("LOCAL".equals(serverCls)){ // guarantee-if.properties 에 LOCAL 테스트 인 경우만 
			resultMap.put(Const.RESULT_STATUS, Const.SUCCESS); // 테스트 용도로 임시 강제 셋팅
		}
		
		if (Const.SUCCESS.equals(resultMap.get(Const.RESULT_STATUS))) { // 시스템수용
			guarData.put("gur_sts_ccd", ElecGuaranteeConst.GUAR_DESTROY);
			
			ksfcRepository.saveFinalResponse(guarData); // 최종응답서 저장
			ksfcRepository.updateCtgrSts(guarData); // 보증 진행상태 변경
			
			return ResultMap.SUCCESS();
		} else {
			LOG.error("error:" +resultMap.get(Const.RESULT_MSG));
			return ResultMap.FAIL();
		}
	}

	public Map receiveGuarantee(HttpServletRequest req, HttpServletResponse res) {
		Map param = Maps.newHashMap();
		Map result = Maps.newHashMap();
		
		Enumeration<String> enmrt = req.getParameterNames();
		String name  = "";
		String value = "";
		String recvDocCode = "";
		while(enmrt.hasMoreElements()) {
			name = (String)enmrt.nextElement();
			value = req.getParameter(name);
			
			// 수신한 보증서 정보 (NameValue 구조)
			if("inNameValue".equals(name)) {
				String[] strArr = value.split("`&"); // `& 로 항목 구분
				String str = "";
				
				for(int i=0; i<strArr.length; i++) {
					str = (String)strArr[i];
					
					// Name`=Value 구분
					name  = str.substring(0, str.indexOf("`="));
					value = str.substring(str.indexOf("`=") + 2);
					LOG.info("name : " + name + "  , value : " + value);
					param.put(name, value);
					
					if("head_mesg_type".equals(name)) {
						recvDocCode = value;
					}
				}
			}
		}
		
		if(recvDocCode.equals("CONGUA")) {
			param.put("bond_kind_code", "002");
		} else if(recvDocCode.equals("FLRGUA")) {
			param.put("bond_kind_code", "003");
		} else if(recvDocCode.equals("PREGUA")) {
			param.put("bond_kind_code", "004");
		} else if(recvDocCode.equals("RCONACK")) {
			param.put("bond_kind_code", "002");
		} else if(recvDocCode.equals("RLFRACK")) {
			param.put("bond_kind_code", "003");
		} else if(recvDocCode.equals("RPREACK")) {
			param.put("bond_kind_code", "004");
		}
			
		// 보증보험 신청/결과 비교
		if("CONGUA".equals(recvDocCode) || "FLRGUA".equals(recvDocCode) || "PREGUA".equals(recvDocCode)) {
		
			// 통보서를 보낸적이 있는지 확인
			String mesgType = "";
			
			if(recvDocCode.equals("CONGUA") || recvDocCode.equals("CONINF")) {
				mesgType = ElecGuaranteeConst.PREINF;
			} else if(recvDocCode.equals("PREGUA") || recvDocCode.equals("PREINF")) {
				mesgType = ElecGuaranteeConst.PREINF;
			} else if (recvDocCode.equals("FLRGUA") || recvDocCode.equals("FLRINF")) {
				mesgType = ElecGuaranteeConst.FLRINF;
			}
	
			param.put("mesg_type", mesgType);
			
			int cnt = ksfcRepository.checkGuarNofndoc(param);
			
			if(cnt == 0) { // 통보서를 보낸적이 없음
				LOG.error("보증 통보서가 존재하지 않습니다."); // ESEGRIF에 데이터가 없음. 협력사에서 온라인보증요청 할 때 생성되는 데이터임
				
				result.put("responseResult", ElecGuaranteeConst.ACK_CD_FAIL);
				result.put("responseName", "시스템거부");
				result.put("responseMessage", "보증 통보서가 존재하지 않습니다.");
				return result;
			}
			
			// 수신받은 보증서가 있는지 확인
			cnt = ksfcRepository.checkCntEgur(param);
			
			if(cnt > 0) { // 이미 보증서가 존재함  - 현재상태가 거부인 경우만 보증서를 재 수신 받을 수 있음 (gur_sts_ccd : Z 거부임 거부할때는 esegrhd 데이터 삭제함)
				LOG.error("동일한 계약번호의 보증서가 이미 발급되었습니다."); // ESEGRIF에 데이터가 없음. 협력사에서 온라인보증요청 할 때 생성되는 데이터임
				
				result.put("responseResult", ElecGuaranteeConst.ACK_CD_FAIL);
				result.put("responseName", "시스템거부");
				result.put("responseMessage", "동일한 계약번호의 보증서가 이미 발급되었습니다.");
				return result;
			}
			
			this.recvAfterProcess(param);
			result.put("responseResult", ElecGuaranteeConst.ACK_CD_SUCCESS);
			result.put("responseName", "시스템수용");
			result.put("responseMessage", "정보 수신업무가 정상적으로 수행되었습니다.");
			return result;
		} else {
			// 소프트공제조합은 실제 연계까 부족하여 프로젝트에서 개발이 필요함(어떤값이 올 수 있는지 확인해보아야함)
			LOG.info("recvDocCode : " + recvDocCode);
			result.put("responseResult", ElecGuaranteeConst.ACK_CD_FAIL);
			result.put("responseName", "시스템거부");
			result.put("responseMessage", "recvDocCode : " + recvDocCode);
			return result;
		}
	}
	
	public void recvAfterProcess(Map recvData) {
		//기존 보증서 삭제
		ksfcRepository.deleteRecvEsegrhd(recvData);
		
		// 보증서 내용 저장
		ksfcRepository.insertEgurdoc(recvData);
		
		ElecGuaranteeUtil.setRecvBondData(recvData);
		
		// 보증신청 테이블 상태값 변경
		ksfcRepository.updateCtgr(recvData);
	}
	
	/**
	 * 보증서 수신
	 * 
	 * @param param
	 * @return the map
	 */
	public void recv(HttpServletRequest request, HttpServletResponse response) {
		Map result = this.receiveGuarantee(request, response);
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(response.getOutputStream());
			out.print("resp_type_code`=" + result.get("responseResult") + "`&resp_type_name`=" + result.get("responseName") + "`&resp_mesg_text`=" + result.get("responseMessage"));
			out.flush();
		} catch(IOException e) {
			LOG.error("class : " + this.getClass().toString() + "recv error : " + e.toString());
		} finally {
			out.close();
		}
	}
}
