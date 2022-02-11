package ru.netology.nmedia.auth

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.repository.TokenRepository
import ru.netology.ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val apiService: ApiService,
    private val tokenRepository: TokenRepository
) {
    private val _authStateFlow : MutableStateFlow<AuthState>

    init {
        val id = tokenRepository.userId
        val token = tokenRepository.token

        if (id == 0L || token ==  null) {
            _authStateFlow = MutableStateFlow(AuthState())
            tokenRepository.clear()
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }

        sendPushToken()


    }

    val authStateFlow : StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        tokenRepository.userId = id
        tokenRepository.token = token
        sendPushToken()
        println("id = $id, token = $token")
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        tokenRepository.clear()

        sendPushToken()
    }

    fun sendPushToken(token : String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                apiService.save(pushToken)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class AuthState(val id : Long = 0L, val token : String? = null, val avatar : String? = null) {
    override fun toString(): String {
        return "AuthState(id=$id)"
    }
}
