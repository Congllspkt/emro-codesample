package smartsuite.app.common.util;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MapUtils {
	
	/**
	 * 기준 Map 에 다른 Map 데이터 매핑한다.
	 * 동일한 Key 데이터 복사한다.
	 *
	 * @param base
	 * @param reference
	 * @param columns
	 * @return
	 */
	public static void copyToKeyValue(Map<String, Object> base, Map<String, Object> reference, List<String> columns) {
		for(String column : columns) {
			base.put(column, reference.get(column));
		}
	}
	
	/**
	 * 기준 Map 에 다른 Map 데이터 매핑한다.
	 * 다른 Key 데이터 복사한다.
	 *
	 * @param base
	 * @param reference
	 * @param columns
	 * @return
	 */
	public static void copyToDiffKeyValue(Map<String, Object> base, List<String> baseColumns, Map<String, Object> reference, List<String> referenceColumns) {
		if(baseColumns.size() != referenceColumns.size()) {
			return;
		}
		for(int i = 0; i < baseColumns.size(); i++) {
			base.put(baseColumns.get(i), reference.get(referenceColumns.get(i)));
		}
	}
}
