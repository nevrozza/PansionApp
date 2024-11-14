package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

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
                withStyle(SpanStyle(fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )) {
                    append("Огромное спасибо!!\n")
                }
                append("что согласились попробовать эту\n")
                withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                    append("альфа-")
                }
                append("версию")
            }, textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}
