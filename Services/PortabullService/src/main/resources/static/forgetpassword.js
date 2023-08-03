

var otpUrl = BASE_URL + 'UM/sendOtp?userNameBased=yes';

var updatepasswordUrl = BASE_URL + 'UM/update-password';



function sendOtp() {


        var singleFileUploadSuccess = document.querySelector('#portabullloader');

        singleFileUploadSuccess.innerHTML = "<div id=\"loader\"></div>";

    var userName = document.getElementById("user").value;

    window.localStorage.setItem("forgetPasswordUserName",userName);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", otpUrl);

    xhr.setRequestHeader("Accept", "application/json");


    xhr.setRequestHeader("Content-Type", "application/json");


    var data = JSON.stringify({"to": [userName]});

    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.status);
            console.log(xhr.responseText);
              document.getElementById("loader").style.display = "none";

            const response = JSON.parse(xhr.responseText);
            if (response.statusCode == 200) {
              window.location.href = "forgetpassworddetails.html";

            }else if(response.statusCode == 404){
                showErrorMessage4();
            }else{
                showErrorMessage();
            }
        }
    };


}

  function showErrorMessage() {
              var x = document.getElementById("snackbar");
              x.className = "show";
              setTimeout(function(){ x.className = x.className.replace("show", ""); }, 9000);
            }

              function showSuccessMessage() {
                          var x = document.getElementById("snackbar3");
                          x.className = "show";
                          setTimeout(function(){ x.className = x.className.replace("show", ""); }, 9000);
                        }

 function showErrorMessage2() {
              var x = document.getElementById("snackbar2");
              x.className = "show";
              setTimeout(function(){ x.className = x.className.replace("show", ""); }, 9000);
            }
 function showErrorMessage1() {
              var x = document.getElementById("snackbar1");
              x.className = "show";
              setTimeout(function(){ x.className = x.className.replace("show", ""); }, 9000);
            }
             function showErrorMessage4() {
                          var x = document.getElementById("snackbar1");
                          x.className = "show";
                          setTimeout(function(){ x.className = x.className.replace("show", ""); }, 9000);
                        }

function updatepassword(){

    var otp = document.getElementById("otp").value;
    var userName = window.localStorage.getItem("forgetPasswordUserName");
    var newpassword = document.getElementById("newpassword").value;
    var confirmNewPassword = document.getElementById("confirmNewPassword").value;


    var xhr = new XMLHttpRequest();
    xhr.open("POST", updatepasswordUrl);

    xhr.setRequestHeader("Accept", "application/json");


    xhr.setRequestHeader("Content-Type", "application/json");


    var data =  JSON.stringify({"to": userName, "otp": otp, "newPassword": newpassword,"confirmNewPassword":confirmNewPassword});

    xhr.send(data);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.status);
            console.log(xhr.responseText);

               const response = JSON.parse(xhr.responseText);

                        if (response.statusCode == 200) {


                            showSuccessMessage();
                            setTimeout(routeToLoginPage, 3000);


                        }else if(response.statusCode==400){
                        showErrorMessage2();
                        }else{
                        showErrorMessage1();
                        }
        }
    };




}


function routeToLoginPage(){
window.location.href = "index.html";
}