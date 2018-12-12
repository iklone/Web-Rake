// document.addEventListener('DOMContentLoaded', function(){
// 	document.getElementById("btn_login").addEventListener('click',login());
// });
// 
var flag = 0;
window.onload = function login() {
	if(flag == 0){
		document.getElementById("btn_login").addEventListener('click', function(){
			// window.location.href = "../html/ourweb.html";
			var username = document.getElementById("username");
			var pass = document.getElementById("password");
			
			if (username.value == "") {
				alert("Please enter your username");
			} else if (pass.value  == "") {
				alert("Please enter your password");
			} else if(username.value == "psyad10" && pass.value == "123456"){
				chrome.browserAction.setPopup({popup: "../html/ourweb.html"});
				flag = -1;
				// setTimeout(function(){document.location.href = "../html/ourweb.html"},500);
				// window.location = "../html/ourweb.html";
			} else {
				alert("Please enter the correct user name or password! ")
			}
		});
	}
}
