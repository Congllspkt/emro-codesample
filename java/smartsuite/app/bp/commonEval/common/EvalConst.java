package smartsuite.app.bp.commonEval.common;

public class EvalConst {
	// field 정의
	public static final String TEN_ID                       = "ten_id"; // 테넌트 아이디
	public static final String EVAL_REQ_UUID                = "eval_req_id"; // 평가요청 아이디
	public static final String OORG_CD                      = "oorg_cd"; // 운영조직코드
	public static final String EVAL_SUBJ_RES_UUID           = "eval_subj_res_uuid"; // 평가 대상 결과 UUID
	public static final String EVAL_SUBJ_EVALTR_RES_UUID    = "eval_subj_evaltr_res_uuid"; // 평가 대상 평가자 결과 UUID
	public static final String EVAL_TASK_TYP_CCD            = "eval_task_typ_ccd"; // 평가업무구분코드 (R704)
	public static final String EVALTMPL_UUID                = "evaltmpl_uuid"; // 평가템플릿 UUID
	public static final String EVAL_REQ_RES_STS_CCD         = "eval_req_res_sts_ccd"; // 평가 요청 결과 상태 공통코드
	public static final String EVAL_SC                      = "eval_sc"; // 평가 점수
	public static final String EVALFACT_UUID                = "evalfact_uuid"; // 평가항목 아이디
	public static final String EFACTG_UUID                  = "efactg_uuid"; // 평가항목군 아이디
	public static final String CALC_SUBJ_EVALFACT_TYP     = "calc_subj_evalfact_typ"; // 집계 대상 : 정량정성 구분
	public static final String COLL_VAL_REQ_YN              = "coll_val_req_yn";   // 계산항목 값 수집 요청 여부
	public static final String CALC_VAL_REQ_YN              = "calc_val_req_yn";   // 계산항목 값 계산 요청 여부
	public static final String RESULT_CODE                  = "ret_cd"; // Procedure return code
	public static final String RESULT_MSG                   = "ret_msg"; // Procedure return message

	public static final String EVAL_SUBJ_LIST              = "evalSubjList"; // 평가 요청 이벤트 수신 파라미터의 평가대상 목록 키
	public static final String EVAL_SUBJ_EVALTR_LIST      = "evalSubjEvaltrList"; // 평가 요청 이벤트 수신 파라미터의 평가대상 평가자 목록 키

	// 기본값 정의
	public static final String Y_STR_VAL = "Y"; // Y
	public static final String N_STR_VAL = "N"; // N
	public static final String SRM_DEF_VAL = "-1"; // 평가 기본 값
	public static final String ERROR_CODE = "-1"; // Procedure return error code
	public static final String VALID_EVAL = "VALID"; // Procedure return error code
	public static final String DEF_ERR_MSG = "No Error Message."; // Procedure return error code
	public static final String SUCCESS_CODE = "0"; // Procedure return success code
	public static final String ROOT = "ROOT"; // ROOT

	// 평가 API 호출 시 필수 필드 정의
	public static final String[] EVAL_SUBJ_REQ_FIELDS = { "TEN_ID", "EVALTMPL_UUID", "EVAL_TASK_TYP_CCD"};  // 평가대상 결과 테이블 AK
	public static final String[] EVAL_FACT_REQ_FIELDS = { "TEN_ID", "EVAL_SUBJ_RES_UUID", "EVALTMPL_UUID"};  // 평가대상 별로 평가항목 결과 추가 시 필요한 key
	public static final String[] EVAL_SUBJ_EVALTR_REQ_FIELDS = {"TEN_ID","EVAL_SUBJ_RES_UUID", "EVALFACT_EVALTR_AUTHTY_CCD", "EVALTR_ID"};  // 평가대상 평가자 결과
	public static final String[] EVALTR_EVALFACT_REQ_FIELDS = {  "TEN_ID","EVAL_SUBJ_EVALTR_UUID", "EVALFACT_RES_UUID"};  // 평가자 평가항목 결과

	public static final String[] EVALTR_EVALFACT_SAVE_FIELDS = {  "ten_id", "evalSubjMap", "saveEvaltmplList", "saveEvalfactList", "saveEvalfactScaleList"};  // 평가자 평가항목 결과 저장


	// check 타입 정의
	public static final String NOT_EXISTS = "N";
	public static final String DUPLICATED = "D";

	public static final String CREATE = "C";
	public static final String DELETE = "D";

	// 요청 유형 정의
	public static final String REQ_TYPE = "req_type";
	public static final String ALL = "ALL";
	public static final String SINGLE = "SINGLE";
	public static final String SUBJ = "SUBJ";
	public static final String EVALTR = "EVALTR";

	public static final String CALCFACT_RE_COLL_REQ_YN = "calcfact_re_coll_req_yn"; // 계산항목 재수집 요청 여부

	public static final String CALCFACT_RE_CALC_REQ_YN = "calcfact_re_calc_req_yn"; // 계산항목 재계산 요청 여부

	// 평가 생성 진행 상태 정의
	public static final String CREATE_PRGS_STS = "create_prgs_sts"; //  QUANT, QUALI

	// 운영단위 연결 유형 정의
	public static final String OPER_LINK_SOGO = "SOGO"; // SRM운영단위-소싱그룹운영단위 연결
	public static final String OPER_LINK_SOEO = "SOEO"; // SRM운영단위-협력사운영단위 연결
	public static final String OPER_LINK_SOPO = "SOPO"; // SRM운영단위-구매운영단위 연결
	public static final String OPER_LINK_SOIO = "SOIO"; // SRM운영단위-품목운영단위 연결
	public static final String OPER_LINK_EOSO = "EOSO"; // 협력사운영단위-SRM운영단위 연결

	// 평가업무구분 정의
	public static final String EV_TASK_VE = "VE"; // 등록평가
	public static final String EV_TASK_RE = "RE"; // 정기평가
	public static final String EV_TASK_SE = "SE"; // 관계유형평가
	public static final String EV_TASK_XE = "NPE"; // 비가격평가
	public static final String EV_TASK_TE = "TE"; // TBE평가
	public static final String EV_TASK_GE = "GE"; // GR(입고검수)
	public static final String EV_TASK_PE = "PE"; // PO(준공,기성 평가)
	public static final String EV_TASK_ME = "ME"; // 자재(위험도)평가
	public static final String EV_TASK_CE = "CE"; // 계약평가
	public static final String EV_TASK_ZE = "ZE"; // 기성평가
	public static final String[] EV_TASK_EX = { "NPE", "GE", "PE", "ME", "CE", "ZE" }; // 기타 평가

	// 평가 상태 정의
	public static final String EV_RST_STS_PRE = "Preparing"; // 작성전
	public static final String EV_RST_STS_PRG = "Progressing"; // 작성중
	public static final String EV_RST_STS_CMPL = "Completed"; // 완료

	// 정량/정성 정의
	public static final String QUANT = "QUANT"; // 정량
	public static final String QUALI = "QUALI"; // 정성
	public static final String EVALFACT_TYP_CCD = "evalfact_typ_ccd"; // 항목 유형 공통코드
	public static final String EVALFACT_TYP_CCD_LIST = "evalfact_typ_ccd_list"; // 항목 유형 공통코드
	public static final String[] QUANT_DIV_CCD_LIST = { "QUANT" }; // 정량 구분
	public static final String[] QUALI_DIV_CCD_LIST = { "QUALI", "QUALI_MULTPL_SEL", "QUALI_SC_INP" }; // 정성 구분(단일선택, 다중선택, 직접입력)

	// 추가 조건 공통코드
	public static final String ADD_CND_CCD = "add_cnd_ccd";
	public static final String ADD_CND_CCD_LIST = "add_cnd_ccd_list"; // 평가항목 종류 코드
	public static final String EVALFACT_TYP_GEN = "NA"; // 일반
	public static final String EVALFACT_TYP_EXPNT = "EXPNT"; // 가점
	public static final String EVALFACT_TYP_DEDTN = "DEDTN"; // 감점

	// 정성평가 진행 상태 정의
	public static final String SRV_STS_10 = "10"; // 작성전
	public static final String SRV_STS_20 = "20"; // 작성중
	public static final String SRV_STS_30 = "30"; // 완료

	// 피평가대상 정의
	public static final String EVAL_TARG_SV = "SV"; // 소싱그룹-협력사
	public static final String EVAL_TARG_SG = "SG"; // 소싱그룹
	public static final String EVAL_TARG_VD = "VD"; // 협력사
	public static final String EVAL_TARG_MT = "MT"; // 자재

	// 평가시트 적용단위 정의
	public static final String EVAL_BAS_VT  = "VT"; // 협력사유형

	// 평가자유형 정의
	public static final String CHR_CLS_SV = "SV"; // 소싱그룹-협력사 담당자
	public static final String CHR_CLS_SG = "SG"; // 소싱그룹 담당자
	public static final String CHR_CLS_VT = "VT"; // 협력사유형 담당자
	public static final String CHR_CLS_VD = "VD"; // 협력사 담당자
	public static final String CHR_CLS_DR = "DR"; // 담당자 직접 입력
	public static final String CHR_CLS_IB = "IB"; // Internal Logic
	public static final String CHR_CLS_FT = "FT"; // 평가항목담당자

	// 평가 집계 유형
	public static final String EV_CALC_S = "S"; // 단일 대상 집계
	public static final String EV_CALC_T = "T"; // 전체 집계

	// 평가 항목 종류
	public static final String EF_KIND_NA = "NA"; // 일반
	public static final String EF_KIND_EXPNT = "EXPNT"; // 가점
	public static final String EF_KIND_DEDTN = "DEDTN"; // 감점
	public static final String EF_KIND_PM = "PM"; // 가감점

	// 평가 등급 구분(절대/상대)
	public static final String ABS = "ABS"; // 절대
	public static final String REL = "REL"; // 상대

	// 차별화 적용 코드
	public static final String DIFF_APPLY_CD	= "diff_apply_cd";
	public static final String DIFF	= "DIFF";	// SCT
	public static final String DIFZ	= "DIFZ";	// 지표
	public static final String DIFF_NONE	= "NONE";	// 미적용

	// 공통 코드 정의
	public static final String R999 = "R999"; // 평가 CODE 채번
	public static final String R998 = "R998"; // 평가 생성 오류 코드


	// sql inject filtering string
	public static final String[] ESCAPE_SQL_STR = {
			"SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "UNION", "FROM", "WHERE", "JOIN",
			"SUBSTR", "SUBSTRING", "USER_TABLES", "USER_TABLE_COLUMNS", "SYSOBJECTS", "INFORMATION_SCHEMA", "DECLARE", "TABLE_SCHEMA"
	};
}
