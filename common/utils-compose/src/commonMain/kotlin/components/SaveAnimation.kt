package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
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
import view.GlobalHazeState
import view.LocalViewManager

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
                    Icon(
                        Icons.Rounded.Done, null,
                        tint = Color.Green,
                        modifier = Modifier.size(70.dp)
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
                    Icon(
                        Icons.Rounded.Close, null,
                        tint = Color.Red,
                        modifier = Modifier.size(70.dp)
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