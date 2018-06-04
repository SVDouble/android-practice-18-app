package com.svdouble.gamestorm

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    private val PREFS_FILENAME = "com.svdouble.gamestorm.prefs"
    private val STRING_TO_SAVE = "string_to_save"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var testStringProperty: String
        get() = prefs.getString(STRING_TO_SAVE, "")
        set(value) = prefs.edit().putString(STRING_TO_SAVE, value).apply()
}


val prefs: Prefs by lazy {
    App.prefs!!
}

class App : Application() {
    companion object {
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
    }
}