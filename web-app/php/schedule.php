<?php
    include "db.php";
	
	$result = mysqli_query($link, "select b.taskID from UserAuthorisation a left join Task b on a.taskID = b.taskID where a.userID = $_SESSION["userId"] and b.taskName = $_POST["taskName"]");
	$pass = mysqli_fetch_row($result);
	$taskID = $pass[0];
	
	mysqli_query($link,"insert into Schedule(taskID, Type, Min, Hour, DotW, DotM) values("."'"."$taskID"."'".",$_POST["Type"],$_POST["Min"],$_POST["Hour"],$_POST["DotW"],$_POST["DotM"])");
	
 	mysqli_close($link);//close the link
?>
