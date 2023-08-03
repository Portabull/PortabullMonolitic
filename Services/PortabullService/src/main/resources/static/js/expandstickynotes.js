 var noteId;
var onHold = false;
var tokenKey = 'token';
var getNotesUrl = BASE_URL + 'DMS/notes/get-note';
var notesUrl = BASE_URL + 'DMS/notes/update-notes';
var createdDate;
var updatedDate;
var notesCache;
var tittleCache;

var isCreating = false;
  init();

  function init() {
noteId       = new URL(window.location.href).searchParams.get("noteId");

if(noteId == null || noteId == "null"){
    createNewNotes();
    return;
}

if(onHold )
            return;

        hideButtons();

        onHold = true;

           var xhr = new XMLHttpRequest();

                xhr.open("POST", getNotesUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var data  = JSON.stringify({"noteId":noteId});

                xhr.send(data);

                try{
 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                onHold = false;
                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert("Session Expired Please login again");
                    window.localStorage.setItem(pageRedirectionPort,"expandstickynotes.html?noteId="+noteId);
                      window.location.href = "index.html";
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                    return;
                }

                if(response.status == "FAILED"){
                    alert(response.message);
                    return;
                }

unHideButtons();

createdDate = response.data.createdDate;
 updatedDate = response.data.updatedDate;

                if(response.data.edb != null && !response.data.edb) {
                document.getElementById("updateNotesDeleteId").style.visibility= 'hidden';
                }

                if(response.data.esb != null && !response.data.esb) {
                       document.getElementById("updateNotesShareId").style.visibility= 'hidden';
                }

                if(response.data.edwb !=null && !response.data.edwb) {
                document.getElementById("updateNotesDownloadId").style.visibility= 'hidden';
                }

                if(response.data.eeb !=null && !response.data.eeb) {
                    document.getElementById("updateNotesButtonId").style.visibility= 'hidden';
                 }

                notesCache = formatTextReplacer(response.data.note);
                tittleCache = formatTextReplacer(response.data.tittle);

      document.getElementById("notesTextAreaId").value = response.data.note;

document.getElementById("tittleId").value = response.data.tittle;

//                     window.location.href = "dmsnotes.html";

};
    }


}   catch (err) {
         alert("Exception Occered");
     }




  }


  function hideButtons(){
  document.getElementById("updateNotesDeleteId").style.visibility= 'hidden';
  document.getElementById("updateNotesShareId").style.visibility= 'hidden';
  document.getElementById("updateNotesDownloadId").style.visibility= 'hidden';
  document.getElementById("updateNotesButtonId").style.visibility= 'hidden';
  }


  function unHideButtons(){
  document.getElementById("updateNotesDeleteId").style.visibility= '';
  document.getElementById("updateNotesShareId").style.visibility= '';
  document.getElementById("updateNotesDownloadId").style.visibility= '';
  document.getElementById("updateNotesButtonId").style.visibility= '';
  }


function updateNotes(){
modifyNotes(false,false);
}


function deleteNotes(){
 let confirmDel = confirm("Are you sure you want to close this? Notes will not be saved");
    if(!confirmDel) return;
    else{
 modifyNotes(true,false);
    }
}

function shareNotes(){
      window.location.href = "sharenotes1.html?noteId="+noteId;
}

function modifyNotes(isdeleteflag,isCreateNew) {
  if(onHold )
            return;

        onHold = true;

           var xhr = new XMLHttpRequest();

                xhr.open("POST", notesUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));


                var data = null;


                if(isCreateNew){
                data = JSON.stringify({"noteId":null,"tittle":document.getElementById("tittleId").value
                                ,"note":document.getElementById("notesTextAreaId").value,"delete":isdeleteflag});

                }else{
                data = JSON.stringify({"noteId":noteId,"tittle":document.getElementById("tittleId").value
                                ,"note":document.getElementById("notesTextAreaId").value,"delete":isdeleteflag});

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

                if(isCreateNew){
                    isCreating = false;
                }

               window.location.href = "dmsnotes.html";

};
    }
    } catch (err) {
        alert("Exception Occered");
    }


}

function downloadNotes(){
    var tittle = document.getElementById("tittleId").value;
     const link = document.createElement("a");
     var aaaa = document.getElementById("notesTextAreaId").value;
       const content =  "Title : \n\t" + tittle + "\nNotes : \n\t" + aaaa + "\nCreated Date : \n\t" + createdDate + "\nUpdated Date : \n\t" + updatedDate;


     const file = new Blob([content], { type: 'text/plain' });
             link.href = URL.createObjectURL(file);
             link.download = tittle + ".txt";
             link.click();
             URL.revokeObjectURL(link.href);
}

function donothing(){
}

function redirectToNotesPage() {

if(!isCreating) {
var notesTextAreaId= formatTextReplacer(document.getElementById("notesTextAreaId").value);
var tittleId =  formatTextReplacer(document.getElementById("tittleId").value);


 if(notesTextAreaId !== notesCache){
    let confirmDel = confirm("Are you sure you want to close this? Notes will not be saved");
    if(!confirmDel) return;
    else{
        	    window.location.href = "dmsnotes.html";
    }
 } else if(tittleId != tittleCache){
    let confirmDel = confirm("Are you sure you want to close this? Notes will not be saved");
    if(!confirmDel) return;
    else{
    window.location.href = "dmsnotes.html";
    }
 }

}


 window.location.href = "dmsnotes.html";

}



function createNewNotes() {

if(isCreating){
modifyNotes(false,true);
return;
}


var notesTextAreaId= formatTextReplacer(document.getElementById("notesTextAreaId").value);
var tittleId =  formatTextReplacer(document.getElementById("tittleId").value);

 if(notesTextAreaId !== notesCache){
    let confirmDel = confirm("Are you sure you want to close this? Notes will not be saved");
    if(!confirmDel) return;
    else{
    applyCreateButton();
    }
 } else  if(tittleId !== tittleCache){
    let confirmDel = confirm("Are you sure you want to close this? Notes will not be saved");
    if(!confirmDel) return;
    else{
    applyCreateButton();
    }
 }
 applyCreateButton();



}


function applyCreateButton(){

isCreating = true;
hideButtons();
document.getElementById("tittleId").value = '';
document.getElementById("notesTextAreaId").value = '';
setTimeout(function(){
    document.getElementById("createNewNotesButtonId").textContent = 'Create';
}, 300);
}

function formatTextReplacer(data){
return data.replace(/^\s+|\s+$/gm,'').replace(/[^\x20-\x7E]/gmi, "");
}


