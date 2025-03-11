# ----------------------------------
# Общие настройки
# ----------------------------------
-allowaccessmodification
#-mergeinterfacesaggressively
-optimizationpasses 1
-flattenpackagehierarchy ''
-repackageclasses ''
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames

#missing_rules.txt:
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

#-dontskipnonpubliclibraryclassmembers
-verbose

# Mostly для сервера
#-ignorewarnings

# ----------------------------------
# Правила для приложения
# ----------------------------------
#-keep class ** { *; }
#-keepclassmembers class ** {
#    public <init>(...);
#    public *;
#}
# excelKt


# Сохраняем важные атрибуты
-keepattributes Signature
#, InnerClasses, *Annotation*, EnclosingMethod, Exceptions, SourceFile, LineNumberTable, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Убираем предупреждения для SLF4J
#-dontwarn org.slf4j.**
#-dontwarn org.slf4j.impl.StaticLoggerBinder

# Убираем предупреждения для Kotlin Serialization
#-dontwarn kotlinx.serialization.**
#-dontwarn kotlinx.datetime.**

# Убираем предупреждения для Ktor и связанных библиотек
#-dontwarn io.ktor.**
#-dontwarn io.netty.**
#-dontwarn com.typesafe.**
#-dontwarn kotlinx.atomicfu.**

# Убираем предупреждения для других библиотек
-keep class javax.annotation.**
-dontwarn javax.annotation.**
#-dontwarn org.codehaus.mojo.animal_sniffer.**
#-dontwarn org.conscrypt.**
#-dontwarn org.bouncycastle.**
#-dontwarn org.openjsse.**
#-dontwarn org.antlr.runtime.**
#-dontwarn org.apache.logging.log4j.**
#-dontwarn org.tinylog.**
#-dontwarn org.ocpsoft.prettytime.i18n.**
#-dontwarn org.jetbrains.skia.**
#-dontwarn org.jetbrains.skiko.**
#-dontwarn java.nio.file.**

# keep some
-keep class java.lang.** { *; }
-keep class org.sqlite.** { *; }
-keep,includedescriptorclasses class org.tinylog.**
-keep,includedescriptorclasses class org.apache.logging.log4j.** { *; }
-keep,includedescriptorclasses class org.antlr.runtime.**

# ----------------------------------
# Правила для Compose Multiplatform
# ----------------------------------
-keep class androidx.compose.** { *; }
-keep class androidx.compose.material3.** { *; }

# Сохраняем методы, используемые Compose
-keepclassmembers class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer, java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer, int, java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}

# ----------------------------------
# Правила для Ktor
# ----------------------------------
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class io.ktor.client.engine.** { *; }

# Сохраняем volatile поля в Ktor
-keepclassmembers class io.ktor.** {
    volatile <fields>;
}

# ----------------------------------
# Правила для Kotlin Serialization
# ----------------------------------
-keep class kotlinx.serialization.** { *; }
-keep class **$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Сохраняем методы для сериализации
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

# ----------------------------------
# Правила для Kodein DI
# ----------------------------------
-keep class org.kodein.di.** { *; }
-keep class org.kodein.type.** { *; }
-keep class * extends org.kodein.type.TypeReference { *; }

# Сохраняем биндинги DI
-keep class * { @org.kodein.di.bindings.Binding *; }

# ----------------------------------
# Правила для Decompose
# ----------------------------------
-keep class com.arkivanov.decompose.** { *; }
-keep class com.arkivanov.mvikotlin.** { *; }

# ----------------------------------
# Правила для других библиотек
# ----------------------------------
-keep class coil3.** { *; }
-keep class com.jetbrains.JBR* { *; }
-keep class com.jetbrains.** { *; }
-keep class com.sun.jna.** { *; }
-keep class org.fife.** { *; }
-keep class org.cef.** { *; }


# ----------------------------------
# Правила для OkHttp и Okio
# ----------------------------------
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# ----------------------------------
# Правила для Kotlin Coroutines
# ----------------------------------
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Сохраняем методы main для запуска
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Убираем проверки Kotlin Intrinsics
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkFieldIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}