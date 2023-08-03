// getting all required elements
const searchWrapper = document.querySelector(".search-input");
const inputBox = searchWrapper.querySelector("input");
const suggBox = searchWrapper.querySelector(".autocom-box");
const icon = searchWrapper.querySelector(".icon");
let linkTag = searchWrapper.querySelector("a");

const userMap = new Map();
  var myEmailAddress = '';

var tokenKey = 'token';

var usersUrl = BASE_URL + 'UM/users/get-users';
var sendEmailUrl = BASE_URL + 'gs/internal-emails/send-internal-email';
var getEmailUrl = BASE_URL + 'gs/internal-emails/get-internal-emails';
var updateMailSeen = BASE_URL + 'gs/internal-emails/update-mail-seen';

var userId;

// if user press any key and release
inputBox.onkeyup = (e)=>{

    if(e.key === "Enter"){

    let userData = e.target.value; //user enetered data
    let emptyArray = [];
    if(userData){
        icon.onclick = ()=>{
              window.location.href = "pagenotfound.html";
        }

         var xhr = new XMLHttpRequest();
            xhr.open("POST",usersUrl );

            xhr.setRequestHeader("Accept", "application/json");


            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

            var data = JSON.stringify({"userName": userData});

            xhr.send(data);


             xhr.onreadystatechange = function () {

                    if (xhr.readyState === 4) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        const response = JSON.parse(xhr.responseText);

                            handleResponse(xhr);

                           if (response.statusCode == 200) {
                            var users = response.data;
                        userId =users[0][1];
                               document.getElementById("myForm").style.display = "block";
                                    document.getElementById("notifyEmailAddress").value = userData;

                           return;


//                                var users = response.data;
//
//                                for (let i = 0; i < users.length; i++) {
//                                    var user = users[i];
//                                    var userName = user[0];
//                                    var id =user[1];
//                                    var userEmail =user[2];
//
//                                    var key = userName + ' -> ' + userEmail;
//
//                                    if(window.localStorage.getItem('emailsTabmyEmailAddress')==userEmail){
//                                        myEmailAddress = key;
//                                    }
//                                    userMap.set(key,user);
//                                }



                           } else {
                                alert(response.message);
                           }

                          var users =   Array.from(userMap.keys());
                             emptyArray = users.filter((data)=>{

                                     if(myEmailAddress!=data){
                                       //filtering array value and user characters to lowercase and return only those words which are start with user enetered chars
                                                                           if(userData.length >= 3){
                                                                           if(data.toLocaleLowerCase().includes(userData.toLocaleLowerCase())){
                                                                               console.log("sdgh");
                                                                                           return data;
                                                                           }
                                                                           }
                                                                           return data.toLocaleLowerCase().startsWith(userData.toLocaleLowerCase());

                                     }


                                  });
                                  emptyArray = emptyArray.map((data)=>{
                                      // passing return data inside li tag
                                      return data = `<li>${data}</li>`;
                                  });
                                  searchWrapper.classList.add("active"); //show autocomplete box
                                  showSuggestions(emptyArray);
                                  let allList = suggBox.querySelectorAll("li");
                                  for (let i = 0; i < allList.length; i++) {
                                      //adding onclick attribute in all li tag
                                      allList[i].setAttribute("onclick", "select(this)");
                                  }
                    }
                };

    }else{
        searchWrapper.classList.remove("active"); //hide autocomplete box
    }

    }

}

function select(element){
//    "Message",
//    "Settings"
    let selectData = element.textContent;

    var user = userMap.get(selectData);

 if(user!=null){
   var userName = user[0];
     userId = user[1];
     var userEmail = user[2];
        document.getElementById("myForm").style.display = "block";
        document.getElementById("notifyEmailAddress").value = userEmail;
    }


}

function showSuggestions(list){
    let listData;
    if(!list.length){
        userValue = inputBox.value;
        listData = `<li>${userValue}</li>`;
    }else{
      listData = list.join('');
    }
    suggBox.innerHTML = listData;
}


function sendMail(){

   var xhr = new XMLHttpRequest();
            xhr.open("POST" , sendEmailUrl);

            xhr.setRequestHeader("Accept", "application/json");


            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

            var data = JSON.stringify({"body": document.getElementById("w3review").value,
            "to":userId,"emailAddress":document.getElementById("notifyEmailAddress").value,
            "subject":document.getElementById("notifyReason").value});


            xhr.send(data);



   xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        console.log(xhr.status);
                        console.log(xhr.responseText);
                        const response = JSON.parse(xhr.responseText);
                            handleResponse(xhr);
                    }
                };
}





function openCity(evt, cityName) {
    var myEmailAddressDom = document.querySelector('#myEmailAddress');


   var xhrStorage = new XMLHttpRequest();
   xhrStorage.open("POST", getEmailUrl);

   xhrStorage.setRequestHeader("Accept", "application/json");

   xhrStorage.setRequestHeader("Content-Type", "application/json");
   xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));
   xhrStorage.send(JSON.stringify({
      "messageType": cityName
   }));

   xhrStorage.onreadystatechange = function () {
      if (xhrStorage.readyState === 4) {

         var inboxInn = document.querySelector('#Inbox');
         var sentboxInn = document.querySelector('#Sentbox');

         var inboxInnerHtml = '<br><br><h3>InBox:</h3>';
         var sentboxInnerHtml = '<br><br><h3>SentBox:</h3>';

         console.log(xhrStorage.status);
         console.log(xhrStorage.responseText);

        handleResponse(xhrStorage);

         var resp = JSON.parse(xhrStorage.responseText);

         myEmailAddressDom.innerHTML = '<h2>' + resp.data.myAddress +'</h2>';

          window.localStorage.setItem('emailsTabmyEmailAddress',resp.data.myAddress);

         if ('Inbox' == cityName) {

            if (resp.data.internalEmails == null || resp.data.internalEmails.length == 0) {
               inboxInnerHtml = inboxInnerHtml + '<br><br><p>Empty Inbox</p>';
            } else {

               resp.data.internalEmails.forEach((item) => {
                  inboxInnerHtml = inboxInnerHtml + '<p>' + 'Email From   : ' + item.senderAddress +
                   '<p> Email To :' + item.receiverAddress + '</p>' + '</p>' + '<p>' + 'Subject : ' +
                    item.messageSubject + '</p><p> Received Date : ' + item.createdDate + '</p>'
                    + '<p> Body    : ' + item.messageBody + '</p>' + '<br>';
               })
            }


            inboxInn.innerHTML = inboxInnerHtml;
         } else {


            if (resp.data.internalEmails == null || resp.data.internalEmails.length == 0) {
               sentboxInnerHtml = sentboxInnerHtml + '<br><br><p>Empty Sentbox</p>';
            }else{
            resp.data.internalEmails.forEach((item) => {
            var messageStatus =  item.mailSeen !=null && item.mailSeen ? 'Seen' : 'Not Seen' ;
               sentboxInnerHtml = sentboxInnerHtml + '<p>' + 'Email From  : ' + item.senderAddress + '</p>' + '<p>' + 'Email To  : ' +
                item.receiverAddress + '</p>' + '<p>' + 'Subject : ' + item.messageSubject +
                 '</p><p> Sent Date : ' + item.createdDate  + '</p>'
                 + '<p> Message Status : ' + messageStatus  + '</p>'
                 + '<p>Body    : ' + item.messageBody + '</p>' + '<br>';
            })

            }


            sentboxInn.innerHTML = sentboxInnerHtml;
         }

         var i, tabcontent, tablinks;
         tabcontent = document.getElementsByClassName("tabcontent");
         for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
         }
         if (evt != null) {
            tablinks = document.getElementsByClassName("tablinks");
            for (i = 0; i < tablinks.length; i++) {
               tablinks[i].className = tablinks[i].className.replace(" active", "");
               console.log(tablinks[i].className)
            }
         }

         document.getElementById(cityName).style.display = "block";

         updateSeenEmails(resp.data.internalEmails,cityName);
         if (evt != null) {
            evt.currentTarget.className += " active";
         }


      }
   };
}


function updateSeenEmails(internalEmails,cityName){

    if('Inbox' == cityName || 'undefined' == cityName){
    const messageIds = [];
     internalEmails.forEach((item) => {
                      messageIds.push(item.messageId);
                     });

   var xhrStorage = new XMLHttpRequest();
                        xhrStorage.open("POST", updateMailSeen);

                        xhrStorage.setRequestHeader("Accept", "application/json");

                        xhrStorage.setRequestHeader("Content-Type", "application/json");
                        xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));
                        xhrStorage.send(JSON.stringify({
                           "messageIds": messageIds
                        }));

   xhrStorage.onreadystatechange = function () {
        if (xhrStorage.readyState === 4) {

          console.log(xhrStorage.status);
                 console.log(xhrStorage.responseText);

                            handleResponse(xhrStorage);
  }
  };
    }





}


  function handleResponse(xhr){
    const response = JSON.parse(xhr.responseText);
    if (xhr.status == 503 || xhr.status == 500) {
                alert(response.message);
                window.location.href =  "loginsuccessfull.html";
    }else if(xhr.status == 401){
                alert(response.message);
                window.location.href = "index.html";
    }


    }