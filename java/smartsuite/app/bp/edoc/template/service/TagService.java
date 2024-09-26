package smartsuite.app.bp.edoc.template.service;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import smartsuite.app.bp.edoc.template.inputElement.*;
import smartsuite.exception.CommonException;

import java.text.DecimalFormat;
import java.util.Map;

@Service
public class TagService {
	private static final Logger LOG = LoggerFactory.getLogger(TagService.class);
	public String getTag(Map clauseInfo){
		String tag = "";
		String datTypCcd = (String)clauseInfo.get("dat_typ_ccd");
		String name = (String)clauseInfo.get("cntr_cl_id");
		String dfltVal = (String)clauseInfo.get("dflt_val");
		String cntrClNm = (String)clauseInfo.get("cntr_cl_nm");
		String value = "";

		switch(datTypCcd){
			case "STR":
				if(!Strings.isNullOrEmpty(dfltVal)){
					value = dfltVal;
				}else{
					value = cntrClNm;
				}
				Text text = new Text(name, value);
				tag = text.getTag();
				break;
			case "DSCRT":
				if(!Strings.isNullOrEmpty(dfltVal)){
					value = dfltVal;
				}
				TextArea textArea = new TextArea(name, value);
				tag = textArea.getTag();
				break;
			case "NUMC":
				if(!Strings.isNullOrEmpty(dfltVal)){
					value = dfltVal;
				}else{
					value = cntrClNm;
				}
				//1000단위 , 수행
				if(isNumberic(value)){
					DecimalFormat decFormat = new DecimalFormat("###,###");
					value = decFormat.format(Integer.parseInt(value));
				}
				Text textNumber = new Text(name,value);
				textNumber.setAttribute(Text.DATA_TYPE, Text.NUMBER_DATA_TYPE);
				tag = textNumber.getTag();
				break;
			case "YYYY년 MM월 DD일":
			case "YYYY/MM/DD":
			case "MM/DD/YYYY":
			case "YYYY.MM.DD":
			case "YYYY-MM-DD":
				Map dataValueMap = null;
				if(Strings.isNullOrEmpty(dfltVal)){
					value = cntrClNm;
				}else{
					if(!isNumberic(dfltVal)){
						throw new CommonException("It is not in date format.");
					}
					value = dfltVal;
				}
				TextDate textDate = new TextDate(name, value, datTypCcd);
				tag = textDate.getTag();
				break;
			case "TMPL":
				DynamicTemplate DynamicTemplate = new DynamicTemplate(name, cntrClNm);
				tag = DynamicTemplate.getTag();
				break;
			case "CHECK":
				if(!Strings.isNullOrEmpty(dfltVal)){
					value = dfltVal;
				}
				CheckBox checkBox = new CheckBox(name,value);
				tag = checkBox.getTag();
				break;
			case "RADIO":
				if(!Strings.isNullOrEmpty(dfltVal)){
					value = dfltVal;
				}
				Radio radio = new Radio(name,value);
				tag = radio.getTag();
				break;
		}
		return tag;
	}

	private boolean isNumberic(String str) {
		return str.chars().allMatch(Character::isDigit);
	}

}
