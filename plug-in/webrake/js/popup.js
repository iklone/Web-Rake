// document.addEventListener('DOMContentLoaded', function(){
// 	document.getElementById("btn_login").addEventListener('click',login());
// });
// 
var flag = 0;
window.onload = function login() {

	document.getElementById("btn_login").addEventListener('click', function(){
	// window.location.href = "../html/ourweb.html";
	    var username = document.getElementById("username");
	    var pass = document.getElementById("password");
// 		var xmlhttp;
// 		xmlhttp=new XMLHttpRequest();
	 
	    if (username.value == "") {
	        alert("Please enter your username");
	    } else if (pass.value  == "") {
	        alert("Please enter your password");
	    } else if(username.value == "psyad10" && pass.value == "123456"){
			chrome.browserAction.setPopup({popup: "../html/ourweb.html"});
			//window.location.href = "../html/ourweb.html";
			//window.location.reload();
// 			xmlhttp.open("GET","../html/ourweb.html",true);
// 			xmlhttp.send();
	    } else {
	        alert("Please enter the correct user name or password! ")
	    }
		});
}
