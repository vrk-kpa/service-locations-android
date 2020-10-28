package com.suomifi.palvelutietovaranto.ui.search

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.FloatingSearchView.LEFT_ACTION_MODE_NO_LEFT_ACTION
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.suomifi.palvelutietovaranto.R
import com.suomifi.palvelutietovaranto.databinding.ActivityLocationSearchBinding
import com.suomifi.palvelutietovaranto.ui.common.BaseActivity
import com.suomifi.palvelutietovaranto.utils.extensions.isPortrait
import com.suomifi.palvelutietovaranto.utils.extensions.observe
import kotlinx.android.synthetic.main.activity_location_search.*
import org.koin.android.viewmodel.ext.android.viewModel

class LocationSearchActivity : BaseActivity() {

    private val viewModel: LocationSearchViewModel by viewModel()
    private var binding: ActivityLocationSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isPortrait()) {
            finish()
            return
        }
        setContentView()
        observeViewModel()
        setupSearchView()
    }

    private fun setContentView() {
        binding = DataBindingUtil.setContentView<ActivityLocationSearchBinding>(this, R.layout.activity_location_search).also { binding ->
            binding.viewModel = viewModel
        }
    }

    private fun observeViewModel() {
        viewModel.closeLocationSearch.observe(this) {
            closeActivity()
        }
        viewModel.searchLocations.observe(this) { searchLocations ->
            search_view.hideProgress()
            search_view.swapSuggestions(searchLocations)
        }
    }

    private fun setupSearchView() {
        // display keyboard
        search_view.setSearchFocused(true)
        // display back arrow
        search_view.setLeftActionMode(LEFT_ACTION_MODE_NO_LEFT_ACTION)
        // finish activity when back arrow clicked
        search_view.findViewById<View>(com.arlib.floatingsearchview.R.id.left_action).setOnClickListener {
            closeActivity()
        }
        search_view.setOnQueryChangeListener { _, query ->
            if (query.isBlank()) {
                search_view.clearSuggestions()
            } else {
                search_view.showProgress()
                viewModel.onSearchQueryChanged(query)
            }
        }
        search_view.setOnSearchListener({ currentQuery ->
            viewModel.onSearchQueryChanged(currentQuery)
        }, { searchSuggestion ->
            setResultAndCloseActivity(searchSuggestion)
        })
        search_view.setOnFocusClearedListener {
            closeActivity()
        }
    }

    private fun closeActivity() {
        // hide keyboard
        search_view.setSearchFocused(false)
        finish()
    }

    private fun setResultAndCloseActivity(searchSuggestion: SearchSuggestion) {
        val searchResult = LocationSearchSuggestionMapper.map(searchSuggestion as LocationSearchSuggestion)
        setResult(RESULT_OK, Intent().putExtra(LOCATION_SEARCH_RESULT, searchResult))
        closeActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.unbind()
    }

    companion object {
        const val LOCATION_SEARCH_RESULT = "LOCATION_SEARCH_RESULT"
    }

}

private fun FloatingSearchView.setOnSearchListener(onSearchAction: (currentQuery: String) -> Unit,
                                                   onSuggestionClicked: (searchSuggestion: SearchSuggestion) -> Unit) {
    setOnSearchListener(object : FloatingSearchView.OnSearchListener {
        override fun onSearchAction(currentQuery: String) {
            onSearchAction.invoke(currentQuery)
        }

        override fun onSuggestionClicked(searchSuggestion: SearchSuggestion) {
            onSuggestionClicked.invoke(searchSuggestion)
        }
    })
}

private fun FloatingSearchView.setOnFocusClearedListener(onFocusCleared: () -> Unit) {
    setOnFocusChangeListener(object : FloatingSearchView.OnFocusChangeListener {
        override fun onFocusCleared() {
            onFocusCleared.invoke()
        }

        override fun onFocus() {
            // noop
        }
    })
}
