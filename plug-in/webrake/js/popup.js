chrome.storage.local.remove(["userInfoInPlugIn","allTask"],function(){
	var error = chrome.runtime.lastError;
	if (error) {
    		console.error(error);
	}
})

chrome.storage.local.get(['logInFlag'], function(result) {
	if(!result.logInFlag || result.logInFlag == 1){
		chrome.storage.local.set({ "logInFlag": 0});
	}else if(result.logInFlag == 2){
		loginWithWebApp();
	}
});

window.onload = function login() {
	document.getElementById("btn_login").addEventListener('click', function(){
		// get the value of username and password
		var u = document.getElementById("username").value;
	    var p = document.getElementById("password").value;
	    if(u != "" && p != ""){
	    		//do format check
	    		 
	    		// XMLHttpRequest
			var xhr = new XMLHttpRequest();
//			xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/login.php", true);
			xhr.open("POST", "http://192.168.64.2/login.php", true);
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
 * automatically login in after loging in the web app
 * @author peichenYU
 */
function loginWithWebApp(){
	chrome.storage.local.get(['userInfoInWebapp'], function(result){
		// get the value of username and password
		var u = result.userInfoInWebapp.userName;
	    var p = result.userInfoInWebapp.userPassword;    		 
	    	// XMLHttpRequest
		var xhr = new XMLHttpRequest();
//		xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/login.php", true);
		xhr.open("POST", "http://192.168.64.2/login.php", true);
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


