function getParameterByName(name) { 
	var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);  
	return match && decodeURIComponent(match[1].replace(/\+/g, ' '));  
} 

function appendUserName(calssname, username){
	$.each($("."+calssname+""), function(index, item){
		var raw_url = $(this).attr("href");
		$(this).attr("href",raw_url+"?user="+username);
	});
}