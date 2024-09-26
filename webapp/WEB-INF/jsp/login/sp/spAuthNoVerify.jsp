<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    <sec:csrfMetaTags />
    <title>인증번호 입력</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .container {
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            text-align: center;
            max-width: 400px; /* 최대 너비 설정 */
            width: 100%; /* 화면 크기에 맞게 너비 조정 */
        }
        .container h1 {
            margin-bottom: 20px;
            font-size: 24px;
        }
        .input-group {
            margin-bottom: 20px;
        }
        .input-group label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .input-group input {
            width: 100%;
            max-width: 100%; /* 입력 필드가 컨테이너를 넘치지 않도록 */
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box; /* 패딩을 포함한 전체 너비 조정 */
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 100%; /* 버튼이 컨테이너 전체를 차지하도록 */
            box-sizing: border-box;
        }
        .btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>인증번호 입력</h1>
        <form action="supplierAuthNumberVerifyProcess.do" method="post">
            <div class="input-group">
                <label for="auth_no">인증번호</label>
                <input type="text" id="auth_no" name="auth_no" required>
            </div>
            <input type="submit" class="btn" value="확인">
            <input name="data" value="${data}" type="hidden">
            <input name="ctry" value="${ctry}" type="hidden">
            <sec:csrfInput/>
    </div>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <script type="text/javascript">
            alert('<%= errorMessage %>');
        </script>
    <%
        }
    %>
</body>
</html>