package smartsuite.app.bp.edoc.network.validator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import smartsuite.app.bp.edoc.network.repository.NetWorkConnectRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;

/**
 * 네트워크 연결 관련 처리를 하는 서비스 Class입니다.
 *
 * @FileName NetWorkConnectService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class NetWorkConnectValidator {

	private static final Logger LOG = LoggerFactory.getLogger(NetWorkConnectValidator.class);
	
	
	/**
	 * connect check 조회
	 * 
	 * @param : hostName, posrt
	 * @return : ResultMap
	 */
	public ResultMap socketConect(String hostName, int port){
		
		Socket socket = new Socket();
		try {
			SocketAddress endpoint = new InetSocketAddress(hostName, port); //ip 연결확인
			socket.connect(endpoint, 10000);
			
		}catch(IOException e) {
			LOG.error("class : " + this.getClass().toString() + "socketConect error : " + e.toString());
			return ResultMap.FAIL("class : " + this.getClass().toString() + "socketConect error : " + e.toString());
		}finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					LOG.error("class : " + this.getClass().toString() + "socketConect error : " + e.toString());
					return ResultMap.FAIL("class : " + this.getClass().toString() + "socketConect error : " + e.toString());
				}
			}
		}
		return ResultMap.SUCCESS();
	}

}