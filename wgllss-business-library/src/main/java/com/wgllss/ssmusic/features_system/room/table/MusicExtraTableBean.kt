package com.wgllss.ssmusic.features_system.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_extra_tab")
class MusicExtraTableBean(
    @PrimaryKey
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") var id: Long,
    @ColumnInfo(name = "lrcStr", typeAffinity = ColumnInfo.TEXT, defaultValue = "") val lrcStr: String,
    @ColumnInfo(name = "paletteBgColor", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") val paletteBgColor: Int,
    @ColumnInfo(name = "paletteTitleTextColor", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") val paletteTitleTextColor: Int,
    @ColumnInfo(name = "paletteBodyTextColor", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0") val paletteBodyTextColor: Int
)