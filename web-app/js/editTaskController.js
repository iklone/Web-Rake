/** function used to add task delete button
 * @author ding Ang
 */
function addDelBtnOnTask(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
}

/**
 * function used to add functionality of task delete button
 * @author ding Ang
 */
function task_delete_btn(){
	// to do
	//taskID
	//deleteTaskInDatabase(taskID);
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
 * @author ding Ang
 */
function addDelBtnOnScrape(li){
	var del_btn = document.createElement("button");
	var txt = document.createTextNode("delete");
	del_btn.className = "del-btn";
	del_btn.appendChild(txt);
	li.appendChild(del_btn);
}

/**
 * function used to add functionality of scrape delete button
 * @author ding Ang
 */
function scrape_delete_btn(){
	//to do
	//scrapeID
	//deleteScrapeInDatabase(scrapeID)
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