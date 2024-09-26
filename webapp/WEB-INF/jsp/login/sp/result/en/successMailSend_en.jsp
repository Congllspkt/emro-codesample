<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="google" content="notranslate" />
	<title>SMARTsuite 10.0 Login</title>

	<link rel="stylesheet" type="text/css" href="../../../../../../css/base.css" />
	<link rel="stylesheet" type="text/css" href="../../../../../../css/popup.css" />
</head>
<body>
	<!--메일 전송 완료-->
	<div class="modal-body pw_serach">
		<h2>Email sent successfully</h2>
		<div class="content pw_result">
			<p>A temporary password has been issued to your email addres.</p>
			<p>Please reset your password after login.</p>
		</div>
		<div class="modal-footer">
			<a href="javascript:popupClose(this)" alt="close">close</a>
		</div>
	</div>

	<script type="text/javascript" src="/js/main/login.js"></script>
</body>

</html>