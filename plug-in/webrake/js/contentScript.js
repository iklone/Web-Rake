/**
 * this is content script code inserted into web page
 */

// used store all scrape waited to be push into scrapeList
var scrapeWaitedList = [];
// used store all scrape has already been created by user
var scrapeList = [];
// used store content of scrape, including name, path, sample data, which will be sent to popup window and database
var scrapeContentList = [];
// array to store all the links and their position of current page
var linkList = [];
// function used to store webapp userInfo
updateWebappUserInfo();


/**
 * initialize logInFlag 0:both web-app and plug-in have been loged in. 1:plug-in has been loged in
 *                      2:web-app has been loged in. 3:both has been loged in
 * @author peichen YU
 */
if(window.location.toString() == "http://avon.cs.nott.ac.uk/~psyjct/web-app/html/index.html"){
//if(window.location.toString() == "http://192.168.64.2/web-app/html/index.html"){
	chrome.storage.local.get(['logInFlag'], function(result) {
		if(!result.logInFlag){
			chrome.storage.local.set({ "logInFlag": 0});
		}
	});
}

/**
 * check the current web url. if url is for current task then start content script
 * @author peichen YU
 */
chrome.storage.local.get(['currentTask'], function(result) {
	if(result.currentTask){
		if(result.currentTask.taskURL == window.location.toString()){
			disableLinks();
			startContentScript();
		}
	}
})

/**
 * listener used to receive request from background and popup in order to start/stop main functionality of content script
 * @author peichen YU
 */
chrome.runtime.onMessage.addListener(
	function(request, sender, response){ 
		console.log(request.txt);
		if(request.txt == "start contentScript"){
			chrome.storage.local.get(['currentTask'], function(result) {
				if(result.currentTask.taskURL == window.location.toString()){
					disableLinks();
					startContentScript();
				}
			})
		}else if(request.contextMenuId){ // receive request from background(contextMenu)
			// in this part using some method to check the data 
			chrome.storage.local.get(['currentTaskScrape'], function(result) {
				currentScrape = result.currentTaskScrape;
				getClickedElement();
			})
		}else if(request.txt == "stop contentScript"){
			// stop content script
			enableLinks();
			for(i in scrapeWaitedList){
				scrapeWaitedList[i].style.background = "";
				scrapeWaitedList[i].style.outline = "";
			}
			scrapeWaitedList = [];
			scrapeList = [];
			scrapeContentList = [];
			
			document.body.onmousedown = null;
		}
		
		return Promise.resolve("Dummy response to keep the console quiet");
	})

/**
 * function used to start main functionality of content script
 * @author peichen YU
 */
function startContentScript(){
	chrome.storage.local.get(['bgColor'], function(result) {
		if(result.bgColor){
			bgColor = result.bgColor;
		}else{
			result.bgColor = 0;
			bgColor = result.bgColor;
		}
	})
	document.body.onmousedown = function(e){
		// when click mouse left, will display a window shows the item user want, the scrape item will be
		// pushed into scrapeWaitedList
		var body = document.getElementsByTagName("body")[0]
		if(e.button == 0){
			var index = scrapeWaitedList.indexOf(e.target);
			if(index == -1){
				console.log("bgColor = " + bgColor);
				var bg = "rgba(120, 252, 252, 1)";
				if(bgColor == "1"){
					bg = "rgba(255, 0, 0, 1)";
				}else if(bgColor == "2"){
					bg = "rgba(173, 255, 47, 1)";
				}else if(bgColor == "3"){
					bg = "rgba(255, 255, 0, 1)"
				}
				e.target.style.background = bg;
				e.target.style.outline = "solid black 1px";
				scrapeWaitedList.push(e.target);
			}else{
				scrapeWaitedList.splice(index,1);
				e.target.style.background = "";
				e.target.style.outline = "";
			}
		}	
	}
}

/**
  * function used to get all scrape in scrapeWaiting and put them into scrapeList with the name user add
  * if the scrape already exits in scrapeList then alert warnning information
  * @author peichen YU
  */
function getClickedElement(){
	// alert the duplicate scrape information
	for(i in scrapeWaitedList){
		for(j in scrapeList){
			if(scrapeWaitedList[i] === scrapeList[j]){
				var content = scrapeWaitedList[i].innerHTML;
				alert("this scrape already exits in task! sample data is " + content);
				// remove duplicate scrape from waiting list
				scrapeWaitedList[i].style.outline = "";
				scrapeWaitedList[i].style.background = "";
				scrapeWaitedList.splice(i,1);
			}
		}
	}
	
	// put all scrape in scrapeWaitedßList into scrapeList, and update scrapeInTask list
	for(i in scrapeWaitedList){
		var tmpScrape = scrapeWaitedList[i];
		var content = tmpScrape.innerHTML;
		if(content.indexOf('<') == -1 && content.indexOf('>') == -1){
			var scrapeName = prompt("Please enter name of scrape, sample data is " + content, "myScrape");
			while(scrapeNameCheck(scrapeName)){
				scrapeName = prompt("Please enter name of scrape, sample data is " + content, "myScrape");
			}
			
			if(scrapeName != "" && scrapeName != null){
				var xPath = createXPathFromElement(tmpScrape);
	//			var path = getDomPath(tmpScrape);
				console.log(xPath)
				var newScrpae = {
					scrapeName:scrapeName,
					data: content,
					path : xPath
				}
				scrapeList.push(tmpScrape);
				scrapeContentList.push(newScrpae);
			}
		}else{
			alert("Oops,the space you have clicked is illegal, please click again. Reason may be that this space contains too much information ");
		}
		
		tmpScrape.style.outline = "";
		tmpScrape.style.background = "";
	}
	
	//re-set scrapeWaitedList
	scrapeWaitedList = [];
	// send new scrapes info to popup window(main.html)
	if(scrapeContentList != []){
		setTaskContentInfo(scrapeContentList);
		scrapeContentList = [];
	}
	
	document.getElementsByTagName('body')[0].style.opacity = "1";
	document.getElementsByTagName('body')[0].style.zIndex = "";
}


/**
 * function used to get xPath of the element
 * @param {Object} clicked target
 * @author peichen YU
 */
function createXPathFromElement(elm) { 
    var allNodes = document.getElementsByTagName('*');
    var target = elm;
    for (var segs = []; elm && elm.nodeType == 1; elm = elm.parentNode) 
    {

        if (elm.hasAttribute('id')) { 
                var uniqueIdCount = 0; 
                for (var n=0;n < allNodes.length;n++) { 
                    if (allNodes[n].hasAttribute('id') && allNodes[n].id == elm.id) uniqueIdCount++; 
                    if (uniqueIdCount > 1) break; 
                }
                if ( uniqueIdCount == 1) { 
                    segs.unshift('id("' + elm.getAttribute('id') + '")'); 
                    return segs.join('/'); 
                } else { 
                    segs.unshift(elm.localName.toLowerCase() + '[@id="' + elm.getAttribute('id') + '"]'); 
                } 
        } else if (elm.hasAttribute('class')) { 
            segs.unshift(elm.localName.toLowerCase() + '[@class="' + elm.getAttribute('class') + '"]'); 
        } else { 
            for (i = 1, sib = elm.previousSibling; sib; sib = sib.previousSibling) { 
                if (sib.localName == elm.localName)  i++; }; 
                segs.unshift(elm.localName.toLowerCase() + '[' + i + ']'); 
        } 
    }
    var xPath = segs.length ? '/' + segs.join('/') : null;
    console.log(xPath);
    return xPath;
}


/**
 * function used send new scrapes info to popup window(main.html)
 * @param {Object} content for each scrape
 * @author peichen YU
 */
function setTaskContentInfo(list){
	// send new scrape info to pop up window
	chrome.storage.local.set({'newTaskScrape':list});
	chrome.runtime.sendMessage({msg:"scrapeUpdate"});
	chrome.runtime.sendMessage({msg:"displayData"});
	
	chrome.storage.local.get([ "currentTaskScrape"],function(result){
		var scrapeList = result.currentTaskScrape;
		console.log(scrapeList);
		for(i in list){
			var scrape = {
				scrapeName:list[i].scrapeName,
				data:list[i].data
			}
			scrapeList.push(scrape);
		}
		chrome.storage.local.set({ "currentTaskScrape": scrapeList});
	});
	
}

/**
 * if LogInFlag == 1 and user is in webapp login page, then use content Script automatically log in web app
 */
chrome.storage.local.get(['logInFlag'], function(result) {
	if(result.logInFlag == 1){
		if(window.location.toString() == "http://avon.cs.nott.ac.uk/~psyjct/web-app/html/index.html"){
//		if(window.location.toString() == "http://192.168.64.2/web-app/html/index.html"){
			chrome.storage.local.get(['userInfoInPlugIn'], function(result) {
		          var userName = result.userInfoInPlugIn.userName;
		          var userPassword = result.userInfoInPlugIn.userPassword;
		          var userId = result.userInfoInPlugIn.userId;
		          var userNameInput = document.getElementsByName("username")[0];
		          var userPasswordInput = document.getElementsByName("password")[0];
		          chrome.storage.local.set({ "userInfoInWebapp": {userName:userName, userId:userId, userPassword:userPassword}});
		          
		          userNameInput.value = userName;
		          userPasswordInput.value = userPassword;
		          
		          document.getElementsByName("login")[0].submit();
		    });
		   	chrome.storage.local.set({ "logInFlag": 3});
		}
	}
})

/**
 * function used to updata webapp info or extension info to make them accordance
 * @author payne YU
 */
function updateWebappUserInfo(){
    if(window.location.toString() == "http://avon.cs.nott.ac.uk/~psyjct/web-app/html/index.html"){
//	if(window.location.toString() == "http://192.168.64.2/web-app/html/index.html"){
		document.getElementsByName("login")[0].onsubmit = function(){
			var userName = document.getElementsByName("username")[0].value;
			var userPassword = document.getElementsByName("password")[0].value;
			chrome.storage.local.set({ "userInfoInWebapp": {userName:userName, userPassword:userPassword}});
			chrome.storage.local.set({ "logInFlag": 2});
		}
	}else if(window.location.toString() == "http://avon.cs.nott.ac.uk/~psyjct/web-app/php/editTask.php"){
//	}else if(window.location.toString() == "http://192.168.64.2/web-app/php/editTask.php"){
		chrome.storage.local.get(['logInFlag'], function(result) {
			if(result.logInFlag == 2){
				chrome.runtime.sendMessage({msg:"re-logInPlugIn"});
			}
		})
	}
}


/**
 *  check whether scrape name is empty or duplicate
 * @author peichen YU
 */
function scrapeNameCheck(scrapeName){
	if(scrapeName == ""){
		alert("scrape name shouldn't be empty");
		return true;
	}
	
	for(i in currentScrape){
		if(currentScrape[i].scrapeName == scrapeName){
			alert("scrape name already exists in task!");
			return true;
		}
	}
	
	return false;
}

/**
 * function used to disable all the links in current page
 * @author peichen Yu
 */
function disableLinks() {
	var links = document.getElementsByTagName('a');
	var link;
	var href;
	for (var i = 0; i < links.length; i++) (function(i){
		link = links[i];
		if(link.href){
			href = link.href
			linkList.push([i, href]);
			link.removeAttribute('href');
		}
	})(i)
}
/**
 * function used to enable all the links in current page
 * @author peichen YU
 */
function enableLinks() {
	var links = document.getElementsByTagName('a');
	for (var i = 0; i < linkList.length; i++) {
		link = links[linkList[i][0]];
		link.setAttribute('href', linkList[i][1]);
	}
}