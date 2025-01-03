package components

//import resources.getAvatarImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pansion.common.utils_compose.generated.resources.Res
import resources.getAvatarPath
import view.LocalViewManager
import view.ThemeTint
import view.esp

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GetAsyncIcon(
    path: String,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        ImageRequest.Builder(LocalPlatformContext.current)
            .data(Res.getUri("drawable/icons/${path}"))
            .crossfade(true)
            .build(),
        modifier = modifier.size(size, size),
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(tint)
    )

//            .components {
//                add(SvgDecoder.Factory())
//            }
//            .crossfade(true)
//            .build()

}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GetAsyncAvatar(
    avatarId: Int,
    name: String,
    size: Dp = 70.dp,
    textSize: TextUnit = 30.esp,
    modifier: Modifier = Modifier,
    isHighQuality: Boolean = true,
    prePath: String? = null,
    isCrossfade: Boolean = true
) {
    val viewManager = LocalViewManager.current
    val isDark = if (viewManager.tint.value == ThemeTint.Auto) isSystemInDarkTheme()
    else viewManager.tint.value == ThemeTint.Dark
    val path = prePath ?: if (avatarId !in listOf(0, 1)) getAvatarPath(avatarId) else null

    Box(
        modifier = modifier.size(size).clip(CircleShape).background(
            brush = Brush.verticalGradient(
                colors = if (isDark) listOf(

                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.inversePrimary,
                ) else listOf(
                    MaterialTheme.colorScheme.inversePrimary,
                    MaterialTheme.colorScheme.primary
                ),
                tileMode = TileMode.Decal
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        if (path == null) { //avatarId in listOf(0, 1) TODOIK
            Text(
                name[0].toString(),
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        } else {
            AsyncImage(
                ImageRequest.Builder(LocalPlatformContext.current)
                    .data(Res.getUri("drawable/avatars/${path}.webp"))
                    .crossfade(300)
                    .build(),
                null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                filterQuality = if (isHighQuality) FilterQuality.High else FilterQuality.Low
            )
        }

    }
}

//@Composable
//fun GetAvatar(
//    avatarId: Int,
//    name: String,
//    size: Dp = 70.dp,
//    textSize: TextUnit = 30.esp,
//    modifier: Modifier = Modifier,
//    isHighQuality: Boolean = true,
//    imageBitmap: ImageBitmap? = null
//) {
//    val viewManager = LocalViewManager.current
//    val isDark = if (viewManager.tint.value == ThemeTint.Auto) isSystemInDarkTheme()
//    else viewManager.tint.value == ThemeTint.Dark
//    val image = imageBitmap ?: if (avatarId !in listOf(0, 1)) getAvatarImageVector(avatarId) else null
//    image?.prepareToDraw()
//    Box(
//        modifier = modifier.size(size).clip(CircleShape).background(
//            brush = Brush.verticalGradient(
//                colors = if (isDark) listOf(
//
//                    MaterialTheme.colorScheme.primary,
//                    MaterialTheme.colorScheme.inversePrimary,
//                ) else listOf(
//                    MaterialTheme.colorScheme.inversePrimary,
//                    MaterialTheme.colorScheme.primary
//                ),
//                tileMode = TileMode.Decal
//            )
//        ),
//        contentAlignment = Alignment.Center
//    ) {
//        if (image == null) { //avatarId in listOf(0, 1) TODOIK
//            Text(
//                name[0].toString(),
//                fontSize = textSize,
//                fontWeight = FontWeight.Normal,
//                color = Color.White
//            )
//        } else {
//            Image(
//                image,
//                null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop,
//                filterQuality = if (isHighQuality) FilterQuality.High else FilterQuality.Low
//            )
//        }
//
//    }
//}

