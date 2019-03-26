<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    $db_host = 'mysql.cs.nott.ac.uk';
	$db_user = 'psyjct';
	$db_pass = '1234Fred';
	$db_name = 'psyjct';

	/*connect into DB*/
	/*connect into DB*/
	$link = mysqli_connect($db_host, $db_user, $db_pass, $db_name);
	if (!$link)
	{
		die('Could not connect: ' . mysql_error());
	}
	
	
	$taskID = $_POST['currentTaskID'];
	$scrapeName = $_POST['scrapeName'];
	$sampleData = $_POST['sampleData'];
	$xPath = $_POST['xPath'];
	
	mysqli_query($link, "insert into Scrape(scrapeName, taskID, Element, sampleData) values("."'"."$scrapeName"."'".","."'"."$taskID"."'".","."'"."$xPath"."'".","."'"."$sampleData"."'".")");
	
	mysqli_close($link);//close the link
?>