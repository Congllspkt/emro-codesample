var __newpin=function(c){var p=function(g){function n(){var a=document.getElementById("us-new-pin-first-textbox").value,b=document.getElementById("us-pin-check-rule1"),c=document.getElementById("us-pin-check-rule2"),d=document.getElementById("us-pin-check-rule3"),f=!0;6==a.length?b.className="check":(b.className="",f=!1);if(2<a.length){for(b=0;b<a.length-2;b++)if(a.charAt(b)===a.charAt(b+1)&&a.charAt(b)===a.charAt(b+2)){c.className="";f=!1;break}else c.className="check";for(b=0;b<a.length-2;b++)if(a.charCodeAt(b)===a.charCodeAt(b+1)-1&&a.charCodeAt(b)===a.charCodeAt(b+2)-2){d.className="";f=!1;break}else if(a.charCodeAt(b)===a.charCodeAt(b+1)+1&&a.charCodeAt(b)===a.charCodeAt(b+2)+2){d.className="";f=!1;break}else d.className="check"}else c.className="check",d.className="check";return f}function h(a){if(!a)return alert("UI load error."),!1;var b=document.createElement("div");document.body.insertBefore(b,document.body.firstChild);b.innerHTML=a;return!0}function e(a){var b=document.getElementById("us-new-pin-first-textbox"),e=document.getElementById("us-new-pin-second-textbox"),d=b.value,f=e.value;if(!d)return c.uiUtil().msgBox(a.IDS_MSGBOX_ERROR_PLEASE_INPUT_NEW_PIN),b.focus(),!1;if(!n())return c.uiUtil().msgBox(a.IDS_MSGBOX_ERROR_CANT_PASS_RULES),b.focus(),!1;if(d!=f)return c.uiUtil().msgBox(a.IDS_MSGBOX_ERROR_PLEASE_RETRY_TO_INPUT_CORRECTLY),b.focus(),!1;g.onConfirm(d);b.value="";e.value="";return!0}function k(a){return 48>a.keyCode||57<a.keyCode?a.returnValue=!1:a.returnValue=!0}var p=function(){var a=window.XMLHttpRequest?new window.XMLHttpRequest:new ActiveXObject("MSXML2.XMLHTTP.3.0");a.open("GET",c.ESVS.SRCPath+"unisignweb/rsrc/layout/newpin.html?version="+c.ver,!1);a.send(null);return a.responseText},q=function(){var a=window.XMLHttpRequest?new window.XMLHttpRequest:new ActiveXObject("MSXML2.XMLHTTP.3.0");a.open("GET",c.ESVS.SRCPath+"unisignweb/rsrc/lang/"+c.ESVS.Language+"/newpin_"+c.ESVS.Language+".js?version="+c.ver,!1);a.send(null);return a.responseText},m=c.ESVS.TabIndex;return function(){var a=c.CustomEval(p),b=c.CustomEval(q,!0);h(a());document.getElementById("us-new-pin-lbl-title").appendChild(document.createTextNode(b.IDS_NEW_PIN));var l=document.getElementById("us-new-pin-cls-btn-img");l.setAttribute("src",c.ESVS.SRCPath+"unisignweb/rsrc/img/x-btn.png",0);l.setAttribute("tabindex",m,0);document.getElementById("us-new-pin-first-lbl").appendChild(document.createTextNode(b.IDS_NEW_PIN_FIRST+" :"));a=document.getElementById("us-new-pin-first-textbox");a.setAttribute("tabindex",m+1,0);a.setAttribute("title",b.IDS_NEW_PIN_FIRST,0);a.onkeypress=function(a){k(a?a:event)};a.onkeyup=function(a){n()};document.getElementById("us-new-pin-second-lbl").appendChild(document.createTextNode(b.IDS_NEW_PIN_SECOND+" :"));a=document.getElementById("us-new-pin-second-textbox");a.setAttribute("tabindex",m+2,0);a.setAttribute("title",b.IDS_NEW_PIN_SECOND,0);a.onkeydown=function(a){if(a=a?a:event)a=a||window.event,13==(a.which||a.keyCode)&&document.getElementById("us-new-pin-confirm-btn").click()};a.onkeypress=function(a){k(a?a:event)};document.getElementById("us-pin-check-rule1").appendChild(document.createTextNode("6 "+b.IDS_PIN_RULE1));document.getElementById("us-pin-check-rule2").appendChild(document.createTextNode(b.IDS_PIN_RULE2));document.getElementById("us-pin-check-rule3").appendChild(document.createTextNode(b.IDS_PIN_RULE3));a=document.getElementById("us-new-pin-confirm-btn");a.setAttribute("value",b.IDS_CONFIRM,0);a.setAttribute("title",b.IDS_CONFIRM+b.IDS_BUTTON,0);a.setAttribute("tabindex",m+3,0);a.onclick=function(){e(b)};var d=document.getElementById("us-new-pin-cancel-btn");d.setAttribute("value",b.IDS_CANCEL,0);d.setAttribute("title",b.IDS_CANCEL+b.IDS_BUTTON,0);d.setAttribute("tabindex",m+4,0);d.onclick=function(){g.onCancel()};a=document.getElementById("us-new-pin-cls-img-btn");a.setAttribute("title",b.IDS_NEW_PIN_CLOSE,0);a.setAttribute("tabindex",m+5,0);a.onclick=function(){g.onCancel()};l=document.getElementById("us-new-pin-cls-btn-img");d.onkeydown=function(a){a=a||window.event;9!=(a.which||a.keyCode)||a.shiftKey||(d.onblur=function(){setTimeout(function(){l.focus()},10)})};var f=!1;l.onkeyup=function(a){a=a||window.event;9==(a.which||a.keyCode)&&a.shiftKey&&f&&(f=!1,setTimeout(function(){d.focus()},10))};l.onkeydown=function(a){a=a||window.event;if(9==(a.which||a.keyCode)&&a.shiftKey)return a.cancelBubble=!0,f=a.returnValue=!0,!1};return document.getElementById("us-div-new-pin")}()};return function(g){var n=c.uiLayerLevel,h=c.uiUtil().getOverlay(n),e=p({type:g.type,args:g.args,onConfirm:g.onConfirm,onCancel:g.onCancel});e.style.zIndex=n+1;c.ESVS.TargetObj.insertBefore(h,c.ESVS.TargetObj.firstChild);var k=window.onresize;return{show:function(){c.ActiveUI=this;draggable(e,document.getElementById("us-div-new-pin-title"));h.style.display="block";c.uiUtil().offsetResize(e);window.onresize=function(){c.uiUtil().offsetResize(e);k&&k()};c.uiLayerLevel+=10;c.ESVS.TabIndex+=30;setTimeout(function(){document.getElementById("us-new-pin-first-textbox").focus()},10)},hide:function(){h.style.display="none";e.style.display="none"},dispose:function(){window.onresize=function(){k&&k()};e.parentNode.removeChild(e);h.parentNode.removeChild(h);c.uiLayerLevel-=10;c.ESVS.TabIndex-=30}}}};