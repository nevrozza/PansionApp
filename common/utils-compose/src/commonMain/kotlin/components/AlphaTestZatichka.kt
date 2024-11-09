package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphaTestZatichka(
    onSettingsClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar =
        {
            if (onSettingsClick != null) {
                AppBar(
                    actionRow = {

                        IconButton(
                            onClick = {
                                onSettingsClick()
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Settings, null
                            )
                        }
                    },
                    hazeState = null
                )
            }
        }
    )
    {
        Column(
            Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 25.sp
                )) {
                    append("Огромное спасибо!!\n")
                }
                append("что согласились попробовать эту\n")
                withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                    append("альфа-")
                }
                append("версию")
            }, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}
