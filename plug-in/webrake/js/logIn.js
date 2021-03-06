 /**
 * this is javascript code for logIn.html
 */

/**
 * remove info stored in chrome.storage.local
 * @author peichen YU
 */
chrome.storage.local.remove(["userInfoInPlugIn","allTask",'currentTabId','currentTask','newTaskScrape','currentTaskScrape'],function(){
	var error = chrome.runtime.lastError;
	if (error) {
    		console.error(error);
	}
})

/**
 * check log in flag, if flag = 2 log in plug in automatically
 * @author peichen YU
 */
chrome.storage.local.get(['logInFlag'], function(result) {
	if(!result.logInFlag || result.logInFlag == 1){
		chrome.storage.local.set({ "logInFlag": 0});
	}else if(result.logInFlag == 2){
		loginWithWebApp();
	}
});

/**
 * function used to initilize log in interface and functionality
 * @author peichen YU
 */
window.onload = function initialize() {
	document.getElementById("logo").addEventListener('click', function(){
//		window.open("http://192.168.64.2/web-app/php/home-page.php");
		window.open("http://avon.cs.nott.ac.uk/~psyjct/web-app/php/home-page.php");
	})
	document.getElementById("btn_login").addEventListener('click', function(){
		// get the value of username and password
		var u = document.getElementById("username").value;
	    var p = document.getElementById("password").value;
	    if(u != "" && p != ""){
	    		//do format check
	    		 
	    		// XMLHttpRequest
			var xhr = new XMLHttpRequest();
			xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/login.php", true);
//			xhr.open("POST", "http://192.168.64.2/plug-in/login.php", true);
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			console.log(xhr);
			xhr.onreadystatechange = function() {
		    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	        	    		var response = xhr.responseText;
	        	    		console.log(response);
	        	    		if(response == "fail"){
	        	    			alert("wrong user name or password!");
	        	    		}else{
						chrome.storage.local.set({ "userInfoInPlugIn": {userName:u, userId:response, userPassword:p}});
						chrome.storage.local.set({ "logInFlag": 1});
	        	    			
	        	    			// log in successful
	        	    			window.location.href ="../html/main.html";
	        	    			chrome.browserAction.setPopup({popup: "../html/main.html"});
	        	    		}
	    			}
			}
			xhr.send('username=' + u + '&password=' + p);
		}
		else{
			alert("user name or password is empty");
		}
	})
}

/**
 * automatically login in plug-in when web app has been logged in
 * @author peichenYU
 */
function loginWithWebApp(){
	chrome.storage.local.get(['userInfoInWebapp'], function(result){
		// get the value of username and password
		var u = result.userInfoInWebapp.userName;
	    var p = result.userInfoInWebapp.userPassword;    		 
	    	// XMLHttpRequest
		var xhr = new XMLHttpRequest();
		xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/php/login.php", true);
//		xhr.open("POST", "http://192.168.64.2/plug-in/login.php", true);
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		console.log(xhr);
		xhr.onreadystatechange = function() {
	    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
        	    		var response = xhr.responseText;
				chrome.storage.local.set({ "userInfoInPlugIn": {userName:u, userId:response, userPassword:p}});
				chrome.storage.local.set({ "logInFlag": 3});
        	    			
    	    			// log in successful
    	    			window.location.href ="../html/main.html";
    	    			chrome.browserAction.setPopup({popup: "../html/main.html"});
        	    	}
    		}
		xhr.send('username=' + u + '&password=' + p);
	})
}


