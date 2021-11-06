package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.SignInFragmentBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewModel.SignViewModel

class SignInFragment : Fragment() {
    private val viewModel : SignViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SignInFragmentBinding.inflate(inflater, container, false)

        binding.singInButton.setOnClickListener {
            if (binding.username.text.isNullOrBlank() || binding.password.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    this.getString(R.string.empty_login),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            else {
                viewModel.signIn()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }

        return binding.root
    }

}