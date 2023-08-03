
const customerInfo = ["customerName", "customerEmail", "custCtNum","customerSubject","customerMessage"];
var tokenKey = 'token';

var saveClientDetails = BASE_URL + 'gs/save-client-contact-details';
const emailRegex = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;


function saveCustomerDetails(){

		var xhr = new XMLHttpRequest();

		xhr.open("POST", saveClientDetails);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

		xhr.setRequestHeader("Authorization",window.localStorage.getItem(tokenKey));

		var data = JSON.stringify({
			"name": document.getElementById("customerName").value,
			"email": document.getElementById("customerEmail").value,
			"contactNumber":document.getElementById("custCtNum").value,
			"subject":document.getElementById("customerSubject").value,
			"message":document.getElementById("customerMessage").value
		});

		if(validate(data)){
		    return;
		}

		xhr.send(data);

		xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const response = JSON.parse(xhr.responseText);
				customerInfo.forEach(clearCustomerInfo);
				alert(response.message);
			}
		};



}

function clearCustomerInfo(id) {

   document.getElementById(id).value = null;

}

function validate(data){

    var message = '';
    var flag = false;



    if (getVal("customerName") == null || getVal("customerName") == "") {
        flag = true;
        message = message + 'Name';
    }

    if (getVal("customerEmail")  == null || getVal("customerEmail")  == "") {
         flag = true;
         message = message + ',Email';
    }

    if (getVal("customerSubject") == null || getVal("customerSubject") == "") {
        flag = true;
        message = message + ',Subject';
    }

    if (getVal("customerMessage") == null || getVal("customerMessage") == "") {
        flag = true;
        message = message + ',Message';
    }

    if (!emailRegex.test(getVal("customerEmail"))) {
        alert("Please provide valid email");
        return true;
    }

    if(flag) {

        message = message.replace(/^,|,$/g, '');

        message = 'Please fill the required fields (' + message.replace(/,+/g,',') + ')';

        alert(message);

        return true;
    }

    return false;
}

function getVal(id){
return document.getElementById(id).value;
}
