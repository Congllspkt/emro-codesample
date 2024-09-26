package smartsuite.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import smartsuite.app.common.security.BidRSACipherUtil;
import smartsuite.app.common.security.FixedBidRSAKeyGenerator;
import smartsuite.config.module.utils.ModulePropertiesUtils;
import smartsuite.security.authentication.DefaultPasswordGenerator;
import smartsuite.security.core.authentication.encryption.PasswordEncryptor;
import smartsuite.security.core.authentication.encryption.ShaPasswordEncryptor;
import smartsuite.security.core.authentication.encryption.salt.SaltSource;
import smartsuite.security.core.authentication.encryption.salt.SimpleSaltGenerator;
import smartsuite.security.core.crypto.AESCipherUtil;
import smartsuite.security.core.crypto.AESIvParameterGenerator;
import smartsuite.security.core.crypto.AESSecretKeyGenerator;
import smartsuite.security.core.crypto.SecureRandomHexaDecimalGenerator;
import smartsuite.security.spring.core.authentication.encoding.ProxyPasswordEncoder;
import smartsuite.security.spring.web.authentication.dao.HttpSessionSaltSource;

import java.util.Properties;

@Configuration
public class CryptoConfiguration {

	/* was 이중화 환경 세션 클러스터링 설정 시 사용 start  */
	@Value("#{smartsuiteProperties['smartsuite.session.isClustered'] ?: false}")
    private boolean isClustered;

    private final String passPhrase;
    private final String key;
    private final int iterationCount;
    private final String iv;

    public CryptoConfiguration() {
		Properties sessionProperties = ModulePropertiesUtils.loadEncryptedProperties("smartsuite/properties/encrypt/session.properties");
		// Adding null check for sessionProperties
        if (sessionProperties == null) {
            // If sessionProperties is null, use default values
            this.passPhrase = "defaultPassPhrase";
            this.key = "defaultKey";
            this.iterationCount = 1000; // default iterationCount
            this.iv = "defaultIv";
        } else {
            // Providing default values in case specific properties are missing
            this.passPhrase = sessionProperties.getProperty("smartsuite.session.passPhrase", "defaultPassPhrase");
            this.key = sessionProperties.getProperty("smartsuite.session.key", "defaultKey");
            this.iterationCount = safeParseInt(sessionProperties.getProperty("smartsuite.session.iterationCount"), 1000); // default iterationCount
            //AESIvParameterGenerator 클래스의 iv 값은 아래와 같이 생성합니다.
		this.iv =  sessionProperties.getProperty("smartsuite.session.iv","defaultIv");
        }
    }

	private int safeParseInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

	/* was 이중화 환경 세션 클러스터링 설정 시 사용 end */

	// ******************************* //
	// 패스워드 암호화
	// ******************************* //	 
	// SHA-512 해시 함수를 이용한  암호화 클래스
	@Bean(name = "shaPasswordEncryptor")
	public PasswordEncryptor shaPasswordEncryptor() {
		return new ShaPasswordEncryptor();
	}
	
	// 세션단위로 관리하는 솔트제공자
	@Bean(name = "httpSessionSaltSource")
	public SaltSource httpSessionSaltSource() {
		return new HttpSessionSaltSource(new SimpleSaltGenerator()); // sha 해시함수 솔트 생성자
	}
	
	// PasswordEncoder 인터페이스를 구현하여 패스워드 인증처리 구현
	@Bean(name = "passwordEncoder")
	public PasswordEncoder passwordEncoder(PasswordEncryptor shaPasswordEncryptor, SaltSource httpSessionSaltSource) {
		ProxyPasswordEncoder passwordEncoder = new ProxyPasswordEncoder(shaPasswordEncryptor);
		passwordEncoder.setSaltSource(httpSessionSaltSource);
		return passwordEncoder;
	}

	// ******************************* //
	// 데이터 암,복호화
	// ******************************* //
	// AES 암호화 키 생성 클래스
	@Bean(name = "aesKeyGenerator")
	public AESSecretKeyGenerator aesKeyGenerator() {
		if(isClustered){
			return new AESSecretKeyGenerator(passPhrase,key,iterationCount);
		}else{
			return new AESSecretKeyGenerator();
		}
	}

	// AES 암호화 iv 생성 클래스
	@Bean(name = "ivParameterGenerator")
	public AESIvParameterGenerator ivParameterGenerator() {
		if(isClustered){
			return new AESIvParameterGenerator(iv);
		}else{
			return new AESIvParameterGenerator();
		}
	}

	// AES 암호화 알고리즘 유틸 클래스
	@Bean(name = "cipherUtil")
	public AESCipherUtil cipherUtil(AESSecretKeyGenerator aesKeyGenerator, AESIvParameterGenerator ivParameterGenerator) {
		return new AESCipherUtil(aesKeyGenerator, ivParameterGenerator);
	}
	
	// 견적 입찰 용 RSA 암호화 키 생성 클래스로 키 사이즈는 1024를 기본값으로 사용하며 생성자를 통해 고정키로 변경할 수 있습니다.
	@Bean(name = "bidRsaKeyGenerator")
	public FixedBidRSAKeyGenerator bidRsaKeyGenerator() {
		return new FixedBidRSAKeyGenerator();
	}
	
	// 견적 입찰 용 RSA 암호화 알고리즘 유틸 클래스
	@Bean(name = "bidRSACipherUtil")
	public BidRSACipherUtil bidRSACipherUtil(FixedBidRSAKeyGenerator bidRsaKeyGenerator) {
		return new BidRSACipherUtil(bidRsaKeyGenerator);
	}

	@Bean(name = "defaultPasswordGenerator")
	public DefaultPasswordGenerator defaultPasswordGenerator() {
		return new DefaultPasswordGenerator();
	}
}
