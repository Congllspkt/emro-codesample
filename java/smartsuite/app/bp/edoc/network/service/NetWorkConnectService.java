package smartsuite.app.bp.edoc.network.service;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.network.repository.NetWorkConnectRepository;
import smartsuite.app.bp.edoc.network.validator.NetWorkConnectValidator;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 네트워크 연결 관련 처리를 하는 서비스 Class입니다.
 *
 * @FileName NetWorkConnectService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class NetWorkConnectService {

	private static final Logger LOG = LoggerFactory.getLogger(NetWorkConnectService.class);
	
	@Inject
	NetWorkConnectRepository netWorkConnectRepository; 
	
	@Inject
	NetWorkConnectValidator netWorkConnectValidator;
	
	/**
	 * ip, port, domain 저장
	 * 
	 * @param param
	 * @return Map
	 */
	public Map<String,Object> saveIpInfo(Map<String,Object> param){
		Map<String,Object> result = Maps.newHashMap();
		
		List<Map<String,Object>> insertList = (List<Map<String,Object>>)param.get("insertList");
		List<Map<String,Object>> updateList = (List<Map<String,Object>>)param.get("updateList");
		for(Map<String,Object> row : insertList) {
			int port = (int) row.get("outsdparty_port");

			result = checkIpAndDomainConnect((String)row.get("outsdparty_ip"), (String)row.get("outsdparty_domain"), port);
			
			row.put("fw_ip_conn_sts", result.get("fw_ip_conn_sts") );
			row.put("fw_domain_conn_sts", result.get("fw_domain_conn_sts") );
			row.put("fw_conn_fail_msg", result.get(Const.RESULT_MSG));
			
			row.put("outsdparty_fw_uuid", UUID.randomUUID().toString());
			netWorkConnectRepository.insertIpInfo(row);
		}
		
		for(Map<String,Object> row : updateList) {
			
			result = checkIpAndDomainConnect((String)row.get("outsdparty_ip"),(String)row.get("outsdparty_domain"), (Integer)row.get("outsdparty_port"));
			
			row.put("fw_ip_conn_sts", result.get("fw_ip_conn_sts") );
			row.put("fw_domain_conn_sts", result.get("fw_domain_conn_sts") );
			row.put("fw_conn_fail_msg", result.get(Const.RESULT_MSG));
			
			netWorkConnectRepository.updateIpInfo(row);
		}
		
		return result;
	}
	
	/**
	 * ip, port, domain 삭제
	 * 
	 * @param param
	 * @return void
	 */
	public void deleteIpInfo(Map<String,Object> param){
		List<Map<String,Object>> deleteList = (List<Map<String,Object>>)param.get("deleteList");
		for(Map<String,Object> row : deleteList) {
			
			netWorkConnectRepository.deleteIpInfo(row);
		}
	}
	
	/**
	 * ip, port, domain 조회
	 * 
	 * @param param
	 * @return List
	 */
	public List<Map<String,Object>> selectIpList(Map<String,Object> param){
		return netWorkConnectRepository.selectIpList(param);
	}
	
	/**
	 * connect check 조회
	 * 
	 * @param param
	 * @return Map
	 */
	public Map<String,Object> checkConnect(Map<String,Object> param){
		List<Map<String,Object>> connectList = (List<Map<String,Object>>)param.get("connectList");
		Map<String,Object> result = Maps.newHashMap();
		
		result.put(Const.RESULT_STATUS, Const.SUCCESS);
		
		for(Map<String,Object> row : connectList) {
			String ip = (String)row.get("outsdparty_ip");
			int outsdparty_port = (Integer)row.get("outsdparty_port");
			String outsdparty_domain = (String)row.get("outsdparty_domain");
			
			result = checkIpAndDomainConnect(ip,outsdparty_domain, outsdparty_port);
			
			row.put("fw_ip_conn_sts", result.get("fw_ip_conn_sts") );
			row.put("fw_domain_conn_sts", result.get("fw_domain_conn_sts") );
			row.put("fw_conn_fail_msg", result.get(Const.RESULT_MSG));
			netWorkConnectRepository.updateConnectInfo(row);
		}
		
		return result;
	}
	
	/**
	 * connect check 조회
	 * 
	 * @param : String ip
	 * @param : String domain
	 * @param : int port
	 * @return Map 결과
	 */
	private Map<String,Object> checkIpAndDomainConnect(String ip, String domain, int port){
		Map result = Maps.newHashMap();
		
		ResultMap validator = netWorkConnectValidator.socketConect(ip,port); //ip 연결확인
		
		if(validator.isSuccess()) {
			result.put("fw_ip_conn_sts", "Y");
			validator = netWorkConnectValidator.socketConect(domain, port); //domain 연결확인
			
			if(validator.isSuccess()) {
				result.put("fw_domain_conn_sts", "Y");
			}else {
				result.put("fw_domain_conn_sts", "N");
				//Map resultData = validator.getResultData();
				result.put(Const.RESULT_MSG, validator.getResultMessage());
			}
		}else {
			Map resultData = validator.getResultData();
			result.put(Const.RESULT_MSG, validator.getResultMessage());
			result.put("fw_ip_conn_sts", "N");
			result.put("fw_domain_conn_sts", "N");
		}
		return result;
	}

}