<?php
	/*set up error printing*/
	error_reporting(-1);
	ini_set('display_errors', 'On');

	/*relevant DB details*/
	$db_host = 'mysql.cs.nott.ac.uk';
	$db_user = 'psyjct';
	$db_pass = '1234Fred';
	$db_name = 'psyjct';

	/*connect into DB*/
	$conn = mysqli_connect($db_host, $db_user, $db_pass, $db_name);
	if (mysqli_connect_errno() != 0) {
		echo "connection error";
	}
?>