package ru.netology.nmedia.viewModel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.dto.UserKey
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class SignViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _data: MutableLiveData<UserKey> = MutableLiveData<UserKey>()
    val data: LiveData<UserKey>
        get() = _data

    fun signIn(login: String, password: String, @ApplicationContext context : Context) = viewModelScope.launch {
        val response = apiService.updateUser(login, password)
        if (!response.isSuccessful) {
            Toast.makeText(context, "Incorrect login or password", Toast.LENGTH_LONG).show()
            return@launch
        }
        val userKey: UserKey = response.body() ?: throw RuntimeException("Body is null")
        _data.value = userKey
    }
}