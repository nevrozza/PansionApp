-allowaccessmodification
-flattenpackagehierarchy
-mergeinterfacesaggressively
-dontnote *
-dontwarn org.slf4j.impl.StaticLoggerBinder
-keep class coil3.** { *; }
# Most of volatile fields are updated with AtomicFU and should not be mangled/removed
-keepclassmembers class io.ktor.** {
    volatile <fields>;
}

-keepclassmembernames class io.ktor.** {
    volatile <fields>;
}

# client engines are loaded using ServiceLoader so we need to keep them
-keep class io.ktor.client.engine.** implements io.ktor.client.HttpClientEngineContainer


-keep class com.jetbrains.** { *; }

# Remove intrinsic assertions.
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkFieldIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}

-keeppackagenames org.jetbrains.jewel

-dontwarn androidx.compose.desktop.DesktopTheme*
-dontwarn kotlinx.datetime.**

-keep class dev.romainguy.kotlin.explorer.code.*TokenMarker { *; }
-dontnote dev.romainguy.kotlin.explorer.code.*TokenMarker

-keep class org.fife.** { *; }
-dontnote org.fife.**

-keep class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe

-keep class com.jetbrains.JBR* { *; }
-dontnote com.jetbrains.JBR*

-keep class com.sun.jna** { *; }
-dontnote com.sun.jna**


-keep class androidx.compose.ui.input.key.KeyEvent_desktopKt { *; }
-dontnote androidx.compose.ui.input.key.KeyEvent_desktopKt

-dontnote androidx.compose.ui.input.key.KeyEvent_skikoKt
-dontwarn androidx.compose.ui.input.key.KeyEvent_skikoKt

-dontnote org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**
-dontwarn org.jetbrains.jewel.intui.markdown.standalone.styling.extensions.**

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }
-keep class io.ktor.client.engine.cio.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory


# Keep extension's common dependencies
-keep class ireader.core.source.** { public protected *; }
-keep class ireader.core.http.** { public protected *; }
-keep,allowoptimization class ireader.** { public protected *; }
-keep,allowoptimization class kotlinx.coroutines.** { public protected *; }
-keep,allowoptimization class androidx.preference.** { public protected *; }
-keep,allowoptimization class okhttp3.** { public protected *; }
-keep,allowoptimization class okio.** { public protected *; }
-keep,allowoptimization class org.jsoup.** { public protected *; }
-keep,allowoptimization class kotlin.** { public protected *; }
-keep,allowoptimization class io.ktor.** { public protected *; }
-keep,allowoptimization class com.google.gson.** { public protected *; }
-keep,allowoptimization class org.jetbrains.kotlinx.** { public protected *; }
-keep,allowoptimization class app.cash.quickjs.** { public protected *; }
-keep,allowoptimization class com.google.accompanist.** { public protected *; }
-keep,allowoptimization class org.tinylog.** { public protected *; }
-keep,allowoptimization class nl.siegmann.epublib.** { public protected *; }
-keep,allowoptimization class org.slf4j.** { public protected *; }
-keep class org.xmlpull.** { public protected *; }
-keep,allowoptimization class org.koin.** { public protected *; }
-keep,allowoptimization class app.cash.sqldelight.** { public protected *; }

-keepattributes SourceFile,
                LineNumberTable,
                RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                AnnotationDefault

-renamesourcefileattribute SourceFile

-dontwarn org.conscrypt.**


##---------------Begin: proguard configuration for couroutines  ----------
# When editing this file, update the following files as well:
# - META-INF/com.android.tools/proguard/coroutines.pro
# - META-INF/com.android.tools/r8/coroutines.pro


# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}





##---------------End: proguard configuration for Couroutines  ----------

##---------------Begin: proguard configuration for Okhttp  ----------
#Okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

##---------------End: proguard configuration for Okhttp  ----------
##---------------Begin: proguard configuration for okio  ----------

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
##---------------End: proguard configuration for okio  ----------

##---------------Begin: proguard configuration for Ktor  ----------
# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
##---------------End: proguard configuration for Ktor  ----------


#---------
# Keep trakt-java and tmdb-java entity names (for GSON)
-keep class ireader.common.models.*.entities.** {
    <fields>;
    <init>(...);
}
-keep class ireader.common.models.*.entities.** {
    <fields>;
    <init>(...);
}




##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*


# Gson specific classes
-dontwarn sun.misc.**


##---------------End: proguard configuration for Gson  ----------

##---------------Begin: proguard configuration for kotlinx.serialization  ----------
-keepattributes *Annotation*, InnerClasses

# kotlinx-serialization-json specific.
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}


-keep,includedescriptorclasses class ireader.**$$serializer { *; }
-keepclassmembers class org.ireader.** {
    *** Companion;
}


-keep class kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.** {
    <methods>;
}

##---------------End: proguard configuration for kotlinx.serialization  ----------

# Log4J
-dontwarn org.apache.logging.log4j.**
-keep,includedescriptorclasses class org.apache.logging.log4j.** { *; }
# tinylog
-dontwarn org.tinylog.**.**
-keep,includedescriptorclasses class org.tinylog.**
# antlr
-dontwarn org.antlr.runtime.**.**
-keep,includedescriptorclasses class org.antlr.runtime.**

-allowaccessmodification
-dontusemixedcaseclassnames
-verbose

-keepattributes *Annotation*

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keepclassmembers class * { public <init>(...); }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-keep class org.ocpsoft.prettytime.i18n**

-dontwarn kotlinx.datetime.**
-dontwarn org.slf4j.**
-keep class org.slf4j.**{ *; }
-keep class com.sun.jna.* { *; }
-keep class * implements com.sun.jna.* { *; }

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkFieldIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}

-keepclasseswithmembers public class Main_desktopKt {  # <-- Change com.company to yours
    public static void main(); #java.lang.String[]
}


-dontwarn kotlinx.coroutines.debug.*
-keep class java.lang.** { *; }
-keep class org.sqlite.** { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

-assumenosideeffects public class androidx.compose.runtime.ComposerKt {
    void sourceInformation(androidx.compose.runtime.Composer,java.lang.String);
    void sourceInformationMarkerStart(androidx.compose.runtime.Composer,int,java.lang.String);
    void sourceInformationMarkerEnd(androidx.compose.runtime.Composer);
}

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations
-dontnote kotlinx.serialization.SerializationKt

# Keep Serializers

-keep,includedescriptorclasses class **$$serializer { *; }  # <-- Change com.company.package
-keepclassmembers class ** {  # <-- Change com.company.package to yours
    *** Companion;
}
-keepclasseswithmembers class ** { # <-- Change com.company.package to yours
    kotlinx.serialization.KSerializer serializer(...);
}

# When kotlinx.serialization.json.JsonObjectSerializer occurs

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
#################################### SLF4J #####################################
-dontwarn org.slf4j.**

# Prevent runtime crashes from use of class.java.getName()
-dontwarn javax.naming.**

# Ignore warnings and Don't obfuscate for now
-ignorewarnings

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

-keep class com.arkivanov.decompose.mainthread.*
-keep class com.arkivanov.decompose.extensions.compose.mainthread.SwingMainThreadChecker

-dontwarn androidx.compose.desktop.DesktopTheme*
-dontoptimize
-keep class org.fife.** { *; }
-dontnote org.fife.**
-keep class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe
-keep class com.jetbrains.JBR* { *; }
-dontnote com.jetbrains.JBR*
-keep class com.sun.jna** { *; }
-dontnote com.sun.jna**

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault


-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

-keepclasseswithmembers class com.kmp.core.* { # <-- change package name to your app's
kotlinx.serialization.KSerializer serializer(...);
}

-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlinx.serialization.SerializationKt

# Keep Serializers

-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# When kotlinx.serialization.json.JsonObjectSerializer occurs

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

-keepattributes Annotation, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

