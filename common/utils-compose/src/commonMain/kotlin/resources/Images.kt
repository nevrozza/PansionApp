package resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.imageResource
import pansion.common.utils_compose.generated.resources.Geologica_Black
import pansion.common.utils_compose.generated.resources.Geologica_BlackItalic
import pansion.common.utils_compose.generated.resources.Geologica_Bold
import pansion.common.utils_compose.generated.resources.Geologica_BoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_ExtraBold
import pansion.common.utils_compose.generated.resources.Geologica_ExtraBoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_ExtraLight
import pansion.common.utils_compose.generated.resources.Geologica_ExtraLightItalic
import pansion.common.utils_compose.generated.resources.Geologica_RegularItalic
import pansion.common.utils_compose.generated.resources.Geologica_Light
import pansion.common.utils_compose.generated.resources.Geologica_LightItalic
import pansion.common.utils_compose.generated.resources.Geologica_Medium
import pansion.common.utils_compose.generated.resources.Geologica_MediumItalic
import pansion.common.utils_compose.generated.resources.Geologica_Regular
import pansion.common.utils_compose.generated.resources.Geologica_SemiBold
import pansion.common.utils_compose.generated.resources.Geologica_SemiBoldItalic
import pansion.common.utils_compose.generated.resources.Geologica_Thin
import pansion.common.utils_compose.generated.resources.Geologica_ThinItalic
import pansion.common.utils_compose.generated.resources.Res
import pansion.common.utils_compose.generated.resources.MGU
import pansion.common.utils_compose.generated.resources.a01nevrozq
import pansion.common.utils_compose.generated.resources.a02nevrozq
import pansion.common.utils_compose.generated.resources.a03nevrozq
import pansion.common.utils_compose.generated.resources.a04nevrozq
import pansion.common.utils_compose.generated.resources.a1
import pansion.common.utils_compose.generated.resources.a2
import pansion.common.utils_compose.generated.resources.a3
import pansion.common.utils_compose.generated.resources.a4
import pansion.common.utils_compose.generated.resources.a5
import pansion.common.utils_compose.generated.resources.a6
import pansion.common.utils_compose.generated.resources.a7
import pansion.common.utils_compose.generated.resources.a8
import pansion.common.utils_compose.generated.resources.emoji0
import pansion.common.utils_compose.generated.resources.emoji1
import pansion.common.utils_compose.generated.resources.emoji2
import pansion.common.utils_compose.generated.resources.emoji3
import pansion.common.utils_compose.generated.resources.emoji4
import pansion.common.utils_compose.generated.resources.emoji5
import pansion.common.utils_compose.generated.resources.emoji6


@Composable
fun getAvatarImageVector(avatarId: Int) : ImageBitmap {
    return when(avatarId) {
        -1 -> Images.Avatars.nevrozq1
        -2 -> Images.Avatars.nevrozq2
        -3 -> Images.Avatars.nevrozq3
        -4 -> Images.Avatars.nevrozq4
        2 -> Images.Avatars.a1
        3 -> Images.Avatars.a2
        4 -> Images.Avatars.a3
        5 -> Images.Avatars.a4
        6 -> Images.Avatars.a5
        7 -> Images.Avatars.a6
        8 -> Images.Avatars.a7
        9 -> Images.Avatars.a8
        else -> Images.Avatars.a8
    }
}

data object Images {
    val MGU: ImageBitmap
        @Composable get() = imageResource(Res.drawable.MGU)
    data object Avatars {
        val avatarIds = listOf<Int>(
            1, 2, 3, 4, 5, 6, 7, 8, -1, -2, -3, -4
        )
        val nevrozq1: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a01nevrozq)
        val nevrozq2: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a02nevrozq)
        val nevrozq3: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a03nevrozq)
        val nevrozq4: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a04nevrozq)
        val a1: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a1)
        val a2: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a2)
        val a3: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a3)
        val a4: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a4)
        val a5: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a5)
        val a6: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a6)
        val a7: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a7)
        val a8: ImageBitmap
            @Composable get() = imageResource(Res.drawable.a8)
    }
    data object Emoji {
        val emoji0: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji0)
        val emoji1: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji1)
        val emoji2: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji2)
        val emoji3: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji3)
        val emoji4: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji4)
        val emoji5: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji5)
        val emoji6: ImageBitmap
            @Composable get() = imageResource(Res.drawable.emoji6)
    }
}
val GeologicaFont: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.Geologica_Black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.Geologica_BlackItalic, FontWeight.Black, FontStyle.Italic),
        Font(Res.font.Geologica_Bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.Geologica_BoldItalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.Geologica_SemiBold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.Geologica_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Geologica_ExtraBold, FontWeight.ExtraBold, FontStyle.Normal),
        Font(Res.font.Geologica_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.Geologica_Medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.Geologica_MediumItalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.Geologica_Regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.Geologica_RegularItalic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.Geologica_Light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.Geologica_LightItalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.Geologica_ExtraLight, FontWeight.ExtraLight, FontStyle.Normal),
        Font(Res.font.Geologica_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.Geologica_Thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.Geologica_ThinItalic, FontWeight.Thin, FontStyle.Italic),
    )