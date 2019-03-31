<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<!-- <script src="../js/editTask.js"></script> -->
		<style type="text/css">
			.main{
				background-color: #AED6F1;
				height: 100%;
				width: 100%;
			}
			.logo-holder{
				position: relative;
				text-align: center;
				top: 20px;
			} img{height:50px; }
			.title{
				position: relative;
				text-align: center;
				font-size: 24px;
			}
			.taskArea{
				position: relative;
				z-index: 1;
				overflow-x: hidden;
				top: 10px;
				border: 5px solid darkblue;
			} h1{position: relative; left: 20px;}
			.left-list{
				position: fixed;
				height: 100%;
				width: 50%;
				left: 8px;
			}
			.right-list{
				position: fixed;
				height: 100%;
				width: 49%;
				left: 50%;
				border-left: 5px solid darkblue;
			}
			.taskSearch{
				position: relative;
				top: 20px;
				left: 20px;
				height: 41px;
			} input{height: 30px;} 
			.taskSearch button{height: 36px;width: 85px;}
			.scrapeSearch{
				position: relative;
				top: 20px;
				left: 20px;
				height: 41px;
			} input{height: 30px;} 
			.scrapeSearch button{height: 36px;width: 85px;}
			.task-box{
				border: 1px solid black;
				border-radius: 7px;
				margin: 15px 5px;
				padding: 5px 10px;
				background-color: lightcyan;
				position: relative;
				top: -5px;
				left: -15px;
				overflow: auto;
				height: 60px;
				width: 88%;
			} 
			.task-box:hover{background-color: greenyellow; cursor: pointer;}
			.taskName{font-size: 25px; font-weight:bold}
			.url{
				position: absolute;
				left: 10px;
				top: 50px;
			}
			.scrape-data{
				/* cursor: pointer; */
				position: relative;
				margin: 16px 0px;
				padding: 12px 8px 12px 20px;
				background: #eee;
				font-size: 18px;
				transition: 0.2s;
				border: 1px solid darkblue;
				overflow: auto;
			}
			.scheduleBtn{
				position: absolute;
				top: 70px;
				left: 80%;
				height: 35px;
				width: 90px;
			}
			.schedule-modal {
				display: none;
				position: fixed; 
				z-index: 1; 
				padding-top: 100px; 
				left: 0;
				top: 0;
				width: 100%; 
				height: 100%; 
				overflow: auto; 
				background-color: rgb(0,0,0); 
				background-color: rgba(0,0,0,0.4); 
			}
			.modal-content{
				position: relative;
				background-color: #fefefe;
				margin: auto;
				padding: 0;
				border: 1px solid #888;
				width: 50%;
				box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
				animation-name: animatetop;
				animation-duration: 0.4s
			}
			.close {
				color: white;
				float: right;
				font-size: 28px;
				font-weight: bold;
			}
			.close:hover,
			.close:focus {
			color: #000;
			text-decoration: none;
			cursor: pointer;
			}
			.modal-header {
				padding: 2px 16px;
				background-color: #d93c3c;
				color: white;
			}
			.modal-footer {
				padding: 20px 26px;
				background-color: #d93c3c;
				color: white;
			}
			.modal-footer button{
				position: relative;
				left: 70%;
			}
			.modal-body{
				padding: 30px 50px;
				font-size: large;
			}
			.type{
				position: absolute;
				top: 100px;
			}
			.radio{
				position: relative;
				left: 80px;
			}
		</style>
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
							mysqli_close($link);//close the link
						?>
						var task_num = <?php echo count($taskList); ?>;
						var ul = document.getElementById('task-ul');
						var taskList = <?php echo json_encode($taskList); ?>;
						console.log(taskList);
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
						}
						<?php
						    include "db.php";

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
						var allScrapeList = <?php echo json_encode($allScrapeList); ?>;
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
									}
								}	
							}
						})(i)
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
		</script>
	</body>
</html>
