package exirium.pe.app.shared.core.presentation.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Store<I : Intent, S : State, E : Effect> {
    val state: StateFlow<S>
    val effects: Flow<E>
    suspend fun dispatch(intent: I)
}