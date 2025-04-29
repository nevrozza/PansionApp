package utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import components.foundation.hazeHeader
import components.foundation.hazeUnder
import view.LocalBottomWebPadding
import view.LocalViewManager
import view.colorScheme
import view.consts.Paddings

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HideKeyboardLayout(
    height: MutableState<Int> = remember { mutableStateOf(0) },
    isSmall: Boolean = false
) {
    println("HEREISHEIGHT:${height.value}")
    val viewManager = LocalViewManager.current
    if (viewManager.isHideKeyboardButtonShown.value) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val ime by rememberImeState()


        val density = LocalDensity.current
        val bottomWebPadding = LocalBottomWebPadding.current


        val yWebOffset = animateDpAsState(
            with(density) { bottomWebPadding.value.toDp() },
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ).value

        val yImeOffset = viewManager.imeInsetValue.value

        var offsetYPos by remember { mutableStateOf(0) }
        var offsetXPos by remember { mutableStateOf(0) }

        CompositionLocalProvider(
            LocalContentColor provides colorScheme.primary
        ) {
            Box(
                if (isSmall) Modifier else
                    Modifier
                        .onGloballyPositioned {
                            try {
                                offsetYPos = it.positionOnScreen().y.toInt()
                                val x = it.positionOnScreen().x.toInt()
                                if (x != 0) {
                                    offsetXPos = x + it.size.width
                                }
                                height.value = it.size.height
                            } catch (_: Throwable) {
                            }
                        }
                        .offset {
                            var totalY = (viewManager.size?.maxHeight?.roundToPx() ?: 0) -
                                    (height.value + (yWebOffset.roundToPx() + yImeOffset))
                            if (offsetYPos != 0) {
                                totalY -= offsetYPos
                            }

                            var totalX = 0
                            if (offsetXPos != 0) {
                                totalX = offsetXPos - (viewManager.size?.maxWidth?.roundToPx() ?: 0)
                            }

                            IntOffset(
                                y = totalY,
                                x = totalX
                            )
                        },
                contentAlignment = Alignment.BottomEnd
            ) {
                AnimatedVisibility(
                    ime,
                    enter = fadeIn(
                        animationSpec = tween(delayMillis = 100, durationMillis = 500)
                    ) + scaleIn(
                        animationSpec = tween(delayMillis = 100, durationMillis = 500)
                    ),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier

                            .padding(
                                bottom = if (isSmall) 0.dp else
                                    Paddings.verySmall
                            )
                            .padding(start = if (isSmall) 0.dp else Paddings.verySmall)
                            .clip(ButtonDefaults.shape)
                            .background(
                                if (viewManager.hazeHardware.value) Color.Transparent else ButtonDefaults.elevatedButtonColors().containerColor
                            )
                            .hazeHeader(
                                viewManager = viewManager,
                                isMasked = false
                            ).hazeUnder(
                                viewManager,
                                zIndex = 15f
                            ).clickable {
                                if (keyboardController == null) {
                                    focusManager.clearFocus()
                                } else {
                                    keyboardController.hide()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Спрятать клавиатуру",
                            modifier = Modifier.padding(
                                vertical = Paddings.verySmall,
                                horizontal = Paddings.semiMedium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}