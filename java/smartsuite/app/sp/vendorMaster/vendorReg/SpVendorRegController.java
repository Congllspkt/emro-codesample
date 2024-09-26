package smartsuite.app.sp.vendorMaster.vendorReg;

import java.util.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import smartsuite.app.common.shared.Const;
import smartsuite.app.common.shared.ResultMap;
import smartsuite.app.common.shared.service.SharedService;
import smartsuite.app.sp.vendorMaster.vendorInfo.service.SpVendorMasterService;
import smartsuite.app.sp.vendorMaster.vendorReg.service.SpVendorRegService;
import smartsuite.data.FloaterStream;
import smartsuite.security.account.service.AccountService;
import smartsuite.security.annotation.AuthCheck;
import smartsuite.security.userdetails.User;
import smartsuite.security.userdetails.UserDetailsProxy;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "**/sp/vendorMaster/vendorReg/**/")
public class SpVendorRegController {
	
    @Inject
    SpVendorRegService spVendorRegService;

	@Inject
	AccountService accountService;

	@Inject
	SpVendorMasterService spVendorMasterService;

	@Inject
	private SharedService sharedService;

	/**
     * 운영단위별 운영조직 조회.
	 * @param param
	 * @Date : 2023. 07. 17
	 * @author yjPark
     */
    @AuthCheck(authCode = Const.READ)
    @RequestMapping (value = "findListOorgCdAll.do")
    public @ResponseBody List findListOorgCdAll(@RequestBody String param) {
        return spVendorMasterService.findListOorgCdAll(param);
    }

    /**
     * Sets security context of Guest
     */
	public void setSecurityContextOfGuest() {
		User user = new User();
		Map<String,Object> userinfo = new HashMap<String, Object>();
		userinfo.put("usr_nm","GUEST");
		userinfo.put("usr_id","GUEST");
		user.setUserInfo(userinfo);

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userinfo, "");
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authRequest);

	}
	
	/**
	 * 로그인 처리
	 *
	 * @param username
	 */
	private void setSecurityContextByUsername(HttpServletRequest request, String username){
		// USR_ID/PW 조회
		User user = accountService.findUserNameAndPasswordForSupplier(username);
		if (user == null) {
			throw new UsernameNotFoundException(username + " id does not exist.");
		}

		// 사용자 정보 조회
		Map<String,Object> sessionUserInfo = accountService.findUserSessionInfo(username);
		user.setUserInfo(sessionUserInfo);

		// 세션 권한 조회
		Collection<GrantedAuthority> authorities = accountService.findListRoleCodeByLoginUserSession(sessionUserInfo);
		user.setAuthorities(authorities);
		UserDetailsProxy userinfo = new UserDetailsProxy(user);

		// 시큐리티 인증		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userinfo, userinfo.getPassword(), userinfo.getAuthorities());
		authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authRequest);

		// 사용자 권한 부여
		onSuccessAuthentication(securityContext.getAuthentication());
	}
	
	
	/**
	 *  사용자 권한 부여
	 *  
	 *  @param authentication
	 */
	public void onSuccessAuthentication(Authentication authentication) {
		// 현재 로그인 성공한 user 객체 조회
		Object principal = authentication.getPrincipal();
		UserDetailsProxy proxy = null;
		if (principal instanceof UserDetails) {
			proxy = (UserDetailsProxy)principal;
		}
		// 사용자 권한에 따른 메뉴-기능-URL 권한정보 session 저장
		if(proxy != null){
			proxy.setUserRoles(sharedService.findListMenuFunctionAndUrlByUserRoleList());
			// 사용자 운영조직 정보 저장
			proxy.setUserOperationOrganizationCodes(sharedService.findListOperationOrganizationByUser(null));
		}
	}
	
	
	/**
     * 초기 Tenant, Locale 세팅
     *
     * @param Param the param
     */
    @RequestMapping(value = "initSessionInfo.do")
    public @ResponseBody ResultMap initSessionInfo(@RequestBody Map param, HttpServletRequest request, HttpServletResponse response) {
	    ResultMap resultMap = ResultMap.getInstance();
    	String tenant = (String)param.get("tenant");
    	String locale = (String)param.get("locale");
    	// Tenant Set
    	try {
		    tenant = sharedService.getTenantId(tenant);
			sharedService.setTenant(tenant, request, response);
			
    		setSecurityContextOfGuest();
    	} catch(RuntimeException e) {
    		return resultMap.FAIL();
    	}
    	
    	// Locale Set
    	if(locale != null){
			WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, StringUtils.parseLocaleString(locale));
		}

    	Map<String,Object> globalValue = new HashMap<String, Object>();
    	globalValue.put("tenant",tenant);
    	globalValue.put("locale",locale);

        return resultMap.SUCCESS(globalValue);
    }

	/**
	 * 중복 체크.
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value = "checkDuplicatedVdInfo.do")
    public @ResponseBody List checkDuplicatedVdInfo(@RequestBody Map param) {
        return spVendorRegService.checkDuplicatedVdInfo(param);
    }
    
    /**
     * 약관 정보 조회
     *
     * @param Param
     * @Date : 2023. 05. 31
     * @author sykim
     */
    @RequestMapping(value = "findTermsList.do")
    public @ResponseBody List<Map<String, Object>> findTermsList(@RequestBody Map<String, Object> param) {
    	return spVendorRegService.findTermsList(param);
    }

	/**
	 * 협력사 신규등록 저장
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value = "saveBasicVdInfo.do")
    public @ResponseBody ResultMap saveBasicVdInfo(HttpServletRequest request, @RequestBody Map param) {
	    ResultMap resultMap = ResultMap.getInstance();
		List checkList = new ArrayList();
		//글로벌 사업 팀 요청 tax_id 필수 및 중복 체크 제거
	    //한국 또는 eu 일 때만 체크
		if("KR".equals(param.get("ctry_ccd")) || (param.get("eu_vat_reg_id") != null && !"".equals(param.get("eu_vat_reg_id")))){
			checkList = spVendorRegService.checkDuplicatedVdInfo(param);
		}
	    if(checkList.isEmpty()){
		    param.put("login_ip", request.getRemoteAddr());
		    resultMap = spVendorRegService.saveBasicVdInfo(param);

			Map<String, Object> resultData = resultMap.getResultData();
		    String username = (String)resultData.get("vd_cd");
		    setSecurityContextByUsername(request, username);
		    return resultMap;
	    }else{
		    return resultMap.DUPLICATED();
	    }
    }

	/**
	 * 협력사 주요정보 조회
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value="findDetailRegVdInfo.do")
    public @ResponseBody Map findDetailRegVdInfo(@RequestBody Map param){
    	return spVendorRegService.findDetailRegVdInfo(param);
    }

	/**
	 * 협력사 주요정보 저장
	 *
	 * @param param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value="saveDetailVdInfo.do")
    public @ResponseBody ResultMap saveDetailVdInfo(@RequestBody Map param){
	    return spVendorRegService.saveDetailVdInfo(param);
    }

	/**
	 * 협력사 재무정보 조회 (findVdStsInfo)
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
    @RequestMapping(value="findVendorFinanceInfo.do")
    public @ResponseBody Map findVendorFinanceInfo(@RequestBody Map param){
    	return spVendorRegService.findVendorFinanceInfo(param);
    }

	/**
	 * 협력사 재무정보 저장 (saveVdStsInfo)
	 *
	 * @param Param
	 * @Date : 2023. 05. 31
	 * @author sykim
	 */
	@RequestMapping(value="saveVendorFinanceInfo.do")
	public @ResponseBody ResultMap saveVendorFinanceInfo(@RequestBody Map param){
		return spVendorRegService.saveVendorFinanceInfo(param);
	}

	/**
	 * 사용대상 협력사 유형 목록 조회를 요청한다.
	 *
	 * @param param
	 * @Date : 2023. 06. 01
	 * @author yjPark
	 */
	@RequestMapping(value = "findListVmtUsing.do")
	public @ResponseBody List<Map<String, Object>> findListVmtUsing(@RequestBody Map param) {
		return spVendorMasterService.findListVmtUsing(param);
	}

	/**
	 * 추가 거래가능 협력사관리그룹 조회 (findListUnregisteredVendorManagementGroup)
	 *
	 * @param param
	 * @Date : 2023. 06. 22
	 * @author yjPark
	 */
	@RequestMapping(value = "findListUnregisteredVendorManagementGroup.do")
	public @ResponseBody FloaterStream findListUnregisteredVendorManagementGroup(@RequestBody Map param) {
		return spVendorMasterService.findListUnregisteredVendorManagementGroup(param);
	}

	/**
	 * 온보딩평가요청 - 등록요청을 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 6. 30
	 * @author yjPark
	 */
    @RequestMapping(value = "saveListReqOnboardingEval.do")
    public @ResponseBody ResultMap saveListReqOnboardingEval(@RequestBody Map param) {
        return spVendorRegService.saveListReqOnboardingEval(param);
    }

	/**
	 * 온보딩평가요청 - 등록취소를 저장한다.
	 *
	 * @param param the param
	 * @Date : 2023. 7. 11
	 * @author yjPark
	 */
    @RequestMapping(value = "saveListReqOnboardingEvalCancel.do")
    public @ResponseBody ResultMap saveListReqOnboardingEvalCancel(@RequestBody Map param) {
		return spVendorRegService.saveListReqOnboardingEvalCancel(param);
    }

	/**
	 * 온보딩평가수행 항목 정보 조회를 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the evalfact information to be fulfilled
	 * @Date : 2023. 7. 1
	 * @Method Name : findOnboardingEvalfactFulfillInfo
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "findOnboardingEvalfactFulfillInfo.do")
	public @ResponseBody ResultMap findOnboardingEvalfactFulfillInfo(@RequestBody Map param) {
		return spVendorRegService.findOnboardingEvalfactFulfillInfo(param);
	}

	/**
	 * 온보딩평가수행 정보 저장을 요청한다.
	 *
	 * @author : sykim
	 * @param param the param
	 * @return the map
	 * @Date : 2023. 7. 13
	 * @Method Name : saveOnboardingEvalFulfillment
	 */
	@AuthCheck (authCode = Const.READ)
	@RequestMapping(value = "saveOnboardingEvalFulfillment.do")
	public @ResponseBody ResultMap saveOnboardingEvalFulfillment(@RequestBody Map param) {
		return spVendorRegService.saveOnboardingEvalFulfillment(param);
	}
}
