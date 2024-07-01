import androidx.compose.material3.CalendarLocale
import java.util.Locale

actual fun getCalendarLocale(): CalendarLocale {
    return Locale.getDefault()
}