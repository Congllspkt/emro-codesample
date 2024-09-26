package smartsuite.app.bp.contract.data;

import smartsuite.app.shared.CntrConst;

public class ContractReq {

	private String preReqTypCcd;
	private String cntrReqTypCcd;
	private String cntrdocTypCcd;
	private String reqId;
	private String cndId;
	private String reqNm;
	private String reqrId;
	private String vdCd;
	private String oorgCd;
	private String purcGrpCd;
	private String cntrId;
	private String reqRsn;
	
	
	public String getPreReqTypCcd() {
		return preReqTypCcd;
	}

	public void setPreReqTypCcd(String preReqTypCcd) {
		this.preReqTypCcd = preReqTypCcd;
	}

	public String getCntrReqTypCcd() {
		return cntrReqTypCcd;
	}

	public void setCntrReqTypCcd(String cntrReqTypCcd) {
		this.cntrReqTypCcd = cntrReqTypCcd;
	}

	public String getCntrdocTypCcd() {
		return cntrdocTypCcd;
	}

	public void setCntrdocTypCcd(String cntrdocTypCcd) {
		this.cntrdocTypCcd = cntrdocTypCcd;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getCndId() {
		return cndId;
	}

	public void setCndId(String cndId) {
		this.cndId = cndId;
	}

	public String getReqNm() {
		return reqNm;
	}

	public void setReqNm(String reqNm) {
		this.reqNm = reqNm;
	}

	public String getReqrId() {
		return reqrId;
	}

	public void setReqrId(String reqrId) {
		this.reqrId = reqrId;
	}
	
	public String getVdCd() {
		return vdCd;
	}

	public void setVdCd(String vdCd) {
		this.vdCd = vdCd;
	}

	public String getOorgCd() {
		return oorgCd;
	}

	public void setOorgCd(String oorgCd) {
		this.oorgCd = oorgCd;
	}

	public String getPurcGrpCd() {
		return purcGrpCd;
	}

	public void setPurcGrpCd(String purcGrpCd) {
		this.purcGrpCd = purcGrpCd;
	}

	public String getCntrId() {
		return cntrId;
	}

	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}

	public String getReqRsn() {
		return reqRsn;
	}

	public void setReqRsn(String reqRsn) {
		this.reqRsn = reqRsn;
	}

	public ContractReq(String preReqTypCcd, String cntrReqTypCcd, String cntrdocTypCcd, String reqId, String cndId, String reqNm, 
			String reqrId, String vdCd, String oorgCd, String purcGrpCd, String cntrId, String reqRsn) {
		this.preReqTypCcd = preReqTypCcd;
		this.cntrReqTypCcd = cntrReqTypCcd;
		this.cntrdocTypCcd = cntrdocTypCcd;
		this.reqId = reqId;
		this.cndId = cndId;
		this.reqNm = reqNm;
		this.reqrId = reqrId;
		this.vdCd = vdCd;
		this.oorgCd = oorgCd;
		this.purcGrpCd = purcGrpCd;
		this.cntrId = cntrId;
		this.reqRsn = reqRsn;
	}
	
	public static ContractReq RFX(String cntrdocTypCcd, String rfxId, String cndId,
			String reqNm, String reqrId, String vdCd, String oorgCd, String purcGrpCd) {
		return ContractReq.builder()
				.preReqTypCcd(CntrConst.PRE_REQ_TYPE.RFX)
				.cntrReqTypCcd(CntrConst.CNTR_TYPE.NEW)
				.cntrdocTypCcd(cntrdocTypCcd)
				.reqId(rfxId)
				.cndId(cndId)
				.reqNm(reqNm)
				.reqrId(reqrId)
				.vdCd(vdCd)
				.oorgCd(oorgCd)
				.purcGrpCd(purcGrpCd)
				.build();
	}
	
	public static ContractReq ONBOARDING(String reqId, String reqNm, String reqrId, String vdCd, String oorgCd) {
		return ContractReq.builder()
				.preReqTypCcd(CntrConst.PRE_REQ_TYPE.ONBOARDING)
				.cntrReqTypCcd(CntrConst.CNTR_TYPE.NEW)
				.cntrdocTypCcd(CntrConst.CNTRDOC_TYPE.ELEMENTARY)
				.reqId(reqId)
				.reqNm(reqNm)
				.reqrId(reqrId)
				.vdCd(vdCd)
				.oorgCd(oorgCd)
				.build();
	}
	
	public static ContractReq PO_CHANGE(String poId, String cndId,
			String reqNm, String reqrId, String vdCd, String oorgCd, String purcGrpCd,
			String cntrId) {
		return ContractReq.builder()
				.preReqTypCcd(CntrConst.PRE_REQ_TYPE.PO)
				.cntrReqTypCcd(CntrConst.CNTR_TYPE.CHANGE)
				.cntrdocTypCcd(CntrConst.CNTRDOC_TYPE.PO)
				.reqId(poId)
				.cndId(cndId)
				.reqNm(reqNm)
				.reqrId(reqrId)
				.vdCd(vdCd)
				.oorgCd(oorgCd)
				.purcGrpCd(purcGrpCd)
				.cntrId(cntrId)
				.build();
	}
	
	public static ContractReq PO_TERMINATION(String rfxId, String cndId,
			String reqNm, String reqrId, String vdCd, String oorgCd, String purcGrpCd,
			String cntrId) {
		return ContractReq.builder()
				.preReqTypCcd(CntrConst.PRE_REQ_TYPE.PO)
				.cntrReqTypCcd(CntrConst.CNTR_TYPE.TERMINATION)
				.cntrdocTypCcd(CntrConst.CNTRDOC_TYPE.PO)
				.reqId(rfxId)
				.cndId(cndId)
				.reqNm(reqNm)
				.reqrId(reqrId)
				.vdCd(vdCd)
				.oorgCd(oorgCd)
				.purcGrpCd(purcGrpCd)
				.cntrId(cntrId)
				.build();
	}
	
	public static ContractReqBuilder builder() {
		return new ContractReqBuilder();
	}
	
}
