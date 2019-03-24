<!DOCTYPE html>
<html>
    <head>
    <title>WebRake</title>
        <link rel = 'stylesheet' type = 'text/css' href = '../css/home-page.css'>
        <link href='https://unpkg.com/ionicons@4.4.4/dist/css/ionicons.min.css' rel='stylesheet'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:700' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:400' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:300' rel='stylesheet' type='text/css'>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script src="../js/functions.js"></script>
    </head>
<body>
<script>
	<?php
			session_start();
			// Start the session
			$db_host = 'mysql.cs.nott.ac.uk';
			$db_user = 'psyjct';
			$db_pass = '1234Fred';
			$db_name = 'psyjct';

			/*connect into DB*/
			$link = mysqli_connect($db_host, $db_user, $db_pass, $db_name);
			if (!$link)
			{
				die('Could not connect: ' . mysql_error());
			}
			$userId = $_SESSION["userId"];

			// find the result
			$results = mysqli_query($link, "select b.taskName from UserAuthorisation a left join Task b on a.taskID = b.taskID where a.userID="."'"."$userId"."'");

			if(mysqli_num_rows($results) >= 1){
				$taskList = [];
				while($task = mysqli_fetch_assoc($results)){
					$taskList[] = $task['taskName'];
				}
				
			}
			mysqli_close($link);//close the link
	?>
</script>
    <div id="navMenu">
        <p id='navClose' class='close'>
        <ion-icon name="close"></ion-icon>
        <ion-icon ios="ios-close" md="md-close"></ion-icon>
        <i class="icon ion-md-close"></i>
        </p>
        <div id='navContent'>
            <h1>h1 TAG Info</h1>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1 (hover=works)
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 2
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 3
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 4
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 5
            </a>
            <br />
            <br />
            <h1>Another h1 TAG Info</h1>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.1
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.2
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.3
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.4
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.5
            </a>
            <br />
            <br />
            <hr />
            <br />
            <br />
            <h1>Contact us (put h1 tag)</h1>
            <p id=contactInfo>
            e-mail etc....
            </p>
        </div>
    </div>
<header>
  
        <nav>
                <a>
                    <img src='../img/logo.png'/>
                </a>
                <a href='javascript:void(0)' id='slide-menu'>
                    <ion-icon name='menu'></ion-icon>
                    <i class='icon ion-md-menu'></i>
                
                </a>
        </nav>
    <!-- <h1 id='brief'> TEXT THAT SHOWS WHAT THE PURPOSE OF WEBSCRAPE IS.</h1> -->
	<div class="intro">
		<div class="brief-intro">
			<h1 class="intro-text1">Financial technology<br />Data<br />Expertise<br /></h1>
			<h2 class="intro-text2">some text....................................................................</h2>
		</div>
		<div class="plugin-image">
			<img src="../img/plugin_image.png" />
		</div>
	</div>
    <h4 id='underHeaderText'>
        <br />
        <br />
        Text to show what this website is for in more detail.
        <br />
        Some more text about the PlugIn?
        <br />
        And even more text?
    </h4>
    <p id='clickForPlugin'>Click the button to access PlugIn</p>
     <a href='javascript:void(0)' id='clickForPluginButton'>Access PlugIn</a>
</header>
        
<div id='tasks' class='clearfix'>
    <h1 id='taskTitle'><?php echo $_SESSION["userName"];?>'s Tasks</h1>
    <h1>
        
    <p id="task1"></p>
        
        
<script>
	var j = <?php echo count($taskList); ?>;
	var position = document.getElementById('tasks');
	var taskList = <?php echo json_encode($taskList); ?>;
	for (var i = 1; i <= j; i++) {
		var a = document.createElement('a');
		a.href = "javascript:void(0)";
		a.innerHTML = taskList[i - 1];
		console.log(a.innerHTML);
		
		var innerDiv = document.createElement('div');
		innerDiv.id = 'task' + i + 'info';
		innerDiv.appendChild(a);
		
		var outerDiv = document.createElement('div');
		if(i % 2 == 1){
			outerDiv.className ="taskBox left";
		}else{
			outerDiv.className = "taskBox right";
		}
		outerDiv.id = 'task' + i;
		outerDiv.appendChild(innerDiv);
		
		position.appendChild(outerDiv);
	}

    
    
</script>

</div>
<footer class='clearfix'>
    <div class='footer1'>
        <h3>About</h3>
        <a href="javascript:void(0)">Our Story</a>
        <br />
        <br />
        <br />
        <p><span>2019 G52GRP31 Doe All Rights Reserved</span></p>
    </div>
    <div class='footer1'>
        <h3>Contact</h3>
        <a href="javascript:void(0)">Contact us</a>
        <a href="javascript:void(0)">FAQ</a>
        <a href="javascript:void(0)">Write for us</a>
    </div>
</footer>
</body>
</html>
