import kotlinx.browser.document
import org.w3c.dom.HTMLMetaElement
import org.w3c.dom.asList

actual fun changeMetaThemeColor(newColor: String) {
    val metaTags = document.head?.querySelectorAll("meta[name=theme-color]")?.asList()
    val themeColorMetaTag = metaTags?.get(0) as HTMLMetaElement?

    if (themeColorMetaTag != null) {
        themeColorMetaTag.content = newColor
    }
    document.body?.style?.backgroundColor = newColor
}