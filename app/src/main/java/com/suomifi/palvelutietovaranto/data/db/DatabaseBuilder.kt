package com.suomifi.palvelutietovaranto.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation
import com.suomifi.palvelutietovaranto.data.search.SearchLocationsParser

object DatabaseBuilder {

    fun createDatabase(context: Context): Database {
        return Room.databaseBuilder(
                context,
                Database::class.java,
                Database.DATABASE_NAME
        ).doOnCreate { db ->
            populateDatabase(context, db)
        }.build()
    }

    private fun populateDatabase(context: Context, db: SupportSQLiteDatabase) {
        SearchLocationsParser.parseLocationsFile(context).locations.forEach { location ->
            db.execSQL("INSERT INTO ${SearchLocation.TABLE_NAME} VALUES(null, \"${location.translations["fi"]}\", \"${location.translations["sv"]}\", ${location.coordinates.latitude}, ${location.coordinates.longitude})")
        }
    }

    private fun <T : RoomDatabase> RoomDatabase.Builder<T>.doOnCreate(callback: (db: SupportSQLiteDatabase) -> Unit): RoomDatabase.Builder<T> {
        return addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                callback.invoke(db)
            }
        })
    }

}
