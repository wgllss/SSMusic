package com.wgllss.ssmusic.features_system.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collect_music_tab")
class CollectMusicTabeBean(
    @PrimaryKey @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") val id: Long,
    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val title: String,
    @ColumnInfo(name = "author", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val author: String,
    @ColumnInfo(name = "url", typeAffinity = ColumnInfo.TEXT, defaultValue = "") var url: String,
    @ColumnInfo(name = "pic", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val pic: String,
)