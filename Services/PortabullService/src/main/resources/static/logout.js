const BASE_URL_L = BASE_URL;

var tokenKey = 'token';

var url = BASE_URL_L + 'UM/plogout';

function logout() {

    try {
                 var xhr = new XMLHttpRequest();
                        xhr.open("POST", url);

                         xhr.setRequestHeader("Accept", "application/json");

                         xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

                        xhr.send(JSON.stringify({}));

                             xhr.onreadystatechange = function () {
                                            if (xhr.readyState === 4) {
                                                console.log(xhr.status);
                                                console.log(xhr.responseText);
                                                var redirectionUrl = window.localStorage.getItem(pageRedirectionPort);
                                                var staticAssets = window.localStorage.getItem(loginstaticimagesConstKey);
                                                var buildId = window.localStorage.getItem(buildIdNumberCashe1238);
                                                window.localStorage.clear();
                                                 window.localStorage.setItem(pageRedirectionPort,redirectionUrl);
                                                 window.localStorage.setItem(buildIdNumberCashe1238,buildId);

                                                   if(staticAssets != null && staticAssets != "null" && staticAssets != undefined && staticAssets != "undefined")
                                                        window.localStorage.setItem(loginstaticimagesConstKey,staticAssets);
                                                let a1 = JSON.parse(xhr.responseText);
                                               window.location.href = "index.html";
                                            }else{
                                                   window.location.href = "index.html";
                                            }
                                        };

                    } catch (err) {
                        alert("Something went wrong please try after sometime");
                         window.location.href = "index.html";
                    }




            }


