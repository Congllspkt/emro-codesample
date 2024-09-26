package smartsuite.app.bp.pro.upcr.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UpcrEventPublisher {

     static final Logger LOGGER = LoggerFactory.getLogger(UpcrEventPublisher.class);

   @Inject
   ApplicationEventPublisher applicationEventPublisher;

   public void createPoPr(Map<String, Object> poData,List<Map<String,Object>> insertPoItems){
        Map<String, Object> poParam = new HashMap<String, Object>();
        poParam.put("poData", poData);
        poParam.put("insertItems", insertPoItems);

		CustomSpringEvent completeEvent =  CustomSpringEvent.toCompleteEvent("createPoPr", poParam);
		applicationEventPublisher.publishEvent(completeEvent);
        LOGGER.info("TransactionSynchronizationManager.getCurrentTransactionName()"+ TransactionSynchronizationManager.getCurrentTransactionName());
	}

    public void updateRfxUpcr(Map<String, Object> keyParam) {
        CustomSpringEvent event =  CustomSpringEvent.toCompleteEvent("updateRfxUpcr", keyParam);
        applicationEventPublisher.publishEvent(event);

        LOGGER.info("TransactionSynchronizationManager.getCurrentTransactionName()"+ TransactionSynchronizationManager.getCurrentTransactionName());
    }
	
	public void requestRfx(List<Map> reqList) {
		if(reqList == null) {
			return;
		}
		if(reqList.isEmpty()) {
			return;
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("requestRfx", reqList);
		applicationEventPublisher.publishEvent(event);
	}
	
	/**
	 * 단가계약요청 변경을 위한 저장 시 뒤 업무에 적재되어 있는 데이터 삭제 Validation
	 *
	 * @param preParam
	 * @return
	 */
	public ResultMap validateDeleteRequestReqInfoByChangeReq(Map preParam) {
		if(preParam == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("validateDeleteRequestReqInfoByChangeReq", preParam);
		applicationEventPublisher.publishEvent(event);
		
		return (ResultMap) event.getResult();
	}
	
	/**
	 * 단가계약요청 변경을 위한 저장 시 뒤 업무에 적재되어 있는 데이터 삭제 필요
	 *
	 * @param preParam
	 * @return
	 */
	public ResultMap deleteRequestReqInfoByChangeReq(Map preParam) {
		if(preParam == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("deleteRequestReqInfoByChangeReq", preParam);
		applicationEventPublisher.publishEvent(event);
		
		return (ResultMap) event.getResult();
	}
	
	public ResultMap recoveryRequestReqInfoByChangeReq(Map preParam) {
		if(preParam == null) {
			return ResultMap.NOT_EXISTS();
		}
		
		CustomSpringEvent event = CustomSpringEvent.toCompleteEvent("recoveryRequestReqInfoByChangeReq", preParam);
		applicationEventPublisher.publishEvent(event);
		
		return (ResultMap) event.getResult();
	}
}
