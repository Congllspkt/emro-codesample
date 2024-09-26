package smartsuite.app.shared;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.contract.repository.EcontractRepository;
import smartsuite.app.bp.edoc.template.repository.MainTemplateRepository;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.exception.CommonException;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class SignOrderService {
	private static final Logger LOG = LoggerFactory.getLogger(SignOrderService.class);
	@Inject
	EcontractRepository econtractRepository;
	@Inject
	SharedService sharedService;
	@Inject
	MainTemplateRepository mainTemplateRepository;

	public SignOrder getSignOrder(GetSignOrderType getSignOrderType, String cntrdocTmplUuid){
		SignOrder signOrder;
		if( GetSignOrderType.BY_SETUP.equals(getSignOrderType)) {

			if( getTemplateSignOrderSetup() ) {
				if(Strings.isNullOrEmpty(cntrdocTmplUuid)){
					signOrder = getDefaultSignOrder();
					return signOrder;
				}
				signOrder = getSignOrderByMainTemplateId(cntrdocTmplUuid);
				return signOrder;
			}else{
				signOrder = getDefaultSignOrder();
				return signOrder;
			}

		}else if( GetSignOrderType.TMPL.equals(getSignOrderType)) {
			signOrder = getSignOrderByMainTemplateId(cntrdocTmplUuid);
			return signOrder;
		}else if( GetSignOrderType.DEFAULT.equals(getSignOrderType)) {
			signOrder = getDefaultSignOrder();
			return signOrder;
		}else{
			signOrder = getDefaultSignOrder();
			return signOrder;
		}
	}

	private SignOrder getSignOrderByMainTemplateId(String cntrdocTmplUuid) {
		SignOrder signOrder ;
		Map param = Maps.newHashMap();
		param.put("cntrdoc_tmpl_uuid", cntrdocTmplUuid);
		List mainTemplateList = mainTemplateRepository.findListCntrForm(param);
		Map<String,Object> mainTemplate = (Map<String, Object>)mainTemplateList.get(0);
		String sgnordTypCcd = (String) mainTemplate.get("sgnord_typ_ccd");
		signOrder = searchSignOrder(sgnordTypCcd);
		return signOrder;
	}


	private SignOrder getDefaultSignOrder() {
		String code = CntrConst.CODE.DEFAULT_SIGN_ORDER_COMMON_CODE;
		List<Map<String,Object>> ccdList = sharedService.findCommonCode(code);
		Map<String,Object> ccd = ccdList.get(0);
		String data = (String)ccd.get("data");
		return searchSignOrder(data);
	}

	private boolean getTemplateSignOrderSetup(){
		Map param = Maps.newHashMap();
		param.put("cstr_cnd_cd", "TMPL_SGNORD");
		param.put("ccd", CntrConst.CODE.USE_YN_COMMON_CODE);

		List<Map<String,Object>> ccdList = sharedService.findListCommonCodeAttributeCode(param);
		Map ccd = (Map)ccdList.get(0);
		String cstrCndVal = (String)ccd.get("cstr_cnd_val");
		if("Y".equals(cstrCndVal)) {
			return true;
		}else if("N".equals(cstrCndVal)) {
			return false;
		}else{
			LOG.error("class : " + this.getClass().toString() + "Invalid TMPL_SGNORD_SETUP property value for D000 common code.");
			throw new CommonException(this.getClass().toString() + "Invalid TMPL_SGNORD_SETUP property value for D000 common code.");
		}
	}

	private SignOrder searchSignOrder(String sgnordTypCcd){
		if(SignOrder.BUYER_VD.toString().equals(sgnordTypCcd)) {
			return SignOrder.BUYER_VD;
		}else if(SignOrder.SIMUL_SGN.toString().equals(sgnordTypCcd)) {
			return SignOrder.SIMUL_SGN;
		}else if(SignOrder.VD_BUYER.toString().equals(sgnordTypCcd)) {
			return SignOrder.VD_BUYER;
		}else{
			LOG.error("class : " + this.getClass().toString() + "There are no defined signature sequence values.");
			throw new CommonException(this.getClass().toString() + "There are no defined signature sequence values.");
		}
	}

	public SignOrder getSignOrderByCntrId(String cntrId){
		String sgnordTypCcd = econtractRepository.getSignOrderByCntrId(cntrId);
		return searchSignOrder(sgnordTypCcd);
	}

}
