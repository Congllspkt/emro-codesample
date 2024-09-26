package smartsuite.app.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import smartsuite.exception.CommonException;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.SecureRandom;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;
/**
 * <PRE>
 * Filename	: EdocStringUtil.java <BR>
 * Function	: <BR>
 * Comment	: String을 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public final class EdocStringUtil {
	private static final Logger LOG = LoggerFactory.getLogger(EdocStringUtil.class);
	public final static String ISO2022 = "iso-2022-kr";
	public final static String KSC5601 = "Shift_JIS";
	public final static String UTF8 = "utf-8";
	private static final int[] VALID_RANGE_START = { 0x1100, 0x3000, 0x3130, 0xAC00, 0x3200, 0x4E00, 0x2E80, 0x2FF0, 0x3400, 0xF900, 0xFE30 };
	private static final int[] VALID_RANGE_END = { 0x11FF, 0x303F, 0x319F, 0xD7A3, 0x33FF, 0x9FFF, 0x2FDF, 0x2FFF, 0x4DBF, 0xFAFF, 0xFE4F };
	
	private EdocStringUtil(){}

	/**
	 * 반각 기준으로 주어진 size 크기가 되도록 input을 지우고 끝을 ... 처리하여 반환한다
	 * 만약 주어진 사이즈보다 작은 경우에는 그대로 반환한다
	 * 만약 null이 들어오면 ""으로 반환한다
	 */
	public static String limit(String input, int size) {
		if (input == null) {
			return "";
		}
		if (input.length() <= size) {
			return input;
		}
		else {
			StringBuffer sb = new StringBuffer(input);
			return sb.delete(size, input.length()).append("...").toString();
		}
	}

	/**
	 * 반각 기준으로 주어진 size 크기가 되도록 input을 지우고 끝을 postfix에서 주어진 문자열로 처리하여 반환한다
	 * 만약 주어진 사이즈보다 작은 경우에는 그대로 반환한다
	 * 만약 null이 들어오면 ""으로 반환한다
	 */
	public static String limit(String input, int size, String postfix) {
		if (input == null) {
			return "";
		}
		if (input.length() <= size) {
			return input;
		}
		else {
			StringBuffer sb = new StringBuffer(input);
			return sb.delete(size, input.length()).append(postfix).toString();
		}
	}
	
	/**
	 * 문자열을 주어진 delim으로 구분하여 Map으로 리턴한다.
	 * <P>[예제] <BR>sepDelim으로는 컴마(',')를 사용하고 equalDelim으로는 컴마('=')를 사용하는 경우
	 * StringUtil.toMap( "page_no=2, src=bonavision", ",", "=" );의 결과 page_no-->2이고 src-->bonavision인 Map이 리턴된다.
	 */
	public static Map toMap(String input, String sepDelim, String equalDelim) {
		Map retMap = new HashMap();
		List sepList = EdocStringUtil.toList( input, sepDelim );
		for( Iterator i=sepList.iterator(); i.hasNext(); ) {
			String element = (String)i.next();
			retMap.put( EdocStringUtil.before( element, equalDelim ), EdocStringUtil.after( element, equalDelim ) );
		}
		return retMap;
	}
	
	/**
	 * input 문자열을 주어진 크기(size)로 쪼개서 반환한다.
	 * 예를 들어 input이 AAAAAAAA 이고 size 3인 경우 { AAA, AAA, AA } 가 반환됨
	 * String[] 형태로 반환한다.
	 */
	public static String[] toArray(String input, int size) {
		if (input == null) {
			return new String[0];
		}

		int count = (int) (input.length() / size) + 1;

		/* 나누어 떨어지는 경우에 count를 1 줄임 (예) 2000/2000 = 1 */
		if (input.length() % size == 0) {
			 count--;
		}

		String[] ret = new String[count];

		/* 구간별로 쪼갬 */
		int length = input.length();
		for (int i = 0; i < count; i++) {
			if ((i + 1) * size <= length) {
				ret[i] = input.substring(i * size, (i + 1) * size);
			} else {
				ret[i] = input.substring(i * size, length);
			}
		}
		return ret;
	}

	/**
	 * 문자열을 주어진 delim으로 구분하여 String의 배열로 리턴한다.
	 */
	public static String[] toArray(String input, String delim){
		List srcList = EdocStringUtil.toList(input, delim);
		String[] ret = new String[srcList.size()];
		return (String[]) srcList.toArray(ret);
	}
	
	/**
	 * 주어진 배열을 delim으로 끊어진 문자열로 반환한다
	 */
	public static String toString(String[] a, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			sb.append(a [i]);
			if (a.length != i + 1) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}

	/**
	 * Map을 주어진 delimiter을 이용하여 문자열로 리턴한다.
	 */
	public static String toString(Map m, String sepDelim, String equalDelim) {
		String ret = "";
		Set keys = m.keySet();
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String key = (String) i.next().toString();
			String value = null;
			if (m.get(key) != null) {
				value = (String)(m.get(key).toString());
			}
			ret = ret + sepDelim + key + equalDelim + value ;
		}
		if ("".equals(ret)) {
			return ret;
		}
		return ret.substring( sepDelim.length() );
	}

	/**
	 * Map을 sepDelim을 ',' equalDelim을 '='로 이용하여 문자열로 리턴한다.
	 */
	public static String toString(Map m) {
		return EdocStringUtil.toString( m, ",", "=" );
	}

	/**
	 * 리스트의 각 원소가 String이라고 가정하고 trim한다.
	 */
	public static Collection trim(Collection input){
		if( input instanceof List ) {
			return EdocStringUtil.trimList((List)input);
		}
		else if( input instanceof Set ) {
			return EdocStringUtil.trimSet((Set)input);
		}		
		return null;
	}

	/**
	 * 리스트의 각 원소가 String이라고 가정하고 trim한다.
	 */
	public static List trimList(List org){
		List ret = new ArrayList();
		for(Iterator i=org.iterator(); i.hasNext(); ) {
			ret.add( ((String)i.next()).trim() );
		}
		return ret;
	}

	/**
	 * 리스트의 각 원소가 String이라고 가정하고 trim한다.
	 */
	public static Set trimSet(Set org){
		Set ret = new HashSet();
		for(Iterator i=org.iterator(); i.hasNext(); ) {
			ret.add( ((String)i.next()).trim() );
		}
		return ret;
	}

	/**
	 * 문자열을 안에서 []안의 숫자를 뽑아서 int로 리턴한다.
	 * 없는 경우에는 -1을 리턴한다.
	 */
	public static int parseIndex( String input ) {
		int start = input.indexOf('[');
		int end = input.indexOf(']');
		if( start == -1 || end == -1 )
			return -1;
		return Integer.parseInt(input.substring(start+1,end).trim());
	}

	/**
	 * 문자열을 안에서 []안의 숫자를 제외한 이름을 뽑아서 int로 리턴한다.
	 */
	public static String parseVarName( String input ) {
		int start = input.indexOf('[');
		int end = input.indexOf(']');
		if( start == -1 || end == -1 )
			return null;
		return input.substring(0,start).trim();
	}

	/**
	 * 한글로 넘어온 파라메터는 이걸로 한번 감싸줘야 한다.
	 * encoding의 예 iso-2022-kr, ks_c_5601-1987 (동일한 인코딩인 KSC5601도 가능함)등
	 */
	public static String decode(String input, String encoding) {
		try{
			if(input==null)
	            return null;
			else
	            return new String(input.getBytes("8859_1"),encoding);
		}catch (UnsupportedEncodingException e){
			return "";
	}
	}

	/**
	 * 한글로 넘어온 파라메터는 이걸로 한번 감싸줘야 한다.
	 * encoding의 예 iso-2022-kr, ks_c_5601-1987 (동일한 인코딩인 KSC5601도 가능함)등
	 */
	public static String decodeUTF8(String input) {
		try{
			if(input==null)
	            return null;
			else
	            return new String(input.getBytes("8859_1"),"utf-8");
		}catch (UnsupportedEncodingException e){
			LOG.error(e.getMessage());
			return "";
		}
	}

	/**
	 * 한글로 넘어온 파라메터는 이걸로 한번 감싸줘야 한다.
	 * 8859_1 Charset으로 인코딩 문자열을 CrossdevContext.get("request_param_encoding_charset")에서 지정된 Charset으로 바꿔준다.
	 */
	public static String decode( String input ) {
		return decode(input, "UTF-8");
	}

	/**
	 * 한글로 넘어온 파라메터는 이걸로 한번 감싸줘야 한다.
	 */
	public static String[] decode( String[] input ) {
		if(input == null) {
			return null;
		}
		String[] ret = new String[input.length];
		for( int i=0; i<input.length; i++) {
			ret[i] = EdocStringUtil.decode(input[i]);
		}
		return ret;
	}

	/**
	 * input value를 8859_1으로 인코딩한다. (decode의 반대)
	 */
	public static String encode(String input, String encoding) {
		try{
			if(input==null)
	            return null;
			else
	            return new String(input.getBytes(encoding),"8859_1");
		}catch (UnsupportedEncodingException e){
			LOG.error(e.getMessage());
			return "";
		}
	}

	/**
	 * input value 로 인코딩한다. decode의 반대 작용
	 */
	public static String encode( String input ) {
		try{
			if(input==null)
	            return null;
			else
	            return new String(input.getBytes("UTF-8"),"8859_1");
		}catch (UnsupportedEncodingException e){
			LOG.error(e.getMessage());
			return "";
		}
	}

	/**
	 * input value로 인코딩한다. decode의 반대 작용
	 */
	public static String[] encode( String[] input ) {
		String[] ret = new String[input.length];
		for( int i=0; i<input.length; i++) {
			ret[i] = EdocStringUtil.encode(input[i]);
		}
		return ret;
	}

	/**
	 * src로 주어지는 문자열을 fromStr과 toStr으로 주어지는 변환 규칙에 따라서 변환한다.
	 * [주의] fromStrings와 toStrings의 배열 크기는 같아야함
	 */
	public static String replace(String src, String[] fromStr, String[] toStr) {
		StringBuffer result = new StringBuffer(src.length());
		int cursorIndex = 0 ;
		//int srcLength = src.length();
		int count = fromStr.length;
		int[] firstIndexes = new int[count];
		/* 무한루프 방지를 위해서 do-while 문이 최대로 반복되는 횟수를 지정한다. */
		int processLimit = 10000;

		/* src가 null로 주어지는 경우 null을 리턴한다 */
		if( src == null ) {
			return null;
		}

		/* firstIndexes를 계산한다 */
		for (int i = 0; i < count; i++ ) {
			firstIndexes[i] = src.indexOf(fromStr[i], cursorIndex);
		}

		do {
			/* firstIndexes 중의 최소를 찾는다 그 index를 sel로 저장한다 */
			int min = Integer.MAX_VALUE;
			int sel = -1;
			for (int i = 0; i < count; i++ ) {
				if (firstIndexes[i] != -1 && firstIndexes[i] < min) {
					min = firstIndexes[i];
					sel = i;
				} else if (firstIndexes[i] == min && fromStr[i].length() > fromStr[sel].length()) {
					sel = i;
				}
			}

			if (sel == -1) {
				/* sel이 변화없이 -1 이면 모든 firstIndexes[i]가  -1 이었으므로 그대로 리턴한다 */
				result.append(src.substring(cursorIndex));
				return result.toString();
			} else {
				result.append(src.substring(cursorIndex, firstIndexes[sel]));
				result.append(toStr[sel]);
				/* min으로 선택된 fromStr의 다음 firstIndexes를 찾아 놓는다 */
				cursorIndex = firstIndexes[sel] + fromStr[sel].length();
				firstIndexes[sel] = src.indexOf(fromStr[sel], cursorIndex);
			}
			processLimit--;

			/* firstIndexes 중에서 어떤것이 현재 cursorIndex 보다 앞쪽을 가리키는지 검사하여 cursorIndex보다 뒷쪽것으로 업데이트한다 */
			for (int i = 0; i < count; i++ ) {
				if (firstIndexes[i] != -1 && firstIndexes[i] < cursorIndex) {
					firstIndexes[i] = src.indexOf(fromStr[i], cursorIndex);
				}
			}
		} while (processLimit > 0);

		return null;
	}

	/**
	 * Text to HTML converter
	 */
	public static String toHtml( String src ) {
		if (src == null) {
			return null;
		}
		String[] fromStr = { "\r", "\n", "<", ">", " ", "\""};
		String[] toStr = { "", "<BR>", "&lt;", "&gt;", "&nbsp;", "&quot;" };
		return EdocStringUtil.replace(src, fromStr, toStr);
	}

	/**
	 * Text to HTML reverse-converter
	 */
	public static String toText( String src ) {
		if (src == null) {
			return null;
		}
		String[] fromStr = { "<BR>", "&lt;", "&gt;", "&nbsp;", "&quot;" };
		String[] toStr = { "\n", "<", ">", " ", "\""};
		return EdocStringUtil.replace(src, fromStr, toStr);
	}

	/**
	 * 주어진 스트링이 HTML인지 구분한다. keywords에 포함한 특정 문자(주로 HTML태그)가 포함되었는가로 구분한다.
	 */
	public static boolean isHtml( String src ) {
		if (src == null) {
			return false;
		}
		String[] keywords = { "img ", "IMG ", "embed ", "EMBED ", "href", "HREF", "br", "BR", "<p>", "<P>", "font", "FONT", "src", "SRC", "&nbsp", "&NBSP" };
		if (src.indexOf('<') != -1 && src.indexOf('>') != -1 && src.indexOf('<') < src.indexOf('>')) {
			for (int i=0 ; i < keywords.length ; i++) {
				if ( src.indexOf(keywords[i]) != -1 ) return true;
			}
		}
		return false;
	}

	/**
	 * 문자열에서  줄바꿈->\r\n, 탭->\t, 따옴표->\", 작은따옴표->\', 역슬래시->\\
	 */
	public static String escape(String src) {
		if (src == null) {
			return null;
		}
		String[] fromStr = { "\n", "\t", "\"", "'", "\\" };
		String[] toStr = { "\\r\\n", "\\t", "\\\"", "\\'", "\\\\" };
		return EdocStringUtil.replace(src, fromStr, toStr);
	}

	/**
	 * 문자열에서  \r\n->줄바꿈, \t->탭, \"->따옴표, \'->작은따옴표, \\->역슬래시
	 */
	public static String unescape(String src) {
		if (src == null) {
			return null;
		}
		String[] fromStr = { "\\r\\n", "\\t", "\\\"", "\\'", "\\\\" };
		String[] toStr = { "\n", "\t", "\"", "'", "\\" };
		return EdocStringUtil.replace(src, fromStr, toStr);
	}

	/**
	 * 문자열을 주어진 size의 문자열이 되도록 나머지를 채운다.
	 * 문자열은 항상 size보다 작아야 한다. 이런 경우는 null을 리턴한다.
	 * [예제] input은 456, size은 6이고 fillStr이 "0"이면 000456을 리턴한다.
	 */
	public static String fill(String input, int size, char ch){
		String ret = "";
		String chStr = String.valueOf(ch);
		if( input.length() > size )
			return null;
		for(int i=0; i<size-input.length(); i++){
			ret = ret + chStr;
		}
		return ret+input;
	}

	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillLeft(boolean src, int size) {
		if(src)
			return fillLeft("true", size);
		else
			return fillLeft("false", size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillLeft(int src, int size) {
		return fillLeft(Integer.toString(src), size);
	}

	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillLeft(long src, int size) {
		return fillLeft(Long.toString(src), size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillLeft(double src, int size) {
		return fillLeft(Double.toString(src), size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillLeft(float src, int size) {
		return fillLeft(Float.toString(src), size);
	}


	/**
	 * 텍스트를 출력하다보면 10글자의 공간을 채우면서 값을 써야하는 경우가 있다.
	 * 이경우 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 * size보다 텍스트의 길이가 크면 그냥 모두 리턴한다.
	 */
	public static String fillLeft(Object src, int size) {
		String ret = null;
		if( src == null )
			ret = "null";
		else
			ret = src.toString();

		if( size <= ret.length() )
			return ret;
		else {
			int count = size - ret.length();
			ret = EdocStringUtil.repeat(" ", count) + ret;
		}
		return ret;
	}


	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillRight(boolean src, int size) {
		if(src)
			return fillRight("true", size);
		else
			return fillRight("false", size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillRight(int src, int size) {
		return fillRight(Integer.toString(src), size);
	}

	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillRight(long src, int size) {
		return fillRight(Long.toString(src), size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillRight(double src, int size) {
		return fillRight(Double.toString(src), size);
	}

   	/**
	 * 전체 길이를 size로 주고 이 함수를 이용하면 오른쪽 정렬된다.
	 */
	public static String fillRight(float src, int size) {
		return fillRight(Float.toString(src), size);
	}

    /**
     * 텍스트를 출력하다보면 10글자의 공간을 채우면서 값을 써야하는 경우가 있다.
     * 이경우 전체 길이를 size로 주고 이 함수를 이용하면 왼쪽 정렬된다.
     * size보다 텍스트의 길이가 크면 그냥 모두 리턴한다.
     */
    public static String fillRight(Object src, int size) {
		String ret = null;
		if( src == null )
			ret = "null";
		else
			ret = src.toString();

		if( size <= ret.length() )
			return ret;
		else {
			int count = size - ret.length();
			ret = ret + EdocStringUtil.repeat(" ", count);
		}
		return ret;
	}

	/**
	 * 주어진 문자열을 count만큼 반복해서 리턴한다.
	 */
	public static String repeat(String input, int count) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<count; i++) {
			sb.append(input);
		}
		return sb.toString();
	}


	/**
	 * 첫번째 구분자 앞의 문자열을 리턴한다. Trim하고 리턴된다.
	 */
	public static String before(String input, String delim) {
		if (input == null) {
			return input;
		}
		if( input.indexOf(delim) == -1 )
			return input;
		return input.substring( 0, input.indexOf(delim) ).trim();
	}


	/**
	 * 두개의 문자열 사이에 있는 문자열을 리턴한다. Trim하고 리턴된다.
	 */
	public static String between(String input, String start, String end) {
		if (input == null) {
			return input;
		}
		if( input.indexOf(start) == -1 || input.indexOf(end) == -1 )
			return null;
		int stIndex = input.indexOf(start)+start.length();
		return input.substring( stIndex, input.indexOf(end, stIndex) ).trim();
	}

	/**
	 * 첫번째 구분자 뒤의 문자열을 리턴한다. Trim하고 리턴된다.
	 */
	public static String after(String input, String delim) {
		if (input == null) {
			return input;
		}
		if( input.indexOf(delim) == -1 )
			return input;
		return input.substring( input.indexOf(delim)+delim.length() ).trim();
	}
	

	/**
	 * 들어온 문자열을 숫자에 맞게 바꾼다.
	 */
	public static String toNumberFormat(String input) {
		if( input.indexOf('.') == -1 )
			return NumberFormat.getInstance().format(Long.valueOf(input));
		else {
			return NumberFormat.getInstance().format(Double.valueOf(input));
		}
	}

	/**
	 * 들어온 문자열을 사업자번호로 포맷하여 출력하는 메쏘드
	 */
	public static String formatSSN(String strSSn) {
		String ret = null;
		if(strSSn == null || strSSn.equals("")){
			ret= "";
		} else if(strSSn.indexOf('-') != -1) {
			ret = strSSn;
		} else if(strSSn.length() == 10) {
			ret = strSSn.substring(0,3)+ "-" + strSSn.substring(3,5) + "-" + strSSn.substring(5);
		} else if(strSSn.length() == 13) {
			ret = strSSn.substring(0,6)+ "-" + strSSn.substring(6);
		} else {
			ret = strSSn;
		}
		return ret;
	}

	/**
	 * 들어온 문자열을 컴마 찍힌 숫자 형태로 포맷하여 출력하는 메쏘드
	 */
	public static String formatNum(String numStr) {
		String formattedStr = "";
		try {
			if(numStr == null || "".equals(numStr)) {
				formattedStr = "";
			}else {
				formattedStr = EdocStringUtil.replace(numStr, "," , "" );
		    	if( numStr.indexOf('.') == -1 )
		    		return NumberFormat.getInstance().format(Long.valueOf(numStr));
		    	else {
		    		return NumberFormat.getInstance().format(Double.valueOf(numStr));
		    	}
			}
	    } catch (Exception e) {
	    	throw new CommonException(e.getMessage());
	    }
    	return formattedStr;
	}
	
	/**
	 * 들어온 문자열을 컴마 찍힌 숫자 형태로 포맷하여 출력하는 메쏘드
	 */
	public static String[] formatNum(String[] numStr) {
		try {
			String[] result = null;
			for(int i=0;i<numStr.length;i++){
				if(numStr[i] == null || "".equals(numStr[i])) {
					numStr[i] = "";
				}else {
					numStr[i] = EdocStringUtil.replace(numStr[i], "," , "" );
			    	if( numStr[i].indexOf('.') == -1 )
			    		numStr[i] = NumberFormat.getInstance().format(Long.valueOf(numStr[i]));
			    	else {
			    		numStr[i] = NumberFormat.getInstance().format(Double.valueOf(numStr[i]));
			    	}
				}
			}
	    } catch (Exception e) {
	    	throw new CommonException(e.getMessage());
	    }
    	return numStr;
	}	


	/**
	 * 들어온 문자열을 숫자(Number)로 리턴하는 메쏘드
	 */
	public static Number parseNum(String numStr) throws ParseException {
		return NumberFormat.getInstance().parse(numStr);
	}

	/**
	 * 들어온 문자열을 정제된 숫자 문자열로 리턴하는 메쏘드
	 */
	public static String cleanNum(String numStr) {
		try {
			return NumberFormat.getInstance().parse(numStr).toString();
		} catch (ParseException e) {
			throw (NumberFormatException) new NumberFormatException("Parsing Failure : " + numStr).initCause(e);
		}
	}

	/**
	 * 들어온 문자열을 통화 형식에 맞게 바꾼다.
	 */
	public static String toCurrencyFormat( String input ) {

		if( input.indexOf('.') == -1 )
			return NumberFormat.getCurrencyInstance().format(Long.valueOf(input));
		else {
			return NumberFormat.getCurrencyInstance().format(Double.valueOf(input));
		}
	}

	/**
	* 입력된 숫자를 한글로 나타어 리턴한다. 이때 주어진 유효자리수만큼 끊는다.
	* @param digitLimit 유효 자리수 [예제] 12345678900에 유효자리가 2이면 123억 4567만
	* @param input  입력 숫자
	*/
	public static String toKoreanCurrencyFormat( String input, int digitLimit ) {
		if( input == null )
			return "자료없음";
		long temp = 0;
		long num = Long.parseLong( input );
		String retStr = "";
		int digitCounter = 0;
		long cho = 10000;
		cho *= 10000;
		cho *= 10000;
		long euk = 10000;
		euk *= 10000;
		long man = 10000;
		if( num == 0 )
			return "0";

		if( num > cho  && digitCounter < digitLimit ) {
			temp = num / cho;
			if( temp != 0 ) {
				num = num - temp * cho;
				digitCounter++;
				retStr = retStr + " " + temp + "조";
			}
		}
		if( num > euk && digitCounter < digitLimit ) {
			temp = num / euk;
			if( temp != 0 ) {
				num = num - temp * euk;
				digitCounter++;
				retStr = retStr + " " + temp + "억";
			}
		}
		if( num > man && digitCounter < digitLimit ) {
			temp = num / man;
			if( temp != 0 ) {
				num = num - temp * man;
				digitCounter++;
				retStr = retStr + " " + temp + "만";
			}
		}
		if( digitCounter < digitLimit ) {
			temp = num;
			if( temp != 0 ) {
				digitCounter++;
				retStr = retStr + " " + temp;
			}
		}
		return retStr.substring(1,retStr.length());
	}

	/**
	 * input으로 주어진 String으로 부터 removings에 주어진 String을 제거한다.
	 * 긴것부터 짧은 순으로 넣어야 효과적이다. 예를 들어 [ "&nbsp;", "&nbsp" ]
	 * remove("GOGO&nbsp HOHO<BO>", [ "&nbsp", "<BO>" ]); --> GOGO  HOHO
	 */
	public static String remove(String input, List removings) {
		StringBuffer sb = new StringBuffer(input);
		for( Iterator i = removings.iterator(); i.hasNext(); ) {
			String removing = (String)i.next();
			while( sb.toString().indexOf(removing) != -1 ) {
				sb.replace( sb.toString().indexOf(removing), sb.toString().indexOf(removing)+removing.length(), " ");
			}
		}
		return sb.toString();
	}

	/**
	 * input으로 주어진 String으로 부터 removing으로 주어진 문자열을 찾아서 inserting으로 주어진 문자열로 변환한다.
	 * 예를 들어 input안에있는 스페이스를 %20으로 바꾸고자 하는 경우 replace( input, "\n", "%20"); 한다.
	 */
	public static String replace(String input, String removing, String inserting) {
		String[] fromStr = new String[1];
		fromStr[0] = removing;

		String[] toStr = new String[1];
		toStr[0] = inserting;

		return EdocStringUtil.replace(input, fromStr, toStr);
	}


	/**
	 * DB에 넣는 경우 Object의 값이 nul일 경우 "" 등으로 바꿔여하는 경우가 많으므로 그와 같은 경우에 사용
	 */
	public static String replaceNull(String str, String replaceStr) {
		if (str == null) {
			return replaceStr;
		}
		return str;
	}

	/**
	 * 값이 0일 경우 "" 등으로 바꿔여하는 경우가 많으므로 그와 같은 경우에 사용
	 */
	public static String replaceIntZero(int str) {
		if (0 == str) {
			return "";
		}
		return String.valueOf(str);
	}

	/**
	 * 값이 0.0일 경우 "" 등으로 바꿔여하는 경우가 많으므로 그와 같은 경우에 사용
	 */
	public static String replaceDoubleZero(double str) {
		if (0.0 == str) {
			return "";
		}
		return Double.toString(str);
	}

    /**
     * null값을 StringNull로 변환
     *
     * @param as_Str	변환대상 문자열
     * @return			변환 결과 문자열
     */
    public static String trimNull(String nStr){
    	String formattedStr = "";
        if(nStr != null)
        	formattedStr = nStr.trim();
        	
        return formattedStr;
    }

	/**
	 * int값을 DB에 넣는 경우 Object의 값이 String일때 콤마를 없애야하는 경우가 많으므로 그와 같은 경우에 사용
	 */
	public static String removeComma(String str) {
		String formattedStr = "";
		if (str != null) {
			formattedStr  = EdocStringUtil.replace(str,",","");
		}
		return formattedStr;
	}

	 /** 계약금액과 공급가액의 자릿수 맞춤시 만들어지는 &nbsp;를 삭제해서 DB에 저장함.
	 */
	public static String removeNbsp(String str) {
		String formattedStr = "";
		if (str != null) {
			formattedStr  = EdocStringUtil.replace(str,"&nbsp;","");
		}
		return formattedStr;
	}

	/**
	 * 오브젝트의 모든 필드를 검사하여 null인 문자열 필드를 ""로 초기화한다.
	 */
	public static void removeNull(Object obj) {
		EdocStringUtil.replaceNull(obj, "");
	}

	/**
	 * 오브젝트의 모든 필드를 검사하여 null인 문자열 필드를 주어진 replaceStr으로 바꾼다.
	 */
	public static void replaceNull(Object obj, Object replaceStr) {
		if (obj == null) {
			return;
		}
		try {
			Field[] fields = obj.getClass().getFields();
			Method[] methods = obj.getClass().getMethods();
			Class stringClass = Class.forName("java.lang.String");
			Class[] noParam = new Class[0];
			Class[] stringParam = new Class[1];
			stringParam[0] = stringClass;


			/* 멤버변수중 PUBLIC인 String을 ""으로 변환 */
			for (int i = 0; i < fields.length; i++) {
				if (Modifier.isPublic(fields[i].getModifiers()) && stringClass.equals(fields[i].getType()) && fields[i].get(obj) == null) {
					fields[i].set(obj, replaceStr);
				}
			}
			/* PUBLIC인 getXXX() 형태의 함수 중 리턴값이 String타입이며 null인 경우 setXXX를 ""을 인자로 주어 호출 */
			for (int i = 0; i < methods.length; i++) {
				if (Modifier.isPublic(methods[i].getModifiers()) && stringClass.equals(methods[i].getReturnType()) && methods[i].getParameterTypes().length == 0 && methods[i].getName().startsWith("get") && methods[i].invoke(obj, noParam) == null) {
					String setMethodName = "s" + methods[i].getName().substring(1);
					Method setMethod = obj.getClass().getMethod(setMethodName, stringParam);
					if (setMethod != null) {
						Object[] paramValue = new Object[1];
						paramValue[0] = replaceStr;
						setMethod.invoke(obj, paramValue);
					}
				}
			}
		} catch (NoSuchMethodException e) {
			if (LOG.isErrorEnabled()) LOG.error("method with given name & param types not found : " + e.getMessage());
		} catch (InvocationTargetException e) {
			if (LOG.isErrorEnabled()) LOG.error("exception occured while invoking method : " + e.getMessage());
		} catch (ClassNotFoundException e) {
			if (LOG.isErrorEnabled()) LOG.error("java.lang.String not found : " + e.getMessage());
		} catch (IllegalAccessException e) {
			if (LOG.isErrorEnabled()) LOG.error("Can not access private or protected variables : " + e.getMessage());
		}
	}
	
	/**
	 * 문자열을 주어진 delim으로 구분하여 List로 리턴한다.
	 */
	public static List toList(String input, String delim){
		List ret = new ArrayList();
		StringTokenizer st = new StringTokenizer(input, delim);
		while (st.hasMoreTokens()) {
			ret.add(st.nextToken().trim());
		}
		return ret;
	}	

 	/**
 	 * decode가 필요한 경우에 decode를 수행한다.
 	 * 한글인지 검사하는 isKoreanCharacter()를 이용하여 검사
 	 */
 	public static String smartDecode(String input, String encoding) {
 		if (input == null) {
 			return input;
 		}
		String decoded = EdocStringUtil.decode(input, encoding);
		if (isKoreanString(decoded)) {
			return decoded;
		}
		return input;
	}

	/**
	 * 한글 영역의 Unicode에 속하는지 반환한다.
	 */
	public static boolean isKoreanCharacter(char c) {
		return 0xAC00 <= (int) c && 0xD7A3 >= (int) c;
	}

	/**
	 * 인코딩이 필요한 문자열인지 여부를 판단하기 위해서 사용되는 메쏘드로서
	 * 한글이 나타나는 경우 true로 반환하고 그렇지 않은 경우에는 false를 반환
	 */
	public static boolean isKoreanString(String input) {
		int korCount = 0;
		int inputLen = input.length();
		int checkLimit;
		if (inputLen < 200) {
			checkLimit = inputLen;
		} else {
			checkLimit = inputLen / 10 + 180;
		}
		int korLimit = inputLen / 10 + 1;

		for (int i = 0; i < checkLimit; i ++) {
			if (EdocStringUtil.isKoreanCharacter(input.charAt(i))) {
				korCount++;
				if (korCount > korLimit) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 한글, 문자, 영어 영역의 Unicode에 속하는지 반환한다.
	 */
	private static boolean isNormalCharacter(char c) {
		if (c == '.' || c == ',' || c == '<' || c == '&' || c == '@' || c == '>' || c == '[' || c == ']' || c == '?' || c == ';' || c == ':' || c == '^' || c == '(' || c == ')' || c == '*' || c == '%' || c == '-' || c == '=' || c == '/' || c == '+' || c == '$' || c == '#' || c == '~' || c == "\"".charAt(0) || c == "'".charAt(0) || Character.isWhitespace(c) || Character.isDigit(c) || Character.isSpaceChar(c) || Character.isISOControl(c)) {
			return true;
		}
		for (int i = 0; i < VALID_RANGE_START.length; i++) {
			if (VALID_RANGE_START[i] <= (int) c && VALID_RANGE_END[i] >= (int) c) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 인코딩이 필요한 문자열인지 여부를 판단하기 위해서 사용되는 메쏘드로서
	 * 대부분의 문자열이 한글, 영어, 특수 문자로 구성되어 있는 경우 true 그렇지 않고
	 * 이상한 아스키 코드 또는 ???? 의 연속으로 되어있는 경우 false를 반환한다
 	 * (규칙1) normal 카운트와 question 카운트를 세어서 abnormal이 10% 이상이면 false
 	 * (규칙2) abnormal이 0이상 10%이하 이고 questionCount > normalCount / 2 이면 false
 	 * (규칙3) 그렇지 않은 경우(abnormal 0이상 2이하이고  normalCount 의 1/2 이상이 ?가 아닌 경우) true
	 */
	public static boolean isNormalString(String input) {
 		if (input == null) {
 			return true;
 		}
 		int abnormalCount = 0;
 		int questionCount = 0;
 		int normalCount = 0;
 		int abnormalLimit = input.length() / 10 + 3;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (EdocStringUtil.isNormalCharacter(c)) {
				normalCount++;
			} else {
				abnormalCount++;
				if (abnormalCount >= abnormalLimit) {
					return false;
				}
			}
			if (c == '?') {
				questionCount++;
			}
		}
		return questionCount <= normalCount / 2;
	}

	/**
	* 이메일 주소의 유효성 검증
	*/
	public static boolean checkEmail(String email)
	{

		if( email == null )
			return false;
		else if( email.indexOf('@') <= 0) {
			return false;
		}

		return true;
	}

	/**
	* java 포맷으로 변경. hello_world_all -> HelloWorldAll or helloWorldAll
	*/
	public static String toJava(String str, boolean firstUpper)
	{
		String temp;
		boolean upperFlag = firstUpper;
		StringBuffer buff= new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			temp = str.substring(i,i+1);
			if ("_".equals(temp))
			{
				upperFlag = true;
			}else{
				if(upperFlag){
					temp = temp.toUpperCase(LocaleContextHolder.getLocale());
					upperFlag = false;
				}
				buff.append(temp);
			}
		}
		return buff.toString();
	}

	/**
	* 사업자등록번호, 우편번호, 법인등록번호에 '-' 붙이기(예:1234567890 -> 123-45-67890)
	* 사용시 trim() 붙여서 보낼것..
	*/
	public static String toBizNo(String str)
	{
		String temp;
		//StringBuffer buff= new StringBuffer();

		if(str.length() == 10) {
			//사업자등록번호 자르기
			temp = str.substring(0,3)+"-"+str.substring(3,5)+"-"+str.substring(5,10);
			return temp;
		} else if(str.length() == 6) {
			//우편번호 자르기
			temp = str.substring(0,3)+"-"+str.substring(3,6);
			return temp;
		}else if(str.length() == 13) {
			//법인등록번호 자르기
			temp = str.substring(0,6);
			temp = temp + "-";
			temp = temp + str.substring(6,13);
			return temp;
		} else {
			return str;
		}//end else
	}//end toBizNo

	/**
	* 0이 들어오면 ""를 return
	*/
	public static String formatNumNoZero(String str)
	{
		String temp = formatNum(str);
		if("0".equals(temp)){
			temp = "";
		}
		return temp;
	}

	public static String replaceAll( String source, String toReplace, String replacement )

	{
		String formattedSource = "";
        int idx = source.lastIndexOf( toReplace );
        
        if ( idx != -1 )
        {
              StringBuffer ret = new StringBuffer( source );
              ret.replace( idx, idx+toReplace.length(), replacement );
              do {
            	  idx=source.lastIndexOf(toReplace, idx-1);
            	  ret.replace( idx, idx+toReplace.length(), replacement );
              }while(idx != -1);
              formattedSource = ret.toString();
        }
        
        return formattedSource;
	}

	/**
	 *  byte의 배열을 String으로 변환한다.
	 */
	public static String makeByteArrayToString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte curByte = bytes[i];
			sb.append(convertByteToChar((byte) ((curByte & 0xF0) >> 4))).append(convertByteToChar((byte) (curByte & 0x0F)));
		}
		return sb.toString();
	}

	/**
	 *  String을 byte 배열로 변환한다.
	 */
	public static byte[] makeStringToByteArray(String byteStr) throws RuntimeException {		
		if (byteStr.length() % 2 == 1) {
			throw new CommonException("길이가 유효하지 않습니다.");
		}
		byte[] retBytes = new byte[byteStr.length() / 2];
		for (int i = 0; i < byteStr.length(); i+=2) {
			retBytes[i / 2] = convertCharToByte(byteStr.charAt(i), byteStr.charAt(i + 1));
		}
		return retBytes;
	}

	/**
	* 1자리의 byte 값을 넣으면 char로 변환한다. 예를 들어 byte 1을 넣으면 char 1이 나오고
	* byte 10을 넣으면 char A가 나온다.
	*/
	public static char convertByteToChar(byte input) {
		if ((int) input < 10) {
			return (char) (input + '0');
		} else {
			return (char) ('A' + (int) input - 10);
		}
	}

	/**
	* 16진법을 기준으로 "AB"를 넣으면 byte 값으로 변화하여 반환한다.
	*/
	public static byte convertCharToByte(char firstChar, char secondChar) {
		byte firstValue = (byte) convertCharToInt(firstChar);
		byte secondValue = (byte) convertCharToInt(secondChar);
		return (byte) ((byte) (firstValue << 4) | (byte) secondValue);
	}

	/**
	* 16진법을 기준으로 "AB"를 넣으면 byte 값으로 변화하여 반환한다.
	*/
	public static int convertCharToInt(char input) {
		if ((int) input >= (int) 'A') {
			return (int) input - (int) 'A' + 10;
		} else {
			return (int) input - (int) '0';
		}
	}

	/**
	 * getFileSizeFormat()
	 * 
	 * @ 파일 사이즈 표시 TB, GB, MB, KB
	 * @ return String
	 */	
	public static String getFileSizeFormat(String totalSize){
		
		double fileSize = Double.parseDouble(totalSize);
		String fileSizeNm = "";
		
		DecimalFormat f2 = new DecimalFormat("0.00"); 
		if(1023 < fileSize){
			fileSize = fileSize / 1024;
			fileSizeNm = f2.format(fileSize) + "KB";
		}else{
			fileSizeNm = fileSize + "byte";
		}
		if(1023 < fileSize){
			fileSize = fileSize / 1024;
			fileSizeNm = f2.format(fileSize) + "MB";
		}
		if(1023 < fileSize){
			fileSize = fileSize /1024;
			fileSizeNm = f2.format(fileSize) + "GB";
		} 
		if(1023 < fileSize){
			fileSize = fileSize /1024;
			fileSizeNm = f2.format(fileSize) + "TB";
		} 	
		
		return fileSizeNm;
	} 
	
    /**
     *123456같은 숫자를 '일십이만삼천사백오십육'으로 변환 해서 반환
     *@param String amount 20글자 이내(천경)의 문자형 숫자
     *@parsm String suffix 변환 된 문자 뒤에 붙일 단위
     *@return String
     */
    public static String parseNumToKor(String amountParam, String suffix) {
    	String[] checkDecimal = amountParam.split("[.]");
    	String amount = amountParam;
        
        if(checkDecimal.length == 2) { //소수점이 있다면, 소수점은 버림
        	amount = checkDecimal[0];
        }
        if (isEmpty(amount) || amount.length() > 20 || ! isNumeric(amount)) {
            return "";
        }
        if ("0".equals(amount)) {
            return "영" + suffix;
        }

        ChoiceFormat cfUnit = new ChoiceFormat("0.0#|1.0#일|2.0#이|3.0#삼|4.0#사|5.0#오|6.0#육|7.0#칠|8.0#팔|9.0#구|10.0#십");
        ChoiceFormat unit   = new ChoiceFormat("0.0#천|1.0#백|2.0#십|3.0#경|4.0#천|5.0#백|" + 
                                               "6.0#십|7.0#조|8.0#천|9.0#백|10.0#십|" + 
                                               "11.0#억|12.0#천|13.0#백|14.0#십|15.0#만|" +
                                               "16.0#천|17.0#백|18.0#십|19.0#");

        String parsedStr = "";
        String primary = "";
        //String secondary = "";

        int loop = amount.length();
        int pos = 20 - loop;
        int count = 0;
        int gap = 4 - (loop % 4) + 1;

        for (int i = 0; i < loop; i++) {
        	primary = cfUnit.format(new Double(convertToStrTrim(amount.substring(i, i + 1))).doubleValue());
            //primary = cfUnit.format(convertToDbl(amount.substring(i, i + 1)));
            //secondary = unit.format((double)i + pos);
            if (isEmpty(primary)) {
                if ((i + gap) % 4 == 0 && count > 0) {
                    parsedStr += unit.format((double)i + pos);
                    count = 0;
                }
            } else {
                parsedStr += primary + unit.format((double)i + pos);
                count++;
                if ((i + gap) % 4 == 0 && count > 0)
                    count = 0;
            }
        }
        return parsedStr + suffix;
    }
    
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }    
    
    /**
     *0-9숫자만으로 이루어진 문자열인가를 검증 한다.
     *@author neoburi-inkuk( neoburi at neoburi.com );
     *@param String 검증 할 문자
     *@return boolean true : 숫자, false : 숫자 이외의 문자가 포함되어 있음
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[^0-9]");
        return ! pattern.matcher(str).find();
    }
    
    /*
    For byte    , from                 -128 to                 127, inclusive
    For short   , from               -32768 to               32767, inclusive
    For int     , from          -2147483648 to          2147483647, inclusive
    For long    , from -9223372036854775808 to 9223372036854775807, inclusive
    For char    , from                    0 to               65535, from '\u0000' to '\uffff' inclusive, that is
    */
   public static double convertToDbl(Double source) {
       return convertToDbl(source, 0);
   }    
   
   public static double convertToDbl(Double source, int defaultValue) {
       double result = defaultValue;
       if (source != null) {
           result = source.doubleValue();
       }
       return result;
   }  
   
   public static double convertToDbl(String source) {
       return convertToDbl(source, 0.0D);
   }   
   
   public static double convertToDbl(String source, double defaultValue) {
       double result = defaultValue;
       try {
           String convertedSource = source.trim();
           for (int ii = 0; ii < safeLength(source); ii++) {
               if (source.charAt(0) == '0' && !source.startsWith("0.")) {
            	   convertedSource = source.substring(1);
            	   if(isEmpty(convertedSource)) continue;
               } else {
                   break;
               }
           }
           
           result = new Double(convertToStrTrim(convertedSource)).doubleValue();
           
       } catch (NumberFormatException e) {
    	   throw new CommonException(e.getMessage());
       } catch (Exception e) {
    	   throw new CommonException(e.getMessage());
       }
       return result;
   }
   
   public static int safeLength(String source) {
       if (source == null) {
           return 0;
       } else {
           return source.length();
       }
   }   
   
   public static String convertToStrTrim(String source) {
       String result = null;
       if (source != null) {
           result = source.trim();
       }
       return result;
   }

   public static String convertToStrTrim(String source, String defaultValue) {
       String result = null;

       result = convertToStrTrim(source);
       if (result == null) {
           result = defaultValue;
       }

       //result = trimToUni(result); // JRun에서 Servlet 구동시 요청값(조회조건)에 대해 인코딩 변환 필요, WebLogic에서는 불필요

       return result;
   } 
   
   	public static String parseTag(String content, Map replaceMap) {

		/* replaceMap이 null인 경우 그대로 리턴함 */
		if (replaceMap == null) {
			return content;
		}

		String[] from = new String[replaceMap.size()];
		String[] to = new String[replaceMap.size()];
		int count = 0;
		for (Iterator i = replaceMap.entrySet().iterator(); i.hasNext(); count++) {
			Map.Entry entry = (Map.Entry) i.next();
			from[count] = "<%=" + entry.getKey().toString() + "%>";
			//from[count] = "<!--DOCLET:" + entry.getKey().toString() + "-->";
			if (entry.getValue() == null) {
				to[count] = "null";
			}
			else {
				to[count] = entry.getValue().toString();
			}
		}
		return EdocStringUtil.replace(content, from, to);
 	}
	/**
	 * 주어진 문자를 이용하여 주어진 크기를 갖는 String을 만든다.
	 * 'N' : Number
	 * 'L' : Lower
	 * 'U' : Upper
	 * 'S' : Symbol
	 * ex) NL : 숫자와 소문자로 구성된 random String 출력
	 */
	public static String getRandomString(int size, String format) {
		String ret = "";
		String num = "0123456789";
		String low = "abcdefghijklmnopqrstuvwxyz";
		String upp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String sym = "!@#$%^&*()";
		String chars = "";
		if(format.indexOf('N') > -1){
			chars = num; 
		}
		if(format.indexOf('L') > -1){
			chars = chars + low; 
		}
		if(format.indexOf('U') > -1){
			chars = chars + upp; 
		}
		if(format.indexOf('S') > -1){
			chars = chars + sym; 
		}
		
		char[] ch = new char[chars.length()];
		double span = 1 / (double)chars.length();
		double[] spanStart = new double[chars.length()];
		double[] spanEnd = new double[chars.length()];
		
		for(int i=0; i<chars.length(); i++) {
			ch[i] = chars.charAt(i);
			spanStart[i] = span * (double)i;
			if( i == chars.length()-1 )
				spanEnd[i] = 1;
			else 
				spanEnd[i] = span * (double)(i+1);
		}
	
		while( ret.length() < size ) {
			double rdNum = new SecureRandom().nextDouble();
			ret = ret + ch[getRangeIndex(spanStart, spanEnd, rdNum)];
		}
		
		return ret;
	}
	
	/**
	 * 주어진 숫자가 주어진 구간의 몇번째 index에 해당하는지 리턴
	 * 해당이 없는 경우에는 -1 리턴
	 */
	private static int getRangeIndex(double[] spanStart, double[] spanEnd, double point) {
		for(int i=0; i<spanStart.length; i++ ){
			if( spanStart[i] <= point && spanEnd[i] > point )
				return i;
		}
		return -1;
	}
	
	/**
	 * request에 리스트 항목을 추출한다.
	 * 
	 * @author : lee daesung
	 * @param param the param
	 * @return the list
	 * @Date : 2020. 3. 11
	 * @Method Name : getRequestParamToList
	 */
	public static List<Map<String,Object>> getRequestParamToList(HttpServletRequest request, String basisField){
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		ArrayList<String> parameters = new ArrayList<String>();
		HashMap<String,String[]> fields = new HashMap<String,String[]>();
		int basisSize = 0;
		if(request.getParameterValues(basisField) != null)//NOPMD
			basisSize = request.getParameterValues(basisField).length;
		
		Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			String name = (String) parameterNames.nextElement();
			String[] value = request.getParameterValues(name);
			if(value.length == basisSize)//NOPMD
				fields.put(name, value);
		}
		
		parameters.addAll(fields.keySet());
		int length = parameters.size();
		Map o = null;
		String name = null;
		for (int i = 0; i < basisSize; i++) {
			o = new HashMap();//NOPMD
			for (int j=0; j < length ; j++) {
				name = parameters.get(j);
				o.put(name, fields.get(name)[i]);
			}
			result.add(o);
		}
		return result;
	}
	
	/**
	 * request에 Map으로 항목을 추출한다.
	 * 
	 * 예시: 이런 경우  <form>
	 * 		<input name="dn" value="test"/>
	 *      <input name="ssn" value="1234567890"/>
	 *      <input name="cntr_no" value="CNTR20200311"/>
	 *     </form> 
	 *
	 * @author : lee daesung
	 * @param param the param
	 * @return the list
	 * @Date : 2020. 3. 11
	 * @Method Name : getRequestParamToLMap
	 */
	public static Map<String,Object> getRequestParamToMap(HttpServletRequest request){
		Map params = new HashMap();
		Enumeration<String> names = request.getParameterNames();
		
		while(names.hasMoreElements()){
			String name = (String) names.nextElement();
			if(request.getParameterValues(name).length > 1){
				params.put(name, request.getParameterValues(name));
			}else{
				params.put (name, request.getParameter(name)); 
			}
		}
		return params;
	}
	
	public static String getTodayDate(String format) {
		String date = "";
		SimpleDateFormat sFormat = new SimpleDateFormat(format, Locale.getDefault());
		Date today = new Date();
		date = sFormat.format(today);
		return date;
	}
	
	public static String getUrlParamToMap(Map<String,Object> param) {
		String urlParam = ""; //cntr_no=CNTR2002003&cntr_rev=2
		
		Iterator i = param.keySet().iterator();
		int cnt = 0;
		while(i.hasNext()) {
			String key = (String)i.next();
			Object value = (Object)param.get(key);
			
			if(cnt == 0) {
				urlParam += key+"="+value;
			}else{
				urlParam += "&"+key+"="+value;
			}
			cnt++;
		}
		LOG.info("urlParam:" +urlParam);
		return urlParam;
	}
	
	/**
	 * convertNumToKr()
	 * 
	 * @ 금액을 한글로 변환시켜주는 method
	 * @ return String
	 */
	public static String convertNumToKr(long amount){
		
		String[] kr = {"천","백","십","억","천","백","십","만","천"};
		String[] numTx = {"영","일","이","삼","사","오","육","칠","팔","구"};
		String result = "";
	
		long div = 100000000L;
		long amt = amount / 1000L;
		boolean first = true;
		
		for (int i = 0; i < 9; i++) {
			long num = amt / div;
			if (num > 0) {
				result += numTx[(int) num]+kr[i];
				amt -= num * div;
				first = false;
			} else if (!first && (i == 3 || i == 7)) {
				result += kr[i];
			}
			div = div / 10L;
		}
		
		return result;
	}
	
	private static int convertCharToNum(char c) {
		return Character.getNumericValue(c); 	
	}
	
	public static String convertNumToEnglish(int inputValue) {
		String result = "";
		String zero = "zero";
		String[] digitType1= {"","one","two","three","four","five","six","seven","eight","nine"};
		String[] digitType2 = {"","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen"};
		String[] tenMultiple = {"","ten","twenty","thirty","forty","fifty","sixty","seventy","eighty","ninety"};
		String[] numUnits = {"hundred","thousand","million"};
		String numUnit = "";
		int digitValue = 0;
		
		
		if(inputValue < 10) {	//값이 0~9 사이
			if(inputValue == 0) {
				result = zero;
			}else {
				result = digitType1[inputValue];
			}
			return result;
		}else if(10 < inputValue && inputValue < 20) { //값이 11~19 사이
			digitValue = inputValue % 10;
			result = digitType2[digitValue];
			return result;
		}else {
			//몇째 자리까지 있는지 확인한다.
			int checkDigit = inputValue / 10;
			int digitCnt = 1; //입력한 숫자 자리 수 확인하는 변수
			while(checkDigit > 0) {
				checkDigit = checkDigit / 10;
				digitCnt++;
			}
			
			String sInVal = Integer.toString(inputValue);	//숫자를 문자로 변경
			String blank = "";
			int currentNum = 0; //현재 자릿수에 숫자
			int nextNum = 0; 	//내 앞에 자릿수에 숫자
			int preNum = 0; 	//내 뒤에 자릿수에 숫자
			boolean isExistNextNum = false;
			boolean isExistPreNum = false;
			//sInVal.charAt 0번째는 맨앞에 문자이다. 예를들어 123457 이라면, charAt(0)값은 숫자 1임
			for(int i = 1; i <= digitCnt; i++) {
				currentNum = convertCharToNum( sInVal.charAt( digitCnt-i ) );
				if(i < digitCnt) {
					isExistNextNum = true;
					nextNum = convertCharToNum( sInVal.charAt( digitCnt-(i+1) ) );
				}else {
					isExistNextNum = false;
				}
				
				if(i != 1) {
					isExistPreNum = true;
					preNum = convertCharToNum( sInVal.charAt( digitCnt-(i-1) ) );
				}else {
					isExistPreNum = false;
				}
				
				if(i == 1) {
					if( isExistNextNum 	//내앞에 자릿수가 있는지 확인  
							&&  nextNum != 1 ) {	//내앞에 자릿수가 1인지 확인 1이면 내앞에 자릿수가 처리함. 예를들어 111인 경우는 one hundred eleven으로 표시 
						result = digitType1[ currentNum ]; //첫번째 자리수 확정
					}
				}else if(i == 3 || i == 4 || i == 6 || i == 7 || i == 9) {
					
					if(i == 3 || i == 6 || i == 9 ) {
						numUnit = numUnits[0]; //3,6,9자리에서는 hundred를 붙인다.
					}else if(i == 4) {
						numUnit = numUnits[1];	//4자리에서는 thousand를 붙인다.
					}else if(i == 7) {
						numUnit = numUnits[2]; //7자리에서는 million를 붙인다.
					}
					
					blank = "";
					
					if(currentNum != 0) {
						blank = " ";
					}
					if(i == 3 || i ==6 || i == 9) {
						if(currentNum == 0) {
							continue;
						}else {
							result = digitType1[currentNum ] + blank + numUnit +  " " + result;
						}
					}else if(i == 7) {
						
						if(isExistNextNum) {
							if(nextNum == 1 ) {
								result = numUnit +  blank + result;
							}else {
								result = digitType1[currentNum ] + blank + numUnit +  " " + result;
							}
						}else {
							result = digitType1[currentNum ] + blank + numUnit +  " " + result;
						}
					}else {
						result = digitType1[currentNum ] + blank + numUnit +  " " + result;
					}
					
				}else if(i == 2 || i == 5 || i == 8) {
					if(currentNum == 0) continue;
					
					if( currentNum == 1 ){
						
						if( isExistPreNum && preNum == 0 ){
							result = tenMultiple[currentNum] + " " + result;
						}else {
							result = digitType2[preNum] + " " + result;
						}
					}else {
						result = tenMultiple[currentNum] + " " + result;
					}
				}
			}
		}
		return result;
	}
	
	public static String getCurrentReqUri() {
		String uri = "";
		
		ServletUriComponentsBuilder serveltCB = ServletUriComponentsBuilder.fromCurrentContextPath();
		UriComponents uc = serveltCB.build();
		
		String scheme = uc.getScheme();
		String host =  uc.getHost();
		
		uri = scheme +"://" + host;
		
		if(uc.getPort() != -1) {
			uri = uri + ":" + uc.getPort();
		}
		
		if( ! EdocStringUtil.isEmpty(uc.getPath()) ) {
			uri = uri + uc.getPath();
		}
		
		LOG.info("uri : " + uri);
		
		return uri;
	}

	public static String getExtension(String fileName) {
		String extension = "";
		
		int sp = fileName.lastIndexOf(".");
		
		if(sp == -1) {
			LOG.info("파일 확장자를 찾을 수 없습니다.");
			throw new CommonException("STD.E9999"); //오류가 발생하였습니다.<br/>관리자에게 문의하세요.
		}
		
		extension = fileName.substring(sp + 1, fileName.length());
		LOG.info("extension : " + extension);
		return extension;
	}
	
	public static String getFileName(String fileName) {
		String extension = "";
		
		int sp = fileName.lastIndexOf(".");
		
		if(sp == -1) {
			LOG.info("파일 확장자를 찾을 수 없습니다.");
			throw new CommonException("STD.E9999"); //오류가 발생하였습니다.<br/>관리자에게 문의하세요.
		}
		
		extension = fileName.substring(0, sp);
		LOG.info("extension : " + extension);
		return extension;
	}

	public static String correctFilePath(String s) {
		if (s == null)
			return null;

		s = s.replace("\\\\", "/");
		s = s.replace("\\", "/");
		return s;
	}
}
