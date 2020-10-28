package com.suomifi.palvelutietovaranto.data.db.dao.search

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation
import io.reactivex.Single

@Dao
interface SearchLocationDao {
    @Query("SELECT * FROM ${SearchLocation.TABLE_NAME} WHERE ${SearchLocation.COLUMN_NAME_FI} LIKE '%' || :nameFi || '%'")
    fun findByNameFi(nameFi: String): Single<List<SearchLocation>>

    @Query("SELECT * FROM ${SearchLocation.TABLE_NAME} WHERE ${SearchLocation.COLUMN_NAME_SV} LIKE '%' || :nameSv || '%'")
    fun findByNameSv(nameSv: String): Single<List<SearchLocation>>

    @Insert
    fun insertAll(vararg searchLocations: SearchLocation)
}
