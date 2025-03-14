package exirium.pe.app.shared.core.presentation.mvi

import kotlinx.coroutines.flow.Flow

interface Processor<I : Intent, E : Effect> {
    suspend fun process(intent: I): Flow<E>
}