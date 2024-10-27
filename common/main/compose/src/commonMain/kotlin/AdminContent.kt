import admin.AdminComponent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AppBar
import components.CLazyColumn
import dev.chrisbanes.haze.HazeState
import view.LocalViewManager
import view.rememberImeState

@ExperimentalLayoutApi
@Composable
fun AdminContent(
    component: AdminComponent,
    isActive: Boolean = false,
    currentRouting: AdminComponent.Output? = null,
) {
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val viewManager = LocalViewManager.current
//    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val lazyListState = rememberLazyListState()
    val hazeState = remember { HazeState() }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            AppBar(
                title = {

                    Text(
                        "Администрация",
                        modifier = Modifier.padding(start = 10.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                hazeState = hazeState,
                isHazeActivated = true
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            if (model.items != null) {
                CLazyColumn(
                    padding = padding,
                    isBottomPaddingNeeded = true,
                    hazeState = hazeState
                ) {
                    items(model.items!!) { item ->
                        AdminItemCompose(item.title, currentRouting == item.routing, isActive) {
                            component.onOutput(item.routing)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            } else {

            }
        }

    }
}

@Composable
fun AdminItemCompose(title: String, isEnabled: Boolean, isActive: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.filledTonalButtonColors(containerColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp), contentColor = if (isEnabled && !isActive) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface)
    ) {
        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null)
        }
    }
}