/**
 * get all user information from the background
 * @author Ang Ding, Peichen Yu
 */
window.onload = function getUserInfo(){
	chrome.storage.local.get(['userInfoInPlugIn'], function(result) {
          console.log('Value currently is ' + result.userInfo);
          userName = result.userInfoInPlugIn.userName;
          userId = result.userInfoInPlugIn.userId;
          userPassword = result.userInfoInPlugIn.userPassword;
   	});
   
    chrome.storage.local.get(['allTask'], function(result) {
		console.log(result.allTask);
		if(result.allTask){
			allTask = result.allTask;
		    displayTask();
		}else{
		  	getTaskInfo();
		}
    });
		
	chrome.storage.local.get(['bgColor'], function(result) {
		if(result.bgColor){
			bgColor = result.bgColor;
		}else{
			result.bgColor = 0;
			bgColor = result.bgColor;
		}
		console.log('Scrape background color currently is ' + result.bgColor);
		document.getElementById("scrape-bgColor").selectedIndex = bgColor;
	});
	
	//set functionality of home page button
	homepage_btn();
}

    

/**
 * function used to display all task that user currently have
 * @author peichen YU
 */
function displayTask(){
	if(allTask.length != 0 && !document.getElementById("all-ul").hasChildNodes()){
		for(var i = 0; i<allTask.length; i++){
			var li = document.createElement("li");
			li.className = "task-box";
			var text = document.createTextNode(allTask[i].taskName);
			var span = document.createElement("span");
			span.appendChild(text);
			li.appendChild(span);
			document.getElementById("all-ul").appendChild(li);
			var url = document.createTextNode(allTask[i].taskURL);
			var urlSpan = document.createElement("span");
			urlSpan.className = "url";
			urlSpan.appendChild(url);
			li.appendChild(urlSpan);
			document.getElementById("all-ul").appendChild(li);
			addBtnToAllTask(li);
		}
		
		//add button to the task
		button_manager();
		
	}
	add_new_task();	
	setting();
	taskSearcher();
}
	


/**
 * link to database get all user's task
 * @author peichen YU
 */
function getTaskInfo(){
	let xhr = new XMLHttpRequest();
//	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/taskDisplay.php", true);
	xhr.open("POST", "http://192.168.64.2/plug-in/taskDisplay.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
	    	if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	    		var response = xhr.responseText;
	    		allTask = [];
	    		if(response != "no record"){
				allTask = JSON.parse(response);
				chrome.storage.local.set({ "allTask": allTask});
				console.log(allTask);
			}
	    		displayTask();
		}
	}
	xhr.send('userId=' + userId);
}


/** 
 * This function shows the window of adding new task.
 * @author Ang Ding
 */
function add_new_task(){
	modal = document.getElementById('addtask-modal');
	
	// Get the button that opens the modal
	var addtask_btn = document.getElementById("newTask-btn");
	
	// Get the <span> element that closes the modal
	var span = document.getElementById("addTask-close");
	
	// Get the button that close the modal
	var cancel_btn = document.getElementById("cancel-btn");
	
	// Get the button that add a new task
	var add_btn = document.getElementById("add-btn");
	
	
	// When the user clicks the button, open the modal 
	addtask_btn.onclick = function() {
		modal.style.display = "block";
	}
	
	// When the user clicks on <span> (x), close the modal
	span.onclick = function() {
		modal.style.display = "none";
		document.getElementById("taskname").value = "";
  		document.getElementById("decription-box").value= "";
	}
	
	cancel_btn.onclick = function(){
		modal.style.display = "none";
		document.getElementById("taskname").value = "";
  		document.getElementById("decription-box").value= "";
	}
	
	add_btn.onclick = function(){
		var flag = newElement();
		if(flag!=false){
			modal.style.display = "none";
		}
	}

	// When the user clicks anywhere outside of the modal, close it
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}
}

/**
 * This function shows setting  window
 * @author: Ang Ding
 */
function setting(){
	var modal = document.getElementById("setting-modal");
	
	var setting_btn = document.getElementById("setting-btn");
	
	var span = document.getElementById("setting-close");
	
	var cancel_btn = document.getElementById("setting-cancel-btn");
	
	var apply_btn = document.getElementById("setting-apply-btn");
	
	setting_btn.onclick = function(){
		modal.style.display = "block";
	}
	
	span.onclick = function() {
		modal.style.display = "none";
	}
	
	apply_btn.onclick = function(){
		modal.style.display = "none";
		var userSelect = document.getElementById("scrape-bgColor");
		var color = userSelect.options[userSelect.selectedIndex].value;
		chrome.storage.local.set({ "bgColor": color});
	}
	
	cancel_btn.onclick = function(){
		modal.style.display = "none";
	}
	
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}
}


/**
 * Creates a new task
 * @author Ang Ding
 */
function newElement() {
	var li = document.createElement("li");
	var firstLi = document.getElementById("all-ul").firstChild;
	li.className = "task-box";
	var inputValue = document.getElementById("taskname").value;
	var text = document.createTextNode(inputValue);
	var span = document.createElement("span");
	span.appendChild(text);
	li.appendChild(span);
	if (inputValue === '') {
		var alert = document.getElementById("alert-noTaskName");
		alert.style.display = "block";
		var span = document.getElementById("alert-noTask-close");		
		var ok_btn = document.getElementById("alert-null-ok");
		span.onclick = function() {
			alert.style.display = "none";
		}		
		ok_btn.onclick = function(){
			alert.style.display = "none";
		}
		window.onclick = function(event) {
			if (event.target == alert) {
				alert.style.display = "none";
			}
		}
		return false;
	}else if(checkTaskNameDuplicate(inputValue)){
		var alert = document.getElementById("alert-dupTaskName");
		alert.style.display = "block";
		var span = document.getElementById("alert-dup-body");		
		var ok_btn = document.getElementById("alert-dup-ok");
		span.onclick = function() {
			alert.style.display = "none";
		}		
		ok_btn.onclick = function(){
			alert.style.display = "none";
		}
		window.onclick = function(event) {
			if (event.target == alert) {
				alert.style.display = "none";
			}
		}
		return false;
	}else{
		document.getElementById("all-ul").insertBefore(li, firstLi);
	}

	//buttons of li	
	addBtnToAllTask(li);
		
	// add button to the new task	
	button_manager();
	
	newtaskSender(inputValue);
	
}

/**
 * Add a delete button
 * @author Ang Ding
 */
function addDelBtn(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
}

///**
// * Add a manage button
// * @author Ang Ding
// */
//function addManageBtn(li){
//	var manage_btn = document.createElement("button");
//	var txt = document.createTextNode("manage");
//	manage_btn.className = "manage-btn";
//	manage_btn.appendChild(txt);
//	li.appendChild(manage_btn);
//}


function addScrapeBtn(li){
	var scrape_btn = document.createElement("button");
	var txt = document.createTextNode("scrape");
	scrape_btn.className = "scrape-btn";
	scrape_btn.appendChild(txt);
	li.appendChild(scrape_btn);
}


/**
 * Add buttons to a task in the AllTask list
 * @author Ang Ding
 */
function addBtnToAllTask(li){
	addScrapeBtn(li);
	addDelBtn(li);
//	addManageBtn(li);
}

function taskSearcher() {
	document.getElementById("search").addEventListener('keyup', function(){
  		// Declare variables
		var input, filter, ul, li, a, i;
		input = document.getElementById("search");
		filter = input.value.toUpperCase();
		ul = document.getElementById("all-ul");
		li = ul.getElementsByTagName("li");
  
  // Loop through all list items, and hide those who don't match the search query
		for (i = 0; i < li.length; i++) {
			a = li[i].getElementsByTagName("span")[0];
			if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
				li[i].style.display = "";
			} else {
    				li[i].style.display = "none";
   			}
  		}
 	})
}

/**
 * Implement the functions of buttons
 * @author Ang Ding
 */
function button_manager(){
	delete_btn();
	scrape_btn();
//	manage_btn();
}

/**
 * Implement the functionality of delete button
 * @author Ang Ding
 */
function delete_btn(){
	var del = document.getElementsByClassName("del-btn");
	var i;
	for (i = 0; i < del.length; i++) {
		del[i].onclick = function() {
			var taskID;
			var div = this.parentElement;
			var name = div.getElementsByTagName("span")[0].innerText;
			var text = document.createTextNode("Delete the task " + name + " perpetually?\n");
			var body = document.getElementById("alert-delete-body");
			body.appendChild(text);
			var alert = document.getElementById("delete-modal");
			alert.style.display = "block";
			document.getElementById("delete-btn-yes").onclick = function(){
				div.style.display = "none";
				alert.style.display = "none";
				body.removeChild(body.firstChild);
				for(i in allTask){
					if(allTask[i].taskName == name){
						taskID = allTask[i].taskID;
						allTask.splice(i, 1);
						break;
					}
				}
				deleteTaskInDatabase(taskID);
			}
			
			var span = document.getElementById("delete-close");		
			var cancel_btn = document.getElementById("delete-btn-cancel");
			span.onclick = function() {
				alert.style.display = "none";
				body.removeChild(body.firstChild);
			}
					
			cancel_btn.onclick = function(){
				alert.style.display = "none";
				body.removeChild(body.firstChild);
			}
		}		
	}
}

/**
 * Implement the functionality of set button
 * @author Ang Ding
 */
function scrape_btn(){
	var set = document.getElementsByClassName("scrape-btn");
	for (i = 0; i < set.length; i++) {
		set[i].onclick = function() {
			var currentTaskName = this.parentElement.getElementsByTagName("span")[0].innerText;
			var currentTaskID;
			for(i in allTask){
				if(currentTaskName == allTask[i].taskName){
					currentTaskID = allTask[i].taskID;
					currentTaskURL = allTask[i].taskURL
					break;
				}
			}
			
			// set current taks
			chrome.storage.local.set({ "currentTask": {taskName:currentTaskName, taskID: currentTaskID, taskURL:currentTaskURL}});
			
			// if current url is not eqaul to currentTaskURL, then window.open(currentTaskURL), else refresh page
			var params = {
				active: true,
				currentWindow: true
			}
			chrome.tabs.query(params, getTabs);
			function getTabs(tab){
				console.log(tab);
				chrome.storage.local.set({ "currentTabId": tab[0].id});
				if(tab[0].url != currentTaskURL){
					window.open(currentTaskURL);
				}
			}
			
			//change popup window url
			openCurrentTask();
			
			// create context menu
			startContextMenu();
		}
	}
}

///**
// * Implement the functionality of manage button
// * @author Ang Ding
// */
//function manage_btn(){
//	var manage = document.getElementsByClassName("manage-btn");
//	for (i = 0; i < manage.length; i++) {
//		manage[i].onclick = function() {
//		}
//	}
//}
/**
 * implement the functionality of homepage button
 * @author peichen YU
 */

function homepage_btn(){
	var homepage = document.getElementById("homePage-btn");
	homepage.onclick = function(){
   		
		chrome.storage.local.get(['userInfoInWebapp'], function(result) {
			if(result.userInfoInWebapp){
				console.log(result.userInfoInWebapp);
				if(userName == result.userInfoInWebapp.userName && userPassword == result.userInfoInWebapp.userPassword){
					//window.open("http://avon.cs.nott.ac.uk/~psyjct/web-app/php/editTask.php");
					window.open("http://192.168.64.2/web-app/php/editTask.php");
				}else{
					chrome.storage.local.set({"logInFlag" : 1});
				  	//window.open("http://avon.cs.nott.ac.uk/~psyjct/web-app/html/index.html")
					window.open("http://192.168.64.2/web-app/html/index.html");
				}
			}else{
				//window.open("http://avon.cs.nott.ac.uk/~psyjct/web-app/html/index.html")
				window.open("http://192.168.64.2/web-app/html/index.html");
			}
		});
	}
}


/**
 * if taskName is duplicate, return true else return flase
 * @author peichen YU
 */
function checkTaskNameDuplicate(taskName){
	for(i in allTask){
		if(taskName == allTask[i].taskName){
			return true;
		}
	}
	return false;
}

/**
 * delete the task on database, also delete the scrape in this task
 * @author peichen YU
 */
function deleteTaskInDatabase(taskID){
	console.log('?');
	let xhr = new XMLHttpRequest();
//	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/deleteTask.php", true);
	xhr.open("POST", "http://192.168.64.2/plug-in/deleteTask.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			console.log(allTask);
			chrome.storage.local.set({ "allTask": allTask});
		}
	}
	xhr.send('taskID=' + taskID);
}


/**
 * send the new task information to the database
 * @param name of the task
 * @author peichen YU
 */
function newtaskSender(taskName) {
	var params = {
		active: true,
		currentWindow: true
	}
	chrome.tabs.query(params, getTabs);
	
	function getTabs(tab){
		url = tab[0].url;
		var taskDescription = document.getElementById('decription-box').value;
		
		let xhr = new XMLHttpRequest();
//		xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/addTask.php", true);
		xhr.open("POST", "http://192.168.64.2/plug-in/addTask.php", true);
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		xhr.onreadystatechange = function() {
			if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
				var newTaskId = xhr.responseText;
				allTask.unshift({"taskName":taskName, "taskID":newTaskId, taskURL:url});
				chrome.storage.local.set({ "currentTask":{taskName: taskName, taskID:newTaskId, taskURL:url}});
				chrome.storage.local.set({ "allTask": allTask});
				
				//open content script
				ContentScriptController();
				
				// open context menu
				startContextMenu();
				
				// open current Task's url
				openCurrentTask();
			}
		}
		xhr.send('userId=' + userId + '&taskName=' + taskName + '&taskDescription=' + taskDescription + "&url=" + url);
	}
}

function openCurrentTask(){
	window.location.href ="../html/currentTask.html";
	chrome.browserAction.setPopup({popup: "../html/currentTask.html"});
}


/**
 * function used to start context menu
 * @author peichen YU
 */
function startContextMenu(){
	
	// create context menu
	chrome.contextMenus.create({
			type: 'normal',
	    title: 'Add Scrape',
	    id: 'scrape',
	    contexts: ['all']
		}, function () {
	    console.log('contextMenus are create.');
	})
}

/**
 * Send message to content script to control content script
 * @author PeiChen Yu
 */
function ContentScriptController(){
	// judge whether it is the current web page
	let params = {
		active: true,
		currentWindow: true
	}
	chrome.tabs.query(params, getTabs);
	
	function getTabs(tab){
		//send message to the content script
		msg={txt: "start contentScript"};
		chrome.tabs.sendMessage(tab[0].id, msg);
	}
}