package com.sirionrazzer.diary.models

import android.content.SharedPreferences
import android.util.Base64
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Named

class UserStorage @Inject constructor() {

    @Inject
    lateinit var gson: Gson

    @Inject
    @field:Named("user_prefs")
    lateinit var prefs: SharedPreferences

    var userSettings: LocalUserSettings
        get() {
            val json = prefs.getString("user_settings", null)
            return if (json == null) LocalUserSettings() else gson.fromJson(
                json,
                LocalUserSettings::class.java
            )
        }
        set(value) =
            prefs.edit().putString("user_settings", gson.toJson(value)).apply()

    fun loadPasswordDigest(): ByteArray {
        val pwStr = prefs.getString("password_digest", "")
        return Base64.decode(pwStr, Base64.DEFAULT)
    }

    fun storePasswordDigest(pw: ByteArray) {
        prefs.edit().putString("password_digest", Base64.encodeToString(pw, Base64.NO_WRAP)).commit()
    }

    fun clearPasswordDigest() {
        prefs.edit().remove("password_digest").apply()
    }

    fun clearUserData() {
        prefs.edit().clear().apply()
    }

    fun updateSettings(updateFunc: (LocalUserSettings) -> Unit) {
        userSettings = userSettings.apply {
            updateFunc(this)
        }
    }
}
