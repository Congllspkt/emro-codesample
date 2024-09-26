package smartsuite.app.bp.calc.calculationFactor.service;

import com.google.common.base.Strings;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.calc.calculationFactor.repository.CalculationFactorRepository;
import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class CalculationFactorService {

	@Inject
	private CalculationFactorRepository calculationFactorRepository;

	@Inject
	private SharedService sharedService;

	/**
	 * 계산항목 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 26
	 */
	public FloaterStream findListCalcFactor(Map param) {
		// 대용량 처리
		return calculationFactorRepository.findListCalcFactor(param);
	}

	/**
	 * 계산항목 상세정보를 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	public ResultMap saveCalcFactor(Map param) {
		final Map<String, Object> saveParam = (Map<String, Object>) param.get("saveParam");

		if(!saveParam.containsKey("calcfact_uuid") || saveParam.get("calcfact_uuid") == null) {
			// 신규
			final String calcFactUuid = UUID.randomUUID().toString();
			final String calcFactCd = sharedService.generateDocumentNumber("RF");
			saveParam.put("calcfact_uuid", calcFactUuid);
			saveParam.put("calcfact_cd", calcFactCd);
			calculationFactorRepository.insertCalcFactor(saveParam);
		} else {
			// 수정
			// 데이터 수집방법이 procedure가 아닌 경우 prcr_cd(기존에 선택했던 prcr_cd가 남아있을 수 있으므로)
			if(!saveParam.get("dat_collmeth_ccd").equals("PRCR")) {
				saveParam.put("prcr_cd", null);
			}

			// 데이터 수집방법이 집계 Table인 경우 조건 컬럼 update
			if(saveParam.get("dat_collmeth_ccd").equals("DAT_TBL")) {
				this.saveCalcCndColumn(param);
			}
			calculationFactorRepository.updateCalcFactor(saveParam);
		}

		// 조건 컬럼 저장
		this.saveCalcCndColumn(param);

		return ResultMap.SUCCESS();
	}

	/**
	 * 계산항목의 조건 컬럼 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 11
	 */
	private void saveCalcCndColumn(Map param) {
		final List<Map<String, Object>> cndColList = (List<Map<String, Object>>) param.get("cndColList");
		final Map<String, Object> saveParam = (Map<String, Object>) param.get("saveParam");

		// 삭제
		calculationFactorRepository.deleteListCndColumn(saveParam);

		// 저장
		if(cndColList.size() > 0) {
			for(Map cndCol : cndColList) {
				cndCol.put("calcfact_uuid", saveParam.get("calcfact_uuid"));
				calculationFactorRepository.insertCndColumn(cndCol);
			}
		}

	}


	/**
	 * 계산항목 상세정보를 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	public Map findCalcFactorInfo(Map param) {
		return calculationFactorRepository.findCalcFactorInfo(param);
	}

	/**
	 * 계산항목 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 27
	 */
	public ResultMap deleteListCalcFactor(Map param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deleteList");

		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> delete : deletes) {
				calculationFactorRepository.deleteListCalcFactor(delete);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 프로시저 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public FloaterStream findListProcedureCode(Map param) {
		return calculationFactorRepository.findListProcedureCode(param);
	}

	/**
	 * 프로시저 목록을 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public ResultMap saveListProcedureCode(Map param) {
		final List<Map<String, Object>> insertList = (List<Map<String, Object>>) param.get("insertList");
		final List<Map<String, Object>> updateList = (List<Map<String, Object>>) param.get("updateList");

		if(!insertList.isEmpty() && insertList.size() > 0) {
			// 신규
			for(Map<String, Object> insert : insertList) {

				// 프로시저명 중복 체크
				if(checkDuplicatedProcedureName(insert).equals("Y")) {
					return ResultMap.DUPLICATED();
				}

				String prcrCd = sharedService.generateDocumentNumber("PRCR");
				insert.put("prcr_cd", prcrCd);
				calculationFactorRepository.insertListProcedureCode(insert);
			}
		} else if(!updateList.isEmpty() && updateList.size() > 0){
			// 수정
			for(Map<String, Object> update : updateList) {
				
				// 프로시저명 중복 체크
				if(checkDuplicatedProcedureName(update).equals("Y")) {
					return ResultMap.DUPLICATED();
				}

				calculationFactorRepository.updateListProcedureCode(update);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 프로시저명을 중복 체크한다.
	 *
	 * @author : jskim
	 * @param procedure the param
	 * @Date : 2023. 7. 13
	 */
	private String checkDuplicatedProcedureName(Map procedure) {
		// 프로시저명 중복체크
		Map<String, Object> duplicatedYn = calculationFactorRepository.checkDuplicatedProcedureName(procedure);
		return (String) duplicatedYn.get("duplicated_yn");
	}

	/**
	 * 프로시저 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public ResultMap deleteListProcedureCode(Map param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deleteList");

		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> delete : deletes) {
				calculationFactorRepository.deleteListProcedureCode(delete);
				calculationFactorRepository.deleteListProcedureParameterByPrcrCd(delete);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 파라미터 목록을 조회한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public FloaterStream findListParameterCode(Map param) {
		return calculationFactorRepository.findListParameterCode(param);
	}

	/**
	 * 파라미터 목록을 저장한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public ResultMap saveListParameterCode(Map param) {
		// 파라미터(parm)에 저장할 list
		final List<Map<String, Object>> insertList = (List<Map<String, Object>>) param.get("insertList");
		final List<Map<String, Object>> updateList = (List<Map<String, Object>>) param.get("updateList");

		// 프로시저 파라미터(prcr_parm)에 저장할 list
		final List<Map<String, Object>> prcrParmList = (List<Map<String, Object>>) param.get("prcrParmList");
		final String prcrCd = (String) param.get("prcrCd");

		// 파라미터(parm)에 저장
		if(!insertList.isEmpty() && insertList.size() > 0) {
			// 신규
			for(Map<String, Object> insert : insertList) {

				// 파라미터명 중복 체크
				if(checkDuplicatedParameterName(insert).equals("Y")) {
					return ResultMap.DUPLICATED();
				}
				
				String parmCd = sharedService.generateDocumentNumber("PARM");
				insert.put("parm_cd", parmCd);
				calculationFactorRepository.insertListParameterCode(insert);
			}
		} else if(!updateList.isEmpty() && updateList.size() > 0){
			// 수정
			for(Map<String, Object> update : updateList) {

				// 파라미터명 중복 체크
				if(checkDuplicatedParameterName(update).equals("Y")) {
					return ResultMap.DUPLICATED();
				}

				calculationFactorRepository.updateListParameterCode(update);
			}
		}

		// 프로시저파라미터 기존 data delete
		Map<String, Object> deleteParam = new HashMap<>();
		deleteParam.put("prcr_cd", prcrCd);
		// prcr_cd 기준 모든 파라미터 삭제
		calculationFactorRepository.deleteListProcedureParameterByPrcrCd(deleteParam);

		// 프로시저파라미터(prcr_parm)에 저장
		if(!prcrParmList.isEmpty() && prcrParmList.size() > 0) {
			for(Map<String, Object> prcrParm : prcrParmList) {
				prcrParm.put("prcr_cd", prcrCd);
				calculationFactorRepository.insertListProcedureParameter(prcrParm);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 파라미터명을 중복 체크한다.
	 *
	 * @author : jskim
	 * @param parameter the param
	 * @Date : 2023. 7. 13
	 */
	private String checkDuplicatedParameterName(Map parameter) {
		// 파라미터명 중복체크
		Map<String, Object> duplicatedYn = calculationFactorRepository.checkDuplicatedParameterName(parameter);
		return (String) duplicatedYn.get("duplicated_yn");
	}

	/**
	 * 파라미터 목록을 삭제한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 6. 28
	 */
	public ResultMap deleteListParameterCode(Map param) {
		final List<Map<String, Object>> deletes = (List<Map<String, Object>>) param.get("deleteList");

		if(deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> delete : deletes) {
				calculationFactorRepository.deleteListParameterCode(delete);
				calculationFactorRepository.deleteListProcedureParameterByParmCd(delete);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 실적 대상 테이블/집계컬럼/조건컬럼 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	public List findListDbTblCol(Map param) {
		List<Map<String, Object>> list = new ArrayList<>();
		final String mode = (String) param.get("mode");

		if(!Strings.isNullOrEmpty(mode)) {
			if(mode.equals("DAT_TBL")) {
				list = calculationFactorRepository.findListDbTable(param);
			} else if(mode.equals("SC")) {
				list = calculationFactorRepository.findListDbColumn(param);
			} else if(mode.equals("CC")) {
				param.put("db_col_phyc_nm", "");
				param.put("db_col_logic_nm", "");
				list = calculationFactorRepository.findListDbColumn(param);
			}
		}

		return list;
	}

	/**
	 * 실적 대상 테이블/집계컬럼/조건컬럼 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	public ResultMap saveDbTblColInfo(Map param) {
		final Map<String, Object> saveParam = (Map<String, Object>) param.get("saveParam");
		String mode = (String) saveParam.get("mode");

		List<Map<String, Object>> inserts = (List<Map<String, Object>>) param.get("insertList");
		List<Map<String, Object>> updates = (List<Map<String, Object>>) param.get("updateList");

		if(!Strings.isNullOrEmpty(mode)) {
			if(mode.equals("DAT_TBL")) {
				this.saveDbTblInfo(inserts, updates);
			} else if(mode.equals("SC") || mode.equals("CC")) {
				this.saveDbColInfo(inserts, updates);
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 실적 대상 테이블 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param inserts the param
	 * @Date : 2023. 8. 4
	 */
	private void saveDbTblInfo(List<Map<String, Object>> inserts, List<Map<String, Object>> updates) {
		if(inserts.size() > 0) { // 신규
			for(Map<String, Object> row : inserts) {
				String dbTblCd = UUID.randomUUID().toString();
				row.put("db_tbl_cd", dbTblCd);
				calculationFactorRepository.insertDbTblInfo(row);
			}
		}

		if(updates.size() > 0) { // 수정
			for(Map<String, Object> row : updates) {
				calculationFactorRepository.updateDbTblInfo(row);
			}
		}
	}

	/**
	 * 실적 대상 컬럼 저장을 요청한다.
	 *
	 * @author : jskim
	 * @param inserts the param
	 * @Date : 2023. 8. 4
	 */
	private void saveDbColInfo(List<Map<String, Object>> inserts, List<Map<String, Object>> updates) {
		if(inserts.size() > 0) { // 신규
			for(Map<String, Object> row : inserts) {
				String dbColCd = UUID.randomUUID().toString();
				row.put("db_col_cd", dbColCd);
				calculationFactorRepository.insertDbColInfo(row);
			}
		}

		if(updates.size() > 0) { // 수정
			for(Map<String, Object> row : updates) {
				calculationFactorRepository.updateDbColInfo(row);
			}
		}
	}

	/**
	 * 실적 대상 테이블 조건목록 조회를 요청한다.
	 *
	 * @author : jskim
	 * @param param the param
	 * @Date : 2023. 8. 4
	 */
	public Map findListDbTblCondition(Map param) {
		final Map<String, Object> resultMap = new HashMap<>();

		final List<Map<String, Object>> conditions = calculationFactorRepository.findListDbTblCondition(param);
		final List<Map<String, Object>> colList = calculationFactorRepository.findListDbTblColumn(param);

		resultMap.put("conditions", conditions);
		resultMap.put("colList", colList);

		return resultMap;
	}


}
