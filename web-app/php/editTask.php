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
			} input{height: 30px;} button{height: 36px;width: 85px;}
			.scrapeSearch{
				position: relative;
				top: 20px;
				left: 20px;
				height: 41px;
			} input{height: 30px;} button{height: 36px;width: 85px;}
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
			.modal-body{
				padding: 30px 120px;
				font-size: large;
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
					<script>
						var j = <?php echo count($taskList); ?>;
						var position = document.getElementById('tasks');
						var taskList = <?php echo json_encode($taskList); ?>;
						console.log(taskList);
					</script>
				</div>
				<?php echo "123"; ?>
				<div id="scrapeList" class="right-list">
					<div class="scrapeSearch">
						<input type="text" class="scrapeSearch-box" placeholder="Search scrapes...">
						<button id="scrapeSearch-btn">Search</button>
					</div>
					<h1>Your scrape:</h1>
					<div>
						<button id="schedule-btn" class="scheduleBtn">Schedule</button>
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
						<label class="container">Weekly
							<input type="radio" name="radio">
							<span class="checkmark"></span>
						</label>
					</div>
					<div class="modal-footer">
						<button id="schedule-ok-btn">Ok</button>
						<button id="schedule-cancel-btn">Cancel</button>
					</div>
				</div>
			</div>
		</div>
		
	</body>
</html>
