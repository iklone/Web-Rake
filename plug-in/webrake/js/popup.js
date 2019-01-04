window.onload = function login() {
	document.getElementById("btn_login").addEventListener('click', function(){
	    var username = document.getElementById("username");
	    var pass = document.getElementById("password");
	    	let params = {
			active: true,
			currentWindow: true
		}
		chrome.tabs.query(params, getTabs);
		
		function getTabs(tab){
			if (username.value == "") {
        			alert("Please enter your username");
    			} else if (pass.value  == "") {
        			alert("Please enter your password");
    			} else if(username.value == "psyad10" && pass.value == "123456"){
    				window.location.href ="../html/main.html";
    			} else {
        			alert("Please enter the correct user name or password! ");
    			}
		}
	})
}
