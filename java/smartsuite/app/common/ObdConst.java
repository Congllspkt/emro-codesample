package smartsuite.app.common;

public class ObdConst {

	public static final String TEN_ID = "ten_id"; //  테넌트 아이디
	public static final String EVAL_REQ_UUID = "eval_req_uuid"; // 평가요청 아아디
	public static final String EVAL_TASK_TYP_CCD = "eval_task_typ_ccd"; // 평가업무유형 공통코드
	public static final String EVAL_TASK_TYP_OE = "OE"; // 평가업무유형 공통코드 - 퍼포먼스

	// field 정의
	public static final String OE_UUID          = "oe_uuid"; // 온보딩평가 UUID
	public static final String OE_PRCS_UUID     = "oe_prcs_uuid"; // 퍼포먼스 평가 요청 아아디
	public static final String EVAL_TYP_ID      = "eval_typ_id"; // 평가종류아이디
	public static final String OPER_ORG_CD      = "oper_org_cd"; // 운영조직코드
	public static final String EVAL_TYP_CD      = "eval_typ_cd"; // 평가업무구분코드 (R704)
	public static final String EVAL_KIND_CD     = "eval_kind_cd"; // 평가종류코드
	public static final String EVAL_CD          = "eval_cd"; // 평가코드
	public static final String EVAL_NM          = "eval_nm"; // 평가명
	public static final String EVAL_YR          = "eval_yr"; // 평가명
	public static final String EXC_STS          = "exc_sts"; // 평가수행상태 (R301)
	public static final String EVAL_RES_ID      = "eval_res_id"; // 평가결과아이디
	public static final String ES_ID            = "es_id"; // 평가시트아이디
	public static final String ES_CD            = "es_cd"; // 평가시트코드
	public static final String EG_CD            = "eg_cd"; // 평가그룹코드
	public static final String SG_ID            = "sg_id"; // 소싱그룹코드
	public static final String SG_CD            = "sg_cd"; // 소싱그룹아이디
	public static final String VD_CD            = "vd_cd"; // 협력사코드
	public static final String TARG_TYP_VAL     = "targ_typ_val"; // 대상유형값
	public static final String TARG_TYP_REV     = "targ_typ_rev"; // 대상유형차수
	public static final String SIM_REV          = "sim_rev"; // 시뮬레이션 차수
	public static final String APPLY_SIM_REV    = "apply_sim_rev"; // 적용 시뮬레이션 차수
	public static final String QUANT_QUALI_CLS      = "quant_quali_cls"; // 정량/정성구분
	public static final String QUANT_QUALI_CLS_LIST = "quant_quali_cls_list"; // 정량/정성구분
	public static final String CHR_MGT_CLS      = "chr_mgt_cls"; // 평가자유형
	public static final String ADD_CRE_YN       = "add_cre_yn"; // 평가대상 추가 생성 여부
	public static final String DEL_TARG_YN      = "del_targ_yn"; // 평가대상 삭제 여부
	public static final String LINK_TYP         = "link_typ"; // 운영단위 연결 유형
	public static final String REG_ID           = "reg_id"; // REG ID
	public static final String S_USR_ID         = "s_usr_id"; // user ID
	public static final String EVAL_SUBJ_RES_UUID  = "eval_subj_res_uuid"; // 평가 대상 결과 UUID

	public static final String EVAL_SUBJ_EVALTR_RES_UUID = "eval_subj_evaltr_res_uuid"; // 평가 대상 평가자 결과 UUID

	public static final String RET_EVAL_CD      = "ret_eval_cd"; // Procedure return evaluation code
	public static final String EVAL_CALC_TYP    = "eval_calc_typ"; // 평가 집계 구분
	public static final String EF_KIND_CD       = "ef_kind_cd"; // 평가항목종류코드
	public static final String EF_KIND_CD_LIST  = "ef_kind_cd_list"; // 평가항목종류코드
	public static final String EVAL_CLS_TYP_CD  = "eval_cls_typ_cd"; // 평가(절대/상대)구분유형코드
	public static final String MAX_SCO          = "max_sco"; // 최대 점수
	public static final String MIN_SCO          = "min_sco"; // 최소 점수
	public static final String EF_CD            = "ef_cd"; // 평가항목코드
	public static final String EF_GRP_CD        = "ef_grp_cd"; // 평가항목군코드
	public static final String RE_YN            = "re_yn"; // 재집계 여부
	public static final String CLOSE_QUALI_YN     = "close_srv_yn"; // 정성 마감 여부
	public static final String CALC_REC_YN      = "calc_rec_yn"; // 실적항목 실측값 집계 여부
	public static final String SCHEDULE_DT      = "schedule_dt"; // 스케줄러 실행 시간
	public static final String EVAL_TARG_CLS    = "eval_targ_cls"; // 피평가대상
	public static final String EVAL_BAS_CD      = "eval_bas_cd"; // 평가시트적용단위

	public static final String RESULT_CODE  = "ret_cd"; // Procedure return code
	public static final String RESULT_MSG   = "ret_msg"; // Procedure return message

	// 기본값 정의
	public static final String Y_STR_VAL = "Y"; // Y
	public static final String N_STR_VAL = "N"; // N
	public static final String SRM_DEF_VAL = "-1"; // 평가 기본 값
	public static final String ERROR_CODE = "-1"; // Procedure return error code
	public static final String VALID_EVAL = "VALID"; // Procedure return error code
	public static final String DEF_ERR_MSG = "No Error Message."; // Procedure return error code
	public static final String SUCCESS_CODE = "0"; // Procedure return success code
	public static final String ROOT = "ROOT"; // ROOT

	// 운영단위 연결 유형 정의
	public static final String OPER_LINK_SOGO = "SOGO"; // SRM운영단위-소싱그룹운영단위 연결
	public static final String OPER_LINK_SOEO = "SOEO"; // SRM운영단위-협력사운영단위 연결
	public static final String OPER_LINK_SOPO = "SOPO"; // SRM운영단위-구매운영단위 연결
	public static final String OPER_LINK_SOIO = "SOIO"; // SRM운영단위-품목운영단위 연결
	public static final String OPER_LINK_EOSO = "EOSO"; // 협력사운영단위-SRM운영단위 연결

	// 평가업무구분 정의
	public static final String EV_TYPE_VE = "VE"; // 등록평가
	public static final String EV_TYPE_RE = "RE"; // 정기평가
	public static final String EV_TYPE_SE = "SE"; // 관계유형평가
	public static final String EV_TYPE_XE = "NPE"; // 비가격평가
	public static final String EV_TYPE_TE = "TE"; // TBE평가
	public static final String EV_TYPE_GE = "GE"; // GR(입고검수)
	public static final String EV_TYPE_PE = "PE"; // PO(준공,기성 평가)
	public static final String EV_TYPE_ME = "ME"; // 자재(위험도)평가
	public static final String EV_TYPE_CE = "CE"; // 계약평가
	public static final String EV_TYPE_ZE = "ZE"; // 기성평가
	public static final String[] EV_TYPES_EX = { "NPE", "GE", "PE", "ME", "CE", "ZE" }; // 기타 평가

	// 평가 상태 정의
	public static final String EV_STS_10 = "10"; // 작성중
	public static final String EV_STS_20 = "20"; // 평가요청(통보)
	public static final String EV_STS_25 = "25"; // 통보오류
	public static final String EV_STS_30 = "30"; // 정량평가
	public static final String EV_STS_35 = "35"; // 정량오류
	public static final String EV_STS_40 = "40"; // 정성평가
	public static final String EV_STS_50 = "50"; // 평가집계
	public static final String EV_STS_55 = "55"; // 집계오류
	public static final String EV_STS_60 = "60"; // 집계완료
	public static final String EV_STS_70 = "70"; // 결재요청
	public static final String EV_STS_75 = "75"; // 결재반려
	public static final String EV_STS_80 = "80"; // 승인완료

	// 정량/정성 정의
	public static final String QUANT = "QUANT"; // 정량
	public static final String QUALI = "QUALI"; // 정성
	public static final String QUANT_CLS = "QUANT"; // 정량 구분
	public static final String[] QUALI_CLS_LIST = { "QUALI", "QUALI_MULTPL_SEL", "QUALI_SC_INP" }; // 정성 구분(단일선택, 다중선택, 직접입력)

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

	ObdConst() {
		// 기본 생성자
	}
}
