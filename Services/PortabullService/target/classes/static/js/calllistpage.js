var getAccountsUrl =  BASE_URL + 'gs/user-accounts/get-user-accounts';
var saveAccountsUrl = BASE_URL + 'gs/user-accounts/save-user-account';
var searchAccountsUrl = BASE_URL + 'gs/user-accounts/search-user-account';
var deleteAccountsUrl = BASE_URL + 'gs/user-accounts/delete-user-accounts';
var tokenKey = 'token';
var pageNo = 0;
var defaultResultSize = 10;
var resultSize = defaultResultSize;
var dataCompleted=false;
var pagination = "<div class=\"pagination\"><label id=\"prevAccountLabelId\" onclick=\"javascript:previousAccounts('event has been triggered');\" for=\"table_radio_1\">&laquo; Previous</label><label id=\"nextAccountLabelId\" onclick=\"javascript:nextAccounts('event has been triggered');\" for=\"table_radio_1\">Next &raquo;</label></div>";
var pageSizeForCallListPage = "pageSizeForCallListPage";
var tableHeader = "<thead><tr><th></th><th>No</th><th>Tittle</th><th>Descrption</th><th>Created Date</th><th>Updated Date</th><th>Total</th><th>Profit & Loss</th></tr></thead>";
var pageNumberForCallListPage = "pageNumberForCallListPage";
var tableBodyTag_O = "<tbody>";
var tableBodyTag_C = "</tbody>";

var tableTag_O = "<table>";
var tableTag_C = "</table>";
var lastSearchText = "";
var onHold = false;
var sNo = 1;
var tittle = "titt";
var desc = "desc";
var createdDate = "374563985745";
var updatedDate = "3745ewew63985745";
var total = 200;
var profitLossData = -1;
var selectedAccountIds = [];

init();



function init() {

if(window.localStorage.getItem(pageNumberForCallListPage)!=null && window.localStorage.getItem(pageNumberForCallListPage)!=undefined)
pageNo = window.localStorage.getItem(pageNumberForCallListPage);

dataCompleted = false;
enableNextButton();

populateAccounts('i');

}

function enableNextButton(){
if(document.getElementById("nextAccountLabelId")!=null && document.getElementById("nextAccountLabelId")!=undefined){
document.getElementById("nextAccountLabelId").style.color = "white";
}

}


function disableNextButton(){
if(document.getElementById("nextAccountLabelId")!=null && document.getElementById("nextAccountLabelId")!=undefined)
{
document.getElementById("nextAccountLabelId").style.color = "#8F8F8F";
}
}

function disablePrevButton() {
if(document.getElementById("prevAccountLabelId")!=null && document.getElementById("prevAccountLabelId")!=undefined)
document.getElementById("prevAccountLabelId").style.color = "#8F8F8F";
}

function enablePrevButton(){
if(document.getElementById("prevAccountLabelId")!=null && document.getElementById("prevAccountLabelId")!=undefined){
document.getElementById("prevAccountLabelId").style.color = "white";
}
}

function populateAccounts(flag) {
try {
if(onHold)
            return;

        onHold = true;

        var pageSizeLimit = window.localStorage.getItem(pageSizeForCallListPage);

        if(pageSizeLimit != undefined && pageSizeLimit != null){
            resultSize = pageSizeLimit;
            document.getElementById("pageSizeSelected").value = pageSizeLimit;
        }

           var xhr = new XMLHttpRequest();

                xhr.open("POST", getAccountsUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var data  = JSON.stringify({"pageNo":pageNo,"resultSize":resultSize});

                xhr.send(data);


 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);


                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                    window.localStorage.setItem(pageRedirectionPort,"callistpage.html");
                      window.location.href = "index.html";
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                      window.location.href = "index.html";
                    return;
                }



                   onHold = false;


                   var startItem = (parseInt(pageNo) * parseInt(resultSize)) + 1;
                   var endItem = (parseInt(startItem) + parseInt(resultSize)) - 1;

                   if(endItem > parseInt(response.data.count)) {
                        endItem = response.data.count;
                   }

var pageNumberDivTag = "<input class=\"table-radio\" type=\"radio\" name=\"table_radio\" id=\"table_radio_0\" checked=\"checked\"/>" + "<div class=\"table-display\">Showing "+ startItem+ " to "+ endItem +" of " + response.data.count + " accounts</div>";


                var tableCompleteData = "";
                response.data.accounts.forEach((item) => {
                var isselected = "";
                     if(checkIfSelected(item.id)){
                        isselected = "checked";
                     }
                     var tableData = "<tr><td><input "+ isselected +"  id=accountCheckBoxId"+ item.id + " onchange=\"callDude("+ item.id + ")\" type=\"checkbox\"/></td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.sNo + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.tittle + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.desc + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.createdDate + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.updatedDate + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.total + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + getProfitLoss(item.profitLossData) + "</td></tr>";
                     tableCompleteData = tableCompleteData + tableData;
                });




                var finalTag =   tableTag_O + tableHeader + tableBodyTag_O + tableCompleteData + tableBodyTag_C + tableTag_C + pagination;
                document.getElementById("fgafgfasgfsa").innerHTML  = pageNumberDivTag + finalTag;

                  if(response.data.accounts.length!=resultSize){
                                 dataCompleted = true;
                                 disableNextButton();
                                 }else{
                                    enableNextButton();
                                 }

                                 if(pageNo == 0)
                                {
                                    disablePrevButton()
                                }else{
                                    enablePrevButton();
                                }
};
    }


}   catch (err) {
         onHold = false;
         alert("Exception Occered");
     }


}


function getProfitLoss(profitLossData) {

var profitLoss = "";

if(profitLossData == 0) {
profitLoss = "<p style=\"color:black\">"+ profitLossData +"</p>";
} else if(profitLossData > 0) {
profitLoss = "<p style=\"color:green\">+"+ profitLossData +"</p>";
} else {
profitLoss = "<p style=\"color:red\">"+ profitLossData +"</p>";
}
return profitLoss;
}


function previousAccounts() {

if(pageNo == 0) {
    return;
}

pageNo--;
window.localStorage.setItem(pageNumberForCallListPage,pageNo);
dataCompleted = false;
enableNextButton();
populateAccounts('p');

}

function nextAccounts(){

if(dataCompleted){
return;
}

pageNo++;
window.localStorage.setItem(pageNumberForCallListPage,pageNo);
populateAccounts('n');


}


function goToAccountPage(id){
if(selectedAccountIds.length!=0){

if(document.getElementById("accountCheckBoxId" + id).checked){
document.getElementById("accountCheckBoxId" + id).checked = false;
}else{
document.getElementById("accountCheckBoxId" + id).checked = true;
}
callDude(id);
return;

}


window.location.href = "accountcalculation.html?accId=" + id;

}



function donothing(){
}

function changePageSize(pageSize){
resultSize = pageSize;
dataCompleted = false;
enableNextButton();
window.localStorage.setItem(pageSizeForCallListPage,pageSize);
pageNo=0;
window.localStorage.setItem(pageNumberForCallListPage,pageNo);
init();
}


function addNewAccounts() {
window.location.href = "accountcreationh.html";
}


function goToAccountsHome() {
lastSearchText = "";
dataCompleted = false;
enableNextButton();
pageNo = 0;
window.localStorage.setItem(pageNumberForCallListPage,0);
init();
}


function searchAccounts(searchAcc) {
        try {
    if(onHold)
            return;

        onHold = true;

       selectedAccountIds = [];

           var xhr = new XMLHttpRequest();

                xhr.open("POST", searchAccountsUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var search = "";

                if(searchAcc !=null && searchAcc!=undefined){
                    search = searchAcc;
                }else{
                 search = document.getElementById("gcallsearch").value;
                }

                if(search == "" || search == undefined || search == null){
                    onHold = false;
                    return;
                }

                if(lastSearchText != "" && lastSearchText!=null && lastSearchText!=undefined){
                    if(search.startsWith(lastSearchText)){
                        onHold = false;
                        return;
                    }
                }

                var data  = JSON.stringify({"search":search});

                xhr.send(data);


 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);


                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                    window.localStorage.setItem(pageRedirectionPort,"callistpage.html");
                      window.location.href = "index.html";
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                      window.location.href = "index.html";
                    return;
                }

                 if(response.data.accounts.length!=resultSize){
                 dataCompleted = true;
                 disableNextButton();
                 }

                   onHold = false;

                   var startItem = 1;
                    if(response.data.count == 0){
                        startItem = 0;
                        lastSearchText = search;
                    } else {
                        lastSearchText = "";
                    }

                   var endItem = response.data.count;

var pageNumberDivTag = "<input class=\"table-radio\" type=\"radio\" name=\"table_radio\" id=\"table_radio_0\" checked=\"checked\"/>" + "<div class=\"table-display\">Showing "+ startItem+ " to "+ endItem +" of " + response.data.count + " accounts</div>";


                var tableCompleteData = "";
                response.data.accounts.forEach((item) => {
                     var tableData = "<tr><td><input id=accountCheckBoxId"+ item.id + " onchange=\"callDude("+ item.id + ")\" type=\"checkbox\"/></td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.sNo + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.tittle + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.desc + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.createdDate + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.updatedDate + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + item.total + "</td><td style=\"cursor: pointer;\" onclick=\"goToAccountPage("+item.id+")\">" + getProfitLoss(item.profitLossData) + "</td></tr>";
                     tableCompleteData = tableCompleteData + tableData;
                });





                var finalTag =   tableTag_O + tableHeader + tableBodyTag_O + tableCompleteData + tableBodyTag_C + tableTag_C ;
                document.getElementById("fgafgfasgfsa").innerHTML  = pageNumberDivTag + finalTag;
};
    }


}   catch (err) {
         onHold = false;
         alert("Exception Occered");
     }

}



var searchBox = document.getElementById("gcallsearch");

searchBox.onkeyup = (e)=>{
    let userData = e.target.value;


if(userData!=null && userData!=undefined && userData.length>=3){
    searchAccounts();
}else if(userData!=null && userData!=undefined && userData.length==0){
 lastSearchText = "";
dataCompleted = false;
enableNextButton();
pageNo = 0;
window.localStorage.setItem(pageNumberForCallListPage,0);
init();
}

    }

    function callDude(id) {
    if(document.getElementById("accountCheckBoxId" + id).checked){
           selectedAccountIds[selectedAccountIds.length] = id;
    }else{

        var tempSelectedAccountIds = selectedAccountIds;

        selectedAccountIds = [];

        var count = 0;
        for(var i=0;i<tempSelectedAccountIds.length;i++){
        if(tempSelectedAccountIds[i] != id){
            selectedAccountIds[count] = tempSelectedAccountIds[i];
            count++;
        }
        }

    }

    }


function deleteSelectedAccounts() {
    try {
	if(selectedAccountIds.length == 0) {
	    alert("Please select at least one account to delete");
	    return;
	}

    let confirmDel = confirm("Are you sure you want to delete " + selectedAccountIds.length + " Accounts");
    if(!confirmDel) return;

    if(onHold)
        return;

    onHold = true;

       var xhr = new XMLHttpRequest();

                   xhr.open("POST", deleteAccountsUrl);

                   xhr.setRequestHeader("Accept", "application/json");

                   xhr.setRequestHeader("Content-Type", "application/json");

                   xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                   var data  = JSON.stringify({"accountIds":selectedAccountIds});

                   xhr.send(data);


    xhr.onreadystatechange = function () {
               if (xhr.readyState === 4) {
                   console.log(xhr.status);
                   console.log(xhr.responseText);


                    const response = JSON.parse(xhr.responseText);

                   if(xhr.status == 401){
                       alert(response.message);
                       window.localStorage.setItem(pageRedirectionPort,"callistpage.html");
                         window.location.href = "index.html";
                       return;
                   }else if(xhr.status == 503){
                       alert(response.message);
                         window.location.href = "index.html";
                       return;
                   }

                   onHold = false;

                    window.localStorage.setItem(pageNumberForCallListPage,0);

                   window.location.href = "callistpage.html";
   };
       }


   }   catch (err) {
             onHold = false;
            alert("Exception Occered");
        }


}

function checkIfSelected(id) {
    for(var i=0;i<selectedAccountIds.length;i++){
        if(selectedAccountIds[i]==id){
            return true;
        }
    }
    return false;
}


function clearAccounts() {

selectedAccountIds = [];

dataCompleted = false;

enableNextButton();

pageNo = 0;

window.localStorage.setItem(pageNumberForCallListPage,0);

resultSize = defaultResultSize;

window.localStorage.setItem(pageSizeForCallListPage,defaultResultSize);

window.location.href = "callistpage.html";

}

function goToBack(){
window.location.href = "loginsuccessfull.html";
}


document.addEventListener('keydown', function(event) {
 const key = event.key;
  if (key === "Delete" && !onHold) {
        deleteSelectedAccounts();
  }
});



function changeView(){
    window.location.href = "callistpage1.html";
}