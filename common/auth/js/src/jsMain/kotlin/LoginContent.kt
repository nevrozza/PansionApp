@file:OptIn(DelicateCoroutinesApi::class)


import com.arkivanov.decompose.value.Value
import emotion.react.css
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.css.fieldset
import login.LoginComponent
import login.LoginStore
import mui.material.Box
import mui.material.Fab
import mui.material.FabVariant
import mui.material.Stack
import mui.material.TextField
import mui.material.Typography
import mui.system.responsive
import mui.system.sx
import react.FC
import react.ReactNode
import react.dom.events.ChangeEvent
import react.dom.html.InputHTMLAttributes
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.fieldset
import react.dom.onChange
import react.useEffect
import react.useEffectOnce
import react.useFunctionState
import react.useMemo
import react.useState
import web.cssom.AlignItems
import web.cssom.AlignSelf
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FontPalette
import web.cssom.Overflow
import web.cssom.Position
import web.cssom.VerticalAlign
import web.cssom.pct
import web.cssom.px
import web.cssom.rgb
import web.html.HTML.fieldset
import web.html.HTMLInputElement
import web.html.InputType
import web.prompts.alert
import kotlin.js.Promise


var LoginContent: FC<RProps<LoginComponent>> = FC { props ->
    val d = darkTheme
    val model by props.component.model.useAsState()


    Box {

        sx {
            backgroundColor = d.background
            height = 100.pct
            width = 100.pct
        }

//        Typography {
//            +"Afisha"
//        }
        Stack {
            spacing = responsive(5)
            sx {
                alignItems = AlignItems.center
                overflowY = Overflow.scroll
                maxHeight = 100.pct
                webkitScrollbar {
                    width = 12.px
                }
                webkitScrollbarThumb {
                    backgroundColor = hoveredColor(d.background, 50)
                    borderRadius = 6.px
                    hover {
                        backgroundColor = hoveredColor(hoveredColor(d.background, 50))
                    }
                }
                webkitScrollbarTrack {
                    backgroundColor = d.background
                }
            }

            TextField {
                type = InputType.text
                label = ReactNode("Логин")
                value = model.login
                sx {
                    ReactHTML.label { color = d.onBackground }
                    "step.Mui-focused" { color = d.secondaryContainer }
                    ReactHTML.fieldset { borderColor = d.secondaryContainer.hv(5) }
                    ".MuiOutlinedInput-root" {
                        "&:hover fieldset" { borderColor = d.secondaryContainer.hv(20) }
                        "&.Mui-focused fieldset" { borderColor = d.secondaryContainer }
                    }
                    ReactHTML.input { color = d.onBackground }
                }
                onChange = { event ->
                    val inputElement = event.target as HTMLInputElement
                    props.component.onEvent(LoginStore.Intent.InputLogin(inputElement.value))

                }
            }
        }

    }

}
