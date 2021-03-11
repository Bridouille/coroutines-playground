package com.coroutines.playground.main

import androidx.lifecycle.viewModelScope
import com.coroutines.playground.network.RickAndMortyEndpoint
import com.coroutines.playground.network.models.Character
import com.coroutines.playground.utils.BaseViewModel
import com.coroutines.playground.utils.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val endpoint: RickAndMortyEndpoint
): BaseViewModel<MainVs, MainEvent, MainResult>(
    MainVs()
) {

    private val queryChanges = MutableStateFlow("")
    private var networkJob: Job? = null

    init {
        queryChanges
            .debounce(timeoutMillis = 300L)
            .onEach { performAction(MainEvent.FetchQuery(it)) }
            .launchIn(viewModelScope)
    }

    override fun performAction(action: MainEvent) {
        when (action) {
            is MainEvent.LoadButtonClicked -> {
                networkJob?.cancel()
                networkJob = viewModelScope.launch {
                    onActionResult(Lce.Loading())
                    Timber.d("delay-ing")
                    delay(2000)
                    val res = try {
                        Lce.Content<MainResult>(MainResult.LoadCharactersResult(endpoint.getAllCharacters().results))
                    } catch (e: Exception) {
                        Lce.Error(e)
                    }
                    onActionResult(res)
                }
            }
            is MainEvent.TextChanged -> {
                queryChanges.value = action.query
            }
            is MainEvent.FetchQuery -> {
                viewModelScope.launch {
                    onActionResult(Lce.Content(MainResult.QueryFetched("hej ${action.query}")))
                }
            }
        }
    }

    override suspend fun reduceState(state: MainVs, result: Lce<MainResult>): MainVs {
        return when (result) {
            is Lce.Loading -> state.copy(isLoading = true)
            is Lce.Error -> state.copy(isLoading = false)
            is Lce.Content -> {
                when (val data = result.data) {
                    is MainResult.LoadCharactersResult -> {
                        state.copy(isLoading = false)
                    }
                    is MainResult.QueryFetched -> {
                        state.copy(query = data.result)
                    }
                }
            }
        }
    }
}

sealed class MainEvent {
    object LoadButtonClicked : MainEvent()
    data class TextChanged(val query: String) : MainEvent()
    data class FetchQuery(val query: String) : MainEvent()
}

sealed class MainResult {
    data class LoadCharactersResult(val list: List<Character>) : MainResult()

    data class QueryFetched(val result: String) : MainResult()
}

data class MainVs(
    val isLoading: Boolean = false,

    val query: String = ""
)
