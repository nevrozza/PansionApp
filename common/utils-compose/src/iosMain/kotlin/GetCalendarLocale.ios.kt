import androidx.compose.material3.CalendarLocale
import platform.Foundation.NSLocale

actual fun getCalendarLocale(): CalendarLocale {
    return NSLocale("Ru")
}