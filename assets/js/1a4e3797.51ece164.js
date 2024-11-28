"use strict";(self.webpackChunk_crowdin_mobile_sdk_android_website=self.webpackChunk_crowdin_mobile_sdk_android_website||[]).push([[138],{1035:(e,t,r)=>{r.r(t),r.d(t,{default:()=>P});var a=r(6540),n=r(4586),l=r(1402),s=r(5260),c=r(5489),o=r(1312);const u=["zero","one","two","few","many","other"];function m(e){return u.filter((t=>e.includes(t)))}const h={locale:"en",pluralForms:m(["one","other"]),select:e=>1===e?"one":"other"};function i(){const{i18n:{currentLocale:e}}=(0,n.A)();return(0,a.useMemo)((()=>{try{return function(e){const t=new Intl.PluralRules(e);return{locale:e,pluralForms:m(t.resolvedOptions().pluralCategories),select:e=>t.select(e)}}(e)}catch(t){return console.error(`Failed to use Intl.PluralRules for locale "${e}".\nDocusaurus will fallback to the default (English) implementation.\nError: ${t.message}\n`),h}}),[e])}function p(){const e=i();return{selectMessage:(t,r)=>function(e,t,r){const a=e.split("|");if(1===a.length)return a[0];a.length>r.pluralForms.length&&console.error(`For locale=${r.locale}, a maximum of ${r.pluralForms.length} plural forms are expected (${r.pluralForms.join(",")}), but the message contains ${a.length}: ${e}`);const n=r.select(t),l=r.pluralForms.indexOf(n);return a[Math.min(l,a.length-1)]}(r,t,e)}}var g=r(53),d=r(6347),f=r(8193);const y=function(){const e=(0,d.W6)(),t=(0,d.zy)(),{siteConfig:{baseUrl:r}}=(0,n.A)(),a=f.A.canUseDOM?new URLSearchParams(t.search):null,l=a?.get("q")||"",s=a?.get("ctx")||"",c=a?.get("version")||"",o=e=>{const r=new URLSearchParams(t.search);return e?r.set("q",e):r.delete("q"),r};return{searchValue:l,searchContext:s,searchVersion:c,updateSearchPath:t=>{const r=o(t);e.replace({search:r.toString()})},updateSearchContext:r=>{const a=new URLSearchParams(t.search);a.set("ctx",r),e.replace({search:a.toString()})},generateSearchPageLink:e=>{const t=o(e);return`${r}search?${t.toString()}`}}};var E=r(5891),C=r(2384),S=r(6841),w=r(3810),x=r(7674),_=r(2849),I=r(4471),v=r(1088);const R={searchContextInput:"searchContextInput_mXoe",searchQueryInput:"searchQueryInput_CFBF",searchResultItem:"searchResultItem_U687",searchResultItemPath:"searchResultItemPath_uIbk",searchResultItemSummary:"searchResultItemSummary_oZHr",searchQueryColumn:"searchQueryColumn_q7nx",searchContextColumn:"searchContextColumn_oWAF"};function A(){const{siteConfig:{baseUrl:e}}=(0,n.A)(),{selectMessage:t}=p(),{searchValue:r,searchContext:l,searchVersion:c,updateSearchPath:u,updateSearchContext:m}=y(),[h,i]=(0,a.useState)(r),[d,f]=(0,a.useState)(),[S,w]=(0,a.useState)(),x=`${e}${c}`,I=(0,a.useMemo)((()=>h?(0,o.T)({id:"theme.SearchPage.existingResultsTitle",message:'Search results for "{query}"',description:"The search page title for non-empty query"},{query:h}):(0,o.T)({id:"theme.SearchPage.emptyResultsTitle",message:"Search the documentation",description:"The search page title for empty query"})),[h]);(0,a.useEffect)((()=>{u(h),d&&(h?d(h,(e=>{w(e)})):w(void 0))}),[h,d]);const A=(0,a.useCallback)((e=>{i(e.target.value)}),[]);return(0,a.useEffect)((()=>{r&&r!==h&&i(r)}),[r]),(0,a.useEffect)((()=>{!async function(){const{wrappedIndexes:e,zhDictionary:t}=await(0,E.Z)(x,l);f((()=>(0,C.m)(e,t,100)))}()}),[l,x]),a.createElement(a.Fragment,null,a.createElement(s.A,null,a.createElement("meta",{property:"robots",content:"noindex, follow"}),a.createElement("title",null,I)),a.createElement("div",{className:"container margin-vert--lg"},a.createElement("h1",null,I),a.createElement("div",{className:"row"},a.createElement("div",{className:(0,g.A)("col",{[R.searchQueryColumn]:Array.isArray(v.Hg),"col--9":Array.isArray(v.Hg),"col--12":!Array.isArray(v.Hg)})},a.createElement("input",{type:"search",name:"q",className:R.searchQueryInput,"aria-label":"Search",onChange:A,value:h,autoComplete:"off",autoFocus:!0})),Array.isArray(v.Hg)?a.createElement("div",{className:(0,g.A)("col","col--3","padding-left--none",R.searchContextColumn)},a.createElement("select",{name:"search-context",className:R.searchContextInput,id:"context-selector",value:l,onChange:e=>m(e.target.value)},a.createElement("option",{value:""},v.dz?(0,o.T)({id:"theme.SearchPage.searchContext.everywhere",message:"everywhere"}):""),v.Hg.map((e=>a.createElement("option",{key:e,value:e},e))))):null),!d&&h&&a.createElement("div",null,a.createElement(_.A,null)),S&&(S.length>0?a.createElement("p",null,t(S.length,(0,o.T)({id:"theme.SearchPage.documentsFound.plurals",message:"1 document found|{count} documents found",description:'Pluralized label for "{count} documents found". Use as much plural forms (separated by "|") as your language support (see https://www.unicode.org/cldr/cldr-aux/charts/34/supplemental/language_plural_rules.html)'},{count:S.length}))):a.createElement("p",null,(0,o.T)({id:"theme.SearchPage.noResultsText",message:"No documents were found",description:"The paragraph for empty search result"}))),a.createElement("section",null,S&&S.map((e=>a.createElement(b,{key:e.document.i,searchResult:e}))))))}function b(e){let{searchResult:{document:t,type:r,page:n,tokens:l,metadata:s}}=e;const o=0===r,u=2===r,m=(o?t.b:n.b).slice(),h=u?t.s:t.t;o||m.push(n.t);let i="";if(v.CU&&l.length>0){const e=new URLSearchParams;for(const t of l)e.append("_highlight",t);i=`?${e.toString()}`}return a.createElement("article",{className:R.searchResultItem},a.createElement("h2",null,a.createElement(c.A,{to:t.u+i+(t.h||""),dangerouslySetInnerHTML:{__html:u?(0,S.Z)(h,l):(0,w.C)(h,(0,x.g)(s,"t"),l,100)}})),m.length>0&&a.createElement("p",{className:R.searchResultItemPath},(0,I.$)(m)),u&&a.createElement("p",{className:R.searchResultItemSummary,dangerouslySetInnerHTML:{__html:(0,w.C)(t.t,(0,x.g)(s,"t"),l,100)}}))}const P=function(){return a.createElement(l.A,null,a.createElement(A,null))}}}]);