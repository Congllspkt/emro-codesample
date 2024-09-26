<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="google" content="notranslate" />
    <link rel="stylesheet" type="text/css" href="../../css/login.css" />
	<style>
		.app-name {
			font-weight: bold;
		}
	</style>
</head>
	
<body class="bg_login">
	<div class="wrap">
		<div class="logo">emro</div>
		<div class="headline">OAuth2</div>
        <div class="container_right flex_center">
        	<div class="container">
            <div class="contents">
                <div class="logo_system">
                    <h2><img src="<c:url value='/img/logo_system.svg'/>"></h2>
                    <p>emro Intergrated Procurement system</p>
                </div>
                <div class="login_wrap mgt_100">
                	<span class="app-name"><c:out value="${applicationName}"></c:out></span> 어플리케이션은 현재 사용이 불가능합니다.
                </div>
            </div>
            </div>
        </div>
    </div>
</body>
</html>