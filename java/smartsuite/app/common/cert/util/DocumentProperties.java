package smartsuite.app.common.cert.util;

import java.util.Map;
import com.google.common.base.Strings;

/**
 * <PRE>
 * Filename	: DocumentProperties.java <BR>
 * Package	: com.emro.util <BR>
 * Function	: <BR>
 * Comment	: Property를 다루는데 유용한 각종 유틸리티 메쏘드 <BR>
 * History	: 2009/04/01,  v1.0, 최초작성 <BR>
 * </PRE>
 * @version	1.0
 * @author 	이창진 Copyright (c) 2009 by EMRO Corp. All Rights Reserved.
 *
 */
public class DocumentProperties {

	/**
	 * 클래스패스상에 나타나는 emrp.properties 파일을 Property 정보를 읽어오고 저장하는데 사용한다.
	 */
	
	//jar file중  emro.properties 가 존재하고 잘 가져오는지 반드시 확인!
	private static PropertyManager propertyMgr = new PropertyManager("/smartsuite/properties/cert.properties");
	//private static PropertyManager propertyMgr = new PropertyManager("emro.properties");

	/**
	 * Property 정보를 파일로부터 다시 읽어옴.
	 */
	public static void reload() {
		propertyMgr.reload();
	}
		
	/**
	 * 설정 정보를 파일에 저장함.
	 */
	public static void save() {
		propertyMgr.save();
	}
	
	/**
	 * 현재 설정 정보를 읽어온 Property 파일의 위치를 반환함.
	 */
	public static String getFileLocation() {
		return propertyMgr.getFileLocation();
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 */
	public static String get(String name) {
		return propertyMgr.get(name);
	}
	
	/**
	 * 주어진 name에 대응되는 Property 값을 반환함.
	 */
	public static String get(String name, String defaultValue) {
		String ret = propertyMgr.get(name);
		if (Strings.isNullOrEmpty(ret)) {
			return defaultValue;
		} else {
			return ret;
		}
	}
	
	/**
	 * 주어진 value를 주어진 name에 해당하는 Property 값으로 설정함.
	 */
	public static String put(String name, String value) {
		return propertyMgr.put(name, value);
	}
	
	/**
	 * Property 정보가 저장되어 있는 Map 객체를 반환함.
	 */
	public static Map getMap() {
		return propertyMgr.getMap();
	}
	
	/**
	 * 주어진 Map을 이용하여 전체 Property 정보를 뒤집어 씌움.
	 */
	public static void setMap(Map map) {
		propertyMgr.setMap(map);
	}
	
	/**
	 * Property의 내용을 File 저장시 사용되는 문자열로 반환한다.
	 */
	public static String toFileString() {
		return propertyMgr.toFileString();
	}
	
	/**
	 * Property의 내용을 HTML 형식의 문자열로 반환한다.
	 */
	public static String toHtmlString() {
		return propertyMgr.toHtmlString();
	}
	
	/**
	 * 기본 toString()은 toFIleString()과 동일하게 구현함.
	 */
	public String toString() {
		return propertyMgr.toFileString();
	}
	
	/**
	 * Property의 내용 중 ${xxx} 형태인 것을 주어진 문자열에 반영시킨다.
	 */
	public static String apply(String input) {
		return propertyMgr.apply(input);
	}
}