package com.wgllss.ssmusic.features_system.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusicBean(musicTabeBean: MusicTabeBean)

    //ASC 默认值，从小到大，升序排列 DESC 从大到小，降序排列
    @Query("SELECT * FROM music_tab ORDER BY createTime ASC")
    fun getList(): LiveData<MutableList<MusicTabeBean>>

    @Query("SELECT COUNT(*) FROM music_tab WHERE id = :uuID")
    fun queryByUUID(uuID: Long): Int
}