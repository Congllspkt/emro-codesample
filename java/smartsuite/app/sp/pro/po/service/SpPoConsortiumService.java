package smartsuite.app.sp.pro.po.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.sp.pro.po.event.SpPoEventPublisher;
import smartsuite.app.sp.pro.po.repository.SpPoConsortiumRepository;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
@Service
@Transactional
public class SpPoConsortiumService {

    @Inject
    SpPoEventPublisher spPoEventPublisher;

    @Inject
    SpPoService spPoService;

    @Inject
    SpPoConsortiumRepository spPoConsortiumRepository;

    /**
     * 컨소시엄 정보 조회
     *
     * @param param {po_uuid}
     * @return
     */
    public Map<String, Object> findPoCsData(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        Map<String, Object> rfxParam = new HashMap<String, Object>();
        rfxParam.put("rfx_uuid", spPoService.findRfxIdByPoId(param));
        //rfxParam.put("rfxData", sqlSession.selectOne("rfx.findRfx", rfxParam));
        resultMap.put("rfxData", spPoEventPublisher.findRfxQta(rfxParam));
        //resultMap.put("rfxData", sqlSession.selectOne("sp-rfx-qta.findRfxQta", rfxParam));

        // 발주 조회
        Map<String, Object> poData = spPoService.findPo(param);
        resultMap.put("poData", poData);

        // 컨소시엄 대표 협력사 코드 = 발주 협력사 코드
        param.put("rep_vd_cd", poData.get("vd_cd"));
        resultMap.put("csData", spPoConsortiumRepository.findPoCs(param));
        resultMap.put("csVdList", spPoConsortiumRepository.findListPoCsVd(param));
        return resultMap;
    }

    /**
     * 컨소시엄 발주 현황 조회
     *
     * @param param
     * @return
     */
    public FloaterStream findListPoByCsVd(Map<String, Object> param) {
        // 대용량 처리
        return spPoConsortiumRepository.findListPoByCsVd(param);
    }
}
