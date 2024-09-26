package smartsuite.app.bp.edoc.template.inputElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.exception.CommonException;

public class TextDate extends Input{
	private static final Logger LOG = LoggerFactory.getLogger(TextDate.class);
	private Map dateValueMap = Maps.newHashMap();
	private ArrayList<Text> textList = new ArrayList<Text>();
	public final static String TYPE = "date";

	private DateComponent[] dateComponentList = new DateComponent[3];
	private String format;
	private String[] strFormatDivisionList = new String[3];

	public class DateComponent{
		private String value = "";
		private String name = "";
		private int index = 0;
		private int size = 0;

		public DateComponent(String name, String value, int index, int size){
			this.value = value;
			this.name = name;
			this.index = index;
			this.size = size;
		}
		public int getValueLength(){
			return value.length();
		}
		public String getValue(){
			return value;
		}
		public int getSize(){
			return size;
		}
		public String getName(){
			return name;
		}
	}
	private Comparator<DateComponent> comparator = new Comparator<DateComponent>() {
		@Override
		public int compare(DateComponent o1, DateComponent o2) {

			if(o1.index > o2.index){
				return 1;
			}else if(o1.index == o2.index){
				return 0;
			}else{
				return -1;
			}
		}
	};
	public TextDate(String name, String value, String format){
		super(name,"span");
		setDateFormat(format);
		setDefaultAttribute(value);
		parseDate();
	}
	private void setDefaultAttribute(String value) {
		if(Strings.isNullOrEmpty(value)){
			this.dateValueMap.put("year","");
			this.dateValueMap.put("month","");
			this.dateValueMap.put("day","");
		}else{
			this.dateValueMap = parseDateValue(value);
		}

		Map nameAttribute = new HashMap<>();
		nameAttribute.put("name", super.getName());
		attributeList.add(nameAttribute);

		Map typeAttribute = new HashMap<>();
		typeAttribute.put("type", TYPE);
		attributeList.add(typeAttribute);
	}
	private void parseDate(){
		orderDate();
		setCharFormatDivisionList();
		setDateComponentList();
	}
	private void orderDate(){

		int yearIndex = format.indexOf("Y");
		int monthIndex = format.indexOf("M");
		int dayIndex = format.indexOf("D");

		int yearSize = ((Long)countChar(format, 'Y')).intValue();
		int monthSize = ((Long)countChar(format, 'M')).intValue();
		int daySize = ((Long)countChar(format, 'D')).intValue();

		DateComponent yearDate = new DateComponent("year", (String)dateValueMap.get("year"), yearIndex, yearSize);
		DateComponent monthDate = new DateComponent("month", (String)dateValueMap.get("month"), monthIndex, monthSize);
		DateComponent dayDate = new DateComponent("day", (String)dateValueMap.get("day"), dayIndex, daySize);

		this.dateComponentList[0] = yearDate;
		this.dateComponentList[1] = monthDate;
		this.dateComponentList[2] = dayDate;

		Arrays.sort(dateComponentList, comparator);
	}

	private void setCharFormatDivisionList(){
		String formatDivision = format.replace(String.valueOf('Y'),"");
		formatDivision = formatDivision.replace(String.valueOf('M'),"");
		formatDivision = formatDivision.replace(String.valueOf('D'),"");

		String blank = "";
		if(formatDivision.indexOf(" ") != -1){
			formatDivision = formatDivision.replace(" ","");
			blank = " ";
		}
		char[] charFormatDivisionList = formatDivision.toCharArray();

		for(int i = 0; i < charFormatDivisionList.length; i++){
			strFormatDivisionList[i] = Character.toString(charFormatDivisionList[i]) + blank;
		}

		if(charFormatDivisionList.length == 2){
			strFormatDivisionList[2] = "";
		}
	}

	private void setDateComponentList(){

		for(int i = 0; i < 3; i++){
			Text text = new Text("sub_" + i + "_" + super.getName(), dateComponentList[i].getValue());
			text.setAttribute("maxlength",  Integer.toString(dateComponentList[i].getSize()) );
			text.setAttribute("size", Integer.toString(dateComponentList[i].getSize()) );
			text.setAttribute("value",  dateComponentList[i].getValue());
			text.setAttribute("date_type",  dateComponentList[i].getName());
			text.setBackEndAdditionalWord(strFormatDivisionList[i]);
			textList.add(text);
		}
	}

	public String getTag() {
		String tag = "";
		tag += super.getTag() + ">";

		for (Text text : textList) {
			tag += text.getTag();
		}
		tag += "</" + super.getTagName() + "> ";
		return tag;
	}

	public DateComponent[] getDateComponentList(){
		return this.dateComponentList;
	}
	private void setDateFormat(String dataTypCcd) {
		format = dataTypCcd;
	}
	private Map parseDateValue(String date) {
		String year = "";
		String month = "";
		String day = "";

		if(isNumberic(date)){
			if( date.length() != 8){
				throw new CommonException(this.getClass() + " TextDate error:" + "Date is not 8 length. input date length :" + date.length());
			}
			year = date.substring(0,4);
			month = date.substring(4,6);
			day = date.substring(6,8);
		}else{
			year = date;
			month = date;
			day = date;
		}

		Map dateMap = Maps.newHashMap();
		dateMap.put("year", year);
		dateMap.put("month", month);
		dateMap.put("day", day);

		return dateMap;
	}
	private long countChar(String str, char ch) {

		return str.chars().filter(c -> c == ch).count();
	}
	private boolean isNumberic(String str) {
		return str.chars().allMatch(Character::isDigit);
	}
}
