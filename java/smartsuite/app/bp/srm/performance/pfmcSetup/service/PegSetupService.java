package smartsuite.app.bp.srm.performance.pfmcSetup.service;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.srm.performance.pfmcEval.service.PfmcEvalService;
import smartsuite.app.bp.srm.performance.pfmcSetup.event.PfmcSetupEventPublisher;
import smartsuite.app.bp.srm.performance.pfmcSetup.repository.PegSetupRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.data.FloaterStream;

import javax.inject.Inject;
import java.util.*;

/**
 * peg group setup 서비스 Class입니다.
 *
 * @author yjPark
 * @see
 * @FileName PegSetupService.java
 * @package smartsuite.app.bp.performance.pfmcSetup.service
 * @Since 2023. 6. 13
 * @변경이력 : [2023. 6. 13] yjPark 최초작성
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PegSetupService {

	@Inject
	private PegSetupRepository pegSetupRepository;
	
	@Inject
	private SharedService sharedService;

	@Inject
	private PfmcEvalshtSetupService pfmcEvalshtSetupService;

	@Inject
	private PfmcEvalService pfmcEvalService;
	
	@Inject
	private PfmcSetupEventPublisher pfmcSetupEventPublisher;

	/**
	 * 퍼포먼스평가그룹 목록을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the peg list
	 * @Date : 2023. 6. 1
	 * @Method Name : findListPeg
	 */
	public FloaterStream findListPeg(Map<String, Object> param) {
		// 대용량 처리
		return pegSetupRepository.findListPeg(param);
	}

	/**
	 * 퍼포먼스평가그룹 목록을 삭제한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : deleteLisPeg
	 */
	public ResultMap deleteListPeg(Map<String, Object> param){
		List<Map<String, Object>> deletes = (List<Map<String, Object>>)param.get("deleteList");
		
		// 1. 퍼포먼스평가그룹 사용여부 체크 (PE 생성 여부)
		if (deletes != null && !deletes.isEmpty()) {
			for (Map<String, Object> row : deletes) {
				String isCreatePeYn = pfmcEvalService.findCreatePeYnByPeg(row);
				if ("Y".equals(isCreatePeYn)) {  // PE 생성 상태
					// param 상태와 불일치
					return ResultMap.INVALID();
				}
			}
		}

		// 2. DELETE
		if (deletes != null && !deletes.isEmpty()) {
			for(Map<String, Object> row : deletes){
				pegSetupRepository.updatePegByDelete(row);  // 2-1. 퍼포먼스평가그룹 삭제
				pegSetupRepository.updateVmgStsByDelete(row);  // 2-2. 퍼포먼스평가그룹 협력사관리그룹 삭제
				// 2023.06.15, yjPark, 어느레벨까지 'D' 처리할 지 고민필요
				/*pfmcEvalshtSetupService.updatePfmcEvalshtStsByDelete(row);  // 퍼포먼스 시트 삭제*/
			}
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 퍼포먼스평가그룹 저장 전 validation을 체크한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 8. 02
	 * @Method Name : checkValidBeforeSavePeg
	 */
	public ResultMap checkValidBeforeSavePeg(Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();
		Boolean validPass = true;
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pegInfo");
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");
		List<Map<String, Object>> insertTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("insertTargetVmgList");
		List<Map<String, Object>> deleteTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("deleteTargetVmgList");

		List<Map<String, Object>> insertInvalidByUsingVmgDataList = new ArrayList<>();
		List<Map<String, Object>> insertInvalidByBeforeCrngVmgDataList = new ArrayList<>();
		List<Map<String, Object>> deleteInvalidByBeforeCrngVmgDataList = new ArrayList<>();

		// 1. 등록예정 협력사관리그룹 Validation
		if (insertTargetVmgList != null && !insertTargetVmgList.isEmpty()) {
			for (Map<String, Object> row : insertTargetVmgList) {
				// 1-1. 타 퍼포먼스평가그룹에서의 사용여부 체크
				Map<String, Object> pegInfoInUse = pegSetupRepository.findPegInfoInUsingVmg(row);

				if(pegInfoInUse != null){  // 존재
					validPass = false;
					// 1-2. 등록예정 협력사관리그룹, 타 퍼포먼스평가그룹으로 평가 통보 전 요청 건 존재여부 체크
					List<Map<String, Object>> peInfoBeforeCrng = pfmcSetupEventPublisher.findPeInfoBeforeCrngVmg(pegInfoInUse);

					if(peInfoBeforeCrng != null && peInfoBeforeCrng.size() != 0) {  // 존재
						insertInvalidByBeforeCrngVmgDataList.addAll(peInfoBeforeCrng);
					}else{
						insertInvalidByUsingVmgDataList.add(pegInfoInUse);
					}
				}
			}
		}

		// 2. 삭제예정 협력사관리그룹 Validation
		if (deleteTargetVmgList != null && !deleteTargetVmgList.isEmpty()) {
			String oorgCd = pegInfo.get("oorg_cd").toString();
			String pegCd = pegInfo.get("peg_cd").toString();
			String pegNm = pegInfo.get("peg_nm").toString();

			for (Map<String, Object> row : deleteTargetVmgList) {
				// 2-2. 삭제예정 협력사관리그룹, 기존 퍼포먼스평가그룹으로 평가 통보 전 요청 건 존재여부 체크
				row.put("oorg_cd", oorgCd);
				row.put("peg_cd", pegCd);
				row.put("peg_nm", pegNm);
				List<Map<String, Object>> peInfoBeforeCrng = pfmcSetupEventPublisher.findPeInfoBeforeCrngVmg(row);

				if(peInfoBeforeCrng != null && peInfoBeforeCrng.size() != 0) {  // 존재
					validPass = false;
					deleteInvalidByBeforeCrngVmgDataList.addAll(peInfoBeforeCrng);
				}
			}
		}

		ResultMap resultMap = ResultMap.getInstance();
		if(!validPass){
			Map<String, Object> invalidInfo = Maps.newHashMap();
			invalidInfo.put("insertInvalidByUsingVmgDataList", insertInvalidByUsingVmgDataList);
			invalidInfo.put("insertInvalidByBeforeCrngVmgDataList", insertInvalidByBeforeCrngVmgDataList);
			invalidInfo.put("deleteInvalidByBeforeCrngVmgDataList", deleteInvalidByBeforeCrngVmgDataList);
			resultMap.setResultData(invalidInfo);
			resultMap.setResultStatus(ResultMap.STATUS.INVALID_STATUS_ERR);
			return resultMap;
		}

		return resultMap.SUCCESS();
	}
	
	/**
	 * 퍼포먼스평가그룹을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : savePeg
	 */
	public ResultMap savePeg(Map<String, Object> param) {
		Map<String, Object> pegInfo = (Map<String, Object>) param.get("pegInfo");
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");
		Map<String, Object> evalgrdInfo = (Map<String, Object>)param.get("evalgrdInfo");

		String pegUuid = pegInfo.get("peg_uuid") == null ? "" : pegInfo.get("peg_uuid").toString();
		String oorgCd = pegInfo.get("oorg_cd") == null ? "" : pegInfo.get("oorg_cd").toString();
		if("".equals(pegUuid)){  // 신규
			pegUuid = UUID.randomUUID().toString();
			String pegCd = sharedService.generateDocumentNumber("PEG");
			pegInfo.put("peg_uuid", pegUuid);
			pegInfo.put("peg_cd", pegCd);

			// 1. 퍼포먼스평가그룹 Mater 저장
			pegSetupRepository.insertPegMaster(pegInfo);
		}else{  // 수정
			// 1. 퍼포먼스평가그룹 Mater 저장
			this.updatePegMaster(pegInfo);
		}

		// 2. 퍼포먼스평가그룹 - 협력사관리그룹 저장
		Map<String, Object> pegVmgParam = new HashMap<String, Object>();
		pegVmgParam.put("peg_uuid", pegUuid);
		pegVmgParam.put("targetVmgInfo", targetVmgInfo);
		this.saveListPegVmg(pegVmgParam);
		
		// 3. 퍼포먼스평가그룹 - 평가등급 저장
		Map<String, Object> pegEvalGrdParam = new HashMap<String, Object>();
		pegEvalGrdParam.put("peg_uuid", pegUuid);
		pegEvalGrdParam.put("evalgrdInfo", evalgrdInfo);
		this.saveListPegEvalGrd(pegEvalGrdParam);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("peg_uuid", pegUuid);
		data.put("oorg_cd", oorgCd);

		return ResultMap.SUCCESS(data);
	}

	/**
	 * 퍼포먼스평가그룹 - 협력사관리그룹을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : saveListPegVmg
	 */
	public void saveListPegVmg(Map<String, Object> param) {
		String pegUuid = param.get("peg_uuid") == null ? "" : param.get("peg_uuid").toString();
		Map<String, Object> targetVmgInfo = (Map<String, Object>) param.get("targetVmgInfo");
		List<Map<String, Object>> insertTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("insertTargetVmgList");
		List<Map<String, Object>> deleteTargetVmgList = (List<Map<String, Object>>) targetVmgInfo.get("deleteTargetVmgList");

		if (insertTargetVmgList != null && !insertTargetVmgList.isEmpty()) {
			for (Map<String, Object> row : insertTargetVmgList) {
				// 1. 타 퍼포먼스평가그룹에서 사용 중인 insert 협력사관리그룹 삭제
				pegSetupRepository.deletePegVmg(row);

				// 2. 퍼포먼스평가그룹 - 협력사관리그룹 저장
				row.put("peg_uuid", pegUuid);
				pegSetupRepository.insertPegVmg(row);
			}
		}
		if (deleteTargetVmgList != null && !deleteTargetVmgList.isEmpty()) {
			for (Map<String, Object> row : deleteTargetVmgList) {
				pegSetupRepository.deletePegVmg(row);
			}
		}
	}
	
	/**
	 * 퍼포먼스평가그룹 - 평가등급을 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 7. 19
	 * @Method Name : saveListPegEvalGrd
	 */
	public void saveListPegEvalGrd(Map<String, Object> param) {
		String pegUuid = param.get("peg_uuid") == null ? "" : param.get("peg_uuid").toString();
		Map<String, Object> evalgrdInfo = (Map<String, Object>)param.get("evalgrdInfo");
		List<Map<String, Object>> insertEvalgrdList = (List<Map<String, Object>>)evalgrdInfo.get("insertEvalgrdList");
		List<Map<String, Object>> updateEvalgrdList = (List<Map<String, Object>>)evalgrdInfo.get("updateEvalgrdList");
		List<Map<String, Object>> deleteEvalgrdList = (List<Map<String, Object>>)evalgrdInfo.get("deleteEvalgrdList");

		if (insertEvalgrdList != null && !insertEvalgrdList.isEmpty()) {
			for (Map<String, Object> row : insertEvalgrdList) {
				row.put("peg_uuid", pegUuid);
				pegSetupRepository.insertEvalgrd(row);
			}
		}
		if (updateEvalgrdList != null && !updateEvalgrdList.isEmpty()) {
			for (Map<String, Object> row : updateEvalgrdList) {
				pegSetupRepository.updateEvalgrd(row);
			}
		}
		if (deleteEvalgrdList != null && !deleteEvalgrdList.isEmpty()) {
			for (Map<String, Object> row : deleteEvalgrdList) {
				pegSetupRepository.deleteEvalgrd(row);
			}
		}
	}

	/**
	 * 퍼포먼스평가그룹을 조회한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 1
	 * @Method Name : findPeg
	 */
	public ResultMap findPeg(Map<String, Object> param) {
		Map<String, Object> results = new HashMap<String, Object>();

		// 1. 퍼포먼스평가그룹 Mater 조회
		Map<String, Object> pegInfo = pegSetupRepository.findPegMaster(param);
		param.put("peg_uuid", pegInfo.get("peg_uuid"));

		// 2. 퍼포먼스평가그룹 - 협력사관리그룹 조회
		List<Map<String, Object>> pegVmgs = pegSetupRepository.findListPegVmg(param);

		// 3. 퍼포먼스평가그룹 - 평가등급 조회
		List<Map<String, Object>> pegEvalGrd = pegSetupRepository.findListPegEvalGrd(param);

		// 4. 퍼포먼스평가그룹 - 평가시트이력 조회
		List<Map<String, Object>> pfmcEvalshtHis = pfmcEvalshtSetupService.findListPfmcEvalshtHis(param);

		// 5. 퍼포먼스 평가시트 조회
		Map<String, Object> pfmcEvalshtInfo = pfmcEvalshtSetupService.findPfmcCurrentEvalsht(pfmcEvalshtHis);

		results.put("pegInfo", pegInfo);
		results.put("pegVmgs", pegVmgs);
		results.put("pegEvalGrd", pegEvalGrd);
		if(pfmcEvalshtHis.size() > 0){
			pfmcEvalshtHis.set(0, pfmcEvalshtInfo);
		}
		results.put("pfmcEvalshtHis", pfmcEvalshtHis);
		results.put("pfmcEvalshtInfo", pfmcEvalshtInfo);
		return ResultMap.SUCCESS(results);
	}

	/**
	 * 퍼포먼스평가그룹 Mater를 저장한다.
	 *
	 * @author : yjPark
	 * @param param the param
	 * @return the map< string, object>
	 * @Date : 2023. 6. 5
	 * @Method Name : updatePegMaster
	 */
	public ResultMap updatePegMaster(Map<String, Object> param) {
		pegSetupRepository.updatePegMaster(param);

		return ResultMap.SUCCESS();
	}
}
