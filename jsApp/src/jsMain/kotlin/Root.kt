import com.arkivanov.decompose.value.Value
import js.core.Object
import js.core.jso
import mui.system.Box
import mui.system.sx
import react.ChildrenBuilder
import react.FC
import react.Props
import react.StateInstance
import react.useEffectOnce
import react.useState
import root.RootComponent
import web.cssom.BoxSizing
import web.cssom.Display
import web.cssom.Flex
import web.cssom.FlexDirection
import web.cssom.Overflow
import web.cssom.Position
import web.cssom.number
import web.cssom.pct
import web.cssom.px


var RootContent: FC<RProps<RootComponent>> = FC { props ->
    val childStack by props.component.childStack.useAsState()
    Box {
        sx {
//            backgroundColor = Color("#000000")
            display = Display.flex
            flexDirection = FlexDirection.column
            position = Position.fixed
            padding = 0.px
            top = 0.px
            bottom = 0.px
            left = 0.px
            right = 0.px
        }

        Box {
            sx {
                width = 100.pct
                boxSizing = BoxSizing.borderBox
                flex = Flex(grow = number(1.0), shrink = number(0.0), basis = 0.px)
                overflowY = Overflow.clip
            }

            when (val child = childStack.active.instance) {
                is RootComponent.Child.AuthLogin -> componentContent(
                    component = child.component,
                    content = LoginContent
                )
                else -> {}
            }.let {}
        }
    }
}