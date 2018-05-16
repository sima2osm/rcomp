// global variables
var nextMsg;
var isWall=0;
var  finalMessage;
var mArea, messageBox, hints, numberBox ,wallName, messageNumber; // defined only after the document is loaded

function loadAndStart() {
    mArea=document.getElementById("messages");
    messageBox=document.getElementById("message");
	numberBox=document.getElementById("number_message_delete");
    hints=document.getElementById("hints");
    setTimeout(getNextMessage, 1000);
    }

function getNextMessage() {
    var request = new XMLHttpRequest();
    
    request.onload = function() {
        if(nextMsg===0) { 
		mArea.value = "";
        	mArea.style.color="blue";
	}
        mArea.value = this.responseText + "\r\n";
        mArea.scrollTop = mArea.scrollHeight; // scroll the textarea to make last lines visible
        nextMsg=nextMsg+1; 
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

function postMessage() {
    var hints.innerHTML="";
	
	wallName=arguments[0];
	
	if(wallName == ""){
		hints.innerHTML="No wall set";
		return;
	}
	
    if(messageBox.value === "") { 
        hints.innerHTML="Not sending empty message.";
        return;
        }
    var POSTrequest = new XMLHttpRequest();
    //wallBox.disabled=true;
    POSTrequest.open("POST", "/walls/"+wallName+"/"+nextMsg, true);
    POSTrequest.timeout = 5000;
    POSTrequest.send("{0} - {1}".format(nextMsg, messageBox.value));
	nextMsg=nextMsg+1;
    }
	

function deleteMessage(){
	var hints.innerHTML="";
	
	if(numberBox.match(/^[0-9]+$/) != null){
		messageNumber=numberBox;
	}else{
		hints.innerHTML="Number of the message must be a integer";
        return;
	}
	
	var DELETErequest = new XMLHttpRequest();
	DELETErequest.open("DELETE", "/walls/"+wallName+"/"+messageNumber, true);
	nextMsg=nextMsg-1;
	DELETErequest.timeout = 5000;
    DELETErequest.send();
    
	
}

function deleteWall(){
	var DELETErequest = new XMLHttpRequest();
	DELETErequest.open("DELETE", "/walls/"+wallBox.value+"/"+messageNumber, true);
	nextMsg=nextMsg-1;
}
	


