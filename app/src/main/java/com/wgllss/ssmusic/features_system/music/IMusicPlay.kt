package com.wgllss.ssmusic.features_system.music

//抽象播放接口，可用于多种播放器适配
interface IMusicPlay {

    /**
     * 初始化
     */
    fun onCreate()

    /**
     * 开始播放
     */
    fun start()

    /**
     * 暂停
     */
    fun onPause()

    /**
     * 恢复
     */
    fun onResume()

    //下一首
    fun playNext(nextUrl: String)

    //前一首
    fun playPrevious(previousUrl: String)

    //准备播放
    fun prePared()

    //设置播放地址
    fun setSource(url: String)

    //是否循环
    fun setPlayCircle(isCircle: Boolean)

    //设置音量
    fun setVolume(volume: Int)

//    //播放模式，0:按照顺序 1：单曲循环 2：随机播放
//    fun setPlayType(type: Int)

    //播放到制定位置
    fun seek(secds: Int, seekingfinished: Boolean, showTime: Boolean)

    fun isPlaying(): Boolean

    fun onDestroy()
}