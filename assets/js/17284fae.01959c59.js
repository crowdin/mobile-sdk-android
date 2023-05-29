"use strict";(self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[]).push([[402],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>h});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var p=r.createContext({}),s=function(e){var t=r.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},c=function(e){var t=s(e.components);return r.createElement(p.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,p=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),d=s(n),m=o,h=d["".concat(p,".").concat(m)]||d[m]||u[m]||i;return n?r.createElement(h,a(a({ref:t},c),{},{components:n})):r.createElement(h,a({ref:t},c))}));function h(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=m;var l={};for(var p in t)hasOwnProperty.call(t,p)&&(l[p]=t[p]);l.originalType=e,l[d]="string"==typeof e?e:o,a[1]=l;for(var s=2;s<i;s++)a[s]=n[s];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5555:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>a,default:()=>u,frontMatter:()=>i,metadata:()=>l,toc:()=>s});var r=n(7462),o=(n(7294),n(3905));const i={},a="Example project",l={unversionedId:"example",id:"example",title:"Example project",description:"Crowdin Android SDK Example is a simple todo app designed to illustrate how you can use Crowdin SDK features with a real Android app. This app's primary purpose is to show the Crowdin SDK integration process in action and test the possibilities it provides.",source:"@site/docs/example.md",sourceDirName:".",slug:"/example",permalink:"/mobile-sdk-android/example",draft:!1,editUrl:"https://github.com/crowdin/mobile-sdk-android/tree/master/website/docs/example.md",tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Synchronous Mode",permalink:"/mobile-sdk-android/guides/synchronous-mode"},next:{title:"Security",permalink:"/mobile-sdk-android/security"}},p={},s=[{value:"App Overview",id:"app-overview",level:2},{value:"Connecting Crowdin project with Crowdin Example app",id:"connecting-crowdin-project-with-crowdin-example-app",level:2},{value:"In-app language changes",id:"in-app-language-changes",level:2},{value:"Multi-module support",id:"multi-module-support",level:2},{value:"SDK Controls",id:"sdk-controls",level:2}],c={toc:s},d="wrapper";function u(e){let{components:t,...n}=e;return(0,o.kt)(d,(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"example-project"},"Example project"),(0,o.kt)("p",null,"Crowdin ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/crowdin/mobile-sdk-android/tree/master/example"},"Android SDK Example")," is a simple todo app designed to illustrate how you can use Crowdin SDK features with a real Android app. This app's primary purpose is to show the Crowdin SDK integration process in action and test the possibilities it provides."),(0,o.kt)("h2",{id:"app-overview"},"App Overview"),(0,o.kt)("p",null,"In the Crowdin Example app, you can create a simple task, add a specific category for it, set the date and time, mark the task as read and delete it.\nAdditionally, you can create new categories and review the history of the completed tasks."),(0,o.kt)("h2",{id:"connecting-crowdin-project-with-crowdin-example-app"},"Connecting Crowdin project with Crowdin Example app"),(0,o.kt)("p",null,"To connect the project with your Crowdin account and test the content delivery as well as other features, follow these steps:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"Clone the current repository"),(0,o.kt)("li",{parentName:"ul"},"Crowdin project setup:",(0,o.kt)("ul",{parentName:"li"},(0,o.kt)("li",{parentName:"ul"},"Add the resources (",(0,o.kt)("inlineCode",{parentName:"li"},"res/values/strings.xml")," files) from the ",(0,o.kt)("strong",{parentName:"li"},"example / example-info")," modules to your Crowdin project. If you\u2019d like to use files from the different modules, check out the ",(0,o.kt)("a",{parentName:"li",href:"https://put-the-correct-link-here.com"},"instructions")),(0,o.kt)("li",{parentName:"ul"},"Translate the resources"),(0,o.kt)("li",{parentName:"ul"},"Create a distribution"))),(0,o.kt)("li",{parentName:"ul"},"App setup:",(0,o.kt)("ul",{parentName:"li"},(0,o.kt)("li",{parentName:"ul"},"Navigate to the ",(0,o.kt)("inlineCode",{parentName:"li"},"App.kt")," class and paste in your ",(0,o.kt)("inlineCode",{parentName:"li"},"distribution_hash")," obtained in Crowdin, enable the other required options for your test case.")))),(0,o.kt)("h2",{id:"in-app-language-changes"},"In-app language changes"),(0,o.kt)("p",null,"On the Settings page, you can switch the UI language used by the Crowdin Example app. When you change the language, Crowdin SDK fetches the latest translations from the Crowdin project and stores them in the local repository."),(0,o.kt)("h2",{id:"multi-module-support"},"Multi-module support"),(0,o.kt)("p",null,"The app consists of the following modules:"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("strong",{parentName:"li"},"example")," - the main app classes"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("strong",{parentName:"li"},"example-info")," - for simplicity, this module contains only one UI screen - ",(0,o.kt)("inlineCode",{parentName:"li"},"InfoActivity.kt")," that has its own string resources")),(0,o.kt)("p",null,"You can navigate to this screen by clicking on the ",(0,o.kt)("inlineCode",{parentName:"p"},"Info")," item using the main screen menu."),(0,o.kt)("h2",{id:"sdk-controls"},"SDK Controls"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"/advanced-features/sdk-controls"},"SDK Controls")," is an overlay widget designed to facilitate control of the Crowdin Android SDK. By default, this component is initialized in the ",(0,o.kt)("inlineCode",{parentName:"p"},"MainActivity.kt"),"."))}u.isMDXComponent=!0}}]);