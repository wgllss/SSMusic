/*
 * ============================================================================
 * 首页交互
 * ============================================================================
 */

/**
 * 页面初始化
 * @return {[type]} [description]
 */
(function(){
  // 初始化事件
  $(document).ready(function() {
    new Lazyload();

    share.resetShareData({
      title: '听歌,听小说,听相声,高清音质歌曲有声小说在线听-酷狗音乐',
      url: window.location.href,
      content: '酷狗音乐在线正版音乐网站，为您提供酷狗音乐播放器下载 、在线音乐试听下载，提供听书、有声小说、有声书、相声评书、儿童故事等在线听和MV播放服务。酷狗音乐，就是歌多！小说相声也很多！场景音乐也很多！',
      img: 'https://m.kugou.com/v3/static/images/img/kugou_app_icon.png',
    })
  
  })
})()