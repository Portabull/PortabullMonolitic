const addBox = document.querySelector(".add-box"),
popupBox = document.querySelector(".popup-box"),
popupTitle = popupBox.querySelector("header p"),
closeIcon = popupBox.querySelector("header i"),
titleTag = popupBox.querySelector("input"),
descTag = popupBox.querySelector("textarea"),
addBtn = popupBox.querySelector("button");
var onHold=false;
var currentNoteIdCashe = null;
const months = ["January", "February", "March", "April", "May", "June", "July",
              "August", "September", "October", "November", "December"];
const notes = JSON.parse(localStorage.getItem("notes") || "[]");
let isUpdate = false, updateId;

var tokenKey = 'token';
var noNotificationMessage = '<p style=\"color:white\">No Notifications present</p>';

const notesCashe=  new Map();
var isNotificationScreen = false;
var getNewNotifications =BASE_URL + 'DMS/notes/get-notifications';

var notificationCount = "0";
var checkNotescountUrl = BASE_URL + 'DMS/notes/check-notes-count';
var getNotesUrl = BASE_URL + 'DMS/notes/get-notes';
var notesUrl = BASE_URL + 'DMS/notes/update-notes';
var downloadFromServerUrl =  BASE_URL + 'DMS/notes/download-notes';
var hideUnHideSingleUrl = BASE_URL + 'DMS/notes/settings';
var acceptRejectNotificationUrl = BASE_URL + 'DMS/notes/accept-reject-notification';

var currentPendingNotes;
addBox.addEventListener("click", () => {
    popupTitle.innerText = "Add a new Note";
    addBtn.innerText = "Add Note";
    popupBox.classList.add("show");
    document.querySelector("body").style.overflow = "hidden";
    if(window.innerWidth > 660) titleTag.focus();
});

closeIcon.addEventListener("click", () => {
    isUpdate = false;
    currentNoteIdCashe = null;
    document.getElementById("errorMessageDiv4999").innerHTML  = "";
    titleTag.value = descTag.value = "";
    popupBox.classList.remove("show");
    document.querySelector("body").style.overflow = "auto";
});

function showNotes() {
getAllnotes();
//    if(!notes) return;
//    document.querySelectorAll(".note").forEach(li => li.remove());
//    notes.forEach((note, id) => {
//        let filterDesc = note.description.replaceAll("\n", '<br/>');
//        let liTag = `<li class="note">
//                        <div class="details">
//                            <p>${note.title}</p>
//                            <span>${filterDesc}</span>
//                        </div>
//                        <div class="bottom-content">
//                            <span>${note.date}</span>
//                            <div class="settings">
//                                <i onclick="showMenu(this)" class="uil uil-ellipsis-h"></i>
//                                <ul class="menu">
//                                    <li onclick="updateNote(${id}, '${note.title}', '${filterDesc}')"><i class="uil uil-pen"></i>Edit</li>
//						<li onclick="downloadNote(${id})"><i class="uil uil-download-alt"></i>Download</li>
//                                    <li onclick="deleteNote(${id})"><i class="uil uil-trash"></i>Delete</li>
//                                </ul>
//                            </div>
//                        </div>
//                    </li>`;
//        addBox.insertAdjacentHTML("afterend", liTag);
//    });
}
showNotes();
init();

function init(){
    setInterval(reloadNotifications, 5000);
}

function showMenu(elem) {
    elem.parentElement.classList.add("show");
    document.addEventListener("click", e => {
        if(e.target.tagName != "I" || e.target != elem) {
            elem.parentElement.classList.remove("show");
        }
    });
}

function deleteNote(noteId) {
    let confirmDel = confirm("Are you sure you want to delete this note?");
    if(!confirmDel) return;

    modifyNotes(null,null,true,noteId);

    showNotes();
}

function downloadNote(noteId,createdDate,updatedDate) {


if(!notesCashe.get(noteId).cv){
downloadFileFromServer(noteId);
return;
}

  var tittle = notesCashe.get(noteId).tittle;
 const link = document.createElement("a");
 var aaaa = notesCashe.get(noteId).note;
   const content =  "Title : \n\t" + tittle + "\nNotes : \n\t" + aaaa + "\nCreated Date : \n\t" + createdDate + "\nUpdated Date : \n\t" + updatedDate;


 const file = new Blob([content], { type: 'text/plain' });
         link.href = URL.createObjectURL(file);
         link.download = tittle + ".txt";
         link.click();
         URL.revokeObjectURL(link.href);

  
}

function showSize(noteId){

    var size = notesCashe.get(noteId).size;
    var createdDate = notesCashe.get(noteId).createdDate;
    var updatedDate = notesCashe.get(noteId).updatedDate;

    var anotherProps = '';

    if(notesCashe.get(noteId).s) {
        anotherProps = "\nShared By : " + notesCashe.get(noteId).sharedBy + "\nMessage : " + notesCashe.get(noteId).message;
    }

    alert("Size : "+size + "\nCreated Date : " + createdDate + "\nUpdated Date : " + updatedDate + anotherProps);
}


function updateNote(noteId) {
currentNoteIdCashe = noteId;

var selectedNotes = notesCashe.get(noteId);

var errorInnerHtml = "<p style=\"color:red;\" id=\"errorMessageHigh\">Only 4999 lines are displayed here you can expand the editor or please download the file and edit in local editor and upload text here</p>";

if(!selectedNotes.cv){
document.getElementById("errorMessageDiv4999").innerHTML  = errorInnerHtml;
}else{
document.getElementById("errorMessageDiv4999").innerHTML  = "";
}

     let filterDesc = selectedNotes.note.replaceAll("\n", '<br/>');

    let description = filterDesc.replaceAll('<br/>', '\r\n');
    updateId = noteId;
    isUpdate = true;
    addBox.click();
    titleTag.value = selectedNotes.tittle;
    descTag.value = description;
    popupTitle.innerText = "Update a Note";
    addBtn.innerText = "Update Note";
}

addBtn.addEventListener("click", e => {
    e.preventDefault();
    let title = titleTag.value.trim(),
    description = descTag.value.trim();

      if(isUpdate){
      var selectedNotes = notesCashe.get(updateId);
        if(title == selectedNotes.tittle && description== selectedNotes.note){
        closeIcon.click();
                return;
        }
      }

    modifyNotes(title,description,false,null);

});










































function modifyNotes(tittle,note,isdeleteflag,noteId){
  if(onHold )
            return;

        onHold = true;

           var xhr = new XMLHttpRequest();

                xhr.open("POST", notesUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));


                var data = null;

                if(isUpdate){
                data = JSON.stringify({"noteId":updateId,"tittle":tittle,"note":note,"delete":isdeleteflag});
                }else if(isdeleteflag){
               data =  JSON.stringify({"noteId":noteId,"tittle":tittle,"note":note,"delete":isdeleteflag});
                }else{
                data =  JSON.stringify({"noteId":null,"tittle":tittle,"note":note,"delete":isdeleteflag});
                }

                xhr.send(data);


                try{
 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;
                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                    return;
                }

                if(response.status == "FAILED"){
                    alert(response.message);
                    return;
                }

                   if(isUpdate){
                   isUpdate = false;
                   currentNoteIdCashe = null;
                   }


               window.location.href = "dmsnotes.html";

};
    }
    } catch (err) {
        alert("Exception Occered");
    }


}




 function shareNotes(noteId){
           window.location.href = "sharenotes1.html?noteId="+noteId;
        }




function getAllnotes() {

    try {

        if(onHold )
            return;

        onHold = true;

        var xhr = new XMLHttpRequest();

        xhr.open("POST", getNotesUrl);

        xhr.setRequestHeader("Accept", "application/json");

        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

        var data = JSON.stringify({});

        xhr.send(data);

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;

                if(xhr.status == 405) {
                    alert("Server might be down please try after sometime");
                    window.location.href = "index.html";
                }

                const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401 && response.status == "FAILED"){
                    alert("Session Expired, Please login again.");
                          window.localStorage.setItem(pageRedirectionPort,"dmsnotes.html");
                    window.location.href = "index.html";
                }else if(xhr.status == 503){
                    alert(response.message);
                    window.location.href = "index.html";
                }

                 document.querySelectorAll(".note").forEach(li => li.remove());

                   notesCashe.clear();

                    document.getElementById("notificationIconSpace").innerHTML = '';
                    var tempNotificationIconSpace = "<button type=\"button\" class=\"icon-button\" onclick=\"openNav()\"><span class=\"material-icons\">notifications</span>";

                    notificationCount = response.data.nc + "";

                     if(response.data.nc != 0) {
     document.getElementById("notificationIconSpace").innerHTML = tempNotificationIconSpace + "<span class=\"icon-button__badge\">" + response.data.nc + "</span>" + "</button>";
                     } else {
                     document.getElementById("notificationIconSpace").innerHTML = tempNotificationIconSpace + "</button>";
                     }



                    document.getElementById("notificationsSpace").innerHTML = '';

                     currentPendingNotes  = response.data.pendingNotes;

                    var notificationsSpaceHtml = '';
                         for(i = 0; i < response.data.pendingNotes.length; i++) {

                                          var eachPendingNote = response.data.pendingNotes[i];

                                        notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Tittle : " + eachPendingNote.tittle + "</p>" ;

                                           notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Shared By : " + eachPendingNote.sharedBy + "</p>";

                                            notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Message : " + eachPendingNote.message + "</p>";

          notificationsSpaceHtml = notificationsSpaceHtml +   "<div align=\"center\"> <button onclick=" + "\"acceptButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton\">Accept</button><button onclick="+
           "\"rejectButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton redRejectButton\">Reject</button></div><br>";

                         }

                         if(notificationsSpaceHtml == undefined){
                            document.getElementById("notificationsSpace").innerHTML =  noNotificationMessage;
                         }else{
    document.getElementById("notificationsSpace").innerHTML = notificationsSpaceHtml;
                         }

                 for(i = 0; i < response.data.notes.length; i++){

                        var eachNote = response.data.notes[i];

                   var hiddenStatus = eachNote.hidden ? "<span style=\"color:red\"><b>hidden</b></span>" : "";

                   var hideUnHideButton = eachNote.hidden ? "UnHide" : "Hide";

                    var hideUnhideDeleteButton = '';
                    if(response.data.hideUnHideDeleteButton == null || !response.data.hideUnHideDeleteButton) {
                    if(eachNote.edb == undefined || eachNote.edb == true)
                         hideUnhideDeleteButton = " <li onclick=\"deleteNote(" +  eachNote.noteId + ")\"><i class=\"uil uil-trash\"></i>Delete</li>";
                    }


var hideUnhideShareButton = eachNote.esb == undefined || eachNote.esb == true ? "<li onclick=\"shareNotes("+ eachNote.noteId + ")\"><i class=\"uil uil-list-ul\"></i>Share</li>" : "";

var hideUnhideButtonForShare = eachNote.s ? "" : "<li onclick=\"hideUnHide('" + hideUnHideButton + "'," + eachNote.noteId + ")\"><i class=\"uil uil-list-ul\"></i>" + hideUnHideButton + "</li>";

var hideUnhideDownloadButton = eachNote.edwb == undefined || eachNote.edwb == true ? "<li onclick=\"downloadNote(" + eachNote.noteId + ",'" + eachNote.createdDate + "','" + eachNote.updatedDate + "')\"><i class=\"uil uil-download-alt\"></i>Download</li>" : "";

                        notesCashe.set(eachNote.noteId ,eachNote);

                           let filterDesc =  eachNote.note.replaceAll("\n", '<br/>');

                                let liTag = `<li class="note">
                                                <div class="details">
                                                    <p>${eachNote.tittle}</p>
                                                    <span>${filterDesc}</span>
                                                </div>
                                                <div class="bottom-content">
                                                    <span>CD : ${eachNote.createdDate} <br> UD : ${eachNote.updatedDate} <br> ${hiddenStatus}</span>
                                                    <div class="settings">
                                                        <i data-toggle="tooltip" data-placement="top" title="Menu" onclick="showMenu(this)" class="uil uil-ellipsis-h"></i>
                                                        <br>
                                                        <i data-toggle="tooltip" data-placement="top" title="Expand" to onclick="expandNotes( ${eachNote.noteId})" class="uil uil-expand-arrows-alt"></i>


                                                        <ul class="menu">
                                                            <li onclick="updateNote(${eachNote.noteId})"><i class="uil uil-pen"></i>Edit</li>
                                                            ${hideUnhideButtonForShare}
                                                            ${hideUnhideDeleteButton}
                        						${hideUnhideDownloadButton}
                        						<li onclick="showSize(${eachNote.noteId})"><i class="uil uil-list-ul"></i>Properties</li>
                                                ${hideUnhideShareButton}
                                                        </ul>
                                                    </div>
                                                </div>
                                            </li>`;
                                addBox.insertAdjacentHTML("afterend", liTag);



                 }
            }
        };
    } catch (err) {
        alert("Exception Occered");
        document.getElementById("loader").style.display = "none";
    }


}

function acceptButton(sni) {
acceptRejectNotification(sni,true);
}


function rejectButton(sni) {
acceptRejectNotification(sni,false);
}


function redirectToDocsPage(){
 window.location.href = "documents.html";
}

function downloadFileFromServer(noteId){
     try {

            if(onHold )
                return;

            onHold = true;

            var xhr = new XMLHttpRequest();

            xhr.open("POST", downloadFromServerUrl);

            xhr.setRequestHeader("Accept", "application/json");

            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

            var data = JSON.stringify({"noteId":noteId});

            xhr.send(data);

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    onHold = false;
                    const link = document.createElement("a");

                     const file = new Blob([xhr.responseText], { type: 'text/plain' });
                             link.href = URL.createObjectURL(file);
                             var selectedNotes = notesCashe.get(noteId);
                             link.download = selectedNotes.tittle + ".txt";
                             link.click();
                             URL.revokeObjectURL(link.href);
                }
            };
        } catch (err) {
            alert("Exception Occered");

        }
}



function hideUnHide(hideUnHideFlag,noteId){
 if(onHold )
            return;

        onHold = true;

           var xhr = new XMLHttpRequest();

                xhr.open("POST", hideUnHideSingleUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var data  = JSON.stringify({"noteId":noteId,"serviceCode":"ED-HIDE-SINGLE","enableDisableHiddenNotes": hideUnHideFlag == "UnHide" ? false : true});

                xhr.send(data);


                try{
 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;
                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                    return;
                }

                if(response.status == "FAILED"){
                    alert(response.message);
                    return;
                }


                showNotes();
                closeIcon.click();

};
    }
    } catch (err) {
        alert("Exception Occered");
    }

}

function uploadFileToDMS() {
 document.getElementById("file-input").click();
}


window.onload = function()
        {
        document.getElementById('file-input').onchange =
          function(e) {
                var fileInput = document.querySelector('#file-input');
              var files = fileInput.files;



                  var reader = new FileReader();


                      var file = files[0];

                      reader.readAsText(file);

                                  reader.onload=function(){


                            modifyNotes(file.name.substring(0, file.name.length - 4),reader.result,false,null);



                                             }






            }
        }




function acceptRejectNotification(sni,acceptFlag) {
if(onHold )
            return;

        onHold = true;

           var xhr = new XMLHttpRequest();

                xhr.open("POST", acceptRejectNotificationUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var data  = JSON.stringify({"sni":sni,"acceptFlag":acceptFlag});

                xhr.send(data);


                try{
 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;
                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                    return;
                }

                if(response.statusCode == 400){
                    alert(response.message);
                    window.location.href = "dmsnotes.html";
                    return;
                }

                if(response.status == "FAILED"){
                    alert(response.message);
                    return;
                }


    document.getElementById("notificationsSpace").innerHTML = '';

                    var notificationsSpaceHtml;
                    var tempCurrentPendingNotes = [];
                    var i1 = 0;
                         for(i = 0; i < currentPendingNotes.length; i++) {

                                          var eachPendingNote = currentPendingNotes[i];

                                          if(eachPendingNote.sni != sni) {

                                          tempCurrentPendingNotes[i1] = eachPendingNote;

                                          i1++;

                                        notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Tittle : " + eachPendingNote.tittle + "</p>" ;

                                           notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Shared By : " + eachPendingNote.sharedBy + "</p>";

                                            notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Message : " + eachPendingNote.message + "</p>";

          notificationsSpaceHtml = notificationsSpaceHtml +   "<div align=\"center\"> <button onclick=" + "\"acceptButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton\">Accept</button><button onclick="+
           "\"rejectButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton redRejectButton\">Reject</button></div>";

                                          }

                         }

                      currentPendingNotes =   tempCurrentPendingNotes;

                      if(notificationsSpaceHtml == undefined){
                      document.getElementById("notificationsSpace").innerHTML = noNotificationMessage;
                      }else{
    document.getElementById("notificationsSpace").innerHTML = notificationsSpaceHtml;
                      }





};
    }


}   catch (err) {
         alert("Exception Occered");
     }

     }




     function expandNotes(noteId){
         document.getElementById("errorMessageDiv4999").innerHTML  = "";
        window.location.href = "expandstickynotes.html?noteId=" + noteId;
     }

     function expandNotesPage(){
     expandNotes(currentNoteIdCashe);
     }



function sadadfadfdafa(){

     window.location.href = "settingsnotes.html";
}

function showHoverOption(){

document.getElementById("settingsIconButton").style = "font-size:59px;color:white";

}

function clearshowHoverOption(){

document.getElementById("settingsIconButton").style = "font-size:59px;color:white";

}

function openNav() {
   isNotificationScreen = true;
   document.getElementById("mySidenav").style.width = "100%";
}

function closeNav() {
  document.getElementById("mySidenav").style.width = "0";
  isNotificationScreen = true;
     window.location.href = "dmsnotes.html";
}


function reloadNotifications() {
    if(onHold || isNotificationScreen)
                return;



            onHold = true;

               var xhr = new XMLHttpRequest();

                    xhr.open("POST", checkNotescountUrl);

                    xhr.setRequestHeader("Accept", "application/json");

                    xhr.setRequestHeader("Content-Type", "application/json");

                    xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                    var data  = JSON.stringify({});

                    xhr.send(data);

                    try{
     xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    console.log(xhr.responseText);
                    onHold = false;
                     const response = JSON.parse(xhr.responseText);

                    if(xhr.status == 401){
                        alert(response.message);
                           window.localStorage.setItem(pageRedirectionPort,"dmsnotes.html");
                            window.location.href = "index.html";
                        return;
                    }else if(xhr.status == 503){
                        alert(response.message);
                        window.location.href = "index.html";
                        return;
                    }

                    if(response.status == "FAILED"){
                        return;
                    }

                    var nCount = response.data + "";

                    if(notificationCount != nCount) {

                    notificationCount = response.data;





                     var xhr1 = new XMLHttpRequest();

                                        xhr1.open("POST", getNewNotifications);

                                        xhr1.setRequestHeader("Accept", "application/json");

                                        xhr1.setRequestHeader("Content-Type", "application/json");

                                        xhr1.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                                        var data  = JSON.stringify({});

                                        xhr1.send(data);


                        xhr1.onreadystatechange = function () {
                                    if (xhr1.readyState === 4) {
                                        console.log(xhr1.status);
                                        console.log(xhr1.responseText);

                     const response = JSON.parse(xhr1.responseText);

                     if(response.status == "FAILED"){
                                            return;
                                        }

                               currentPendingNotes = response.data;


    document.getElementById("notificationsSpace").innerHTML = '';

                    var notificationsSpaceHtml;
                         for(i = 0; i < currentPendingNotes.length; i++) {

                                          var eachPendingNote = currentPendingNotes[i];



                                        notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Tittle : " + eachPendingNote.tittle + "</p>" ;

                                           notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Shared By : " + eachPendingNote.sharedBy + "</p>";

                                            notificationsSpaceHtml = notificationsSpaceHtml + "<p style=\"color:white\">Message : " + eachPendingNote.message + "</p>";

          notificationsSpaceHtml = notificationsSpaceHtml +   "<div align=\"center\"> <button onclick=" + "\"acceptButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton\">Accept</button><button onclick="+
           "\"rejectButton('"+ eachPendingNote.sni + "')\"" +" class=\"greenAcceptButton redRejectButton\">Reject</button></div>";


                         }

                      if(notificationsSpaceHtml == undefined){
                      document.getElementById("notificationsSpace").innerHTML = noNotificationMessage;
                      } else {
    document.getElementById("notificationsSpace").innerHTML = notificationsSpaceHtml;
                      }

                           document.getElementById("notificationIconSpace").innerHTML = '';
                          var tempNotificationIconSpace = "<button type=\"button\" class=\"icon-button\" onclick=\"openNav()\"><span class=\"material-icons\">notifications</span>";


                                           if(notificationCount != "0") {
                                           playAudio();
                           document.getElementById("notificationIconSpace").innerHTML = tempNotificationIconSpace + "<span class=\"icon-button__badge\">" + notificationCount + "</span>" + "</button>";
                                           } else {
                                           document.getElementById("notificationIconSpace").innerHTML = tempNotificationIconSpace + "</button>";
                                           }




                     };
                            }



                    }else{
                        return;
                    }

    };
        }


    }   catch (err) {
             alert("Exception Occered");
         }



}



function playAudio() {
var sound = document.getElementById("notificationTone");
  sound.play();
}
