package ru.netology.nmedia.repository

import android.content.Context
import androidx.core.content.edit
import java.util.*

interface TokenRepository {
   var token : String?
   var userId : Long
    fun clear()
}

class TokenRepositoryPreferences(
    context: Context
) : TokenRepository {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    companion object {
        private const val idKey = "id"
        private const val tokenKey = "token"
    }

    override var token: String?
        get() = prefs.getString(tokenKey, null)
        set(value) {
            with(prefs.edit()) {
                putString(tokenKey, value)
                apply()
            }
        }

    override var userId: Long
        get() = prefs.getLong(idKey, 0L)
        set(value) {
            with(prefs.edit()) {
                putLong(idKey,value ?: 0L)
                apply()
            }
        }

    override fun clear() {
        prefs.edit { clear() }
    }

}