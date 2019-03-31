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
	header('Location: home-page.php');
} else {
	header('Location: ../html/index.html');
}
?>
</body>