(function(){function p(k){var b=k.util=k.util||{},l=k.jsustoolkitErrCode=k.jsustoolkitErrCode||{};b.getVersion=function(){return"jsustoolkit V1.1.18.0"};b.ByteBuffer=function(a){this.data=a||"";this.read=0};b.ByteBuffer.prototype.length=function(){return this.data.length-this.read};b.ByteBuffer.prototype.isEmpty=function(){return 0===this.data.length-this.read};b.ByteBuffer.prototype.putByte=function(a){this.data+=String.fromCharCode(a)};b.ByteBuffer.prototype.fillWithByte=function(a,c){a=String.fromCharCode(a);for(var d=this.data;0<c;)c&1&&(d+=a),c>>>=1,0<c&&(a+=a);this.data=d};b.ByteBuffer.prototype.putBytes=function(a){this.data+=a};b.ByteBuffer.prototype.putString=function(a){this.data+=b.encodeUtf8(a)};b.ByteBuffer.prototype.putInt16=function(a){this.data+=String.fromCharCode(a>>8&255)+String.fromCharCode(a&255)};b.ByteBuffer.prototype.putInt24=function(a){this.data+=String.fromCharCode(a>>16&255)+String.fromCharCode(a>>8&255)+String.fromCharCode(a&255)};b.ByteBuffer.prototype.putInt32=function(a){this.data+=String.fromCharCode(a>>24&255)+String.fromCharCode(a>>16&255)+String.fromCharCode(a>>8&255)+String.fromCharCode(a&255)};b.ByteBuffer.prototype.putInt16Le=function(a){this.data+=String.fromCharCode(a&255)+String.fromCharCode(a>>8&255)};b.ByteBuffer.prototype.putInt24Le=function(a){this.data+=String.fromCharCode(a&255)+String.fromCharCode(a>>8&255)+String.fromCharCode(a>>16&255)};b.ByteBuffer.prototype.putInt32Le=function(a){this.data+=String.fromCharCode(a&255)+String.fromCharCode(a>>8&255)+String.fromCharCode(a>>16&255)+String.fromCharCode(a>>24&255)};b.ByteBuffer.prototype.putInt=function(a,c){do c-=8,this.data+=String.fromCharCode(a>>c&255);while(0<c)};b.ByteBuffer.prototype.putBuffer=function(a){this.data+=a.getBytes()};b.ByteBuffer.prototype.getByte=function(){return this.data.charCodeAt(this.read++)};b.ByteBuffer.prototype.getInt16=function(){var a=this.data.charCodeAt(this.read)<<8^this.data.charCodeAt(this.read+1);this.read+=2;return a};b.ByteBuffer.prototype.getInt24=function(){var a=this.data.charCodeAt(this.read)<<16^this.data.charCodeAt(this.read+1)<<8^this.data.charCodeAt(this.read+2);this.read+=3;return a};b.ByteBuffer.prototype.getInt32=function(){var a=this.data.charCodeAt(this.read)<<24^this.data.charCodeAt(this.read+1)<<16^this.data.charCodeAt(this.read+2)<<8^this.data.charCodeAt(this.read+3);this.read+=4;return a<<32>>>32};b.ByteBuffer.prototype.getInt16Le=function(){var a=this.data.charCodeAt(this.read)^this.data.charCodeAt(this.read+1)<<8;this.read+=2;return a};b.ByteBuffer.prototype.getInt24Le=function(){var a=this.data.charCodeAt(this.read)^this.data.charCodeAt(this.read+1)<<8^this.data.charCodeAt(this.read+2)<<16;this.read+=3;return a};b.ByteBuffer.prototype.getInt32Le=function(){var a=this.data.charCodeAt(this.read)^this.data.charCodeAt(this.read+1)<<8^this.data.charCodeAt(this.read+2)<<16^this.data.charCodeAt(this.read+3)<<24;this.read+=4;return a};b.ByteBuffer.prototype.getInt=function(a){var c=0;do c=(c<<a)+this.data.charCodeAt(this.read++),a-=8;while(0<a);return c};b.ByteBuffer.prototype.getBytes=function(a){if(a){a=Math.min(this.length(),a);var c=this.data.slice(this.read,this.read+a);this.read+=a}else 0===a?c="":(c=0===this.read?this.data:this.data.slice(this.read),this.clear());return c};b.ByteBuffer.prototype.bytes=function(a){return"undefined"===typeof a?this.data.slice(this.read):this.data.slice(this.read,this.read+a)};b.ByteBuffer.prototype.at=function(a){return this.data.charCodeAt(this.read+a)};b.ByteBuffer.prototype.setAt=function(a,c){this.data=this.data.substr(0,this.read+a)+String.fromCharCode(c)+this.data.substr(this.read+a+1)};b.ByteBuffer.prototype.last=function(){return this.data.charCodeAt(this.data.length-1)};b.ByteBuffer.prototype.copy=function(){var a=b.createBuffer(this.data);a.read=this.read;return a};b.ByteBuffer.prototype.compact=function(){0<this.read&&(this.data=this.data.slice(this.read),this.read=0)};b.ByteBuffer.prototype.clear=function(){this.data="";this.read=0};b.ByteBuffer.prototype.truncate=function(a){a=Math.max(0,this.length()-a);this.data=this.data.substr(this.read,a);this.read=0};b.ByteBuffer.prototype.toHex=function(){for(var a="",c=this.read;c<this.data.length;++c){var d=this.data.charCodeAt(c);16>d&&(a+="0");a+=d.toString(16)}return a};b.ByteBuffer.prototype.toBinaryString=function(){for(var a="",c=this.read;c<this.data.length;++c){var d=this.data.charCodeAt(c);16>d&&(a+="0000");a+=d.toString(2)}return a};b.ByteBuffer.prototype.toSubHex=function(a){for(var c="",d=this.read;d<this.read+a;++d){var b=this.data.charCodeAt(d);16>b&&(c+="0");c+=b.toString(16)}return c};b.ByteBuffer.prototype.toString=function(){return b.decodeUtf8(this.bytes())};b.getAddMod32=function(a,c){return a+c&4294967295};b.createBuffer=function(a,c){void 0!==a&&"utf8"===(c||"raw")&&(a=b.encodeUtf8(a));return new b.ByteBuffer(a)};b.fillString=function(a,c){for(var d="";0<c;)c&1&&(d+=a),c>>>=1,0<c&&(a+=a);return d};b.xorBytes=function(a,c,d){for(var b="",g,e="",h=0,l=0;0<d;--d,++h)g=a.charCodeAt(h)^c.charCodeAt(h),10<=l&&(b+=e,e="",l=0),e+=String.fromCharCode(g),++l;return b+e};b.hexToBytes=function(a){var c="";a.length&1&&(a="0"+a);for(var b=0;b<a.length;b+=2)c+=String.fromCharCode(parseInt(a.substr(b,2),16));return c};b.bytesToHex=function(a){return b.createBuffer(a).toHex()};b.bytesToBinaryString=function(a){return b.createBuffer(a).toBinaryString()};b.stringToBytes=function(a){return b.hexToBytes(b.createBuffer(a).toHex())};b.bytesToString=function(a){return b.createBuffer(a).toString()};b.int32ToBytes=function(a){return String.fromCharCode(a>>24&255)+String.fromCharCode(a>>16&255)+String.fromCharCode(a>>8&255)+String.fromCharCode(a&255)};var q=[62,-1,-1,-1,63,52,53,54,55,56,57,58,59,60,61,-1,-1,-1,64,-1,-1,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,-1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51];b.encode64=function(a,c){for(var b="",f="",g,e,h,l=0;l<a.length;)g=a.charCodeAt(l++),e=a.charCodeAt(l++),h=a.charCodeAt(l++),b+="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(g>>2),b+="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt((g&3)<<4|e>>4),isNaN(e)?b+="==":(b+="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt((e&15)<<2|h>>6),b+=isNaN(h)?"=":"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".charAt(h&63)),c&&b.length>c&&(f+=b.substr(0,c)+"\r\n",b=b.substr(c));return f+b};b.decode64=function(a){if(null==a||"undefined"==typeof a)throw{code:"119005",message:l["119005"]};a=a.replace(/[^A-Za-z0-9\+\/=]/g,"");for(var c="",b,f,g,e,h=0;h<a.length;)b=q[a.charCodeAt(h++)-43],f=q[a.charCodeAt(h++)-43],g=q[a.charCodeAt(h++)-43],e=q[a.charCodeAt(h++)-43],c+=String.fromCharCode(b<<2|f>>4),64!==g&&(c+=String.fromCharCode((f&15)<<4|g>>2),64!==e&&(c+=String.fromCharCode((g&3)<<6|e)));return c};b.encodeUtf8=function(a){return unescape(encodeURIComponent(a))};b.decodeUtf8=function(a){return decodeURIComponent(escape(a))};b.deflate=function(a,c,d){c=b.decode64(a.deflate(b.encode64(c)).rval);d&&(a=2,c.charCodeAt(1)&32&&(a=6),c=c.substring(a,c.length-4));return c};b.inflate=function(a,c,d){a=a.inflate(b.encode64(c)).rval;return null===a?null:b.decode64(a)};b.trim=function(a){return a.replace(/^\s*|\s*$/g,"")};var m=function(a,c,d){if(!a)throw{code:"119001",message:l["119001"]};null===d?a=a.removeItem(c):(d=b.encode64(JSON.stringify(d)),a=a.setItem(c,d));if("undefined"!==typeof a&&!0!==a.rval)throw{code:"119002",message:l["119002"]+a.error};},u=function(a,c){if(!a)throw{code:"119003",message:l["119003"]};c=a.getItem(c);if(a.init)if(null===c.rval){if(c.error)throw c.error;c=null}else c=c.rval;null!==c&&(c=JSON.parse(b.decode64(c)));return c},n=function(a,c,b,f){var d=u(a,c);null===d&&(d={});d[b]=f;m(a,c,d)},p=function(a,c,b){a=u(a,c);null!==a&&(a=b in a?a[b]:null);return a},t=function(a,c,b){var d=u(a,c);if(null!==d&&b in d){delete d[b];b=!0;for(var g in d){b=!1;break}b&&(d=null);m(a,c,d)}},w=function(a,c){m(a,c,null)},r=function(a,c,b){var d=null;"undefined"===typeof b&&(b=["web","flash"]);var g=!1,e=null,h;for(h in b){var k=b[h];try{if("flash"===k||"both"===k){if(null===c[0])throw{code:"119004",message:l["119004"]};d=a.apply(this,c);g="flash"===k}if("web"===k||"both"===k)c[0]=localStorage,d=a.apply(this,c),g=!0}catch(x){e=x}if(g)break}if(!g)throw e;return d};b.setItem=function(a,c,b,f,g){r(n,arguments,g)};b.getItem=function(a,c,b,f){return r(p,arguments,f)};b.removeItem=function(a,c,b,f){r(t,arguments,f)};b.clearItems=function(a,c,b){r(w,arguments,b)};b.parseUrl=function(a){var c=/^(https?):\/\/([^:&^\/]*):?(\d*)(.*)$/g;c.lastIndex=0;c=c.exec(a);if(a=null===c?null:{full:a,scheme:c[1],host:c[2],port:c[3],path:c[4]})a.fullHost=a.host,a.port?80!==a.port&&"http"===a.scheme?a.fullHost+=":"+a.port:443!==a.port&&"https"===a.scheme&&(a.fullHost+=":"+a.port):"http"===a.scheme?a.port=80:"https"===a.scheme&&(a.port=443),a.full=a.scheme+"://"+a.fullHost;return a};var v=null;b.getQueryVariables=function(a){var c=function(a){var c={};a=a.split("&");for(var b=0;b<a.length;b++){var d=a[b].indexOf("=");if(0<d){var h=a[b].substring(0,d);d=a[b].substring(d+1)}else h=a[b],d=null;h in c||(c[h]=[]);null!==d&&c[h].push(unescape(d))}return c};"undefined"===typeof a?(null===v&&(v="undefined"===typeof window?{}:c(window.location.search.substring(1))),a=v):a=c(a);return a};b.parseFragment=function(a){var c=a,d="",f=a.indexOf("?");0<f&&(c=a.substring(0,f),d=a.substring(f+1));a=c.split("/");0<a.length&&""==a[0]&&a.shift();f=""==d?{}:b.getQueryVariables(d);return{pathString:c,queryString:d,path:a,query:f}};b.makeRequest=function(a){var c=b.parseFragment(a),d={path:c.pathString,query:c.queryString,getPath:function(a){return"undefined"===typeof a?c.path:c.path[a]},getQuery:function(a,b){"undefined"===typeof a?a=c.query:(a=c.query[a])&&"undefined"!==typeof b&&(a=a[b]);return a},getQueryLast:function(a,b){return(a=d.getQuery(a))?a[a.length-1]:b}};return d};b.makeLink=function(a,b,d){a=jQuery.isArray(a)?a.join("/"):a;b=jQuery.param(b||{});d=d||"";return a+(0<b.length?"?"+b:"")+(0<d.length?"#"+d:"")};b.setPath=function(a,b,d){if("object"===typeof a&&null!==a)for(var c=0,g=b.length;c<g;){var e=b[c++];if(c==g)a[e]=d;else{var h=e in a;if(!h||h&&"object"!==typeof a[e]||h&&null===a[e])a[e]={};a=a[e]}}};b.getPath=function(a,b,d){for(var c=0,g=b.length,e=!0;e&&c<g&&"object"===typeof a&&null!==a;){var h=b[c++];(e=h in a)&&(a=a[h])}return e?a:d};b.deletePath=function(a,b){if("object"===typeof a&&null!==a)for(var c=0,f=b.length;c<f;){var g=b[c++];if(c==f)delete a[g];else{if(!(g in a)||"object"!==typeof a[g]||null===a[g])break;a=a[g]}}};b.isEmpty=function(a){for(var b in a)if(a.hasOwnProperty(b))return!1;return!0};b.format=function(a){var b=/%./g,d,f,g=0,e=[];for(f=0;d=b.exec(a);)switch(f=a.substring(f,b.lastIndex-2),0<f.length&&e.push(f),f=b.lastIndex,d=d[0][1],d){case "s":case "o":g<arguments.length?e.push(arguments[g++ +1]):e.push("<?>");break;case "%":e.push("%");break;default:e.push("<%"+d+"?>")}e.push(a.substring(f));return e.join("")};b.formatNumber=function(a,b,d,f){var c=isNaN(b=Math.abs(b))?2:b;b=void 0===d?",":d;f=void 0===f?".":f;d=0>a?"-":"";var e=parseInt(a=Math.abs(+a||0).toFixed(c),10)+"",h=3<e.length?e.length%3:0;return d+(h?e.substr(0,h)+f:"")+e.substr(h).replace(/(\d{3})(?=\d)/g,"$1"+f)+(c?b+Math.abs(a-e).toFixed(c).slice(2):"")};b.formatSize=function(a){return a=1073741824<=a?b.formatNumber(a/1073741824,2,".","")+" GiB":1048576<=a?b.formatNumber(a/1048576,2,".","")+" MiB":1024<=a?b.formatNumber(a/1024,0)+" KiB":b.formatNumber(a,0)+" bytes"};window.crosscert=window.crosscert;k.util=k.util;b.hexToBytes=k.util.hexToBytes;b.encode64=k.util.encode64;b.decode64=k.util.decode64;b.createBuffer=k.util.createBuffer}var t=[],n=null;"function"!==typeof define&&("object"===typeof module&&module.exports?n=function(k,b){b(require,module)}:(crosscert=window.crosscert=window.crosscert||{},p(crosscert)));(n||"function"===typeof define)&&(n||define)(["require","module"].concat(t),function(k,b){b.exports=function(b){var l=t.map(function(b){return k(b)}).concat(p);b=b||{};b.defined=b.defined||{};if(b.defined.util)return b.util;b.defined.util=!0;for(var m=0;m<l.length;++m)l[m](b);return b.util}})})();