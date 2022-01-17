package ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {

            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {

            }
            modelClass.isAssignableFrom(SignViewModel::class.java) -> {

            }
            else -> error("Unknown view model ${modelClass.name}")
        }

    }
}