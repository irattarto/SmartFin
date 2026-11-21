# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/google/home/cartland/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Begin Enable minification

#-ignorewarnings
-dontobfuscate

-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
#-keepattributes InnerClasses

#-keep public class com.liskovsoft.youtubeapi.** { *; }
#-keep class okhttp3.**{*;}
#-keep interface okhttp3.** { *; }

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.jayway.jsonpath.**
-dontwarn retrofit2.**
-dontwarn org.slf4j.**

# End Enable minification

# ---------------------------------------------------------------------------------------------
# SmartFin: R8 configuration
# Conservative on purpose: the app and several libraries rely on reflection (Gson/JsonPath/RegExp
# type adapters reading annotated fields, Retrofit interfaces), JNI (Cronet, Conscrypt, J2V8) and
# XML-inflated classes. Those are kept completely; R8 still removes unused code from the other
# third-party libraries. Obfuscation stays off (-dontobfuscate above) to keep stack traces readable.
# ---------------------------------------------------------------------------------------------
-keepattributes InnerClasses,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault,Exceptions

# App and own modules (MVP presenters/views, prefs, models parsed by reflection)
-keep class com.liskovsoft.** { *; }
-keep interface com.liskovsoft.** { *; }
# Modified library copies built from source
-keep class com.google.android.exoplayer2.** { *; }
-keep class androidx.leanback.** { *; }
-keep class androidx.fragment.** { *; }
-keep class com.stfalcon.chatkit.** { *; }
-keep class arte.programar.materialfile.** { *; }
-keep class com.github.vkay94.dtpv.** { *; }
-keep class com.r0adkll.slidr.** { *; }
-keep class org.apache.commons.io.** { *; }
# Fields read by name (Helpers.getField)
-keepclassmembers class androidx.recyclerview.widget.RecyclerView$ViewHolder { *** mNestedRecyclerView; }
# JNI / reflection based libraries
-keep class org.chromium.net.** { *; }
-keep class com.google.net.cronet.** { *; }
-keep class org.conscrypt.** { *; }
-keep class com.eclipsesource.v8.** { *; }
-keep class org.xbill.DNS.** { *; }
-keep class com.jayway.jsonpath.** { *; }
-keep class net.minidev.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class info.guardianproject.netcipher.** { *; }
-dontwarn org.chromium.**
-dontwarn org.conscrypt.**
-dontwarn org.xbill.DNS.**
-dontwarn lombok.**
-dontwarn javax.naming.**
-dontwarn com.sun.jna.**

# Release builds: drop debug/verbose logcat output (may contain URLs and identifiers).
# SmartFin's own optional file logger still works (it doesn't go through android.util.Log for files).
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

# Glide: modules are discovered by reflection (manifest meta-data / generated AppGlideModule)
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep class * extends com.bumptech.glide.module.LibraryGlideModule { <init>(...); }
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }
-keep class com.bumptech.glide.integration.webp.** { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# WorkManager / Room: generated *_Impl classes are instantiated by reflection
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.work.** { *; }
-keep class androidx.work.impl.** { *; }
-keep class * extends androidx.work.ListenableWorker { <init>(android.content.Context, androidx.work.WorkerParameters); }
-keep class * extends androidx.work.InputMerger { <init>(); }
