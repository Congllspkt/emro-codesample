<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="java.util.*" %>
<!-- VestCert V3 인증서관리 프로그램
설치 후 이동할 페이지 경로 설정 : mainPageUrl
-->
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge"/>    
    <TITLE>인증서 관리프로그램 설치 &lt; 한국전자인증</TITLE>
    <style type="text/css">
        body,div,ul,ol,li,nav,section,footer{margin: 0px auto; padding: 0px; font-family :NanumGothic, dotum, Georgia, serif,san-serif; font-size : 14px;color: #333;}
        h1,h2,h3,h4,h5{ margin: 0px; padding: 0px; font-weight: normal;}
        ul,li{ list-style: none; }
        a{text-decoration:none;border-radius:3px;}
        
        #setup{ width: 95%; height: 100%; margin: auto;}
        #next{ padding: 10px}
        #faq{ float: right; margin-bottom: 10px }
        #list{ list-style: none; line-height: 150%;margin-top: 10px}
        #bottom{ width: 100%; background: #f7f8f9;padding:15px 10px; position: fixed; bottom:100px;}
        #floating{ position: fixed; bottom:7px; right:10px; z-index:1;}
        
        /* button */
        #btn{ text-align: center; margin-top:15px}
        #btn li{ display: inline; }
        .bl_s a{height: 32px; line-height: 32px; border: 1px solid #244997; background: #2e58a6;color: #fff; padding: 7px 20px; margin:5px;font-size : 12px;}
        .bl_s a:hover{color: #2e58a6; background: #fff}
        .wh a{height: 32px; line-height: 32px;  background: #fff;color: #2e58a6;border: 1px solid #244997; padding: 10px 10px;font-size : 18px;}
        .wh a:hover{ background: #2e58a6;color: #fff;}
        .wh_s a{line-height: 32px;  background: #fff;color: #2e58a6;border: 1px solid #244997;  padding: 6px 18px; font-size : 12px;}
        .wh_s a:hover{ background: #2e58a6;color: #fff;}
        .rd_s a:hover{  background: #fff;color: #e55c5c;border: 1px solid #e55c5c; }
        .rd_s a{ background: #e55c5c;color: #fff; line-height: 32px; padding: 7px 20px; font-size : 12px;}
        .setting{ color: red;}
        .flor{ float: right;  }
        #setup{ max-width: 720px; }
    
        /* table */
        table {border-collapse: collapse; width:100%; border-top: 2px solid #5e6062; border-left: 2px solid #fff; border-right: 2px solid #fff;margin-top:10px;text-align:center}
        td{ background: #f7f8f9; font-size: 12px;}
        td a{ color: #333; }
        td a:hover{ color: #28459d}
        th, td {padding:8px; border: 1px solid #dee0e2; line-height: 140%; }
        td li{ text-align: left;line-height: 180%; background: url('../images/icon_dot.gif') no-repeat 0 8px; padding-left: 8px;margin-left: 5px}
    </style>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/econtract/crosscert_home/unisignweb/framework/json2.js" ></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/econtract/crosscert_home/install/ObjectInstall.js" ></script>
</head>
<body>
    <div style="text-align:center;height:130px; width:100%; background:#0066bb;margin-bottom:5px"><img src="http://www.unisign.co.kr/kbstarQR/images/module.jpg" alt="" /></div>

    <div style="text-align:center"><img src="http://www.unisign.co.kr/kbstarQR/images/startstop.gif" class="loading" alt=""  /></div>

    <div id="setup">
        <ul id="list">
            <li>•고객님의 안전한 공동인증서비스 이용을 위하여 인증프로그램을 설치합니다.</li>
            <li>•설치를 마치신 후 반드시 새로고침 버튼을 눌러 주시기 바랍니다.</li>
            <li>•설치화면이 반복적으로 나올 경우 웹브라우저를 종료하고 다시 접속하세요.</li>
        </ul>
        <br><br>
        <div id="faq">
            <span style="margin-right: 15px;">문의전화 1566-0566</span>
            <span id="btn" class="wh_s"><a href="http://822.co.kr" target="_blank">원격지원서비스</a></span>&nbsp;&nbsp;
            <span id="btn" class="rd_s"><a href="https://board.crosscert.com/faq/uni/9" target="_blank">프로그램 설치 오류 해결하기</a></span>
        </div>
        <table>
            <tr>
                <th width="150px">프로그램명</th>
                <th>기능</th>
                <th width="150px">설치상태</th>
            </tr>
            <tr>
                <td>UniSign<br>(<span id="usversion"></span>)</td>
                <td> 인증서 이동을 위한 응용프로그램입니다. </td>
                <td id="install_2">
                    <span class="setting" id="install_txt_2">설치확인중</span><br />
                    <span id="btn_install_2" class="wh_s" style="display: none;"><a href="#" onclick="fnDownload()">설치하기</a></span>
                    <span id="btn_run_2" class="wh_s" style="display: none;"><a href="#" onclick="fnRun(2)">실행하기</a></span>
                </td>
            </tr>
        </table>
        <iframe id="us-downloadURL" name="us-downloadURL" width="0" height="0" style="display: none;"></iframe>
        <div id="next" style="text-align: center">
            설치가 완료되면 자동으로 페이지를 이동합니다.<br>
            <!-- <span id="btn" class="bl_s"><a href="javascript:;" onclick="self.close()" style="font-weight: bold;">확인</a></span> -->
        </div>
    </div>
    <div id="btn" class="bl_s"><a href="javascript:location.reload()">새로고침</a></div>
</body>

    <script type="text/javascript">
        function fnDownload(){
            document.getElementById("us-downloadURL").src = '${pageContext.request.contextPath}/resources/econtract/crosscert_home/install/UniSignCRSV3Setup.exe';
        }
        
        function fnUniCRSCall(){
            document.getElementById("hsmiframe").src = "UNICRSV3:///";
            setTimeout(function(){document.location.reload();}, 5000);
        }
        
        var checker = UniCRSInstall({
            options: {
                callerName: 'checker' // 선언 변수명과 반드시 동일 해야 함!
            },
            success: function(){
                document.getElementById("install_txt_2").innerHTML = "설치됨";
                document.getElementById("btn_install_2").style.display = 'none';
                document.dataForm.submit();
            },
            fail: function(code, msg, detailmsg){        
                document.getElementById("install_txt_2").innerHTML = msg;
                document.getElementById("next").innerHTML = detailmsg;            
        
                document.getElementById("btn_install_2").style.display = 'block';
            },
            progress: function(msg){
                document.getElementById("next").innerHTML = msg;
            }
        });
        document.getElementById('usversion').innerHTML = checker.info.version;
    
    </script>
    
    <!-- form 태그 -->
    <form name="dataForm" method="post" action="${callbackUrl}">
        <input type="hidden" name="installStatus" value="COMPLETE">
    </form>
    
    <!-- form 태그 밑 parameter 셋팅 -->
    <%
       Enumeration<String> enumeration = request.getParameterNames();
       while(enumeration.hasMoreElements()){
           String key   = enumeration.nextElement();
           String value = request.getParameter(key);
    %>
          <script>
              var input = document.createElement("input");
              input.type  = "hidden";
              input.name  = "<%=key%>";
              input.value = '<%=value%>';
              
              document.dataForm.appendChild(input);
          </script>
    <%
       }
    %>
</html>