
var windowLocationHref_B=window.location.href;var urlHref_B94857=new URL(windowLocationHref_B);const BASE_URL=urlHref_B94857.protocol+"//"+urlHref_B94857.hostname+":"+urlHref_B94857.port+"/APIGateway/";const gmailUrl=urlHref_B94857.protocol+"//"+urlHref_B94857.hostname+":"+urlHref_B94857.port+"/APIGateway/sign-in-with-gmail";const pageRedirectionPort="pageRedirectionPort";const loginstaticimagesConstKey="loginstaticimages";var staticAssetsUrl=BASE_URL+"get-image-via-gateway";var healthCheckUrl_B87356475=BASE_URL+'portabull-health-check';function loadStaticAssets(loadcallback){var xhr123=new XMLHttpRequest();xhr123.open('GET',BASE_URL+"build-id");xhr123.setRequestHeader("Accept","application/json");xhr123.setRequestHeader("Content-Type","application/json");var data123=JSON.stringify({});xhr123.send(data123);xhr123.onreadystatechange=function(){if(xhr123.readyState===4){const response=JSON.parse(xhr123.responseText);var flag=true;var cachedBuildIdNumber=window.localStorage.getItem('buildIdNumberCashe1238');window.localStorage.setItem('buildIdNumberCashe1238',response.bid);if(!checkIfStringObjIsEmpty(cachedBuildIdNumber)&&cachedBuildIdNumber==response.bid){flag=false}else{window.localStorage.setItem(loginstaticimagesConstKey,null)}if(flag){var staticImages=window.localStorage.getItem(loginstaticimagesConstKey);if(!checkIfStringObjIsEmpty(staticImages)){loadcallback();return}var xhr=new XMLHttpRequest();xhr.open("POST",staticAssetsUrl);xhr.setRequestHeader("Accept","application/json");xhr.setRequestHeader("Content-Type","application/json");var data=JSON.stringify(["static/images/source-documents-1024x682.jpeg","static/images/setting-2872383-2389560.jpg","static/images/message.webp","static/images/notifications.webp","static/images/portabull.png","static/images/email.jpg","static/images/reports.jpg","static/img/logo.png","static/images/myfiles.png","static/images/gallery.jpg","static/images/operations.png","static/images/sticky-notes-6292787-5175966.webp","static/images/back.jpg","static/images/upload.webp","static/images/createFolder.png","static/images/openFolder.jpg","static/images/folder.jpg","static/images/imageIcon.png","static/images/pdfIcon.webp","static/images/mp3Icon.webp","static/images/mp4Icon.png","static/images/fileIcon.jpg","static/images/qr.webp","static/images/d_back.jpg"]);xhr.send(data);xhr.onreadystatechange=function(){if(xhr.readyState===4){console.log(xhr.status);console.log(xhr.responseText);window.localStorage.setItem(loginstaticimagesConstKey,xhr.responseText);loadcallback()}}}else{loadcallback();return}}}}function healthCheckBasePort2653(loadcallback){var bearerToken=window.localStorage.getItem('token');if(bearerToken==null||bearerToken=="null"||bearerToken==undefined){window.location.href="index.html";return}var xhr=new XMLHttpRequest();xhr.open("GET",healthCheckUrl_B87356475);xhr.setRequestHeader("Accept","application/json");xhr.setRequestHeader("Content-Type","application/json");var data=JSON.stringify({});xhr.setRequestHeader("Authorization",window.localStorage.getItem('token'));xhr.send(data);xhr.onreadystatechange=function(){if(xhr.readyState===4){console.log(xhr.status);console.log(xhr.responseText);const response=JSON.parse(xhr.responseText);if(xhr.status==503||xhr.status==500){window.location.href="portabullcompleteserverdown.html"}else if(xhr.status==401){window.localStorage.removeItem(tokenKey);window.location.href="index.html"}else{loadcallback()}}}}function executeRestCall(url,data,method,callbackMethod,custHeaders){var bearerToken=window.localStorage.getItem('token');if(bearerToken==null||bearerToken=="null"||bearerToken==undefined){window.location.href="index.html";return}var xhr=new XMLHttpRequest();xhr.open(method,url);xhr.setRequestHeader("Accept","application/json");xhr.setRequestHeader("Content-Type","application/json");xhr.setRequestHeader("Authorization",window.localStorage.getItem('token'));if(custHeaders!=null&&custHeaders!=undefined&&custHeaders!="null"){for(let[key,value]of custHeaders){xhr.setRequestHeader(key,value)}}xhr.send(data);xhr.onreadystatechange=function(){if(xhr.readyState===4){console.log(xhr.status);console.log(xhr.responseText);const response=JSON.parse(xhr.responseText);if(xhr.status==503||xhr.status==500){window.location.href="portabullcompleteserverdown.html"}else if(xhr.status==401&&response.message=="Unauthorized"){window.localStorage.removeItem('token');window.location.href="index.html"}else{callbackMethod(xhr,response)}}}}function checkIfStringObjIsEmpty(data){if(data==null||data=="null"||data==undefined||data==""){return true}return false}