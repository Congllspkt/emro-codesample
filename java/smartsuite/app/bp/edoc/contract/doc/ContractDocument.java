package smartsuite.app.bp.edoc.contract.doc;

import com.google.common.base.Strings;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartsuite.app.bp.edoc.template.inputElement.*;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.exception.CommonException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContractDocument {
	private Document document;
	private Document disAbledDocument;
	private StringBuffer stringBufferDocument;
	private List<Map<String, Object>> clauseList;
	private boolean putValue = false;

	private boolean edit = false;

	private static final Logger LOG = LoggerFactory.getLogger(ContractDocument.class);

	public ContractDocument(String tmplCont, List<Map<String, Object>> clauseList) {
		this.document = Jsoup.parse(tmplCont);
		this.stringBufferDocument = new StringBuffer(tmplCont);
		this.clauseList = clauseList;
	}

	public void putValueToTemplate(Map<String, Object> valueObject, String getValueType) {
		putValue = true;
		String cntrClId = "";
		String datTypCcd = "";
		String cntrClAka = "";
		Object value;
		String dynTmplCont = "";

		for (Map clause : clauseList) {
			cntrClId = (String) clause.get("cntr_cl_id");
			datTypCcd = (String) clause.get("dat_typ_ccd");
			cntrClAka = (String) clause.get("cntr_cl_aka");
			dynTmplCont = (String) clause.get("dyn_tmpl_cont");

			if( GetValueType.CNTR_CL_ID.equals(getValueType) ){
				value = valueObject.get(cntrClId);
			}else if( GetValueType.CNTR_CL_AKA.equals(getValueType) ){
				value = valueObject.get(cntrClAka);
			}else{
				value = valueObject.get(cntrClAka);
			}

			if (value != null) {

				Elements elements = document.getElementsByAttributeValue("name", cntrClId);
				Iterator iterator = valueToIterator(value);
				Object elementValue = null;

				for (Element element : elements) {

					if( iterator.hasNext() ){
						elementValue = iterator.next();
					}else{
						elementValue = value;
					}
					LOG.info("TYPE:" + element.attr("type"));
					if (CheckBox.TYPE.equals(element.attr("type"))
							|| Radio.TYPE.equals(element.attr("type"))) {

						if ("Y".equals((String) elementValue)) {
							element.attr("checked", "");
						} else {
							element.removeAttr("checked");
						}

					} else if (TextDate.TYPE.equals(element.attr("type"))) {
						Elements textList = element.children();
						TextDate textDate = new TextDate(cntrClId, (String) elementValue, datTypCcd);
						TextDate.DateComponent[] dateComponents = textDate.getDateComponentList();

						for (int i = 0; i < textList.size(); i++) {
							Element text = textList.get(i);
							text.val(dateComponents[i].getValue());
						}

					} else if (DynamicTemplate.TYPE.equals(element.attr("type"))) {
						dynTmplCont = getDynamicTemplate(dynTmplCont, cntrClId, elementValue);

						element.text("");
						element.append(dynTmplCont);
						
					} else if (Text.TYPE.equals(element.attr("type"))) {
						String elementVal = (String) elementValue;
						
						if (Text.NUMBER_DATA_TYPE.equals(element.attr(Text.DATA_TYPE))) {
							if (isNumberic(elementVal)) {
								DecimalFormat decFormat = new DecimalFormat("###,###");
								elementValue = decFormat.format(Long.parseLong(elementVal));
								//value = elementValue;
							}
							element.val(elementVal);
						} else {
							element.val(elementVal);
						}

					} else {
						element.val((String) elementValue);
					}
				}
			}
		}
	}

	private String getDynamicTemplate(String DynamicTemplate, String cntrClId, Object dynamicBindingObject) {
		StringWriter writer = new StringWriter();
		try {
			Template freemarkerTempl = new Template(cntrClId, new StringReader(DynamicTemplate));
			freemarkerTempl.process((Object) dynamicBindingObject, writer);
		} catch (IOException | TemplateException e) {
			LOG.error("class : " + this.getClass().toString() + "getDynamicTemplate error : " + e.toString());
			throw new CommonException(this.getClass() + "getDynamicTemplate error : " + e.getMessage() + e.toString());
		}

		return writer.toString();
	}

	private Iterator valueToIterator(Object value) {
		ArrayList arrayList = new ArrayList<>();

		if (value.getClass().equals(ArrayList.class)) {
			arrayList = (ArrayList) value;
		} else {
			arrayList.add(value);
		}
		return arrayList.iterator();
	}
	public void activateEdit() {
		edit = true;
		for (Map clause : clauseList) {
			String cntrClId = (String) clause.get("cntr_cl_id");
			String modPossYn = (String) clause.get("mod_poss_yn");

			if ("N".equals(modPossYn)) {
				Elements elements = document.getElementsByAttributeValue("name", cntrClId);

				for (Element element : elements) {
					if (CheckBox.TYPE.equals(element.attr("type"))
							|| Radio.TYPE.equals(element.attr("type"))) {
						element.attr("disabled", "");
					} else if (TextDate.TYPE.equals(element.attr("type"))) {

						Elements textList = element.children();

						for (int i = 0; i < textList.size(); i++) {
							Element text = textList.get(i);
							text.after("<span>" + text.val() + "</span>");
							text.remove();
						}
					} else if (DynamicTemplate.TYPE.equals(element.attr("type"))) {
						LOG.info("가변템플릿은 따로 처리안함.");
					} else {
						element.after("<span>" + element.val() + "</span>");
						element.remove();
					}
				}
			}
		}
	}
	private void disAbleClauseInDocument() {

		disAbledDocument = Jsoup.parse(document.outerHtml());
		for (Map clause : clauseList) {
			String cntrClId = (String) clause.get("cntr_cl_id");

			Elements elements = disAbledDocument.getElementsByAttributeValue("name", cntrClId);
			//Elements elements = disAbledDocument.getElementsByAttribute("name");
			/*if(elementValue.getClass().equals(String.class)){
				elementValue = EdocStringUtil.toHtml((String)elementValue);
			}*/
			for (Element element : elements) {
				String type = element.attr("type");

				if( Strings.isNullOrEmpty(type) ){
					continue;
				}

				if (CheckBox.TYPE.equals(type)
						|| Radio.TYPE.equals(type)) {
					element.attr("disabled", "");
				} else if (TextDate.TYPE.equals(type)) {

					LOG.info("name:" + element.attr("name"));
					/*element.after("<span>" + element.val() + "</span>");
					element.remove();*/

					Elements textList = element.children();

					for (int i = 0; i < textList.size(); i++) {
						Element text = textList.get(i);
						text.after("<span>" + text.val() + "</span>");
						text.remove();
					}
				} else if (DynamicTemplate.TYPE.equals(type)) {
					LOG.info("동적템플릿은 따로 처리안함.");
				} else if (TextArea.TYPE.equals(type)){

					String value = EdocStringUtil.toHtml((String)element.val());
					element.after("<span>" + value + "</span>");
					element.remove();
				} else if(Text.TYPE.equals( type )) {
					LOG.info("name:" + element.attr("name"));
					element.after("<span>" + element.val() + "</span>");
					element.remove();
				}
			}
		}
	}
	
	public String getDisAbleDocument() {
		disAbleClauseInDocument();
		return disAbledDocument.toString();
	}
	
	public String getDocument() {
		return document.outerHtml();
	}

	private boolean isNumberic(String str) {
		if(Strings.isNullOrEmpty(str)) {
			return false;
		}
		return str.chars().allMatch(Character::isDigit);
	}
	
	public boolean isInputValue(){
		if(putValue && edit){
			String modPossYn = "";
			String cntrClId = "";
			for (Map clause : clauseList) {
				cntrClId = (String) clause.get("cntr_cl_id");
				modPossYn = (String) clause.get("mod_poss_yn");

				if("Y".equals(modPossYn)){
					Elements elements = document.getElementsByAttributeValue("name", cntrClId);

					for(Element element : elements) {

						if (TextDate.TYPE.equals(element.attr("type"))) {
							Elements textList = element.children();

							for (int i = 0; i < textList.size(); i++) {
								Element text = textList.get(i);
								if(Strings.isNullOrEmpty(text.val())){
									return true;
								}
							}

						} else if ( Text.TYPE.equals(element.attr("type"))
								|| TextArea.TYPE.equals(element.attr("type"))) {
							if(Strings.isNullOrEmpty(element.val())){
								return true;
							}

						}
					}
				}
			}
			return false;
		}else{
			throw new CommonException(this.getClass() + "validateRequiredValue error : " + "The validateRequiredValue function must be called after executing the putValueToTemplate function.");
		}
	}
	public String validateRequiredValue(){
		if(!putValue){
			throw new CommonException(this.getClass() + "validateRequiredValue error : " + "The validateRequiredValue function must be called after executing the putValueToTemplate function.");
		}

		String cntrClId = "";

		for (Map clause : clauseList) {
			cntrClId = (String) clause.get("cntr_cl_id");
			Elements elements = document.getElementsByAttributeValue("name", cntrClId);

			for(Element element : elements) {

				if (TextDate.TYPE.equals(element.attr("type"))) {
					Elements textList = element.children();

					for (int i = 0; i < textList.size(); i++) {
						Element text = textList.get(i);
						if(Strings.isNullOrEmpty(text.val())){
							return "";
						}
					}

				} else if ( Text.TYPE.equals(element.attr("type"))
							|| TextArea.TYPE.equals(element.attr("type"))) {
					if(Strings.isNullOrEmpty(element.val())){
						return "";
					}

				}
			}
		}

		return "";

	}
}
