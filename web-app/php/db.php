<?php
	/*set up error printing*/
	error_reporting(-1);
	ini_set('display_errors', 'On');

	/*relevant DB details*/
    $db_host = '192.168.64.2';
	$db_user = 'psypy1';
	$db_pass = 'Ypc1998!';
	$db_name = 'psypy1';

	/*connect into DB*/
	$link = mysqli_connect($db_host, $db_user, $db_pass, $db_name);
	if (mysqli_connect_errno() != 0) {
		echo "connection error";
	}
?>