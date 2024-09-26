package smartsuite.app.bp.contract.data;

public class ContractReqBuilder {

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
	
	
	public ContractReqBuilder preReqTypCcd(String preReqTypCcd) {
		this.preReqTypCcd = preReqTypCcd;
		return this;
	}
	
	public ContractReqBuilder cntrReqTypCcd(String cntrReqTypCcd) {
		this.cntrReqTypCcd = cntrReqTypCcd;
		return this;
	}
	
	public ContractReqBuilder cntrdocTypCcd(String cntrdocTypCcd) {
		this.cntrdocTypCcd = cntrdocTypCcd;
		return this;
	}
	
	public ContractReqBuilder reqId(String reqId) {
		this.reqId = reqId;
		return this;
	}
	
	public ContractReqBuilder cndId(String cndId) {
		this.cndId = cndId;
		return this;
	}
	
	public ContractReqBuilder reqNm(String reqNm) {
		this.reqNm = reqNm;
		return this;
	}
	
	public ContractReqBuilder reqrId(String reqrId) {
		this.reqrId = reqrId;
		return this;
	}
	
	public ContractReqBuilder vdCd(String vdCd) {
		this.vdCd = vdCd;
		return this;
	}
	
	public ContractReqBuilder oorgCd(String oorgCd) {
		this.oorgCd = oorgCd;
		return this;
	}
	
	public ContractReqBuilder purcGrpCd(String purcGrpCd) {
		this.purcGrpCd = purcGrpCd;
		return this;
	}
	
	public ContractReqBuilder cntrId(String cntrId) {
		this.cntrId = cntrId;
		return this;
	}

	public ContractReqBuilder reqRsn(String reqRsn) {
		this.reqRsn = reqRsn;
		return this;
	}
	
	public ContractReqBuilder() {
		
	}
	
	public ContractReq build() {
		return new ContractReq(preReqTypCcd, cntrReqTypCcd, cntrdocTypCcd, reqId, cndId, reqNm, reqrId, vdCd, oorgCd, purcGrpCd, cntrId, reqRsn);
	}
	
}
