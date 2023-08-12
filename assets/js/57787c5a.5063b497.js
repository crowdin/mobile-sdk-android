"use strict";(self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[]).push([[117],{5162:(e,t,a)=>{a.d(t,{Z:()=>l});var n=a(7294),r=a(6010);const o={tabItem:"tabItem_Ymn6"};function l(e){let{children:t,hidden:a,className:l}=e;return n.createElement("div",{role:"tabpanel",className:(0,r.Z)(o.tabItem,l),hidden:a},t)}},4866:(e,t,a)=>{a.d(t,{Z:()=>w});var n=a(7462),r=a(7294),o=a(6010),l=a(2466),i=a(6550),s=a(1980),u=a(7392),d=a(12);function c(e){return function(e){return r.Children.map(e,(e=>{if(!e||(0,r.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}(e).map((e=>{let{props:{value:t,label:a,attributes:n,default:r}}=e;return{value:t,label:a,attributes:n,default:r}}))}function p(e){const{values:t,children:a}=e;return(0,r.useMemo)((()=>{const e=t??c(a);return function(e){const t=(0,u.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,a])}function m(e){let{value:t,tabValues:a}=e;return a.some((e=>e.value===t))}function h(e){let{queryString:t=!1,groupId:a}=e;const n=(0,i.k6)(),o=function(e){let{queryString:t=!1,groupId:a}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return a??null}({queryString:t,groupId:a});return[(0,s._X)(o),(0,r.useCallback)((e=>{if(!o)return;const t=new URLSearchParams(n.location.search);t.set(o,e),n.replace({...n.location,search:t.toString()})}),[o,n])]}function b(e){const{defaultValue:t,queryString:a=!1,groupId:n}=e,o=p(e),[l,i]=(0,r.useState)((()=>function(e){let{defaultValue:t,tabValues:a}=e;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!m({value:t,tabValues:a}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${a.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const n=a.find((e=>e.default))??a[0];if(!n)throw new Error("Unexpected error: 0 tabValues");return n.value}({defaultValue:t,tabValues:o}))),[s,u]=h({queryString:a,groupId:n}),[c,b]=function(e){let{groupId:t}=e;const a=function(e){return e?`docusaurus.tab.${e}`:null}(t),[n,o]=(0,d.Nk)(a);return[n,(0,r.useCallback)((e=>{a&&o.set(e)}),[a,o])]}({groupId:n}),v=(()=>{const e=s??c;return m({value:e,tabValues:o})?e:null})();(0,r.useLayoutEffect)((()=>{v&&i(v)}),[v]);return{selectedValue:l,selectValue:(0,r.useCallback)((e=>{if(!m({value:e,tabValues:o}))throw new Error(`Can't select invalid tab value=${e}`);i(e),u(e),b(e)}),[u,b,o]),tabValues:o}}var v=a(2389);const k={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function f(e){let{className:t,block:a,selectedValue:i,selectValue:s,tabValues:u}=e;const d=[],{blockElementScrollPositionUntilNextRender:c}=(0,l.o5)(),p=e=>{const t=e.currentTarget,a=d.indexOf(t),n=u[a].value;n!==i&&(c(t),s(n))},m=e=>{let t=null;switch(e.key){case"Enter":p(e);break;case"ArrowRight":{const a=d.indexOf(e.currentTarget)+1;t=d[a]??d[0];break}case"ArrowLeft":{const a=d.indexOf(e.currentTarget)-1;t=d[a]??d[d.length-1];break}}t?.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.Z)("tabs",{"tabs--block":a},t)},u.map((e=>{let{value:t,label:a,attributes:l}=e;return r.createElement("li",(0,n.Z)({role:"tab",tabIndex:i===t?0:-1,"aria-selected":i===t,key:t,ref:e=>d.push(e),onKeyDown:m,onClick:p},l,{className:(0,o.Z)("tabs__item",k.tabItem,l?.className,{"tabs__item--active":i===t})}),a??t)})))}function g(e){let{lazy:t,children:a,selectedValue:n}=e;const o=(Array.isArray(a)?a:[a]).filter(Boolean);if(t){const e=o.find((e=>e.props.value===n));return e?(0,r.cloneElement)(e,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},o.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==n}))))}function y(e){const t=b(e);return r.createElement("div",{className:(0,o.Z)("tabs-container",k.tabList)},r.createElement(f,(0,n.Z)({},e,t)),r.createElement(g,(0,n.Z)({},e,t)))}function w(e){const t=(0,v.Z)();return r.createElement(y,(0,n.Z)({key:String(t)},e))}},6540:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>k,contentTitle:()=>b,default:()=>w,frontMatter:()=>h,metadata:()=>v,toc:()=>f});var n=a(7462),r=(a(7294),a(3905)),o=a(4866),l=a(5162),i=a(614);const s="override fun onCreate(savedInstanceState: Bundle?) {\n    super.onCreate(savedInstanceState)\n    initCrowdinControl(this)\n}",u="@Override\nprotected void onCreate(Bundle savedInstanceState) {\n    super.onCreate(savedInstanceState);\n    CrowdinControlUtil.initCrowdinControl(this);\n}",d="override fun onDestroy() {\n    super.onCreate(savedInstanceState)\n    destroyCrowdinControl(this)\n}",c="@Override\nprotected void onDestroy() {\n    super.onDestroy();\n    CrowdinControlUtil.destroyCrowdinControl(this);\n}",p="override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {\n     super.onActivityResult(requestCode, resultCode, data)\n     onActivityResult(this, requestCode)\n}",m="@Override\nprotected void onActivityResult(int requestCode, int resultCode, Intent data) {\n    super.onActivityResult(requestCode, resultCode, data);\n    CrowdinControlUtil.onActivityResult(this, requestCode);\n}",h={},b="SDK Controls",v={unversionedId:"advanced-features/sdk-controls",id:"advanced-features/sdk-controls",title:"SDK Controls",description:"Crowdin SDK Controls - UI widget for easy access to the main features of Crowdin SDK. The component can be dragged and dropped anywhere on the device screen and has two states:",source:"@site/docs/advanced-features/sdk-controls.mdx",sourceDirName:"advanced-features",slug:"/advanced-features/sdk-controls",permalink:"/mobile-sdk-android/advanced-features/sdk-controls",draft:!1,editUrl:"https://github.com/crowdin/mobile-sdk-android/tree/master/website/docs/advanced-features/sdk-controls.mdx",tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Screenshots",permalink:"/mobile-sdk-android/advanced-features/screenshots"},next:{title:"Multiple Flavor Apps",permalink:"/mobile-sdk-android/guides/multiple-flavor-app"}},k={},f=[{value:"Installation",id:"installation",level:3},{value:"Initialization",id:"initialization",level:3}],g={toc:f},y="wrapper";function w(e){let{components:t,...h}=e;return(0,r.kt)(y,(0,n.Z)({},g,h,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"sdk-controls"},"SDK Controls"),(0,r.kt)("p",null,"Crowdin SDK Controls - UI widget for easy access to the main features of Crowdin SDK. The component can be dragged and dropped anywhere on the device screen and has two states:"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Collapsed (simple icon)"),(0,r.kt)("th",{parentName:"tr",align:null},"Expanded"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("img",{alt:"SDK Controls collapsed",src:a(3963).Z,width:"1080",height:"2160"})),(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("img",{alt:"SDK Controls expanded",src:a(8025).Z,width:"1080",height:"2160"}))))),(0,r.kt)("p",null,"The expanded state provides the following action buttons:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"Log In/Out: launch authorization flow or clear user authorization data on logout."),(0,r.kt)("li",{parentName:"ul"},"Real-time preview Enable/Disable: open/close connection that is required to work with Crowdin editor."),(0,r.kt)("li",{parentName:"ul"},"Capture screenshots: capture a screenshot of the current screen with all tags and upload it to Crowdin."),(0,r.kt)("li",{parentName:"ul"},"Reload Translations:",(0,r.kt)("ul",{parentName:"li"},(0,r.kt)("li",{parentName:"ul"},"Real-Time-Preview ON: it will fetch the latest translations from Crowdin and apply changes to UI."),(0,r.kt)("li",{parentName:"ul"},"Real-Time-Preview OFF: it will fetch the content from distribution (it won't update UI instantly. Translations will be updated on the next application load or Activity change).")))),(0,r.kt)("h3",{id:"installation"},"Installation"),(0,r.kt)("p",null,"Add it to your root ",(0,r.kt)("inlineCode",{parentName:"p"},"build.gradle")," at the end of repositories:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-groovy"},"allprojects {\n    repositories {\n        ...\n        maven { url 'https://jitpack.io' }\n    }\n}\n")),(0,r.kt)("p",null,"Add the dependency:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-groovy"},"dependencies {\n    implementation 'com.github.crowdin.mobile-sdk-android:controls:1.4.3'\n}\n")),(0,r.kt)("h3",{id:"initialization"},"Initialization"),(0,r.kt)("p",null,"Add this line to your base Activity onCreate method:"),(0,r.kt)(o.Z,{groupId:"language",mdxType:"Tabs"},(0,r.kt)(l.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"kotlin",mdxType:"CodeBlock"},s)),(0,r.kt)(l.Z,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"java",mdxType:"CodeBlock"},u))),(0,r.kt)("p",null,"Call the ",(0,r.kt)("inlineCode",{parentName:"p"},"destroy")," method to hide the control component. Also, you can do it by clicking on the close image button:"),(0,r.kt)(o.Z,{groupId:"language",mdxType:"Tabs"},(0,r.kt)(l.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"kotlin",mdxType:"CodeBlock"},d)),(0,r.kt)(l.Z,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"java",mdxType:"CodeBlock"},c))),(0,r.kt)("p",null,"Starting from Android M - the ",(0,r.kt)("strong",{parentName:"p"},"Display over other apps")," permission is required. You will be redirected to settings automatically. To handle result properly, please add the next lines to your Activity class:"),(0,r.kt)(o.Z,{groupId:"language",mdxType:"Tabs"},(0,r.kt)(l.Z,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"kotlin",mdxType:"CodeBlock"},p)),(0,r.kt)(l.Z,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.kt)(i.Z,{language:"java",mdxType:"CodeBlock"},m))))}w.isMDXComponent=!0},3963:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/crowdin_controls_collapsed-62437a1238a5520aefe029e0e99e6e13.png"},8025:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/crowdin_controls_expanded-74e9f2a6ce323629cd56633241a21e49.png"}}]);