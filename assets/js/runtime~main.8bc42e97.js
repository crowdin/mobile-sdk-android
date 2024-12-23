(()=>{"use strict";var e,r,t,o,a,n={},f={};function i(e){var r=f[e];if(void 0!==r)return r.exports;var t=f[e]={exports:{}};return n[e].call(t.exports,t,t.exports,i),t.exports}i.m=n,e=[],i.O=(r,t,o,a)=>{if(!t){var n=1/0;for(d=0;d<e.length;d++){t=e[d][0],o=e[d][1],a=e[d][2];for(var f=!0,b=0;b<t.length;b++)(!1&a||n>=a)&&Object.keys(i.O).every((e=>i.O[e](t[b])))?t.splice(b--,1):(f=!1,a<n&&(n=a));if(f){e.splice(d--,1);var c=o();void 0!==c&&(r=c)}}return r}a=a||0;for(var d=e.length;d>0&&e[d-1][2]>a;d--)e[d]=e[d-1];e[d]=[t,o,a]},i.n=e=>{var r=e&&e.__esModule?()=>e.default:()=>e;return i.d(r,{a:r}),r},t=Object.getPrototypeOf?e=>Object.getPrototypeOf(e):e=>e.__proto__,i.t=function(e,o){if(1&o&&(e=this(e)),8&o)return e;if("object"==typeof e&&e){if(4&o&&e.__esModule)return e;if(16&o&&"function"==typeof e.then)return e}var a=Object.create(null);i.r(a);var n={};r=r||[null,t({}),t([]),t(t)];for(var f=2&o&&e;"object"==typeof f&&!~r.indexOf(f);f=t(f))Object.getOwnPropertyNames(f).forEach((r=>n[r]=()=>e[r]));return n.default=()=>e,i.d(a,n),a},i.d=(e,r)=>{for(var t in r)i.o(r,t)&&!i.o(e,t)&&Object.defineProperty(e,t,{enumerable:!0,get:r[t]})},i.f={},i.e=e=>Promise.all(Object.keys(i.f).reduce(((r,t)=>(i.f[t](e,r),r)),[])),i.u=e=>"assets/js/"+({70:"0480b142",75:"96be3f92",138:"1a4e3797",199:"617041fe",320:"b199372b",386:"57787c5a",401:"17896441",441:"d674f98f",486:"39d847f0",570:"db0371bf",581:"935f2afb",645:"17284fae",712:"3ba3edb9",714:"1be78505",719:"b66c3ab3",803:"3b8c55ea",839:"931397ec",884:"db32d859",903:"f8409a7e",987:"b5805bc7"}[e]||e)+"."+{70:"3ae43cfa",75:"babeedfc",138:"51ece164",199:"021ee045",282:"7c31d91d",320:"6f078c17",386:"59714fc1",401:"a36ac6fe",441:"a645a301",486:"daabb1b5",489:"30a81ee7",570:"9a3603dc",581:"3a6496fa",645:"93534b06",712:"8bfa9bac",714:"7504deeb",719:"9700e3e3",741:"faa348c1",774:"879e2c12",803:"a27b6686",839:"14618ce8",884:"79ce8438",903:"8a28b581",987:"f59fab59"}[e]+".js",i.miniCssF=e=>{},i.g=function(){if("object"==typeof globalThis)return globalThis;try{return this||new Function("return this")()}catch(e){if("object"==typeof window)return window}}(),i.o=(e,r)=>Object.prototype.hasOwnProperty.call(e,r),o={},a="@crowdin/mobile-sdk-android-website:",i.l=(e,r,t,n)=>{if(o[e])o[e].push(r);else{var f,b;if(void 0!==t)for(var c=document.getElementsByTagName("script"),d=0;d<c.length;d++){var l=c[d];if(l.getAttribute("src")==e||l.getAttribute("data-webpack")==a+t){f=l;break}}f||(b=!0,(f=document.createElement("script")).charset="utf-8",f.timeout=120,i.nc&&f.setAttribute("nonce",i.nc),f.setAttribute("data-webpack",a+t),f.src=e),o[e]=[r];var u=(r,t)=>{f.onerror=f.onload=null,clearTimeout(s);var a=o[e];if(delete o[e],f.parentNode&&f.parentNode.removeChild(f),a&&a.forEach((e=>e(t))),r)return r(t)},s=setTimeout(u.bind(null,void 0,{type:"timeout",target:f}),12e4);f.onerror=u.bind(null,f.onerror),f.onload=u.bind(null,f.onload),b&&document.head.appendChild(f)}},i.r=e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},i.p="/mobile-sdk-android/",i.gca=function(e){return e={17896441:"401","0480b142":"70","96be3f92":"75","1a4e3797":"138","617041fe":"199",b199372b:"320","57787c5a":"386",d674f98f:"441","39d847f0":"486",db0371bf:"570","935f2afb":"581","17284fae":"645","3ba3edb9":"712","1be78505":"714",b66c3ab3:"719","3b8c55ea":"803","931397ec":"839",db32d859:"884",f8409a7e:"903",b5805bc7:"987"}[e]||e,i.p+i.u(e)},(()=>{var e={354:0,869:0};i.f.j=(r,t)=>{var o=i.o(e,r)?e[r]:void 0;if(0!==o)if(o)t.push(o[2]);else if(/^(354|869)$/.test(r))e[r]=0;else{var a=new Promise(((t,a)=>o=e[r]=[t,a]));t.push(o[2]=a);var n=i.p+i.u(r),f=new Error;i.l(n,(t=>{if(i.o(e,r)&&(0!==(o=e[r])&&(e[r]=void 0),o)){var a=t&&("load"===t.type?"missing":t.type),n=t&&t.target&&t.target.src;f.message="Loading chunk "+r+" failed.\n("+a+": "+n+")",f.name="ChunkLoadError",f.type=a,f.request=n,o[1](f)}}),"chunk-"+r,r)}},i.O.j=r=>0===e[r];var r=(r,t)=>{var o,a,n=t[0],f=t[1],b=t[2],c=0;if(n.some((r=>0!==e[r]))){for(o in f)i.o(f,o)&&(i.m[o]=f[o]);if(b)var d=b(i)}for(r&&r(t);c<n.length;c++)a=n[c],i.o(e,a)&&e[a]&&e[a][0](),e[a]=0;return i.O(d)},t=self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[];t.forEach(r.bind(null,0)),t.push=r.bind(null,t.push.bind(t))})()})();