# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep WebView with JS interface, uncomment and modify if needed:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve the line number information for debugging stack traces.
# This is useful for understanding where errors occurred in obfuscated code.
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name for security purposes.
#-renamesourcefileattribute SourceFile

# Suppress warnings for missing classes related to LoudnessCodecController.
-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener
-dontwarn android.media.LoudnessCodecController

# Keep androidx.credentials and playservices credentials classes.
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
    *;
}

# Add additional keep rules to preserve important classes and prevent R8 from removing them.
# This will help avoid issues with class references missing at runtime.
-keep class android.media.LoudnessCodecController { *; }
-keep class android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener { *; }

