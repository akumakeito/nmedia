package ru.netology.nmedia.repository

import android.content.SharedPreferences
import androidx.core.content.edit

interface UserRepository {
    var token : String?
    var userId : Long?
}

class UserRepositoryImpl (
    private val prefs : SharedPreferences
    ) : UserRepository {

        companion object {
            private const val idKey = "id"
            private const val tokenKey = "token"
        }

    override var token: String?
        get() = prefs.getString(tokenKey, null)
        set(value) {
            prefs.edit { putString(tokenKey, value) }
        }
    override var userId: Long?
        get() = prefs.getLong(idKey, 0)
        set(value) {
            prefs.edit { putLong(idKey, value ?: 0) }
        }
}