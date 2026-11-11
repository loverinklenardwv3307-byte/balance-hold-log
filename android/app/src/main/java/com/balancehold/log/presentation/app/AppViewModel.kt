package com.balancehold.log.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balancehold.log.core.navigation.Screen
import com.balancehold.log.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { value ->
                _darkTheme.value = value.darkTheme
            }
        }
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }
}
