package components

//import resources.getAvatarImageVector
import androidx.compose.desktop.ui.tooling.preview.utils.esp
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import deviceSupport.DeviceTypex
import deviceSupport.deviceType
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pansion.Res
import resources.Images
import resources.getAvatarPath
import view.LocalViewManager


@OptIn(ExperimentalResourceApi::class)
private fun String.getUri(fromOnline: Boolean = false) : String {
    return if (deviceType == DeviceTypex.WINDOWS || fromOnline) "https://pansionapp-test.ru/composeResources/pansion/${this}"
    else Res.getUri(this)
}

@Composable
fun GetAsyncIcon(
    path: String,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
    size: Dp = 22.dp,
    modifier: Modifier = Modifier
) {
    val uri = "drawable/icons/${path}".getUri()
    AsyncImage(
        ImageRequest.Builder(LocalPlatformContext.current)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .data(uri)
            .crossfade(true)
            .build(),
        onError = {
            println(it.result)
        },
        modifier = modifier.size(size, size),
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(tint)
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GetAsyncImage(
    path: String,
    modifier: Modifier = Modifier.size(25.dp),
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    filterQuality: FilterQuality = FilterQuality.None
) {
    val uri = "drawable/${path}".getUri()
    AsyncImage(
        ImageRequest.Builder(LocalPlatformContext.current)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .data(uri)
            .crossfade(true)
            .build(),
        modifier = modifier,
        contentDescription = contentDescription,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
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
    ignoreShowAvatars: Boolean = false
) {
    val viewManager = LocalViewManager.current
    val isDark = viewManager.isDark.value
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
        if (path == null || (!viewManager.showAvatars.value && !ignoreShowAvatars)) { //avatarId in listOf(0, 1) TODOIK
            Text(
                name[0].toString(),
                fontSize = textSize,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        } else {
            val fromOnline = path == Images.Avatars.Nevrozq.me1.second.path
            val uri = "drawable/avatars/${path}.webp".getUri(fromOnline)
            AsyncImage(
                ImageRequest.Builder(LocalPlatformContext.current)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(if (fromOnline) CachePolicy.DISABLED else CachePolicy.ENABLED)
                    .data(uri)
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

