package components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.mpChose.mpChoseStore
import forks.colorPicker.ColorSlider
import view.ThemeTint
import view.ViewManager
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun BottomThemePanel(
    viewManager: ViewManager,
    onThemeClick: (ThemeTint) -> Unit,
    modifier: Modifier = Modifier,
    changeColor: (Color) -> Unit
) {
    val isColorMenuOpened = remember { mutableStateOf(false) }
    val tints = listOf(
        Pair(ThemeTint.Dark, Icons.Rounded.DarkMode),
        Pair(ThemeTint.Auto, Icons.Rounded.AutoMode),
        Pair(ThemeTint.Light, Icons.Rounded.LightMode)
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = modifier.widthIn(max = 470.dp).fillMaxWidth()
//                    .bringIntoView(scrollState, imeState)

                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box() {
                IconButton(
                    onClick = {
                        isColorMenuOpened.value = !isColorMenuOpened.value
                    }
                ) {
                    Icon(
                        Icons.Rounded.Brush,
                        null,
                        modifier = Modifier.size(27.dp)
                    )
                }

            }


            Row() {
                Icon(
                    Icons.AutoMirrored.Rounded.Send,
                    null,
                    modifier = Modifier.rotate(360 - 45.0f)
                )
                Text(
                    "@pansionApp"
                )
            }

            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    Icons.Rounded.Translate,
                    null,
                    modifier = Modifier.size(27.dp)
                )
            }
        }
        Box(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth().animateContentSize()
        ) {
            AnimatedVisibility(
                isColorMenuOpened.value,
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 })
            ) {
                Surface(
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 60.dp)
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(40)
                ) {
                    Column(Modifier.align(Alignment.Center).padding(vertical = 10.dp)) {
                        ColorSlider(Modifier.padding(bottom = 5.dp)) {
                            changeColor(it)
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            tints.forEach { tint ->
                                ChangeTintButton(
                                    tint = tint.first,
                                    imageVector = tint.second,
                                    isSelected = viewManager.tint.value == tint.first
                                ) {
                                    onThemeClick(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangeTintButton(
    tint: ThemeTint,
    imageVector: ImageVector,
    isSelected: Boolean,
    onClick: (ThemeTint) -> Unit
) {
    IconButton(
        onClick = {
            onClick(tint)
        },
        colors = IconButtonDefaults.iconButtonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = tint.name,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
