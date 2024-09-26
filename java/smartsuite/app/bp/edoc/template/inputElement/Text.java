package smartsuite.app.bp.edoc.template.inputElement;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Text extends Input {
	private static final Logger LOG = LoggerFactory.getLogger(Text.class);
	private String dataType = "";
	public final static String NUMBER_DATA_TYPE = "number";
	public final static String TYPE = "text";
	public final static String DATA_TYPE = "datatype";
	private String frontAdditionalWord = "";
	private String backEndAdditionalWord = "";

	public Text(String name, String value){
		super(name, "input");
		setDefaultAttribute(value);
	}

	/**
	 * 기본 속성값
	 */
	private void setDefaultAttribute( String value ){
		Map nameAttribute = new HashMap<>();
		nameAttribute.put("name", super.getName());
		attributeList.add(nameAttribute);

		if(!Strings.isNullOrEmpty(value)){
			Map valueAttribute = new HashMap<>();
			valueAttribute.put("value", value);
			attributeList.add(valueAttribute);
		}

		Map typeAttribute = new HashMap<>();
		typeAttribute.put("type",TYPE);
		attributeList.add(typeAttribute);
	}

	public String getTag() {
		String tag = frontAdditionalWord + super.getTag();
		tag += "/>" + backEndAdditionalWord;
		LOG.info("tag : " + tag);
		return tag;
	}
	public void setBackEndAdditionalWord(String backEndAdditionalWord){
		this.backEndAdditionalWord = backEndAdditionalWord;
	}
}
