package smartsuite.app.sp.rfx.nego.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.rfx.nego.repository.SpNegoRepository;
import smartsuite.app.sp.rfx.nego.validator.SpNegoValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * 협상관련 service
 * @author user
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class SpNegoService {
	
	@Inject
	SpNegoRepository spNegoRepository;
	
	@Inject
	SpNegoValidator spNegoValidator;
	
	@Inject
	SpNegoItemService spNegoItemService;
	
	/**
	 * 협상대상 조회
	 * @param param
	 * @return
	 */
	public List findNegoTargetList(Map param) {
		return spNegoRepository.findNegoTargetList(param);
	}

	/**
	 * 협상헤더 정보 조회
	 * @param param
	 * @return
	 */
	public Map findNegoInfo(Map param) {
		Map resultMap = Maps.newHashMap();
		resultMap.put("rfxData", spNegoRepository.findNegoRfxData(param));
		resultMap.put("negoDataList", spNegoRepository.findNegoQtaData(param));
		
		return resultMap;
	}

	/**
	 * 협상상세 품목 조회
	 * @param param
	 * @return
	 */
	public Map findNegoDetailInfo(Map param) {
		Map resultMap = this.findNegoInfo(param);
		resultMap.put("resultList", spNegoItemService.findNegoQtaDetail(param));
		return resultMap;
	}

	/**
	 * 협상 정보 저장
	 * @param param
	 * @return
	 */
	public void saveNegoInfo(Map param, String vdProgSts) {
		Map negoData = (Map) param.get("negoData");
		List updateList = (List) param.get("updateList");
		
		spNegoValidator.validate(negoData);
		
		negoData.put("nego_vd_sts_ccd", vdProgSts);
		this.saveNegoHeader(negoData);
		this.saveNegoDetail(updateList);
	}

	/**
	 * 협상상세 저장
	 * @param updateList
	 */
	private void saveNegoDetail(List updateList) {
		for(Map item: (List<Map>) updateList) {
			spNegoItemService.saveNegoDetail(item);
		}
	}
	
	/**
	 * 협상헤더 저장
	 * @param negoData
	 */
	private void saveNegoHeader(Map negoData) {
		spNegoRepository.saveNegoHeader(negoData);
	}
	
	/**
	 * 협상 포기
	 * @param param
	 * @return
	 */
	public void rejectNego(Map param) {
		spNegoValidator.validate(param);
		
		param.put("nego_vd_sts_ccd", "GUP");
		this.saveNegoHeader(param);
		this.initNegoItemPrice(param);
	}
	
	
	public void temporarySaveNego(Map param) {
		this.saveNegoInfo(param, "CRNG");
	}

	public void submitNego(Map param) {
		this.saveNegoInfo(param, "SUBM");
	}
	
	/**
	 * 협상품목 가격(단가, 금액) 초기화
	 * @param param
	 */
	public void initNegoItemPrice(Map param) {
		spNegoItemService.initNegoItemPrice(param);
	}

}
