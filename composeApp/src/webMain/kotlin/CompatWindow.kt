import androidx.compose.runtime.Composable
import com.arkivanov.essenty.backhandler.BackDispatcher
import root.RootComponent

expect fun CompatWindow(
    content: @Composable (Triple<RootComponent, BackDispatcher, String>) -> Unit
)