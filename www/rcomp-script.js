// global variables
var nextMsg;
var isWall=0;
var  finalMessage;
var a;
var mArea, messageBox/*, hints*/, numberBox ,wallName, messageNumber; // defined only after the document is loaded

function loadAndStart() {
    mArea=document.getElementById("messages");
    messageBox=document.getElementById("message");
	numberBox=document.getElementById("number_message_delete");
    /*hints=document.getElementById("hints");*/
	hideContent();
	a = 0;
    setTimeout(getNextMessage, 1000);
    }
	
function hideContent() {
	document.getElementById("messages").style.visibility="hidden";
	document.getElementById("message").style.visibility="hidden";
	document.getElementById("message_text").style.visibility="hidden";
	document.getElementById("number_message_delete").style.visibility="hidden";
	document.getElementById("number_text").style.visibility="hidden";
	document.getElementById("send_button").style.visibility="hidden";
	document.getElementById("mdel_button").style.visibility="hidden";
	document.getElementById("wdel_button").style.visibility="hidden";
}

function showContent() {
	document.getElementById("messages").style.visibility="visible";
	document.getElementById("message").style.visibility="visible";
	document.getElementById("message_text").style.visibility="visible";
	document.getElementById("number_message_delete").style.visibility="visible";
	document.getElementById("number_text").style.visibility="visible";
	document.getElementById("send_button").style.visibility="visible";
	document.getElementById("mdel_button").style.visibility="visible";
	document.getElementById("wdel_button").style.visibility="visible";
}

function getNextMessage() {
    var request = new XMLHttpRequest();
    
    request.onload = function() {
        if(nextMsg===0) { 
		mArea.value = "";
        	mArea.style.color="blue";
	}
        mArea.value = this.responseText + "\r\n";
        /*nextMsg=nextMsg+1;*/ 
        setTimeout(getNextMessage, 100);
        };

    request.onerror = function() { 
        nextMsg=0;
        mArea.value = "Server not responding.";
        mArea.style.color="red";
        setTimeout(getNextMessage, 1000); 
    };

    request.ontimeout = function() { 
        nextMsg=0;
        mArea.value = "Server not responding.";
        mArea.style.color="red";
        setTimeout(getNextMessage, 100); 
    };
        
        
    request.open("GET", "/walls/" + wallName, true);
    if(nextMsg===0) request.timeout = 1000;
    // Message 0 is a server's greeting, it should always exist
    // no timeout, for following messages, the server responds only when the requested
    // message number exists
    request.send();
	}
	
function setWall(){
	var wallBox = document.getElementById("wall");
	
	wallName=wallBox.value;
	
	if(Boolean(wallName)){
		document.getElementById("currentWall").innerHTML = "Current wall: ".concat(wallName);
		nextMsg=0;
		if (a == 0) {
			showContent();
			a=1;
		}
		
	}else{
		document.getElementById("currentWall").innerHTML = "Wall name can't be empty.";
		if (a == 1) {
			hideContent();
			a=0;
		}
        return;
	}
}

function postMessage(wall) {
	
	wallName=wall.value;
	
	
	if(wallName == ""){
		messageBox.disable=true;
		return;
	}
	
    if(messageBox.value === "") { 
        return;
        }
    var POSTrequest = new XMLHttpRequest();
    //wallBox.disabled=true;
    POSTrequest.open("POST", "/walls/" + wallName, true);
    POSTrequest.timeout = 5000;
    POSTrequest.send(messageBox.value);
	nextMsg=nextMsg+1;
    mArea.scrollTop = mArea.scrollHeight; // scroll the textarea to make last lines visible
    }
	

function deleteMessage(){
	/*hints.innerHTML="";*/
	
	messageNumber=numberBox.value;
	
	var DELETErequest = new XMLHttpRequest();
	DELETErequest.open("DELETE", "/walls/"+wallName+"/"+messageNumber, true);
	/*nextMsg=nextMsg-1;*/
	DELETErequest.timeout = 5000;
    DELETErequest.send();
    
	
	}

function deleteWall(){
	var DELETErequest = new XMLHttpRequest();
	DELETErequest.open("DELETE", "/walls/"+wallName, true);
	DELETErequest.timeout = 5000;
    DELETErequest.send();
	wallName="";
	document.getElementById("currentWall").innerHTML="No wall selected";
	document.getElementById("currentWall").innerHTML = "Wall name can't be empty.";
		if (a == 1) {
			hideContent();
			a=0;
		}
        return;
	/*nextMsg=nextMsg-1;*/
	}
	


