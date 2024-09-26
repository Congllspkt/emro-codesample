<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String event = (String)request.getAttribute("event");
    String usrCls = (String)request.getAttribute("usrCls");
    String returnUiId = (String)request.getAttribute("return_ui_id");
%>
<script language="javascript">

	var agent = navigator.userAgent.toLowerCase();
	var browser = "";
	if ((navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ) {
		browser = "IE";
	}
	if(browser === "IE") {
		if('<%=usrCls%>' === "BUYER") {
			alert("서명완료하였고, 계약서를 협력사에게 발송하였습니다.\n IE 브라우저는 재조회를 해야 변경된 진행상태를 확인 할 수 있습니다.");
		} else if('<%=usrCls%>' === "VD") {
			if('<%=event%>' === "rejected"){
				alert("거부 하였습니다.\n IE 브라우저는 재조회를 해야 변경된 진행상태를 확인 할 수 있습니다.");
			} else if('<%=event%>' === "completed") {
				alert("서명 완료하였습니다.\n IE 브라우저는 재조회를 해야 변경된 진행상태를 확인 할 수 있습니다.");
			}
        }
		this.close();
	} else {
		if(this.opener) {
			var parentNode = this.opener.document.querySelector('<%=returnUiId%>');
			parentNode.adobesignCallback('<%=event%>');
			this.close();
		} else {
			this.close();
        }
	}
</script>
