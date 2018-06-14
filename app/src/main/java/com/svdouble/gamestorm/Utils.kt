package com.svdouble.gamestorm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/* Shared preferences */
class Prefs (context: Context) {
    private val PREFS_FILENAME = "com.svdouble.gamestorm.prefs"
    private val STRING_TO_SAVE = "string_to_save"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var testStringProperty: String
        get() = prefs.getString(STRING_TO_SAVE, "")
        set(value) = prefs.edit().putString(STRING_TO_SAVE, value).apply()
}


//val prefs: Prefs by lazy {
//    App.prefs!!
//}
//
//class App : Application() {
//    companion object {
//        var prefs: Prefs? = null
//    }
//
//    override fun onCreate() {
//        prefs = Prefs(applicationContext)
//        super.onCreate()
//    }
//}

/* Source: https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

