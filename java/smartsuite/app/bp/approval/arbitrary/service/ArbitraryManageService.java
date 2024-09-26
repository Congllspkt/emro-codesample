package smartsuite.app.bp.approval.arbitrary.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import smartsuite.app.bp.approval.arbitrary.repository.ArbitraryManagerRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;
import smartsuite.security.authentication.Auth;

/**
 * 전결규정관리 관련 처리하는 서비스 Class입니다.
 *
 * @Since 2019.02.20
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class ArbitraryManageService {


	@Inject
	private SharedService sharedService;

	@Inject
	ArbitraryManagerRepository arbitraryManagerRepository;

	@Inject
	ArbitraryDataSourceService arbitraryDataSourceService;

	@Inject
	MessageSource messageSource;

	
	/**
	 * 전결규정 결재유형 정보를 수정한다.
	 *
	 */
	public void updateArbitraryAprvType(Map<String, Object> param) {
		//sqlSession.update(NAMESPACE + "updateArbitraryAprvType", param);

		Map<String,Object> getArbitraryAprvType = arbitraryManagerRepository.getArbitraryAprvType(param);

		//존재 할 경우, update
		if(MapUtils.isNotEmpty(getArbitraryAprvType)){
			arbitraryManagerRepository.updateArbitraryAprvApplType(param);
			arbitraryManagerRepository.updateArbitraryAprvUseType(param);
		}else{//존재하지 않을 경우, insert
			arbitraryManagerRepository.insertArbitraryAprvApplType(param);
			arbitraryManagerRepository.insertArbitraryAprvUseType(param);
		}
	}
	
	/**
	 * 전결규정 정보를 조회한다. - 헤더/상세테이블/가능한 적용시작일
	 *
	 */
	public Map<String, Object> selectArbitraryData(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		String headerId = (String)param.get("dapvl_uuid");
		
		if (StringUtils.isNotEmpty(headerId)) {
			resultMap.put("headerData", arbitraryManagerRepository.selectArbitraryHeader(param));
			resultMap.put("detailTables", arbitraryManagerRepository.selectListArbitraryDetailTable(param));
		}
		resultMap.put("availApplySd", arbitraryManagerRepository.selectArbitraryHeaderAvailApplySd(param));
		return resultMap;
	}
	
	/**
	 * 전결규정 결재유형 정보를 저장한다.
	 *
	 */
	public Map<String, Object> saveListArbitraryAprvType(Map<String, Object> param) {
		List<Map<String, Object>> updateAprvTypes = (List<Map<String, Object>>)param.get("updateAprvTypes");
		List<Map<String, Object>> updateList = Lists.newArrayList();
		
		for (Map<String, Object> data : updateAprvTypes) {
			updateList.add(data);
		}
		for (Map<String, Object> data : updateList) {
			this.updateArbitraryAprvType(data);
		}
		Map<String, Object> resultMap = Maps.newHashMap();
		return resultMap;
	}

	/**
	 * 전결규정 헤더 및 상세테이블 정보를 저장한다.
	 *
	 */
	public Map<String, Object> saveArbitraryData(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		List<Map<String, Object>> insertTables = (List<Map<String, Object>>)param.get("insertTables");
		List<Map<String, Object>> updateTables = (List<Map<String, Object>>)param.get("updateTables");
		List<Map<String, Object>> insertTableList = Lists.newArrayList();
		List<Map<String, Object>> updateTableList = Lists.newArrayList();
		List<Map<String, Object>> insertHeaderList = Lists.newArrayList();
		List<Map<String, Object>> updateHeaderList = Lists.newArrayList();
		List<Map<String, Object>> updateHeaderApplyEdList = Lists.newArrayList();
		
		final Date MAX_APPLY_ED = this.maxApplyEd();
		final String HEADER_ID_KEY = "dapvl_uuid";

		if ("CNFD".equals((String)headerData.get("dapvl_crn_sts_ccd"))) { // 확정
			headerData.put("efct_exp_dt", MAX_APPLY_ED);
			
			Map<String, Object> maxHeader = arbitraryManagerRepository.selectArbitraryHeaderByApplyEd(headerData);
			if (maxHeader != null && !maxHeader.isEmpty()) {
				Date applySd = (Date)headerData.get("efct_st_dt");
				maxHeader.put("efct_exp_dt", this.getPlusDays(applySd, -1)); // minus one day
				updateHeaderApplyEdList.add(maxHeader);
			}
		} else {
			headerData.put("efct_exp_dt", null);
		}

		String headerId = (String)headerData.get(HEADER_ID_KEY); // 전결항목 아이디
		if (StringUtils.isEmpty(headerId)) { // 신규
			headerId = UUID.randomUUID().toString(); // UUID
			headerData.put(HEADER_ID_KEY, headerId);

			insertHeaderList.add(headerData);
		} else {
			updateHeaderList.add(headerData);
		}
		
		for (Map<String, Object> data : insertTables) {
			data.put(HEADER_ID_KEY, headerId);
			insertTableList.add(data);
		}
		for (Map<String, Object> data : updateTables) {
			data.put(HEADER_ID_KEY, headerId);
			updateTableList.add(data);
		}

		
		for (Map<String, Object> data : insertHeaderList) {
			arbitraryManagerRepository.insertArbitraryHeader(data); // 신규 시 - 헤더 등록
		}
		for (Map<String, Object> data : updateHeaderList) {
			arbitraryManagerRepository.updateArbitraryHeader(data); // 수정 시 - 헤더 수정
		}
		for (Map<String, Object> data : updateHeaderApplyEdList) {
			arbitraryManagerRepository.updateArbitraryHeaderApplyEd(data); // 확정일 경우 - 기존 마지막 헤더의 efct_exp_dt 수정
		}
		for (Map<String, Object> data : insertTableList) {
			arbitraryManagerRepository.insertArbitraryDetailTable(data); // 상세테이블 추가
		}
		for (Map<String, Object> data : updateTableList) {
			arbitraryManagerRepository.updateArbitraryDetailTable(data); // 상세테이블 수정
		}
		for (Map<String, Object> data : insertTableList) {
			arbitraryManagerRepository.insertArbitraryDetailFactor(data); // 추가된 상세테이블 항목 추가
		}
		
		resultMap.put("headerData", headerData);
		return resultMap;
	}
	
	/**
	 * 전결규정 헤더 및 상세테이블 정보를 임시저장한다.
	 *
	 */
	public Map<String, Object> saveDraftArbitraryData(Map<String, Object> param) {
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		headerData.put("dapvl_crn_sts_ccd", "CRNG");
		return this.saveArbitraryData(param);
	}
	
	/**
	 * 전결규정 헤더 및 상세테이블 정보를 확정저장한다.
	 *
	 */
	public Map<String, Object> saveFixArbitraryData(Map<String, Object> param) {
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		headerData.put("dapvl_crn_sts_ccd", "CNFD");
		return this.saveArbitraryData(param);
	}
	
	/**
	 * 전결규정 헤더 확정취소
	 */
	public Map<String, Object> saveCancelArbitraryHeader(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		List<Map<String, Object>> updateHeaderList = Lists.newArrayList();
		List<Map<String, Object>> updateHeaderApplyEdList = Lists.newArrayList();
		
		Date applySd = (Date)headerData.get("efct_st_dt");
		Map<String, Object> prevParam = Maps.newHashMap();
		prevParam.put("apvl_typ_ccd", headerData.get("apvl_typ_ccd"));
		prevParam.put("efct_exp_dt", this.getPlusDays(applySd, -1)); // minus one day
		
		Map<String, Object> prevHeader = arbitraryManagerRepository.selectArbitraryHeaderByApplyEd(prevParam);
		if (prevHeader != null && !prevHeader.isEmpty()) {
			prevHeader.put("efct_exp_dt", this.maxApplyEd());
			updateHeaderApplyEdList.add(prevHeader);
		}
		
		headerData.put("dapvl_crn_sts_ccd", "CRNG"); // 임시저장 상태로.
		headerData.put("efct_exp_dt", null);
		updateHeaderList.add(headerData);
		
		for (Map<String, Object> data : updateHeaderList) {
			arbitraryManagerRepository.updateArbitraryHeader(data);
		}
		for (Map<String, Object> data : updateHeaderApplyEdList) {
			arbitraryManagerRepository.updateArbitraryHeaderApplyEd(data);
		}
		
		resultMap.put("headerData", headerData);
		return resultMap;
		
	}
	
	/**
	 * 전결규정 정보 복사생성
	 */
	public Map<String, Object> saveCopyArbitraryData(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Map<String, Object> srcHeaderData = (Map<String, Object>)param.get("headerData");
		List<Map<String, Object>> copyLineList = Lists.newArrayList();
		
		final String HEADER_ID_KEY = "dapvl_uuid";
		final String LINE_ID_KEY = "dapvl_apvlln_id";
		
		String newHeaderId = UUID.randomUUID().toString(); // UUID
		String srcHeaderId = (String)srcHeaderData.get(HEADER_ID_KEY);
		String aprvTyp = (String)srcHeaderData.get("apvl_typ_ccd");

		List<Map<String, Object>> lineList = arbitraryManagerRepository.selectListArbitraryAprvLineByHeaderId(srcHeaderData);
		if (lineList != null && !lineList.isEmpty()) {
			for (Map<String, Object> data : lineList) {
				String newLineId = sharedService.generateDocumentNumber("ABDSN_LINE_ID").replaceAll("\\{0\\}", aprvTyp); // 문서번호
				
				Map<String, Object> newLineData = Maps.newHashMap();
				newLineData.put("src_abdsn_hd_id", srcHeaderId);
				newLineData.put("new_abdsn_hd_id", newHeaderId);
				
				newLineData.put("src_dapvl_apvlln_id", data.get(LINE_ID_KEY));
				newLineData.put("new_dapvl_apvlln_id", newLineId);
				copyLineList.add(newLineData);
			}
		}
		
		Map<String, Object> newHeaderData = Maps.newHashMap();
		newHeaderData.put(HEADER_ID_KEY, newHeaderId);
		newHeaderData.put("apvl_typ_ccd", aprvTyp);
		newHeaderData.put("efct_st_dt", null);
		newHeaderData.put("efct_exp_dt", null);
		newHeaderData.put("dapvl_crn_sts_ccd", "CRNG");
		
		Map<String, Object> copyData = Maps.newHashMap();
		copyData.put("src_abdsn_hd_id", srcHeaderId);
		copyData.put("new_abdsn_hd_id", newHeaderId);

		arbitraryManagerRepository.insertArbitraryHeader(newHeaderData); // 신규 - 헤더 등록
		arbitraryManagerRepository.insertCopyArbitraryDetailTable(copyData); // 상세테이블 복사
		arbitraryManagerRepository.insertCopyArbitraryDetailFactor(copyData); // 상세테이블 항목 복사

		for (Map<String, Object> data : copyLineList) {
			arbitraryManagerRepository.insertCopyArbitraryAprvLine(data); // 상세테이블 결재선 복사
			arbitraryManagerRepository.insertCopyArbitraryAprvLineDetail(data); // 상세테이블 결재선 상세 복사
			arbitraryManagerRepository.insertCopyArbitraryAprvLineCondition(data); // 상세테이블 결재선 조건 복사
		}
		
		resultMap.put("headerData", newHeaderData);
		return resultMap;
		
	}
	
	/**
	 * 전결규정 전결결재선 정보를 저장(신규등록/수정)한다.
	 *
	 */
	public Map<String, Object> saveListArbitraryAprvLine(Map<String, Object> param) {
		List<Map<String, Object>> insertAprvLines = (List<Map<String, Object>>)param.get("insertAprvLines");
		List<Map<String, Object>> updateAprvLines = (List<Map<String, Object>>)param.get("updateAprvLines");
		List<Map<String, Object>> insertList = Lists.newArrayList();
		List<Map<String, Object>> updateList = Lists.newArrayList();
		List<Map<String, Object>> defaultBasLineList = Lists.newArrayList();
		final String LINE_ID_KEY = "dapvl_apvlln_id";
		
		for (Map<String, Object> data : insertAprvLines) {
			String lineId = sharedService.generateDocumentNumber("ABDSN_LINE_ID").replaceAll("\\{0\\}", (String)data.get("apvl_typ_ccd")); // 문서번호
			data.put(LINE_ID_KEY, lineId);
			
			insertList.add(data);

			if ("Y".equals(data.get("dflt_apvlln_yn"))) {
				defaultBasLineList.add(data);
			}
		}

		for (Map<String, Object> data : updateAprvLines) {
			updateList.add(data);

			if ("Y".equals(data.get("dflt_apvlln_yn"))) {
				defaultBasLineList.add(data);
			}
		}
		
		for (Map<String, Object> data : insertList) {
			arbitraryManagerRepository.insertArbitraryAprvLine(data);
		}
		for (Map<String, Object> data : updateList) {
			arbitraryManagerRepository.updateArbitraryAprvLine(data);
		}
		for (Map<String, Object> data : defaultBasLineList) {
			arbitraryManagerRepository.updateArbitraryAprvLineBasAprvlnOff(data);
		}
		
		
		
		Map<String, Object> resultMap = Maps.newHashMap();
		return resultMap;
	}

	/**
	 * 전결규정 전결결재선 상세 정보를 저장(신규등록/수정)한다.
	 *
	 */
	public Map<String, Object> saveListArbitraryAprvLineDetail(Map<String, Object> param) {
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		Map<String, Object> detailInfo = (Map<String, Object>)param.get("detailInfo");
		List<Map<String, Object>> ccdCommons = (List<Map<String, Object>>)param.get("ccdCommons");
		List<Map<String, Object>> curAmtList = (List<Map<String, Object>>)param.get("curList");


		final String APRVER_ID_KEY = "dapvl_apvr_uuid";
		final String LINE_ID_KEY = "dapvl_apvlln_id";

		if((String)headerData.get("dapvl_apvlln_id") == null) {
			String lineId = sharedService.generateDocumentNumber("ABDSN_LINE_ID").replaceAll("\\{0\\}", (String)headerData.get("apvl_typ_ccd")); // 문서번호
			String lNo = UUID.randomUUID().toString();
			headerData.put(LINE_ID_KEY, lineId);
			arbitraryManagerRepository.insertArbitraryAprvLine(headerData);
			if(ccdCommons != null) {
				if(ccdCommons.size() > 0) {
					if(!(ccdCommons.size() == 1 && ccdCommons.get(0).get("parm_key_val").equals("DFLT_TASK"))){
						ccdCommons.stream().forEach(item -> {
							item.put(LINE_ID_KEY, lineId);
							item.put("dapvl_lno_uuid", lNo);
							arbitraryManagerRepository.insertArbitraryAprvLineInpVar(item);
						});
					}
				}
			}
		}

		if(detailInfo.containsKey(APRVER_ID_KEY) && detailInfo.get(APRVER_ID_KEY) != null){
			arbitraryManagerRepository.updateArbitraryAprvLineDetail(detailInfo);
		}else{
			String aprverId = UUID.randomUUID().toString();

			detailInfo.put(APRVER_ID_KEY, aprverId);
			detailInfo.put("dapvl_uuid",headerData.get("dapvl_uuid"));
			detailInfo.put("dapvl_tbl_id",headerData.get("dapvl_tbl_id"));
			detailInfo.put("dapvl_apvlln_id",headerData.get("dapvl_apvlln_id"));
			arbitraryManagerRepository.insertArbitraryAprvLineDetail(detailInfo);
			if( "Y".equals(detailInfo.get("dapvl_cnd_amt_rv_yn")) && curAmtList != null) {
				if(curAmtList.size() > 0) {
					curAmtList.stream().forEach(item -> {
						Map<String, Object> apvrCurParam = Maps.newHashMap();
						apvrCurParam.put("dapvl_uuid",headerData.get("dapvl_uuid"));
						apvrCurParam.put("dapvl_tbl_id",headerData.get("dapvl_tbl_id"));
						apvrCurParam.put("dapvl_apvlln_id",headerData.get("dapvl_apvlln_id"));
						apvrCurParam.put(APRVER_ID_KEY, aprverId);
						apvrCurParam.put("cur", item.get("data"));
						apvrCurParam.put("dapvl_cnd_amt", item.get("amount"));
						arbitraryManagerRepository.insertArbitraryAprvLineDetailCurrency(apvrCurParam);
					});
				}
			}
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("headerData", headerData);
		return resultMap;
	}
	
	/**
	 * 전결규정 전결결재선 조건 목록을 저장한다.
	 *
	 */
	public ResultMap saveListArbitraryAprvLineCondition(Map param) {
		Map<String, Object> headerData = (Map<String, Object>)param.get("headerData");
		List<List<Map<String, Object>>> insertAprvLineConditions = (List<List<Map<String, Object>>>)param.get("insertAprvLineConditions");
		List<Map<String, Object>> insertList = Lists.newArrayList();
		List<Map<String, Object>> invalidList = Lists.newArrayList();
		final String LNO_ID_KEY = "dapvl_lno_uuid";
		String headerId = (String)headerData.get("dapvl_uuid");
		String tableId = (String)headerData.get("dapvl_tbl_id");
		
		for (List<Map<String, Object>> conditions : insertAprvLineConditions) {

			Map<String, Object> subParam = Maps.newHashMap();
			subParam.put("dapvl_uuid", headerId);
			subParam.put("dapvl_tbl_id", tableId);
			subParam.put("factors", conditions);
			List<Map<String, Object>> countList = arbitraryManagerRepository.selectListCountArbitraryAprvLineCondition(subParam);

			boolean exist = false;
			for (Map<String, Object> data : countList) {
				int cnt = 0;
				if(data.get("cnt") != null){
					cnt = (Integer)data.get("cnt");
				}
				if (cnt >= conditions.size()) {
					exist = true;
				}
			}
			if (exist) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("duplicated", conditions);
				invalidList.add(map);
			} else {
				String lnoId = UUID.randomUUID().toString();
				for (Map<String, Object> data : conditions) {
					data.put(LNO_ID_KEY, lnoId);
					insertList.add(data);
				}
			}
		}
		
		for (Map<String, Object> data : insertList) {
			arbitraryManagerRepository.insertArbitraryAprvLineCondition(data);
		}

		ResultMap resultMap  = ResultMap.getInstance();
		if (invalidList.isEmpty()) {
			resultMap.setResultStatus(ResultMap.STATUS.SUCCESS);
		} else {
			resultMap.setResultStatus(ResultMap.STATUS.DUPLICATED);
			Map<String,Object> resultData = Maps.newHashMap();
			resultData.put("invalid_datas", invalidList);
			resultMap.setResultData(resultData);
		}
		return resultMap;
	}
	
	
	
	
	/**
	 * 전결규정 헤더 목록을 삭제한다.
	 *
	 */
	public Map<String, Object> deleteListArbitraryHeader(Map param) {
		List<Map<String, Object>> deleteHeaders = (List<Map<String, Object>>)param.get("deleteHeaders");
		List<Map<String, Object>> deleteList = Lists.newArrayList();
		
		for (Map<String, Object> data : deleteHeaders) {
			deleteList.add(data);
		}
		
		for (Map<String, Object> data : deleteList) {
			arbitraryManagerRepository.deleteArbitraryHeader(data); // 전결규정 헤더 삭제 (sts -> 'D')
		}
		
		Map<String, Object> resultMap = Maps.newHashMap();
		return resultMap;
	}
	
	/**
	 * 전결규정 상세테이블 목록을 삭제한다.
	 *
	 */
	public ResultMap deleteListArbitraryDetailTable(Map param) {
		List<Map<String, Object>> deleteTables = (List<Map<String, Object>>)param.get("deleteTables");

		for (Map<String, Object> data : deleteTables) {
			arbitraryManagerRepository.deleteArbitraryDetailFactorByTableId(data); // 전결 상세테이블 항목 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineDetailCurrencyByTableId(data); // 전결결재선 상세 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineDetailByTableId(data); // 전결결재선 상세 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineConditionByTableId(data); // 전결결재선 조건 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineByTableId(data); // 전결결재선 삭제
			arbitraryManagerRepository.deleteArbitraryDetailTable(data); // 젼결규정 상세테이블 삭제
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 전결규정 전결결재선 목록을 삭제한다.
	 *
	 */
	public ResultMap deleteListArbitraryAprvLine(Map param) {
		List<Map<String, Object>> deleteAprvLines = (List<Map<String, Object>>)param.get("deleteAprvLines");

		for (Map<String, Object> data : deleteAprvLines) {
			arbitraryManagerRepository.deleteArbitraryAprvLineConditionByLineId(data); // 젼결결재선 조건 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineDetailCurrencyByLineId(data); // 젼결결재선 결재자 통화 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineDetailByLineId(data); // 젼결결재선 상세 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLine(data);  // 젼결결재선 삭제
		}
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 전결규정 전결결재선 상세 목록을 삭제한다.
	 *
	 */
	public ResultMap deleteListArbitraryAprvLineDetail(Map param) {
		List<Map<String, Object>> deleteAprvLineDetails = (List<Map<String, Object>>)param.get("deleteAprvLineDetails");

		for (Map<String, Object> data : deleteAprvLineDetails) {
			arbitraryManagerRepository.deleteArbitraryAprvLineDetailCurrencyByApvrUuid(data); // 전결결재선 상세 통화 삭제
			arbitraryManagerRepository.deleteArbitraryAprvLineDetail(data); // 젼결결재선 상세 삭제
		}

		return ResultMap.SUCCESS();
	}
	
	/**
	 * 전결규정 전결결재선 조건 목록을 삭제한다.
	 *
	 */
	public ResultMap deleteListArbitraryAprvLineCondition(Map param) {
		List<Map<String, Object>> deleteAprvLineConditions = (List<Map<String, Object>>)param.get("deleteAprvLineConditions");

		for (Map<String, Object> data : deleteAprvLineConditions) {
			arbitraryManagerRepository.deleteArbitraryAprvLineCondition(data); // 젼결결재선 조건 삭제
		}

		return ResultMap.SUCCESS();
	}
	
	/**
	 * 전결규정 전결결재선 조건을 초기화 한다. 
	 *
	 */
	public Map<String, Object> resetArbitraryDetailFactor(Map param) {
		Map<String, Object> tableData = (Map<String, Object>)param.get("tableData");

		arbitraryManagerRepository.deleteArbitraryDetailFactorByTableId(tableData); // 전결 상세테이블 항목 삭제
		arbitraryManagerRepository.deleteArbitraryAprvLineConditionByTableId(tableData); // 전결결재선 조건 삭제
		arbitraryManagerRepository.insertArbitraryDetailFactor(tableData); // 상세테이블 항목 추가
		
		Map<String, Object> resultMap = Maps.newHashMap();
		return resultMap;
	}
	
	
	public List<Map<String, Object>> selectListArbitrayLine(Map<String, Object> param) {
		
		List<Map<String, Object>> results       = Lists.newArrayList();
		List<Map<String, Object>> approvalLines = Lists.newArrayList();
		Map<String, Object>       appData       = (Map<String, Object>)param.get("appData");
		
		//전결에 설정한 테이블 정보 가져옴
		List<Map<String, Object>> tables  = arbitraryManagerRepository.selectListArbitrayTable(param);

		if(null != tables && tables.size() > 0){  //전결 설정한 테이블이 없을 경우 조회 하지 않도록 처리 (SMARTNINE-4086)
			for(Map<String, Object> table : tables){
				//전결에 설정한 테이블의 항목정보를 가져옴.
				List<Map<String, Object>> factors = arbitraryManagerRepository.selectListArbitrayTableFactor(table);
				
				if(factors.size() == 0) {
					break;
				}
				Map<String, Object> conditions = table;
				conditions.put("factors", factors);
				String appValue = "";
				//row 항목 데이터를 and조건으로 검색하기 위해 하나의 컬럼에 값들을 붙여서 파라미터로 넘긴다.
				for(Map<String, Object> factor : factors){
					String paramKey = (String)factor.get("parm_key");
					if(appData.get(paramKey) != null)
						appValue = appValue.concat((String)appData.get(paramKey));
				}
				conditions.put("rowValue", appValue);
				conditions.put("appAmt", param.get("appAmt"));
				conditions.put("curCcd", appData.get("cur_ccd"));
				//해당 조건에 맞는 전결라인 찾기
				approvalLines = arbitraryManagerRepository.selectListArbitrayApprovalLines(conditions);
				if(approvalLines != null && approvalLines.size() > 0)
					break;
			}

//			if(approvalLines == null || approvalLines.size() == 0){
//				param.put("dapvl_uuid",  tables.get(0).get("dapvl_uuid"));
//				approvalLines = arbitraryManagerRepository.selectListDefaultArbitrayApprovalLines(param);
//			}

			//찾은 전결라인에 속성으로 실제 결재자 찾아 매핑.
			for(Map<String, Object> apporvalLine : approvalLines){
				String type = String.valueOf(apporvalLine.get("apvr_srch_scop_ccd"));
				if("DFTR_DEPT".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("dept_cd",  Auth.getCurrentUserInfo().get("dept_cd"));
					approverParam.put("dept_nm",  Auth.getCurrentUserInfo().get("dept_nm"));
					approverParam.put("jobtit_ccd", apporvalLine.get("apvr_jobtit_ccd"));
					results.add(findApproverByDept(approverParam, apporvalLine));
				}else if("DEPT_DES".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("dept_cd",  apporvalLine.get("apvr_dept_cd"));
					approverParam.put("dept_nm",  apporvalLine.get("dept_nm"));
					approverParam.put("jobtit_ccd", apporvalLine.get("apvr_jobtit_ccd"));
					results.add(findApproverByDept(approverParam, apporvalLine));
				}else if("COWD".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("jobtit_ccd", apporvalLine.get("apvr_jobtit_ccd"));
					results.add(findApproverByUser(approverParam, apporvalLine));

				}else if("APVR_DES".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("usr_id", apporvalLine.get("apvr_id"));
					results.add(findApproverByUser(approverParam, apporvalLine));

				}else if("INCHR_DEPT".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("usr_id", param.get("aprv_incharge_usr_id"));
					Map<String,Object> forwardParam = findApproverByUser(approverParam, apporvalLine);
					forwardParam.put("jobtit_ccd", apporvalLine.get("apvr_jobtit_ccd"));
					results.add(findApproverByDept(forwardParam, apporvalLine));

				}else if("PIC".equals(type)){
					Map<String,Object> approverParam = Maps.newHashMap();
					approverParam.put("usr_id", param.get("aprv_incharge_usr_id"));
					results.add(findApproverByUser(approverParam, apporvalLine));
				}
			}

			//기안자가 전결라인의 결재자인 경우 처리 추가 2019.11.07 추가
			int index = existsAppover(results);
			if(index > -1){
				results = results.subList(index+1, results.size());
			}
		} else {
			// 전결에 설정한 테이블 정보가 없을 경우
			return results;
		}

		return setUpLastApprovalUserType(results);
	}

	/**
	 * 최종 결재자를 검색해서 처리 한다.
	 * @param results
	 * @return
	 */
	private List<Map<String, Object>> setUpLastApprovalUserType(List<Map<String, Object>> results) {

		if(null != results && results.size() > 0) {
			int lastApprovalIndex = 0;
			Map<String, Object> lastApprovalInfo = Maps.newHashMap();
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> approvalInfo = results.get(i);
				if (approvalInfo.get("apvr_typ_ccd").equals("APVL")) {
					lastApprovalIndex = i;
					lastApprovalInfo = approvalInfo;
				}
			}

			lastApprovalInfo.put("last_apvr_yn", "Y");

			results.set(lastApprovalIndex, lastApprovalInfo);
		}

		return results;
	}

	//기안자가 전결 결재자에 포함되어 있는 경우.
	private int existsAppover(List<Map<String,Object>> lines){
		int index = 0;
		String userId = Auth.getCurrentUserName();
		for(Map<String, Object> approver : lines){
			if(userId.equals(approver.get("usr_id")))
				if(!"R".equals(approver.get("apvr_typ_ccd")))
					break;
			index++;
		}
		if(lines.size() <= index)
			index = -1;
		return index;
	}
	
	//부서 직책으로 결재자 검색
	private Map<String, Object> findApproverByDept(Map<String, Object> approverParam, Map<String, Object> apporvalLine) {
		Map<String, Object> result;
		List<Map<String, Object>> approver = arbitraryManagerRepository.selectListArbitrayApproverByDept(approverParam);

		if(approver != null && approver.size() > 0)
			result = approver.get(0);
		else
			result = createEmptyApprover(approverParam);
		result.put("apvlln_mod_poss_yn", apporvalLine.get("apvlln_mod_poss_yn"));
		result.put("apvr_typ_ccd", apporvalLine.get("apvr_typ_ccd"));
		return result;
	}
	
	//사용자 id/직책만으로 결재자 검색
	private Map<String, Object> findApproverByUser(Map<String, Object> approverParam, Map<String, Object> apporvalLine) {
		Map<String, Object> result;
		List<Map<String, Object>> approver =  arbitraryManagerRepository.selectListArbitrayApproverByUser(approverParam);

		if(approver != null && approver.size() > 0)
			result = approver.get(0);
		else
			result = createEmptyApprover(approverParam);
		result.put("apvlln_mod_poss_yn", apporvalLine.get("apvlln_mod_poss_yn"));
		result.put("apvr_typ_ccd", apporvalLine.get("apvr_typ_ccd"));
		return result;
	}
	
	private Map<String, Object> createEmptyApprover(Map<String, Object> approverParam) {
		Map<String, Object> empty = Maps.newHashMap();
		empty.put("dept_nm", approverParam.get("DEPT_NM"));
		empty.put("usr_nm",  messageSource.getMessage("STD.APR3005", null, "결재자를 찾을 수 없습니다.", LocaleContextHolder.getLocale()));
		return empty;
	}

	/**
	 * 적용종료일 최대값
	 */
	private Date maxApplyEd() {
		Calendar cal = Calendar.getInstance();
		cal.set(2100, Calendar.DECEMBER, 31); // 2100-12-31
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 날짜 더하기
	 */
	private Date getPlusDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);  
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTime().getTime() + (1000 * 60 * 60 * 24 * days));
	}

	public FloaterStream selectListArbitraryAprvType(Map param) {
		return arbitraryManagerRepository.selectListArbitraryAprvType(param);
	}

	public FloaterStream selectListArbitraryHeader(Map param) {
		return arbitraryManagerRepository.selectListArbitraryHeader(param);
	}

	public FloaterStream selectListArbitraryDetailFactor(Map param) {
		return arbitraryManagerRepository.selectListArbitraryDetailFactor(param);
	}


	public List selectListArbitraryAprvLineWithDetail(Map param) {
		List<String> parmKeyVals = (List) param.get("parm_key_vals");
		List<Map<String, Object>> ret = arbitraryManagerRepository.selectListArbitraryAprvLineWithDetail(param);

		// 전결 결재선 조직 아닌 대상이 없는 경우
		if(parmKeyVals != null) {
			// 전결 결재선 조직 아닌 대상이 없는 경우 (Default task만 있는 경우)
			if(parmKeyVals.size() == 1 && parmKeyVals.contains("DFLT_TASK")) {
				return ret;
			} else {
				// 조직이 아닌 결재선 대상을 포함하고 있는지 확인 후 true라면 filtered에 결재선 추가 후 return
				if(ret != null && ret.size() > 0) {
					List<Map<String, Object>> filtered = new ArrayList<>();
					ret.stream().forEach(item -> {
						Set<String> flags = new HashSet<>();
						List<String> aprvLineDetails = (List<String>) item.get("apvlln_inpvars");
						for(int i=0; i<aprvLineDetails.size(); i++) {
							for(int j=0; j<parmKeyVals.size(); j++) {
								if(aprvLineDetails.get(i).equals(parmKeyVals.get(j))) {
									flags.add(parmKeyVals.get(j));
									break;
								}
							}
						}
						if(flags.size() == parmKeyVals.size()) {
							filtered.add(item);
						}
					});
					return filtered;
				}
			}
		}
		return ret;
	}

	public FloaterStream selectListArbitraryAprvLineDetail(Map param) {
		return arbitraryManagerRepository.selectListArbitraryAprvLineDetail(param);
	}

	/**
	 * 전결 업무 유형 조직 입력변수 조회
	 */
	public List selectListArbitraryAprvLineCondition(Map param) {
		return arbitraryManagerRepository.selectListArbitraryAprvLineCondition(param);
	}

	/**
	 * 전결 업무 유형 비조직 입력변수 조회
	 */
	public List<Map<String, Object>> selectListArbitraryAprvLineConditionCcd(Map param) {

		List<Map<String,Object>> sqlFactors = (List<Map<String,Object>>)param.get("dapvl_cnd_inpvar_sqlFactors");
		List<Map<String, Object>> sqlRet =  new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ret = arbitraryManagerRepository.selectListArbitraryAprvLineConditionCcd(param);
		Boolean defaultFlag = Boolean.FALSE;

		// 구매그룹 (sql)이 전결 업무 상세 유형에 있는 경우
		if(sqlFactors != null) {
			if(sqlFactors.size() > 0) {
				sqlFactors.stream().forEach(item -> {
					List<Map<String, Object>> factors = arbitraryDataSourceService.selectListDataSource(item);
					if(factors != null && factors.size() > 0) {
						factors.forEach(factor -> {
							factor.put("dapvl_uuid", item.get("dapvl_uuid"));
							factor.put("dapvl_tbl_id", item.get("dapvl_tbl_id"));
							factor.put("dapvl_cnd_inpvar_uuid", item.get("dapvl_cnd_inpvar_uuid"));
							factor.put("ten_id", item.get("ten_id"));
							factor.put("parm_key", item.get("dapvl_cnd_inpvar_from_id"));
							factor.put("parm_key_val", factor.get("purc_grp_cd"));
							factor.put("parm_nm", item.get("dapvl_cnd_inpvar_from_nm"));
							factor.put("parm_nm_val", factor.get("purc_grp_nm"));
							sqlRet.add(factor);
						});
					}
				});
			}
		}
		ret.addAll(sqlRet);

		// 전결 업무 상세 유형에 비조직 입력변수가 없는 경우, Default Task를 추가
		if(ret.isEmpty()) {
			ret = arbitraryManagerRepository.selectDefaultArbitraryAprvLineConditionCcd(param);
			defaultFlag = Boolean.TRUE;
		}

		// 비조직 입력변수 모든 조합 생성
		Map<String, List<Map<String, Object>>> groupedByUuid = new HashMap<>();
		for (Map<String, Object> item : ret) {
			String uuid = (String) item.get("dapvl_cnd_inpvar_uuid");
			groupedByUuid.computeIfAbsent(uuid, k -> new ArrayList<>()).add(item);
		}

		// 모든 조합을 생성
		List<Map<String, Object>> combinations = new ArrayList<>();
		if (!groupedByUuid.isEmpty()) {
			combinations = generateCombinations(groupedByUuid);
		}

		// 각각 결재선 개수 조회
		List<Map<String, Object>> apvllns = arbitraryManagerRepository.selectListArbitraryAprvLineWithDetail(param);
		if(apvllns != null && combinations != null && !combinations.isEmpty()) {
			if(!defaultFlag) {
				combinations.stream().forEach(item -> {
					List<Map<String, Object>> children = (List<Map<String, Object>>)item.get("children");
					List<String> parmKeyVals = children.stream()
							.map(child -> (String)child.get("parm_key_val"))
							.collect(Collectors.toList());

					List<Map<String, Object>> filtered = new ArrayList<>();
					apvllns.forEach(apvlln -> {
						Set<String> flags = new HashSet<>();
						List<String> aprvLineDetails = (List<String>) apvlln.get("apvlln_inpvars");
						for(int i=0; i<aprvLineDetails.size(); i++) {
							for(int j=0; j<parmKeyVals.size(); j++) {
								if(aprvLineDetails.get(i).equals(parmKeyVals.get(j))) {
									flags.add(parmKeyVals.get(j));
									break;
								}
							}
						}
						if(flags.size() == parmKeyVals.size()) {
							filtered.add(apvlln);
						}
					});

					if(filtered.size() > 0) {
						item.put("apvlln_cnt", filtered.size());
						BigDecimal totalInpvarsCnt = filtered.stream()
								.map(m -> (BigDecimal) m.getOrDefault("cond_count", BigDecimal.ZERO))
								.reduce(BigDecimal.ZERO, BigDecimal::add);
						item.put("apvlln_inpvars_cnt", totalInpvarsCnt.subtract(BigDecimal.ONE)); // 업무 조합과 결재선 연결을 위한 더미 데이터 개수 제거
					} else {
						item.put("apvlln_cnt", 0);
						item.put("apvlln_inpvars_cnt", 0);
					}
				});
			} else { // default task인 경우
				BigDecimal totalInpvarsCnt = apvllns.stream()
						.map(m -> (BigDecimal) m.getOrDefault("cond_count", BigDecimal.ZERO))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				combinations.stream().forEach(item -> {
					item.put("apvlln_inpvars_cnt", totalInpvarsCnt);
					item.put("apvlln_cnt", apvllns.size());
				});
			}

		}

		// 최종 결과 반환
		return combinations;
	}

	public Map selectArbitrayYnByAprvType(Map param) {
		return arbitraryManagerRepository.selectArbitrayYnByAprvType(param);
	}

	/**
	 * 전결 결재선 생성가능 체크
	 */
	public ResultMap validateDapvlTbl(Map param) {
		Map<String, Object> res = arbitraryManagerRepository.validateDapvlTbl(param);

		if(res != null) return ResultMap.SUCCESS();
		else return ResultMap.FAIL();
	}

	/**
	 * 전결 업무 유형 확정가능 체크
	 */
	public ResultMap validateFixable(Map param) {
		Map<String, Object> res = arbitraryManagerRepository.validateFixable(param);

		if(res != null)  {
			if(BigDecimal.ZERO.compareTo((BigDecimal)res.get("cond_count")) < 0) {
				Map<String, Object> paramForCcdCondition = (Map<String, Object>)param.get("param_for_cond");
				if(paramForCcdCondition != null) {
					List<Map<String, Object>> conditions = this.selectListArbitraryAprvLineConditionCcd(paramForCcdCondition);
					ResultMap ret = ResultMap.SUCCESS();
					for(int i=0; i<conditions.size(); i++) {
						if (new BigDecimal(conditions.get(i).getOrDefault("apvlln_cnt", 0).toString()).compareTo(BigDecimal.ZERO) == 0
								|| new BigDecimal(conditions.get(i).getOrDefault("apvlln_inpvars_cnt", 0).toString()).compareTo(BigDecimal.ZERO) == 0) {
							// 전결 업무 결재선 or 결재선 조직 지정이 안된 업무 유형 조합이 있어도 확정 가능함.
							// 만약 모든 유형 조합에 결재선 + 조직 지정을 해야 확정가능으로 변경하려면 아래 코드로 변경
							// ret = ResultMap.FAIL();
							ret = ResultMap.SUCCESS();
							break;
						}
					}
					return ret;
				} else return ResultMap.FAIL();
            } else return ResultMap.FAIL();
		}
		return ResultMap.FAIL();
	}

	/**
	 * 전결 업무 유형 비조직 입력변수 조합 생성
	 */
	private List<Map<String, Object>> generateCombinations(Map<String, List<Map<String, Object>>> groupedByUuid) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<String> keys = new ArrayList<>(groupedByUuid.keySet());

		// keys가 비어 있는지 확인
		if (keys.isEmpty()) {
			return result; // 빈 리스트 반환
		}

		generateCombinationsRecursive(groupedByUuid, keys, 0, new HashMap<>(), result);

		return result;
	}

	/**
	 * 전결 업무 유형 비조직 입력변수 조합 생성
	 */
	private void generateCombinationsRecursive(Map<String, List<Map<String, Object>>> groupedByUuid, List<String> keys, int index, Map<String, Object> currentCombination, List<Map<String, Object>> result) {
		if (index == keys.size()) {
			// 새로운 조합을 만들기 위한 children 리스트
			List<Map<String, Object>> children = new ArrayList<>();
			for (String key : keys) {
				children.add((Map<String, Object>) currentCombination.get(key));
			}

			// 조합을 결과에 추가
			Map<String, Object> combination = new HashMap<>();
			Map<String, Object> firstItem = (Map<String, Object>) currentCombination.get(keys.get(0));
			combination.put("dapvl_tbl_id", firstItem.get("dapvl_tbl_id"));
			combination.put("dapvl_uuid", firstItem.get("dapvl_uuid"));
			combination.put("children", children);

			result.add(combination);
			return;
		}

		String key = keys.get(index);
		for (Map<String, Object> value : groupedByUuid.get(key)) {
			currentCombination.put(key, value);
			generateCombinationsRecursive(groupedByUuid, keys, index + 1, currentCombination, result);
		}
	}

	/**
	 * 전결 업무 유형 비조직 Default 입력변수 생성
	 */
	public List<Map<String, Object>> selectDefaultArbitraryDetailFactor(Map param) {
		return arbitraryManagerRepository.selectDefaultArbitraryDetailFactor(param);
	}

	public List searchArbitraryApprovalLineCurrency(Map param) {
		return arbitraryManagerRepository.searchArbitraryApprovalLineCurrency(param);
	}
}
