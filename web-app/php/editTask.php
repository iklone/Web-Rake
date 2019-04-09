<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>FinRake</title>
		<link rel = 'stylesheet' type = 'text/css' href = '../css/editTask.css'>
		<script type="text/javascript" src='../js/editTaskController.js'></script>
	</head>
	<body>
		<div class="main">
			<div class="logo-holder">
				<span id="helpbut">
					<a href='javascript:void(0)'>
					Help
					</a>
				</span>
				<img src="../img/logo.png" />
				<span id="logout">
					<a href='../php/home-page.php'>
					Log Out
					</a>
				</span>
			</div>
			<div class="title">
				<h1>Task List</h1>
			</div>
			
			<div class="taskArea">
				<div id="taskList" class="left-list">
					<div class="taskSearch">
						<input type="text" class="taskSearch-box" id="taskSearch" onkeyup="taskSearcher()" placeholder="Search tasks...">
						<button id="taskSearch-btn">Search</button>
					</div>
					
					<h1>Your Tasks:</h1>
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
							$results = mysqli_query($link, "select b.taskName, b.taskID, b.taskURL from UserAuthorisation a left join Task b on a.taskID = b.taskID where a.userID="."'"."$userId"."'");

							$taskList = [];
							$allScrapeList = [];

							if(mysqli_num_rows($results) >= 1){
								while($task = mysqli_fetch_assoc($results)){
									$taskList[] = $task;
								}
				

							    foreach($taskList as $task){
							    	$taskID = $task['taskID'];

									// find the result
									$results = mysqli_query($link, "select taskID, scrapeName, scrapeID, sampleData as data, flag from Scrape where taskID ="."'"."$taskID"."'");

									$scrapeList = [];
									while($scrapeInfo = mysqli_fetch_assoc($results)) {
										$id = $scrapeInfo['scrapeID'];
										$values = mysqli_query($link, "SELECT b.taskID, b.scrapeName, b.scrapeID, a.resultValue as data, b.flag FROM Result a left join Scrape b on a.scrapeID = b.scrapeID WHERE a.ScrapeID = "."'"."$id"."'"." order by resultTime DESC");
										if(mysqli_num_rows($values) >= 1){
											$value = mysqli_fetch_assoc($values);
										}else{
											$value = $scrapeInfo;
										}
								    	$scrapeList[] = $value;
									}
									$allScrapeList[] = $scrapeList;
								}
							}
						?>
						var currentTaskID;
						var currentTaskName;
						var taskList = <?php echo json_encode($taskList); ?>;
						var allScrapeList = <?php echo json_encode($allScrapeList); ?>;
						console.log(allScrapeList);
						var task_num = taskList.length;
						var ul = document.getElementById('task-ul');
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
							addDelBtnOnTask(li);
							task_delete_btn();
						}
						//task_delete_btn();
						for (var i = 0; i < task_num; i++) (function(i){
							var x = document.getElementsByClassName('task-box')[i];
							x.onclick = function(){
								var btn = document.getElementById("schedule-btn").style.display = "block";
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
								var ul = document.getElementById("scrape-ul");
								while(ul.firstChild){
									ul.removeChild(ul.firstChild);
								}
								if(allScrapeList[index]){
									if(allScrapeList[index].length != 0){
										for(j in allScrapeList[index]){
											var li = document.createElement("li");
											li.className = "scrape-data";
											var data = allScrapeList[index][j].data;
											var s = allScrapeList[index][j].scrapeName + ": " + data;
											var span = document.createElement("span");
											span.className = "scrapeName";
											if(allScrapeList[index][j].flag == 1){
												s = s + " (AI has changed the scrape)";
												span.style.color = "blue";
											}else if(allScrapeList[index][j].flag == 2){
												s = s + " (Scrape has failed)"
												span.style.color = "red";
											}
											var text = document.createTextNode(s);
											span.appendChild(text);
											li.appendChild(span);
											ul.appendChild(li);
											addDelBtnOnScrape(li);
											scrape_delete_btn();
										}
									}
								}
							}
						})(i)
					</script>
				</div>
				<div id="scrapeList" class="right-list">
					<div class="scrapeSearch">
						<input type="text" class="scrapeSearch-box" id="scrapeSearch" onkeyup="scrapeSearcher()" placeholder="Search scrapes...">
						<button id="scrapeSearch-btn">Search</button>
					</div>
					<h1>Your Scrapes:</h1>
					<div>
						<button id="schedule-btn" class="scheduleBtn" onclick="schedule()">Schedule</button>
					</div>
					
					<div>
						<ul id="scrape-ul"></ul>
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
		</script>
	</body>
</html>
