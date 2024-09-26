package smartsuite.app.bp.vendorMaster.vendorImprove.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.vendorMaster.vendorImprove.service.VendorImproveService;
import smartsuite.app.bp.vendorMaster.vendorInfo.service.VendorMasterService;
import smartsuite.app.event.CustomSpringEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VendorImproveEventListener {

	@Inject
	VendorImproveService vdImproveService;

	/**
	 * 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 요청한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.pe_subj_res_uuid -  퍼포먼스 평가대상 결과 아이디 <br>
	 * @param event
	 */
	@EventListener(condition ="#event.eventId == 'findListPeSubjVdImprove'")
	public void findListPeSubjVdImprove(CustomSpringEvent event){
		List vdImproveList = vdImproveService.findListPeSubjVdImprove((Map) event.getData());
		event.setResult(vdImproveList);
	}

	/**
	 * 퍼포먼스 평가 대상 결과의 개선관리 등록 데이터를 요청한다.
	 * <br><br>
	 * <b>Required:</b><br>
	 * param.ten_id - 테넌트 아이디 <br>
	 * param.pe_subj_res_uuid -  퍼포먼스 평가대상 결과 아이디 <br>
	 * @param event
	 */
	@EventListener(condition ="#event.eventId == 'createPeSubjVdImprove'")
	public void createPeSubjVdImprove(CustomSpringEvent event){
		vdImproveService.createPeSubjVdImprove((Map) event.getData());
	}

}
