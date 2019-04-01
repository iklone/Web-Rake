<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";

	$taskID = $_POST['scrapeID'];
	
	mysqli_query($link, "delete from Scrape where scrapeID ="."'"."$scrapeID"."'");
	mysqli_query($link, "delete from Result where scrapeID ="."'"."$scrapeID"."'");
	
	mysqli_close($link);//close the link
?>