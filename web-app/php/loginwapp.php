<?php
// Start the session
session_start();
?>
<head>
</head>
<body>
<?php
include "login.php";
if($pa == $password){
	$_SESSION["userId"] = $userId[0];
	$_SESSION["userName"] = $_POST["username"];
	header('Location: mainseven.php');
} else {
	header('Location: ../html/index.html');
}
?>
</body>