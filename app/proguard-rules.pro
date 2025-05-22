# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html



#Retrofit config:

-dontwarn retrofit2.**

# Не трогаем библиотечные классы Retrofit
-keep class retrofit2.** { *; }

# Не трогаем все интерфейсы для Retrofit
-keep interface retrofit2.** { *; }

# Если используешь CallAdapter или Converter (у нас GsonConverterFactory)
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Для поддержки аннотаций HTTP (GET, POST и др.)
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.nemislimus.tratometr.authorization.data.dto.** { *; }