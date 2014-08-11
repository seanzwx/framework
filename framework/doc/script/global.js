function getQueryString(name)
{
    // 如果链接没有参数，或者链接中不存在我们要获取的参数，直接返回空
    if(location.href.indexOf("?")==-1 || location.href.indexOf(name+'=')==-1)
    {
        return '';
    }
 
    // 获取链接中参数部分
    var href = decodeURI(location);
    var queryString = href.substring(location.href.indexOf("?")+1);
 
    // 分离参数对 ?key=value&key2=value2
    var parameters = queryString.split("&");
 
    var pos, paraName, paraValue;
    for(var i=0; i<parameters.length; i++)
    {
        // 获取等号位置
        pos = parameters[i].indexOf('=');
        if(pos == -1) { continue; }
 
        // 获取name 和 value
        paraName = parameters[i].substring(0, pos);
        paraValue = parameters[i].substring(pos + 1);
 
        // 如果查询的name等于当前name，就返回当前值，同时，将链接中的+号还原成空格
        if(paraName == name)
        {
            return unescape(paraValue.replace(/\+/g, " "));
        }
    }
    return '';
}

function getCurrentTime()
{
	var date = new Date();
	var tmp = (date.getYear() + 1900) + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
	return tmp;
}

function longToDate(time)
{
	var date = new Date();
	date.setFullYear(1970,1,1);
	date.setTime(0);
	date.setMilliseconds(time);
	var strdate = (date.getYear() + 1900) + "-" + (date.getMonth() +1) + "-" + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
	return strdate;
}

function showTime()
{
	document.getElementById("time").innerHTML = getCurrentTime();
	setTimeout(function()
	{
		showTime();
	}, 1000);
}