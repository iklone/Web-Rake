function check(){
   for(var i=0;i<document.registerForm.elements.length-1;i++){
      if(document.registerForm.elements[i].value==""){
         document.registerForm.elements[i].focus();
         console.log(document.registerForm.elements[i].value);
         return false;
      }
   }
   return true;
}