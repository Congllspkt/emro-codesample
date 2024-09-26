package smartsuite.app.bp.rfx.rfx.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.rfx.rfx.repository.RfxItemPriceFactorRepository;
import smartsuite.app.common.RfxConst;
import smartsuite.app.common.shared.ResultMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@Transactional
public class RfxItemPriceFactorService {
	
	@Inject
	RfxItemPriceFactorRepository rfxItemPriceFactorRepository;
	
	/**
	 * RFx 품목 별 원가구성항목 저장한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfxItem - 견적요청 품목<br>
	 * param.checkItem - 견적요청 품목 - 변경 전 정보<br>
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap saveRfxItemPriceFactor(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		Map rfxItem = (Map) param.get("rfxItem");
		Map checkItem = (Map) param.get("checkItem");
		
		//기존 가격군과 다르다면 모든 ESPRQTC 삭제
		String prcGrpCd = (String) rfxItem.get("costfact_grp_cd");
		String prePrcGrpCd = (String) checkItem.get("costfact_grp_cd");
		if(prcGrpCd != null && !prcGrpCd.equals(prePrcGrpCd)) {
			this.deletePriceFactorByRfxItem(rfxItem);
		}
		
		List insertItems = (List) param.get("insertItems");
		List removeItems = (List) param.get("removeItems");
		
		this.insertPriceFactor(rfxItem, insertItems);
		this.deletePriceFactorByRfxItem(removeItems);
		
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 다건의 원가구성항목을 신규 저장한다.<br><br>
	 *
	 * @param rfxItem          - 견적요청 품목
	 * @param priceFactorItems - 신규 저장할 원가구성항목 다건
	 */
	private void insertPriceFactor(Map rfxItem, List priceFactorItems) {
		if(priceFactorItems == null) {
			return;
		}
		if(priceFactorItems.isEmpty()) {
			return;
		}
		
		for(Map priceFactorItem : (List<Map>) priceFactorItems) {
			this.insertPriceFactor(rfxItem, priceFactorItem);
		}
	}
	
	/**
	 * 원가구성항목을 신규 저장한다.<br><br>
	 *
	 * @param rfxItem         - 견적요청 품목
	 * @param priceFactorItem - 신규 저장할 원가구성항목
	 */
	private void insertPriceFactor(Map rfxItem, Map priceFactorItem) {
		if(priceFactorItem == null) {
			return;
		}
		
		priceFactorItem.put("rfx_item_uuid", rfxItem.get("rfx_item_uuid"));
		priceFactorItem.put("costfact_uom_ccd", rfxItem.get("uom_ccd"));
		rfxItemPriceFactorRepository.insertPriceFactor(priceFactorItem);
	}
	
	/**
	 * 여러건의 견적품목 원가구성항목을 삭제한다.
	 *
	 * @param deleteRfxItem - 삭제할 견적품목 다건
	 * @return void
	 */
	public void deletePriceFactorByRfxItem(List<Map> rfxItems) {
		if(rfxItems == null) {
			return;
		}
		if(rfxItems.isEmpty()) {
			return;
		}
		
		for(Map rfxItem : rfxItems) {
			rfxItemPriceFactorRepository.deletePriceFactorByRfxItem(rfxItem);
		}
	}
	
	/**
	 * 견적품목의 원가구성항목을 삭제한다.
	 *
	 * @param rfxItem - 삭제할 견적품목
	 * @return void
	 */
	public void deletePriceFactorByRfxItem(Map rfxItem) {
		rfxItemPriceFactorRepository.deletePriceFactorByRfxItem(rfxItem);
	}
	
	/**
	 * 원가구성항목설정여부가 "N"인 경우 원가구성항목 정보 전체 삭제
	 *
	 * @param rfxData - 견적마스터
	 * @return void
	 */
	public void settingPriceFactorValueN(Map rfxData) {
		if(rfxData == null) {
			return;
		}
		if(rfxData.get("coststr_use_yn") == null) {
			return;
		}
		if("Y".equals(rfxData.get("coststr_use_yn"))) {
			return;
		}
		
		rfxItemPriceFactorRepository.deletePriceFactorByRfx(rfxData);
	}
	
	/**
	 * 견적 ID 기준으로 기존 견적의 원가구성항목을 새로운 견적에 복사한다.<br><br>
	 *
	 * @param priceFactorSetYn - 원가구성여부 Y/N
	 * @param prevRfxId        - 기존 견적요청 ID
	 * @param newRfxId         - 신규 견적요청 ID
	 */
	public void copyPriceFactorByRfx(String priceFactorSetYn, String prevRfxId, String newRfxId) {
		if(priceFactorSetYn == null || "N".equals(priceFactorSetYn)) {
			return;
		}
		if(prevRfxId == null) {
			return;
		}
		if(newRfxId == null) {
			return;
		}
		
		Map param = Maps.newHashMap();
		param.put("src_rfx_uuid", prevRfxId);
		param.put("tgt_rfx_uuid", newRfxId);
		
		rfxItemPriceFactorRepository.copyRfxItemPriceFactors(param);
	}
	
	/**
	 * 원가구성항목 설정 여부 확인한다.<br><br>
	 * <b>Required:</b><br>
	 * param.rfx_uuid - 견적요청 ID<br>
	 * param.coststr_use_yn - 원가구성 설정여부<br>
	 *
	 * @param param - 견적요청 데이터
	 * @return ResultMap
	 */
	public ResultMap checkPriFactSet(Map param) {
		if(param == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		String rfxId = (String) param.get("rfx_uuid");
		String priFactSetYn = (String) param.get("coststr_use_yn");
		if("N".equals(priFactSetYn)) {
			return ResultMap.SKIP();
		}
		
		Integer notMappedCnt = rfxItemPriceFactorRepository.checkRfxItemNotMappedPriFact(rfxId);
		if(notMappedCnt > 0) {
			return ResultMap.builder()
			                .resultStatus(RfxConst.RFX_PRI_FACT_SET_INCOMPELETED)
			                .build();
		} else {
			return ResultMap.SUCCESS();
		}
	}
	
	/**
	 * 견적요청품목 별 가격군 연결 조회<br><br>
	 * <b>Required:</b><br>
	 * param.rfxId - 견적요청 ID<br>
	 *
	 * @param param
	 * @return List
	 */
	public List searchRfxItemWithPriceGroup(Map param) {
		return rfxItemPriceFactorRepository.searchRfxItemWithPriceGroup(param);
	}
	
	/**
	 * 견적요청 품목 별 원가구성항목 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.oorg_cd - 조직코드<br>
	 * param.rfx_item_uuid - 견적요청 품목 ID<br>
	 * param.costfact_grp_cd - 가격군 코드<br>
	 *
	 * @param param
	 * @return List
	 */
	public List searchPriceFactorByRfxItem(Map param) {
		return rfxItemPriceFactorRepository.searchPriceFactorByRfxItem(param);
	}
	
	/**
	 * 품목군 별 가격군 목록 조회한다.<br><br>
	 * <b>Required:</b><br>
	 * param.oorg_cd - 조직코드<br>
	 * param.coststr_purc_typ_ccd - 품목그룹<br>
	 *
	 * @param param
	 * @return List
	 */
	public List searchPriceGroupByItemGrpTyp(Map param) {
		return rfxItemPriceFactorRepository.searchPriceGroupByItemGrpTyp(param);
	}
}
