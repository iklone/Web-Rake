/**
 * the is used for chrome background
 */


/** 
 * listener: used to receive request from contextMenu and send information back to the web page
 * @author peichen YU
 */
chrome.contextMenus.onClicked.addListener(function addScriptOnClick(info, tab){
	if(tab){
		chrome.tabs.sendMessage(tab.id, {'contextMenuId': info.menuItemId, 'info': info}, function(response) {});
	}
	return Promise.resolve("Dummy response to keep the console quiet");
});

/**
 * listener: used to receive request from popup or contenScript
 * @author peichen YU
 */
chrome.runtime.onMessage.addListener(function(request, sender, sendResponse){
   	if(request.msg == "scrapeUpdate"){
		chrome.storage.local.get(['newTaskScrape'], function(result) {
			var newTaskScrape = result.newTaskScrape;
			for(i in newTaskScrape){
				sendScrape(newTaskScrape[i]);
			}
		})
   	}else if(request.msg == "re-logInPlugIn"){
   		chrome.storage.local.remove(["userInfoInPlugIn","allTask",'currentTabId','currentTask','newTaskScrape','newTaskScrape'],function(){
			var error = chrome.runtime.lastError;
			if (error) {
    				console.error(error);
			}
		});
		chrome.storage.local.set({ "logInFlag": 2});
   		chrome.browserAction.setPopup({popup: "../html/logIn.html"});
   	}
   	
   	return Promise.resolve("Dummy response to keep the console quiet");
})

/**
 * function used to send new scrapes to the database
 * @param {Object} content for each scrape
 * @author payne YU
 */
function sendScrape(scrape){
	console.log(scrape);
	chrome.storage.local.get(['currentTask'], function(result) {
		currentTaskID = result.currentTask.taskID;
		console.log(currentTaskID);
		let xhr = new XMLHttpRequest();
		xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/addTaskContent.php", true);
//		xhr.open("POST", "http://192.168.64.2/plug-in/addTaskContent.php", true);
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		xhr.onreadystatechange = function() {
	    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	    	    		console.log(xhr.responseText);
			}
		}
	    xhr.send('&currentTaskID= ' + currentTaskID + '&scrapeName=' + scrape.scrapeName + '&sampleData=' + scrape.data + '&xPath=' + scrape.path);
	})
}

/**
 * listener: used to receive request contenScript, and send request back to web page in order to stop or start content script.
 * @author peichen YU
 */
chrome.tabs.onUpdated.addListener(
	function(tabId, changeInfo, tab) {
		chrome.storage.local.get(['currentTask'], function(result) {
			if(result.currentTask && changeInfo.url){
				var currentTaskURL = result.currentTask.taskURL;
				var msg;
				if(changeInfo.url != currentTaskURL){
					msg = {txt: "stop contentScript temporarily"};
				}else{
					msg = {txt: "start contentScript"};
					chrome.storage.local.set({ "currentTabId": tabId});
				}
				chrome.storage.local.get(['currentTabId'], function(result) { console.log(result.currentTabId)});				
				chrome.tabs.sendMessage(tabId, msg);
			}
		});
	}
)

