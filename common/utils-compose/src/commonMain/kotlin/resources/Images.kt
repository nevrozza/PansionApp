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
import pansion.common.utils_compose.generated.resources.primer
import pansion.common.utils_compose.generated.resources.sb
import pansion.common.utils_compose.generated.resources.sfera

data object Images {
    val MGU: ImageBitmap
        @Composable get() = imageResource(Res.drawable.MGU)
    val SberLogo: ImageBitmap
        @Composable get() = imageResource(Res.drawable.sfera)
    val SberPrimer: ImageBitmap
        @Composable get() = imageResource(Res.drawable.primer)
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

val SberFont: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.sb, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.Black, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.ExtraBold, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.ExtraLight, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.sb, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.sb, FontWeight.Thin, FontStyle.Italic),
    )