
const searchWrapper = document.querySelector(".search-input");
const inputBox = searchWrapper.querySelector("input");
const suggBox = searchWrapper.querySelector(".autocom-box");
const icon = searchWrapper.querySelector(".icon");
let linkTag = searchWrapper.querySelector("a");
let webLink;


var userRegistered = BASE_URL + 'UM/user-registered';
var getLastLoggedTime =  BASE_URL + 'UM/get-last-logged-time'



// if user press any key and release
inputBox.onkeyup = (e)=>{
    let userData = e.target.value; //user enetered data
    let emptyArray = [];
    if(userData){
        icon.onclick = ()=>{
              window.location.href = "pagenotfound.html";
        }
        emptyArray = suggestions.filter((data)=>{
            //filtering array value and user characters to lowercase and return only those words which are start with user enetered chars
            if(userData.length >= 3){
            if(data.toLocaleLowerCase().includes(userData.toLocaleLowerCase())){
                console.log("sdgh");
                            return data;
            }
            }
            return data.toLocaleLowerCase().startsWith(userData.toLocaleLowerCase());


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
    }else{
        searchWrapper.classList.remove("active"); //hide autocomplete box
    }
}

function select(element){
//    "Message",
//    "Settings"
    let selectData = element.textContent;
    if(selectData == "Documents"){
     window.location.href = "documents.html";
    }else if(selectData == "View Documents"){
     window.location.href = "viewDocuments.html";
    }else if(selectData == "Document Operations"){
     window.location.href = "documentoperations.html";
    }else if(selectData == "Upload Documents"){
     window.location.href = "uploadDocuments.html";
    }else if(selectData == "Email"){
     window.location.href = "emails.html";
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

function loadLoggedInPages(){

		var xhr = new XMLHttpRequest();

		xhr.open("POST", userRegistered);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

        xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

        xhr.send(JSON.stringify({}));

        xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);
				window.localStorage.setItem('registrationCheck', true);
				if (response.data ==null || !response.data) {
                     window.location.href = "registrationData.html";
				}else {
				 window.location.href = "loginsuccessfull.html";
				}
			}
		};
}

function loadTimeAndLogout(){
		var xhr = new XMLHttpRequest();

		xhr.open("POST", getLastLoggedTime);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

        xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

        xhr.send(JSON.stringify({}));

        xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);
				      var inboxInn = document.querySelector('#LoggedTimeAndLogout');
                     var aaa = '<a href="#" onclick="logout()" class="btn btn-info btn-lg">        <span class="glyphicon glyphicon-log-out"></span> Log out    </a>';
                      inboxInn.innerHTML = '<p>Last Logged Time: ' + response.data + '</p>' + aaa;
			}
		};



    }
