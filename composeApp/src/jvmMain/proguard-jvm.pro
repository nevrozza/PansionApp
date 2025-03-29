-dontwarn okhttp3.internal.platform.**
-keeppackagenames org.jetbrains.jewel
-keep class org.jetbrains.jewel.**

-keepclasseswithmembers public class Main_desktopKt {  # <-- Change com.company to yours
    public static void main(); #java.lang.String[]
}

-keep class sun.misc.Unsafe { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory


# Убираем предупреждения для Compose Desktop
-dontwarn androidx.compose.desktop.DesktopTheme*
-keep class androidx.compose.ui.input.key.KeyEvent_desktopKt { *; }
-dontnote androidx.compose.ui.input.key.KeyEvent_desktopKt

-keep class androidx.compose.material3.ShapesKt

#-keep class androidx.compose.ui.input.key.KeyEvent_skikoKt { *; }
-dontnote androidx.compose.ui.input.key.KeyEvent_skikoKt
-dontwarn androidx.compose.ui.input.key.KeyEvent_skikoKt