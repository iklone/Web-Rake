<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";

	$taskID = $_POST['taskID'];
	
	mysqli_query($link, "delete from UserAuthorisation where taskID ="."'"."$taskID"."'");
	mysqli_query($link, "delete from Scrape where taskID ="."'"."$taskID"."'");
	mysqli_query($link, "delete from Task where taskID ="."'"."$taskID"."'");
	mysqli_query($link, "delete from Schedule where taskID ="."'"."$taskID"."'");
	
	mysqli_close($link);//close the link
?>