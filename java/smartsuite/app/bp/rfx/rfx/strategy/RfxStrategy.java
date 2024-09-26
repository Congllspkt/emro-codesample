package smartsuite.app.bp.rfx.rfx.strategy;

import java.util.Map;

import smartsuite.app.common.shared.ResultMap;

/**
 * RfxStrategy Interface 입니다.
 *
 * @author Yeon-u Kim
 */
@SuppressWarnings ({ "rawtypes" })
public interface RfxStrategy {

	/**
	 * RFx 시작
	 * 
	 * @param param
	 */
	public void startRfx(Map<String, Object> param);
	
	/**
	 * RFx 마감
	 *
	 * @param param the param
	 * @return the map
	 */
	public ResultMap closeRfx(Map param);
	
	/**
	 * RFx 개찰전 Ranking 구하기
	 * 
	 * @param param
	 */
	public void computeRanking(Map<String, Object> param);
}
