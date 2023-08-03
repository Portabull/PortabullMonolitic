var tokenKey = 'token';
const shareDetails = new Map();

var onHold= false;
var userShareData;
var getShareDetailsUrl =  BASE_URL + 'DMS/notes/get-notes-share-details';
var shareDetailsToUserUrl = BASE_URL + 'DMS/notes/share-notes-details';
var checkUserExistsUrl = BASE_URL + 'DMS/notes/check-user-exists';
var removeUserShareAccess = BASE_URL + 'DMS/notes/remove-share-access';

var noteId  = new URL(window.location.href).searchParams.get("noteId");

var currentUserName;
var currentUserMessage;

init();
function init(){

populateShareDetails(noteId);

}

function populateShareDetails(noteId) {
try {

        if(onHold)
            return;

        onHold = true;

        var xhr = new XMLHttpRequest();

        xhr.open("POST", getShareDetailsUrl);

        xhr.setRequestHeader("Accept", "application/json");

        xhr.setRequestHeader("Content-Type", "application/json");

         xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

        var data = JSON.stringify({"noteId": noteId});

        xhr.send(data);

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;
                const response = JSON.parse(xhr.responseText);
                document.getElementById('sharableMembersSpace').innerHTML = '';
                userShareData = response.data;

                document.getElementById('input-name').disabled = false;
                	document.getElementById('input-message').disabled = false;
                document.getElementById('input-submit').disabled = false;
                    document.getElementById('checkBoxsPlace').hidden = true;

                    	document.getElementById('input-name').value = '';
                    	document.getElementById('input-message').value = '';

                for(i=0;i<userShareData.length;i++) {

                        var data =  userShareData[i];

                        var userName = data.su;

                        var message = data.m;

                        var isDelete = data.d;
                        var download = data.dw;
                        var edit = data.e;
                        var share = data.s;

                        document.getElementById('sharableMembersSpace').innerHTML = document.getElementById('sharableMembersSpace').innerHTML + "<button class=\"button87465415 button4354254v\" onclick=\"editUserShareSettings(" + isDelete +","+download+","+edit+","+share+"," + "'" + userName+ "','" + message + "')\">" + userName + "</button>";
                        document.getElementById('input-name').value = '';
                        document.getElementById('input-message').value = '';
                }



            }
        };
    } catch (err) {
        alert("Exception Occered");
        document.getElementById("loader").style.display = "none";
    }
}




    function redirectToNotesPage(){
     window.location.href = "dmsnotes.html";
    }


function addUserNameMessage() {


       try {

               if(onHold)
                   return;

               onHold = true;

                   var userName = document.getElementById('input-name').value;

                   var message = document.getElementById('input-message').value;

                   currentUserMessage = message;

                   currentUserName = userName;

               var xhr = new XMLHttpRequest();

               xhr.open("POST", checkUserExistsUrl);

               xhr.setRequestHeader("Accept", "application/json");

               xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

               var data = JSON.stringify({"noteId": noteId, "userName":userName});

               xhr.send(data);

               xhr.onreadystatechange = function () {
                   if (xhr.readyState === 4) {
                       console.log(xhr.status);
                       console.log(xhr.responseText);
                       onHold = false;
                       const response = JSON.parse(xhr.responseText);

                        if(xhr.status == 400){
                            alert(response.message);
                            return;
                        }

                        if(xhr.status == 401 || xhr.status == 503){
                        window.localStorage.setItem(pageRedirectionPort,"sharenotes1.html?noteId=" + new URL(window.location.href).searchParams.get("noteId"));
                            	window.location.href = "index.html";
                        }

                         document.getElementById('checkBoxsPlace').hidden = false;
                       	document.getElementById('input-name').disabled = true;
                       	document.getElementById('input-message').disabled = true;
                       	document.getElementById('input-submit').disabled = true;

                   }
               };
           } catch (err) {
               alert("Exception Occered");
               document.getElementById("loader").style.display = "none";
           }


}

function removeUserNameMessage() {
    try {

               if(onHold)
                   return;

               onHold = true;

               var xhr = new XMLHttpRequest();

               xhr.open("POST", removeUserShareAccess);

               xhr.setRequestHeader("Accept", "application/json");

               xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

               var data = JSON.stringify({"noteId": noteId, "userName":currentUserName});

               xhr.send(data);

               xhr.onreadystatechange = function () {
                   if (xhr.readyState === 4) {
                       console.log(xhr.status);
                       console.log(xhr.responseText);
                       onHold = false;
                       const response = JSON.parse(xhr.responseText);

                       if(xhr.status == 400) {
                            alert(response.message);
                       }
	document.getElementById('input-name').disabled = false;
	document.getElementById('input-message').disabled = false;
document.getElementById('input-submit').disabled = false;
    document.getElementById('checkBoxsPlace').hidden = true;

                       populateShareDetails(noteId);


                   }
               };
           } catch (err) {
               alert("Exception Occered");
               document.getElementById("loader").style.display = "none";
           }


}

function donothing() {

}

function editUserShareSettings(isDelete,isDownload,isEdit,isShare,userName,message) {

      document.getElementById('checkBoxsPlace').hidden = false;
      document.getElementById('input-name').disabled = true;
      document.getElementById('input-message').disabled = true;
      document.getElementById('input-submit').disabled = true;

document.getElementById("editButton").checked = isEdit;
document.getElementById("downloadButton").checked = isDownload;
document.getElementById("shareButton").checked = isShare;
document.getElementById("deleteButton").checked = isDelete;


      currentUserName = userName;
      currentUserMessage = message;

    	document.getElementById('input-name').value = userName;
	document.getElementById('input-message').value = message;
}


function shareUserNotes() {
      var userName = currentUserName;
        var message = currentUserMessage;

       try {

               if(onHold)
                   return;

               onHold = true;

               var xhr = new XMLHttpRequest();

               xhr.open("POST", shareDetailsToUserUrl);

               xhr.setRequestHeader("Accept", "application/json");

               xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

               var data = JSON.stringify({"noteId": noteId, "userName":currentUserName,"e": document.getElementById("editButton").checked
                ,"s": document.getElementById("shareButton").checked,"dw":document.getElementById("downloadButton").checked,"d":document.getElementById("deleteButton").checked,
                "m":currentUserMessage});

               xhr.send(data);

               xhr.onreadystatechange = function () {
                   if (xhr.readyState === 4) {
                       console.log(xhr.status);
                       console.log(xhr.responseText);
                       onHold = false;
                       const response = JSON.parse(xhr.responseText);

                        if(xhr.status == 400){
                            alert(response.message);
                        }

                       populateShareDetails(noteId);


                   }
               };
           } catch (err) {
               alert("Exception Occered");
               document.getElementById("loader").style.display = "none";
           }


}
