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
        while($task = mysqli_fetch_assoc($results)){
            $taskList[] = $task['taskName'];
		}
	}

 	mysqli_close($link);//close the link
?>
