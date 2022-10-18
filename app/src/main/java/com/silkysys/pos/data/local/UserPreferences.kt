package com.silkysys.pos.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private var pref: SharedPreferences,
    private val editor: SharedPreferences.Editor
) {

    fun write(key: String, value: Any?) {
        editor.apply {
            if (value is String) putString(key, value)
            else if (value is Int) putInt(key,value)
            apply()
        }
    }


    fun read(key: String) =
        pref.getString(key, "")

    fun readInt(key: String) =
        pref.getInt(key, 0)


    fun remove(key: String) {
        editor.apply {
            remove(key)
            apply()
        }
    }
}
