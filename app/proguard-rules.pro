#Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions

#OkHttp
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

#Keep empty constructors of classes used by Koin DI
-keepclassmembers public class com.suomifi.palvelutietovaranto.** {
    public <init>(...);
}

#Wikitude
-keep class com.wikitude.** { *; }

#Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Application classes that will be serialized/deserialized over Gson
-keep class com.suomifi.palvelutietovaranto.data.wfs.dto.** { *; }
-keep class com.suomifi.palvelutietovaranto.model.ptv.** { *; }
-keep class com.suomifi.palvelutietovaranto.data.search.entity.** { *; }

#Fabric
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
