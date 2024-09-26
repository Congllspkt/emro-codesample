package smartsuite.app.bp.edoc.template.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.template.inputElement.*;
import smartsuite.app.bp.edoc.template.repository.ClauseRepository;
import smartsuite.app.bp.edoc.template.validator.ClauseValidator;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.shared.EdocConst;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 계약항목 관련 처리하는 서비스 Class입니다.
 *
 * @FileName ClauseService.java
 */

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class ClauseService {

	@Inject
	TagService tagService;
	@Inject
	ClauseRepository clauseRepository;

	@Inject
	ClauseValidator clauseValidator;

	@Inject
	SharedService sharedService;

	/**
	 * 대용량 계약항목 목록 조회
	 *
	 * @param : Map 계약항목 조회 조건
	 * @return : the list
	 */
	public FloaterStream largeSearchListCntrItem(Map<String, Object> param) {
		// 대용량 처리
		return clauseRepository.largeSearchListCntrItem(param);
	}

	/**
	 * 계약항목 목록 저장
	 *
	 * @param : Map 계약항목 정보
	 * @return : ResultMap
	 */
	public ResultMap saveListCntrItem(Map<String, Object> param) {
		List insertList = (List) param.get("insertList");
		List updateList = (List) param.get("updateList");

		// UPDATE
		for (Map row : (List<Map>) updateList) {
			// 항목명 중복체크
			int nameCheckResult = clauseRepository.checkCntrItemName(row);
			ResultMap validator = clauseValidator.checkCntrItemName(nameCheckResult);

			if (!validator.isSuccess()) {
				return validator;
			}
			clauseRepository.updateCntrItem(row);
		}
		// INSERT
		for (Map row : (List<Map>) insertList) {
			// 항목명 중복체크
			int nameCheckResult = clauseRepository.checkCntrItemName(row);
			ResultMap validator = clauseValidator.checkCntrItemName(nameCheckResult);

			if (!validator.isSuccess()) {
				return validator;
			}
			String cntrClId = "";
			String datTypCcd = (String) row.get("dat_typ_ccd");

			if ( CntrConst.CLAUSE_DAT_TYP.STRING.equals(datTypCcd)
				|| CntrConst.CLAUSE_DAT_TYP.NUMBER.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.YYYYMMDD_TYPE1.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.YYYYMMDD_TYPE2.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.YYYYMMDD_TYPE3.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.YYYYMMDD_TYPE4.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.MMDDYYYY_TYPE1.equals(datTypCcd)
					|| CntrConst.CLAUSE_DAT_TYP.CHECKBOX.equals(datTypCcd)) {   //input
				cntrClId = sharedService.generateDocumentNumber("INP");
			} else if ( CntrConst.CLAUSE_DAT_TYP.DSCRIPT.equals(datTypCcd)) {    //textarea
				cntrClId = sharedService.generateDocumentNumber("TXT_AREA");
			} else if ( CntrConst.CLAUSE_DAT_TYP.TEMPLATE.equals(datTypCcd)) {    //template 동적
				cntrClId = sharedService.generateDocumentNumber("TMPL");
			}
			cntrClId = cntrClId.toLowerCase();

			row.put("cntr_cl_id", cntrClId);
			row.put("cntr_cl_uuid", UUID.randomUUID().toString());
			clauseRepository.insertCntrItem(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약항목 삭제
	 *
	 * @param : ResultMap
	 * @return Map
	 */
	public ResultMap deleteListCntrItem(Map param) {
		List deleteList = (List) param.get("deleteList");

		// 사용되고 있는 계약 조항이 있으면, 삭제하지 않고 해당 리스트를 반환한다.
		ResultMap validResult = this.checkCntrClauseInUse(deleteList);
		if (!validResult.isSuccess()) {
			return validResult;
		}

		for (Map row : (List<Map>) deleteList) {
			clauseRepository.deleteCntrItem(row);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약서식 동적 템플릿 조회
	 *
	 * @param param
	 * @return the Map
	 */
	public Map findDynamicTmpById(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();

		resultMap = clauseRepository.findDynamicTmpById(param);

		return resultMap;
	}

	/**
	 * 계약서식 동적 템플릿 저장
	 *
	 * @param : Map
	 * @return ResultMap
	 */
	public ResultMap saveDynamicTmp(Map<String, Object> param) {

		clauseRepository.saveDynamicTmp(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List getAttrAll(Map<String, Object> param) {

		return clauseRepository.getAttrAll(param);
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List searchListCntrItem(Map<String, Object> param) {

		return clauseRepository.searchListCntrItem(param);
	}

	/**
	 * 동적계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public List findDynamicTmpByVariable(Map<String, Object> param) {

		return clauseRepository.findDynamicTmpByVariable(param);
	}

	/**
	 * 계약항목 조회
	 *
	 * @param : Map
	 * @return List
	 */
	public Map findListCntrItem(Map<String, Object> param) {

		return clauseRepository.findListCntrItem(param);
	}

	/**
	 * 계약 조항 목록 조회
	 *
	 * @param param
	 * @return FloaterStream
	 */
	public FloaterStream largeFindListCntrClause(Map param) {
		return clauseRepository.largeFindListCntrClause(param);
	}

	/**
	 * 계약 조항 저장
	 *
	 * @param param
	 * @return ResultMap
	 */
	public ResultMap saveCntrClause(Map param) {
		int nameCheckResult = clauseRepository.checkCntrItemName(param);
		ResultMap validator = clauseValidator.checkCntrItemName(nameCheckResult);

		if (!validator.isSuccess()) {
			return validator;
		}
		String datTypCcd = param.get("dat_typ_ccd").toString();
		// 입력창유형이 TEMPLATE인 경우 맵핑컬럼명 중복 체크
		if (EdocConst.CONTRACT_ITEM_BOX_TYP_TEMPLATE.equals(datTypCcd)) {
			int tmpVariableCheckResult = clauseRepository.checkCntrTmpItemVariable(param);
			validator = clauseValidator.checkCntrTmpItemVariable(tmpVariableCheckResult);

			if (!validator.isSuccess()) {
				return validator;
			}
		}

		String cntrClId = "";

		if( "STR".equals(datTypCcd)
				|| "NUMC".equals(datTypCcd)
				|| "YYYY년 MM월 DD일".equals(datTypCcd)
				|| "YYYY/MM/DD".equals(datTypCcd)
				|| "MM/DD/YYYY".equals(datTypCcd)
				|| "YYYY.MM.DD".equals(datTypCcd)
				|| "YYYY-MM-DD".equals(datTypCcd)
				|| "CHECK".equals(datTypCcd) ) {
			cntrClId = sharedService.generateDocumentNumber("INP");
		} else if( "DSCRT".equals(datTypCcd) ) {
			cntrClId = sharedService.generateDocumentNumber("TXT_AREA");
		} else if( "TMPL".equals(datTypCcd) ) {
			cntrClId = sharedService.generateDocumentNumber("TMPL");
		}

		cntrClId = cntrClId.toLowerCase();

		param.put("cntr_cl_id", cntrClId);
		param.put("cntr_cl_uuid", UUID.randomUUID().toString());

		clauseRepository.insertCntrItem(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 조항 사용 정보 저장
	 *
	 * @param tmplId:        템플릿 uuid ( cntrdoc_tmpl_uuid, appx_tmpl_uuid )
	 * @param useClauseList: 서식에 사용하는 계약조항 리스트 [ {CNTR_CL_ID: USE_CNT}, ... ]
	 */
	public void saveCntrClauseUse(String tmplId, List useClauseList) {
		if (tmplId == null || tmplId.isEmpty()) {
			return;
		}

		// 기존 사용 현황 삭제
		this.deleteCntrClauseUse(tmplId);

		if (useClauseList != null && !useClauseList.isEmpty()) {
			// 사용 갯수 추출
			Map<String, Integer> names = Maps.newHashMap();
			for (String name : (List<String>) useClauseList) {
				Integer count = names.get(name);
				if (count == null) {
					names.put(name, 1);
				} else {
					names.put(name, count + 1);
				}
			}

			// 저장할 리스트 생성
			List saveUseClauseList = Lists.newArrayList();
			Map item = null;
			for (String key : names.keySet()) {
				item = clauseRepository.getCntrClauseByCntrClId(key);
				if (item != null) {
					item.put("tmpl_uuid", tmplId);
					item.put("use_cnt", names.get(key));
					saveUseClauseList.add(item);
				}
			}

			// 리스트 저장
			for (Map saveCl : (List<Map>) saveUseClauseList) {
				clauseRepository.insertCntrClauseUse(saveCl);
			}
		}
	}

	/**
	 * 계약조항 사용 정보 삭제
	 *
	 * @param tmplId
	 */
	public void deleteCntrClauseUse(String tmplId) {
		Map deleteParam = Maps.newHashMap();
		deleteParam.put("tmpl_uuid", tmplId);
		clauseRepository.deleteCntrClauseUse(deleteParam);
	}

	/**
	 * 사용중인 계약 조항인지 체크
	 *
	 * @param clauseList
	 * @return
	 */
	public ResultMap checkCntrClauseInUse(List clauseList) {
		List useClauseList = Lists.newArrayList();

		for (Map clause : (List<Map>) clauseList) {
			int useCnt = clauseRepository.getCntrClauseUseCnt(clause);

			if (useCnt > 0) {
				useClauseList.add(clause);
			}
		}

		if (useClauseList.size() > 0) {
			return ResultMap.builder()
					.resultStatus(ResultMap.STATUS.INVALID_STATUS_ERR)
					.resultList(useClauseList)
					.build();
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 계약 조항을 사용중인 템플릿 목록 조회
	 *
	 * @param param ( use_clause_list )
	 * @return
	 */
	public List findListCntrClauseUse(Map param) {
		List useClauseList = (List) param.get("use_clause_list");
		List resultList = Lists.newArrayList();

		for (Map clause : (List<Map>) useClauseList) {
			List useTmplList = clauseRepository.findCntrClauseUseByCntrClUuid((String) clause.get("cntr_cl_uuid"));
			resultList.addAll(useTmplList);
		}

		return resultList;
	}

	/**
	 * 계약 조항 tag 생성
	 *
	 * @param : param ( cntr_cl_uuid )
	 * @return
	 */
	public String findCntrClauseTag(Map param) {
		String tag = "";
		Map clause = clauseRepository.findCntrClause(param);
		tag = tagService.getTag(clause);
		return tag;
	}

	private String makeTag(Map clause) {

		String cntrClId = (String) clause.get("cntr_cl_id");
		String datTypCcd = (String) clause.get("dat_typ_ccd");
		String dfltVal = (String) clause.get("dflt_val");
		String cntrClNm = (String) clause.get("cntr_cl_nm");
		String value = "";

		switch(datTypCcd){
			case "STR":
				if(Strings.isNullOrEmpty(dfltVal)){
					value = cntrClNm;
				}else{
					value = dfltVal;
				}
				//Tag tag = new Text("input", cntrClId, value);

				//return tag.getTag();
				break;
			case "DSCRT":
				//Tag tag = new TextArea("textarea", cntrClId, dfltVal);
				//return tag.getTag();
				break;
			case "NUMC":
				break;
			case "YYYY년 MM월 DD일":
			case "YYYY/MM/DD":
			case "MM/DD/YYYY":
			case "YYYY.MM.DD":
			case "YYYY-MM-DD":
				break;
			case "TMPL":
				break;
			case "CHECK":
				break;
		}

		return "";
	}
	public List findClauseCntrUseByCntrClUuid(Map param) {
		return clauseRepository.findClauseCntrUseByCntrClUuid(param);
	}



}