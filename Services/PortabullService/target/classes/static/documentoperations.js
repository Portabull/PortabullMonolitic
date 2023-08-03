'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');


var tokenKey = 'token'

function uploadSingleFile(file,text) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", BASE_URL + "DMS/createPassword" + "?password=" + text);

    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

    xhr.onload = function () {
        console.log(xhr.responseText);

        downloadFileResponse(xhr,"Successfully created password protected file please check its already downloaded");
    }

    xhr.send(formData);
}

function uploadMultipleFiles(file,text) {
    var formData = new FormData();
      formData.append("file", file);

      var xhr = new XMLHttpRequest();
      xhr.open("POST", BASE_URL + "DMS/removePassword" + "?password=" + text);

      xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

      xhr.onload = function () {

          console.log(xhr.responseText);

          downloadFileResponse(xhr,"Successfully password removed please check its already downloaded");
      }

      xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function (event) {
    var files = singleFileUploadInput.files;
    var text = createpasword.value;
     if(text.length==0){
        multipleFileUploadError.innerHTML = "Password Should Not be empty";

        }
    if (files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0],text);
    event.preventDefault();
}, true);


multipleUploadForm.addEventListener('submit', function (event) {
    var files = multipleFileUploadInput.files;
    var text =removepassword.value;
    if(text.length==0){
    multipleFileUploadError.innerHTML = "Password Should Not be empty";
             multipleFileUploadError.style.display = "block";
    }

    if (files.length === 0) {
        multipleFileUploadError.innerHTML = "Please select a file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files[0],text);
    event.preventDefault();
}, true);


function saveBlob(blob1, fileName) {
      var blob = new Blob([blob1], {type: 'application/pdf'});
          //Create a link element, hide it, direct
          //it towards the blob, and then 'click' it programatically
          let a = document.createElement("a");
          a.style = "display: none";
          document.body.appendChild(a);
          //Create a DOMString representing the blob
          //and point the link element towards it
          let url = window.URL.createObjectURL(blob);
          a.href = url;
          a.download = fileName;
          //programatically click the link to trigger the download
          a.click();
          //release the reference to the file by revoking the Object URL
          window.URL.revokeObjectURL(url);
}

function isJson(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}



function downloadFileResponse(xhr,message){
          const fileResponse = JSON.parse(xhr.responseText);

                if (fileResponse.statusCode == 200) {



                     var a = document.createElement("a");
                       a.href =fileResponse.data.file;
                          a.download = fileResponse.data.fileName; //File name Here
                        a.click();
                        alert(message);
                } else {
                           alert(fileResponse.message);
                    }
                }
