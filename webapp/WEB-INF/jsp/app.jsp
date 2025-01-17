<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
	<head>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <meta http-equiv="Cache-control" content="no-cache" />
	    <meta http-equiv="Pragma" content="no-cache" />
	    <meta http-equiv="Expires" content="0"/>
	    <meta name="robots" content="noindex, nofollow">
	    <meta name="viewport" content="width=device-width, minimum-scale=1.0, initial-scale=1, user-scalable=yes">
	    <meta name="${_passwordSaltSource.name}" salt="${_passwordSaltSource.salt}" iteration="${_passwordSaltSource.iterationCount}"/>
	    <meta name="${_aesCipherKey.name}" passphrase="${_aesCipherKey.passPhrase}" key="${_aesCipherKey.key}" iteration="${_aesCipherKey.iterationCount}" iv="${_aesCipherKey.iv}">

	    <meta name="smartsuiteVariables" cache_bust="${_cacheBust}"
	    								 session_timeout="${pageContext.session.maxInactiveInterval}"
	    								 is_sso="${isSso}" locale="${pageContext.response.locale}"
	    								 use_error_user_message="${useErrorUserMessage}"
	    								 is_parameter_encrypt="${_secParam}"
	    								 session_usr_id="${session_usr_id}" />

	    <title>SMARTsuite 10.0</title>
	    <sec:csrfMetaTags />

		<style type="text/css">
		    /** JSP Body Loading Bar Image */
			body.body-ready
			{
				background-image: url(ui/assets/img/body/body_loading.gif);
			    background-repeat: no-repeat;
			    background-position: center;
			}
		</style>

	</head>
	<body class="body-ready">

		<div class="top_progress"></div>
		<!-- logout form -->
		<form id="logoutForm" action="<c:url value='/logoutProcess.do' />" method="POST" hidden>
			<sec:csrfInput/>
			<input hidden="true" id="locale" name="locale" value="${pageContext.response.locale}"/>
		</form>

		<script src="license.do"></script>

		<!-- mdi 태그 선언 -->
		<sc-mdi id="mdiMain" use-single-page="true" use-storage-menu="false" hidden="true" main-module></sc-mdi>

        <script type="text/javascript">
		var mdiMain = document.getElementById('mdiMain'),
			start = function() {
			    var params = ${paramMap};
	        	var menuId = params.menuId;

	        	if(mdiMain.mdiMainCompleted) {
        			var menuList = SCMenuManager.getMenuList();
		        	var windowUrl = "";
		        	var menuName = "";
		        	for (idx in menuList) {
	                    if (menuId == menuList[idx].menu_id) {
	                    	windowUrl = menuList[idx].menu_url;
	                   	 	menuName = menuList[idx].menu_nm;
	                    }
	                }
        			if(windowUrl.indexOf('?') > -1){
        				importUrl = windowUrl.slice(0, windowUrl.indexOf('?'));
        			}else{
        				importUrl = windowUrl;
        			}
        			MDIUT.createWindow(menuId, menuName, importUrl, {lazy: false, params : params});
	        	}

				if(window.CollectGarbage) {
					setInterval(function() {
						CollectGarbage();
				   	}, 60000);
		    	}
			};

		mdiMainCompletedHandler = function(e) {
			mdiMain.removeEventListener('mdi-manager-initialized', mdiMainCompletedHandler);
			mdiMain.mdiMainCompleted = true;
			start();
		};
		mdiMain.addEventListener('mdi-manager-initialized', mdiMainCompletedHandler);

		document.addEventListener('keydown',function(e){
			var ele = e.srcElement ? e.srcElement : e.target,
				rx = /INPUT|SELECT|TEXTAREA/i,
			    k = e.which || e.keyCode;
			if((!ele.type || !rx.test(e.target.tagName)|| (ele.readOnly || ele.disabled )) && ["true", ""].indexOf(ele.getAttribute("contenteditable")) === -1 ) {
	 	    	if(k == 8){
	 	    		e.preventDefault();
	 	     	}
	 	     }
		},true);

	</script>

		<script src="ui/smartsuite/standard-loader.js"></script>
		<script src="ui/smartsuite/standard-resources.js"></script>
	  	<script src="ui/smartsuite/standard-setup.js"></script>
	  	<script src="ui/smartsuite/standard-util.js"></script>

	</body>
</html>