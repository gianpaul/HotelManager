package exirium.pe.app.shared.core.presentation.mvi.implementation

import exirium.pe.app.shared.core.presentation.mvi.Effect
import exirium.pe.app.shared.core.presentation.mvi.Intent
import exirium.pe.app.shared.core.presentation.mvi.Reducer
import exirium.pe.app.shared.core.presentation.mvi.State
import exirium.pe.app.shared.core.presentation.mvi.Processor
import exirium.pe.app.shared.core.presentation.mvi.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultStore<I : Intent, S : State, E : Effect>(
    initialState: S,
    private val reducer: Reducer<S, I>,
    private val processor: Processor<I, E>,
    private val coroutineScope: CoroutineScope
) : Store<I, S, E> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<E>()
    override val effects: Flow<E> = _effects.asSharedFlow()

    override suspend fun dispatch(intent: I) {
        val newState = reducer.reduce(state.value, intent)
        _state.value = newState

        coroutineScope.launch {
            processor.process(intent).collect { effect ->
                _effects.emit(effect)
            }
        }
    }
}