(()=>{"use strict";var e,t,r,a,o,n={},d={};function f(e){var t=d[e];if(void 0!==t)return t.exports;var r=d[e]={exports:{}};return n[e].call(r.exports,r,r.exports,f),r.exports}f.m=n,e=[],f.O=(t,r,a,o)=>{if(!r){var n=1/0;for(c=0;c<e.length;c++){r=e[c][0],a=e[c][1],o=e[c][2];for(var d=!0,i=0;i<r.length;i++)(!1&o||n>=o)&&Object.keys(f.O).every((e=>f.O[e](r[i])))?r.splice(i--,1):(d=!1,o<n&&(n=o));if(d){e.splice(c--,1);var b=a();void 0!==b&&(t=b)}}return t}o=o||0;for(var c=e.length;c>0&&e[c-1][2]>o;c--)e[c]=e[c-1];e[c]=[r,a,o]},f.n=e=>{var t=e&&e.__esModule?()=>e.default:()=>e;return f.d(t,{a:t}),t},r=Object.getPrototypeOf?e=>Object.getPrototypeOf(e):e=>e.__proto__,f.t=function(e,a){if(1&a&&(e=this(e)),8&a)return e;if("object"==typeof e&&e){if(4&a&&e.__esModule)return e;if(16&a&&"function"==typeof e.then)return e}var o=Object.create(null);f.r(o);var n={};t=t||[null,r({}),r([]),r(r)];for(var d=2&a&&e;"object"==typeof d&&!~t.indexOf(d);d=r(d))Object.getOwnPropertyNames(d).forEach((t=>n[t]=()=>e[t]));return n.default=()=>e,f.d(o,n),o},f.d=(e,t)=>{for(var r in t)f.o(t,r)&&!f.o(e,r)&&Object.defineProperty(e,r,{enumerable:!0,get:t[r]})},f.f={},f.e=e=>Promise.all(Object.keys(f.f).reduce(((t,r)=>(f.f[r](e,t),t)),[])),f.u=e=>"assets/js/"+({48:"a94703ab",70:"0480b142",75:"96be3f92",98:"a7bd4aaa",138:"1a4e3797",199:"617041fe",386:"57787c5a",401:"17896441",441:"d674f98f",486:"39d847f0",570:"db0371bf",645:"17284fae",647:"5e95c892",685:"773a3eff",712:"3ba3edb9",719:"b66c3ab3",742:"aba21aa0",803:"3b8c55ea",839:"931397ec",884:"db32d859",903:"f8409a7e",921:"138e0e15"}[e]||e)+"."+{42:"6fa221fa",48:"a3569cd6",70:"d8f34a78",75:"926d5a82",98:"fe5010ab",138:"9ddca047",199:"191aa130",386:"72b49a8d",401:"ad2b1e21",441:"ac529a5a",486:"a90b9f84",489:"30a81ee7",542:"77dcc5e1",570:"cb268579",645:"88d31064",647:"3231f7e9",685:"13633b45",712:"a11661ad",719:"ecb5e9d5",741:"faa348c1",742:"21176773",803:"c5180bb5",839:"61bb30fc",884:"e085660f",903:"13404e13",913:"daca3842",921:"efbe8b17"}[e]+".js",f.miniCssF=e=>{},f.g=function(){if("object"==typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"==typeof window)return window}}(),f.o=(e,t)=>Object.prototype.hasOwnProperty.call(e,t),a={},o="@crowdin/mobile-sdk-android-website:",f.l=(e,t,r,n)=>{if(a[e])a[e].push(t);else{var d,i;if(void 0!==r)for(var b=document.getElementsByTagName("script"),c=0;c<b.length;c++){var l=b[c];if(l.getAttribute("src")==e||l.getAttribute("data-webpack")==o+r){d=l;break}}d||(i=!0,(d=document.createElement("script")).charset="utf-8",d.timeout=120,f.nc&&d.setAttribute("nonce",f.nc),d.setAttribute("data-webpack",o+r),d.src=e),a[e]=[t];var u=(t,r)=>{d.onerror=d.onload=null,clearTimeout(s);var o=a[e];if(delete a[e],d.parentNode&&d.parentNode.removeChild(d),o&&o.forEach((e=>e(r))),t)return t(r)},s=setTimeout(u.bind(null,void 0,{type:"timeout",target:d}),12e4);d.onerror=u.bind(null,d.onerror),d.onload=u.bind(null,d.onload),i&&document.head.appendChild(d)}},f.r=e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},f.p="/mobile-sdk-android/",f.gca=function(e){return e={17896441:"401",a94703ab:"48","0480b142":"70","96be3f92":"75",a7bd4aaa:"98","1a4e3797":"138","617041fe":"199","57787c5a":"386",d674f98f:"441","39d847f0":"486",db0371bf:"570","17284fae":"645","5e95c892":"647","773a3eff":"685","3ba3edb9":"712",b66c3ab3:"719",aba21aa0:"742","3b8c55ea":"803","931397ec":"839",db32d859:"884",f8409a7e:"903","138e0e15":"921"}[e]||e,f.p+f.u(e)},(()=>{f.b=document.baseURI||self.location.href;var e={354:0,869:0};f.f.j=(t,r)=>{var a=f.o(e,t)?e[t]:void 0;if(0!==a)if(a)r.push(a[2]);else if(/^(354|869)$/.test(t))e[t]=0;else{var o=new Promise(((r,o)=>a=e[t]=[r,o]));r.push(a[2]=o);var n=f.p+f.u(t),d=new Error;f.l(n,(r=>{if(f.o(e,t)&&(0!==(a=e[t])&&(e[t]=void 0),a)){var o=r&&("load"===r.type?"missing":r.type),n=r&&r.target&&r.target.src;d.message="Loading chunk "+t+" failed.\n("+o+": "+n+")",d.name="ChunkLoadError",d.type=o,d.request=n,a[1](d)}}),"chunk-"+t,t)}},f.O.j=t=>0===e[t];var t=(t,r)=>{var a,o,n=r[0],d=r[1],i=r[2],b=0;if(n.some((t=>0!==e[t]))){for(a in d)f.o(d,a)&&(f.m[a]=d[a]);if(i)var c=i(f)}for(t&&t(r);b<n.length;b++)o=n[b],f.o(e,o)&&e[o]&&e[o][0](),e[o]=0;return f.O(c)},r=self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[];r.forEach(t.bind(null,0)),r.push=t.bind(null,r.push.bind(r))})()})();