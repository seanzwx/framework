var host = 'localhost';
var port = 8080;
var appname = 'microtalk-web';

var v = jQuery.browser.version;
var isIE;
if (v == '9.0' || v == '8.0' || v == '7.0' || v == '6.0' || v == '5.0')
{
	isIE = true;
}

//ajax请求事件发送者不播放动画
function ajax(url, parameter, callback)
{
	$.ajax(
	{
		url : 'http://' + host + ":" + port + '/' + appname + '/' + url,
		cache : 'false',
		type : 'get',
		dataType : 'jsonp',
		jsonp : "callback",
		data : parameter,

		success : function(json)
		{
			ajaxBack(json, callback);
		}
	});
}

/**
 * ajax结果处理
 * @param data						ajax返回信息
 * @param alertMsg					回调函数
 */
function ajaxBack(jsonstr, callback)
{
	var jsonobj = eval('(' + jsonstr + ')');
	callback(jsonstr, jsonobj);
}