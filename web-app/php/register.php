<?php
//connect to database
include "db.php";


$result = mysqli_query($link, "select * from User where binary userName='$_POST[username]'");
if(mysqli_num_rows($result) > 0){
    echo "username already exits!";
    echo "<a href=../html/register.html>[Click here to continue register]</a>";
} else {
    mysqli_query($link, "INSERT INTO User(userName, userPassword) VALUES ('$_POST[username]', '$_POST[password]')");
	header('Location: ../html/index.html');
}
?>