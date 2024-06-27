package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import resources.getAvatarImageVector
import view.LocalViewManager

@Composable
fun GetAvatar(avatarId: Int, name: String, size: Dp = 70.dp, textSize: TextUnit = 30.sp, modifier: Modifier = Modifier) {
    val viewManager = LocalViewManager.current
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(
            brush = Brush.verticalGradient(
                colors = if (viewManager.isDark.value) listOf(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primaryContainer
                ) else listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.secondary
                ),
                tileMode = TileMode.Decal
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        if (avatarId in listOf(0, 1)) { //avatarId in listOf(0, 1) TODOIK
            Text(
                name[0].toString(),
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        } else {
            Image(
                getAvatarImageVector(avatarId),
                null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High
            )
        }

    }
}

