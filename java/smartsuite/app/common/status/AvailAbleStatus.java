package smartsuite.app.common.status;

import com.google.common.collect.Lists;
import smartsuite.app.bp.pro.shared.ProConst;

import java.util.List;

public final class AvailAbleStatus {

    private AvailAbleStatus(){}

    // 구매 반송가능한 상태
    public static List<String> prReturnStatusList(){
        List<String> availableStsList = Lists.newArrayList();
        availableStsList.add(ProConst.PR_STS_CCD_RW);	// 구매요청 접수대기
        availableStsList.add(ProConst.PR_STS_CCD_RV);	// 구매요청 접수
        availableStsList.add(ProConst.PR_PROG_STS_IC);	// RFI 완료
        return availableStsList;
    }

    // 구매요청접수 화면에서 RFx & Po & Rfi 생성 시
    public static List<String> prCreateRfxPoStatusList(){
        List<String> availableStsList = Lists.newArrayList();
        availableStsList.add(ProConst.PR_STS_CCD_RW);	// 구매요청접수대기 (ZER-341 이슈에 따라 접수하지 않아도 다음 프로세스 진행가능)
        availableStsList.add(ProConst.PR_STS_CCD_RV);	// 구매요청접수
        availableStsList.add(ProConst.PR_PROG_STS_IC);	// RFI완료
        return availableStsList;
    }

    // PR Item 변경 가능여부 체크
    public static List<String> prItemSaveModeStatusList(){
        List<String> availableStsList = Lists.newArrayList();
        availableStsList.add(ProConst.PR_STS_CCD_RW);		// 구매요청접수대기 (ZER-341 이슈에 따라 접수하지 않아도 다음 프로세스 진행가능)
        availableStsList.add(ProConst.PR_STS_CCD_RV);		// 구매요청접수
        availableStsList.add(ProConst.PR_STS_CCD_RD);		// 구매반려
        return availableStsList;
    }

    // 구매요청 접수
    public static List<String> prReceivePrStatusList(){
        List<String> availableStsList = Lists.newArrayList();
        availableStsList.add(ProConst.PR_STS_CCD_RW);
        return availableStsList;
    }

    // 구매요청 접수
    public static List<String> prUpdatePurcStatusList(){
        List<String> availableStsList = Lists.newArrayList();
        availableStsList.add(ProConst.PR_STS_CCD_RW);
        availableStsList.add(ProConst.PR_STS_CCD_RV);
        return availableStsList;
    }

}
