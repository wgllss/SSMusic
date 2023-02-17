package com.wgllss.ssmusic.features_system.room.help

import androidx.room.migration.Migration
import javax.inject.Inject

class RoomDBMigration @Inject constructor() {

    fun createMigration(): Array<Migration> {
        val migrations = arrayListOf<Migration>()
//        val migration1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS collect (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , id INTEGER NOT NULL DEFAULT 0 , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 ,name TEXT NOT NULL DEFAULT '')")
//            }
//        }
//
//        val migration2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS cacheOrder (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 , cacheOrderTime INTEGER NOT NULL DEFAULT 0 , goodsSampleName TEXT NOT NULL DEFAULT ''" +
//                            ", total TEXT NOT NULL DEFAULT '0.00', productListJson TEXT NOT NULL DEFAULT '')"
//                )
//            }
//        }
//
//        val migration3_5 = object : Migration(3, 5) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS editPriceFailTable ( gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,id INTEGER NOT NULL DEFAULT 0 , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 , postBodyJson TEXT NOT NULL DEFAULT '')"
//                )
//            }
//        }
//
//        val migration5_6 = object : Migration(5, 6) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS offlineOrderBean (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , offlineOrderID INTEGER  PRIMARY KEY NOT NULL DEFAULT 0 ,shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 ,offlineCrateTime INTEGER NOT NULL DEFAULT 0 ,status INTEGER NOT NULL DEFAULT 0, orderJson TEXT NOT NULL DEFAULT '')"
//                )
//            }
//        }
//        val migration6_7 = object : Migration(6, 7) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS collect (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , id INTEGER NOT NULL DEFAULT 0 , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 ,name TEXT NOT NULL DEFAULT '')")
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS cacheOrder (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 , cacheOrderTime INTEGER NOT NULL DEFAULT 0 , goodsSampleName TEXT NOT NULL DEFAULT ''" +
//                            ", total TEXT NOT NULL DEFAULT '0.00', productListJson TEXT NOT NULL DEFAULT '')"
//                )
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS editPriceFailTable ( gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,id INTEGER NOT NULL DEFAULT 0 , shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 , postBodyJson TEXT NOT NULL DEFAULT '')"
//                )
//
//                database.execSQL(
//                    "CREATE TABLE IF NOT EXISTS offlineOrderBean (gID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , offlineOrderID INTEGER  PRIMARY KEY NOT NULL DEFAULT 0 ,shopId INTEGER NOT NULL DEFAULT 0, marketId INTEGER NOT NULL DEFAULT 0 ,offlineCrateTime INTEGER NOT NULL DEFAULT 0 ,status INTEGER NOT NULL DEFAULT 0, orderJson TEXT NOT NULL DEFAULT '')"
//                )
//            }
//        }

//        val migration2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
////                database.apply {
////                    execSQL("ALTER TABLE product ADD COLUMN sellPrice REAL  NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN costPrice REAL  NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN barCode TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN brand TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN shopId Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN marketId Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN supplierId Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN stdType Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat3Id Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat3Name TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat2Id Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat2Name TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat1Id Integer NOT NULL DEFAULT 0 ")
////                    execSQL("ALTER TABLE product ADD COLUMN cat1Name TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN img TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN letterFirsts TEXT NOT NULL DEFAULT '' ")
////                    execSQL("ALTER TABLE product ADD COLUMN needWeighing Integer NOT NULL DEFAULT 0 ")
////                }
//            }
//        }
//        migrations.apply {
//            add(migration1_2)
//            add(migration2_3)
//            add(migration5_6)
//            add(migration1_2)
//        }
        return migrations.toTypedArray()
    }
}