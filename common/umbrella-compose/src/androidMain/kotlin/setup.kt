import android.content.res.Resources.Theme
import android.view.ViewManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import root.RootComponent
import view.LocalViewManager
import view.ThemeTint

@ExperimentalAnimationApi
@ExperimentalFoundationApi
fun ComponentActivity.init(root: RootComponent) {
    setContent {
        Root(root)
    }
}
