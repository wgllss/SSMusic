/**
 * [utility 公用功能函数]
 * @type {Object}
 */
window.utility = {
    /**
     * [strToJson 字符串转json]
     * @param  {[type]} str [description]
     * @return {[type]}     [description]
     */
    strToJson:function(str){ 
        var json = eval('(' + str + ')'); 
        return json; 
    },
    /**
     * [read 获取cookies]
     * @param  {[string]} name [description]
     * @param  {[string]} key  [description]
     * @return {[type]}      [description]
     */
    read :function(name, key) {
        var cookieValue = "";
        var search = name + "=";
        if (document.cookie.split("").length > 0) {
            offset = document.cookie.indexOf(search);
            if (offset != -1) {
                offset += search.length;
                end = document.cookie.indexOf(";", offset);
                if (end == -1) {
                    end = document.cookie.split("").length;
                }
                cookieValue = document.cookie.substring(offset, end);
            }

        }
        var a = cookieValue.split("&");
        var o = {};
        var haveVal = false;
        for (var i = 0, l = a.length; i < l; i++) {
            var k = a[i].split("=");
            if(k[0]!=""){
                o[k[0]] = k[1];
                haveVal = true;
            }
        }
        return haveVal ? o : false;
    },
    /**
     * [trim 去除前后空格]
     * @param  {[string]} str [string]
     * @return {[string]}     [string]
     */
    trim:function (str){ 
        return str.replace(/(^\s*)|(\s*$)/g, ""); 
    }, 
    /**
     * [loadScript 加载脚本]
     * @param  {[type]} option [description]
     * @return {[type]}        [description]
     */
    loadScript:function (option) {
        var url = option.url,
            callback = option.callback;
        var script = document.createElement("script");
        script.type = 'text/javascript';
        if (script.readyState) {
            script.onreadystatechange = function() {
                if (this.readyState == "complete" || this.readyState == "loaded") {
                    callback && callback();
                    script.onreadystatechange = script = callback = null;
                }
            }
        } else {
            script.onload = function() {
                callback && callback();
                script.onload = script = callback = null;
            };
        }

        script.src = url;
        document.getElementsByTagName("head")[0].appendChild(script);
    },
    /**
     * [formatDateTime 格式化时间]
     * @param  {[type]} date [description]
     * @return {[type]}      [description]
     */
    formatDateTime:function (date) {
        var y = date.getFullYear();  
        var m = date.getMonth() + 1;  
        m = m < 10 ? ('0' + m) : m;  
        var d = date.getDate();  
        d = d < 10 ? ('0' + d) : d;  
        var h = date.getHours();  
        var minute = date.getMinutes();  
        minute = minute < 10 ? ('0' + minute) : minute;  
        return y + '-' + m + '-' + d+' '+h+':'+minute;  
    },
    /**
     * [detectOS 获取操作系统]
     * @return {[type]} [description]
     */
    detectOS:function () {
        var sUserAgent = navigator.userAgent;
        var isWin = (navigator.platform == "Win32") || (navigator.platform == "Windows") || (navigator.platform == "Win64");
        var isMac = (navigator.platform == "Mac68K") || (navigator.platform == "MacPPC") || (navigator.platform == "Macintosh") || (navigator.platform == "MacIntel");
        if (isMac) return "Mac";
        var isUnix = (navigator.platform == "X11") && !isWin && !isMac;
        if (isUnix) return "Unix";
        var isLinux = (String(navigator.platform).indexOf("Linux") > -1);
        if (isLinux) return "Linux";
        if (isWin) {
            return "Windows";
        }
        return "other";
    },
    getBrowser:function (){
        var Sys = {};
        var ua = navigator.userAgent.toLowerCase();
        var s;
        (s = ua.match(/rv:([\d.]+)\) like gecko/)) ?  Sys={"v":s[1],"type":"ie"}:
        (s = ua.match(/msie ([\d.]+)/)) ?  Sys={"v":s[1],"type":"ie"} :
        (s = ua.match(/firefox\/([\d.]+)/)) ?  Sys={"v":s[1],"type":"firefox"} :
        (s = ua.match(/chrome\/([\d.]+)/)) ?  Sys={"v":s[1],"type":"chrome"} :
        (s = ua.match(/opera.([\d.]+)/)) ?  Sys={"v":s[1],"type":"opera"} :
        (s = ua.match(/version\/([\d.]+).*safari/)) ?   Sys={"v":s[1],"type":"safari"} : 0;
        return Sys
    }
}
