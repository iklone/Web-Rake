<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";

	$userId = $_POST["userId"];
	$taskName = $_POST['taskName'];
	$taskDescription = $_POST["taskDescription"];
	$taskURL = $_POST['url'];


	mysqli_query($link, "insert into Task (taskName,taskDescription,taskURL) values("."'"."$taskName"."'".","."'"."$taskDescription"."'".","."'"."$taskURL"."'".")");
	//**************if the taskName is duplicate work to do
	$result = mysqli_query($link, "select taskID from Task where binary taskName = "."'"."$taskName"."'");
	$pass = mysqli_fetch_row($result);
	$taskId = $pass[0];
	echo $taskId;

	mysqli_query($link, "insert into UserAuthorisation(userID, taskID) values("."'"."$userId"."'".","."'"."$taskId"."'".")");
	
	mysqli_close($link);//close the link
?>