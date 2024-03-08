package view

import MRColors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

//Colors.kt
//val greenLightPalette: ColorScheme
//    @Composable
//    get() = greenLightPalette()

//@Composable
//fun AppTheme(colorScheme: ColorScheme, content: @Composable () -> Unit) {
//    MaterialTheme(
//        colorScheme = colorScheme,
//        content = content
//    )
//}
//
//var magicForUpdateSettings: MutableState<Boolean> = mutableStateOf(false)

@Composable
fun defaultLightPalette():
        ColorScheme
        = ColorScheme(
    primary =               MRColors.defaultLightPrimary.toColor(),
    onPrimary =             MRColors.defaultLightOnPrimary.toColor(),
    primaryContainer =      MRColors.defaultLightPrimaryContainer.toColor(),
    onPrimaryContainer =    MRColors.defaultLightOnPrimaryContainer.toColor(),
    inversePrimary =        MRColors.defaultLightInversePrimary.toColor(),
    secondary =             MRColors.defaultLightSecondary.toColor(),
    onSecondary =           MRColors.defaultLightOnSecondary.toColor(),
    secondaryContainer =    MRColors.defaultLightSecondaryContainer.toColor(),
    onSecondaryContainer =  MRColors.defaultLightOnSecondaryContainer.toColor(),
    tertiary =              MRColors.defaultLightTertiary.toColor(),
    onTertiary =            MRColors.defaultLightOnTertiary.toColor(),
    tertiaryContainer =     MRColors.defaultLightTertiaryContainer.toColor(),
    onTertiaryContainer =   MRColors.defaultLightOnTertiaryContainer.toColor(),
    background =            MRColors.defaultLightBackground.toColor(),
    onBackground =          MRColors.defaultLightOnBackground.toColor(),
    surface =               MRColors.defaultLightSurface.toColor(),
    onSurface =             MRColors.defaultLightOnSurface.toColor(),
    surfaceVariant =        MRColors.defaultLightSurfaceVariant.toColor(),
    onSurfaceVariant =      MRColors.defaultLightOnSurfaceVariant.toColor(),
    surfaceTint =           MRColors.defaultLightSurfaceTint.toColor(),
    inverseSurface =        MRColors.defaultLightInverseSurface.toColor(),
    inverseOnSurface =      MRColors.defaultLightInverseOnSurface.toColor(),
    error =                 MRColors.defaultLightError.toColor(),
    onError =               MRColors.defaultLightOnError.toColor(),
    errorContainer =        MRColors.defaultLightErrorContainer.toColor(),
    onErrorContainer =      MRColors.defaultLightOnErrorContainer.toColor(),
    outline =               MRColors.defaultLightOutline.toColor(),
    outlineVariant =        MRColors.defaultLightOutlineVariant.toColor(),
    scrim = Color.Black
)

@Composable
fun defaultDarkPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.defaultDarkPrimary.toColor(),
    onPrimary =                 MRColors.defaultDarkOnPrimary.toColor(),
    primaryContainer =          MRColors.defaultDarkPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.defaultDarkOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.defaultDarkInversePrimary.toColor(),
    secondary =                 MRColors.defaultDarkSecondary.toColor(),
    onSecondary =               MRColors.defaultDarkOnSecondary.toColor(),
    secondaryContainer =        MRColors.defaultDarkSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.defaultDarkOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.defaultDarkTertiary.toColor(),
    onTertiary =                MRColors.defaultDarkOnTertiary.toColor(),
    tertiaryContainer =         MRColors.defaultDarkTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.defaultDarkOnTertiaryContainer.toColor(),
    background =                MRColors.defaultDarkBackground.toColor(),
    onBackground =              MRColors.defaultDarkOnBackground.toColor(),
    surface =                   MRColors.defaultDarkSurface.toColor(),
    onSurface =                 MRColors.defaultDarkOnSurface.toColor(),
    surfaceVariant =            MRColors.defaultDarkSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.defaultDarkOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.defaultDarkSurfaceTint.toColor(),
    inverseSurface =            MRColors.defaultDarkInverseSurface.toColor(),
    inverseOnSurface =          MRColors.defaultDarkInverseOnSurface.toColor(),
    error =                     MRColors.defaultDarkError.toColor(),
    onError =                   MRColors.defaultDarkOnError.toColor(),
    errorContainer =            MRColors.defaultDarkErrorContainer.toColor(),
    onErrorContainer =          MRColors.defaultDarkOnErrorContainer.toColor(),
    outline =                   MRColors.defaultDarkOutline.toColor(),
    outlineVariant =            MRColors.defaultDarkOutlineVariant.toColor(),
    scrim = Color.Black
)

@Composable
fun greenLightPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.greenLightPrimary.toColor(),
    onPrimary =                 MRColors.greenLightOnPrimary.toColor(),
    primaryContainer =          MRColors.greenLightPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.greenLightOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.greenLightInversePrimary.toColor(),
    secondary =                 MRColors.greenLightSecondary.toColor(),
    onSecondary =               MRColors.greenLightOnSecondary.toColor(),
    secondaryContainer =        MRColors.greenLightSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.greenLightOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.greenLightTertiary.toColor(),
    onTertiary =                MRColors.greenLightOnTertiary.toColor(),
    tertiaryContainer =         MRColors.greenLightTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.greenLightOnTertiaryContainer.toColor(),
    background =                MRColors.greenLightBackground.toColor(),
    onBackground =              MRColors.greenLightOnBackground.toColor(),
    surface =                   MRColors.greenLightSurface.toColor(),
    onSurface =                 MRColors.greenLightOnSurface.toColor(),
    surfaceVariant =            MRColors.greenLightSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.greenLightOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.greenLightSurfaceTint.toColor(),
    inverseSurface =            MRColors.greenLightInverseSurface.toColor(),
    inverseOnSurface =          MRColors.greenLightInverseOnSurface.toColor(),
    error =                     MRColors.greenLightError.toColor(),
    onError =                   MRColors.greenLightOnError.toColor(),
    errorContainer =            MRColors.greenLightErrorContainer.toColor(),
    onErrorContainer =          MRColors.greenLightOnErrorContainer.toColor(),
    outline =                   MRColors.greenLightOutline.toColor(),
    outlineVariant =            MRColors.greenLightOutlineVariant.toColor(),
    scrim = Color.Black
)
@Composable
fun greenDarkPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.greenDarkPrimary.toColor(),
    onPrimary =                 MRColors.greenDarkOnPrimary.toColor(),
    primaryContainer =          MRColors.greenDarkPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.greenDarkOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.greenDarkInversePrimary.toColor(),
    secondary =                 MRColors.greenDarkSecondary.toColor(),
    onSecondary =               MRColors.greenDarkOnSecondary.toColor(),
    secondaryContainer =        MRColors.greenDarkSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.greenDarkOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.greenDarkTertiary.toColor(),
    onTertiary =                MRColors.greenDarkOnTertiary.toColor(),
    tertiaryContainer =         MRColors.greenDarkTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.greenDarkOnTertiaryContainer.toColor(),
    background =                MRColors.greenDarkBackground.toColor(),
    onBackground =              MRColors.greenDarkOnBackground.toColor(),
    surface =                   MRColors.greenDarkSurface.toColor(),
    onSurface =                 MRColors.greenDarkOnSurface.toColor(),
    surfaceVariant =            MRColors.greenDarkSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.greenDarkOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.greenDarkSurfaceTint.toColor(),
    inverseSurface =            MRColors.greenDarkInverseSurface.toColor(),
    inverseOnSurface =          MRColors.greenDarkInverseOnSurface.toColor(),
    error =                     MRColors.greenDarkError.toColor(),
    onError =                   MRColors.greenDarkOnError.toColor(),
    errorContainer =            MRColors.greenDarkErrorContainer.toColor(),
    onErrorContainer =          MRColors.greenDarkOnErrorContainer.toColor(),
    outline =                   MRColors.greenDarkOutline.toColor(),
    outlineVariant =            MRColors.greenDarkOutlineVariant.toColor(),
    scrim = Color.Black
)

@Composable
fun redLightPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.redLightPrimary.toColor(),
    onPrimary =                 MRColors.redLightOnPrimary.toColor(),
    primaryContainer =          MRColors.redLightPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.redLightOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.redLightInversePrimary.toColor(),
    secondary =                 MRColors.redLightSecondary.toColor(),
    onSecondary =               MRColors.redLightOnSecondary.toColor(),
    secondaryContainer =        MRColors.redLightSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.redLightOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.redLightTertiary.toColor(),
    onTertiary =                MRColors.redLightOnTertiary.toColor(),
    tertiaryContainer =         MRColors.redLightTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.redLightOnTertiaryContainer.toColor(),
    background =                MRColors.redLightBackground.toColor(),
    onBackground =              MRColors.redLightOnBackground.toColor(),
    surface =                   MRColors.redLightSurface.toColor(),
    onSurface =                 MRColors.redLightOnSurface.toColor(),
    surfaceVariant =            MRColors.redLightSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.redLightOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.redLightSurfaceTint.toColor(),
    inverseSurface =            MRColors.redLightInverseSurface.toColor(),
    inverseOnSurface =          MRColors.redLightInverseOnSurface.toColor(),
    error =                     MRColors.redLightError.toColor(),
    onError =                   MRColors.redLightOnError.toColor(),
    errorContainer =            MRColors.redLightErrorContainer.toColor(),
    onErrorContainer =          MRColors.redLightOnErrorContainer.toColor(),
    outline =                   MRColors.redLightOutline.toColor(),
    outlineVariant =            MRColors.redLightOutlineVariant.toColor(),
    scrim = Color.Black
)
@Composable
fun redDarkPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.redDarkPrimary.toColor(),
    onPrimary =                 MRColors.redDarkOnPrimary.toColor(),
    primaryContainer =          MRColors.redDarkPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.redDarkOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.redDarkInversePrimary.toColor(),
    secondary =                 MRColors.redDarkSecondary.toColor(),
    onSecondary =               MRColors.redDarkOnSecondary.toColor(),
    secondaryContainer =        MRColors.redDarkSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.redDarkOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.redDarkTertiary.toColor(),
    onTertiary =                MRColors.redDarkOnTertiary.toColor(),
    tertiaryContainer =         MRColors.redDarkTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.redDarkOnTertiaryContainer.toColor(),
    background =                MRColors.redDarkBackground.toColor(),
    onBackground =              MRColors.redDarkOnBackground.toColor(),
    surface =                   MRColors.redDarkSurface.toColor(),
    onSurface =                 MRColors.redDarkOnSurface.toColor(),
    surfaceVariant =            MRColors.redDarkSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.redDarkOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.redDarkSurfaceTint.toColor(),
    inverseSurface =            MRColors.redDarkInverseSurface.toColor(),
    inverseOnSurface =          MRColors.redDarkInverseOnSurface.toColor(),
    error =                     MRColors.redDarkError.toColor(),
    onError =                   MRColors.redDarkOnError.toColor(),
    errorContainer =            MRColors.redDarkErrorContainer.toColor(),
    onErrorContainer =          MRColors.redDarkOnErrorContainer.toColor(),
    outline =                   MRColors.redDarkOutline.toColor(),
    outlineVariant =            MRColors.redDarkOutlineVariant.toColor(),
    scrim = Color.Black
)

@Composable
fun yellowLightPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.yellowLightPrimary.toColor(),
    onPrimary =                 MRColors.yellowLightOnPrimary.toColor(),
    primaryContainer =          MRColors.yellowLightPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.yellowLightOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.yellowLightInversePrimary.toColor(),
    secondary =                 MRColors.yellowLightSecondary.toColor(),
    onSecondary =               MRColors.yellowLightOnSecondary.toColor(),
    secondaryContainer =        MRColors.yellowLightSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.yellowLightOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.yellowLightTertiary.toColor(),
    onTertiary =                MRColors.yellowLightOnTertiary.toColor(),
    tertiaryContainer =         MRColors.yellowLightTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.yellowLightOnTertiaryContainer.toColor(),
    background =                MRColors.yellowLightBackground.toColor(),
    onBackground =              MRColors.yellowLightOnBackground.toColor(),
    surface =                   MRColors.yellowLightSurface.toColor(),
    onSurface =                 MRColors.yellowLightOnSurface.toColor(),
    surfaceVariant =            MRColors.yellowLightSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.yellowLightOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.yellowLightSurfaceTint.toColor(),
    inverseSurface =            MRColors.yellowLightInverseSurface.toColor(),
    inverseOnSurface =          MRColors.yellowLightInverseOnSurface.toColor(),
    error =                     MRColors.yellowLightError.toColor(),
    onError =                   MRColors.yellowLightOnError.toColor(),
    errorContainer =            MRColors.yellowLightErrorContainer.toColor(),
    onErrorContainer =          MRColors.yellowLightOnErrorContainer.toColor(),
    outline =                   MRColors.yellowLightOutline.toColor(),
    outlineVariant =            MRColors.yellowLightOutlineVariant.toColor(),
    scrim = Color.Black
)
@Composable
fun yellowDarkPalette():
        ColorScheme
        = ColorScheme(
    primary =                   MRColors.yellowDarkPrimary.toColor(),
    onPrimary =                 MRColors.yellowDarkOnPrimary.toColor(),
    primaryContainer =          MRColors.yellowDarkPrimaryContainer.toColor(),
    onPrimaryContainer =        MRColors.yellowDarkOnPrimaryContainer.toColor(),
    inversePrimary =            MRColors.yellowDarkInversePrimary.toColor(),
    secondary =                 MRColors.yellowDarkSecondary.toColor(),
    onSecondary =               MRColors.yellowDarkOnSecondary.toColor(),
    secondaryContainer =        MRColors.yellowDarkSecondaryContainer.toColor(),
    onSecondaryContainer =      MRColors.yellowDarkOnSecondaryContainer.toColor(),
    tertiary =                  MRColors.yellowDarkTertiary.toColor(),
    onTertiary =                MRColors.yellowDarkOnTertiary.toColor(),
    tertiaryContainer =         MRColors.yellowDarkTertiaryContainer.toColor(),
    onTertiaryContainer =       MRColors.yellowDarkOnTertiaryContainer.toColor(),
    background =                MRColors.yellowDarkBackground.toColor(),
    onBackground =              MRColors.yellowDarkOnBackground.toColor(),
    surface =                   MRColors.yellowDarkSurface.toColor(),
    onSurface =                 MRColors.yellowDarkOnSurface.toColor(),
    surfaceVariant =            MRColors.yellowDarkSurfaceVariant.toColor(),
    onSurfaceVariant =          MRColors.yellowDarkOnSurfaceVariant.toColor(),
    surfaceTint =               MRColors.yellowDarkSurfaceTint.toColor(),
    inverseSurface =            MRColors.yellowDarkInverseSurface.toColor(),
    inverseOnSurface =          MRColors.yellowDarkInverseOnSurface.toColor(),
    error =                     MRColors.yellowDarkError.toColor(),
    onError =                   MRColors.yellowDarkOnError.toColor(),
    errorContainer =            MRColors.yellowDarkErrorContainer.toColor(),
    onErrorContainer =          MRColors.yellowDarkOnErrorContainer.toColor(),
    outline =                   MRColors.yellowDarkOutline.toColor(),
    outlineVariant =            MRColors.yellowDarkOutlineVariant.toColor(),
    scrim = Color.Black
)

fun String.toColor() = Color(parseColor(this))


fun parseColor(colorString: String): Long {
    if (colorString[0] == '#') { // Use a long to avoid rollovers on #ffXXXXXX
        var color = colorString.substring(1).toLong(16)
        if (colorString.length == 7) { // Set the alpha value
            color = color or -0x1000000
        } else require(colorString.length == 9) { "Unknown color" }
        return color
    }
    throw IllegalArgumentException("Unknown color")
}
