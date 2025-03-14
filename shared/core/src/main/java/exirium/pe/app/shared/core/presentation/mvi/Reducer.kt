package exirium.pe.app.shared.core.presentation.mvi

interface Reducer<S : State, I : Intent> {
    fun reduce(state: S, intent: I): S
}