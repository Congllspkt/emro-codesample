<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="google" content="notranslate" />
		<title>SMARTsuite 10.0</title>
		<link rel="stylesheet" type="text/css" href="../../css/login.css" />
		<style>
			.confirm_wrap {
			  font-size: 13pt;
			  margin-top: 50px;
			}
			.confirm_wrap .summary .app-name {
				font-weight: bold;
			}
			.confirm_wrap .notice-approvals {
				margin: 7px;
				overflow: auto;
			}
			.confirm_wrap .notice-approvals .scope-desc {
				font-size: 13pt;
				height: 34px;
				margin: 9px;
				border-bottom: 1px solid lightgrey;
			}
			.confirm_wrap .notice-approvals .scope-desc .scope-icon {
				width: 12px;
				height: 12px;
				display: inline-block;
				background-color: #5D2ECF;
				border-radius: 10em;
			}
			.confirm_wrap .terms {
				padding-top: 10px;
			}
			.action-btn-group {
				display: flex;
				width: 100%;
  				height: 52px;
  				margin-top: 30px;
			}
			.action-btn-group {
				display: flex;
				flex:1;
			}
			.action-btn-group .action-btn {
				display: block;
				flex:1;
				margin: 2px;
				border: 0;
				text-align: center;
				border-radius: 4px;
				background: #5D2ECF;
				text-align: center;
				font-weight: 400;
			  	color: #FFFFFF;
			  	line-height: 52px;
			 	letter-spacing: .5px;
			 	cursor: pointer;
			}
			.action-btn-group .action-btn:hover {
			  background: #1E0165;
			}
			.action-btn-group .btn_apporval {
			}
			.action-btn-group .btn_deny {
				background: gray;
			}
		</style>
	</head>
	<body>
		<div class="wrap">
			<div class="logo">emro</div>
			<div class="headline">OAuth2</div>
			
			<div class="container_right flex_center">
				<div class="container">
	                <div class="logo_system">
	                    <h2><img src="<c:url value='/img/logo_system.svg'/>"></h2>
	                    <p>emro Intergrated Procurement system</p>
	                </div>
	                <div class="confirm_wrap mgt_100">
	                	<form name="consent_form" method="post" action="${requestURI}">
			                <input type="hidden" name="client_id" value="${clientId}">
			                <input type="hidden" name="state" value="${state}">	                	
		                	<div class="summary">
		           				<span class="app-name"><c:out value="${clientNm}"></c:out></span> 
		           				어플리케이션에서 사용자에게 다음과 같은 접근 권한을 요청합니다.
		           			</div>
		           			<div class="notice-approvals">
		           				<c:forEach var="apiGroupInfo" items="${apiGroupList}">
		            				<div class="scope-desc">
		            					<div class="scope-icon"></div>
		            					<input type="checkbox" name="scope" id="${apiGroupInfo.apiGrpCd}" value="${apiGroupInfo.apiGrpCd}" checked="true" style="display:none;"/>
		            					<span>${apiGroupInfo.apiGrpExpln}</span>
		            				</div>
		           				</c:forEach>
		           			</div>
		           			<div class="terms">
		           				허용 시 사용자는 엠로의 사용자 보호 약관에 동의하게 되며,
		           				해당 보호 약관은 다음에서 확인하실 수 있습니다. 
		           			</div>
		           			<div class="action-btn-group">
		           				<button class="action-btn btn_apporval" type="submit">동의하기</button>
		           				<button class="action-btn btn_deny" type="button" onclick="cancelConsent();">취소</button>
		           			</div>
	           			</form>
	                </div>
            		<!-- Footer -->
		            <div class="footer">
		                <p class="copyright">ⓒ emro All Right Reserved.</p>
		            </div>
	            </div>
			</div>

		</div>
		<script type="text/javascript" src="../../bower_components/crypto-js/crypto-js.js"></script>
        <script type="text/javascript" src="../../bower_components/password-encryptor/password-encryptor.min.js"></script>

        <script src="../../license.do"></script>

        <script src="../../bower_components/webcomponentsjs/webcomponents-lite.min.js"></script>
	    <script>
	        function cancelConsent() {
	        	var scopeList = document.consent_form.querySelectorAll('[name="scope"]');
	        	for (var i = 0, len = scopeList.length; i < len; i++) {
	        		scopeList[i].checked = false;
				}
	            document.consent_form.submit();
	        }
	    </script>        
        <link rel="import" href="../../bower_components/sc-component/sc-elements.build.html"/>
        <link rel="stylesheet" type="text/css" href="../../bower_components/sc-component/sc-elements.build.css"/>
	</body>
</html>