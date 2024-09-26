package smartsuite.security.userdetails;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import smartsuite.security.account.service.AccountService;
import smartsuite.security.authentication.ProxyPasswordEncoder;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Transactional
public class DefaultUserDetailsService implements UserDetailsService {

	@Inject
	AccountService accountService;

	// 사용 여부
	String accountEnabledName = "use_yn";

	// 계정 잠금 여부
	String accountNonLockedName = "acct_lckd_yn";

	// 패스워드 변경 일시
	String accountCredentialsNonExpiredName = "pwd_chg_dttm";

	// 패스워드 변경 필요 여부
	String accountCredentialsNonInitializedName = "pwd_chg_reqd_yn";

	boolean withoutPassword = false;

	// 계정 상태 공통코드
	String accountStatusInfoCode = "acct_sts_ccd";
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		ServletRequestAttributes servletAttr= (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String strLocale =   servletAttr.getRequest().getParameter("locale");
		
        if("" != strLocale && null != strLocale) {
        	String[] splitedLocale = strLocale.split("_");
        	Locale usrLocale = new Locale(splitedLocale[0], splitedLocale[1]);
        	LocaleContextHolder.setLocale(usrLocale);
        }
		
        
		User user = accountService.findUserNameAndPassword(username);

		if (user == null) {
			throw new UsernameNotFoundException(username + " id does not exist.");
		}
		//사용자 정보 조회
		Map<String,Object> userInfo = accountService.findUserSessionInfo(username);

		user.setUserInfo(userInfo);
		//사용자 롤 조회
		Collection<GrantedAuthority> authorities = accountService.findListRoleCodeByLoginUserSession(userInfo);

		user.setAuthorities(authorities);
		
		//사용여부
		user.setEnabled("Y".equals(userInfo.get(accountEnabledName)));

		if(withoutPassword) {
			//비밀번호 없이 로그인 하는 경우 만료여부 체크 안함
			user.setCredentialsNonExpired(true);
		} else if(null == userInfo.get(accountCredentialsNonExpiredName)){
			//pw_mod_dt가 null 일 경우 처리 방안
			user.setCredentialsNonExpired(false);
		} else {
			//비밀번호 만료여부
			user.setCredentialsNonExpired(accountService.isCredentialsNonExpired((Date)userInfo.get(accountCredentialsNonExpiredName)));
		}

		//잠김여부
		user.setAccountNonLocked("N".equals(userInfo.get(accountNonLockedName)));
		// 혹여 잠금여부가 N이 더라도, 계정 상태(LCKD) 에 따라 잠금 처리
		if("LCKD".equals(userInfo.get(accountStatusInfoCode))){
			// 사용자 계정 상태 (잠금 시)
			/** 잠금 유형에 따라 Lock을 처리할지 않할지 처리도 가능 할 듯 ( UserDetails Override 처리 하지 않는 이상 별도의 다른 처리 불가)
			 *
			 *  TABLE = USR
			 *  COLUMN = LCKD_TYP_CCD (잠금 유형 공통코드)
			 * ---------------------------------------------------------------
			 * 관리자 잠금(SYSADM_LCKD) : 잠금 처리 시
			 * 장기 미사용 잠금(LONG-TERM_UNUD_LCKD) : 장기 미사용자 배치 실행 시
			 * 비밀번호 잠금(PW_LCKD) : 사용자 비번 O회 오류 시
			 * 사용자 탈퇴 잠금(USR_WDRL_LCKD) : 사용자 탈퇴 처리 시(S&C 검토 필요)
			 * 부서 전배 잠금(DEPT_TRANS_LCKD) : HR 인터페이스 시 고려 필요"
			 */
			user.setAccountNonLocked(false);
		}


		//비밀번호 초기화 여부
		user.setCredentialsNonInitialized("N".equals(userInfo.get(accountCredentialsNonInitializedName)));
		
		return new UserDetailsProxy(user);
	}
	
	public void setWithoutPassword(boolean value) {
		this.withoutPassword = value;
	}
	
}