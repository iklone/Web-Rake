window.onload = function login() {
	document.getElementById("btn_login").addEventListener('click', function(){
	    var username = document.getElementById("username");
	    var pass = document.getElementById("password");
	    	let params = {
			active: true,
			currentWindow: true
		}
		//chrome.tabs.query(params, getTabs);
		var query = {
                username : "",
                password : ""
            }
//      query.username = username.value;
//​        query.password = pass.value;
		console.log("!");
		$.ajax({
                url: 'mysql.cs.nott.ac.uk',
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(query),
                dataType: 'json',
                success:function(data){
                    if(data.success == true){
                        console.log("success");
                    } else {
                        console.log("fail");//后台返回一个错误
                    }
                }
            })
	})
}
