<?php
//connect to database
	include "db.php";
	mysqli_query($link, "update Scrape Set Flag = 0 where scrapeID = $_POST[scrapeID]");
	mysqli_close($link)
?>
