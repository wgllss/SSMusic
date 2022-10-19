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

    @Query("SELECT * FROM music_tab ORDER BY createTime DESC")
    fun getList(): LiveData<MutableList<MusicTabeBean>>

    @Query("SELECT COUNT(*) FROM music_tab WHERE id = :uuID")
    fun queryByUUID(uuID: Long): Int
}