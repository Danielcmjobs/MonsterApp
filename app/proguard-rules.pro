# Default rules for ProGuard
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.moshi.** { *; }
-keep class com.example.monsterapp.data.model.** { *; }
-keep class com.example.monsterapp.data.db.** { *; }
