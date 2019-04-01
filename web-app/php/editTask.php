<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<!-- <script src="../js/editTask.js"></script> -->
		<link rel = 'stylesheet' type = 'text/css' href = '../css/editTask.css'>
		<script type="text/javascript" src='../js/editTaskController.js'></script>
	</head>
	<body>
		<div class="main">
			<div class="logo-holder">
				<img src="../img/logo.png" />
			</div>
			<div class="title">
				<h1>Task List</h1>
			</div>
			
			<div class="taskArea">
				<div id="taskList" class="left-list">
					<div class="taskSearch">
						<input type="text" class="taskSearch-box" placeholder="Search tasks...">
						<button id="taskSearch-btn">Search</button>
					</div>
					
					<h1>Your Task:</h1>
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
							
							if(mysqli_num_rows($results) >= 1){
								$taskList = [];
								while($task = mysqli_fetch_assoc($results)){
									$taskList[] = $task;
								}
				
							}

						    $allScrapeList = [];
						    foreach($taskList as $task){
						    	$taskID = $task['taskID'];

								// find the result
								$results = mysqli_query($link, "select scrapeName, scrapeID, sampleData from Scrape where taskID ="."'"."$taskID"."'");

								$scrapeList = [];
								while($scrapeInfo = mysqli_fetch_assoc($results)) {
									$id = $scrapeInfo['scrapeID'];
									$values = mysqli_query($link, "SELECT b.scrapeName, a.resultValue FROM Result a left join Scrape b on a.scrapeID = b.scrapeID WHERE a.ScrapeID = "."'"."$id"."'"." order by resultTime DESC");
									if(mysqli_num_rows($values) >= 1){
										$value = mysqli_fetch_assoc($values);
									}else{
										$value = $scrapeInfo;
									}
							    	$scrapeList[] = $value;
								}
								$allScrapeList[] = $scrapeList;
							}
						?>
						var task_num = <?php echo count($taskList); ?>;
						var ul = document.getElementById('task-ul');
						var taskList = <?php echo json_encode($taskList); ?>;
						var allScrapeList = <?php echo json_encode($allScrapeList); ?>;
						for (var i = 1; i <= task_num; i++){
							var li = document.createElement('li');
							li.className = "task-box";
							var text = document.createTextNode(taskList[i - 1].taskName);
							var span = document.createElement("span");
							span.className = "taskName";
							span.appendChild(text);
							li.appendChild(span);
							//console.log(span);
							//ul.appendChild(li);
							
							// var url = document.createTextNode("URL: " + taskList[i - 1].taskURL);
							var urlHref = document.createElement("a");
							urlHref.className = "url";
							urlHref.href = taskList[i - 1].taskURL;
							urlHref.innerText = taskList[i - 1].taskURL;
							// urlHerf.appendChild(url);
							li.appendChild(urlHref);
							ul.appendChild(li);
							addDelBtnOnTask(li);
						}
						//task_delete_btn();
						for (var i = 0; i < task_num; i++) (function(i){
							var x = document.getElementsByClassName('task-box')[i];
							x.onclick = function(){
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
								console.log(allScrapeList[index]);
								if(allScrapeList[index].length != 0){
									for(j in allScrapeList[index]){
										var li = document.createElement("li");
										li.className = "scrape-data";
										var data;
										if(allScrapeList[index][j].sampleData){
											data = allScrapeList[index][j].sampleData;
							    		}else if(allScrapeList[index][j].resultValue){
							    			data = allScrapeList[index][j].resultValue;
							    		}
										var text = document.createTextNode(allScrapeList[index][j].scrapeName + ": " + data);
										li.appendChild(text);
										ul.appendChild(li);
										addDelBtnOnScrape(li);
									}
								}	
							}
						})(i)
						//scrape_delete_btn();
					</script>
				</div>
				<div id="scrapeList" class="right-list">
					<div class="scrapeSearch">
						<input type="text" class="scrapeSearch-box" placeholder="Search scrapes...">
						<button id="scrapeSearch-btn">Search</button>
					</div>
					<h1>Your scrape:</h1>
					<div>
						<button id="schedule-btn" class="scheduleBtn" onclick="schedule()">Schedule</button>
					</div>
					
					<div>
						<ul id="scrape-ul"></ul>
					</div>
				</div>
			</div>
			<script>
				function schedule(){
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
				}
				schedule();
			</script>
			
			<div id="taskSchedule" class="schedule-modal">
				<div class="modal-content">
					<div class="modal-header">
						<span id="schedule-close" class="close">&times;</span>
						<h2>Please schedule your task</h2>
					</div>
					<div class="modal-body">
						
						<div>
							<p class="type">Type:</p>
							<div class="radio">
								<label class="container">Minutely
									<input type="radio" checked="checked" name="radio">
									<span class="checkmark"></span>
									&nbsp &nbsp &nbsp
								</label>
								<label class="container">Hourly
									<input type="radio" name="radio">
									<span class="checkmark"></span>
									&nbsp &nbsp &nbsp
								</label>
								<label class="container">Daily
									<input type="radio" name="radio">
									<span class="checkmark"></span>
									&nbsp &nbsp &nbsp
								</label>
								<label class="container">Weekly
									<input type="radio" name="radio">
									<span class="checkmark"></span>
								</label>
							</div>
							
						</div>
					</div>
					<div class="modal-footer">
						<button id="schedule-ok-btn">Apply</button>
						<button id="schedule-cancel-btn">Cancel</button>
					</div>
				</div>
			</div>
		</div>
		<script>
		</script>
	</body>
</html>
