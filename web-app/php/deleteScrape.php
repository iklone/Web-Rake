<?php
    include "db.php";

	mysqli_query($link, "delete from Scrape where scrapeID =$_POST[scrapeID]");
	mysqli_query($link, "delete from Result where scrapeID =$_POST[scrapeID]");
	
	mysqli_close($link);//close the link
?>