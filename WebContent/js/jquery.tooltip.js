$.fn.ToolTips=function(tip_text){ 
	var div = document.createElement("div"); 
	div.style.cssText = 'text-align:left;width:300px; line-height:15px; border:solid 1px #000000; background-color:#FFFFFF; color:#000000; padding:6px 10px; font-size:10px;position:absolute;border-radius: 0.5rem'; 
	div.onclick=function(){$(div).remove();}; 
	$(this).mouseover(function(e){ 
	  if(!e){e=window.event;} 
	  div.innerHTML= tip_text; 
	  var doc = document.documentElement ? document.documentElement : document.body; 
	  div.style.left=(e.clientX+doc.scrollLeft + 5)+"px"; 
	  div.style.top=(e.clientY+doc.scrollTop + 5)+"px"; 
	  document.body.appendChild(div);
	}).mouseout(function(){ 
	  $(div).remove(); 
	}); 
};