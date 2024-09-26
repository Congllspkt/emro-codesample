package smartsuite.app.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import smartsuite.app.common.template.service.TemplateGeneratorService;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.shared.EdocConst;
import smartsuite.exception.CommonException;

/**
 * HTML FORM을 작성하는 class
 *
 * @author SungHyun Kang
 * @FileName MakingCntrForm.java
 * @package smartsuite.app.bp.edoc.util
 * @since 2018. 1. 10
 */

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class MakingCntrForm {
	private static final Logger LOG = LoggerFactory.getLogger(MakingCntrForm.class);
	@Inject
	TemplateGeneratorService templateGeneratorService;
	/**
	 * 계약서 생성(파라미터 및 기본 값으로 계약서 생성)
	 * input,textarea 입력 값 setting x
	 * 
	 * @author SungHyun Kang
	 * @param cntrInfo
	 * @param attInputAllList
	 * @return
	 * @since 2018. 1. 10
	 */
	public Map<String, Object> setCntrForm(Map<String,Object> cntrInfo, List<Map<String,Object>> attInputAllList){
		String beforeContent = (String) cntrInfo.get("edit_content");
		String editedContent  = "";
		
		// 1. value setting
		editedContent = setContent(cntrInfo, attInputAllList, beforeContent);
		
		cntrInfo.put("orgn_content", editedContent);
		
		// 2. remove Input, textArea
		editedContent = DocumentUtil.removeInputBox(editedContent);
		editedContent = DocumentUtil.removeTextArea(editedContent);
		
		cntrInfo.put("edit_content", editedContent);
		return cntrInfo;
	}
	
	/**
	 * 계약서 동적으로 생성되는 부분 freemarker 활용하여 리플레이스 (계약서식에 동적생성부분이 여러개인 경우 호출)
	 * 
	 * @author daesung lee
	 * @param :String content:서식내용
	 * @param  : List<Map<String,Object>> dynamicTmpList:동적으로 그리기 위한 데이터
	 * @return
	 * @since 2020. 06. 19
	 */
	public String makeDynamicrForm(String content, List<Map<String, Object>> dynamicTmpList){
		String rtCont = content;
		String tmpBasCont = "";
		String tmpAttrNm = "";
		Map<String, Object> data = Maps.newHashMap();
		
		if(!validateDynamicAttrNm(content)) { //서식내용에 동적태그 여부 확인
			return content;
		}

		if(dynamicTmpList == null || dynamicTmpList.size() < 1) {
			LOG.info("class:" + this.getClass() + ",method : makeItemList " +"동적 생성 데이터가 없습니다.");
			return content;
		}
		
		for(Map<String,Object> row : dynamicTmpList) {
			if(!validateDynamicInfo(row)) {
				return content;
			}
			tmpAttrNm = (String)row.get("cntr_cl_id");
			tmpBasCont = (String)row.get("dyn_tmpl_cont");
			data = (Map<String,Object>)row.get("dynamicTmpMappingData");
			
			try {
				tmpBasCont = templateGeneratorService.freemarkerTemplateGenerate(tmpAttrNm, tmpBasCont, data);
			} catch (Exception e) {
				LOG.error(this.getClass().getName() + ".replaceFreemarker(Map,String) : " + e.getMessage(), e.toString());
				throw new CommonException(this.getClass().getName() + ".replaceFreemarker(Map,String) : " + e.getMessage(), e.toString());
			}
			rtCont = DocumentUtil.replaceTagContent(rtCont,"span",tmpAttrNm,tmpBasCont);
		}
	
		return rtCont;
	}
	/**
	 * 계약서 동적으로 생성되는 부분 freemarker 활용하여 리플레이스 (계약서식에 동적생성부분이 1개인 경우 호출)
	 * 
	 * @author daesung lee
	 * @param : cntrInfo
	 * @param : makeDynamicrForm
	 * @return
	 * @since 2020. 06. 19
	 */
	public String makeDynamicrForm(String content, Map<String, Object> dynamicTmpInfo){
		String rtCont = content;
		String tmpBasCont = "";
		String tmpAttrNm = "";
		Map<String, Object> data = Maps.newHashMap();
		
		if(!validateDynamicAttrNm(content)) { //서식내용에 동적태그 여부 확인
			return content;
		}
		
		if( dynamicTmpInfo == null ) {
			LOG.info("class:" + this.getClass() + ",method : makeItemList " +"동적 생성 데이터가 없습니다.");
			return content;
		}
		if(!validateDynamicInfo(dynamicTmpInfo)) {//필수값 체크
			return content;
		}
		tmpAttrNm = (String)dynamicTmpInfo.get("cntr_cl_id");
		tmpBasCont = (String)dynamicTmpInfo.get("dyn_tmpl_cont");
		data = (Map<String,Object>)dynamicTmpInfo.get("dynamicTmpMappingData");
		
		try {
			tmpBasCont = templateGeneratorService.freemarkerTemplateGenerate(tmpAttrNm, tmpBasCont, data);
		} catch (Exception e) {
			LOG.error(this.getClass().getName() + ".replaceFreemarker(Map,String) : " + e.getMessage(), e.toString());
			throw new CommonException(this.getClass().getName() + ".replaceFreemarker(Map,String) : " + e.getMessage(), e.toString());
		}
		rtCont = DocumentUtil.replaceTagContent(rtCont,"span",tmpAttrNm,tmpBasCont);
	
		return rtCont;
	}
	/**
	 * 계약서 생성(파라미터 및 기본 값으로 계약서 생성)
	 * input,textarea 입력 값 setting o (etcInfo)
	 * 
	 * @author SungHyun Kang
	 * @param : cntrInfo
	 * @param : etcrInfo (입력항목)
	 * @param attInputAllList (계약항목)
	 * @param attInputNumList (number 포맷 계약항목)
	 * @return
	 * @since 2018. 1. 10
	 */
	public Map<String, Object> setCntrForm(Map<String,Object> cntrInfo, Map<String,Object> etcInfo, List<Map<String,Object>> attInputAllList, List<Map<String,Object>> attInputNumList){
		String beforeContent = (String) cntrInfo.get("edit_content");
		String editedContent  = "";
		
		// 1. value setting
		editedContent  = setContent(cntrInfo, attInputAllList, beforeContent);
		// 2. etc value setting(input, textarea에서 입력한 value setting)
		// 3. remove Input, textArea
		Map<String,Object> contentsMap = Maps.newHashMap();
		if(etcInfo == null){
			cntrInfo.put("orgn_content", editedContent);
			editedContent = DocumentUtil.removeInputBox(editedContent);
			editedContent = DocumentUtil.removeTextArea(editedContent);
			cntrInfo.put("edit_content", editedContent);
		}else{
			contentsMap = setEtcValues(etcInfo, attInputNumList, editedContent);
			cntrInfo.put("orgn_content", contentsMap.get("orgn_content"));
			cntrInfo.put("edit_content", contentsMap.get("edit_content"));
		}

		return cntrInfo;
	}
	
	/**
	 * 첨부서류(TXT) 생성(파라미터 및 기본 값으로 계약서 생성)
	 * input,textarea 입력 값 setting x
	 * 
	 * @author Daesung Lee
	 * @param : cntrAppInfo
	 * @param attInputAllList
	 * @return
	 * @since 2020. 6. 22
	 */
	public String setAppCntrForm(String editContent, Map<String,Object> cntrInfo, List<Map<String,Object>> attInputAllList){
//		String beforeAppContent = (String) cntrAppInfo.get("edit_content");
		String beforeAppContent = editContent;
		String editedAppContent  = "";
		
		if(cntrInfo.containsKey(EdocConst.DYNAMIC_TEMPLATE_LIST)) { //한 계약서에 동적생성부분이 여러개인 경우
			//동적생성 함수 수행
			beforeAppContent = makeDynamicrForm(beforeAppContent, (List<Map<String,Object>>)cntrInfo.get(EdocConst.DYNAMIC_TEMPLATE_LIST));
		}else if(cntrInfo.containsKey(EdocConst.DYNAMIC_TEMPLATE_INFORMATION)) { //한 계약서에 동적생성부분이 한개인 경우
			//동적생성 함수 수행
			beforeAppContent = makeDynamicrForm(beforeAppContent, (Map<String,Object>)cntrInfo.get(EdocConst.DYNAMIC_TEMPLATE_INFORMATION));
		}
		
		//if 조건으로 삭제해야 할 내용
		if(cntrInfo.containsKey(EdocConst.DELETE_TAG_INFORMATION)) {
			beforeAppContent = deleteTag(beforeAppContent,(Map<String,String>)cntrInfo.get(EdocConst.DELETE_TAG_INFORMATION));
		}
		// 1. value setting
		editedAppContent = setContent(cntrInfo, attInputAllList, beforeAppContent);
		return editedAppContent;
	}
	
	/**
	 * 첨부서류(TXT) 생성
	 * input,textarea 입력값 setting
	 * 
	 * @author SungHyun Kang
	 * @param cntrInfo
	 * @param : etcrInfo (입력항목)
	 * @param : attInputAllList (계약항목)
	 * @param attInputNumList (number 포맷 계약항목)
	 * @return
	 * @since 2018. 1. 10
	 */
	public Map<String, Object> setAppCntrForm(Map<String,Object> cntrInfo, Map<String,Object> etcInfo, List<Map<String,Object>> attInputNumList){
		String beforeAppContent = (String) cntrInfo.get("content");
		
		// 1. etc value setting(input, textarea에서 입력한 value setting)
		// 2. remove Input, textArea
		Map<String,Object> contentsMap = setEtcValues(etcInfo, attInputNumList, beforeAppContent);
		
		cntrInfo.put("app_orgn_content", contentsMap.get("orgn_content"));
		cntrInfo.put("app_edit_content", contentsMap.get("edit_content"));
		return cntrInfo;
	}
	
	
	public String setContent(Map<String,Object> cntrInfo, List<Map<String,Object>> attInputAllList, String content){
		String rtCont = content;
		if(cntrInfo.containsKey(EdocConst.DYNAMIC_TEMPLATE_LIST)) { //한 계약서에 동적생성부분이 여러개인 경우
			//동적생성 함수 수행
			rtCont = makeDynamicrForm(rtCont, (List<Map<String,Object>>)cntrInfo.get(EdocConst.DYNAMIC_TEMPLATE_LIST));
		}else if(cntrInfo.containsKey(EdocConst.DYNAMIC_TEMPLATE_INFORMATION)) { //한 계약서에 동적생성부분이 한개인 경우
			//동적생성 함수 수행
			rtCont = makeDynamicrForm(rtCont, (Map<String,Object>)cntrInfo.get(EdocConst.DYNAMIC_TEMPLATE_INFORMATION));
		}
		
		//if 조건으로 삭제해야 할 내용
		if(cntrInfo.containsKey(EdocConst.DELETE_TAG_INFORMATION)) {
			rtCont = deleteTag(rtCont,(Map<String,String>)cntrInfo.get(EdocConst.DELETE_TAG_INFORMATION));
		}
		
		rtCont = DocumentUtil.setValueInput(rtCont, attInputAllList, cntrInfo);
		return rtCont;
	}
	
	
	/**
	 * 입력받은 값(etcInfo)를 계약서에 setting
	 * @author SungHyun Kang
	 * @param etcInfo
	 * @param afterContent
	 * @return
	 * @throws Exception 
	 * @since 2018. 1. 10
	 */
	public Map<String,Object> setEtcValues(Map<String,Object> etcInfo, List<Map<String,Object>> attInputNumList, String afterContent){
		Map<String, Object> resultMap = Maps.newHashMap();
		String beforeEtcContent = afterContent;
		String editedEtcContent  = "";
		
		Iterator iter = (Iterator) etcInfo.keySet().iterator();
		
		// 1. etc value setting(input, textarea에서 입력한 value setting)
		while(iter.hasNext()) {
			String item = (String) iter.next();

			// 숫자 포맷 적용( 1000 -> 1,000)
			for (int i = 0; i < attInputNumList.size(); i++) {
				Map<String,Object> attInputNumItem = (Map<String,Object>) attInputNumList.get(i);
				String attrNm = (String) attInputNumItem.get("cntr_cl_id");

				if (item.equals(attrNm)) {
					if (etcInfo.get(item).getClass() == String.class) {
						String itemValue = (String) etcInfo.get(item);
						etcInfo.put(item, EdocStringUtil.formatNum(itemValue));
					} else if(etcInfo.get(item).getClass() == ArrayList.class){
						ArrayList<String> itemValues = (ArrayList<String>) etcInfo.get(item);
						ArrayList<String> convertItemValues = new ArrayList<String>();
						for(String value :itemValues) {
							value = EdocStringUtil.formatNum(value);
							convertItemValues.add(value);
						}
						etcInfo.put(item, convertItemValues);
					}
				}
			}

			LOG.info("item:"+item);
			// 입력값 HTML 계약서에 setting
			if (etcInfo.get(item) == null) {
				continue;
//				String value = (String) etcInfo.get(item);
//				if (item.indexOf("inp_") > -1) {
//					beforeEtcContent = DocumentUtil.setValueInput(beforeEtcContent, item, value);
//				} else if (item.indexOf("txt_area_") > -1) {
//					beforeEtcContent = DocumentUtil.setValueTextForm(beforeEtcContent, item, value);
//				}
			} else if (etcInfo.get(item).getClass() == String.class) {
				String value = (String) etcInfo.get(item); 
				//value = EdocStringUtil.toHtml(value);//value 값 특수 문자 치환 
				if (item.indexOf("inp_") > -1) {
					beforeEtcContent = DocumentUtil.setValueInput(beforeEtcContent, item, value);
				} else if (item.indexOf("txt_area_") > -1) {
					beforeEtcContent = DocumentUtil.setValueTextForm(beforeEtcContent, item, value);
				}
			} else if (etcInfo.get(item).getClass() == Integer.class) {
				String value = etcInfo.get(item).toString();
				//value = EdocStringUtil.toHtml(value);//value 값 특수 문자 치환 
				if (item.indexOf("inp_") > -1) {
					beforeEtcContent = DocumentUtil.setValueInput(beforeEtcContent, item, value);
				} else if (item.indexOf("txt_area_") > -1) {
					beforeEtcContent = DocumentUtil.setValueTextForm(beforeEtcContent, item, value);
				}
			}else {
				List<String> list = (ArrayList<String>) etcInfo.get(item);
				String[] values = new String[list.size()];
				values = list.toArray(values);
				
				/*for(int i = 0; i < values.length; i++){
					values[i] = EdocStringUtil.toHtml(values[i]);//value 값 특수 문자 치환
				}*/
				
				if (item.indexOf("inp_") > -1) {
					beforeEtcContent = DocumentUtil.setValueInput(beforeEtcContent, item, values);
				} else if (item.indexOf("txt_area_") > -1) {
					beforeEtcContent = DocumentUtil.setValueTextForm(beforeEtcContent, item, values);
				}
			}
		}
		
		//특수문자 치환 로직 적용
		editedEtcContent = beforeEtcContent;
		iter = (Iterator) etcInfo.keySet().iterator();
		while(iter.hasNext()) {
			String item = (String) iter.next();
			LOG.info("item:" + item);
			if(etcInfo.get(item) == null) {
				continue;
			}
			else if (etcInfo.get(item).getClass() == String.class) {
				String value = (String) etcInfo.get(item); 
				value = EdocStringUtil.toHtml(value);//value 값 특수 문자 치환 
				if (item.indexOf("inp_") > -1) {
					editedEtcContent = DocumentUtil.setValueInput(editedEtcContent, item, value);
				} else if (item.indexOf("txt_area_") > -1) {
					editedEtcContent = DocumentUtil.setValueTextForm(editedEtcContent, item, value);
				}
			}else if (etcInfo.get(item).getClass() == Integer.class) {
				String value = etcInfo.get(item).toString(); 
				value = EdocStringUtil.toHtml(value);//value 값 특수 문자 치환 
				if (item.indexOf("inp_") > -1) {
					editedEtcContent = DocumentUtil.setValueInput(editedEtcContent, item, value);
				} else if (item.indexOf("txt_area_") > -1) {
					editedEtcContent = DocumentUtil.setValueTextForm(editedEtcContent, item, value);
				}
			} else {
				List<String> list = (ArrayList<String>) etcInfo.get(item);
				String[] values = new String[list.size()];
				values = list.toArray(values);
				
				for(int i = 0; i < values.length; i++){
					values[i] = EdocStringUtil.toHtml(values[i]);//value 값 특수 문자 치환
				}
				
				if (item.indexOf("inp_") > -1) {
					editedEtcContent = DocumentUtil.setValueInput(editedEtcContent, item, values);
				} else if (item.indexOf("txt_area_") > -1) {
					editedEtcContent = DocumentUtil.setValueTextForm(editedEtcContent, item, values);
				}
			}
		}
		
		editedEtcContent = DocumentUtil.removeInputBox(editedEtcContent);
		editedEtcContent = DocumentUtil.removeTextArea(editedEtcContent);
		resultMap.put("orgn_content", beforeEtcContent);
		resultMap.put("edit_content", editedEtcContent);
		
		return resultMap;
	}
	
	/**
	 * input textarea 제거
	 * 
	 * @author daesung lee
	 * @param attInputList 계약항목목록
	 * @param : editContent 서식내용
	 * @return String editContent
	 * @since 2019-05-17
	 */
	public String removeInputAndTextArea(List<Map<String,Object>> attInputList, String editedContent){
		String returnContent = editedContent;
		String inputName = "";
		// 2. remove Input, textArea
		for(Map row : attInputList) {
			inputName = (String)row.get("cntr_cl_id");
			returnContent = DocumentUtil.setValueToHtml( returnContent,inputName);
		}
		returnContent = DocumentUtil.removeInputBox(returnContent);
		returnContent = DocumentUtil.removeTextArea(returnContent);
		 
		return returnContent;
	}
	/**
	 * input항목에 기본값 적용
	 * 
	 * @author daesung lee
	 * @param attInputList 계약항목목록
	 * @param : editContent 서식내용
	 * @return String editContent
	 * @since 2019-05-17
	 */
	public String replaceInputBaseValue(List<Map<String,Object>> attInputList, String content){
		String returnContent = content;
		String searchWord = "";
		String replaceStartWord = "";
		String replaceEndWord = "";
		String replaceValue = "";
		String tag = "";
		for(Map row : attInputList) {
			searchWord = (String)row.get("cntr_cl_id");
			replaceValue = (String)row.get("dflt_val");
			
			if(Strings.isNullOrEmpty(replaceValue)) {
				continue;
			}
			
			if(searchWord.indexOf("inp_") > -1) { //input 항목인 경우
				replaceStartWord = "value=\"";
				tag = "input";
			}else if(searchWord.indexOf("txt_area_") > -1) { //textarea 항목인 경우
				replaceStartWord = ">";
				tag = "textarea";
			}
			returnContent = DocumentUtil.searchAndReplace(returnContent, replaceValue, searchWord, replaceStartWord, tag);
		}
		return returnContent;
	}
	/* 동적생성하기 위한 데이터 필수값 체크 */
	private boolean validateDynamicInfo(Map dynamicTmpInfo) {
		
		String tmpAttrNm = (String)dynamicTmpInfo.get("cntr_cl_id");
		String tmpBasCont = (String)dynamicTmpInfo.get("dyn_tmpl_cont");
		Map data = (Map<String,Object>)dynamicTmpInfo.get("dynamicTmpMappingData");
		
		if(Strings.isNullOrEmpty(tmpAttrNm)) {
			LOG.info("class:" + this.getClass() + ",method : makeItemList " +"동적 생성하기 위한 cntr_cl_id 데이터 없습니다.");
			return false;
		}
		if(Strings.isNullOrEmpty(tmpBasCont)) {
			LOG.info("class:" + this.getClass() + ",method : makeItemList " +"동적 생성하기 위한 dyn_tmpl_cont 데이터가 없습니다.");
			return false;
		}
		if(data == null || data.isEmpty()) {
			LOG.info("class:" + this.getClass() + ",method : makeItemList " +"동적 생성하기 위한 dynamicTmpMappingData 데이터가 없습니다.");
			return false;
		}
		
		return true;
	}
	
	private boolean validateDynamicAttrNm(String content) {
		if(content.indexOf("[[tmp]]_") < 0) {
			return false;
		}else {
			return true;
		}
	}
	/**
	 * 태그 삭제
	 * 서식내용에 특정 조건에 따라 내용 일부를 삭제할 경우
	 * 사용 방법
	 * 1. 서식내용에 삭제해야할 태그 속성값 설정. 예시) <tr deleteTarget="[[PERFOR_BOND_YN]]"><td>test</td></tr>
	 * 2. deleteTargInfo 해당 value 값은 N으로 해야 삭제 대상임. 예시: deleteTagInfo.put("cpgur_use_yn","N");
	 * @author daesung lee
	 * @param content 서식내용
	 * @param deleteTagInfo 삭제해야할 대상 정보
	 * @return String content
	 * @since 2020-06-22
	 */
	private String deleteTag(String content, Map<String,String> deleteTagInfo) {
		String result = content;
		Map<String,Object> repDeleteTagInfo = Maps.newHashMap();
		Iterator i = deleteTagInfo.keySet().iterator();
		String key = "";
		String deleteTargetVal = "";
		
		if(content.indexOf(EdocConst.DELETE_TARGET) < 0) {
			LOG.info(this.getClass() + ", method : deleteTag message:"+"서식에 삭제해야할 태그 속석명이 없습니다.");
			return content;
		}
		
		while(i.hasNext()) {
			key = (String)i.next();
			if(!Strings.isNullOrEmpty(key)) {
				deleteTargetVal = "[[" + key.toUpperCase() + "]]"; //변환예시 : cpgur_use_yn -> [[PERFOR_BOND_YN]] 으로 변환함 (서식내용에 deleteTarget="[[PERFOR_BOND_YN]]" 이렇게 사용해야함)
				
				repDeleteTagInfo.put(deleteTargetVal, deleteTagInfo.get(key));
			}
		}
		
		i = repDeleteTagInfo.keySet().iterator();
		while(i.hasNext()) {
			key = (String)i.next();
			if(!Strings.isNullOrEmpty(key)) {
				if("N".equals(repDeleteTagInfo.get(key))) { //조건 N 인 경우
					result = DocumentUtil.removeTagContent(result,EdocConst.DELETE_TARGET , key);
				}
			}
		}
		
		return result;
	}
}
