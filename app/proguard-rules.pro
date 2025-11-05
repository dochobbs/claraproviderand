# ProGuard rules for Clara Provider App

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Kotlin data classes
-keepclassmembers class com.clara.provider.models.** {
    <fields>;
    <methods>;
}

# Keep Supabase classes
-keep class io.github.jan_tennert.supabase.** { *; }
-keepclassmembers class io.github.jan_tennert.supabase.** { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keepclassmembers class com.google.firebase.** { *; }

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keepclassmembers class okhttp3.** { *; }

# Keep Retrofit classes
-keep class retrofit2.** { *; }
-keepclassmembers class retrofit2.** { *; }

# Preserve line numbers for debugging
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
