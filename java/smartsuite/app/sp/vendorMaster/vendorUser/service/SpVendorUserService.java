package smartsuite.app.sp.vendorMaster.vendorUser.service;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.app.bp.admin.auth.service.UserService;
import smartsuite.app.common.mail.MailService;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.sp.vendorMaster.vendorUser.repository.SpVendorUserRepository;
import smartsuite.security.account.service.AccountService;
import smartsuite.security.authentication.PasswordGenerator;
import smartsuite.security.core.authentication.encryption.PasswordEncryptor;
import smartsuite.security.core.crypto.AESIvParameterGenerator;
import smartsuite.security.core.crypto.AESSecretKeyGenerator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VendorMgt 담당자 관련 처리하는 서비스 Class입니다.
 *
 * @see 
 * @FileName VendorUserService.java
 * @package smartsuite.app.bp.esourcing.vendorInfoMgt
 * @Since 2019. 11. 27
 */
@Service
@Transactional
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SpVendorUserService {
	@Inject
	private SpVendorUserRepository spVendorUserRepository;

	@Inject
	AccountService accountService;

	@Inject
	UserService userService;

	@Inject
	MailService mailService;
	
	/** global.properties. */
	@Value ("#{globalProperties['sp.main.url']}")
	private String spUrl; 

	/**  SecretKey Generator PW 암복호화시 사용. */
	@Inject
	AESSecretKeyGenerator keyGenerator;
	
	/**  Parameter Generator PW 암복호화시 사용. */
	@Inject
	AESIvParameterGenerator parameterGenerator;
	
	/** The password generator. */
	@Inject
	PasswordGenerator passwordGenerator;
	
	/** The password encryptor. */
	@Inject
	PasswordEncryptor passwordEncryptor;
	
	/** The Constant NAMESPACE. */
	private static final String NAMESPACE = "sp-vendorInfoMgt-user.";
	
	/**
	 * 협력사 담당자를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 30
	 * @author cyhwang
	 */
	public ResultMap savePicInfo(Map<String, Object> param) {
		Map<String, Object> resultInfo = Maps.newHashMap();
		Map<String, Object> saveParam = (Map<String, Object>)param.get("saveParam");
		Boolean isNew = false;
		if(saveParam.get("isNew") != null){
			isNew = (Boolean)saveParam.get("isNew");
		}
		Boolean isExistUser = userService.existUser(saveParam);

		// 신규 등록인데 이미 사용자 아이디가 있는 경우
		if(isNew && isExistUser) {
			// 중복 오류
			return ResultMap.DUPLICATED();
		}

		if(isExistUser) { // 수정 (update)
			spVendorUserRepository.updatePicUserInfo(saveParam);
		}else{ // 신규 (insert)

			saveParam.put("usr_typ_ccd", "VD");
			// 신규생성 시 비밀번호 발급
			// global.properties : use-default-password 가 true 일경우 default-password에 설정된 비밀번호로 발급
			String tempPassword = passwordGenerator.generate();
			String encryptedPassword = passwordEncryptor.encryptPw(tempPassword);

			resultInfo.put("tempPw", tempPassword);
			saveParam.put("tempPw", tempPassword);
			saveParam.put("pwd", encryptedPassword);
			saveParam.put("role_cd", "V100");
			spVendorUserRepository.insertPicUserInfo(saveParam);
			spVendorUserRepository.insertPicRole(saveParam);

			// 메일 발송
			addUserMailSend(saveParam);
		}
		Map<String, Object> userInfo = this.findPicInfo(saveParam);
		resultInfo.put("picInfo", userInfo);
		return ResultMap.SUCCESS(resultInfo);
	}

	/**
	 * 협력사 담당자를 삭제한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @Method Name : deleteListPic
	 */
	public  ResultMap deleteListPic(Map<String,Object> param) {
		List<Map<String, Object>> deletedPics = (List<Map<String, Object>>) param.get("deletedPics");

		for(Map<String, Object> deletedPic: deletedPics){
			// 역할 삭제
			spVendorUserRepository.deletePicRole(deletedPic);
			// 사용자 삭제
			spVendorUserRepository.deletePicUser(deletedPic);
		}

		return ResultMap.SUCCESS();
	}

	/**
	 * 담당자 목록을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 21
	 * @author cyhwang
	 */
	public List findListPic(Map<String, Object> param) {
		return spVendorUserRepository.findListPic(param);
	}

    
	/**
	 * 담당자 상세 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 21
	 * @author cyhwang
	 */
	public Map findPicInfo(Map<String ,Object> param){
		Map vendorUserInfo = spVendorUserRepository.findPicInfo(param);
		vendorUserInfo.put("limitLoginInvalidPw",accountService.getAccountSettings().getLimitLoginInvalidPasswordCount());

		return vendorUserInfo;
	}


    /**
	 * 사용자 신규 추가 시 메일 발송.
	 *
	 * @param user the user
	 */
	private void addUserMailSend(Map user) {
		Map<String, Object> mailParam = new HashMap<String, Object>();

		mailParam.put("eml"	  , user.get("eml"));
		mailParam.put("usr_id", user.get("usr_id"));
		mailParam.put("tempPw", user.get("tempPw"));
		mailParam.put("usr_nm", user.get("usr_nm"));

		mailService.sendAsync("NEW_USER", null, mailParam);
	}

	public ResultMap initPassword(Map param) {
		Map<String, Object> user = spVendorUserRepository.findPicInfo(param);
		if(user == null) {
			return ResultMap.NOT_EXISTS();
		}

		String tempPassword = passwordGenerator.generate();
		String encryptedPassword = passwordEncryptor.encryptPw(tempPassword);
		user.put("pwd", encryptedPassword);
		user.put("tempPw", tempPassword);

		// 비밀번호 초기화 메일 전송
		mailSend(user);

		spVendorUserRepository.initPw(user);
		return ResultMap.SUCCESS();
	}
	
	/**
	 * 메일을 전송한다. 
	 *
	 * @author : JongHyeok Choi
	 * @param param the param
	 * @Date : 2016. 8. 16
	 * @Method Name : mailSend
	 */
	private void mailSend(Map user) {
		Map<String, Object> mailParam = new HashMap<String, Object>();

		mailParam.put("eml"   , user.get("eml"));
		mailParam.put("usr_id", user.get("usr_id"));
		mailParam.put("tempPw", user.get("tempPw"));
		mailParam.put("usr_nm", user.get("usr_nm"));
		mailParam.put("url"   , spUrl);

		mailService.sendAsync("INIT_PW_ML", null, mailParam);
	}

	/**
	 * 계정 잠김해제를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	public ResultMap saveInfoAcctLockYn(Map param) {
		spVendorUserRepository.saveInfoAcctLockYn(param);
		return ResultMap.SUCCESS();
	}
	/**
	 * 협력사 담당자별 역할을 조회한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	public List findListRoleByPicUser(Map param){
		return spVendorUserRepository.findListRoleByPicUser(param);
	}

	/**
	 * 협력사 담당자별 역할을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 3
	 * @author cyhwang
	 */
	public void saveRoleByPicUser(Map<String, Object> param) {
		// 기존에 granted 가 Y 인 건은 삭제 처리, N 인 건은 신규 추가
		String granted = param.getOrDefault("granted","")  == null?  "" : (String) param.getOrDefault("granted","");

		if("Y".equals(granted)) {
			// 사용자 별 역할 삭제 ( roleCode & UserId )
			spVendorUserRepository.deletePicRoleByRole(param);
		} else {
			// 사용자 별 역할 추가
			spVendorUserRepository.insertPicRole(param);
		}
	}
}
