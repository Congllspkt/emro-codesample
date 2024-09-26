package smartsuite.app.bp.edoc.template.inputElement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zefer.font.c;
import smartsuite.app.shared.EdocConst;
import smartsuite.exception.CommonException;

import javax.swing.text.Document;
import javax.swing.text.Element;
import java.util.HashMap;
import java.util.Map;

public class Radio extends Input{
	private static final Logger LOG = LoggerFactory.getLogger(Radio.class);
	private String value = "";
	public final static String TYPE = "radio";
	private final static String CHECKED = "checked";


	public Radio(String name) {
		super(name, "input");
	}
	public Radio(String name, String value) {
		super(name, "input");
		this.value = value;
		setDefaultAttribute();
	}
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
		typeAttribute.put("type", TYPE);
		attributeList.add(typeAttribute);
	}

	public String getTag() {
		String tag = super.getTag();
		tag += "/>";
		return tag;
	}

}
