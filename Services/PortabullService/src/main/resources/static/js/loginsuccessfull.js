

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

    }else if(optionName == 'Notifications'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\" " + loginstaticimages.get('static/images/notifications.webp') + "\" id=\"documentButton\"/><p>Notifications</p>";;

    } else if(optionName == 'SPF'){

     showHoverOptionDivInner =  "<input class=\"documentButton\" type=\"image\" src=\" " +  loginstaticimages.get('static/images/portabull.png') + "\" id=\"documentButton\"/><p>SPF</p>";;

    } else if(optionName == 'Email'){

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" +  loginstaticimages.get('static/images/email.jpg') + "\" id=\"documentButton\"/><p>Email</p>";;

    } else{

     showHoverOptionDivInner = "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/reports.jpg') + "\" id=\"documentButton\"/><p>Mis Reports</p>";;

    }

    showHoverOptionDiv.innerHTML = showHoverOptionDivInner;

}

    initToken();
    function initToken(){


const div = document.getElementById("myDiv");
div.addEventListener("contextmenu", (e) => {e.preventDefault()});


var showHoverOptionDiv = document.querySelector('#showHoverOptionDiv');

    showHoverOptionDiv.innerHTML = '<br><br><br><br><br><br><br><br><br><br>';

     var jwtToken = new URL(window.location.href).searchParams.get("jwt");
     var userName = new URL(window.location.href).searchParams.get("userName");
     var twoStepVer = new URL(window.location.href).searchParams.get("twoStepVer");

     if(jwtToken!=null){
        window.localStorage.setItem('isUserLoggedViaOAuth',true);
        window.localStorage.setItem('token', 'Bearer ' + jwtToken);
        if(twoStepVer!=null && "true"==twoStepVer){
         sendOtp(userName);
         window.location.href = "otp.html";
         return;
        }
     }



     if(window.localStorage.getItem('registrationCheck') == null || !window.localStorage.getItem('registrationCheck')){
     loadLoggedInPages();
     }

    }
  loadTimeAndLogout();



  init();

  function init() {
    healthCheckBasePort2653(loadStaticAssets1121122);
        }

        function loadStaticAssets1121122(){
             loadStaticAssets(createStaticButtons);
        }

  function getStaticImage() {

  }

  function createStaticButtons() {

        var loginstaticimages1 =  window.localStorage.getItem(loginstaticimagesConstKey);

        var loginstaticimages = new Map(Object.entries(JSON.parse(loginstaticimages1)))

    var aaaaaa = loginstaticimages.get('static/images/source-documents-1024x682.jpeg');

    var documentsButton =  "<input class=\"documentButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/source-documents-1024x682.jpeg') + "\" draggable=\"false\"  onmouseover=\"showHoverOption('Documents')\" onclick=\"redirectToDocsPage()\"  onmouseleave=\"clearshowHoverOption()\" id=\"documentButton\"/>";
    var settingsButton = "<input class=\"settingsButton\" onclick=\"redirectTosettingsPage()\" type=\"image\" src=\"" + loginstaticimages.get('static/images/setting-2872383-2389560.jpg') + "\" onmouseover=\"showHoverOption('Settings')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\" id=\"settingsButton\"/>";
    var messageButton = "<input class=\"messageButton\" onclick=\"redirectToMessagePage()\" type=\"image\" src=\"" + loginstaticimages.get('static/images/message.webp') + "\" onmouseover=\"showHoverOption('Message')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\" id=\"messageButton\"/>";
    var notificationButton = "<input onclick=\"redirectToemailPage()\" class=\"notificationButton\" type=\"image\" onmouseover=\"showHoverOption('Notifications')\" draggable=\"false\"  onmouseleave=\"clearshowHoverOption()\"  src=\"" + loginstaticimages.get('static/images/notifications.webp') + "\" id=\"notificationButton\">";
    var spfButton = "<input onclick=\"redirectToSPFPage()\" class=\"spf\" type=\"image\" src=\"" + loginstaticimages.get('static/images/portabull.png') + "\" draggable=\"false\"   onmouseover=\"showHoverOption('SPF')\" onmouseleave=\"clearshowHoverOption()\" id=\"spf\">";
    var emailButton = "<input onclick=\"redirectToNotificationPage()\" class=\"emailButton\" type = \"image\" src = \"" + loginstaticimages.get('static/images/email.jpg') + "\" draggable = \"false\" onmouseover = \"showHoverOption('Email')\" onmouseleave = \"clearshowHoverOption()\" id = \"emailButton\"/>";
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
        location.href = "notificationButton.html";
    };
     function redirectToMISReportsPage() {
        location.href = "adminmisreports.html";
    };

