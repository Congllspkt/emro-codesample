package smartsuite.app.bp.edoc.template.inputElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Input
 *  - 계약조항 tag 공통 기능을 모아 만든 클래스
 *  - tag 공통 기능
 *    - name은 필수 값이다. ( 예시 : <input name="input_c001" />)
 *    - tag에 속성값을 추가할 수 있다. ( 예시 : group 속셍에 값을 cntrInfo 로 추가한다면 <input group="cntrInfo" /> )
 */
public class Input  {
	private static final Logger LOG = LoggerFactory.getLogger(Input.class);
	private String name; // <input name="input_c005" />
	private String tagName;
	protected List<Map> attributeList = new ArrayList<Map>(); // tag에 속성을 보관하는 변수 ( name="input_c005"일 때 map.put("name","input_c005")의 값을 list에 보관
	protected Input(String name, String tagName){
		this.name = name;
		this.tagName = tagName;
	}
	public String getName() {
		return this.name;
	}
	public String getTagName(){
		return this.tagName;
	}
	/**추가한 속성값으로 tag를 생성한다. name이 span이고, 속성값이 map.put("name","input_c005"),map.put("value","계약일자") 인경우는
	 * <span name="input_c005" value="계약일자"  <= 생성함.
	 *
	 *
	 **/
	protected String getTag() {
		String tag = "";

		tag = "<" + tagName;

		for (Map attribute : attributeList) {
			Iterator<String> iterator = attribute.keySet().iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = (String) attribute.get(key);
				tag += " " + key + "=" + "\'" + value + "\'";
			}
		}
		return tag;
	}
	/**
	 * 속성값을 추가한다. 동일한 속성값이 들어오면 업데이트 한다.
	 * 기존 value라는 key이름을 가지고 있다면, 업데이트 수행
	 * **/
	public void setAttribute(String key, String value) {

		boolean isExistName = false;
		for (Map attribute : attributeList) {
			Iterator<String> iterator = attribute.keySet().iterator();

			while (iterator.hasNext()) {
				String mapKey = iterator.next();
				if (key.equals(mapKey)) {
					attribute.put(mapKey, value);
					isExistName = true;
					return;
				}
			}
		}
		if (isExistName == false) {
			Map attribute = new HashMap<>();
			attribute.put(key, value);
			attributeList.add(attribute);
		}

	}

}
