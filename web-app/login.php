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

	$username = $_POST["username"];
	$password = $_POST["password"];
	//*****************if the user name is same************************************
	// find the result
	$result = mysqli_query($link, "select userPassword from User where binary userName="."'"."$username"."'");
	$pass=mysqli_fetch_row($result);
	$pa = $pass[0];

	if($pa == $password){
		$id = mysqli_query($link, "select userID from User where binary userName="."'"."$username"."'"." and binary userPassword="."'"."$password"."'");
		$userId =  mysqli_fetch_row($id);
		echo $userId[0];
	}else{
		echo "fail";
	}
 	mysqli_close($link);//close the link
?>
