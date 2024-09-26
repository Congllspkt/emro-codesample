package smartsuite.app.bp.pro.po.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.pro.po.event.PoEventPublisher;
import smartsuite.app.bp.pro.po.repository.PoConsortiumVedorRepository;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PoConsortiumVendorService {

	@Inject
	PoConsortiumVedorRepository poConsortiumVendorRepository;

	@Inject
	PoEventPublisher poEventPublisher;

	@Inject
	PoService poService;

	/**
	 * 컨소시엄 정보 조회
	 *
	 * @param param {po_uuid}
	 * @return
	 */
	public Map<String, Object> findPoConsortium(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> rfxParam = Maps.newHashMap();

		rfxParam.put("rfx_uuid", poService.findRfxIdByPoId(param));
		//resultMap.put("rfxData", sqlSession.selectOne("rfx.findRfx", rfxParam));
		resultMap.put("rfxData", poEventPublisher.findRfx(rfxParam));

		Map<String, Object> poData = poService.findPo(param);
		resultMap.put("poData", poData);

		Map<String, Object> csParam = Maps.newHashMap();
		csParam.put("po_uuid"    , param.get("po_uuid"));
		csParam.put("rep_vd_cd", poData.get("vd_cd"));
		resultMap.put("csData", poConsortiumVendorRepository.findPoCs(csParam));
		resultMap.put("csVdList", poConsortiumVendorRepository.findListPoCsVd(csParam));
		return resultMap;
	}

	/**
	 * 컨소시엄 구성원 정보 저장
	 *
	 * @param param
	 * @return
	 */
	public ResultMap savePoConsortiumData(Map<String, Object> param) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> updatePoCss = (List<Map<String, Object>>)param.get("updatePoCss");
		for(Map<String, Object> updatePoCs : updatePoCss) {
			poConsortiumVendorRepository.updatePoConsortium(updatePoCs);
		}
		return ResultMap.SUCCESS();
	}

	/**
	 * 발주 컨소시엄 정보 조회 하는 함수
	 *
	 * @author : LDS
	 * @param param( po_uuid: 발주아이디 )
	 * @return
	 * @Date : 2020. 05. 13
	 * @Method Name : findConsortiumListByPoId
	 */
	public List<Map<String, Object>> findConsortiumListByPoId(Map<String, Object> param) {
		return poConsortiumVendorRepository.findConsortiumListByPoId(param);
	}

	/**
	 * 발주 컨소시엄 목록을 삭제한다.
	 *
	 * @param param
	 */
	public void deletePoConsortiumByPoId(Map<String, Object> param) {
		poConsortiumVendorRepository.deletePoConsortiumByPoId(param);
	}

	public List<Map<String, Object>> findListPoCsVd(Map<String, Object> csParam) {
		return poConsortiumVendorRepository.findListPoCsVd(csParam);
	}

	public void insertPoConsortium(Map<String, Object> insertPoCs) {
		poConsortiumVendorRepository.insertPoConsortium(insertPoCs);
	}
}
