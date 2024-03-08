package decomposeComponents

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.AnimatedCommonButton
import components.CustomTextButton
import components.CustomTextField
import components.LoadingAnimation
import components.cAlertDialog.CAlertDialogComponent
import components.listDialog.ListDialogComponent
import components.listDialog.ListDialogStore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import components.cAlertDialog.CAlertDialogStore
import kotlinx.coroutines.launch
import view.LocalViewManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CAlertDialogContent(
    component: CAlertDialogComponent,
    title: String = "",
    content: @Composable (() -> Unit)
) {
    val model by component.model.subscribeAsState()
    if (model.isDialogShowing) {
        AlertDialog(
            onDismissRequest = {
                model.onDeclineClick?.invoke()
            },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
                shape = MaterialTheme.shapes.large
            ) {
                Crossfade(
                    model,
                    modifier = Modifier.animateContentSize().heightIn(max = 600.dp).widthIn(min = TextFieldDefaults.MinWidth)
                ) {

                    when {
                        !it.isInProcess && it.error.isBlank() -> {
                            Column(Modifier.padding(6.dp)) {
                                if(title.isNotBlank()) {
                                    Text(
                                        title, fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp, modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Box(Modifier.heightIn(max = 350.dp)) {
                                    content()
                                }
                                AnimatedCommonButton(
                                    text = "Создать",
                                    isEnabled = model.isButtonEnabled,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (model.isButtonEnabled) {
                                        model.onAcceptClick?.invoke()
                                    }
                                }

                            }
                        }

                        it.isInProcess -> {
                            LoadingAnimation()
                        }

                        else -> {
                            Column(
                                Modifier.width(TextFieldDefaults.MinWidth).padding(6.dp)
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(model.error)
                                Spacer(Modifier.height(7.dp))
                                CustomTextButton("Попробовать ещё раз") {
                                    component.onEvent(CAlertDialogStore.Intent.ClearError(model.onRetrySpecialClick))
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}