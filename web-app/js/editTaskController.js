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


/** function used to add task delete button
 * @author Ang Ding
 */
function addDelBtnOnTask(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "task-del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
}


/**
 * function used to add functionality of task delete button
 * @author Ang Ding
 */
function task_delete_btn(){
	// to do
	//taskID
	//deleteTaskInDatabase(taskID);
	var del = document.getElementsByClassName("task-del-btn");
	for (var i = 0; i < del.length; i++) {
		del[i].onclick = function() {
			var div = this.parentElement;
			var name = div.getElementsByTagName("span")[0].innerText;
			var a = confirm("Delete the task " + name + " permanently?");
			if(a == true){
				div.style.display = "none";
				for(i in allTask){
					if(allTask[i].taskName == name){
						taskID = allTask[i].taskID;
						allTask.splice(i, 1);
						break;
					}
				}
				deleteTaskInDatabase(taskID);
			}else{
				
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
//	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/webapp/php/deleteTask.php", true);
	xhr.open("POST", "http://192.168.64.2/webapp/php/deleteTask.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			console.log(allTask);
			chrome.storage.local.set({ "allTask": allTask});
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
	//to do
	//scrapeID
	//deleteScrapeInDatabase(scrapeID)
	var del = document.getElementsByClassName("scrape-del-btn");
	for (var i = 0; i < del.length; i++) {
		del[i].onclick = function() {
			var div = this.parentElement;
			//var name = allScrapeList[i].scrapeName;
			var a = confirm("Delete the scrape " + name + " permanently?");
			if(a == true){
				div.style.display = "none";
				for(i in allScrapeList){
					if(allScrapeList[i].scrapeName == name){
						scrapeID = allScrapeList[i].scrapeID;
						allScrapeList.splice(i, 1);
						break;
					}
				}
				deleteScrapeInDatabase(scrapeID);
			}else{
				
			}
		}
	}
}


/**
 * delete the scrape in the database
 * @author peichen YU
 */
function deleteScrapeInDatabase(scrapeID){
	let xhr = new XMLHttpRequest();
//	xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/webapp/php/deleteTask.php", true);
	xhr.open("POST", "http://192.168.64.2/webapp/php/deleteScrape.php", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	xhr.onreadystatechange = function() {
    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			console.log(allTask);
			chrome.storage.local.set({ "allTask": allTask});
		}
	}
	xhr.send('scrapeID=' + scrapeID);
}

/**
 * function used to check the format of schedule form
 * @author peichen YU
 */
function check(){
	var type = document.schedule.elements[0].value;
	if(type == "0"){
		return false;
	}
}