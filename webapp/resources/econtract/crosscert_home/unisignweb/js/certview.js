var __certview=function(a){var D=function(r){function A(a){if(!a)return alert("UI load error."),!1;var b=document.createElement("div");document.body.insertBefore(b,document.body.firstChild);b.innerHTML=a;return!0}function l(){z.restoreOnMouseEvent();r.onCancel()}function q(c){c=parseInt(c);var b=document.getElementById("us-view-simple"),f=document.getElementById("us-view-detail"),e=document.getElementById("us-view-certpath"),g=document.getElementById("us-view-cert-cps-btn");document.getElementById("us-view-cert-confirm-btn");document.getElementById("us-view-cls-img-btn");0===c?(b.style.display="block",f.style.display="none",e&&(e.style.display="none"),a.uiUtil().isIraq()?g.style.display="none":g.style.display="inline",z.clearList()):1===c?(b.style.display="none",f.style.display="block",e&&(e.style.display="none"),g.style.display="none",z.redrawList(B.list,B.list.length)):2===c&&(b.style.display="none",f.style.display="none",e.style.display="block",g.style.display="none")}function u(a,b){a=a||window.event;a=a.target||a.srcElement;b=b.getElementsByTagName("a");var c=document.getElementById("us-view-tab-simple"),e=document.getElementById("us-view-tab-detail"),g=document.getElementById("us-view-tab-certpath");for(nTab in b)if(a==b[nTab]){isNaN(nTab)&&("us-view-tab-simple"==nTab?nTab=0:"us-view-tab-detail"==nTab?nTab=1:"us-view-tab-certpath"==nTab&&(nTab=2));0==nTab?(c.className="us-view-tab-tag active",e.className="us-view-tab-tag",g&&(g.className="us-view-tab-tag")):1==nTab?(c.className="us-view-tab-tag",e.className="us-view-tab-tag active",g&&(g.className="us-view-tab-tag")):2==nTab&&(c.className="us-view-tab-tag",e.className="us-view-tab-tag",g.className="us-view-tab-tag active");q(nTab);break}}function I(c,b,f,e){a.usWebToolkit.x509Certificate.parser(e,f);e=a.usWebToolkit.x509Certificate.getVersion();var g=a.usWebToolkit.x509Certificate.getSerialNumber(),k=a.usWebToolkit.x509Certificate.getSignAlgo(),h=a.usWebToolkit.x509Certificate.getIssuerName(),m=a.certUtil().getLocalDateNTime(a.usWebToolkit.x509Certificate.getNotBefore()),p=a.certUtil().getLocalDateNTime(a.usWebToolkit.x509Certificate.getNotAfter()),d=a.usWebToolkit.x509Certificate.getSubjectName(),t=a.usWebToolkit.x509Certificate.getPublickey(),v=a.usWebToolkit.x509Certificate.getAuthorityKeyIdentifier(),E=a.usWebToolkit.x509Certificate.getSubjectKeyIdentifier(),y=a.usWebToolkit.x509Certificate.getKeyUsage(),C=a.usWebToolkit.x509Certificate.getCertificatePoliciesOid(),F=a.usWebToolkit.x509Certificate.getSubjectAltName(),q=a.usWebToolkit.x509Certificate.getAuthorityInfoAccess(),r=a.usWebToolkit.x509Certificate.getcRLDistributionPoints();a.usWebToolkit.x509Certificate.getCertificatePoliciesCPS();a.usWebToolkit.x509Certificate.getCertificatePoliciesUserNotice();var l="";try{l=a.usWebToolkit.x509Certificate.getCertificatePoliciesCPS()}catch(J){l=""}var x="";try{x=a.usWebToolkit.x509Certificate.getCertificatePoliciesUserNotice()}catch(J){x=""}var u=a.usWebToolkit.x509Certificate.getSignature();f=[];f[0]=e;f[1]=g;f[2]=k;f[3]=h;f[4]=m;f[5]=p;f[6]=d;f[7]=t;"MPKI"==a.ESVS.PKI?(f[8]=y,f[9]=C,f[10]=r,f[11]=u):(f[8]=v,f[9]=E,f[10]=y,f[11]=C,f[12]=F,f[13]=q,f[14]=r,f[15]=l,f[16]=x,f[17]=u);e=[];for(g=0;g<f.length;g++)k=[],k[0]=b[g].field,k[1]=f[g],e[g]=k;B={list:e};G=a.loadUI("gridlist");z=G({type:"detailslist",tblid:"us-view-tbl-list",tbltitleid:"us-view-tbl-list-th",titlelistid:"us-view-grid-head-div",titlerowid:"us-view-list-title-row",titleelementid:"us-view-list-title-element",titledividerid:null,titlelistcn:"us-layout-view-grid-head-div",titlerowcn:"us-layout-view-grid-head-row",titleelementcn:"us-layout-view-grid-row-title-element",titledividercn:null,tblbodyid:"us-view-tbl-list-td",datalistid:"us-view-grid-body-div",datarowid:"us-view-list-body-row",dataelementid:"us-view-list-data-element",datalistcn:"us-layout-view-grid-body-div",datarowcn:"us-layout-view-grid-body-row",dataelementcn:"us-layout-view-grid-row-data-element",dataselectcn:"us-layout-view-grid-row-data-selected-element",textboxid:"us-view-detail-text-box"});z.drawTitle(c,c.length,w+3,!1)}function x(c,b,f,e,g){if(!c||0>=b||!f||!e)return!1;var k=document.getElementById("us-view-cert-status-img"),h=document.getElementById("us-view-cert-status-msg"),m=-1;if(4<=a.ESVS.Mode&&!a.uiUtil().isItPFDevice(a.SELECTINFO.curdevice))a.SELECTINFO.curdevice!=a.CONST.__USFB_M_DISK.device&&a.SELECTINFO.curdevice!=a.CONST.__USFB_M_HDD.device||null==a.Whale()?a.nimservice()?a.nimservice().VerifyCertificate(b,1,function(b,d){if(0===b)p=!0,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_valid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_VALID));else switch(p=!1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),b){case 3005:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_EXPIRED));break;case 3016:case 3017:case 3900:case 3901:case 3902:case 3903:case 3904:case 3905:case 3906:case 3907:case 3908:case 3909:case 3999:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_REVOKED));__RevokedStatus=!0;break;case 4212E4:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_EXPIRED));__RevokedStatus=!0;break;default:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_INVALID))}g(0)}):(a.uiUtil().msgBox(__lang.IDS_MSGBOX_NIM_ERROR_UNLOAD),rv=-1,g(-1)):a.Whale().verifyCertitficate(b,function(b,d){if(0===b)p=!0,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_valid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_VALID));else switch(p=!1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),b){case 3005:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_EXPIRED));break;case 3016:case 3017:case 3900:case 3901:case 3902:case 3903:case 3904:case 3905:case 3906:case 3907:case 3908:case 3909:case 3999:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_REVOKED));__RevokedStatus=!0;break;case 4212E4:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_EXPIRED));__RevokedStatus=!0;break;default:h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_INVALID))}g(0)});else if(2&a.ESVS.Mode)try{var l=a.usWebToolkit.pki.createCaStore(),d=a.PFSH.GetCACerts(),t;for(t in d)caCert=d[t],l.addCertificate(a.usWebToolkit.pki.certificateFromBase64(caCert));var v=a.usWebToolkit.pki.certificateFromBase64(f);a.usWebToolkit.pki.verifyCertificateChain(l,v,function(b,d,c){if(null!=k&&null!=h){if(!0===b){a.usWebToolkit.x509Certificate.parser(f,"Base64");var v=a.usWebToolkit.x509Certificate.getcRLDistributionPoints();if(""==v)m=-1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_INVALID)),p=!1;else if(b=a.usWebToolkit.x509Certificate.crlDownload(a.usWebToolkit.usWebCMP.info.CMPUrl,v),null!=b&&""!=b){d=b;if("object"==typeof b){v=v.split("?");var y="";1<v.length&&(y=v[1]);y&&(d=b[y])}!1===a.usWebToolkit.x509Certificate.verifyCRL(c[1],a.usWebToolkit.util.decode64(d)).verify?(m=-1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_REVOKED)),p=!1):(m=0,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_valid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_VALID)),p=!0)}else m=-1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_INVALID)),p=!1}else m=-1,k.setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/cert_invalid.png",0),null!=c&&void 0!=c&&0<=c.indexOf("Certificate is not valid yet or has expired")?h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_EXPIRED)):null!=c&&void 0!=c&&0<=c.indexOf("no parent issuer, so certificate not trusted")?h.appendChild(document.createTextNode(e.IDS_CERT_STATUS_ISSUER_FAIL)):h.appendChild(document.createTextNode(c)),p=!1;g(m)}})}catch(E){g(-1)}(function(){a.usWebToolkit.x509Certificate.parser(f,c);document.getElementById("us-view-cert-subject-name-data").appendChild(document.createTextNode(a.certUtil().getCN(a.usWebToolkit.x509Certificate.getSubjectName())));var b=document.getElementById("us-view-cert-issuer-name-data"),d=a.certUtil().getCN(a.usWebToolkit.x509Certificate.getIssuerName());if(""==d||"undefined"==d)d=a.certUtil().getO(a.usWebToolkit.x509Certificate.getIssuerName());b.appendChild(document.createTextNode(d));document.getElementById("us-view-cert-validity-date-from").appendChild(document.createTextNode(a.certUtil().getLocalDate(a.usWebToolkit.x509Certificate.getNotBefore())));document.getElementById("us-view-cert-validity-date-to").appendChild(document.createTextNode(a.certUtil().getLocalDate(a.usWebToolkit.x509Certificate.getNotAfter())));b=document.getElementById("us-view-cert-purpose");d=null;try{d=a.usWebToolkit.x509Certificate.getCertificatePoliciesUserNotice()}catch(C){d=""}null!=d&&b.appendChild(document.createTextNode(d));b=document.getElementById("us-view-cert-cps-btn");if("MPKI"==a.ESVS.PKI||a.uiUtil().isIraq())b.style.visibility="hidden";b.onclick=function(){var b=a.usWebToolkit.x509Certificate.getCertificatePoliciesCPS();"firefox"==a.browserName?window.open(b,"cps_url","scrollbars=1"):window.open(b);this.focus()}})();return!0}function D(a){if(null==a||0==a.length)return"";for(var b=a.length,c="",e=0,g=b;e<b;e++,g--)0==g%4&&g!=b&&(c+=" "),c+=a.charAt(e);return c.toUpperCase()}function H(c,b,f,e,g){function k(a,b){(a=document.getElementById(a))&&a.appendChild(document.createTextNode(b))}function h(a,b){var d=document.createElement("li");1==a?d.setAttribute("class","valid"):0==a?d.setAttribute("class","invalid"):null!=a?d.setAttribute("class",a):d.appendChild(document.createTextNode(b));return d}function m(b,d){if(null==b||0>=b.length)return null;b=a.usWebToolkit.x509Certificate.parser(b,d);d=a.usWebToolkit.asn1.fromDer(b.getExtension("authorityKeyIdentifier").value);b=a.usWebToolkit.util.bytesToHex(d.value[0].value);d.value[2]&&(b+="_"+parseInt(a.usWebToolkit.util.bytesToHex(d.value[2].value),16));d=a.PFSH.GetCACerts();var c=null,e;for(e in d)e.toUpperCase()==b.toUpperCase()&&(c=d[e]);return c}function l(b,g,m){try{a.usWebToolkit.x509Certificate.parser(g,c);d.appendChild(h(!0));d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()));k("us-view-certpath-version",e.IDS_CERT_PATH_ROOTCA_VERSION);k("us-view-certpath-version-data",a.usWebToolkit.x509Certificate.getVersion());k("us-view-certpath-date",e.IDS_CERT_PATH_ROOTCA_VALIDDATE);k("us-view-certpath-date-from",a.certUtil().getLocalDateNTime(a.usWebToolkit.x509Certificate.getNotBefore()));k("us-view-certpath-date-to",a.certUtil().getLocalDateNTime(a.usWebToolkit.x509Certificate.getNotAfter()));a.usWebToolkit.x509Certificate.parser(b,c);d.appendChild(h("dot"));d.appendChild(h(!0));d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()));a.usWebToolkit.x509Certificate.parser(f,c);d.appendChild(h("dot2"));d.appendChild(h(p));d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()));var l=D(m);if(null==l||0==l.length)l="  ";k("us-view-certpath-hash",e.IDS_CERT_PATH_NOTICE);k("us-view-certpath-hash-data",l);k("us-view-certpath-tail",e.IDS_CERT_PATH_ROOTHASH_URL);k("us-view-certpath-tail",e.IDS_CERT_PATH_ROOTHASH_URL2);k("us-view-certpath-tail",e.IDS_CERT_PATH_ROOTHASH_URL3);document.getElementById("us-view-certpath-tail").setAttribute("tabindex",w+6,0);document.getElementById("us-view-certpath-tail").setAttribute("title",e.IDS_LINK_TITLE,0);document.getElementById("us-view-certpath-tail").onclick=function(){window.open(e.IDS_CERT_PATH_ROOTHASH_LINK,"ca_hash","scrollbars=1;width=650;height=460;")};document.getElementById("us-view-certpath-tail").onkeydown=function(a){13==a.keyCode&&window.open(e.IDS_CERT_PATH_ROOTHASH_LINK,"ca_hash","scrollbars=1;width=650;height=460;")}}catch(F){a.usWebToolkit.x509Certificate.parser(f,c),d&&d.appendChild(h(p)),d&&d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()))}}"MPKI"==a.ESVS.PKI&&g();if(4&a.ESVS.Mode&&!a.nimservice())a.uiUtil().msgBox(e.IDS_MSGBOX_NIM_ERROR_UNLOAD);else{var d=document.getElementById("us-view-certpath-info-tree");if(4&a.ESVS.Mode&&!a.uiUtil().isItPFDevice(a.SELECTINFO.curdevice))a.SELECTINFO.curdevice!=a.CONST.__USFB_M_DISK.device&&a.SELECTINFO.curdevice!=a.CONST.__USFB_M_HDD.device||null==a.Whale()?a.nimservice().GetCACertificates(b,function(b,e,k,m,t){a.uiUtil().loadingBox(!1,"us-div-list-load");null!=d&&(0==b?l(k,m,t):(a.usWebToolkit.x509Certificate.parser(f,c),d&&d.appendChild(h(p)),d&&d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()))),g())}):a.Whale().getCACertificates(b,function(b,e,k,m,t){a.uiUtil().loadingBox(!1,"us-div-list-load");null!=d&&(0==b?l(k,m,t):(a.usWebToolkit.x509Certificate.parser(f,c),d&&d.appendChild(h(p)),d&&d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName()))),g())});else if(2&a.ESVS.Mode){var t=b=null;b=m(f,c);t=m(b,c);null!=b&&null!=t?l(b,t):(a.usWebToolkit.x509Certificate.parser(f,c),d&&d.appendChild(h(p)),d&&d.appendChild(h(null,a.usWebToolkit.x509Certificate.getSubjectName())));g()}}}var K=function(){var c=window.XMLHttpRequest?new window.XMLHttpRequest:new ActiveXObject("MSXML2.XMLHTTP.3.0");"MPKI"==a.ESVS.PKI?c.open("GET",a.ESVS.SRCPath+"unisignweb/rsrc/layout/certview_verisign.html?version="+a.ver,!1):c.open("GET",a.ESVS.SRCPath+"unisignweb/rsrc/layout/certview.html?version="+a.ver,!1);c.send(null);return c.responseText},L=function(){var c=window.XMLHttpRequest?new window.XMLHttpRequest:new ActiveXObject("MSXML2.XMLHTTP.3.0");c.open("GET",a.ESVS.SRCPath+"unisignweb/rsrc/lang/"+a.ESVS.Language+"/certview_"+a.ESVS.Language+".js?version="+a.ver,!1);c.send(null);return c.responseText},G=null,z=null,B=null,p=!0,w=a.ESVS.TabIndex;return function(){var c=a.CustomEval(K),b=a.CustomEval(L,!0),f=r.args.type,e=r.args.idx,g=r.args.cert;A(c());var k=document.getElementById("us-view-lbl-title");k.appendChild(document.createTextNode(b.IDS_CERT_VIEW));k.setAttribute("tabindex",w,0);c=document.getElementById("us-view-tab");c.onclick=function(a){u(a?a:event,this)};c.onkeydown=function(a){if((a=a?a:event)&&this){var b=a||window.event;13==(b.which||b.keyCode)&&u(a,this)}};c=document.getElementById("us-view-tab-simple");c.setAttribute("tabindex",w+1,0);c.appendChild(document.createTextNode(b.IDS_CERT_SIMPLE_VIEW));c.onfocus=function(a){a.target.style.border="1px dotted"};c.onblur=function(a){a.target.style.border=""};document.getElementById("us-view-simple").setAttribute("tabindex",w+2,0);c=document.getElementById("us-view-tab-detail");c.appendChild(document.createTextNode(b.IDS_CERT_DETAIL_VIEW));c.setAttribute("tabindex",w+3,0);document.getElementById("us-view-tbl-list-caption").innerText=b.IDS_CET_DETAIL_VIEW_CAPTION;c.onfocus=function(a){a.target.style.border="1px dotted"};c.onblur=function(a){a.target.style.border=""};if(c=document.getElementById("us-view-tab-certpath"))c.appendChild(document.createTextNode(b.IDS_CERT_PATH_VIEW)),c.setAttribute("tabindex",w+5,0),c.onfocus=function(a){a.target.style.border="1px dotted"},c.onblur=function(a){a.target.style.border=""};document.getElementById("us-view-cert-info").appendChild(document.createTextNode(b.IDS_CERT_INFO));document.getElementById("us-view-cert-subject-name").appendChild(document.createTextNode(b.IDS_CERT_SUBJECT_NAME));document.getElementById("us-view-cert-issuer-name").appendChild(document.createTextNode(b.IDS_CERT_ISSUER_NAME));document.getElementById("us-view-cert-validity-date").appendChild(document.createTextNode(b.IDS_CERT_VALIDITY_DATE));c=document.getElementById("us-view-cert-cps-btn");c.setAttribute("value",b.IDS_CERT_SUPPLEMENT_INFO,0);c.setAttribute("tabindex",w+7,0);c.setAttribute("title",b.IDS_LINK_TITLE,0);c.style.display="none";var h=document.getElementById("us-view-cert-confirm-btn");h.setAttribute("value",b.IDS_CERT_CONFIRM,0);h.setAttribute("tabindex",w+8,0);h.onclick=function(){l()};var m=document.getElementById("us-view-cls-img-btn");m.setAttribute("alt",b.IDS_CERT_VIEW_CLOSE,0);m.setAttribute("tabindex",w+9,0);m.onclick=function(){l()};m.onfocus=function(a){m.style.border="1px dotted white"};document.getElementById("us-view-cls-btn-img").setAttribute("src",a.ESVS.SRCPath+"unisignweb/rsrc/img/x-btn.png",0);m.onkeydown=function(a){var b=a||window.event,d=b.which||b.keyCode;9==d&&b.shiftKey&&(m.onblur=function(){m.style.border="";setTimeout(function(){h.focus()},10)});9!=d||b.shiftKey||(m.onblur=function(){m.style.border="";setTimeout(function(){k.focus()},10)});13==a.keyCode&&(a.preventDefault(),l())};var p=!1;k.onkeyup=function(a){a=a||window.event;9==(a.which||a.keyCode)&&a.shiftKey&&p&&(p=!1,setTimeout(function(){m.focus()},10))};k.onkeydown=function(a){a=a||window.event;if(9==(a.which||a.keyCode)&&a.shiftKey)return a.cancelBubble=!0,p=a.returnValue=!0,!1};I([{title:b.IDS_CERT_FIELD},{title:b.IDS_CERT_VALUE}],"MPKI"==a.ESVS.PKI?[{field:b.IDS_CERT_VERSION},{field:b.IDS_CERT_SERIAL_NUMBER},{field:b.IDS_CERT_SIGN_ALGOLISM},{field:b.IDS_CERT_ISSUER},{field:b.IDS_CERT_VALIDATE_FROM},{field:b.IDS_CERT_VALIDATE_TO},{field:b.IDS_CERT_SUBJECT},{field:b.IDS_CERT_PUBLIC_KEY},{field:b.IDS_CERT_KEY_USAGE},{field:b.IDS_CERT_POLICY},{field:b.IDS_CERT_CRL_DISTRIBUTE_POINTS},{field:b.IDS_CERT_SIGNATURE}]:[{field:b.IDS_CERT_VERSION},{field:b.IDS_CERT_SERIAL_NUMBER},{field:b.IDS_CERT_SIGN_ALGOLISM},{field:b.IDS_CERT_ISSUER},{field:b.IDS_CERT_VALIDATE_FROM},{field:b.IDS_CERT_VALIDATE_TO},{field:b.IDS_CERT_SUBJECT},{field:b.IDS_CERT_PUBLIC_KEY},{field:b.IDS_CERT_AUTHORITY_KEY_IDENTIFIER},{field:b.IDS_CERT_SUBJECT_KEY_IDENTIFIER},{field:b.IDS_CERT_KEY_USAGE},{field:b.IDS_CERT_POLICY},{field:b.IDS_CERT_SUBJECT_ALT_NAME},{field:b.IDS_CERT_AUTHORITY_INFO_ACCESS},{field:b.IDS_CERT_CRL_DISTRIBUTE_POINTS},{field:b.IDS_CERT_CPS},{field:b.IDS_CERT_PURPOSE},{field:b.IDS_CERT_SIGNATURE}],f,g);4&a.ESVS.Mode&&!a.uiUtil().isItPFDevice(a.SELECTINFO.curdevice)?x(f,e,g,b,function(a){H(f,e,g,b,function(a){})}):2&a.ESVS.Mode?x(f,e,g,b,function(a){H(f,e,g,b,function(a){})}):(x(f,e,g,b),document.getElementById("us-view-tab3").style.display="none");q(0);return document.getElementById("us-div-cert-view")}()};return function(r){var A=a.uiLayerLevel,l=a.uiUtil().getOverlay(A),q=D({type:r.type,args:r.args,onConfirm:r.onConfirm,onCancel:r.onCancel});q.style.zIndex=A+1;a.ESVS.TargetObj.insertBefore(l,a.ESVS.TargetObj.firstChild);var u=window.onresize;return{show:function(){a.ActiveUI=this;draggable(q,document.getElementById("us-div-view-title"));l.style.display="block";a.uiUtil().offsetResize(q);window.onresize=function(){a.uiUtil().offsetResize(q);u&&u()};a.uiLayerLevel+=10;a.ESVS.TabIndex+=30;setTimeout(function(){var a=q.getElementsByTagName("p");if(0<a.length)for(var l=0;l<a.length;l++)"us-view-lbl-title"==a[l].id&&a[l].focus()},10)},hide:function(){l.style.display="none";q.style.display="none"},dispose:function(){window.onresize=function(){u&&u()};q.parentNode.parentNode.removeChild(q.parentNode);l.parentNode.removeChild(l);a.uiLayerLevel-=10;a.ESVS.TabIndex-=30}}}};