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
import ru.netology.nmedia.repository.UserRepository
import ru.netology.ru.netology.nmedia.dto.PushToken

class AppAuth(
    private val service: ApiService,
    private val userRepository: UserRepository
)
 {
     private val _authStateFlow : MutableStateFlow<AuthState>

    init {
        val id = userRepository.userId ?: 0
        val token = userRepository.token

        if (id == 0L || token ==  null) {
            _authStateFlow = MutableStateFlow(AuthState())
            clear()
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }

        sendPushToken()


    }



     val authStateFlow : StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        userRepository.userId = id
        userRepository.token = token

        sendPushToken()
        println("id = $id, token = $token")
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        clear()

        sendPushToken()
    }

    fun sendPushToken(token : String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                service.save(pushToken)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }
     private fun clear() {
     userRepository.token = null
     userRepository.userId = null
 }



//    companion object {
//        @Volatile
//        private var instance : AppAuth? = null
//
//        fun getInstance() : AppAuth = synchronized(this) {
//            instance ?: throw IllegalStateException(
//                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
//            )
//        }
//
//        fun initApp(context : Context) : AppAuth = instance ?: synchronized(this) {
//            instance ?: buildAuth(context).also { instance = it}
//        }
//
//        private fun buildAuth(context: Context) : AppAuth = AppAuth(context)
//    }

}

data class AuthState(val id : Long = 0L, val token : String? = null, val avatar : String? = null) {
    override fun toString(): String {
        return "AuthState(id=$id)"
    }
}
