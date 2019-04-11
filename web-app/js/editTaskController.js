/**
 * Search tasks
 * @author Ang Ding
 */
function taskSearcher() {
  		// Declare variables
		var input, filter, ul, li, a, i;
		input = document.getElementById("taskSearch");
		filter = input.value.toUpperCase();
		ul = document.getElementById("task-ul");
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
}


/**
 * Search scrapes
 * @author Ang Ding
 */
function scrapeSearcher() {
  		// Declare variables
		var input, filter, ul, li, a, i;
		input = document.getElementById("scrapeSearch");
		filter = input.value.toUpperCase();
		ul = document.getElementById("scrape-ul");
		li = ul.getElementsByClassName("scrape-data");
  
  // Loop through all list items, and hide those who don't match the search query
		for (i = 0; i < li.length; i++) {
			a = li[i].getElementsByTagName("span")[0];
			var s = a.innerHTML;
			var scrapeName = s.substring(0, s.indexOf("["));
			if (scrapeName.toUpperCase().indexOf(filter) > -1) {
				li[i].style.display = "";
			} else {
    			li[i].style.display = "none";
   			}
  		}
}


/** function used to add task delete button
 * @author Ang Ding
 */
function addBtnOnTask(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "task-del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
	
	var info_btn = document.createElement("button");
	var text = document.createTextNode("task_info");
	info_btn.className = "task-info-btn";
	info_btn.appendChild(text);
	li.appendChild(info_btn);
}


/**
 * function used to add functionality of task delete button
 * @author Ang Ding
 */
function task_delete_btn(){
	var del = document.getElementsByClassName("task-del-btn");
	for (var i = 0; i < del.length; i++) {
		del[i].onclick = function() {
			var div = this.parentElement;
			var name = div.getElementsByTagName("span")[0].innerText;
			var a = confirm("Delete the task " + name + " permanently?");
			if(a == true){
				div.style.display = "none";
				for(i in taskList){
					if(taskList[i].taskName == name){
						taskID = taskList[i].taskID;
						taskList.splice(i, 1);
						break;
					}
				}
				deleteTaskInDatabase(taskID);
			}
		}
	}
}


/**
 * delete the task on database, also delete the scrape in this task
 * @author peichen YU
 */
function deleteTaskInDatabase(taskID){
	let xhr = new XMLHttpRequest();
	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/web-app/php/deleteTask.php", true);
//	xhr.open("POST", "http://192.168.64.2/web-app/php/deleteTask.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
   	if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    		console.log(taskList);
		}
	}
	xhr.send('taskID=' + taskID);
}


/** function used to add scrape delete button
 * @author Ang Ding
 */
function addDelBtnOnScrape(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "scrape-del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
}


/**
 * function used to add functionality of scrape delete button
 * @author Ang Ding
 */
function scrape_delete_btn(){
	var del = document.getElementsByClassName("scrape-del-btn");
	for (var i = 0; i < del.length; i++)(function(i){
		del[i].onclick = function() {
			var div = this.parentElement;
			var scrape = del[i].parentNode.children[0].innerHTML;
			var name = scrape.substring(0, scrape.indexOf(":"));
			var a = confirm("Delete the scrape " + name + " permanently?");
			var scrapeID;
			if(a == true){
				div.style.display = "none";
				for(i in allScrapeList){
					if(allScrapeList[i][0].taskID = currentTaskID){
						for(j in allScrapeList[i]){
							if(allScrapeList[i][j].scrapeName == name){
								scrapeID = allScrapeList[i][j].scrapeID;
								allScrapeList[i].splice(j, 1);
								break;
							}
						}
					}
				}
				console.log(scrapeID);
				deleteScrapeInDatabase(scrapeID);
			}
		}
	})(i)
}


/**
 * delete the scrape in the database
 * @author peichen YU
 */
function deleteScrapeInDatabase(scrapeID){
	let xhr = new XMLHttpRequest();
	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/web-app/php/deleteScrape.php", true);
//	xhr.open("POST", "http://192.168.64.2/web-app/php/deleteScrape.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			// console.log(allScrapeList);
		}
	}
	xhr.send('scrapeID=' + scrapeID);
}

/**
 * function used to check the format of schedule form
 * @author peichen YU
 */
function submit(){
	var Type = document.getElementById('Type').value;
	if(Type == "0"){
		return false;
	}

	var Min = document.getElementById('Min').value;
	var Hour = document.getElementById('Hour').value;
	var DotW = document.getElementById('DotW').value;
	var DotM = document.getElementById('DotM').value;
	
	let xhr = new XMLHttpRequest();
	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/web-app/php/schedule.php", true);
//	xhr.open("POST", "http://192.168.64.2/web-app/php/schedule.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			document.getElementById("taskSchedule").style.display = "none";
		}
	}

	xhr.send('taskID=' + currentTaskID + '&Type=' + Type + '&Min=' + Min + '&Hour=' + Hour + '&DotW=' + DotW + '&DotM=' + DotM);
}

/**
 * function used to manipulate allScrapeList
 * @param {Object} allScrapeList
 * @author peichen YU
 */
function listOperator(allScrapeList){
	var newAllScrapeList = [];
	for(i in allScrapeList){
		var scrapeListForTask = [];
		var scrapeID = allScrapeList[i][0].scrapeID;
		var scrapeName = allScrapeList[i][0].scrapeName;
		var flag = allScrapeList[i][0].flag;
		var taskID = allScrapeList[i][0].taskID;
		var data = [];
		for(j in allScrapeList[i]){
			if(j == allScrapeList[i].length - 1 && allScrapeList[i][j] == null){
				var scrape = {scrapeName:scrapeName, data:data, flag:flag, scrapeID:scrapeID, taskID:taskID};
				scrapeListForTask.push(scrape);
			}
			
			if(allScrapeList[i][j] == null){
				continue;
			}

			if(allScrapeList[i][j].scrapeID != scrapeID){
				var scrape = {scrapeName:scrapeName, data:data, flag:flag, scrapeID:scrapeID, taskID:taskID};
				scrapeListForTask.push(scrape);
				data = [];
				data.push([allScrapeList[i][j].time, allScrapeList[i][j].data]);
				var scrapeID = allScrapeList[i][j].scrapeID;
				var scrapeName = allScrapeList[i][j].scrapeName;
				var flag = allScrapeList[i][j].flag;
				if(j == allScrapeList[i].length - 1){
					var scrape = {scrapeName:scrapeName, data:data, flag:flag, scrapeID:scrapeID, taskID:taskID};
					scrapeListForTask.push(scrape);
				}
			}else{
				data.push([allScrapeList[i][j].time, allScrapeList[i][j].data]);
			}

		}
		newAllScrapeList.push(scrapeListForTask);
	} 
	return newAllScrapeList;
}


/**
 * This function shows task description and task schedule type
 * @author Ang Ding
 */
function task_info(taskList){
	var info = document.getElementsByClassName("task-info-btn");
	var info_modal = document.getElementById("task-info-modal");
	for(var i = 0; i < info.length;i++)(function(i){
		info[i].onclick = function(){
			var info_modal = document.getElementById("task-info-modal");
			info_modal.style.display = "block";
			var text1 = document.createTextNode("Task Name: " + taskList[i].taskName);
			var text2 = document.createTextNode("Task Type: " + taskList[i].type);
			var text3 = document.createTextNode("Task URL: " + taskList[i].taskURL);
			var text4 = document.createTextNode("Task Description: " + taskList[i].taskDescription);
			// var body = document.getElementById("info-body");
			var p = document.getElementsByClassName("info-p");
			p[0].appendChild(text1);
			p[1].appendChild(text2);
			p[2].appendChild(text3);
			p[3].appendChild(text4);
		}
	})(i)
}
