package smartsuite.app.bp.edoc.template.inputElement;

import java.util.HashMap;
import java.util.Map;
public class DynamicTemplate extends Input{

	public final static String TYPE = "dynamic";
	private String cntrClNm;

	public DynamicTemplate(String name, String cntrClNm){
		super(name,"span");
		this.cntrClNm = cntrClNm;
		setDefaultAttribute();
	}
	private void setDefaultAttribute(){
		Map nameAttribute = new HashMap<>();
		nameAttribute.put("name", super.getName());
		attributeList.add(nameAttribute);

		Map typeAttribute = new HashMap<>();
		typeAttribute.put("type", TYPE);
		attributeList.add(typeAttribute);

	}
	public String getTag() {
		String tag = super.getTag();
		tag += ">" + "[[동적 템플릿 : " + cntrClNm + "]]";
		tag += "</" + super.getTagName() + "> ";
		return tag;
	}

}
