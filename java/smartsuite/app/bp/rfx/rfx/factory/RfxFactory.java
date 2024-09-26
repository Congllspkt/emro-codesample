package smartsuite.app.bp.rfx.rfx.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import smartsuite.app.bp.rfx.rfx.strategy.IRfxRankingStrategy;
import smartsuite.app.bp.rfx.rfx.strategy.RfxStrategy;

/**
 * Rfx 오브젝트를 생성하는 팩토리 Class입니다.
 *
 * @author Yeon-u Kim
 */
@Service
public class RfxFactory implements ApplicationContextAware {
	
	private ApplicationContext actx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		actx = applicationContext;
	}
	
	/**
	 * rfx strategy의 값을 반환한다.
	 *
	 * @param rfxTyp the bid typ
	 * @return rfx strategy
	 */
	public RfxStrategy getRfxStrategy(String rfxTyp) {
		RfxStrategy strategy;
		if("RFP".equals(rfxTyp)) {
			strategy = (RfxStrategy) actx.getBean("rfpService");
		} else if("RFQ".equals(rfxTyp)) {
			strategy = (RfxStrategy) actx.getBean("rfqService");
		} else {
			strategy = null;
		}
		return strategy;
	}
	
	public IRfxRankingStrategy getRfxRankingStrategy(String type) {
		IRfxRankingStrategy strategy;
		if("AMOUNT".equals(type)) {
			strategy = (IRfxRankingStrategy) actx.getBean("rfxAmountRankingStrategy");
		} else if("SCORE".equals(type)) {
			strategy = (IRfxRankingStrategy) actx.getBean("rfxScoreRankingStrategy");
		} else {
			strategy = null;
		}
		return strategy;
	}
}
