package com.suomifi.palvelutietovaranto.domain.interactors

import android.content.SharedPreferences
import com.suomifi.palvelutietovaranto.data.db.Database
import com.suomifi.palvelutietovaranto.data.db.model.search.SearchLocation
import com.suomifi.palvelutietovaranto.domain.interactors.base.SingleUseCase
import com.suomifi.palvelutietovaranto.domain.localization.Language
import com.suomifi.palvelutietovaranto.utils.extensions.selectedLanguage
import io.reactivex.Single

class SearchLocationUseCase(
        private val database: Database,
        private val sharedPreferences: SharedPreferences
) : SingleUseCase<String, List<SearchLocation>>() {

    override fun execute(params: String): Single<List<SearchLocation>> {
        return when (sharedPreferences.selectedLanguage()) {
            Language.Swedish -> database.searchLocationDao().findByNameSv(params)
            else -> database.searchLocationDao().findByNameFi(params)
        }
    }

}
