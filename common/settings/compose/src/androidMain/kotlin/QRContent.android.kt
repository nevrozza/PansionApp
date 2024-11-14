import android.graphics.PathEffect
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import qr.isCameraAvailable
import qrscanner.QrScanner
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.rounded.FlashlightOff
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.CustomTextField
import components.networkInterface.NetworkState
import qr.QRComponent
import qr.QRStore
import kotlin.io.path.Path

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun QRContent(component: QRComponent, snackBarHostState: SnackbarHostState) {


    val model by component.model.subscribeAsState()
    val nModel by component.nInterface.networkModel.subscribeAsState()

    var flashlightOn by remember { mutableStateOf(false) }
    var openImagePicker by remember { mutableStateOf(value = false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = if (isCameraAvailable()) {
                Modifier
                    .fillMaxSize()


            } else {
                Modifier
            },
            contentAlignment = Alignment.Center
        ) {
            QrScanner(
                modifier = Modifier,
                flashlightOn = flashlightOn,
                openImagePicker = openImagePicker,
                onCompletion = {
                    if(!component.authBottomSheet.model.value.isDialogShowing && !component.registerBottomSheet.model.value.isDialogShowing) {
                        component.onEvent(
                            QRStore.Intent.ChangeCode(it)
                        )
                        component.onEvent(
                            QRStore.Intent.SendToServer
                        )
                    }
                },
                imagePickerHandler = {
                    openImagePicker = it
                },
                onFailure = {
                    if(!component.authBottomSheet.model.value.isDialogShowing) {
                        coroutineScope.launch {
                            if (it.isEmpty()) {
                                snackBarHostState.showSnackbar("Invalid qr code")
                            } else {
                                snackBarHostState.showSnackbar(it)
                            }
                        }
                    }
                }
            )
            Box(
                Modifier
                    .size(200.dp)
                    .drawRoundedCornerBorders(
                        color = Color.White,
                        strokeWidth1 = 6.dp,
                        cornerRadius1 = 30.dp
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
                .padding(top = if (isCameraAvailable()) 470.dp else 0.dp)
        ) {
            if (isCameraAvailable()) {
                IconButton(
                    onClick = {
                        flashlightOn = !flashlightOn
                    },
                    modifier = Modifier.size(50.dp).clip(CircleShape)
                        .background(Color.Black.copy(alpha = .5f))
                ) {
                    Icon(
                        imageVector = if (flashlightOn) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
                        "flash",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(Modifier.height(50.dp))
            Box(modifier = Modifier.clip(
                RoundedCornerShape(16.dp)).background(Color.Black.copy(alpha = .5f))) {
                    CustomTextField(
                        value = model.code,
                        onValueChange = {
                            component.onEvent(
                                QRStore.Intent.ChangeCode(it)
                            )
                        },
                        isMoveUpLocked = true,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        onEnterClicked = {
                            component.onEvent(
                                QRStore.Intent.SendToServer
                            )
                        },
                        isEnabled = true,
                        text = "Код",
                        modifier = Modifier.padding(horizontal = 6.dp).padding(bottom = 6.dp, top = 0.dp)
                    )
            }
        }


    }



}

fun Modifier.drawRoundedCornerBorders(
    color: Color,
    strokeWidth1: Dp,
    cornerRadius1: Dp
) = this.then(
    Modifier.drawWithContent {
        val strokeWidth = strokeWidth1.toPx()
        val cornerRadius = cornerRadius1.toPx()
        drawContent()
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, cornerRadius)
                arcTo(
                    rect = Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                moveTo(size.width - cornerRadius, 0f)
                arcTo(
                    rect = Rect(size.width - cornerRadius * 2, 0f, size.width, cornerRadius * 2),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                moveTo(size.width, size.height - cornerRadius)
                arcTo(
                    rect = Rect(
                        size.width - cornerRadius * 2,
                        size.height - cornerRadius * 2,
                        size.width,
                        size.height
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                moveTo(cornerRadius, size.height)
                arcTo(
                    rect = Rect(0f, size.height - cornerRadius * 2, cornerRadius * 2, size.height),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
)


fun Modifier.drawCornerBorders(
    color: Color,
    strokeWidth1: Dp,
    cornerSize1: Dp
) = this.then(
    Modifier.drawWithContent {

        val strokeWidth = strokeWidth1.toPx()
        val cornerSize = cornerSize1.toPx()
        drawContent()
        val pathEffect = androidx.compose.ui.graphics.PathEffect.cornerPathEffect(cornerSize)
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, 0f)
                lineTo(cornerSize, 0f)
                moveTo(0f, 0f)
                lineTo(0f, cornerSize)
                moveTo(size.width, 0f)
                lineTo(size.width - cornerSize, 0f)
                moveTo(size.width, 0f)
                lineTo(size.width, cornerSize)
                moveTo(0f, size.height)
                lineTo(cornerSize, size.height)
                moveTo(0f, size.height)
                lineTo(0f, size.height - cornerSize)
                moveTo(size.width, size.height)
                lineTo(size.width - cornerSize, size.height)
                moveTo(size.width, size.height)
                lineTo(size.width, size.height - cornerSize)
            },
            color = color,
            style = Stroke(width = strokeWidth),

            )
    }
)