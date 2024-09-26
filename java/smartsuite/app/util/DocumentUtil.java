package smartsuite.app.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aspose.pdf.Document;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.exception.CommonException;

@Service
public class DocumentUtil {
	private static final Logger LOG = LoggerFactory.getLogger(DocumentUtil.class);

	public static String nbsp = "&nbsp;";

	public static boolean checkBrowser(String content){
		int iSt = content.indexOf("<input");
		int eSt = content.indexOf('>', iSt);
		int vSt = content.indexOf("value=", iSt);
		int nSt = content.indexOf("name=", iSt);

		//System.out.println("i_st = " + i_st + ", e_st = " + e_st + ", v_st = " + v_st + ", n_st = " + n_st);
		if(eSt > vSt && eSt > nSt){
			return vSt > nSt;
		}
		return false;
	}

	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.inputbox도 제거. ConvertInsertForm (날짜)
	 * </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 */
	public static String setValueInputDate(String formContent,String inputName,String[] inputValue){
		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  && inputValue.length > count ) {

				int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);     //name="input_c001" 찾기
				int st=sb.toString().indexOf("value=\"", ed);                            //value=" 찾기
				int st2=sb.toString().indexOf("\"", st);
				int ed2 = sb.toString().indexOf("\"", st2+1);

				nowPoint = st;

				if (ed != -1) {
					String inputValueSingle = EdocStringUtil.replaceNull(inputValue[count],"");
					if ( Strings.isNullOrEmpty(inputValueSingle)|| Strings.isNullOrEmpty(inputValueSingle.trim())  ) {
						inputValueSingle = "";
					}

					String selectSrc ="value=\""+inputValueSingle+"\" ";
					count++;
					if(count == 3){
						count = 0;
					}

					sb.replace( st, ed2+1, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					LOG.error("DocumentUtil.setValueInputDate:Loop 한도초과");
					throw new CommonException("DocumentUtil.setValueInputDate(String,String,String[]) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			if (LOG.isErrorEnabled()) LOG.error("DocumentUtil.setValueInputDate error : "+e.toString());
			throw new CommonException("DocumentUtil.setValueInputDate(String,String,String[]) : " + e.getMessage() + e.toString());
		}
		return sb.toString();

	}
	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.inputbox도 제거. ConvertInsertForm
	 * </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 * @throws Exception
	 */
	public static String setValueInput(String formContent,String inputName,String inputValue){
		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		String replaceInputValue = EdocStringUtil.replaceNull(inputValue,"");
		if(Strings.isNullOrEmpty( replaceInputValue ) ){
//			relpace_input_value = "\"\"";
			replaceInputValue = "";
		}

		StringBuffer sb = new StringBuffer(formContent);
		int nowPoint = 0;
		int loopCount = 0;

		while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1 ) {
			int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
			int st=sb.toString().indexOf("value=\"",ed);
			int st2=sb.toString().indexOf("\"", st);
			int ed2 = sb.toString().indexOf("\"", st2 + 1);
			String selectSrc ="value=\""+replaceInputValue+"\"";
			nowPoint = st;

			LOG.info("selectSrc:" + selectSrc);
			LOG.info("st:" + st);
			LOG.info("et:" + ed2);
			LOG.info("inputName:" + inputName);

			sb.replace( st, ed2+1, selectSrc);


			loopCount++;
			if(loopCount >1000) {
				throw new CommonException("DocumentUtil.setValueInput(String, String, String) : Loop 한도초과");
			}
		}
		return sb.toString();
	}

	public static boolean validateContentEmptyTag(String editContent, String inputName){
		StringBuffer sb = new StringBuffer(editContent);
		int nowPoint = 0;
		int loopCount = 0;
		int ed = 0;

		while( sb.toString().indexOf("name=\""+inputName, nowPoint) != -1 ) {
			ed=sb.toString().indexOf("name=\""+inputName, nowPoint);
			nowPoint = ed+2;

			if(sb.toString().lastIndexOf("value=\"\"", ed) > -1){
				return false;
			}

			loopCount++;
			if(loopCount >1000) {
				throw new CommonException("DocumentUtil.setValueInput(String, String) : Loop 한도초과");
			}
		}
		return true;
	}
	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.inputbox도 제거. ConvertInsertForm
	 * </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 * @throws Exception
	 */
	public static String setValueInput(String formContent,String inputName,String[] inputValue){

		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  && inputValue.length > count ) {
				int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
				int st=sb.toString().indexOf("value=\"",ed);
				int st2=sb.toString().indexOf("\"",st);
				int ed2 = sb.toString().indexOf("\"",st2+1);
//				st = (st > st2)? st : st2;

				if (ed != -1) {
					String inputValueSingle = EdocStringUtil.replaceNull(inputValue[count],"");
					if ( Strings.isNullOrEmpty(inputValueSingle) || Strings.isNullOrEmpty(inputValueSingle.trim())  ) {
						inputValueSingle = "";
					}
					String selectSrc ="value=\""+inputValueSingle+"\"";
					count++;
					nowPoint = st;
					sb.replace( st, ed2+1, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueInput error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : " + e.getMessage() + e.toString());
		}
		return sb.toString();

	}
	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.inputbox도 제거. ConvertInsertForm
	 * </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 */
	public static String setValueTextForm(String formContent,String inputName,String inputValue){

		if (Strings.isNullOrEmpty(inputValue)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			//int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  ) {

				nowPoint = sb.toString().indexOf("name=\""+inputName,nowPoint);
				int ed=sb.toString().indexOf("</textarea>",nowPoint);

				int st=sb.toString().indexOf(">",nowPoint);

				if (ed != -1 && st<ed) {
					String selectSrc = EdocStringUtil.replaceNull(inputValue,"");
					//count++;
					nowPoint = st+selectSrc.length()+11;
					sb.replace( st+1, ed, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					throw new CommonException("DocumentUtil.setValueTextForm(String,String,String) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueTextForm error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueTextForm(String,String,String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}


	public static String setValueTextForm(String formContent,String inputName,String[] inputValue){

		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		//form_content = StringUtil.replace(form_content, "\n", "<BR>");

		StringBuffer sb = new StringBuffer(EdocStringUtil.replace(formContent, "\n", "<BR>"));
		try{

			int nowPoint = 0;
			int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name="+inputName,nowPoint) != -1  ) {

				nowPoint = sb.toString().indexOf("name="+inputName,nowPoint);
				int ed=sb.toString().indexOf("</textarea>",nowPoint);
				int st=sb.toString().lastIndexOf(">",ed);

				if (ed != -1 && st<ed) {
					String selectSrc = EdocStringUtil.replaceNull(inputValue[count],"");
					count++;
					nowPoint = st+selectSrc.length()+11;
					sb.replace( st+1, ed, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					throw new CommonException("DocumentUtil.setValueTextForm(String,String,String[]) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueTextForm error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueTextForm(String,String,String[]) : " + e.getMessage() + e.toString());
		}
		return sb.toString();

	}


	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다. ConvertInsertForm
	 * </pre>
	 * @param form_content
	 * @param articleList
	 * @param contractMap
	 * @return
	 * @throws Exception
	 */
	public static String setValueInput(String formContentParam, List<Map<String,Object>> cntrAttList, Map<String,Object> valueMap){
		String formContent = formContentParam;
		for(int i=0;i<cntrAttList.size();i++){
			Map<String,Object> cntrAtt = (Map<String,Object>)cntrAttList.get(i);
			String attNm = (String)cntrAtt.get("cntr_cl_id");
			attNm = attNm.trim();
			if(attNm.indexOf("inp") > -1){
				String dataTyp = (String)cntrAtt.get("dat_typ_ccd");
				String basVal = (String)cntrAtt.get("dflt_val");
				String cntr_cl_aka = (String)cntrAtt.get("cntr_cl_aka");
				String modPosiYn = (String)cntrAtt.get("mod_poss_yn");
				String value = "";
				String[] valueArr = null;

				if(null == valueMap.get(cntr_cl_aka)){
					if("STR".equals(dataTyp)){
						if("Y".equals(modPosiYn)){
							formContent = setValueInput(formContent, attNm, basVal);
						}else{
							formContent = setValueForm(formContent, basVal, attNm);
						}
					}else if("NUMC".equals(dataTyp)){
						if("Y".equals(modPosiYn)){
							formContent = setValueInputNum(formContent, attNm, basVal);
						}else{
							formContent = setValueFormNum(formContent, basVal, attNm);
						}
					}else {
						String[] dateValue = {"", "", ""};
						if(!Strings.isNullOrEmpty(basVal) && basVal.length() == 8){
							if("MM/DD/YYYY".equals(dataTyp)){
								if(CalendarUtil.validDate(value)){
									dateValue[0] = basVal.substring(4, 6);
									dateValue[1] = basVal.substring(6, 8);
									dateValue[2] = basVal.substring(0, 4);
								}else{
									dateValue[0] = basVal.substring(0, 2);
									dateValue[1] = basVal.substring(2, 4);
									dateValue[2] = basVal.substring(4, 8);
								}
							}else if("YYYY_MM_DD".equals(dataTyp) || "YYYY/MM/DD".equals(dataTyp)){
								dateValue[0] = basVal.substring(0, 4);
								dateValue[1] = basVal.substring(4, 6);
								dateValue[2] = basVal.substring(6, 8);
							}
						}
						if("Y".equals(modPosiYn)){
							formContent = setValueInputDate(formContent, attNm, dateValue);
						}else{
							formContent = setValueForm(formContent, dateValue, attNm);
						}
					}
				}else{
					if(valueMap.get(cntr_cl_aka).getClass() == String.class ){
						value = (String)valueMap.get(cntr_cl_aka);
						value = EdocStringUtil.toHtml(value);

						if("STR".equals(dataTyp)){
							if("Y".equals(modPosiYn)){
								formContent = setValueInput(formContent, attNm, value);
							}else{
								formContent = setValueForm(formContent, value, attNm);
							}
						}else if("NUMC".equals(dataTyp)){
							if("Y".equals(modPosiYn)){
								formContent = setValueInputNum(formContent, attNm, value);
							}else{
								formContent = setValueFormNum(formContent, value, attNm);
							}
						}else {
							String[] dateValue = {"", "", ""};
							if(!Strings.isNullOrEmpty(value) && value.length() == 8){
								if("MM/DD/YYYY".equals(dataTyp)){
									if(CalendarUtil.validDate(value)){
										dateValue[0] = value.substring(4, 6);
										dateValue[1] = value.substring(6, 8);
										dateValue[2] = value.substring(0, 4);
									}else{
										dateValue[0] = value.substring(0, 2);
										dateValue[1] = value.substring(2, 4);
										dateValue[2] = value.substring(4, 8);
									}
								}else if("YYYY_MM_DD".equals(dataTyp) || "YYYY/MM/DD".equals(dataTyp)){
									dateValue[0] = value.substring(0, 4);
									dateValue[1] = value.substring(4, 6);
									dateValue[2] = value.substring(6, 8);
								}
							}
							if("Y".equals(modPosiYn)){
								formContent = setValueInputDate(formContent, attNm, dateValue);
							}else{
								formContent = setValueForm(formContent, dateValue, attNm);
							}
						}
					}else if(valueMap.get(cntr_cl_aka).getClass() == BigDecimal.class || valueMap.get(cntr_cl_aka).getClass() == Integer.class || valueMap.get(cntr_cl_aka).getClass() == Double.class || valueMap.get(cntr_cl_aka).getClass() == Long.class){
						value = valueMap.get(cntr_cl_aka) + "";
						if("NUMC".equals(dataTyp)){
							if("Y".equals(modPosiYn)){
								formContent = setValueInputNum(formContent, attNm, value);
							}else{
								formContent = setValueFormNum(formContent, value, attNm);
							}
						}
					}else{
						//String 타입이 아니기때문에, String으로 변환하고 그 값을 다시 array 형태로 변환하는 작업을 하였습니다. ex)금액..
						//valueArr = (String[])valueMap.get(cntr_cl_aka).toString().split("");
						valueArr = (String[])valueMap.get(cntr_cl_aka);
						if("STR".equals(dataTyp)){
							if("Y".equals(modPosiYn)){
								formContent = setValueInput(formContent, attNm, valueArr);
							}else{
								formContent = setValueForm(formContent, valueArr, attNm);
							}
						}else if("NUMC".equals(dataTyp)){
							if("Y".equals(modPosiYn)){
								formContent = setValueInputNum(formContent, attNm, valueArr);
							}else{
								formContent = setValueFormNum(formContent, valueArr, attNm);
							}
						}else {
							if("Y".equals(modPosiYn)){
								String tempDt = valueArr[0];
								String[] dateValue = {"", "", ""};
								if(!Strings.isNullOrEmpty(basVal) && basVal.length() == 8){
									if("MM/DD/YYYY".equals(dataTyp)){
										if(CalendarUtil.validDate(value)){
											dateValue[0] = tempDt.substring(4, 6);
											dateValue[1] = tempDt.substring(6, 8);
											dateValue[2] = tempDt.substring(0, 4);
										}else{
											dateValue[0] = tempDt.substring(0, 2);
											dateValue[1] = tempDt.substring(2, 4);
											dateValue[2] = tempDt.substring(4, 8);
										}
									}else if("YYYY_MM_DD".equals(dataTyp) || "YYYY/MM/DD".equals(dataTyp)){
										dateValue[0] = tempDt.substring(0, 4);
										dateValue[1] = tempDt.substring(4, 6);
										dateValue[2] = tempDt.substring(6, 8);
									}
								}
								formContent = setValueInputDate(formContent, attNm, dateValue);
							}else{
								formContent = setValueForm(formContent, valueArr, attNm);
							}
						}
					}
				}
			}else if(attNm.indexOf("txt") > -1){
				//String data_type = (String)cntrAtt.get("dat_typ_ccd");
				String basVal = (String)cntrAtt.get("dflt_val");
				String cntr_cl_aka = (String)cntrAtt.get("cntr_cl_aka");
				String modPosiYn = (String)cntrAtt.get("mod_poss_yn");
				String value = "";
				String[] valueArr = null;
				if(null == valueMap.get(cntr_cl_aka)){
					if("Y".equals(modPosiYn)){
						formContent = setValueText(formContent, attNm, basVal);
					}else{
						formContent = setValueForm(formContent, basVal, attNm);
					}
				}else{
					if(valueMap.get(cntr_cl_aka).getClass() == String.class ){
						value = (String)valueMap.get(cntr_cl_aka);
						if("Y".equals(modPosiYn)){
							formContent = setValueText(formContent, attNm, value);
						}else{
							formContent = setValueForm(formContent, value, attNm);
						}
					}else{
						valueArr = (String[])valueMap.get(cntr_cl_aka);
						if("Y".equals(modPosiYn)){
							formContent = setValueText(formContent, attNm, valueArr);
						}else{
							formContent = setValueForm(formContent, valueArr, attNm);
						}
					}
				}
			}
		}
		return formContent;
	}
	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.inputbox도 제거. ConvertInsertForm
	 * </pre>
	 * @param src
	 * @param inValue
	 * @param changeStr
	 * @return
	 * @throws Exception
	 */
	public static String setValueForm(String src,String[] inValue,String changeStr){

		if (Strings.isNullOrEmpty(changeStr)) {
			return src;
		}

		String changeTarget = "";
		if ("inp".equals(changeStr.substring(0,3))) 		{ 		changeTarget = "<input";	}
		else if ("tex".equals(changeStr.substring(0,3)))		{		changeTarget = "<textarea";	}
		else if ("sel".equals(changeStr.substring(0,3)))		{		changeTarget = "<select";	}
		else { changeTarget = "<input";  }

		StringBuffer sb = new StringBuffer(src);
		String tknStr = "[["+changeStr.substring(0,1)+"emro"+changeStr.substring(1)+"]]";
		int sp = 0;
		try
		{
			if( "<input".equals(changeTarget)  )		{
				int loopCount = 0;
				String sbString = sb.toString();

				while (sbString.indexOf(changeStr) != -1) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf('>',st);
					int mid=sbString.indexOf(changeStr,st);

					if (st<mid && mid<ed){
						sb.replace( st, ed+1, tknStr);
					}else{
						sp= st+6;
					}

					loopCount++;
					if(loopCount >1000){
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}
			else if("<textarea".equals(changeTarget))		{
				sp=0;
				int loopCount = 0;
				String sbString = sb.toString();

				while (sb.toString().indexOf(changeStr) != -1  ) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf("</textarea>",st);
					int mid=sbString.indexOf(changeStr,st);

					if (st<mid && mid<ed){
						sb.replace( st, ed+11, tknStr); // 11��'<TEXTAREA>' ��湲몄씠
					}else{
						sp= st+7;
					}
					loopCount++;
					if(loopCount > 1000){
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}
			else if("<select".equals(changeTarget))		{
				sp=0;
				int loopCount = 0;
				String sbString = sb.toString();
				while( sb.toString().indexOf(changeStr) != -1 ) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf("</select>",st);
					int mid=sbString.indexOf(changeStr,st);
					if (st<mid && mid<ed){
						sb.replace( st, ed+9, tknStr);
					}else{
						sp= st+7;
					}
					loopCount++;
					if(loopCount >1000){
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}

			int count = 0;
			while( sb.toString().indexOf(tknStr) !=-1){
				int st=sb.toString().indexOf(tknStr);
				if (count == inValue.length){
					count = 0;
				}
				if (Strings.isNullOrEmpty(inValue[count])) {
					sb.replace( st, st+tknStr.length(), "");
				}else{
					String changeValue = inValue[count];
					if( !Strings.isNullOrEmpty(changeValue) && "".equals(changeValue.trim()) ){
						changeValue ="&nbsp;";
					}else{
						changeValue = "<B>"+changeValue+"</B>";
					}
					sb.replace( st,st+tknStr.length(),changeValue);
				}
				count++;
				if(count >1000){
					throw new CommonException("setValueForm Loop 한도초과");
				}
			}
		}

		catch(Exception e){
			LOG.error("DocumentUtil.setValueForm error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueForm(String,String[],String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 *  계약서내용이 채워진 폼으로 변환한다. ConvertInsertForm
	 * </pre>
	 * @param src
	 * @param inValue
	 * @param changeStr
	 * @return
	 * @throws Exception
	 */
	public static String setValueForm(String src,String inValue,String changeStr){

		if (Strings.isNullOrEmpty(changeStr)) {
			return src;
		}

		String changeTarget = "";
		if ("inp".equals(changeStr.substring(0,3))) 		{ 		changeTarget = "<input";	}
		else if ("tex".equals(changeStr.substring(0,3)))		{		changeTarget = "<textarea";	}
		else if ("sel".equals(changeStr.substring(0,3)))		{		changeTarget = "<select";	}
		else { changeTarget = "<input";  }


		StringBuffer sb = new StringBuffer(src);
		String tknStr = "[["+changeStr.substring(0,1)+"emro"+changeStr.substring(1)+"]]";
		int sp = 0;
		try
		{
			if( "<input".equals(changeTarget)  )		{
				int loopCount = 0;
				String sbString = sb.toString();
				while (sbString.indexOf(changeStr) != -1) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf('>',st);
					int mid=sbString.indexOf(changeStr,st);

					if (st<mid && mid<ed){
						sb.replace( st, ed+1, tknStr);
					}else{
						sp= st+6;
					}

					loopCount++;

					if(loopCount >1000){
						LOG.error(sbString);
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}
			else if("<textarea".equals(changeTarget))		{
				sp=0;
				int loopCount = 0;
				String sbString = sb.toString();
				while( sb.toString().indexOf(changeStr) != -1  ) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf("</textarea>",st);
					int mid=sbString.indexOf(changeStr,st);


					if (st<mid && mid<ed){
						sb.replace( st, ed+11, tknStr);
					}else{
						sp= st+7;
					}
					loopCount++;
					if(loopCount > 1000){
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}
			else if("<select".equals(changeTarget))		{
				sp=0;
				int loopCount = 0;
				String sbString = sb.toString();
				while( sb.toString().indexOf(changeStr) != -1 ) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf("</select>",st);
					int mid=sbString.indexOf(changeStr,st);
					if (st<mid && mid<ed){
						sb.replace( st, ed+9, tknStr);
					}else{
						sp= st+7;
					}
					loopCount++;
					if(loopCount >1000){
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}


			int count = 0;
			while( sb.toString().indexOf(tknStr) !=-1 ){
				int st=sb.toString().indexOf(tknStr);
				if (Strings.isNullOrEmpty(inValue)) {
					sb.replace( st, st+tknStr.length(), "");
				}else{
					//String changeValue = in_value;
					String changeValue = inValue.replace("\n", "<br>");
					if( !Strings.isNullOrEmpty(changeValue) && "".equals(changeValue.trim()) ){
						changeValue ="&nbsp;";
					}else{
						changeValue = "<B>"+changeValue+"</B>";
					}
					sb.replace( st,st+tknStr.length(),changeValue);
				}
				count++;
				if(count >1000){
					throw new CommonException("setValueForm Loop 한도초과");
				}
			}
		}
		catch(Exception e){
			LOG.error("DocumentUtil.setValueForm error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueForm(String,String,String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	public static String setValueFormDate(String src,String inValue,String changeStr, String dateType)  throws Exception {

		if (Strings.isNullOrEmpty(changeStr)) {
			return src;
		}

		String changeTarget = "";
		if ("inp".equals(changeStr.substring(0,3))) 		{ 		changeTarget = "<input";	}
		else if ("tex".equals(changeStr.substring(0,3)))		{		changeTarget = "<textarea";	}
		else if ("sel".equals(changeStr.substring(0,3)))		{		changeTarget = "<select";	}
		else { changeTarget = "<input";  }


		StringBuffer sb = new StringBuffer(src);
		String tknStr = "[["+changeStr.substring(0,1)+"emro"+changeStr.substring(1)+"]]";
		int sp = 0;
		try
		{
			if( "<input".equals(changeTarget)  )		{
				int loopCount = 0;
				String sbString = sb.toString();
				while (sbString.indexOf(changeStr) != -1) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf('>',st);
					int mid=sbString.indexOf(changeStr,st);

					if (st<mid && mid<ed){
						sb.replace( st, ed+1, tknStr);
					}else{
						sp= st+6;
					}

					loopCount++;

					if(loopCount >1000){
						LOG.error(sbString);
						throw new CommonException("setValueForm Loop 한도초과");
					}
				}
			}


			int count = 0;
			while( sb.toString().indexOf(tknStr) !=-1 ){
				int st=sb.toString().indexOf(tknStr);
				if (Strings.isNullOrEmpty(inValue)) {
					sb.replace( st, st+tknStr.length(), "");
				}else{
					String changeValue = inValue;
					String[] dateValue = {"", "", ""};

					dateValue[0] = changeValue.substring(0, 4);
					dateValue[1] = changeValue.substring(4, 6);
					dateValue[2] = changeValue.substring(6, 8);

					if("YYYY_MM_DD".equals(dateType)){
						changeValue = dateValue[0] + "년 " + dateValue[1] + "월 " + dateValue[2] + "일";
					} else if("YYYY/MM/DD".equals(dateType)){
						changeValue = dateValue[0] + "/" + dateValue[1] + "/" + dateValue[2];
					} else if("MM/DD/YYYY".equals(dateType)){
						changeValue = dateValue[2] + "/" + dateValue[1] + "/" + dateValue[0];
					}

					if( !Strings.isNullOrEmpty(changeValue) && "".equals(changeValue.trim()) ){
						changeValue ="&nbsp;";
					}else{
						changeValue = "<B>"+changeValue+"</B>";
					}
					sb.replace( st,st+tknStr.length(),changeValue);
				}
				count++;
				if(count >1000){
					throw new CommonException("setValueForm Loop 한도초과");
				}
			}
		}
		catch(Exception e){
			LOG.error("DocumentUtil.setValueFormDate: {}", e.toString());
			throw new CommonException("DocumentUtil.setValueFormDate(String,String,String,String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	/**
	 *  <pre>
	 *  계약서를 작성할때 계약서 조회시 기본 내용들을 채워주는 Method
	 *  </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 * @throws Exception
	 */
	public static String setValueText(String formContent,String inputName,String inputValue){

		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			//int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  ) {

				nowPoint = sb.toString().indexOf("name=\""+inputName,nowPoint);
				int ed=sb.toString().indexOf("</textarea>",nowPoint);
				int st=sb.toString().lastIndexOf(">",ed);

				if (ed != -1 && st<ed) {
					String selectSrc = EdocStringUtil.replaceNull(inputValue,"");
					//count++;
					nowPoint = st+selectSrc.length()+11;
					sb.replace( st+1, ed, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					throw new CommonException("DocumentUtil.setValueText(String,String,String) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueText error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueText(String,String,String) : " + e.getMessage() +  e.toString());
		}
		return sb.toString();

	}

	/**
	 *  <pre>
	 *  계약서를 작성할때 계약서 조회시 기본 내용들을 채워주는 Method
	 *  </pre>
	 * @param formContent
	 * @param inputName
	 * @param input_value
	 * @return
	 * @throws Exception
	 */
	public static String setValueInputNum(String formContent,String inputName,String paramInputValue){

		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		String inputValue = EdocStringUtil.replaceNull(paramInputValue,"");

		if( Strings.isNullOrEmpty(inputValue) || "".equals(inputValue.trim()) ){
//			input_value = "\"\"";
			inputValue = "";
		}

		StringBuffer sb = new StringBuffer(formContent);

		int nowPoint = 0;
		int loopCount = 0;
		while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1 ) {
			int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
			int st=sb.toString().indexOf("value=\"",ed);
			int st2=sb.toString().indexOf("\"",st);
			int ed2=sb.toString().indexOf("\"",st2+1);
			inputValue = EdocStringUtil.toText(inputValue);
			inputValue = EdocStringUtil.formatNum(inputValue.trim());

			String selectSrc ="value=\""+inputValue+"\"";
			nowPoint = st;

			sb.replace( st, ed2+1, selectSrc);

			loopCount++;
			if(loopCount >1000) {
				throw new CommonException("DocumentUtil.setValueInputNum(String,String,String) : Loop 한도초과");
			}
		}
		return sb.toString();
	}

	/**
	 *  <pre>
	 *  계약서를 작성할때 계약서 조회시 기본 내용들을 채워주는 Method
	 *  </pre>
	 * @param formContent
	 * @param inputName
	 * @param inputValue
	 * @return
	 * @throws Exception
	 */
	public static String setValueInputNum(String formContent,String inputName,String[] inputValue){

		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  && inputValue.length > count ) {

				int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
				int st=sb.toString().indexOf("value=\"",ed);
				int st2 = sb.toString().indexOf("\"",st);
				int ed2 = sb.toString().indexOf("\"",st2+1);

				if (ed != -1) {
					String inputValueSingle = EdocStringUtil.replaceNull(inputValue[count],"");
					if ( Strings.isNullOrEmpty(inputValueSingle) || "".equals(inputValueSingle.trim())  ) {
//						input_value_single = "\"\"";
						inputValueSingle = "";
					}else{
						inputValueSingle = EdocStringUtil.formatNum(inputValueSingle);
					}
					String selectSrc ="value=\""+inputValueSingle+"\"";
					count++;
					nowPoint = st;
					sb.replace( st, ed2+1, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					LOG.error("DocumentUtil.setValueInputNum:Loop 한도초과");
					throw new CommonException("Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueInputNum error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueInputNum(String,String,String[]) : " + e.getMessage()+ e.toString());
		}
		return sb.toString();

	}

	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.ConvertFilledFormNum 숫자에 comma를 넣어서.
	 * </pre>
	 * @param src
	 * @param inValue
	 * @param changeStr
	 * @return
	 */
	public static String setValueFormNum(String src,String inValue,String changeStr){

		if (Strings.isNullOrEmpty(changeStr)) {
			return src;
		}

		String changeTarget = "";
		if ("inp".equals(changeStr.substring(0,3))) 			{ 		changeTarget = "<input";	}
		else if ("tex".equals(changeStr.substring(0,3)))		{		changeTarget = "<textarea";	}
		else if ("sel".equals(changeStr.substring(0,3)))		{		changeTarget = "<select";	}
		else { changeTarget = "<input";  }

		StringBuffer sb = new StringBuffer(src);
		String tknStr = "[["+changeStr.substring(0,1)+"emro"+changeStr.substring(1)+"]]";
		int sp = 0;
		try
		{
			if( "<input".equals(changeTarget)  )		{
				int loopCount = 0;
				String sbString = sb.toString();
				while (sbString.indexOf(changeStr) != -1) {
					sbString = sb.toString();
					int st=sbString.indexOf(changeTarget,sp);
					int ed=sbString.indexOf('>',st);
					int mid=sbString.indexOf(changeStr,st);

					if (st<mid && mid<ed){
						sb.replace( st, ed+1, tknStr);
					}else{
						sp= st+6;
					}
					loopCount++;
					if(loopCount >1000){
						throw new CommonException("setValueFormNum Loop 한도초과");
					}
				}
			}

			int count = 0;
			while( sb.toString().indexOf(tknStr) !=-1 ){
				int st=sb.toString().indexOf(tknStr);
				if (Strings.isNullOrEmpty(inValue)) {
					sb.replace( st, st+tknStr.length(), "");
				}else{
					String changeValue = inValue;
					changeValue = EdocStringUtil.formatNum(changeValue);
					if( !Strings.isNullOrEmpty(changeValue) && "".equals(changeValue.trim()) ){
						changeValue ="&nbsp;";
					}else{
						changeValue = "<B>"+changeValue+"</B>";
					}
					sb.replace( st,st+tknStr.length(),changeValue);
				}
				count++;
				if(count >1000){
					throw new CommonException("setValueFormNum Loop 한도초과");
				}
			}
		}
		catch(Exception e){
			LOG.error("DocumentUtil.setValueFormNum error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueFormNum(String,String,String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	/**
	 * <pre>
	 * 계약서내용이 채워진 폼으로 변환한다.ConvertFilledFormNum 숫자에 comma를 넣어서.
	 * </pre>
	 * @param src
	 * @param inValue
	 * @param changeStr
	 * @return
	 * @throws Exception
	 */
	public static String setValueFormNum(String src,String[] inValue,String changeStr){

		if (Strings.isNullOrEmpty(changeStr)) {
			return src;
		}

		String changeTarget = "";
		String changeStrTemp = "";

		if ("inp".equals(changeStr.substring(0,3))) {
			changeTarget = "<input";
			changeStrTemp = changeStr + ">";
		}
		if ("tex".equals(changeStr.substring(0,3))) { changeTarget = "<TEXTAREA";}

		StringBuffer sb = new StringBuffer(src);
		String tknStr = "[["+changeStr.substring(0,1)+"emro"+changeStr.substring(1)+"]]";
		int sp = 0;
		int loopCount = 0;
		try
		{
			while( sb.toString().indexOf(changeStrTemp) != -1 ) {
				int st=sb.toString().indexOf(changeTarget,sp);
				int ed=sb.toString().indexOf(">",st);
				int mid=sb.toString().indexOf(changeStr,st);

				if (st<mid && mid<ed){
					sb.replace( st, ed+1, tknStr);
				}else{
					sp= st+6;
				}
				loopCount++;
				if(loopCount >1000){
					throw new CommonException("setValueFormNum Loop 한도초과");
				}
			}

			int count = 0;
			while( sb.toString().indexOf(tknStr) !=-1 && count<inValue.length){
				int st=sb.toString().indexOf(tknStr);
				if (Strings.isNullOrEmpty(inValue[count])) {

					sb.replace( st, st+tknStr.length(), "");
				}else{

					String changeValue = (Strings.isNullOrEmpty(inValue[count])) ? "" : inValue[count];
					if( !Strings.isNullOrEmpty(changeValue) && "".equals(changeValue.trim()) ) changeValue ="&nbsp;";
					changeValue = EdocStringUtil.formatNum(changeValue);
					changeValue = "<B>"+changeValue+"</B>";
					sb.replace( st,st+tknStr.length(),changeValue);
				}
				count++;
				if(count >1000){
					throw new CommonException("setValueFormNum Loop 한도초과");
				}
			}
		}
		catch(Exception e){
			LOG.error("DocumentUtil.setValueFormNum error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueFormNum(String,String[],String) : " + e.getMessage() + e.toString());
		}
		return sb.toString();
	}

	/* input 창들 제거. value 값만 표시
	 * @param form_content String
	 * @return String
	 */
	public static String removeInputBox(String formContent){

		String inputValue = "";
		StringBuffer sb = new StringBuffer(formContent);
		try{
			int nowPoint = 0;
			int loopCount = 0;
			while( sb.toString().indexOf("<input",nowPoint) != -1  ) {
				// input의 value 추출
				nowPoint = sb.toString().indexOf("<input",nowPoint);

				int valueStartIndex =sb.toString().indexOf("value=\"",nowPoint); //6
				int valueEndIndex =sb.toString().indexOf("\"", valueStartIndex);
				int valueEndIndex2 =sb.toString().indexOf("\"", valueEndIndex + 1);
				// value= 가 6글자
				if(valueEndIndex > valueStartIndex){
					inputValue = sb.toString().substring(valueEndIndex, valueEndIndex2);
					inputValue = "".equals(inputValue.trim()) ? "&nbsp;" : inputValue;
					// 따옴표 제거
					inputValue = EdocStringUtil.replace(inputValue,"\"","");
				}else{
					throw new CommonException("[서식 오류] 서식 수정 필요");
				}

				// input 창 제거, value로 replace
				int inputEndIndex =sb.toString().indexOf(">", valueEndIndex);
				if ( inputEndIndex > nowPoint ) {
					inputValue = "<B>"+inputValue+"</B>";
					sb = sb.replace( nowPoint, inputEndIndex+1, inputValue);
				}else{
					throw new CommonException("[서식 오류] 서식 수정 필요");
				}
				//now_point = valueStartIndex;
				loopCount++;
				if(loopCount >1000){
					throw new CommonException("removeInputBox Loop 제한 초과");
				}
			}// while

		} catch ( Exception e ) {
			LOG.error("DocumentUtil.removeInputBox error : " + e.toString());
			throw new CommonException("DocumnetUtil.removeInputBox(String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	public static String removeTextArea(String formContent){
		String inputValue = "";
		StringBuffer sb = new StringBuffer(formContent);
		try{
			int nowPoint = 0;
			int loopCount = 0;
			while( sb.toString().indexOf("<textarea",nowPoint) != -1  ) {
				// input의 value 추출
				nowPoint = sb.toString().indexOf("<textarea",nowPoint);
				int valueEndIndex =sb.toString().indexOf("</textarea>", nowPoint);
				//int valueStartIndex =sb.toString().lastIndexOf(">",valueEndIndex) + 1;
				int valueStartIndex =sb.toString().indexOf(">",nowPoint) + 1;
				// value= 가 6글자
				if(valueEndIndex >= valueStartIndex){
					inputValue = sb.toString().substring(valueStartIndex, valueEndIndex);
					inputValue = "".equals(inputValue.trim()) ? "&nbsp;" : inputValue;
					// 따옴표 제거
					inputValue = EdocStringUtil.replace(inputValue,"\"","");
				}else{
					throw new CommonException("[서식 오류] 서식 수정 필요");
				}

				// input 창 제거, value로 replace
				int inputEndIndex =sb.toString().indexOf(">", valueEndIndex);
				if ( inputEndIndex > nowPoint ) {
					//input_value = StringUtil.toHtml(input_value);
					inputValue = "<B>"+inputValue+"</B>";
					sb = sb.replace( nowPoint, inputEndIndex+1, inputValue);
				}else{
					throw new CommonException("[서식 오류] 서식 수정 필요");
				}
				loopCount++;
				if(loopCount >1000){
					throw new CommonException("removeInputBox Loop 제한 초과");
				}
			}// while

		} catch ( Exception e ) {
			LOG.error("DocumentUtil.removeTextArea error : " + e.toString());
			throw new CommonException("DocumnetUtil.removeTextArea(String) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	/**
	 * Object를 Map으로 변환하여 리턴하는 함수
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> convertObjectToMap(Object obj) throws Exception{
        //Class<?> pomclass = obj.getClass();
        //pomclass = obj.getClass();
        Method[] methods = obj.getClass().getMethods();

        Map<String, Object> map = Maps.newHashMap();
        for (Method m : methods) {
           if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
              Object value = (Object) m.invoke(obj);
              map.put(m.getName().substring(3).toLowerCase(), (Object) value);
           }
        }
    return map;
	}

	/**
	 * Map을 Object로 변환하여 리턴하는 함수
	 * @param map
	 * @param objClass
	 * @return
	 */
	public static Object convertMapToObject(Map<String,Object> map, Object objClass){
        String keyAttribute = null;
        String setMethodString = "set";
        String methodString = null;
        Iterator<String> itr = map.keySet().iterator();
        while(itr.hasNext()){
            keyAttribute = itr.next();
            methodString = setMethodString+keyAttribute.substring(0,1).toUpperCase()+keyAttribute.substring(1);
            try {
                Method[] methods = objClass.getClass().getDeclaredMethods();
                for(int i=0;i<=methods.length-1;i++){
                    if(methodString.equals(methods[i].getName())){
                    	LOG.error("invoke : {}", methodString);
                        methods[i].invoke(objClass, map.get(keyAttribute));
                    }
                }
            } catch (SecurityException e) {
                LOG.error(e.getMessage());
            } catch (IllegalAccessException e) {
            	LOG.error(e.getMessage());
            } catch (IllegalArgumentException e) {
            	LOG.error(e.getMessage());
            } catch (InvocationTargetException e) {
            	LOG.error(e.getMessage());
            }
        }
        return objClass;
    }

	public static String setValueText(String formContent,String inputName,String[] inputValue){
		// input_name��Null String�대㈃ 吏꾪뻾�섏� �딅뒗��
		if (Strings.isNullOrEmpty(inputName)) {
			return formContent;
		}

		StringBuffer sb = new StringBuffer(formContent);
		try{

			int nowPoint = 0;
			int count = 0 ;
			int loopCount = 0;
			while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1  ) {

				nowPoint = sb.toString().indexOf("name=\""+inputName,nowPoint);
				int ed=sb.toString().indexOf("</TEXTAREA>",nowPoint);
				int st=sb.toString().lastIndexOf(">",ed);

				if (ed != -1 && st<ed) {
					String selectSrc = EdocStringUtil.replaceNull(inputValue[count],"");
					count++;
					nowPoint = st+selectSrc.length()+11;
					sb.replace( st+1, ed, selectSrc);
				}
				loopCount++;
				if(loopCount >1000) {
					throw new CommonException("DocumentUtil.setValueText(String,String,String[]) : Loop 한도초과");
				}
			}
		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueText error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueText(String,String,String[]) : " + e.getMessage() + e.toString());
		}
		return sb.toString();

	}

	public static String correctFilePath(String s) {
		if (Strings.isNullOrEmpty(s))
			return null;

		s = s.replace("\\\\", "/");
		s = s.replace("\\", "/");
		return s;
		//return s.replaceAll("[\\/\\\\]+", "/");
	}

	public void mergePdfUsingAspose(String pdfFullPath, List<Map<String,Object>> pdfPathList){
		Document doc = new Document(pdfFullPath);

		for(Map<String,Object> pdfPathMap : pdfPathList){
			doc.getPages().add(new Document((String)pdfPathMap.get("path")).getPages());
		}

		doc.save();
	}

	/**
	 *
	 * @param form_content
	 * @param articleList
	 * @param contractMap
	 * @return
	 * @throws Exception
	 */
	public static String setValueToHtml(String editContent, String inputName){
		StringBuffer sb = new StringBuffer(editContent);
		String value= "";
		try{

			int nowPoint = 0;
			int loopCount = 0;

			if(inputName.indexOf("inp_") > -1){
				while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1) {
					int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
					int st=sb.toString().indexOf("value=\"",ed);
					int st2=sb.toString().indexOf("\"",st);

					if (ed != -1) {
						value = sb.substring(st, st2);
						value = EdocStringUtil.toHtml(value);
						nowPoint = st;
						sb.replace( st+1, st2, value);
					}
					loopCount++;
					if(loopCount >1000) {
						throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
					}
				}
			}else if(inputName.indexOf("txt_area_") > -1){//text area 인 경우
				while( sb.toString().indexOf("name=\""+inputName,nowPoint) != -1) {
					int ed=sb.toString().indexOf("name=\""+inputName,nowPoint);
					int st=sb.toString().indexOf(">",ed);
					int st2=sb.toString().indexOf("</textarea>",st);

					if (ed != -1) {
						value = sb.substring(st+1, st2);
						value = EdocStringUtil.toHtml(value);
						nowPoint = st;
						sb.replace( st+1, st2, value);
					}
					loopCount++;
					if(loopCount >1000) {
						throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
					}
				}
			}


		} catch ( Exception e ) {
			LOG.error("DocumentUtil.setValueToHtml error : " + e.toString());
			throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : " + e.getMessage() + e.toString());
		}

		return sb.toString();
	}

	public static String searchAndReplace(String content, String replaceValue, String searchWord, String replaceStartWord, String tag) {
		StringBuffer sb = new StringBuffer(content);
		int np = 0;
		int sp = 0;
		int ep = 0;
		int loopCount = 0;
		// <input name="input_c001" value="">
		// <textarea cols="50" name="text_c001" rows="3"></textarea>

		while(sb.indexOf(searchWord,np) > -1) {
			np = sb.indexOf(searchWord, np);

			if("input".equals(tag)) {
				sp = sb.indexOf(replaceStartWord,np);
				sp = sb.indexOf("\"",sp);
				ep = sb.indexOf("/>",sp);
				ep = sb.lastIndexOf("\"",ep);

			}else if("textarea".equals(tag)) {
				sp = sb.indexOf(replaceStartWord,np);
				sp = sb.indexOf(">",sp);
				ep = sb.indexOf("</textarea>",sp);
			}

			if(ep >= sp && ep > -1) {
				sb.replace(sp+1, ep, replaceValue);
			}
			np = ep;
			loopCount++;
			if(loopCount >1000) {
				throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
			}
		}

		return sb.toString();
	}

	/**
	 * html 내용에 특정 부분을 replace 할 때 사용 함 (예시 : 계약서에 계약일자를 서명일자로 변경할 때)
	 *
	 * 예시
	 * <body><div name="[[!@#sgn_dttm!@#]]">2020년 3월 04일</div></body>
	 * 변환후
	 * <body><div name="[[!@#sgn_dttm!@#]]">2020년 9월 30일</div></body>
	 *
	 * @param content html 내용
	 * @param tag 태그명 (예시 : div)
	 * @param name 찾을 name값 (예시 : <div name="[[!@#sgn_dttm]]"></div> 태그 시  [[!@#sgn_dttm]] 이 값을 넘겨주면 됨)
	 * @param replaceValue 교체할 값 (예시 : 2020년 09월 30일)
	 * @return replaceValue로 교체 된 content html 내용
	 * @throws Exception
	 */
	public static String replaceTagContent(String content, String tag, String name, String replaceValue) {
		StringBuffer sb = new StringBuffer(content);

		int np = 0;
		int sp = 0;
		int ep = 0;
		int loopCount = 0;

		while(sb.indexOf(name, np) > -1) {
			np = sb.indexOf(name, np);
			sp = sb.indexOf(">", np) + 1;
			ep = sb.indexOf("</"+tag, sp);

			if(ep >= sp && ep > -1) {
				sb.replace(sp, ep, replaceValue);
			}
			if(loopCount > 1000) {
				throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
			}
			np = ep;
			loopCount++;
		}

		return sb.toString();
	}


	/**
	 * html 내용에 특정 부분을 삭제 할 때 사용 함 (예시 : 계약이행보증보험을 안했을 경우 계약서식에 내용을 삭제)
	 *
	 * 예시
	 * <body><table><tr><td>계약금</td><td>10,000원</td></tr><tr deleteTarget="[[MAINT_BOND_YN]]"><td>계약이행보증</td><td>10%</td></tr></table></body>
	 * 변환후
	 * <body><table><tr><td>계약금</td><td>10,000원</td></tr></table></body>
	 * 주의사항
	 * 삭제할 태그 하위에 동일한 태그명이 들어가면 안됨. <div deleteTarget="[[MAINT_BOND_YN]]"><div></div></div> -> 이런경우는 이렇게 됨 -> </div>
	 * @param content html 내용
	 * @param attrNm 태그 속성 명  예시 : <td deleteTarget="[[PERFOR_BOND_YN]]"></td>  attrNm은 deleteTarget
	 * @return attrVal 태그 속성 값 예시 : [[PERFOR_BOND_YN]]
	 * @throws Exception
	 */
	public static String removeTagContent(String content, String attrNm, String attrVal) {
		StringBuffer sb = new StringBuffer(content);

		int np = 0;
		int sp = 0;
		int ep = 0;
		int tagNameSp = 0;
		int tagNameEp = 0;
		int loopCount = 0;
		String tagName = "";
		String deleteTarget = attrNm +"=\"" + attrVal + "\"";


		while(sb.indexOf(deleteTarget, np) > -1) {
			np = sb.indexOf(deleteTarget, np);

			sp = sb.lastIndexOf("<", np);

			if(tagName.isEmpty()) {
				tagNameSp = sp + 1;
				tagNameEp = sb.indexOf(" ", sp);
				tagName = sb.substring(tagNameSp, tagNameEp); //태그 명 찾기 예시: <tr deleteTarget="[[PERFOR_BOND_YN]]">  ->  tr 추출
			}

			ep = sb.indexOf("</"+tagName, np) + (tagName.length() + 1 + "</".length());

			if(ep >= sp && ep > -1) {
				sb.replace(sp, ep, "");
			}
			if(loopCount > 1000) {
				throw new CommonException("DocumentUtil.setValueInput(String,String,String[]) : Loop 한도초과");
			}
			np = ep;
			loopCount++;
		}

		return sb.toString();
	}

	/**
	 * 계약서식 저장시 ck-editor에서 html태그부분은 붙이지 않기 때문에, 붙이는 작업수행
	 *
	 * @author : daesung lee
	 * @param void
	 * @return list
	 * @Date : 2020. 11. 04
	 * @Method Name : makeHtml
	 */
	public static String makeHtml(String content) {
		String htmlForm = "<html>";
		htmlForm+="<head>";
		htmlForm+="<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\" />";
		htmlForm+="</head>";
		htmlForm+="<body>";
		htmlForm+=content;
		htmlForm+="</body>";
		htmlForm+="</html>";
		return htmlForm;
	}


	public static String removeOunitCd(String oorgCd, List<Map<String,Object>> ounitList ){
		String logicOrgCd = "";
		for(Map<String,Object> ounit : ounitList){
			String ounitCd = (String)ounit.get("ounit_cd");
			int length = ounitCd.length();
			if(ounitCd.equals(oorgCd.substring(0,length))){
				logicOrgCd = oorgCd.replace(ounitCd,"");
				return logicOrgCd;
			}
		}
		return logicOrgCd;
	}

}