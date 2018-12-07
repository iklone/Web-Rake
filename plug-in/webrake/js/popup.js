window.onload = function(){
	document.getElementById("btn_login").addEventListener("click",login());
}

function login() {
 
    var username = document.getElementById("username");
    var pass = document.getElementById("password");
 
    if (username.value == "") {
 
        alert("Please enter your username");
 
    } else if (pass.value  == "") {
 
        alert("Please enter your password");
 
    } else if(username.value == "psyad10" && pass.value == "123456"){
 
        window.location.href = "../html/ourweb.html";
 
    } else {
 
        alert("Please enter the correct user name or password! ")
 
    }
}