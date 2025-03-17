package decompose

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import kotlinx.coroutines.flow.Flow

interface DefaultMVIComponent<Intent : Any, State : Any, Label : Any> : InstanceKeeper.Instance {
    val store: Store<Intent, State, Label>
    val model: Value<State>
        get() = store.asValue()
    val labels: Flow<Label>
        get() = store.labels

    fun onEvent(event: Intent) {
        store.accept(event)
    }
}
