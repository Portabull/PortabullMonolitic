
var isDecrpt = false;

var tokenKey = 'token'


var decryptionUrl = BASE_URL + 'DMS/decrypt-any-file?directDownload=false';

var encryptionUrl = BASE_URL + 'DMS/encrypt-any-file?directDownload=false';


var pdfPasswordLockUrl = BASE_URL + 'DMS/create-pdf-password';


var pdfPasswordUnLockUrl = BASE_URL + 'DMS/remove-pdf-password';


var onHold = false;


var downloadFileXHR;

var isDownload = false;

function invokeDecryption(file){


        if(onHold)
            return;

        onHold = true;

        startProgressBar();


      	var formData = new FormData();
   
 formData.append("file", file);

    var xhr = new XMLHttpRequest();

    xhr.open("POST",decryptionUrl );

    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

    xhr.onload = function () {
        console.log(xhr.responseText);

        onHold = false;

        downloadFileResponse(xhr,"File Decrypted Successfully");
    }

    xhr.send(formData);

}




function invokeEncryption(file){
    if(onHold)
            return;

        onHold = true;
 startProgressBar();

	var formData = new FormData();
   
 formData.append("file", file);

    var xhr = new XMLHttpRequest();

    xhr.open("POST",encryptionUrl );

    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

    xhr.onload = function () {
        console.log(xhr.responseText);

        onHold = false;

        downloadFileResponse(xhr,"File Encrypted Successfully");
    }

    xhr.send(formData);
}





function downloadFileResponse(xhr,message){
          const fileResponse = JSON.parse(xhr.responseText);

                if (fileResponse.statusCode == 200) {

                     var a = document.createElement("a");
                       a.href =fileResponse.data.file;
                          a.download = fileResponse.data.fileName; //File name Here
                        a.click();

                        closeProgressBar();

                         var toastMaker = document.querySelector('#toast');
                          toastMaker.innerHTML =   "<div id=\"desc1\" style=\"color:white;\">" +message + "</div>";

                             var x = document.getElementById("toast")
                             x.className = "show";
                             setTimeout(function(){ x.className = x.className.replace("show", ""); }, 5000);



                } else {

                     var toastMaker = document.querySelector('#toast');
                                          toastMaker.innerHTML =   "<div id=\"desc1\" style=\"color:white;\">" +fileResponse.message + "</div>";

                                             var x = document.getElementById("toast")
                                             x.className = "show";
                                             setTimeout(function(){ x.className = x.className.replace("show", ""); }, 5000);


                    }
                }


function closeProgressBar(){
    var singleFileUploadSuccess = document.querySelector('#portabullloader');
    singleFileUploadSuccess.innerHTML = "";

}

function startProgressBar(){
var singleFileUploadSuccess = document.querySelector('#portabullloader');
                singleFileUploadSuccess.innerHTML = "<div id=\"loader\"></div>";
}

function lockUnlockPdf(file,fileName,flag,password){

    if(flag == "true" || flag==true){
    lockPdf(file,fileName,password);
    }else{
    unLockPdf(file,fileName,password);
    }



}

function unLockPdf(file,fileName,password){


        if(onHold)
            return;

        onHold = true;

        startProgressBar();

         var xhr = new XMLHttpRequest();

            xhr.open("POST", pdfPasswordUnLockUrl);

            xhr.setRequestHeader("Accept", "application/json");

            xhr.setRequestHeader("Content-Type", "application/json");

             xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

            var data = JSON.stringify({"file": file, "fileName": fileName, "password": password});

            xhr.send(data);

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    console.log(xhr.responseText);

        onHold = false;



                        closeProgressBar();

                           const fileResponse = JSON.parse(xhr.responseText);

                                   if(fileResponse.statusCode == 503 || fileResponse.statusCode == 401 || fileResponse.message == "Unauthorized Access"){
                                                      alert(fileResponse.message);
                                            window.location.href = "index.html";
                                                  return;
                                             }

                                   if(fileResponse.status == "FAILED"){
                                            alert(fileResponse.message);
                                        return;
                                   }



                         var directorySpaceDiv = document.querySelector('#submitDownloadButton');

                           directorySpaceDiv.innerHTML = "";

                           isDownload = true;

                           directorySpaceDiv.innerHTML = "<input type=\"button\" onclick=\"downloadFileResponse1()\" value=\"Download\">";

                                downloadFileXHR = xhr;
                }
            };

}


function lockPdf(file,fileName,password){

        if(onHold)
            return;

        onHold = true;

        startProgressBar();

         var xhr = new XMLHttpRequest();

            xhr.open("POST", pdfPasswordLockUrl);

            xhr.setRequestHeader("Accept", "application/json");

            xhr.setRequestHeader("Content-Type", "application/json");

             xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

            var data = JSON.stringify({"file": file, "fileName": fileName, "password": password});

            xhr.send(data);

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    console.log(xhr.responseText);

        onHold = false;





                        closeProgressBar();

          const fileResponse = JSON.parse(xhr.responseText);

           if(fileResponse.statusCode == 503 || fileResponse.statusCode == 401 || fileResponse.message == "Unauthorized Access"){
                              alert(fileResponse.message);
                    window.location.href = "index.html";
                          return;
                     }

           if(fileResponse.status == "FAILED"){
                    alert(fileResponse.message);
                return;
           }


   var directorySpaceDiv = document.querySelector('#submitDownloadButton');

   directorySpaceDiv.innerHTML = "";

    isDownload = true;

   directorySpaceDiv.innerHTML = "<input type=\"button\" onclick=\"downloadFileResponse1()\" value=\"Download\">";

        downloadFileXHR = xhr;

                }
            };


}



function downloadFileResponse1(){
          const fileResponse = JSON.parse(downloadFileXHR.responseText);

                if (fileResponse.statusCode == 200) {

                     var a = document.createElement("a");
                       a.href =fileResponse.data.file;
                          a.download = fileResponse.data.fileName; //File name Here
                        a.click();

                    alert("File Downloaded Successfully");

                    window.location.href = "endepdf.html";


                } else {
                    alert(fileResponse.message);
                       window.location.href = "endepdf.html";

                    }
                }


                function submitDownloadClick(){

                    if(isDownload){
                            downloadFileResponse1();
                    }else{
                    submitPdf();
                    }

                }


                    function submitPdf(){

                    var file = window.localStorage.getItem("portabullUploadFileForPdfLock");

                    var fileName = window.localStorage.getItem("portabullUploadFileForPdfLockFileName");

                    var flag = window.localStorage.getItem("portabullLockPdfFlag");

                    if(file == undefined || flag==undefined){
                         window.location.href = "endepdf.html";
                    }

                        if(document.getElementById("pdfRePasswordPortabull").value == document.getElementById("pdfPasswordPortabull").value){
                            if(document.getElementById("pdfRePasswordPortabull").value == undefined || document.getElementById("pdfRePasswordPortabull").value == ""){
                            alert("Password is empty");
                            return;
                            }
                        }else{
                        alert("Password and Re-password not same");
                        return;
                        }

                        lockUnlockPdf(file,fileName,flag,document.getElementById("pdfPasswordPortabull").value );

                    }