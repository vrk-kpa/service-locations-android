package com.suomifi.palvelutietovaranto.data.db.model.search

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class SearchLocation(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        @ColumnInfo(name = COLUMN_NAME_FI)
        val nameFi: String,
        @ColumnInfo(name = COLUMN_NAME_SV)
        val nameSv: String,
        @ColumnInfo(name = COLUMN_LATITUDE)
        val latitude: Double,
        @ColumnInfo(name = COLUMN_LONGITUDE)
        val longitude: Double
) {
    companion object {
        const val TABLE_NAME = "SearchLocation"
        const val COLUMN_NAME_FI = "nameFi"
        const val COLUMN_NAME_SV = "nameSv"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }
}
