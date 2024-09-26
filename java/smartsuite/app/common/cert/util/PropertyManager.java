package smartsuite.app.common.cert.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import smartsuite.app.common.util.EdocStringUtil;

/**
 * <PRE>
 * Filename	: PropertyManager.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: Property파일을 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public class PropertyManager {

	/** 파일의 실제 저장 위치를 담음 */
	private final String actualFilePath;
	
	/** Property 데이터를 담음 */
	private Map dataMap = new TreeMap();
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertyManager.class);

	/**
	 * 주어진 fileName으로부터 Property 정보를 읽고 저장하는 PropertyManager 객체를 생성한다.
	 */
	public PropertyManager(String fileName) {
		actualFilePath = fileName;
		reload(); //NOPMD
	}

	/**
	 * Property 정보를 파일로부터 다시 읽어옴.
	 */
	public void reload() {
		dataMap.clear();
		List rowList = null;
		InputStream is = null;
		
		try {
			is = this.getClass().getResourceAsStream(actualFilePath);
			StringBuffer contents = new StringBuffer();
			String newLine = "";
			InputStreamReader fi = null;
			try {
				fi = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(fi);
				do {
					newLine=br.readLine();
					contents.append( newLine).append('\n' );
				}while(!Strings.isNullOrEmpty(newLine));
			}
			catch( NullPointerException e ) {
			    LOG.error(e.getMessage(), e);
			}finally{
				if(fi!=null) fi.close();
			}
			rowList = EdocStringUtil.toList(contents.toString(), "\n");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			if(LOG.isDebugEnabled()) LOG.debug("Error occurred while reading text from " + actualFilePath + " : " + e.getMessage());
			return;
		}finally{
			try {
				if(is != null) is.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				if(LOG.isDebugEnabled()) LOG.debug("Error occurred while reading text from " + actualFilePath + " : " + e.getMessage());
			}
		}

		for (Iterator i = rowList.iterator(); i.hasNext(); ) {
			String row = (String) i.next();
			int sepIndex = row.indexOf('=');
			if (sepIndex != -1) {
				String key = row.substring(0, sepIndex);
				String value = unescape(row.substring(sepIndex + 1));
				dataMap.put(key, value);
			}
		}
	}
		
	/**
	 * 설정 정보를 파일에 저장함.
	 */
	public void save() {
		if (Strings.isNullOrEmpty(actualFilePath)) {
			LOG.debug("The location of Property file required if you want to save the Property data. Check if Property file is located on your classpath. The name of Property file is given at the construction of a PropertyManager instance.");
			return;
		}
		
		String fileStr = toFileString();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(actualFilePath, false);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(fileStr);
			osw.close();
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			if(LOG.isDebugEnabled()) LOG.debug("Error occurred while saving Property data into " + actualFilePath);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 현재 설정 정보를 읽어온 Property 파일의 위치를 반환함.
	 */
	public String getFileLocation() {
		return actualFilePath;
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 */
	public String get(String name) {
		return (String) dataMap.get(name);
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 * 만약 해당 name에 대응되는 값이 없으면 defaultValue를 반환함.
	 */
	public String get(String name, String defaultValue) {
		String value = get(name);
		if (Strings.isNullOrEmpty(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}
	
	/**
	 * 주어진 value를 주어진 name에 해당하는 Property 값으로 설정함.
	 */
	public String put(String name, String value) {
		return (String) dataMap.put(name, value);
	}
	
	/**
	 * Property 정보가 저장되어 있는 Map 객체를 반환함.
	 */
	public Map getMap() {
		return dataMap;
	}
	
	/**
	 * 주어진 Map을 이용하여 전체 Property 정보를 뒤집어 씌움.
	 */
	public void setMap(Map map) {
		dataMap = new TreeMap(map);
	}
	
	/**
	 * Property의 내용을 File 저장시 사용되는 문자열로 반환한다.
	 */
	public String toFileString() {
		String fileStr = "";
		Set entrySet = dataMap.entrySet();
		for (Iterator i = entrySet.iterator(); i .hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			fileStr += entry.getKey() + "=" + escape((String) entry.getValue()) + "\n";
		}
		return fileStr;
	}
	
	/**
	 * Property의 내용을 HTML 형식의 문자열로 반환한다.
	 */
	public String toHtmlString() {
		String fileStr = "<table border=1>";
		Set entrySet = dataMap.entrySet();
		for (Iterator i = entrySet.iterator(); i .hasNext(); ) {
			Map.Entry entry = (Map.Entry) i.next();
			fileStr += "<tr><td>" + entry.getKey() + "</td><td>" + escape((String) entry.getValue()) + "</td></tr>";
		}
		fileStr += "</table>";
		return fileStr;
	}
	
	/**
	 * 주어진 문자열에 나타나는 ${name} 부분을 Property에 저장된 ${name} 의 값으로 바꾸어준다.
	 */
	public String apply(String input) {
		if (!Strings.isNullOrEmpty(input)) {
			
			int firstStartIndex = input.indexOf("${");
			if (firstStartIndex != -1) {
				int firstEndIndex = input.indexOf("}", firstStartIndex + 2);
				if (firstEndIndex != -1) {
					int secondStartIndex = input.indexOf("${", firstEndIndex + 1);
					if (secondStartIndex == -1) {
						String foundName = input.substring(firstStartIndex + 2, firstEndIndex);
						String foundValue = (String) dataMap.get(foundName);
						if (!Strings.isNullOrEmpty(foundValue)) {
							return EdocStringUtil.replace(input, new String[] { "${" + foundName + "}" }, new String[] { foundValue });
						}
					}
				}
			}

			List fromList = new ArrayList();
			List toList = new ArrayList();
			int startIndex = firstStartIndex;
			int endIndex = 0;
			while (startIndex != -1) {
				endIndex = input.indexOf("}", startIndex + 2);
				if (endIndex == -1) {
					break;
				} else {
					String foundName = input.substring(startIndex + 2, endIndex);
					String foundValue = (String) dataMap.get(foundName);
					if (!Strings.isNullOrEmpty(foundValue)) {
						fromList.add("${" + foundName + "}");
						toList.add(foundValue);
					}
				}
				startIndex = input.indexOf("${", endIndex + 1);
			}
			
			String[] fromStr = (String[]) fromList.toArray(new String[fromList.size()]);
			String[] toStr = (String[]) toList.toArray(new String[toList.size()]);
			return EdocStringUtil.replace(input, fromStr, toStr);
		}
		return null;
	}
	
	
	/**
	 * 기본 toString()은 toFIleString()과 동일하게 구현함.
	 */
	public String toString() {
		return toFileString();
	}

	/**
	 * Escape 처리된 문자열을 원상 복귀 시킴
	 */
	private static String unescape(String input) {
		String[] fromStr = { "\\n", "\\r", "\\t", "\\\\" };
		String[] toStr = { "\n", "\r", "\t", "\\" };
		return EdocStringUtil.replace(input, fromStr, toStr);
	}
	
	/**
	 * 문자열을 Escape 처리함
	 */
	private static String escape(String input) {
		String[] fromStr = { "\n", "\r", "\t", "\\" };
		String[] toStr = { "\\n", "\\r", "\\t", "\\\\" };
		return EdocStringUtil.replace(input, fromStr, toStr);
	}
}