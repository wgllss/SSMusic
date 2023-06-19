/**
 * Kugou 手机内嵌页通信接口
 * 异步回调方式
 *
 * @Module KgMobileCall
 *
*/
var KgMobileCall = {
	/*库版本*/
	ver : '3.0',
	/**
	* 是否调试模式
	* 0 : 关闭
	* 1 ：console
	* 2 : alert
	*/
	debug : 0,
	/*是否在IOS内嵌页*/
	isIOS : navigator.userAgent.match(/KGBrowser/gi) ? true : false,
	/*是否安卓新通讯模式*/
	isKugouAndroid: navigator.userAgent.match(/kugouandroid/gi) ? true : false, 
	/*是否安卓旧通讯模式*/
	isAndroid : (typeof external == 'undefined') ? false : ((typeof external.superCall == 'undefined') ? false : true),
	chkAndroidApp : function(){
		try{
			if(external.superCall(122)){
				return true;
			}
		}catch(ex){
			return false;
		}
	},
	cbNum : 0,
	/*链接格式*/
	formatURL : {
		"browser":"",
		"url":""
	},
	/*歌曲格式数据*/
	formatSong : {
		"filename": "",
		"filesize": "",
		"hash": "",
		"bitrate": "",
		"extname": "",
		"duration": "",
		"mvhash": "",
		"m4afilesize": "",
		"320hash": "",
		"320filesize": "",
		"sqhash": "",
		"sqfilesize": 0,
		"feetype": 0,
		"isfirst": 0
	},
	/*MV格式数据*/
	formatMV : {
		"filename": "",
		"singername": "",
		"hash": "",
		"imgurl": ""
	},
	/*分享数据*/
	formatShare : {
		"shareName" : "",
		"topicName" : "",
		"hash" : "",
		"listID" : "",
		"type" : "",
		"suid" : "",
		"slid" : "",
		"imgUrl" : "",
		"filename" : "",
		"duration" :"",
		"shareData" : {
			"linkUrl" : "",
			"picUrl" : "",
			"content" : "",
			"title" : ""
		}
	},
	/**
	 * 格式化数据,覆盖,1层递归
	* @param { Object } osrc，格式数据
	* @param { Object } src， 数据
	*/
	_extend : function(osrc, src){
		var me = this;

		if(src){
			for (var property in osrc) {
				if(!src.hasOwnProperty(property)){
					src[property] = osrc[property];
				}
			}
		}
		return src;
	},
	/**
	* @method str2Json 
	*
	* @param { String } jsonStr
	* @return { Object } json
	*/
	str2Json : function(jsonStr){
		var json = {};
		if(Object.prototype.toString.call(jsonStr) === "[object String]"){
			try {
				json = JSON.parse(jsonStr);
			} catch (ex) {
				json = {};
			}
		}
		return json;
	},
	json2Str : function(json){
		var jsonStr = "";
		if(typeof json !== "string"){
			try {
				jsonStr = JSON.stringify(json);
			} catch (e) {
				jsonStr = "";
			}
		}
		return jsonStr;
	},
	loadUrl: function(url) {
		var iFrame;

	    iFrame = document.createElement("iframe");
	    iFrame.setAttribute("src", url);
	    iFrame.setAttribute("style", "display:none;");
	    iFrame.setAttribute("height", "0px");
	    iFrame.setAttribute("width", "0px");
	    iFrame.setAttribute("frameborder", "0");
	    document.body.appendChild(iFrame);
	    iFrame.parentNode.removeChild(iFrame);
	    iFrame = null;
	},
	/**
     * 执行不返回数据的调用
     *
     * @method callCmd
     * @function
     * @param { Object } {cmd:Number(命令号),jsonStr:String(参数),callback:Function(回调函数)}
     * @return void(0)
     */
	callCmd: function(o) {
		var me = this,
        iosApi = "",
        androidApi = "",
        res,
        androidObj = {},
        androidString = '',
        dc = document;

		//debug
		if(dc.getElementById('mod_debugwin_set')){
			dc.getElementById('mod_debugwin_set').innerHTML = JSON.stringify(o).replace(/\\/g,'');
		}
		if(me.debug > 0){
			switch(me.debug) {
				case 1 :
					if(window.console){
						console.log(o);
					}
					break;
				case 2 :
					alert(JSON.stringify(o).replace(/\\/g,''));
					break; 
				default:
					break;
			}
		}



		if(me.isKugouAndroid){
            androidObj.cmd = o.cmd;
            if(o.callback){
                me.cbNum++;
                cbName = "kgandroidmobilecall" + me.cbNum + Math.random().toString().substr(2,9);
                /*创建随机函数*/
                window[cbName] = function(res, encodeParam){
                    if(typeof res != 'undefined'){
                        if(o.callback){
                            if(Object.prototype.toString.call(res) === '[object String]'){
                                if (encodeParam === '#') {
                                    res = decodeURIComponent(res);
                                } else {
                                    res = decodeURIComponent(decodeURIComponent(res));
                                }
                                o.callback(me.str2Json(res));
                            }else{
                                o.callback(res);
                            }
                        }
                    }
                };

                if (o.AndroidCallback === true) {
                    var json = me.str2Json(o.jsonStr);
                    json.AndroidCallback = cbName;
                    o.jsonStr = me.json2Str(json);
                    if(o.jsonStr){
                        androidObj.jsonStr = o.jsonStr;
                        //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +',"jsonStr":'+ o.jsonStr +'}';
                    }else{
                        //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +'}';
                    }                    
                } else {
                    if(o.jsonStr){
                        androidObj.jsonStr = o.jsonStr;
                        androidObj.callback = cbName;
                        
                        //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +',"jsonStr":'+ o.jsonStr +',"callback":"'+ cbName +'"}';
                    }else{
                        androidObj.callback = cbName;
                        //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +',"callback":"'+ cbName +'"}';
                    }
                }
            }else{
                if(o.jsonStr){
                    androidObj.jsonStr = o.jsonStr;
                    //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +',"jsonStr":'+ o.jsonStr +'}';
                }else{
                    //androidApi = 'kugoujsbridge://start.kugou_jsbridge/?{"cmd":'+ o.cmd +'}';
                }
            }
            androidString = JSON.stringify(androidObj);
            androidApi = 'kugoujsbridge://start.kugou_jsbridge/?' + encodeURIComponent(androidString);
            me.loadUrl(androidApi);


        } else if(me.isAndroid){
			try{

				if(o.jsonStr){
					
					if(o.callback && o.callback !== '' && o.AndroidCallback === true){
						me.cbNum++;
						cbName = "kgmobilecall" + me.cbNum + Math.random().toString().substr(2,9);
						/*创建随机函数*/
						window[cbName] = function(r){
							if(typeof res != 'undefined'){
								if(o.callback){
									if(Object.prototype.toString.call(r) === '[object String]'){
										o.callback(me.str2Json(r));
									}else{
										o.callback(r);
									}
								}
							}
							//delete window[cbName];
						};
						var json = me.str2Json(o.jsonStr);
						json.AndroidCallback = cbName;
						o.jsonStr = me.json2Str(json);
						
					}
					res = external.superCall(o.cmd, o.jsonStr);
				}else{
					res = external.superCall(o.cmd);
				}
				if(o.callback && o.callback !== '' && res!="AndroidCallback"){
					res = me.str2Json(res);
					o.callback(res);
				}
			}catch(ex){}
		} else if(me.isIOS){
			if(o.callback){
				me.cbNum++;
				cbName = "kgmobilecall" + me.cbNum + Math.random().toString().substr(2,9);
				/*创建随机函数*/
				window[cbName] = function(res){
					if(typeof res != 'undefined'){
						if(o.callback){
							if(Object.prototype.toString.call(res) === '[object String]'){
								o.callback(me.str2Json(res));
							}else{
								o.callback(res);
							}
						}
					}
					//delete window[cbName];
				};

				if(o.jsonStr){
					iosApi = 'kugouurl://start.music/?{"cmd":'+ o.cmd +', "jsonStr":'+ o.jsonStr +', "callback":"'+ cbName +'"}';
				}else{
					iosApi = 'kugouurl://start.music/?{"cmd":'+ o.cmd +', "callback":"'+ cbName +'"}';
				}
			}else{
				if(o.jsonStr){
					iosApi = 'kugouurl://start.music/?{"cmd":'+ o.cmd +', "jsonStr":'+ o.jsonStr +'}';
				}else{
					iosApi = 'kugouurl://start.music/?{"cmd":'+ o.cmd +'}';
				}
			}
			//location.href = iosApi;
			me.loadUrl(iosApi);
		}else {
			if(o.callback){
				o.callback({"status": 0, "error":"不在客户端内"});
			}
		}
	},
	/**
     * 获取登录用户信息
     *
     * @method getUserInfo
     * @function
	 * @param { String } 回调函数名
     */
	getUserInfo: function(callback) {
		var me = this;

		if(callback){
			me.callCmd({cmd:101, callback:callback});
		} else {
			me.callCmd({cmd:101});
		}
	},
	/**
     * 弹出登录窗口
     *
     * @method callSoftUserLogin
	 *
	 * @param { Object } json参数
	 */
	callSoftUserLogin: function(json, callback) {
		var me = this,
		jsonStr = "";

		if(json){

			if(!json.loginType){
				json.loginType = '';
			}

			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd: 102, jsonStr: jsonStr, callback: callback});
			}else{
				me.callCmd({cmd: 102, jsonStr: jsonStr});
			}
		}else{
			if(callback){
				me.callCmd({cmd: 102, callback: callback});
			}else{
				me.callCmd({cmd: 102});
			}
		}
	},
	/**
     * 弹出注册窗口
     *
     * @method showRegisterBox
     * @function
     */
	showRegisterBox: function(json, callback) {
		var me = this,
		jsonStr = "";

		if(json){
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd: 103, jsonStr: jsonStr, callback: callback});
			}else{
				me.callCmd({cmd: 103, jsonStr: jsonStr});
			}
		}else{
			if(callback){
				me.callCmd({cmd: 103, callback: callback});
			}else{
				me.callCmd({cmd: 103});
			}
		}
	},
	/**
     * 退出登录
     *
     * @method logout
     */
	logout: function(callback) {
		var me = this;
		if(callback){
			me.callCmd({cmd:104, callback: callback});
		}else{
			me.callCmd({cmd:104});
		}
	},
    /**
     * 下载歌曲不播放
     *
     * @method downNoPlay
	 * @param { Object } JSON参数
	 */
    downNoPlay : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:107, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:107, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 歌曲试听不下载(添加到默认列表)
     *
     * @method listen
	 * @param { Object } JSON参数
	 */
    listen : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			/*歌曲数*/
			if(!json.total){
				json.total = json.info.length;
			}
			/*列表名*/
			if(!json.className){
				json.className = '';
			}
			/*播放序列*/
			if(!json.playIndex){
				json.playIndex = 0;
			}
			/*打开播放页*/
			if(!json.isJump){
				json.isJump = 0;
			}
			/*音效*/
			//if(!json.effect){
			//	json.effect = 0;
			//}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:108, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:108, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 创建新列表
     *
     * @method createList
     * @function
     * @param { String } JSON string
     * @return
     */
    createList : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:109, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:109, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 歌曲添加不下载
     *
     * @method addNoDown
	 * @param { Object } JSON参数
	 */
    addNoDown : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:110, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:110, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 添加到当前歌曲下一位置稍后播放（插播）
     *
     * @method spots
	 * @param { Object } JSON
	 */
    spots : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:111, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:111, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 创建歌单列表并播放歌曲并下载
     *
     * @method createListDownAndPlaySong
     * @param { Object } JSON参数
     */
    createListDownAndPlaySong : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";
		
		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:112, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:112, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 创建新列表并下载歌曲不播放
     *
     * @method createListDownNoPlaySong
	 * @param { Object } JSON参数
	 */
    createListDownNoPlaySong : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}
			
			//保存回json
			json.info = info;

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:113, jsonStr:jsonStr});
			}else{
				me.callCmd({cmd:113, jsonStr:jsonStr, callback: callback});
			}
		}
    },
    /**
     * 创建专辑收藏并添加歌曲
     *
     * @method createCollectListAddSong
	 * @param { Object } JSON参数
	 */
    createCollectListAddSong : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";
		
		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			if(!json.total){
				json.total = json.info.length;
			}
			if(!json.className){
				json.className = '';
			}

			//转成字符串JSON
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:114, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:114, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 分享歌曲/专辑/歌单
     *
     * @method share
     * @function
     * @param { Object } JSON参数
     * @param { Function } iosCallback
	 * @param { Function } weixinCallback 如果此参数为空时，在非客户端非微信的情况下就会调用新浪分享，不为空时就会调用此callback。。。 
	 * @param { Function } otherCallback 其他回调
     * @return
     */
    share : function(json, iosCallback, weixinCallback, otherCallback){
		var me = this,
		jsonStr = "",
		info = {},
		ua = navigator.userAgent,
		_wbShare = function(){
			var option = {
				title: encodeURIComponent(json.shareData.title) || "" ,
				url: json.shareData.linkUrl || "",
				content: encodeURIComponent(json.shareData.content) || "",
				imgSrc: encodeURI(json.shareData.picUrl) || "",
				swf:"",
				appkey:"340086183",
				/*(新浪微博) 用参数,关联用户的UID，分享微博会@该用户(可选)*/
				ralateUid:""
			},
			winSrc = "",
			queryStr = "";

			winSrc = "http://service.t.sina.com.cn/share/share.php";
			queryStr = [
				"url=" + option.url,
				"appkey=" + option.appkey,
				"title=" + option.content,
				"pic=" + option.imgSrc,
				"ralateUid=" + option.ralateUid 
			].join("&");

			location.href = winSrc + "?" + queryStr;
		};

		if(json){
			info = me._extend(me.formatShare, json);
			json = info;
		}

		//TODO
		if(me.isIOS){
			if(iosCallback){
				me.callCmd({cmd : 115, jsonStr : me.json2Str(json), callback : iosCallback});
			}else{
				me.callCmd({cmd : 115, jsonStr : me.json2Str(json)});
			}
		}else if(me.isAndroid || me.isKugouAndroid){
			me.getVersion(function(res){
				if(res && json && json.type == 3 && res.platform == "android" && Number(res.version) < 6340){
					_wbShare();
				}else{
					me.callCmd({cmd : 115, jsonStr : me.json2Str(json)});
				}
			});
		}else if(ua.match('MicroMessenger')){
			if(weixinCallback){
				weixinCallback();
			}else{
				_wbShare();
			}
		}else{
			if(otherCallback){
				otherCallback();
			}else{
				_wbShare();
			}
		}
    },
    /**
     * 调用客户端搜索
     *
     * @method openSearch
     * @function
     * @param jsonStr
     * @return
     */
    openSearch : function(json, callback){
		var me = this,
		jsonStr = "";
        if(json){
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:116,jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:116,jsonStr:jsonStr});
			}
        }else{
            me.callCmd({cmd:116});
        }
    },
    /**
     * 播放MV
     *
     * @method playMV
     * @function
     * @param json
     * @return
     */
    playMV : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatMV, data[i]));
				}
			}

			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:117,jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:117,jsonStr:jsonStr});
			}
		}
    },
    /**
     * 播放电台
     *
     * @method playRadio
     * @function
     * @return
     */
    playRadio : function(json, callback){
		var me = this,
		jsonStr = "";
		
		if(json){
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:119,jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:119,jsonStr:jsonStr});
			}
		}
    },
    /**
     * 获取当前正在播放的电台信息
     *
     * @method getRadioStatus
     * @function
     * @return
     */
    getRadioStatus : function(callback){
		var me = this;
		if(callback){
			callCmd({cmd:121, callback: callback});
		}else{
			callCmd({cmd:121});
		}
    },
    /**
     * 获取当前酷狗客户端版本号
     *
     * @method getVersion
     
	 * @param { String } callback
	 */
    
    getVersion: function(callback) {
		this.callCmd({cmd:122, callback:callback});
    },
    /**
     * 调用系统浏览器打开链接
     *
     * @method openURL
     * @function
     * @return
     */
    openURL : function(json, callback){
		var me = this,
		jsonStr = "";

		if(json){
			json = me._extend(me.formatURL, json);

			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:123, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:123, jsonStr:jsonStr});
			}
		}else{
			me.callCmd({cmd:123});
		}
    },
    /**
     * 获取当前手机信息
     *
     * @method getMobileInfo
     * @function
     * @return
     */
    getMobileInfo : function(callback){
        this.callCmd({cmd:124, callback:callback});
    },
    /**
     * 通知客户端暂停/续播歌曲
     *
     * @method playOrPause
     * @function
     * @return
     */
    playOrPause : function(json, callback){
		var me = this,
		jsonStr = "";

		if(!json){
			json = {
				type : 1
			};
		}

		if(json){
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:125, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:125, jsonStr:jsonStr});
			}
		}
    },
    /**
     * 打开客户端相关页面
     * tab : "main" //main : 主页,musicLib : 乐库主页,singer : 歌手页,local : 附近,more : 更多,app : 应用推荐
     *
     * @method openTab
     * @function
     * @param json
     * @return
     */
    openTab : function(json, callback){
		var me = this,
		jsonStr = "";
		if(json){
			jsonStr = me.json2Str(json);
			if(callback){
				me.callCmd({cmd:126, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:126, jsonStr:jsonStr});
			}
		}else{
			if(callback){
				me.callCmd({cmd:126, callback: callback});
			}else{
				me.callCmd({cmd:126});
			}
		}
    },
    clearListAndAddSongs : function(json, callback){
		var me = this,
		data = [],
		info = [],
		jsonStr = "";

		if(json){
			data = json.info;

			if(Object.prototype.toString.call(data) === '[object Array]'){
				for (var i = 0, len = data.length; i < len; i++) {
					info.push(me._extend(me.formatSong, data[i]));
				}
			}

			//保存回json
			json.info = info;

			//转成字符串JSON
			jsonStr = me.json2Str(json);

			if(callback){
				me.callCmd({cmd:127, jsonStr:jsonStr, callback: callback});
			}else{
				me.callCmd({cmd:127, jsonStr:jsonStr});
			}
        }else{
			if(callback){
				me.callCmd({cmd:127, callback: callback});
			}else{
				me.callCmd({cmd:127});
			}
        }
    },
	/**
	 * 显示隐藏Bar条
	 */
	showHideBar : function(json, callback){
		var me = this,
		jsonStr = '';

		if(!json){
			json = {
				state : 1
			};
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:128, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:128, jsonStr:jsonStr});
		}
	},
	/**
	 * 重力感应（设备移动的加速度）
	 *
	 */
	getAcceleration : function(callback){
		var me = this;

		if(callback){
			me.callCmd({cmd:129, callback: callback});
		}
	},
	/**
	 * 陀螺仪
	 *
	 */
	getOrientation : function(callback){
		var me = this;

		if(callback){
			me.callCmd({cmd:130, callback: callback});
		}
	},
    /**
     * 是否在客户端里面
     *
     * @method isClient
     * @function
     * @return
     */
    isInClient : function(){
		var me = this;
		if(me.isAndroid || me.isKugouAndroid){
			return true;
		}else if(me.isIOS){
			return me.isIOS;
		}else{
			return false;
		}
    },
	/**
	 * 更新客户端内嵌页标题
	 */
	updateTitle : function(json, callback){
		var me = this,
		jsonStr = '';

		if(!json){
			return;
		}

		if(!json.title){
			json.title = '';
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:602, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:602, jsonStr:jsonStr});
		}
	},
	/**
	 * 添加统计信息
	 */
	statistics: function(androidId, iosId, name, category, source, callback) {
		var me = this;
		var json;

		if (!androidId || !iosId) {
			return false;
		}
		json = {
			name: name,
			category: category,
			source: source || ""
		};

		if (me.isAndroid || me.isKugouAndroid) {
			json.id = androidId;
		} else if(me.isIOS) {
			json.id = iosId;
		} else {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:147, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:147, jsonStr:jsonStr});
		}
	},
	/**
	 * 添加歌曲到历史列表
	 */
	isFirstEnter: function(json, callback) {
		var me = this;

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:148, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:148, jsonStr:jsonStr});
		}
	},
	/**
	 * 显示或者隐藏内嵌页更多功能中的某些功能（790版本及以上）
	 */
	showHideFun: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:149, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:149, jsonStr:jsonStr});
		}
	},
	/**
	 * 获取签名(8.0版本及以上)
	 */
	getSign: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:150, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:150, jsonStr:jsonStr});
		}
	},
	/**
	 * 关掉客户端当前窗口
	 */
	closeFrame: function(json) {
		var me = this,
		jsonStr = '';

		if(!json){
			return;
		}

		jsonStr = me.json2Str(json);
		me.callCmd({cmd:158, jsonStr:jsonStr});
	},
		
	/**
	 * 单曲购买 - 获取签名
	 */
	getSigns: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:173, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:173, jsonStr:jsonStr});
		}
	},
	
	/**
	 * 添加apm统计
	 */
	apmStatistic: function(json, callback) {
		var me = this,
		jsonStr = '';

		if(!json){
			return;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:178, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:178, jsonStr:jsonStr});
		}
	},

	/**
	 * 跳转至个人中心页
	 */
	openPersonalPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if(!json){
			return;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:182, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:182, jsonStr:jsonStr});
		}
	},
	
	 /**
     * ack
     */
    getData: function(json, callback) {
        var me = this,
        jsonStr = '';

        if(!json){
            return;
        }

        jsonStr = me.json2Str(json);

        if(callback){
            me.callCmd({cmd:186, jsonStr:jsonStr, callback: callback, AndroidCallback: true});
        }else{
            me.callCmd({cmd:186, jsonStr:jsonStr});
        }
    },

	
	/**
	 * 公共内嵌页跳转个人账户（绑定手机 / 更改密码）页面
	 */
	pageJumpToUserCountSetPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:188, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:188, jsonStr:jsonStr});
		}
	},
	/**
	 * 获取当前Bar条正在播放的歌曲信息
	 *
	 */
	getBarSongStatus : function(callback){
		var me = this;

		if(callback){
			me.callCmd({cmd:189, callback: callback});
		}
	},

	/**
	 * 获取海外信息
	 */
	getOverseas: function(callback) {
		var me = this;

		if(callback){
			me.callCmd({cmd:191, callback:callback});
		} else {
			me.callCmd({cmd:191});
		}
	},

	/**
	 * 展示右上角按钮
     */
    showRightMenus: function(json, callback) {
        var me = this,
            jsonStr = '';

        if (!json) {

            return false;
        }
        jsonStr = me.json2Str(json);
        if(callback){
            me.callCmd({cmd:192, jsonStr:jsonStr, callback: callback});
        }else{
            me.callCmd({cmd:192, jsonStr:jsonStr});
        }
    },
    /**
     * 右滑改成靠左边退出
     */
    leftCanHand: function(json, callback) {
        var me = this,
            jsonStr = '';

        if (!json) {
            return false;
        }

        jsonStr = me.json2Str(json);

        if(callback){
            me.callCmd({cmd:193, jsonStr:jsonStr, callback: callback});
        }else{
            me.callCmd({cmd:193, jsonStr:jsonStr});
        }
    },
    /**
     * 直接打开外部浏览器
     */
    useOutBrowserOpenUrl: function(json, callback) {
        var me = this,
            jsonStr = '';

        if (!json) {
            return false;
        }

        jsonStr = me.json2Str(json);

        if(callback){
            me.callCmd({cmd:194, jsonStr:jsonStr, callback: callback});
        }else{
            me.callCmd({cmd:194, jsonStr:jsonStr});
        }
    },
    /**
     * 增加关闭功能
     */
    addCloseMenu: function(json, callback) {
        var me = this,
            jsonStr = '';

        if (!json) {
            return false;
        }

        jsonStr = me.json2Str(json);

        if(callback){
            me.callCmd({cmd:195, jsonStr:jsonStr, callback: callback});
        }else{
            me.callCmd({cmd:195, jsonStr:jsonStr});
        }
    },
    /**
     * 上报统计数据
     */
    reportStatisticsData: function(json, callback) {
	    var me = this,
	    jsonStr = '';

	    if(!json){
	        return;
	    }

	    jsonStr = me.json2Str(json);

	    if(callback){
	        me.callCmd({cmd:197, jsonStr:jsonStr, callback: callback});
	    }else{
	        me.callCmd({cmd:197, jsonStr:jsonStr});
	    }
	},
	/**
     * 打开音效页
     */
    openSoundEffect: function(json, callback) {
	    var me = this,
	    jsonStr = '';

	    if(!json){
	        return;
	    }

	    jsonStr = me.json2Str(json);

	    if(callback){
	        me.callCmd({cmd:213, jsonStr:jsonStr, callback: callback});
	    }else{
	        me.callCmd({cmd:213, jsonStr:jsonStr});
	    }
	},

	/**
     * replace页面
     */
    reportStatisticsData: function(json, callback) {
	    var me = this,
	    jsonStr = '';

	    if(!json){
	        return;
	    }

	    jsonStr = me.json2Str(json);

	    if(callback){
	        me.callCmd({cmd:217, jsonStr:jsonStr, callback: callback});
	    }else{
	        me.callCmd({cmd:217, jsonStr:jsonStr});
	    }
	},
	


	/*------- 乐库下 分类tab 使用命令号 (页面跳转用)------------*/
	/**
	 * 乐库下分类内嵌页跳转歌单页(单个歌单)
	 */
	categoryJumpSinglePlaylistPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:701, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:701, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转专辑页(单个专辑)
	 */
	categoryJumpSingleAlbumlistPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:702, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:702, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转歌手页(单个歌手)
	 */
	categoryJumpSingerPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:703, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:703, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转对应分类标签页
	 */
	categoryJumpCategoryPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:704, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:704, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳到对应MV歌单页
	 */
	categoryJumpMVlistPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:705, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:705, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转排行榜页
	 */
	categoryJumpRanklistPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:706, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:706, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转播放MV（跳转单个MV页面）
	 */
	categoryPlayMV: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:707, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:707, jsonStr:jsonStr});
		}
	},
	/**
	 * 乐库下分类内嵌页跳转播放电台
	 */
	categoryPlayFM: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:708, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:708, jsonStr:jsonStr});
		}
	},
	/**
	 * 分享按钮显示隐藏
	 */
	showHideRightTopShareBar: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:709, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:709, jsonStr:jsonStr});
		}
	},
	/**
	 * 保存新打开的内嵌页
	 */
	savePreReturnPage: function(json, callback) {
		var me = this,
		jsonStr = '';

		if (!json) {
			return false;
		}

		jsonStr = me.json2Str(json);

		if(callback){
			me.callCmd({cmd:710, jsonStr:jsonStr, callback: callback});
		}else{
			me.callCmd({cmd:710, jsonStr:jsonStr});
		}
	}


};

/**
 * 客户端调用网页端接口
 */
var KgWebMobileCall = {
	/*客户端用户状态改变时回调*/
	userStatus : function(cmdid, jsonStr){},
	/*用户离开切换内嵌页回调*/
	pageStatus : function(cmdid, jsonStr){},
	/*歌曲分享成功后，回调通知网页*/
	shareStatus : function(cmdid, jsonStr){},
	/*播放状态回调*/
	playStatus : function(jsonStr){}
};

