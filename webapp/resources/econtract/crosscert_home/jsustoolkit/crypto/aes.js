(function(){function y(g){var q=g.jsustoolkitErrCode=g.jsustoolkitErrCode||{},B=g.aes=g.aes||{};g.cipher=g.cipher||{};g.cipher.algorithms=g.cipher.algorithms||{};g.cipher.aes=g.cipher.algorithms.aes=B;var C=!1,m,D,v,x,u,y=function(){C=!0;v=[0,1,2,4,8,16,32,64,128,27,54];for(var a=Array(256),c=0;128>c;++c)a[c]=c<<1,a[c+128]=c+128<<1^283;m=Array(256);D=Array(256);x=Array(4);u=Array(4);for(c=0;4>c;++c)x[c]=Array(256),u[c]=Array(256);var b=0,e=0;for(c=0;256>c;++c){var d=e^e<<1^e<<2^e<<3^e<<4;d=d>>8^d&255^99;m[b]=d;D[d]=b;var f=a[d];var p=a[b];var h=a[p];var n=a[h];f^=f<<24^d<<16^d<<8^d;h=(p^h^n)<<24^(b^n)<<16^(b^h^n)<<8^b^p^n;for(var k=0;4>k;++k)x[k][b]=f,u[k][d]=h,f=f<<24|f>>>8,h=h<<24|h>>>8;0===b?b=e=1:(b=p^a[a[a[p^n]]],e^=a[a[e]])}},z=function(a,c){a=a.slice(0);for(var b,e=1,d=a.length,f=4*(d+6+1),p=d;p<f;++p)b=a[p-1],0===p%d?(b=m[b>>>16&255]<<24^m[b>>>8&255]<<16^m[b&255]<<8^m[b>>>24]^v[e]<<24,e++):6<d&&4==p%d&&(b=m[b>>>24]<<24^m[b>>>16&255]<<16^m[b>>>8&255]<<8^m[b&255]),a[p]=a[p-d]^b;if(c){b=u[0];e=u[1];d=u[2];var h=u[3],n=a.slice(0);f=a.length;p=0;for(var k=f-4;p<f;p+=4,k-=4)if(0===p||p===f-4)n[p]=a[k],n[p+1]=a[k+3],n[p+2]=a[k+2],n[p+3]=a[k+1];else for(var g=0;4>g;++g)c=a[k+g],n[p+(3&-g)]=b[m[c>>>24]]^e[m[c>>>16&255]]^d[m[c>>>8&255]]^h[m[c&255]];a=n}return a},F=function(a,c,b,e){var d=a.length/4-1;if(e){var f=u[0];var g=u[1];var h=u[2];var n=u[3];var k=D}else f=x[0],g=x[1],h=x[2],n=x[3],k=m;var l=c[0]^a[0];var r=c[e?3:1]^a[1];var t=c[2]^a[2];c=c[e?1:3]^a[3];for(var w=3,E=1;E<d;++E){var G=f[l>>>24]^g[r>>>16&255]^h[t>>>8&255]^n[c&255]^a[++w];var q=f[r>>>24]^g[t>>>16&255]^h[c>>>8&255]^n[l&255]^a[++w];var B=f[t>>>24]^g[c>>>16&255]^h[l>>>8&255]^n[r&255]^a[++w];c=f[c>>>24]^g[l>>>16&255]^h[r>>>8&255]^n[t&255]^a[++w];l=G;r=q;t=B}b[0]=k[l>>>24]<<24^k[r>>>16&255]<<16^k[t>>>8&255]<<8^k[c&255]^a[++w];b[e?3:1]=k[r>>>24]<<24^k[t>>>16&255]<<16^k[c>>>8&255]<<8^k[l&255]^a[++w];b[2]=k[t>>>24]<<24^k[c>>>16&255]<<16^k[l>>>8&255]<<8^k[r&255]^a[++w];b[e?1:3]=k[c>>>24]<<24^k[l>>>16&255]<<16^k[r>>>8&255]<<8^k[t&255]^a[++w]},A=function(a,c){var b=null;if(null==a||"undefined"==typeof a)throw{code:"100011",message:q["100011"]};C||y();if(a.constructor==String&&(16==a.length||32==a.length))a=g.util.createBuffer(a);else if(a.constructor==Array&&(16==a.length||32==a.length)){var e=a;a=g.util.createBuffer();for(var d=0;d<e.length;++d)a.putByte(e[d])}else if(a.constructor==String||16!=a.length()&&32!=a.length())throw{code:"100012",message:q["100012"]};if(a.constructor!=Array){e=a;a=[];var f=e.length();if(16==f||32==f)for(f>>>=2,d=0;d<f;++d)a.push(e.getInt32())}if(a.constructor==Array&&(4==a.length||8==a.length)){var l=z(a,c),h,n,k,m,r,t;b={output:null,start:function(a,c){if(null==a||"undefined"==typeof a)throw{code:"100015",message:q["100015"]};if(16!=a.length)throw{code:"100016",message:q["100016"]};if(a.constructor==String&&16==a.length)a=g.util.createBuffer(a);else if(a.constructor==Array&&16==a.length){var d=a;a=g.util.createBuffer();for(var e=0;16>e;++e)a.putByte(d[e])}a.constructor!=Array&&(d=a,a=Array(4),a[0]=d.getInt32(),a[1]=d.getInt32(),a[2]=d.getInt32(),a[3]=d.getInt32());h=g.util.createBuffer();n=c||g.util.createBuffer();r=a.slice(0);k=Array(4);m=Array(4);t=!1;b.output=n},update:function(a){if(null==h&&null==n)throw{code:"100014",message:q["100014"]};null!=a&&a.constructor==String&&(a=g.util.createBuffer(a));if(!t){if(null==a||"undefined"==typeof a)throw{code:"100013",message:q["100013"]};h.putBuffer(a)}for(a=c&&!t?32:16;h.length()>=a;){if(c)for(var b=0;4>b;++b)k[b]=h.getInt32();else for(b=0;4>b;++b)k[b]=r[b]^h.getInt32();F(l,k,m,c);if(c){for(b=0;4>b;++b)n.putInt32(r[b]^m[b]);r=k.slice(0)}else{for(b=0;4>b;++b)n.putInt32(m[b]);r=m}}},finish:function(a){var d=!0;if(!c)if(a)d=a(c,16,h);else{var e=16==h.length()?16:16-h.length();h.fillWithByte(e,e)}d&&(t=!0,b.update());if(c)if(d=0===h.length())a?d=a(c,16,n):(a=n.length(),a=n.at(a-1),16<a?d=!1:n.truncate(a));else throw{code:"100017",message:q["100017"]};return d},tmonetpadding:function(a,b,c){if(a){a=c.length();var d=0,e;for(e=1;e<b+1;e++){var f=c.at(a-e);if(128==f){d++;break}else if(0==f)d++;else break}c.truncate(d);return!0}}}}return b};g.aes.startEncrypting=function(a,c,b){a=A(a,!1);a.start(c,b);return a};g.aes.createEncryptionCipher=function(a){return A(a,!1)};g.aes.startDecrypting=function(a,c,b){a=A(a,!0);a.start(c,b);return a};g.aes.createDecryptionCipher=function(a){return A(a,!0)};var l={cipher:function(a,c){for(var b=c.length/4-1,e=[[],[],[],[]],d=0;16>d;d++)e[d%4][Math.floor(d/4)]=a[d];e=l.addRoundKey(e,c,0,4);for(d=1;d<b;d++)e=l.subBytes(e,4),e=l.shiftRows(e,4),e=l.mixColumns(e,4),e=l.addRoundKey(e,c,d,4);e=l.subBytes(e,4);e=l.shiftRows(e,4);e=l.addRoundKey(e,c,b,4);c=Array(16);for(d=0;16>d;d++)c[d]=e[d%4][Math.floor(d/4)];return c},keyExpansion:function(a){for(var c=a.length/4,b=c+6,e=Array(4*(b+1)),d=Array(4),f=0;f<c;f++)e[f]=[a[4*f],a[4*f+1],a[4*f+2],a[4*f+3]];for(f=c;f<4*(b+1);f++){e[f]=Array(4);for(a=0;4>a;a++)d[a]=e[f-1][a];if(0==f%c)for(d=l.subWord(l.rotWord(d)),a=0;4>a;a++)d[a]^=l.rCon[f/c][a];else 6<c&&4==f%c&&(d=l.subWord(d));for(a=0;4>a;a++)e[f][a]=e[f-c][a]^d[a]}return e},subBytes:function(a,c){for(var b=0;4>b;b++)for(var e=0;e<c;e++)a[b][e]=l.sBox[a[b][e]];return a},shiftRows:function(a,c){for(var b=Array(4),e=1;4>e;e++){for(var d=0;4>d;d++)b[d]=a[e][(d+e)%c];for(d=0;4>d;d++)a[e][d]=b[d]}return a},mixColumns:function(a,c){for(c=0;4>c;c++){for(var b=Array(4),e=Array(4),d=0;4>d;d++)b[d]=a[d][c],e[d]=a[d][c]&128?a[d][c]<<1^283:a[d][c]<<1;a[0][c]=e[0]^b[1]^e[1]^b[2]^b[3];a[1][c]=b[0]^e[1]^b[2]^e[2]^b[3];a[2][c]=b[0]^b[1]^e[2]^b[3]^e[3];a[3][c]=b[0]^e[0]^b[1]^b[2]^e[3]}return a},addRoundKey:function(a,c,b,e){for(var d=0;4>d;d++)for(var f=0;f<e;f++)a[d][f]^=c[4*b+f][d];return a},subWord:function(a){for(var c=0;4>c;c++)a[c]=l.sBox[a[c]];return a},rotWord:function(a){for(var c=a[0],b=0;3>b;b++)a[b]=a[b+1];a[3]=c;return a},sBox:[99,124,119,123,242,107,111,197,48,1,103,43,254,215,171,118,202,130,201,125,250,89,71,240,173,212,162,175,156,164,114,192,183,253,147,38,54,63,247,204,52,165,229,241,113,216,49,21,4,199,35,195,24,150,5,154,7,18,128,226,235,39,178,117,9,131,44,26,27,110,90,160,82,59,214,179,41,227,47,132,83,209,0,237,32,252,177,91,106,203,190,57,74,76,88,207,208,239,170,251,67,77,51,133,69,249,2,127,80,60,159,168,81,163,64,143,146,157,56,245,188,182,218,33,16,255,243,210,205,12,19,236,95,151,68,23,196,167,126,61,100,93,25,115,96,129,79,220,34,42,144,136,70,238,184,20,222,94,11,219,224,50,58,10,73,6,36,92,194,211,172,98,145,149,228,121,231,200,55,109,141,213,78,169,108,86,244,234,101,122,174,8,186,120,37,46,28,166,180,198,232,221,116,31,75,189,139,138,112,62,181,102,72,3,246,14,97,53,87,185,134,193,29,158,225,248,152,17,105,217,142,148,155,30,135,233,206,85,40,223,140,161,137,13,191,230,66,104,65,153,45,15,176,84,187,22],rCon:[[0,0,0,0],[1,0,0,0],[2,0,0,0],[4,0,0,0],[8,0,0,0],[16,0,0,0],[32,0,0,0],[64,0,0,0],[128,0,0,0],[27,0,0,0],[54,0,0,0]],encrypt:function(a,c,b){if(128!=b&&192!=b&&256!=b)return"";a=g.util.encodeUtf8(a);c=g.util.encodeUtf8(c);var e=b/8,d=Array(e);for(b=0;b<e;b++)d[b]=isNaN(c.charCodeAt(b))?0:c.charCodeAt(b);d=l.cipher(d,l.keyExpansion(d));d=d.concat(d.slice(0,e-16));c=Array(16);b=(new Date).getTime();e=Math.floor(b/1E3);var f=b%1E3;for(b=0;4>b;b++)c[b]=e>>>8*b&255;for(b=0;4>b;b++)c[b+4]=f&255;e="";for(b=0;8>b;b++)e+=String.fromCharCode(c[b]);d=l.keyExpansion(d);f=Math.ceil(a.length/16);for(var p=Array(f),h=0;h<f;h++){for(b=0;4>b;b++)c[15-b]=h>>>8*b&255;for(b=0;4>b;b++)c[15-b-4]=h/4294967296>>>8*b;var n=l.cipher(c,d),k=h<f-1?16:(a.length-1)%16+1,m=Array(k);for(b=0;b<k;b++)m[b]=n[b]^a.charCodeAt(16*h+b),m[b]=String.fromCharCode(m[b]);p[h]=m.join("")}a=e+p.join("");return a=g.util.encode64(a)},decrypt:function(a,c,b){if(128!=b&&192!=b&&256!=b)return"";a=g.util.decode64(a);c=g.util.encodeUtf8(c);var e=b/8,d=Array(e);for(b=0;b<e;b++)d[b]=isNaN(c.charCodeAt(b))?0:c.charCodeAt(b);d=l.cipher(d,l.keyExpansion(d));d=d.concat(d.slice(0,e-16));c=Array(8);ctrTxt=a.slice(0,8);for(b=0;8>b;b++)c[b]=ctrTxt.charCodeAt(b);e=l.keyExpansion(d);d=Math.ceil((a.length-8)/16);b=Array(d);for(var f=0;f<d;f++)b[f]=a.slice(8+16*f,16*f+24);a=b;var m=Array(a.length);for(f=0;f<d;f++){for(b=0;4>b;b++)c[15-b]=f>>>8*b&255;for(b=0;4>b;b++)c[15-b-4]=(f+1)/4294967296-1>>>8*b&255;var h=l.cipher(c,e),n=Array(a[f].length);for(b=0;b<a[f].length;b++)n[b]=h[b]^a[f].charCodeAt(b),n[b]=String.fromCharCode(n[b]);m[f]=n.join("")}a=m.join("");return a=g.util.decodeUtf8(a)}};g.aes.CTREncrypt=function(a,c,b){return l.encrypt(a,c,b)};g.aes.CTRDecrypt=function(a,c,b){return l.decrypt(a,c,b)};g.aes._expandKey=function(a,c){C||y();return z(a,c)};g.aes._updateBlock=F}var z=["./util","./jsustoolkitErrCode"],v=null;"function"!==typeof define&&("object"===typeof module&&module.exports?v=function(g,q){q(require,module)}:(crosscert=window.crosscert=window.crosscert||{},y(crosscert)));(v||"function"===typeof define)&&(v||define)(["require","module"].concat(z),function(g,q){q.exports=function(q){var v=z.map(function(m){return g(m)}).concat(y);q=q||{};q.defined=q.defined||{};if(q.defined.aes)return q.aes;q.defined.aes=!0;for(var m=0;m<v.length;++m)v[m](q);return q.aes}})})();