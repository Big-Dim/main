var wsUri = "ws://" + document.location.host + document.location.pathname + "myendpoint";
var websocket = new WebSocket(wsUri);
var json ;
 //       = JSON.stringify({
 //   "login": document.getElementById("login").value,
 //   "password": document.getElementById("password").value
//});
var output = document.getElementById("output");
websocket.onopen = function(evt) { 
    onOpen(evt); 
};
websocket.onmessage = function(evt) { 
    console.log("received: " + evt.data);
    writeAnswer(evt.data);
};


websocket.onerror = function(evt) { 
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
};



function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
}


function sendText(type) {
    json = JSON.stringify({
        "type": type,
        "sequence_id": generateUUID(),
        "data":{
            "email": document.getElementById("login").value,
            "password": document.getElementById("password").value
        }
    });
    websocket.send(json);
    console.log("sending text: " + json);    
}


    
function writeAnswer(answer){
    var json = JSON.parse(answer);
    document.getElementById("output").innerHTML = JSON.stringify( json )
    console.log("answer text: " + json);
}

function generateUUID(){
    var d = new Date().getTime();
    if(window.performance && typeof window.performance.now === "function"){
        d += performance.now(); //use high-precision timer if available
    }
    var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
    return uuid;
}