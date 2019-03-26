chrome.storage.local.clear(function() {
    var error = chrome.runtime.lastError;
    if (error) {
        console.error(error);
    }
});

window.onload = function login() {
	document.getElementById("btn_login").addEventListener('click', function(){
		// get the value of username and password
		let u = document.getElementById("username").value;
	    let p = document.getElementById("password").value;
	    if(u != "" && p != ""){
	    		//do format check
	    		 
	    		// XMLHttpRequest
			let xhr = new XMLHttpRequest();
			xhr.open("POST", "http://avon.cs.nott.ac.uk/~psyjct/plug-in/login.php", true);
//			xhr.open("POST", "http://192.168.64.2/login.php", true);
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			console.log(xhr);
			xhr.onreadystatechange = function() {
		    		if(xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
	        	    		let response = xhr.responseText;
	        	    		console.log(response);
	        	    		if(response == "fail"){
	        	    			alert("wrong user name or password!");
	        	    		}else{
						chrome.storage.local.set({ "userInfo": {userName:u, userId:response, userPassword:p}});
						chrome.storage.local.set({ "logInWebAppFlag": 0});
	        	    			
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


