var getNotesSettings =  BASE_URL + 'DMS/notes/get-settings';
var changeNotesSettings =  BASE_URL + 'DMS/notes/settings';


var tokenKey = 'token';
var onHold=false;

function initSettings(){

 try {

            if(onHold )
                return;

            onHold = true;

            var xhr = new XMLHttpRequest();

            xhr.open("POST", getNotesSettings);

            xhr.setRequestHeader("Accept", "application/json");

            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

            var data = JSON.stringify({});

            xhr.send(data);

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    onHold = false;


                         const response = JSON.parse(xhr.responseText);

                          if(xhr.status == 401 && response.message == "Unauthorized Access"){
                            alert("Session Expired Please login again");
                            window.localStorage.setItem(pageRedirectionPort,"settingsnotes.html");
                            window.location.href = "index.html";
                           }

                         var enableDisableShareNotes = response.data.enableDisableShareNotes;

                         if(enableDisableShareNotes == null || !enableDisableShareNotes){
                            document.getElementById("enableDisableShareNotesToggle").checked = false;
                         }else{
                         document.getElementById("enableDisableShareNotesToggle").checked = true;
                         }

   var hideUnHideNotes = response.data.hideUnHideNotes;
  if(hideUnHideNotes == null || !hideUnHideNotes){
                            document.getElementById("hideUnHideNotesToggleButton").checked = false;
                         }else{
                         document.getElementById("hideUnHideNotesToggleButton").checked = true;
                         }


                           var hideUnHideDeleteButt = response.data.hideUnHideDeleteButton;
                           if(hideUnHideDeleteButt == null || !hideUnHideDeleteButt){
                                                     document.getElementById("hideUnhideDeleteButton").checked = false;
                                                  }else{
                                                  document.getElementById("hideUnhideDeleteButton").checked = true;
                                                  }
                }
            };
        } catch (err) {
            alert("Exception Occered");

        }
}

function changeSettings(flag,hucFlag){

 try {

            if(onHold )
                return;

            onHold = true;

            var xhr = new XMLHttpRequest();

            xhr.open("POST", changeNotesSettings);

            xhr.setRequestHeader("Accept", "application/json");

            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

            var data;
            if(flag == "sn"){
           data = JSON.stringify({"serviceCode":"SHARE","enableDisableShareNotes":document.getElementById("enableDisableShareNotesToggle").checked});
            }else if(flag == "hn"){
             data = JSON.stringify({"serviceCode":"ED-HIDE","enableDisableHiddenNotes":document.getElementById("hideUnHideNotesToggleButton").checked});
            }else if(flag == "deleteBut"){
                data = JSON.stringify({"serviceCode":"ED-DELETE-B","hideUnHideDeleteButton": document.getElementById("hideUnhideDeleteButton").checked});
            }else{
            data = JSON.stringify({"serviceCode":"HUC","hideUnHideCompleteNotes": hucFlag});
            }


            xhr.send(data);

            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    console.log(xhr.status);
                    onHold = false;


                         const response = JSON.parse(xhr.responseText);
                         if(xhr.status == 401 && response.message == "Unauthorized Access"){
                            alert("Session Expired Please login again");
                             window.localStorage.setItem(pageRedirectionPort,"settingsnotes.html");
                            window.location.href = "index.html";
                         }
                }
            };
        } catch (err) {
            alert("Exception Occered");

        }
}