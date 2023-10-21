var getDirFiles = BASE_URL + 'DMS/get-dms-files';
var createDMSDir = BASE_URL + 'DMS/create-dms-dir';
var modifyDMSFileDir = BASE_URL + 'DMS/modify-dms-file-dir';
var uploadFileToDMSServer = BASE_URL + 'DMS/upload-multiple-files-to-dir';
var loginstaticimages;
loadStaticAssets(createStaticImages);

document.querySelector("#image-viewer .close").addEventListener("click", function () {
  document.querySelector("#image-viewer").style.display = "none";
});



function createStaticImages() {
     var loginstaticimages1 =  window.localStorage.getItem(loginstaticimagesConstKey);

    loginstaticimages  = new Map(Object.entries(JSON.parse(loginstaticimages1)));

}

var dirLevel = 0;

var currentDirName = '';
var currentDirId = '';

var parentDirId = '';
var parentDirName = '';

var enableDirName = '';
var enableDirId = '';

var selectedFileId = '';
var selectedFileName = '';

var onHold = false;

var currentCreateFolderDirId = '';


var renameIcon = "<a href=\"#\" id=\"aEditIconId\" onclick=\"renameDir()\"><i class='fas fa-pen' style='font-size:29px'></i></a>";

var viewIcon = "<a href=\"#\" id=\"aViewIconId\" onclick=\"viewFileContent()\"><i class='fa fa-eye' style='font-size:29px'></i></a>";

var deleteIcon = "<a href=\"#\" id=\"aDeleteIconId\" onclick=\"deleteDir()\"><i  class=\"material-icons md-48\" style='font-size:36px' >delete</i></a>";

var downloadIcon = "<a href=\"#\" id=\"aDownloadIconId\" onclick=\"myfunction1()\"><i  class=\"fa fa-download\" style='font-size:36px' ></i></a>";

var uploadFolderButtonIcon = "<input draggable=\"false\"  data-toggle=\"tooltip\"  onclick=\"uploadFileToDMS()\" data-placement=\"top\" title=\"Upload\" class=\"createFolderButton\" id=\"uploadFolderButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/upload.webp') + "\"/>";

var createFolderButtonIcon = "<input draggable=\"false\"  data-toggle=\"tooltip\" onclick=\"createDMSDirectory()\" data-placement=\"top\" title=\"Create Folder\" class=\"createFolderButton\" id=\"createFolderButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/createFolder.png') + "\"/>";

var openFolderButtonIcon = "<input draggable=\"false\"  data-toggle=\"tooltip\" onclick=\"openFolder()\" data-placement=\"top\" title=\"Open Folder\" class=\"createFolderButton\" id=\"createFolderButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/openFolder.jpg') + "\"/>";

var backButtonIcon = "<input draggable=\"false\"  data-toggle=\"tooltip\" onclick=\"goToPrevFolder()\" data-placement=\"top\" title=\"Back\" class=\"createFolderButton\" id=\"backButtonInput\" type=\"image\" src=\"" + loginstaticimages.get('static/images/back.jpg') + "\"/>";

var backButtonIconToMyfiles = "<input draggable=\"false\" data-toggle=\"tooltip\" onclick=\"redirectToDocumentsPage()\" data-placement=\"top\" title=\"Back\" class=\"createFolderButton\" id=\"backButtonInput\" type=\"image\" src=\"" + loginstaticimages.get('static/images/back.jpg') + "\"/>";

var myfilesIcons = document.querySelector('#myfilesIcons');


function getDmsFiles(dirId,dirStatus) {

    invokeLoader();

   onHold = true;

   var directorySpaceDiv = document.querySelector('#directorySpaceDiv');

    directorySpaceDiv.innerHTML = '';

   var xhrStorage = new XMLHttpRequest();

   xhrStorage.open("POST", getDirFiles);

   xhrStorage.setRequestHeader("Accept", "application/json");

   xhrStorage.setRequestHeader("Content-Type", "application/json");

   xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));

   hideAndUnHideIcons();

   sendRequest(xhrStorage,dirId, dirStatus);

   xhrStorage.onreadystatechange = function () {

      if (xhrStorage.readyState === 4) {

         stopLoader();

         onHold = false;

         var response = parseResponse(xhrStorage);

         var dirs = response.data.dirs;

         var currentDirFiles = response.data.currentFiles;

         parentDirId = response.data.parentDirId;

         if(checkFilesAreEmpty(dirs , currentDirFiles)){

             showEmptyFilesScreen();

             return;

         }

         showDirPath(response.data.dirStructure);

         populateDirectories(dirs,directorySpaceDiv);

         populateFiles(currentDirFiles,directorySpaceDiv);

      }
   };
}


function createDMSDirectory(){
 document.getElementById("myForm").style.display = "block";
}

function uploadFileToDMS(){
   document.getElementById("file-input").click();
}

function getFormattedDirName(dirName){
try{


return  dirName.replace(/[" "]/g, "@");
}
catch(err){
alert(err);
}


}

function getOrginalDirName(dirName) {
return  dirName.replace(/["@"]/g, " ");
}







function enableIcons(dirName,dirId){
enableDirName = getOrginalDirName(dirName);
enableDirId = dirId;

currentDirName = getOrginalDirName(dirName);
currentDirId = dirId;

if(document.getElementById("aDeleteIconId")!=undefined)
document.getElementById("aDeleteIconId").style.color = "#555555";

if(document.getElementById("aEditIconId")!=undefined)
document.getElementById("aEditIconId").style.color = "#555555";

console.log("Enable:"+dirName);
}


function renameDir(){
if(enableDirName=='' && selectedFileId==''){
return;
}

if(enableDirName!=''){
openEditFolderBox();


document.getElementById("editFolderNameId").value = enableDirName;

}else{
    openFileEditFolderBox();


document.getElementById("fileEditFolderNameId").value = selectedFileName;

}

}

function viewFileContent(){

if(enableDirName=='' && selectedFileId==''){
return;
}



if(enableDirName!=''){
    return;
}else{


executeRestCall( BASE_URL + "DMS/download-documents" + "?documentId=" + selectedFileId  ,null,"GET",viewDocumentData,null);



}

}

function viewDocumentData(xhr,response){

 if (response.statusCode == 200) {

    document.querySelector("#full-image").src = response.data.file;
     document.querySelector("#image-viewer").style.display = "block";

                }
}


function deleteDir(){
if(enableDirName==''  && selectedFileId==''){
return;
}


if(enableDirName!=''){
openDeleteFolderBox();


document.getElementById("deleteFolderNameId").value = enableDirName;;

}else{
  openDeleteFileBox();


document.getElementById("fileDeleteFolderNameId").value = selectedFileName;

}






}


function openFolder(){
    if(enableDirName == ''){
    return;
    }

    disableEditIcons();

    goToNextFolder(currentDirName,currentDirId,'n');
}

function handleErrors(resp,status) {
    if(status==401){
        alert("Session Expired, Please Login again");
           window.localStorage.setItem(pageRedirectionPort,"myfiles.html");
        window.location.href = "index.html";
    }

    if(status==400){
        alert(resp.message);
        return;
    }

    if(status==503 || status!=200){
        alert(resp.message);
        window.location.href = "index.html";
    }

}



  function createDirSpace() {

    invokeLoader();

    onHold = true;

   var directorySpaceDiv = document.querySelector('#directorySpaceDiv');

   var tempInnerHTML = directorySpaceDiv.innerHTML;

   directorySpaceDiv.innerHTML = '';

    var xhrStorage = new XMLHttpRequest();

     xhrStorage.open("POST", createDMSDir);

     xhrStorage.setRequestHeader("Accept", "application/json");

     xhrStorage.setRequestHeader("Content-Type", "application/json");

     xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));

     var directoryFullName= document.getElementById("folderNameId").value;

     xhrStorage.send(JSON.stringify({
                 "dirName": getOrginalDirName(directoryFullName),
                 "level":dirLevel,
                 "parentDirId": currentCreateFolderDirId
                 }));


  xhrStorage.onreadystatechange = function () {

      if (xhrStorage.readyState === 4) {

         stopLoader();

         onHold = false;

         var resp = JSON.parse(xhrStorage.responseText);

               handleErrors(resp,xhrStorage.status);

               if(xhrStorage.status==400)
               {
                  directorySpaceDiv.innerHTML = tempInnerHTML;
               return;
               }


            var  directoryFullNameFormatted = directoryFullName;

           directoryFullName = getFormattedDirName(directoryFullName);

        var m1 =    "<label class=\"not-bold\"> <input draggable=\"false\"  ondblclick=goToNextFolder('" + directoryFullName + "'," +  resp.data + ")  onclick=enableIcons('" + directoryFullName + "'," +  resp.data + ")" + "  data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + directoryFullNameFormatted + "\" class=\"filesButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/folder.jpg') + "\" id=\"filesButton1\"/><br>";

        var m2='</label>'

        var dirName = '';
           if(directoryFullNameFormatted.length > 11){
             dirName =   directoryFullNameFormatted.substring(0, 11) + "..."
            }else{
                dirName = directoryFullNameFormatted;
            }

        var innerHt = m1 + dirName + m2;

        directorySpaceDiv.innerHTML = tempInnerHTML +  innerHt;

        }
        };

  }

function goToNextFolder(dirName,dirId) {

        if(onHold)
            return;

        disableEditIcons();

        dirLevel++;

        hideAndUnHideIcons();

        currentCreateFolderDirId = dirId;

        getDmsFiles(dirId,'n');

}

function goToPrevFolder() {

    if(onHold)
        return;

    disableEditIcons();

    if(dirLevel != 0) {
        dirLevel--;
    }

    hideAndUnHideIcons();

    currentCreateFolderDirId = parentDirId;

    if(parentDirId == '' || parentDirId == undefined || parentDirId == null) {
        return;
    }

    if(parentDirId == 'root'){
        getDmsFiles();
        return;
    }

    getDmsFiles(parentDirId,'p');

}


function hideAndUnHideIcons() {

        myfilesIcons.innerHTML = '';

        if(dirLevel != 0) {

            myfilesIcons.innerHTML =  renameIcon + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + downloadIcon + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + deleteIcon + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + viewIcon + '<br>' + backButtonIcon + createFolderButtonIcon + uploadFolderButtonIcon + openFolderButtonIcon ;

        } else{

            myfilesIcons.innerHTML = backButtonIconToMyfiles + openFolderButtonIcon + '<br><br><br><br><br><br>';

        }

}

function sendRequest(xhrStorage,dirId,dirStatus) {

    if(dirId==undefined || dirId==null) {
        xhrStorage.send(JSON.stringify({
            "fetchRootDirs": true,
            "ds":dirStatus
            }));
     } else{
        xhrStorage.send(JSON.stringify({
            "dirId": dirId,
            "ds":dirStatus
            }));
      }

}

function parseResponse(xhrStorage) {

         console.log(xhrStorage.status);

         console.log(xhrStorage.responseText);

         var response = JSON.parse(xhrStorage.responseText);

         handleErrors(response, xhrStorage.status);

         return response;

}


function checkFilesAreEmpty(dirs , currentDirFiles) {

        return  (dirs ==null || dirs==undefined || dirs.length==0) && (currentDirFiles ==null || currentDirFiles==undefined || currentDirFiles.length==0);

}

function showEmptyFilesScreen() {

       var directorySpaceDiv = document.querySelector('#directorySpaceDiv');

       directorySpaceDiv.innerHTML = '<p>No Files there in the current directory</p>';

}

function showDirPath(dirNames) {

//        var myfilesPath= document.querySelector('#myfilesPath');
//
//        var filePath = '';
//
//        for(i=0;i<dirNames.length;i++){
//
//            filePath = filePath + '<a>' + dirNames.dirName + '</a>/' ;
//
//        }
//
//        myfilesPath.innerHTML = filePath;

}

function populateDirectories(dirs,directorySpaceDiv) {

        var directoryFullName = '';

        var directoryName ='';

        var innerHtmlDirSpace = '';

        for(i = 0; i < dirs.length; i++){

            directoryFullName = getFormattedDirName(dirs[i].dirName);

            var dirId =  dirs[i].dirId;

            if(directoryFullName.length > 11){

             directoryName =   directoryFullName.substring(0, 11) + "..."

            } else {

                directoryName = directoryFullName;

            }

            directoryName = getOrginalDirName(directoryName);

            directoryFullNameFormatted = getOrginalDirName(directoryFullName);

            innerHtmlDirSpace = innerHtmlDirSpace + "<label class=\"not-bold\"> <input draggable=\"false\"  ondblclick=goToNextFolder('" + directoryFullName + "'," + dirId + ") onclick=enableIcons('" + directoryFullName + "'," + dirId + ")" + " data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + directoryFullNameFormatted + "\" class=\"filesButton\" type=\"image\" src=\"" + loginstaticimages.get('static/images/folder.jpg') + "\" id=\"filesButton1\"/><br>" + directoryName + "</label>";

     }

     directorySpaceDiv.innerHTML = innerHtmlDirSpace;

}

function downloadFile(fileName,fileId) {

    if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".tiff") ||
     fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".hif") ||
     fileName.endsWith(".hcic") || fileName.endsWith(".webp"))
     {
            selectedFileId = fileId;
            viewFileContent();

             return;
     }



    myfunction1(fileId);

}


function enableDownloadFile(fileName,fileId) {

    selectedFileId = fileId;

    selectedFileName = fileName;

}

function populateFiles(files,directorySpaceDiv) {

   var fileFullName = '';

        var fileName ='';

        var innerHtmlDirSpace = '';

        for(i = 0; i < files.length; i++){

            fileFullName = getFormattedDirName(files[i].fileName);

            var dirId =  files[i].fileId;

            if(fileFullName.length > 11){

             fileName =   fileFullName.substring(0, 11) + "..."

            } else {

                fileName = fileFullName;

            }

            fileName = getOrginalDirName(fileName);

            fileFullNameFormatted = getOrginalDirName(fileFullName);

            var fileNameFormat = '';

            if(fileFullName.endsWith(".png") || fileFullName.endsWith(".jpg") || fileFullName.endsWith(".jpeg") || fileFullName.endsWith(".webp") || fileFullName.endsWith(".tiff")) {
                fileNameFormat = loginstaticimages.get('static/images/imageIcon.png');
            }else if(fileFullName.endsWith(".pdf")) {
                 fileNameFormat = loginstaticimages.get('static/images/pdfIcon.webp');
            }else if(fileFullName.endsWith(".mp3")) {
                fileNameFormat = loginstaticimages.get('static/images/mp3Icon.webp');
            }else if(fileFullName.endsWith(".mp4")) {
                fileNameFormat = loginstaticimages.get('static/images/mp4Icon.png');
            } else{
              fileNameFormat = loginstaticimages.get('static/images/fileIcon.jpg');;
            }

            innerHtmlDirSpace = innerHtmlDirSpace + "<label class=\"not-bold\"> <input draggable=\"false\"  ondblclick=downloadFile('" + fileFullName + "'," + dirId + ") onclick=enableDownloadFile('" + fileFullName + "'," + dirId + ")" + " data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + fileFullNameFormatted + "\" class=\"filesButton\" type=\"image\" src=\"" + fileNameFormat + "\" id=\"filesButton1\"/><br>" + fileName + "</label>";

     }

        directorySpaceDiv.innerHTML = directorySpaceDiv.innerHTML + innerHtmlDirSpace;

}

function disableEditIcons() {

    enableDirId = '';

    enableDirName = '';

    selectedFileId = '';

    selectedFileName = '';

    closeForm();

    closeEditForm();

    closeDeleteForm();

    closeFileEditForm();

    closeFileDeleteForm();

}

 function openNewFolderBox(){
    document.getElementById("myForm").style.display = "block";
    }


 function openEditFolderBox(){
    document.getElementById("myEditForm").style.display = "block";
    }


 function openDeleteFolderBox(){
    document.getElementById("myDeleteForm").style.display = "block";
    }

 function openDeleteFileBox(){
    document.getElementById("myFileDeleteForm").style.display = "block";
    }
 function openFileEditFolderBox(){
    document.getElementById("myFileEditForm").style.display = "block";
    }


function closeForm() {
   document.getElementById("myForm").style.display = "none";
}


function closeEditForm() {
   document.getElementById("myEditForm").style.display = "none";
}


function closeFileEditForm() {
   document.getElementById("myFileEditForm").style.display = "none";
}

function closeDeleteForm() {
   document.getElementById("myDeleteForm").style.display = "none";
}
function closeFileDeleteForm() {
   document.getElementById("myFileDeleteForm").style.display = "none";
}


function ifBoxClosed() {

    if(document.getElementById("myForm").style.display == 'none' &&
     document.getElementById("myDeleteForm").style.display == 'none' &&
     document.getElementById("myEditForm").style.display == 'none' &&
     document.getElementById("myFileEditForm").style.display == 'none' &&
     document.getElementById("myFileDeleteForm").style.display == 'none'){
        return true;
    }

    return false;
}

function redirectToDocumentsPage() {
      window.location.href = "documents.html";
}




  function myfunction1(documentId){

            invokeLoader();

            if(documentId == undefined || documentId ==null){
                documentId = selectedFileId;
            }


           var xhr = new XMLHttpRequest();
           xhr.open("GET",BASE_URL + "DMS/download-documents" + "?documentId="+documentId);

           xhr.setRequestHeader("Accept", "application/json");


           xhr.setRequestHeader("Content-Type", "application/json");
           xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

           var data = JSON.stringify({});

           xhr.send(data);


             xhr.onreadystatechange = function () {

                    stopLoader();

                    if (xhr.readyState === 4) {
                        console.log(xhr.status);

                         const fileResponse = JSON.parse(xhr.responseText);

                if (fileResponse.statusCode == 200) {



                     var a = document.createElement("a");
                       a.href =fileResponse.data.file;
                          a.download = fileResponse.data.fileName; //File name Here
                        a.click();

                } else {
                           alert(fileResponse.message);
                    }
                }

                    };
                }




  function createFileSpace(files) {

     myfilesIcons.innerHTML = '';

   var directorySpaceDiv = document.querySelector('#directorySpaceDiv');

    directorySpaceDiv.innerHTML = '';

    var errorMessageScroll = document.querySelector('#errorMessageScroll');

    errorMessageScroll.innerHTML = '<h4 class=\"uploadLoading\" style="text-align: center; color: #4CAF50;">Uploading files to server, please wait</h4>';

    invokeLoader();

    onHold = true;

     var formData = new FormData();

          for (var index = 0; index < files.length; index++) {

              formData.append("file", files[index]);

          }

          var xhr = new XMLHttpRequest();

          xhr.open("POST", uploadFileToDMSServer + "?dirId="+currentCreateFolderDirId);

           xhr.setRequestHeader("Accept", "application/json");

           xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

          xhr.send(formData);

          xhr.onload = function () {

              stopLoader();

              errorMessageScroll.innerHTML = '';

              var response = parseResponse(xhr);

              populateErrorMessageIfAny(response);

              reloadCurrentDir(currentCreateFolderDirId)

          };

  }




function reloadCurrentDir(dirId) {

        disableEditIcons();

        hideAndUnHideIcons();

        getDmsFiles(dirId,'s');

}

function populateErrorMessageIfAny(response){

   var errorMessageScroll = document.querySelector('#errorMessageScroll');

    errorMessageScroll.innerHTML = '';

    var errorMessage = '';

   for(i=0;i<response.data.length;i++){

    var fileResp = response.data[i];

        if(fileResp.status == "FAILED") {
                errorMessage = errorMessage + '<p style="color:red; font-size:15px;"> File Name(' + fileResp.data + ') Message(' + fileResp.message + ')</p>' + '<br>';
        }
     }

       if(errorMessage!=''){
                 errorMessage = '<p style="color:red; font-size:15px;">Error : (Below files are not able to upload to the server) <p><br>' + errorMessage + '<button onclick="clearUploadErrorMessage()">OK</button>';
       }

   errorMessageScroll.innerHTML = errorMessage;


}


function clearUploadErrorMessage(){

   var errorMessageScroll = document.querySelector('#errorMessageScroll');

   errorMessageScroll.innerHTML = '';

}


function invokeLoader(){
     document.getElementById("uploadLoading123").hidden = false;
}

function stopLoader(){
 document.getElementById("uploadLoading123").hidden = true;
}


//function invokeLoader(){
// portabullloaderQ = document.querySelector('#portabullloader');
//      portabullloaderQ.style.display = "";
//        portabullloaderQ.innerHTML = "<div id=\"loader\"></div>";
//}
//
//function stopLoader(){
//            portabullloaderQ.style.display = "none";
//}

function editFileDirectory(mType) {

    invokeLoader();

    onHold = true;

    var xhrStorage = new XMLHttpRequest();

     xhrStorage.open("POST", modifyDMSFileDir);

     xhrStorage.setRequestHeader("Accept", "application/json");

     xhrStorage.setRequestHeader("Content-Type", "application/json");

     xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));

     var editedFileName= document.getElementById("fileEditFolderNameId").value;

     var editedDirName= document.getElementById("editFolderNameId").value;

     xhrStorage.send(JSON.stringify({
                 "mt": mType,
                 "dirId": enableDirId,
                 "dirName":editedDirName,
                 "fileId": selectedFileId,
                 "fileName":editedFileName
                 }));


  xhrStorage.onreadystatechange = function () {

      if (xhrStorage.readyState === 4) {

         stopLoader();

         onHold = false;

         var resp = JSON.parse(xhrStorage.responseText);

               handleErrors(resp,xhrStorage.status);

                reloadCurrentDir(currentCreateFolderDirId);
        }
        };



}