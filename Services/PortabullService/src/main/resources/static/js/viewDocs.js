
var pno = 0;
var resultSize = 12;
  function myfunction1(documentId){


           var xhr = new XMLHttpRequest();
           xhr.open("GET",BASE_URL + "DMS/download-documents" + "?documentId="+documentId);

           xhr.setRequestHeader("Accept", "application/json");


           xhr.setRequestHeader("Content-Type", "application/json");
           xhr.setRequestHeader("Authorization", window.localStorage.getItem('token'));

           var data = JSON.stringify({});

           xhr.send(data);


             xhr.onreadystatechange = function () {

                    if (xhr.readyState === 4) {
                        console.log(xhr.status);

                         const fileResponse = JSON.parse(xhr.responseText);

                if (fileResponse.statusCode == 200) {



                     var a = document.createElement("a");
                       a.href =fileResponse.data.file;
                          a.download = fileResponse.data.fileName; //File name Here
                        a.click();

                } else {
                           alert(fileResponse.message);
                    }
                }

                    }
                };

function myfunction12(documentId){
window.location.href = BASE_URL + "dmsvideoplayer.html?aa=" + window.btoa(documentId);
}

function viewPdfDocuments(documentId){
window.location.href = "pdfviewer.html?doc="+documentId;
}



function mytable() {
            try {
                let data;

                var xhrStorage = new XMLHttpRequest();
                xhrStorage.open("POST", BASE_URL + "DMS/get-storage");

                xhrStorage.setRequestHeader("Accept", "application/json");

                xhrStorage.setRequestHeader("Content-Type", "application/json");
                xhrStorage.setRequestHeader("Authorization", window.localStorage.getItem('token'));

                xhrStorage.send(JSON.stringify({}));

                xhrStorage.onreadystatechange = function () {
                    if (xhrStorage.readyState === 4) {
                        console.log(xhrStorage.status);
                        console.log(xhrStorage.responseText);
                        let a1 = JSON.parse(xhrStorage.responseText);

                        if (a1.message == "Unauthorized Access" || a1.message == "Session Expired") {
                            window.location.href = "index.html";
                        }

                        var storageResponseId = document.querySelector('#storageResponseId');

                        var storageValue = a1.storage > 50 ? "over50 p" + a1.storage : "p" + a1.storage;

                        var tableHeader = "<div class=\"progress-circle " + storageValue + " \"> <span>" + a1.storage + "%</span><div class=\"left-half-clipper\"> <div class=\"first50-bar\"></div> <div class=\"value-bar\"></div> <div> </div>";

                        storageResponseId.innerHTML = tableHeader;
                    }
                };

            loadDocsData(pno);

            } catch (err) {
                alert("Failed");
            }

        }

function loadDocsData(pnos){
  var xhr = new XMLHttpRequest();
                xhr.open("POST", BASE_URL + "DMS/view-documents?pageNo="+pnos+"&resultSize="+resultSize);

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

                        let tableHeader =   "<table>  <tr>    <th>SNO</th>    <th>FILE NAME</th>    <th>SIZE</th>    <th>UPLOADED TIME</th>    <th>USER NAME</th>    <th>ACTION</th>  </tr>";

                        data.forEach((item) => {
                            var playButton = "";
                            if(item.fileName.includes(".mp4")){
                            playButton = " <button type=\"button\"  onclick=\"myfunction12('" +  item.documentId + "')\">PLAY</button>"
                            }else if(item.fileName.includes(".pdf")){
                            playButton = " <button type=\"button\"  onclick=\"viewPdfDocuments('" +  item.documentId + "')\">VIEW</button>"
                             }
                            tableHeader = tableHeader + "<tr><td>" + item.sno + "</td><td>" + item.fileName + "</td><td>" + item.size + "</td><td>" + item.uploadedTime + "</td><td>" + item.userName + "</td><td>"  + playButton + " " + "<button type=\"button\"  onclick=\"myfunction1('" +  item.documentId + "')\">DOWNLOAD</button>" + "<button style=\"font-size:16px;color:#04acec\" onclick=\"shareDocument('"+ item.documentId +"')\">Share <i class=\"fa fa-share-alt\"></i></button>" + "</td></tr>";
                        })

                        tableHeader = tableHeader + '</table>';
                        console.log(tableHeader);
                         var flag = false;

                                                if(data.length!=resultSize){
                                                    flag = true;
                                                }

                        singleFileUploadSuccess.innerHTML = tableHeader +  "<p align=\"center\"> <a  href=\"#\" class=\"previous\" onclick=\"prevC()\">&laquo; Previous</a>" + "<a  href=\"#\" class=\"next\" onclick=\"nextC("+ flag +")\">Next &raquo; &raquo;</a> </p>";;
                    }
                };
}

function shareDocument(documentId){
    alert(documentId);
}

function goToStatistics(){
  window.location.href = "storagestatistics.html";
}


        function nextC(flag){

            if(flag){
                alert("No Furter Documents Available....");
                return;
            }


            pno++;
            loadDocsData(pno);
        }

        function prevC(){
            if(pno==0){
                alert("You are in first page...")
                return;
            }
            pno--;
            loadDocsData(pno);
        }


   function viewDoccsinClassic(){
           window.location.href = BASE_URL + "viewDocuments.html";
        }