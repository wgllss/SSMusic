package com.wgllss.ssmusic.features_system.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_tab")
class MusicTabeBean(
    @PrimaryKey @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") val id: Long,
    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val title: String,
    @ColumnInfo(name = "author", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val author: String,
    @ColumnInfo(name = "url", typeAffinity = ColumnInfo.TEXT, defaultValue = "") var url: String,
    @ColumnInfo(name = "pic", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val pic: String,
    @ColumnInfo(name = "createTime", typeAffinity = ColumnInfo.INTEGER, defaultValue = "") val createTime: Long //加入播放列表时间
)