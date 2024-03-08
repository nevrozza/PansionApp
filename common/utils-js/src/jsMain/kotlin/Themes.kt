//import dev.icerock.moko.resources.ColorResource
//import dev.icerock.moko.resources.desc.color.asColorDesc
import web.cssom.Color
import web.cssom.rgb
import web.prompts.alert

data class Theme(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val inversePrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceTint: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val outline: Color,
    val outlineVariant: Color,
    val scrim: Color,
)

fun colorFromResource(value: ColorResource): Color  {
    return rgb(value.lightColor.red, value.lightColor.green, value.lightColor.blue)
}

val lightTheme = Theme(
    primary =               colorFromResource(MRColors.defaultLightPrimary),
    onPrimary =             colorFromResource(MRColors.defaultLightOnPrimary),
    primaryContainer =      colorFromResource(MRColors.defaultLightPrimaryContainer),
    onPrimaryContainer =    colorFromResource(MRColors.defaultLightOnPrimaryContainer),
    inversePrimary =        colorFromResource(MRColors.defaultLightInversePrimary),
    secondary =             colorFromResource(MRColors.defaultLightSecondary),
    onSecondary =           colorFromResource(MRColors.defaultLightOnSecondary),
    secondaryContainer =    colorFromResource(MRColors.defaultLightSecondaryContainer),
    onSecondaryContainer =  colorFromResource(MRColors.defaultLightOnSecondaryContainer),
    tertiary =              colorFromResource(MRColors.defaultLightTertiary),
    onTertiary =            colorFromResource(MRColors.defaultLightOnTertiary),
    tertiaryContainer =     colorFromResource(MRColors.defaultLightTertiaryContainer),
    onTertiaryContainer =   colorFromResource(MRColors.defaultLightOnTertiaryContainer),
    background =            colorFromResource(MRColors.defaultLightBackground),
    onBackground =          colorFromResource(MRColors.defaultLightOnBackground),
    surface =               colorFromResource(MRColors.defaultLightSurface),
    onSurface =             colorFromResource(MRColors.defaultLightOnSurface),
    surfaceVariant =        colorFromResource(MRColors.defaultLightSurfaceVariant),
    onSurfaceVariant =      colorFromResource(MRColors.defaultLightOnSurfaceVariant),
    surfaceTint =           colorFromResource(MRColors.defaultLightSurfaceTint),
    inverseSurface =        colorFromResource(MRColors.defaultLightInverseSurface),
    inverseOnSurface =      colorFromResource(MRColors.defaultLightInverseOnSurface),
    error =                 colorFromResource(MRColors.defaultLightError),
    onError =               colorFromResource(MRColors.defaultLightOnError),
    errorContainer =        colorFromResource(MRColors.defaultLightErrorContainer),
    onErrorContainer =      colorFromResource(MRColors.defaultLightOnErrorContainer),
    outline =               colorFromResource(MRColors.defaultLightOutline),
    outlineVariant =        colorFromResource(MRColors.defaultLightOutlineVariant),
    scrim = Color("#000000")
)


val darkTheme = Theme(
    primary =               colorFromResource(MRColors.defaultDarkPrimary),
    onPrimary =             colorFromResource(MRColors.defaultDarkOnPrimary),
    primaryContainer =      colorFromResource(MRColors.defaultDarkPrimaryContainer),
    onPrimaryContainer =    colorFromResource(MRColors.defaultDarkOnPrimaryContainer),
    inversePrimary =        colorFromResource(MRColors.defaultDarkInversePrimary),
    secondary =             colorFromResource(MRColors.defaultDarkSecondary),
    onSecondary =           colorFromResource(MRColors.defaultDarkOnSecondary),
    secondaryContainer =    colorFromResource(MRColors.defaultDarkSecondaryContainer),
    onSecondaryContainer =  colorFromResource(MRColors.defaultDarkOnSecondaryContainer),
    tertiary =              colorFromResource(MRColors.defaultDarkTertiary),
    onTertiary =            colorFromResource(MRColors.defaultDarkOnTertiary),
    tertiaryContainer =     colorFromResource(MRColors.defaultDarkTertiaryContainer),
    onTertiaryContainer =   colorFromResource(MRColors.defaultDarkOnTertiaryContainer),
    background =            colorFromResource(MRColors.defaultDarkBackground),
    onBackground =          colorFromResource(MRColors.defaultDarkOnBackground),
    surface =               colorFromResource(MRColors.defaultDarkSurface),
    onSurface =             colorFromResource(MRColors.defaultDarkOnSurface),
    surfaceVariant =        colorFromResource(MRColors.defaultDarkSurfaceVariant),
    onSurfaceVariant =      colorFromResource(MRColors.defaultDarkOnSurfaceVariant),
    surfaceTint =           colorFromResource(MRColors.defaultDarkSurfaceTint),
    inverseSurface =        colorFromResource(MRColors.defaultDarkInverseSurface),
    inverseOnSurface =      colorFromResource(MRColors.defaultDarkInverseOnSurface),
    error =                 colorFromResource(MRColors.defaultDarkError),
    onError =               colorFromResource(MRColors.defaultDarkOnError),
    errorContainer =        colorFromResource(MRColors.defaultDarkErrorContainer),
    onErrorContainer =      colorFromResource(MRColors.defaultDarkOnErrorContainer),
    outline =               colorFromResource(MRColors.defaultDarkOutline),
    outlineVariant =        colorFromResource(MRColors.defaultDarkOutlineVariant),
    scrim = Color("#000000")
)