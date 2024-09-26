package smartsuite.app.common.cert.pki;

import java.util.Arrays;

/**
 * CertInfo
 *
 * @author daesung lee
 * @see 
 * @FileName CertService.java
 * @package smartsuite.app.bp.edoc.contract
 * @since 2019.02.27
 * @변경이력 : [2019.02.27] daesung lee 최초작성
 */

public class CertInfo {
	
	private final byte [] signCert;
	private final byte [] signPri;
	private final byte [] kmCert;
	private final byte [] kmPri;	
	private final byte [] pfx;

	private final String certPw;
	
	private String signCertFilePath;
	private String signPriFilePath;
	private String kmCertFilePath;
	private String kmPriFilePath;
	private String pfxFilePath;
	
	
	/**L
	 * @param signCert.der 파일 바이트
	 * @param signPri.key 파일 바이트
	 * @param certPw 인증서 비밀번호
	 * 
	 * */
	public CertInfo(byte[] signCert, byte[] signPri, byte[] kmCert, byte[] kmPri, byte[] pfx, String certPw
			,String signCertFilePath
			,String signPriFilePath
			,String kmCertFilePath
			,String kmPriFilePath
			,String pfxFilePath) {
		
		this.signCert = signCert;
		this.signPri = signPri;
		this.kmCert = kmCert;
		this.kmPri = kmPri;
		this.pfx = pfx;
		this.certPw = certPw;
		
		this.signCertFilePath = signCertFilePath;
		this.signPriFilePath = signPriFilePath;
		this.kmCertFilePath = kmCertFilePath;
		this.kmPriFilePath = kmPriFilePath;
		this.pfxFilePath = pfxFilePath;
	}
	
	public CertInfo(byte[] signCert, byte[] signPri, byte[] kmCert, byte[] kmPri, byte[] pfx, String certPw) {
		
		this.signCert = signCert;
		this.signPri = signPri;
		this.kmCert = kmCert;
		this.kmPri = kmPri;
		this.pfx = pfx;
		this.certPw = certPw;
		
	}
	
	public byte[] getSignCert() {
		return Arrays.copyOf(signCert, signCert.length);
	}

	public byte[] getSignPri() {
		return Arrays.copyOf(signPri, signPri.length);
	}

	public byte[] getPfx() {
		return Arrays.copyOf(pfx, pfx.length);
	}

	public String getCertPw() {
		return certPw;
	}
	public byte[] getKmCert() {
		return kmCert;
	}

	public byte[] getKmPri() {
		return kmPri;
	}

	public String getSignCertFilePath() {
		return signCertFilePath;
	}

	public String getSignPriFilePath() {
		return signPriFilePath;
	}

	public String getKmCertFilePath() {
		return kmCertFilePath;
	}

	public String getKmPriFilePath() {
		return kmPriFilePath;
	}

	public String getPfxFilePath() {
		return pfxFilePath;
	}
	
}