<?php
// Start the session
session_start();
?>
<!DOCTYPE html>
<html>
    <head>
    <title>WebRake</title>
        <link rel = 'stylesheet' type = 'text/css' href = 'main2.css'>
        <link href='https://unpkg.com/ionicons@4.4.4/dist/css/ionicons.min.css' rel='stylesheet'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:700' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:400' rel='stylesheet' type='text/css'>
        <link href='https://fonts.googleapis.com/css?family=Raleway:300' rel='stylesheet' type='text/css'>
    </head>
<body>
    <div id="navMenu">
        <p id='navClose' class='close'>
        <ion-icon name="close"></ion-icon>
        <ion-icon ios="ios-close" md="md-close"></ion-icon>
        <i class="icon ion-md-close"></i>
        </p>
        <div id='navContent'>
            <h1>h1 TAG Info</h1>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 1 (hover=works)
            </a>
            <a href='javascript:void(0)'>
            &nbsp;&nbsp;Nav Button Tag 2
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
            <h1>Another h1 TAG Info</h1>
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
            <h1>Contact us (put h1 tag)</h1>
            <p id=contactInfo>
            e-mail etc....
            </p>
        </div>
    </div>
<header>
  
        <nav>
                <a href='mainseven.html'>
                    <img src='logo.png'/>
                </a>
                <a href='javascript:void(0)' id='slide-menu'>
                    <ion-icon name='menu'></ion-icon>
                    <i class='icon ion-md-menu'></i>
                
                </a>
        </nav>
    <h1 id='brief'> TEXT THAT SHOWS WHAT THE PURPOSE OF WEBSCRAPE IS.</h1>
    <h4 id='underHeaderText'>
        <br />
        <br />
        Text to show what this website is for in more detail.
        <br />
        Some more text about the PlugIn?
        <br />
        And even more text?
    </h4>
    <p id='clickForPlugin'>Click the button to access PlugIn</p>
     <a href='javascript:void(0)' id='clickForPluginButton'>Access PlugIn</a>
</header>
<div id='tasks' class='clearfix'>
    <h1 id='taskTitle'><?php echo $_SESSION["userName"];?>'s Tasks</h1>
    <div class='taskBox left' id='task1'>
        <div id='task1info'>
            <a href="javascript:void(0)">
            Task 1
            </a>
        </div>
    </div>
    <div class='taskBox right' id='task2'>
        <div id='task2info'>
            <a href="javascript:void(0)">
            Task 2
            </a>
        </div>    
    </div>
    <div class='taskBox left' id='task3'>
        <div id='task3info'>
            <a href="javascript:void(0)">
            Task 3
            </a>
        </div>    
    </div>
    <div class='taskBox right' id='task4'>
        <div id='task4info'>
            <a href="javascript:void(0)">
            Task 4
            </a>
        </div>    
    </div>
    <div class='taskBox left' id='task5'>
         <div id='task5info'>
            <a href="javascript:void(0)">
            Task 5
            </a>
        </div>   
    </div>
    <div class='taskBox right' id='task6'>
        <div id='task6info'>
            <a href="javascript:void(0)">
            Task 6
            </a>
        </div>    
    </div>
    <div class='taskBox left' id='task7'>
        <div id='task7info'>
            <a href="javascript:void(0)">
            Task 7
            </a>
        </div>    
    </div>
    <div class='taskBox right' id='task8'>
        <div id='task8info'>
            <a href="javascript:void(0)">
            Task 8
            </a>
        </div>    
    </div>
    <div class='taskBox left' id='task9'>
         <div id='task9info'>
            <a href="javascript:void(0)">
            Task 9
            </a>
        </div>   
    </div>
</div>
<footer class='clearfix'>
    <div class='footer1'>
        <h3>Header for links here?</h3>
        <a href="javascript:void(0)">Link here? (hover=works)</a>
        <a href="javascript:void(0)">Link2 here?</a>
        <a href="javascript:void(0)">Link3 here?</a>
        <p>Simple text here?</p>
        <br />
        <br />
        <br />
        <p><span>i guess copyrights &copy; and stuff?</span></p>
    </div>
    <div class='footer1'>
        <h3>Right side of h3 tag </h3>
        <a href="javascript:void(0)">Link right side here?</a>
        <p>Simple text here?</p>
    </div>
</footer>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js">
</script>
<script src="functions.js">
</script>
</body>
</html>