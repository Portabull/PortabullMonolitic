// getting all required elements
const searchWrapper = document.querySelector(".search-input");
const inputBox = searchWrapper.querySelector("input");
const suggBox = searchWrapper.querySelector(".autocom-box");
const icon = searchWrapper.querySelector(".icon");
let linkTag = searchWrapper.querySelector("a");
let webLink;

let suggestions = [
    "Email",
    "MIS Reports",
    "Lock/Unlock"
];


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
   if(selectData == "Email"){
     window.location.href = "adminemails.html";
    }else if(selectData == "MIS Reports"){
       window.location.href = "displayviews.html";
    }else if(selectData == "Lock/Unlock"){
        window.location.href =  "adminlockpage.html";
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
