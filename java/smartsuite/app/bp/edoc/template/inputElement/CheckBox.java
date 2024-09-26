package smartsuite.app.bp.edoc.template.inputElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartsuite.app.shared.EdocConst;

import java.util.HashMap;
import java.util.Map;
public class CheckBox extends Input{
	private static final Logger LOG = LoggerFactory.getLogger(CheckBox.class);
	private String value = "";

	public final static String TYPE = "checkbox";

	public CheckBox(String name, String value){
		super(name,"input");
		this.value = value;
		setDefaultAttribute();

	}
	//private String get
	private void setDefaultAttribute(){
		Map nameAttribute = new HashMap<>();
		nameAttribute.put("name", super.getName());
		attributeList.add(nameAttribute);

		if("Y".equals(this.value)){
			Map checkedAttribute = new HashMap<>();
			checkedAttribute.put(EdocConst.CHECKED, EdocConst.CHECKED);
			attributeList.add(checkedAttribute);
		}

		Map typeAttribute = new HashMap<>();
		typeAttribute.put("type",TYPE);
		attributeList.add(typeAttribute);

	}

	public String getTag() {
		String tag = super.getTag();
		tag += "/>";
		return tag;
	}

}
