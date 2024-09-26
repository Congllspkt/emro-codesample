package smartsuite.app.common;

public class PfmcConst {

	// 평가 생성 요청 이벤트 파라미터
	public static final String TEN_ID = "ten_id"; //  테넌트 아이디
	public static final String EVAL_REQ_UUID = "eval_req_uuid"; // 평가요청 아아디
	public static final String EVAL_SUBJ_LIST = "evalSubjList"; // 평가대상 목록
	public static final String EVAL_SUBJ_EVALTR_LIST      = "evalSubjEvaltrList"; // 평가 요청 이벤트 수신 파라미터의 평가대상 평가자 목록 키
	public static final String PE_UUID = "pe_uuid"; // 퍼포먼스 평가 요청 아아디
	public static final String PE_SUBJ_UUID = "pe_subj_uuid"; // 퍼포먼스 대상 아이디
	public static final String EVAL_TASK_TYP_CCD = "eval_task_typ_ccd"; // 평가업무유형 공통코드
	public static final String PE_STS_CCD = "pe_sts_ccd"; // 퍼포먼스평가요청 상태 공통코드
	public static final String EVALTR_TYP_CCD = "evaltr_typ_ccd"; // 평가자 유형 공통코드
	public static final String EVALFACT_EVALTR_AUTHTY_CCD = "evalfact_evaltr_authty_ccd"; // 평가항목 평가자 권한 공통코드
	public static final String SLFCK_SUBJ_YN = "slfck_subj_yn"; // 자체점검 대상 여부
	public static final String SLFCK_RES_YN = "slfck_res_yn"; // 자체점검 결과 여부
	public static final String EVAL_SUBJ_RES_UUID = "eval_subj_res_uuid"; // 평가대상 결과 아이디
	public static final String EVAL_SUBJ_EVALTR_RES_UUID = "eval_subj_evaltr_res_uuid"; // 평가대상 평가자 결과 아이디
	public static final String PE_SUBJ_EVALTR_UUID = "pe_subj_evaltr_uuid"; // 퍼포먼스평가대상 평가자 아이디
	public static final String PE_SUBJ_RES_UUID = "pe_subj_res_uuid"; // 퍼포먼스평가대상 결과 아이디
	
	public static final String NOT_SUBM_EVALTR_CNT = "not_subm_evaltr_cnt"; // 미제출 평가자 수
	// 기본값 정의
	public static final String EVAL_TASK_TYP_PE = "PE"; // 평가업무유형 공통코드 - 퍼포먼스
	

	// 요청 유형 정의
	public static final String REQ_TYPE = "req_type"; // 가능 목록 : PE, PE_SUBJ, PE_SUBJ_RES, EVAL_SUBJ_RES
	public static final String EVAL_SUBJ_RES = "EVAL_SUBJ_RES";
	public static final String PE_SUBJ_RES = "PE_SUBJ_RES";
	
	// 유효성 검사 유형 정의
	public static final String EFCT_EVALSHT_YN = "efct_evalsht_yn";  // 퍼포먼스 평가그룹에 저장된 평가시트가 현재 유효한 평가시트 여부
	public static final String EFCT_VMG_YN = "efct_vmg_yn";  // 퍼포먼스 평가그룹 평가대상의 협력사관리그룹이 현재 평가그룹의 협력사관리그룹과 일치하는지 여부
	public static final String EXISTS_PE_SUBJ_YN = "exists_pe_subj_yn";   // 퍼포먼스 평가그룹에 평가대상이 존재하는지 여부
	public static final String EXISTS_PE_EVALTR_YN = "exists_pe_evaltr_yn";  // 퍼포먼스 평가그룹 평가대상에 평가자가 존재하는지 여부
	public static final String EFCT_AUTHTY_YN = "efct_authty_yn";  // 퍼포먼스 평가그룹의 평가대상 평가항목권한이 평가템플릿의 평가항목권한과 일치하는지 여부

	// 평가 생성 진행 상태 정의
	public static final String CREATE_PRGS_STS = "create_prgs_sts"; //  QUANT, QUALI
	public static final String QUANT = "QUANT"; // 정량
	public static final String QUALI = "QUALI"; // 정성

	public static final String Y_STR_VAL = "Y"; // Y
	public static final String N_STR_VAL = "N"; // N
	public static final String NOT_EXISTS = "N";  // 미존재
	public static final String RE_CALC_YN = "re_calc_yn";  // 퍼포먼스 평가 재계산 여부

	public static final String DAT_COLL_XCEPT_CALCFACT_LIST = "dat_coll_xcept_calcfact_list"; // 계산항목 집계 제외 목록

	public static final String PFMC_DFLT_VAL = "-1"; // 평가 기본 값

	public static final String ADD_CRE_YN = "add_cre_yn"; // 대상 추가 여부
	public static final String EVALFACT_EVALTR_AUTHTY_DFLT_VAL = "ALL"; // 평가항목 평가자 권한 기본 값

	// 퍼포먼스 평가 관련 object
	public static final String PE = "PE"; // 퍼포먼스평가 요청
	public static final String PE_SUBJ = "PE_SUBJ"; // 퍼포먼스평가 대상
	public static final String PE_PEG = "PE_PEG";  //  퍼포먼스평가 퍼포먼스평가그룹
	public static final String PE_SUBJ_EVALTR = "PE_SUBJ_EVALTR"; // 퍼포먼스평가 평가대상 평가자
	public static final String PFMC_EVALSHT = "PFMC_EVALSHT";  //  퍼포먼스평가 시트

	// 퍼포먼스평가상태 공통코드
	public static final String PE_STS_CRNG = "CRNG";	// 작성중
	public static final String PE_STS_CRN_WTG = "CRN_WTG";	// 생성 대기
	public static final String PE_STS_CRN_ERR = "CRN_ERR";	// 생성 오류
	public static final String PE_STS_CALCFACT_DAT_COLL = "CALCFACT_DAT_COLL";	// 계산항목 데이터 수집
	public static final String PE_STS_CALCFACT_DAT_COLL_ERR = "CALCFACT_DAT_COLL_ERR";	// 계산항목 데이터 수집 오류
	public static final String PE_STS_QUALI_EVAL_PRGSG = "QUALI_EVAL_PRGSG";	// 정성 평가 진행중
	public static final String PE_STS_EVAL_RES_CALC_ERR = "EVAL_RES_CALC_ERR";	// 평가 결과 계산 오류
	public static final String PE_STS_EVAL_RES_CALC_CMPLD = "EVAL_RES_CALC_CMPLD";	// 평가 결과 계산 완료
	public static final String PE_STS_APVL_PRGSG = "APVL_PRGSG";	// 결재 진행중
	public static final String RET = "RET";	// 반려
	public static final String APVD = "APVD";	// 승인

	public static final String EVALFACT_AUTHTY_PIC = "EVALFACT_AUTHTY_PIC"; // 평가항목 권한 담당자
	public static final String VMG_PIC = "VMG_PIC"; // 소싱그룹 담당자
	public static final String PURC_PIC = "PURC_PIC"; // 담당자


	public static final String RESULT_CODE  = "ret_cd"; // Procedure return code
	public static final String RESULT_MSG   = "ret_msg"; // Procedure return message

	// 삭제 유형
	public static final String DELETE_TYPE = "del_typ";

	public class PE_SUBJ_EVALTR_UUID {
	}
}
