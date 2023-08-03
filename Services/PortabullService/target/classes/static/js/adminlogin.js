
var adminLoginUrl = BASE_URL + 'UM/admin/login';

var tokenKey = 'token';

function login() {

	try {

		var userName = btoa(btoa(document.getElementById("userNameId").value).split("").reverse().join(""));

		var password = btoa(btoa(document.getElementById("password-field").value).split("").reverse().join(""));

		if (userName == "" || password == "") {
			alert("UserName/Password Should not be empty");
			return;
		}

		var xhr = new XMLHttpRequest();

		xhr.open("POST", adminLoginUrl);

		xhr.setRequestHeader("Accept", "application/json");

		xhr.setRequestHeader("Content-Type", "application/json");

		xhr.setRequestHeader("latlong",window.localStorage.getItem("location"));

		var data = JSON.stringify({
			"username": userName,
			"password": password
		});

		let latLong = window.localStorage.getItem("location");
		console.log("lattitude;longitude:" + latLong);
		xhr.setRequestHeader("location", latLong);

		if(latLong == null) {

		}

		xhr.send(data);

		xhr.onreadystatechange = function() {
			if (xhr.readyState === 4) {
				console.log(xhr.status);
				console.log(xhr.responseText);
				const tokenData = JSON.parse(xhr.responseText);
				if (tokenData.statusCode == 200) {
					window.localStorage.setItem(tokenKey, 'Bearer ' + tokenData.data.jwt);
				    window.location.href = "adminlandingpage.html";
				} else {
				    alert(tokenData.message);
				}
			}
		};
	} catch (err) {
		alert("Exception Occered");
	}

}


function redirectTOUserLoginPage(){
  window.location.href = BASE_URL;
}