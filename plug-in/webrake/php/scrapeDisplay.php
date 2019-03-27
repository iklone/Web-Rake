<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";
	$taskID = $_POST["currentTaskID"];
	
	// find the result
	$results = mysqli_query($link, "select scrapeName, sampleData from Scrape where taskID ="."'"."$taskID"."'");

	if(mysqli_num_rows($results) >= 1){
		$taskList = [];
		while($task = mysqli_fetch_assoc($results)) {
	    		$taskList[] = $task;
		}

		//send all task names back
		echo json_encode($taskList);
	}else{
		echo "no record";
	}


 	mysqli_close($link);//close the link
?>