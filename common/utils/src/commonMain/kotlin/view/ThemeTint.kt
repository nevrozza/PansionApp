package view

enum class ThemeTint {
    Auto, Dark, Light
}

enum class FontTypes {
    Default, Cursive, Monospace, SansSerif, Serif, Geologica
}

fun String.toTint(): ThemeTint {
    return when (this) {
        ThemeTint.Dark.name -> ThemeTint.Dark
        ThemeTint.Light.name -> ThemeTint.Light
        else -> ThemeTint.Auto
    }
}
