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

function initSelectMenu(menu_list, select_menu_id){
	$("#"+select_menu_id).empty();
		for(key in menu_list){
			var $opt = $("<option>").attr("value", (parseInt(key)+1).toString()).text(menu_list[key]); 
		$("#"+select_menu_id).append($opt);
	}
}

function getUrlParam(name)
{
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r!=null) return unescape(r[2]); return null; //返回参数值
}