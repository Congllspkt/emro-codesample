package smartsuite.app.bp.law.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.edoc.contract.service.EcontractService;
import smartsuite.app.bp.edoc.template.service.CntrTemplateService;
import smartsuite.app.bp.law.event.LawEventPublisher;
import smartsuite.app.bp.law.repository.LawRepository;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.common.util.EdocStringUtil;
import smartsuite.app.shared.CntrConst;
import smartsuite.app.util.MakingCntrForm;
import smartsuite.data.FloaterStream;
import smartsuite.upload.StdFileService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class LawService {
	
	@Inject
	SharedService shared;
	@Inject
	StdFileService fileService;
	@Inject
	LawRepository lawRepository;
	@Inject
	CntrTemplateService cntrTemplateService;
	@Inject
	EcontractService contractService;
	@Inject
	MakingCntrForm makingCntrForm;
	@Inject
	LawEventPublisher lawEventPublisher;

	/**
	 * 계약검토요청 목록 조회
	 */
	public FloaterStream largeFindReviewReqList(Map param) {
		// 대용량 처리
		return lawRepository.largeFindReviewReqList(param);
	}

	/**
	 * 계약검토요청
	 */
	public ResultMap requestReview(Map param) {
		ResultMap saveResult = this.saveReqReview(param); // 검토요청 정보 저장
		
		Map reviewData = saveResult.getResultData();
		reviewData.put("lgl_rv_sts_ccd", "RV_REQ");
		lawRepository.updateRvSts(reviewData); // 검토상태 업데이트
		
		return ResultMap.SUCCESS(reviewData);
	}

	/**
	 * 계약검토 상세 조회
	 */
	public ResultMap findReqReviewDetail(Map param) {
		Map reviewData = lawRepository.findReqReviewDetail(param); // 계약검토 상세 조회
		List refList = lawRepository.findRefList(param); // 참조자 리스트 조회
		List cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
		List appxList = lawRepository.findReqReviewAppxTmpl(param);

		Map resultData = Maps.newHashMap();
		resultData.put("reviewData", reviewData);
		resultData.put("refList", refList);
		resultData.put("cntrDocList", cntrDocList);
		resultData.put("appxList", appxList);
		
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 검토요청 정보 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveReqReview(Map param) {
		Map reviewData = (Map) param.get("reviewData");
		String rvReqId = (String) reviewData.get("cntrdoc_lgl_rv_uuid");
		String rvReqNo = (String) reviewData.get("cntrdoc_lgl_rv_no"); // 문서번호
		int rvReqRevNo = reviewData.get("cntrdoc_lgl_rv_revno") == null ? 1 : Integer.parseInt(reviewData.get("cntrdoc_lgl_rv_revno").toString()); // 차수
		
		if (Strings.isNullOrEmpty(rvReqNo)) {
			rvReqNo = shared.generateDocumentNumber("LAW");
			rvReqRevNo = 1;
		}
		reviewData.put("cntrdoc_lgl_rv_no", rvReqNo);
		reviewData.put("cntrdoc_lgl_rv_revno", rvReqRevNo);
		
		if(Strings.isNullOrEmpty(rvReqId)) {
			rvReqId = UUID.randomUUID().toString();
			reviewData.put("cntrdoc_lgl_rv_uuid", rvReqId);
			reviewData.put("lgl_rv_sts_ccd", "CCMPLD");
			
			lawRepository.insertReqReview(reviewData); // 검토요청 정보 저장
		} else {
			lawRepository.updateReqReview(reviewData); // 검토요청 정보 수정
			lawRepository.deleteRefList(reviewData); // 참조자 삭제
		}
		
		// 참조자 저장
		this.saveRefList((List<Map<String, Object>>) param.get("refList"), rvReqId);
		
		// 계약서 정보 저장
		String cntrTmplTypCcd = (String)reviewData.get("cntr_tmpl_typ_ccd");
		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(cntrTmplTypCcd)) {
			this.deleteReqReviewTmplList((List<Map<String, Object>>) param.get("deleteCntrDocItems"));
			this.deleteReqReviewTmplList((List<Map<String, Object>>) param.get("deleteAppxItems"));
			this.saveReqReviewTmplList((List<Map<String, Object>>) param.get("cntrDocItems"), rvReqId);
			this.saveReqReviewTmplList((List<Map<String, Object>>) param.get("appxItems"), rvReqId);
		} else {
			lawRepository.deleteReqReviewTmplRvId(reviewData); // 검토요청 템플릿 정보 전체 삭제
		}
		
		Map resultData = Maps.newHashMap();
		resultData.put("cntrdoc_lgl_rv_uuid", rvReqId);
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 계약검토 삭제
	 */
	public ResultMap delReqReview(Map param) {
		lawRepository.deleteReqReviewTmplRvId(param); // 검토요청 템플릿 정보 전체 삭제
		lawRepository.deleteRefList(param); // 참조자 삭제
		lawRepository.delReqReview(param); // 계약검토 삭제

		return ResultMap.SUCCESS();
	}

	/**
	 * ROLE_CD에 해당하는 사용자 정보 조회
	 */
	public List<Map<String, Object>> findUserByRole(Map param) {
		return lawRepository.findUserByRole(param);
	}

	/**
	 * 검토자 지정
	 */
	public ResultMap regReviewer(Map param) {
		lawRepository.regReviewer(param); // 계약 검토자 지정
		
		param.put("lgl_rv_sts_ccd", "RV_PRGSG");
		lawRepository.updateRvSts(param); // 검토진행상태 업데이트
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 검토 정보 저장
	 */
	public ResultMap saveReqConfirm(Map param) {
		lawRepository.deleteRefList(param); // 참조자 삭제
		List refList = (List<Map<String, Object>>) param.get("refList");
		String rvReqId = (String) param.get("cntrdoc_lgl_rv_uuid");
		this.saveRefList(refList, rvReqId); // 참조자 저장
		
		lawRepository.updateRvOpn(param); // 검토의견 업데이트
		 
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 검토승인 요청
	 */
	public ResultMap reqConfirm(Map param) {
		lawRepository.deleteRefList(param); // 참조자 삭제
		List refList = (List<Map<String, Object>>) param.get("refList");
		String rvReqId = (String) param.get("cntrdoc_lgl_rv_uuid");
		this.saveRefList(refList, rvReqId); // 참조자 저장
		
		param.put("lgl_rv_sts_ccd", "APVL_REQ"); // 승인요청
		lawRepository.updateRvSts(param); // 검토진행상태 업데이트
		lawRepository.updateRvOpn(param); // 검토의견 업데이트
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 검토 반려
	 */
	public ResultMap rejectReview(Map param) {
		param.put("lgl_rv_sts_ccd", "RV_RET"); // 검토반려
		lawRepository.updateRvSts(param); // 검토진행상태 업데이트
		lawRepository.updateTmldRvOpn(param); // 팀장의견 업데이트
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 검토 승인
	 */
	public ResultMap confirmReview(Map param) {
		param.put("lgl_rv_sts_ccd", "APVD");
		lawRepository.updateRvSts(param); // 검토진행상태 업데이트
		lawRepository.updateTmldRvOpn(param); // 팀장의견 업데이트
		
		return ResultMap.SUCCESS();
	}

	/**
	 * 참조자 계약검토목록 조회
	 */
	public FloaterStream largeFindReviewReqListByRef(Map param) {
		// 대용량 처리
		return lawRepository.largeFindReviewReqListByRef(param);
	}
	
	/**
	 * 재검토 요청 정보 조회
	 */
	public ResultMap findReReqReviewDetail(Map param) {
		Map reviewData = lawRepository.findReqReviewDetail(param); // 계약검토 상세 조회
		List refList = lawRepository.findRefList(param); // 참조자 리스트 조회
		
		reviewData.put("cntrdoc_lgl_rv_uuid", "");
		reviewData.put("cntrdoc_lgl_rv_revno", Integer.parseInt(String.valueOf(reviewData.get("max_revno")))+1);
		reviewData.put("lgl_rv_opn", "");
		reviewData.put("lgl_rv_dt", "");
		reviewData.put("lgl_tmld_rv_opn", "");
		reviewData.put("lgl_tmld_apvd_dt", "");
		
		// 검토 관련 파일 복사
		String rvReqGrpCd = String.valueOf(reviewData.get("lgl_rv_req_athg_uuid"));
		reviewData.put("lgl_rv_req_athg_uuid", fileService.copyFile(rvReqGrpCd));
		String rvResGrpCd = String.valueOf(reviewData.get("lgl_rv_res_athg_uuid"));
		reviewData.put("lgl_rv_res_athg_uuid", fileService.copyFile(rvResGrpCd));

		// 템플릿 정보 조회
		List<Map<String, Object>> cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
		List<Map<String, Object>> appxList = lawRepository.findReqReviewAppxTmpl(param);
		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(reviewData.get("cntr_tmpl_typ_ccd"))) {
			for(Map<String, Object> cntrDoc : cntrDocList) {
				cntrDoc.put("cntrdoc_lgl_rv_tmpl_uuid", "");
				cntrDoc.put("cntrdoc_lgl_rv_uuid", "");
			}
			for(Map<String, Object> appx : appxList) {
				appx.put("cntrdoc_lgl_rv_tmpl_uuid", "");
				appx.put("cntrdoc_lgl_rv_uuid", "");
			}
		}
		
		Map saveParam = Maps.newHashMap();
		saveParam.put("reviewData", reviewData);
		saveParam.put("refList", refList);
		saveParam.put("cntrDocItems", cntrDocList);
		saveParam.put("appxItems", appxList);
		
		return this.saveReqReview(saveParam);
	}
	
	/**
	 * 참조자 리스트 저장
	 */
	public void saveRefList(List<Map<String,Object>> refList, String rvReqId){
		for(Map<String,Object> ref : refList){
			ref.put("cntrdoc_lgl_rv_uuid", rvReqId);
			lawRepository.insertRef(ref);
		}
	}
	
	/** 
	 * review_req_id에 해당하는 계약건 조회 
	 */
	public List<Map<String, Object>> getCntrListByRvUuid(Map param) {
		return lawRepository.getCntrListByRvUuid(param);
	}
	
	/**
	 * 검토요청 템플릿 정보 조회
	 * @param param
	 * @return
	 */
	public ResultMap findReqReviewTmpl(Map param) {
		List cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
		List appxList = lawRepository.findReqReviewAppxTmpl(param);

		Map resultData = Maps.newHashMap();
		resultData.put("cntrDocList", cntrDocList);
		resultData.put("appxList", appxList);
		
		return ResultMap.SUCCESS(resultData);
	}

	/**
	 * 검토요청 템플릿 리스트 저장
	 * @param tmplList
	 * @param rvReqId
	 */
	private void saveReqReviewTmplList(List<Map<String, Object>> tmplList, String rvReqId) {
		if(tmplList == null || tmplList.isEmpty()) {
			return;
		}
		for(Map<String,Object> tmpl : tmplList){
			String rvReqTmplId = (String) tmpl.get("cntrdoc_lgl_rv_tmpl_uuid");
			if(Strings.isNullOrEmpty(rvReqTmplId)) {
				rvReqTmplId =  UUID.randomUUID().toString();
				tmpl.put("cntrdoc_lgl_rv_tmpl_uuid", rvReqTmplId);
				tmpl.put("cntrdoc_lgl_rv_uuid", rvReqId);
				lawRepository.insertReqReviewTmpl(tmpl);
			} else {
				lawRepository.updateReqReviewTmpl(tmpl);
			}
		}
	}

	/**
	 * 검토요청 템플릿 리스트 삭제
	 * @param tmplList
	 */
	private void deleteReqReviewTmplList(List<Map<String, Object>> tmplList) {
		if(tmplList == null || tmplList.isEmpty()) {
			return;
		}
		for(Map<String,Object> tmpl : tmplList){
			String rvReqTmplId = (String) tmpl.get("cntrdoc_lgl_rv_tmpl_uuid");
			if(!Strings.isNullOrEmpty(rvReqTmplId)) {
				lawRepository.deleteReqReviewTmplByRvTmplId(tmpl);
			}
		}
	}

	/**
	 * 검토요청 템플릿 내용 비교 조회
	 * @param param
	 * @return
	 */
	public ResultMap findReqReviewTmplDiff(Map param) {
		Map resultData = Maps.newHashMap();
		Map reqRvTmpl = lawRepository.findReqReviewTmplCont(param);
		
		if(reqRvTmpl == null || reqRvTmpl.isEmpty()) {
			// 저장된 검토요청 템플릿 정보가 없으면 템플릿 원본 내용을 가져온다.
			String tmplTypCcd = param.get("tmpl_typ_ccd") == null ? "" : (String) param.get("tmpl_typ_ccd");
			Map origTmpl = Maps.newHashMap();
			
			if("CNTRDOC".equals(tmplTypCcd)) {
				origTmpl = cntrTemplateService.findCntrFormCont(param);
				resultData.put("origCont", origTmpl.get("cntrdoc_tmpl_cont"));
				resultData.put("reqRvCont", origTmpl.get("cntrdoc_tmpl_cont"));
			} else if("APPX".equals(tmplTypCcd)) {
				origTmpl = cntrTemplateService.findAppformCont(param);
				resultData.put("origCont", origTmpl.get("appx_tmpl_cont"));
				resultData.put("reqRvCont", origTmpl.get("appx_tmpl_cont"));
			}
			
			resultData.put("isNew", true);
			
			if(origTmpl == null || origTmpl.isEmpty()) {
				return ResultMap.FAIL();
			}
		} else {
			resultData.put("origCont", reqRvTmpl.get("orig_tmpl_cont"));
			resultData.put("reqRvCont", reqRvTmpl.get("lgl_rv_tmpl_cont"));
			resultData.put("rvOpn", reqRvTmpl.get("lgl_rv_opn"));
			resultData.put("rvTmldOpn", reqRvTmpl.get("lgl_tmld_rv_opn"));
			resultData.put("rvResult", reqRvTmpl.get("lgl_rv_tmpl_res"));
			resultData.put("reqRvAthgUuid", reqRvTmpl.get("lgl_rv_athg_uuid"));
			resultData.put("rvResultAthgUuid", reqRvTmpl.get("lgl_rv_res_athg_uuid"));
			resultData.put("isNew", false);
		}
		
		return ResultMap.SUCCESS(resultData);
	}
	
	/**
	 * 검토요청 템플릿 내용 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveReqReviewTmplCont(Map param) {
		lawRepository.updateReqReviewTmplCont(param);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 검토요청 템플릿 검토 의견 저장
	 * @param param
	 * @return
	 */
	public ResultMap saveReqReviewTmplOpn(Map param) {
		lawRepository.updateReqReviewTmplOpn(param);
		return ResultMap.SUCCESS();
	}

	/**
	 * 검토 템플릿으로 계약 작성 화면 조회
	 * @param param
	 * @return
	 */
	public Map findCntrContByRvTmpl(Map param) {
		Map reviewData = lawRepository.findReqReviewDetail(param); // 계약검토 상세 조회
		List cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
		Map<String, Object> cntrInfo = Maps.newHashMap(); // 계약서 정보
		
		/*** 계약서 셋팅 ***/
		// 계약서식을 가져온다.
		Map cntrDocTmpl = (Map) cntrDocList.get(0);
		Map cntrDocTmplCont = Maps.newHashMap();
		String editContent = "";
		if("Y".equals(cntrDocTmpl.get("lgl_rv_subj_yn"))) { // 계약서 템플릿 법무 검토 대상인 경우
			cntrDocTmplCont = lawRepository.findReqReviewTmplCont(cntrDocTmpl);
			editContent = (String) cntrDocTmplCont.get("lgl_rv_tmpl_cont");
		} else {
			cntrDocTmplCont = cntrTemplateService.findCntrFormCont(cntrDocTmpl);
			editContent = (String) cntrDocTmplCont.get("cntrdoc_tmpl_cont");
		}
		
		// 계약서 정보 셋팅
		cntrInfo.put("cntrdoc_tmpl_uuid", String.valueOf(cntrDocTmpl.get("cntrdoc_tmpl_uuid")));
		cntrInfo.put("cntrdoc_tmpl_nm", String.valueOf(cntrDocTmpl.get("cntrdoc_tmpl_nm")));
		cntrInfo.put("cntr_nm"        , reviewData.get("lgl_rv_req_nm"));
		cntrInfo.put("oorg_cd"        , reviewData.get("oorg_cd"));
		cntrInfo.put("vd_cd"          , reviewData.get("vd_cd"));
		cntrInfo.put("cntrdoc_lgl_rv_uuid", reviewData.get("cntrdoc_lgl_rv_uuid")); // 법무검토ID
		cntrInfo.put("cntrpd_auto_ext_use_yn", reviewData.get("cntrpd_auto_ext_use_yn")); // 자동갱신여부
		cntrInfo.put("content"        , editContent); // 수정 계약서 내용
		cntrInfo.put("edit_content"   , editContent); // 원본 계약서 내용
		cntrInfo.put("cntr_typ_ccd"   , reviewData.get("cntr_typ_ccd"));
		cntrInfo.put("cntr_dt"        , reviewData.get("cntr_st_dt"));
		cntrInfo.put("cntr_st_dt"     , reviewData.get("cntr_st_dt"));
		cntrInfo.put("cntr_exp_dt"    , reviewData.get("cntr_exp_dt"));
		cntrInfo.put("cntr_amt"       , reviewData.get("cntr_amt"));
		cntrInfo.put("cur_ccd"        , reviewData.get("cur_ccd"));

		// 계약대상 협력사 정보를 가져온다
		Map compInfo = contractService.findBasicVdInfo(cntrInfo);
		String vdBizRegNo = EdocStringUtil.formatSSN((String) compInfo.get("bizregno"));
		compInfo.put("bizregno", vdBizRegNo);
		String compBizRegNo = EdocStringUtil.formatSSN((String) compInfo.get("comp_biz_reg_no"));
		compInfo.put("comp_biz_reg_no", compBizRegNo);
		
		// 계약서 정보와 협력사 정보를 merge
		cntrInfo = contractService.mapMerge(cntrInfo, compInfo);
				
		// 계약항목 name select
		List<Map<String, Object>> attInputAllList = cntrTemplateService.getAttrAll(param);
		editContent = makingCntrForm.setContent(cntrInfo, attInputAllList, editContent);
		cntrInfo.put("content", editContent);
		
		Map itemParam = Maps.newHashMap();
		itemParam.put("use_yn", "Y");
		itemParam.put("mod_poss_yn", "Y");
		itemParam.put("mand_yn", "Y");
		cntrInfo.put("itemList", cntrTemplateService.searchListCntrItem(itemParam));
		
		return cntrInfo;
	}

	/**
	 * 계약 작성을 위한 계약서 템플릿 내용 조회
	 * @param param
	 * @return
	 */
	public Map findReviewCntrdocTmpl(Map param) {
		List cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
		Map cntrDocTmpl = (Map) cntrDocList.get(0);

		Map cntrDocTmplCont;
		String editContent;
		if("Y".equals(cntrDocTmpl.get("lgl_rv_subj_yn"))) { // 계약서 템플릿 법무 검토 대상인 경우
			cntrDocTmplCont = lawRepository.findReqReviewTmplCont(cntrDocTmpl);
			editContent = (String) cntrDocTmplCont.get("lgl_rv_tmpl_cont");
		} else {
			cntrDocTmplCont = cntrTemplateService.findCntrFormCont(cntrDocTmpl);
			editContent = (String) cntrDocTmplCont.get("cntrdoc_tmpl_cont");
		}

		Map resultMap = Maps.newHashMap();
		resultMap.put("cntrdoc_tmpl_cont", editContent);
		return resultMap;
	}

	/**
	 * 계약 작성을 위한 부속서류 목록 조회
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findListReviewAppxTmpl(Map param) {
		return lawRepository.findListReviewAppxTmpl(param);
	}

	/**
	 * 검토 완료된 게약 작성
	 * @param param
	 * @return
	 */
	public ResultMap saveTemplateContract(Map param) {
		Map reviewData = lawRepository.findReqReviewDetail(param);

		Map cntrInfo = Maps.newHashMap();
		cntrInfo.put("cntrdoc_lgl_rv_uuid", reviewData.get("cntrdoc_lgl_rv_uuid"));
		cntrInfo.put("cntr_nm", reviewData.get("lgl_rv_req_nm"));
		cntrInfo.put("cntr_st_dt", reviewData.get("cntr_st_dt"));
		cntrInfo.put("cntr_exp_dt", reviewData.get("cntr_exp_dt"));
		cntrInfo.put("cntrdoc_typ_ccd", reviewData.get("cntrdoc_typ_ccd"));
		cntrInfo.put("cur_ccd", reviewData.get("cur_ccd"));
		cntrInfo.put("cntr_amt", reviewData.get("cntr_amt"));
		cntrInfo.put("oorg_cd", reviewData.get("oorg_cd"));
		cntrInfo.put("vd_cd", reviewData.get("vd_cd"));
		cntrInfo.put("cntrr_eml", reviewData.get("vd_pic_eml"));
		cntrInfo.put("cntrr_mob", reviewData.get("vd_pic_tel"));

		if(CntrConst.SIGN_METHOD.OFFLINE.equals(reviewData.get("cntr_sgnmeth_ccd"))) {
			cntrInfo.put("cntr_sgnmeth_ccd", CntrConst.SIGN_METHOD.OFFLINE);

		} else {
			cntrInfo.put("cntr_sgnmeth_ccd", reviewData.get("cntr_sgnmeth_ccd"));
		}

		if(CntrConst.TMPL_TYPE.TEMPLATE.equals(param.get("cntr_tmpl_typ_ccd"))) {
			cntrInfo.put("cntr_tmpl_typ_ccd", CntrConst.TMPL_TYPE.TEMPLATE);

			List cntrDocList = lawRepository.findReqReviewCntrDocTmpl(param);
			Map cntrDocTmpl = (Map) cntrDocList.get(0);
			cntrInfo.put("cntrdoc_tmpl_uuid", cntrDocTmpl.get("cntrdoc_tmpl_uuid"));
		} else {
			cntrInfo.put("cntr_tmpl_typ_ccd", CntrConst.TMPL_TYPE.USR_FILE);
			cntrInfo.put("tmpl_unud_cntrdoc_athg_uuid", reviewData.get("lgl_rv_res_athg_uuid"));
		}

		return lawEventPublisher.saveContract(cntrInfo);
	}

}
