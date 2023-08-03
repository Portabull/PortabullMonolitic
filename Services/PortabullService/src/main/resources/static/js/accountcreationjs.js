var saveAccountsUrl = BASE_URL + 'gs/user-accounts/save-user-account';
var tokenKey = 'token';

var onHold = false;



function createAccount(){


if(onHold)
            return;

        onHold = true;

var description = document.getElementById("messageDescriptionId").value;

var tittle = document.getElementById("tittleId").value;


           var xhr = new XMLHttpRequest();

                xhr.open("POST", saveAccountsUrl);

                xhr.setRequestHeader("Accept", "application/json");

                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.setRequestHeader("Authorization", window.localStorage.getItem(tokenKey));

                var data  = JSON.stringify({"desc":description,"tittle":tittle});

                xhr.send(data);

                try{
 xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                console.log(xhr.status);
                console.log(xhr.responseText);
                 const response = JSON.parse(xhr.responseText);

                if(xhr.status == 401){
                    alert(response.message);
                     window.localStorage.setItem(pageRedirectionPort,"accountcreationh.html");
                      window.location.href = "index.html";
                    return;
                }else if(xhr.status == 503){
                    alert(response.message);
                      window.location.href = "index.html";
                    return;
                }


                   onHold = false;

                   window.location.href = "callistpage1.html";

};
    }


}   catch (err) {
         alert("Exception Occered");
     }

}