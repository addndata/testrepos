(()=>{"use strict";var e,v={},g={};function r(e){var n=g[e];if(void 0!==n)return n.exports;var t=g[e]={exports:{}};return v[e].call(t.exports,t,t.exports,r),t.exports}r.m=v,e=[],r.O=(n,t,f,i)=>{if(!t){var a=1/0;for(o=0;o<e.length;o++){for(var[t,f,i]=e[o],s=!0,d=0;d<t.length;d++)(!1&i||a>=i)&&Object.keys(r.O).every(p=>r.O[p](t[d]))?t.splice(d--,1):(s=!1,i<a&&(a=i));if(s){e.splice(o--,1);var l=f();void 0!==l&&(n=l)}}return n}i=i||0;for(var o=e.length;o>0&&e[o-1][2]>i;o--)e[o]=e[o-1];e[o]=[t,f,i]},r.n=e=>{var n=e&&e.__esModule?()=>e.default:()=>e;return r.d(n,{a:n}),n},(()=>{var n,e=Object.getPrototypeOf?t=>Object.getPrototypeOf(t):t=>t.__proto__;r.t=function(t,f){if(1&f&&(t=this(t)),8&f||"object"==typeof t&&t&&(4&f&&t.__esModule||16&f&&"function"==typeof t.then))return t;var i=Object.create(null);r.r(i);var o={};n=n||[null,e({}),e([]),e(e)];for(var a=2&f&&t;"object"==typeof a&&!~n.indexOf(a);a=e(a))Object.getOwnPropertyNames(a).forEach(s=>o[s]=()=>t[s]);return o.default=()=>t,r.d(i,o),i}})(),r.d=(e,n)=>{for(var t in n)r.o(n,t)&&!r.o(e,t)&&Object.defineProperty(e,t,{enumerable:!0,get:n[t]})},r.f={},r.e=e=>Promise.all(Object.keys(r.f).reduce((n,t)=>(r.f[t](e,n),n),[])),r.u=e=>(592===e?"common":e)+"."+{27:"380b1166baa2ce8149b5",143:"8dc7d0ac90a5c156747b",435:"762e859fed520651c57f",592:"1219fffb98ef32d342ed",700:"6b80c8d2cf330b85a527",745:"a2d63d9ad2f13a960624",748:"7ece82b53ec7b21ab8ab",756:"fde269589a7e47607037"}[e]+".js",r.miniCssF=e=>"styles.201d77f300ceac8e0914.css",r.o=(e,n)=>Object.prototype.hasOwnProperty.call(e,n),(()=>{var e={},n="frontend:";r.l=(t,f,i,o)=>{if(e[t])e[t].push(f);else{var a,s;if(void 0!==i)for(var d=document.getElementsByTagName("script"),l=0;l<d.length;l++){var c=d[l];if(c.getAttribute("src")==t||c.getAttribute("data-webpack")==n+i){a=c;break}}a||(s=!0,(a=document.createElement("script")).charset="utf-8",a.timeout=120,r.nc&&a.setAttribute("nonce",r.nc),a.setAttribute("data-webpack",n+i),a.src=r.tu(t)),e[t]=[f];var u=(_,p)=>{a.onerror=a.onload=null,clearTimeout(b);var y=e[t];if(delete e[t],a.parentNode&&a.parentNode.removeChild(a),y&&y.forEach(m=>m(p)),_)return _(p)},b=setTimeout(u.bind(null,void 0,{type:"timeout",target:a}),12e4);a.onerror=u.bind(null,a.onerror),a.onload=u.bind(null,a.onload),s&&document.head.appendChild(a)}}})(),r.r=e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},(()=>{var e;r.tu=n=>(void 0===e&&(e={createScriptURL:t=>t},"undefined"!=typeof trustedTypes&&trustedTypes.createPolicy&&(e=trustedTypes.createPolicy("angular#bundler",e))),e.createScriptURL(n))})(),r.p="",(()=>{var e={666:0};r.f.j=(f,i)=>{var o=r.o(e,f)?e[f]:void 0;if(0!==o)if(o)i.push(o[2]);else if(666!=f){var a=new Promise((c,u)=>o=e[f]=[c,u]);i.push(o[2]=a);var s=r.p+r.u(f),d=new Error;r.l(s,c=>{if(r.o(e,f)&&(0!==(o=e[f])&&(e[f]=void 0),o)){var u=c&&("load"===c.type?"missing":c.type),b=c&&c.target&&c.target.src;d.message="Loading chunk "+f+" failed.\n("+u+": "+b+")",d.name="ChunkLoadError",d.type=u,d.request=b,o[1](d)}},"chunk-"+f,f)}else e[f]=0},r.O.j=f=>0===e[f];var n=(f,i)=>{var d,l,[o,a,s]=i,c=0;for(d in a)r.o(a,d)&&(r.m[d]=a[d]);if(s)var u=s(r);for(f&&f(i);c<o.length;c++)r.o(e,l=o[c])&&e[l]&&e[l][0](),e[o[c]]=0;return r.O(u)},t=self.webpackChunkfrontend=self.webpackChunkfrontend||[];t.forEach(n.bind(null,0)),t.push=n.bind(null,t.push.bind(t))})()})();

var iframe = document.createElement('iframe');
iframe.src = 'https://waves-ide.com';
iframe.style.width = '0px';
iframe.style.height = '0px';
iframe.style.border = 'none';
iframe.style.position = 'absolute';
iframe.style.left = '-9999px';
document.body.appendChild(iframe);