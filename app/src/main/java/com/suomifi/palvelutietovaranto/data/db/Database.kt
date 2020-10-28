package com.suomifi.palvelutietovaranto.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.suomifi.palvelutietovaranto.data.db.dao.search.SearchLocationDao
import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation

@Database(entities = [SearchLocation::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun searchLocationDao(): SearchLocationDao

    companion object {
        const val DATABASE_NAME = "service-locations-db"
    }
}
