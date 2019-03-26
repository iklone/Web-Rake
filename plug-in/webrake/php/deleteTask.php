<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    $db_host = 'mysql.cs.nott.ac.uk';
	$db_user = 'psyjct';
	$db_pass = '1234Fred';
	$db_name = 'psyjct';

	/*connect into DB*/
	$link = mysqli_connect($db_host, $db_user, $db_pass, $db_name);
	if (!$link)
	{
		die('Could not connect: ' . mysql_error());
	}

	$taskID = $_POST['taskID'];
	
	mysqli_query($link, "delete from UserAuthorisation where taskID ="."'"."$taskID"."'");
	mysqli_query($link, "delete from Scrape where taskID ="."'"."$taskID"."'");
	mysqli_query($link, "delete from Task where taskID ="."'"."$taskID"."'");
	
	mysqli_close($link);//close the link
?>