(window.webpackJsonp=window.webpackJsonp||[]).push([[2],{"+1kA":function(e,t,n){"use strict";n.d(t,"a",function(){return r}),n.d(t,"b",function(){return s});var i=n("ofXK"),o=n("fXoL");const r=new o.s("WindowToken");class a{get nativeWindow(){throw new Error("Not implemented.")}}const s=[{provide:a,useClass:(()=>{class e extends a{constructor(){super()}get nativeWindow(){return window}}return e.\u0275fac=function(t){return new(t||e)},e.\u0275prov=o.Mb({token:e,factory:e.\u0275fac}),e})()},{provide:r,useFactory:function(e,t){return Object(i.t)(t)?e.nativeWindow:{}},deps:[a,o.D]}]},"/4YZ":function(e,t,n){"use strict";n.d(t,"a",function(){return s});var i=n("fXoL"),o=n("Q6dW"),r=n("jhN1"),a=n("5KMX");let s=(()=>{class e{constructor(e,t,n){this.browserService=e,this.sanitizer=t,this.dompurifySanitizer=n}transform(e,t=i.K.HTML){if(!this.browserService.isBrowser)return this.sanitizer.sanitize(t,e);try{return this.bypassSecurityTrust(t,this.dompurifySanitizer.sanitize(t,e,{ADD_TAGS:["iframe"],ADD_ATTR:["target"]}))}catch(n){return this.sanitizer.sanitize(t,e)}}bypassSecurityTrust(e,t){switch(e){case i.K.HTML:return this.sanitizer.bypassSecurityTrustHtml(t);case i.K.STYLE:return this.sanitizer.bypassSecurityTrustStyle(t);case i.K.SCRIPT:return this.sanitizer.bypassSecurityTrustScript(t);case i.K.URL:return this.sanitizer.bypassSecurityTrustUrl(t);case i.K.RESOURCE_URL:return this.sanitizer.bypassSecurityTrustResourceUrl(t);default:return null}}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(o.a),i.Qb(r.c),i.Qb(a.b))},e.\u0275pipe=i.Pb({name:"safeHtml",type:e,pure:!0}),e})()},0:function(e,t,n){e.exports=n("u2Ms")},AyuD:function(e,t){function n(e){return Promise.resolve().then(function(){var t=new Error("Cannot find module '"+e+"'");throw t.code="MODULE_NOT_FOUND",t})}n.keys=function(){return[]},n.resolve=n,e.exports=n,n.id="AyuD"},DV3j:function(e,t,n){"use strict";n.d(t,"a",function(){return _});var i=n("lTCR"),o=n.n(i),r=n("gHoO");const a=()=>o.a`
  fragment FieldsSeoOnSite on ComponentInfoSeo {
    title
    description
    keywords
    opengraph {
      url
    }
  }
`,s=()=>o.a`
  fragment FieldsComponentPagesDefaultPage on ComponentPagesDefaultPage {
    name
    title: title_htmlinline
    description: description_html
    cover {
      url
      previewUrl
    }
    seo {
      ...FieldsSeoOnSite
    }
    blocks {
      ...FieldsOnBlock
    }
    actions {
      ...FieldsComponentBlocksLink
    }
  }
`,l=()=>o.a`
  fragment FieldsComponentPagesSimplePage on ComponentPagesSimplePage {
    name
    title: title_html
    description: description_html
    blocks {
      ...FieldsOnBlock
    }
    seo {
      ...FieldsSeoOnSite
    }
  }
`,c=()=>o.a`
  fragment FieldsComponentPagesExternalLinkPage on ComponentPagesExternalLinkPage {
    name
    link
  }
`,u=()=>o.a`
  fragment FieldsPageOnSite on Page {
    path
    order
    showOnNavigation
    showOnSitemap
    view {
      ...FieldsComponentPagesDefaultPage
      ...FieldsComponentPagesSimplePage
      ...FieldsComponentPagesExternalLinkPage
    }
    parent {
      path
    }
    children {
      path
      view {
        ... on ComponentPagesDefaultPage {
          name
        }
        ... on ComponentPagesSimplePage {
          name
        }
        ... on ComponentPagesExternalLinkPage {
          name
        }
      }
    }
  }
`;n("ofXK");var d=n("fXoL"),h=n("eIep"),p=n("lJxs"),g=n("vkgz"),f=n("05l1"),m=n("x+ZX"),b=n("LRne"),v=n("/IUn"),k=n("tyNb"),C=n("Q6dW"),w=n("VsGz"),O=n("NKZn"),y=n("LR87");let _=(()=>{class e{constructor(e,t,n,i,d,b,v){this.apollo=e,this.route=t,this.browserService=n,this.urlService=i,this.langService=d,this.seoService=b,this.blockService=v,this.site$=this.langService.lang.pipe(Object(h.a)(e=>this.apollo.query({query:o.a`
    query($langFilter: JSON) {
      site {
        seo {
          ...FieldsSeoOnSite
        }
        pages(where: $langFilter) {
          ...FieldsPageOnSite
        }
      }
    }

    ${u()}
    ${s()}
    ${l()}
    ${c()}
    ${a()}

    ${Object(r.x)()}
    ${Object(r.w)()}

    ${Object(r.c)()}
    ${Object(r.d)()}
    ${Object(r.e)()}
    ${Object(r.g)()}
    ${Object(r.f)()}
    ${Object(r.q)()}
    ${Object(r.h)()}
    ${Object(r.r)()}
    ${Object(r.s)()}

    ${Object(r.t)()}
    ${Object(r.u)()}
    ${Object(r.v)()}

    ${Object(r.i)()}
    ${Object(r.j)()}
    ${Object(r.k)()}
    ${Object(r.m)()}
    ${Object(r.l)()}
    ${Object(r.n)()}
    ${Object(r.o)()}
    ${Object(r.p)()}
  `,variables:{langFilter:{langName:e}},context:{headers:{}}}).pipe(Object(p.a)(e=>e.data.site))),Object(g.a)(()=>{this.browserService.triggerLoadPage()}),Object(f.a)(1),Object(m.a)()),this.pages={}}get site(){return this.site$}get page(){return this.urlService.url.pipe(Object(h.a)(e=>this.getPageByUrl(e)),Object(g.a)(e=>{e.seo&&this.seoService.addSeoData(e.seo)}))}getPageByUrl(e){const t=this.createIndex(this.langService.currentLang,e);return this.pages[t]||(this.pages[t]=this.site$.pipe(Object(p.a)(t=>t.pages.find(t=>e===t.path)),Object(h.a)(t=>t?Object(b.a)(t):this.loadPage(e)),Object(p.a)(this.preparePageRaw.bind(this)),Object(f.a)(1),Object(m.a)())),this.pages[t]}get sitemap(){return this.site$.pipe(Object(p.a)(e=>e.pages.filter(e=>!!e.showOnSitemap).map(e=>this.prepareSitemapItem(e)).sort((e,t)=>e.order-t.order)))}get navigation(){return this.site$.pipe(Object(p.a)(e=>e.pages.filter(e=>!!e.showOnNavigation).map(e=>this.prepareSitemapItem(e)).sort((e,t)=>e.order-t.order)))}createIndex(e,t){return this.langService.currentLang+"|"+t}preparePageRaw(e){var t,n,i;return Object.assign(Object.assign({},null==e?void 0:e.view[0]),{blocksObjects:null===(n=null===(t=null==e?void 0:e.view[0])||void 0===t?void 0:t.blocks)||void 0===n?void 0:n.reduce((e,t)=>{let n;return n=t||new r.a,Object.assign(Object.assign({},e),{[t.slug]:this.blockService.prepareData(n)})},{}),path:null==e?void 0:e.path,children:null==e?void 0:e.children,parent:null===(i=null==e?void 0:e.parent)||void 0===i?void 0:i.path})}prepareSitemapItem(e){var t,n,i;return{path:null==e?void 0:e.path,name:null===(t=null==e?void 0:e.view[0])||void 0===t?void 0:t.name,link:(null===(n=null==e?void 0:e.view[0])||void 0===n?void 0:n.link)||null,order:e.order,children:null===(i=null==e?void 0:e.children)||void 0===i?void 0:i.map(e=>this.prepareSitemapItem(e))}}loadPage(e){return this.langService.lang.pipe(Object(h.a)(t=>this.apollo.query({query:o.a`
    query($where: JSON!) {
      pageByLang(where: $where) {
        ...FieldsPageOnSite
      }
    }

    ${u()}
    ${s()}
    ${l()}
    ${c()}
    ${a()}

    ${Object(r.x)()}
    ${Object(r.w)()}

    ${Object(r.c)()}
    ${Object(r.d)()}
    ${Object(r.e)()}
    ${Object(r.g)()}
    ${Object(r.f)()}
    ${Object(r.q)()}
    ${Object(r.h)()}
    ${Object(r.r)()}
    ${Object(r.s)()}

    ${Object(r.t)()}
    ${Object(r.u)()}
    ${Object(r.v)()}

    ${Object(r.i)()}
    ${Object(r.j)()}
    ${Object(r.k)()}
    ${Object(r.m)()}
    ${Object(r.l)()}
    ${Object(r.n)()}
    ${Object(r.o)()}
    ${Object(r.p)()}
  `,variables:{where:{langName:t,path:e}},context:{headers:{}}}).pipe(Object(p.a)(t=>{var n,i;return null===(i=null===(n=null==t?void 0:t.data)||void 0===n?void 0:n.pageByLang.slice().sort((t,n)=>e.replace(t.path).length-e.replace(n.path).length))||void 0===i?void 0:i[0]}))),Object(f.a)(1),Object(m.a)())}}return e.\u0275fac=function(t){return new(t||e)(d.ac(v.a),d.ac(k.a),d.ac(C.a),d.ac(w.a),d.ac(O.a),d.ac(y.a),d.ac(r.b))},e.\u0275prov=d.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},LR87:function(e,t,n){"use strict";n.d(t,"a",function(){return p});var i=n("LRne"),o=n("lJxs"),r=n("vkgz");class a{}var s=n("lTCR"),l=n.n(s),c=n("ofXK"),u=n("fXoL"),d=n("NKZn"),h=n("jhN1");let p=(()=>{class e{constructor(e,t,n,i){this.langService=e,this.titleService=t,this.metaService=n,this.document=i}getSeoAndApply(){return Object(i.a)(null)}getFragment(e){const t=e.replace(/[^a-z0-9]/g,"");return this.langService.applyFragment(((e,t)=>l.a`
    fragment SeoOnLangFragment${e} on Lang {
      ${"seo_"+e}: seo_pages (where: {slug: "${t}"}) {
      ...FieldsOnSeo
      }
    }

    ${l.a`
  fragment FieldsOnSeo on SeoPage {
    title
    slug
    description
    url
    opengraph {
      url
    }
    sitename
  }
`}
  `)(t,e)).pipe(Object(o.a)(e=>{if(!e)return new a;const n=e["seo_"+t];return n&&n.length>0?n[0]:new a}),Object(r.a)(e=>{this.addSeoData(e)}))}addSeoData(e){const{title:t,description:n,keywords:i,siteName:o,url:r,opengraph:a}=e;this.setBaseTags(),this.setTitle(t),this.setDescription(n),this.setKeywords(i),this.setSiteName(o),this.setUrl(r),this.setImage(a?a.url:null)}setBaseTags(){this.metaService.updateTag({name:"referrer",content:"no-referrer-when-downgrade"})}setDescription(e){e&&(this.metaService.updateTag({property:"og:description",content:e}),this.metaService.updateTag({name:"description",content:e}))}setKeywords(e){e&&this.metaService.updateTag({name:"keywords",content:e})}setUrl(e){e&&this.metaService.updateTag({property:"og:url",content:e})}setImage(e){e&&(this.metaService.updateTag({property:"og:image",content:e}),this.metaService.updateTag({name:"twitter:card",content:e}))}setTitle(e){e&&(this.titleService.setTitle(e),this.metaService.updateTag({property:"og:title",content:e}))}setSiteName(e){e&&this.metaService.updateTag({name:"og:site_name",content:e})}setFbAppId(e){e&&this.metaService.updateTag({property:"fb:app_id",content:e})}setTwitterUsername(e){e&&this.metaService.updateTag({name:"twitter:site",content:e})}}return e.\u0275fac=function(t){return new(t||e)(u.ac(d.a),u.ac(h.f),u.ac(h.e),u.ac(c.d))},e.\u0275prov=u.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},NKZn:function(e,t,n){"use strict";n.d(t,"a",function(){return x});var i=n("2Vo4"),o=n("XNiG"),r=n("LRne"),a=n("lTCR"),s=n.n(a);const l=e=>{const t=(e=>{const t={};return Array.isArray(e)&&e.length>0?e.reduce((e,n,i)=>{if(t[n.definitions[0].name.value])return console.warn("Dublicate "+n.definitions[0].name.value+" entities in one request. Change name entity"),e;t[n.definitions[0].name.value]=!0;let o=e;const{loc:{source:{body:r}}}=n;return 0===i?(o=r,o):`${o}${r}`},""):""})(e),n={},i=e.map(e=>{const t=e.definitions[0];return n[t.name.value]?"":(n[t.name.value]=!0,"..."+t.name.value)}).join("\n");return s.a`
    query($lang: JSON) {
      langs(where: $lang) {
        ...FieldsOnLang
        ${i}
      }
    }

    ${s.a`
  fragment FieldsOnLang on Lang {
    id
    slug
    title
  }
`}
    ${t}
  `};var c=n("05l1"),u=n("x+ZX"),d=n("p9/F"),h=n("Kj3r"),p=n("5+tZ"),g=n("IzEk"),f=n("vkgz"),m=n("eIep"),b=n("pLZG"),v=n("lJxs"),k=n("/uUt");class C{}var w=n("ofXK"),O=n("+1kA"),y=n("fXoL"),_=n("/IUn"),L=n("tyNb"),j=n("RF2t");let x=(()=>{class e{constructor(e,t,n,r,a){this.apollo=e,this.router=t,this.baseHref=n,this.boxService=r,this.window=a,this.defaultLang="en",this.currentLang$=new i.a(this.defaultLang),this.bufferBy=new o.a,this.fragmentSubject=new i.a(null),this.langSubscribe=this.currentLang$.pipe(Object(c.a)(1),Object(u.a)()),this.fragments=[],this.prefixLang="",this.random=()=>Math.round(1e4*Math.random());const s=this.baseHref.replace(/[^a-z]/g,""),l=this.getLangStorage();this.currentLang$.next(l||(""===s?this.defaultLang:s)),this.init()}init(){this.subscribe=this.fragmentSubject.pipe(Object(d.a)(this.bufferBy.pipe(Object(h.a)(0))),Object(p.a)(e=>this.request(e,this.currentLang$.value).pipe(Object(g.a)(1))),Object(c.a)(1),Object(u.a)())}applyFragment(e,t){return this.langSubscribe.pipe(Object(f.a)(()=>{this.fragments.push(e),this.fragmentSubject.next(e),clearTimeout(this.timeout),this.timeout=setTimeout(()=>{this.bufferBy.next()},0)}),Object(m.a)(()=>this.subscribe.pipe(Object(m.a)(e=>Object(r.a)(e[t])),Object(b.a)(e=>!!e))))}request(e,t){return this.apollo.query({query:l(e),variables:{lang:{slug:t}},context:{headers:{}}}).pipe(Object(f.a)(()=>{this.fragments=[]}),Object(v.a)(e=>e.data&&e.data.langs&&e.data.langs.length?e.data.langs[0]:new C),Object(v.a)(e=>e),Object(g.a)(1))}setLang(e){this.changeLanguage(e)}get lang(){return this.currentLang$.pipe(Object(k.a)())}get currentLang(){return this.currentLang$.getValue()}get prefix(){return this.prefixLang}changeLanguage(e){this.setLangStorage(e),this.window&&this.window.location.replace((e===this.defaultLang?"..":"/"+e)+this.router.url)}setLangStorage(e){this.window&&this.window.sessionStorage&&this.window.sessionStorage.setItem("lang-"+this.random,e)}getLangStorage(){return this.window&&this.window.sessionStorage?this.window.sessionStorage.getItem("lang-"+this.random):null}}return e.\u0275fac=function(t){return new(t||e)(y.ac(_.a),y.ac(L.e),y.ac(w.a),y.ac(j.a),y.ac(O.a))},e.\u0275prov=y.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},Nh5a:function(e,t,n){"use strict";n.d(t,"a",function(){return O});var i=n("fXoL"),o=n("ofXK"),r=n("tyNb");function a(e,t){1&e&&i.Sb(0)}function s(e,t){if(1&e&&(i.Wb(0,"span",7),i.Cc(1,a,1,0,"ng-container",8),i.Vb()),2&e){i.ic(3);const e=i.tc(5);i.Eb(1),i.oc("ngTemplateOutlet",e)}}const l=function(e){return{"sui-link-with-arrow":e,link:!0}};function c(e,t){if(1&e&&(i.Wb(0,"a",5),i.Cc(1,s,2,1,"span",6),i.Vb()),2&e){const e=i.ic(2),t=i.tc(7);i.oc("className",e.classNameCombined)("ngClass",i.rc(5,l,e.arrow))("routerLink",e.hrefSource),i.Eb(1),i.oc("ngIf",!e.arrow)("ngIfElse",t)}}function u(e,t){if(1&e&&(i.Ub(0),i.Cc(1,c,2,7,"a",4),i.Tb()),2&e){const e=i.ic(),t=i.tc(3);i.Eb(1),i.oc("ngIf","internal"===e.type)("ngIfElse",t)}}function d(e,t){1&e&&i.Sb(0)}function h(e,t){if(1&e&&(i.Ub(0),i.Cc(1,d,1,0,"ng-container",8),i.Tb()),2&e){i.ic(2);const e=i.tc(5);i.Eb(1),i.oc("ngTemplateOutlet",e)}}const p=function(e){return{disabled:!0,"sui-link-with-arrow":e,link:!0}};function g(e,t){if(1&e&&(i.Ub(0),i.Wb(1,"span",9),i.Cc(2,h,2,1,"ng-container",10),i.Vb(),i.Tb()),2&e){const e=i.ic(),t=i.tc(7);i.Eb(1),i.oc("className",e.classNameCombined)("ngClass",i.rc(4,p,e.arrow)),i.Eb(1),i.oc("ngIf",!e.arrow)("ngIfElse",t)}}function f(e,t){1&e&&i.Sb(0)}function m(e,t){if(1&e&&(i.Wb(0,"span",7),i.Cc(1,f,1,0,"ng-container",8),i.Vb()),2&e){i.ic(2);const e=i.tc(5);i.Eb(1),i.oc("ngTemplateOutlet",e)}}function b(e,t){if(1&e&&(i.Wb(0,"a",11),i.Cc(1,m,2,1,"span",6),i.Vb()),2&e){const e=i.ic(),t=i.tc(7);i.oc("className",e.classNameCombined)("ngClass",i.rc(5,l,e.arrow))("href",e.hrefSource,i.xc),i.Eb(1),i.oc("ngIf",!e.arrow)("ngIfElse",t)}}function v(e,t){1&e&&i.mc(0)}function k(e,t){1&e&&i.Sb(0)}function C(e,t){if(1&e&&(i.Wb(0,"span",12),i.Cc(1,k,1,0,"ng-container",8),i.Vb(),i.Wb(2,"span",13),i.hc(),i.Wb(3,"svg",14),i.Rb(4,"path",15),i.Vb(),i.Vb()),2&e){i.ic();const e=i.tc(5);i.Eb(1),i.oc("ngTemplateOutlet",e)}}const w=["*"];let O=(()=>{class e{constructor(e){this.cdr=e,this.type="internal",this.theme="primary",this.className=null,this.arrow=!1,this.disabled=!1}set href(e){this.type=0===(null==e?void 0:e.indexOf("http"))||0===(null==e?void 0:e.indexOf("mailto:"))||0===(null==e?void 0:e.indexOf("#"))?"external":"internal",this.hrefSource=e,this.cdr.markForCheck()}ngOnChanges(e){var t,n,i;((null===(t=null==e?void 0:e.className)||void 0===t?void 0:t.currentValue)||(null===(n=null==e?void 0:e.theme)||void 0===n?void 0:n.currentValue)||(null===(i=null==e?void 0:e.arrow)||void 0===i?void 0:i.currentValue))&&(this.classNameCombined=[this.className,"link link--"+this.theme,this.arrow?"sui-link-with-arrow":""].filter(e=>!!e).join(" "))}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(i.h))},e.\u0275cmp=i.Kb({type:e,selectors:[["ui-link"]],inputs:{href:"href",theme:"theme",className:"className",arrow:"arrow",disabled:"disabled"},features:[i.Cb],ngContentSelectors:w,decls:8,vars:2,consts:[[4,"ngIf"],["externalTemplate",""],["content",""],["contentArrow",""],[3,"className","ngClass","routerLink",4,"ngIf","ngIfElse"],[3,"className","ngClass","routerLink"],["class","link__container",4,"ngIf","ngIfElse"],[1,"link__container"],[4,"ngTemplateOutlet"],[1,"link__container",3,"className","ngClass"],[4,"ngIf","ngIfElse"],["target","_blank","rel","nofollow noopener noreferrer",3,"className","ngClass","href"],[1,"sui-link-with-arrow__text"],[1,"sui-link-with-arrow__arrow"],["xmlns","http://www.w3.org/2000/svg","width","13","height","8","viewBox","0 0 13 8","fill","none",1,"sui-link-with-arrow__arrow-ico","sui-link-with-arrow__arrow-ico"],["d","M12.354 4.354a.5.5 0 000-.708L9.172.464a.5.5 0 10-.708.708L11.293 4 8.464 6.828a.5.5 0 10.708.708l3.182-3.182zM0 4.5h12v-1H0v1z"]],template:function(e,t){1&e&&(i.nc(),i.Cc(0,u,2,2,"ng-container",0),i.Cc(1,g,3,6,"ng-container",0),i.Cc(2,b,2,7,"ng-template",null,1,i.Dc),i.Cc(4,v,1,0,"ng-template",null,2,i.Dc),i.Cc(6,C,5,1,"ng-template",null,3,i.Dc)),2&e&&(i.oc("ngIf",!t.disabled),i.Eb(1),i.oc("ngIf",t.disabled))},directives:[o.l,r.g,o.i,o.p],styles:[".link[_ngcontent-%COMP%]{border:0}.link__container[_ngcontent-%COMP%]{display:flex;align-items:center}.link.disabled[_ngcontent-%COMP%]{cursor:default}.link[_ngcontent-%COMP%]:after{display:none}.link--primary[_ngcontent-%COMP%]{color:var(--color-primary);transition:color .3s ease}.link--primary[_ngcontent-%COMP%]:hover{color:var(--color-primary-darken)}.link--primary[_ngcontent-%COMP%]:hover     path{fill:var(--color-primary-darken)}.link--primary[_ngcontent-%COMP%]     path{fill:var(--color-primary)}.link--secondary[_ngcontent-%COMP%]{color:#fff;transition:color .3s ease}.link--secondary[_ngcontent-%COMP%]:hover{color:#c0c1c3}.link--secondary[_ngcontent-%COMP%]:hover     path{fill:#c0c1c3}.link--secondary[_ngcontent-%COMP%]     path{fill:#fff}"],changeDetection:0}),e})()},Q6dW:function(e,t,n){"use strict";n.d(t,"a",function(){return a});var i=n("ofXK"),o=n("fXoL"),r=n("+1kA");let a=(()=>{class e{constructor(e,t,n){this.window=e,this.document=t,this.isBrowserPlatform=Object(i.t)(n)}get isBrowser(){return this.isBrowserPlatform}triggerLoadPage(){this.isBrowserPlatform&&this.document.dispatchEvent(new Event("load-content"))}}return e.\u0275fac=function(t){return new(t||e)(o.ac(r.a),o.ac(i.d),o.ac(o.D))},e.\u0275prov=o.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},RF2t:function(e,t,n){"use strict";n.d(t,"a",function(){return b});var i=n("ofXK"),o=n("fXoL"),r=n("2Vo4"),a=n("XNiG"),s=n("xgIS"),l=n("VRyK"),c=n("LRne"),u=n("lJxs"),d=n("05l1"),h=n("x+ZX"),p=n("pLZG"),g=n("/uUt"),f=n("gcYM"),m=n("+1kA");let b=(()=>{class e{constructor(e,t,n){this.window=e,this.document=t,this.event$=new r.a(null),this.load$=new a.a,this.resize$=Object(s.a)(this.window,"resize",{passive:!0}).pipe(Object(u.a)(e=>{var t;return null===(t=null==this?void 0:this.document)||void 0===t?void 0:t.documentElement}),Object(d.a)(1),Object(h.a)()),this.scroll$=Object(s.a)(this.document,"scroll",{passive:!0}).pipe(Object(u.a)(e=>{var t;return null===(t=null==this?void 0:this.document)||void 0===t?void 0:t.documentElement}),Object(d.a)(1),Object(h.a)()),this.viewClientRect$=Object(l.a)(this.resize$,this.scroll$).pipe(Object(d.a)(1),Object(h.a)()),this.eventObservable=this.event$.pipe(Object(p.a)(e=>!!e),Object(g.a)((e,t)=>e.width+e.height===t.width+t.height),Object(d.a)(1),Object(h.a)()),this.boxCurrentValues=null,this.isBrowser=Object(i.t)(n),Object(i.t)(n)&&(this.resize$.pipe(Object(f.a)(100),Object(u.a)(e=>{const t=null==e?void 0:e.clientWidth,n=null==e?void 0:e.clientHeight;return this.event$.next({isBrowser:this.isBrowser,width:t,height:n}),{width:t,height:n}})).subscribe(),setTimeout(()=>{this.window.dispatchEvent(new Event("resize"))}))}get resize(){return this.isBrowser?this.resize$:Object(c.a)(null)}get scroll(){return this.isBrowser?this.scroll$:Object(c.a)(null)}get viewClientRect(){return this.isBrowser?this.viewClientRect$:Object(c.a)(null)}get subscription(){return this.eventObservable}getElementCoords(e){if(this.isBrowser){const t=e.getBoundingClientRect(),n=this.document.body,i=this.document.documentElement,o=t.left+(this.window.pageXOffset||i.scrollLeft||n.scrollLeft)-(i.clientLeft||n.clientLeft||0);return{top:Math.round(t.top+(this.window.pageYOffset||i.scrollTop||n.scrollTop)-(i.clientTop||n.clientTop||0)),left:Math.round(o)}}return null}get changed(){return this.event$}}return e.\u0275fac=function(t){return new(t||e)(o.ac(m.a),o.ac(i.d),o.ac(o.D))},e.\u0275prov=o.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},UnTy:function(e,t,n){"use strict";n.d(t,"a",function(){return r});var i=n("fXoL"),o=n("jhN1");let r=(()=>{class e{constructor(e){this.sanitized=e}transform(e){return this.sanitized.sanitize(1,"string"==typeof e?e.replace(/<\/?(p|div)>/g,""):e)}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(o.c))},e.\u0275pipe=i.Pb({name:"safeInnerHtml",type:e,pure:!0}),e})()},VsGz:function(e,t,n){"use strict";n.d(t,"a",function(){return l});var i=n("tyNb"),o=n("pLZG"),r=n("05l1"),a=n("lJxs"),s=n("fXoL");let l=(()=>{class e{constructor(e){this.router=e,this.url$=this.router.events.pipe(Object(o.a)(e=>e instanceof i.c),Object(r.a)(1))}init(){this.url$.connect()}get url(){return this.url$.pipe(Object(a.a)(e=>null==e?void 0:e.url))}get id(){return this.url$.pipe(Object(a.a)(e=>null==e?void 0:e.id))}get route(){return this.url$}}return e.\u0275fac=function(t){return new(t||e)(s.ac(i.e))},e.\u0275prov=s.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})()},e669:function(e,t,n){"use strict";n.d(t,"a",function(){return r});var i=n("ofXK"),o=n("fXoL");let r=(()=>{class e{}return e.\u0275mod=o.Ob({type:e}),e.\u0275inj=o.Nb({factory:function(t){return new(t||e)},imports:[[i.c]]}),e})()},gHoO:function(e,t,n){"use strict";n.d(t,"b",function(){return T}),n.d(t,"a",function(){return a}),n.d(t,"w",function(){return c}),n.d(t,"g",function(){return u}),n.d(t,"q",function(){return d}),n.d(t,"f",function(){return h}),n.d(t,"h",function(){return p}),n.d(t,"d",function(){return g}),n.d(t,"e",function(){return f}),n.d(t,"c",function(){return m}),n.d(t,"r",function(){return b}),n.d(t,"s",function(){return v}),n.d(t,"t",function(){return k}),n.d(t,"v",function(){return C}),n.d(t,"u",function(){return w}),n.d(t,"m",function(){return O}),n.d(t,"l",function(){return y}),n.d(t,"j",function(){return _}),n.d(t,"n",function(){return L}),n.d(t,"p",function(){return j}),n.d(t,"i",function(){return x}),n.d(t,"k",function(){return S}),n.d(t,"o",function(){return F}),n.d(t,"x",function(){return $});var i=n("lJxs"),o=n("05l1"),r=n("x+ZX");class a{}var s=n("lTCR"),l=n.n(s);const c=()=>l.a`
  fragment FieldsComponentFile on UploadFile {
    url
    ext
    name
    mime
    size
    previewUrl
    width
    height
    formats
    hash
    caption
  }
`,u=()=>l.a`
  fragment FieldsComponentBlocksLink on ComponentBlocksLink {
    slug
    link
    title
  }
`,d=()=>l.a`
  fragment FieldsComponentBlocksOembedMedia on ComponentBlocksOembedMedia {
    slug
    oembed
  }
`,h=()=>l.a`
  fragment FieldsComponentBlocksLabel on ComponentBlocksLabel {
    slug
    value: value_htmlinline
  }
`,p=()=>l.a`
  fragment FieldsComponentBlocksLinkFile on ComponentBlocksLinkFile {
    slug
    link
    title
    media {
      ...FieldsComponentFile
    }
  }
`,g=()=>l.a`
  fragment FieldsComponentBlocksFile on ComponentBlocksFile {
    slug
    media {
      ...FieldsComponentFile
    }
  }
`,f=()=>l.a`
  fragment FieldsComponentBlocksJson on ComponentBlocksJson {
    slug
    json
  }
`,m=()=>l.a`
  fragment FieldsComponentBlocksContent on ComponentBlocksContent {
    slug
    content: content_html
  }
`,b=()=>l.a`
  fragment FieldsComponentBlocksStandartBlock on ComponentBlocksStandartBlock {
    slug
    content: content_html
    title: title_htmlinline
  }
`,v=()=>l.a`
  fragment FieldsComponentBlocksTitleContentFile on ComponentBlocksTitleContentFile {
    slug
    content: content_html
    title: title_htmlinline
    media {
      ...FieldsComponentFile
    }
  }
`,k=()=>l.a`
  fragment FieldsComponentBlocksTitleContentFileLink on ComponentBlocksTitleContentFileLink {
    slug
    content: content_html
    title: title_htmlinline
    media {
      ...FieldsComponentFile
    }
    links {
      ...FieldsComponentBlocksLink
    }
  }
`,C=()=>l.a`
  fragment FieldsComponentBlocksTitleDescContentLinkFile on ComponentBlocksTitleDescContentLinkFile {
    slug
    content: content_html
    title: title_htmlinline
    description: description_html
    media {
      ...FieldsComponentFile
    }
    links {
      ...FieldsComponentBlocksLink
    }
  }
`,w=()=>l.a`
  fragment FieldsComponentBlocksTitleContentLink on ComponentBlocksTitleContentLink {
    slug
    content: content_html
    title: title_htmlinline
    links {
      ...FieldsComponentBlocksLink
    }
  }
`,O=()=>l.a`
  fragment FieldsComponentBlocksListLink on ComponentBlocksListLink {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksLink
    }
  }
`,y=()=>l.a`
  fragment FieldsComponentBlocksListLabel on ComponentBlocksListLabel {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksLabel
    }
  }
`,_=()=>l.a`
  fragment FieldsComponentBlocksListFile on ComponentBlocksListFile {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksFile
    }
  }
`,L=()=>l.a`
  fragment FieldsComponentBlocksListLinkFile on ComponentBlocksListLinkFile {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksLinkFile
    }
  }
`,j=()=>l.a`
  fragment FieldsComponentBlocksListTitleContentFile on ComponentBlocksListTitleContentFile {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksTitleContentFile
    }
  }
`,x=()=>l.a`
  fragment FieldsComponentBlocksListContent on ComponentBlocksListContent {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksContent
    }
  }
`,S=()=>l.a`
  fragment FieldsComponentBlocksListJson on ComponentBlocksListJson {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksJson
    }
  }
`,F=()=>l.a`
  fragment FieldsComponentBlocksListTitleContent on ComponentBlocksListTitleContent {
    slug
    title: title_html
    list {
      ...FieldsComponentBlocksStandartBlock
    }
  }
`,$=()=>l.a`
  fragment FieldsOnBlock on Block {
    slug
    name
    data {
      ...FieldsComponentBlocksLink
      ...FieldsComponentBlocksLabel
      ...FieldsComponentBlocksOembedMedia
      ...FieldsComponentBlocksJson
      ...FieldsComponentBlocksContent
      ...FieldsComponentBlocksFile
      ...FieldsComponentBlocksLinkFile
      ...FieldsComponentBlocksStandartBlock
      ...FieldsComponentBlocksTitleContentFile

      ...FieldsComponentBlocksTitleContentFileLink
      ...FieldsComponentBlocksTitleContentLink
      ...FieldsComponentBlocksTitleDescContentLinkFile

      ...FieldsComponentBlocksListLink
      ...FieldsComponentBlocksListLabel
      ...FieldsComponentBlocksListLinkFile
      ...FieldsComponentBlocksListJson
      ...FieldsComponentBlocksListFile
      ...FieldsComponentBlocksListContent
      ...FieldsComponentBlocksListTitleContent
      ...FieldsComponentBlocksListTitleContentFile
    }
  }
`;var P=n("fXoL"),B=n("NKZn");let T=(()=>{class e{constructor(e){this.langService=e,this.blocks={}}getBlock(e){return this.blocks[e]||(this.blocks[e]=this.getFragment(e)),this.blocks[e]}getFragment(e){const t="block_"+e.toLowerCase().replace(/[^a-z0-9]/g,"");return this.langService.applyFragment((e=>{const t=e.toLowerCase().replace(/[^a-z0-9-]/g,""),n=e.toLowerCase().replace(/[^a-z0-9]/g,"");return l.a`
    fragment BlocksOnLangFragment${n} on Lang {
      ${"block_"+n}: blocks (where: {slug: "${t}", isPublished: true }) {
        ...FieldsOnBlock
      }
    }

    ${$()}
    ${c()}

    ${m()}
    ${g()}
    ${f()}
    ${u()}
    ${h()}
    ${d()}
    ${p()}
    ${b()}
    ${v()}

    ${k()}
    ${w()}
    ${C()}

    ${x()}
    ${_()}
    ${S()}
    ${O()}
    ${y()}
    ${L()}
    ${F()}
    ${j()}
`})(e),t).pipe(Object(i.a)(e=>e&&e&&e instanceof Array&&e.length>0?this.prepareData(e[0]):new a),Object(o.a)(1),Object(r.a)())}prepareData(e){return Object.assign(Object.assign({},e),{data:e.data.reduce((e,t)=>("ComponentBlocksOembedMedia"===t.__typename&&(t=Object.assign(Object.assign({},t),{oembed:JSON.parse(t.oembed)})),!e[t.slug]||e[t.slug]instanceof Array?e[t.slug]instanceof Array?(e[t.slug].push(t),e):(e[t.slug]=t,e):(e[t.slug]=[e[t.slug],t],e)),{})})}}return e.\u0275fac=function(t){return new(t||e)(P.ac(B.a))},e.\u0275prov=P.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})();n("ofXK")},iTSz:function(e,t,n){"use strict";n.d(t,"a",function(){return l});var i=n("ofXK"),o=n("tyNb"),r=n("fXoL");let a=(()=>{class e{}return e.\u0275mod=r.Ob({type:e}),e.\u0275inj=r.Nb({factory:function(t){return new(t||e)},imports:[[i.c]]}),e})(),s=(()=>{class e{}return e.\u0275mod=r.Ob({type:e}),e.\u0275inj=r.Nb({factory:function(t){return new(t||e)},imports:[[i.c]]}),e})(),l=(()=>{class e{}return e.\u0275mod=r.Ob({type:e}),e.\u0275inj=r.Nb({factory:function(t){return new(t||e)},imports:[[i.c,o.h,a,s]]}),e})()},"sQ/X":function(e,t,n){"use strict";n.d(t,"a",function(){return h});var i=n("2Vo4"),o=n("XNiG"),r=n("1G5W"),a=n("pLZG"),s=n("eIep"),l=n("IzEk"),c=n("vkgz"),u=n("fXoL"),d=n("gHoO");let h=(()=>{class e{constructor(e,t){this.cdr=e,this.blockService=t,this.$isLoaded=new i.a(!1),this._isLoaded=!1,this.content$=new i.a(null),this.blockDataName$=new i.a(null),this.destroyed$=new o.a,this.block$=this.blockDataName$.pipe(Object(r.a)(this.destroyed$),Object(a.a)(e=>!!e),Object(s.a)(e=>this.blockService.getBlock(e).pipe(Object(l.a)(1),Object(c.a)(e=>{(null==e?void 0:e.data)&&(this.isLoaded=!0,this.cdr.markForCheck(),this.cdr.detectChanges())}))))}get isLoaded(){return this._isLoaded}set isLoaded(e){this._isLoaded=e,this.$isLoaded.next(e)}get valid(){return this.isLoaded}set content(e){(null==e?void 0:e.data)&&(this.isLoaded=!0),this.content$.next(e)}set blockDataName(e){this.blockDataName$.next(e)}trackByTitle(e){return e.title}trackBySlug(e){return e.slug}trackByFn(e){return e.id}ngOnInit(){console.log()}ngOnDestroy(){this.destroyed$.next(),this.destroyed$.complete(),this.content$.complete()}}return e.\u0275fac=function(t){return new(t||e)(u.Qb(u.h),u.Qb(d.b))},e.\u0275cmp=u.Kb({type:e,selectors:[["ng-component"]],hostVars:2,hostBindings:function(e,t){2&e&&u.Ib("active",t.valid)},inputs:{isLoaded:"isLoaded",content:"content",blockDataName:"blockDataName"},decls:0,vars:0,template:function(e,t){},encapsulation:2,changeDetection:0}),e})()},u2Ms:function(e,t,n){"use strict";n.r(t);var i=n("fXoL");const o={production:!0,recaptcha:"6LcC6tgUAAAAAPPxJyzMAtyH92EJi6pN0ik-Tckc",gaTrackingId:"UA-75283398-26",fbqId:null,sentryRelease:"",sentryEnvironment:"",webFontConfig:{google:{families:["Cairo:400,600,700,800:latin"]}}};var r=n("jhN1"),a=n("tyNb");const s=new i.s("Common Layout Header Component"),l=new i.s("Common Layout Footer Component"),c=new i.s("Common Layout Provider");function u(e){return[{provide:l,useValue:e}]}var d=n("1G5W"),h=n("pLZG"),p=n("XNiG"),g=n("DV3j"),f=n("Ox28"),m=n("ofXK");const b=["content"];function v(e,t){1&e&&i.Sb(0)}function k(e,t){1&e&&i.Sb(0)}function C(e,t){1&e&&i.Sb(0)}let w=(()=>{class e{constructor(e,t,n,i,o){this.router=e,this.activatedRoute=t,this.headerComponent=n,this.footerComponent=i,this.siteService=o,this.singlePageLayout=!1,this.secondLevelOnNavigation=!1,this.destroyed$=new p.a}ngOnInit(){this.checkSinglePageLayout(this.activatedRoute.snapshot),this.siteService.page.pipe(Object(d.a)(this.destroyed$)).subscribe(e=>{var t;this.secondLevelOnNavigation=!!(null===(t=null==e?void 0:e.children)||void 0===t?void 0:t.length)||!!e.parent}),this.router.events.pipe(Object(h.a)(e=>e instanceof a.d),Object(d.a)(this.destroyed$)).subscribe(()=>{this.dispose()}),this.router.events.pipe(Object(h.a)(e=>e instanceof a.b),Object(d.a)(this.destroyed$)).subscribe(e=>{this.checkSinglePageLayout(e.snapshot)})}ngOnDestroy(){this.dispose(),this.destroyed$.next()}onActivateComponent(e){this.footerExtension=e.uiFooterExtensionTemplate?e.uiFooterExtensionTemplate:void 0}hasLayoutContent(){return!!this.layoutContentRef}getClearedLayoutContentComputedStyle(){return this.clearLayoutAndGetComputedStyle(this.layoutContentRef)}checkSinglePageLayout(e){let t=e;for(;Object.prototype.hasOwnProperty.call(t,"firstChild");)t=Object.prototype.hasOwnProperty.call(t,"firstChild");this.singlePageLayout=Object.prototype.hasOwnProperty.call(t.data,"singlePageLayout")}dispose(){this.singlePageLayout=!1,this.footerExtension=void 0}clearLayoutAndGetComputedStyle(e){e.nativeElement.childNodes.forEach(e=>{e instanceof HTMLElement&&e.classList.add("not_display")});const t=Object.assign({},getComputedStyle(e.nativeElement));return e.nativeElement.childNodes.forEach(e=>{e instanceof HTMLElement&&e.classList.remove("not_display")}),t}}var t;return e.\u0275fac=function(t){return new(t||e)(i.Qb(a.e),i.Qb(a.a),i.Qb(s),i.Qb(l),i.Qb(g.a))},e.\u0275cmp=i.Kb({type:e,selectors:[["ui-common-layout"]],viewQuery:function(e,t){if(1&e&&i.zc(b,!0,i.l),2&e){let e;i.sc(e=i.fc())&&(t.layoutContentRef=e.first)}},features:[i.Db([(t=e,[{provide:c,useExisting:t}])])],decls:10,vars:7,consts:[[1,"page-wrapper"],[1,"page-wrapper__header"],[4,"ngxComponentOutlet"],[1,"page-wrapper__content"],["content",""],[3,"activate"],[1,"page-wrapper__footer"],[4,"ngTemplateOutlet"],[1,"footer-popups-wrapper"]],template:function(e,t){1&e&&(i.Wb(0,"div",0),i.Wb(1,"header",1),i.Cc(2,v,1,0,"ng-container",2),i.Vb(),i.Wb(3,"div",3,4),i.Wb(5,"router-outlet",5),i.ec("activate",function(e){return t.onActivateComponent(e)}),i.Vb(),i.Vb(),i.Wb(6,"footer",6),i.Cc(7,k,1,0,"ng-container",7),i.Cc(8,C,1,0,"ng-container",2),i.Vb(),i.Rb(9,"div",8),i.Vb()),2&e&&(i.Ib("single-page-layout",t.singlePageLayout)("page-wrapper--second-level-navigation",t.secondLevelOnNavigation),i.Eb(2),i.oc("ngxComponentOutlet",t.headerComponent),i.Eb(5),i.oc("ngTemplateOutlet",t.footerExtension),i.Eb(1),i.oc("ngxComponentOutlet",t.footerComponent))},directives:[f.a,a.i,m.p],styles:[".page-wrapper[_ngcontent-%COMP%]{overflow:hidden;display:flex;flex-direction:column;min-height:100vh;position:relative}.page-wrapper__header[_ngcontent-%COMP%]{position:relative;z-index:100;width:100%}.page-wrapper__content[_ngcontent-%COMP%]{display:flex;flex:1 1 auto;flex-direction:column}.page-wrapper__footer[_ngcontent-%COMP%]{margin-top:auto}"],changeDetection:0}),e})(),O=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c,a.h,f.b]]}),e})();var y=n("05l1"),_=n("x+ZX"),L=n("gHoO"),j=n("RF2t"),x=n("/4YZ");const S=["block"];function F(e,t){if(1&e&&(i.Wb(0,"div",6),i.Wb(1,"a",7),i.Ec(2),i.Vb(),i.Vb()),2&e){const e=t.$implicit;i.Eb(1),i.oc("routerLink",null==e?null:e.link),i.Eb(1),i.Fc(null==e?null:e.title)}}const $=[{path:"",component:w,children:[{path:"",loadChildren:()=>n.e(8).then(n.bind(null,"U2z0")).then(e=>e.MainPageV3Module),pathMatch:"full"},{path:"faq",loadChildren:()=>Promise.all([n.e(1),n.e(7)]).then(n.bind(null,"ujeN")).then(e=>e.FaqPageModule),pathMatch:"full"},{path:"whitepaper",loadChildren:()=>Promise.all([n.e(1),n.e(10)]).then(n.bind(null,"laq3")).then(e=>e.WhitepaperPageModule),pathMatch:"full"},{path:"stylesheet",loadChildren:()=>n.e(9).then(n.bind(null,"ZeBH")).then(e=>e.StylesheetPageModule),pathMatch:"full"}]},{path:"**",component:(()=>{class e{constructor(e,t,n,i){this.blocksService=e,this.cdr=t,this.siteService=n,this.boxService=i,this.animation=null,this.page=null,this.destroyed$=new p.a}ngOnInit(){this.siteService.getPageByUrl("/404").pipe(Object(d.a)(this.destroyed$),Object(y.a)(1),Object(_.a)()).subscribe(e=>{this.page=e,this.cdr.markForCheck()})}ngAfterViewInit(){}ngOnDestroy(){this.destroyed$.next()}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(L.b),i.Qb(i.h),i.Qb(g.a),i.Qb(j.a))},e.\u0275cmp=i.Kb({type:e,selectors:[["app-not-found-page"]],viewQuery:function(e,t){if(1&e&&i.Hc(S,!0),2&e){let e;i.sc(e=i.fc())&&(t.block=e.first)}},decls:7,vars:4,consts:[["id","errorage",1,"error-page","default-typography","default-theme"],["block",""],[1,"error-page__inner"],[1,"error-page__box"],[3,"innerHTML"],["class","error-page__action",4,"ngFor","ngForOf"],[1,"error-page__action"],[1,"button",3,"routerLink"]],template:function(e,t){1&e&&(i.Wb(0,"div",0,1),i.Wb(2,"div",2),i.Wb(3,"div",3),i.Rb(4,"h3",4),i.jc(5,"safeHtml"),i.Cc(6,F,3,2,"div",5),i.Vb(),i.Vb(),i.Vb()),2&e&&(i.Eb(4),i.oc("innerHTML",i.kc(5,2,null==t.page?null:t.page.title),i.wc),i.Eb(2),i.oc("ngForOf",null==t.page?null:t.page.actions))},directives:[m.k,a.g],pipes:[x.a],styles:[".error-page[_ngcontent-%COMP%]{max-height:100%;max-width:100%;overflow:hidden}.error-page__inner[_ngcontent-%COMP%]{position:relative;align-items:center;display:flex;height:100vh;height:calc(var(--vh, 1vh) * 100);justify-content:center;width:100vw}.error-page__box[_ngcontent-%COMP%]{position:relative;margin-bottom:rem(60)}.error-page__action[_ngcontent-%COMP%]{margin-top:rem(10)}.error-page__action[_ngcontent-%COMP%]   .button[_ngcontent-%COMP%]{color:#fff}"],changeDetection:0}),e})()}];let P=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[a.h.forRoot($,{initialNavigation:"enabled",relativeLinkResolution:"legacy",enableTracing:!1})],a.h]}),e})();var B=n("+1kA"),T=n("zP0r"),M=n("z/o8"),E=n("lCAa"),I=n("LR87"),N=n("VsGz"),D=n("2Vo4"),V=n("Kj3r"),z=n("lJxs");let R=(()=>{class e{constructor(e,t,n){this.router=e,this.route=t,this.boxService=n,this.routerDataCssVariables$=new D.a({}),this.routerData$=new D.a({}),this.changeLayout(),this.routerData$.next(this.findChildData(this.route.snapshot)),this.routerDataCssVariables$.next(this.findChildDataCssVariables(this.route.snapshot))}changeLayout(){this.router.events.pipe(Object(h.a)(e=>e instanceof a.b),Object(V.a)(0),Object(z.a)(e=>e.snapshot)).subscribe(e=>{this.routerData$.next(this.findChildData(this.route.snapshot)),this.routerDataCssVariables$.next(this.findChildDataCssVariables(this.route.snapshot))})}findChildData(e){let t=e;for(;t.firstChild;)t=t.firstChild;return t.data}findChildDataCssVariables(e){let t=e;for(;t.firstChild;)t=t.firstChild;return t.data.cssVariables?t.data.cssVariables:{}}get layout(){return this.routerData$.value.layout}get cssVariables(){return this.routerData$.value.cssVariables}set data(e){this.routerData$.next(e)}subscriptionLayout(){return this.routerData$.pipe(Object(z.a)(e=>e.layout))}subscriptionCssVariables(){return this.routerDataCssVariables$.pipe(Object(z.a)(e=>e))}subscription(){return this.routerData$}}return e.\u0275fac=function(t){return new(t||e)(i.ac(a.e),i.ac(a.a),i.ac(j.a))},e.\u0275prov=i.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})();var A=n("NKZn");let W=(()=>{class e{constructor(e,t){this.document=e,this.langService=t}init(){var e;const t=null===(e=this.document)||void 0===e?void 0:e.body;t&&this.langService.lang.subscribe(e=>{t.setAttribute("lang",e)})}}return e.\u0275fac=function(t){return new(t||e)(i.ac(m.d),i.ac(A.a))},e.\u0275prov=i.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})();var H=n("zM63"),Q=n("qrii"),X=n("akRu");let U=(()=>{class e extends X.b{constructor(e){super(e)}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(m.d))},e.\u0275cmp=i.Kb({type:e,selectors:[["ui-taiga-default-theme"]],features:[i.Bb],decls:0,vars:0,template:function(e,t){},styles:[':root{--tui-primary-text:#000;--tui-primary:var(--color-yellow);--tui-primary-hover:#d29700;--tui-primary-active:#d29700;--tui-link:var(--tui-primary);--tui-link-hover:var(--tui-primary-hover);--tui-selection:#ffe8a8;--tui-font-heading:"Cairo","Arial","Times New Roman",Times,serif;--tui-font-text:"Cairo","Arial","Times New Roman",Times,serif;--tui-heading-font:"Cairo","Arial","Times New Roman",Times,serif;--tui-text-font:"Cairo","Arial","Times New Roman",Times,serif;--tui-font-heading:var(--tui-heading-font);--tui-font-heading-1:bold 3.125rem/1.3 var(--tui-font-heading);--tui-font-heading-2:bold 2.625rem/1.4 var(--tui-font-heading);--tui-font-heading-3:bold 2.25rem/1.4 var(--tui-font-heading);--tui-font-heading-4:bold 1.75rem/2rem var(--tui-font-heading);--tui-font-heading-5:bold 1.5rem/1.75rem var(--tui-font-heading);--tui-font-heading-6:bold 1.25rem/1.5rem var(--tui-font-heading);--tui-font-text:var(--tui-text-font);--tui-font-text-xl:normal 1.1875rem/1.75rem var(--tui-font-text);--tui-font-text-l:normal 1.0625rem/1.5rem var(--tui-font-text);--tui-font-text-m:normal 0.9375rem/1.5rem var(--tui-font-text);--tui-font-text-s:normal 0.8125rem/1.25rem var(--tui-font-text);--tui-font-text-xs:normal 0.6875rem/1rem var(--tui-font-text)}@media (max-width:1024px){:root{--tui-font-heading-1:bold 2rem/1.3 var(--tui-font-heading);--tui-font-heading-2:bold 2rem/1.4 var(--tui-font-heading);--tui-font-heading-3:bold 1.5rem/1.4 var(--tui-font-heading)}}@media (max-width:768px){:root{--tui-font-heading-1:bold 1.5rem/1.3 var(--tui-font-heading);--tui-font-heading-2:bold 1.5rem/1.4 var(--tui-font-heading);--tui-font-heading-3:bold 1.25rem/1.4 var(--tui-font-heading)}}'],encapsulation:2,changeDetection:0}),e})(),K=(()=>{class e{constructor(e,t,n,i,o,r,a,s,l){this.route=e,this.router=t,this.seoService=n,this.urlService=i,this.boxService=o,this.siteService=r,this.routeDataService=a,this.window=s,this.rootAttrsService=l,this.destroyed$=new p.a,M.a.registerPlugin(E.a),this.urlService.init(),this.siteService.site.pipe(Object(d.a)(this.destroyed$)).subscribe(e=>{this.seoService.addSeoData(e.seo)})}updateCssVariableVh(){this.boxService.isBrowser&&document.documentElement.style.setProperty("--vh",.01*this.window.innerHeight+"px")}pageScrollTop(){this.boxService.isBrowser&&this.router.events.subscribe(e=>{e instanceof a.c&&M.a.to(this.window,{duration:.25,scrollTo:0,onComplete:()=>{}})})}ngOnInit(){this.rootAttrsService.init(),this.pageScrollTop(),this.updateCssVariableVh(),this.boxService.subscription.pipe(Object(d.a)(this.destroyed$),Object(T.a)(1)).subscribe(()=>{this.updateCssVariableVh()})}ngOnDestroy(){this.destroyed$.next()}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(a.a),i.Qb(a.e),i.Qb(I.a),i.Qb(N.a),i.Qb(j.a),i.Qb(g.a),i.Qb(R),i.Qb(B.a),i.Qb(W))},e.\u0275cmp=i.Kb({type:e,selectors:[["app-root"]],decls:4,vars:0,template:function(e,t){1&e&&(i.Rb(0,"ng-progress"),i.Wb(1,"tui-root"),i.Rb(2,"ui-taiga-default-theme"),i.Rb(3,"router-outlet"),i.Vb())},directives:[H.b,Q.a,U,a.i],styles:[""]}),e})();var q=n("ALmS"),J=n("HuS5"),Z=n("j9u/");function G(e){function t(e,t){const n=(65535&e)+(65535&t);return(e>>16)+(t>>16)+(n>>16)<<16|65535&n}function n(e,t){return e>>>t|e<<32-t}function i(e,t){return e>>>t}function o(e,t,n){return e&t^~e&n}function r(e,t,n){return e&t^e&n^t&n}function a(e){return n(e,2)^n(e,13)^n(e,22)}function s(e){return n(e,6)^n(e,11)^n(e,25)}function l(e){return n(e,7)^n(e,18)^i(e,3)}return function(e){let t="";for(let n=0;n<4*e.length;n++)t+="0123456789abcdef".charAt(e[n>>2]>>8*(3-n%4)+4&15)+"0123456789abcdef".charAt(e[n>>2]>>8*(3-n%4)&15);return t}(function(e,c){const u=new Array(1116352408,1899447441,3049323471,3921009573,961987163,1508970993,2453635748,2870763221,3624381080,310598401,607225278,1426881987,1925078388,2162078206,2614888103,3248222580,3835390401,4022224774,264347078,604807628,770255983,1249150122,1555081692,1996064986,2554220882,2821834349,2952996808,3210313671,3336571891,3584528711,113926993,338241895,666307205,773529912,1294757372,1396182291,1695183700,1986661051,2177026350,2456956037,2730485921,2820302411,3259730800,3345764771,3516065817,3600352804,4094571909,275423344,430227734,506948616,659060556,883997877,958139571,1322822218,1537002063,1747873779,1955562222,2024104815,2227730452,2361852424,2428436474,2756734187,3204031479,3329325298),d=new Array(1779033703,3144134277,1013904242,2773480762,1359893119,2600822924,528734635,1541459225),h=new Array(64);let p,g,f,m,b,v,k,C,w,O;e[c>>5]|=128<<24-c%32,e[15+(c+64>>9<<4)]=c;for(let _=0;_<e.length;_+=16){p=d[0],g=d[1],f=d[2],m=d[3],b=d[4],v=d[5],k=d[6],C=d[7];for(let c=0;c<64;c++)h[c]=c<16?e[c+_]:t(t(t(n(y=h[c-2],17)^n(y,19)^i(y,10),h[c-7]),l(h[c-15])),h[c-16]),w=t(t(t(t(C,s(b)),o(b,v,k)),u[c]),h[c]),O=t(a(p),r(p,g,f)),C=k,k=v,v=b,b=t(m,w),m=f,f=g,g=p,p=t(w,O);d[0]=t(p,d[0]),d[1]=t(g,d[1]),d[2]=t(f,d[2]),d[3]=t(m,d[3]),d[4]=t(b,d[4]),d[5]=t(v,d[5]),d[6]=t(k,d[6]),d[7]=t(C,d[7])}var y;return d}(function(e){const t=Array();for(let n=0;n<8*e.length;n+=8)t[n>>5]|=(255&e.charCodeAt(n/8))<<24-n%32;return t}(e=function(e){e=e.replace(/\r\n/g,"\n");let t="";for(let n=0;n<e.length;n++){const i=e.charCodeAt(n);i<128?t+=String.fromCharCode(i):i>127&&i<2048?(t+=String.fromCharCode(i>>6|192),t+=String.fromCharCode(63&i|128)):(t+=String.fromCharCode(i>>12|224),t+=String.fromCharCode(i>>6&63|128),t+=String.fromCharCode(63&i|128))}return t}(e)),8*e.length))}var Y=n("/IUn"),ee=n("E21e");const te=Object(r.h)("apollo.state");let ne=(()=>{class e{constructor(e,t,n,i){this.apollo=e,this.transferState=t,this.httpLink=n,this.platformId=i;const o=Object(m.t)(i),r=this.httpLink.create({uri:"/graphql"}),a=Object(Z.a)({sha256:G,useGETForHashedQueries:!1});this.link=q.ApolloLink.from([a,this.getErrorLink(),r]),this.cache=this.getCache(),this.apollo.create(Object.assign({link:this.link,cache:this.cache},o?{}:{ssrMode:!0})),o?this.onBrowser():this.onServer()}getCache(){return new q.InMemoryCache}getErrorLink(){return Object(J.a)(({graphQLErrors:e,networkError:t})=>{e&&e.map(({message:e,locations:t,path:n})=>{"PersistedQueryNotFound"!==e&&console.log(`[GraphQL error]: Message: ${e}, Location: ${t}, Path: ${n}`)}),t&&console.log(`[Network error]: ${t}`)})}getDefaultOption(){return{watchQuery:{fetchPolicy:"cache-and-network",errorPolicy:"all"},query:{fetchPolicy:"cache-and-network",errorPolicy:"all"},mutate:{errorPolicy:"all"}}}onServer(){this.transferState.onSerialize(te,()=>this.cache.extract())}onBrowser(){const e=this.transferState.get(te,null);this.cache.restore(e)}}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)(i.ac(Y.a),i.ac(r.g),i.ac(ee.a),i.ac(i.D))}}),e})();var ie=n("tk/3");let oe=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c]]}),e})();var re=n("sQ/X"),ae=n("Nh5a"),se=n("UnTy");function le(e,t){if(1&e&&(i.Ub(0),i.Wb(1,"ui-link",6),i.Rb(2,"img",7),i.Rb(3,"span",8),i.jc(4,"safeInnerHtml"),i.Vb(),i.Tb()),2&e){const e=t.$implicit;i.Eb(1),i.oc("href",null==e?null:e.link)("disabled","#"===(null==e?null:e.link)),i.Eb(1),i.Fb("src","assets/icons/"+(null==e?null:e.slug)+".svg",i.xc),i.Eb(1),i.oc("innerHTML",i.kc(4,4,null==e?null:e.title),i.wc)}}function ce(e,t){if(1&e&&(i.Wb(0,"ui-link",10),i.Rb(1,"span",8),i.jc(2,"safeInnerHtml"),i.Vb()),2&e){const e=i.ic().$implicit;i.oc("href",null==e?null:e.link),i.Eb(1),i.oc("innerHTML",i.kc(2,2,null==e?null:e.title),i.wc)}}function ue(e,t){if(1&e&&(i.Ub(0),i.Cc(1,ce,3,4,"ui-link",9),i.Tb()),2&e){const e=t.$implicit;i.Eb(1),i.oc("ngIf","#"!==(null==e?null:e.link))}}function de(e,t){if(1&e&&(i.Wb(0,"div",1),i.Wb(1,"div",2),i.Wb(2,"div",3),i.Cc(3,le,5,6,"ng-container",4),i.Vb(),i.Wb(4,"div",5),i.Cc(5,ue,2,1,"ng-container",4),i.Vb(),i.Vb(),i.Vb()),2&e){const e=t.ngIf,n=i.ic();i.Eb(3),i.oc("ngForOf",null==e||null==e.data||null==e.data.socials?null:e.data.socials.list)("ngForTrackBy",n.trackByFn),i.Eb(2),i.oc("ngForOf",null==e||null==e.data||null==e.data.links?null:e.data.links.list)("ngForTrackBy",n.trackByFn)}}let he=(()=>{class e extends re.a{constructor(e,t){super(e,t),this.cdr=e,this.blockService=t,this.year=(new Date).getFullYear()+"",this.blockDataName$.next("footer")}toggleItems(){}ngOnInit(){super.ngOnInit()}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(i.h),i.Qb(L.b))},e.\u0275cmp=i.Kb({type:e,selectors:[["ui-footer"]],features:[i.Bb],decls:2,vars:3,consts:[["class","footer",4,"ngIf"],[1,"footer"],[1,"footer__layout","layout"],[1,"footer__social"],[4,"ngFor","ngForOf","ngForTrackBy"],[1,"footer__links"],[1,"link",3,"href","disabled"],[1,"link__icon"],[1,"link__text",3,"innerHTML"],["class","link",3,"href",4,"ngIf"],[1,"link",3,"href"]],template:function(e,t){1&e&&(i.Cc(0,de,6,4,"div",0),i.jc(1,"async")),2&e&&i.oc("ngIf",i.kc(1,1,t.block$))},directives:[m.l,m.k,ae.a],pipes:[m.b,se.a],styles:["[_nghost-%COMP%]{display:block;position:relative;z-index:1;padding:2.5rem 0 2.75rem;font-size:12px;line-height:1.3333333333}@media (max-width:1024px){[_nghost-%COMP%]{padding:3.125rem 0 1.25rem}}@media (max-width:768px){[_nghost-%COMP%]{padding:2.25rem 0}}.footer[_ngcontent-%COMP%]{--links-gap:60px;color:#525252}.footer__layout[_ngcontent-%COMP%]{display:flex}@media (min-width:769px){.footer__layout[_ngcontent-%COMP%]{align-items:center;justify-content:center}}@media (max-width:768px){.footer__layout[_ngcontent-%COMP%]{justify-content:space-between}}.footer[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]{display:inline-flex;align-items:center}.footer[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]     a{color:#525252;text-decoration:none}.footer[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]     a:hover{text-decoration:underline}.footer[_ngcontent-%COMP%]   .link__icon[_ngcontent-%COMP%]{max-height:20px;width:auto;margin-right:10px}.footer__links[_ngcontent-%COMP%]{font-size:16px;line-height:1}@media (min-width:769px){.footer__links[_ngcontent-%COMP%]{display:flex;align-items:center}}@media (max-width:768px){.footer__links[_ngcontent-%COMP%]{display:flex;flex-direction:column}}@media (min-width:769px){.footer__links[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]{margin-right:var(--links-gap)}.footer__links[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]:last-child{margin-right:0}}@media (max-width:768px){.footer__links[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]{margin-bottom:10px}.footer__links[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]:last-child{margin-bottom:0}}.footer__social[_ngcontent-%COMP%]{font-size:.9375rem;line-height:1.3}@media (min-width:769px){.footer__social[_ngcontent-%COMP%]{margin-right:var(--links-gap);display:flex;align-items:center}}@media (max-width:768px){.footer__social[_ngcontent-%COMP%]{margin-right:20px;display:flex;flex-direction:column}}@media (min-width:769px){.footer__social[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]{margin-right:var(--links-gap)}.footer__social[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]:last-child{margin-right:0}}@media (max-width:768px){.footer__social[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]{margin-bottom:10px}.footer__social[_ngcontent-%COMP%]   .link[_ngcontent-%COMP%]:last-child{margin-bottom:0}}"],changeDetection:0}),e})();var pe=n("e669");let ge=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c]]}),e})();var fe=n("iTSz");let me=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c,a.h,pe.a,ge,fe.a]]}),e})();var be=n("a4Kx");let ve=(()=>{class e{constructor(e,t){this.request=e,this.platformId=t,this.isBrowser=Object(m.t)(t)}intercept(e,t){return t.handle(e)}}return e.\u0275fac=function(t){return new(t||e)(i.ac(be.a,8),i.ac(i.D))},e.\u0275prov=i.Mb({token:e,factory:e.\u0275fac}),e})();var ke=n("9YtQ");let Ce=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c,a.h,pe.a]]}),e})();var we=n("pc+1"),Oe=n("Haw6");const ye=["container"];function _e(e,t){if(1&e&&(i.Wb(0,"ui-link",12),i.Rb(1,"span",13),i.jc(2,"safeInnerHtml"),i.Vb()),2&e){const e=i.ic().$implicit;i.oc("href",null==e?null:e.link),i.Eb(1),i.oc("innerHTML",i.kc(2,2,null==e?null:e.title),i.wc)}}function Le(e,t){if(1&e&&(i.Ub(0),i.Cc(1,_e,3,4,"ui-link",11),i.Tb()),2&e){const e=t.$implicit;i.Eb(1),i.oc("ngIf","#"!==(null==e?null:e.link))}}function je(e,t){if(1&e&&(i.Ub(0),i.Cc(1,Le,2,1,"ng-container",10),i.Tb()),2&e){const e=t.ngIf,n=i.ic();i.Eb(1),i.oc("ngForOf",null==e||null==e.data||null==e.data.menu?null:e.data.menu.list)("ngForTrackBy",n.trackByFn)}}const xe=function(){return["/"]};let Se=(()=>{class e extends re.a{constructor(e,t,n,i,o){super(n,o),this.el=e,this.boxService=t,this.cdr=n,this.document=i,this.blockService=o,this.containerIsUp=!0,this.blockDataName$.next("header")}updateCssVariable(){let e=0;this.el&&this.container.nativeElement&&this.boxService.isBrowser&&(this.cdr.markForCheck(),this.cdr.detectChanges(),e=this.container.nativeElement.offsetHeight,this.document.documentElement.style.setProperty("--header-height",e+"px"))}createSecondLevelScrollTrigger(){if(this.boxService.isBrowser&&!this.scrollTrigger){const e=this;we.w.registerPlugin(Oe.a),this.lastScrollTop=this.boxService.document.documentElement.scrollTop,e.scrollTrigger=Oe.a.create({trigger:".page-wrapper",start:0,end:1e4,onUpdate:t=>{e.boxService.document.documentElement.scrollTop<200&&e.container.nativeElement.classList.contains("header--white")&&(e.scrollTriggerTl=we.w.timeline().to(e.container.nativeElement,{duration:0,className:"header"}).to(e.container.nativeElement,{duration:.5,y:"0%"})),e.boxService.document.documentElement.scrollTop>=200&&(-1!==t.direction||e.containerIsUp?1===t.direction&&e.containerIsUp&&(e.containerIsUp=!1,e.scrollTriggerTl=we.w.timeline().set(e.container.nativeElement,{duration:0,className:"header header--white"}).fromTo(e.container.nativeElement,{duration:.5,y:"-100%"},{y:"0%"})):(e.containerIsUp=!0,e.scrollTriggerTl=we.w.timeline({onComplete:()=>{}}).to(e.container.nativeElement,{duration:0,className:"header header--white"}).fromTo(e.container.nativeElement,{y:"0%"},{duration:.5,y:"-100%"})))}})}}ngOnInit(){super.ngOnInit(),this.boxService.subscription.pipe(Object(d.a)(this.destroyed$),Object(T.a)(1)).subscribe(()=>{this.updateCssVariable()})}ngAfterViewInit(){this.updateCssVariable(),this.createSecondLevelScrollTrigger()}}return e.\u0275fac=function(t){return new(t||e)(i.Qb(i.l),i.Qb(j.a),i.Qb(i.h),i.Qb(m.d),i.Qb(L.b))},e.\u0275cmp=i.Kb({type:e,selectors:[["ui-header"]],viewQuery:function(e,t){if(1&e&&i.Hc(ye,!0),2&e){let e;i.sc(e=i.fc())&&(t.container=e.first)}},features:[i.Bb],decls:11,vars:5,consts:[[1,"header"],["container",""],[1,"header__layout","layout"],[1,"header_lc"],[1,"header__logo",3,"routerLink"],["xmlns","http://www.w3.org/2000/svg","width","45","height","45","viewBox","0 0 45 45","fill","none"],["cx","22.5","cy","22.5","r","22.5"],["d","M14.267 35.084h16.467v-4.458H19.372v-6.04h8.437v-4.243H14.267v14.741ZM30.662 9.916H14.267v4.458h16.395V9.916Z"],[1,"header_rc"],[4,"ngIf"],[4,"ngFor","ngForOf","ngForTrackBy"],["class","header__link",3,"href",4,"ngIf"],[1,"header__link",3,"href"],[1,"header__link-text",3,"innerHTML"]],template:function(e,t){1&e&&(i.Wb(0,"div",0,1),i.Wb(2,"div",2),i.Wb(3,"div",3),i.Wb(4,"a",4),i.hc(),i.Wb(5,"svg",5),i.Rb(6,"circle",6),i.Rb(7,"path",7),i.Vb(),i.Vb(),i.Vb(),i.gc(),i.Wb(8,"div",8),i.Cc(9,je,2,2,"ng-container",9),i.jc(10,"async"),i.Vb(),i.Vb(),i.Vb()),2&e&&(i.Eb(4),i.oc("routerLink",i.qc(4,xe)),i.Eb(5),i.oc("ngIf",i.kc(10,2,t.block$)))},directives:[a.g,m.l,m.k,ae.a],pipes:[m.b,se.a],styles:[".header{position:absolute;display:block;left:0;top:0;width:100%;z-index:1;--color-bg:transparent;--color-text:#fff;--color-logo-text:#000;--color-logo-bg:#fff;padding:1.125rem 0;background:var(--color-bg);border-bottom:1px solid transparent}.header--white{position:fixed;--color-bg:#fff;--color-text:#000;--color-logo-text:#fff;--color-logo-bg:#000;border-bottom:1px solid #dbebf6}@media (max-width:1024px){.header{padding:.9375rem 0}}@media (max-width:768px){.header{padding:.9375rem 0}}.header__layout,.header_rc{display:flex}.header_rc{margin-left:auto;align-items:center}.header__logo svg{width:2.8125rem;height:auto}.header__logo svg circle{fill:var(--color-logo-bg)}.header__logo svg path{fill:var(--color-logo-text)}@media (max-width:1024px){.header__logo svg{width:2.5rem}}@media (max-width:768px){.header__logo svg{width:2.1875rem}}.header__link{margin-right:55px;font-size:1rem;line-height:1;font-weight:700}@media (max-width:1024px){.header__link{font-size:1rem;margin-right:35px;font-size:.875rem;margin-right:25px}}.header__link a{color:var(--color-text);text-decoration:none;transition:color .15s ease-in-out}.header__link a:hover{color:var(--color-yellow)}.header__link a:active,.header__link a:focus{color:var(--color-yellow-hover)}"],encapsulation:2,changeDetection:0}),e})();var Fe=n("NEux");let $e=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},imports:[[m.c,Fe.b,a.h,fe.a,pe.a]]}),e})();class Pe{constructor(e,t){this.langList=["ru","en"],this.defaultLang="en",this.baseHref="/";const n=this.getPrefixLang(e?e.url:t.location.pathname);this.setBaseHref(n),this.setDefaultLang(n)}setDefaultLang(e){this.defaultLang=e}getDefaultLang(){return this.defaultLang}getPrefixLang(e){const t=e.split("/")[1];return this.langList.indexOf(t)>=0?t:this.defaultLang}setBaseHref(e){this.baseHref=e!==this.defaultLang?"/"+e+"/":"/"}getBaseHref(){return this.baseHref}}var Be=n("+H4W"),Te=n("sjCC"),Me=n("5KMX"),Ee=n("R1ws"),Ie=n("e/9f");let Ne=(()=>{class e{}return e.\u0275mod=i.Ob({type:e}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)}}),e})();function De(e){return()=>e.init()}let Ve=(()=>{class e{constructor(e,t){this.platformId=e,this.projectWebFontConfig=t}init(){this.initWebFont()}initWebFont(){Object(m.u)(this.platformId)||this.projectWebFontConfig&&n.e(11).then(n.t.bind(null,"J9Y1",7)).then(e=>{e.load(this.projectWebFontConfig)})}}return e.\u0275fac=function(t){return new(t||e)(i.ac(i.D),i.ac("projectWebFontConfig"))},e.\u0275prov=i.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})(),ze=(()=>{class e{}var t;return e.\u0275mod=i.Ob({type:e,bootstrap:[K]}),e.\u0275inj=i.Nb({factory:function(t){return new(t||e)},providers:[B.b,{provide:i.w,useValue:"en-GB"},(t=Se,[{provide:s,useValue:t}]),u(he),{provide:ie.a,useClass:ve,multi:!0},{provide:"projectWebFontConfig",useValue:o.webFontConfig},{provide:i.d,useFactory:De,multi:!0,deps:[Ve,"projectWebFontConfig"]},{provide:m.a,useFactory:(e,t)=>new Pe(e,t).getBaseHref(),deps:[[new i.C,new i.q(be.a)],[new i.C,new i.q(B.a)]]}],imports:[[r.a.withServerTransition({appId:"app-root"}),r.b,P,ne,ie.c,O,me,$e,oe,ge,ke.a.forRoot(),Ce,H.c,Be.a,Te.a,Me.a,Q.b,Ee.a,Ne,Ie.a.forRoot({trackingId:o.gaTrackingId,trackPageviews:!1,debug:!o.production})]]}),e})();o.production&&(Object(i.X)(),window&&(window.console.log=window.console.debug=window.console.warn=window.console.info=()=>{})),document.addEventListener("DOMContentLoaded",()=>{requestAnimationFrame(()=>{r.i().bootstrapModule(ze).catch(e=>console.error(e))})})}},[[0,0,6]]]);
