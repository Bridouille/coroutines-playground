package com.coroutines.playground.main

import androidx.lifecycle.viewModelScope
import com.coroutines.playground.network.DigitalOceanStatusEndpoint
import com.coroutines.playground.network.models.StatusResponse
import com.coroutines.playground.utils.BaseViewModel
import com.coroutines.playground.utils.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val endpoint: DigitalOceanStatusEndpoint
): BaseViewModel<MainVs, MainEvent, MainResult>(
    MainVs()
) {

    // private val queryChanges = MutableStateFlow("")
    private var networkJob: Job? = null

    init {
        /*queryChanges
            .debounce(timeoutMillis = 300L)
            .onEach { performAction(MainEvent.FetchQuery(it)) }
            .launchIn(viewModelScope)*/
    }

    private val genericErrorHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            onActionResult(Lce.Error(throwable))
        }
    }

    override fun performAction(action: MainEvent) {
        when (action) {
            is MainEvent.FetchButtonClicked -> {
                if (action.cancel) {
                    networkJob?.cancel()
                    viewModelScope.launch {
                        onActionResult(Lce.Content(MainResult.NetworkCallCanceled))
                    }
                } else {
                    networkJob = viewModelScope.launch(genericErrorHandler) {
                        onActionResult(Lce.Loading())
                        Timber.d("delay-ing")
                        delay(2000)

                        val resp = MainResult.StatusLoaded(endpoint.getSummary())
                        onActionResult(Lce.Content(resp))
                    }
                }
            }
        }
    }

    override suspend fun reduceState(state: MainVs, result: Lce<MainResult>): MainVs {
        return when (result) {
            is Lce.Loading -> MainVs(isLoading = true)
            is Lce.Error -> MainVs(error = result.err.message)
            is Lce.Content -> {
                when (val data = result.data) {
                    is MainResult.StatusLoaded -> {
                        state.copy(
                            isLoading = false,
                            error = null,
                            status = data.resp
                        )
                    }
                    is MainResult.NetworkCallCanceled -> {
                        MainVs(error = "User canceled the request!")
                    }
                }
            }
        }
    }
}

sealed class MainEvent {
    data class FetchButtonClicked(val cancel: Boolean) : MainEvent()
}

sealed class MainResult {
    data class StatusLoaded(val resp: StatusResponse) : MainResult()

    object NetworkCallCanceled : MainResult()
}

data class MainVs(
    val isLoading: Boolean = false,
    val error: String? = null,
    val status: StatusResponse? = null
)
