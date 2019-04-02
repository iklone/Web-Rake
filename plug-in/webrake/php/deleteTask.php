<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";
	
	
	mysqli_query($link, "delete from Result where scrapeID in (select scrapeID from Scrape where taskID = $_POST[taskID])");
	mysqli_query($link, "delete from UserAuthorisation where taskID = $_POST[taskID]");
	mysqli_query($link, "delete from Scrape where taskID =$_POST[taskID]");
	mysqli_query($link, "delete from Task where taskID = $_POST[taskID]");
	mysqli_query($link, "delete from Schedule where taskID = $_POST[taskID]");
	
	mysqli_close($link);//close the link
?>