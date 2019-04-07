/**
 * go to register page
 * @author peichen YU
 */
window.onload = function registerButtonClicked(){
	document.getElementById("registerButton").onclick = function(){
		location.href = "register.html";
	}
}

/**
 * check the format of password and userName
 * @author peichen YU
 */
function check(){
	var userName = document.login.elements[0].value;
	var password = document.login.elements[1].value;
	if(userName = "" || password == ""){
		return false;
	}else{
		return true;
	}
}
