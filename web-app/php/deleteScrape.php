<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";

	mysqli_query($link, "delete from Scrape where scrapeID =$_POST[scrapeID]");
	mysqli_query($link, "delete from Result where scrapeID =$_POST[scrapeID]");
	
	mysqli_close($link);//close the link
?>