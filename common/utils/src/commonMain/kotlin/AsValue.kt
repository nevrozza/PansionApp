
import com.arkivanov.decompose.Cancellation
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.store.Store

fun <T : Any> Store<*, T, *>.asValue(): Value<T> =
    object : Value<T>() {

        override val value: T get() = state
        private var disposables = emptyMap<(T) -> Unit, Disposable>()

        override fun subscribe(observer: (T) -> Unit): Cancellation {
            val disposable = states(com.arkivanov.mvikotlin.core.rx.observer(onNext = observer))
            this.disposables += observer to disposable
            return Cancellation { unsubscribe(observer) }
        }

        fun unsubscribe(observer: (T) -> Unit) {
            val disposable = disposables[observer] ?: return
            this.disposables -= observer
            disposable.dispose()
        }
    }

const val applicationVersion = 21
const val applicationVersionString = "1.2.2"


const val isTestMode = false


expect val androidVersion: Int