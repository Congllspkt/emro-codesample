package smartsuite.app.bp.edoc.template.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.contract.common.service.ContractService;
import smartsuite.app.bp.edoc.contract.doc.ContractDocument;
import smartsuite.app.bp.edoc.contract.doc.GetValueType;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.util.MakingCntrForm;

/**
 * 전자계약 계약서식관리 관련 처리를 하는 서비스 Class입니다.
 *
 * @FileName MainTemplateService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class CntrTemplateService {

	private static final Logger LOG = LoggerFactory.getLogger(CntrTemplateService.class);

	@Inject
	MainTemplateService mainTemplateService;

	@Inject
	SubTemplateService subTemplateService;

	@Inject
	ClauseService clauseService;

	@Inject
	MakingCntrForm makingCntrForm;

	@Inject
	EcontractService econtractService;

	@Inject
	ContractService contractService;


	/**
	 * 계약서식내용 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map findFormCont(Map<String,Object> param){
		return mainTemplateService.findFormCont(param);
	}

	/**
	 * 첨부서식내용 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map findAppformCont(Map<String,Object> param){
		return subTemplateService.findAppformCont(param);
	}

	/**
	 * 첨부서식 변경이력 내용 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map findAppCont(Map<String,Object> param){
		return subTemplateService.findAppCont(param);
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List getAttrAll(Map<String,Object> param){
		return clauseService.getAttrAll(param);
	}

	/**
	 * 계약서식 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map cntrFormView(Map<String,Object> param){
		return mainTemplateService.cntrFormView(param);
	}

	/**
	 * 계약서 별 첨부서식 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List getAppList(Map<String,Object> param){
		return subTemplateService.getAppList(param);
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List searchListCntrItem(Map<String,Object> param){
		return clauseService.searchListCntrItem(param);
	}

	/**
	 * 첨부서식 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List getAppFormTextContent(Map<String,Object> param){
		return subTemplateService.getAppFormTextContent(param);
	}

	/**
	 * 동적계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List findDynamicTmpByVariable(Map<String,Object> param){
		return clauseService.findDynamicTmpByVariable(param);
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map findListCntrItem(Map<String,Object> param){
		return clauseService.findListCntrItem(param);
	}

	/**
	 * 계약내용 조회
	 *
	 * @param : Map
	 * @return : Map
	 */
	public Map findCntrFormCont(Map<String,Object> param){
		return mainTemplateService.findCntrFormCont(param);
	}

	/**
	 * 계약서식 조회
	 *
	 * @param : Map
	 * @return : List
	 */
	public List findCntrdocFormList(Map<String,Object> param){
		return mainTemplateService.findCntrdocFormList(param);
	}

	/**
	 * 계약서식 조회
	 *
	 * @param : Map
	 * @return : List
	 */
	public List findUseByCntrClause(Map<String,Object> param){
		return clauseService.findClauseCntrUseByCntrClUuid(param);
	}


	/**
	 * 계약서식 조회
	 *
	 * @param : Map
	 * @return : List
	 */
	public String bindValueToMainTemplate(Map<String,Object> param, Map<String,Object> bindingObject){

		Map mainTemplate = mainTemplateService.findCntrFormCont(param);
		String cntrdocTmplCont = (String)mainTemplate.get("cntrdoc_tmpl_cont");

		List<Map<String,Object>> clauseList = clauseService.findClauseCntrUseByCntrClUuid(param);


		return cntrdocTmplCont;
	}

	private String bindValueToTemplate(String tmplCont, Map<String,Object> bindingObject, List<Map<String,Object>> clauseList){
		String cntrClId = "";
		String datTypCcd = "";
		String cntrClAka = "";

		for(Map clause : clauseList){
			cntrClId = (String)clause.get("cntr_cl_id");
			datTypCcd = (String)clause.get("dat_typ_ccd");
			cntrClAka = (String)clause.get("cntr_cl_aka");

			//바인딩 부분을 찾는다.
			//바인딩 부분을 교체한다.
		}

		return "";
	}

	private Integer[] searchReplacePoint(String tmplCont, String cntrClId, String datTypCcd){
		int startPoint = 0;
		int endPoint = 0;
		int cursor = 0;
		Integer[] replacePoint = new Integer[2];

		StringBuffer stringBuffer = new StringBuffer(tmplCont);
		while(stringBuffer.indexOf(cntrClId) != -1){
			cursor = stringBuffer.indexOf(cntrClId);
			startPoint = searchReplaceStartPoint(cursor, datTypCcd);
		}

		replacePoint[0] = startPoint;
		replacePoint[1] = endPoint;

		return replacePoint;
	}
	private int searchReplaceStartPoint(int cursor, String datTypCcd){

		switch(datTypCcd){
			case "STR":
				break;
			case "DATE1":
				break;
			case "DATE2":
				break;
			case "DATE3":
				break;
		}
		return 0;
	}

	public Map cntrFormPreview(Map param) {
		String editContent ="";

		Map cntrInfo = param;
		Map resultMap = new HashMap();

		resultMap = cntrFormView(param);
		editContent = (String) resultMap.get("content");


		// 계약 날짜 셋팅
		cntrInfo = econtractService.checkAndInputDate(cntrInfo);

		String oorgCd = (String) cntrInfo.get("oorg_cd");
		String rootLogicOrgCd = contractService.findRootLogicOrganizationWithOperationOrg(oorgCd);
		cntrInfo.put("logic_org_cd", rootLogicOrgCd);

		// 계약대상 협력사 정보
		Map<String, Object> vdInfo = econtractService.findBasicVdInfo(cntrInfo); // 업체정보
		cntrInfo = econtractService.mapMerge(vdInfo, cntrInfo);

		// 계약서 생성
		cntrInfo.put("tmpl_uuid", cntrInfo.get("cntrdoc_tmpl_uuid"));
		List<Map<String, Object>> useClauseList = findUseByCntrClause(cntrInfo);

		ContractDocument contractDocument = new ContractDocument(editContent, useClauseList);
		contractDocument.putValueToTemplate(cntrInfo, GetValueType.CNTR_CL_AKA);
		contractDocument.activateEdit();

		String cntrAppxCrngCont = contractDocument.getDisAbleDocument();

		cntrInfo.put("content", cntrAppxCrngCont);
		return cntrInfo;
	}



}