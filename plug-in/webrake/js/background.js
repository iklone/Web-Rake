var flag = 0;

function getFlag(){
	return flag;
}

function setFlag(f){
	flag = f;
}

chrome.contextMenus.create({
    type: 'normal',
    title: 'Add Script',
    id: 'script',
    contexts: ['all']
}, function () {
    console.log('contextMenus are create.');
})

chrome.contextMenus.onClicked.addListener(function addScriptOnClick(info, tab){
	chrome.tabs.sendMessage(tab.id, {'contextMenuId': info.menuItemId, 'info': info}, function(response) {});
})

chrome.runtime.onMessage.addListener(
	function(request, sender, response) {
		console.log(request);
		console.log(flag);
		if (request.greeting == "shouldIStart" && flag == 1){
			var params = {
				active: true,
				currentWindow: true
			}
			chrome.tabs.query(params, sendPermission);
			function sendPermission(tab){
				//send message to the content script
				let msg={
					txt: "start contentScript"
				};
				chrome.tabs.sendMessage(tab[0].id, msg);
		    }
		}
});