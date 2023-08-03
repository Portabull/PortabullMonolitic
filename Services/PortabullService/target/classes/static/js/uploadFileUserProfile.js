var base64;
var fileInput;


var getProfilePictureUrl = BASE_URL + 'UM/profile/get-profile-picture';

var uploadProfilePicUrl = BASE_URL + 'UM/profile/upload-profile-picture';

var updateTwoStepVerUrl = BASE_URL + 'UM/profile/update-twostep-verification';

var tokenKey = 'token';

var userName;

var twoStepChecked = "unchecked";

var singleSignInChecked = "unchecked";

var twoStepVerButton1 = "<h2 align=\"center\">    Enable Two Step Verification: <span><label class=\"switch\">  <input type=\"checkbox\" ";
var twoStepVerButton2 = " onchange=\"updateTwoStepVerification(this,'twoStep')\">  <span class=\"slider round\"></span></label></span></h2>";


var singleSignInButton1 = "<h2 align=\"center\">    Enable Single SignIn(Log In): <span><label class=\"switch\">  <input type=\"checkbox\" ";
var singleSignInButton2 = " onchange=\"updateTwoStepVerification(this,'singleSignIn')\">  <span class=\"slider round\"></span></label></span></h2>";
function showProfilePhoto(){

var profilePicDivId = document.querySelector('#profilePicDivId');


var toggleButtonsDivId = document.querySelector('#toggleButtonsDivId');
var singleSignIntoggleButtonsDivId = document.querySelector('#singleSignIntoggleButtonsDivId');

var profilePicButtonId = document.querySelector('#profilePicButtonId');

                var reader = new FileReader();

                 fileInput = document.getElementById('myFile');

               reader.readAsDataURL(fileInput.files[0]);

                 reader.onload = function() {
               var fileName = fileInput.files[0].name;
               base64 = reader.result;

               var fileContentType = base64.split(';')[0].split(':')[1];

               if(!fileContentType.includes("image")){
                    alert("Please Upload Only Image");
                    return;
               }


profilePicDivId.innerHTML ="<img  src=\""+ base64 +"\" alt=\"Avatar\" style=\"width:200px\">" + "<h2  >"+userName+"</h2>"  ;
                                            profilePicButtonId.innerHTML = "<br><div class=\"upload-btn-wrapper\"> <form action=\"javascript:showProfilePhoto()\">    <button  class=\"btn\">Upload Profile Photo</button><input type=\"file\"  onchange=\"this.form.submit()\" id=\"myFile\" name=\"myFile\" /></form>    </div> <br>  <button  class=\"btn\" onclick=\"uploadProfilePhoto()\">&nbsp;&nbsp;Save Profile Photo&nbsp;&nbsp;</button>";


                toggleButtonsDivId.innerHTML = twoStepVerButton1 + twoStepChecked + twoStepVerButton2;
                singleSignIntoggleButtonsDivId.innerHTML = singleSignInButton1 + singleSignInChecked + singleSignInButton2;
           };

           reader.onerror = function(error) {
               alert("unable to proceed due to incorrect file please upload a valid file");
               return;
           };
}


function uploadProfilePhoto(){

var toggleButtonsDivId = document.querySelector('#toggleButtonsDivId');
var singleSignIntoggleButtonsDivId = document.querySelector('#singleSignIntoggleButtonsDivId');
var profilePicButtonId = document.querySelector('#profilePicButtonId');

   if(undefined == base64 || base64 == null || undefined == fileInput || undefined == fileInput.files || fileInput.files.length === 0){
        alert("Please upload a picture, before save");
        return;
   }


    var formData = new FormData();

    formData.append("file", fileInput.files[0]);


    var xhr = new XMLHttpRequest();
    xhr.open("POST",uploadProfilePicUrl);


    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

     xhr.onload = function () {
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            if (response.statusCode == 200) {
                alert(response.message);
            } else {
                alert(response.message);
            }
        }


    xhr.send(formData);
}


function loadUserProfileDetails() {
var toggleButtonsDivId = document.querySelector('#toggleButtonsDivId');
var singleSignIntoggleButtonsDivId = document.querySelector('#singleSignIntoggleButtonsDivId');

var profilePicButtonId = document.querySelector('#profilePicButtonId');

var profilePicDivId = document.querySelector('#profilePicDivId');

try{

                         var xhr = new XMLHttpRequest();

                         xhr.open("GET", getProfilePictureUrl);

                         xhr.setRequestHeader("Accept", "application/json");

                         xhr.setRequestHeader("Content-Type", "application/json");

                         xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                         xhr.send(JSON.stringify({}));

                         xhr.onreadystatechange = function () {
                             if (xhr.readyState === 4) {
                                 console.log(xhr.status);
                                 console.log(xhr.responseText);
                                 var data = JSON.parse(xhr.responseText);

                                 if(xhr.status ==401){
                                    alert(data.message);
                                 }

                                  var mfaLoginType =  data.data.mfaLoginType;

                                  if(mfaLoginType == 0){
                                        document.getElementById('sendOtpToEmailId').checked = "checked";
                                  }else
                                 {
                                        document.getElementById('emailLinkAuthId').checked = "checked";
                                 }

                                  if(data.data.file == null){

                                    userName = data.data.userName;

                                    twoStepChecked = data.data.twoStepChecked;

                                    singleSignInChecked = data.data.singleSignInChecked;

                                        profilePicDivId.innerHTML =  "<img  src=\"images/avatar.png\" alt=\"Avatar\" style=\"width:200px\">" +"<h2  >"+userName+"</h2>" ;
                                        profilePicButtonId.innerHTML =    "<div class=\"upload-btn-wrapper\"> <form action=\"javascript:showProfilePhoto()\">    <button class=\"btn\">Upload Profile Photo</button><input type=\"file\"  onchange=\"this.form.submit()\" id=\"myFile\" name=\"myFile\" /></form>    </div> <br>  <button  class=\"btn\" onclick=\"uploadProfilePhoto()\">&nbsp;&nbsp;Save Profile Photo&nbsp;&nbsp;</button>";
                                        toggleButtonsDivId.innerHTML = twoStepVerButton1 + twoStepChecked + twoStepVerButton2;
                                        singleSignIntoggleButtonsDivId.innerHTML = singleSignInButton1 + singleSignInChecked + singleSignInButton2;
                                  }
                                    else{

                                 base64 = data.data.file;

                                   userName = data.data.userName;

                                    twoStepChecked = data.data.twoStepChecked;

                                    singleSignInChecked = data.data.singleSignInChecked;

                                 profilePicDivId.innerHTML =   "<img src=\""+ base64 +"\" alt=\"Avatar\" style=\"width:200px\">" +"<h2  >"+userName+"</h2>" ;
                                                                   profilePicButtonId.innerHTML =          "<div class=\"upload-btn-wrapper\"> <form action=\"javascript:showProfilePhoto()\">    <button  class=\"btn\">Upload Profile Photo</button><input type=\"file\"  onchange=\"this.form.submit()\" id=\"myFile\" name=\"myFile\" /></form>    </div> <br>  <button class=\"btn\" onclick=\"uploadProfilePhoto()\">&nbsp;&nbsp;Save Profile Photo&nbsp;&nbsp;</button>";
 toggleButtonsDivId.innerHTML = twoStepVerButton1 + twoStepChecked + twoStepVerButton2;
 singleSignIntoggleButtonsDivId.innerHTML = singleSignInButton1 + singleSignInChecked + singleSignInButton2;
                                    }

                             }
                         };
       } catch (err) {
                         alert("Failed");
                     }


}


function updateTwoStepVerification(obj,flag) {

var requestParams = "";

    if(flag == 'twoStep'){
      requestParams =   "?twoStep=" + obj.checked
    }else if(flag == 'singleSignIn'){
     requestParams = "?singleSignIn=" + obj.checked;
    }else if(flag == 'updateMfaLoginType'){
     requestParams = "?mfa=" + getMfaValue();
    }

try{
    var xhr = new XMLHttpRequest();

                         xhr.open("POST", updateTwoStepVerUrl + requestParams);

                         xhr.setRequestHeader("Accept", "application/json");

                         xhr.setRequestHeader("Content-Type", "application/json");

                         xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                         xhr.send(JSON.stringify({}));

                         xhr.onreadystatechange = function () {
                             if (xhr.readyState === 4) {
                                 console.log(xhr.status);
                                 console.log(xhr.responseText);
                                 var data = JSON.parse(xhr.responseText);

                                 if(xhr.status ==401){
                                    alert(data.message);
                                    window.location.href = "index.html";
                                 }

                             }
                         };
}catch (err) {
                          alert("Failed");
                      }


}

function getMfaValue(){



if (document.getElementById('emailLinkAuthId').checked) {
  return 1;
}else {
return 0;
}
}
