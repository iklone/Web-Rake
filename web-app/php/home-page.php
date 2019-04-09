<!DOCTYPE html>
<html>

<head>
    <title>FinRake</title>
	<link rel = 'stylesheet' type = 'text/css' href = '../css/home-page.css'>
	<link rel = 'stylesheet' type = 'text/css' href = '../css/home-page.css'>
	<link href='https://unpkg.com/ionicons@4.4.4/dist/css/ionicons.min.css' rel='stylesheet'>
	<link href='https://fonts.googleapis.com/css?family=Raleway:700' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Raleway:400' rel='stylesheet' type='text/css'>
	<link href='https://fonts.googleapis.com/css?family=Raleway:300' rel='stylesheet' type='text/css'>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script src="../js/functions.js"></script>
</head>
	
<body>
    <div id="navMenu">
        <p id='navClose' class='close'>
        <ion-icon name="close"></ion-icon>
        <ion-icon ios="ios-close" md="md-close"></ion-icon>
        <i class="icon ion-md-close"></i>
        </p>
        <div id='navContent'>
            <h1>Our Story</h1>
            <a href='../html/about.html'>
            &nbsp;&nbsp;About Us
            </a>
            <a href='../php/home-page.php'>
            &nbsp;&nbsp;Home
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 3
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 4
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 5
            </a>
            <br />
            <br />
            <h1>FAQ</h1>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.1
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.2
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.3
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.4
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1.5
            </a>
            <br />
            <br />
            <hr />
            <br />
            <br />
            <h1>Contact us</h1>
            <p id=contactInfo>
            e-mail etc....
            </p>
        </div>
    </div>
	
	<header>
			<nav>
				<div class=logo1 >
					<a href='https://www.refinitiv.com'>
						<img src='../img/logo.png'/>
					</a>
				</div>
				
				<div id="login">
					<a href='../html/index.html'>
					Login
					</a>
				</div>
				
				<div id="signUp"> 
					<a href='../html/register.html'>
					Sign Up
					</a>
				</div>
				<a href='javascript:void(0)' id='slide-menu'>
					<ion-icon name='menu'></ion-icon>
					<i class='icon ion-md-menu'></i>
				</a>
			</nav>
		<!-- <h1 id='brief'> TEXT THAT SHOWS WHAT THE PURPOSE OF WEBSCRAPE IS.</h1> -->
		<div class="intro">
			<div class="brief-intro">
				<h1 class="intro-text1">FinRake<br /></h1>
				<h2 class="intro-text2">An Expert Data Scraping Tool Built for Finance</h2>
			</div>
			<div class="plugin-image">
				<img src="../img/plugin_image.png" />
			</div>
		</div>
		<h4 id='underHeaderText'>
			<br />
			<br />
			An intuitive web-scraping tool: simple but powerful
			<br />
			Built with financial use in mind
			<br />
			Automatic scrape managing keeps up with dynamic web-pages
		</h4>
		<p id='clickForPlugin'>Install the Plugin here:</p>
		<a href='../../plug-in/finrake.zip' id='clickForPluginButton'>Download PlugIn</a>
	</header>
        
<!--
<div id='tasks' class='clearfix'>
    <h1 id='taskTitle'><php echo $_SESSION["userName"];?>'s Tasks</h1>
    <h1>
        
    <p id="task1"></p>
        
        
<script>
	var j = <php echo count($taskList); ?>;
	var position = document.getElementById('tasks');
	var taskList = <php echo json_encode($taskList); ?>;
	for (var i = 1; i <= j; i++) {
		var a = document.createElement('a');
		a.href = "javascript:void(0)";
		a.innerHTML = taskList[i - 1];
		console.log(a.innerHTML);
		
		var innerDiv = document.createElement('div');
		innerDiv.id = 'task' + i + 'info';
		innerDiv.appendChild(a);
		
		var outerDiv = document.createElement('div');
		if(i % 2 == 1){
			outerDiv.className ="taskBox left";
		}else{
			outerDiv.className = "taskBox right";
		}
		outerDiv.id = 'task' + i;
		outerDiv.appendChild(innerDiv);
		
		position.appendChild(outerDiv);
	}

    
    
</script>

</div>
-->
<footer class='clearfix'>
    <div class='footer1'>
        <h3>About</h3>
        Built for Refinitiv under supervision by<br>
        the University of Nottingham<br>
        <br />
        <p><span>2019 G52GRP31</span></p>
    </div>
    <div class='footer1'>
        <h3>Contact</h3>
        <a href="javascript:void(0)">Contact us</a>
        <a href="javascript:void(0)">FAQ</a>
        <a href="javascript:void(0)">Write for us</a>
    </div>
</footer>
</body>
</html>
