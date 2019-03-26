var taskName;
var currentTaskScrape = [];

/**
 * set the currentTask page's basic information
 */
window.onload = function pageSet(){
	document.getElementById("back-btn").addEventListener('click', function(){
		ContentScriptController(false);
		
		chrome.contextMenus.remove('scrape');
		chrome.storage.local.remove(['currentTaskScrape']);
		chrome.storage.local.remove(['newTaskScrape']);
		chrome.storage.local.remove(['currentTask']);
		
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
	    		var text = document.createTextNode(scrape[i].scrapeName + ": " + scrape[i].sampleData);
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
	let params = {
		active: true,
		currentWindow: true
	}
	chrome.tabs.query(params, getTabs);
	
	function getTabs(tab){
		//send message to the content script
		var msg;
		if(flag == true){
			msg={txt: "start contentScript"};
		}else{
			msg={txt: "stop contentScript"};
		}
		chrome.tabs.sendMessage(tab[0].id, msg);
	}
}

/**
 * function used to get all scrape of a specific task
 * @author payne YU
 */
function getScrape(taskID){
	let xhr = new XMLHttpRequest();
	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/scrapeDisplay.php", true);
//	xhr.open("POST", "http://192.168.64.2/scrapeDisplay.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			let response = xhr.responseText;
    			if(response != "no record"){
    	    			currentTaskScrape = JSON.parse(response);
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
