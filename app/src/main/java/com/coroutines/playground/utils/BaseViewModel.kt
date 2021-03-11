package com.coroutines.playground.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

/**
 * reduceState(currState, result) should emit a new state depending on the result
 * performAction(actionToPerform) should call onActionResult() if the action has a result
 */
abstract class BaseViewModel<StateT, ActionT, ActionResultT>(
    initialState: StateT
) : ViewModel() {

    private val actionResults = MutableSharedFlow<Lce<ActionResultT>>(
        replay = 0,
        extraBufferCapacity = 64
    )
    private val stateFlow = MutableStateFlow(initialState)

    protected val state: StateT
        get() = stateFlow.value

    init {
        actionResults
            .scan(initialState, ::reduceState)
            .onEach { stateFlow.value = it }
            .launchIn(viewModelScope)
    }

    protected abstract suspend fun reduceState(
        state: StateT,
        result: Lce<ActionResultT>
    ): StateT

    abstract fun performAction(action: ActionT)

    protected suspend fun onActionResult(result: Lce<ActionResultT>) {
        actionResults.emit(result)
    }

    fun observeState(): Flow<StateT> = stateFlow
}