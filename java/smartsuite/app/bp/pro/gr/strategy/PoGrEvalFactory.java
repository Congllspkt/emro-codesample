package smartsuite.app.bp.pro.gr.strategy;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class PoGrEvalFactory implements ApplicationContextAware {
	
	private ApplicationContext actx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		actx = applicationContext;
	}
	
	public PoGrEvalStrategy getStrategy(String type, String gemtCd) {
		PoGrEvalStrategy strategy;
		
		if("PO".equals(type)) {
			if("PURCTYP".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("poEvalPurcTypStrategy");
			} else if("SG".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("poEvalSgStrategy");
			} else if("ITEMCAT".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("poEvalItemCatStrategy");
			} else if("VMT".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("poEvalVmtStrategy");
			} else {
				strategy = null;
			}
		} else if("GR".equals(type)) {
			if("PURCTYP".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("grEvalPurcTypStrategy");
			} else if("SG".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("grEvalSgStrategy");
			} else if("ITEMCAT".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("grEvalItemCatStrategy");
			} else if("VMT".equals(gemtCd)) {
				strategy = (PoGrEvalStrategy) actx.getBean("grEvalVmtStrategy");
			} else {
				strategy = null;
			}
		} else {
			strategy = null;
		}
		return strategy;
	}
}
