chrome.browserAction.setPopup({popup: "../html/main.html"});
window.onload = function connectCS(){
	document.getElementById("newTask").addEventListener('click', function(){
		var bg = chrome.extension.getBackgroundPage();
		if(bg.getFlag() == 0){
			let params = {
				active: true,
				currentWindow: true
			}
			chrome.tabs.query(params, getTabs);
			
			function getTabs(tab){
				//send message to the content script
				let msg={
					txt: "start contentScript"
				};
				console.log(tab[0].id);
				chrome.tabs.sendMessage(tab[0].id, msg);
			}
			bg.setFlag(1);
		}
	})
	
	
}
