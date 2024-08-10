
var getNameAndTimeDtls = BASE_URL + 'UM/users/get-logged-in-dtls'

function clearshowHoverOption(){

var showHoverOptionDiv = document.querySelector('#showHoverOptionDiv');

    showHoverOptionDiv.innerHTML = '<br><br><br><br><br><br><br><br><br><br>';


}

function showHoverOption(optionName){

  var loginstaticimages1 =  window.localStorage.getItem(loginstaticimagesConstKey);

        var loginstaticimages = new Map(Object.entries(JSON.parse(loginstaticimages1)))


var showHoverOptionDivInner ='';

var showHoverOptionDiv = document.querySelector('#showHoverOptionDiv');

    if(optionName == 'Documents'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/source-documents-1024x682.jpeg')  + "\" id=\"documentButton\"/><p>Documents</p>";;

    }else if(optionName == 'Settings'){


     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/setting-2872383-2389560.jpg') + "\" id=\"documentButton\"/><p>Settings</p>";;

    }else if(optionName == 'Message'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/message.webp') + "\" id=\"documentButton\"/><p>Message</p>";;

    }else if(optionName == 'Schedulers'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\" " + loginstaticimages.get('static/images/scheduler.webp') + "\" id=\"documentButton\"/><p>Schedulers</p>";;

    } else if(optionName == 'SPF'){

     showHoverOptionDivInner =  "<input class=\"documentButton\" type=\"image\" src=\" " +  loginstaticimages.get('static/images/portabull.png') + "\" id=\"documentButton\"/><p>SPF</p>";;

    } else if(optionName == 'Email'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" +  loginstaticimages.get('static/images/email.jpg') + "\" id=\"documentButton\"/><p>Email</p>";;

    } else{

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/reports.jpg') + "\" id=\"documentButton\"/><p>Mis Reports</p>";;

    }

    showHoverOptionDiv.innerHTML = showHoverOptionDivInner;

}


(function () {
    'use strict';

    var module = {
        options: [],
        header: [navigator.platform, navigator.userAgent, navigator.appVersion, navigator.vendor, window.opera],
        dataos: [
            { name: 'Windows Phone', value: 'Windows Phone', version: 'OS' },
            { name: 'Windows', value: 'Win', version: 'NT' },
            { name: 'iPhone', value: 'iPhone', version: 'OS' },
            { name: 'iPad', value: 'iPad', version: 'OS' },
            { name: 'Kindle', value: 'Silk', version: 'Silk' },
            { name: 'Android', value: 'Android', version: 'Android' },
            { name: 'PlayBook', value: 'PlayBook', version: 'OS' },
            { name: 'BlackBerry', value: 'BlackBerry', version: '/' },
            { name: 'Macintosh', value: 'Mac', version: 'OS X' },
            { name: 'Linux', value: 'Linux', version: 'rv' },
            { name: 'Palm', value: 'Palm', version: 'PalmOS' }
        ],
        databrowser: [
            { name: 'Chrome', value: 'Chrome', version: 'Chrome' },
            { name: 'Firefox', value: 'Firefox', version: 'Firefox' },
            { name: 'Safari', value: 'Safari', version: 'Version' },
            { name: 'Internet Explorer', value: 'MSIE', version: 'MSIE' },
            { name: 'Opera', value: 'Opera', version: 'Opera' },
            { name: 'BlackBerry', value: 'CLDC', version: 'CLDC' },
            { name: 'Mozilla', value: 'Mozilla', version: 'Mozilla' }
        ],
        init: function () {
            var agent = this.header.join(' '),
                os = this.matchItem(agent, this.dataos),
                browser = this.matchItem(agent, this.databrowser);

            return { os: os, browser: browser };
        },
        matchItem: function (string, data) {
            var i = 0,
                j = 0,
                html = '',
                regex,
                regexv,
                match,
                matches,
                version;

            for (i = 0; i < data.length; i += 1) {
                regex = new RegExp(data[i].value, 'i');
                match = regex.test(string);
                if (match) {
                    regexv = new RegExp(data[i].version + '[- /:;]([\\d._]+)', 'i');
                    matches = string.match(regexv);
                    version = '';
                    if (matches) { if (matches[1]) { matches = matches[1]; } }
                    if (matches) {
                        matches = matches.split(/[._]+/);
                        for (j = 0; j < matches.length; j += 1) {
                            if (j === 0) {
                                version += matches[j] + '.';
                            } else {
                                version += matches[j];
                            }
                        }
                    } else {
                        version = '0';
                    }
                    return {
                        name: data[i].name,
                        version: parseFloat(version)
                    };
                }
            }
            return { name: 'unknown', version: 0 };
        }
    };

    var e = module.init(),
        debug = '';

    debug += 'os.name = ' + e.os.name + '<br/>';
    debug += 'os.version = ' + e.os.version + '<br/>';
    debug += 'browser.name = ' + e.browser.name + '<br/>';
    debug += 'browser.version = ' + e.browser.version + '<br/>';

    debug += '<br/>';
    debug += 'navigator.userAgent = ' + navigator.userAgent + '<br/>';
    debug += 'navigator.appVersion = ' + navigator.appVersion + '<br/>';
    debug += 'navigator.platform = ' + navigator.platform + '<br/>';
    debug += 'navigator.vendor = ' + navigator.vendor + '<br/>';

window.localStorage.setItem("devdtls", e.os.name + ',' + e.os.version  + ',' + e.browser.name  + ',' +e.browser.version);
}());

 initDetails();
    function initDetails(){
     var jwtToken = new URL(window.location.href).searchParams.get("jwt");
          if(jwtToken!=null){
window.localStorage.setItem('token', 'Bearer ' + jwtToken);
         let devdtls = window.localStorage.getItem("devdtls");
              let latLong = window.localStorage.getItem("location");
            executeRestCall('UM/cache-logged-dev-dtls',null,'POST',initToken,new Map([["devdtls", devdtls],["location",latLong ]]));
            }else{
            initToken();
            }


    }


    function initToken(){



const div = document.getElementById("myDiv");
div.addEventListener("contextmenu", (e) => {e.preventDefault()});


var showHoverOptionDiv = document.querySelector('#showHoverOptionDiv');

    showHoverOptionDiv.innerHTML = '<br><br><br><br><br><br><br><br><br><br>';

     var jwtToken = new URL(window.location.href).searchParams.get("jwt");
     var userName = new URL(window.location.href).searchParams.get("userName");
     var twoStepVer = new URL(window.location.href).searchParams.get("twoStepVer");

     window.history.pushState({},document.title,"/APIGateway/"+"loginsuccessfull.html");

     if(jwtToken!=null){

        window.localStorage.setItem('isUserLoggedViaOAuth',true);
        window.localStorage.setItem('token', 'Bearer ' + jwtToken);
        if(twoStepVer!=null && "true"==twoStepVer){
         sendOtp(userName);
         window.location.href = "otp.html";
         return;
        }
     }


      init();

     if(window.localStorage.getItem('registrationCheck') == null || !window.localStorage.getItem('registrationCheck')){
     loadLoggedInPages();
     }

    }




var clearTextIntervalId;
var loadCounter = 0;
    function doNothing(xhr,response){
    alert(response.message);
    }

  function init() {

  clearTextIntervalId = setInterval(loadTextToUser, 500);
    populateNameAndTimeDetails();
    healthCheckBasePort2653(loadStaticAssets1121122);
        }

        function loadStaticAssets1121122(){
             loadStaticAssets(createStaticButtons);
        }

        function loadTextToUser(){

            if(loadCounter == 5){
              document.getElementById('uploadLoading1').innerHTML = 'Installing Assets';
            }else if(loadCounter == 13){
               document.getElementById('uploadLoading1').innerHTML = 'Please wait while installing assets';
            }else if(loadCounter == 21){
             document.getElementById('uploadLoading1').innerHTML = 'Almost done with the installation please wait';
            }else if(loadCounter == 40){
              document.getElementById('uploadLoading1').innerHTML = 'Your internet connection is slow please hold';
           }else if(loadCounter == 55){
               document.getElementById('uploadLoading1').innerHTML = 'Please wait for some more time to install assets';
            }else if(loadCounter == 65){
             document.getElementById('uploadLoading1').innerHTML = 'Your internet connection is slow please hold';
            }else if(loadCounter == 80){
               document.getElementById('uploadLoading1').innerHTML = 'Please wait while installing assets';
            }else if(loadCounter == 95){
             document.getElementById('uploadLoading1').innerHTML = 'Your internet connection is very slow please hold';
        }
            loadCounter++;
        }

  function getStaticImage() {

  }

  function populateNameAndTimeDetails(){
         executeRestCall(getNameAndTimeDtls,null,"GET",populateNameAndTimeDetails1,null);
  }

  function populateNameAndTimeDetails1(xhr,response){

    document.getElementById("nameAndTimeDetails").innerHTML = '';

  document.getElementById("nameAndTimeDetails").innerHTML = '<p><b>' + response.data.time + '<br>' + response.data.userName + '</b></p>';

  }

  function createStaticButtons() {

        var loginstaticimages1 =  window.localStorage.getItem(loginstaticimagesConstKey);

        var loginstaticimages = new Map(Object.entries(JSON.parse(loginstaticimages1)));

        clearInterval(clearIntervalId);
        loadCounter = 0;

            document.getElementById('uploadLoading1').innerHTML = 'Loading';

    var aaaaaa = loginstaticimages.get('static/images/source-documents-1024x682.jpeg');

    var documentsButton =  "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/source-documents-1024x682.jpeg') + "\" draggable=\"false\"  onmouseover=\"showHoverOption('Documents')\" onclick=\"redirectToDocsPage()\"  onmouseleave=\"clearshowHoverOption()\" id=\"documentButton\"/>";
    var settingsButton = "<input class=\"settingsButton\" onclick=\"redirectTosettingsPage()\" type=\"image\" src=\"" + loginstaticimages.get('static/images/setting-2872383-2389560.jpg') + "\" onmouseover=\"showHoverOption('Settings')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\" id=\"settingsButton\"/>";
    var messageButton = "<input class=\"messageButton\" onclick=\"redirectToMessagePage()\" type=\"image\" src=\"" + loginstaticimages.get('static/images/message.webp') + "\" onmouseover=\"showHoverOption('Message')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\" id=\"messageButton\"/>";
    var notificationButton = "<input onclick=\"redirectToNotificationPage()\" class=\"notificationButton\" type=\"image\" onmouseover=\"showHoverOption('Schedulers')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\"  src=\"" + loginstaticimages.get('static/images/scheduler.webp') + "\" id=\"notificationButton\">";
    var spfButton = "<input onclick=\"redirectToSPFPage()\" class=\"spf\" type=\"image\" src=\"" + loginstaticimages.get('static/images/portabull.png') + "\" draggable=\"false\"   onmouseover=\"showHoverOption('SPF')\" onmouseleave=\"clearshowHoverOption()\" id=\"spf\">";
    var emailButton = "<input onclick=\"redirectToemailPage()\" class=\"emailButton\" type = \"image\" src = \"" + loginstaticimages.get('static/images/email.jpg') + "\" draggable = \"false\" onmouseover = \"showHoverOption('Email')\" onmouseleave = \"clearshowHoverOption()\" id = \"emailButton\"/>";
    var misReportsButton = "<input onclick=\"redirectToMISReportsPage()\" class=\"misReportsButton\" type = \"image\" src = \"" + loginstaticimages.get('static/images/reports.jpg') + "\" draggable = \"false\" onmouseover = \"showHoverOption('Mis Reports')\" onmouseleave = \"clearshowHoverOption()\" id=\"misReportsButton\">";

        document.getElementById("buttonsTagDivStatic").innerHTML = documentsButton + settingsButton + messageButton + notificationButton+ spfButton + emailButton +misReportsButton;


document.getElementById("logoID").innerHTML = "<h4>Powered by : <span><img src=\"" + loginstaticimages.get('static/img/logo.png') + "\" alt=\"portabull\"/></span></h4>";


 document.getElementById("uploadLoading1").hidden = true;
   }



     function redirectToDocsPage() {
        location.href = "documents.html";
    }



    function redirectTosettingsPage() {
            location.href = "wdw-snippet.html";
        }

         function redirectToMessagePage() {

                var xhr = new XMLHttpRequest();

                xhr.open("POST", BASE_URL + "UM/get-user-name");

                xhr.setRequestHeader("Accept", "application/json");


                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

                var data = JSON.stringify({});

                xhr.send(data);

                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);

                        const response = JSON.parse(xhr.responseText);

                        if (response.statusCode == 200) {
                            location.href = "http://portabull.in:8087?aa="+response.data;
                        }else{
                            alert("Please Login Again Session Expired");
                        }
                    }
                };
            };


function redirectToemailPage() {
        location.href = "emails.html";
    };

    function redirectToSPFPage() {
        location.href = "callistpage1.html";
    };

    function redirectToNotificationPage() {
        location.href = "scheduler.html";
    };
     function redirectToMISReportsPage() {
        location.href = "adminmisreports.html";
    };

