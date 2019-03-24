<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
// Start the session
session_start();
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
		//while($task = mysqli_fetch_assoc($results)) {
        while($task = @mysqli_fetch_assoc($results)){
            $taskList[] = $task['taskName'];
         //   echo $taskList[0];
           // echo $task['taskName']; 
          //  echo "<br>";
	    	// $taskList[] = $task;
		}
	}else{
		echo "no record";
	}

 	mysqli_close($link);//close the link
?>
<!DOCTYPE html>
<html>
    <head>
    <title>WebRake</title>
        <link rel = 'stylesheet' type = 'text/css' href = '../css/main2.css'>
        <link href='https://unpkg.com/ionicons@4.4.4/dist/css/ionicons.min.css' rel='stylesheet'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:700' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:400' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:300' rel='stylesheet' type='text/css'>
    </head>
<body>
    <h1><php var_dump(count($taskList)); ?></h1>
    <h1><php echo $taskList[1]; ?></h1>
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
                <a href='mainseven.php'>
                    <img src='../img/logo.png'/>
                </a>
                <a href='javascript:void(0)' id='slide-menu'>
                    <ion-icon name='menu'></ion-icon>
                    <i class='icon ion-md-menu'></i>
                
                </a>
        </nav>
    <h1 id='brief'> TEXT THAT SHOWS WHAT THE PURPOSE OF WEBSCRAPE IS.</h1>
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
    
    var j = "<?php echo count($taskList) ?>";
    for (var i = 0; i < 10; i++) {
    		var MyDiv1 = document.getElementById('task1').innerHTML
	//  alert(MyDiv1)
    }
    
    
</script>
        </h1>
    <!--<div class='taskBox left' id='task1'>
        <div id='task1info'>
            <a href="javascript:void(0)">
            Task 1test
            </a>
            <h4><?php print_r($taskList[0]); ?></h4>
        </div>
    </div>
    <div class='taskBox right' id='task2'>
        <div id='task2info'>
            <a href="javascript:void(0)">
            Task 2
            </a>
            <h4><?php print_r($taskList[1]); ?></h4>
        </div>    
    </div>
    <div class='taskBox left' id='task3'>
        <div id='task3info'>
            <a href="javascript:void(0)">
            Task 3
            </a>
            <h4><?php print_r($taskList[2]); ?></h4>
        </div>    
    </div>
    <div class='taskBox right' id='task4'>
        <div id='task4info'>
            <a href="javascript:void(0)">
            Task 4
            </a>
            <h4><?php print_r($taskList[3]); ?></h4>
        </div>    
    </div>
    <div class='taskBox left' id='task5'>
         <div id='task5info'>
            <a href="javascript:void(0)">
            Task 5
            </a>
             <h4><?php print_r($taskList[4]); ?></h4>
        </div>   
    </div>
    <div class='taskBox right' id='task6'>
        <div id='task6info'>
            <a href="javascript:void(0)">
            Task 6
            </a>
            <h4><?php print_r($taskList[5]); ?></h4>
        </div>    
    </div>
    <div class='taskBox left' id='task7'>
        <div id='task7info'>
            <a href="javascript:void(0)">
            Task 7
            </a>
            <h4><?php print_r($taskList[6]); ?></h4>
        </div>    
    </div>
    <div class='taskBox right' id='task8'>
        <div id='task8info'>
            <a href="javascript:void(0)">
            Task 8
            </a>
            <h4><?php print_r($taskList[7]); ?></h4>
        </div>    
    </div>
    <div class='taskBox left' id='task9'>
         <div id='task9info'>
            <a href="javascript:void(0)">
            Task 9
            </a>
             <h4><?php print_r($taskList[8]); ?></h4>
        </div>   
    </div>
    <div class='taskBox right' id='task10'>
         <div id='task10info'>
            <a href="javascript:void(0)">
            Task 10
            </a>
             <h4><?php print_r($taskList[9]); ?></h4>
        </div>   
    </div>-->
</div>
<footer class='clearfix'>
    <div class='footer1'>
        <h3>About</h3>
        <a href="javascript:void(0)">Our Story</a>
        <br />
        <br />
        <br />
        <p><span>2019 G52GRP Doe All Rights Reserved</span></p>
    </div>
    <div class='footer1'>
        <h3>Contact</h3>
        <a href="javascript:void(0)">Contact us</a>
        <a href="javascript:void(0)">FAQ</a>
        <a href="javascript:void(0)">Write for us</a>
    </div>
</footer>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js">
</script>
<script src="functions.js">
</script>
</body>
</html>
