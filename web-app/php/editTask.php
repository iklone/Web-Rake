<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>FinRake</title>
		<link rel = 'stylesheet' type = 'text/css' href = '../css/editTask.css'>
		<script type="text/javascript" src='../js/editTaskController.js'></script>
		<script>
			setTimeout(timeoutnotif, 60000);
			
			function timeoutnotif() {
				alert("There may be more recent scrapes. Refresh the page to view them.");
			}
		</script>
	</head>
	<body>
		<div class="main">
			<div class="logo-holder">
				<a href='javascript:void(0)'>
					<span id="helpbut">
						Help
					</span>
				</a>
				
				<img src="../img/logo.png" />
				
				<a href='../php/home-page.php'>
					<span id="logout">
						Log Out
					</span>
				</a>
			</div>
			<div class="title">
				<h1>Task List</h1>
			</div>
			
			<div class="taskArea">
				<div id="taskList" class="left-list">
					<div class="taskSearch">
						<input type="text" class="taskSearch-box" id="taskSearch" onkeyup="taskSearcher()" placeholder="Search tasks...">
					</div>
					
					<h1>Tasks:</h1>
					<div>
						<ul id="task-ul"></ul>
					</div>
					
					<script>
						<?php
							session_start();
							// Start the session
							include "db.php";
            
							$userId = $_SESSION["userId"];

							// find the result
							$results = mysqli_query($link, "select b.taskName, b.taskID, b.taskURL, b.taskDescription, GROUP_CONCAT(c.type) as type from UserAuthorisation a left join Task b on a.taskID = b.taskID left join Schedule c on a.taskID = c.taskID where a.userID="."'"."$userId"."'"."group by taskID");

							$taskList = [];
							$allScrapeList = [];

							if(mysqli_num_rows($results) >= 1){
								while($task = mysqli_fetch_assoc($results)){
									$taskList[] = $task;
								}
				

							    foreach($taskList as $task){
							    	$taskID = $task['taskID'];

									// find the result
									$results = mysqli_query($link, "select taskID, scrapeName, scrapeID, sampleData as data, flag, creationTime as time from Scrape where taskID ="."'"."$taskID"."'");

									$scrapeList = [];
									while($scrapeInfo = mysqli_fetch_assoc($results)) {
										$counter = 0;
										$id = $scrapeInfo['scrapeID'];
										$values = mysqli_query($link, "SELECT b.taskID, b.scrapeName, b.scrapeID, a.resultValue as data, a.resultTime as time, b.flag FROM Result a left join Scrape b on a.scrapeID = b.scrapeID WHERE a.ScrapeID = "."'"."$id"."'"." order by resultTime DESC");
										if(mysqli_num_rows($values) >= 1){
											while($counter < 10){
												$value = mysqli_fetch_assoc($values);
												$scrapeList[] = $value;
												$counter++;
											}
										}else{
											$value = $scrapeInfo;
											$scrapeList[] = $value;
										}
									}
									$allScrapeList[] = $scrapeList;
								}
							}
						?>
						var currentTaskID;
						var currentTaskName;
						var taskList = <?php echo json_encode($taskList); ?>;
						var allScrapeList = <?php echo json_encode($allScrapeList); ?>;
						console.log(allScrapeList)
						if(allScrapeList.length != 0){
							allScrapeList = listOperator(allScrapeList);
						}
						console.log(allScrapeList);
						var task_num = taskList.length;
						var ul = document.getElementById('task-ul');
						console.log(taskList);

						//display all tasks
						for (var i = 1; i <= task_num; i++){
							var li = document.createElement('li');
							li.className = "task-box";
							var text = document.createTextNode(taskList[i - 1].taskName);
							var span = document.createElement("span");
							span.className = "taskName";
							span.appendChild(text);
							li.appendChild(span);
							var urlHref = document.createElement("a");
							urlHref.className = "url";
							urlHref.href = taskList[i - 1].taskURL;
							urlHref.innerText = taskList[i - 1].taskURL;
							li.appendChild(urlHref);
							ul.appendChild(li);
							addBtnOnTask(li);
							task_delete_btn();
							task_info(taskList);
						}

						//display each scrape in each task
						for (var i = 0; i < task_num; i++) (function(i){
							var x = document.getElementsByClassName('task-box')[i];
							x.onclick = function(){
								var btn = document.getElementById("schedule-btn").style.display = "block";
								// compare the taskName in order to find the index
								for(j in taskList){
									if(taskList[j].taskName == x.firstElementChild.innerHTML){
										currentTaskID = taskList[j].taskID;
										currentTaskName = taskList[j].taskName;
										break;
									}
								}
								var taskName = x.childNodes[0].innerHTML;
								var index;
								for(z in taskList){
									if(taskList[z]['taskName'] == taskName){
										index = z;
									}
								}
								var scrapeUl = document.getElementById("scrape-ul");
								while(scrapeUl.firstChild){
									scrapeUl.removeChild(scrapeUl.firstChild);
								}
								// display the scrape for specific task
								if(allScrapeList[index]){
									if(allScrapeList[index].length != 0){
										var scrapeID = allScrapeList[index][0].scrapeID;
										for(j in allScrapeList[index])(function(j){
											// display the scrape
											var scrapeli = document.createElement("li");
											scrapeli.className = "scrape-data";
											var time = allScrapeList[index][j].data[0][0];
											var data = allScrapeList[index][j].data[0][1];
										    var s = allScrapeList[index][j].scrapeName + " [" + time + "] : " + data;
											var span = document.createElement("span");
											span.className = "scrapeName";
											if(allScrapeList[index][j].flag == 1){
												s = s + " (AI has changed the scrape)";
												var blue_btn = document.createElement("button");
												var blue_btn_text = document.createTextNode("Confirm");
												blue_btn.className = "scrape-confirm-btn";
												blue_btn.appendChild(blue_btn_text);
												scrapeli.appendChild(blue_btn);
												blue_btn.onclick = function(){
													confirmAI(allScrapeList[index][j].scrapeID);
												}
												span.style.color = "blue";
											}else if(allScrapeList[index][j].flag == 2){
												s = s + " (Scrape has failed)"
												span.style.color = "red";
											}
											var text = document.createTextNode(s);
											span.appendChild(text);
											scrapeli.appendChild(span);
											scrapeUl.appendChild(scrapeli);
											addDelBtnOnScrape(scrapeli);
											scrape_delete_btn();

											//display historical data of this scrape
											listUl = document.createElement("ul");
											listUl.className = "list-ul";
											scrapeli.appendChild(listUl);
											for(var z = 1; z < allScrapeList[index][j].data.length; z++){
												var li = document.createElement("li");
												var text = document.createTextNode("[" + allScrapeList[index][j].data[z][0] + "] : "+ allScrapeList[index][j].data[z][1]);
												var span = document.createElement("span");
												span.className = "listName";
												span.appendChild(text);
												li.appendChild(span);
												listUl.appendChild(li);
											}
											listUl.style.display = "none";
											scrapeli.onclick = function(){
												css = document.getElementsByClassName("list-ul")[j];
												if(css.style.display === "block"){
													css.style.display = "none";
												}else{
													css.style.display = "block";
												}
											}
										})(j)
									}
								}
							}
						})(i)
					</script>
				</div>
				<div id="scrapeList" class="right-list">
					<div class="scrapeSearch">
						<input type="text" class="scrapeSearch-box" id="scrapeSearch" onkeyup="scrapeSearcher()" placeholder="Search scrapes...">
					</div>
					<h1>Scrapes:</h1>
					<div>
						<button id="schedule-btn" class="scheduleBtn" onclick="schedule()">Schedule</button>
					</div>
					
					<div>
						<ul id="scrape-ul"></ul>
					</div>
				</div>
			</div>
			
			<div id="task-info-modal" class="schedule-modal">
				<div class="modal-content">
					<div class="modal-header">
						<span id="task-info-close" class="close">&times;</span>
						<h2>Task Information</h2>
					</div>
					<div id="info-body" class="modal-body">
						<p class="info-p"></p>
						<p class="info-p"></p>
						<p class="info-p"></p>
						<p class="info-p"></p>
					</div>
					<div class="modal-footer">
					</div>
				</div>
			</div>
			
			<div id="taskSchedule" class="schedule-modal">
				<div class="modal-content">
					<div class="modal-header">
						<span id="schedule-close" class="close">&times;</span>
						<h2>Please schedule your task</h2>
					</div>
					<div class="modal-body">
						
						<div>
							<div>
						       Type:<select id="Type">
						        <option value="0" selected>(please select:)</option>
						        <option value="Minutely">Minutely</option>
						        <option value="Hourly">Hourly</option>
						        <option value="Daily">Daily</option>
						        <option value="Weekly">Weekly</option>
						       </select>
						       Min:<input id="Min" type="number" min="0" max="59" name="Min">
						       Hour:<input id="Hour"type="number" min="0" max="23" name="Hour">
						       day:<input id="DotW"type="number" min="1" max="31" name="DotW">
						       month:<input id="DotM" type="number" min="1" max="12" name="DotM">
						       <input type="button" id="sub" value="submit" onclick="submit()"/>
					    	</div>						
						</div>
					</div>
					<div class="modal-footer">
						<button id="schedule-cancel-btn">Cancel</button>
					</div>
				</div>
			</div>
		</div>
		<script>
			var schedule_btn = document.getElementById("schedule-btn");
			var schedule = document.getElementById("taskSchedule");
			var cancel_btn = document.getElementById("schedule-cancel-btn");
			var ok_btn = document.getElementById("schedule-ok-btn");
			var close = document.getElementById("schedule-close");
			schedule_btn.onclick = function(){
				schedule.style.display = "block";
			}
			cancel_btn.onclick = function(){
					schedule.style.display = "none";
				}
			close.onclick = function() {
				schedule.style.display = "none";
			}
			window.onclick = function(event) {
				if (event.target == schedule) {
					schedule.style.display = "none";
				}
			}
			
			var info_modal = document.getElementById("task-info-modal");
			var p = document.getElementsByClassName("info-p");
			var close = document.getElementById("task-info-close");
			close.onclick = function() {
				info_modal.style.display = "none";
				for(var i = 0;i < 4; i++){
					p[i].innerText = "";
				}
			}
			window.onclick = function(event) {
				if (event.target == info_modal) {
					info_modal.style.display = "none";
					for(var i = 0;i < 4; i++){
						p[i].innerText = "";
					}
				}
			}
		</script>
	</body>
</html>
