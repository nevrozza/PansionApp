import androidx.compose.material3.CalendarLocale

actual fun getCalendarLocale(): CalendarLocale {
    return java.util.Locale.getDefault()
}