import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import java.awt.Desktop
import java.net.URI

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun DecoratedWindowScope.TitleBarView() {
//    TitleBar(Modifier.newFullscreenControls()) {
//        Text(title)
//
//        Row(Modifier.align(Alignment.End)) {
//            Tooltip({
//                Text("Open Jewel Github repository")
//            }) {
//                IconButton({
//                    Desktop.getDesktop().browse(URI.create("https://github.com/JetBrains/jewel"))
//                }, Modifier.size(40.dp).padding(5.dp)) {
//                    Icon(Icons.Rounded.Category, "Github")
//                }
//            }
//        }
//    }
//}