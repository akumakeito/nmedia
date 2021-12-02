package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDB
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDB.getInstance(context = application).postDao(),
            AppDB.getInstance(context = application).postWorkDao())

    fun signIn() = viewModelScope.launch {
        val response = repository.signIn()
        response.token?.let { AppAuth.getInstance().setAuth(response.id, response.token) }
    }

}