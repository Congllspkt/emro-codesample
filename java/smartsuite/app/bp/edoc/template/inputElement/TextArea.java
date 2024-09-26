package smartsuite.app.bp.edoc.template.inputElement;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
public class TextArea extends Input{
	private static final Logger LOG = LoggerFactory.getLogger(TextArea.class);
	public final static String TYPE = "textarea";
	private final String defaultCols = "50";
	private final String defaultRows = "5";

	private String value;

	public TextArea(String name, String value){
		super(name,"textarea");
		this.value = value;
		setDefaultAttribute();
	}
	public TextArea(String name){
		super(name,"textarea");
		setDefaultAttribute();
	}

	private void setDefaultAttribute(){
		Map nameAttribute = new HashMap<>();
		nameAttribute.put("name", super.getName());
		attributeList.add(nameAttribute);

		Map colsAttribute = new HashMap<>();
		colsAttribute.put("cols",defaultCols);
		attributeList.add(colsAttribute);

		Map rowsAttribute = new HashMap<>();
		rowsAttribute.put("rows",defaultRows);
		attributeList.add(rowsAttribute);

		Map typeAttribute = new HashMap<>();
		typeAttribute.put("type",TYPE);
		attributeList.add(typeAttribute);
	}

	public String getTag() {

		String tag = super.getTag();
		tag += ">";
		if(!Strings.isNullOrEmpty(value)){
			tag += value;
		}
		tag += "</" + super.getTagName() + ">";
		LOG.info("tag : " + tag);
		return tag;
	}

}
