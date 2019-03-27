<?php
	header("Content-type:application/x-www-form-urlencoded;charset=utf-8");
    include "db.php";

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