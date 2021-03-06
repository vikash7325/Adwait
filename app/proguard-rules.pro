-dontskipnonpubliclibraryclassmembers
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-dontwarn *.**
-verbose
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}


 #Application classes that will be serialized/deserialized over Gson
-keep class * extends ad.adwait.mcom.utils.ADBaseModel
-keepclassmembers class * extends ad.adwait.mcom.utils.ADBaseModel { *; }
