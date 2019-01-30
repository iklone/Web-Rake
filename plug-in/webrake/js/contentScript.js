chrome.runtime.sendMessage({greeting: "shouldIStart"});

chrome.runtime.onMessage.addListener(
	function(request, sender, response){
		if(request.txt == "start contentScript"){
			document.body.onmousedown = function(e){
				if(e.button == 2){
					getClickedElement(e);
					chrome.extension.onMessage.addListener(function(request, sender, response){
						if(request.contextMenuId == "script"){
						//put into task list
						}
					})
				}
			}
		}
	})


function getClickedElement(e){
	console.log(e.target.innerHTML);
	if(e.target.style.outline == ''){
		e.target.style.outline = 'solid black 1px';
	}
	var path = getDomPath(e.target);
	console.log(path);
}


function getDomPath(el) {
	if(!el){
		//click error!
		return;
	}
	
	var stack = [];
	while (el.parentNode != null) {
		//console.log(el.nodeName);
	    var sibCount = 0;
	    var sibIndex = 0;
	    // get sibling indexes
	    for (var i = 0; i < el.parentNode.childNodes.length; i++) {
	    		var sib = el.parentNode.childNodes[i];
	      	if ( sib.nodeName == el.nodeName ) {
	        		if ( sib === el ) {
	          		sibIndex = sibCount;
	        		}
	        	sibCount++;
	      	}
	    }
	    var nodeName = el.nodeName.toLowerCase();
	    if(el.hasAttribute('id') && el.id != ''){
	  		nodeName += '#' + el.id;
	  	}
		if(el.hasAttribute('class') && el.id != ''){
			nodeName += '.' + el.className;
		}
		if ( sibCount > 1 ) {
	  		nodeName += ':eq(' + sibIndex + ')';
	  }
		stack.unshift(nodeName);
	    el = el.parentNode;
	}
	//console.log(stack);
	return stack.slice(1).join(' > '); 
}
