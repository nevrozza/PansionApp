package components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import resources.RIcons

@Composable
fun SaveAnimation(isShowing: Boolean, customText: String? = null, modifier: Modifier? = null, unShow: () -> Unit) {
    Box(modifier ?: Modifier.fillMaxSize()) {
        AnimatedVisibility(
            isShowing,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val scope = rememberCoroutineScope()
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 5.dp,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(15.dp)
                ) {
                    GetAsyncIcon(
                        path = RIcons.Check,
                        tint = Color.Green,
                        size = 70.dp
                    )
                    Text(customText ?: "Успешно сохранено!")
                }
            }
            scope.launch {
                delay(1500)
                unShow()
            }
        }
    }
}
@Composable
fun ErrorAnimation(isShowing: Boolean, textError: String, modifier: Modifier? = null, unShow: () -> Unit) {
    Box(modifier ?: Modifier.fillMaxSize()) {
        AnimatedVisibility(
            isShowing,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val scope = rememberCoroutineScope()
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 5.dp,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(15.dp)
                ) {
                    GetAsyncIcon(
                        path = RIcons.Close,
                        tint = Color.Red,
                        size = 70.dp
                    )
                    Text(textError, textAlign = TextAlign.Center)
                }
            }
            scope.launch {
                delay(1500)
                unShow()
            }
        }
    }
}