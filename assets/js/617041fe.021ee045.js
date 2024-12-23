"use strict";(self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[]).push([[199],{9365:(e,t,a)=>{a.d(t,{A:()=>i});var n=a(6540),r=a(53);const o={tabItem:"tabItem_Ymn6"};function i(e){let{children:t,hidden:a,className:i}=e;return n.createElement("div",{role:"tabpanel",className:(0,r.A)(o.tabItem,i),hidden:a},t)}},1470:(e,t,a)=>{a.d(t,{A:()=>C});var n=a(8168),r=a(6540),o=a(53),i=a(3104),l=a(6347),u=a(7485),s=a(1682),d=a(9466);function p(e){return function(e){return r.Children.map(e,(e=>{if(!e||(0,r.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}(e).map((e=>{let{props:{value:t,label:a,attributes:n,default:r}}=e;return{value:t,label:a,attributes:n,default:r}}))}function g(e){const{values:t,children:a}=e;return(0,r.useMemo)((()=>{const e=t??p(a);return function(e){const t=(0,s.X)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,a])}function c(e){let{value:t,tabValues:a}=e;return a.some((e=>e.value===t))}function m(e){let{queryString:t=!1,groupId:a}=e;const n=(0,l.W6)(),o=function(e){let{queryString:t=!1,groupId:a}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return a??null}({queryString:t,groupId:a});return[(0,u.aZ)(o),(0,r.useCallback)((e=>{if(!o)return;const t=new URLSearchParams(n.location.search);t.set(o,e),n.replace({...n.location,search:t.toString()})}),[o,n])]}function y(e){const{defaultValue:t,queryString:a=!1,groupId:n}=e,o=g(e),[i,l]=(0,r.useState)((()=>function(e){let{defaultValue:t,tabValues:a}=e;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!c({value:t,tabValues:a}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${a.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const n=a.find((e=>e.default))??a[0];if(!n)throw new Error("Unexpected error: 0 tabValues");return n.value}({defaultValue:t,tabValues:o}))),[u,s]=m({queryString:a,groupId:n}),[p,y]=function(e){let{groupId:t}=e;const a=function(e){return e?`docusaurus.tab.${e}`:null}(t),[n,o]=(0,d.Dv)(a);return[n,(0,r.useCallback)((e=>{a&&o.set(e)}),[a,o])]}({groupId:n}),h=(()=>{const e=u??p;return c({value:e,tabValues:o})?e:null})();(0,r.useLayoutEffect)((()=>{h&&l(h)}),[h]);return{selectedValue:i,selectValue:(0,r.useCallback)((e=>{if(!c({value:e,tabValues:o}))throw new Error(`Can't select invalid tab value=${e}`);l(e),s(e),y(e)}),[s,y,o]),tabValues:o}}var h=a(2303);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function f(e){let{className:t,block:a,selectedValue:l,selectValue:u,tabValues:s}=e;const d=[],{blockElementScrollPositionUntilNextRender:p}=(0,i.a_)(),g=e=>{const t=e.currentTarget,a=d.indexOf(t),n=s[a].value;n!==l&&(p(t),u(n))},c=e=>{let t=null;switch(e.key){case"Enter":g(e);break;case"ArrowRight":{const a=d.indexOf(e.currentTarget)+1;t=d[a]??d[0];break}case"ArrowLeft":{const a=d.indexOf(e.currentTarget)-1;t=d[a]??d[d.length-1];break}}t?.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,o.A)("tabs",{"tabs--block":a},t)},s.map((e=>{let{value:t,label:a,attributes:i}=e;return r.createElement("li",(0,n.A)({role:"tab",tabIndex:l===t?0:-1,"aria-selected":l===t,key:t,ref:e=>d.push(e),onKeyDown:c,onClick:g},i,{className:(0,o.A)("tabs__item",v.tabItem,i?.className,{"tabs__item--active":l===t})}),a??t)})))}function w(e){let{lazy:t,children:a,selectedValue:n}=e;const o=(Array.isArray(a)?a:[a]).filter(Boolean);if(t){const e=o.find((e=>e.props.value===n));return e?(0,r.cloneElement)(e,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},o.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==n}))))}function b(e){const t=y(e);return r.createElement("div",{className:(0,o.A)("tabs-container",v.tabList)},r.createElement(f,(0,n.A)({},e,t)),r.createElement(w,(0,n.A)({},e,t)))}function C(e){const t=(0,h.A)();return r.createElement(b,(0,n.A)({key:String(t)},e))}},6789:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>C,contentTitle:()=>w,default:()=>A,frontMatter:()=>f,metadata:()=>b,toc:()=>N});var n=a(8168),r=(a(6540),a(5680)),o=a(1470),i=a(9365),l=a(2355);const u="override fun getDelegate() = BaseContextWrappingDelegate(super.getDelegate())",s="@NonNull\n@Override\npublic AppCompatDelegate getDelegate() {\n    return new BaseContextWrappingDelegate(super.getDelegate());\n}",d="override fun attachBaseContext(newBase: Context) {\n    super.attachBaseContext(Crowdin.wrapContext(newBase))\n}",p="@Override\nprotected void attachBaseContext(Context newBase) {\n    super.attachBaseContext(Crowdin.wrapContext(newBase));\n}",g="override fun onCreate() {\n    super.onCreate()\n        Crowdin.init(applicationContext,\n            CrowdinConfig.Builder()\n                .withDistributionHash(your_distribution_hash)\n                .withOrganizationName(organization_name)      // required for Crowdin Enterprise\n                .withNetworkType(network_type)                // optional\n                .withUpdateInterval(interval_in_seconds)      // optional\n                .build())\n}",c="@Override\nprotected void onCreate() {\n    super.onCreate();\n\n    Crowdin.init(this,\n        new CrowdinConfig.Builder()\n            .withDistributionHash(your_distribution_hash)\n            .withOrganizationName(organization_name)      // required for Crowdin Enterprise\n            .withNetworkType(network_type)                // optional\n            .withUpdateInterval(interval_in_seconds)      // optional\n            .build());\n}",m='/**\n * Should be overridden in case you want to change locale programmatically.\n * For a custom language, set your application locale with language and country/region constraints.\n * This should match with `Locale code:` for your custom language in Crowdin.\n *\n * language - [a-zA-Z]{2,8}\n * country/region - [a-zA-Z]{2} | [0-9]{3}\n *\n * Example: "aa-BB"\n */\noverride fun attachBaseContext(newBase: Context) {\n    languagePreferences = LanguagePreferences(newBase)\n    super.attachBaseContext(\n        ContextWrapper(newBase.updateLocale(languagePreferences.getLanguageCode()))\n    )\n}',y='/**\n * Should be overridden in case you want to change locale programmatically.\n * For a custom language, set your application locale with language and country/region constraints.\n * This should match with `Locale code:` for your custom language in Crowdin.\n *\n * language - [a-zA-Z]{2,8}\n * country/region - [a-zA-Z]{2} | [0-9]{3}\n *\n * Example: "aa-BB"\n */\n@Override\nprotected void attachBaseContext(Context newBase) {\n    languagePreferences = new LanguagePreferences(newBase);\n    super.attachBaseContext(new ContextWrapper(newBase) {\n        @Override\n        public Context getApplicationContext() {\n            return this;\n        }\n\n        @Override\n        public Resources getResources() {\n            Configuration configuration = getBaseContext().getResources().getConfiguration();\n            configuration.setLocale(new Locale(languagePreferences.getLanguageCode()));\n            Context updatedContext = getBaseContext().createConfigurationContext(configuration);\n            return updatedContext.getResources();\n        }\n    });\n}\n',h="override fun onCreateOptionsMenu(menu: Menu): Boolean {\n    menuInflater.inflateWithCrowdin(R.menu.activity_menu, menu, resources)\n    return true\n}",v="@Override\npublic boolean onCreateOptionsMenu(Menu menu) {\n    ExtentionsKt.inflateWithCrowdin(getMenuInflater(), R.menu.your_menu, menu, getResources());\n    return true;\n}",f={},w="Setup",b={unversionedId:"setup",id:"setup",title:"Setup",description:"To configure Android SDK integration you need to:",source:"@site/docs/setup.mdx",sourceDirName:".",slug:"/setup",permalink:"/mobile-sdk-android/setup",draft:!1,editUrl:"https://github.com/crowdin/mobile-sdk-android/tree/master/website/docs/setup.mdx",tags:[],version:"current",frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Installation",permalink:"/mobile-sdk-android/installation"},next:{title:"Real-Time Preview",permalink:"/mobile-sdk-android/advanced-features/real-time-preview"}},C={},N=[{value:"Context wrapping",id:"context-wrapping",level:3},{value:"Configuring Crowdin SDK",id:"configuring-crowdin-sdk",level:3},{value:"Change locale programmatically",id:"change-locale-programmatically",level:3},{value:"Config options",id:"config-options",level:3},{value:"Tips and tricks",id:"tips-and-tricks",level:3},{value:"Limitations",id:"limitations",level:3},{value:"See also",id:"see-also",level:3}],x={toc:N},T="wrapper";function A(e){let{components:t,...a}=e;return(0,r.yg)(T,(0,n.A)({},x,a,{components:t,mdxType:"MDXLayout"}),(0,r.yg)("h1",{id:"setup"},"Setup"),(0,r.yg)("p",null,"To configure Android SDK integration you need to:"),(0,r.yg)("ul",null,(0,r.yg)("li",{parentName:"ul"},"Upload your localization files to Crowdin. If you have existing translations, you can upload them as well. You can use one of the following options:",(0,r.yg)("ul",{parentName:"li"},(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://crowdin.github.io/crowdin-cli/"},"Crowdin CLI")),(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://store.crowdin.com/android-studio"},"Android Studio Plugin")),(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://github.com/marketplace/actions/crowdin-action"},"Crowdin GitHub Action")),(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://support.crowdin.com/uploading-files/"},"and more")))),(0,r.yg)("li",{parentName:"ul"},"Set up Distribution in Crowdin."),(0,r.yg)("li",{parentName:"ul"},"Set up SDK and enable Over-The-Air Content Delivery feature in your project.")),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"Distribution")," is a CDN vault that mirrors the translated content of your project and is required for integration with Android app."),(0,r.yg)("ul",null,(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://support.crowdin.com/content-delivery/"},"Creating a distribution in crowdin.com")),(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"https://support.crowdin.com/enterprise/content-delivery/"},"Creating a distribution in Crowdin Enterprise"))),(0,r.yg)("admonition",{type:"info"},(0,r.yg)("ul",{parentName:"admonition"},(0,r.yg)("li",{parentName:"ul"},"By default, the translation downloading happens ",(0,r.yg)("strong",{parentName:"li"},"asynchronously")," after launching the app. The downloaded translations will be used after the next launch of the app or Activity re-render. Otherwise, the ",(0,r.yg)("a",{parentName:"li",href:"/cache"},"previously cached translations")," will be used (or local translations if a cache does not exist)."),(0,r.yg)("li",{parentName:"ul"},"The CDN feature does not update the localization files. if you want to add new translations to the localization files you need to do it yourself."),(0,r.yg)("li",{parentName:"ul"},"Once SDK receives the translations, it's stored on the device as application files for further sessions to minimize requests the next time the app starts. Storage time can be configured using ",(0,r.yg)("inlineCode",{parentName:"li"},"withUpdateInterval")," option."),(0,r.yg)("li",{parentName:"ul"},"CDN caches all the translation in release for up to 1 hour and even when new translations are released in Crowdin, CDN may return it with a delay."),(0,r.yg)("li",{parentName:"ul"},"To display a string, Crowdin will try to find it in the dynamic strings (from the CDN) and use the bundled version as a fallback. In other words, only the newly provided strings will be overridden and the bundled version will be used for the rest."))),(0,r.yg)("p",null,"To integrate the SDK with your application, follow the step-by-step instructions:"),(0,r.yg)("h3",{id:"context-wrapping"},"Context wrapping"),(0,r.yg)("p",null,"Inject Crowdin translations by adding the ",(0,r.yg)("em",{parentName:"p"},"override")," method to the ",(0,r.yg)("em",{parentName:"p"},"BaseActivity")," class to inject Crowdin translations into the Context. If you have already migrated to ",(0,r.yg)("a",{parentName:"p",href:"https://developer.android.com/jetpack/androidx/releases/appcompat"},"AppCompat")," ",(0,r.yg)("strong",{parentName:"p"},"1.2.0+")," version, use this method:"),(0,r.yg)(o.A,{groupId:"language",mdxType:"Tabs"},(0,r.yg)(i.A,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"kotlin",mdxType:"CodeBlock"},u)),(0,r.yg)(i.A,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"java",mdxType:"CodeBlock"},s))),(0,r.yg)("p",null,"For AppCompat ",(0,r.yg)("strong",{parentName:"p"},"1.1.0")," and lower use this:"),(0,r.yg)(o.A,{groupId:"language",mdxType:"Tabs"},(0,r.yg)(i.A,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"kotlin",mdxType:"CodeBlock"},d)),(0,r.yg)(i.A,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"java",mdxType:"CodeBlock"},p))),(0,r.yg)("admonition",{type:"info"},(0,r.yg)("p",{parentName:"admonition"},"If you don't have the ",(0,r.yg)("em",{parentName:"p"},"BaseActivity")," class, add the above code to all of your activities.")),(0,r.yg)("h3",{id:"configuring-crowdin-sdk"},"Configuring Crowdin SDK"),(0,r.yg)("p",null,"Enable ",(0,r.yg)("em",{parentName:"p"},"Over-The-Air Content Delivery")," in your project so that the application can pull translations from the CDN vault. Add the following code to the ",(0,r.yg)("em",{parentName:"p"},"App"),"/",(0,r.yg)("em",{parentName:"p"},"Application")," class:"),(0,r.yg)(o.A,{groupId:"language",mdxType:"Tabs"},(0,r.yg)(i.A,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"kotlin",mdxType:"CodeBlock"},g)),(0,r.yg)(i.A,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"java",mdxType:"CodeBlock"},c))),(0,r.yg)("h3",{id:"change-locale-programmatically"},"Change locale programmatically"),(0,r.yg)("p",null,"Crowdin works with the current locale, if you want to change the locale programmatically use the ",(0,r.yg)("em",{parentName:"p"},"language")," plus ",(0,r.yg)("em",{parentName:"p"},"country")," format: ",(0,r.yg)("inlineCode",{parentName:"p"},'Locale("en", "US")'),"."),(0,r.yg)("p",null,"Example of language change in ",(0,r.yg)("strong",{parentName:"p"},"App.kt/Application.java"),":"),(0,r.yg)(o.A,{groupId:"language",mdxType:"Tabs"},(0,r.yg)(i.A,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"kotlin",mdxType:"CodeBlock"},m)),(0,r.yg)(i.A,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"java",mdxType:"CodeBlock"},y))),(0,r.yg)("admonition",{type:"caution"},(0,r.yg)("p",{parentName:"admonition"},"Make sure you've added this code to the ",(0,r.yg)("strong",{parentName:"p"},"App.kt/Application.java")," class.")),(0,r.yg)("h3",{id:"config-options"},"Config options"),(0,r.yg)("table",null,(0,r.yg)("thead",{parentName:"table"},(0,r.yg)("tr",{parentName:"thead"},(0,r.yg)("th",{parentName:"tr",align:null},"Config option"),(0,r.yg)("th",{parentName:"tr",align:null},"Description"),(0,r.yg)("th",{parentName:"tr",align:null},"Example"))),(0,r.yg)("tbody",{parentName:"table"},(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},(0,r.yg)("inlineCode",{parentName:"td"},"withDistributionHash")),(0,r.yg)("td",{parentName:"tr",align:null},"Distribution Hash"),(0,r.yg)("td",{parentName:"tr",align:null},(0,r.yg)("inlineCode",{parentName:"td"},'withDistributionHash("7a0c1...7uo3b")'))),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},(0,r.yg)("inlineCode",{parentName:"td"},"withNetworkType")),(0,r.yg)("td",{parentName:"tr",align:null},"Network type to be used for translations download"),(0,r.yg)("td",{parentName:"tr",align:null},"Acceptable values are:",(0,r.yg)("br",null),"- ",(0,r.yg)("inlineCode",{parentName:"td"},"NetworkType.ALL")," (default)",(0,r.yg)("br",null)," - ",(0,r.yg)("inlineCode",{parentName:"td"},"NetworkType.CELLULAR"),(0,r.yg)("br",null),"- ",(0,r.yg)("inlineCode",{parentName:"td"},"NetworkType.WIFI"))),(0,r.yg)("tr",{parentName:"tbody"},(0,r.yg)("td",{parentName:"tr",align:null},(0,r.yg)("inlineCode",{parentName:"td"},"withUpdateInterval")),(0,r.yg)("td",{parentName:"tr",align:null},"Translations update interval in seconds. The minimum and the default value is 15 minutes. Visit the ",(0,r.yg)("a",{parentName:"td",href:"/cache"},"Cache")," page for more details"),(0,r.yg)("td",{parentName:"tr",align:null},(0,r.yg)("inlineCode",{parentName:"td"},"withUpdateInterval(900)"))))),(0,r.yg)("h3",{id:"tips-and-tricks"},"Tips and tricks"),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"1.")," To translate menu items you need to update your ",(0,r.yg)("inlineCode",{parentName:"p"},"onCreateOptionsMenu")," method:"),(0,r.yg)(o.A,{groupId:"language",mdxType:"Tabs"},(0,r.yg)(i.A,{value:"kotlin",label:"Kotlin",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"kotlin",mdxType:"CodeBlock"},h)),(0,r.yg)(i.A,{value:"java",label:"Java",mdxType:"TabItem"},(0,r.yg)(l.A,{language:"java",mdxType:"CodeBlock"},v))),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"2.")," In case you have custom views that uses ",(0,r.yg)("inlineCode",{parentName:"p"},"TypedArray")," and ",(0,r.yg)("inlineCode",{parentName:"p"},"stylable")," attributes, you will need to use the following approach:"),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-kotlin"},"val textId = typedArray.getResourceId(R.styleable.sample_item, 0)\n textView.setText(textId)\n")),(0,r.yg)("p",null,"instead of ",(0,r.yg)("inlineCode",{parentName:"p"},"typedArray.getString(R.styleable.sample_item)"),"."),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"3.")," Activity title defined via ",(0,r.yg)("em",{parentName:"p"},"AndroidManifest")," won't be translated:"),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-xml"},'<activity\n    android:name=".activities.SampleActivity"\n    android:label="@string/title"/>\n')),(0,r.yg)("p",null,"You can simply update your ",(0,r.yg)("inlineCode",{parentName:"p"},"toolbar")," inside of activity or fragment:"),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-java"},"toolbar.setTitle(R.string.title);\n")),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"4.")," In case your project already overrides ",(0,r.yg)("inlineCode",{parentName:"p"},"attachBaseContext"),":"),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-java"},"super.attachBaseContext(Crowdin.wrapContext(SomeLib.wrap(newBase)));\n")),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"5.")," You can register/unregister observer for data changes by adding this lines:"),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-kotlin"},"override fun onCreate(savedInstanceState: Bundle?) {\n    Crowdin.registerDataLoadingObserver(this)\n}\n")),(0,r.yg)("p",null,"It has callback method ",(0,r.yg)("inlineCode",{parentName:"p"},"onDataChanged()")," that can be used to invalidate your UI (TextView/Menu etc.). It will use downloaded resources automatically."),(0,r.yg)("pre",null,(0,r.yg)("code",{parentName:"pre",className:"language-kotlin"},"override fun onDataChanged() {\n    invalidateOptionsMenu()\n    Crowdin.updateMenuItemsText(R.menu.activity_main_drawer, navigationView.menu, resources)\n    toolbarMain.title = getString(R.string.category)\n}\n")),(0,r.yg)("p",null,"Otherwise, new resources are applied when the activity is restarted."),(0,r.yg)("p",null,(0,r.yg)("strong",{parentName:"p"},"6.")," In case you have a custom ",(0,r.yg)("inlineCode",{parentName:"p"},"TextView")," with string specified in xml, make sure you follow this naming convention ",(0,r.yg)("inlineCode",{parentName:"p"},"PlaceholderTextView")," otherwise SDK will skip this view during inflating process and it won't be translated."),(0,r.yg)("h3",{id:"limitations"},"Limitations"),(0,r.yg)("ol",null,(0,r.yg)("li",{parentName:"ol"},"Plurals are supported from Android SDK version 24."),(0,r.yg)("li",{parentName:"ol"},(0,r.yg)("inlineCode",{parentName:"li"},"TabItem")," text added via xml won't be updated. There is workaround: you can store tabItem titles in your string-array and add tabs dynamically."),(0,r.yg)("li",{parentName:"ol"},(0,r.yg)("inlineCode",{parentName:"li"},"PreferenceScreen")," defined via XML are not supported.")),(0,r.yg)("h3",{id:"see-also"},"See also"),(0,r.yg)("ul",null,(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"advanced-features/real-time-preview"},"Real-Time Preview")),(0,r.yg)("li",{parentName:"ul"},(0,r.yg)("a",{parentName:"li",href:"advanced-features/screenshots"},"Screenshots"))))}A.isMDXComponent=!0}}]);