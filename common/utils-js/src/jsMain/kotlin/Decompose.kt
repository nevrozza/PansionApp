import com.arkivanov.decompose.value.Value
import js.core.Object
import js.core.jso
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateInstance
import react.useEffectOnce
import react.useState

var uniqueId: Long = 0L
internal fun Any.uniqueId(): Long {
    var id: dynamic = asDynamic().__unique_id
    if (id == undefined) {
        id = ++uniqueId
        Object.defineProperty<Any, Long>(this, "__unique_id", jso { value = id })
    }

    return id
}

internal fun Any.uniqueKey(): String = uniqueId().toString()
fun <T : Any> Value<T>.useAsState(): StateInstance<T> {
    val state = useState { value }
    val (_, set) = state

    useEffectOnce {
        val observer: (T) -> Unit = { set(it) }
        subscribe(observer)
        cleanup { unsubscribe(observer) }
    }

    return state
}

fun <T : Any> ChildrenBuilder.componentContent(component: T, content: FC<RProps<T>>) {
    content {
        this.component = component
        key = component.uniqueKey()
    }
}

external interface RProps<T : Any> : Props {
    var component: T
}