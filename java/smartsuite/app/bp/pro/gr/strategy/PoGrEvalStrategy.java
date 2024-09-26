package smartsuite.app.bp.pro.gr.strategy;

import java.util.List;

@SuppressWarnings ({ "rawtypes" })
public interface PoGrEvalStrategy {
	
	/**
	 * 발주/입고 ID 기준으로 각 유형 별 입고평가관리그룹 값 조회
	 * ex) 입고평가 유형이 '구매 유형'인 경우 해당 입고의 구매 유형 값 리스트
	 * ex) 입고평가 유형이 '소싱그룹'인 경우 해당 입고 품목의 소싱그룹 포함 상위 소싱그룹 값 리스트
	 * ex) 입고평가 유형이 '품목분류'인 경우 해당 입고 품목의 품목분류 포함 상위 품목분류 값 리스트
	 * ex) 입고평가 유형이 '협력사 관리 유형'인 경우 해당 입고 업체의 관리 유형 값 리스트
	 *
	 * @param poUuid|grUuid
	 * @return
	 */
	public List<String> findListGemgValues(String uuid);
}
