function customConsole() {
    window.myNewFunction = function() {
        console.log("Hello I'm available from console.");
    };
}

var script = document.createElement('script'),
    code   = document.createTextNode('(' + customConsole + ')();');
script.appendChild(code);
(document.body || document.head || document.documentElement).appendChild(script);