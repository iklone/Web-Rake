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
	    /*el.nodetype()*************
	     * need to do
	    used to specify the kind of node 
	    */
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

function getClickedElement(){
	document.addEventListener("click",function(e){
			console.log(e.target.innerHTML);
			if(e.target.style.outline == ''){
				e.target.style.outline = 'solid black 1px';
			}else{
				e.target.style.outline = '';
			}
			var path = getDomPath(e.target);
			console.log(path);
	})
}
//
//function highlight(style){
//	style = 
//}

getClickedElement();

