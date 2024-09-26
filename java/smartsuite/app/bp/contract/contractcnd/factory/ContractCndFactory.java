package smartsuite.app.bp.contract.contractcnd.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.contract.contractcnd.service.ContractCndService;
import smartsuite.app.shared.CntrConst;

@Service
public class ContractCndFactory implements ApplicationContextAware {
	
	ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public ContractCndService getModule(String type) {
		if(CntrConst.CNTRDOC_TYPE.PO.equals(type) || CntrConst.CNTRDOC_TYPE.UNIT_PRICE.equals(type)) {
			return (ContractCndService) applicationContext.getBean("purcContractCndService");
		} else if(CntrConst.CNTRDOC_TYPE.GENERAL.equals(type)) {
			return (ContractCndService) applicationContext.getBean("otrsContractCndService");
		}
		return null;
	}
}
