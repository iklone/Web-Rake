// document.addEventListener('DOMContentLoaded', function(){
// 	document.getElementById("btn_login").addEventListener('click',login());
// });
// 
window.onload = function login() {
	document.getElementById("btn_login").addEventListener('click', function(){
	// window.location.href = "../html/ourweb.html";
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
    				window.location.href ="../html/ourweb.html"
				chrome.browserAction.setPopup({tabId:tab[0].id, popup: "../html/ourweb.html"});
    			} else {
        			alert("Please enter the correct user name or password! ")
    			}
		}
	})
}
