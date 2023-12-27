(function() {
    
    window.__jumpToSongPage = function () {
        document.body.style.display = "none";
        var hash, album_id, album_audio_id;
        var encode_album_audio_id = "";
        if (location.hash != "") {
            hash = getHashParams("hash");
            album_id = getHashParams("album_id");
            album_audio_id = getHashParams("album_audio_id");
            
            var tempHash = location.hash ? location.hash.replace('#', '') : ''
            if (
                tempHash.indexOf('hash') == -1 
                && (tempHash.length > 5 && tempHash.length < 15 && !(/^\d+$/.test(tempHash))) // 去除特殊场景的非加密mixsongid，避免误拿
            ) { // 新的加密mixsongid参数
                encode_album_audio_id = location.hash.replace("#", "")
            }
        } else if (dataFromSmarty && dataFromSmarty.length) { // 
            hash = dataFromSmarty[0].hash;
            album_id = dataFromSmarty[0].album_id;
            album_audio_id = dataFromSmarty[0].album_audio_id;
            encode_album_audio_id = dataFromSmarty[0].encode_album_audio_id;
        } else if ($.jStorage?.version) {
            var data = JSON.parse($.jStorage.get("k_play_list")) || [];
            if (data.length != 0) {
                hash = data[0].Hash;
                album_id = data[0].album_id;
                album_audio_id = data[0].album_audio_id;
                encode_album_audio_id = data[0].encode_album_audio_id;
            }
        }
        if (hash || album_id || album_audio_id || encode_album_audio_id) {
            window.__isJump2MobilePageNow = 1
            
            window.getBaseInfo(1014, function(bInfo) {
               
                var beSignInfo = {
                    appid: 1014,
                    dfid: bInfo.dfid,
                    mid: bInfo.mid,
                    uuid: bInfo.mid,
                    token: bInfo.token ? bInfo.token : '',
                    userid: KgUser.Cookie.read("KuGoo", "KugooID") || '0',
                }
                if (hash) {
                    beSignInfo.hash = hash
                }
                if (album_id) {
                    beSignInfo.album_id = album_id
                }
                if (album_audio_id) {
                    beSignInfo.album_audio_id = album_audio_id
                }
                if (encode_album_audio_id) {
                    beSignInfo.encode_album_audio_id = encode_album_audio_id
                }

                window.infSign(beSignInfo, null, {
                    useH5: true,
                    postType: 'json',
                    // log: true,
                    callback: function (spa) {
                        $.ajax({
                            type: "GET",
                            url: 'https://wwwapi.kugou.com/play/songinfo',
                            timeout: 5000,
                            data: spa,
                            "success": function (res, status, xhr) {
                                if (res.status == 1) {
                                    location.href = "https://m.kugou.com/mixsong/" + res.data.encode_album_audio_id + ".html"
                                } else {
                                    document.body.style.display = "block";
                                }
                            },
                            "fail": function (err) {
                                document.body.style.display = "block";
                            }
                    })
                    }
                })
            })
        } else {
            document.body.style.display = "block";
        }
    }

    if (!IsPC()) {
        var searchPageReg = /www.kugou.com\/yy\/html\/search.html/i;  //搜索页
        var specialHomePageReg = /www.kugou.com\/yy\/html\/special.html/i; //歌单主页
        var specialPageReg = /www.kugou.com\/yy\/special\/single\/[0-9]*.html/i;   //歌单页
        var rankHomePage = /www.kugou.com\/yy\/html\/rank.html/i;   //排行榜主页
        var rankPage = /www.kugou.com\/yy\/rank\/home\//i;  //排行榜页
        var songPageReg = /www.kugou.com\/song\//i;   //歌曲播放页
        var singerHomePageReg = /www.kugou.com\/yy\/html\/singer.html/i;   //歌手主页
        var singerListPageReg = /www.kugou.com\/yy\/singer\/index\//i; //歌手列表页
        var singerPageReg = /www.kugou.com\/yy\/singer\/home\//i; //歌手页
        if (location.href.match(searchPageReg)) {
            jumpToSearchPage();
            return;
        } else if (location.href.match(specialHomePageReg)) {
            jumpToSpecialHomePage();
            return;
        } else if (location.href.match(specialPageReg)) {
            jumpToSpecialPage();
            return;
        } else if (location.href.match(rankHomePage)) {
            jumpToRankHomePage();
            return;
        } else if (location.href.match(rankPage)) {
            jumpToRankPage();
            return;
        } else if (location.href.match(songPageReg) || location.href.match(/www.kugou.com\/share\//i)) {
            window.__jumpToSongPage();
            return;
        } else if (location.href.match(singerHomePageReg)) {
            jumpToSingerHomePage();
            return;
        } else if (location.href.match(singerListPageReg)) {
            jumpToSingerListPage();
            return;
        } else if (location.href.match(singerPageReg)) {
            jumpToSingerPage();
            return;
        }
    }

    function jumpToSearchPage() {
        location.href = "https://m3ws.kugou.com/search/index";
    }
    function jumpToSpecialHomePage() {
        location.href = "https://m3ws.kugou.com/plist/index";
    }
    function jumpToSpecialPage() {
        var locationArr = location.href.split("/");
        var htmlStr = locationArr[locationArr.length-1].split(".")[0];
        location.href = "https://m3ws.kugou.com/plist/list/" + htmlStr;
    }
    function jumpToRankHomePage() {
        location.href = "https://m3ws.kugou.com/rank/list";
    }
    function jumpToRankPage() {
        var locationArr = location.href.split("?")[0].split("/");
        var htmlStr = locationArr[locationArr.length-1].split(".")[0].split("-")[1];
        location.href = "https://m3ws.kugou.com/rank/info/" + htmlStr;
    }
    function jumpToSingerHomePage() {
        location.href = "https://m3ws.kugou.com/singer/class";
    }
    function jumpToSingerListPage() {
        var locationArr = location.href.split("/");
        var htmlArr = locationArr[locationArr.length-1].split("-");
        var htmlStr = parseInt(htmlArr[htmlArr.length -1]) - 1;
        location.href = "https://m3ws.kugou.com/singer/list/" + htmlStr;
    }
    function jumpToSingerPage() {
        var locationArr = location.href.split("/");
        var htmlStr = locationArr[locationArr.length-1].split(".")[0];
        location.href = "https://m3ws.kugou.com/singer/info/" + htmlStr;
    }
})();

function IsPC() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            flag = false;
            break;
        }
    }
    return flag;
}

function getHashParams(key) {
    var arr = location.hash.replace("#", "").split("&"), keyValue = "";
    for (var i = 0;i<arr.length;i++) {
        if (arr[i].split("=")[0] == key) {
            keyValue = arr[i].split("=")[1];
            break;
        }
    }
    return keyValue;
}

function hex_md5(a){return rstr2hex(rstr_md5(str2rstr_utf8(a)))}function b64_md5(a){return rstr2b64(rstr_md5(str2rstr_utf8(a)))}function any_md5(a,b){return rstr2any(rstr_md5(str2rstr_utf8(a)),b)}function hex_hmac_md5(a,b){return rstr2hex(rstr_hmac_md5(str2rstr_utf8(a),str2rstr_utf8(b)))}function b64_hmac_md5(a,b){return rstr2b64(rstr_hmac_md5(str2rstr_utf8(a),str2rstr_utf8(b)))}function any_hmac_md5(a,b,c){return rstr2any(rstr_hmac_md5(str2rstr_utf8(a),str2rstr_utf8(b)),c)}function md5_vm_test(){return"900150983cd24fb0d6963f7d28e17f72"==hex_md5("abc").toLowerCase()}function rstr_md5(a){return binl2rstr(binl_md5(rstr2binl(a),8*a.length))}function rstr_hmac_md5(a,b){var d,e,f,g,c=rstr2binl(a);for(c.length>16&&(c=binl_md5(c,8*a.length)),d=Array(16),e=Array(16),f=0;16>f;f++)d[f]=909522486^c[f],e[f]=1549556828^c[f];return g=binl_md5(d.concat(rstr2binl(b)),512+8*b.length),binl2rstr(binl_md5(e.concat(g),640))}function rstr2hex(a){var c,d,e,f;try{}catch(b){hexcase=0}for(c=hexcase?"0123456789ABCDEF":"0123456789abcdef",d="",f=0;f<a.length;f++)e=a.charCodeAt(f),d+=c.charAt(15&e>>>4)+c.charAt(15&e);return d}function rstr2b64(a){var c,d,e,f,g,h;try{}catch(b){b64pad=""}for(c="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",d="",e=a.length,f=0;e>f;f+=3)for(g=a.charCodeAt(f)<<16|(e>f+1?a.charCodeAt(f+1)<<8:0)|(e>f+2?a.charCodeAt(f+2):0),h=0;4>h;h++)d+=8*f+6*h>8*a.length?b64pad:c.charAt(63&g>>>6*(3-h));return d}function rstr2any(a,b){var d,e,f,g,h,j,k,l,c=b.length,i=Array(Math.ceil(a.length/2));for(d=0;d<i.length;d++)i[d]=a.charCodeAt(2*d)<<8|a.charCodeAt(2*d+1);for(j=Math.ceil(8*a.length/(Math.log(b.length)/Math.log(2))),k=Array(j),e=0;j>e;e++){for(h=Array(),g=0,d=0;d<i.length;d++)g=(g<<16)+i[d],f=Math.floor(g/c),g-=f*c,(h.length>0||f>0)&&(h[h.length]=f);k[e]=g,i=h}for(l="",d=k.length-1;d>=0;d--)l+=b.charAt(k[d]);return l}function str2rstr_utf8(a){for(var d,e,b="",c=-1;++c<a.length;)d=a.charCodeAt(c),e=c+1<a.length?a.charCodeAt(c+1):0,d>=55296&&56319>=d&&e>=56320&&57343>=e&&(d=65536+((1023&d)<<10)+(1023&e),c++),127>=d?b+=String.fromCharCode(d):2047>=d?b+=String.fromCharCode(192|31&d>>>6,128|63&d):65535>=d?b+=String.fromCharCode(224|15&d>>>12,128|63&d>>>6,128|63&d):2097151>=d&&(b+=String.fromCharCode(240|7&d>>>18,128|63&d>>>12,128|63&d>>>6,128|63&d));return b}function str2rstr_utf16le(a){var c,b="";for(c=0;c<a.length;c++)b+=String.fromCharCode(255&a.charCodeAt(c),255&a.charCodeAt(c)>>>8);return b}function str2rstr_utf16be(a){var c,b="";for(c=0;c<a.length;c++)b+=String.fromCharCode(255&a.charCodeAt(c)>>>8,255&a.charCodeAt(c));return b}function rstr2binl(a){var c,b=Array(a.length>>2);for(c=0;c<b.length;c++)b[c]=0;for(c=0;c<8*a.length;c+=8)b[c>>5]|=(255&a.charCodeAt(c/8))<<c%32;return b}function binl2rstr(a){var c,b="";for(c=0;c<32*a.length;c+=8)b+=String.fromCharCode(255&a[c>>5]>>>c%32);return b}function binl_md5(a,b){var c,d,e,f,g,h,i,j,k;for(a[b>>5]|=128<<b%32,a[(b+64>>>9<<4)+14]=b,c=1732584193,d=-271733879,e=-1732584194,f=271733878,g=0;g<a.length;g+=16)h=c,i=d,j=e,k=f,c=md5_ff(c,d,e,f,a[g+0],7,-680876936),f=md5_ff(f,c,d,e,a[g+1],12,-389564586),e=md5_ff(e,f,c,d,a[g+2],17,606105819),d=md5_ff(d,e,f,c,a[g+3],22,-1044525330),c=md5_ff(c,d,e,f,a[g+4],7,-176418897),f=md5_ff(f,c,d,e,a[g+5],12,1200080426),e=md5_ff(e,f,c,d,a[g+6],17,-1473231341),d=md5_ff(d,e,f,c,a[g+7],22,-45705983),c=md5_ff(c,d,e,f,a[g+8],7,1770035416),f=md5_ff(f,c,d,e,a[g+9],12,-1958414417),e=md5_ff(e,f,c,d,a[g+10],17,-42063),d=md5_ff(d,e,f,c,a[g+11],22,-1990404162),c=md5_ff(c,d,e,f,a[g+12],7,1804603682),f=md5_ff(f,c,d,e,a[g+13],12,-40341101),e=md5_ff(e,f,c,d,a[g+14],17,-1502002290),d=md5_ff(d,e,f,c,a[g+15],22,1236535329),c=md5_gg(c,d,e,f,a[g+1],5,-165796510),f=md5_gg(f,c,d,e,a[g+6],9,-1069501632),e=md5_gg(e,f,c,d,a[g+11],14,643717713),d=md5_gg(d,e,f,c,a[g+0],20,-373897302),c=md5_gg(c,d,e,f,a[g+5],5,-701558691),f=md5_gg(f,c,d,e,a[g+10],9,38016083),e=md5_gg(e,f,c,d,a[g+15],14,-660478335),d=md5_gg(d,e,f,c,a[g+4],20,-405537848),c=md5_gg(c,d,e,f,a[g+9],5,568446438),f=md5_gg(f,c,d,e,a[g+14],9,-1019803690),e=md5_gg(e,f,c,d,a[g+3],14,-187363961),d=md5_gg(d,e,f,c,a[g+8],20,1163531501),c=md5_gg(c,d,e,f,a[g+13],5,-1444681467),f=md5_gg(f,c,d,e,a[g+2],9,-51403784),e=md5_gg(e,f,c,d,a[g+7],14,1735328473),d=md5_gg(d,e,f,c,a[g+12],20,-1926607734),c=md5_hh(c,d,e,f,a[g+5],4,-378558),f=md5_hh(f,c,d,e,a[g+8],11,-2022574463),e=md5_hh(e,f,c,d,a[g+11],16,1839030562),d=md5_hh(d,e,f,c,a[g+14],23,-35309556),c=md5_hh(c,d,e,f,a[g+1],4,-1530992060),f=md5_hh(f,c,d,e,a[g+4],11,1272893353),e=md5_hh(e,f,c,d,a[g+7],16,-155497632),d=md5_hh(d,e,f,c,a[g+10],23,-1094730640),c=md5_hh(c,d,e,f,a[g+13],4,681279174),f=md5_hh(f,c,d,e,a[g+0],11,-358537222),e=md5_hh(e,f,c,d,a[g+3],16,-722521979),d=md5_hh(d,e,f,c,a[g+6],23,76029189),c=md5_hh(c,d,e,f,a[g+9],4,-640364487),f=md5_hh(f,c,d,e,a[g+12],11,-421815835),e=md5_hh(e,f,c,d,a[g+15],16,530742520),d=md5_hh(d,e,f,c,a[g+2],23,-995338651),c=md5_ii(c,d,e,f,a[g+0],6,-198630844),f=md5_ii(f,c,d,e,a[g+7],10,1126891415),e=md5_ii(e,f,c,d,a[g+14],15,-1416354905),d=md5_ii(d,e,f,c,a[g+5],21,-57434055),c=md5_ii(c,d,e,f,a[g+12],6,1700485571),f=md5_ii(f,c,d,e,a[g+3],10,-1894986606),e=md5_ii(e,f,c,d,a[g+10],15,-1051523),d=md5_ii(d,e,f,c,a[g+1],21,-2054922799),c=md5_ii(c,d,e,f,a[g+8],6,1873313359),f=md5_ii(f,c,d,e,a[g+15],10,-30611744),e=md5_ii(e,f,c,d,a[g+6],15,-1560198380),d=md5_ii(d,e,f,c,a[g+13],21,1309151649),c=md5_ii(c,d,e,f,a[g+4],6,-145523070),f=md5_ii(f,c,d,e,a[g+11],10,-1120210379),e=md5_ii(e,f,c,d,a[g+2],15,718787259),d=md5_ii(d,e,f,c,a[g+9],21,-343485551),c=safe_add(c,h),d=safe_add(d,i),e=safe_add(e,j),f=safe_add(f,k);return Array(c,d,e,f)}function md5_cmn(a,b,c,d,e,f){return safe_add(bit_rol(safe_add(safe_add(b,a),safe_add(d,f)),e),c)}function md5_ff(a,b,c,d,e,f,g){return md5_cmn(b&c|~b&d,a,b,e,f,g)}function md5_gg(a,b,c,d,e,f,g){return md5_cmn(b&d|c&~d,a,b,e,f,g)}function md5_hh(a,b,c,d,e,f,g){return md5_cmn(b^c^d,a,b,e,f,g)}function md5_ii(a,b,c,d,e,f,g){return md5_cmn(c^(b|~d),a,b,e,f,g)}function safe_add(a,b){var c=(65535&a)+(65535&b),d=(a>>16)+(b>>16)+(c>>16);return d<<16|65535&c}function bit_rol(a,b){return a<<b|a>>>32-b}var hexcase=0,b64pad="";