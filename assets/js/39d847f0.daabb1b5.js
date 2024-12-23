"use strict";(self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[]).push([[486],{5680:(e,r,t)=>{t.d(r,{xA:()=>p,yg:()=>y});var n=t(6540);function o(e,r,t){return r in e?Object.defineProperty(e,r,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[r]=t,e}function a(e,r){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);r&&(n=n.filter((function(r){return Object.getOwnPropertyDescriptor(e,r).enumerable}))),t.push.apply(t,n)}return t}function i(e){for(var r=1;r<arguments.length;r++){var t=null!=arguments[r]?arguments[r]:{};r%2?a(Object(t),!0).forEach((function(r){o(e,r,t[r])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):a(Object(t)).forEach((function(r){Object.defineProperty(e,r,Object.getOwnPropertyDescriptor(t,r))}))}return e}function l(e,r){if(null==e)return{};var t,n,o=function(e,r){if(null==e)return{};var t,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||(o[t]=e[t]);return o}(e,r);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)t=a[n],r.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var s=n.createContext({}),d=function(e){var r=n.useContext(s),t=r;return e&&(t="function"==typeof e?e(r):i(i({},r),e)),t},p=function(e){var r=d(e.components);return n.createElement(s.Provider,{value:r},e.children)},u="mdxType",c={inlineCode:"code",wrapper:function(e){var r=e.children;return n.createElement(n.Fragment,{},r)}},m=n.forwardRef((function(e,r){var t=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),u=d(t),m=o,y=u["".concat(s,".").concat(m)]||u[m]||c[m]||a;return t?n.createElement(y,i(i({ref:r},p),{},{components:t})):n.createElement(y,i({ref:r},p))}));function y(e,r){var t=arguments,o=r&&r.mdxType;if("string"==typeof e||o){var a=t.length,i=new Array(a);i[0]=m;var l={};for(var s in r)hasOwnProperty.call(r,s)&&(l[s]=r[s]);l.originalType=e,l[u]="string"==typeof e?e:o,i[1]=l;for(var d=2;d<a;d++)i[d]=t[d];return n.createElement.apply(null,i)}return n.createElement.apply(null,t)}m.displayName="MDXCreateElement"},7412:(e,r,t)=>{t.r(r),t.d(r,{assets:()=>s,contentTitle:()=>i,default:()=>c,frontMatter:()=>a,metadata:()=>l,toc:()=>d});var n=t(8168),o=(t(6540),t(5680));const a={},i="Platforms Support",l={unversionedId:"guides/platforms-support",id:"guides/platforms-support",title:"Platforms Support",description:"Android TV",source:"@site/docs/guides/platforms-support.md",sourceDirName:"guides",slug:"/guides/platforms-support",permalink:"/mobile-sdk-android/guides/platforms-support",draft:!1,editUrl:"https://github.com/crowdin/mobile-sdk-android/tree/master/website/docs/guides/platforms-support.md",tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Multiple Flavor Apps",permalink:"/mobile-sdk-android/guides/multiple-flavor-app"},next:{title:"Synchronous Mode",permalink:"/mobile-sdk-android/guides/synchronous-mode"}},s={},d=[{value:"Android TV",id:"android-tv",level:2},{value:"Fire OS",id:"fire-os",level:2}],p={toc:d},u="wrapper";function c(e){let{components:r,...t}=e;return(0,o.yg)(u,(0,n.A)({},p,t,{components:r,mdxType:"MDXLayout"}),(0,o.yg)("h1",{id:"platforms-support"},"Platforms Support"),(0,o.yg)("h2",{id:"android-tv"},"Android TV"),(0,o.yg)("p",null,"Android TV is an Android-based smart TV operating system developed by Google for televisions, digital media players, set-top boxes, and sound bars."),(0,o.yg)("p",null,"Crowdin SDK is compatible with Android TV. You can use Over-The-Air, Screenshots, Real-Time Preview features for your Android TV application in the same way as for regular Android apps."),(0,o.yg)("h2",{id:"fire-os"},"Fire OS"),(0,o.yg)("p",null,"Fire OS is a mobile operating system based on the Android Open Source Project and created by Amazon for its Fire tablets, Echo smart speakers, and Fire TV devices."),(0,o.yg)("table",null,(0,o.yg)("thead",{parentName:"table"},(0,o.yg)("tr",{parentName:"thead"},(0,o.yg)("th",{parentName:"tr",align:null},"Fire OS Version"),(0,o.yg)("th",{parentName:"tr",align:null},"Android Version"))),(0,o.yg)("tbody",{parentName:"table"},(0,o.yg)("tr",{parentName:"tbody"},(0,o.yg)("td",{parentName:"tr",align:null},"Fire OS 5"),(0,o.yg)("td",{parentName:"tr",align:null},"Based on Android 5.1 (Lollipop, API level 22)")),(0,o.yg)("tr",{parentName:"tbody"},(0,o.yg)("td",{parentName:"tr",align:null},"Fire OS 6"),(0,o.yg)("td",{parentName:"tr",align:null},"Based on Android 7.1 (Nougat, API level 25)")),(0,o.yg)("tr",{parentName:"tbody"},(0,o.yg)("td",{parentName:"tr",align:null},"Fire OS 7"),(0,o.yg)("td",{parentName:"tr",align:null},"Based on Android 9 (Pie, API level 28)")))),(0,o.yg)("p",null,"Because both Amazon Fire TV and Android TV use Android, you can publish the same Android app to both the Amazon Appstore and the Google Play Store."),(0,o.yg)("p",null,"When you test your Amazon Fire TV app code, you use a real Fire TV device (either the set-top box or stick) instead of a virtual emulator. See Connecting to Fire TV via ADB for more details."),(0,o.yg)("p",null,"The ",(0,o.yg)("strong",{parentName:"p"},"Crowdin SDK is compatible with Fire OS"),". You can use Over-The-Air, Screenshots, and Real-Time Preview features."),(0,o.yg)("admonition",{type:"caution"},(0,o.yg)("p",{parentName:"admonition"},"Some issues are observed with the ",(0,o.yg)("inlineCode",{parentName:"p"},"initCrowdinControl")," and overlay permission. In case you have such issues, please disable SDK Controls.")))}c.isMDXComponent=!0}}]);