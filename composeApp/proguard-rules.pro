# Общие настройки
-optimizationpasses 1
-dontusemixedcaseclassnames
-verbose

# Сохраняем важные атрибуты
-keepattributes Signature

# Основные правила сохранения
-keep class java.lang.** { *; }
-keep class javax.annotation.**
-dontwarn javax.annotation.**

# Compose Multiplatform
-keep class androidx.compose.** { *; }
-keep class androidx.compose.material3.** { *; }
-keepclassmembers class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer, java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer, int, java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }

# Kotlin Serialization
-keep class **$$serializer { *; }
-keepclassmembers class ** { *** Companion; }
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class com.jetbrains.** { *; }
-keep class com.sun.jna.** { *; }

# Kodein DI
-keep class org.kodein.di.** { *; }
-keep class * extends org.kodein.type.TypeReference { *; }

# Decompose
-keep class com.arkivanov.decompose.** { *; }

-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

-keep class coil3.decode.** {*;}
-keep class coil3.network.** {*;}
-keep class coil3.svg.** {*;}
-keep class coil3.util.** {*;}


# Main класс
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# missing_rules
-dontwarn androidx.compose.animation.tooling.ComposeAnimatedProperty
-dontwarn androidx.compose.animation.tooling.ComposeAnimation
-dontwarn androidx.compose.animation.tooling.ComposeAnimationType
-dontwarn androidx.compose.animation.tooling.TransitionInfo
-dontwarn io.ktor.client.network.sockets.SocketTimeoutException
-dontwarn io.ktor.client.plugins.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.plugins.HttpTimeout$Plugin
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.utils.io.CoroutinesKt
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
