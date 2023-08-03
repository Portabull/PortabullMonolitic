var columns;
var users;


var map = new Map();
var inn = '';


var createMisReportsUrl = BASE_URL + 'MIS/create-mis-report';

var getCompleteUsersUrl =BASE_URL + 'UM/users/get-users';

var getColumnsUrl =BASE_URL + 'MIS/get-columns';

var getViewsUrl = BASE_URL + 'MIS/get-views';

var a1 = "<button onclick=\"" + "removeUser('";

	var a2 = "')\" class=\"button123 button1\">";

	var a3 = "&nbsp;&nbsp;&nbsp;&nbsp;X</button>";

function prepareRequest(base64,fileName) {

    var response = new Map();

    var selectClause = new Map();


    for (i = 0; i < columns.length; i++) {
        var checked = document.querySelector('.' + columns[i] + "checkBoxClassName").checked;
        if (checked) {
            selectClause.set(columns[i], document.getElementById(columns[i] + "aliasId").value);
        }
    }

    response.set("selectClause", Object.fromEntries(selectClause));
    response.set("misName", document.getElementById("misName").value);
    response.set("documentTittle", document.getElementById("documentTittle").value);
    response.set("headerColourCode", document.getElementById("headerColourCode").value);
    response.set("groupBy", document.getElementById("groupBy").value);
    response.set("orderBy", document.getElementById("orderBy").value);
    response.set("timeStampFormat", document.getElementById("timeStampFormat").value);
    response.set("logoRequired", document.querySelector('.isLogoRequired').checked);

    response.set("timeStamoRequired", document.querySelector('.isTimeStamoRequired').checked);
    response.set("file", base64);
    response.set("fileName",fileName);



    response.set("userName", map.entries().next().value[0]);


    var hrefUrl = window.location.href;
    var url = new URL(hrefUrl);
    response.set("viewName",url.searchParams.get("view"));

    console.log(JSON.stringify(response));

    return JSON.stringify(Object.fromEntries(response));
}

function createMisReports(request) {

        hideSelectClause('selectCaluse');

        var portabullloader = document.querySelector('#portabullloader');

        portabullloader.innerHTML = "<div id=\"loader\"></div>";

        var xhr = new XMLHttpRequest();

		xhr.open("POST", createMisReportsUrl);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

        xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

        xhr.send(request);

        xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);

                document.getElementById("loader").style.display = "none";
				if (response.statusCode == 200) {
                    window.location.href = "displayviews.html";
				} else {
                    alert(response.message);
				}
			}
		};
}

function populateUsers(){

        var xhr = new XMLHttpRequest();

		xhr.open("GET", getCompleteUsersUrl);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

        xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

        xhr.send( JSON.stringify({}));

        xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);
			    users = response.data;
			    const userNames = [];
			    for(i=0;i<response.data.length;i++){
			          userNames[i]= response.data[i][1];
			    }


			     var myUserOptions = document.querySelector('#myUserOptions');



                         var label = '<label for="users">* Users</label> <select id="mycompleteUsers" name="users"> ';

                         var selectTag = ' </select>';

                         var options = '';

                         for (i = 0; i < userNames.length; i++) {

                           var aa = userNames[i];

                           options = options + "<option value=\"" +aa+ "\">" + aa + "</option>";


                         }

                         myUserOptions.innerHTML = label + options + selectTag;

			}
		};
}

function getUserId(userName){
 for (i = 0; i < users.length; i++) {
    var user = users[i];
        if(user[1]==userName){
            return user[0];
        }

}
}


function initSelectClause() {

         var selectC = '<br>';

         var selectCaluse = document.querySelector('#selectCaluse');

                 var xhr = new XMLHttpRequest();

         		xhr.open("POST", getColumnsUrl);

         		xhr.setRequestHeader("Accept", "application/json");

         		xhr.setRequestHeader("Content-Type", "application/json");

                 xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

                 var hrefUrl = window.location.href;
                     var url = new URL(hrefUrl);

                 xhr.send( JSON.stringify({"viewName":url.searchParams.get("view")}));

  xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);


         columns = response.columns;

         for (i = 0; i < columns.length; i++) {

           var checkBoxName = columns[i];

           var checkBoxClassName = columns[i];

           var aliasId = columns[i];

           selectC = selectC + "<label class=\"container\">" + checkBoxName + "<input type=\"checkbox\" class=\"" + checkBoxClassName + "checkBoxClassName" + "\" >  <span class=\"checkmark\"></span></label>    <input type=\"text\" id=\"" + aliasId + "aliasId" + "\" name=\"documentTittle\" placeholder=\"Enter Alias Name...\"> <br><br>";


         }

         selectCaluse.innerHTML = selectC;
			}
		};


         }



         function displayViews() {
                     try {

                         var xhr = new XMLHttpRequest();

                         xhr.open("POST", getViewsUrl);

                         xhr.setRequestHeader("Accept", "application/json");

                         xhr.setRequestHeader("Content-Type", "application/json");

                         xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

                         xhr.send(JSON.stringify({}));

                         xhr.onreadystatechange = function () {
                             if (xhr.readyState === 4) {
                                 console.log(xhr.status);
                                 console.log(xhr.responseText);
                                 data = JSON.parse(xhr.responseText);

                                 var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

                                 let tableHeader = " <section class=\"ftco-section\">   <div class=\"container\"> <div class=\"row justify-content-center\">      </div>       <div class=\"row\"> <div class=\"col-md-12\"> <div class=\"table-wrap\"> <table class=\"table table-striped\">  <thead>  <tr> <th>Sno</th>    <th>Table Name</th>   <th>Schema Name</th>  <th>ACTION</th> </tr> </thead>  <tbody>";

                                 data.forEach((item) => {

                                     tableHeader = tableHeader + "<tr><th scope=\"row\">" + item.sno + "</th><td>" + item.tableName + "</td><td>" + item.schemeName +  "</td><td>" + "<a href=\"#\" onclick=configureViews123('" + item.viewName  +"') id=\"" + item.sno + "\" class=\"btn btn-success\">Configure</a>" + "</td></tr>";
                                 })


                                 let tableFooter = " </tbody> </table> </div> </div> </div>\t</div> </section>";

                                 singleFileUploadSuccess.innerHTML = tableHeader + tableFooter;
                             }
                         };


                     } catch (err) {
                         alert("Failed");
                     }

                 }

function configureViews123(tableNameWithSchemeName){
    window.location.href = "adminmiscreation.html?view=" + tableNameWithSchemeName;
}



   function createMisReport() {
         var base64 = '';
         var request = '';
         var fileName = '';

         var fileInput = document.getElementById('myFile');

         var len = fileInput.files.length;

         if (len != 0) {
           var reader = new FileReader();
           reader.readAsDataURL(fileInput.files[0]);
           reader.onload = function() {
               fileName = fileInput.files[0].name;
               base64 = reader.result;

               var fileContentType = base64.split(';')[0].split(':')[1];

               if(!fileContentType.includes("image")){
                    alert("Please Upload Only Image");
                    return;
               }

               request = prepareRequest(base64,fileName);
               createMisReports(request);
           };
           reader.onerror = function(error) {
               alert("unable to proceed due to incorrect file please upload a valid file");
               return;
           };
         } else {
           request = prepareRequest(base64,fileName);
           createMisReports(request);
         }


         }


         function showSelectClause(id) {
         var x = document.getElementById(id);
         if (x.className.indexOf("w3-show") == -1) {
           x.className += " w3-show";
         } else {
           x.className = x.className.replace(" w3-show", "");
         }
         }

         function hideSelectClause(id){
            var x = document.getElementById(id);
               if (x.className.indexOf("w3-show") != -1) {
                x.className = x.className.replace(" w3-show", "");
               }
         }

         function gotodisplayViews(){
            window.location.href = "displayviews.html";
         }



function ghdghgdhg(){
alert("dfgfsdgsf");
}

         function init() {

         var misHeaderCreation = document.querySelector('#misHeaderCreation');

         var hrefUrl = window.location.href;

         var url = new URL(hrefUrl);

         misHeaderCreation.innerHTML = "<button class=\"button123321 button22223\" onclick=\"gotodisplayViews()\">Back</button>" + "<h3>MIS Reports Creation : " + url.searchParams.get("view") + "</h3>"

<!--         populateUsers();-->

         initSelectClause();

         }




 function removeUser(userName) {
 var listssss = document.querySelector('#listssss');

 	map.delete(userName);

     inn = '';

 	for (let key of map.keys()) {

     var aa = a1 + key + a2 + key + a3;

		 inn = inn + aa;

	}

 listssss.innerHTML = inn;

 }




 function addd(){
 var listssss = document.querySelector('#listssss');

 	var userName = document.getElementById("userNameId").value;

    document.getElementById("userNameId").value = '';

    if(map.get(userName) == userName){
    alert("Already User added")
    return;
    }

    map.set(userName,userName);



        var aa = a1 + userName + a2 + userName + a3;

        inn = inn + aa;

    listssss.innerHTML = inn;
 }