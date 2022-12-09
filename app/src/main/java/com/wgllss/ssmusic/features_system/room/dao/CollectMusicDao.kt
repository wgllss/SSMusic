package com.wgllss.ssmusic.features_system.room.dao

//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.wgllss.ssmusic.features_system.room.table.CollectMusicTabeBean
//import com.wgllss.ssmusic.features_system.room.table.MusicTabeBean
//
//@Dao
//interface CollectMusicDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertMusicBean(musicTabeBean: CollectMusicTabeBean)
//
//    @Query("SELECT * FROM collect_music_tab")
//    fun getList(): LiveData<MutableList<CollectMusicTabeBean>>
//}