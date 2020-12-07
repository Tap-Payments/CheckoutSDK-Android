# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class company.tap.checkout** { *; }
-keep public interface company.tap.checkout.internal.interfaces.BaseLayouttManager {*;}
-keep public interface company.tap.checkout.internal.viewmodels.TapLayoutViewModell {*;}
-keep public interface company.tap.checkout.internal.enums** {*;}
-dontwarn okhttp3.**
-dontwarn okhttp2.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepnames class company.tap.tapuilibrary**{*;}
#-dontobfuscate
-optimizations !code/allocation/variable
# GSON.
-keepnames enum company.tap.checkout** {*;}
-keepnames interface company.tap.checkout** {*;}

-keepclassmembers enum * { *; }