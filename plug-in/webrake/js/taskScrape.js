 /**
 * this is javascript code for currentTask.html
 */


//variable used to store taskName
var taskName;
//variable used to store all scrpae in current task
var currentTaskScrape = [];

/**
 * set the currentTask page's basic information
 * @author peichen YU
 */
window.onload = function pageSet(){
	document.getElementById("back-btn").addEventListener('click', function(){
		ContentScriptController(false);
		
		chrome.contextMenus.remove('scrape');
		chrome.storage.local.remove(['currentTaskScrape']);
		chrome.storage.local.remove(['newTaskScrape']);
		chrome.storage.local.remove(['currentTask']);
		chrome.storage.local.remove(['currentTabId']);
		
		window.location.href ="../html/main.html";
		chrome.browserAction.setPopup({popup: "../html/main.html"});
	
	})
	
    	chrome.storage.local.get(['currentTask'], function(result) {
          console.log('Value currently is ' + result.currentTask);
          currentTaskName = result.currentTask.taskName;
          currentTaskID = result.currentTask.taskID;
          console.log(currentTaskName + currentTaskID);
          var div = document.getElementById("currentTask-name")
		  var text = document.createTextNode(currentTaskName);
		  //text.className = "task-name";
	      div.appendChild(text);
	      
	      
	      chrome.storage.local.get(['currentTaskScrape'], function(result) {
			  if(result.currentTaskScrape){
		      	 currentTaskScrape = result.currentTaskScrape;
		      	 display_scrape(currentTaskScrape);
			  }else{
		      	 getScrape(currentTaskID);
			  }
    	   	 });
   });
   
   chrome.runtime.onMessage.addListener(function(request, sender, sendResponse){
   		if(request.msg == "displayData"){
   			chrome.storage.local.get(['newTaskScrape'], function(result) {
   				display_scrape(result.newTaskScrape);
   			})
   	    }
   		return Promise.resolve("Dummy response to keep the console quiet");
   	})
	
	ContentScriptController(true);
}

/**
 * function used to display the scrape
 * @author any Ding
 */
function display_scrape(scrape){
	if(scrape.length != 0){
		ul = document.getElementById("cur-ul");
		for(i in scrape){
	    		var li = document.createElement("li");
	    		li.className = "scrape-data";
	    		console.log(scrape);
	    		var data = scrape[i].data
	    		var text = document.createTextNode(scrape[i].scrapeName + ": " + data);
	    		li.appendChild(text);
	    		ul.appendChild(li);
	   	}
 	}	
}


/**
 * Send message to content script to control content script
 * @author PeiChen Yu
 */
function ContentScriptController(flag){
	// judge whether it is the current web page
	chrome.storage.local.get(['currentTabId'], function(result) {
		var msg;
		if(flag == true){
			msg={txt: "start contentScript"};
		}else{
			msg={txt: "stop contentScript permanently"};
		}
		chrome.tabs.sendMessage(result.currentTabId, msg);
	});
}

/**
 * function used to get all scrape of a specific task
 * @author payne YU
 */
function getScrape(taskID){
	let xhr = new XMLHttpRequest();
	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/scrapeDisplay.php", true);
//	xhr.open("POST", "http://192.168.64.2/plug-in/scrapeDisplay.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			let response = xhr.responseText;
    			console.log(response);
    			if(response != "no record"){
    	    			currentTaskScrape = JSON.parse(response);
    	    			console.log(currentTaskScrape);
				chrome.storage.local.set({"currentTaskScrape": currentTaskScrape});
    	    			display_scrape(currentTaskScrape);
    	    		}
    	    		else{
    	    			chrome.storage.local.set({ "currentTaskScrape": []});
    	    		}
		}
	}
    xhr.send('&currentTaskID= ' + taskID);
}
