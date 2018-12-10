window.onload = function sendToCS(){
	document.getElementById("newTask").addEventListener('click', function(){
	
	let params = {
		active: true,
		currentWindow: true
	}
	chrome.tabs.query(params, getTabs);
	
	function getTabs(tab){
		//send message to the content script
		let msg={
			txt: "start script"
		};
		chrome.tabs.sendMessage(tab[0].id, msg);
		}
	})
}
