<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";
	
	
	$taskID = $_POST['currentTaskID'];
	$scrapeName = $_POST['scrapeName'];
	$sampleData = $_POST['sampleData'];
	$xPath = $_POST['xPath'];
	
	mysqli_query($link, "insert into Scrape(scrapeName, taskID, Element, sampleData) values("."'"."$scrapeName"."'".","."'"."$taskID"."'".","."'"."$xPath"."'".","."'"."$sampleData"."'".")");
	
	mysqli_close($link);//close the link
?>