import js.core.asList
import org.w3c.dom.HTMLMetaElement
import web.dom.document

actual fun changeMetaThemeColor(newColor: String) {
    val metaTags = document.head.querySelectorAll("meta[name=theme-color]").asList()
    @Suppress("CAST_NEVER_SUCCEEDS") val themeColorMetaTag = metaTags[0] as HTMLMetaElement?

    if (themeColorMetaTag != null) {
        themeColorMetaTag.content = newColor
    }
    document.body.style.backgroundColor = newColor
}