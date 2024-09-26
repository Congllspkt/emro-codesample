package smartsuite.app.bp.edoc.network;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import smartsuite.app.bp.edoc.network.service.NetWorkConnectService;

/**
 * ip연결 관리 컨트롤러 Class입니다.
 *
 * @author daesung lee
 * @see 
 * @FileName NetWorkConnectController.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/edoc/**")
public class NetWorkConnectController {

	@Inject
	NetWorkConnectService netWorkConnectService;
	
	/**
	 * ip port domain 정보 저장
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the param
	 * @Date : 2019. 3. 06
	 * @Method Name : saveCert
	 */
	@RequestMapping(value = "saveIpInfo.do")
	public @ResponseBody Map<String,Object> saveIpInfo(@RequestBody Map param){
		return netWorkConnectService.saveIpInfo(param);
	}
	
	/**
	 * ip port domain 정보 삭제
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the param
	 * @Date : 2019. 3. 06
	 * @Method Name : saveCert
	 */
	@RequestMapping(value = "deleteIpInfo.do")
	public @ResponseBody void deleteIpInfo(@RequestBody Map param){
		netWorkConnectService.deleteIpInfo(param);
	}
	
	/**
	 * ip port domain 정보 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the param
	 * @Date : 2019. 3. 06
	 * @Method Name : selectIpList
	 */
	@RequestMapping(value = "selectIpList.do")
	public @ResponseBody List<Map<String,Object>> selectIpList(@RequestBody Map param){
		return netWorkConnectService.selectIpList(param);
	}
	/**
	 * ip port domain 정보 조회
	 *
	 * @author : daesung lee
	 * @param param the param
	 * @return the param
	 * @Date : 2019. 3. 06
	 * @Method Name : checkConnect
	 */
	@RequestMapping(value = "checkConnect.do")
	public @ResponseBody Map<String,Object> checkConnect(@RequestBody Map param){
		return netWorkConnectService.checkConnect(param);
	}
	
	
}