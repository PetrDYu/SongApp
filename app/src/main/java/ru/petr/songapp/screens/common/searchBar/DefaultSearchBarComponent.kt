package ru.petr.songapp.screens.common.searchBar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackCallback

/**
 * Default implementation of the SearchBarComponent interface.
 * Manages search text input and search activation state with back navigation support.
 * 
 * @param componentContext The Decompose component context
 * @param onSearchClick Callback function invoked when search is performed
 */

class DefaultSearchBarComponent(
        componentContext: ComponentContext,
        private val onSearchClick: (newText: String) -> Unit
) : SearchBarComponent, ComponentContext by componentContext {
    
    /**
     * Mutable container for the current search text
     */
    private val _searchText = MutableValue("")
    
    /**
     * Read-only access to the current search text
     */
    override val searchText: Value<String> = _searchText


    /**
     * Mutable container for search activation state
     */
    private val _searchIsActive = MutableValue(false)
    
    /**
     * Read-only access to search activation state
     */
    override val searchIsActive: Value<Boolean> = _searchIsActive


    /**
     * Back callback that clears search text when back navigation is triggered
     */
    private val backCallback = BackCallback (false) {
        _searchText.update { "" }
    }

    init {
        /**
         * Subscribe to changes in search text to update search state and back callback
         */
        _searchText.subscribe { text ->
            // Deactivate search if text is empty
            if (text.isEmpty()) {
                _searchIsActive.update { false }
            }
            // Enable back navigation only when there is text in the search field
            backCallback.isEnabled = text.isNotEmpty()
        }

        // Register back callback to clear search on back navigation
        backHandler.register(backCallback)
    }

    /**
     * Updates the search text with a new value
     * @param newText New text to set in the search field
     */
    override fun onChangeSearchText(newText: String) {
        _searchText.update { newText }
    }

    /**
     * Initiates a search operation if the search text is not empty
     * Sets the search as active and invokes the provided search callback
     */
    override fun onSearch() {
        if (searchText.value.isNotEmpty()) {
            onSearchClick(searchText.value)
            _searchIsActive.update { true }
        }
    }
}