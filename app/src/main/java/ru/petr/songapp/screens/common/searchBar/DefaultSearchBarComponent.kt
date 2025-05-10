package ru.petr.songapp.screens.common.searchBar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackCallback

class DefaultSearchBarComponent(
        componentContext: ComponentContext,
        private val onSearchClick: (newText: String) -> Unit
) : SearchBarComponent, ComponentContext by componentContext {
    private val _searchText = MutableValue("")
    override val searchText: Value<String> = _searchText

    private val _searchIsActive = MutableValue(false)
    override val searchIsActive: Value<Boolean> = _searchIsActive

    private val backCallback = BackCallback (false) {
        _searchText.update { "" }
    }

    init {
        _searchText.subscribe { text ->
            if (text.isEmpty()) {
                _searchIsActive.update { false }
            }
            backCallback.isEnabled = text.isNotEmpty()
        }

        backHandler.register(backCallback)
    }

    override fun onChangeSearchText(newText: String) {
        _searchText.update { newText }
    }

    override fun onSearch() {
        if (searchText.value.isNotEmpty()) {
            onSearchClick(searchText.value)
            _searchIsActive.update { true }
        }
    }
}