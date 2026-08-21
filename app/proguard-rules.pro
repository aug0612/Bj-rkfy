# Add project specific ProGuard rules here.

# Keep Spotify data models (kotlinx.serialization reflects on these)
-keep class com.rodrigofy.app.data.** { *; }
-keepattributes *Annotation*
-keepattributes InnerClasses

-dontwarn kotlinx.serialization.**
-dontwarn io.ktor.**
