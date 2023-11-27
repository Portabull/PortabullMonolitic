

var url = BASE_URL + 'UM/login';

var otpUrl = BASE_URL + 'UM/sendOtp';

var validateUrl = BASE_URL + 'UM/validateOtp';

var tokenKey = 'token'

var registrationUrl = BASE_URL + 'UM/registration';

var healthCheckUrl = BASE_URL + 'portabull-health-check';

var notifyUrl = BASE_URL + 'UM/notify-administrator';

var verifyNotifyToken = BASE_URL + 'UM/mfa/verify-random-token';

var loggedInUserNameForOtp = "loggedInUserNameForOtp";

var onHold = false;

var notificationHold = false;

var notifyLoaderCount = 0;

var notifyTokenRan = "";

var clearIntervalId;

var redirectionUrl = "";

function login() {

    try {

        if(onHold || notificationHold)
            return;

        onHold = true;
        notifyLoaderCount = 0;

        var singleFileUploadSuccess = document.querySelector('#portabullloader');
        singleFileUploadSuccess.innerHTML = "<div id=\"loader\"></div>";

        var userName = btoa(btoa(document.getElementById("user").value).split("").reverse().join(""));

        var password =btoa(btoa(document.getElementById("pass").value).split("").reverse().join(""));

        if(userName == "" || password == ""){
          document.getElementById("loader").style.display = "none";
            alert("UserName/Password Should not be empty");
            return;
        }

        var xhr = new XMLHttpRequest();
        xhr.open("POST", url);

        xhr.setRequestHeader("Accept", "application/json");


        xhr.setRequestHeader("Content-Type", "application/json");


        var data = JSON.stringify({"username": userName, "password": password});

        let latLong = window.localStorage.getItem("location");
        let devdtls = window.localStorage.getItem("devdtls");
        console.log("lattitude;longitude:" + latLong);
        xhr.setRequestHeader("location", latLong);
        xhr.setRequestHeader("devdtls",devdtls );

        redirectionUrl = window.localStorage.getItem(pageRedirectionPort);
         var staticAssets = window.localStorage.getItem(loginstaticimagesConstKey);
         var buildId = window.localStorage.getItem(buildIdNumberCashe1238);
        window.localStorage.clear();
        window.localStorage.setItem(buildIdNumberCashe1238,buildId);
           window.localStorage.setItem("location",latLong);
           window.localStorage.setItem("devdtls",devdtls);
           if(staticAssets != null && staticAssets != "null" && staticAssets != undefined && staticAssets != "undefined")
        window.localStorage.setItem(loginstaticimagesConstKey,staticAssets);

        xhr.send(data);

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);

                const tokenData = JSON.parse(xhr.responseText);
                if (tokenData.statusCode == 200) {
                    window.localStorage.setItem(tokenKey, 'Bearer ' + tokenData.data.jwt);
                    window.localStorage.setItem('isUserLoggedViaOAuth',false);
                    if(tokenData.data.twostepVerificationEnabled){

                        if(tokenData.data.notificationType != undefined && tokenData.data.notificationType){
                        notificationHold = true;
                        document.getElementById("loader").style.display = "none";
                            invokeProgressBar();
                            notifyTokenRan = tokenData.data.randomToken;
                            clearIntervalId = setInterval(verifyNotifyTokenData, 5000);
                            return;
                        }

                            sendOtp(document.getElementById("user").value,true);
                            return;
                    } else {
                           if(redirectionUrl != null && redirectionUrl != undefined && redirectionUrl != "" && redirectionUrl != "null") {
                                  window.location.href = redirectionUrl;
                                  return;
                           }
                            window.location.href = "loginsuccessfull.html";
                    }
                } else if(xhr.status == 401){
                onHold = false;
                       if('Your account is locked Due to 3 wrong attempts, please contact administrator' == tokenData.message
                       || 'Your account is locked by admin team, please contact administrator'  == tokenData.message ){
                        myFunction123();
                       }

                    alert(tokenData.message);
                }else {
                   window.location.href = "portabullcompleteserverdown.html";
                }

                         document.getElementById("loader").style.display = "none";
            }
        };
    } catch (err) {
        alert("Exception Occered");
        document.getElementById("loader").style.display = "none";
    }


}


function sendOtp() {

    var userName = document.getElementById("user").value;

    sendOtp(userName,true);
}

function sendOtp(userName,flag){
  var xhr = new XMLHttpRequest();
    xhr.open("POST", otpUrl);

    xhr.setRequestHeader("Accept", "application/json");


    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

    var data = JSON.stringify({"to": [userName]});

    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.status);
            console.log(xhr.responseText);
                  window.localStorage.setItem("loggedInUserNameForOtp", userName);
                  if(flag)
                  window.location.href = "otp.html";
                  else{
                    stopPLoader();
                  }
        }
    };
}

function validateOtp() {


startPLoader();

    var password = document.getElementById("pass").value;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", validateUrl + "?otp=" + password);

    xhr.setRequestHeader("Accept", "application/json");


    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

    var data = JSON.stringify({});

    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.status);
            console.log(xhr.responseText);

            const response = JSON.parse(xhr.responseText);

            stopPLoader();
            if (response.statusCode == 200) {
            if(redirectionUrl != null && redirectionUrl != undefined && redirectionUrl != "" && redirectionUrl != "null") {
                   window.location.href = redirectionUrl;
                   return;
            }
                window.location.href = "loginsuccessfull.html";
            } else {
                alert(response.message);
            }
        }
    };
}


function registration() {

    var userName = document.getElementById("userName").value;
    var password = document.getElementById("password").value;
    var repeatPassword = document.getElementById("repeatPassword").value;
    var emailAddress = document.getElementById("emailAddress").value;

    if (password != repeatPassword) {
        alert("Passeord/Repeat Password is different");
    } else {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", otpUrl + "?isRegistration=true");

        xhr.setRequestHeader("Accept", "application/json");


        xhr.setRequestHeader("Content-Type", "application/json");


        var data = JSON.stringify({"username": userName, "password": password, "email": emailAddress});

         window.localStorage.setItem("registrationData",data);

        xhr.send(JSON.stringify({"to": [emailAddress]}));


        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                const response = JSON.parse(xhr.responseText);
                if (response.status == "SUCCESS") {
                  window.localStorage.setItem("registrationToken", response.data.token);
                  window.location.href = "register.html";
                } else {
                    alert(response.message)
                }
            }
        };

    }



}


function register() {


    var otp = document.getElementById("pass").value;

        var xhr = new XMLHttpRequest();
        xhr.open("POST", registrationUrl);

        xhr.setRequestHeader("Accept", "application/json");

        xhr.setRequestHeader("Content-Type", "application/json");

         var registrationDataText = window.localStorage.getItem("registrationData");

          const registrationData = JSON.parse(registrationDataText);

        var data = JSON.stringify({"username": registrationData.username, "password": registrationData.password,
         "email": registrationData.email,"otp":otp,"token":window.localStorage.getItem("registrationToken")});

        xhr.send(data);

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                const response = JSON.parse(xhr.responseText);
                if (response.status == "SUCCESS") {
                      window.localStorage.setItem(tokenKey, 'Bearer ' +response.data.jwt);
                    window.location.href = "loginsuccessfull.html";
                } else {
                    alert(response.message)
                }
            }
        };





}


            function healthCheck() {
                    var xhr = new XMLHttpRequest();
                     xhr.open("GET", healthCheckUrl);
                    xhr.setRequestHeader("Accept", "application/json");
                    xhr.setRequestHeader("Content-Type", "application/json");
                    var data = JSON.stringify({});
                    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));
                    xhr.send(data);

                    xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);

                        const response = JSON.parse(xhr.responseText);
                        if (xhr.status == 503 || xhr.status == 500) {
                            window.location.href = "portabullcompleteserverdown.html";
                        } else if(xhr.status == 401) {
                            window.localStorage.removeItem(tokenKey);
                            window.location.href = "index.html";
                        }else{
                            window.location.href = "loginsuccessfull.html";
                        }
                    }
                };


}

	function givealert(){
	                    var xhr = new XMLHttpRequest();
                         xhr.open("POST", notifyUrl);
                        xhr.setRequestHeader("Accept", "application/json");
                        xhr.setRequestHeader("Content-Type", "application/json");
                        var data = JSON.stringify({"emailAddress": document.getElementById('notifyEmailAddress').value, "reason": document.getElementById('notifyReason').value});
                        xhr.send(data);

                        xhr.onreadystatechange = function () {
                        if (xhr.readyState === 4) {
                            console.log(xhr.status);
                            console.log(xhr.responseText);

                            const response = JSON.parse(xhr.responseText);
                            if (xhr.status == 503 || xhr.status == 500) {
                                window.location.href = "portabullcompleteserverdown.html";
                            } else{
                                alert(response.message);
                            }
                        }
                    };

	}


function redirectToGmailLogin(){
        window.location.href = gmailUrl;
}



    function myFunction123() {
             var singleFileUploadSuccess = document.querySelector('#myDIV');
                singleFileUploadSuccess.innerHTML = "<button class=\"sign-btn\">Raise Unlock Request</button>";

}

 function invokeProgressBar() {
    var singleFileUploadSuccess = document.querySelector('#myDIV');
    singleFileUploadSuccess.innerHTML = "<div class=\"indeterminate-progress-bar\"><div class=\"indeterminate-progress-bar__progress\"></div></div><br><p class=\"text\">Please login to your gmail account and approve the login request<a style=\"color: blue;\" href=\"https://gmail.com\" target=\"_blank\" rel=\"noopener noreferrer\">Login to gmail</a></p>";

}

function closeProgressBar(){
    var singleFileUploadSuccess = document.querySelector('#myDIV');
    singleFileUploadSuccess.innerHTML = "";

}


function goToHomePage(){
 window.location.href = "index.html";
}

function resendOtp(){

startPLoader();

var userName = window.localStorage.getItem(loggedInUserNameForOtp);

   sendOtp(userName,false);


}



function startPLoader(){
    var singleFileUploadSuccess = document.querySelector('#portabullloader');
        singleFileUploadSuccess.innerHTML = "<div id=\"loader\"></div>";
}


function stopPLoader(){
document.getElementById("loader").style.display = "none";
}


function verifyNotifyTokenData(){

    if(notifyLoaderCount==19){
    closeProgressBar();
    clearInterval(clearIntervalId);
    launch_toast1();
    notificationHold = false;
    return;
    }

    notifyLoaderCount++;

    var xhr = new XMLHttpRequest();

    xhr.open("POST", verifyNotifyToken);

    xhr.setRequestHeader("Accept", "application/json");


    xhr.setRequestHeader("Content-Type", "application/json");

    var data = JSON.stringify({"token": notifyTokenRan});

    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.status);
            console.log(xhr.responseText);
  const response = JSON.parse(xhr.responseText);

    if(response.data.a == 1){
    closeProgressBar();
              clearInterval(clearIntervalId);
           window.localStorage.setItem(tokenKey, 'Bearer ' + response.data.token);

if(redirectionUrl != null && redirectionUrl != undefined && redirectionUrl != "" && redirectionUrl != "null") {
       window.location.href = redirectionUrl;
       return;
}
       window.location.href = "loginsuccessfull.html";
    }else if(response.data.a == 2){
      launch_toast();
          closeProgressBar();
          clearInterval(clearIntervalId);
          notificationHold = false;
    }
        }
    };



}



function launch_toast() {
        var toastMaker = document.querySelector('#toast');
        toastMaker.innerHTML = "<div id=\"desc\" style=\"color:white;\">Access Denined</div>";

    var x = document.getElementById("toast")
    x.className = "show";
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 5000);
}


function launch_toast1() {
   var toastMaker = document.querySelector('#toast');
        toastMaker.innerHTML =   "<div id=\"desc1\" style=\"color:white;\">Request Timeout</div>";

    var x = document.getElementById("toast")
    x.className = "show";
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 5000);
}

