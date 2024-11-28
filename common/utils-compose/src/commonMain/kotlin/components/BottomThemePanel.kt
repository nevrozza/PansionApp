package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import forks.colorPicker.ColorSlider
import resources.RIcons
import view.ThemeTint
import view.ViewManager
import view.esp

@Composable
fun BottomThemePanel(
    viewManager: ViewManager,
    onThemeClick: (ThemeTint) -> Unit,
    modifier: Modifier = Modifier,
    isColorMenuOpened: MutableState<Boolean> = remember { mutableStateOf(false) },
    isShowBottomBar: Boolean = true,
    changeColor: (Color) -> Unit
) {
    val tints = listOf(
        Pair(ThemeTint.Dark, RIcons.DarkMode),
        Pair(ThemeTint.Auto, RIcons.AutoMode),
        Pair(ThemeTint.Light, RIcons.LightMode)
    )
    Box(
        Modifier.fillMaxSize()
            .then(
                if (isColorMenuOpened.value) {
                    Modifier.clickable(interactionSource = null, indication = null) { isColorMenuOpened.value = false }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isShowBottomBar) {
            Row(
                modifier = modifier.widthIn(max = 470.dp).fillMaxWidth()
                    //                    .bringIntoView(scrollState, imeState)

                    .padding(bottom = 12.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box() {
                    IconButton(
                        onClick = {
                            isColorMenuOpened.value = !isColorMenuOpened.value
                        }
                    ) {
                        GetAsyncIcon(
                            RIcons.BigBrush
                        )
                    }

                }


                Row() {
                    GetAsyncIcon(
                        RIcons.Telegram
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "PansionApp",
                        fontSize = 17.esp
                    )
                }

                IconButton(
                    onClick = {

                    }
                ) {
                    GetAsyncIcon(
                        RIcons.Translate,
                        size = 27.dp
                    )
                }
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
                        .padding(horizontal = 15.dp).clickable(interactionSource = null, indication = null) {  },
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
                                    iconPath = tint.second,
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
    iconPath: String,
    isSelected: Boolean,
    onClick: (ThemeTint) -> Unit
) {
    IconButton(
        onClick = {
            onClick(tint)
        },
        colors = IconButtonDefaults.iconButtonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
    ) {
        GetAsyncIcon(
            path = iconPath,
            contentDescription = tint.name,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
