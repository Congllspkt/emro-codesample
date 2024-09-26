package smartsuite.app.cxfapi.po.bean;

import java.util.ArrayList;
import java.util.List;

public class SendPoParam {
	
	List<Header> header = new ArrayList<Header>();
	
	List<Item> item = new ArrayList<Item>();
	
	public List<Header> getHeader() {
		return header;
	}
	
	public void setHeader(List<Header> header) {
		this.header = header;
	}
	
	public List<Item> getItem() {
		return item;
	}
	
	public void setItem(List<Item> item) {
		this.item = item;
	}
	
	public static class Header {
		
		String oorgCd;
		
		String poNo;
		
		String poRevno;
		
		String vdCd;
		
		String poTit;
		
		String purcTypCcd;
		
		String poReqTypCcd;
		
		String poCrnTypCcd;
		
		String domovrsDivCcd;
		
		String poCrnDt;
		
		String curCcd;
		
		String poAmt;
		
		String pymtmethCcd;
		
		String pymtmethExpln;
		
		String pymtmethUseYn;
		
		String dlvymethCcd;
		
		String dlvymethExpln;
		
		String dlvymethUseYn;
		
		String taxTypCcd;
		
		String otrexp;
		
		String otrexpExpln;
		
		String apymtRo;
		
		String apymtAmt;
		
		String ipymtRo;
		
		String ipymtAmt;
		
		String balRo;
		
		String balAmt;
		
		String pymtmethCnd;
		
		String uprccntrNo;
		
		String cntrAmt;
		
		String supAmt;
		
		String vatAmt;
		
		String cntrStDt;
		
		String cntrExpDt;
		
		String dfrmRo;
		
		public String getOorgCd() {
			return oorgCd;
		}
		
		public void setOorgCd(String oorgCd) {
			this.oorgCd = oorgCd;
		}
		
		public String getPoNo() {
			return poNo;
		}
		
		public void setPoNo(String poNo) {
			this.poNo = poNo;
		}
		
		public String getPoRevno() {
			return poRevno;
		}
		
		public void setPoRevno(String poRevno) {
			this.poRevno = poRevno;
		}
		
		public String getVdCd() {
			return vdCd;
		}
		
		public void setVdCd(String vdCd) {
			this.vdCd = vdCd;
		}
		
		public String getPoTit() {
			return poTit;
		}
		
		public void setPoTit(String poTit) {
			this.poTit = poTit;
		}
		
		public String getPurcTypCcd() {
			return purcTypCcd;
		}
		
		public void setPurcTypCcd(String purcTypCcd) {
			this.purcTypCcd = purcTypCcd;
		}
		
		public String getPoReqTypCcd() {
			return poReqTypCcd;
		}
		
		public void setPoReqTypCcd(String poReqTypCcd) {
			this.poReqTypCcd = poReqTypCcd;
		}
		
		public String getPoCrnTypCcd() {
			return poCrnTypCcd;
		}
		
		public void setPoCrnTypCcd(String poCrnTypCcd) {
			this.poCrnTypCcd = poCrnTypCcd;
		}
		
		public String getDomovrsDivCcd() {
			return domovrsDivCcd;
		}
		
		public void setDomovrsDivCcd(String domovrsDivCcd) {
			this.domovrsDivCcd = domovrsDivCcd;
		}
		
		public String getPoCrnDt() {
			return poCrnDt;
		}
		
		public void setPoCrnDt(String poCrnDt) {
			this.poCrnDt = poCrnDt;
		}
		
		public String getCurCcd() {
			return curCcd;
		}
		
		public void setCurCcd(String curCcd) {
			this.curCcd = curCcd;
		}
		
		public String getPoAmt() {
			return poAmt;
		}
		
		public void setPoAmt(String poAmt) {
			this.poAmt = poAmt;
		}
		
		public String getPymtmethCcd() {
			return pymtmethCcd;
		}
		
		public void setPymtmethCcd(String pymtmethCcd) {
			this.pymtmethCcd = pymtmethCcd;
		}
		
		public String getPymtmethExpln() {
			return pymtmethExpln;
		}
		
		public void setPymtmethExpln(String pymtmethExpln) {
			this.pymtmethExpln = pymtmethExpln;
		}
		
		public String getPymtmethUseYn() {
			return pymtmethUseYn;
		}
		
		public void setPymtmethUseYn(String pymtmethUseYn) {
			this.pymtmethUseYn = pymtmethUseYn;
		}
		
		public String getDlvymethCcd() {
			return dlvymethCcd;
		}
		
		public void setDlvymethCcd(String dlvymethCcd) {
			this.dlvymethCcd = dlvymethCcd;
		}
		
		public String getDlvymethExpln() {
			return dlvymethExpln;
		}
		
		public void setDlvymethExpln(String dlvymethExpln) {
			this.dlvymethExpln = dlvymethExpln;
		}
		
		public String getDlvymethUseYn() {
			return dlvymethUseYn;
		}
		
		public void setDlvymethUseYn(String dlvymethUseYn) {
			this.dlvymethUseYn = dlvymethUseYn;
		}
		
		public String getTaxTypCcd() {
			return taxTypCcd;
		}
		
		public void setTaxTypCcd(String taxTypCcd) {
			this.taxTypCcd = taxTypCcd;
		}
		
		public String getOtrexp() {
			return otrexp;
		}
		
		public void setOtrexp(String otrexp) {
			this.otrexp = otrexp;
		}
		
		public String getOtrexpExpln() {
			return otrexpExpln;
		}
		
		public void setOtrexpExpln(String otrexpExpln) {
			this.otrexpExpln = otrexpExpln;
		}
		
		public String getApymtRo() {
			return apymtRo;
		}
		
		public void setApymtRo(String apymtRo) {
			this.apymtRo = apymtRo;
		}
		
		public String getApymtAmt() {
			return apymtAmt;
		}
		
		public void setApymtAmt(String apymtAmt) {
			this.apymtAmt = apymtAmt;
		}
		
		public String getIpymtRo() {
			return ipymtRo;
		}
		
		public void setIpymtRo(String ipymtRo) {
			this.ipymtRo = ipymtRo;
		}
		
		public String getIpymtAmt() {
			return ipymtAmt;
		}
		
		public void setIpymtAmt(String ipymtAmt) {
			this.ipymtAmt = ipymtAmt;
		}
		
		public String getBalRo() {
			return balRo;
		}
		
		public void setBalRo(String balRo) {
			this.balRo = balRo;
		}
		
		public String getBalAmt() {
			return balAmt;
		}
		
		public void setBalAmt(String balAmt) {
			this.balAmt = balAmt;
		}
		
		public String getPymtmethCnd() {
			return pymtmethCnd;
		}
		
		public void setPymtmethCnd(String pymtmethCnd) {
			this.pymtmethCnd = pymtmethCnd;
		}
		
		public String getUprccntrNo() {
			return uprccntrNo;
		}
		
		public void setUprccntrNo(String uprccntrNo) {
			this.uprccntrNo = uprccntrNo;
		}
		
		public String getCntrAmt() {
			return cntrAmt;
		}
		
		public void setCntrAmt(String cntrAmt) {
			this.cntrAmt = cntrAmt;
		}
		
		public String getSupAmt() {
			return supAmt;
		}
		
		public void setSupAmt(String supAmt) {
			this.supAmt = supAmt;
		}
		
		public String getVatAmt() {
			return vatAmt;
		}
		
		public void setVatAmt(String vatAmt) {
			this.vatAmt = vatAmt;
		}
		
		public String getCntrStDt() {
			return cntrStDt;
		}
		
		public void setCntrStDt(String cntrStDt) {
			this.cntrStDt = cntrStDt;
		}
		
		public String getCntrExpDt() {
			return cntrExpDt;
		}
		
		public void setCntrExpDt(String cntrExpDt) {
			this.cntrExpDt = cntrExpDt;
		}
		
		public String getDfrmRo() {
			return dfrmRo;
		}
		
		public void setDfrmRo(String dfrmRo) {
			this.dfrmRo = dfrmRo;
		}
	}
	
	public static class Item {
		
		String oorgCd;
		
		String poNo;
		
		String poRevno;
		
		String vdCd;
		
		String poLno;
		
		String itemCd;
		
		String poQty;
		
		String curCcd;
		
		String poUprc;
		
		String poAmt;
		
		String custRo;
		
		String custAmt;
		
		String whCcd;
		
		String dlvyPlc;
		
		String purcGrpCd;
		
		String grPicId;
		
		String uprccntrNo;
		
		String uprccntrRevno;
		
		String uprccntrLno;
		
		String constStDt;
		
		String constExpDt;
		
		String reqDlvyDt;
		
		public String getOorgCd() {
			return oorgCd;
		}
		
		public void setOorgCd(String oorgCd) {
			this.oorgCd = oorgCd;
		}
		
		public String getPoNo() {
			return poNo;
		}
		
		public void setPoNo(String poNo) {
			this.poNo = poNo;
		}
		
		public String getPoRevno() {
			return poRevno;
		}
		
		public void setPoRevno(String poRevno) {
			this.poRevno = poRevno;
		}
		
		public String getVdCd() {
			return vdCd;
		}
		
		public void setVdCd(String vdCd) {
			this.vdCd = vdCd;
		}
		
		public String getPoLno() {
			return poLno;
		}
		
		public void setPoLno(String poLno) {
			this.poLno = poLno;
		}
		
		public String getItemCd() {
			return itemCd;
		}
		
		public void setItemCd(String itemCd) {
			this.itemCd = itemCd;
		}
		
		public String getPoQty() {
			return poQty;
		}
		
		public void setPoQty(String poQty) {
			this.poQty = poQty;
		}
		
		public String getCurCcd() {
			return curCcd;
		}
		
		public void setCurCcd(String curCcd) {
			this.curCcd = curCcd;
		}
		
		public String getPoUprc() {
			return poUprc;
		}
		
		public void setPoUprc(String poUprc) {
			this.poUprc = poUprc;
		}
		
		public String getPoAmt() {
			return poAmt;
		}
		
		public void setPoAmt(String poAmt) {
			this.poAmt = poAmt;
		}
		
		public String getCustRo() {
			return custRo;
		}
		
		public void setCustRo(String custRo) {
			this.custRo = custRo;
		}
		
		public String getCustAmt() {
			return custAmt;
		}
		
		public void setCustAmt(String custAmt) {
			this.custAmt = custAmt;
		}
		
		public String getWhCcd() {
			return whCcd;
		}
		
		public void setWhCcd(String whCcd) {
			this.whCcd = whCcd;
		}
		
		public String getDlvyPlc() {
			return dlvyPlc;
		}
		
		public void setDlvyPlc(String dlvyPlc) {
			this.dlvyPlc = dlvyPlc;
		}
		
		public String getPurcGrpCd() {
			return purcGrpCd;
		}
		
		public void setPurcGrpCd(String purcGrpCd) {
			this.purcGrpCd = purcGrpCd;
		}
		
		public String getGrPicId() {
			return grPicId;
		}
		
		public void setGrPicId(String grPicId) {
			this.grPicId = grPicId;
		}
		
		public String getUprccntrNo() {
			return uprccntrNo;
		}
		
		public void setUprccntrNo(String uprccntrNo) {
			this.uprccntrNo = uprccntrNo;
		}
		
		public String getUprccntrRevno() {
			return uprccntrRevno;
		}
		
		public void setUprccntrRevno(String uprccntrRevno) {
			this.uprccntrRevno = uprccntrRevno;
		}
		
		public String getUprccntrLno() {
			return uprccntrLno;
		}
		
		public void setUprccntrLno(String uprccntrLno) {
			this.uprccntrLno = uprccntrLno;
		}
		
		public String getConstStDt() {
			return constStDt;
		}
		
		public void setConstStDt(String constStDt) {
			this.constStDt = constStDt;
		}
		
		public String getConstExpDt() {
			return constExpDt;
		}
		
		public void setConstExpDt(String constExpDt) {
			this.constExpDt = constExpDt;
		}
		
		public String getReqDlvyDt() {
			return reqDlvyDt;
		}
		
		public void setReqDlvyDt(String reqDlvyDt) {
			this.reqDlvyDt = reqDlvyDt;
		}
	}
}
