<?php
    include "db.php";

	$result = mysqli_query($link,"insert into Schedule(taskID, Type, Min, Hour, DotW, DotM) values('$_POST[taskID]','$_POST[Type]','$_POST[Min]','$_POST[Hour]','$_POST[DotW]','$_POST[DotM]')");
	
 	mysqli_close($link);//close the link
?>
