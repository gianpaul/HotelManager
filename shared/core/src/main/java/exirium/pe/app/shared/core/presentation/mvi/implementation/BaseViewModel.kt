package exirium.pe.app.shared.core.presentation.mvi.implementation

import exirium.pe.app.shared.core.presentation.mvi.Effect
import exirium.pe.app.shared.core.presentation.mvi.Intent
import exirium.pe.app.shared.core.presentation.mvi.Processor
import exirium.pe.app.shared.core.presentation.mvi.Reducer
import exirium.pe.app.shared.core.presentation.mvi.State
import exirium.pe.app.shared.core.presentation.mvi.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<I : Intent, S : State, E : Effect>(
    initialState: S,
    private val reducer: Reducer<S, I>,
    private val processor: Processor<I, E>
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    protected val store: Store<I, S, E> = DefaultStore(
        initialState = initialState,
        reducer = reducer,
        processor = processor,
        coroutineScope = viewModelScope
    )

    val state: StateFlow<S> = store.state
    val effects: Flow<E> = store.effects

    fun dispatch(intent: I) {
        viewModelScope.launch {
            store.dispatch(intent)
        }
    }

    fun clear() {
        viewModelScope.cancel()
    }
}